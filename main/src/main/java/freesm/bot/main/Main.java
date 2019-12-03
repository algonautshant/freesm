package freesm.bot.main;

import freesm.api.CmdLoop;
import freesm.bot.BotActions;
import freesm.bot.BotReactions;

public class Main {

	public static void main(String[] args) {
		CmdLoop cmd = new CmdLoop();
		BotActions botConfig = new BotActions("botconfig.xml");
		BotReactions bot = new BotReactions(botConfig);
		
		cmd.startLoop(botConfig, bot);

	}

}
