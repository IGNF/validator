package fr.ign.validator.command;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

import fr.ign.validator.cnig.utils.IdgestExtractor;


/**
 * 
 * Extraction de IDGEST pour les SUP (pré-validateur CNIG)
 * 
 * @author fcerizay
 *
 */
public class CnigExtractIdgestCommand extends AbstractCommand {
	
	public static final String NAME = "cnig_extract_idgest";
	
	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * Get command line options
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
	
	
	@Override
	public int run(String[] args){
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
			return 1;
		}

		String proxyString = commandline.getOptionValue("proxy", "");
		configureProxy(proxyString);
		
		/*
		 * extraction idgest à partir du fichier en paramètre
		 */
		File servitudeFile = new File(commandline.getOptionValue("input"));
		IdgestExtractor idgestExtractor = new IdgestExtractor();
		String idGest = idgestExtractor.findIdGest(servitudeFile);
		if ( idGest == null || idGest.isEmpty() ){
			System.err.println("fail to read IdGest from "+servitudeFile);
			return 1;
		}
		
		/*
		 * écriture du résultat
		 */
		File resultFile = new File(servitudeFile.getParent(), "idGest.txt");
		try {
			FileUtils.writeStringToFile(resultFile, idGest);
		} catch (IOException e) {
			System.err.println("fail to write "+idGest+" to "+resultFile);
			return 1;
		}
		
		return 0;
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