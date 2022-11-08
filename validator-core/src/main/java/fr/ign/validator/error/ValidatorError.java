package fr.ign.validator.error;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fr.ign.validator.io.json.EnvelopeSerializer;
import fr.ign.validator.io.json.ValidatorErrorDeserializer;

import org.locationtech.jts.geom.Envelope;

/**
 * A validation error with corresponding context informations
 * 
 * @author CBouche
 *
 */
@JsonDeserialize(using = ValidatorErrorDeserializer.class)
public class ValidatorError implements Cloneable {

    /**
     * ErrorCode
     */
    private ErrorCode code;

    /**
     * ErrorScope
     */
    private ErrorScope scope;

    /**
     * ErrorLevel
     */
    private ErrorLevel level;

    /**
     * Explanation message (init with a template message from configuration)
     */
    private String message;

    /**
     * model - DocumentModel name
     */
    private String documentModel;

    /**
     * model - FileModel name
     */
    private String fileModel;

    /**
     * model - Attribute name
     */
    private String attribute;

    /**
     * data - File concerned by the error (either relative path for DocumentFile or
     * directory name for Document)
     */
    private String file;

    /**
     * data - Identifier of an element in a file (line for table)
     */
    private String id;

    /**
     * The CRS:84 bounding box of the concerned feature.
     */
    private Envelope featureBbox;

    /**
     * The CRS:84 WKT locating the geometry error.
     */
    private String errorGeometry;

    /**
     * Feature identifier is available.
     */
    private String featureId;

    /**
     * XSD error code (XSD_SCHEMA_ERROR)
     */
    @JsonInclude(value = Include.NON_EMPTY)
    private String xsdErrorCode;

    /**
     * XSD error message according to the local (XSD_SCHEMA_ERROR)
     */
    @JsonInclude(value = Include.NON_EMPTY)
    private String xsdErrorMessage;

    /**
     * @param code
     */
    public ValidatorError(ErrorCode code) {
        this.code = code;
    }

    /**
     * @return
     */
    public ErrorCode getCode() {
        return code;
    }

    /**
     * @return
     */
    public ErrorScope getScope() {
        return scope;
    }

    /**
     * @param scope
     */
    public ValidatorError setScope(ErrorScope scope) {
        this.scope = scope;
        return this;
    }

    /**
     * @return
     */
    public ErrorLevel getLevel() {
        return level;
    }

    /**
     * @param level
     */
    public ValidatorError setLevel(ErrorLevel level) {
        this.level = level;
        return this;
    }

    /**
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     */
    public ValidatorError setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Replace {name} by value in error message (original is the template)
     * 
     * @param name
     * @param value
     * @return
     */
    public ValidatorError setMessageParam(String name, String value) {
        this.message = StringUtils.replace(this.message, "{" + name + "}", value);
        return this;
    }

    /**
     * @return
     */
    public String getDocumentModel() {
        return documentModel;
    }

    /**
     * @param documentModel
     */
    public ValidatorError setDocumentModel(String documentModel) {
        this.documentModel = documentModel;
        return this;
    }

    /**
     * @return
     */
    public String getFileModel() {
        return fileModel;
    }

    /**
     * @param fileModel
     */
    public ValidatorError setFileModel(String fileModel) {
        this.fileModel = fileModel;
        return this;
    }

    /**
     * @return
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @param attribute
     */
    public ValidatorError setAttribute(String attribute) {
        this.attribute = attribute;
        return this;
    }

    /**
     * @return
     */
    public String getFile() {
        return file;
    }

    /**
     * @param filename
     */
    public ValidatorError setFile(String filename) {
        this.file = filename;
        return this;
    }

    /**
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public ValidatorError setId(String id) {
        this.id = id;
        return this;
    }

    public ValidatorError setFeatureBbox(Envelope featureBBox) {
        this.featureBbox = featureBBox;
        return this;
    }

    @JsonSerialize(using = EnvelopeSerializer.class)
    public Envelope getFeatureBbox() {
        return this.featureBbox;
    }

    /**
     * WKT geometry error
     * 
     * @return
     */
    public String getErrorGeometry() {
        return errorGeometry;
    }

    /**
     * WKT geometry error
     * 
     * @param errorGeometry
     * @return
     */
    public ValidatorError setErrorGeometry(String errorGeometry) {
        this.errorGeometry = errorGeometry;
        return this;
    }

    public String getFeatureId() {
        return featureId;
    }

    public ValidatorError setFeatureId(String featureId) {
        this.featureId = featureId;
        return this;
    }

    public String getXsdErrorCode() {
        return xsdErrorCode;
    }

    public void setXsdErrorCode(String xsdErrorCode) {
        this.xsdErrorCode = xsdErrorCode;
    }

    public String getXsdErrorMessage() {
        return xsdErrorMessage;
    }

    public void setXsdErrorMessage(String xsdErrorMessage) {
        this.xsdErrorMessage = xsdErrorMessage;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ValidatorError cloned = (ValidatorError) super.clone();
        return cloned;
    }

    @Override
    public String toString() {
        return code + "|" + scope + "|" + level + "|" + message;
    }

}
