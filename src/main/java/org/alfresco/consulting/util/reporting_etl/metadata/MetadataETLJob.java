package org.alfresco.consulting.util.reporting_etl.metadata;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.alfresco.consulting.util.ConsultingUtilsConstants;
import org.alfresco.consulting.util.reporting_etl.AbstractBaseETLJob;
import org.alfresco.consulting.util.tracking.TrackingComponent;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.audit.AuditComponent;
import org.alfresco.repo.domain.node.Transaction;
import org.alfresco.repo.domain.propval.PropertyValueDAO;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.audit.AuditQueryParameters;
import org.alfresco.service.cmr.audit.AuditService.AuditQueryCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeRef.Status;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MetadataETLJob extends AbstractBaseETLJob {
	
	int maxResults = 100;
	private final static String MAX_RESULTS_KEY="maxResults";
	private final static String METADATA_ETL_HANDLER_REGISTRY_KEY="metadataETLHandlerRegistry";
	private final static String METADATA_ETL_TRACKER_KEY="metadataETLTracker";
	private final static String TRACKING_COMPONENT_KEY="trackingComponent";
	private static final Log logger = LogFactory.getLog(MetadataETLJob.class);
	
	
	@Override
	protected QName getLockName() {
		return QName.createQName(ConsultingUtilsConstants.CONSULTING_UTILS_MODEL_1_0_URI, MetadataETLJob.class.getCanonicalName());
	}


	@Override
	protected void executeInt(JobExecutionContext ctx,final long endTime) throws JobExecutionException {

		JobDataMap map = ctx.getJobDetail().getJobDataMap();
		if (map.containsKey(MAX_RESULTS_KEY)) {
			maxResults = map.getInt(MAX_RESULTS_KEY);
		}

		final MetadataETLHandlerRegistry metadataETLHandlerRegistry = getBean(map,METADATA_ETL_HANDLER_REGISTRY_KEY);
		final MetadataETLTracker metadataETLTracker = getBean(map,METADATA_ETL_TRACKER_KEY);
		final TrackingComponent trackingComponent = getBean(map,TRACKING_COMPONENT_KEY);
		final long lastProcessedTransactionTime = metadataETLTracker.getLastProcessedTimeStamp();

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
					for (MetadataETLHandler h : metadataETLHandlerRegistry.getHandlers().values()) {
						String name = h.getETLHandlerName();
						for (Status change : changes) {
							NodeRef nodeRef = change.getNodeRef();
							h.extractMetadataEntry(nodeRef, change.isDeleted(), txn.getId());
						}
						metadataETLTracker.updateLastProcessedTimeStamp(ts, h);
					}
					metadataETLTracker.updateLastProcessedTimeStamp(ts);
					if (now() > endTime) {
						break;
					}

				}
				return null;
			}
			
		});
	}

}
