package freesm.bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import freesm.api.BaseConfiguration;
import freesm.utils.client.KmdClientApi;

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
		kmd = new KmdClientApi(kmdAddress, kmdToken);
		if (kmd.hasWallet("botwallet")) {
			out.println("Wallet 'botwallet' found.");
		} else {
			out.println("Wallet botwallet is missing. Creating...");
			out.println("Enter the wallet password ...");
			InputStreamReader ird = new InputStreamReader(in);			
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
		if (addresses == null || 0 == addresses.size()) {
			out.println("Generating a key using kmd.");
			kmd.generateKey(walletName, passwd);
			addresses = kmd.getAddressesInWallet(walletName, passwd);
		}
		for (String addr : addresses) {
			out.println(algodApi.getAccountInformation(addr));
		}		
		setElementValue(ADDRESS, addresses.get(0));
	}
	
	public String getConfigFilePath() {
		return configFilePath;
	}
}
