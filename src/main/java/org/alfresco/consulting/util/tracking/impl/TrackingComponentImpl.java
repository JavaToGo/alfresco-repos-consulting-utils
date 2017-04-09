/**
 * 
 */
package org.alfresco.consulting.util.tracking.impl;

import java.util.Calendar;
import java.util.List;

import org.alfresco.consulting.util.tracking.TrackingComponent;
import org.alfresco.repo.domain.node.NodeDAO;
import org.alfresco.repo.domain.node.Transaction;
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
	private ServiceRegistry serviceRegistry;
	private NodeDAO nodeDAO;

	public void setNodeDAO(NodeDAO nodeDAO) {
		this.nodeDAO = nodeDAO;
	}

	private long now() {
		return Calendar.getInstance().getTimeInMillis();
	}

	@Override
	public long firstTxnTimeStamp() {
        nodeDAO.setCheckNodeConsistency();
		List<Transaction> txnList = nodeDAO.getTxnsByCommitTimeAscending(0L, now(), 1, null, false);
		return txnList.get(0).getCommitTimeMs();
	}

	@Override
	public long getTimeStampOfLastTransactionBefore(long ts) {
        nodeDAO.setCheckNodeConsistency();
		long txnId = nodeDAO.getMaxTxnIdByCommitTime(ts-1);
		return nodeDAO.getTxnById(txnId).getCommitTimeMs();
	}

	@Override
	public long getTimeStampOfLastTransaction() {
        nodeDAO.setCheckNodeConsistency();
		return nodeDAO.getMaxTxnCommitTime();
	}
	

	@Override
	public List<Transaction> getTxnsByCommitTimeDescending(long fromTs, long toTs, int txnLimit) {
        nodeDAO.setCheckNodeConsistency();
		return nodeDAO.getTxnsByCommitTimeDescending(fromTs, toTs, txnLimit, null, false);
	}

	@Override
	public List<Transaction> getTxnsByCommitTimeAscending(long fromTs, long toTs, int txnLimit) {
        nodeDAO.setCheckNodeConsistency();
		return nodeDAO.getTxnsByCommitTimeAscending(fromTs, toTs, txnLimit, null, false);
	}

	@Override
	public List<Status> getTxnChanges(long txId) {
        nodeDAO.setCheckNodeConsistency();
		return nodeDAO.getTxnChanges(txId);
	}

}
