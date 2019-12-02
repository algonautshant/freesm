package freesm.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import freesm.utils.client.AlgodClientApi;
import freesm.utils.client.KmdClientApi;
import freesm.utils.messaging.ReportException;

public class BaseConfiguration {
	
	protected String xmlFilePath;
	protected Document doc;
	
	protected AlgodClientApi algodApi;
	protected KmdClientApi kmd;
	
	protected static String NODE_PATH = "/freesm/Node/path";
	protected static String NODE_NET = "/freesm/Node/algod/net";
	protected static String NODE_TOKEN = "/freesm/Node/algod/token";
	
	protected static String WALLET_NAME = "/freesm/Node/wallet/name";
	protected static String WALLET_PASSWORD = "/freesm/Node/wallet/pasword";

	protected static String KMD_NET = "/freesm/Node/wallet/kmd/net";
	protected static String KMD_TOKEN = "/freesm/Node/wallet/kmd/token";

	
	protected static String ADDRESS = "/freesm/address";
	
	public BaseConfiguration() {
	}
	
	private void loadXml(String filePath) throws IOException {
		xmlFilePath = filePath;
		File xmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			ReportException.errorMessageDefaultAction("Failed to read configuration xml file", e);
		} catch (SAXException e) {
			ReportException.errorMessageDefaultAction("Failed to parse configuration xml file", e);			
		} catch (IOException e) {
			ReportException.errorMessageDefaultAction("Failed to read configuration xml file: " + filePath, e);
		}
	}
	
	protected void updateNodeParameters() {
		// Update the url, port, token:
		String nodePath = this.getElementValue(NODE_PATH);
		String kmdPath = nodePath + "/" + "kmd-v0.5";
		String algodNetP = nodePath + "/" + "algod.net";
		String algodTokenP = nodePath + "/" + "algod.token";
		String kmdNetP = kmdPath + "/" + "kmd.net";
		String kmdTokenP = kmdPath + "/" + "kmd.token";
		
		String algodNet = readFile(algodNetP);
		String algodToken = readFile(algodTokenP);
		String kmdNet = readFile(kmdNetP);
		String kmdToken = readFile(kmdTokenP);
		
		this.setElementValue(NODE_NET, algodNet);
		this.setElementValue(NODE_TOKEN, algodToken);
		this.setElementValue(KMD_NET, kmdNet);
		this.setElementValue(KMD_TOKEN, kmdToken);
	}
	
	private static String readFile(String filepath) {
		File file = new File(filepath);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			try {
				return br.readLine().trim();
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	protected void setElementValue(String xPath, String value) {
		XPath tokenPath = XPathFactory.newInstance().newXPath();
		NodeList nodes = null;
		try {
			nodes = (NodeList)tokenPath.evaluate(xPath, doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			ReportException.runtimeException("Failed to find path: " + xPath, e);
		}
		if (nodes.getLength() > 1) {
			ReportException.runtimeException("Expected one element for " + xPath + " got: " + nodes.getLength());
		}
		if (nodes.getLength() == 0) {
			try {
				Node node = (Node)tokenPath.evaluate(xPath, doc, XPathConstants.NODE);
				node.setTextContent(value);
			} catch (XPathExpressionException e) {
				ReportException.runtimeException("Failed to find path: " + xPath, e);
			}
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
			ReportException.runtimeException("Failed to save xml file!", e);
		} catch (TransformerException e) {
			ReportException.runtimeException("Failed to save xml file!", e);
		}
	}

	protected String getElementValue(String xPath) {
		XPath toeknPath = (XPath) XPathFactory.newInstance().newXPath();
		NodeList nodes = null;
		try {
			nodes = (NodeList)toeknPath.evaluate(xPath, doc, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			ReportException.errorMessageDefaultAction("Failed to find path: " + xPath, e);
			return "";
		}
		if (nodes.getLength() > 1) {
			ReportException.errorMessageDefaultAction("Expected one element for " + xPath + " got: " + nodes.getLength());
		}
		if (nodes.getLength() == 0) {
			return "";
		}
		return nodes.item(0).getNodeValue().trim();
	}

	public String getConfigFilePath() {
		System.out.println("Enter config file path:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		try {
			line = br.readLine().trim();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return line;
	}
	
	public void init(PrintStream out, InputStream in ) {
		// Check Node path and address
		// TODO: get algonet address, port and toekn from the path
		try {
			loadXml(getConfigFilePath());
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		updateNodeParameters();
		out.println("Getting node status...");
		String address = this.getElementValue(NODE_NET);
		String token = this.getElementValue(NODE_TOKEN);
		out.println("Address: " + address);
		out.println("Token: " + token);
		algodApi = new AlgodClientApi(address, token); 
		out.println(algodApi.getNodeStatus().toString());
	}
	
	public AlgodClientApi getAlgodClientApi() {
		return algodApi;
	}
	
	public KmdClientApi getKmdClientApi() {
		return kmd;
	}
	
}
