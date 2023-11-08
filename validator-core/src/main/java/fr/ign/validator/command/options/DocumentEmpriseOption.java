package fr.ign.validator.command.options;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.geotools.geometry.jts.WKTReader2;
import org.locationtech.jts.geom.Geometry;

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
                "Path to a geometry or a geometry. WKT or GeoJSON format are supported. Declared in the same CRS than data source"
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
        String textGeometry = null;
        Geometry documentReferenceGeometry = null;

        File file = new File(parsedOption);
        if (file.exists()) {
            FileReader fileReader;
            try {
                fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                textGeometry = bufferedReader.readLine().trim();
                bufferedReader.close();
            } catch (IOException e) {
                DocumentEmpriseOption.throwParseErrorException(parsedOption, "File is unreadable");
            }
        } else {
            textGeometry = parsedOption;
        }

        if (textGeometry == null) {
            DocumentEmpriseOption.throwParseErrorException(parsedOption, "Text geometry is empty");
        }

        WKTReader2 reader2 = new WKTReader2();
        try {
            documentReferenceGeometry = reader2.read(textGeometry);
        } catch (org.locationtech.jts.io.ParseException e) {
            DocumentEmpriseOption.throwParseErrorException(parsedOption, "Error while parsing WKT");
        }

        if (documentReferenceGeometry == null) {
            DocumentEmpriseOption.throwParseErrorException(
                parsedOption, "TODO IMPLEMENT GeoJSON reader or provide WKT"
            );
        }

        // TODO content
        // try is JSON geometry
        // if go with it
        // if not throw ParseException "Invalid document emprise option"
        return documentReferenceGeometry;
    }

    private static void throwParseErrorException(String parsedOption, String reason) throws ParseException {
        // TODO try with GeoJSON format before throwing
        String message = String.format(
            "Invalid document emprise format - %1s - %1s given for %1s",
            reason, parsedOption, OPTION_NAME
        );
        throw new ParseException(message);
    }

}
