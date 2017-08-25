package fr.ign.validator.report;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.Row;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FileModel;


/**
 * 
 * Writes an XML validation report using log4j2
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
	public ReportBuilderLegacy(File validationRapport) {
		ThreadContext.put("path", validationRapport.getAbsolutePath().toString());
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
			// Directory | <code> | <level> | <file> | <message>
			logMessage = "Directory" ;
			logMessage += " | "+error.getCode() ;             // Code
			logMessage += " | "+error.getLevel() ;            // Type 
			logMessage += " | "+getFileName(context) ;        // Fichier
			logMessage += " | "+error.getMessage() ;          // Message
			
		}else if( error.getScope().equals(ErrorScope.HEADER) ){
			// Header | <code> | <fileModel> | <level> | <column> | <value> | <modelValue> | <message>
			logMessage = "Header" ;
			logMessage += " | "+error.getCode() ;              // Code
			logMessage += " | "+getFileModelName(context);     // Table
			logMessage += " | "+error.getLevel() ;             // Type
			logMessage += " | "+getAttributeName(context);     // Champ (déprécié, présent dans les messages)
			logMessage += " | ";                               // Courant (déprécié, présent dans les messages)
			logMessage += " | "+getDocumentModelName(context); // Modèle
			logMessage += " | "+error.getMessage();            // Message
			
		}else if( error.getScope().equals(ErrorScope.FEATURE) ){
			// Feature | <code> | <fileModel> | <level> | <column> | <line> | <currentvalue> | <type> | <model> | <message>
			logMessage = "Feature" ;
			logMessage += " | "+error.getCode() ;              // Code
			logMessage += " | "+getFileModelName(context);     // Table
			logMessage += " | "+error.getLevel();              // Type
			logMessage += " | "+getAttributeName(context);     // Champ
			logMessage += " | "+getLine(context);              // Identifiant
			logMessage += " | ";                               // valeur (déprécié, présent dans les messages)
			logMessage += " | ";                               // taille/type (déprécié, présent dans les messages)
			logMessage += " | "+getDocumentModelName(context); // Modèle
			logMessage += " | "+ error.getMessage();           // Message
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
	

	/**
	 * Renvoie le nom du DocumentFile courant s'il existe, le nom du document sinon
	 * @param context
	 * @return
	 */
	public String getFileName(Context context){
		DocumentFile documentFile = context.getDataByType(DocumentFile.class);
		if ( documentFile != null ){
			return context.relativize( documentFile.getPath() ) ;
		}
		Document document = context.getDataByType(Document.class);
		if ( document != null ){
			return document.getDocumentName()+"/";
		}
		return "";
	}

	
	/**
	 * TODO clarifier
	 * @param context
	 * @return
	 */
    public String getDocumentModelName(Context context){
    	DocumentModel documentModel = context.getModelByType(DocumentModel.class);
		if ( documentModel != null ){
			return documentModel.getName();
		}
		return "";
	}
    
    /**
     * Renvoie le nom de l'attribut
     * @param context
     * @return
     */
    private String getAttributeName(Context context){
    	AttributeType<?> attribute = context.getModelByType(AttributeType.class);
		if ( attribute != null ){
			return attribute.getName();
		}
		return "";
	}
	
    /**
     * Renvoie le nom du modèle
     * @param context
     * @return
     */
    private String getFileModelName(Context context){
		FileModel fileModel = context.getModelByType(FileModel.class);
		if ( fileModel != null ){
			return fileModel.getName();
		}
		return "";
	}
	
	/**
	 * Renvoie le numéro de ligne
	 * @param context
	 * @return
	 */
	public String getLine(Context context){
		Row row = context.getDataByType(Row.class);
		if ( row != null ){
			return ""+row.getLine();
		}
		return "";
	}
	

}
