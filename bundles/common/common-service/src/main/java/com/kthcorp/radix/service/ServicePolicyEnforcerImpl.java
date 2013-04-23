package com.kthcorp.radix.service;

import java.util.Date;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.kthcorp.radix.api.service.PolicyEnforcer;
import com.kthcorp.radix.domain.exception.PolicyException;
import com.kthcorp.radix.domain.exception.PolicyRateLimitException;
import com.kthcorp.radix.util.Time;

public class ServicePolicyEnforcerImpl implements PolicyEnforcer {
	
	private int durationTime;
	private String durationTimeunit;
	private long maxLimitCount;
	
	@Override
	public String preCheck(Map<String, String> specificationForPolicy, String currentStatusForPolicy) throws PolicyException, JSONException {
		String result = currentStatusForPolicy;
		
		// Get basic data of policy specification
		String policyDuration = specificationForPolicy.get("duration");
		durationTime = Integer.parseInt(policyDuration.substring(0, policyDuration.length() - 1));
		durationTimeunit = policyDuration.substring(policyDuration.length() - 1);
		maxLimitCount = Long.parseLong(specificationForPolicy.get("maxCount"));
		
		// Get current status data of policy
		if(currentStatusForPolicy == null || "".equals(currentStatusForPolicy)) {
			result = createNewServiceStatus();
		} else {
			result = checkStatus(currentStatusForPolicy);
		}
		return result;
	}
	
	@Override
	public String postCheck(Map<String, String> specificationForPolicy, String currentStatusForPolicy) throws PolicyException, JSONException {
		long accumulativeCount;
		
		if(currentStatusForPolicy == null || "".equals(currentStatusForPolicy)) {
			throw new PolicyRateLimitException("Not found a status of serviceAPI policy");
		}
		
		JSONObject jsonCurrentStatus = new JSONObject(currentStatusForPolicy);
		accumulativeCount = Long.parseLong(jsonCurrentStatus.getString("count"));
		jsonCurrentStatus.put("count", Long.toString(++accumulativeCount));
		
		return jsonCurrentStatus.toString();
	}
	
	private String createNewServiceStatus() throws JSONException {
		JSONObject jsonCurrentStatus = new JSONObject();
		
		String beginDuration = Time.strNow();
		String finishDuration = Time.dateAddFormat(Time.getTimeUnit(durationTimeunit), durationTime, Time.now());
		jsonCurrentStatus.put("from", beginDuration);
		jsonCurrentStatus.put("to", finishDuration);
		jsonCurrentStatus.put("count", "0");
		
		return jsonCurrentStatus.toString();
	}
	
	private String checkStatus(String policyStatus) throws JSONException, PolicyRateLimitException {
		String status = policyStatus;
		long accumulativeCount;
		
		JSONObject currentStatus = new JSONObject(status);
		Date now = Time.now();
		Date from = Time.getDateFromString(currentStatus.getString("from"));
		Date to = Time.getDateFromString(currentStatus.getString("to"));
		accumulativeCount = Long.parseLong(currentStatus.getString("count"));
		
		if(from.after(now)) {
			// now < from : It's impossible event!
			throw new PolicyRateLimitException("service don't begin yet");
		}
		if(to.before(now)) {
			// to < now : service term expired! Note that policy status have to reset!
			status = createNewServiceStatus();
		}
		
		if(from.before(now) && to.after(now)) {
			// from < now < to
			if(0 <= maxLimitCount && maxLimitCount <= accumulativeCount) {
				throw new PolicyRateLimitException("Service policy exhausted available usage!!");
			}
		}
		
		return status;
	}
}
