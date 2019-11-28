package freesm.bot;

import java.math.BigInteger;
import java.util.ArrayList;

import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.model.Block;
import com.algorand.algosdk.algod.client.model.NodeStatus;
import com.algorand.algosdk.algod.client.model.Transaction;
import com.algorand.algosdk.algod.client.model.TransactionList;

import freesm.utils.client.AlgodClientApi;
import freesm.utils.events.NodeEventListener;

public class TransactionListener extends Thread {
	
	private long firstListenedRound;
	private long lastProcessedRound;
	private volatile boolean stop;
	private NodeStatus status;
	private AlgodClientApi api;
	
	public TransactionListener(AlgodClientApi api) {
		firstListenedRound = -1;
		lastProcessedRound = -1;
		stop = false;
		this.api = api;
	}
	
	ArrayList<NodeEventListener> listeners;

	public void registerListener(NodeEventListener listener) {
		listeners.add(listener);
	}
	
	void onAssetRequest() {
	}
	
	public synchronized void stopLoop() {
		stop = true;
	}
	
	private void processBlock(Block block) {
		TransactionList transactions = block.getTxns();
		for (Transaction tx : transactions.getTransactions()) {
			
			System.out.println(tx.toString());
			
		}
	}
	
	private void updateStatus() {
		status = api.getNodeStatus();
	}
	

	private long getLastRound() {
		this.updateStatus();
		return status.getLastRound().longValue();
	}
	
	private Block getBlock(long round) {
		return api.getBlock(round);
	}
	
	public void run() {
		startLoop();
	}
	
	public void startLoop() {
		long lastRound = this.getLastRound();
		this.firstListenedRound = 0;
		this.lastProcessedRound = 0;
		long currentRound = lastRound;

		do {
			Block block = null;
			if (currentRound > this.lastProcessedRound) {
				block = this.getBlock(currentRound);
				this.processBlock(block);								
				this.lastProcessedRound = currentRound;
			}

			long nextRound = this.getLastRound();
			if (nextRound == this.lastProcessedRound) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			currentRound = Math.min(currentRound+1, nextRound);
			System.out.println("This round: " + currentRound + " last round: " + nextRound);
		} while (false == stop);
	}
}

