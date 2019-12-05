package opensm.api;

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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.algorand.algosdk.algod.client.model.TransactionID;
import com.algorand.algosdk.transaction.Transaction;

import opensm.utils.client.AlgodClientApi;
import opensm.utils.client.KmdClientApi;
import opensm.utils.messaging.ReportMessage;

public class Actions {
	
	protected String xmlFilePath;
	protected Document doc;
	
	protected AlgodClientApi algodApi;
	protected KmdClientApi kmd;
	
	protected static String NODE_PATH = "/opensm/Node/path";
	protected static String NODE_NET = "/opensm/Node/algod/net";
	protected static String NODE_TOKEN = "/opensm/Node/algod/token";
	
	protected static String WALLET_NAME = "/opensm/Node/wallet/name";
	protected static String WALLET_PASSWORD = "/opensm/Node/wallet/password";

	protected static String KMD_NET = "/opensm/Node/wallet/kmd/net";
	protected static String KMD_TOKEN = "/opensm/Node/wallet/kmd/token";

	
	protected static String ADDRESS = "/opensm/address";
	
	public Actions() {
	}
	
	public static String getBotAccountAddress(String configFile) {
		Document doc = loadXmlFile(configFile);
		if (null != doc) {
			return getElementValue(ADDRESS, doc);
		}
		return null;
	}
	
	public String getAccountAddress() {
		return getElementValue(ADDRESS);
	}
	
	private static Document loadXmlFile(String xmlFile) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			return dBuilder.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			ReportMessage.errorMessageDefaultAction("Failed to read configuration xml file", e);
		} catch (SAXException e) {
			ReportMessage.errorMessageDefaultAction("Failed to parse configuration xml file", e);			
		} catch (IOException e) {
//			ReportMessage.errorMessageDefaultAction("Failed to read configuration xml file: " + xmlFile, e);
		} 
		return null;
	}
	
	private void loadXml(String filePath) throws IOException {
		xmlFilePath = filePath;
		doc = loadXmlFile(filePath);
		if (null == doc) {
			doc = createXmlConfigDocument();
			saveDocument();
		}
	}
	
	protected void updateNodeParameters() {
		// Update the url, port, token:
		String nodePath = this.getElementValue(NODE_PATH);
		if  (nodePath.isEmpty()) {
			ReportMessage.runtimeException("/opensm/Node/path should be set in the configuration xml.\nBye.");
		}
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
			ReportMessage.runtimeException("Failed to find path: " + xPath, e);
		}
		if (nodes.getLength() > 1) {
			ReportMessage.runtimeException("Expected one element for " + xPath + " got: " + nodes.getLength());
		}
		if (nodes.getLength() == 0) {
			try {
				Node node = (Node)tokenPath.evaluate(xPath, doc, XPathConstants.NODE);
				node.setTextContent(value);
			} catch (XPathExpressionException e) {
				ReportMessage.runtimeException("Failed to find path: " + xPath, e);
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
			ReportMessage.runtimeException("Failed to save xml file!", e);
		} catch (TransformerException e) {
			ReportMessage.runtimeException("Failed to save xml file!", e);
		}
	}

	protected String getElementValue(String xPath) {
		return getElementValue(xPath, doc);
	}
	
	public static String getElementValue(String xPath, Document doc) {
		XPath tokenPath = (XPath) XPathFactory.newInstance().newXPath();
		NodeList nodes = null;
		try {
			nodes = (NodeList)tokenPath.evaluate(xPath, doc, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			ReportMessage.errorMessageDefaultAction("Failed to find path: " + xPath, e);
			return "";
		}
		if (nodes.getLength() > 1) {
			ReportMessage.errorMessageDefaultAction("Expected one element for " + xPath + " got: " + nodes.getLength());
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
	
	protected void register() {
		// Should be implemented by the subclass
	}
	
	public AlgodClientApi getAlgodClientApi() {
		return algodApi;
	}
	
	public KmdClientApi getKmdClientApi() {
		return kmd;
	}
	
	public TransactionID signAndSendTransaction(Transaction tx) {
		return algodApi.sendTransaction(
				kmd.signTransaction(tx));
	}
	
	protected void publishArticle(String article) {
	}

	private Document createXmlConfigDocument() {
		DocumentBuilderFactory dbFactory =
				DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			ReportMessage.runtimeException("Can't create docuemnt.", e);
		}
		Document doc = dBuilder.newDocument();

		Element opensm = doc.createElement("opensm");
		doc.appendChild(opensm);
		Element Node = doc.createElement("Node");
		opensm.appendChild(Node);
		Node.appendChild(doc.createElement("path"));
		Element algod = doc.createElement("algod");
		Node.appendChild(algod);
		algod.appendChild(doc.createElement("net"));
		algod.appendChild(doc.createElement("token"));
		Element wallet = doc.createElement("wallet");
		Node.appendChild(wallet);
		wallet.appendChild(doc.createElement("name"));
		wallet.appendChild(doc.createElement("password"));
		Element kmd = doc.createElement("kmd");
		wallet.appendChild(kmd);
		kmd.appendChild(doc.createElement("net"));
		kmd.appendChild(doc.createElement("token"));
		wallet.appendChild(doc.createElement("address"));
		opensm.appendChild(doc.createElement("address"));
		return doc;
	}
}
