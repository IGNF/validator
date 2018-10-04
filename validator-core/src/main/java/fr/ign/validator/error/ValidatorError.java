package fr.ign.validator.error;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Envelope;

import fr.ign.validator.jackson.serializer.EnvelopeSerializer;

/**
 * A validation error with corresponding context informations
 * 
 * @author CBouche
 *
 */
public class ValidatorError implements Cloneable {

	/**
	 * ErrorCode 
	 */
	private ErrorCode code ;

	/**
	 * ErrorScope
	 */
	private ErrorScope scope ;

	/**
	 * ErrorLevel
	 */
	private ErrorLevel level ;

	/**
	 * Explanation message (init with a template message from configuration)
	 */
	private String message ;

	/**
	 * model - DocumentModel name
	 */
	private String documentModel;

	/**
	 * model - FileModel name
	 */
	private String fileModel ;

	/**
	 * model - Attribute name 
	 */
	private String attribute ;

	/**
	 * data - File concerned by the error (either relative path for DocumentFile or directory name for Document)
	 */
	private String file ;

	/**
	 * data - Identifier of an element in a file (line for table)
	 */
	private String id;

	/**
	 * Bounding box of the concerned feature (if context == Feature and a geometry is available)
	 */
	private Envelope featureBbox;

	/**
	 * WKT geometry error
	 */
	private String errorGeometry;

	/**
	 * @param code
	 */
	public ValidatorError(ErrorCode code) {
		this.code = code ;
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
	public void setScope(ErrorScope scope) {
		this.scope = scope;
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
	public void setLevel(ErrorLevel level) {
		this.level = level;
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
	public void setMessage(String message) {
		this.message = message;
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
	public void setDocumentModel(String documentModel) {
		this.documentModel = documentModel;
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
	public void setFileModel(String fileModel) {
		this.fileModel = fileModel;
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
	public void setAttribute(String attribute) {
		this.attribute = attribute;
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
	public void setFile(String filename) {
		this.file = filename;
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
	public void setId(String id) {
		this.id = id;
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
	 * @return
	 */
	public String getErrorGeometry() {
		return errorGeometry;
	}

	/**
	 * WKT geometry error
	 * @param errorGeometry
	 * @return
	 */
	public ValidatorError setErrorGeometry(String errorGeometry) {
		this.errorGeometry = errorGeometry;
		return this;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		ValidatorError cloned = (ValidatorError)super.clone();
		return cloned ;
	}

	@Override
	public String toString() {
		return code+"|"+scope+"|"+level+"|"+message ;
	}

}
