package fr.ign.validator.cnig.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
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
    private List<Geometry> geometries;

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
                this.geometries.add(validateGeometry(geometry));
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
        GeometryFactory geometryFactory = new GeometryFactory();
        MultiPolygon multiPolygon;
        if (simplified instanceof Polygon){
            Polygon[] polys = new Polygon[1];
            polys[0] = (Polygon) simplified;
            multiPolygon = geometryFactory.createMultiPolygon(polys);
        } else {
            multiPolygon = (MultiPolygon) simplified;
        }

        // to WKT
        return multiPolygon.toText();
    }
        //ecriture WKT
        //dans gpu-site, lancement commande + lecture WKT

        /**
         * Attemmpts to fix invalid geometries.
         * @param geometry
         * @return
         */
        public static Geometry validateGeometry(Geometry geometry){
            if (! geometry.isValid()){
                // Alternative to ST_MakeValid
                Geometry fixedGeometry = geometry.buffer(0);
                if (! fixedGeometry.isValid()){
                    log.error("Geometry cannot be fixed : ", geometry.toText());
                }
                return fixedGeometry;
            } else {
                return geometry;
            }
        }
}