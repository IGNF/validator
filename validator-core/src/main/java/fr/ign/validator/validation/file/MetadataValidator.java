package fr.ign.validator.validation.file;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.exception.InvalidMetadataException;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.reader.MetadataReader;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validation d'une fiche de métadonnées
 * 
 * @author MBorne
 *
 */
public class MetadataValidator implements Validator<DocumentFile> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("MetadataValidator") ;	
	
	@Override
	public void validate(Context context, DocumentFile documentFile) {
		assert ( documentFile.getFileModel() instanceof MetadataModel ) ;

		try {
			File file = documentFile.getPath() ;
			MetadataReader reader = new MetadataReader(file);
			
			/*
			 * Validation du fileIdentifier
			 */
			{
				String fileIdentifier = reader.getFileIdentifier() ;
				log.info(MARKER, "fileIdentifier : {}", fileIdentifier);
				if ( null == fileIdentifier || "" == fileIdentifier ){
					context.report(
						ErrorCode.METADATA_FILEIDENTIFIER_NOT_FOUND,
						context.relativize(file)
					);
				}
			}
			
			{
				String mdIdentifier = reader.getMDIdentifier() ;
				log.info(MARKER, "mdIdentifier : {}", mdIdentifier);
				if ( null == mdIdentifier || "" == mdIdentifier ){
					context.report(
						ErrorCode.METADATA_MD_IDENTIFIER_NOT_FOUND,
						context.relativize(file)
					);
				}
			}
			
		} catch (InvalidMetadataException e) {
			context.report( 
				ErrorCode.METADATA_INVALID_FILE,
				context.relativize(documentFile.getPath())
			);
		} 
	}

	
}
