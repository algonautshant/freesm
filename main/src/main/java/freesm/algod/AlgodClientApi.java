package freesm.algod;

import com.algorand.algosdk.algod.client.AlgodClient;
import com.algorand.algosdk.algod.client.api.AlgodApi;
import com.algorand.algosdk.algod.client.auth.ApiKeyAuth;
import com.algorand.algosdk.algod.client.model.TransactionID;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

public class AlgodClientApi {
	private AlgodClient algodClient;
	private AlgodApi algodApiInstance;
	
	public AlgodClientApi(String algodApiAddr, String algodApiToken) {
		algodClient = new AlgodClient();
		
        if (algodApiAddr.indexOf("//") == -1) {
        	algodApiAddr = "http://" + algodApiAddr;
        }
        algodClient = new AlgodClient();
        algodClient.setBasePath(algodApiAddr);
        // Configure API key authorization: api_key
        ApiKeyAuth api_key = (ApiKeyAuth) algodClient.getAuthentication("api_key");
        api_key.setApiKey(algodApiToken);
        algodApiInstance = new AlgodApi(algodClient);
	}
	
	public void sendTransaction(SignedTransaction tx) {
        byte[] encodedTxBytes = Encoder.encodeToMsgPack(tx);
        TransactionID id = algodApiInstance.rawTransaction(encodedTxBytes);

	}

}
