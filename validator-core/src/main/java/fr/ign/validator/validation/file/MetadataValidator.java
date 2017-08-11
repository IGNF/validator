package fr.ign.validator.validation.file;

import java.io.File;
import java.io.IOException;

import org.jdom.JDOMException;

import fr.ign.validator.Context;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.error.ErrorCode;
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

	@Override
	public void validate(Context context, DocumentFile documentFile) {
		if ( ! ( documentFile.getFileModel() instanceof MetadataModel ) ){
			throw new RuntimeException(
				"Le validateur MetadataValidator supporte uniquement le type MetadataModel"
			);
		}
		
		try {
			File file = documentFile.getPath() ;
			MetadataReader reader = new MetadataReader(file);
			
			/*
			 * Validation du fileIdentifier
			 */
			{
				String fileIdentifier = reader.getFileIdentifier() ;
				if ( null == fileIdentifier || "" == fileIdentifier ){
					context.report(
						ErrorCode.METADATA_FILEIDENTIFIER_NOT_FOUND,
						context.relativize(file)
					);
				}
			}
			
			{
				String mdIdentifier = reader.getMDIdentifier() ;
				if ( null == mdIdentifier || "" == mdIdentifier ){
					context.report(
						ErrorCode.METADATA_MD_IDENTIFIER_NOT_FOUND,
						context.relativize(file)
					);
				}
			}
			
		} catch (JDOMException e) {
			context.report( 
				ErrorCode.METADATA_INVALID_FILE,
				context.relativize(documentFile.getPath())
			);
		} catch (IOException e) {
			context.report( 
				ErrorCode.METADATA_INVALID_FILE,
				context.relativize(documentFile.getPath())
			);
		}
	}

	
}
