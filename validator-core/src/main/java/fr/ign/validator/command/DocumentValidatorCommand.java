package fr.ign.validator.command;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
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
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

import fr.ign.validator.Context;
import fr.ign.validator.command.options.StringFixerOptions;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.plugin.Plugin;
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.report.FilteredReportBuilder;
import fr.ign.validator.report.JsonReportBuilder;
import fr.ign.validator.report.ReportBuilder;
import fr.ign.validator.report.ReportBuilderLegacy;
import fr.ign.validator.repository.DocumentModelRepository;
import fr.ign.validator.repository.xml.XmlDocumentModelRepository;
import fr.ign.validator.string.StringFixer;

/**
 * 
 * Validate a document directory according to a DocumentModel
 * 
 * @author MBorne
 *
 */
public class DocumentValidatorCommand extends AbstractCommand {

	public static final String NAME = "document_validator";

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("DocumentValidatorCommand");

	public static final String VALIDATION_DIRECTORY_NAME = "validation" ;	

	/**
	 * Path to directory containing standards
	 */
	protected File configDir ; 

	/**
	 * Standard name (ex : cnig_PLU_2013)
	 */
	protected String documentModelName ;

	/**
	 * Path to the document
	 */
	protected File documentPath;
	
	/**
	 * Path to the validation directory (default is validation in documentPath's parent)
	 */
	protected File validationDirectory ;

	/**
	 * Report builder
	 */
	protected ReportBuilder reportBuilder ;

	/**
	 * data - CRS
	 */
	protected CoordinateReferenceSystem coordinateReferenceSystem;
	
	/**
	 * data - Charset
	 */
	protected Charset encoding = StandardCharsets.UTF_8;

	/**
	 * option - deep character validation options
	 */
	protected StringFixer stringFixer = new StringFixer();
	
	/**
	 * option - native data extent
	 */
	protected Geometry nativeDataExtent ;
	
	/**
	 * option - flat validation mode
	 */
	protected boolean flat = false;
	
	/**
	 * option - plugins used for validation
	 */
	protected List<Plugin> plugins = new ArrayList<>();
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	protected void buildCustomOptions(Options options) {
		/*
		 * input directory & model
		 */
		buildDocumentPathOption(options);
		buildConfigOption(options);
		buildVersionOption(options);

		/*
		 * output options
		 */
		buildValidationDirectoryOption(options);
		buildReportBuilderOptions(options);
		
		/*
		 * validation options
		 */
		buildCoordinateReferenceSystemOptions(options);
		buildDataExtentOption(options);
		buildEncodingOption(options);
		buildStringFixerOptions(options);
		buildFlatOption(options);
		buildPluginsOption(options);
	}

	
	
	@Override
	protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
		/*
		 * input
		 */
		parseDocumentPath(commandLine);

		/*
		 * output
		 */
		parseValidationDirectory(commandLine);
		parseReportBuilder(commandLine);
		
		/*
		 * config and version 
		 */
		parseConfig(commandLine);
		parseVersion(commandLine);

		/*
		 * Validation options
		 */
		parseCoordinateReferenceSystem(commandLine);
		parseEncoding(commandLine);
		parseStringFixerOptions(commandLine);
		parseDataExtent(commandLine);
		parseFlatOption(commandLine);

