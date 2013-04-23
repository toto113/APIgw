package com.kthcorp.radix.component.adaptor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/rest")
@Controller
public class RestController extends ControllerBase {
	
	private static final Charset CHARSET = Charset.forName("utf-8");
	
	public RestController() {
	
	}
	
	private HttpHeaders createHttpHeader(MediaType mediaType) {
		
		HttpHeaders headers = new HttpHeaders();
		Map<String, String> headerParameters = new HashMap<String, String>();
		headerParameters.put("charset", CHARSET.name());
		headers.setContentType(new MediaType(mediaType, headerParameters));
		return headers;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<byte[]> rest(HttpServletRequest request, HttpServletResponse response,
			Locale locale, Model model) throws IOException {

		debug(request);
		String body = "rest";
		HttpHeaders headers = createHttpHeader(MediaType.TEXT_PLAIN);
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body.getBytes(CHARSET), headers, HttpStatus.OK);
		return entity;
	}
	
	@RequestMapping(value = "/wait", method = RequestMethod.GET)
	public ResponseEntity<byte[]> wait(HttpServletRequest request, HttpServletResponse response, Locale locale) throws IOException {

		debug(request);
		String number = request.getParameter("number");
		long timewait = Long.parseLong(number);
		try {
			Thread.sleep(timewait * 1000);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		String body = "deffered reply for " + number;
		HttpHeaders headers = createHttpHeader(MediaType.TEXT_PLAIN);
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body.getBytes(CHARSET), headers, HttpStatus.OK);
		return entity;
	}

	@RequestMapping(value="/get", method=RequestMethod.GET)
	public ResponseEntity<byte[]> get(HttpServletRequest request, HttpServletResponse response, Locale locale) throws IOException {

		debug(request);
		String body = "get";
		HttpHeaders headers = createHttpHeader(MediaType.TEXT_PLAIN);
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body.getBytes(CHARSET), headers, HttpStatus.OK);
		return entity;
	}
	
	@RequestMapping(value = "/post", method = RequestMethod.POST)
	public ResponseEntity<byte[]> post(HttpServletRequest request, HttpServletResponse response,
			Locale locale, Model model) throws IOException {

		debug(request);
		String body = "post";
		HttpHeaders headers = createHttpHeader(MediaType.TEXT_PLAIN);
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body.getBytes(CHARSET), headers, HttpStatus.OK);
		return entity;
	}

	@RequestMapping(value = "/put", method = RequestMethod.PUT)
	public ResponseEntity<byte[]> put(HttpServletRequest request, HttpServletResponse response,
			Locale locale, Model model) throws IOException {

		debug(request);
		String body = "put";
		HttpHeaders headers = createHttpHeader(MediaType.TEXT_PLAIN);
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body.getBytes(CHARSET), headers, HttpStatus.OK);
		return entity;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public ResponseEntity<byte[]> delete(HttpServletRequest request, HttpServletResponse response,
			Locale locale, Model model) throws IOException {

		debug(request);
		String body = "delete";
		HttpHeaders headers = createHttpHeader(MediaType.TEXT_PLAIN);
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body.getBytes(CHARSET), headers, HttpStatus.OK);
		return entity;
	}
}
