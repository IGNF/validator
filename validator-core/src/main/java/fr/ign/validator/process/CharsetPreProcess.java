package fr.ign.validator.process;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.exception.InvalidMetadataException;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.code.CharacterSetCode;
import fr.ign.validator.metadata.gmd.MetadataISO19115;
import fr.ign.validator.model.file.MetadataModel;

/**
 * Pre-process
 * <ul>
 * 		<li>Makes sure of the presence of a metadata file</li>
 * 		<li>Extracts file encoding from these metadata</li>
 * 		<li>Verify the existence of particular attributes</li>
 * </ul>
 * 
 * @author CBouche
 */
public class CharsetPreProcess implements ValidatorListener {
	public static final Logger log = LogManager.getRootLogger() ;
	private static final Marker MARKER = MarkerManager.getMarker("CharsetPreProcess");
	
	
	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		//RAS
	}

	/**
	 * Reading charset from first metadata file found
	 */
	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		log.info(MARKER, "Search data charset in metadata files...");
		Charset charset = readCharsetFromMetadata(context, document);
		if ( null == charset ){
			log.warn(MARKER, "Charset not found in metadata files!");
		}else{
			context.setEncoding(charset);
		}
		log.info(MARKER, "Charset : {}",context.getEncoding());
	}

	/**
	 * Read charset from metadata (returns null if not found)
	 * @param context
	 * @param document
	 * @return
	 */
	private Charset readCharsetFromMetadata(Context context, Document document) {
		List<DocumentFile> metadataFiles = document.getDocumentFiles(MetadataModel.class);
		if ( metadataFiles.isEmpty() ){
			return null;
		}
		if ( metadataFiles.size() > 1 ){
			context.report(
				CoreErrorCodes.METADATA_MULTIPLE_FILES,
				formatFiles(context,metadataFiles)
			);
		}
		
		File metadataFile = metadataFiles.get(0).getPath() ;
		try {
			Metadata reader = MetadataISO19115.readFile(metadataFile);
			CharacterSetCode characterSet = reader.getCharacterSet();
			if ( characterSet == null ){
				log.info(MARKER,
					"{} - characterSet is not defined in metadata file ", 
					metadataFile
				);
				return null;
			}
			Charset dataEncoding = characterSet.getCharset() ;
			log.info(MARKER,
				"{} - characterSet : {} converted to java value {}", 
				metadataFile,
				characterSet.getValue(),
				dataEncoding
			);
			return dataEncoding;
		}catch(InvalidMetadataException e){
			log.error(MARKER,
				"fail to read metadata file : {}", 
				e.getMessage()
			);
			return null;
		}
	}
	
	/**
	 * Generates a concatenated list of filenames
	 * @param context
	 * @param matchingFiles
	 * @return
	 */
	private String formatFiles(Context context, List<DocumentFile> matchingFiles) {
		StringBuilder sb = new StringBuilder();
		boolean first = true ;
		for (DocumentFile documentFile : matchingFiles) {
			if ( ! first ){
				sb.append(", ") ;
			}else{
				first = false ;
			}
			sb.append( context.relativize(documentFile.getPath()) );
		}
		return sb.toString() ;
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		//RAS
	}
	
}
