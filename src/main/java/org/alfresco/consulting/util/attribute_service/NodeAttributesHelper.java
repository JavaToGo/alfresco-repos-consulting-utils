package org.alfresco.consulting.util.attribute_service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.attributes.AttributeService.AttributeQueryCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class NodeAttributesHelper implements AttributeApplication {
    static final String CONSULTING_UTILS_1_0_URI = "http://www.alfresco.com/consulting/utils/model/1.0";
    static final String CONSULTING_UTILS_PREFIX = "alfconsult";
    static final QName NODE_ATTRIBUTE_APPLICATION = QName.createQName(CONSULTING_UTILS_1_0_URI, "nodeAttributes");
    private static Log logger = LogFactory.getLog(NodeAttributesHelper.class);
    
    private ServiceRegistry serviceRegistry;
	private AttributeService attributeService;
	public interface NodeAttributeCallback {
		void onStart();
		boolean processAttribute(QName attribute,NodeRef node,Serializable value,long id);
		void onEnd();
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
		this.attributeService = this.serviceRegistry.getAttributeService();
	}

	@Override
	public QName getApplicationName() {
		return NODE_ATTRIBUTE_APPLICATION;
	}
	public Boolean isNodeInCollection(NodeRef nodeRef, QName collectionName) {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Getting: " + getApplicationName() + ":" + collectionName + ":" +nodeRef);
    	}
    	Boolean value = (Boolean) attributeService.getAttribute(getApplicationName(), collectionName, nodeRef);
    	if (logger.isDebugEnabled()) {
    		logger.debug("Returning: " + value);
    	}
        return value;
	}
	public void addNodeToCollection(NodeRef nodeRef, QName collectionName) {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Adding: "  + getApplicationName() + ":" + collectionName + ":" +nodeRef);
    	}
        attributeService.setAttribute(true, getApplicationName(), collectionName, nodeRef);		
	}
	
	public void removeNodeFromCollection(NodeRef nodeRef, QName collectionName) {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Removing: " + getApplicationName() + ":" + collectionName + ":" +nodeRef);
    	}
        attributeService.removeAttribute(getApplicationName(), collectionName, nodeRef);		
	}
	
	//TODO: check to see if the node is in the group before enabling/disabling
	public void enableNodeInCollection(NodeRef nodeRef, QName collectionName) {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Enabling: " + getApplicationName() + ":" + collectionName + ":" +nodeRef);
    	}
        attributeService.setAttribute(true, getApplicationName(), collectionName, nodeRef);		
	}
	
	public void disableNodeInCollection(NodeRef nodeRef, QName collectionName) {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Disabling: " + getApplicationName() + ":" + collectionName + ":" +nodeRef);
    	}
        attributeService.setAttribute(false, getApplicationName(), collectionName, nodeRef);		
	}
	
	public void setAttribute(NodeRef nodeRef, QName attributeName, Serializable value) {
        // Do not overwrite the attribute unless the values are the same
    	if (logger.isDebugEnabled()) {
    		logger.debug("Setting: " +value + " at " + getApplicationName() + ":" + nodeRef + ":" +attributeName);
    	}
        attributeService.setAttribute(value, getApplicationName(), nodeRef, attributeName);
    }

	public Serializable getAttribute(NodeRef nodeRef, QName attributeName) {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Getting: " + getApplicationName() + ":" + nodeRef + ":" +attributeName);
    	}
    	Serializable value = attributeService.getAttribute(getApplicationName(), nodeRef, attributeName);
    	if (logger.isDebugEnabled()) {
    		logger.debug("Returning: " + value);
    	}
        return value;
    }
    
	public void setSerializedAttribute(NodeRef nodeRef, QName attributeName, Serializable value) throws IOException {
        // Do not overwrite the attribute unless the values are the same
    	if (logger.isDebugEnabled()) {
    		logger.debug("Setting: " +value + " at " + getApplicationName() + ":" + nodeRef + ":" +attributeName);
    	}
        attributeService.setAttribute(serialize(value), getApplicationName(), nodeRef, attributeName);
    }

	public Serializable getSerializedAttribute(NodeRef nodeRef, QName attributeName) throws  ClassNotFoundException, IOException {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Getting: " + getApplicationName() + ":" + nodeRef + ":" +attributeName);
    	}
    	Serializable value = (Serializable) deserialize((byte[]) attributeService.getAttribute(getApplicationName(), nodeRef, attributeName));
    	if (logger.isDebugEnabled()) {
    		logger.debug("Returning: " + value);
    	}
        return value;
    }
	public boolean exists(NodeRef nodeRef, QName attributeName) {
    	return attributeService.exists(getApplicationName(), nodeRef, attributeName);
    }

	public void removeAttribute(NodeRef nodeRef, QName attributeName) {
        //TODO: Check to make sure that the node is no longer there
    	if (logger.isDebugEnabled()) {
    		logger.debug("Removing: " + getApplicationName() + ":" + nodeRef + ":" +attributeName);
    	}
        attributeService.removeAttribute(getApplicationName(), nodeRef, attributeName);
    }
	
	//Helper functions that will allow for serialization of objects to avoid having things like hashmaps
	//Spawn multiple tables
    public static byte[] serialize(Object obj) throws IOException {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream o = new ObjectInputStream(b)){
                return o.readObject();
            }
        }
    }

    
    public void processCollection(final NodeAttributeCallback callback,QName collectionName) {
    	callback.onStart();
    	AttributeQueryCallback aqcb = new AttributeQueryCallback() {

			@Override
			public boolean handleAttribute(Long id, Serializable value, Serializable[] keys) {
				return callback.processAttribute((QName) keys[1], (NodeRef) keys[2], value, id);
			}
    		
    	};
    	attributeService.getAttributes(aqcb,getApplicationName(),collectionName);
    	callback.onEnd();
    }
	
}
