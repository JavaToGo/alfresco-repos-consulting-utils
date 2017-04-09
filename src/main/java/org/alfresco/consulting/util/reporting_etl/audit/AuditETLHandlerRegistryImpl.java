package org.alfresco.consulting.util.reporting_etl.audit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AuditETLHandlerRegistryImpl implements AuditETLHandlerRegistry {
	
	Map<String,Set<AuditETLHandler>> handlerCache = new HashMap<String,Set<AuditETLHandler>>();
	Map<String,AuditETLHandler> handlers = new HashMap<String,AuditETLHandler> ();

	private static final Log logger = LogFactory.getLog(AuditETLHandlerRegistryImpl.class);
	AuditETLTracker auditETLTracker;
	
	public void setAuditETLTracker(AuditETLTracker auditETLTracker) {
		this.auditETLTracker = auditETLTracker;
	}

	@Override
	public void registerHandler(AuditETLHandler h) {
		handlers.put(h.getETLHandlerName(),h);
		//Clear Cache when adding new hander
		synchronized(this) {
		    handlerCache.clear();
		}
	}
	
	private Set<AuditETLHandler> getHandlersForAuditAppInt(String appname)  {
		// Make the population of the cache thread safe
		synchronized(this) {
			if (!handlerCache.containsKey(appname)) {
				Set<AuditETLHandler> set = new HashSet<AuditETLHandler>();
				for (AuditETLHandler h : handlers.values()) {
					//Any Handler will must listen to at least one application otherwise it will listen to all audit applications
					if (h.relevantApplicationNames() == null || h.relevantApplicationNames().isEmpty() || h.relevantApplicationNames().contains(appname)) {
						set.add(h);
					}
				}
				handlerCache.put(appname, set);
			}
		}
		return handlerCache.get(appname);
	}

	@Override
	public boolean valuesRequired() {
		//Require the Values for an ETL process
		return true;
	}
	
	@Override
	public Set<AuditETLHandler> getHandlersForAuditApp(String appname) {
		return getHandlersForAuditAppInt(appname);
	}
 
	//Do the ETL process on the entry and then keep track of the last processed entry
	@Override
	public boolean handleAuditEntry(Long entryId, String applicationName, String user, long time, Map<String, Serializable> values) {
		boolean ret = true;
		for (AuditETLHandler h : getHandlersForAuditApp(applicationName)) {
			if (h.isEnabled()) {
				ret = ret && h.extractAuditEntry(entryId,applicationName,user, time, values);
				auditETLTracker.updateLastProcessedEntry(entryId,h,applicationName);
			}
		}
		//make sure that the last entry is recorded even if there are no handlers for it
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Last Entry: %d", entryId));
		}
		auditETLTracker.updateLastProcessedEntry(entryId);
		return ret;
	}

	@Override
	public boolean handleAuditEntryError(Long entryId, String errorMsg, Throwable error) {
		//Log errors and keep going when doign ETL
		if (logger.isErrorEnabled()) {
			logger.error(String.format("Entry %d: %s", entryId,errorMsg), error);
		}
		return true;
	}

	@Override
	public Map<String, AuditETLHandler> getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}

}
