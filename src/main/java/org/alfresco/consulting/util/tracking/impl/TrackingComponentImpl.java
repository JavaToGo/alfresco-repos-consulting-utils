/**
 * 
 */
package org.alfresco.consulting.util.tracking.impl;

import java.util.Calendar;
import java.util.List;

import org.alfresco.consulting.util.tracking.TrackingComponent;
import org.alfresco.repo.domain.node.NodeDAO;
import org.alfresco.repo.domain.node.Transaction;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.repository.NodeRef.Status;
import org.alfresco.service.cmr.search.SearchService;

/**
 * @author rmcknight
 *
 * Some code modeled after org.alfresco.repo.solr.SOLRTrackingComponentImpl
 */
public class TrackingComponentImpl implements TrackingComponent {
	private NodeDAO nodeDAO;
	RetryingTransactionHelper txnHelper;
	ServiceRegistry serviceRegistry;
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
		this.txnHelper = this.serviceRegistry.getRetryingTransactionHelper();
	}

	public void setNodeDAO(NodeDAO nodeDAO) {
		this.nodeDAO = nodeDAO;
	}

	private long now() {
		return Calendar.getInstance().getTimeInMillis();
	}

	@Override
	public long firstTxnTimeStamp() {
		return txnHelper.doInTransaction(new RetryingTransactionCallback<Long>(){

			@Override
			public Long execute() throws Throwable {
		        nodeDAO.setCheckNodeConsistency();
				List<Transaction> txnList = nodeDAO.getTxnsByCommitTimeAscending(0L, now(), 1, null, false);
				return txnList.get(0).getCommitTimeMs();
			}
		});
	}

	@Override
	public long getTimeStampOfLastTransactionBefore(final long ts) {
		return txnHelper.doInTransaction(new RetryingTransactionCallback<Long>(){

			@Override
			public Long execute() throws Throwable {
		        nodeDAO.setCheckNodeConsistency();
				long txnId = nodeDAO.getMaxTxnIdByCommitTime(ts-1);
				return nodeDAO.getTxnById(txnId).getCommitTimeMs();
			}
		});
	}

	@Override
	public long getTimeStampOfLastTransaction() {
		return txnHelper.doInTransaction(new RetryingTransactionCallback<Long>(){

			@Override
			public Long execute() throws Throwable {
		        nodeDAO.setCheckNodeConsistency();
				return nodeDAO.getMaxTxnCommitTime();
			}
		});
	}
	

	@Override
	public List<Transaction> getTxnsByCommitTimeDescending(final long fromTs, final long toTs, final int txnLimit) {
		return txnHelper.doInTransaction(new RetryingTransactionCallback<List<Transaction>>(){

			@Override
			public List<Transaction> execute() throws Throwable {
		        nodeDAO.setCheckNodeConsistency();
				return nodeDAO.getTxnsByCommitTimeDescending(fromTs, toTs, txnLimit, null, false);
			}
		});
	}

	@Override
	public List<Transaction> getTxnsByCommitTimeAscending(final long fromTs, final long toTs, final int txnLimit) {
		return txnHelper.doInTransaction(new RetryingTransactionCallback<List<Transaction>>(){

			@Override
			public List<Transaction> execute() throws Throwable {
		        nodeDAO.setCheckNodeConsistency();
				return nodeDAO.getTxnsByCommitTimeAscending(fromTs, toTs, txnLimit, null, false);
			}
		});
	}

	@Override
	public List<Status> getTxnChanges(final long txId) {
		return txnHelper.doInTransaction(new RetryingTransactionCallback<List<Status>>(){

			@Override
			public List<Status> execute() throws Throwable {
		        nodeDAO.setCheckNodeConsistency();
				return nodeDAO.getTxnChanges(txId);
			}
		});
	}

}
