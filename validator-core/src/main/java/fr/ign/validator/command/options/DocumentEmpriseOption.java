package fr.ign.validator.command.options;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.geometry.GeometryReader;
import fr.ign.validator.tools.FileConverter;
import fr.ign.validator.tools.TableReader;

/**
 *
 * @author cbouche
 *
 */
public class DocumentEmpriseOption {

    private static final String OPTION_NAME = "cnig-document-emprise";

    private DocumentEmpriseOption() {
        // disabled
    }

    /**
     * Add "cnig-document-emprise" option to command line.
     *
     * @param options
     */
    public static void buildOptions(Options options) {
        {
            Option option = new Option(
                null, OPTION_NAME, true,
                "Path to a GeoJSON feature collection file or a simple WKT geometry."
                    + "Coordinates must be declare in ESPG:4326."
            );
            option.setRequired(false);
            options.addOption(option);
        }
    }

    /**
     * Parse option from command line.
     *
     * @param commandLine
     * @return
     * @throws ParseException
     */
    public static Geometry parseCustomOptions(CommandLine commandLine) throws ParseException {

        if (!commandLine.hasOption(OPTION_NAME)) {
            return null;
        }

        String parsedOption = (String) commandLine.getParsedOptionValue(OPTION_NAME);

        File file = new File(parsedOption);
        if (file.exists()) {
            return parseGeoJSONFileOption(parsedOption);
        }

        return parseWKTOption(parsedOption);
    }

    private static Geometry parseWKTOption(String parsedOption) throws ParseException {
        Geometry documentReferenceGeometry = null;
        GeometryReader geometryReader = new GeometryReader();

        try {
            documentReferenceGeometry = geometryReader.read(parsedOption);
        } catch (org.locationtech.jts.io.ParseException e) {
            DocumentEmpriseOption.throwParseErrorException(parsedOption, "Error while parsing WKT");
        }

        if (documentReferenceGeometry == null) {
            DocumentEmpriseOption.throwParseErrorException(parsedOption, "Parsed geometry is empty");
        }

        return documentReferenceGeometry;

    }

    private static Geometry parseGeoJSONFileOption(String parsedOption) throws ParseException {
        File file = new File(parsedOption);
        String filename = file.getName().toLowerCase();

        if (!filename.endsWith(".json")) {
            DocumentEmpriseOption.throwParseErrorException(parsedOption, "Ensure the input file has .json extension");
        }
        FileConverter fileConverter = FileConverter.getInstance();
        File transformedFile = new File(file.getParentFile().getPath() + "/cnig_document_emprise_wkt.csv");
        try {
            fileConverter.convertToCSV(file, transformedFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            DocumentEmpriseOption.throwParseErrorException(
                parsedOption,
                "Ensure the input file respect geojson format"
            );
        }

        GeometryReader geometryReader = new GeometryReader();
        Geometry union = null;

        try {
            // read CSV file
            TableReader tableReader = TableReader.createTableReader(transformedFile, StandardCharsets.UTF_8);

            int column = tableReader.findColumn("WKT");
            while (tableReader.hasNext()) {
                String wkt = tableReader.next()[column];
                Geometry geometry = geometryReader.read(wkt);
                if (union == null) {
                    union = geometry;
                } else {
                    // merge featureCollection
                    union = union.union(geometry);
                }
            }
        } catch (IOException e) {
            DocumentEmpriseOption.throwParseErrorException(parsedOption, "Parsed geometry is empty");
        } catch (org.locationtech.jts.io.ParseException e) {
            DocumentEmpriseOption.throwParseErrorException(parsedOption, "Parsed geometry is empty");
        }

        return union;
    }

    private static void throwParseErrorException(String parsedOption, String reason) throws ParseException {
        // TODO try with GeoJSON format before throwing
        String message = String.format(
            "Invalid document emprise format - %1s - %1s given for %1s", reason,
            parsedOption, OPTION_NAME
        );
        throw new ParseException(message);
    }

}
