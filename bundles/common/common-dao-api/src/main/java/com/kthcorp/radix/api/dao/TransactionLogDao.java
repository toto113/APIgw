package com.kthcorp.radix.api.dao;

import com.kthcorp.radix.domain.exception.RadixException;
import com.kthcorp.radix.domain.stat.RadixTransactionLog;

public interface TransactionLogDao {
	
	public void insertTransactionLog(String messageId, RadixTransactionLog transactionLog) throws RadixException;
	
	public RadixTransactionLog selectTransactionLog(String messageId) throws RadixException;
	
	public void updateTransactionLog(String messageId, RadixTransactionLog transactionLog) throws RadixException;
	
	public void deleteTransactionLog(String messageId) throws RadixException;
	
}
