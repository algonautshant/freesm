package freesm.bot.api;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import freesm.bot.TransactionListener;
import freesm.utils.client.AlgodClientApi;

public class CmdLoop {

	private static String header = "FreeSM Bot\n  "
			+ "Commands: \n"
			+ "runchecks  <path to config.xml> // loads configuration, runs checks and initializes fsmbot\n"
			+ "starttlistening                 // start listening to transaciton and processes requests\n"
			+ "stoplistening                   // stop listening to transactions\n"
			+ "pwd                             // get current working directory";
	
	private HashMap<String, Integer>  commandMap;

	public CmdLoop() {
		commandMap = new HashMap<String, Integer>();
		commandMap.put("runchecks", 1);
		commandMap.put("startlistening",  2);
		commandMap.put("stoplistening",  3);
		commandMap.put("pwd",  4);
	}
	
	public void startLoop() {
		boolean inturrupted = false;
		TransactionListener tl = null;
		AlgodClientApi algodApi = null;
		Configuration config = null;

		System.out.println(header);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line;
		System.out.print(">> ");		
		try {
			while((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				if (!st.hasMoreTokens()) {
					System.out.print(">> ");
					continue;
				}
				String keyword = st.nextToken();
				String arg = st.hasMoreTokens() ? st.nextToken() : "";
				int commandId = 9999;
				try {
					commandId = commandMap.get(keyword);
				} catch (NullPointerException e) {}
				switch(commandId) {
				case 1: // runchecks
					if (arg.length() == 0) {
						System.out.println("config file must be passed as argument.");
						break;
					}
					try {
						config = new Configuration(arg);
					} catch (Exception e) {}
					if (config != null) {
						config.runChecks(System.out, System.in);
					}
					break;
				case 2:  //  startlistening
					if (algodApi == null || config == null) {
						System.out.println("Run 'runchecks' first to load the configuration");
						break;
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
				case 4:
					System.out.println(System.getProperty("user.dir"));
					break;
				default:
					System.out.println("Unknown input: " + line);
				}
				System.out.print(">> ");				
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to read input from command line.");
		}
	}
}
