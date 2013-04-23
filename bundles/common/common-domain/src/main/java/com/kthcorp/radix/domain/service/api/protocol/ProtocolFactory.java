package com.kthcorp.radix.domain.service.api.protocol;

import com.kthcorp.radix.domain.exception.NotSupportException;

public class ProtocolFactory {
	
	public static Protocol createProtocol(ProtocolType protocolType, ProtocolMode protocolMode) throws NotSupportException {
		if(protocolType == ProtocolType.HTTP_v1_0 || protocolType == ProtocolType.HTTP_v1_1) {
			if(ProtocolMode.CLIENT == protocolMode) {
				return new com.kthcorp.radix.domain.service.api.protocol.http.ClientProtocol();
			} else if(ProtocolMode.SERVER == protocolMode) {
				return new com.kthcorp.radix.domain.service.api.protocol.http.ServerProtocol();
			} else {
				throw new NotSupportException("not support protocolMode");
			}
		} else {
			throw new NotSupportException("not support protocolType");
		}
	}
	
}
