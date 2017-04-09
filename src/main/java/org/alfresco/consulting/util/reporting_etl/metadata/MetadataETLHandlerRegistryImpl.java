package org.alfresco.consulting.util.reporting_etl.metadata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MetadataETLHandlerRegistryImpl implements MetadataETLHandlerRegistry {
	
	Map<String,Set<MetadataETLHandler>> handlerCache = new HashMap<String,Set<MetadataETLHandler>>();
	Map<String,MetadataETLHandler> handlers = new HashMap<String,MetadataETLHandler> ();

	private static final Log logger = LogFactory.getLog(MetadataETLHandlerRegistryImpl.class);
	

	@Override
	public void registerHandler(MetadataETLHandler h) {
		handlers.put(h.getETLHandlerName(),h);
		//Clear Cache when adding new hander
		synchronized(this) {
		    handlerCache.clear();
		}
	}


	@Override
	public Map<String, MetadataETLHandler> getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}
	


}
