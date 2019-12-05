package opensm.publisher;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.algorand.algosdk.crypto.Address;

import opensm.bot.BotActions;
import opensm.utils.events.NodeEventListener;
import opensm.utils.messaging.ReportMessage;

public class PublisherReactions implements NodeEventListener {
	private  PublisherActions  publisherConfig;
	private byte [] bot;
	public PublisherReactions(PublisherActions publisherConfig) {
		this.publisherConfig = publisherConfig;
		String botaddrstr = BotActions.getBotAccountAddress("config.xml");
		if (null == botaddrstr) {
			ReportMessage.runtimeException("Could not find config.xml file with bot address.");
		}
		Address botaddr =  null;
		try {
			botaddr = new Address(botaddrstr);
		} catch (NoSuchAlgorithmException e) {
			ReportMessage.errorMessageDefaultAction("Failed to get bot address.", e);
			return;
		}
		bot  =  botaddr.getBytes();
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
		if (tx.getNoteb64() != null &&
				Arrays.equals(tx.getNoteb64(), 0, 32, bot, 0, 32))  {
			String article = new String(Arrays.copyOfRange(tx.getNoteb64(),  33, tx.getNoteb64().length));
			ReportMessage.printMessage("Article  recieved: "  + article +"\n"
					+ "From: " + tx.getFrom() + "\n"
							+ "tId: "  + tx.getTx());
		}
	}


	public String getAddress() {
		return publisherConfig.getAccountAddress();
	}
		
}
