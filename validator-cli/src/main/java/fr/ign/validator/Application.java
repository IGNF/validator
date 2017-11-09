package fr.ign.validator;

import fr.ign.validator.command.CommandApplication;

/**
 * Command line application
 * @author MBorne
 */
public class Application {

	public static void main(String[] args) {
		CommandApplication application = new CommandApplication();
		application.loadRegistredCommands();
		System.exit(application.run(args));
	}
	
	
}
