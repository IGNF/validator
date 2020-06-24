package fr.ign.validator.info.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.Envelope;

import fr.ign.validator.data.Document;
import fr.ign.validator.info.internal.DocumentFileInfoComparator;
import fr.ign.validator.io.json.EnvelopeSerializer;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.model.DocumentModel;

/** 
 * 
 * Represents a directory.
 * Contains DataFiles and DataLayers
 *  
 * 
 * @author CBouche
 *
 */
@JsonPropertyOrder({ 
	"name",
	"documentModel",
	"files",
	"documentExtent",
	"tags",
	"metadata"
})
public class DocumentInfo {
	
	/**
	 * Wrapped document
	 */
	private Document document;

	/**
	 * List of DataFiles in directory
	 */
	private List<DocumentFileInfo> files = new ArrayList<DocumentFileInfo>() ;

	/**
	 * Enveloppe du document
	 */
	private Envelope documentExtent = new Envelope();

	/**
	 * Metadata 
	 */
	private Metadata metadata ;

	/**
	 * Constructor
	 */
	public DocumentInfo(Document document) {
		this.document = document ;
	}

	/**
	 * get directory name
	 * @return
	 */
	public String getName() {
		return document.getDocumentName();
	}

	/**
	 * Get partial informations for DocumentModel
	 * @return
	 */
	public DocumentModelInfo getDocumentModel() {
	    DocumentModel documentModel = document.getDocumentModel();
		return documentModel != null ? new DocumentModelInfo(documentModel) : null;
	}

	/**
	 * get register Files
	 * @return
	 */
	public List<DocumentFileInfo> getFiles() {
		return files;
	}

	/**
	 * add register File
	 * @param dataFile
	 */
	public void addFile(DocumentFileInfo dataFile) {
		files.add(dataFile) ;
	}
	

	
	@JsonSerialize(using=EnvelopeSerializer.class)
	@JsonInclude(value=Include.NON_NULL)
	public Envelope getDocumentExtent() {
		return documentExtent;
	}


	public void setDocumentExtent(Envelope documentExtent) {
		this.documentExtent = documentExtent;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * Get tags
	 * @return
	 */
	public Map<String,String> getTags(){
		return document.getTags();
	}


	/**
	 * simplify test
	 */
	public void sortFiles(){
		Collections.sort(files, new DocumentFileInfoComparator());
	}
	
}



