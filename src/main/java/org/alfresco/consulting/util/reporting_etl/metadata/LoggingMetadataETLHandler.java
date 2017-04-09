package org.alfresco.consulting.util.reporting_etl.metadata;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

public class LoggingMetadataETLHandler extends MetadataETLHandlerBase {
	
	public final static String NAME="LOGGING_METADATA";
	private static final Log logger = LogFactory.getLog(LoggingMetadataETLHandler.class);
	private boolean enabled=false;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String getETLHandlerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean extractMetadataEntry(NodeRef nodeRef, long txnId) {
		// TODO Auto-generated method stub
		return false;
	}

}