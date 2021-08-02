package fr.ign.validator;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.Row;
import fr.ign.validator.data.Table;
import fr.ign.validator.data.file.MetadataFile;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.error.ErrorFactory;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.exception.ValidatorFatalError;
import fr.ign.validator.geometry.ProjectionList;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.Model;
import fr.ign.validator.model.Projection;
import fr.ign.validator.process.CheckFeatureTypesPreProcess;
import fr.ign.validator.process.DocumentInfoExtractorPostProcess;
import fr.ign.validator.process.FilterMetadataPreProcess;
import fr.ign.validator.process.MetadataPreProcess;
import fr.ign.validator.process.NormalizePostProcess;
import fr.ign.validator.process.ProjectionPreProcess;
import fr.ign.validator.process.RemovePreviousFilesPreProcess;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.report.ReportBuilder;
import fr.ign.validator.string.StringFixer;
import fr.ign.validator.validation.Validatable;

/**
 * 
 * Validation context
 * 
 * @author MBorne
 *
 */
public class Context {
    private static final Logger log = LogManager.getRootLogger();
    private static final Marker MARKER = MarkerManager.getMarker("Context");

    /**
     * Data charset (overridden by metadata provided charset)
     * 
     * @see MetadataPreProcess
     */
    private Charset encoding = StandardCharsets.UTF_8;

    /**
     * Input - Data projection provided as a command line option.
     */
    private Projection projection = ProjectionList.getInstance().findByCode(Projection.CODE_CRS84);

    /**
     * Expected data extent provided with data projection.
     */
    private Geometry nativeDataExtent;

    /**
     * Allows to disable strict file hierarchy validation matching DocumentFile to
     * FileModel by name instead of path.
     */
    private boolean flatValidation;

    /**
     * Configures deep character validation
     */
    private StringFixer stringFixer = new StringFixer();

    /**
     * Input - Current data directory (equivalent to documentPath)
     */
    private File currentDirectory;

    /**
     * Output - validation directory containing validation and normalization results
     */
    private File validationDirectory;

    /**
     * Output - allow to enable/disable data normalization in validation/DATA
     * directory
     */
    private boolean normalizeEnabled = false;

    /**
     * Output - output projection for normalized DATA
     */
    private Projection outputProjection = ProjectionList.getInstance().findByCode(Projection.CODE_CRS84);

    /**
     * Execution context - modelStack providing current model informations while
     * reporting errors.
     */
    private List<Model> modelStack = new ArrayList<Model>();
    /**
     * Execution context - dataStack providing current data informations while
     * reporting errors.
     */
    private List<Validatable> dataStack = new ArrayList<Validatable>();

    /**
     * Reporting - Create errors according to configuration files (template string
     * and ErrorLevel)
     */
    private ErrorFactory errorFactory = new ErrorFactory();

    /**
     * Reporting - Generates validation report
     */
    private ReportBuilder reportBuilder = new InMemoryReportBuilder();

    /**
     * Customization - validation listener
     */
    private List<ValidatorListener> listeners = new ArrayList<ValidatorListener>();

    /**
     * Tolerance for geometric operation such as intersection
     */
    private Double dgprTolerance;

    /**
     * Simplification distance
     */
    private Double dgprSimplification;

    /**
     * Allow using faster simplification against topologicaly safe simplification
     */
    private Boolean dgprSafeMode;

    public Context() {
        registerDefaultListeners();
    }

    /**
     * Get validatorListeners
     * 
     * @return
     */
    public List<ValidatorListener> getValidatorListeners() {
        return this.listeners;
    }

