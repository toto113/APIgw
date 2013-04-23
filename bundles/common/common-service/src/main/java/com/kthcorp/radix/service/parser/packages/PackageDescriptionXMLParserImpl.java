package com.kthcorp.radix.service.parser.packages;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xerces.parsers.DOMParser;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.kthcorp.radix.api.service.PackageDescriptionParser;
import com.kthcorp.radix.api.service.PolicyManagerService;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.packages.Packages;
import com.kthcorp.radix.domain.policy.Policy;
import com.kthcorp.radix.domain.policy.PolicyType;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.util.UUIDUtils;

public class PackageDescriptionXMLParserImpl implements PackageDescriptionParser {
	
	private final static Logger LOG = UuidViewableLoggerFactory.getLogger(PackageDescriptionXMLParserImpl.class);
	private final transient DOMParser parser;
	private transient Document xmlDocument;
	private final transient InputSource source;
	private final transient XPath xpathInvoker;
	
	private byte[] businessPlatformID;
	private String partnerID;
	private boolean isModify = false;

	private PolicyManagerService policyManagerService;
	
	protected PackageDescriptionXMLParserImpl(final String source, PolicyManagerService policyManagerService) {
		parser = new DOMParser();
		xpathInvoker = XPathFactory.newInstance().newXPath();
		this.source = new InputSource(new StringReader(source));
		this.policyManagerService = policyManagerService;
	}
	
	@Override
	public Packages getPackage(final byte[] businessPlatformID, final String partnerID, final boolean isModify) throws ValidateException {
		try {
			if(LOG.isDebugEnabled()) {
				LOG.debug("[PackageDescriptionParser] Parsing XML Document, Document->" + this.source);
			}
			this.businessPlatformID = businessPlatformID;
			this.partnerID = partnerID;
			this.isModify = isModify;
			
			parser.parse(source);
			xmlDocument = parser.getDocument();
			
			final Packages packages = new Packages();
			packages.setBusinessPlatformID(this.businessPlatformID);
			packages.setPartnerID(this.partnerID);
			
			// Get Mother Property
			final Node node = (Node) xpathInvoker.evaluate("//package", this.xmlDocument, XPathConstants.NODE);
			final NamedNodeMap nodeAttributes = node.getAttributes();
			
			Node nameN = nodeAttributes.getNamedItem("name");
			if(nameN!=null&&StringUtils.hasText(nameN.getTextContent())) {
				packages.setName(nameN.getTextContent());
			} else {
				throw new ValidateException("package->name does not found");
			}
			
			// Get Service APIs
			packages.setServiceApiList(this.getServiceAPIs("//package/apis/api"));
			
			// Get Package Policies
			packages.setPolicyList(this.getPolicies(this.xmlDocument, "//package/policies/*"));
			
			return packages;
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
	
	private NodeList getNodeListFromDocument(final String path) throws XPathExpressionException, ValidateException {
		return this.getNodeListFromDocument(this.xmlDocument, path);
	}
	
	private NodeList getNodeListFromDocument(Object doc, final String path) throws XPathExpressionException, ValidateException {
		final NodeList nodeList = (NodeList) xpathInvoker.evaluate(path, doc, XPathConstants.NODESET);
		if(nodeList == null) {
			throw new ValidateException("nodeList(" + path + ") does not found");
		}
		return nodeList;
	}
	
	public List<ServiceAPI> getServiceAPIs(final String path) throws XPathExpressionException, ValidateException {
		final NodeList apiNodeList = this.getNodeListFromDocument(path);
		final int nodeListLen = apiNodeList.getLength();
		
		final List<ServiceAPI> serviceAPIs = new ArrayList<ServiceAPI>();
		for(int i = 0; i < nodeListLen; i++) {
			final Node node = apiNodeList.item(i);
			final ServiceAPI serviceAPI = new ServiceAPI();
			
			
			Node idN = node.getAttributes().getNamedItem("id");
			if(idN!=null&&StringUtils.hasText(idN.getTextContent())) {
				try {
					serviceAPI.setId(UUIDUtils.getBytes(idN.getTextContent()));
				} catch(Exception e) {
					throw new ValidateException("serviceAPI->id("+idN.getTextContent()+") is invalid");
				}
			} else {
				throw new ValidateException("serviceAPI->id does not found");
			}
			serviceAPI.setPolicyList(this.getPolicies(node, "policies/*"));
			serviceAPIs.add(serviceAPI);
			if(LOG.isDebugEnabled()) {
				LOG.debug("[PackageDescriptionParser] Added ServiceAPI->" + serviceAPI.getId());
			}
		}
		return serviceAPIs;
	}
	
	public List<Policy> getPolicies(Object doc, final String path) throws XPathExpressionException, ValidateException {
		final NodeList policyNodeList = this.getNodeListFromDocument(doc, path);
		final int nodeListLen = policyNodeList.getLength();
		
		final List<Policy> policies = new ArrayList<Policy>();
		for(int i = 0; i < nodeListLen; i++) {
			final Node node = policyNodeList.item(i);
			final Policy policy = new Policy();
			final NamedNodeMap nodeAttributes = node.getAttributes();
			
			policy.setBusinessPlatformID(this.businessPlatformID);
			policy.setPartnerID(partnerID);
			
			if(isModify) {
				Node idN = nodeAttributes.getNamedItem("id");
				if(idN!=null&&StringUtils.hasText(idN.getTextContent())) {
					try {
						policy.setId(UUIDUtils.getBytes(idN.getTextContent()));
					} catch(Exception e) {
						throw new ValidateException("policy->id("+idN.getTextContent()+") is invalid on modify mode");
					}
				}
			}

			String policyTypeID = node.getNodeName();
			if(LOG.isDebugEnabled()) {
				LOG.debug("Set PolicyTypeID->"+policyTypeID);
			}
			
			Node policyNameN = nodeAttributes.getNamedItem("name");
			if(policyNameN==null||!StringUtils.hasText(policyNameN.getTextContent())) {
				throw new ValidateException("policy("+policyTypeID+")->name does not found");
			}
			policy.setName(policyNameN.getTextContent());

			PolicyType policyType = policyManagerService.getPolicyType(policyTypeID);
			if(policyType==null) {
				throw new ValidateException("policyType->"+policyTypeID+" is invalid");
			}
			
			policy.setPolicyTypeID(policyTypeID);
			
			List<String> properties = policyType.getPropertiesObj();
			
			if(properties!=null) {
				if(properties.size()>0) {

					// Parameter
					final NodeList parameterNodeList = this.getNodeListFromDocument(node,"*");
					int parameterCount = parameterNodeList.getLength();
					if(LOG.isDebugEnabled()) {
						LOG.debug("Parameter Found,Count->"+parameterCount);
					}
					
					for(int j=0;j<parameterCount;j++) {
						Node parameterNode = parameterNodeList.item(j);
						String name = parameterNode.getNodeName();
						String value = parameterNode.getTextContent();
						if(LOG.isDebugEnabled()) {
							LOG.debug("Parameter Found,Name->"+name+",Value->"+value);
						}
						if(properties.contains(name)) {
							policy.addProperty(name, value);
							if(LOG.isDebugEnabled()) {
								LOG.debug("Parameter Added,Name->"+name+",Value->"+value+" to PolicyType->"+policyTypeID);
							}
						} else {
							throw new ValidateException("parameterName->"+name+" does support in "+policyTypeID);
						}
					}
				}
			}
			policies.add(policy);
		}
		return policies;
	}
	
}
