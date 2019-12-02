package freesm.bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import freesm.api.BaseConfiguration;
import freesm.utils.client.KmdClientApi;
import freesm.utils.messaging.ReportException;

public class BotConfiguration extends BaseConfiguration {
	private String configFilePath;
	
	public BotConfiguration(String configFilePath) {
		super();
		this.configFilePath = configFilePath;
	}

	public void init(String configFilePath, PrintStream out, InputStream in ) {

		super.init(out, in);
		// Check for the wallet
		String kmdAddress = this.getElementValue(KMD_NET);
		String kmdToken = this.getElementValue(KMD_TOKEN);
		out.println("KMD Address: " + kmdAddress);
		out.println("KMD Token: " + kmdToken);
		
		out.println("Checking for the wallet...");
		
		String passwd = getElementValue(WALLET_PASSWORD);
		String walletName = getElementValue(WALLET_NAME);
		
		
		kmd = new KmdClientApi(kmdAddress, kmdToken, walletName, passwd);
		if (kmd.hasWallet("botwallet")) {
			out.println("Wallet 'botwallet' found.");
		} else {
			out.println("Wallet botwallet is missing. Creating...");
			out.println("Enter the wallet password ...");
			InputStreamReader ird = new InputStreamReader(in);			
			BufferedReader br = new BufferedReader(ird);
			try {
				passwd = br.readLine();
			} catch (IOException e) {
				ReportException.errorMessageDefaultAction("Failed to read password.", e);
				return;
			}
			String id = kmd.createWallet();
			out.println("Wallet botwallet created with id: " + id);
			this.setElementValue(WALLET_PASSWORD, passwd);
			this.setElementValue(WALLET_NAME, "botwallet");
		}

		// Check for fsmbot account
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
		
		//  Create the assets if not created yet
		// TODO
		
	}
	
	public String getConfigFilePath() {
		return configFilePath;
	}
}
