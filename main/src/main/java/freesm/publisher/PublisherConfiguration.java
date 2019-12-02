package freesm.publisher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import freesm.api.BaseConfiguration;
import freesm.utils.Utils;
import freesm.utils.client.KmdClientApi;
import freesm.utils.messaging.ReportException; 

public class PublisherConfiguration extends BaseConfiguration{

	private String configFilePath;
	
	public PublisherConfiguration(String configFilePath) {
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
				ReportException.errorMessageDefaultAction("Try init again. Invalid wallet name.");
				return;
			}
		}
		
		if (passwd.isEmpty()) {
			System.out.println("Enter the password for wallet: " + walletName);
			passwd = Utils.readInput(in, "Failed reading wallet password");
			if (passwd.isEmpty()) {
				ReportException.errorMessageDefaultAction("Try init again. Invalid wallet password.");
				return;
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
		for (String addr : addresses) {
			out.println(algodApi.getAccountInformation(addr));
		}		
		setElementValue(ADDRESS, addresses.get(0));
		
		//  Accept the assets if not accepted yet
		// TODO
		
	}
	
	public String getConfigFilePath() {
		return configFilePath;
	}
}
