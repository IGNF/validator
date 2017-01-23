package fr.ign.validator.cnig.info;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

/** 
 * 
 * Représente un répertoire de l'arborescence de fichier. 
 * 
 * Il contient des DataFile et DataLayer.
 * 
 * 
 * @author CBouche
 *
 */
public class DocumentInfo {
	
	/**
	 * String repertoryName
	 */
	private String name ;
	
	/**
	 * La référence de saisie (champ TYPEREF de la table DOC_URBA)
	 */
	private String typeref ;

	/**
	 * Le champ fileIdentifier de la fiche de métadonnées
	 */
	private String metadataFileIdentifier ;
		
	/**
	 * Le champ MD_Identifier de la fiche de métadonnées
	 */
	private String metadataMdIdentifier ;
	
	/**
	 * La géométrie du document (ex : union des zones urba)
	 */
	private Geometry geometry ;
	
	/**
	 * List : liste des fichiers du repertoire
	 */
	private List<DataFile> dataFiles = new ArrayList<DataFile>() ;
	

	/**
	 * List : liste des fichiers du repertoire
	 */
	private List<DataLayer> dataLayers = new ArrayList<DataLayer>() ;

	/**
	 * Constructor
	 */
	public DocumentInfo(String repertoryName) {
		this.name = repertoryName ;
	}
	
	
	/**
	 * get repertory name
	 * @return
	 */
	public String getName() {
		return name;
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

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
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
}
