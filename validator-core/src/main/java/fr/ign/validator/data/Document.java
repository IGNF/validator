package fr.ign.validator.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.database.Database;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.tools.FileUtils;
import fr.ign.validator.validation.Validatable;
import fr.ign.validator.validation.Validator;

/**
 * Document materialized as a directory (documentPath) associated to a
 * DocumentModel (documentModel)
 *
 * @author MBorne
 *
 */
public class Document implements Validatable {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("Document");

    /**
     * Allowed file extensions used to perform file listing
     */
    private String[] allowedExtensions = {
        "dbf", "DBF", "tab", "TAB", "pdf", "PDF", "xml", "XML", "gml", "GML", "csv", "CSV", "gpkg", "GPKG"
    };

    /**
     * The document model
     */
    private DocumentModel documentModel;

    /**
     * Document path (root directory for validation)
     */
    private File documentPath;

    /**
     * Files related to Document (defined after matching step)
     */
    private List<DocumentFile> documentFiles = new ArrayList<>();

    /**
     * Additional informations
     */
    private Map<String, String> tags = new HashMap<>();

    /**
     *
     * @param documentModel
     * @param path
     */
    public Document(DocumentModel documentModel, File documentPath) {
        this.documentModel = documentModel;
        this.documentPath = documentPath;
    }

    /**
     * @return the documentModel
     */
    public DocumentModel getDocumentModel() {
        return documentModel;
    }

    /**
     * @return the documentPath
     */
    public File getDocumentPath() {
        return documentPath;
    }

    /**
     * documentName calculated from directory name
     *
     * @return
     */
    public String getDocumentName() {
        return documentPath.getName();
    }

    /**
     * @return the documentFiles
     */
    public List<DocumentFile> getDocumentFiles() {
        return documentFiles;
    }

    /**
     * Retrieves documentFiles by FileModel type
     *
     * Example : document.getDocumentFiles(MetadataFile.class)
     *
     * @param type
     * @return
     */
    public <T extends FileModel> List<DocumentFile> getDocumentFiles(Class<T> type) {
        List<DocumentFile> result = new ArrayList<>();
        for (DocumentFile documentFile : documentFiles) {
            if (type.isAssignableFrom(documentFile.getFileModel().getClass())) {
                result.add(documentFile);
            }
        }
        return result;
    }

    /**
     * @param documentFiles the documentFiles to set
     */
    public void removeDocumentFile(DocumentFile documentFile) {
        this.documentFiles.remove(documentFile);
    }

    /**
     * Retrieve documentFiles corresponding to a model
     *
     * @param fileModel
     * @return
     */
    public List<DocumentFile> getDocumentFilesByModel(FileModel fileModel) {
        List<DocumentFile> result = new ArrayList<>();
        for (DocumentFile documentFile : documentFiles) {
            if (documentFile.getFileModel() == fileModel) {
                result.add(documentFile);
            }
        }
        return result;
    }

    @Override
    public void validate(Context context) throws Exception {
        log.info(
            MARKER, "Validate '{}' according to {}...",
            documentPath,
            documentModel
        );

        context.setCurrentDirectory(documentPath);

        context.beginModel(documentModel);
        context.beginData(this);

        /*
         * calculations before matching step
         */
        triggerBeforeMatching(context);

        /*
         * matching files with model
         */
        findDocumentFiles(context);

        /*
         * executing process before validation
         */
        triggerBeforeValidate(context);

        /*
         * Validation at document level
         */
        for (Validator<Document> validator : documentModel.getValidators()) {
            validator.validate(context, this);
        }

        /*
         * Validation at file level
         */
        for (DocumentFile documentFile : documentFiles) {
            documentFile.validate(context);
        }

        /*
         * Validation at database level (unicity, references,...)
         */
        runDatabaseValidators(context);

        /*
         * executing process after validation
         */
        triggerAfterValidate(context);

        context.endModel(documentModel);
        context.endData(this);
    }

