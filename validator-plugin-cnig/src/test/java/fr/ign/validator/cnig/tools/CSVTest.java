package fr.ign.validator.cnig.tools;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.tools.ResourceHelper;

public class CSVTest {

    @Test
    public void testCountRows() throws IOException {
        File csvFile = ResourceHelper.getResourceFile(getClass(), "/csv/DOC_URBA.csv");
        int numRows = CSV.countRows(csvFile);
        assertEquals(844, numRows);
    }

    @Test
    public void testGetGeometryColumn() throws IOException {
        File csvFile = ResourceHelper.getResourceFile(getClass(), "/geometry/fair_geometries.csv");
        List<String> geometries = new ArrayList<String>();

        //no corresponding column
        geometries.add("wrong_column");
        assertEquals(-1, CSV.getGeometryColumn(csvFile, geometries));

        // Geometry column detection
        geometries.add("geom");
        assertEquals(1, CSV.getGeometryColumn(csvFile, geometries));
    }

    @Test
    public void testGetGeometriesFromFile() throws IOException {
        File fairFile = ResourceHelper.getResourceFile(getClass(), "/geometry/fair_geometries.csv");
        List<String> geometries = new ArrayList<String>();
        geometries.add("geom");

        //column index out of bounds
        IOException OOBException = assertThrows(IOException.class,
            () -> CSV.getGeometriesFromFile(fairFile, -1));
        assertEquals(fairFile.getAbsolutePath() + ": Geometry column cant be found. Please check arguments.", OOBException.getMessage());

        //correct column index
        String[] fairWKTExpected = {
            "POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))",
            "POLYGON ((1 0, 1 1, 2 1, 2 0, 1 0))",
            "POLYGON ((2 0, 3 1, 4 0, 2 0))"
        };
        List<Geometry> fairGeometries = CSV.getGeometriesFromFile(fairFile, 1);
        List<String> fairActual = new ArrayList<String>();
        for (Geometry geometry : fairGeometries){
            fairActual.add(geometry.toText());
        }
        assertArrayEquals(fairWKTExpected, fairActual.toArray());

        //dubious geometries
        File dubiousFile = ResourceHelper.getResourceFile(getClass(), "/geometry/dubious_geometries.csv");
        String[] dubiousWKTExpected = {
            "POLYGON ((0 0, 0 1, 2 1, 2 2, 1 2, 1 0, 0 0))", //self intersection / butterfly
            "POLYGON ((0 0, 0 1, 0 2, 0 0))", //polygon -> linee
            "POLYGON ((0 0, 0 0, 0 0, 0 0))" //polygon -> point
        };
        List<Geometry> dubiousGeometries = CSV.getGeometriesFromFile(dubiousFile, 1);
        List<String> dubiousActual = new ArrayList<String>();
        for (Geometry geometry : dubiousGeometries){
            dubiousActual.add(geometry.toText());
        }
        assertArrayEquals(dubiousWKTExpected, dubiousActual.toArray());

        //non-parsable geometries
        File brokenFile = ResourceHelper.getResourceFile(getClass(), "/geometry/broken_geometries.csv");
        String[] brokenFileWKTExpected = {
            "POLYGON ((0 0, 0 1, 2 1, 2 2, 1 2, 1 0, 0 0))", //non-closed ring
            "POLYGON non wkt" //non-parsable
        };
        IOException brokenException = assertThrows(IOException.class,
            () -> CSV.getGeometriesFromFile(brokenFile, 1));
        assertEquals(brokenFile.getAbsolutePath() + ": Parsed geometry is empty or invalid", brokenException.getMessage());
    }
}
