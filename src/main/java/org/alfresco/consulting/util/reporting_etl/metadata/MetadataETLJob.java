package org.alfresco.consulting.util.reporting_etl.metadata;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.consulting.util.tracking.TrackingComponent;
import org.alfresco.repo.audit.AuditComponent;
import org.alfresco.repo.domain.propval.PropertyValueDAO;
import org.alfresco.service.cmr.audit.AuditQueryParameters;
import org.alfresco.service.cmr.audit.AuditService.AuditQueryCallback;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MetadataETLJob implements Job {
	
	int maxResults = 10000;
	private final static String MAX_RESULTS_KEY="maxResults";
	private final static String METADATA_ETL_HANDLER_REGISTRY_KEY="metadataETLHandlerRegistry";
	private final static String METADATA_ETL_TRACKER_KEY="metadataETLTracker";
	private static final Log logger = LogFactory.getLog(MetadataETLJob.class);
	
	private <T> T getBean(JobDataMap map,String key) {
        T t = (T) map.get(key);
        if (t == null)
        {
            throw new IllegalArgumentException(key + " in job data map was null");
        }
        return t;
	}

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		
		MetadataETLHandlerRegistry metadataETLHandlerRegistry;
		TrackingComponent metadataETLTracker;

		JobDataMap map = ctx.getJobDetail().getJobDataMap();
		if (map.containsKey(MAX_RESULTS_KEY)) {
			maxResults = map.getInt(MAX_RESULTS_KEY);
		}
		
		metadataETLTracker = getBean(map,METADATA_ETL_TRACKER_KEY);
		metadataETLHandlerRegistry = getBean(map,METADATA_ETL_HANDLER_REGISTRY_KEY);

		//AuditQueryParameters parameters = new AuditQueryParameters();
		//parameters.setFromId(metadataETLTracker.getLastProcessedEntry()+1);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Max Results: %d", maxResults));
		}
		
		//auditComponent.auditQuery(auditETLHandlerRegistry, parameters, maxResults);
	}

}
