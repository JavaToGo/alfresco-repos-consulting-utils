package org.alfresco.consulting.util.unique_property;

import java.io.Serializable;

import org.alfresco.consulting.util.attribute_service.UniqueIDAttributeHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public class UniquePropertyManager extends UniqueIDAttributeHelper {
	
	public void setServiceRegistry(ServiceRegistry s) {
		initServiceRegistry(s);
	}

	@Override
	protected Serializable getAppId() {
		return this.getClass().getCanonicalName();
	}
	
	public NodeRef getGlobalNodeById(QName propertyName,Serializable id) {
		return getNodeRefByUniqueId(propertyName, id);
	}

	public Long getNextGlobalSequenceId(QName propertyName) {
		return getNextSequenceId(propertyName);
	}

	public void storeGlobalUniqueId(QName propertyName, Serializable id, NodeRef nodeRef) {
        storeUniqueId(propertyName,id,nodeRef);
    }

    public void clearGlobalUniqueId(QName propertyName, Serializable id) {
       clearUniqueId(propertyName, id);
    }
    
	public NodeRef getScopedNodeById(NodeRef parent,QName propertyName,Serializable id) {
		Serializable[] scopedPropertyName = {parent, propertyName };
		return getNodeRefByUniqueId(scopedPropertyName, id);
	}

	public Long getNextScopedSequenceId(NodeRef parent,QName propertyName) {
		Serializable[] scopedPropertyName = {parent, propertyName };
		return this.getNextSequenceId(scopedPropertyName);
	}
	
	public void storeScopedUniqueId(NodeRef parent,QName propertyName, Serializable id, NodeRef nodeRef) {
		Serializable[] scopedPropertyName = {parent, propertyName };
        storeUniqueId(scopedPropertyName,id,nodeRef);
    }

    public void clearScopedUniqueId(NodeRef parent,QName propertyName, Serializable id) {
		Serializable[] scopedPropertyName = {parent, propertyName };
       clearUniqueId(scopedPropertyName, id);
    }
    
}
