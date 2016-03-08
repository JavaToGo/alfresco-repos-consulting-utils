package org.alfresco.consulting.util.transactions.impl;

import java.util.List;

import org.alfresco.consulting.util.transactions.BulkUpdater;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;

public class BulkUpdaterImpl implements BulkUpdater {
	private static final int BATCH_SIZE=100;
	
	RetryingTransactionHelper txnHelper;

	@Override
	public <T> int bulkUpdate(final List<T> items, final BulkUpdaterCallback<T> cb) {
		int startIdx=0;
		int pCount=0;
		for (startIdx=0;startIdx < items.size();startIdx += BATCH_SIZE) {
			final int myStartIdx=startIdx;
			pCount = txnHelper.doInTransaction(new RetryingTransactionCallback<Integer>(){

				@Override
				public Integer execute() throws Throwable {
					int i;
					for (i=myStartIdx;i<Math.min(myStartIdx+BATCH_SIZE,items.size());i++) {
						cb.executeUpdate(items.get(i));
					}
					return i;
				}
			});
		}
		return pCount;
	}

}
