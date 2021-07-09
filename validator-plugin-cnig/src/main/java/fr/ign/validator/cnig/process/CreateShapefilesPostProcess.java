package fr.ign.validator.cnig.process;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.tools.VRT;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.tools.FileConverter;

/**
 * 
 * Effectue une conversion en SHP des CSV présents dans le dossier validation.
 * Converts csv files (in validation directory) to shp
 * 
 * @author MBorne
 *
 */
public class CreateShapefilesPostProcess implements ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("CreateShapefilesPostProcess");

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        // nothing to do
    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
        // nothing to do
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        File dataDirectory = context.getDataDirectory();
        log.info(MARKER, "Convert normalized CSV files to shapefile ...");

        FileConverter fileConverter = FileConverter.getInstance();

        String[] extensions = {
            "csv"
        };
        Collection<File> csvFiles = FileUtils.listFiles(dataDirectory, extensions, true);
        for (File csvFile : csvFiles) {
            // get FeatureType
            String typeName = FilenameUtils.getBaseName(csvFile.getName());
            FileModel fileModel = context.getDocumentModel().getFileModelByName(typeName);
            FeatureType featureType = fileModel.getFeatureType();

            // create vrt file
            File vrtFile = new File(csvFile.getParent(), FilenameUtils.getBaseName(csvFile.getName()) + ".vrt");
            VRT.createFile(csvFile, featureType);

            // create shapefile from vrt
            String shpExtension = featureType.isSpatial() ? "shp" : "dbf";
            File shpFile = new File(
                csvFile.getParent(), FilenameUtils.getBaseName(csvFile.getName()) + "." + shpExtension
            );
            fileConverter.convertToShapefile(vrtFile, shpFile);
        }

        log.info(MARKER, "Convert normalized CSV files to shapefile : completed.");
    }

}
