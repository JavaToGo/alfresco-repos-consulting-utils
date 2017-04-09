package org.alfresco.consulting.util.tracking;

import java.util.List;

import org.alfresco.repo.domain.node.Transaction;
import org.alfresco.service.cmr.repository.NodeRef.Status;

//Todo Move this over to Consulting Utils in org.
public interface TrackingComponent {
	public long firstTxnTimeStamp();
	public long getTimeStampOfLastTransactionBefore(long ts);
	public long getTimeStampOfLastTransaction();
	public List<Transaction> getTxnsByCommitTimeDescending(long fromTs, long toTs, int txnLimit);
	public List<Transaction> getTxnsByCommitTimeAscending(long fromTs, long toTs, int txnLimit);
	public List<Status>  getTxnChanges(long txId);
}
