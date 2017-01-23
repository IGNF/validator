package fr.ign.validator.cnig.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.tools.TableReader;

/**
 * 
 * Utilitaire pour l'extraction de la référence de saisie (TYPEREF : 01 ou 02)
 * dans la table DOCURBA
 * 
 * @author MBorne
 * 
 */
public class TyperefExtractor {
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("TYPEREF_EXTRACTOR");

	/**
	 * Recherche d'un
	 * 
	 * @param documentName
	 * @return
	 */
	public String findTyperef(File docUrbaFile, String documentName) {
		String regexpIDURBA = IdurbaUtils.getRegexp(documentName);
		if (null == regexpIDURBA) {
			log.info(MARKER, "Le nom du document ne correspond pas à un DU, TYPEREF ne sera pas extrait");
			return null;
		}

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

			// recherche de la ligne correspondant à documentName
			while (reader.hasNext()) {
				String[] row = reader.next();
				String idurba = row[indexIdurba];

				if (null == idurba || idurba.isEmpty()) {
					continue;
				}

				if (!idurba.matches(regexpIDURBA)) {
					continue;
				}

				String result = row[indexTyperef];

				// si IDURBA est trouvé mais que TYPEREF est null, on le met par
				// défaut à 01
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
