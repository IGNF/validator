package fr.ign.validator.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.tools.internal.FixGML;
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

    public static final String ENCODING_UTF8 = "UTF-8";
    public static final String ENCODING_LATIN1 = "ISO-8859-1";

    private static FileConverter instance = new FileConverter();

    /**
     * ogr2ogr version
     */
    private OgrVersion version;

    /**
     * Default constructor
     */
    private FileConverter() {
        this.version = retrieveAndValidateOgrVersion();
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
     * Get path to ogr2ogr. Default is ogr2ogr, it can be specified with :
     * <ul>
     * <li>Environment variable OGR2OGR_PATH</li>
     * <li>System property ogr2ogr_path</li>
     * </ul>
     * 
     * @return
     */
    private String getOgr2ogrPath() {
        String result = System.getenv("OGR2OGR_PATH");
        if (result != null) {
            return result;
        }
        return System.getProperty("ogr2ogr_path", "ogr2ogr");
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
     * Récupération de la version de ogr2ogr
     * 
     * @return
     */
    private OgrVersion retrieveAndValidateOgrVersion() {
        String fullVersion = retrieveFullVersion();
        OgrVersion version = new OgrVersion(fullVersion);
        version.ensureVersionIsSupported();
        return version;
    }

    /**
     * Call `ogr2ogr --version` to get GDAL version
     * 
     * @return
     */
    private String retrieveFullVersion() {
        log.info(MARKER, "Run 'ogr2ogr --version' to retrieve GDAL version...");
        String[] args = new String[] {
            getOgr2ogrPath(), "--version"
        };
        ProcessBuilder builder = new ProcessBuilder(args);
        try {
            Process process = builder.start();

            process.waitFor();

            InputStream stdout = process.getInputStream();
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
            String version = stdoutReader.readLine();
            stdoutReader.close();
            return version;
        } catch (IOException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * Convert a source file with a given sourceCharset to an UTF-8 encoded CSV
     * target
     * 
     * @param source
     * @param target
     * @param sourceCharset
     * @throws IOException
     */
    public void convertToCSV(File source, File target, Charset sourceCharset) throws IOException {
        log.info(MARKER, "{} => {} (gdal {})...", source, target, version);
        if (target.exists()) {
            target.delete();
        }
        String sourceExtension = FilenameUtils.getExtension(source.getName()).toLowerCase();
        /*
         * patch on GML files
         */
        if (sourceExtension.equals("gml")) {
            fixGML(source);
        }
        /*
         * Removing cpg
         */
        CompanionFileUtils.removeCompanionFile(source, "cpg");
        CompanionFileUtils.removeCompanionFile(source, "CPG");

        String[] args = getArguments(source, target, "CSV");
        Map<String, String> envs = new HashMap<String, String>();
        // encoding is specified in UTF-8 so that ogr2ogr doesn't convert
        if (sourceExtension.equals("dbf") || sourceExtension.equals("shp")) {
            envs.put("SHAPE_ENCODING", toEncoding(sourceCharset));
        }
        runCommand(args, envs);
        /*
         * Controls that output file is created
         */
        if (!target.exists()) {
            log.error(MARKER, "Impossible de créer le fichier de sortie {}", target.getName());
            createFalseCSV(target);
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
     * Converts a source file in LATIN1 encoded shapefile
     * 
     * @param files
     * @throws IOException
     */
    public void convertToShapefile(File source, File target) throws IOException {
        log.info(MARKER, "{} => {} (gdal {})...", source, target, version);
        if (FilenameUtils.getExtension(source.getName()).toLowerCase().equals("gml")) {
            fixGML(source);
        }

        String[] args = getArguments(source, target, "ESRI Shapefile");
        Map<String, String> envs = new HashMap<String, String>();
        envs.put("SHAPE_ENCODING", ENCODING_LATIN1);
        runCommand(args, envs);
        /*
         * Controls that output file is created
         */
        if (!target.exists()) {
            // TODO throw IOException
            log.error(MARKER, "Impossible de créer le fichier de sortie {}", target.getName());
            createFalseCSV(target);
        }
        /*
         * Generating cgp file
         */
        File cpgFile = CompanionFileUtils.getCompanionFile(target, "cpg");
        FileUtils.writeStringToFile(cpgFile, ENCODING_LATIN1, StandardCharsets.UTF_8);
    }

    /**
     * 
     * Any invalid csv file blocks ogr2ogr use A valid file with header without data
     * is created to avoid this problem
     * 
     * @param target
     * @throws IOException
     */
    private void createFalseCSV(File target) throws IOException {
        target.createNewFile();
        FileWriter fileWriter = new FileWriter(target);
        String header = "header1,header2,header3";
        fileWriter.append(header);
        fileWriter.flush();
        fileWriter.close();
    }

    /**
     * Get arguments to invoke ogr2ogr
     * 
     * @param source
     * @param target
     * @param driver
     * @param encode
     * @return
     */
    private String[] getArguments(File source, File target, String driver) {
        List<String> arguments = new ArrayList<String>();
        arguments.add(getOgr2ogrPath());

        // Otherwise, some ogr2ogr versions transforms 01 to 1...
        if (FilenameUtils.getExtension(source.getName()).toLowerCase().equals("gml")) {
            arguments.add("--config");
            arguments.add("GML_FIELDTYPES");
            arguments.add("ALWAYS_STRING");
        }

        arguments.add("-f");
        arguments.add(driver);
        /*
         * Getting format-specific parameters
         */
        if (driver.equals("CSV")) {
            if (hasSpatialColumn(source)) {
                // unsure conversion to WKT
                arguments.add("-lco");
                arguments.add("GEOMETRY=AS_WKT");
            }

            // avoid useless quotes (GDAL 2.3 or more)
            arguments.add("-lco");
            arguments.add("STRING_QUOTING=IF_NEEDED");

            // avoid coordinate rounding
            arguments.add("-lco");
            arguments.add("OGR_WKT_ROUND=NO");

            // force "\r\n"
            arguments.add("-lco");
            arguments.add("LINEFORMAT=CRLF");
        }
        /*
         * Getting input/output files
         */
        arguments.add(target.getAbsolutePath());
        arguments.add(source.getAbsolutePath());

        // force 2d output (to be removed, related to old JTS versions)
        arguments.add("-dim");
        arguments.add("2");

        /*
         * Getting source encoding
         */
        String[] args = new String[arguments.size()];
        arguments.toArray(args);
        return args;
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
        if (!FilenameUtils.getExtension(source.getName()).toLowerCase().equals("dbf")) {
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
    private void runCommand(String[] args, Map<String, String> envs) throws IOException {
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
             * 
             * TODO redirect output to log4j logs
             */
            builder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            builder.redirectError(ProcessBuilder.Redirect.DISCARD);
            process = builder.start();
            process.waitFor();

            if (process.exitValue() != 0) {
                log.error(MARKER, "command fail!");
            }
        } catch (IOException e1) {
            throw new RuntimeException("ogr2ogr command fails", e1);
        } catch (InterruptedException e) {
            throw new RuntimeException("ogr2ogr command fails", e);
        }
    }

    /**
     * Logs the execution of a command
     * 
     * @param args
     */
    private String commandToString(String[] args) {
        String message = "";
        for (int i = 0; i < args.length; i++) {
            if (i == 0 || args[i].isEmpty() || (args[i].charAt(0) == '-') || (args[i].charAt(0) == '"')
                || (args[i].charAt(0) == '\'')) {
                message += args[i] + " ";
            } else {
                message += "'" + args[i] + "' ";
            }
        }
        return message;
    }

    /**
     * ogr2ogr ignores self-closing tags. They are changed to empty tags
     * 
     * @param source
     * @throws IOException
     */
    private void fixGML(File source) throws IOException {
        File backupedFile = new File(source.getPath() + ".backup");
        source.renameTo(backupedFile);
        FixGML.replaceAutoclosedByEmpty(backupedFile, source);
    }

}
