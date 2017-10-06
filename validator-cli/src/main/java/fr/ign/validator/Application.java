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
	
    private String getCommandNames(){
        String result = "(";
        for ( CommandInterface command : commands ){
            if ( result.length() != 1 ){
                result += "|";
            }
            result += command.getName();
        }
        result += ")";
        return result;
    }
    
	private void run(String args[]) {
		if ( args.length == 0 ){
			System.err.println("dump-validator "+getCommandNames()+" --help");
			System.exit(1);
		}
		String commandName = args[0];
		CommandInterface command = getCommandByName(commandName);
		if ( command == null ){
			System.err.println("command '"+commandName+"' not found");
			System.exit(1);
		}
		try {
			command.run( Arrays.copyOfRange(args, 1, args.length));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		Application application = new Application();
		application.run(args);
	}
	

}
