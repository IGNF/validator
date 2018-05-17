package fr.ign.validator.cnig.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.cnig.idurba.IdurbaHelper;
import fr.ign.validator.tools.TableReader;

/**
 * 
 * Extracts typeref (cadastral reference) from DOC_URBA table
 * 
 * 
 * @author MBorne
 * 
 */
public class TyperefExtractor {
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("TYPEREF_EXTRACTOR");

	/**
	 * The document model providing IDURBA naming convention
	 */
	private IdurbaHelper idurbaHelper ;
	
	/**
	 * 
	 * @param idurbaHelper
	 */
	public TyperefExtractor(IdurbaHelper idurbaHelper){
		this.idurbaHelper = idurbaHelper;
	}
	
	/**
	 * Finds a typeref in docUrbaFile according to documentName
	 * 
	 * @param documentName
	 * @return
	 */
	public String findTyperef(File docUrbaFile, String documentName) {
		if (!docUrbaFile.exists()) {
			log.error(MARKER, "Impossible d'extraire TYPEREF, DOC_URBA non trouvée");
			return null;
		}

		try {
			TableReader reader = TableReader.createTableReader(docUrbaFile, StandardCharsets.UTF_8);

			int indexTyperef = reader.findColumn("TYPEREF");
			if (indexTyperef < 0) {
				log.error(MARKER, "Champ TYPEREF non défini dans DOC_URBA");
				return null;
			}
			int indexIdurba = reader.findColumn("IDURBA");
			if (indexIdurba < 0) {
				log.error(MARKER, "IDURBA non défini dans DOC_URBA");
				return null;
			}

			/*
			 * Search of row corresponding to documentName
			 */
			while (reader.hasNext()) {
				String[] row = reader.next();
				String idurba = row[indexIdurba];

				if (null == idurba || idurba.isEmpty()) {
					continue;
				}

				if ( ! idurbaHelper.isValid(idurba,documentName) ){
					continue;
				}

				String result = row[indexTyperef];

				/*
				 * if idUrba found an typeref is null, default is 01
				 */
				if (null == result) {
					result = "01";
				}

				log.info(MARKER, "TYPEREF pour IDURBA={} : {}", idurba, result);
				return result;
			}

		} catch (Exception e) {
			log.error(MARKER, "Erreur dans la lecture de DOC_URBA.csv");
			return null;
		}

		log.error(MARKER, "Impossible de trouver une ligne correspondant à {} dans DOC_URBA.csv", documentName);
		return null;
	}

}
