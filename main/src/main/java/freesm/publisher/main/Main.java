package freesm.publisher.main;

import freesm.api.CmdLoop;
import freesm.bot.BotConfiguration;
import freesm.bot.FreeSmBot;
import freesm.publisher.Publisher;
import freesm.publisher.PublisherConfiguration;
import freesm.utils.messaging.ReportException;

public class Main {

	public static void main(String[] args) {
		if (args.length != 1) {
			ReportException.errorMessageDefaultAction("Required xml configuration file path as parameter.");
			return;
		}
		CmdLoop cmd = new CmdLoop();
		PublisherConfiguration publisherConfig = new PublisherConfiguration(args[0]);
		Publisher publisher = new Publisher(publisherConfig);
		
		cmd.startLoop(publisherConfig, publisher);

	}

}
