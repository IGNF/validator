package fr.ign.validator.command;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
import fr.ign.validator.string.StringFixer;
import fr.ign.validator.string.transform.DoubleUtf8Decoder;
import fr.ign.validator.string.transform.EscapeForCharset;
import fr.ign.validator.string.transform.IsoControlEscaper;
import fr.ign.validator.string.transform.StringSimplifier;

/**
 * 
 * Validate a document directory according to a DocumentModel
 * 
 * TODO :
 * 
 * <ul>
 * 	<li>Member variable for each command line option</li>
 *  <li>Use option.type to specify kind of arguments & commandLine.getParsedOptionValue("input")
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

	/**
	 * Validation context
	 */
	private Context context;
	
	/**
	 * DocumentModel
	 */
	private DocumentModel documentModel;
	
	/**
	 * Path to the document
	 */
	private File documentPath;
	
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	protected void buildCustomOptions(Options options) {
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
		// étendue dans laquelle la géométrie est attendue
		{
			Option option = new Option(null, "data-extent", true, "Domaine dans lequel la géométrie des données est attendue (format : WKT, projection : WGS84)");
			option.setRequired(false);
			options.addOption(option);
		}		

		// proxy
		{
			Option option = new Option("p", "proxy", true, "Adresse du proxy principalement utilisé pour accéder aux schémas XSD (ex : proxy.ign.fr:3128)");
			option.setRequired(false);
			options.addOption(option);
		}
		// encoding
		{
			Option option = new Option(
				"W", "encoding", true, 
				"Encodage des données (UTF-8 par défaut), ignoré en présence d'une fiche de métadonnées définissant l'encodage"
			);
			option.setRequired(false);
			options.addOption(option);
		}
		
		
		// mode souple
		{
			Option option = new Option(null, "flat", false, "Validation à plat (pas de validation de l'arborescence)");
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
	}
	
	
	
	@Override
	protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
		// prepare validation context...
		this.context = new Context();
		
		// input...
		this.documentPath = new File(commandLine.getOptionValue("input"));
		if (!documentPath.exists()) {
			String message = String.format("Paramètre invalide 'input' : '%1s' (fichier non existant)", documentPath);
			log.error(MARKER, message);
			throw new ParseException(message);
		}
		if (!documentPath.isDirectory()) {
			String message = String.format("Paramètre invalide 'input' : '%1s' (le fichier n'est pas un répertoire)", documentPath);
			log.error(MARKER, message);
			throw new ParseException(message);
		}

		// output...
		File validationDirectory = new File( documentPath.getParentFile(), VALIDATION_DIRECTORY_NAME ) ;
		if ( validationDirectory.exists() ) {
			log.info(MARKER,
				"Suppression du répertoire existant {}", 
				validationDirectory.getAbsolutePath() 
			);
			try {
				FileUtils.deleteDirectory( validationDirectory );
			} catch (Exception e) {
				String message = String.format("Echec dans la suppression du dossier : '%1s'", validationDirectory);
				log.error(MARKER, message);
				throw new ParseException(message);
			}
		}
		log.info(MARKER,
			"Création du répertoire de validation {}", 
			validationDirectory.getAbsolutePath() 
		);
		validationDirectory.mkdirs() ;
		context.setValidationDirectory( validationDirectory ) ;

		// validation report...
		File validationRapport = new File( validationDirectory, "validation.xml" );
		context.setReportBuilder(new ReportBuilderLegacy(validationRapport));
		
		// max-errors in validation report...
		if (commandLine.hasOption("max-errors")) {
			int maxError = Integer.parseInt(commandLine.getOptionValue("max-errors"));
			context.setReportBuilder( new FilteredReportBuilder(context.getReportBuilder(),maxError) );
		}
		
		// config and version... 
		File configDir = new File(commandLine.getOptionValue("config"));
		File configPath = new File(configDir, commandLine.getOptionValue("version") + "/files.xml");
		if (!configPath.exists()) {
			String message = String.format("Le fichier de configuration '%1s' n'existe pas", configPath);
			log.error(MARKER, message);
			throw new ParseException(message);
		}
		try {
			ModelLoader modelLoader = new ModelLoader();
			String message = String.format("Chargement du modèle '%1s'...", configPath);
			log.debug(MARKER, message);
			this.documentModel = modelLoader.loadDocumentModel(configPath);
		} catch (JAXBException e) {
			String message = String.format("Problème lors du chargement de '%1s' (XML invalide)", configPath);
			log.error(MARKER, message);
			e.printStackTrace();
			throw new ParseException(message);
		}

		
		// srs...
		if ( commandLine.hasOption("srs") ) {
			String srsString = commandLine.getOptionValue("srs");
			try {
				context.setCoordinateReferenceSystem(CRS.decode(srsString));
			} catch (NoSuchAuthorityCodeException e1) {
				String message = String.format("Paramètre invalide 'srs' : '%1s' (NoSuchAuthorityCodeException)", srsString);
				log.error(MARKER, message);
				throw new ParseException(message);
			} catch (FactoryException e1) {
				String message = String.format("Paramètre invalide 'srs' : '%1s' (FactoryException)", srsString);
				log.error(MARKER, message);
				throw new ParseException(message);
			}
		}

		// encoding (charset)...
		if (commandLine.hasOption("encoding")) {
			String encoding = commandLine.getOptionValue("encoding");
			context.setEncoding(Charset.forName(encoding));
		}else{
			context.setEncoding(StandardCharsets.UTF_8);
		}
		
		/*
		 * deep character validation...
		 */
		{
			StringFixer stringFixer = new StringFixer();
			// TODO string-fix-utf8
			stringFixer.addTransform(new DoubleUtf8Decoder());

			// TODO string-simplify
			StringSimplifier simplifier = new StringSimplifier();
			simplifier.loadCommon();
			
			// TODO string-charset-compatility=ISO_8859_1
			simplifier.loadCharset(StandardCharsets.ISO_8859_1);
			stringFixer.addTransform(simplifier);

			// TODO string-fix-controls
			stringFixer.addTransform(new IsoControlEscaper(true));
			// TODO string-charset-compatility=ISO_8859_1
			stringFixer.addTransform(new EscapeForCharset(StandardCharsets.ISO_8859_1)); 
			context.setStringFixer(stringFixer);
		}


		// data-extent...
		if ( commandLine.hasOption("data-extent") ){
			String wktExtent = commandLine.getOptionValue("data-extent");
			WKTReader reader = new WKTReader();
			try {
				context.setNativeDataExtent(reader.read(wktExtent));
			} catch (com.vividsolutions.jts.io.ParseException e) {
				String message = String.format("Invalid parameter 'data-extent' : '%1s' (invalid WKT)", wktExtent);
				log.error(MARKER, message);
				throw new ParseException(message);
			}
		}

		// flat...
		context.setFlatValidation(commandLine.hasOption("flat"));

		// plugins...
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
	}
	
	
	@Override
	public int execute() {
		Document document = new Document(documentModel,documentPath);
		try {
			document.validate(context);
			return 0;
		} catch (Exception e) {
			log.fatal(MARKER, e.getMessage());
			context.report(ErrorCode.VALIDATOR_EXCEPTION);
			e.printStackTrace();
			return 1;
		}
	}

}