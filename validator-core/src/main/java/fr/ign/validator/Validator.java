package fr.ign.validator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.model.Document;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.process.CharsetPreProcess;
import fr.ign.validator.process.FilterMetadataPreProcess;
import fr.ign.validator.process.NormalizePostProcess;
import fr.ign.validator.process.PrepareValidationDirectory;

/**
 * Class Validator
 * Coeur du programme
 * Exécute les différentes étapes de validation d'un MultiDocument
 * @author CBouche
 *
 */
public class Validator {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker VALIDATOR = MarkerManager.getMarker("VALIDATOR") ;
	
	public static final String VALIDATION_DIRECTORY_NAME = "validation" ;

	public static final String VALIDATION_REPORT_NAME    = "validation" ;

	/**
	 * Le contexte de validation
	 */
	private Context context ;
	/**
	 * Validator plugins
	 */
	private List<Plugin> plugins = new ArrayList<Plugin>() ;
	
	/**
	 * Constructeur d'un validateur pour le modele de donnees en entree
	 * @param configFolder
	 * @param modelFormat 
	 * @param proxyString 
	 */
	public Validator(Context context) {
		this.context = context ;
		
		registerDefaultListeners();

		/*
		 * Chargement des processus specifiques aux différents plugins installés
		 */
		loadPlugins(); 
	}

	/**
	 * Get Context
	 * @return
	 */
	public Context getContext(){
		return context ;
	}

	/**
	 * Chargement des processus par défaut de l'application
	 * - (re-)création du dossier de validation
	 * - preparation des donnes en vue de validation (csv)
	 * - preparation des donnes en vue d'export en base (shp)
	 * - extraction d'informations sur les fichiers traitées
	 */
	private void registerDefaultListeners(){
		context.addListener( new PrepareValidationDirectory() );
		// before CharsetPreProcess
		context.addListener( new FilterMetadataPreProcess() );
		context.addListener( new CharsetPreProcess() );
		context.addListener( new NormalizePostProcess() ); 
	}

	
	/**
	 * Chargement des plugins
	 */
	private void loadPlugins(){
		ServiceLoader<Plugin> loader = ServiceLoader.load( Plugin.class );
		for (Plugin plugin : loader) {
			addPlugin(plugin);
		}
	}



	/**
	 * run validation 
	 * 
	 * TODO :
	 * - au besoin, ajouter un attribut Validator sur les PreProcess/PostProcess pour récupérer ces variables.
	 * 
	 * @param validationDirectory
	 * @throws Exception 
	 */
	public void validate(DocumentModel documentModel, File documentPath) throws Exception {
		log.info(VALIDATOR, "Validation de {} avec le modèle {}",
			documentPath,
			documentModel.getName()
		);
		
		/*
		 * Chargement des parametres generaux
		 */
		context.setCurrentDirectory(documentPath) ;
		File validationDirectory = new File( documentPath.getParentFile(), VALIDATION_DIRECTORY_NAME ) ;
		context.setValidationDirectory( validationDirectory ) ;
		
		Document document = new Document(documentModel,documentPath);
		document.validate(context);
	}

	
	/**
	 * Get plugins
	 * @return
	 */
	public List<Plugin> getPlugins() {
		return plugins ;
	}

	/**
	 * Register a new plugin
	 * @param plugin
	 */
	public void addPlugin( Plugin plugin ){
		log.info( VALIDATOR, "Chargement du plugin '{}'", plugin.getName() );
		plugins.add(plugin);
		plugin.setup(context);
	}
			
}
