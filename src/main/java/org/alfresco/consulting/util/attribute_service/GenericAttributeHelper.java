package org.alfresco.consulting.util.attribute_service;

import java.io.Serializable;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class GenericAttributeHelper {
	abstract protected Serializable getAppId();
	protected void initServiceRegistry(ServiceRegistry s) {
		attributeService = s.getAttributeService();
	}
	private AttributeService attributeService;
    private static Log logger = LogFactory.getLog(GenericAttributeHelper.class);

	protected void storeUniqueId(Serializable uniqueAttrName, Serializable id, Serializable value) {
        // Do not overwrite the attribute unless the values are the same
    	if (logger.isDebugEnabled()) {
    		logger.debug("Storing: " +value + " at " + getAppId() + ":" + uniqueAttrName + ":" +id);
    	}
        if (attributeService.exists(getAppId(), uniqueAttrName, id)) {
            logger.info("**POTENTIAL -- Duplicate Unique id:" + getAppId() +":"+  uniqueAttrName + ":" +id);
            if (!attributeService.getAttribute(getAppId(), uniqueAttrName, id).equals(value)) {
                logger.error("Actual -- Duplicate Unique id:" + getAppId() +":"+  uniqueAttrName + ":" +id);
                throw new DuplicateUniqueIdRuntimeException("Duplicate Unique id:" + getAppId() +":"+  uniqueAttrName + ":" +id);
            }
        }
        logger.debug("Adding Unique id:" + getAppId() +":"+  uniqueAttrName + ":" +id);
        attributeService.setAttribute(value, getAppId(), uniqueAttrName, id);
    }

    protected Serializable getValueByUniqueId(Serializable uniqueAttrName, Serializable id) {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Getting: " + getAppId() + ":" + uniqueAttrName + ":" +id);
    	}
    	Serializable value = attributeService.getAttribute(getAppId(), uniqueAttrName, id);
    	if (logger.isDebugEnabled()) {
    		logger.debug("Returning: " + value);
    	}
        return value;
    }
    
    protected boolean hasValue(Serializable uniqueAttrName, Serializable id) {
    	return attributeService.exists(getAppId(), uniqueAttrName, id);
    }

    protected void clearUniqueId(Serializable uniqueAttrName, Serializable id) {
        //TODO: Check to make sure that the node is no longer there
    	if (logger.isDebugEnabled()) {
    		logger.debug("Clearing: " + getAppId() + ":" + uniqueAttrName + ":" +id);
    	}
        attributeService.removeAttribute(getAppId(), uniqueAttrName, id);
    }

}
