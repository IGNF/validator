package fr.ign.validator.cli;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.ign.validator.Context;
import fr.ign.validator.Validator;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.loader.ModelLoader;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.report.FilteredReportBuilder;
import fr.ign.validator.report.ReportBuilderLegacy;

/**
 * Validateur de document en ligne de commande
 * 
 * @author CBouche
 * @author MBorne
 */
public class DocumentValidatorCLI {
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("DocumentValidatorCLI");

	public static Options getCommandLineOptions() {
		Options options = new Options();
		options.addOption("h", "help", false, "affichage du message d'aide");

		// input
		{
			Option option = new Option("i", "input", true, "Dossier contenant les données à valider");
			option.setRequired(true);
			options.addOption(option);
		}

		// input EPSG code
		{
			Option option = new Option("s", "srs", true, "Système de référence des données sources (ex : EPSG:2154)");
			option.setRequired(true);
			options.addOption(option);
		}

		// config
		{
			Option option = new Option("c", "config", true, "Dossier contenant les configurations");
			option.setRequired(true);
			options.addOption(option);
		}

		// version
		{
			Option option = new Option("v", "version", true, "Norme et version a prendre en compte");
			option.setRequired(true);
			options.addOption(option);
		}

		// proxy
		{
			Option option = new Option("p", "proxy", true, "Adresse du proxy (ex : proxy.ign.fr:3128)");
			option.setRequired(false);
			options.addOption(option);
		}
		// encoding
		{
			Option option = new Option("W", "encoding", true, "Encodage des données (defaut : UTF-8)");
			option.setRequired(false);
			options.addOption(option);
		}
		// mode souple
		{
			Option option = new Option("f", "flat", false, "Validation à plat (pas de validation de l'arborescence)");
			option.setRequired(false);
			options.addOption(option);
		}
		// limitation erreurs
		{
			Option option = new Option("e", "maxerror", true, "Limitation du nombre d'erreur du même type");
			option.setRequired(false);
			options.addOption(option);
		}
		return options;
	}

	public static void main(String[] args) {
		// -Dlog4j.configurationFile=${project_loc}/log4j2.xml
		String log4jConfigurationFile = System.getProperty("log4j.configurationFile");
		if (log4jConfigurationFile == null) {
			System.err.println(
					"log4j.configurationFile n'est pas défini (-Dlog4j.configurationFile=${project_loc}/log4j2.xml)");
		} else {
			log.info(MARKER, "log4j.configurationFile={}", log4jConfigurationFile);
		}

		/*
		 * Récupération des options de la ligne de commande
		 */
		Options options = getCommandLineOptions();

		// TODO option input and config

		CommandLineParser parser = new GnuParser();
		CommandLine commandline = null;
		try {
			commandline = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("validator", options);
			System.exit(1);
		}

		if (commandline.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("validator", options);
			System.exit(0);
		}

		File documentPath = new File(commandline.getOptionValue("input"));
		File configDir = new File(commandline.getOptionValue("config"));
		File configPath = new File(configDir, commandline.getOptionValue("version") + "/files.xml");
		String proxyString = commandline.getOptionValue("proxy", "");
		String srsString = commandline.getOptionValue("srs");

		// charset
		Charset charset = StandardCharsets.UTF_8;
		if (commandline.hasOption("encoding")) {
			String encoding = commandline.getOptionValue("encoding");
			charset = Charset.forName(encoding);
		}

		/*
		 * check directory exists
		 */
		if (!documentPath.exists()) {
			System.out.println(documentPath + " does not exists");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("validator", options);
			System.exit(1);
		}

		/*
		 * Spatial reference system check
		 */

		CoordinateReferenceSystem coordinateReferenceSystem = null;
		try {
			coordinateReferenceSystem = CRS.decode(srsString);
		} catch (NoSuchAuthorityCodeException e1) {
			System.err.println("bad srs parameter (EPSG:<epsg-code>) - NoSuchAuthorityCodeException");
			e1.printStackTrace(System.err);
			System.exit(0);
		} catch (FactoryException e1) {
			System.err.println("bad srs parameter (EPSG:<epsg-code>) - FactoryException");
			e1.printStackTrace(System.err);
			System.exit(0);
		}

		/*
		 * configuration du proxy
		 */
		if (!proxyString.isEmpty()) {
			String[] proxyParts = proxyString.split(":");
			if (proxyParts.length != 2) {
				System.err.println("bad proxy parameter (<proxy-host>:<proxy-port>)");
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("validator", options);
				System.exit(1);
			}
			Properties systemSettings = System.getProperties();
			systemSettings.put("proxySet", "true");
			systemSettings.put("http.proxyHost", proxyParts[0]);
			systemSettings.put("http.proxyPort", proxyParts[1]);
		}

		/*
		 * configPath chemin vers le modèle
		 */
		if (!configPath.exists()) {
			String message = String.format("Le fichier de configuration '%1s' n'existe pas", configPath);
			log.error(MARKER, message);
			System.exit(1);
		}

		DocumentModel documentModel = null;
		try {
			/*
			 * Chargement de la configuration
			 */
			ModelLoader modelLoader = new ModelLoader();
			String message = String.format("Chargement du modèle '%1s'...", configPath);
			log.debug(MARKER, message);
			documentModel = modelLoader.loadDocumentModel(configPath);
		} catch (JAXBException e) {
			String message = String.format("Problème lors du chargement de '%1s' (XML invalide)", configPath);
			log.error(MARKER, message);
			e.printStackTrace();
			System.exit(1);
		}

		/*
		 * Validation du document
		 */
		Context context = new Context();
		context.setEncoding(charset);
		context.setReportBuilder(new ReportBuilderLegacy());
		
		// si maxerrors définis, 
		if (commandline.hasOption("maxerror")) {
			int maxError = Integer.parseInt(commandline.getOptionValue("maxerror"));
			context.setReportBuilder( new FilteredReportBuilder(context.getReportBuilder(),maxError) );
		}
		
		
		context.setCoordinateReferenceSystem(coordinateReferenceSystem);
		context.setFlatValidation(commandline.hasOption("f"));

		Validator validator = new Validator(context);
		try {
			validator.validate(documentModel, documentPath);
		} catch (Exception e) {
			log.fatal(MARKER, e.getMessage());
			validator.getContext().report(ErrorCode.VALIDATOR_EXCEPTION);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
