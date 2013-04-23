package com.kthcorp.radix.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.util.StringUtils;

import com.kthcorp.radix.domain.RequestBodyMap;
import com.kthcorp.radix.domain.exception.ValidateException;

public class RequestBodyMapUtil {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(RequestBodyMap.class);
	
	public static RequestBodyMap<String, String> getBodyObjFromRequest(HttpServletRequest request, Set<String> getParams) throws ValidateException {
		
		String contentType = request.getContentType();
		if(contentType != null) {
			contentType = contentType.split(";")[0];
		}
		String characterEncoding = request.getCharacterEncoding();
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("Request:ContentType->" + contentType + ",contentSize->" + request.getContentLength() + ",encoding->" + request.getCharacterEncoding());
		}
		if(contentType == null) {
			throw new ValidateException("content_type is null");
		}
		if(request.getContentLength() < 1) {
			throw new ValidateException("body'length is 0");
		}
		if(characterEncoding == null) {
			characterEncoding = "utf-8";
		}
		if(getParams == null) {
			return null;
		}
		
		RequestBodyMap<String, String> bodyObj = new RequestBodyMap<String, String>();
		if("application/x-www-form-urlencoded".equals(contentType.toLowerCase())) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("Request:Parsing the form data");
			}
			try {
				InputStreamEntity entity = new InputStreamEntity(request.getInputStream(), request.getContentLength());
				
				List<NameValuePair> pairList = URLEncodedUtils.parse(entity);
				
				if(LOG.isDebugEnabled()) {
					LOG.debug("Request:PairList->" + pairList);
				}
				
				for(NameValuePair pair : pairList) {
					if(getParams.contains(pair.getName())) {
						bodyObj.put(pair.getName(), pair.getValue());
					}
				}
			} catch(IOException e) {
				throw new ValidateException("cannot load body stream,msg->" + e.getMessage(), e.getCause());
			}
		} else if("application/json".equals(contentType.toLowerCase())) {
			
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(request.getInputStream(), request.getCharacterEncoding()));
				String jsonString = null;
				String line = null;
				
				while((line = br.readLine()) != null) {
					if(jsonString == null) {
						jsonString = line;
					} else {
						jsonString = jsonString + line;
					}
				}
				
				LOG.debug("Request:JSONString->" + jsonString);
				JSONObject obj = new JSONObject(jsonString);
				
				@SuppressWarnings("unchecked")
				Iterator<Object> objKeys = obj.keys();
				while(objKeys.hasNext()) {
					Object key = objKeys.next();
					if(key != null) {
						if(key instanceof String) {
							if(getParams.contains(key)) {
								bodyObj.put(key.toString(), obj.getString(key.toString()));
							}
						}
					}
				}
			} catch(JSONException e) {
				throw new ValidateException("the data in JSON cannot be parsed,msg->" + e.getMessage(), e.getCause());
			} catch(UnsupportedEncodingException e) {
				throw new ValidateException("your character set does not support,msg->" + e.getMessage(), e.getCause());
			} catch(IOException e) {
				throw new ValidateException("IO error,msg->" + e.getMessage(), e.getCause());
			} finally {
				if(br != null) {
					try {
						br.close();
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			throw new ValidateException("this content type(" + contentType + ") is not supported");
		}
		return bodyObj;
		
	}
	
	public static RequestBodyMap<String, String> getBodyObjFromRequest(final String contentType, int contentLength, String characterEncoding, final String bodyString) throws ValidateException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Request:ContentType->" + contentType + ",contentSize->" + contentLength + ",encoding->" + characterEncoding);
		}
		
		try {
			RequestBodyMap<String, String> bodyObj = new RequestBodyMap<String, String>();
			if(StringUtils.hasText(bodyString) == false)
				return bodyObj;
			
			if("application/x-www-form-urlencoded".equals(contentType.toLowerCase())) {
				LOG.debug("Request:Parsing the form data");
				StringEntity entity = new StringEntity(bodyString);
				if(characterEncoding == null) {
					entity.setContentEncoding("utf-8");
				} else {
					entity.setContentEncoding(characterEncoding);
				}
				entity.setContentType(contentType);
				
				List<NameValuePair> pairList = URLEncodedUtils.parse(entity);
				LOG.debug("Request:PairList->" + pairList);
				
				for(NameValuePair pair : pairList) {
					bodyObj.put(pair.getName(), pair.getValue());
				}
			} else if("application/json".equals(contentType.toLowerCase())) {
				
				LOG.debug("Request:JSONString->" + bodyString);
				JSONObject obj = new JSONObject(bodyString);
				
				@SuppressWarnings("unchecked")
				Iterator<Object> objKeys = obj.keys();
				while(objKeys.hasNext()) {
					Object key = objKeys.next();
					if(key != null) {
						if(key instanceof String) {
							bodyObj.put(key.toString(), obj.getString(key.toString()));
						}
					}
				}
			} else {
				throw new ValidateException("this content type(" + contentType + ") is not supported");
			}
			return bodyObj;
		} catch(JSONException e) {
			throw new ValidateException("the data in JSON cannot be parsed,msg->" + e.getMessage());
		} catch(UnsupportedEncodingException e) {
			throw new ValidateException("Unsupported character encoding,msg->" + e.getMessage());
		} catch(IOException e) {
			throw new ValidateException("IO error,msg->" + e.getMessage());
		}
	}
}
