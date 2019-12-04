package opensm.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import opensm.utils.events.NodeEventListener;
import opensm.utils.messaging.ReportMessage;

public class CmdLoop {

	private static String header = "openSM Bot\n"
			+ "Commands: \n"
			+ "	init              // loads configuration, runs checks and initializes fsmbot\n"
			+ "	startlistening    // start listening to transaciton and processes requests\n"
			+ "	stoplistening     // stop listening to transactions\n"
			+ "	register          // pay bot and receive assets\n"
			+ "	publish <article> // publish the  content   in <article>\n"
			+ "	pwd               // get current working directory\n"
			+ "	help              // print this message";
	
	private HashMap<String, Integer>  commandMap;

	public CmdLoop() {
		commandMap = new HashMap<String, Integer>();
		commandMap.put("init", 1);
		commandMap.put("startlistening",  2);
		commandMap.put("stoplistening",  3);
		commandMap.put("pwd",  4);
		commandMap.put("help", 5);
		commandMap.put("register", 6);
		commandMap.put("publish", 7);
	}
	
	public void startLoop(Actions action, NodeEventListener nodeEventListener) {
		TransactionListener tl = null;
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
				String arg = st.hasMoreTokens() ? line.substring(keyword.length()).trim() : "";
				int commandId = 9999;
				try {
					commandId = commandMap.get(keyword);
				} catch (NullPointerException e) {}
				switch(commandId) {
				case 1: // init
					action.init(System.out, System.in);

					break;
				case 2:  //  startlistening
					if (action == null || action.getAlgodClientApi() == null) {
						System.out.println("Run 'init' first to load the configuration");
						break;
					} else {
						tl = new TransactionListener(action.getAlgodClientApi(), System.out);
						tl.start();
						tl.registerListener(nodeEventListener);
//						tl.registerListener(new BaseNodeEventListener());
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
				case 5:
					System.out.println(header);
					break;
				case 6:
					action.register();
					break;
				case  7:
					action.publishArticle(arg);
					break;
				default:
					System.out.println("Unknown input: " + line);
				}
				System.out.print(">> ");				
			}
			System.out.println("\nBye.");
		} catch (IOException e) {
			ReportMessage.errorMessageDefaultAction("Failed to read input from command line.", e);
		}
	}
}
