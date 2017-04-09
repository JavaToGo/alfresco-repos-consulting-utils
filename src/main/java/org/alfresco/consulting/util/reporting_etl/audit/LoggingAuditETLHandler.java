package org.alfresco.consulting.util.reporting_etl.audit;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

public class LoggingAuditETLHandler extends AuditETLHandlerBase {
	
	public final static String NAME="LOGGING_AUDIT";
	private static final Log logger = LogFactory.getLog(LoggingAuditETLHandler.class);
	private boolean enabled=false;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean extractAuditEntry(Long entryId, String applicationName,
			String user, long time, Map<String, Serializable> values) {
		if (logger.isDebugEnabled()) {
			JSONObject obj = new JSONObject(values);
			logger.debug(String.format("%s: %d: %s, %s, %d: %s",NAME,entryId,applicationName,user,time,obj.toString()));
		}
		return true;
	}

	@Override
	public String getETLHandlerName() {
		return NAME;
	}

}