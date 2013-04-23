package com.kthcorp.radix.domain.canonical.reply;

import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class AdaptorRestReplyToStringTest {


	@Test
	public void Reply_body에_기본_utf8이_아닌_charset으로_인코딩된_bytes_일때_toString에서_발생하는_버그_처리() {
		AdaptorRestReply reply = new AdaptorRestReply();
		byte[] bodyBytes = null;
		try {
			bodyBytes = "123어쩌구".getBytes("euc_kr");
		} catch (UnsupportedEncodingException e) {
			fail("preparing bodyBytes failed. e="+e);
		}
		reply.setBody(bodyBytes);
		try {
			System.out.println(reply.toString());
		} catch(Throwable e) {
			e.printStackTrace();
			fail("call toString() failed. e="+e);
		}

	}

}
