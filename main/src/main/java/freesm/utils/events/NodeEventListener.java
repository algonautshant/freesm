package freesm.utils.events;

import com.algorand.algosdk.algod.client.model.Transaction;

public interface NodeEventListener {
	
	public void onRegisterRequest();
	
	public void onAssetRequest();
	
	public void onPublishRequest();
	
	public void onCommentRequest();
	
	public void onNewRound(long currentRound);
	
	public void onNewTransaction(Transaction tx);

}
