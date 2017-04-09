package org.alfresco.consulting.util.reporting_etl.audit;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AuditETLHandler {
	boolean extractAuditEntry(Long entryId, String applicationName, String user, long time, Map<String, Serializable> values);
	String getETLHandlerName();
	Set<String> relevantApplicationNames();
	boolean isEnabled();
}
