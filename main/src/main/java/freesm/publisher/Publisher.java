package freesm.publisher;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.model.TransactionID;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;

import freesm.utils.client.AlgodClientApi;
import freesm.utils.events.NodeEventListener;
import freesm.utils.messaging.ReportException;
import freesm.utils.account.Account;

public class Publisher implements NodeEventListener {
	
	private AlgodClientApi api;
	private Account account;
	
	private TransactionID signAndSendTransaction(Transaction tx) {
		try {
			tx.sender = new Address(account.getAddress());
//			tx.receiver = new Address(FsmBot.getBotAddress());
			tx.amount = BigInteger.valueOf(100);
		} catch (NoSuchAlgorithmException e) {
			ReportException.errorMessageDefaultAction("Failed to get Address from address string.", e);
		}
		SignedTransaction stx = account.signTransaction(tx);
		TransactionID tid = api.sendTransaction(stx);
		return tid;
	}
	
	public Publisher(PublisherConfiguration publisherConfig) {
	}
	
	public void registerAccount() {
		Transaction tx = api.getTransaction();
//		tx.note = BotRequest.requestRegisterAccount();
		signAndSendTransaction(tx);		
	}
	
	public void publishArticle(String url) {
		
		
	}

	public void onRegisterRequest() {
		// TODO Auto-generated method stub
		
	}

	public void onPublishRequest() {
		// TODO Auto-generated method stub
		
	}

	public void onCommentRequest() {
		// TODO Auto-generated method stub
		
	}

	public void onNewRound(long currentRound) {
		// TODO Auto-generated method stub
		
	}

	public void onNewTransaction(com.algorand.algosdk.algod.client.model.Transaction tx) {
		// TODO Auto-generated method stub
		
	}
		
}
