package fr.ign.validator.process;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.file.MultiTableFile;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.EmbeddedTableModel;
import fr.ign.validator.model.file.MultiTableModel;
import fr.ign.validator.model.file.SingleTableModel;
import fr.ign.validator.tools.AutoFeatureType;
import fr.ign.validator.tools.MultiTableReader;

/**
 * Ensure that FeatureType are defined for each {@link SingleTableModel} and
 * {@link EmbeddedTableModel} in {@link MultiTableModel}.
 *
 * If not, a FeatureType is determined from data in order to allow table
 * validation of some aspects (geometry,...) without providing a complete model.
 *
 * @author MBorne
 *
 */
public class CheckFeatureTypesPreProcess implements ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    private static final Marker MARKER = MarkerManager.getMarker("CheckFeatureTypesPreProcess");

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        // nothing to (data required)
    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
        process(context, document, document.getDocumentModel());
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        // nothing to (data required)
    }

    /**
     * Process DocumentModel components.
     *
     * @param document
     * @throws IOException
     */
    private void process(Context context, Document document, DocumentModel documentModel) throws IOException {
        log.info(MARKER, "Process {}...", documentModel);
        context.beginModel(documentModel);
        for (FileModel fileModel : documentModel.getFileModels()) {
            if (fileModel instanceof SingleTableModel) {
                process(context, document, (SingleTableModel) fileModel);
            } else if (fileModel instanceof MultiTableModel) {
                process(context, document, (MultiTableModel) fileModel);
            }
        }
        context.endModel(documentModel);
        log.info(MARKER, "Process {} : completed", documentModel);
    }

    /**
     * Process FeatureType associated to TableModel.
     *
     * @param context
     * @param document
     * @param tableModel
     * @throws IOException
     */
    private void process(Context context, Document document, SingleTableModel tableModel) throws IOException {
        log.info(MARKER, "Process {}...", tableModel);

        if (tableModel.getFeatureType() != null) {
            log.info(
                MARKER, "Process {} : FeatureType is already defined.",
                tableModel
            );
            return;
        }

        DocumentFile documentFile = getFirstDocumentFile(document, tableModel);
        if (documentFile == null) {
            log.warn(
                MARKER,
                "Process {} : No DocumentFile found to detect FeatureType, create empty FeatureType.",
                tableModel
            );
            tableModel.setFeatureType(createEmptyFeatureType(tableModel.getName()));
            return;
        }

        FeatureType featureType = AutoFeatureType.createFeatureTypeFromTable(documentFile.getPath());
        featureType.setName(tableModel.getName());
        log.info(
            MARKER, "Process {} : FeatureType detected from {}",
            tableModel,
            documentFile.getPath()
        );
    }

    private void process(Context context, Document document, MultiTableModel multiTableModel) throws IOException {
        log.info(MARKER, "Process {}...", multiTableModel);
        if (haveAllFeatureFeatureTypeDefined(multiTableModel)) {
            log.info(
                MARKER, "Process {} : completed (all FeatureType are already defined).",
                multiTableModel
            );
            return;
        }

        DocumentFile documentFile = getFirstDocumentFile(document, multiTableModel);

        /*
         * create empty FeatureType when no DocumentFile is available.
         */
        if (documentFile == null) {
            for (EmbeddedTableModel tableModel : multiTableModel.getTableModels()) {
                if (tableModel.getFeatureType() != null) {
                    continue;
                }
                log.warn(
                    MARKER,
                    "Process {} : No DocumentFile found to detect FeatureType, create empty FeatureType for {}",
                    multiTableModel,
                    tableModel
                );
                tableModel.setFeatureType(createEmptyFeatureType(tableModel.getName()));
            }
            return;
        }

        log.info(
            MARKER,
            "Process {} : Create missing FeatureTypes from {}...",
            multiTableModel,
            documentFile.getPath()
        );
        MultiTableReader reader = ((MultiTableFile) documentFile).getReader();
        for (EmbeddedTableModel tableModel : multiTableModel.getTableModels()) {
            if (tableModel.getFeatureType() != null) {
                continue;
            }
            String tableName = tableModel.getName();
            try {
                File path = reader.getTablePath(tableName);
                FeatureType featureType = AutoFeatureType.createFeatureTypeFromTable(path);
                featureType.setName(tableModel.getName());
                log.info(
                    MARKER, "Process {} : FeatureType create for {} from {}",
                    tableModel,
                    tableModel.getName(),
                    documentFile.getPath()
                );
                tableModel.setFeatureType(featureType);
            } catch (IOException e) {
                log.warn(
                    MARKER, "Process {} : Create empty FeatureType for {} ('{}' not found)",
                    tableModel,
                    tableModel.getName(),
                    tableName
                );
                tableModel.setFeatureType(createEmptyFeatureType(tableModel.getName()));
            }
        }
    }

    /**
     * Get first {@link DocumentFile} matched to {@link FileModel}
     *
     * @param document
     * @param fileModel
     * @return
     */
    private DocumentFile getFirstDocumentFile(Document document, FileModel fileModel) {
        List<DocumentFile> documentFiles = document.getDocumentFilesByModel(fileModel);
        if (documentFiles.isEmpty()) {
            return null;
        }
        if (documentFiles.size() > 1) {
            log.warn(
                MARKER,
                "Process {} : found {} document files, using first one to detect FeatureType",
                fileModel,
                documentFiles.size()
            );
        }
        return documentFiles.get(0);
    }

    /**
     * False if {@link MultiTableModel} as at least one missing {@link FeatureType}
     * definition.
     *
     * @param multiTableModel
     * @return
     */
    private boolean haveAllFeatureFeatureTypeDefined(MultiTableModel multiTableModel) {
        for (EmbeddedTableModel tableModel : multiTableModel.getTableModels()) {
            if (tableModel.getFeatureType() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Create empty FeatureType to avoid risk of crash during runtime.
     *
     * @param name
     * @return
     */
    private FeatureType createEmptyFeatureType(String name) {
        FeatureType featureType = new FeatureType();
        featureType.setName(name);
        return featureType;
    }

}
