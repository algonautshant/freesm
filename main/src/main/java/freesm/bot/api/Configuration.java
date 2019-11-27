package freesm.bot.api;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.security.utils.XPathFactory;

import freesm.utils.client.AlgodClientApi;
import freesm.utils.client.KmdClientApi;

public class Configuration {
	
	private String xmlFilePath;
	private Document doc;
	
	private static String NODE_PATH = "/freesm/bot/botNode/path";
	private static String NODE_ADDRESS = "/freesm/bot/botNode/algod/address";
	private static String NODE_PORT = "/freesm/bot/botNode/algod/port";
	private static String NODE_TOKEN = "/freesm/bot/botNode/algod/token";
	
	private static String WALLET_NAME = "/freesm/bot/botNode/botwallet/name";
	private static String WALLET_PASSWORD = "/freesm/bot/botNode/botwallet/pasword";

	private static String KMD_ADDRESS = "/freesm/bot/botNode/botwallet/kmd/address";
	private static String KMD_PORT = "/freesm/bot/botNode/botwallet/kmd/port";
	private static String KMD_TOKEN = "/freesm/bot/botNode/botwallet/kmd/token";

	
	private static String PUBLIC_KEY = "/freesm/bot/publickey";
	private static String SECRET_KEY = "/freesm/bot/secretkey";
	private static String MNEMONIC = "/freesm/bot/mnemonic";
	
	public Configuration(String filePath) {
		xmlFilePath = filePath;
		File xmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to read configuration xml file");
		} catch (SAXException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to read configuration xml file");			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to read configuration xml file");			
		}
	}
	
	private void setElementValue(String xPath, String value) {
		XPath toeknPath = (XPath) XPathFactory.newInstance().newXPathAPI();
		NodeList nodes;
		try {
			nodes = (NodeList)toeknPath.evaluate(xPath, doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to find path: " + xPath);
		}
		if (nodes.getLength() != 1) {
			throw new RuntimeException("Expected one element for " + xPath + " got: " + nodes.getLength());
		}
		nodes.item(0).setTextContent(value);
		saveDocument();
	}

	private void saveDocument() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
	        DOMSource source = new DOMSource(doc);
	        StreamResult result = new StreamResult(new File(this.xmlFilePath));
	        transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to save xml file!");
		} catch (TransformerException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to save xml file!");
		}
	}

	private String getElementValue(String xPath) {
		XPath toeknPath = (XPath) XPathFactory.newInstance().newXPathAPI();
		NodeList nodes;
		try {
			nodes = (NodeList)toeknPath.evaluate(xPath, doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to find path: " + xPath);
		}
		if (nodes.getLength() != 1) {
			throw new RuntimeException("Expected one element for " + xPath + " got: " + nodes.getLength());
		}
		return nodes.item(0).getTextContent();
	}

	public void runChecks(PrintStream out) {
		// Check Node path and address
		// TODO: get algonet address, port and toekn from the path
		
		out.println("Getting node status...");
		String address = this.getElementValue(NODE_ADDRESS);
		String port = this.getElementValue(this.NODE_PORT);
		String token = this.getElementValue(NODE_TOKEN);
		out.println("Address: " + address + port);
		out.println("Token: " + token);
		AlgodClientApi  algodApi = new AlgodClientApi(address+port, token); 
		out.println(algodApi.getNodeStatus().toString());
		
		// Check for the wallet
		String kmdAddress = this.getElementValue(KMD_ADDRESS);
		String kmdPort = this.getElementValue(KMD_PORT);
		String kmdToken = this.getElementValue(KMD_TOKEN);
		out.println("KMD Address: " + kmdAddress + kmdPort);
		out.println("KMD Token: " + kmdToken);
		
		out.println("Checking for the wallet...");
		KmdClientApi kmd = new KmdClientApi(kmdAddress+kmdPort, kmdToken);
		if (kmd.hasWallet("botwallet")) {
			out.println("Wallet botwallet found.");
		} else {
			out.println("Wallet botwallet is missing. Creating...");
			String id = kmd.createWallet("botwallet");
			out.println("Wallet botwallet created with id: " + id);
		}
		
		// Check for fsmbot account
	}
	
}
