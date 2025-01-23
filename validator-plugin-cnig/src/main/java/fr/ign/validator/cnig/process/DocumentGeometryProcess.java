package fr.ign.validator.cnig.process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.cnig.geometry.GeometryHelpers;
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
    public void detectGeometries(){
        for(File inputFile : this.inputFiles){
            // Obtention des géométries
            int geometryColumn = CSV.getGeometryColumn(inputFile, this.geometryColumnNames);
            List<Geometry> csvGeometries = CSV.getGeometriesFromFile(inputFile, geometryColumn);

            // Attempting to fix broken geometries
            for (Geometry geometry : csvGeometries){
                if (GeometryHelpers.validateGeometry(geometry)){
                    log.error("Geometry cannot be fixed : ", geometry.toText());
                } else {
                    this.geometries.add(geometry);
                }
            }
        }
    }

    /**
     * Merges geometries
     * @return WKT of union
     */
    public String union(){

    }
        //
        //union_geometries
            //valid geom
        //ecriture WKT
        //dans gpu-site, lancement commande + lecture WKT
    }
}