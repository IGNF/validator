package fr.ign.validator.command;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.vividsolutions.jts.io.WKTReader;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.loader.ModelLoader;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.plugin.Plugin;
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.report.FilteredReportBuilder;
import fr.ign.validator.report.ReportBuilderLegacy;

/**
 * 
 * Validate a document directory according to a DocumentModel
 * 
 * TODO :
 * 
 * <ul>
 * 	<li>Custom parameters as member params + final run(args) && abstract execute()</li>
 *  <li>Use option.type to specify kind of arguments & commandLine.getParsedOptionValue("input")
 *  <li>Generate a basic UI according to option.type?</li>
 * </ul>
 * 
 * 
 * @author MBorne
 *
 */
public class DocumentValidatorCommand extends AbstractCommand {

	public static final String NAME = "document_validator";

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("DocumentValidatorCLI");

	public static final String VALIDATION_DIRECTORY_NAME = "validation" ;	
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int run(String[] args) {
		CommandLine commandLine = parseCommandLine(args);
		if ( commandLine == null ){
			return 1;
		}
		
		// gestion de l'affichage de l'aide...
		if (commandLine.hasOption("help")) {
			displayHelp();
			return 1;
		}

		/*
		 * Chargement du document utilisé pour la validation
		 */
		File configDir = new File(commandLine.getOptionValue("config"));
		File configPath = new File(configDir, commandLine.getOptionValue("version") + "/files.xml");		
		if (!configPath.exists()) {
			String message = String.format("Le fichier de configuration '%1s' n'existe pas", configPath);
			log.error(MARKER, message);
			return 1;
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
			return 1;
		}

		/*
		 * récupération du chemin vers le document à valider...
		 */
		File documentPath = new File(commandLine.getOptionValue("input"));
		if (!documentPath.exists()) {
			System.out.println("invalid parameter 'input' : "+documentPath + " does not exists");
			return 1;
		}
		if (!documentPath.isDirectory()) {
			System.out.println("invalid parameter 'input' : "+documentPath + " is not a directory");
			return 1;
		}
		
		/*
		 * Préparation du contexte pour la validation
		 */
		Context context = new Context();
		
		// configuration de la projection des données...
		if ( commandLine.hasOption("srs") ) {
			String srsString = commandLine.getOptionValue("srs");
			try {
				context.setCoordinateReferenceSystem(CRS.decode(srsString));
			} catch (NoSuchAuthorityCodeException e1) {
				System.err.println("bad srs parameter (EPSG:<epsg-code>) - NoSuchAuthorityCodeException");
				e1.printStackTrace(System.err);
				return 1;
			} catch (FactoryException e1) {
				System.err.println("bad srs parameter (EPSG:<epsg-code>) - FactoryException");
				e1.printStackTrace(System.err);
				return 1;
			}
		}
		
		// configuration de l'encodage des données...
		if (commandLine.hasOption("encoding")) {
			String encoding = commandLine.getOptionValue("encoding");
			context.setEncoding(Charset.forName(encoding));
		}else{
			context.setEncoding(StandardCharsets.UTF_8);
		}
		
		// configuration du répertoire de validation...
		File validationDirectory = new File( documentPath.getParentFile(), VALIDATION_DIRECTORY_NAME ) ;
		if ( validationDirectory.exists() ) {
			log.info(MARKER,
				"Suppression du répertoire existant {}", 
				validationDirectory.getAbsolutePath() 
			);
			try {
				FileUtils.deleteDirectory( validationDirectory );
			} catch (Exception e) {
				String message = String.format("Problème dans la suppression du répertoire de validation: '%1s'", validationDirectory);
				log.error(MARKER, message);
				e.printStackTrace();
				return 1;
			}
		}
		log.info(MARKER,
			"Création du répertoire de validation {}", 
			validationDirectory.getAbsolutePath() 
		);
		validationDirectory.mkdirs() ;
		context.setValidationDirectory( validationDirectory ) ;

		// configuration de l'écriture du rapport
		File validationRapport = new File( validationDirectory, "validation.xml" );
		context.setReportBuilder(new ReportBuilderLegacy(validationRapport));

		// configuration de l'emprise limite des données		
		if ( commandLine.hasOption("data-extent") ){
			String wktExtent = commandLine.getOptionValue("data-extent");
			WKTReader reader = new WKTReader();
			try {
				context.setNativeDataExtent(reader.read(wktExtent));
			} catch (com.vividsolutions.jts.io.ParseException e) {
				String message = String.format("Problème dans le décodage de 'data-extent' : '%1s' (WKT invalide)", wktExtent);
				log.error(MARKER, message);
				e.printStackTrace();
				return 1;
			}
		}

		// configuration de la limite du nombre d'erreur
		if (commandLine.hasOption("max-errors")) {
			int maxError = Integer.parseInt(commandLine.getOptionValue("max-errors"));
			context.setReportBuilder( new FilteredReportBuilder(context.getReportBuilder(),maxError) );
		}

		// configuration de la validation à plat
		context.setFlatValidation(commandLine.hasOption("f"));

		if ( commandLine.hasOption("plugins") ){
			PluginManager pluginManager = new PluginManager();
			String[] pluginNames = commandLine.getOptionValue("plugins").split(",");
			for (String pluginName : pluginNames) {
				Plugin plugin = pluginManager.getPluginByName(pluginName);
				if ( plugin == null ){
					String message = String.format("fail to load plugin '%1s'", pluginName);
					log.error(MARKER, message);
					System.exit(1);
				}
				log.info(MARKER, String.format("setup plugin '%1s'...", pluginName));
				plugin.setup(context);
			}
		}
		
		Document document = new Document(documentModel,documentPath);
		try {
			document.validate(context);
		} catch (Exception e) {
			log.fatal(MARKER, e.getMessage());
			context.report(ErrorCode.VALIDATOR_EXCEPTION);
			e.printStackTrace();
			return 1;
		}
		return 0;
	}


	@Override
	public Options getCommandLineOptions() {
		Options options = getCommonOptions();
		
		// input
		{
			Option option = new Option("i", "input", true, "Dossier contenant les données à valider");
			option.setRequired(true);
			options.addOption(option);
		}
		
		// config
		{
			Option option = new Option("c", "config", true, "Dossier contenant les configurations");
			option.setRequired(true);
			option.setType(File.class);
			options.addOption(option);
		}

		// version
		{
			Option option = new Option("v", "version", true, "Norme et version a prendre en compte");
			option.setRequired(true);
			options.addOption(option);
		}


		// input EPSG code
		{
			Option option = new Option("s", "srs", true, "Système de référence des données sources (ex : EPSG:2154)");
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
			Option option = new Option(null, "flat", false, "Validation à plat (pas de validation de l'arborescence)");
			option.setRequired(false);
			options.addOption(option);
		}
		// étendue dans laquelle la géométrie est attendue
		{
			Option option = new Option(null, "data-extent", true, "Domaine dans lequel la géométrie des données est attendue (format : WKT, projection : WGS84)");
			option.setRequired(false);
			options.addOption(option);
		}
		// limitation erreurs
		{
			Option option = new Option(null, "max-errors", true, "Limitation du nombre d'erreur du même type dans le rapport de validation");
			option.setRequired(false);
			options.addOption(option);
		}
		// plugins
		{
			Option option = new Option(null, "plugins", true, "Liste des plugins à charger (noms séparés par des virgules)");
			option.setRequired(false);
			options.addOption(option);
		}

		return options;
	}

}
