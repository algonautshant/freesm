package freesm.bot.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.kmd.client.model.GenerateKeyRequest;
import com.sun.org.apache.xml.internal.security.utils.XPathFactory;

import freesm.utils.client.AlgodClientApi;
import freesm.utils.client.KmdClientApi;

public class Configuration {
	
	private String xmlFilePath;
	private Document doc;
	
	private AlgodClientApi algodApi;
	private KmdClientApi kmd;
	
	private static String NODE_PATH = "/freesm/bot/botNode/path";
	private static String NODE_ADDRESS = "/freesm/bot/botNode/algod/address";
	private static String NODE_PORT = "/freesm/bot/botNode/algod/port";
	private static String NODE_TOKEN = "/freesm/bot/botNode/algod/token";
	
	private static String WALLET_NAME = "/freesm/bot/botNode/botwallet/name";
	private static String WALLET_PASSWORD = "/freesm/bot/botNode/botwallet/pasword";

	private static String KMD_ADDRESS = "/freesm/bot/botNode/botwallet/kmd/address";
	private static String KMD_PORT = "/freesm/bot/botNode/botwallet/kmd/port";
	private static String KMD_TOKEN = "/freesm/bot/botNode/botwallet/kmd/token";

	
	private static String ADDRESS = "/freesm/bot/address";
	
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

	public void runChecks(PrintStream out, InputStream in ) {
		// Check Node path and address
		// TODO: get algonet address, port and toekn from the path
		
		InputStreamReader ird = new InputStreamReader(in);
		out.println("Getting node status...");
		String address = this.getElementValue(NODE_ADDRESS);
		String port = this.getElementValue(this.NODE_PORT);
		String token = this.getElementValue(NODE_TOKEN);
		out.println("Address: " + address + port);
		out.println("Token: " + token);
		algodApi = new AlgodClientApi(address+port, token); 
		out.println(algodApi.getNodeStatus().toString());
		
		// Check for the wallet
		String kmdAddress = this.getElementValue(KMD_ADDRESS);
		String kmdPort = this.getElementValue(KMD_PORT);
		String kmdToken = this.getElementValue(KMD_TOKEN);
		out.println("KMD Address: " + kmdAddress + kmdPort);
		out.println("KMD Token: " + kmdToken);
		
		out.println("Checking for the wallet...");
		kmd = new KmdClientApi(kmdAddress+kmdPort, kmdToken);
		if (kmd.hasWallet("botwallet")) {
			out.println("Wallet 'botwallet' found.");
		} else {
			out.println("Wallet botwallet is missing. Creating...");
			out.println("Enter the wallet password ...");
			BufferedReader br = new BufferedReader(ird);
			String passwd;
			try {
				passwd = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to read password.");
			}
			String id = kmd.createWallet("botwallet", passwd);
			out.println("Wallet botwallet created with id: " + id);
			this.setElementValue(WALLET_PASSWORD, passwd);
			this.setElementValue(WALLET_NAME, "botwallet");
		}

		// Check for fsmbot account
		String passwd = getElementValue(WALLET_PASSWORD);
		String walletName = getElementValue(WALLET_NAME);
		List<String> addresses = kmd.getAddressesInWallet(walletName, passwd);
		if (0 == addresses.size()) {
			out.println("Generating a key using kmd.");
			kmd.generateKey(walletName, passwd);
			addresses = kmd.getAddressesInWallet(walletName, passwd);
		}
		for (String addr : addresses) {
			out.println(algodApi.getAccountInformation(addr));
		}		
		setElementValue(ADDRESS, addresses.get(0));
	}
	
	public AlgodClientApi getAlgodClientApi() {
		return algodApi;
	}
	
}
