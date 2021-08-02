package fr.ign.validator.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.exception.ValidatorFatalError;
import fr.ign.validator.tools.ogr.OgrVersion;

/**
 * 
 * Helper based on GDAL/ogr2ogr to convert spatial file formats
 * 
 * @author MBorne
 * @author CBouche
 * 
 */
public class FileConverter {

    public static final Marker MARKER = MarkerManager.getMarker("FileConverter");
    public static final Logger log = LogManager.getRootLogger();

    private static final String DRIVER_CSV = "CSV";
    private static final String DRIVER_SHAPEFILE = "ESRI Shapefile";

    public static final String ENCODING_UTF8 = "UTF-8";
    public static final String ENCODING_LATIN1 = "ISO-8859-1";

    private static FileConverter instance = new FileConverter();

    /**
     * Path to ogr2ogr
     */
    private String ogr2ogrPath;

    /**
     * ogr2ogr version
     */
    private OgrVersion version;

    /**
     * EXPERIMENTAL - PCRS - Optional GMLAS config path provided by GMLAS_CONFIG
     * environment variable.
     * 
     * @see CONFIG_FILE option for GMLAS driver
     *      https://gdal.org/drivers/vector/gmlas.html#dataset-creation-options
     */
    private File gmlasConfig;

    /**
     * Default constructor
     */
    private FileConverter() {
        log.info(MARKER, "Instanciate FileConverter ensuring that ogr2ogr version is supported...");
        this.ogr2ogrPath = retrieveOgr2ogrPath();
        this.version = retrieveAndValidateOgrVersion();
        this.gmlasConfig = retrieveAndValidateGmlasConfig();
    }

    /**
     * Get instance
     * 
     * @return
     */
    public static FileConverter getInstance() {
        return instance;
    }

    /**
     * returns ogr2ogr version
     * 
     * @return null if command `ogr2ogr --version` fails
     */
    public OgrVersion getVersion() {
        return this.version;
    }

    /**
     * Convert a source file with a given sourceCharset to an UTF-8 encoded CSV
     * target.
     * 
     * @param source
     * @param target
     * @param options
     * @throws IOException
     */
    public void convertToCSV(File source, File target, TableReaderOptions options) throws IOException {
        log.info(MARKER, "{} => {} (gdal {})...", source, target, version);
        if (target.exists()) {
            FileUtils.forceDelete(target);
        }

        /*
         * Prepare command arguments.
         */
        List<String> args = new ArrayList<>();
        args.add(ogr2ogrPath);

        // Otherwise, some ogr2ogr versions transforms 01 to 1...
        boolean sourceIsGML = FilenameUtils.getExtension(source.getName()).equalsIgnoreCase("gml");
        if (sourceIsGML) {
            args.add("--config");
            args.add("GML_FIELDTYPES");
            args.add("ALWAYS_STRING");

            File gfsFile = CompanionFileUtils.getCompanionFile(source, "gfs");
            if (gfsFile.exists()) {
                log.warn(MARKER, "remove gfs file {}...", gfsFile);
                FileUtils.forceDelete(gfsFile);
            }
        }

        args.add("-f");
        args.add(DRIVER_CSV);

        if (hasSpatialColumn(source)) {
            // geometry conversion to WKT
            args.add("-lco");
            args.add("GEOMETRY=AS_WKT");
        }

        // avoid useless quotes (GDAL 2.3 or more)
        args.add("-lco");
        args.add("STRING_QUOTING=IF_NEEDED");

        // avoid coordinate rounding
        args.add("-lco");
        args.add("OGR_WKT_ROUND=NO");

        // force "\r\n"
        args.add("-lco");
        args.add("LINEFORMAT=CRLF");

        if (sourceIsGML && options.hasXsdSchema()) {
            // ignore empty types
            args.add("-oo");
            args.add("REMOVE_UNUSED_LAYERS=YES");

            if (gmlasConfig != null) {
                args.add("-oo");
                args.add("CONFIG_FILE=" + gmlasConfig.getAbsolutePath());
            }

            // specify XSD schema path
            args.add("-oo");
            args.add("XSD=" + options.getXsdSchema().toString());
        }

        /*
         * Getting input/output files
         */
        args.add(target.getAbsolutePath());
        if (sourceIsGML && options.hasXsdSchema()) {
            args.add("GMLAS:" + source.getAbsolutePath());
        } else {
            args.add(source.getAbsolutePath());
        }

        // force 2d output (to be removed, related to old JTS versions)
        args.add("-dim");
        args.add("2");

        /*
         * Remove CPG files as they may contains non portable values such as system.
         */
        CompanionFileUtils.removeCompanionFile(source, "cpg");
        CompanionFileUtils.removeCompanionFile(source, "CPG");

        /*
         * Configure charset for shapefiles
         */
        Map<String, String> envs = new HashMap<>();
        String sourceExtension = FilenameUtils.getExtension(source.getName()).toLowerCase();
        if (sourceExtension.equals("dbf") || sourceExtension.equals("shp")) {
            envs.put("SHAPE_ENCODING", toEncoding(options.getSourceCharset()));
        }
        runCommand(args, envs);

        /*
         * Ensure that output file is created
         */
        if (!target.exists()) {
            String message = String.format(
                "Fail to convert '%1s' to CSV at '%2s'",
                source.getAbsolutePath(),
                target.getAbsolutePath()
            );
            throw new ValidatorFatalError(message);
        }
    }

