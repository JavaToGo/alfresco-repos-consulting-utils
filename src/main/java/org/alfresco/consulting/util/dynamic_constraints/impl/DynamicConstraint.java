package org.alfresco.consulting.util.dynamic_constraints.impl;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.consulting.util.dynamic_constraints.DynamicConstraintService;
import org.alfresco.repo.dictionary.constraint.AbstractConstraint;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.ConstraintException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;


public class DynamicConstraint extends AbstractConstraint {
    private static DynamicConstraintService dynamicConstraintService;
    private static ServiceRegistry serviceRegistry;
    private static NamespaceService namespaceService;
	private String constraintName;

	public void setConstraintName(String constraintName) {
		this.constraintName = constraintName;

	}

	public static void setDynamicConstraintService(
			DynamicConstraintService dynamicConstraintService) {
		DynamicConstraint.dynamicConstraintService = dynamicConstraintService;
	}

   public static void setServiceRegistry(ServiceRegistry serviceRegistry) {
		DynamicConstraint.serviceRegistry = serviceRegistry;
		DynamicConstraint.namespaceService = serviceRegistry.getNamespaceService();
	}

@Override
    public Map<String, Object> getParameters()
    {
        Map<String, Object> params = new HashMap<String, Object>(2);
        
        params.put("constraintName", this.constraintName);
        
        return params;
    }
	// TODO -- make this work for other than strings.....
	@Override
	protected void evaluateSingleValue(Object value) {
		try {
			dynamicConstraintService.validateDynamicConstraint(QName.createQName(constraintName,namespaceService), (String) value);
		} catch (DynamicConstraintException ex) {
			throw new ConstraintException(ex.getMessage(), value);
		}

	}

}
