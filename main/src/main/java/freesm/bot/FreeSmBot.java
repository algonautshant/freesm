package freesm.bot;

import com.algorand.algosdk.algod.client.model.Transaction;

import freesm.utils.account.Account;
import freesm.utils.client.AlgodClientApi;
import freesm.utils.events.NodeEventListener;

public class FreeSmBot implements NodeEventListener{
	
	private BotConfiguration botConfig;
	
	public FreeSmBot(BotConfiguration botConfig) {
		this.botConfig = botConfig;
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
		// TODO Auto-generated method stub
		
	}
	
	



}
