package org.alfresco.consulting.util.reporting_etl.audit;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AuditETLHandlerBase implements AuditETLHandler  {
	
	AuditETLHandlerRegistry auditETLHandlerRegistry;
	
	public void setAuditETLHandlerRegistry(AuditETLHandlerRegistry auditETLHandlerRegistry) {
		this.auditETLHandlerRegistry = auditETLHandlerRegistry;
		this.auditETLHandlerRegistry.registerHandler(this);
	}

	@Override
	public abstract boolean extractAuditEntry(Long entryId, String applicationName,
			String user, long time, Map<String, Serializable> values);

	@Override
	public abstract String getETLHandlerName();
	
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Set<String> relevantApplicationNames() {
		//Subscribe to all
		return null;
	}


}
