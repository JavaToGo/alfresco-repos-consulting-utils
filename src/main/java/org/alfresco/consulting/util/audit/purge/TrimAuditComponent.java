package org.alfresco.consulting.util.audit.purge;

import java.util.Date;
import java.util.List;

import org.alfresco.repo.audit.AuditComponent;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.ServiceRegistry;

public class TrimAuditComponent {
	AuditComponent auditComponent;
	ServiceRegistry serviceRegistry;
	RetryingTransactionHelper retryingTransactionHelper;
	public void setAuditComponent(AuditComponent auditComponent) {
		this.auditComponent = auditComponent;
	}
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
		this.retryingTransactionHelper = this.serviceRegistry.getRetryingTransactionHelper();
	}
	private long getToTime(long keepSeconds) {
		return  System.currentTimeMillis() - keepSeconds*1000;		

	}
	private int doCleanupInt(long toTime,String applicationName) {
		return auditComponent.deleteAuditEntries(applicationName,null , toTime);
	}
	public int doCleanup(long keepSeconds,String applicationName) {
		if (applicationName == null) {
			return 0;
		}
		return doCleanupInt(getToTime(keepSeconds), applicationName);
	}
	public int doCleanup(long keepSeconds,List<String> applicationNames) {
		if (applicationNames == null) {
			return 0;
		}
		long toTime = getToTime(keepSeconds);
		int total = 0;
		for (String applicationName : applicationNames) {
			total += doCleanupInt(toTime, applicationName);;
		}	
		return total;
	}
	public int doCleanupTxn(final long keepSeconds,final String applicationName) {
		return retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Integer> () {

			@Override
			public Integer execute() throws Throwable {
				return doCleanup(keepSeconds,applicationName);
			}
			
		});
	}
	public int doCleanupTxn(final long keepSeconds,final List<String> applicationNames) {
		return retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Integer> () {

			@Override
			public Integer execute() throws Throwable {
				return doCleanup(keepSeconds,applicationNames);
			}
			
		});
	}

}