    /**
     * Create a validation Database for the Document and apply validators
     *
     * @param context
     */
    private void runDatabaseValidators(Context context) {
        try {
            log.info(MARKER, "Create validation Database...");
            Database database = Database.createDatabase(context, true);
            database.createTables(getDocumentModel());
            database.createIndexes(getDocumentModel());
            database.load(context, this);

            log.info(MARKER, "Validate document using database validators...");
            for (Validator<Database> validator : context.getDocumentModel().getDatabaseValidators()) {
                validator.validate(context, database);
            }

            database.close();
        } catch (Exception e) {
            log.error(MARKER, "Fail to create validation database");
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates event indicating file matching is starting
     *
     * @param context
     * @throws Exception
     */
    protected void triggerBeforeMatching(Context context) throws Exception {
        log.info(MARKER, "Run 'beforeMatching' pre-processes...");
        for (ValidatorListener validatorListener : context.getValidatorListeners()) {
            validatorListener.beforeMatching(context, this);
        }
        log.info(MARKER, "Run 'BeforeMatching' pre-processes : completed");
    }

    /**
     * Generates event indicating validation is starting
     *
     * @param context
     * @throws Exception
     */
    protected void triggerBeforeValidate(Context context) throws Exception {
        log.info(MARKER, "Run 'beforeValidate' pre-processes...");
        for (ValidatorListener validatorListener : context.getValidatorListeners()) {
            validatorListener.beforeValidate(context, this);
        }
        log.info(MARKER, "Run 'beforeValidate' pre-processes : completed");
    }

    /**
     * Generates event indicating validation is done
     *
     * @param context
     * @throws Exception
     */
    protected void triggerAfterValidate(Context context) throws Exception {
        log.info(MARKER, "Run 'afterValidate' pre-processes...");
        for (ValidatorListener validatorListener : context.getValidatorListeners()) {
            validatorListener.afterValidate(context, this);
        }
        log.info(MARKER, "Run 'afterValidate' pre-processes : completed");
    }

    /**
     * Matching files in documentPath with FileModel defined in DocumentModel
     *
     * @param documentPath
     */
    public void findDocumentFiles(Context context) {
        clearFiles();

        log.info(MARKER, "List files and directories in '{}'...", documentPath);
        Collection<File> files = FileUtils.listFilesAndDirs(documentPath, allowedExtensions);
        log.info(MARKER, "{} file(s) found in '{}'.", files.size(), documentPath);

        /*
         * find match with FileModel
         */
        for (File file : files) {
            log.info(MARKER, "Looking for a FileModel for '{}'...", file);

            FileModel fileModel = documentModel.findFileModelByPath(file);

            if (fileModel != null) {
                log.info(MARKER, "Found {} by path for '{}'", fileModel, file);
                addDocumentFile(fileModel, file);
                continue;
            }
            /*
             * move elsewhere ?
             */
            fileModel = documentModel.findFileModelByFilename(file);
            if (fileModel != null) {

                log.info(MARKER, "Found {} by name for '{}' (FILE_MISPLACED)", fileModel, file);
                if (context.isFlatValidation()) {
                    addDocumentFile(fileModel, file);
                } else {
                    context.beginModel(fileModel);
                    context.report(
                        context.createError(CoreErrorCodes.FILE_MISPLACED)
                            .setMessageParam("FILEPATH", context.relativize(file))
                    );
                    context.endModel(fileModel);
                }
                continue;
            }
            /*
             * not covered by model
             */
            log.info(MARKER, "FileModel not found for '{}' (FILE_UNEXPECTED)!", file);
            ErrorCode errorCode = CoreErrorCodes.FILE_UNEXPECTED;
            if (file.isDirectory()) {
                errorCode = CoreErrorCodes.DIRECTORY_UNEXPECTED;
            }
            context.createError(errorCode)
                .setMessageParam("FILEPATH", context.relativize(file));
        }

        log.info(
            MARKER, "List files and directories : completed, {} document file(s) found.",
            documentFiles.size()
        );
    }

    private void addDocumentFile(FileModel fileModel, File path) {
        this.documentFiles.add(fileModel.createDocumentFile(path));
    }

    /**
     * Deletes list of files matching with model
     */
    private void clearFiles() {
        documentFiles.clear();
    }

    /**
     * Get tags
     *
     * @return
     */
    public Map<String, String> getTags() {
        return this.tags;
    }

    /**
     * Insert or update tag
     *
     * @param key
     * @param value
     */
    public void setTag(String key, String value) {
        this.tags.put(key, value);
    }

}
