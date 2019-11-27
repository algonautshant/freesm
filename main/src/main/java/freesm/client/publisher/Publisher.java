package freesm.client.publisher;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.model.TransactionID;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;

import freesm.utils.client.AlgodClientApi;
import freesm.client.fsmbot.BotRequest;
import freesm.client.fsmbot.FsmBot;
import freesm.utils.account.Account;

public class Publisher {
	
	private AlgodClientApi api;
	private Account account;
	
	private TransactionID signAndSendTransaction(Transaction tx) {
		try {
			tx.sender = new Address(account.getAddress());
			tx.receiver = new Address(FsmBot.getBotAddress());
			tx.amount = BigInteger.valueOf(100);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get Address from address string.");
		}
		SignedTransaction stx = account.signTransaction(tx);
		TransactionID tid = api.sendTransaction(stx);
		return tid;
	}
	
	public Publisher(String accountMnemonic) {
		this(accountMnemonic, "127.0.0.1:65014", "dbb134f8cffa1b2dfac5af493e6487f1b2a8a05af6489191684d79cfb9467891");
	}
	
	public Publisher(String accountMnemonic, String algodApiAddr, String algodApiToken) {
		account = Account.loadAccount(accountMnemonic);
		api = new AlgodClientApi(algodApiAddr, algodApiToken);
	}
	
	public void registerAccount() {
		Transaction tx = api.getTransaction();
		tx.note = BotRequest.requestRegisterAccount();
		signAndSendTransaction(tx);		
	}
	
	public void publishArticle(String url) {
		
		
	}
		
}
