package freesm.bot.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import freesm.bot.TransactionListener;
import freesm.utils.client.AlgodClientApi;

public class CmdLoop {

	private static String header = "FreeSM Bot\n  "
			+ "Commands: \n"
			+ "runchecks  <path to config.xml> // loads configuration, runs checks and initializes fsmbot\n"
			+ "starttlistening  <path to config.xml> // start listening to transaciton and processes requests"
			+ "stoplistening // stop listening to transactions";
	
	private HashMap<String, Integer>  commandMap;

	public CmdLoop() {
		commandMap = new HashMap<String, Integer>();
		commandMap.put("runchecks", 1);
		commandMap.put("startlistening",  2);
		commandMap.put("stoplistening",  3);
	}
	
	public void startLoop() {
		boolean inturrupted = false;
		TransactionListener tl = null;
		AlgodClientApi algodApi = null;
		Configuration config = null;

		System.out.println(header);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line;
		
		try {
			while((line = br.readLine()) != null) {
				switch(commandMap.get(line.substring(0, line.indexOf(' ')))) {
				case 1: // runchecks
					config = new Configuration(line.substring(line.indexOf(' ')+1));
					config.runChecks(System.out, System.in);
					break;
				case 2:  //  startlistening
					if (algodApi == null || config == null) {
						System.out.println("Run 'runchecks' first to load the configuration");
					} else {
						algodApi = config.getAlgodClientApi();
						tl = new TransactionListener(algodApi);
					}
					break;	
				case 3: 
					if (tl == null) {
						System.out.println("Not listening to transactions. Nothing to stop.");
					} else {
						tl.stopLoop();
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to read input from command line.");
		}
	}
}
