package fr.ign.validator.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.ign.validator.data.Document;
import fr.ign.validator.database.Database;
import fr.ign.validator.validation.Validator;
import fr.ign.validator.validation.attribute.AttributeReferenceValidator;
import fr.ign.validator.validation.database.AttributeUniqueValidator;
import fr.ign.validator.validation.database.FeatureTypeConditionsValidator;
import fr.ign.validator.validation.database.ForeignKeyValidator;
import fr.ign.validator.validation.document.DocumentFolderNameValidator;
import fr.ign.validator.validation.document.DocumentMandatoryFileValidator;

/**
 * A DocumentModel defines a list of FileModel
 *
 * @author MBorne
 */
public class DocumentModel implements Model {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("DocumentModel");

    /**
     * The name of the DocumentModel (ex : "cnig_PLU_2013")
     */
    private String name;

    /**
     * The list of files in Document
     */
    private List<FileModel> fileModels = new ArrayList<FileModel>();

    /**
     * The list of codes in Document
     */
    private List<StaticTable> staticTables = new ArrayList<StaticTable>();

    /**
     * Constraints on the document
     */
    private DocumentConstraints constraints = new DocumentConstraints();

    /**
     * The list of validators on the Document
     */
    private List<Validator<Document>> validators = new ArrayList<Validator<Document>>();

    /**
     * Validators applied on validation database dedicated to the Document (unicity,
     * references,...)
     */
    private List<Validator<Database>> databaseValidators = new ArrayList<>();

    /**
     * Constructs a DocumentModel with default constraints
     */
    public DocumentModel() {
        addValidator(new DocumentFolderNameValidator());
        addValidator(new DocumentMandatoryFileValidator());
        // validators relying on the validation Database
        addDatabaseValidator(new AttributeUniqueValidator());
        addDatabaseValidator(new AttributeReferenceValidator());
        addDatabaseValidator(new FeatureTypeConditionsValidator());
        addDatabaseValidator(new ForeignKeyValidator());
    }

    /**
     * Returns the name of the document model
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Defines the name of the document model
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    public DocumentConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(DocumentConstraints constraints) {
        this.constraints = constraints;
    }

    /**
     * @deprecated kept for compatibility with old models
     * @return
     */
    @JsonIgnore
    public String getRegexp() {
        return constraints.getFolderName();
    }

    /**
     * Defines regexp matching the directory of the document
     *
     * @param regexp
     *
     * @deprecated
     */
    public void setRegexp(String regexp) {
        this.constraints.setFolderName(regexp);
    }

    @JsonProperty("files")
    public List<FileModel> getFileModels() {
        return fileModels;
    }

    /**
     * Find FileModel by name
     *
     * @param typeName
     * @return
     */
    public FileModel getFileModelByName(String typeName) {
        for (FileModel fileModel : fileModels) {
            if (fileModel.getName().equals(typeName)) {
                return fileModel;
            }
        }
        return null;
    }

    /**
     * @param fileModels
     */
    public void setFileModels(List<FileModel> fileModels) {
        this.fileModels = fileModels;
    }

    /**
     * Finds FileModel corresponding to File with (full) filepath
     *
     * @param documentPath
     * @param file
     * @return
     */
    public FileModel findFileModelByPath(File file) {
        FileModel result = null;
        for (FileModel fileModel : fileModels) {
            if (fileModel.matchPath(file)) {
                // keep longest regexp
                if (result == null || fileModel.getPath().length() > result.getPath().length()) {
                    result = fileModel;
                }
            }
        }
        return result;
    }

    /**
     * Finds FileModel corresponding to File with (only) filename
     *
     * @param file
     * @return
     */
    public FileModel findFileModelByFilename(File file) {
        FileModel result = null;
        for (FileModel fileModel : fileModels) {
            if (fileModel.matchFilename(file)) {
                // keep longest regexp
                if (result == null || fileModel.getPath().length() > result.getPath().length()) {
                    result = fileModel;
                }
            }
        }
        return result;
    }

    @JsonProperty("codes")
    public List<StaticTable> getStaticTables() {
        return this.staticTables;
    }

    public void setStaticTables(List<StaticTable> staticTables) {
        this.staticTables = staticTables;
    }

    public StaticTable getStaticTableByName(String staticTableName) {
        for (StaticTable staticTable : staticTables) {
            if (staticTable.getName().equals(staticTableName)) {
                return staticTable;
            }
        }
        return null;
    }

    @JsonIgnore
    public List<Validator<Document>> getValidators() {
        return this.validators;
    }

    public void addValidator(Validator<Document> validator) {
        this.validators.add(validator);
    }

    @JsonIgnore
    public List<Validator<Database>> getDatabaseValidators() {
        return databaseValidators;
    }

    public void addDatabaseValidator(Validator<Database> validator) {
        databaseValidators.add(validator);
    }

    @Override
    public String toString() {
        return name + " (" + getClass().getSimpleName() + ")";
    }

}
