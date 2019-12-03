package freesm.api;

import com.algorand.algosdk.algod.client.model.Transaction;

import freesm.utils.events.NodeEventListener;

public class BaseNodeEventListener implements NodeEventListener {

	public void onRegisterRequest() {
		// TODO Auto-generated method stub

	}

	public void onAssetRequest() {
		// TODO Auto-generated method stub

	}

	public void onPublishRequest() {
		// TODO Auto-generated method stub

	}

	public void onCommentRequest() {
		// TODO Auto-generated method stub

	}

	public void onNewRound(long currentRound) {
		System.out.println("Current round: " + currentRound);

	}

	public void onNewTransaction(Transaction tx) {
		System.out.println(tx.toString());
	}

	public String getAddress() {
		return "";
	}

}
