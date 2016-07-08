package fr.ign.validator.cli;

import java.io.File;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import fr.ign.validator.tools.IdgestExtractor;


/**
 * 
 * 
 * @author fcerizay
 *
 */
public class PreValidatorCnig {
	
	/**
	 * 
	 * @return
	 */
	public static Options getCommandLineOptions() {
		
		Options options = new Options();

		// input
		{
			Option option = new Option("i", "input", true, "Fichier à convertir");
			option.setRequired(true);
			options.addOption(option);
		}
		// proxy
		{
			Option option = new Option("p", "proxy", true,"Adresse du proxy (ex : proxy.ign.fr:3128)");
			option.setRequired(false);
			options.addOption(option);
		}
		return options;
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		/*
		 * Récupération des options de la ligne de commande
		 */
		Options options = getCommandLineOptions();

		CommandLineParser parser = new GnuParser();
		CommandLine commandline = null;
		
		try {
			commandline = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

		String proxyString = commandline.getOptionValue("proxy", "");
		configureProxy(proxyString);
		
		File servitudeFile = new File(commandline.getOptionValue("input"));
		IdgestExtractor idgestExtractor = new IdgestExtractor(servitudeFile);
		idgestExtractor.findIdGest();
	}
	
	/**
	 * 
	 * @param proxyString
	 */
	private static void configureProxy( String proxyString ){
		
		if (!proxyString.isEmpty()) {
			String[] proxyParts = proxyString.split(":");
			if (proxyParts.length != 2) {
				System.err.println("bad proxy parameter (<proxy-host>:<proxy-port>)");
				System.exit(1);
			}
			Properties systemSettings = System.getProperties();
			systemSettings.put("proxySet", "true");
			systemSettings.put("http.proxyHost", proxyParts[0]);
			systemSettings.put("http.proxyPort", proxyParts[1]);
		}
	}
}