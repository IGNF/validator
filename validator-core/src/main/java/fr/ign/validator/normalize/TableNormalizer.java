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
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.geometry.GeometryTransform;
import fr.ign.validator.geometry.NullTransform;
import fr.ign.validator.geometry.ProjectionTransform;
import fr.ign.validator.geometry.internal.FastWktWriter;
import fr.ign.validator.info.model.TableStats;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.tools.EnvelopeUtils;
import fr.ign.validator.tools.HeaderHelper;
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
     * Normalized file
     */
    private File targetFile;

    /**
     * Stats of the normalized file (same as reading it with TableReader, see
     * DocumentInfoExtractor)
     */
    private TableStats stats = new TableStats();

    /**
     * False if the stats can't be ensured (the file will be read to get them)
     */
    private boolean statsValid = true;

    /**
     * Position of the column "WKT" used for the bounding box (-1 if not found)
     */
    private int wktIndex = -1;

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
        this.targetFile = targetFile;
        initStats(outputHeader);
        log.info(MARKER, "Create CSV file {}...", targetFile);
        printer = new CSVPrinter(
            new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8)
            ), CSVFormat.RFC4180
        );
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
        try (TableReader reader = TableReader.createTableReader(csvFile, context.getEncoding())) {
            append(csvFile, reader);
        } catch (IOException | RuntimeException e) {
            statsValid = false;
            throw e;
        }
    }

    /**
     * Stats are only computed for simple headers (otherwise, TableReader may skip
     * columns or lines)
     *
     * @param outputHeader
     */
    private void initStats(List<String> outputHeader) {
        if (outputHeader.size() < 2) {
            statsValid = false;
            return;
        }
        for (String name : outputHeader) {
            if (name == null || name.trim().isEmpty() || !name.equals(name.trim())) {
                statsValid = false;
                return;
            }
        }
        wktIndex = HeaderHelper.findColumn(outputHeader.toArray(new String[outputHeader.size()]), "WKT");
    }

    /**
     * Update stats with a written row (same result as DocumentInfoExtractor reading
     * the normalized file)
     *
     * @param outputRow
     * @param wktGeometry  geometry of the column WKT
     * @param wktFormatted formatted value of the geometry before the StringFixer
     */
    private void updateStats(String[] outputRow, Geometry wktGeometry, String wktFormatted) {
        if (!statsValid) {
            return;
        }
        stats.incrementTotalFeatures();
        if (wktIndex < 0 || outputRow[wktIndex] == null) {
            return;
        }
        String value = outputRow[wktIndex];
        String wkt = value.trim();
        if (wkt.isEmpty()) {
            return;
        }
        Envelope envelope = null;
        if (wktGeometry != null && value.equals(wktFormatted) && wkt.length() == value.length()) {
            envelope = FastWktWriter.getWrittenEnvelope(wktGeometry);
        }
        if (envelope == null) {
            try {
                envelope = EnvelopeUtils.getEnvelope(wkt);
            } catch (RuntimeException e) {
                statsValid = false;
                return;
            }
        }
        stats.getBoundingBox().expandToInclude(envelope);
    }

    private void append(File csvFile, TableReader reader) throws IOException {
        /*
         * writing each feature
         */
        String[] inputHeader = reader.getHeader();
        while (reader.hasNext()) {
            String[] inputRow = reader.next();
            String[] outputRow = new String[featureType.getAttributeCount()];
            Geometry wktGeometry = null;
            String wktFormatted = null;
            if (!reader.isLastRowValid()) {
                context.report(
                    context.createError(CoreErrorCodes.TABLE_INVALID_ROW)
                        .setMessageParam("FILEPATH", csvFile.getPath())
                );
                continue;
            }
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
                if (position == wktIndex) {
                    // the last value written in the column is kept
                    wktGeometry = bindedValue instanceof Geometry ? (Geometry) bindedValue : null;
                    wktFormatted = outputValue;
                }
                // apply string fixer to remove bad chars.
                outputValue = context.getStringFixer().transform(outputValue);
                outputRow[position] = outputValue;
            }
            printer.printRecord((Object[]) outputRow);
            updateStats(outputRow, wktGeometry, wktFormatted);
        }
    }

    @Override
    public void close() {
        try {
            printer.close();
        } catch (IOException e) {
            log.error(MARKER, "fail to close CSV printer!");
            statsValid = false;
        }
        if (statsValid) {
            context.getTableStatsCache().put(targetFile, stats);
        }
    }

}
