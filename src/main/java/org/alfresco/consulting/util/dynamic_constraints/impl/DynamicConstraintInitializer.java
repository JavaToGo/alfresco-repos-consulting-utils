package org.alfresco.consulting.util.dynamic_constraints.impl;

import org.alfresco.consulting.util.dynamic_constraints.DynamicConstraintService;
import org.alfresco.service.ServiceRegistry;


public class DynamicConstraintInitializer {
	public void setDynamicConstraintService(
			DynamicConstraintService dynamicConstraintService) {
		DynamicConstraint.setDynamicConstraintService(dynamicConstraintService);
	}
	   public  void setServiceRegistry(ServiceRegistry serviceRegistry) {
			DynamicConstraint.setServiceRegistry(serviceRegistry);
		}

}
