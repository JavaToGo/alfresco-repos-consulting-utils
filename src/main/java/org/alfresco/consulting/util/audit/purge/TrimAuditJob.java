package org.alfresco.consulting.util.audit.purge;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TrimAuditJob implements Job {
	
	int maxResults = 10000;
	private final static String TRIM_AUDIT_COMPONENT_KEY="trimAuditComponent";
	private final static String TRIM_AUDIT_KEEP_SECONDS="keepSeconds";
	private final static String TRIM_AUDIT_APPLICATION_NAMES="trimAuditApplicationNames";
	private static final Log logger = LogFactory.getLog(TrimAuditJob.class);
	
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
		

		JobDataMap map = ctx.getJobDetail().getJobDataMap();
		final Long keepSeconds = map.getLongValue(TRIM_AUDIT_KEEP_SECONDS);
		final String applicationNamesString = map.getString(TRIM_AUDIT_APPLICATION_NAMES);
		final TrimAuditComponent trimAuditComponent = getBean(map,TRIM_AUDIT_COMPONENT_KEY);
        final RunAsWork<Integer> doCleanRunAs = new RunAsWork<Integer>()
        {
            public Integer doWork() throws Exception
            {
            	List<String> applicationNames = null;
        		if (applicationNamesString == null || !applicationNamesString.trim().isEmpty()) {
        			applicationNames = Arrays.asList(applicationNamesString.split("\\s*,\\s*")); 

        		}
        		if (logger.isDebugEnabled()){
        			logger.debug("KeepSeconds= " + keepSeconds);
        			if (applicationNames != null) {
	        			for (String applicationName : applicationNames) {
	        				logger.debug("  -- Application Name = " + applicationName);
	        			}
        			}
        		}
                try
                {
                	return trimAuditComponent.doCleanupTxn(keepSeconds, applicationNames);
                }
                catch (Throwable e)
                {
                    logger.error("Error Executing Job",e);
                    return null;
                }
            }
        };
        
        int total = AuthenticationUtil.runAs(doCleanRunAs, AuthenticationUtil.getSystemUserName());

		if (logger.isDebugEnabled()) {
			logger.debug(total + " Entries Deleted");
		}

		
	}

}
