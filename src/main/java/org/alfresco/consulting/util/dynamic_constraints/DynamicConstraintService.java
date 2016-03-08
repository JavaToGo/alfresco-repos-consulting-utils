package org.alfresco.consulting.util.dynamic_constraints;

import java.io.Serializable;
import java.util.Map;
import org.alfresco.service.namespace.QName;

public interface DynamicConstraintService {
	public void createListConstraint(QName constraintName,QName dataType);
	/* public void createTreeConstraint(QName constraintName);
	public void createCompositeConstraint(String constraintName,List<QName> componentConstraints); */
	public boolean listConstraintExists(QName constraintName);
	public void addValueToList(QName constraintName,Serializable value);
	public void addValueToList(QName constraintName,Serializable value,String displayValue);
	public void addValueToList(QName constraintName,Serializable value,String displayValue, boolean enabled);
	public void addValueToList(QName constraintName,Serializable value, boolean enabled);
	public void removeValueFromList(QName constraintName,Serializable value);
	public void disableValue(QName constraintName,Serializable value);
	public void enableValue(QName constraintName,Serializable value);
	public String getValueFromList(QName constraintName,Serializable value);
	public void validateDynamicConstraint(QName constraintName,String constraintValue);
	public boolean valueExists(QName constraintName,String constraintValue);
	public boolean valueEnabled(QName constraintName,String constraintValue);
	public Map<Serializable,String> getListConstraintMap(QName constraintName);
	public String getListConstraintJSON(QName constraintName);
}
