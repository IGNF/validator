package fr.ign.validator.data.file;

import java.io.File;
import java.io.IOException;

import fr.ign.validator.Context;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.Header;
import fr.ign.validator.data.Row;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.exception.InvalidCharsetException;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.tools.TableReader;

public class TableFile extends DocumentFile {

	private TableModel fileModel ; 
	
	public TableFile(TableModel fileModel, File path) {
		super(path);
		this.fileModel = fileModel;
	}

	@Override
	public TableModel getFileModel() {
		return fileModel;
	}
	
	@Override
	protected void validateContent(Context context) {
		validateTable(context,fileModel,getPath()) ;
	}

	/**
	 * File validation
	 * @param context
	 * @param matchingFile
	 */
	protected void validateTable(Context context, TableModel tableModel, File matchingFile){
		/*
		 * csv file validation
		 */
		log.debug(MARKER, "Lecture des données de la table {}...", matchingFile) ;
		try {
			TableReader reader;
			try {
				reader = TableReader.createTableReader(matchingFile,context.getEncoding());
			}  catch (InvalidCharsetException e) {
				log.error(MARKER, "Charset invalide détectée pour {}", matchingFile);
				context.report(
					CoreErrorCodes.TABLE_UNEXPECTED_ENCODING,
					context.getEncoding().toString()
				);
				log.info(MARKER, "Tentative d'autodétection de la charset pour la validation de {}",matchingFile);
				reader = TableReader.createTableReaderDetectCharset(matchingFile) ;
			}
			
			/*
			 * header validation
			 */
			String[] columns = reader.getHeader() ;
			FeatureTypeMapper mapping = new FeatureTypeMapper(columns, tableModel.getFeatureType()) ;
			Header header = new Header(matchingFile, mapping);
			header.validate(context);

			/*
			 * feature validation
			 */
			int count = 0 ;
			while ( reader.hasNext() ){
				count++ ;
				
				Row row = new Row(count,reader.next(),mapping);
				row.validate(context);
			}
			
			/*
			 * check for empty file
			 */
			if ( count == 0 ){
				context.report(
					CoreErrorCodes.FILE_EMPTY,
					context.relativize(matchingFile)						
				);
			}
			
			log.info( MARKER, "{} objet validé(s)", count );
		}  catch (IOException e) {
			context.report(
				CoreErrorCodes.FILE_NOT_OPENED,
				context.relativize(matchingFile)
			);
			return ;
		}
	}
	
}
