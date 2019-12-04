package opensm.utils.client;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import com.algorand.algosdk.algod.client.AlgodClient;
import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.api.AlgodApi;
import com.algorand.algosdk.algod.client.auth.ApiKeyAuth;
import com.algorand.algosdk.algod.client.model.Account;
import com.algorand.algosdk.algod.client.model.Block;
import com.algorand.algosdk.algod.client.model.NodeStatus;
import com.algorand.algosdk.algod.client.model.TransactionID;
import com.algorand.algosdk.algod.client.model.TransactionParams;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.Transaction.Type;

import opensm.utils.messaging.ReportMessage;

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
	
	public Transaction algoSendTransaction (
			long amount,
			String fromAddress,
			String toAddress) {

		Transaction tx = this.getBlankTransaction();
		try {
			tx.sender = new Address(fromAddress);
			tx.type = Type.Payment;
			tx.receiver = new Address(toAddress);
			tx.amount = BigInteger.valueOf(amount);
		} catch (NoSuchAlgorithmException e) {
			ReportMessage.errorMessageDefaultAction("Failed to get account from address.", e);
			return null;
		}
		try {
			com.algorand.algosdk.account.Account.setFeeByFeePerByte(tx, tx.fee);
		} catch (NoSuchAlgorithmException e) {
			ReportMessage.errorMessageDefaultAction("Failed to get the fee.", e);
			return null;
		}
		return  tx;
	}

	public Transaction assetAcceptTransaction (
			long assetId, 
			String fromAddress) {
		TransactionParams params = this.getTransactionParams();
		try {
			Transaction tx = Transaction.createAssetAcceptTransaction(
					new Address(fromAddress),
					params.getFee(),
					params.getLastRound(),
					BigInteger.valueOf(params.getLastRound().longValue()+1000),
					null,
					params.getGenesisID(),
					new Digest(params.getGenesishashb64()),
					BigInteger.valueOf(assetId));
			com.algorand.algosdk.account.Account.setFeeByFeePerByte(tx, tx.fee);
			return tx;
		} catch (NoSuchAlgorithmException e) {
			ReportMessage.errorMessageDefaultAction("Could not create address from: " + fromAddress, e);
			return null;
		}
	}

	public Transaction assetSendTransaction(
			long assetId, 
			long assets,
			String fromAddress,
			String toAddress) {
		return assetSendTransaction(assetId, assets, fromAddress, toAddress, null);
				
	}
	public Transaction assetSendTransaction(
			long assetId, 
			long assets,
			String fromAddress,
			String toAddress,
			byte[]  note) {
		TransactionParams params = this.getTransactionParams();
		try {
			Transaction tx = Transaction.createAssetTransferTransaction(
					new Address(fromAddress),
					new Address(toAddress),
					new Address(),
					BigInteger.valueOf(assets),
					params.getFee(),
				params.getLastRound(),
				BigInteger.valueOf(params.getLastRound().longValue()+1000),
				note,
				params.getGenesisID(),
				new Digest(params.getGenesishashb64()),
				BigInteger.valueOf(assetId));
			com.algorand.algosdk.account.Account.setFeeByFeePerByte(tx, tx.fee);
			return tx;
		} catch (NoSuchAlgorithmException e) {
			ReportMessage.errorMessageDefaultAction("Could not create address from: " + fromAddress + " or : " + toAddress, e);
			return null;
		}
	}
	
	
	private TransactionParams getTransactionParams() {
        TransactionParams params = null;
		try {
			params = algodApiInstance.transactionParams();
		} catch (ApiException e1) {
			ReportMessage.errorMessageDefaultAction("Failed to get transaction parameters.", e1);
		}
		return params;
	}
	
	public Transaction getBlankTransaction() {
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
        NodeStatus status;
		try {
			status = algodApiInstance.getStatus();
		} catch (ApiException e) {
			ReportMessage.errorMessageDefaultAction("Failed to get not status.", e);
			return null;
		}
        long firstRound = status.getLastRound().longValue();

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
	
	public TransactionID sendTransaction(byte[] signedTransaction) {
        TransactionID id;
		try {
			id = algodApiInstance.rawTransaction(signedTransaction);
		} catch (ApiException e) {
			ReportMessage.errorMessageDefaultAction("Failed to submit raw transaction.", e);
			return null;
		}
        return id;
	}
	
	public NodeStatus getNodeStatus() {
		try {
			return algodApiInstance.getStatus();
		} catch (ApiException e) {
			ReportMessage.errorMessageDefaultAction("Failed to get node status.", e);
			return null;
		}
	}
	
	public Block getBlock(long round) {
		try {
			return algodApiInstance.getBlock(BigInteger.valueOf(round));
		} catch (ApiException e) {
			ReportMessage.errorMessageDefaultAction("Failed to get block: " + round, e);
			return null;
		}
	}
	
	public Account getAccountInformation(String address) {
		try {
			return algodApiInstance.accountInformation(address);
		} catch (ApiException e) {
			ReportMessage.errorMessageDefaultAction("Failed to get account information.", e);
			return null;
		}
	}	
}
