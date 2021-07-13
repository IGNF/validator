package fr.ign.validator.normalize;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.Context;
import fr.ign.validator.geometry.GeometryTransform;
import fr.ign.validator.geometry.NullTransform;
import fr.ign.validator.geometry.ProjectionTransform;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.tools.TableReader;

/**
 * 
 * Normalize table file producing a CSV according to FeatureType columns.
 * 
 * @author MBorne
 *
 */
public class TableNormalizer implements Closeable {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("TableNormalizer");

    /**
     * Context providing StringFixer
     */
    private Context context;

    /**
     * Target FeatureType
     */
    private FeatureType featureType;

    /**
     * Target projection
     */
    private GeometryTransform geometryTransform = new NullTransform();

    /**
     * CSV writer
     */
    private CSVPrinter printer;

    /**
     * Create table normalizer with a given FeatureType.
     * 
     * @param context
     * @param featureType
     * @param targetFile
     * @throws IOException
     */
    public TableNormalizer(Context context, FeatureType featureType, File targetFile) throws IOException {
        this.context = context;
        this.featureType = featureType;

        /*
         * Optionally create projection transform for normalized data
         */
        if (context.getProjection() != null && context.getOutputProjection() != null) {
            log.info(
                MARKER, "Configure projection transform from {} to {}",
                context.getProjection().getCode(),
                context.getOutputProjection().getCode()
            );
            this.geometryTransform = new ProjectionTransform(
                context.getProjection(),
                context.getOutputProjection()
            );
        } else {
            log.info(MARKER, "Projection transform is disabled");
        }

        /*
         * Create CSV file with an header given by the model.
         */
        List<String> outputHeader = featureType.getAttributeNames();
        log.info(MARKER, "Create CSV file {}...", targetFile);
        BufferedWriter fileWriter = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8)
        );
        printer = new CSVPrinter(fileWriter, CSVFormat.RFC4180);
        printer.printRecord(outputHeader);
    }

    /**
     * Append rows corresponding to a document file
     * 
     * @param documentFile
     * @throws IOException
     * @throws Exception
     */
    public void append(File csvFile) throws IOException {
        log.info(MARKER, "Append data from {}...", csvFile);
        TableReader reader = TableReader.createTableReader(
            csvFile,
            context.getEncoding()
        );

        /*
         * writing each feature
         */
        String[] inputHeader = reader.getHeader();
        while (reader.hasNext()) {
            String[] inputRow = reader.next();
            String[] outputRow = new String[featureType.getAttributeCount()];
            for (int i = 0; i < inputRow.length; i++) {
                int position = featureType.indexOf(inputHeader[i]);
                if (position < 0) {
                    continue;
                }
                /*
                 * bind value to the expected type
                 */
                AttributeType<?> attribute = featureType.getAttribute(position);
                Object bindedValue = null;
                try {
                    bindedValue = attribute.bind(inputRow[i]);
                    if (bindedValue instanceof Geometry) {
                        bindedValue = geometryTransform.transform((Geometry) bindedValue);
                    }
                } catch (IllegalArgumentException e) {
                    log.warn(
                        MARKER, "{}.{} : {} converted to null (bad type).",
                        inputRow[i],
                        featureType.getName(),
                        attribute.getName()
                    );
                }
                // format binded value to get normalized output.
                String outputValue = attribute.formatObject(bindedValue);
                // apply string fixer to remove bad chars.
                outputValue = context.getStringFixer().transform(outputValue);
                outputRow[position] = outputValue;
            }
            printer.printRecord(outputRow);
        }
    }

    @Override
    public void close() {
        try {
            printer.close();
        } catch (IOException e) {
            log.error(MARKER, "fail to close CSV printer!");
        }
    }

}
