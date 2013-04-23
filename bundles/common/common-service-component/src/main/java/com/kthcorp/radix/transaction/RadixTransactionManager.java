package com.kthcorp.radix.transaction;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.exception.RadixException;
import com.kthcorp.radix.domain.stat.RadixTransactionLog;

public class RadixTransactionManager extends AbstractPlatformTransactionManager {
	
	private static final long serialVersionUID = 7732247039741298375L;
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(RadixTransactionManager.class);
	
	private RadixTransactionFactory radixTransactionFactory;
	public void setRadixTransactionFactory(RadixTransactionFactory radixTransactionFactory) {
		this.radixTransactionFactory = radixTransactionFactory;
	}
	
	@Override
	protected Object doGetTransaction() throws TransactionException {
		
		LOG.debug("\ndoGetTransaction");
		RadixTransactionObject transactionObject = radixTransactionFactory.getTransactionObject();
		return transactionObject;
	}
	
	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
		
		LOG.debug("doBegin : transaction={}, definition={}", transaction, definition);
		((RadixTransactionObject) transaction).begin();
	}
	
	@Override
	protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
		
		LOG.debug("doCommit : status={}", status);
		RadixTransactionObject transaction = (RadixTransactionObject) status.getTransaction();
		transaction.commit();
		radixTransactionFactory.removeTransactionObject(transaction.getTransactionId());
	}
	
	@Override
	protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
		
		LOG.debug("Rollback RadixTransaction");
		RadixTransactionObject transaction = (RadixTransactionObject) status.getTransaction();
		transaction.rollback();
		radixTransactionFactory.removeTransactionObject(transaction.getTransactionId());
	}
	
	private static RadixTransactionObject getCurrentTransaction() {
		
		TransactionStatus status = TransactionInterceptor.currentTransactionStatus();
		if(status != null) {
			return (RadixTransactionObject) ((DefaultTransactionStatus) status).getTransaction(); 
		}
		return null;
	}
	
	public String getTransactionId() {
		
		RadixTransactionObject transaction = getCurrentTransaction();
		if(transaction == null) {
			throw new NoTransactionException(null);
		}
		return transaction.getTransactionLog().getTransactionId();
	}
	
	public void doLogControllerReturn(ResponseEntity<byte[]> entity) {
		
		LOG.debug("doLogControllerReturn : entity={}", entity);
		RadixTransactionObject transaction = getCurrentTransaction();
		RadixTransactionLog transactionLog = transaction.getTransactionLog();
		transactionLog.setStatusCode(entity.getStatusCode().toString());
		transactionLog.setResponseBodyByteLength(entity.getBody().length);
		transactionLog.setResponseTime(System.currentTimeMillis());
	}
	
	public void doLogControllerException(Exception ex) throws Throwable {
		
		LOG.debug("doLogControllerException : ex={}", ex);
		RadixTransactionObject transaction = getCurrentTransaction();
		RadixTransactionLog transactionLog = transaction.getTransactionLog();
		if(ex instanceof RadixException) {
			RadixException radixException = (RadixException) ex;
			LOG.error("RadixException {} : {}", radixException.getHttpStatus() + ", " + radixException.getMessage(), radixException.getStackTrace());
			transactionLog.setStatusCode(radixException.getHttpStatus().toString());
			// errorCode를 로그에 넘기기로 했지만, 2012/06/29 현재 errorCode는 정의되지 않았다.
			// 대신 예외 메시지를 넘긴다.
			transactionLog.setErrorCode(radixException.getMessage());
		} else if(ex.getCause()!=null){
			Throwable cause = ex.getCause();
			LOG.debug("cause={}", cause);
			if(cause instanceof RadixException) {
				RadixException radixException = (RadixException) cause;
				LOG.error("RadixException {} : {}", radixException.getHttpStatus() + ", " + radixException.getMessage(), radixException.getStackTrace());
				transactionLog.setStatusCode(radixException.getHttpStatus().toString());
				// errorCode를 로그에 넘기기로 했지만, 2012/06/29 현재 errorCode는 정의되지 않았다.
				// 대신 예외 메시지를 넘긴다.
				transactionLog.setErrorCode(radixException.getMessage());
			} else {
				LOG.error("Exception {} : {}", cause.getMessage(), cause.getStackTrace());
				transactionLog.setStatusCode("500");
				transactionLog.setErrorCode("Service unavailable");
			}
		}
		transactionLog.setResponseTime(System.currentTimeMillis());
	}
	
	public void doLogServiceController(JoinPoint joinPoint) throws TransactionException {
		
		LOG.debug("doLogServiceController : joinPoint={}", joinPoint);
//		int i = 0;
//		for(Object obj : joinPoint.getArgs()) {
//			LOG.debug("arg[{}]={}", i++, obj);
//		}
		
		RadixTransactionObject transaction = getCurrentTransaction();
		RadixTransactionLog transactionLog = transaction.getTransactionLog();
		
		transactionLog.setControllerBeginTime(System.currentTimeMillis());
		
		HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0];
		transactionLog.setHostName(request.getLocalName());
		transactionLog.setIp(request.getLocalAddr());
		transactionLog.setClientIp(request.getRemoteAddr());
		transactionLog.setClientKey((String) request.getAttribute("clientKey"));
		transactionLog.setApiName((String) request.getAttribute("serviceApiName"));
		transactionLog.setPathTemplate((String) request.getAttribute("pathTemplate"));
		transactionLog.setRequestUri(request.getRequestURI());
		transactionLog.setMethod(request.getMethod());
		transactionLog.setStatusCode("200");
		transactionLog.setErrorCode("");
		transactionLog.setRequestTime(new Date());
		transactionLog.setResponseBodyByteLength(0);
		transactionLog.setUserAgent(request.getParameter("user_agent"));
		transactionLog.setOs(request.getParameter("os"));
		transactionLog.setOsVersion(request.getParameter("version"));
		transactionLog.setTerminal(request.getParameter("terminal"));
	}
	
	public void doLogServiceControllerAfter(JoinPoint joinPoint) throws TransactionException {
		
		LOG.debug("doLogServiceControllerAfter : joinPoint={}", joinPoint);
		
		RadixTransactionObject transaction = getCurrentTransaction();
		RadixTransactionLog transactionLog = transaction.getTransactionLog();
		
		transactionLog.setControllerTime(System.currentTimeMillis());
	}
	
	public void doLogOrchestrator(JoinPoint joinPoint) {
		
		LOG.debug("doLogOrchestrator : joinPoint={}", joinPoint);
		
		Exchange exchange = (Exchange)joinPoint.getArgs()[0];
		CanonicalMessage canonicalMessage = exchange.getIn().getBody(CanonicalMessage.class);
		String messageId = canonicalMessage.getHeader().getMessageId();
		RadixTransactionObject transaction = radixTransactionFactory.getTransactionObject(messageId);;
		RadixTransactionLog transactionLog = transaction.getRadixTransactionLog();
		transactionLog.setOrchestratorBeginTime(System.currentTimeMillis());
	}
	
	public void doLogOrchestratorAfter(JoinPoint joinPoint) {
		
		LOG.debug("doLogOrchestratorAfter : joinPoint={}", joinPoint);
		
		Exchange exchange = (Exchange)joinPoint.getArgs()[0];
		CanonicalMessage canonicalMessage = exchange.getIn().getBody(CanonicalMessage.class);
		String messageId = canonicalMessage.getHeader().getMessageId();
		RadixTransactionObject transaction = radixTransactionFactory.getTransactionObject(messageId);;
		RadixTransactionLog transactionLog = transaction.getRadixTransactionLog();
		transactionLog.setOrchestratorTime(System.currentTimeMillis());
	}
	
	public void doLogAdaptor(JoinPoint joinPoint) {
		
		LOG.debug("doLogAdaptor : joinPoint={}", joinPoint);
		
		CanonicalMessage canonicalMessage = (CanonicalMessage)joinPoint.getArgs()[0];
		String messageId = canonicalMessage.getHeader().getMessageId();
		RadixTransactionObject transaction = radixTransactionFactory.getTransactionObject(messageId);;
		RadixTransactionLog transactionLog = transaction.getRadixTransactionLog();
		transactionLog.setAdaptorBeginTime(System.currentTimeMillis());
	}
	
	public void doLogAdaptorAfter(JoinPoint joinPoint) {
		
		LOG.debug("doLogAdaptorAfter : joinPoint={}", joinPoint);
		
		CanonicalMessage canonicalMessage = (CanonicalMessage)joinPoint.getArgs()[0];
		String messageId = canonicalMessage.getHeader().getMessageId();
		RadixTransactionObject transaction = radixTransactionFactory.getTransactionObject(messageId);;
		RadixTransactionLog transactionLog = transaction.getRadixTransactionLog();
		transactionLog.setAdaptorTime(System.currentTimeMillis());
	}
	
	public void doLogPartner(JoinPoint joinPoint) {
		
		LOG.debug("doLogPartner : joinPoint={}", joinPoint);
		
		String messageId = (String) joinPoint.getArgs()[0];
		RadixTransactionObject transaction = radixTransactionFactory.getTransactionObject(messageId);;
		RadixTransactionLog transactionLog = transaction.getRadixTransactionLog();
		transactionLog.setPartnerBeginTime(System.currentTimeMillis());
	}
	
	public void doLogPartnerAfter(JoinPoint joinPoint) {
		
		LOG.debug("doLogPartnerAfter : joinPoint={}", joinPoint);
		
		String messageId = (String) joinPoint.getArgs()[0];
		RadixTransactionObject transaction = radixTransactionFactory.getTransactionObject(messageId);
		RadixTransactionLog transactionLog = transaction.getRadixTransactionLog();
		transactionLog.setPartnerTime(System.currentTimeMillis());
	}
}