    /**
     * add listener
     * 
     * @param listener
     */
    public void addListener(ValidatorListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Add listener before the first listener of a given class
     * 
     * @param listener
     * @param clazz
     */
    public void addListenerBefore(ValidatorListener listener, Class<?> clazz) {
        int index = findListener(clazz);
        if (index < 0) {
            index = 0;
        }
        this.listeners.add(index, listener);
    }

    /**
     * Find listener for a given class
     * 
     * @param clazz
     * @return
     */
    private int findListener(Class<?> clazz) {
        for (int i = 0; i < listeners.size(); i++) {
            if (clazz.isInstance(listeners.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Load defaults validation listeners
     */
    private void registerDefaultListeners() {
        addListener(new RemovePreviousFilesPreProcess());

        // Filter XML files
        addListener(new FilterMetadataPreProcess());
        // Extract informations such as charset from metadata (after
        // FilterMetadataPreProcess)
        addListener(new MetadataPreProcess());
        // Info and warning about CRS
        addListener(new ProjectionPreProcess());

        // Check and complete FeatureType definitions
        addListener(new CheckFeatureTypesPreProcess());

        // generate CSV
        addListener(new NormalizePostProcess());
        // produce document-info.json
        addListener(new DocumentInfoExtractorPostProcess());
    }

    /**
     * @return the encoding
     */
    public Charset getEncoding() {
        return encoding;
    }

    /**
     * @param encoding the encoding to set
     */
    public void setEncoding(Charset encoding) {
        log.info(MARKER, "Set encoding to {}", encoding);
        this.encoding = encoding;
    }

    /**
     * @return
     */
    public Projection getProjection() {
        return projection;
    }

    /**
     * @param projection
     */
    public void setProjection(Projection projection) {
        log.info(MARKER, "Set projection to {}", projection);
        this.projection = projection;
    }

    /**
     * Set projection from CRS code
     * 
     * @param code
     */
    public void setProjection(String code) {
        Projection result = ProjectionList.getInstance().findByCode(code);
        if (result == null) {
            String message = String.format("Projection '%1s' not found", code);
            throw new IllegalArgumentException(message);
        }
        setProjection(result);
    }

    /**
     * @return
     */
    public boolean hasNativeDataExtent() {
        return nativeDataExtent != null;
    }

    /**
     * @return
     */
    public Geometry getNativeDataExtent() {
        return nativeDataExtent;
    }

    /**
     * @param extent
     */
    public void setNativeDataExtent(Geometry extent) {
        log.info(MARKER, "Set native data extent to {}", extent);
        this.nativeDataExtent = extent;
    }

    /**
     * @return the currentDirectory
     */
    public File getCurrentDirectory() {
        return currentDirectory;
    }

    /**
     * @param currentDirectory the currentDirectory to set
     */
    public void setCurrentDirectory(File currentDirectory) {
        log.info(MARKER, "Set current directory to {}", currentDirectory);
        this.currentDirectory = currentDirectory;
    }

    /**
     * Get ErrorFactory
     * 
     * @return
     */
    public ErrorFactory getErrorFactory() {
        return errorFactory;
    }

    /**
     * Set ErrorFactory
     * 
     * @param errorFactory
     */
    public void setErrorFactory(ErrorFactory errorFactory) {
        this.errorFactory = errorFactory;
    }

    /**
     * Push given model to stack
     * 
     * @param model
     */
    public void beginModel(Model model) {
        log.trace(MARKER, "begin model {}...", model);
        modelStack.add(model);
    }

    /**
     * Get model stack
     * 
     * @return
     */
    public List<Model> getModelStack() {
        return this.modelStack;
    }

    /**
     * Get current model by type
     * 
     * @param clazz
     * @return
     */
    public <T extends Model> T getModelByType(Class<T> clazz) {
        for (int index = modelStack.size() - 1; index >= 0; index--) {
            Model model = modelStack.get(index);
            if (clazz.isInstance(model)) {
                return clazz.cast(model);
            }
        }
        return null;
    }

    /**
     * Get current document model
     * 
     * @return
     */
    public DocumentModel getDocumentModel() {
        return this.getModelByType(DocumentModel.class);
    }

    /**
     * Get current document model name
     * 
     * @param context
     * @return
     */
    public String getDocumentModelName() {
        DocumentModel documentModel = getModelByType(DocumentModel.class);
        if (documentModel != null) {
            return documentModel.getName();
        }
        return "";
    }

    /**
     * Get current file model name
     * 
     * @param context
     * @return
     */
    public String getFileModelName() {
        FileModel fileModel = getModelByType(FileModel.class);
        if (fileModel != null) {
            return fileModel.getName();
        }
        return "";
    }

    /**
     * Get current attribute name
     * 
     * @param context
     * @return
     */
    public String getAttributeName() {
        AttributeType<?> attribute = getModelByType(AttributeType.class);
        if (attribute != null) {
            return attribute.getName();
        }
        return "";
    }

    /**
     * Pop given model from stack
     */
    public void endModel(Model model) {
        log.trace(MARKER, "end model {}", model);
        int position = modelStack.size() - 1;
        if (position < 0 || modelStack.get(position) != model) {
            throw new ValidatorFatalError("Unconsistent usage of beginModel/endModel");
        }
        modelStack.remove(position);
    }

    /**
     * Begin data validation (push data on dataStack)
     * 
     * @param location
     */
    public void beginData(Validatable data) {
        dataStack.add(data);
    }

    /**
     * Get data by type from data stack
     * 
     * @param clazz
     * @return
     */
    public <T extends Validatable> T getDataByType(Class<T> clazz) {
        for (int index = dataStack.size() - 1; index >= 0; index--) {
            Validatable model = dataStack.get(index);
            if (clazz.isInstance(model)) {
                return clazz.cast(model);
            }
        }
        return null;
    }

    /**
     * Get current scope from data stack
     * 
     * @return
     */
    public ErrorScope getScope() {
        if (getDataByType(Attribute.class) != null) {
            return ErrorScope.FEATURE;
        } else if (getDataByType(Table.class) != null) {
            return ErrorScope.HEADER;
        } else if (getDataByType(MetadataFile.class) != null) {
            return ErrorScope.METADATA;
        } else {
            return ErrorScope.DIRECTORY;
        }
    }

    /**
     * Get current fileName from data stack
     * 
     * @param context
     * @return
     */
    public String getFileName() {
        DocumentFile documentFile = getDataByType(DocumentFile.class);
        if (documentFile != null) {
            return relativize(documentFile.getPath());
        }
        Document document = getDataByType(Document.class);
        if (document != null) {
            return document.getDocumentName() + "/";
        }
        return "";
    }

    /**
     * Get current line number from data stack
     * 
     * @param context
     * @return
     */
    public String getLine() {
        Row row = getDataByType(Row.class);
        if (row != null) {
            return "" + row.getLine();
        }
        return "";
    }

    /**
     * Get current feature bounding box from data stack
     * 
     * @return string
     */
    public Envelope getFeatureBBox() {
        Row row = getDataByType(Row.class);
        if (row != null) {
            return row.getFeatureBbox();
        }
        return new Envelope();
    }

    /**
     * Get current identifiant
     * 
     * @return
     */
    public String getFeatureId() {
        Row row = getDataByType(Row.class);
        if (row != null) {
            return row.getFeatureId();
        }
        return "";
    }

    /**
     * End data validation (pop data from dataStack)
     * 
     * @param location
     */
    public void endData(Validatable data) {
        int position = dataStack.size() - 1;
        if (position < 0 || dataStack.get(position) != data) {
            throw new ValidatorFatalError("Unconsistent usage of beginData/endData");
        }
        dataStack.remove(position);
    }

    /**
     * Get relative path according to current directory.
     * 
     * @param path
     * @return
     */
    public String relativize(File path) {
        DocumentModel documentModel = getDocumentModel();
        if (documentModel != null) {
            return currentDirectory.toPath().relativize(
                path.toPath()
            ).toString();
        } else {
            return path.getName();
        }
    }

    /**
     * Create and report an error according to its code
     * 
     * @param code
     * @param messageParams
     */
    public void report(ErrorCode code) {
        ValidatorError validatorError = createError(code);
        reportBuilder.addError(validatorError);
    }

    /**
     * Direct reporting of an existing error
     * 
     * @param validatorError
     */
    public void report(ValidatorError validatorError) {
        reportBuilder.addError(validatorError);
    }

    /**
     * Generic method for building errors
     * 
     * @param code
     * @param messageParams
     */
    public ValidatorError createError(ErrorCode code) {
        ValidatorError validatorError = errorFactory.newError(code);
        validatorError.setScope(getScope());

        /*
         * Add model informations
         */
        validatorError.setDocumentModel(getDocumentModelName());
        validatorError.setFileModel(getFileModelName());
        validatorError.setAttribute(getAttributeName());
        /*
         * Add data informations
         */
        validatorError.setFile(getFileName());
        validatorError.setId(getLine());
        /*
         * Add data informations (new)
         */
        validatorError.setFeatureBbox(getFeatureBBox());
        validatorError.setFeatureId(getFeatureId());

        return validatorError;
    }

    /**
     * Get ValidationDirectory
     * 
     * @return
     */
    public File getValidationDirectory() {
        return validationDirectory;
    }

    /**
     * Set ValidationDirectory
     * 
     * @return
     */
    public void setValidationDirectory(File validationDirectory) {
        log.info(MARKER, "set validation directory to {}", validationDirectory);
        this.validationDirectory = validationDirectory;
    }

    public boolean isNormalizeEnabled() {
        return normalizeEnabled;
    }

    /**
     * Enable or disable data normalization.
     * 
     * @param normalizeEnabled
     */
    public void setNormalizeEnabled(boolean normalizeEnabled) {
        log.info(MARKER, "set normalize enabled to {}", normalizeEnabled);
        this.normalizeEnabled = normalizeEnabled;
    }

    /**
     * Get output projection for normalized data.
     * 
     * @return
     */
    public Projection getOutputProjection() {
        return outputProjection;
    }

    /**
     * Set output projection for normalized data.
     * 
     * @param outputProjection
     */
    public void setOutputProjection(Projection outputProjection) {
        log.info(MARKER, "set output projection to {}", outputProjection);
        this.outputProjection = outputProjection;
    }

    /**
     * Get DATA directory for normalized data ({validation_dir}/DATA)
     * 
     * @return
     */
    public File getDataDirectory() {
        File result = new File(validationDirectory, getCurrentDirectory().getName() + "/DATA");
        if (!result.exists()) {
            result.mkdirs();
        }
        return result;
    }

    /**
     * Get metadata directory ({validation_dir}/METADATA)
     * 
     * @return
     */
    public File getMetadataDirectory() {
        File result = new File(validationDirectory, getCurrentDirectory().getName() + "/METADATA");
        if (!result.exists()) {
            result.mkdirs();
        }
        return result;
    }

    /**
     * @return
     */
    public ReportBuilder getReportBuilder() {
        return reportBuilder;
    }

    /**
     * @param reportBuilder
     */
    public void setReportBuilder(ReportBuilder reportBuilder) {
        this.reportBuilder = reportBuilder;
    }

    /**
     * @return
     */
    public boolean isFlatValidation() {
        return flatValidation;
    }

    /**
     * @param flatValidation
     */
    public void setFlatValidation(boolean flatValidation) {
        log.info(MARKER, "set flat validation to {}", flatValidation);
        this.flatValidation = flatValidation;
    }

    /**
     * @return
     */
    public StringFixer getStringFixer() {
        return stringFixer;
    }

    /**
     * @param stringFixer
     */
    public void setStringFixer(StringFixer stringFixer) {
        this.stringFixer = stringFixer;
    }

    /**
     * tolerance option used in geometric operation (dgpr plugin)
     * 
     * @return
     */
    public Double getDgprTolerance() {
        return this.dgprTolerance;
    }

    /**
     * tolerance option used in geometric operation (dgpr plugin)
     * 
     * @param topologicalTolerance
     */
    public void setDgprTolerance(double topologicalTolerance) {
        this.dgprTolerance = topologicalTolerance;
    }

    /**
     * simplification distance option
     * 
     * @return distance
     */
    public Double getDgprSimplification() {
        return dgprSimplification;
    }

    /**
     * simplification distance option
     * 
     * @param distanceSimplification
     */
    public void setDgprSimplification(Double distanceSimplification) {
        this.dgprSimplification = distanceSimplification;
    }

    /**
     * safe simplification option
     * 
     * @return safe simplication allowed
     */
    public Boolean isDgprSafeMode() {
        return dgprSafeMode;
    }

    /**
     * safe simplification option
     * 
     * @param safeSimplification
     */
    public void setDgprSafeMode(boolean safeSimplification) {
        this.dgprSafeMode = safeSimplification;
    }

}
