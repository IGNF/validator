package fr.ign.validator.process;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jdom.JDOMException;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.reader.MetadataReader;

/**
 * Pré traitement
 * <ul>
 * <li>S'assure de la présence d'un fichier de metadonnées</li>
 * <li>Extrait l'encodage des fichiers depuis ces metadonnées</li>
 * <li>Vérifie la présence de certains attributs</li>
 * </ul>
 * 
 * @author CBouche
 */
public class CharsetPreProcess implements ValidatorListener {
	public static final Logger log = LogManager.getRootLogger() ;
	private static final Marker MARKER = MarkerManager.getMarker("PREPROCESS_METADATA");
	
	
	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		//RAS
	}

	/**
	 * Lecture de la charset à partir du premier fichier de métadonnées rencontré
	 */
	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		log.info(MARKER, "Récupération de la charset dans les fichiers de métadonnées...");
		readCharsetFromMetadata(context, document);
		
		if ( null == context.getEncoding() ){
			log.warn(MARKER, "La charset n'a pas pu être détectée à partir de la fiche des métadonnées");
			context.setEncoding(StandardCharsets.UTF_8);
		}
	}
	
	/**
	 * Lecture de la charset à partir du fichier de métadonnées
	 * @param context
	 * @param document
	 * @throws JDOMException
	 * @throws IOException
	 */
	private void readCharsetFromMetadata(Context context, Document document)
			throws JDOMException, IOException {
		for ( FileModel fileModel : context.getDocumentModel().getFileModels() ) {
			if ( fileModel instanceof MetadataModel ){
				List<DocumentFile> matchingFiles = document.getDocumentFilesByModel(fileModel) ;
				if ( matchingFiles.isEmpty() ){
					continue ;
				}

				File metadataFile = matchingFiles.get(0).getPath() ;
				
				if ( matchingFiles.size() > 1 ){
					context.report(
						ErrorCode.METADATA_MULTIPLE_FILES,
						formatFiles(context,matchingFiles)
					);
				}
				
				/*
				 * Lecture de l'encodage à partir du fichier de métadonnées
				 */
				MetadataReader reader = new MetadataReader(metadataFile);
				Charset dataEncoding = reader.getCharacterSetCode() ;
				log.info(MARKER,
					"{} détecté (encodage des caractères : {})", 
					metadataFile,
					dataEncoding
				);
				context.setEncoding( dataEncoding );
			}
		}
	}
	
	/**
	 * Génération d'une liste concaténée des noms de fichiers
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
