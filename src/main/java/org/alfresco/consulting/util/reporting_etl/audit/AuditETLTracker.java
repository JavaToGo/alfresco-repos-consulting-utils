package org.alfresco.consulting.util.reporting_etl.audit;

import java.io.Serializable;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.attributes.AttributeService;

/**
 * Keep track of the last audit entry tracked
 */
public class AuditETLTracker {

	private AttributeService attributeService;

	protected Serializable getETLBaseAppId() {
		// TODO Consider Replacing with Declared Constant
		return AuditETLHandlerBase.class.getCanonicalName();
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.attributeService = serviceRegistry.getAttributeService();
	}

	public void resetLastProcessedEntry() {
		attributeService.removeAttributes(getETLBaseAppId());
	}
	public void updateLastProcessedEntry(Long entryId) {
		// TODO Sort out if we need to store this at multiple levels
		attributeService.setAttribute(entryId, getETLBaseAppId());
	}

	public void updateLastProcessedEntry(Long entryId, AuditETLHandler handler,
			String applicationName) {
		// TODO Sort out if we need to store this at multiple levels
		attributeService.setAttribute(entryId, getETLBaseAppId(),
				handler.getETLHandlerName(), applicationName);
		attributeService.setAttribute(entryId, getETLBaseAppId(),
				handler.getETLHandlerName());
		updateLastProcessedEntry(entryId);
	}

	public long getLastProcessedEntry() {
		try {
			return (Long) attributeService.getAttribute(getETLBaseAppId());
		} catch (NullPointerException ex) {
			return 0;
		}
	}

	public long getLastProcessedEntry(AuditETLHandler handler) {
		try {
			return (Long) attributeService.getAttribute(getETLBaseAppId(),
					handler.getETLHandlerName());
		} catch (NullPointerException ex) {
			return 0;
		}
	}

	public long getLastProcessedEntry(AuditETLHandler handler,
			String applicationName) {
		try {
			return (Long) attributeService.getAttribute(getETLBaseAppId(),
					handler.getETLHandlerName(), applicationName);
		} catch (NullPointerException ex) {
			return 0;
		}
	}
}
