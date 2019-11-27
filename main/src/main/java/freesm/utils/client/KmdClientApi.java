package freesm.utils.client;

import java.util.List;

import com.algorand.algosdk.kmd.client.ApiException;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.auth.ApiKeyAuth;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletResponse;
import com.algorand.algosdk.kmd.client.model.APIV1Wallet;
import com.algorand.algosdk.kmd.client.model.CreateWalletRequest;

public class KmdClientApi {

	private KmdApi kmdApiInstance;
	
	public KmdClientApi(String kmdApiAddr, String kmdApiToken) {
		KmdClient kmdClientInstance;
        kmdClientInstance = new KmdClient();
        kmdClientInstance.setBasePath(kmdApiAddr);
        ApiKeyAuth api_key = (ApiKeyAuth) kmdClientInstance.getAuthentication("api_key");
        api_key.setApiKey(kmdApiToken);
        kmdApiInstance = new KmdApi(kmdClientInstance);
	}
	
	public boolean hasWallet(String name) {
		List<APIV1Wallet> list = getWalletList();
		for (APIV1Wallet w : list) {
			if (name.contentEquals(w.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public String createWallet(String walletName) {
        APIV1POSTWalletResponse wallet;
        CreateWalletRequest req = new CreateWalletRequest()
        		.walletName(walletName)
        		.walletDriverName("sqlite");
        try {
			wallet = kmdApiInstance.createWallet(req);
		} catch (ApiException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to create wallet: " + walletName);			
		}
        return wallet.getWallet().getId();
	}
	
	private List<APIV1Wallet> getWalletList() {
		List<APIV1Wallet> list;
		try {
			list = kmdApiInstance.listWallets().getWallets();
		} catch (ApiException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the wallet list from kmd.");
		}
		return list;
	}
}
