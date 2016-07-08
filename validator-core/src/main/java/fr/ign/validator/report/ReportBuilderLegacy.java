package fr.ign.validator.report;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.validator.Context;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.Model;


/**
 * 
 * Report errors in logger
 * 
 * @author FCerizay
 *
 */
public class ReportBuilderLegacy implements ReportBuilder {
	
	/** 
	 * Instance de log4j
	 */
	public static final Logger log = LogManager.getLogger( ReportBuilderLegacy.class ) ;
		
	/**
	 * Constructor
	 * @throws IOException 
	 */
	public ReportBuilderLegacy() {
		
	}
	
	/**
	 Log le message d'erreur au niveau qui lui correspond
	 * 1 ==> Debug, message destiné au programmeur
	 * 2 ==> Info, message d'information sur une action particulière, permet de suivre l'avancement de la validation
	 * 3 ==> Warning, message portant sur une erreur empéchant la publication légale des données
	 * 4 ==> Error, message portant sur une erreur empéchant la publication légale et technique des données
	 * 5 ==> Fatal, message portant sur une erreur empéchant la poursuite de la validation 
	 */
	@Override
	public void addError(Context context, ValidatorError error) {
		
		String logMessage = error.getScope().toString() ;
		
		if ( error.getScope().equals(ErrorScope.DIRECTORY) ){
			
			logMessage = "Directory" ;
			logMessage += " | "+error.getCode() ;
			logMessage += " | "+error.getLevel() ;
			logMessage += " | "+getFile(context);
			logMessage += " | "+error.getMessage() ;
			
		}else if( error.getScope().equals(ErrorScope.HEADER) ){
			
			logMessage = "Header" ;
			logMessage += " | "+error.getCode() ;
			logMessage += " | "+getTable(context);
			logMessage += " | "+error.getLevel() ;
			logMessage += " | "+getField(context);
			logMessage += " | "; //courant
			logMessage += " | "+getModel(context);
			logMessage += " | "+error.getMessage();
			
		}else if( error.getScope().equals(ErrorScope.FEATURE) ){

			logMessage = "Feature" ;
			logMessage += " | "+error.getCode() ;
			logMessage += " | "+getTable(context);
			logMessage += " | "+error.getLevel();
			logMessage += " | "+getField(context);
			logMessage += " | "+getLine(context);
			logMessage += " | "; // valeur
			logMessage += " | "; // taille/type
			logMessage += " | "+getModel(context);
			logMessage += " | "+ error.getMessage();
		}
		
		
		ErrorLevel level  = error.getLevel() ;
		
		if ( level.equals( ErrorLevel.FATAL ) ) {
			log.fatal(logMessage) ;
		}else if ( level.equals( ErrorLevel.ERROR ) ) {
			log.error(logMessage) ;
		}else if ( level.equals( ErrorLevel.WARNING ) ) {
			log.warn(logMessage) ;
		}else if ( level.equals( ErrorLevel.INFO ) ) {
			log.info(logMessage) ;
		}else if ( level.equals( ErrorLevel.DEBUG ) ) {
			log.debug(logMessage) ;
		}else {
			throw new RuntimeException( String.format(
				"Le niveau d'erreur {} n'est pas pris en compte", level 
			) ) ;
		}
	}
	

	
	public String getFile(Context context){
		
		String file = "";
		List<Model> model = context.getModelStack();
		
		if( model.size() > 1 ){
			file = model.get(model.size()-1).getName();
		}else{
			for (String data : context.getDataStack()){
				file = data+"/" ;
			}
		}
		return file ;
	}
	
	
    public String getModel(Context context){
		
		String modele ="";
		
		List<Model> model = context.getModelStack();
		
		if( model.size() > 0 ){
			modele = model.get(0).getName();
		}
		return modele;
	}
    
    public String getField(Context context){
		
		String field ="";
		
		List<Model> model = context.getModelStack();
		
		if( model.size() > 2 ){
			field = model.get(2).getName();
		}
		return field;
	}
	
	public String getTable(Context context){
		
		String table ="";
		
		List<Model> model = context.getModelStack();
		
		if( model.size() > 1 ){
			table = model.get(1).getName();
		}
		return table;
	}
	
	public String getLine(Context context){
		
		String value ="";
		
		List<String> data = context.getDataStack();
		
		if( data.size() > 2 ){
			value = data.get(2);
		}
		return value;
	}
	
}
