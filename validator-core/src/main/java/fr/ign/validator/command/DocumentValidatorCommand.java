package fr.ign.validator.command;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import fr.ign.validator.Context;
import fr.ign.validator.command.options.StringFixerOptions;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.exception.ModelNotFoundException;
import fr.ign.validator.io.ModelReader;
import fr.ign.validator.io.ModelReaderFactory;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.Projection;
import fr.ign.validator.plugin.Plugin;
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.report.FilteredReportBuilder;
import fr.ign.validator.report.JsonReportBuilder;
import fr.ign.validator.report.ReportBuilder;
import fr.ign.validator.repository.ProjectionRepository;
import fr.ign.validator.string.StringFixer;
import fr.ign.validator.tools.FileConverter;

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

    public static final String VALIDATION_DIRECTORY_NAME = "validation";

    /**
     * URL of the document model.
     * 
     */
    protected URL documentModelUrl;

    /**
     * Standard name (ex : cnig_PLU_2013)
     */
    protected String documentModelName;

    /**
     * Path to the document
     */
    protected File documentPath;

    /**
     * Path to the validation directory (default is validation in documentPath's
     * parent)
     */
    protected File validationDirectory;

    /**
     * Report builder
     */
    protected ReportBuilder reportBuilder;

    /**
     * Optional path to overwrite error code configuration.
     */
    protected File errorConfig;

    /**
     * data projection
     */
    protected Projection projection;

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
    protected Geometry nativeDataExtent;

    /**
     * option - flat validation mode
     */
    protected boolean flat = false;

    /**
     * option - produce normalized data in validation/DATA directory?
     */
    protected boolean normalize = false;

    /**
     * option - plugins used for validation
     */
    protected List<Plugin> plugins = new ArrayList<>();

    /**
     * option - distance (double) express in the input projection system. Default
     * value is a meter express in the default CRS (EPSG:4326): 0.0001
     */
    protected double dgprTolerance;

    /**
     * option - distance (double) express in the input projection system. Example
     * value : a meter express in the default CRS (EPSG:4326): 0.0001 Default value
     * : null (no simplification)
     */
    protected Double dgprSimplification;

    /**
     * option - switch (boolean) Default value is false (faster)
     */
    protected boolean dgprSafeMode;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void buildCustomOptions(Options options) {
        /*
         * input document directory
         */
        buildDocumentPathOption(options);
        /*
         * input document model
         */
        buildModelOption(options);
        // legacy
        buildConfigOption(options);
        buildVersionOption(options);

        /*
         * output options
         */
        buildValidationDirectoryOption(options);
        buildReportBuilderOptions(options);
        buildErrorConfigOption(options);
        buildNormalizeOption(options);

        /*
         * validation options
         */
        buildCoordinateReferenceSystemOptions(options);
        buildDataExtentOption(options);
        buildEncodingOption(options);
        buildStringFixerOptions(options);
        buildFlatOption(options);
        buildPluginsOption(options);

        /*
         * dgpr-plugin options
         */
        buildDgprTolerance(options);
        buildDgprSimplifyDistance(options);
        buildDgprSimplifySafe(options);
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
        parseErrorConfigOption(commandLine);
        parseNormalizeOption(commandLine);

        /*
         * Config and version
         */
        parseDocumentModelUrl(commandLine);

        /*
         * Validation options
         */
        parseCoordinateReferenceSystem(commandLine);
        parseEncoding(commandLine);
        parseStringFixerOptions(commandLine);
        parseDataExtent(commandLine);
        parseFlatOption(commandLine);
        parseTopologicalToleranceOption(commandLine);
        parseDistanceSimplificationOption(commandLine);
        parseSafeSimplificationOption(commandLine);

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
        context.setValidationDirectory(validationDirectory);
        context.setReportBuilder(reportBuilder);

        /*
         * configure error codes
         */
        if (this.errorConfig != null) {
            context.getErrorFactory().loadErrorCodes(this.errorConfig);
        }

        /*
         * configure output mode
         */
        context.setNormalizeEnabled(normalize);

        /*
         * forward validation options
         */
        context.setProjection(projection);
        context.setEncoding(encoding);
        context.setStringFixer(stringFixer);
        context.setNativeDataExtent(nativeDataExtent);
        context.setFlatValidation(flat);

        /*
         * dgpr-plugin options
         */
        context.setDgprTolerance(dgprTolerance);
        context.setDgprSimplification(dgprSimplification);
        context.setDgprSafeMode(dgprSafeMode);

        /*
         * load plugins
         */
        for (Plugin plugin : plugins) {
            plugin.setup(context);
        }

        /*
         * Validate document
         */
        try {
            DocumentModel documentModel = loadDocumentModel();
            context.report(
                context.createError(CoreErrorCodes.VALIDATOR_INFO).setMessageParam(
                    "MESSAGE",
                    "Validation avec le modèle : " + documentModel.getName()
                )
            );

            String ogrVersion = FileConverter.getInstance().getVersion().toString();
            context.report(
                context.createError(CoreErrorCodes.VALIDATOR_INFO).setMessageParam(
                    "MESSAGE",
                    "Version GDAL utilisée : " + ogrVersion
                )
            );

            Document document = new Document(documentModel, documentPath);
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
     * 
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
     * 
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
     * 
     * @param options
     */
    protected void buildConfigOption(Options options) {
        // config
        {
            Option option = new Option("c", "config", true, "[deprecated]Folder containing document models");
            option.setRequired(false);
            option.setType(File.class);
            option.setArgName("CONFIG_DIR");
            options.addOption(option);
        }
    }

    /**
     * Add "version" option
     * 
     * @param options
     */
    protected void buildVersionOption(Options options) {
        // version (standard name)
        {
            Option option = new Option("v", "version", true, "[deprecated]Document model name (ex : cnig_PLU_2013)");
            option.setRequired(false);
            option.setArgName("STANDARD_NAME");
            options.addOption(option);
        }
    }

    /**
     * Add "document-model" option
     * 
     * @param options
     */
    protected void buildModelOption(Options options) {
        {
            Option option = new Option("m", "model", true, "URL of the document model");
            option.setRequired(false);
            option.setArgName("DOCUMENT_MODEL_URL");
            options.addOption(option);
        }
    }

    /**
     * Parse "config" option to "configDir"
     * 
     * @param commandLine
     * @throws ParseException
     */
    protected void parseDocumentModelUrl(CommandLine commandLine) throws ParseException {
        String config = commandLine.getOptionValue("config");
        String version = commandLine.getOptionValue("version");

        try {
            if (!StringUtils.isEmpty(config) && !StringUtils.isEmpty(version)) {
                File configDir = new File(config);
                this.documentModelUrl = findDocumentModelUrl(configDir, version);
            } else {
                String modelUri = commandLine.getOptionValue("model");
                if (StringUtils.isEmpty(modelUri)) {
                    throw new ParseException("--model or --config,--version options required)");
                }
                this.documentModelUrl = new URL(modelUri);
            }
        } catch (MalformedURLException e) {
            throw new ParseException("Invalid model URL");
        }
    }

    /**
     * Locate either files.json or files.xml in configDir.
     * 
     * @param configDir
     * @param version
     * @return
     * @throws ParseException
     * @throws MalformedURLException
     */
    private URL findDocumentModelUrl(File configDir, String version) throws ParseException, MalformedURLException {
        if (!configDir.exists()) {
            String message = String.format("Config directory '%1s' not found!", configDir.getAbsolutePath());
            log.error(MARKER, message);
            throw new ParseException(message);
        }
        File jsonPath = new File(configDir, version + "/files.json");
        if (jsonPath.exists()) {
            return jsonPath.toURI().toURL();
        }
        File xmlPath = new File(configDir, version + "/files.xml");
        if (xmlPath.exists()) {
            return xmlPath.toURI().toURL();
        }
        throw new ParseException(String.format("Model '%1s' not found", version));
    }

    /**
     * Load document model according to documentModelName
     * 
     * @return
     * @throws IOException
     * @throws ModelNotFoundException
     */
    protected DocumentModel loadDocumentModel() throws IOException, ModelNotFoundException {
        ModelReader modelReader = ModelReaderFactory.createModelReader(documentModelUrl);
        return modelReader.loadDocumentModel(documentModelUrl);
    }

    /**
     * Add option "validation-directory"
     * 
     * @param options
     */
    protected void buildValidationDirectoryOption(Options options) {
        // TODO add optional -O, "validation-directory" option to allow output
        // directory configuration
    }

    /**
     * Parse validation directory
     * 
     * @param commandLine
     * @throws ParseException
     */
    protected void parseValidationDirectory(CommandLine commandLine) throws ParseException {
        // TODO optional customization (should not be a child of documentPath)
        validationDirectory = new File(documentPath.getParentFile(), VALIDATION_DIRECTORY_NAME);
        if (validationDirectory.exists()) {
            log.info(MARKER, "Remove directory {}...", validationDirectory.getAbsolutePath());
            try {
                FileUtils.deleteDirectory(validationDirectory);
            } catch (Exception e) {
                String message = String.format("Fail to delete directory : '%1s'", validationDirectory);
                log.error(MARKER, message);
                throw new ParseException(message);
            }
        }
        log.info(MARKER, "Create directory {}...", validationDirectory.getAbsolutePath());
        validationDirectory.mkdirs();
    }

    /**
     * Add "report-format" and "max-errors" options
     * 
     * @param options
     */
    protected void buildReportBuilderOptions(Options options) {
        {
            Option option = new Option(
                null, "report-format", true,
                "report format (DEPRECATED, jsonl default and required)"
            );
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
     * Add option --error-config to the command line.
     * 
     * @param options
     */
    protected void buildErrorConfigOption(Options options) {
        {
            Option option = new Option(
                null, "error-config", true, "Custom error-config.json file to overwrite some error code configuration (message, level,...)"
            );
            option.setRequired(true);
            options.addOption(option);
        }
    }

    /**
     * Retrieve --normalize option.
     * 
     * @param commandLine
     */
    protected void parseErrorConfigOption(CommandLine commandLine) throws ParseException {
        if (!commandLine.hasOption("error-config")) {
            this.errorConfig = null;
            return;
        }
        this.errorConfig = new File(commandLine.getOptionValue("error-config"));
        if (!documentPath.exists()) {
            String message = String.format(
                "Invalid parameter 'error-config' : '%1s' (file not found)", this.errorConfig
            );
            log.error(MARKER, message);
            throw new ParseException(message);
        }
        if (documentPath.isDirectory()) {
            String message = String.format(
                "Invalid parameter 'error-config' : '%1s' (file is a directory)", this.errorConfig
            );
            log.error(MARKER, message);
            throw new ParseException(message);
        }
    }

    /**
     * Add option --normalize to the command line.
     * 
     * @param options
     */
    protected void buildNormalizeOption(Options options) {
        {
            Option option = new Option(
                null, "normalize", false,
                "Enable normalized data production in validation directory (required for validation/document-info.json)."
            );
            option.setRequired(false);
            options.addOption(option);
        }
    }

    /**
     * Retrieve --normalize option.
     * 
     * @param commandLine
     */
    protected void parseNormalizeOption(CommandLine commandLine) {
        this.normalize = commandLine.hasOption("normalize") ? true : false;
    }

    /**
     * Create report builder from commandLine
     * 
     * @param commandLine
     */
    protected void parseReportBuilder(CommandLine commandLine) throws ParseException {
        File validationRapport = new File(validationDirectory, "validation.jsonl");
        reportBuilder = new JsonReportBuilder(validationRapport);
        if (commandLine.hasOption("report-format") && !commandLine.getOptionValue("report-format").equals("jsonl")) {
            throw new ParseException("Since 4.0, only jsonl report are supported");
        }
        // max-errors in validation report...
        if (commandLine.hasOption("max-errors")) {
            // TODO check Integer.parseInt exceptions
            int maxError = Integer.parseInt(commandLine.getOptionValue("max-errors"));
            reportBuilder = new FilteredReportBuilder(reportBuilder, maxError);
        }
    }

    /**
     * Add "srs" option
     * 
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
     * 
     * @param commandLine
     * @return
     */
    protected void parseCoordinateReferenceSystem(CommandLine commandLine) throws ParseException {
        String srsString = commandLine.getOptionValue("srs", "CRS:84");

        ProjectionRepository projectionRepository = ProjectionRepository.getInstance();
        Projection projection = projectionRepository.findByCode(srsString);
        if (projection == null) {
            String message = String.format("Paramètre invalide 'srs' : '%1s' non supporté", srsString);
            throw new ParseException(message);
        }
        this.projection = projection;
    }

    /**
     * Build option data-extent
     * 
     * @param options
     */
    protected void buildDataExtentOption(Options options) {
        {
            Option option = new Option(
                null, "data-extent", true,
                "Provide a geometry to control data extent (format : WKT, projection : same as data)"
            );
            option.setRequired(false);
            option.setArgName("WKT");
            options.addOption(option);
        }
    }

    /**
     * Add "data-extent" option
     * 
     * @param commandLine
     * @throws ParseException
     */
    protected void parseDataExtent(CommandLine commandLine) throws ParseException {
        if (!commandLine.hasOption("data-extent")) {
            return;
        }
        String wktExtent = commandLine.getOptionValue("data-extent");
        WKTReader reader = new WKTReader();
        try {
            nativeDataExtent = reader.read(wktExtent);
        } catch (org.locationtech.jts.io.ParseException e) {
            String message = String.format("Invalid parameter 'data-extent' : '%1s' (invalid WKT)", wktExtent);
            log.error(MARKER, message);
            throw new ParseException(message);
        }
    }

    /**
     * Add "--encoding" option
     * 
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
     * 
     * @param commandLine
     * @return
     */
    protected void parseEncoding(CommandLine commandLine) throws ParseException {
        try {
            this.encoding = Charset.forName(commandLine.getOptionValue("encoding", "UTF-8"));
        } catch (Exception e) {
            throw new ParseException("invalid parameter 'encoding' : " + encoding + " (charset not found)");
        }
    }

    /**
     * Build options corresponding to StringFixer
     * 
     * @param options
     */
    protected void buildStringFixerOptions(Options options) {
        StringFixerOptions.buildOptions(options);
    }

    /**
     * Parse command line arguments
     * 
     * @param commandLine
     * @return
     */
    protected void parseStringFixerOptions(CommandLine commandLine) {
        stringFixer = StringFixerOptions.parseCommandLine(commandLine);
    }

    /**
     * Add option "--flat"
     * 
     * @param options
     */
    protected void buildFlatOption(Options options) {
        {
            Option option = new Option(
                null, "flat", false,
                "Allows to ignore file hierarchy in validation (match files to models using name instead of path)"
            );
            option.setRequired(false);
            options.addOption(option);
        }
    }

    /**
     * Parse flat option
     * 
     * @param commandLine
     * @return
     */
    protected void parseFlatOption(CommandLine commandLine) {
        this.flat = commandLine.hasOption("flat");
    }

    /**
     * Add option "--plugins"
     * 
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
     * 
     * @param commandLine
     */
    protected void parsePluginsOption(CommandLine commandLine) {
        this.plugins = new ArrayList<>();
        if (commandLine.hasOption("plugins")) {
            PluginManager pluginManager = new PluginManager();
            String[] pluginNames = commandLine.getOptionValue("plugins").split(",");
            for (String pluginName : pluginNames) {
                Plugin plugin = pluginManager.getPluginByName(pluginName);
                if (plugin == null) {
                    String message = String.format("fail to load plugin '%1s'", pluginName);
                    log.error(MARKER, message);
                    System.exit(1);
                }
                log.info(MARKER, String.format("setup plugin '%1s'...", pluginName));
                plugins.add(plugin);
            }
        }
    }

    /**
     * Add option "--dgpr-tolerance"
     * 
     * @param options
     */
    protected void buildDgprTolerance(Options options) {
        {
            Option option = new Option(
                null, "dgpr-tolerance", true,
                "Tolerance express in the input CRS (ex: 1 for 1 meter in EPSG:2154), used in geometry comparison"
            );
            option.setRequired(false);
            options.addOption(option);
        }
    }

    /**
     * Add option "--dgpr-simplify"
     * 
     * @param options
     */
    protected void buildDgprSimplifyDistance(Options options) {
        {
            Option option = new Option(
                null, "dgpr-simplify", true,
                "Distance for geometric simplification express in the input CRS (ex: 1 for 1 meter in EPSG:2154), used in geometry comparison"
            );
            option.setRequired(false);
            options.addOption(option);
        }
    }

    /**
     * Add option "--dgpr-safe-simplify"
     * 
     * @param options
     */
    protected void buildDgprSimplifySafe(Options options) {
        {
            Option option = new Option(
                null, "dgpr-safe-simplify", false,
                "Force the use of TopologyPreservingSimplifier over DouglasPeuckerSimplifier"
            );
            option.setRequired(false);
            options.addOption(option);
        }
    }

    /**
     * Parse tolerance option
     *
     * @param commandLine
     * @throws ParseException
     */
    protected void parseTopologicalToleranceOption(CommandLine commandLine) throws ParseException {
        String toleranceString = commandLine.getOptionValue("dgpr-tolerance", "0.0000001");

        try {
            Double tolerance = Double.valueOf(toleranceString);
            this.dgprTolerance = tolerance;
        } catch (NumberFormatException e) {
            String message = String.format(
                "Paramètre invalide 'dgpr-tolerance' : '%1s' n'est pas un double",
                toleranceString
            );
            throw new ParseException(message);
        }
    }

    /**
     * Parse tolerance option
     *
     * @param commandLine
     * @throws ParseException
     */
    protected void parseDistanceSimplificationOption(CommandLine commandLine) throws ParseException {
        String strValue = commandLine.getOptionValue("dgpr-simplify", null);
        if (strValue == null) {
            this.dgprSimplification = null;
            log.info(MARKER, "validator command running whith no geometric simplification");
            return;
        }

        try {
            Double distance = Double.valueOf(strValue);
            this.dgprSimplification = distance;
        } catch (NumberFormatException e) {
            String message = String.format("Paramètre invalide 'dgpr-simplify' : '%1s' n'est pas un double", strValue);
            throw new ParseException(message);
        }
    }

    /**
     * Parse flat option
     * 
     * @param commandLine
     * @return
     */
    protected void parseSafeSimplificationOption(CommandLine commandLine) {
        this.dgprSafeMode = commandLine.hasOption("dgpr-safe-simplify");
        if (this.dgprSafeMode) {
            log.info(MARKER, "validator command running whith Safe Simplifier");
        } else {
            log.info(MARKER, "validator command running whith unsafe Simplifier");
        }
    }

}
