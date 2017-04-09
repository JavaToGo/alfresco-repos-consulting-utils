package org.alfresco.consulting.util.reporting_etl.metadata;

import org.alfresco.service.cmr.repository.NodeRef;

public interface MetadataETLHandler {
	boolean extractMetadataEntry(NodeRef nodeRef,long txnId);
	String getETLHandlerName();
	boolean isEnabled();
}
