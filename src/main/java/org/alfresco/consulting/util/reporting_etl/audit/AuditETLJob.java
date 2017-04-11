package org.alfresco.consulting.util.reporting_etl.audit;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.consulting.util.ConsultingUtilsConstants;
import org.alfresco.consulting.util.reporting_etl.AbstractBaseETLJob;
import org.alfresco.consulting.util.reporting_etl.metadata.MetadataETLJob;
import org.alfresco.repo.audit.AuditComponent;
import org.alfresco.repo.domain.propval.PropertyValueDAO;
import org.alfresco.service.cmr.audit.AuditQueryParameters;
import org.alfresco.service.cmr.audit.AuditService.AuditQueryCallback;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AuditETLJob  extends AbstractBaseETLJob {
	
	int maxResults = 100;
	private final static String MAX_RESULTS_KEY="maxResults";
	private final static String AUDIT_ETL_HANDLER_REGISTRY_KEY="auditETLHandlerRegistry";
	private final static String AUDIT_ETL_TRACKER_KEY="auditETLTracker";
	private final static String AUDIT_COMPONENT_KEY="auditComponent";
	private static final Log logger = LogFactory.getLog(AuditETLJob.class);
	

	@Override
	protected QName getLockName() {
		return QName.createQName(ConsultingUtilsConstants.CONSULTING_UTILS_MODEL_1_0_URI, AuditETLJob.class.getCanonicalName());
	}
	@Override
	protected void executeInt(JobExecutionContext ctx, long endTime) throws JobExecutionException {
		
		//TODO: Add endTime to Query parameters
		
		AuditETLHandlerRegistry auditETLHandlerRegistry;
		AuditETLTracker auditETLTracker;
		AuditComponent auditComponent;

		JobDataMap map = ctx.getJobDetail().getJobDataMap();
		if (map.containsKey(MAX_RESULTS_KEY)) {
			maxResults = map.getInt(MAX_RESULTS_KEY);
		}
		
		auditComponent = getBean(map,AUDIT_COMPONENT_KEY);
		auditETLTracker = getBean(map,AUDIT_ETL_TRACKER_KEY);
		auditETLHandlerRegistry = getBean(map,AUDIT_ETL_HANDLER_REGISTRY_KEY);

		AuditQueryParameters parameters = new AuditQueryParameters();
		parameters.setFromId(auditETLTracker.getLastProcessedEntry()+1);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Max Results: %d", maxResults));
		}
		
		auditComponent.auditQuery(auditETLHandlerRegistry, parameters, maxResults);
	}

}
