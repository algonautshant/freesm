package opensm.publisher;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import com.algorand.algosdk.algod.client.model.TransactionID;
import com.algorand.algosdk.transaction.Transaction;

import opensm.api.Actions;
import opensm.bot.BotActions;
import opensm.utils.Utils;
import opensm.utils.client.KmdClientApi;
import opensm.utils.events.Request;
import opensm.utils.messaging.ReportMessage; 

public class PublisherActions extends Actions{

	private String configFilePath;
	
	public PublisherActions(String configFilePath) {
		super();
		this.configFilePath = configFilePath;
	}

	public void init(PrintStream out, InputStream in ) {
		super.init(out, in);
		// Check for the wallet
		String kmdAddress = this.getElementValue(KMD_NET);
		String kmdToken = this.getElementValue(KMD_TOKEN);
		out.println("KMD Address: " + kmdAddress);
		out.println("KMD Token: " + kmdToken);
		
		out.println("Checking for the wallet...");
		
		String passwd = getElementValue(WALLET_PASSWORD);
		String walletName = getElementValue(WALLET_NAME);
		if (walletName.isEmpty()) {
			System.out.println("Enter wallet name:");
			walletName = Utils.readInput(in, "Failed reading wallet name");
			if (walletName.isEmpty()) {
				ReportMessage.errorMessageDefaultAction("Try init again. Invalid wallet name.");
				return;
			}
		}
		
		if (passwd.isEmpty()) {
			System.out.println("Enter the password for wallet: " + walletName);
			passwd = Utils.readInput(in, "Failed reading wallet password");
			if (passwd.isEmpty()) {
				ReportMessage.errorMessageDefaultAction("No password set for wallet: " + walletName);
			}
		}
		kmd = new KmdClientApi(kmdAddress, kmdToken, walletName, passwd);
		if (kmd.hasWallet(walletName)) {
			out.println("Wallet " + walletName + " found.");
		} else {
			out.println("Wallet " + walletName + " is missing. Creating...");
			String id = kmd.createWallet();
			out.println("Wallet " + walletName + " created with id: " + id);
		}
		this.setElementValue(WALLET_PASSWORD, passwd);
		this.setElementValue(WALLET_NAME, walletName);


		// Check for publisher account
		List<String> addresses = kmd.getAddressesInWallet();
		if (addresses == null || 0 == addresses.size()) {
			out.println("Generating a key using kmd.");
			kmd.generateKey();
			addresses = kmd.getAddressesInWallet();
		}
		if (addresses == null) {
			return;
		}
		for (String addr : addresses) {
			out.println(algodApi.getAccountInformation(addr));
		}		
		setElementValue(ADDRESS, addresses.get(0));		
	}
	
	public String getConfigFilePath() {
		return configFilePath;
	}
	
	protected void register() {
		signAndSendTransaction(
				algodApi.assetSendTransaction(10010, 
						0, 
						getAccountAddress(), 
						getAccountAddress()));// accept asset transaction
		signAndSendTransaction(
				algodApi.assetSendTransaction(10012, 
						0, 
						getAccountAddress(), 
						getAccountAddress()));// accept asset transaction
		signAndSendTransaction(
				algodApi.assetSendTransaction(10984, 
						0, 
						getAccountAddress(), 
						getAccountAddress()));// accept asset transaction
		
		Transaction tx = algodApi.algoSendTransaction(
				1000, 
				this.getAccountAddress(), 
				getBotAccountAddress("config.xml"));
		tx.note = Request.requestRegisterAccount();
		TransactionID tid = signAndSendTransaction(tx);
		if (null != tid) {
			tid.toString();
		}
	}
	
	protected void publishArticle(String article) {
		String bot = BotActions.getBotAccountAddress("config.xml");
		TransactionID  id  = signAndSendTransaction(
				algodApi.assetSendTransaction(
						10984, 
						1,
						getAccountAddress(),
						bot,
						Request.requestPublish(bot, article)));
		ReportMessage.printMessage("Article \""  + article + "\" published: " + id.toString());
	}

}
