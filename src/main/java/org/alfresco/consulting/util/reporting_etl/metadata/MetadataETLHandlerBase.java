package org.alfresco.consulting.util.reporting_etl.metadata;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class MetadataETLHandlerBase implements MetadataETLHandler  {
	
	MetadataETLHandlerRegistry metadataETLHandlerRegistry;
	
	private ServiceRegistry serviceRegistry;
	private NodeService nodeService;
	
	public void setMetadataETLHandlerRegistry(MetadataETLHandlerRegistry metadataETLHandlerRegistry) {
		this.metadataETLHandlerRegistry = metadataETLHandlerRegistry;
		this.metadataETLHandlerRegistry.registerHandler(this);
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
		this.nodeService = this.serviceRegistry.getNodeService();
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	@Override
	public abstract String getETLHandlerName();
	
	@Override
	public boolean isEnabled() {
		return true;
	}


}
