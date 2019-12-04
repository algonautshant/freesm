package freesm.bot;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.algorand.algosdk.algod.client.model.Transaction;
import com.algorand.algosdk.crypto.Address;

import freesm.utils.account.Account;
import freesm.utils.client.AlgodClientApi;
import freesm.utils.events.NodeEventListener;
import freesm.utils.events.Request;
import freesm.utils.messaging.ReportMessage;

public class BotReactions implements NodeEventListener{
	
	private BotActions botAction;
	private byte[] botAddress;
	
	public BotReactions(BotActions botConfig) {
		this.botAction = botConfig;
		try {
			Address botAccount  = new Address (BotActions.getBotAccountAddress("config.xml"));
			botAddress  = botAccount.getBytes();
		} catch (NoSuchAlgorithmException e) {
			ReportMessage.errorMessageDefaultAction("Could not get  address.", e);
		}
	}

	public void onRegisterRequest() {
		//botConfig.getAlgodClientApi()
		
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

	public void onNewTransaction(Transaction tx) {
		if (Arrays.equals(tx.getNoteb64(), Request.requestRegisterAccount())) {
			String sender = tx.getFrom();
			botAction.signAndSendTransaction(
					botAction.getAlgodClientApi().assetSendTransaction(
							10010, 1000, getAddress(), sender));
			botAction.signAndSendTransaction(
					botAction.getAlgodClientApi().assetSendTransaction(
							10012, 1000, getAddress(), sender));
			botAction.signAndSendTransaction(
					botAction.getAlgodClientApi().assetSendTransaction(
							10984, 1000, getAddress(), sender));
			ReportMessage.printMessage("Assets sent to: " +  sender);
		} else if (tx.getNoteb64() != null &&
				Arrays.equals(tx.getNoteb64(), 0, 32, botAddress, 0, 32))  {
			String article = new String(Arrays.copyOfRange(tx.getNoteb64(),  33, tx.getNoteb64().length));
			ReportMessage.printMessage("Article  recieved: "  + article +"\n"
					+ "From: " + tx.getFrom() + "\n"
							+ "tId: "  + tx.getTx());
		}
					
		
	}

	public String getAddress() {
		return botAction.getAccountAddress();
	}
	
	



}
