package fr.ign.validator.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

import fr.ign.validator.Version;

/**
 * 
 * Application composed of commands
 * 
 * Note that command are 
 * 
 * @author MBorne
 */
public class CommandApplication {

	private List<Command> commands = new ArrayList<>();
	
	public CommandApplication() {

	}

	/**
	 * Loads commands from src/main/resources/META-INF/services/fr.ign.validation.command.Command
	 */
	public void loadRegistredCommands(){
		ServiceLoader<Command> loader = ServiceLoader.load( Command.class );
		for (Command command : loader) {
			addCommand(command);
		}
	}
	
	public void addCommand(Command command){
		commands.add(command);
	}
	
	private Command getCommandByName(String name){
		for (Command command : commands) {
			if ( command.getName().equals(name) ){
				return command;
			}
		}
		return null;
	}
    
    private void displayHelp(){
    	System.out.println("Usage : COMMAND --help");
    	System.out.println("");
    	System.out.println("Validate, normalize and extract data according to models");
    	System.out.println("");
    	System.out.println("Version : "+Version.getVersion());
    	System.out.println("");
    	System.out.println("Commands:");
    	for (Command command : commands) {
    		System.out.println("\t"+command.getName());
		}
    }

	public int run(String args[]) {
		if ( args.length == 0 ){
			displayHelp();
			return 1;
		}
		String commandName = args[0];
		Command command = getCommandByName(commandName);
		if ( command == null || command.equals("--help") || command.equals("-h") ){
			System.err.println("command '"+commandName+"' not found");
			displayHelp();
			return 1;
		}
		try {
			return command.run( Arrays.copyOfRange(args, 1, args.length));
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}


}
