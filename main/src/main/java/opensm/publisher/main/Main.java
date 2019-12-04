package opensm.publisher.main;

import opensm.api.CmdLoop;
import opensm.bot.BotActions;
import opensm.bot.BotReactions;
import opensm.publisher.PublisherActions;
import opensm.publisher.PublisherReactions;
import opensm.utils.messaging.ReportMessage;

public class Main {

	public static void main(String[] args) {
		if (args.length != 1) {
			ReportMessage.errorMessageDefaultAction("Required xml configuration file path as parameter.");
			return;
		}
		CmdLoop cmd = new CmdLoop();
		PublisherActions publisherConfig = new PublisherActions(args[0]);
		PublisherReactions publisher = new PublisherReactions(publisherConfig);
		
		cmd.startLoop(publisherConfig, publisher);

	}

}
