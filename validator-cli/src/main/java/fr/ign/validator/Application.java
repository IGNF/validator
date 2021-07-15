package fr.ign.validator;

import org.apache.logging.log4j.LogManager;

import fr.ign.validator.command.CommandApplication;

/**
 * Command line application
 * 
 * @author MBorne
 */
public class Application {

    public static void main(String[] args) {
        CommandApplication application = new CommandApplication();
        int result = application.run(args);
        LogManager.shutdown();
        System.exit(result);
    }

}
