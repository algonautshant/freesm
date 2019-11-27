package freesm.utils.client;

import java.math.BigInteger;

import com.algorand.algosdk.algod.client.AlgodClient;
import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.api.AlgodApi;
import com.algorand.algosdk.algod.client.auth.ApiKeyAuth;
import com.algorand.algosdk.algod.client.model.NodeStatus;
import com.algorand.algosdk.algod.client.model.TransactionID;
import com.algorand.algosdk.algod.client.model.TransactionParams;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;

public class AlgodClientApi {
	private AlgodApi algodApiInstance;
	
	public AlgodClientApi(String algodApiAddr, String algodApiToken) {
		AlgodClient algodClient = new AlgodClient();
		
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
	
	public Transaction getTransaction() {
        TransactionParams params;
		try {
			params = algodApiInstance.transactionParams();
		} catch (ApiException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Failed to get transaction parameters.");
		}
        BigInteger feePerByte = params.getFee();
        Digest genesisHash = new Digest(params.getGenesishashb64());
        String genesisID = params.getGenesisID();
        System.out.println("Suggested Fee: " + feePerByte);
        NodeStatus s;
		try {
			s = algodApiInstance.getStatus();
		} catch (ApiException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get not status.");
		}
        long firstRound = s.getLastRound().longValue();
        System.out.println("Current Round: " + firstRound);

        Transaction tx = new Transaction(
        		null, // sender
        		feePerByte,
        		BigInteger.valueOf(firstRound), 
        		BigInteger.valueOf(firstRound + 1000), 
        		null, // note
        		BigInteger.valueOf(0), // amount
        		null, // receiver
        		genesisID,
        		genesisHash);
        return tx;
	}
	
	public TransactionID sendTransaction(SignedTransaction signedTransaction) {
        byte[] encodedTxBytes = Encoder.encodeToMsgPackNoException(signedTransaction);
        TransactionID id;
		try {
			id = algodApiInstance.rawTransaction(encodedTxBytes);
		} catch (ApiException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to submit raw transaction.");
		}
        return id;
	}
	
	public NodeStatus getNodeStatus() {
		try {
			return algodApiInstance.getStatus();
		} catch (ApiException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get node status.");
		}
	}
	
	

}
