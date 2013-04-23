package com.kthcorp.radix.service.parser.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xerces.parsers.DOMParser;
import org.json.JSONException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.kthcorp.radix.api.service.ServiceDescriptionParser;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.service.ResourceOwner;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.ServiceVersion;
import com.kthcorp.radix.domain.service.api.Parameters;
import com.kthcorp.radix.domain.service.api.ValueGenerator;
import com.kthcorp.radix.domain.service.api.ValueObject;
import com.kthcorp.radix.domain.service.api.partnerAPI.PartnerAPI;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolFactory;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolMode;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolType;
import com.kthcorp.radix.domain.service.api.protocol.http.ClientProtocol;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ServerProtocol;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.api.transform.TransformType;
import com.kthcorp.radix.domain.service.api.transport.TransportType;
import com.kthcorp.radix.domain.service.mapping.Mapping;
import com.kthcorp.radix.domain.service.mapping.MappingInfo;
import com.kthcorp.radix.domain.service.mapping.MappingType;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.util.UUIDUtils;

public class ServiceDescriptionXmlParserImpl implements ServiceDescriptionParser {



	@Override
	public Service getService(String inputSource) throws SAXException, IOException, XPathExpressionException, JSONException, ValidateException, NotSupportException, DOMException {

		Service service = new Service();

		InputSource src = new InputSource(new StringReader(inputSource));
		DOMParser parser = new DOMParser();

		parser.parse(src);
		Document doc = parser.getDocument();
		XPath xpath = XPathFactory.newInstance().newXPath();

		service.setName(parseName(doc, xpath));
		service.setVersion(parseServiceVersion(doc, xpath));
		service.setResourceOwner(parseResourceOwner(doc, xpath));
		service.setResourceAuthUrl(parserResourceAuthUrl(doc, xpath));
		service.setApiList(parseApiList(doc, xpath));

		return service;
	}

	private List<RoutingMethod> parseApiList(Document doc, XPath xpath) throws ValidateException, XPathExpressionException, JSONException, NotSupportException, DOMException {

		List<RoutingMethod> apiList = new ArrayList<RoutingMethod>();
		NodeList nodeList = (NodeList) xpath.evaluate("//Service/api-list/api", doc, XPathConstants.NODESET);

		if(nodeList==null) { return apiList; }

		try {
			for(int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				DirectMethod method = new DirectMethod();

				Object id = xpath.evaluate("id", node, XPathConstants.STRING);
				if(id != null && id.toString().length() > 0) {
					method.setId(UUIDUtils.getBytes(id.toString()));
				}

				Node serviceApiNode = (Node) xpath.evaluate("service-api", node, XPathConstants.NODE);

				if(serviceApiNode != null) {
					ServiceAPI serviceApi = new ServiceAPI();

					Object sid = xpath.evaluate("id", serviceApiNode, XPathConstants.STRING);
					if(sid != null && sid.toString().length() > 0) {
						serviceApi.setId(UUIDUtils.getBytes(sid.toString()));
					}

					serviceApi.setTransportType(TransportType.fromType(serviceApiNode.getAttributes().getNamedItem("type").getTextContent()));
					serviceApi.setProtocolType(ProtocolType.fromCode(serviceApiNode.getAttributes().getNamedItem("protocol").getTextContent()));
					serviceApi.setName((String) xpath.evaluate("name", serviceApiNode, XPathConstants.STRING));
					serviceApi.setProtocolObj(parseProtocolObjs(serviceApiNode, doc, xpath));
					Parameters parametersObj = parseParameters((NodeList) xpath.evaluate("params/param", serviceApiNode, XPathConstants.NODESET), doc, xpath);
					serviceApi.setParametersObj(parametersObj);

					String transform = (String) xpath.evaluate("transform", serviceApiNode, XPathConstants.STRING);

					if(transform != null && transform.length() > 0) {
						serviceApi.setDefaultTransformType(TransformType.valueOf(transform.toUpperCase()));
					} else {
						serviceApi.setDefaultTransformType(TransformType.XML);
					}

					method.setServiceAPI(serviceApi);
				}

				Node partnerApiNode = (Node) xpath.evaluate("partner-api", node, XPathConstants.NODE);

				if(partnerApiNode != null) {
					PartnerAPI partnerApi = new PartnerAPI();

					Object pid = xpath.evaluate("id", partnerApiNode, XPathConstants.STRING);
					if(pid != null && pid.toString().length() > 0) {
						partnerApi.setId(UUIDUtils.getBytes(pid.toString()));
					}

					partnerApi.setTransportType(TransportType.fromType(partnerApiNode.getAttributes().getNamedItem("type").getTextContent()));
					partnerApi.setProtocolType(ProtocolType.fromCode(partnerApiNode.getAttributes().getNamedItem("protocol").getTextContent()));
					partnerApi.setName((String) xpath.evaluate("name", partnerApiNode, XPathConstants.STRING));
					partnerApi.setProtocolObj(parseClientProtocolObjs(partnerApiNode, doc, xpath));
					Parameters parametersObj = parseParameters((NodeList) xpath.evaluate("params/param", partnerApiNode, XPathConstants.NODESET), doc, xpath);
					partnerApi.setParametersObj(parametersObj);

					String transform = (String) xpath.evaluate("transform", partnerApiNode, XPathConstants.STRING);

					if(transform != null) {
						partnerApi.setDefaultTransformType(TransformType.valueOf(transform.toUpperCase()));
					} else {
						partnerApi.setDefaultTransformType(TransformType.XML);
					}

					method.setPartnerAPI(partnerApi);
				}

				MappingInfo parameterMap = parseMappingInfo((NodeList) xpath.evaluate("mapping/parameter/map", node, XPathConstants.NODESET), MappingType.PARAMETER);

				Object pid = xpath.evaluate("mapping/parameter/id", node, XPathConstants.STRING);
				if(pid != null && pid.toString().length() > 0) {
					parameterMap.setId(UUIDUtils.getBytes(pid.toString()));
				}

				method.setParameterMap(parameterMap);

				MappingInfo resultMap = parseMappingInfo((NodeList) xpath.evaluate("mapping/result/map", node, XPathConstants.NODESET), MappingType.RESULT);

				Object rid = xpath.evaluate("mapping/result/id", node, XPathConstants.STRING);
				if(rid != null && rid.toString().length() > 0) {
					resultMap.setId(UUIDUtils.getBytes(rid.toString()));
				}

				method.setResultMap(resultMap);

				apiList.add(method);
			}
		} catch(NumberFormatException e) {
			throw new ValidateException("Your description has wrong number format,msg->" + e.getMessage());
		}

		return apiList;
	}

