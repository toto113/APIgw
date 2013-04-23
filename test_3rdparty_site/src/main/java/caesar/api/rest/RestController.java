package caesar.api.rest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

@Controller
@RequestMapping("/rest")
public class RestController {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	ResponseEntity<byte[]> SUCCESS;

	public RestController() {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "plain", Charset
				.forName("utf-8")));
		SUCCESS = new ResponseEntity<byte[]>("SUCCESS".getBytes(), headers,
				HttpStatus.OK);
	}

	private void debug(HttpServletRequest request) {

		LOG.debug("method={}", request.getMethod());
		LOG.debug("uri={}", request.getRequestURI());
		LOG.debug("contentType={}", request.getContentType());
		LOG.debug("contentLength={}", request.getContentLength());

		ParameterMap headers = parseHeader(request);
		for (String key : headers.keys()) {
			LOG.debug("header : {}={}", key, headers.get(key));
		}

		LOG.debug("queryString={}", request.getQueryString());
		ParameterMap queryParams = parseQuery(request.getQueryString(),
				request.getCharacterEncoding());
		for (String key : queryParams.keys()) {
			LOG.debug("query parameter : {}={}", key, queryParams.get(key));
		}

		ParameterMap formParams = parseForm(request, queryParams);
		for (String key : formParams.keys()) {
			LOG.debug("form parameter : {}={}", key, formParams.get(key));
		}
	}

	private void debug(MultipartRequest request) {

		debug((HttpServletRequest) request);
	}

	@SuppressWarnings("rawtypes")
	private ParameterMap parseHeader(HttpServletRequest request) {

		ParameterMap map = new ParameterMap();
		Enumeration em = request.getHeaderNames();
		while (em.hasMoreElements()) {
			String name = (String) em.nextElement();
			map.put(name, request.getHeader(name));
		}
		return map;
	}

	private ParameterMap parseQuery(String queryString, String charset) {

		ParameterMap map = new ParameterMap();
		if (queryString == null || "".equals(queryString)) {
			return map;
		}

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		URLEncodedUtils.parse(parameters, new Scanner(queryString), charset);
		for (NameValuePair pair : parameters) {
			map.put(pair.getName(), pair.getValue());
		}
		return map;
	}

	@SuppressWarnings("rawtypes")
	private ParameterMap parseForm(HttpServletRequest request,
			ParameterMap queryParam) {

		ParameterMap map = new ParameterMap();
		Enumeration em = request.getParameterNames();
		while (em.hasMoreElements()) {
			String name = (String) em.nextElement();
			List<String> values = new ArrayList<String>();
			values.addAll(Arrays.asList(request.getParameterValues(name)));
			if (queryParam.containsKey(name)) {
				for (String value : queryParam.get(name)) {
					values.remove(value);
				}
			}
			if (values.size() > 0) {
				map.put(name, values);
			}
		}
		return map;
	}

	protected final void save(String fileName, byte[] data) {

		String path = "/data/upload/";
		String extension = fileName.substring(fileName.indexOf(".") + 1);
		fileName = fileName.substring(0, fileName.indexOf("."));
		File file = new File(path.concat(fileName).concat(".")
				.concat(extension));
		int indexer = 0;
		while (file.exists()) {
			file = new File(path.concat(fileName).concat("[")
					.concat(String.valueOf(++indexer)).concat("]").concat(".")
					.concat(extension));
		}
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(file);
			for (byte b : data) {
				os.write(b);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/**", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doGet(HttpServletRequest request) {

		LOG.debug(">>>>> doGet : request={}", request);
		debug(request);

		return SUCCESS;
	}

	@RequestMapping(value = "/**", method = RequestMethod.POST)
	public ResponseEntity<byte[]> doPost(HttpServletRequest request)
			throws IOException {

		LOG.debug(">>>>> doPost : request={}", request);
		debug(request);

		return SUCCESS;
	}

	@RequestMapping(value = "/**", method = RequestMethod.POST, headers = "content-type=multipart/*")
	public ResponseEntity<byte[]> doPost(MultipartRequest request)
			throws IOException {

		LOG.debug(">>>>> doPostMultiPart : request={}", request);
		debug(request);

		Iterator<String> em = request.getFileNames();
		while (em.hasNext()) {
			String key = em.next();
			MultipartFile file = request.getFile(key);
			LOG.debug("file : fileName={}, originalName={}", file.getName(),
					file.getOriginalFilename());
			LOG.debug("contentType={}, size={}", file.getContentType(),
					file.getSize());
			// LOG.debug("{}={}", file, file.getBytes());
//			save(file.getOriginalFilename(), file.getBytes());
			String fileName = file.getOriginalFilename();
			String path = "/data/upload/";
			String extension = fileName.substring(fileName.indexOf(".") + 1);
			fileName = fileName.substring(0, fileName.indexOf("."));
			File saveFile = new File(path.concat(fileName).concat(".")
					.concat(extension));
			int indexer = 0;
			while (saveFile.exists()) {
				saveFile = new File(path.concat(fileName).concat("[")
						.concat(String.valueOf(++indexer)).concat("]").concat(".")
						.concat(extension));
			}
			long start = System.currentTimeMillis();
			file.transferTo(saveFile);
			LOG.info(">>>>>> transfer file estimate : {}", System.currentTimeMillis() - start);
		}

		return SUCCESS;
	}

	@RequestMapping(value = "/**", method = RequestMethod.PUT)
	public ResponseEntity<byte[]> doPut(HttpServletRequest request)
			throws IOException {

		LOG.debug(">>>>> doPut : request={}", request);
		debug(request);

		return SUCCESS;
	}

	@RequestMapping(value = "/**", method = RequestMethod.DELETE)
	public ResponseEntity<byte[]> doDelete(HttpServletRequest request)
			throws IOException {

		LOG.debug(">>>>> doDelete : request={}", request);
		debug(request);

		return SUCCESS;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private HttpHeaders getHeaders(HttpServletRequest request) {

		Enumeration<String> em = request.getHeaderNames();
		HttpHeaders headers = new HttpHeaders();
		while (em.hasMoreElements()) {
			String key = em.nextElement();
			headers.set(key, request.getHeader(key));
			LOG.debug("found header : {}={}", key, request.getHeader(key));
		}
		return headers;
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	private MultiValueMap<String, Object> getBody(HttpServletRequest request) {

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
		Enumeration em = request.getParameterNames();
		while (em.hasMoreElements()) {
			String name = (String) em.nextElement();
			LOG.debug("{}={}", name, request.getParameter(name));
			body.set(name, request.getParameter(name));
		}
		return body;
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	private MultiValueMap<String, Object> getMultipartBody(
			HttpServletRequest request) {

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
		Enumeration em = request.getParameterNames();
		while (em.hasMoreElements()) {
			String name = (String) em.nextElement();
			LOG.debug("{}={}", name, request.getParameter(name));
			body.set(name, request.getParameter(name));
		}
		return body;
	}

	@SuppressWarnings("unused")
	private byte[] getByte(HttpServletRequest request) throws IOException {

		byte[] body = new byte[request.getContentLength()];
		InputStream is = null;
		try {
			is = request.getInputStream();
			is.read(body);
		} catch (IOException e) {
			throw e;
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return body;
	}
}
