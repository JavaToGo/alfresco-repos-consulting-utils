package org.alfresco.consulting.util.tracking;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.alfresco.consulting.util.ConsultingUtilsConstants;
import org.alfresco.consulting.util.reporting_etl.metadata.MetadataETLHandler;
import org.alfresco.consulting.util.reporting_etl.metadata.MetadataETLHandlerBase;
import org.alfresco.consulting.util.reporting_etl.metadata.MetadataETLHandlerRegistry;
import org.alfresco.consulting.util.reporting_etl.metadata.MetadataETLJob;
import org.alfresco.consulting.util.reporting_etl.metadata.MetadataETLTracker;
import org.alfresco.consulting.util.tracking.TrackingComponent;
import org.alfresco.repo.domain.node.Transaction;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.repo.lock.LockAcquisitionException;
import org.alfresco.repo.lock.JobLockService.JobLockRefreshCallback;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeRef.Status;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ApplicationContextHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public abstract class AbstractBaseTrackingJob implements Job, JobLockRefreshCallback {

	int maxResults = 100;
	private final static String MAX_RESULTS_KEY="maxResults";
	private final static String TRACKING_COMPONENT_KEY="trackingComponent";
	private static final Log logger = LogFactory.getLog(AbstractBaseTrackingJob.class);
	private static final String TRACKER_JOB_TIMEOUT_KEY = "trackerJobTimeout";
	private static final String TRACKER_SERVICE_REGISTRY = "ServiceRegistry";
	private static final String TRACKER_JOB_APP_ID = "..ABSTRACT_TRACKING_JOB_DATA..";
	private String lockToken;
	private boolean active;
	private long lockDuration;
	private boolean cancelFlag=false;
	private ServiceRegistry serviceRegistry;
	private AttributeService attributeService;
	private JobLockService jobLockService;

	private long getLockDuration() {
		return lockDuration;
	}
	protected QName getLockName() {
		return QName.createQName(ConsultingUtilsConstants.CONSULTING_UTILS_MODEL_1_0_URI, getJobAppName());		
	}
	protected Serializable getTrackerJobAppId() {
		return TRACKER_JOB_APP_ID;
	}
	
	public void resetLastProcessedTimeStamp() {
		attributeService.removeAttributes(getTrackerJobAppId(),getJobAppName());
	}

	public void updateLastProcessedTimeStamp(Long ts) {
		attributeService.setAttribute(ts, getTrackerJobAppId(),getJobAppName());
	}

	public long getLastProcessedTimeStamp() {
		try {
			return (Long) attributeService.getAttribute(getTrackerJobAppId(),getJobAppName());
		} catch (NullPointerException ex) {
			return 0;
		}
	}

	abstract protected String getJobAppName();
	abstract protected void processTxnNode(Status status,Transaction txn);
	protected long now() {
		return Calendar.getInstance().getTimeInMillis();
	}
	protected <T> T getBean(JobDataMap map,String key) {
        T t = (T) map.get(key);
        if (t == null)
        {
            throw new IllegalArgumentException(key + " in job data map was null");
        }
        return t;
	}
	private void executeInt(JobExecutionContext ctx,final long endTime) throws JobExecutionException {

		JobDataMap map = ctx.getJobDetail().getJobDataMap();
		if (map.containsKey(MAX_RESULTS_KEY)) {
			maxResults = map.getInt(MAX_RESULTS_KEY);
		}

		final TrackingComponent trackingComponent = getBean(map,TRACKING_COMPONENT_KEY);
		final long lastProcessedTransactionTime = getLastProcessedTimeStamp();

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Max Results: %d", maxResults));
			logger.debug(String.format("Last Processed Time: %d", lastProcessedTransactionTime));
		}
		
		
		AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {

			@Override
			public Object doWork() throws Exception {
				List<Transaction> txns = trackingComponent.getTxnsByCommitTimeAscending(lastProcessedTransactionTime+1, now(), maxResults);
				for (Transaction txn : txns) {
					List<Status> changes = trackingComponent.getTxnChanges(txn.getId());
					long ts = txn.getCommitTimeMs();
					for (Status change : changes) {
						processTxnNode(change,txn);
					}
					updateLastProcessedTimeStamp(ts);
					if (now() > endTime) {
						break;
					}

				}
				return null;
			}
			
		});
	}
	
	@Override
	public final void execute(JobExecutionContext ctx) throws JobExecutionException {
		JobDataMap map = ctx.getJobDetail().getJobDataMap();
		long timeout = 60000;
		if (map.containsKey(TRACKER_JOB_TIMEOUT_KEY)) {
			timeout = map.getInt(TRACKER_JOB_TIMEOUT_KEY);
		}
		serviceRegistry = getBean(map,TRACKER_SERVICE_REGISTRY);
		jobLockService = serviceRegistry.getJobLockService();
		attributeService = serviceRegistry.getAttributeService();

		lockDuration = timeout;
		acquireLock();
		synchronized(this) {
			cancelFlag=false;
		}
		
		long endTime=now() + timeout;
		
		try {

		executeInt(ctx,endTime);
		} catch (RuntimeException e) {
			logger.debug("ERROR Processing Job", e);
		} finally {		
			releaseLock();
		}
	}
    private void acquireLock()
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("Attempting to get Lock: " + getLockName() );
        }
       try 
       {
          // Quick try
          lockToken = jobLockService.getLock(
        		  getLockName(),
                getLockDuration(),
                5 * 1000, // Every 5 seconds
                6         // 6 times = wait up to 30 seconds
          );
          
          active = true;
          
          /**
           * Got the lock - now register the refresh callback which will keep the 
           * lock alive
           */
          jobLockService.refreshLock(
              lockToken,
              getLockName(),
              getLockDuration(), 
              this
          );
          
          if(logger.isDebugEnabled())
          {
              logger.debug("lock aquired:" + getLockName() );
          }
       } 
       catch(LockAcquisitionException e) 
       {
           long retryTime = 30*1000;
           int retries = (int)(60); 
           
          logger.debug(
                "Unable to get the replication job lock on " +
                getLockName() +
                ", retrying every " + (int)(retryTime/1000) + " seconds"
          );
          
          active = true; // Shouldn't this be after?
          
          // Long try - every 30 seconds
          lockToken = jobLockService.getLock(
        		getLockName(),
                getLockDuration(),
                retryTime,
                retries
          );
          
          /**
           * Got the lock - now register the refresh callback which will keep the 
           * lock alive
           */
          jobLockService.refreshLock(
              lockToken,
	      	  getLockName(),
	          getLockDuration(),
              this
          );
          
          if(logger.isDebugEnabled())
          {
              logger.debug("lock aquired (from long _timeout):" + getLockName() );
          }
       }
    }
          
    private void releaseLock()
    {
        if(active)
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("about to release lock:" + getLockName());
            }
            jobLockService.releaseLock(
               lockToken,
               getLockName());
            active=false;
        }
    }

    private boolean isCancelled()
    {
        return cancelFlag;
    }

    /**
     * Job Lock Refresh
     * @return
     */
    @Override
    public boolean isActive()
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("lock callback isActive:" + active + ", " + getLockName());
        }
        return active;
    }

    /**
     * Job Lock Service has released us.
     */
    @Override
    public void lockReleased()
    {
        logger.debug("lock released:" + getLockName());
        // nothing to do
    }

}
