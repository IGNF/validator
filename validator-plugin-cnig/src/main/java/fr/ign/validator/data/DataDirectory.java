package fr.ign.validator.data;

import java.util.ArrayList;
import java.util.List;

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
public class DataDirectory {
	
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
	 * List : liste des fichiers du repertoire
	 */
	private List<DataFile> registerFiles = new ArrayList<DataFile>() ;
	

	/**
	 * List : liste des fichiers du repertoire
	 */
	private List<DataLayer> registerLayers = new ArrayList<DataLayer>() ;
	
	
	/**
	 * List : liste des sous repertoire du repertoire
	 */
	private List<DataDirectory> registerSubRepertories = new ArrayList<DataDirectory>() ;

	
	/**
	 * Constructor
	 */
	public DataDirectory(String repertoryName) {
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


	/**
	 * get register Files
	 * @return
	 */
	public List<DataFile> getRegisterFiles() {
		return registerFiles;
	}
	
	/**
	 * get register Files
	 * @return
	 */
	public List<DataLayer> getRegisterLayers() {
		return registerLayers;
	}
	
	/**
	 * get register Repertories
	 * @return
	 */
	public List<DataDirectory> getRegisterSubRepertories() {
		return registerSubRepertories;
	}
	
	/**
	 * add register File
	 * @param registerFile
	 */
	public void addRegisterFile(DataFile registerFile) {
		registerFiles.add(registerFile) ;
	}
	
	
	/**
	 * add register repertory
	 * @param registerRepertory
	 */
	public void addRegisterRepertory(DataDirectory registerRepertory) {
		registerSubRepertories.add(registerRepertory) ;
	}


	public void addRegisterLayer(DataLayer layer) {
		registerLayers.add(layer) ;
		
	}
}
