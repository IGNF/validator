package fr.ign.validator.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class IdgestExtractor {
	
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("IDGEST_EXTRACTOR");
	
	/**
	 * Le fichier SERVITUDE
	 */
	private File servitudeFile ;

	/**
	 * Construction à partir du fichier SERVITUDE
	 * @param servitudeFile
	 */
	public IdgestExtractor(File servitudeFile){
		this.setServitudeFile(servitudeFile) ;
	}
	
	
	/**
	 * 
	 * @param filePath
	 */
	public void findIdGest(){

		if (! this.getServitudeFile().exists()) {
			log.error(MARKER,this.getServitudeFile() + " does not exists");
			System.err.println(this.getServitudeFile() + " does not exists");
			System.exit(1);
		}

		TableReader reader = null ;
		try {
			reader = TableReader.createTableReaderDetectCharset(this.getServitudeFile());
		} catch (IOException e) {
			log.error("error reading table");
			System.err.println("error reading table");
			System.exit(1);
		} 
		
		/*
		 * Lecture du csv
		 */
		String[] inputHeader = reader.getHeader() ;
		int index = 0;
		for ( int i = 0; i < inputHeader.length; i++ ){
			if( inputHeader[i].equals("IdGest")){
				index = i;
				break;
			}
		}
		
		if ( index == 0 ){
			log.error("No attribute idGest");
			System.err.println("No attribute idGest");
			System.exit(1);
		}
		
		/*
		 * Lecture de chaque Feature
		 */
		String idGest = "";
		while ( reader.hasNext() ){
			String[] inputRow = reader.next() ;
			if( inputRow[index] != "" ){
				idGest = inputRow[index];
				break;
			}
		}
		
		File csvFile = new File( 
				this.getServitudeFile().getParent(),
				FilenameUtils.getBaseName(this.getServitudeFile().getName())+".csv"
				);
					
		if(! csvFile.delete()){
			log.error("Delete operation has failed.");
			System.err.println("Delete operation has failed.");
			System.exit(1);
		}
		
		if ( idGest == "" ){
			log.error("attribute idGest is empty");
			System.err.println("attribute idGest is empty");
			System.exit(1);
		}
		
		File resultFile = new File( this.getServitudeFile().getParent(),"idGest.txt");

		try {
			
			if (!resultFile.exists()) {
				resultFile.createNewFile();
			}
			FileWriter fw = new FileWriter( resultFile.getAbsoluteFile() );
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(idGest);
			bw.close();
			
		} catch (IOException e) {
			System.exit(1);
		}
	}

	public File getServitudeFile() {
		return servitudeFile;
	}

	public void setServitudeFile(File servitudeFile) {
		this.servitudeFile = servitudeFile;
	}

}
