package fr.ign.validator.cnig.process;

import static org.junit.Assert.assertEquals;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;

import fr.ign.validator.tools.ResourceHelper;

/**
 *
 * Test DocumentGeometryProcess
 *
 * @author DDarras
 *
 */
public class DocumentGeometryProcessTest {

    DocumentGeometryProcess documentGeometryProcess;

    /**
     * Initialize test
     *
     * @throws Exception
     */
    public DocumentGeometryProcessTest() throws Exception {
        List<File> inputFiles = new ArrayList<File>();
        inputFiles.add(ResourceHelper.getResourceFile(getClass(), "/geometry/fair_geometries.csv"));
        inputFiles.add(ResourceHelper.getResourceFile(getClass(), "/geometry/dubious_geometries.csv"));

        List<String> geometries = new ArrayList<String>();
        geometries.add("geom");
        geometries.add("geometry");
        geometries.add("WKT");

        this.documentGeometryProcess = new DocumentGeometryProcess(inputFiles, geometries);
    }

    @Test
    public void testDetectGeometries() throws Exception {
        this.documentGeometryProcess.detectGeometries();

        String expected = "POLYGON EMPTY,POLYGON EMPTY,MULTIPOLYGON (((0 0, 0 1, 1 1, 1 0, 0 0)), ((1 1, 1 2, 2 2, 2 1, 1 1))),POLYGON ((2 0, 3 1, 4 0, 2 0)),POLYGON ((1 0, 1 1, 2 1, 2 0, 1 0)),POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0)),";
        assertEquals(expected, this.documentGeometryProcess.getGeometries());
    }

    @Test
    public void testGeometryToPolygonArray() throws Exception {
        WKTReader wktReader = new WKTReader();

        List<Polygon> expected = new ArrayList<Polygon>();
        expected.add(
            (Polygon) wktReader.read(
                "POLYGON ((1 0, 0 1, 1 1, 1 0))"
            )
        );
        expected.add(
            (Polygon) wktReader.read(
                "POLYGON ((11 10, 10 11, 11 11, 11 10))"
            )
        ); // For some reason fixing Geometry rotates anticlockwise
        expected.add(
            (Polygon) wktReader.read(
                "POLYGON ((20 21, 21 21, 21 20, 20 21))"
            )
        );

        Geometry testGeometry = wktReader.read(
            "GEOMETRYCOLLECTION(MULTIPOLYGON(((0 1, 1 1, 1 0, 0 1)), ((10 11, 11 11, 11 10, 10 11))), POLYGON ((20 21, 21 21, 21 20, 20 21)))"
        );
        List<Polygon> polygons = new ArrayList<Polygon>();

        DocumentGeometryProcess.geometryToPolygonArray(testGeometry, polygons);
        assertEquals(expected, polygons);
    }

    @Test
    public void testGeometryToMultiPolygon() throws Exception {
        WKTReader wktReader = new WKTReader();

        Geometry expected = wktReader.read(
            "MULTIPOLYGON(((1 0, 0 1, 1 1, 1 0)), ((11 10, 10 11, 11 11, 11 10)), ((20 21, 21 21, 21 20, 20 21)))"
        );
        Geometry testGeometry = wktReader.read(
            "GEOMETRYCOLLECTION(MULTIPOLYGON(((0 1, 1 1, 1 0, 0 1)), ((10 11, 11 11, 11 10, 10 11))), POLYGON ((20 21, 21 21, 21 20, 20 21)))"
        );

        assertEquals(expected, DocumentGeometryProcess.geometryToMultiPolygon(testGeometry));
    }
}