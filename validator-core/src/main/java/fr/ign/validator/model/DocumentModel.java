package fr.ign.validator.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.ign.validator.data.Document;
import fr.ign.validator.validation.Validator;
import fr.ign.validator.validation.document.DocumentFolderNameValidator;
import fr.ign.validator.validation.document.DocumentMandatoryFileValidator;

/**
 * A DocumentModel defines a list of FileModel
 * 
 * @author MBorne
 */
@XmlRootElement(name = "document")
@XmlType(propOrder = {
    "name", "regexp", "fileModels"
})
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
     * Constraints on the document
     */
    private DocumentConstraints constraints = new DocumentConstraints();

    /**
     * The list of validators on the Document
     */
    private List<Validator<Document>> validators = new ArrayList<Validator<Document>>();

    /**
     * Constructs a DocumentModel with default constraints
     */
    public DocumentModel() {
        addValidator(new DocumentFolderNameValidator());
        addValidator(new DocumentMandatoryFileValidator());
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

    @XmlTransient
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

    @XmlElementWrapper(name = "files")
    @XmlElement(name = "file")
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

    /**
     * Add a validator to the document
     * 
     * @param validator
     */
    public void addValidator(Validator<Document> validator) {
        this.validators.add(validator);
    }

    /**
     * Get validators on document
     * 
     * @return
     */
    @JsonIgnore
    public List<Validator<Document>> getValidators() {
        return this.validators;
    }

}
