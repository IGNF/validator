package fr.ign.validator.command;

/**
 * 
 * Interface for CLI interpreter commands
 * 
 * @author MBorne
 *
 */
public interface CommandInterface {

	/**
	 * Get command identifier (ex : document_validator)
	 * @return
	 */
	public String getName();

	/**
	 * Run the command with args (except command name) and returns exit code
	 * @param args
	 */
	public int run(String[] args);
	
}
