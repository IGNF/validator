package fr.ign.validator.normalize;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.file.MultiTableFile;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.file.EmbeddedTableModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.model.file.MultiTableModel;
import fr.ign.validator.model.file.PdfModel;
import fr.ign.validator.model.file.SingleTableModel;
import fr.ign.validator.tools.MultiTableReader;

/**
 * Creates DATA and METADATA directories in the validation directory :
 * 
 * <ul>
 * <li>Tables are normalized according to FeatureType as
 * DATA/{fileModel.name}.csv</li>
 * <li>Tables are normalized according to FeatureType as
 * DATA/{fileModel.name}/{tableName}.csv</li>
 * <li>PDF are copied to DATA directory</li>
 * <li>Metadata are copied to METADATA directory</li>
 * <li>Directories are ignored</li>
 * </ul>
 * 
 * Note that DATA and METADATA corresponds to the structure of an EaaS delivery
 * (former geoportal datastore).
 * 
 * @author MBorne
 *
 */
public class DocumentNormalizer {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("NormalizePostProcess");

    /**
     * Normalize document files.
     * 
     * @param context
     */
    public void normalize(Context context, Document document) throws IOException {
        log.info(MARKER, "Normalize document data in {} ...", context.getDataDirectory());

        /*
         * Create a normalized CSV file for each FileModel.
         */
        List<FileModel> fileModels = document.getDocumentModel().getFileModels();
        for (FileModel fileModel : fileModels) {
            // Retrieve document files corresponding to the FileModel
            List<DocumentFile> documentFiles = document.getDocumentFilesByModel(fileModel);

            log.info(
                MARKER,
                "Normalize input data according to {} ({} file(s))...",
                fileModel,
                documentFiles.size()
            );
            if (fileModel instanceof SingleTableModel) {
                normalizeTable(context, (TableModel) fileModel, documentFiles);
            } else if (fileModel instanceof MultiTableModel) {
                normalizeMultiTable(context, (MultiTableModel) fileModel, documentFiles);
            } else if (fileModel instanceof PdfModel) {
                createFlatCopyInTargetDirectory(fileModel, documentFiles, context.getDataDirectory());
            } else if (fileModel instanceof MetadataModel) {
                createFlatCopyInTargetDirectory(fileModel, documentFiles, context.getMetadataDirectory());
            } else {
                log.error(MARKER, "Normalization is not supported for {}!", fileModel);
            }
            log.info(
                MARKER,
                "Normalize input data according to {} ({} file(s)) : completed",
                fileModel,
                documentFiles.size()
            );
        }

        log.info(MARKER, "Normalize document data in {} : completed", context.getDataDirectory());
    }

    /**
     * Convert documentFiles in a normalized DATA/{fileModel.name}.csv file.
     * 
     * @param context
     * @param fileModel
     * @param documentFiles
     * @throws IOException
     */
    private void normalizeTable(Context context, TableModel fileModel, List<DocumentFile> documentFiles)
        throws IOException {

        FeatureType featureType = fileModel.getFeatureType();
        if (featureType == null) {
            log.warn(MARKER, "Skip normalization for {} (no FeatureType provided)", fileModel);
            return;
        }

        File csvFile = new File(context.getDataDirectory(), fileModel.getName() + ".csv");
        TableNormalizer normalizer = new TableNormalizer(context, featureType, csvFile);
        try {
            for (DocumentFile documentFile : documentFiles) {
                normalizer.append(documentFile.getPath());
            }
        } finally {
            normalizer.close();
        }
    }

    /**
     * Convert documentFiles in a normalized DATA/{fileModel.name}.{tableName}.csv
     * file.
     * 
     * @param context
     * @param fileModel
     * @param documentFiles
     * @throws IOException
     */
    private void normalizeMultiTable(Context context, MultiTableModel fileModel, List<DocumentFile> documentFiles)
        throws IOException {
        if (documentFiles.size() != 1) {
            log.warn(
                MARKER, "Skip normalization for {} (found {} files, normalization supported only for one DocumentFile)",
                fileModel,
                documentFiles.size()
            );
            return;
        }

        MultiTableFile documentFile = (MultiTableFile) documentFiles.get(0);
        MultiTableReader reader = documentFile.getReader();
        for (String tableName : reader.getTableNames()) {
            /*
             * Retrieve source path for CSV converted table.
             */
            File sourceFile = reader.getTablePath(tableName);
            EmbeddedTableModel tableModel = fileModel.getTableModelByName(tableName);
            if (tableModel == null) {
                log.warn(
                    MARKER, "Skip table {} in '{}' (table model not found in {})",
                    tableName,
                    documentFile.getPath(),
                    fileModel
                );
                continue;
            }

            /*
             * Create normalized CSV file (note that using tableModel.name avoids renaming
             * type to low case)
             */
            File outputFile = new File(context.getDataDirectory(), tableModel.getName() + ".csv");
            TableNormalizer normalizer = null;
            try {
                normalizer = new TableNormalizer(
                    context,
                    tableModel.getFeatureType(),
                    outputFile
                );
                normalizer.append(sourceFile);
            } finally {
                if (normalizer != null) {
                    normalizer.close();
                }
            }

        }
    }

    /**
     * Copy files to targetDirectory without original hierarchy.
     * 
     * @param documentFiles
     * @param targetDirectory
     * @throws IOException
     */
    private void createFlatCopyInTargetDirectory(
        FileModel fileModel,
        List<DocumentFile> documentFiles,
        File targetDirectory) throws IOException {

        log.warn(MARKER, "{} - Copy {} files to {} ...", fileModel.getName(), fileModel.getType(), targetDirectory);
        for (DocumentFile documentFile : documentFiles) {
            File srcFile = documentFile.getPath();
            File destFile = new File(targetDirectory, srcFile.getName());
            log.info(MARKER, "Copy {} to {}...", srcFile, destFile);
            FileUtils.copyFile(srcFile, destFile);
        }
    }

}
