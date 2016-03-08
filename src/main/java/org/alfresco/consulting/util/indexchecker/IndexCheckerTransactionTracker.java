package org.alfresco.consulting.util.indexchecker;

import org.alfresco.repo.domain.node.Transaction;
import org.alfresco.repo.node.index.IndexTransactionTracker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndexCheckerTransactionTracker extends IndexTransactionTracker {
    private static Log logger = LogFactory.getLog(IndexCheckerTransactionTracker.class);

	@Override
	public InIndex isTxnPresentInIndex(Transaction txn) {
		InIndex retval =super.isTxnPresentInIndex(txn);
		boolean missing = retval.equals(InIndex.NO);
		if (missing && logger.isWarnEnabled()) {
            logger.warn("IndexChecker - Missing - Txn: " + txn.getId() + " (" +  txn.getChangeTxnId() + ")");
		} else if (logger.isInfoEnabled() && !missing) {
            logger.info("IndexChecker - Found - Txn: " + txn.getId() + " (" +  txn.getChangeTxnId() + ")");
		}
		return retval;
	}

}
