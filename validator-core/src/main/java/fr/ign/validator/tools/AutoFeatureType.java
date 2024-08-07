package fr.ign.validator.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.model.type.StringType;

/**
 * Dirty way to create minimal {@link FeatureType} reading data from table in
 * order to allow geometry validation when no model is provided for data.
 *
 * @author MBorne
 *
 */
public class AutoFeatureType {

    public static final Logger log = LogManager.getRootLogger();
    private static final Marker MARKER = MarkerManager.getMarker("AutoFeatureType");

    private AutoFeatureType() {
        // helper class grouping static helpers
    }

    /**
     * Create a minimal {@link FeatureType} reading data from a CSV generated by
     * ogr2ogr :
     * <ul>
     * <li>WKT column is defined as {@link GeometryType}</li>
     * <li>other fields are defined as {@link StringType}</li>
     * </ul>
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static FeatureType createFeatureTypeFromTable(File path) throws IOException {
        log.info(MARKER, "Create FeatureType reading data from {}", path);
        FeatureType result = new FeatureType();
        result.setName(FilenameUtils.getBaseName(path.getName()));

        TableReader reader = TableReader.createTableReader(path, StandardCharsets.UTF_8);
        for (String attributeName : reader.getHeader()) {
            AttributeType<?> attribute = attributeName.equalsIgnoreCase("WKT") ? new GeometryType() : new StringType();
            attribute.setName(attributeName);
            if (attributeName.equalsIgnoreCase("gml_id") || attributeName.equalsIgnoreCase("id")) {
                attribute.getConstraints().setRequired(true);
                attribute.getConstraints().setUnique(true);
            } else {
                attribute.getConstraints().setRequired(false);
                attribute.getConstraints().setUnique(false);
            }
            result.addAttribute(attribute);
        }

        return result;
    }

}