	private MappingInfo parseMappingInfo(NodeList nodeList, MappingType mappingType) throws JSONException {

		MappingInfo mappingInfo = new MappingInfo();
		Mapping mappingObj = new Mapping();

		for(int j = 0; j < nodeList.getLength(); j++) {
			Node subnode = nodeList.item(j);
			String from = subnode.getAttributes().getNamedItem("from").getTextContent();
			String to = subnode.getAttributes().getNamedItem("to").getTextContent();
			mappingObj.putMapping(from, to);

		}

		mappingInfo.setMappingObj(mappingObj);
		mappingInfo.setMappingType(mappingType);
		return mappingInfo;
	}

	private Parameters parseParameters(NodeList nodeList, Document doc, XPath xpath) throws XPathExpressionException {

		Parameters parametersObj = new Parameters();

		for(int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String name = node.getAttributes().getNamedItem("name").getTextContent();
			Node defaultNode = (Node) xpath.evaluate("default", node, XPathConstants.NODE);
			ValueObject valueObject = null;

			if(defaultNode != null) {
				String defaultValue = defaultNode.getAttributes().getNamedItem("type").getTextContent();
				String value = (String) xpath.evaluate("default", node, XPathConstants.STRING);

				if(defaultValue != null) {
					valueObject = new ValueObject();
					valueObject.setValueGenerator(ValueGenerator.valueOf(defaultValue.toUpperCase()));
					valueObject.setResource(value);
				}
			}

			parametersObj.put(name, valueObject);
		}

		return parametersObj;
	}

	private ClientProtocol parseClientProtocolObjs(Node node, Document doc, XPath xpath) throws XPathExpressionException, NotSupportException, DOMException {

		// PartnerAPI's ProtocolMode is fixed as CLIENT
		//
		ClientProtocol protocol = (ClientProtocol) ProtocolFactory.createProtocol(ProtocolType.fromCode(node.getAttributes().getNamedItem("protocol").getTextContent()), ProtocolMode.CLIENT);

		// HttpHost host = null;
		String uri = (String) xpath.evaluate("uri", node, XPathConstants.STRING);

		if(uri != null) {
			// host = new HttpHost(uri);
			protocol.setUri(uri);
		}

		protocol.setMethod(HttpMethod.valueOf((String) xpath.evaluate("method", node, XPathConstants.STRING)));
		protocol.setContentType((String) xpath.evaluate("content-type", node, XPathConstants.STRING));
		protocol.setUser((String) xpath.evaluate("user", node, XPathConstants.STRING));
		protocol.setPassword((String) xpath.evaluate("password", node, XPathConstants.STRING));
		protocol.setSignatureEncoding((String) xpath.evaluate("signature-encoding", node, XPathConstants.STRING));

		return protocol;
	}

	private ServerProtocol parseProtocolObjs(Node serviceApiNode, Document doc, XPath xpath) throws XPathExpressionException, NotSupportException, DOMException {

		// ServiceAPI's ProtocolMode is fixed as SERVER
		//
		ServerProtocol protocol = (ServerProtocol) ProtocolFactory.createProtocol(ProtocolType.fromCode(serviceApiNode.getAttributes().getNamedItem("protocol").getTextContent()), ProtocolMode.SERVER);

		protocol.setMethod(HttpMethod.valueOf((String) xpath.evaluate("method", serviceApiNode, XPathConstants.STRING)));
		
		String pathTemplateString = (String)xpath.evaluate("pathTemplate", serviceApiNode, XPathConstants.STRING);
		protocol.setPathTemplate(pathTemplateString);
		
		return protocol;
	}

	private String parserResourceAuthUrl(Document doc, XPath xpath) throws XPathExpressionException {

		return (String) xpath.evaluate("//Service/resource/authURL", doc, XPathConstants.STRING);
	}

	private ResourceOwner parseResourceOwner(Document doc, XPath xpath) throws XPathExpressionException {

		String owner = (String) xpath.evaluate("//Service/resource/owner", doc, XPathConstants.STRING);
		// return ResourceOwner.valueOf( owner ) ;
		return ResourceOwner.fromType(owner);
	}

	private ServiceVersion parseServiceVersion(Document doc, XPath xpath) throws ValidateException, XPathExpressionException {

		ServiceVersion version = null;
		String versionNumberStr = (String) xpath.evaluate("//Service/version", doc, XPathConstants.STRING);

		try {
			Integer versionNumber = Integer.parseInt(versionNumberStr);
			if(versionNumber != null) {
				version = new ServiceVersion();
				version.setVersionNumber(versionNumber);
			}
		} catch(NumberFormatException e) {
			throw new ValidateException("version information is incorrect," + e.getMessage());
		}

		return version;
	}

	private String parseName(Document doc, XPath xpath) throws XPathExpressionException {
		return (String) xpath.evaluate("//Service/name", doc, XPathConstants.STRING);
	}
}
