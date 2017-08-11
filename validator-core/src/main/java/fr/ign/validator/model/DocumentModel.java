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

import fr.ign.validator.data.Document;
import fr.ign.validator.validation.Validator;
import fr.ign.validator.validation.document.DocumentDirectoryNameValidator;
import fr.ign.validator.validation.document.DocumentMandatoryFileValidator;

/**
 * Décrit un document composé d'une liste de fichiers.
 * 
 * @author MBorne
 */
@XmlRootElement(name="document")
@XmlType(propOrder = { "name", "regexp", "fileModels" })
public class DocumentModel implements Model {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("DocumentModel") ;
	
	/**
	 * Le nom du fichier
	 */
	private String name ;
	/**
	 * Le nom du dossier donné par une expression régulière
	 */
	private String regexp ;
	/**
	 * La liste des fichiers du document
	 */
	private List<FileModel> fileModels = new ArrayList<FileModel>();
	/**
	 * La liste des Feature
	 */
	private FeatureCatalogue featureCatalogue = new FeatureCatalogue() ;

	/**
	 * La liste des validateurs sur le document
	 */
	private List< Validator<Document> > validators = new ArrayList<Validator<Document>>();
	
	/**
	 * Construction d'un modèle de document avec les contraintes par défaut
	 */
	public DocumentModel(){
		addValidator(new DocumentDirectoryNameValidator());
		addValidator(new DocumentMandatoryFileValidator());
	}
	
	
	/**
	 * Renvoie le nom du document
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * Définit le nom du document
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Renvoie l'expression régulière correspondant au dossier
	 * @return
	 */
	public String getRegexp() {
		return regexp;
	}
	/**
	 * Définit l'expression régulière correspondant au dossier du document
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
	public void setFileModels(List<FileModel> documentFiles) {
		this.fileModels = documentFiles;
	}
	
	/**
	 * Renvoie le FeatureCatalogue
	 * @return
	 */
	public FeatureCatalogue getFeatureCatalogue(){
		return this.featureCatalogue ;
	}
	/**
	 * Définit le FeatureCatalogue
	 * @param featureCatalogue
	 */
	@XmlTransient
	public void setFeatureCatalogue(FeatureCatalogue featureCatalogue){
		this.featureCatalogue = featureCatalogue ;
	}

	/**
	 * Trouve la définition de fichier correspondant à file en prenant en compte le path complet
	 * @param documentPath
	 * @param file
	 * @return
	 */
	public FileModel getMatchingFileModelByPath(File documentPath, File file) {
		/*
		 * On parcours la mapRule a la recherche
		 * d'une correspondance avec le fichier
		 */
		for  (FileModel fileModel : fileModels ) {
			if ( fileModel.matchPath(file) ){
				return fileModel ;
			}
		}
		return null;
	}
	
	/**
	 * Trouve la définition de fichier correspondant à file en prenant en compte le path complet
	 * @param documentPath
	 * @param file
	 * @return
	 */
	public FileModel getMatchingFileModelByName(File documentPath, File file) {
		/*
		 * On parcours la mapRule a la recherche
		 * d'une correspondance avec le fichier
		 */
		for  (FileModel fileModel : fileModels ) {
			if ( fileModel.matchFilename(file) ){
				return fileModel ;
			}
		}
		return null;
	}

	
	/**
	 * Ajout d'un validateur sur le document
	 * @param validator
	 */
	public void addValidator(Validator<Document> validator) {
		this.validators.add(validator);
	}
	
	/**
	 * Récupération des validateurs sur le document
	 * @return
	 */
	public List<Validator<Document>> getValidators(){
		return this.validators ;
	}
 	
}

