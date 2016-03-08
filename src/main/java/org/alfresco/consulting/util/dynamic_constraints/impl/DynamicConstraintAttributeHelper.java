package org.alfresco.consulting.util.dynamic_constraints.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.consulting.util.ConsultingUtilsConstants;
import org.alfresco.consulting.util.attribute_service.GenericAttributeHelper;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DynamicConstraintAttributeHelper extends GenericAttributeHelper {
    private static Log logger = LogFactory.getLog(DynamicConstraintAttributeHelper.class);
    ServiceRegistry serviceRegistry;
    AttributeService attributeService;
    
    public void setServiceRegistry (ServiceRegistry serviceRegistry) {
    	this.serviceRegistry = serviceRegistry;
    	this.attributeService = this.serviceRegistry.getAttributeService();
    	super.initServiceRegistry(this.serviceRegistry);
    }

	public void createListConstraint(QName constraintName,QName dataType) {
		this.storeUniqueId(constraintName, ConsultingUtilsConstants.DYNAMIC_CONSTRAINT_DATA_TYPE, dataType);
	}

	public void addValueToList(QName constraintName,Serializable value) {
		this.storeUniqueId(constraintName, value, value.toString());
	}
	public void addValueToList(QName constraintName,Serializable value,String displayValue) {
		this.storeUniqueId(constraintName, value, displayValue);
	}
	public void removeValueFromList(QName constraintName,Serializable value) {
		this.clearUniqueId(constraintName, value);
	}
	public String getDisplayValueFromList(QName constraintName,Serializable value) {
		Object ret =getValueByUniqueId(constraintName, value);
		if (ret == null) return null;
		return ret.toString();
	}
	public boolean validateDynamicConstraint(QName constraintName,String constraintValue) {
		return this.hasValue(constraintName, constraintValue);
	}
	public Map<Serializable,String> getListConstraintMap(QName constraintName) {
	       final Map<Serializable, String> map = new HashMap<Serializable, String>();
	         attributeService.getAttributes(new AttributeService.AttributeQueryCallback() {
	            @Override
	            public boolean handleAttribute(Long id, Serializable value,
	                    Serializable[] keys) {
	            	if (ConsultingUtilsConstants.DYNAMIC_CONSTRAINT_DATA_TYPE.equals(keys[2])) {
	            		return true;
	            	}
	            	if (keys[2] == null) {
	            		return true;
	            	}
	            	if (keys[2] instanceof Serializable) { // Check based upon ConsultingUtilsConstants.DYNAMIC_CONSTRAINT_DATA_TYPE
		                map.put(keys[2], value.toString());
		                return true;
	            	}
	            	return true;
	            }
	        }, getAppId(), constraintName);
	        return map;
	}

	@Override
	protected Serializable getAppId() {
		return ConsultingUtilsConstants.DYNAMIC_CONSTRAINT;
	}
}
