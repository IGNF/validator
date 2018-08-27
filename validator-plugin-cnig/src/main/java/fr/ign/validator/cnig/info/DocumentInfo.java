package fr.ign.validator.cnig.info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;

import fr.ign.validator.cnig.info.internal.DataFileComparator;

/** 
 * 
 * Represents a directory.
 * Contains DataFiles and DataLayers
 *  
 * 
 * @author CBouche
 *
 */
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
	 * Cadastral reference used (reference system id)
	 */
	private String typeref ;

	/**
	 * fileIdentifier field in metadata
	 */
	private String metadataFileIdentifier ;
		
	/**
	 * MD_Identifier field in metadata
	 */
	private String metadataMdIdentifier ;

	/**
	 * List of DataFiles in directory
	 */
	private List<DataFile> dataFiles = new ArrayList<DataFile>() ;

	/**
	 * List of DataLayers in directory
	 */
	private List<DataLayer> dataLayers = new ArrayList<DataLayer>() ;

	/**
	 * Enveloppe du document
	 */
	private Envelope documentExtent = new Envelope();
	
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
	 * Get document type according to standard
	 * @return
	 */
	public String getType(){
		if ( standard == null ){
			return null;
		}
		return standard.split("_")[1];
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
	 * @return the typeref
	 */
	public String getTyperef() {
		return typeref;
	}


	/**
	 * @param typeref the typeref to set
	 */
	public void setTyperef(String typeref) {
		this.typeref = typeref;
	}
	
	

	/**
	 * @return the metadataFileIdentifier
	 */
	public String getMetadataFileIdentifier() {
		return metadataFileIdentifier;
	}


	/**
	 * @param metadataFileIdentifier the metadataFileIdentifier to set
	 */
	public void setMetadataFileIdentifier(String metadataFileIdentifier) {
		this.metadataFileIdentifier = metadataFileIdentifier;
	}
	

	/**
	 * @return the metadataMdIdentifier
	 */
	public String getMetadataMdIdentifier() {
		return metadataMdIdentifier;
	}


	/**
	 * @param metadataMdIdentifier the metadataMdIdentifier to set
	 */
	public void setMetadataMdIdentifier(String metadataMdIdentifier) {
		this.metadataMdIdentifier = metadataMdIdentifier;
	}


	/**
	 * get register Files
	 * @return
	 */
	public List<DataFile> getDataFiles() {
		return dataFiles;
	}
	
	/**
	 * get register Files
	 * @return
	 */
	public List<DataLayer> getDataLayers() {
		return dataLayers;
	}

	/**
	 * add register File
	 * @param dataFile
	 */
	public void addDataFile(DataFile dataFile) {
		dataFiles.add(dataFile) ;
	}
	

	public void addDataLayer(DataLayer dataLayer) {
		dataLayers.add(dataLayer) ;
	}
	
	
	public Envelope getDocumentExtent() {
		return documentExtent;
	}


	public void setDocumentExtent(Envelope documentExtent) {
		this.documentExtent = documentExtent;
	}


	/**
	 * simplify test
	 */
	public void sortFiles(){
		Collections.sort(dataFiles, new DataFileComparator());
		Collections.sort(dataLayers, new DataFileComparator());
	}
	
}



