package fr.ign.validator.cnig.info.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Envelope;

import fr.ign.validator.cnig.info.internal.DocumentFileInfoComparator;
import fr.ign.validator.jackson.serializer.EnvelopeSerializer;
import fr.ign.validator.metadata.Metadata;

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
	"standard",
	"files",
	"documentExtent",
	"tags",
	"metadata"
})
public class DocumentInfo {
	
	/**
	 * String directoryName
	 */
	private String name ;
	
	/**
	 * The standard (documentModelName)
	 */
	private String standard ;

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
	 * Additional informations
	 */
	private Map<String,String> tags = new HashMap<>();
	
	/**
	 * Constructor
	 */
	public DocumentInfo(String documentName) {
		this.name = documentName ;
	}

	/**
	 * get directory name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getStandard() {
		return standard;
	}
	/**
	 * @param standard
	 */
	public void setStandard(String standard) {
		this.standard = standard;
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
		return this.tags;
	}

	/**
	 * Insert or update tag
	 * @param key
	 * @param value
	 */
	public void setTag(String key, String value){
		this.tags.put(key, value);
	}
	
	/**
	 * simplify test
	 */
	public void sortFiles(){
		Collections.sort(files, new DocumentFileInfoComparator());
	}
	
}



