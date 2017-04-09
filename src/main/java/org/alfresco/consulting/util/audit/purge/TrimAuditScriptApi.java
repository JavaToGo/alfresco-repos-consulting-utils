package org.alfresco.consulting.util.audit.purge;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.ServiceRegistry;

public class TrimAuditScriptApi extends BaseScopableProcessorExtension {
	TrimAuditComponent trimAuditComponent;
	ServiceRegistry serviceRegistry;
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setTrimAuditComponent(TrimAuditComponent trimAuditComponent) {
		this.trimAuditComponent = trimAuditComponent;
	}

	public int doCleanup(long keepSeconds,String applicationName) {
		return trimAuditComponent.doCleanup(keepSeconds, applicationName);
		
	}
	

}
