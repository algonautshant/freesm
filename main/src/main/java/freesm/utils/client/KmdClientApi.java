package freesm.utils.client;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.kmd.client.ApiException;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.auth.ApiKeyAuth;
import com.algorand.algosdk.kmd.client.model.APIV1POSTKeyListResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletInitResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletResponse;
import com.algorand.algosdk.kmd.client.model.APIV1Wallet;
import com.algorand.algosdk.kmd.client.model.CreateWalletRequest;
import com.algorand.algosdk.kmd.client.model.GenerateKeyRequest;
import com.algorand.algosdk.kmd.client.model.InitWalletHandleTokenRequest;
import com.algorand.algosdk.kmd.client.model.ListKeysRequest;

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
	
	public String createWallet(String walletName, String walletPassword) {
        APIV1POSTWalletResponse wallet;
        CreateWalletRequest req = new CreateWalletRequest();
        req.setWalletName(walletName);
        req.setWalletDriverName("sqlite");
        req.setWalletPassword(walletPassword);
        try {
			wallet = kmdApiInstance.createWallet(req);
		} catch (ApiException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to create wallet: " + walletName);			
		}
        return wallet.getWallet().getId();
	}
	
	String getWalletHandle(String walletName, String walletPassword) {
		List<APIV1Wallet> wList = getWalletList();
		String walletId = "";
		for (APIV1Wallet w : wList) {
			if (walletName.contentEquals(w.getName())) {
				walletId = w.getId();
			}
		}
		if (walletId.isEmpty()) {
			throw new RuntimeException("Did not find wallet: " + walletName);
		}
		
		InitWalletHandleTokenRequest req = new InitWalletHandleTokenRequest();
		req.setWalletId(walletName);
		req.setWalletPassword(walletPassword);
		try {
			return kmdApiInstance.initWalletHandleToken(req).getWalletHandleToken();
		} catch (ApiException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the wallet handle.");
		}
	}
	
	public List<String> getAddressesInWallet(String walletName, String walletPassword) {
		
		ListKeysRequest lkrq = new ListKeysRequest();
		lkrq.setWalletHandleToken(this.getWalletHandle(walletName, walletPassword));
		try {
			return kmdApiInstance.listKeysInWallet(lkrq).getAddresses();
		} catch (ApiException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get accounts in the wallet: " + walletName);
		}
	}
	
	public void generateKey(String walletName, String walletPassword) {
        GenerateKeyRequest req = new GenerateKeyRequest();
        req.setDisplayMnemonic(false);
        req.setWalletHandleToken(getWalletHandle(walletName, walletPassword));
        try {
			kmdApiInstance.generateKey(req);
		} catch (ApiException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate a key.");
		}
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
