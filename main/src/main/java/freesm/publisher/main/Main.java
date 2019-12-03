package freesm.publisher.main;

import freesm.api.CmdLoop;
import freesm.bot.BotActions;
import freesm.bot.BotReactions;
import freesm.publisher.PublisherReactions;
import freesm.publisher.PublisherActions;
import freesm.utils.messaging.ReportMessage;

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