		// plugins...
		parsePluginsOption(commandLine);
	}


	@Override
	public void execute() throws Exception {
		/*
		 * prepare Context
		 */
		Context context = new Context();
		
		/*
		 * Input directory (should be removed)
		 */
		context.setCurrentDirectory(documentPath);
		
		/*
		 * configure output directory
		 */
		context.setValidationDirectory( validationDirectory ) ;
		context.setReportBuilder(reportBuilder);

		/*
		 * forward validation options
		 */
		context.setCoordinateReferenceSystem(coordinateReferenceSystem);		
		context.setEncoding(encoding);
		context.setStringFixer(stringFixer);
		context.setNativeDataExtent(nativeDataExtent);
		context.setFlatValidation(flat);

		/*
		 * load plugins
		 */
		for (Plugin plugin : plugins) {
			plugin.setup(context);
		}
		
		/*
		 * load model
		 */
		DocumentModelRepository repository = new XmlDocumentModelRepository(configDir);
		DocumentModel documentModel = repository.findOneByName(documentModelName);
		
		/*
		 * Validate document
		 */
		try {
			context.report(CoreErrorCodes.VALIDATOR_INFO, "Validation avec le modèle : "+documentModel.getName());
			Document document = new Document(documentModel,documentPath);
			document.validate(context);
		} catch (Exception e) {
			// trace validation exception
			log.fatal(MARKER, e.getMessage());
			context.report(CoreErrorCodes.VALIDATOR_EXCEPTION);
			throw e;
		}
	}

	/**
	 * Add "input" option
	 * @param options
	 */
	protected void buildDocumentPathOption(Options options) {
		{
			Option option = new Option("i", "input", true, "Input data directory");
			option.setRequired(true);
			options.addOption(option);
		}
	}

	/**
	 * Parse documentPath from commandLine
	 * @param commandLine
	 * @throws ParseException
	 */
	protected void parseDocumentPath(CommandLine commandLine) throws ParseException {
		this.documentPath = new File(commandLine.getOptionValue("input"));
		if (!documentPath.exists()) {
			String message = String.format("Invalid parameter 'input' : '%1s' (file not found)", documentPath);
			log.error(MARKER, message);
			throw new ParseException(message);
		}
		if (!documentPath.isDirectory()) {
			String message = String.format("Invalid parameter 'input' : '%1s' (file is not a directory)", documentPath);
			log.error(MARKER, message);
			throw new ParseException(message);
		}
	}

	
	/**
	 * Add "config" option
	 * @param options
	 */
	protected void buildConfigOption(Options options) {
		// config
		{
			Option option = new Option("c", "config", true, "Folder containing document models");
			option.setRequired(true);
			option.setType(File.class);
			option.setArgName("CONFIG_DIR");
			options.addOption(option);
		}
	}

	/**
	 * Add "version" option
	 * @param options
	 */
	protected void buildVersionOption(Options options){
		// version (standard name)
		{
			Option option = new Option("v", "version", true, "Document model name (ex : cnig_PLU_2013)");
			option.setRequired(true);
			option.setArgName("STANDARD_NAME");
			options.addOption(option);
		}
	}

	/**
	 * Parse "config" option to "configDir"
	 * @param commandLine
	 * @throws ParseException
	 */
	protected void parseConfig(CommandLine commandLine) throws ParseException {
		this.configDir = new File(commandLine.getOptionValue("config"));
		//TODO check configDir
	}

	/**
	 * Parse "config" option to "configDir"
	 * @param commandLine
	 * @throws ParseException
	 */
	protected void parseVersion(CommandLine commandLine) throws ParseException {
		this.documentModelName   = commandLine.getOptionValue("version");
	}
	
	
	
	
	/**
	 * Add option "validation-directory"
	 * @param options
	 */
	protected void buildValidationDirectoryOption(Options options) {
		//TODO add optional -O, "validation-directory" option to allow output directory configuration
	}
	

	/**
	 * Parse validation directory
	 * @param commandLine
	 * @throws ParseException
	 */
	protected void parseValidationDirectory(CommandLine commandLine) throws ParseException {
		//TODO optional customization (should not be a child of documentPath)
		validationDirectory = new File( documentPath.getParentFile(), VALIDATION_DIRECTORY_NAME ) ;
		if ( validationDirectory.exists() ) {
			log.info(MARKER,
				"Remove directory {}...", 
				validationDirectory.getAbsolutePath() 
			);
			try {
				FileUtils.deleteDirectory( validationDirectory );
			} catch (Exception e) {
				String message = String.format("Fail to delete directory : '%1s'", validationDirectory);
				log.error(MARKER, message);
				throw new ParseException(message);
			}
		}
		log.info(MARKER,
			"Create directory {}...", 
			validationDirectory.getAbsolutePath() 
		);
		validationDirectory.mkdirs() ;
	}

	
	/**
	 * Add "report-format" and "max-errors" options
	 * @param options
	 */
	protected void buildReportBuilderOptions(Options options) {
		{
			Option option = new Option(null, "report-format", true, "report format (xml|jsonl), default : xml");
			option.setRequired(false);
			option.setArgName("FORMAT");
			options.addOption(option);
		}
		{
			Option option = new Option(null, "max-errors", true, "Maximum number of error reported for a same code");
			option.setRequired(false);
			options.addOption(option);
		}
	}

	/**
	 * Create report builder from commandLine
	 * @param commandLine
	 */
	protected void parseReportBuilder(CommandLine commandLine) {
		File validationRapport = null ;
		if ( commandLine.hasOption("report-format") && commandLine.getOptionValue("report-format").equals("jsonl") ){
			validationRapport = new File( validationDirectory, "validation.jsonl" );
			reportBuilder = new JsonReportBuilder(validationRapport);		
		}else{
			validationRapport = new File( validationDirectory, "validation.xml" );
			reportBuilder = new ReportBuilderLegacy(validationRapport);	
		}

		// max-errors in validation report...
		if (commandLine.hasOption("max-errors")) {
			//TODO check Integer.parseInt exceptions
			int maxError = Integer.parseInt(commandLine.getOptionValue("max-errors"));
			reportBuilder = new FilteredReportBuilder(reportBuilder,maxError);
		}
	}

	
	/**
	 * Add "srs" option
	 * @param options
	 */
	protected void buildCoordinateReferenceSystemOptions(Options options) {
		{
			Option option = new Option("s", "srs", true, "data coordinate reference system (ex : EPSG:2154)");
			option.setRequired(true);
			options.addOption(option);
		}
	}
	
	/**
	 * Parse CoordinateReferenceSystem
	 * @param commandLine
	 * @return
	 */
	protected void parseCoordinateReferenceSystem(CommandLine commandLine) throws ParseException { 
		String srsString = commandLine.getOptionValue("srs", "EPSG:4326");
		try {
			this.coordinateReferenceSystem = CRS.decode(srsString);
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
	
	
	/**
	 * Build option data-extent
	 * @param options
	 */
	protected void buildDataExtentOption(Options options) {
		{
			Option option = new Option(null, "data-extent", true, "Provide a geometry to control data extent (format : WKT, projection : same as data)");
			option.setRequired(false);
			option.setArgName("WKT");
			options.addOption(option);
		}
	}
	/**
	 * Add "data-extent" option
	 * @param commandLine
	 * @throws ParseException
	 */
	protected void parseDataExtent(CommandLine commandLine) throws ParseException {
		if ( ! commandLine.hasOption("data-extent") ){
			return ;
		}
		String wktExtent = commandLine.getOptionValue("data-extent");
		WKTReader reader = new WKTReader();
		try {
			nativeDataExtent = reader.read(wktExtent);
		} catch (com.vividsolutions.jts.io.ParseException e) {
			String message = String.format("Invalid parameter 'data-extent' : '%1s' (invalid WKT)", wktExtent);
			log.error(MARKER, message);
			throw new ParseException(message);
		}
	}
	
	/**
	 * Add "--encoding" option
	 * @param options
	 */
	protected void buildEncodingOption(Options options) {
		{
			Option option = new Option(
				"W", "encoding", true, 
				"Data charset (default is UTF-8, value is overriden if metadata provides a definition)"
			);
			option.setRequired(false);
			options.addOption(option);
		}
	}
	
	/**
	 * Parse encoding
	 * @param commandLine
	 * @return
	 */
	protected void parseEncoding(CommandLine commandLine) throws ParseException {
		try {
			this.encoding = Charset.forName(commandLine.getOptionValue("encoding","UTF-8"));
		}catch (Exception e){
			throw new ParseException("invalid parameter 'encoding' : "+encoding+ " (charset not found)");
		}
	}
	

	/**
	 * Build options corresponding to StringFixer
	 * @param options
	 */
	protected void buildStringFixerOptions(Options options) {
		StringFixerOptions.buildOptions(options);
	}
	
	/**
	 * Parse command line arguments
	 * @param commandLine
	 * @return
	 */
	protected void parseStringFixerOptions(CommandLine commandLine) {
		stringFixer = StringFixerOptions.parseCommandLine(commandLine);
	}
	
	
	/**
	 * Add option "--flat"
	 * @param options
	 */
	protected void buildFlatOption(Options options) {
		{
			Option option = new Option(null, "flat", false, "Allows to ignore file hierarchy in validation (match files to models using name instead of path)");
			option.setRequired(false);
			options.addOption(option);
		}
	}
	/**
	 * Parse flat option
	 * @param commandLine
	 * @return
	 */
	protected void parseFlatOption(CommandLine commandLine) {
		this.flat = commandLine.hasOption("flat");
	}

	/**
	 * Add option "--plugins"
	 * @param options
	 */
	protected void buildPluginsOption(Options options) {
		{
			Option option = new Option(null, "plugins", true, "Plugin loaded for validation (comma separated names)");
			option.setRequired(false);
			options.addOption(option);
		}
	}

	/**
	 * Parse plugins option
	 * @param commandLine
	 */
	protected void parsePluginsOption(CommandLine commandLine) {
		this.plugins = new ArrayList<>();
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
				plugins.add(plugin);
			}
		}
	}



}