    /**
     * Convert a source file with a given sourceCharset to an UTF-8 encoded CSV
     * target.
     * 
     * @param source
     * @param target
     * @param sourceCharset
     * @throws IOException
     */
    public void convertToCSV(File source, File target, Charset sourceCharset) throws IOException {
        TableReaderOptions options = new TableReaderOptions(sourceCharset);
        convertToCSV(source, target, options);
    }

    /**
     * Converts a VRT file to a LATIN1 encoded shapefile.
     * 
     * @deprecated related to a legacy datastore (EaaS / mongeoportail), used only
     *             by plugin-cnig
     * 
     * @param files
     * @throws IOException
     */
    public void convertToShapefile(File source, File target) throws IOException {
        if (!FilenameUtils.getExtension(source.getName()).equalsIgnoreCase("vrt")) {
            throw new ValidatorFatalError(
                "convertToShapefile is deprecated and source file is not a vrt file " + source.getAbsolutePath()
            );
        }
        log.info(MARKER, "{} => {} (gdal {})...", source, target, version);

        List<String> args = new ArrayList<>();
        args.add(ogr2ogrPath);

        args.add("-f");
        args.add(DRIVER_SHAPEFILE);

        /*
         * Getting input/output files
         */
        args.add(target.getAbsolutePath());
        args.add(source.getAbsolutePath());

        // force 2d output (to be removed, related to old JTS versions)
        args.add("-dim");
        args.add("2");

        Map<String, String> envs = new HashMap<>();
        envs.put("SHAPE_ENCODING", ENCODING_LATIN1);
        runCommand(args, envs);
        /*
         * Controls that output file is created
         */
        if (!target.exists()) {
            String message = String.format(
                "Fail to convert '%1s' to shapefile at '%2s'",
                source.getAbsolutePath(),
                target.getAbsolutePath()
            );
            throw new ValidatorFatalError(message);
        }
        /*
         * Generating cgp file
         */
        File cpgFile = CompanionFileUtils.getCompanionFile(target, "cpg");
        FileUtils.writeStringToFile(cpgFile, ENCODING_LATIN1, StandardCharsets.UTF_8);
    }

    /**
     * Get path to ogr2ogr. Default is ogr2ogr, it can be specified with :
     * <ul>
     * <li>Environment variable OGR2OGR_PATH</li>
     * <li>System property ogr2ogr_path</li>
     * </ul>
     * 
     * @return
     */
    private String retrieveOgr2ogrPath() {
        String result = System.getenv("OGR2OGR_PATH");
        if (result != null) {
            log.info(MARKER, "Found env OGR2OGR_PATH={}", result);
            return result;
        }
        result = System.getProperty("ogr2ogr_path", null);
        if (result != null) {
            log.info(MARKER, "Found -Dogr2ogr_path={}", result);
            return result;
        }
        log.info(MARKER, "Env OGR2OGR_PATH not found, using OGR2OGR_PATH=ogr2ogr");
        return "ogr2ogr";
    }

