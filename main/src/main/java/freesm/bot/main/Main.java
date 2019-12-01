package freesm.bot.main;

import freesm.api.CmdLoop;
import freesm.bot.BotConfiguration;
import freesm.bot.FreeSmBot;

public class Main {

	public static void main(String[] args) {
		CmdLoop cmd = new CmdLoop();
		BotConfiguration botConfig = new BotConfiguration("botconfig.xml");
		FreeSmBot bot = new FreeSmBot(botConfig);
		
		cmd.startLoop(botConfig, bot);

	}

}
