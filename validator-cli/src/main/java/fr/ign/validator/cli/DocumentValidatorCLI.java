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

import com.vividsolutions.jts.io.WKTReader;

import fr.ign.validator.Context;
import fr.ign.validator.Validator;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.loader.ModelLoader;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.plugin.Plugin;
import fr.ign.validator.plugin.PluginManager;
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

	public static final String VALIDATION_DIRECTORY_NAME = "validation" ;

	/**
	 * Configuration des options de la ligne de commande
	 * @return
	 */
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
		// étendue dans laquelle la géométrie est attendue
		{
			Option option = new Option("de", "data-extent", false, "Domaine dans lequel la géométrie des données est attendue (format : WKT, projection : WGS84)");
			option.setRequired(false);
			options.addOption(option);
		}
		// limitation erreurs
		{
			Option option = new Option("e", "maxerror", true, "Limitation du nombre d'erreur du même type");
			option.setRequired(false);
			options.addOption(option);
		}
		// plugins
		{
			Option option = new Option("pgs", "plugins", true, "Liste des plugins à charger (noms séparés par des virgules)");
			option.setRequired(false);
			options.addOption(option);
		}

		return options;
	}

	/**
	 * Lecture des options et exécution du validateur
	 * @param args
	 */
	public static void main(String[] args) {
		// -Dlog4j.configurationFile=${project_loc}/log4j2.xml
		String log4jConfigurationFile = System.getProperty("log4j.configurationFile");
		if (log4jConfigurationFile == null) {
			System.err.println("log4j.configurationFile n'est pas défini (-Dlog4j.configurationFile=${project_loc}/log4j2.xml)");
		} else {
			log.info(MARKER, "log4j.configurationFile={}", log4jConfigurationFile);
		}

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
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("validator", options);
			System.exit(1);
		}

		// gestion de l'affichage de l'aide...
		if (commandline.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("validator", options);
			System.exit(0);
		}
		

		// configuration du proxy...
		String proxyString = commandline.getOptionValue("proxy", "");
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
		 * Chargement du document utilisé pour la validation
		 */
		File configDir = new File(commandline.getOptionValue("config"));
		File configPath = new File(configDir, commandline.getOptionValue("version") + "/files.xml");		
		if (!configPath.exists()) {
			String message = String.format("Le fichier de configuration '%1s' n'existe pas", configPath);
			log.error(MARKER, message);
			System.exit(1);
		}
		DocumentModel documentModel = null;
		try {
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
		 * récupération du chemin vers le document à valider...
		 */
		File documentPath = new File(commandline.getOptionValue("input"));
		if (!documentPath.exists()) {
			System.out.println(documentPath + " does not exists");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("validator", options);
			System.exit(1);
		}
		if (!documentPath.isDirectory()) {
			System.out.println(documentPath + " is not a directory");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("validator", options);
			System.exit(1);
		}
		
		/*
		 * Préparation du contexte pour la validation
		 */
		Context context = new Context();
		
		// configuration de la projection des données...
		if ( commandline.hasOption("srs") ) {
			String srsString = commandline.getOptionValue("srs");
			try {
				context.setCoordinateReferenceSystem(CRS.decode(srsString));
			} catch (NoSuchAuthorityCodeException e1) {
				System.err.println("bad srs parameter (EPSG:<epsg-code>) - NoSuchAuthorityCodeException");
				e1.printStackTrace(System.err);
				System.exit(1);
			} catch (FactoryException e1) {
				System.err.println("bad srs parameter (EPSG:<epsg-code>) - FactoryException");
				e1.printStackTrace(System.err);
				System.exit(1);
			}
		}
		
		// configuration de l'encodage des données...
		if (commandline.hasOption("encoding")) {
			String encoding = commandline.getOptionValue("encoding");
			context.setEncoding(Charset.forName(encoding));
		}else{
			context.setEncoding(StandardCharsets.UTF_8);
		}
		
		// configuration du répertoire de validation...
		File validationDirectory = new File( documentPath.getParentFile(), VALIDATION_DIRECTORY_NAME ) ;
		context.setValidationDirectory( validationDirectory ) ;

		// configuration de l'écriture du rapport
		context.setReportBuilder(new ReportBuilderLegacy());

		// configuration de l'emprise limite des données		
		if ( commandline.hasOption("data-extent") ){
			String wktExtent = commandline.getOptionValue("data-extent");
			WKTReader reader = new WKTReader();
			try {
				context.setNativeDataExtent(reader.read(wktExtent));
			} catch (com.vividsolutions.jts.io.ParseException e) {
				String message = String.format("Problème dans le décodage de 'data-extent' : '%1s' (WKT invalide)", wktExtent);
				log.error(MARKER, message);
				e.printStackTrace();
				System.exit(1);
			}
		}

		// configuration de la limite du nombre d'erreur
		if (commandline.hasOption("maxerror")) {
			int maxError = Integer.parseInt(commandline.getOptionValue("maxerror"));
			context.setReportBuilder( new FilteredReportBuilder(context.getReportBuilder(),maxError) );
		}
		
		// configuration de la validation à plat
		context.setFlatValidation(commandline.hasOption("f"));

		if ( commandline.hasOption("plugins") ){
			PluginManager pluginManager = new PluginManager();
			String[] pluginNames = commandline.getOptionValue("plugins").split(",");
			for (String pluginName : pluginNames) {
				Plugin plugin = pluginManager.getPluginByName(pluginName);
				if ( plugin == null ){
					String message = String.format("fail to load plugin '%1s'", pluginName);
					log.error(MARKER, message);
					System.exit(1);
				}
				log.info(MARKER, "setup plugin '%1s'...", pluginName);
				plugin.setup(context);
			}
		}
		
		
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
