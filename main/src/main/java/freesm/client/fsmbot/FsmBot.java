package freesm.client.fsmbot;

public class FsmBot {
	
	private static String botAddress;
	
	
	
	public static void initFsmBot(String botAddress) {
		FsmBot.botAddress = botAddress;
	}
	
	public static String getBotAddress() {
		if (botAddress.isEmpty()) {
			throw new RuntimeException("BsmBot address is not initialized.");
		}
		return botAddress;
	}
}
