package freesm.publisher;

import freesm.utils.events.NodeEventListener;

public class PublisherReactions implements NodeEventListener {
	private  PublisherActions  publisherConfig;
	
	public PublisherReactions(PublisherActions publisherConfig) {
		this.publisherConfig = publisherConfig;
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


	public String getAddress() {
		return publisherConfig.getAccountAddress();
	}
		
}
