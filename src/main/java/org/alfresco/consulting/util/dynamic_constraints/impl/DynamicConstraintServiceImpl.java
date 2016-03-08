package org.alfresco.consulting.util.dynamic_constraints.impl;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.consulting.util.dynamic_constraints.DynamicConstraintService;
import org.alfresco.service.namespace.QName;
import org.json.JSONObject;

public class DynamicConstraintServiceImpl implements DynamicConstraintService {
	
	private DynamicConstraintAttributeHelper attributeComponent;

	public void setAttributeComponent(
			DynamicConstraintAttributeHelper attributeComponent) {
		this.attributeComponent = attributeComponent;
	}

	@Override
	public void createListConstraint(QName constraintName, QName dataType) {
		attributeComponent.createListConstraint(constraintName, dataType);
	}

	@Override
	public void addValueToList(QName constraintName, Serializable value) {
		attributeComponent.addValueToList(constraintName, value);
	}

	@Override
	public void addValueToList(QName constraintName, Serializable value, String displayValue) {
		attributeComponent.addValueToList(constraintName, value, displayValue);
	}

	@Override
	public void removeValueFromList(QName constraintName, Serializable value) {
		attributeComponent.removeValueFromList(constraintName, value);
	}

	@Override
	public String getValueFromList(QName constraintName, Serializable value) {
		return attributeComponent.getDisplayValueFromList(constraintName, value);
	}

	@Override
	public void validateDynamicConstraint(QName constraintName, String constraintValue) {
		if (!attributeComponent.validateDynamicConstraint(constraintName, constraintValue)) {
			throw new DynamicConstraintException("Value: " + constraintValue + " not found for constraint " + constraintName);
		}
	}

	@Override
	public Map<Serializable, String> getListConstraintMap(QName constraintName) {
		return attributeComponent.getListConstraintMap(constraintName);
 	}

	@Override
	public String getListConstraintJSON(QName constraintName) {
	    JSONObject json = new JSONObject(getListConstraintMap(constraintName));
	    return json.toString();
	}

	@Override
	public boolean listConstraintExists(QName constraintName) {
		//Empty == Nonexistent
		return 0 != getListConstraintMap(constraintName).size();
	}

	@Override
	public void addValueToList(QName constraintName, Serializable value,
			String displayValue, boolean enabled) {
		throw new DynamicConstraintException("Enable/Disable Capability Not Implemented");
	}

	@Override
	public void addValueToList(QName constraintName, Serializable value,
			boolean enabled) {
		throw new DynamicConstraintException("Enable/Disable Capability Not Implemented");
	}

	@Override
	public void disableValue(QName constraintName, Serializable value) {
		throw new DynamicConstraintException("Enable/Disable Capability Not Implemented");
	}

	@Override
	public void enableValue(QName constraintName, Serializable value) {
		throw new DynamicConstraintException("Enable/Disable Capability Not Implemented");
	}

	@Override
	public boolean valueExists(QName constraintName, String constraintValue) {
		return attributeComponent.validateDynamicConstraint(constraintName, constraintValue);
	}

	@Override
	public boolean valueEnabled(QName constraintName, String constraintValue) {
		throw new DynamicConstraintException("Enable/Disable Capability Not Implemented");
	}

}
