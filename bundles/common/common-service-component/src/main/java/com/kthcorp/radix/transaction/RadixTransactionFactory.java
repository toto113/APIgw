package com.kthcorp.radix.transaction;

import java.util.Hashtable;
import java.util.Map;

import org.springframework.transaction.NoTransactionException;

public class RadixTransactionFactory {

	private static Map<String, RadixTransactionObject> transactionMap = new Hashtable<String, RadixTransactionObject>();
	
	RadixTransactionObject getTransactionObject() {
		
		RadixTransactionObject transactionObject = null;
		transactionObject = new RadixTransactionObject();
		transactionMap.put(transactionObject.getTransactionId(), transactionObject);
		return transactionObject;
	}
	
	RadixTransactionObject getTransactionObject(String transactionId) {
		
		if(!transactionMap.containsKey(transactionId)) {
			throw new NoTransactionException("cannot find transaction for transactionId " + transactionId);
		}
		return transactionMap.get(transactionId);
	}
	
	void removeTransactionObject(String transactionId) {
		
		transactionMap.remove(transactionId);
	}
}
