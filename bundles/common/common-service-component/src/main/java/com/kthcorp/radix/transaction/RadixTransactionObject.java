package com.kthcorp.radix.transaction;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.kthcorp.radix.domain.stat.RadixTransactionLog;


public class RadixTransactionObject {

	private static final Logger radixTransactionLogger = UuidViewableLoggerFactory.getLogger("transaction");
	
	private String transactionId = UUID.randomUUID().toString();
	
	private RadixTransactionLog radixTransactionLog = new RadixTransactionLog(transactionId);
	
	RadixTransactionLog getTransactionLog() {
		return this.radixTransactionLog;
	}
	
	public void begin() {
		
		this.radixTransactionLog.setRequestTime(new Date());
	}
	
	public void commit() {
		
		radixTransactionLogger.info("{}", radixTransactionLog.getFormatted());
	}
	
	public void rollback() {
		
		radixTransactionLogger.info("{}", radixTransactionLog.getFormatted());
	}		
	
	public String getTransactionId() {
		return transactionId;
	}

	public RadixTransactionLog getRadixTransactionLog() {
		return radixTransactionLog;
	}

	public void setRadixTransactionLog(RadixTransactionLog radixTransactionLog) {
		this.radixTransactionLog = radixTransactionLog;
	}
}
