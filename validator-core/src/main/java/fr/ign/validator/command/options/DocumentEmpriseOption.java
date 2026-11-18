package fr.ign.validator.command.options;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.union.UnaryUnionOp;

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

    /**
     * Holes of the union smaller than this area (in square degrees, about 8 ha) are
     * removed: they are gaps between the borders of adjacent features
     * (municipalities of gpu-site), not part of the emprise.
     */
    static final double MIN_HOLE_AREA = 1e-5;

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
        String filename = file.getName().toLowerCase(Locale.ROOT);

        if (!filename.endsWith(".json")) {
            DocumentEmpriseOption.throwParseErrorException(parsedOption, "Ensure the input file has .json extension");
        }
        FileConverter fileConverter = FileConverter.getInstance();
        // unique temporary file (the GeoJSON directory may be shared by concurrent
        // validations), removed after reading
        File transformedFile;
        try {
            transformedFile = File.createTempFile("cnig_document_emprise_wkt", ".csv");
        } catch (IOException e) {
            throw new IllegalStateException("Fail to create a temporary file", e);
        }
        try {
            return readEmprise(parsedOption, file, fileConverter, transformedFile);
        } finally {
            FileUtils.deleteQuietly(transformedFile);
        }
    }

    private static Geometry readEmprise(String parsedOption, File file, FileConverter fileConverter,
        File transformedFile) throws ParseException {
        try {
            fileConverter.convertToCSV(file, transformedFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            DocumentEmpriseOption.throwParseErrorException(
                parsedOption,
                "Ensure the input file respect geojson format"
            );
        }

        GeometryReader geometryReader = new GeometryReader();
        List<Geometry> geometries = new ArrayList<>();

        // read CSV file
        try (TableReader tableReader = TableReader.createTableReader(transformedFile, StandardCharsets.UTF_8)) {

            int column = tableReader.findColumn("WKT");
            while (tableReader.hasNext()) {
                String wkt = tableReader.next()[column];
                geometries.add(geometryReader.read(wkt));
            }
        } catch (IOException e) {
            DocumentEmpriseOption.throwParseErrorException(parsedOption, "Parsed geometry is empty");
        } catch (org.locationtech.jts.io.ParseException e) {
            DocumentEmpriseOption.throwParseErrorException(parsedOption, "Parsed geometry is empty");
        }

        if (geometries.isEmpty()) {
            return null;
        }
        if (geometries.size() == 1) {
            return geometries.get(0);
        }
        // merge featureCollection in a single pass (a pairwise union is quadratic)
        return removeSmallHoles(UnaryUnionOp.union(geometries), MIN_HOLE_AREA);
    }

    /**
     * Remove the interior rings with an area lower than the given threshold.
     *
     * @param geometry
     * @param minHoleArea
     * @return
     */
    static Geometry removeSmallHoles(Geometry geometry, double minHoleArea) {
        if (geometry instanceof Polygon) {
            return removeSmallHoles((Polygon) geometry, minHoleArea);
        }
        if (geometry instanceof MultiPolygon) {
            Polygon[] polygons = new Polygon[geometry.getNumGeometries()];
            for (int i = 0; i < polygons.length; i++) {
                polygons[i] = removeSmallHoles((Polygon) geometry.getGeometryN(i), minHoleArea);
            }
            return geometry.getFactory().createMultiPolygon(polygons);
        }
        return geometry;
    }

    private static Polygon removeSmallHoles(Polygon polygon, double minHoleArea) {
        List<LinearRing> holes = new ArrayList<>();
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            LinearRing hole = polygon.getInteriorRingN(i);
            if (polygon.getFactory().createPolygon(hole).getArea() >= minHoleArea) {
                holes.add(hole);
            }
        }
        if (holes.size() == polygon.getNumInteriorRing()) {
            return polygon;
        }
        return polygon.getFactory().createPolygon(polygon.getExteriorRing(), holes.toArray(new LinearRing[0]));
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
