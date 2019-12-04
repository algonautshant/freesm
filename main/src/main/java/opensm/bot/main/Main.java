package opensm.bot.main;

import opensm.api.CmdLoop;
import opensm.bot.BotActions;
import opensm.bot.BotReactions;

public class Main {

	public static void main(String[] args) {
		CmdLoop cmd = new CmdLoop();
		BotActions botConfig = new BotActions("botconfig.xml");
		BotReactions bot = new BotReactions(botConfig);
		
		cmd.startLoop(botConfig, bot);

	}

}
