package org.alfresco.consulting.util.reporting_etl.metadata;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
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
		return NAME;
	}
	
	JSONObject nodeToJsonLine(NodeRef nodeRef) {
		NodeService nodeService = getNodeService();
		JSONObject jobj = new JSONObject();
		try {
			Set<QName> aspectSet = nodeService.getAspects(nodeRef);
			JSONArray aspectArray = new JSONArray(aspectSet);
			jobj.put("aspects",aspectArray);
			Map<QName,Serializable> props = nodeService.getProperties(nodeRef);
			jobj.put("properties", props);
			jobj.put("childAssocs", nodeService.getChildAssocs(nodeRef));
			jobj.put("parentAssocs", nodeService.getParentAssocs(nodeRef));
			jobj.put("targetAssocs", nodeService.getTargetAssocs(nodeRef, RegexQNamePattern.MATCH_ALL));
			jobj.put("sourceAssocs", nodeService.getSourceAssocs(nodeRef, RegexQNamePattern.MATCH_ALL));
			
		} catch (JSONException e) {
			logger.error("Error processing Node: " + nodeRef.toString(),e);
		}
		return jobj;
	}

	@Override
	public boolean extractMetadataEntry(NodeRef nodeRef, boolean isDeleted, long txnId) {
		if (logger.isDebugEnabled()) {
			if (isDeleted) {
				logger.debug(String.format("%s: %d: %s: *** DELETED ***",NAME,txnId,nodeRef.toString()));
			} else {
				NodeService nodeService = getNodeService();		
				JSONObject obj = nodeToJsonLine(nodeRef);
				logger.debug(String.format("%s: %d: %s, %s, %s, %s: %s",NAME,txnId,nodeRef.toString(),nodeService.getProperty(nodeRef,ContentModel.PROP_NAME),nodeService.getType(nodeRef),nodeService.getPath(nodeRef),obj.toString()));
			}
		}
		return true;
	}

}