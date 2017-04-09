package org.alfresco.consulting.util.reporting_etl.metadata;

import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.audit.AuditService.AuditQueryCallback;

public interface MetadataETLHandlerRegistry {
	Map<String,MetadataETLHandler> getHandlers();
	void registerHandler(MetadataETLHandler h);
}
