package org.alfresco.consulting.util.attribute_service;

import java.io.Serializable;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.attributes.AttributeService;
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

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
		this.attributeService = this.serviceRegistry.getAttributeService();
	}

	@Override
	public QName getApplicationName() {
		return NODE_ATTRIBUTE_APPLICATION;
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

	
}
