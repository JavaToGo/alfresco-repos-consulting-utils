package org.alfresco.consulting.util.unique_property;

import java.io.Serializable;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public interface UniquePropertyHandler {
	boolean isIdGlobal(QName propName);
	void onCreateProperty(QName propName, Serializable value, NodeRef parent, NodeRef node);
	void onUpdateProperty(QName propName, Serializable oldValue, Serializable newValue, NodeRef oldParent, NodeRef newParent, NodeRef node);
	void onDeleteProperty(QName propName, Serializable value, NodeRef parent, NodeRef node);
}
