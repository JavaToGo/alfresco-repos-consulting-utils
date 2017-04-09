package org.alfresco.consulting.util.reporting_etl.metadata;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class MetadataETLHandlerBase implements MetadataETLHandler  {
	
	MetadataETLHandlerRegistry metadataETLHandlerRegistry;
	
	public void setAuditETLHandlerRegistry(MetadataETLHandlerRegistry metadataETLHandlerRegistry) {
		this.metadataETLHandlerRegistry = metadataETLHandlerRegistry;
		this.metadataETLHandlerRegistry.registerHandler(this);
	}

	@Override
	public abstract String getETLHandlerName();
	
	@Override
	public boolean isEnabled() {
		return true;
	}


}
