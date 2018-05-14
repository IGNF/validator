package fr.ign.validator.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.data.Document;
import fr.ign.validator.validation.Validator;
import fr.ign.validator.validation.document.DocumentDirectoryNameValidator;
import fr.ign.validator.validation.document.DocumentMandatoryFileValidator;

/**
 * A DocumentModel defines a list of FileModel
 * 
 * @author MBorne
 */
@XmlRootElement(name="document")
@XmlType(propOrder = { "name", "regexp", "fileModels" })
public class DocumentModel implements Model {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("DocumentModel") ;
	
	/**
	 * The name of the DocumentModel (ex : "cnig_PLU_2013")
	 */
	private String name ;
	/**
	 * The name of the document provided as a regexp (e.g. "(2A|2B|[0-9]{2})[0-9]{3}_PLU_[0-9]{8}")
	 */
	private String regexp ;
	/**
	 * The list of files in Document
	 */
	private List<FileModel> fileModels = new ArrayList<FileModel>();

	/**
	 * The list of validators on the Document
	 */
	private List< Validator<Document> > validators = new ArrayList<Validator<Document>>();
	
	/**
	 * Constructs a DocumentModel with default constraints
	 */
	public DocumentModel(){
		addValidator(new DocumentDirectoryNameValidator());
		addValidator(new DocumentMandatoryFileValidator());
	}
	
	
	/**
	 * Returns the name of the document
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * Defines the name of the document
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Returns regexp matching the directory
	 * @return
	 */
	public String getRegexp() {
		return regexp;
	}
	/**
	 * Defines regexp matching the directory of the document
	 * @param regexp
	 */
	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}
	
	
	@XmlElementWrapper(name = "files")
	@XmlElement(name = "file")
	public List<FileModel> getFileModels() {
		return fileModels;
	}
	

	public FileModel getFileModelByName(String typeName) {
		for (FileModel fileModel : fileModels) {
			if ( fileModel.getName().equals(typeName) ){
				return fileModel;
			}
		}
		return null;
	}
 	
	
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
	public FileModel FindFileModelByFilepath(File file) {
		FileModel result = null;
		for  (FileModel fileModel : fileModels ) {
			if ( fileModel.matchPath(file) ){
				// keep longest regexp
				if ( result == null || fileModel.getRegexp().length() > result.getRegexp().length() ){
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
		for  (FileModel fileModel : fileModels ) {
			if ( fileModel.matchFilename(file) ){
				// keep longest regexp
				if ( result == null || fileModel.getRegexp().length() > result.getRegexp().length() ){
					result = fileModel;
				}
			}
		}
		return result;
	}

	
	/**
	 * Adds a validator to the document
	 * @param validator
	 */
	public void addValidator(Validator<Document> validator) {
		this.validators.add(validator);
	}
	
	/**
	 * Gets validators on document
	 * @return
	 */
	public List<Validator<Document>> getValidators(){
		return this.validators ;
	}


}

