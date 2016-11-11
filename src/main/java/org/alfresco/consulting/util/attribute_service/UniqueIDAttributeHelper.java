package org.alfresco.consulting.util.attribute_service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class UniqueIDAttributeHelper extends GenericAttributeHelper {
	abstract protected Serializable getAppId();
	protected void initServiceRegistry(ServiceRegistry s) {
		super.initServiceRegistry(s);
		attributeService = s.getAttributeService();
	}
	private AttributeService attributeService;
	private static final String MAX_ID_ATTR="..MAX_ID..";
    private static Log logger = LogFactory.getLog(UniqueIDAttributeHelper.class);

	protected void storeUniqueId(Serializable uniqueAttrName, Serializable id, NodeRef nodeRef) {
         super.storeUniqueId(uniqueAttrName,id,nodeRef);
    }

    protected NodeRef getNodeRefByUniqueId(Serializable uniqueAttrName, Serializable id) {
    	return (NodeRef) super.getValueByUniqueId(uniqueAttrName, id);
    }

    protected void clearUniqueId(Serializable uniqueAttrName, Serializable id) {
        super.clearUniqueId(uniqueAttrName, id);
    }
    private long getMaxSequenceId(Serializable uniqueAttrName) {
        if (attributeService.exists(getAppId(), uniqueAttrName, MAX_ID_ATTR)) {
            return (Long) attributeService.getAttribute(getAppId(), uniqueAttrName, MAX_ID_ATTR);
        }
        return 0;
    }

    private long findMaxSequenceId(Serializable uniqueAttrName) {
        //TODO: fix this hack -- had to use something that was final and a Long could not be updated
        final List<Long> l = new ArrayList<Long>();
        l.add(0, 0L);
        attributeService.getAttributes(new AttributeService.AttributeQueryCallback() {
            @Override
            public boolean handleAttribute(Long id, Serializable value,
                    Serializable[] keys) {
            	if (MAX_ID_ATTR.equals(keys[2])) {
            		return true;
            	}
            	if (keys[2] == null) {
            		return true;
            	}
            	if (keys[2] instanceof Long) {
	                Long myVal = (Long) keys[2];
	                if (myVal > l.get(0)) {
	                    l.remove(0);
	                    l.add(0, myVal);
	                }
	                return true;
            	}
            	return true;
            }
        }, getAppId(), uniqueAttrName);
        return l.get(0);
    }

    protected long getNextSequenceId(Serializable uniqueAttrName) {
        long nextId = getMaxSequenceId(uniqueAttrName) + 1;
        attributeService.setAttribute((Long) nextId, getAppId(), uniqueAttrName, MAX_ID_ATTR);
        if (getNodeRefByUniqueId(uniqueAttrName, nextId) == null) {
            return nextId;
        }
        // Cycle through if max was not updated
        nextId = findMaxSequenceId(uniqueAttrName) + 1;
        attributeService.setAttribute((Long) nextId,getAppId(), uniqueAttrName, MAX_ID_ATTR);
        if (getNodeRefByUniqueId(uniqueAttrName, nextId) == null) {
            return nextId;
        }
        //TODO: fix this to recover
        throw new RuntimeException("Sequence Counter Error");
    }

}
