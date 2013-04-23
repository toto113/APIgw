package com.kthcorp.radix.service.parser.client;

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
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.kthcorp.radix.api.service.ClientPackageDescriptionParser;
import com.kthcorp.radix.domain.client.CPackages;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.packages.Parameters;
import com.kthcorp.radix.util.UUIDUtils;

public class ClientPackageDescriptionXMLParserImpl implements ClientPackageDescriptionParser {
	
	private final static Logger LOG = UuidViewableLoggerFactory.getLogger(ClientPackageDescriptionXMLParserImpl.class);
	private final transient DOMParser parser;
	private transient Document xmlDocument;
	private final transient InputSource source;
	private final transient XPath xpathInvoker;
	
	private byte[] clientID;
	
	protected ClientPackageDescriptionXMLParserImpl(final String source) {
		parser = new DOMParser();
		xpathInvoker = XPathFactory.newInstance().newXPath();
		this.source = new InputSource(new StringReader(source));
	}
	
	@Override
	public List<CPackages> getPackageList(final byte[] clientID) throws ValidateException {
		try {
			if(LOG.isDebugEnabled()) {
				LOG.debug("[ClientPackageDescriptionXMLParser] Parsing XML Document, Document->" + this.source);
			}
			this.clientID = clientID;
			
			parser.parse(source);
			xmlDocument = parser.getDocument();
			
			final List<CPackages> ret = new ArrayList<CPackages>();
			
			// Get Mother Property
			final NodeList nodeList = (NodeList) xpathInvoker.evaluate("//clientKey/packages/package", this.xmlDocument, XPathConstants.NODESET);
			final int packageCount = nodeList.getLength();
			
			if(LOG.isDebugEnabled()) {
				LOG.debug("[getPackageList] found packages(count->"+packageCount+")");
			}
			
			for(int i=0;i<packageCount;i++) {
				ret.add(this.getPackage(nodeList.item(i)));
			}
			
			return ret;
		} catch(SAXException e) {
			LOG.warn(e.getMessage());
			throw new ValidateException(e.getMessage(), e.getCause());
		} catch(NumberFormatException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			throw new ValidateException(e.getMessage(), e.getCause());
		} catch(XPathExpressionException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			throw new ValidateException(e.getMessage(), e.getCause());
		} catch(IOException e) {
			LOG.warn(e.getMessage());
			throw new ValidateException(e.getMessage(), e.getCause());
		}
	}
	
	private NodeList getNodeListFromDocument(Object doc, final String path) throws XPathExpressionException, ValidateException {
		final NodeList nodeList = (NodeList) xpathInvoker.evaluate(path, doc, XPathConstants.NODESET);
		if(nodeList == null) {
			throw new ValidateException("nodeList(" + path + ") does not found");
		}
		return nodeList;
	}
	
	public CPackages getPackage(Node packageNode) throws XPathExpressionException, ValidateException {
		
		NamedNodeMap nodeMap = packageNode.getAttributes();
		
		if(nodeMap.getLength()<1) {
			throw new ValidateException("package->id does not found");
		}
		Node idNode = nodeMap.getNamedItem("id");
		if(idNode==null) {
			throw new ValidateException("package->id does not found");
		}
		
		byte[] packageID = null;
		String packageIDInString = idNode.getTextContent();
		
		if(!StringUtils.hasText(packageIDInString)) {
			throw new ValidateException("id does not exists");
		}
		try {
			packageID = UUIDUtils.getBytes(packageIDInString);
		} catch(Exception e) {
			throw new ValidateException("packageID("+packageIDInString+") is invalid");
		}

		CPackages packages = new CPackages();
		try {
			packages.setParametersObj(this.getParameters(packageNode, "*"));
		} catch (JSONException e) {
			throw new ValidateException("parameter is invalid,"+e.getMessage());
		}
		packages.setClientID(clientID);
		packages.setPackageID(packageID);
		
		return packages;
	}
	
	public Parameters getParameters(Object doc, final String path) throws XPathExpressionException, ValidateException {
		final NodeList parameterNodeList = this.getNodeListFromDocument(doc, path);
		final int nodeListLen = parameterNodeList.getLength();
		
		final Parameters parameters = new Parameters();

		for(int i = 0; i < nodeListLen; i++) {
			final Node node = parameterNodeList.item(i);
			final String value = node.getChildNodes().item(0).getTextContent();
			
			if(LOG.isDebugEnabled()) {
				LOG.debug("[getParameters] loaded parameter(name->"+node.getNodeName()+",value->"+value+")");
			}
			
			parameters.put(node.getNodeName(), value);
		}
		return parameters;
	}
	
}
