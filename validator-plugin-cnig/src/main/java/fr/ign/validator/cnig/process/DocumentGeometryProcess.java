package fr.ign.validator.cnig.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.GeometryFixer;
import org.locationtech.jts.operation.union.UnaryUnionOp;
import org.locationtech.jts.simplify.TopologyPreservingSimplifier;

import fr.ign.validator.cnig.tools.CSV;

/**
 *
 * Process for command DocumentGeometryCommand
 *
 * @author DDarras
 *
 */
public class DocumentGeometryProcess {

    public static final Logger log = LogManager.getRootLogger();

    /**
     * Tolerance to fix small holes
     */
    private static final double tolerance = 0.00005;

    /**
     * Files to process
     */
    private List<File> inputFiles;

    /**
     * Geometry columns names
     */
    private List<String> geometryColumnNames;

    /**
     * Geometries detected to merge
     */
    private List<Geometry> geometries = new ArrayList<Geometry>();

    /**
     * Constructor for processing class
     * @param inputFiles
     * @param geometryColumnNames
     */
    public DocumentGeometryProcess(List<File> inputFiles, List<String> geometryColumnNames){
        this.inputFiles = inputFiles;
        this.geometryColumnNames = geometryColumnNames;
    }

    /**
     * Populates geometry list
     */
    public void detectGeometries() throws IOException{
        for(File inputFile : this.inputFiles){
            // Obtention des géométries
            int geometryColumn = CSV.getGeometryColumn(inputFile, this.geometryColumnNames);
            List<Geometry> csvGeometries = CSV.getGeometriesFromFile(inputFile, geometryColumn);

            // Attempting to fix broken geometries
            for (Geometry geometry : csvGeometries){
                this.geometries.add(GeometryFixer.fix(geometry));
            }
        }
    }

    /**
     * Merges geometries
     * @return WKT of union
     */
    public String union(){
        // filling micro holes according to tolerance
        List<Geometry> bigBuffers = new ArrayList<>();
        for (Geometry geometry : this.geometries){
            bigBuffers.add(geometry.buffer(tolerance));
        }
        Geometry bigUnion = UnaryUnionOp.union(bigBuffers);
        Geometry smallBuffer = bigUnion.buffer(-tolerance);
        // is valid
        Geometry simplified = TopologyPreservingSimplifier.simplify(smallBuffer, tolerance);

        // homogenize to multipolygon
        MultiPolygon multiPolygon = geometryToMultiPolygon(simplified);

        // to WKT
        return multiPolygon.toText();
    }


    /**
     * Homogenize geometry to multipolygon
     *
     * @param geometry
     * @return
     */
    public static MultiPolygon geometryToMultiPolygon(Geometry geometry){
        GeometryFactory geometryFactory = new GeometryFactory();
        List<Polygon> polygons = new ArrayList<Polygon>();
        geometryToPolygonArray(geometry, polygons);
        return geometryFactory.createMultiPolygon((Polygon[]) polygons.toArray());
    }

    /**
     * Iterates over Collections to fill Polygons list
     *
     * @param geometry
     * @param polygons
     */
    public static void geometryToPolygonArray(Geometry geometry, List<Polygon> polygons){
        Geometry valid = GeometryFixer.fix(geometry);
        if (valid instanceof Polygon){
            polygons.add((Polygon) valid);
        } else if (valid instanceof MultiPolygon){
            for (int i = 0; i < valid.getNumGeometries(); i++) {
                Polygon polygon = (Polygon) valid.getGeometryN(i);
                polygons.add(polygon);
            }
        } else if (geometry instanceof GeometryCollection) {
            for (int i = 0; i < valid.getNumGeometries(); i++) {
                Geometry subGeometry = valid.getGeometryN(i);
                geometryToPolygonArray(subGeometry, polygons);
            }
        }
    }
}