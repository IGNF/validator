package fr.ign.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.ign.validator.command.CnigExtractIdgestCommand;
import fr.ign.validator.command.CommandInterface;
import fr.ign.validator.command.DocumentValidatorCommand;

/**
 * 
 * CLI application to invoke commands
 * 
 * @author MBorne
 */
public class Application {

	private List<CommandInterface> commands = new ArrayList<>();
	
	public Application() {
		addCommand(new DocumentValidatorCommand());
		addCommand(new CnigExtractIdgestCommand());
	}

	private void addCommand(CommandInterface command){
		commands.add(command);
	}
	
	private CommandInterface getCommandByName(String name){
		for (CommandInterface command : commands) {
			if ( command.getName().equals(name) ){
				return command;
			}
		}
		return null;
	}
    
    private void displayHelp(){
    	System.out.println("Usage : java -jar validator-cli.jar COMMAND --help");
    	System.out.println("");
    	System.out.println("Validate, normalize and extract data according to models");
    	System.out.println("");
    	System.out.println("Version : "+Version.getVersion());
    	System.out.println("");
    	System.out.println("Commands:");
    	for (CommandInterface command : commands) {
    		System.out.println("\t"+command.getName());
		}
    }

	private int run(String args[]) {
		if ( args.length == 0 ){
			displayHelp();
			return 1;
		}
		String commandName = args[0];
		CommandInterface command = getCommandByName(commandName);
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

	public static void main(String[] args) {
		Application application = new Application();
		System.exit(application.run(args));
	}
	

}
