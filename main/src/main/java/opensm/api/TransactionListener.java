package opensm.api;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import com.algorand.algosdk.algod.client.model.Block;
import com.algorand.algosdk.algod.client.model.NodeStatus;
import com.algorand.algosdk.algod.client.model.Transaction;
import com.algorand.algosdk.algod.client.model.TransactionList;

import opensm.utils.client.AlgodClientApi;
import opensm.utils.events.NodeEventListener;

public class TransactionListener extends Thread {
	
	private long lastProcessedRound;
	private volatile boolean stop;
	private NodeStatus status;
	private AlgodClientApi api;
	private PrintStream out;
	private ArrayList<NodeEventListener> listeners;
	
	public TransactionListener(AlgodClientApi api, PrintStream out) {
		lastProcessedRound = -1;
		stop = false;
		this.api = api;
		this.out = out;
		listeners = new ArrayList<NodeEventListener>();
	}

	public synchronized void registerListener(NodeEventListener listener) {
		listeners.add(listener);
		out.println("Listener added.");
	}
	
	void onAssetRequest() {
	}
	
	public synchronized void stopLoop() {
		stop = true;
	}
	
	private void processBlock(Block block) {
		TransactionList transactions = block.getTxns();
		for (Transaction tx : transactions.getTransactions()) {
			for (NodeEventListener lsn : listeners) {
				if  (tx.getPayment() != null &&
						tx.getPayment().getTo().contentEquals(lsn.getAddress())) {
					lsn.onNewTransaction(tx);
				} else if  (tx.getType().contentEquals("axfer"))  {
					lsn.onNewTransaction(tx);
				}
				
			}
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
		this.lastProcessedRound = 0;
		long currentRound = lastRound;
		out.println("Started listening.");
		do {
			Block block = null;
			if (currentRound > this.lastProcessedRound) {
				for (NodeEventListener lsn : listeners) {
					lsn.onNewRound(currentRound);
				}
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
		} while (false == stop);
		out.println("Stopped listening.");
	}
}

