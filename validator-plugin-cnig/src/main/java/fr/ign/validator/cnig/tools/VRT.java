package fr.ign.validator.cnig.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.tools.TableReader;

/**
 * Utility class that creates a VRT file to go with WKT files
 *
 * @author MBorne
 *
 */
public class VRT {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker VRT = MarkerManager.getMarker("VRT");

    private static final String EOL = "\n";

    /**
     * Creates a vrt corresponding to a sourceFile for a given FeatureType
     *
     * @param csvFile
     * @param featureType
     * @return
     */
    public static File createFile(File csvFile, FeatureType featureType) {
        try {
            File vrtFile = createVRTfile(csvFile, featureType);
            return vrtFile;
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * #FIX genetares vrt files compatible with ogr2ogr 1.9
     *
     * @param csvFile
     * @param featureType
     * @return
     * @throws IOException
     */
    public static File createVRTfile(File csvFile, FeatureType featureType) throws Exception {

        File vrtFile = new File(csvFile.getParent(), FilenameUtils.getBaseName(csvFile.getName()) + ".vrt");

        log.info(
            VRT, "Create VRT '{}' for '{}'...",
            vrtFile,
            csvFile
        );

        FileWriter writer = new FileWriter(vrtFile);

        writer.append("<OGRVRTDataSource>");
        writer.append(EOL);
        writer.append(" <OGRVRTLayer name='" + FilenameUtils.getBaseName(csvFile.getName()) + "'>");
        writer.append(EOL);
        writer.append("  <SrcDataSource relativeToVRT=\"1\">" + csvFile.getName() + "</SrcDataSource>");
        writer.append(EOL);

        AttributeType<?> geometryAttribute = featureType.getAttribute("WKT");
        if (null != geometryAttribute) {
            writer.append("  <GeometryType>" + "wkb" + geometryAttribute.getTypeName() + "</GeometryType>");
            writer.append(EOL);
        }

        /*
         * Each <Field> except WKT
         *
         * Note : this step is mandatory so that ogr2ogr doesn't truncate the text
         * fields to 80 characters when converting in shapefile
         *
         */
        List<String> fieldNames = getFieldNamesFromCSV(csvFile);
        for (String fieldName : fieldNames) {
            writer.append("  <Field name=\"" + fieldName + "\" type=\"String\" width=\"254\" />");
            writer.append(EOL);
        }

        writer.append(" </OGRVRTLayer>");
        writer.append(EOL);
        writer.append("</OGRVRTDataSource>");
        writer.append(EOL);

        writer.flush();
        writer.close();

        return vrtFile;
    }

    /**
     * Gets the list of fields in csv file (except for geometry)
     *
     * @param csvFile
     * @return
     * @throws IOException
     */
    private static List<String> getFieldNamesFromCSV(File csvFile) throws Exception {
        List<String> result = new ArrayList<String>();
        TableReader reader = TableReader.createTableReader(csvFile, StandardCharsets.UTF_8);
        String[] header = reader.getHeader();
        for (String name : header) {
            if (name.equals("WKT")) {
                continue;
            }
            result.add(name);
        }
        return result;
    }

}
