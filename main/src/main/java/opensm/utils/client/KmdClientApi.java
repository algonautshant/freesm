package opensm.utils.client;

import java.util.List;

import com.algorand.algosdk.kmd.client.ApiException;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletInitResponse;
import com.algorand.algosdk.kmd.client.model.APIV1POSTWalletResponse;
import com.algorand.algosdk.kmd.client.model.APIV1Wallet;
import com.algorand.algosdk.kmd.client.model.CreateWalletRequest;
import com.algorand.algosdk.kmd.client.model.GenerateKeyRequest;
import com.algorand.algosdk.kmd.client.model.InitWalletHandleTokenRequest;
import com.algorand.algosdk.kmd.client.model.ListKeysRequest;
import com.algorand.algosdk.kmd.client.model.SignTransactionRequest;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

import opensm.utils.messaging.ReportMessage;

public class KmdClientApi {

	private KmdApi kmdApiInstance;
	private String walletName;
	private String walletPasswrd;
	
	public KmdClientApi(
			String kmdApiAddr, 
			String kmdApiToken, 
			String walletName,
			String walletPasswrd) {
		KmdClient kmdClientInstance;
		this.walletName = walletName;
		this.walletPasswrd =  walletPasswrd;
		
        kmdClientInstance = new KmdClient();
        if (kmdApiAddr.indexOf("//") == -1) {
        	kmdApiAddr = "http://" + kmdApiAddr;
        }
        kmdClientInstance.setBasePath(kmdApiAddr);
        kmdClientInstance.setApiKey(kmdApiToken);
        kmdApiInstance = new KmdApi(kmdClientInstance);
	}
	
	public boolean hasWallet(String name) {
		List<APIV1Wallet> list = getWalletList();
		if (list == null) {
			return false;
		}
		for (APIV1Wallet w : list) {
			if (name.contentEquals(w.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public String createWallet() {
        APIV1POSTWalletResponse wallet;
        CreateWalletRequest req = new CreateWalletRequest();
        req.setWalletName(walletName);
        req.setWalletDriverName("sqlite");
        req.setWalletPassword(walletPasswrd);
        try {
			wallet = kmdApiInstance.createWallet(req);
		} catch (ApiException e) {
			ReportMessage.errorMessageDefaultAction("Failed to create wallet: " + walletName, e);
			return "";
		}
        return wallet.getWallet().getId();
	}
	
	public String getWalletHandle() {
		List<APIV1Wallet> wList = getWalletList();
		if (wList == null) {
			return null;
		}
		String walletId = "";
		for (APIV1Wallet w : wList) {
			if (walletName.contentEquals(w.getName())) {
				walletId = w.getId();
			}
		}
		if (walletId.isEmpty()) {
			ReportMessage.errorMessageDefaultAction("Did not find wallet: " + walletName);
			return "";
		}
		
		InitWalletHandleTokenRequest req = new InitWalletHandleTokenRequest();
		req.setWalletId(walletId);
		req.setWalletPassword(walletPasswrd);
		try {
			APIV1POSTWalletInitResponse kmdi = kmdApiInstance.initWalletHandleToken(req);
			return kmdi.getWalletHandleToken();
		} catch (ApiException e) {
			ReportMessage.errorMessageDefaultAction("Failed to get the wallet handle.", e);
			return "";
		}
	}
	
	public List<String> getAddressesInWallet() {
		
		ListKeysRequest lkrq = new ListKeysRequest();
		lkrq.setWalletHandleToken(this.getWalletHandle());
		try {
			return kmdApiInstance.listKeysInWallet(lkrq).getAddresses();
		} catch (ApiException e) {
			ReportMessage.errorMessageDefaultAction("Failed to get accounts in the wallet: " + walletName, e);
			return null;
		}
	}
	
	public void generateKey() {
        GenerateKeyRequest req = new GenerateKeyRequest();
        req.setDisplayMnemonic(false);
        req.setWalletHandleToken(getWalletHandle());
        try {
			kmdApiInstance.generateKey(req);
		} catch (ApiException e) {
			ReportMessage.errorMessageDefaultAction("Failed to generate a key.", e);
		}
	}
	
	private List<APIV1Wallet> getWalletList() {
		List<APIV1Wallet> list;
		try {
			list = kmdApiInstance.listWallets().getWallets();
		} catch (ApiException e) {
			ReportMessage.runtimeException("Failed to get the wallet list from kmd.", e);
			return null;
		}
		return list;
	}
	
	public byte [] signTransaction(Transaction tx)  {
		
        SignTransactionRequest req = new SignTransactionRequest();
        req.setTransaction(Encoder.encodeToMsgPackNoException(tx));
        req.setWalletHandleToken(getWalletHandle());
        req.setWalletPassword(walletPasswrd);
        try {
        	return kmdApiInstance.signTransaction(req).getSignedTransaction();
		} catch (ApiException e) {
			ReportMessage.errorMessageDefaultAction("Failed to sign transaction.", e);
			return null;
		}        
	}
}
