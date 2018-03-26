package fr.ign.validator.cnig.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.tools.CompanionFileUtils;
import fr.ign.validator.tools.TableReader;

/**
 * Extracts IdGest from a "servitude" file
 */
public class IdgestExtractor {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("IDGEST_EXTRACTOR");
	
	private static final String IDGEST_COLUMN_NAME = "IdGest";

	public IdgestExtractor(){
	}

	/**
	 * 
	 * @param filePath
	 */
	public String findIdGest(File servitudeFile) {

		if (!servitudeFile.exists()) {
			log.error(MARKER, servitudeFile + " does not exists");
			return null;
		}

		TableReader reader = null;
		try {
			reader = TableReader.createTableReaderDetectCharset(servitudeFile);
		} catch (IOException e) {
			log.error(MARKER, "error reading table");
			return null;
		}
		
		/*
		 * Reading csv
		 */
		int index = reader.findColumn(IDGEST_COLUMN_NAME);
		if ( index < 0 ) {
			log.error(MARKER, "No attribute idGest");
			return null;
		}

		/*
		 * Finding first not-empty idGest
		 */
		String idGest = null;
		while (reader.hasNext()) {
			String[] inputRow = reader.next();
			String candidate = inputRow[index];
			if ( ! candidate.isEmpty() ){
				idGest = candidate;
				break;
			}
		}
		
		/*
		 * Deleting temporary csv file
		 */
		if ( ! FilenameUtils.getExtension(servitudeFile.getName()).equals("csv") ){
			File csvFile = CompanionFileUtils.getCompanionFile(servitudeFile, "csv");
			if ( ! csvFile.delete() ) {
				log.error(MARKER, "Delete operation has failed.");
			}
		}
		
		return idGest;
	}

}
