package org.alfresco.consulting.util.reporting_etl;

import java.util.Calendar;

import org.alfresco.consulting.util.reporting_etl.metadata.MetadataETLHandlerRegistry;
import org.alfresco.consulting.util.reporting_etl.metadata.MetadataETLJob;
import org.alfresco.consulting.util.tracking.TrackingComponent;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.repo.lock.LockAcquisitionException;
import org.alfresco.repo.lock.JobLockService.JobLockRefreshCallback;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ApplicationContextHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public abstract class AbstractBaseETLJob implements Job, JobLockRefreshCallback {

	private static final Log logger = LogFactory.getLog(AbstractBaseETLJob.class);
	private static final String ETL_JOB_TIMEOUT_KEY = "etlJobTimeout";
	private static final String ETL_SERVICE_REGISTRY = "ServiceRegistry";
	private String lockToken;
	private boolean active;
	private long lockDuration;
	private boolean cancelFlag=false;
	private ServiceRegistry serviceRegistry;
	private JobLockService jobLockService;

	private long getLockDuration() {
		return lockDuration;
	}
	abstract protected QName getLockName();
	abstract protected void executeInt(JobExecutionContext ctx,long endTime)  throws JobExecutionException ;
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
	
	@Override
	public final void execute(JobExecutionContext ctx) throws JobExecutionException {
		JobDataMap map = ctx.getJobDetail().getJobDataMap();
		long timeout = 60000;
		if (map.containsKey(ETL_JOB_TIMEOUT_KEY)) {
			timeout = map.getInt(ETL_JOB_TIMEOUT_KEY);
		}
		serviceRegistry = getBean(map,ETL_SERVICE_REGISTRY);
		jobLockService = serviceRegistry.getJobLockService();

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
