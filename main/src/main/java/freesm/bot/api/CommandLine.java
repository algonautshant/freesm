package freesm.bot.api;

import freesm.bot.TransactionListener;
import freesm.utils.client.AlgodClientApi;

public class CommandLine {

	public static void main(String[] args) {
		String token = "dbb134f8cffa1b2dfac5af493e6487f1b2a8a05af6489191684d79cfb9467891";
		String net = "127.0.0.1:65014";
		
		AlgodClientApi api = new AlgodClientApi(net, token);
		TransactionListener tl = new TransactionListener(api);
		
		tl.start();
		
		try {
			tl.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
