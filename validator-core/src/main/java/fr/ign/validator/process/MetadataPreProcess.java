package fr.ign.validator.process;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.exception.InvalidMetadataException;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.code.CharacterSetCode;
import fr.ign.validator.metadata.gmd.MetadataISO19115;
import fr.ign.validator.model.file.MetadataModel;

/**
 * Pre-process extracting data charset from metadata file
 * 
 * Note that future version may also extract CRS (see
 * https://github.com/IGNF/validator/issues/40)
 * 
 * @author CBouche
 */
public class MetadataPreProcess implements ValidatorListener {
    public static final Logger log = LogManager.getRootLogger();
    private static final Marker MARKER = MarkerManager.getMarker("MetadataPreProcess");

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        // nothing to do
    }

    /**
     * Reading charset from first metadata file found
     */
    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
        log.info(MARKER, "Retreive validation parameters from metadata files...");
        /*
         * Find first metadata file
         */
        log.info(MARKER, "Locate metadata files...");
        List<DocumentFile> metadataFiles = document.getDocumentFiles(MetadataModel.class);

        /*
         * Stop process is metadata is not found.
         */
        if (metadataFiles.isEmpty()) {
            // note that an error is already reported if metadata is expected
            log.warn(MARKER, "Retrieve validation parameters from metadata files : completed (no metadata found)");
            return;
        }

        /*
         * Report error when multiple metadata files are provided for the dataset
         */
        if (metadataFiles.size() > 1) {
            log.warn(MARKER, "Found {} metadata files (read first one)!", metadataFiles.size());
            context
                .report(
                    context.createError(CoreErrorCodes.METADATA_MULTIPLE_FILES)
                        .setMessageParam("FILENAME_LIST", formatFiles(context, metadataFiles))
                );
        }

        /*
         * Read first file to retrieve the charset
         */
        File metadataFile = metadataFiles.get(0).getPath();
        log.info(MARKER, "Try to retrieve charset from {}...", metadataFile);
        Charset charset = readCharsetFromMetadata(metadataFile);
        if (null == charset) {
            log.warn(MARKER, "Charset not found in metadata files (keep {})", context.getEncoding());
        } else {
            log.info(MARKER, "Charset found : {}", context.getEncoding());
            context.setEncoding(charset);
        }

        log.info(MARKER, "Retrieve validation parameters from metadata files. : completed");
    }

    /**
     * Read charset from metadata (returns null if not found)
     * 
     * @param context
     * @param document
     * @return
     */
    private Charset readCharsetFromMetadata(File metadataFile) {
        try {
            Metadata reader = MetadataISO19115.readFile(metadataFile);
            CharacterSetCode characterSet = reader.getCharacterSet();
            if (characterSet == null) {
                log.info(
                    MARKER,
                    "characterSet is not defined in '{}'",
                    metadataFile
                );
                return null;
            }
            Charset dataEncoding = characterSet.getCharset();
            log.info(
                MARKER,
                "characterSet={} found in '{}'",
                characterSet.getValue(),
                metadataFile
            );
            return dataEncoding;
        } catch (InvalidMetadataException e) {
            log.error(
                MARKER,
                "fail to read metadata file : {}",
                e.getMessage()
            );
            return null;
        }
    }

    /**
     * Generates a concatenated list of filenames
     * 
     * @param context
     * @param matchingFiles
     * @return
     */
    private String formatFiles(Context context, List<DocumentFile> matchingFiles) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (DocumentFile documentFile : matchingFiles) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            sb.append(context.relativize(documentFile.getPath()));
        }
        return sb.toString();
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        // nothing to do
    }

}
