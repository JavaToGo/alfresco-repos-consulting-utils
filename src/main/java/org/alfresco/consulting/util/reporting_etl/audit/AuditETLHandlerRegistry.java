package org.alfresco.consulting.util.reporting_etl.audit;

import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.audit.AuditService.AuditQueryCallback;

public interface AuditETLHandlerRegistry extends AuditQueryCallback {
	Map<String,AuditETLHandler> getHandlers();
	Set<AuditETLHandler> getHandlersForAuditApp(String appname);
	void registerHandler(AuditETLHandler h);
}