    /**
     * Get ogr2ogr version
     * 
     * @return
     */
    private OgrVersion retrieveAndValidateOgrVersion() {
        String fullVersion = retrieveFullVersion();
        log.info(MARKER, "ogr2ogr --version : {}", fullVersion);
        OgrVersion result = new OgrVersion(fullVersion);
        result.ensureVersionIsSupported();
        return result;
    }

    /**
     * Get path to GMLAS driver config.
     * 
     * @return
     */
    private File retrieveAndValidateGmlasConfig() {
        String value = System.getenv("GMLAS_CONFIG");
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        File result = new File(value);
        if (!result.exists()) {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid value for GMLAS_CONFIG, '%1s' not found",
                    value
                )
            );
        }
        return result;
    }

    /**
     * Set gmlasConfig for test purpose.
     * 
     * @param gmlasConfig
     */
    public void setGmlasConfig(File gmlasConfig) {
        this.gmlasConfig = gmlasConfig;
    }

    /**
     * Call `ogr2ogr --version` to get GDAL version
     * 
     * @return
     */
    private String retrieveFullVersion() {
        log.info(MARKER, "Run 'ogr2ogr --version' to retrieve GDAL version...");
        String[] args = new String[] {
            ogr2ogrPath, "--version"
        };
        ProcessBuilder builder = new ProcessBuilder(args);
        try {
            Process process = builder.start();

            process.waitFor();

            InputStream stdout = process.getInputStream();
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
            String result = stdoutReader.readLine();
            stdoutReader.close();
            return result;
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    /**
     * Convert java charset to GDAL encoding
     * 
     * @param sourceCharset
     * @return
     */
    private String toEncoding(Charset sourceCharset) {
        if (sourceCharset.equals(StandardCharsets.ISO_8859_1)) {
            return ENCODING_LATIN1;
        } else {
            return ENCODING_UTF8;
        }
    }

    /**
     * Indicates if a source file has a geometry column
     * 
     * Note : This is used to avoid the different behaviors of ogr2ogr when treating
     * dbf files
     * 
     * @param source
     * @return
     */
    private boolean hasSpatialColumn(File source) {
        if (!FilenameUtils.getExtension(source.getName()).equalsIgnoreCase("dbf")) {
            return true;
        }
        // C'est un .dbf, est-ce qu'il y a un .shp?
        if (CompanionFileUtils.hasCompanionFile(source, "shp") || CompanionFileUtils.hasCompanionFile(source, "SHP")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Run command line
     * 
     * @throws IOException
     */
    private void runCommand(List<String> args, Map<String, String> envs) throws IOException {
        Process process = null;
        try {
            /* output command line to logs */
            String commandLine = commandToString(args);
            log.info(MARKER, commandLine);

            /* create process */
            ProcessBuilder builder = new ProcessBuilder(args);
            for (String envName : envs.keySet()) {
                builder.environment().put(envName, envs.get(envName));
            }

            /*
             * Run process ignoring outputs (previous method seams to cause deadlocks).
             */
            builder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            builder.redirectError(ProcessBuilder.Redirect.DISCARD);
            process = builder.start();
            process.waitFor();

            if (process.exitValue() != 0) {
                log.error(MARKER, "command fail!");
            }
        } catch (IOException | InterruptedException e) {
            throw new ValidatorFatalError("ogr2ogr command fails", e);
        }
    }

    /**
     * Logs the execution of a command
     * 
     * @param args
     */
    private String commandToString(List<String> args) {
        String message = "";
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            if (i == 0 || arg.isEmpty() || (arg.charAt(0) == '-') || (arg.charAt(0) == '"')
                || (arg.charAt(0) == '\'')) {
                message += arg + " ";
            } else {
                message += "'" + arg + "' ";
            }
        }
        return message;
    }

}
