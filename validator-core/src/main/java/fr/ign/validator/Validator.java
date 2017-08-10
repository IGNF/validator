package fr.ign.validator;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.model.Document;
import fr.ign.validator.model.DocumentModel;

/**
 * Exécute les différentes étapes de validation d'un document
 * 
 * @deprecated utiliser directement validate sur Document
 * 
 * @author CBouche
 *
 */
public class Validator {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker VALIDATOR = MarkerManager.getMarker("VALIDATOR") ;
	

	/**
	 * Le contexte de validation
	 */
	private Context context ;
	
	/**
	 * Constructeur d'un validateur pour le modele de donnees en entree
	 * @param configFolder
	 * @param modelFormat 
	 * @param proxyString 
	 */
	public Validator(Context context) {
		this.context = context ;
		
		
	}

	/**
	 * Get Context
	 * @return
	 */
	public Context getContext(){
		return context ;
	}

	/**
	 * run validation 
	 * 
	 * @param validationDirectory
	 * @throws Exception 
	 */
	public Document validate(DocumentModel documentModel, File documentPath) throws Exception {
		/*
		 * Chargement des parametres generaux
		 */
		Document document = new Document(documentModel,documentPath);
		document.validate(context);
		return document;
	}

}


