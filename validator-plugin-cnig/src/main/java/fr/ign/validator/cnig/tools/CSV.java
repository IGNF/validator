package fr.ign.validator.cnig.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.tools.TableReader;
import fr.ign.validator.geometry.GeometryReader;


public class CSV {

    /**
     * Count rows in a CSV file.
     *
     * @param csvFile
     * @return
     * @throws IOException
     */
    public static int countRows(File csvFile) throws IOException {
        TableReader reader = TableReader.createTableReader(csvFile, StandardCharsets.UTF_8);
        int numRows = 0;
        while (reader.hasNext()) {
            numRows++;
            reader.next();
        }
        return numRows;
    }

    /**
     * Get the index of the most probable geometry column
     *
     * @param csvFile
     * @param geometryColumnNames
     * @return
     * @throws IOException
     */
    public static int getGeometryColumn(File csvFile, List<String> geometryColumnNames) throws IOException {
        TableReader reader = TableReader.createTableReader(csvFile, StandardCharsets.UTF_8);
        String[] header = reader.getHeader();
        for (String geometryColumnName : geometryColumnNames){
            if (Arrays.asList(header).contains(geometryColumnName)){
                return reader.findColumn(geometryColumnName);
            }
        }
        return -1;
    }

    public static List<Geometry> getGeometriesFromFile(File csvFile, int geometryColumn) throws IOException {
        if (geometryColumn == -1){
            throw new IOException(csvFile.getAbsolutePath() + ": Geometry column cant be found. Please check arguments.");
        }
        TableReader tableReader = TableReader.createTableReader(csvFile, StandardCharsets.UTF_8);
        GeometryReader geometryReader = new GeometryReader();
        List<Geometry> geometries = new ArrayList<Geometry>();

        try {
            while (tableReader.hasNext()) {
                String wkt = tableReader.next()[geometryColumn];
                Geometry geometry = geometryReader.read(wkt);
                geometries.add(geometry);
            }
        } catch (org.locationtech.jts.io.ParseException e) {
            throw new IOException(csvFile.getAbsolutePath() + ": Parsed geometry is empty or invalid");
        }

        return geometries;
    }

}
