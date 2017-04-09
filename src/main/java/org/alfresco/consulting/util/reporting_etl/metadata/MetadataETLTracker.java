package org.alfresco.consulting.util.reporting_etl.metadata;

import java.io.Serializable;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.attributes.AttributeService;

/**
 * Keep track of the last audit entry tracked
 */
public class MetadataETLTracker {

	private AttributeService attributeService;

	protected Serializable getETLBaseAppId() {
		// TODO Consider Replacing with Declared Constant
		return MetadataETLHandlerBase.class.getCanonicalName();
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.attributeService = serviceRegistry.getAttributeService();
	}

	public void updateLastProcessedTimeStamp(Long ts) {
		// TODO Sort out if we need to store this at multiple levels
		attributeService.setAttribute(ts, getETLBaseAppId());
	}

	public void updateLastProcessedTimeStamp(Long ts, MetadataETLHandler handler) {
		// TODO Sort out if we need to store this at multiple levels
		attributeService.setAttribute(ts, getETLBaseAppId(), handler.getETLHandlerName());
		updateLastProcessedTimeStamp(ts);
	}

	public long getLastProcessedTimeStamp() {
		try {
			return (Long) attributeService.getAttribute(getETLBaseAppId());
		} catch (NullPointerException ex) {
			return 0;
		}
	}

	public long getLastProcessedTimeStamp(MetadataETLHandler handler) {
		try {
			return (Long) attributeService.getAttribute(getETLBaseAppId(),
					handler.getETLHandlerName());
		} catch (NullPointerException ex) {
			return 0;
		}
	}

}
