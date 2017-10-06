package fr.ign.validator.command;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * 
 * Common command implementation and helpers
 *  
 * @author MBorne
 *
 */
public abstract class AbstractCommand implements CommandInterface {
	
	/**
	 * Command option for network proxy management (required to access remote XSD schemas)
	 */
	private String proxy = "";

	/**
	 * Get common options
	 * @return
	 */
	protected Options getCommonOptions() {
		Options options = new Options();
		
		// help
		options.addOption("h", "help", false, "affichage du message d'aide");

		// proxy
		{
			Option option = new Option("p", "proxy", true, "Adresse du proxy (ex : proxy.ign.fr:3128)");
			option.setRequired(false);
			options.addOption(option);
		}

		return options;
	}

	protected CommandLine parseCommandLine(String[] args){
		Options options = getCommandLineOptions();
		CommandLineParser parser = new GnuParser();
		try {
			CommandLine commandLine = parser.parse(options, args);
			parseProxyOption(commandLine);
			return commandLine;
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(getName(), options);
			return null;
		}
	}
	
	protected void displayHelp(){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(getName(), getCommonOptions());
	}

	/**
	 * Parse proxy option and define proxy
	 * @param commandLine
	 * @throws ParseException
	 */
	protected void parseProxyOption(CommandLine commandLine) throws ParseException{
		proxy = commandLine.getOptionValue("proxy", "");
		if (!proxy.isEmpty()) {
			String[] proxyParts = proxy.split(":");
			if (proxyParts.length != 2) {
				throw new ParseException("Invalid 'proxy' parameter (<proxy-host>:<proxy-port>)");
			}
			Properties systemSettings = System.getProperties();
			systemSettings.put("proxySet", "true");
			systemSettings.put("http.proxyHost", proxyParts[0]);
			systemSettings.put("http.proxyPort", proxyParts[1]);
		}
	}

}
