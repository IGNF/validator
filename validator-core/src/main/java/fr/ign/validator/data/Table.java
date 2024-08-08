package fr.ign.validator.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.file.MultiTableFile;
import fr.ign.validator.data.file.SingleTableFile;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.tools.TableReader;
import fr.ign.validator.validation.Validatable;

/**
 * A Table from a {@link SingleTableFile} or a {@link MultiTableFile}
 *
 * @author MBorne
 */
public class Table implements Validatable {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("DocumentFile");

    /**
     * Expected model for the table.
     */
    private FeatureType featureType;

    /**
     * Source file to read table data.
     */
    private TableReader reader;

    /**
     * Path relative to the root of the document, suffixed with an hash for
     * MultiTableFile, in order to report errors.
     */
    private String relativePath;

    public Table(FeatureType featureType, TableReader reader, String relativePath) {
        this.featureType = featureType;
        this.reader = reader;
        this.relativePath = relativePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public void validate(Context context) {
        context.beginData(this);
        doValidate(context);
        context.endData(this);
    }

    public void doValidate(Context context) {
        log.info(MARKER, "Validate '{}' according to {}...", relativePath, featureType);
        if (!reader.isCharsetValid()) {
            log.error(MARKER, "Invalid charset '{}' for '{}'", context.getEncoding(), relativePath);
            context.report(
                context.createError(CoreErrorCodes.TABLE_UNEXPECTED_ENCODING)
                    .setMessageParam("ENCODING", context.getEncoding().toString())
            );
        }

        /*
         * header validation
         */
        String[] columns = reader.getHeader();
        FeatureTypeMapper mapping = new FeatureTypeMapper(columns, featureType);
        Header header = new Header(relativePath, mapping);
        header.validate(context);

        /*
         * feature validation
         */
        int count = 0;
        while (reader.hasNext()) {
            count++;

            Row row = new Row(count, reader.next(), mapping);
            row.validate(context);
            if (count % 10000 == 0) {
                log.debug(MARKER, "'{}' : {} rows processed...", relativePath, count);
            }
        }

        /*
         * check for empty file
         */
        if (count == 0) {
            context.report(
                context.createError(CoreErrorCodes.FILE_EMPTY)
                    .setMessageParam("FILEPATH", relativePath)
            );
        }

        log.info(MARKER, "'{}' : {} row(s) processed", relativePath, count);

    }

}
