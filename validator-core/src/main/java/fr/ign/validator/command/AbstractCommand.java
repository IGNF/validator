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
public abstract class AbstractCommand implements Command {
	
	/**
	 * Command option for network proxy management (required to access remote XSD schemas)
	 */
	private String proxy = "";

	/**
	 * Append custom CLI options to default ones
	 * @param options
	 */
	protected abstract void buildCustomOptions(Options options) ;
	
	/**
	 * Parse custom CLI options to member variable
	 * @param commandLine
	 * @throws ParseException
	 */
	protected abstract void parseCustomOptions(CommandLine commandLine) throws ParseException ;
	

	@Override
	public Options getCommandLineOptions() {
		Options options = this.getCommonOptions();
		buildCustomOptions(options);
		return options;
	}
	
	
	@Override
	public final int run(String[] args) {
		Options options = getCommandLineOptions();
		
		/*
		 * parse command line options and handle command line options error
		 */
		CommandLineParser parser = new GnuParser();
		try {
			CommandLine commandLine = parser.parse(options, args);
			if ( commandLine.hasOption("help") ){
				displayHelp(options);
				return 1;
			}
			parseProxyOption(commandLine);
			parseCustomOptions(commandLine);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			displayHelp(options);
			return 1;
		}
		
		/*
		 * run command and handle execution error
		 */
		try {
			this.execute();
			return 0;
		}catch (Exception e){
			e.printStackTrace();
			return 1;
		}
	}	
	

	
	/**
	 * Get common options
	 * @return
	 */
	private Options getCommonOptions() {
		Options options = new Options();
		
		// help
		options.addOption("h", "help", false, "display help message");

		// proxy
		{
			Option option = new Option("p", "proxy", true, "Network proxy (ex : proxy.ign.fr:3128)");
			option.setRequired(false);
			options.addOption(option);
		}
		
		return options;
	}

	/**
	 * Display help
	 * @param options
	 */
	private void displayHelp(Options options){
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(120);
		formatter.printHelp(getName(), options);
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
