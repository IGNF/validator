package fr.ign.validator.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.xml.sax.SAXException;

import fr.ign.validator.exception.OgrBadVersionException;
import fr.ign.validator.exception.OgrNotFoundException;
import fr.ign.validator.tools.internal.FixGML;



/**
 * 
 * File converter to different formats based on GDAL - ogr2ogr
 * 
 * @author MBorne
 * @author CBouche
 * 
 */
public class FileConverter {
	public static final Marker MARKER = MarkerManager.getMarker("FileConverter") ;
	public static final Logger log = LogManager.getRootLogger() ;
	
	public static final String ENCODING_UTF8   = "UTF-8" ;
	public static final String ENCODING_LATIN1 = "ISO-8859-1" ;

	private static FileConverter instance = new FileConverter();
	
	/**
	 * @brief path to ogr2ogr executable
	 */
	private String ogr2ogr = System.getProperty("ogr2ogr_path", "ogr2ogr") ;
	
	/**
	 * ogr2ogr version
	 */
	private String version ;
		
	/**
	 * Default constructor
	 */
	private FileConverter() {
		this.version = retrieveVersion();
		log.info(MARKER, "ogr2ogr version : "+this.version);
		if ( this.version == null ){
			throw new OgrNotFoundException();
		}else if ( this.version.contains("1.11.0") ){
			throw new OgrBadVersionException("ogr2ogr 1.11.0 is not supported (bug in WKT limited to 8000 characters)");
		}
	}

	/**
	 * Get instance
	 * @return
	 */
	public static FileConverter getInstance(){
		return instance;
	}
	
	/**
	 * returns ogr2ogr version
	 * @return null if command `ogr2ogr --version` fails
	 */
	public String getVersion(){
		return this.version ;
	}
	
	/**
	 * Call `ogr2ogr --version` to get GDAL version
	 * @return
	 */
	private String retrieveVersion(){
		log.info(MARKER, "ogr2ogr --version");
		String[] args = new String[]{ogr2ogr,"--version"};
		ProcessBuilder builder = new ProcessBuilder(args);
		try {
			Process process = builder.start();
			
			process.waitFor() ;
			
			InputStream stdout = process.getInputStream() ;
			BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
			
			String version = stdoutReader.readLine() ;
			return version ;
		} catch (IOException e) {
			return null ;
		} catch (InterruptedException e) {
			return null ;
		}
	}

	/**
	 * 
	 * Converts a source file in csv
	 * 
	 * Warning :
	 * As GDAL doesn't convert SHP and TAB the same way,
	 * GDAL is used as if data were encoded in utf-8
	 * so it doesn't convert data encoding
	 * 
	 * @param source
	 * @param destination
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws Exception 
	 */
	public void convertToCSV( File source, File target) throws IOException{
		if ( target.exists() ){
			target.delete() ;
		}

		/*
		 * patch on GML files
		 */
		if ( FilenameUtils.getExtension(source.getName()).toLowerCase().equals("gml") ){
			fixGML(source);
		}
		/*
		 * Removing cpg
		 */
		CompanionFileUtils.removeCompanionFile(source, "cpg"); 
		CompanionFileUtils.removeCompanionFile(source, "CPG");

		String version = getVersion() ;
		log.info(MARKER, "{} => {} (gdal {})", source, target, version );
		
		String[] args = getArguments(source, target, "CSV") ;
		/*
		 * Note : encoding is specified in UTF-8 so that ogr2ogr doesn't convert 
		 */
		runCommand(args,ENCODING_UTF8);
		/*
		 * Controls that output file is created
		 */
		if ( ! target.exists() ) {
			log.error(MARKER, "Impossible de créer le fichier de sortie {}", target.getName());
			createFalseCSV(target) ;
		}
	}
	
	
	/**
	 * Converts a source file in LATIN1 encoded shapefile
	 * 
	 * @param files
	 * @throws IOException 
	 */
	public void convertToShapefile(File source, File target) throws IOException {
		if ( FilenameUtils.getExtension(source.getName()).toLowerCase().equals("gml") ){
			fixGML(source);
		}
		
		String[] args = getArguments(source, target, "ESRI Shapefile") ;
		runCommand(args,ENCODING_LATIN1);
		/*
		 * Controls that output file is created
		 */
		if ( ! target.exists() ) {
			log.error(MARKER, "Impossible de créer le fichier de sortie {}", target.getName());
			createFalseCSV(target) ;
		}
		/*
		 * Generating cgp file
		 */
		File cpgFile = CompanionFileUtils.getCompanionFile(target,"cpg") ;
		FileUtils.writeStringToFile(cpgFile, ENCODING_LATIN1);
	}

	
	/**
	 * 
	 * Any invalid csv file blocks ogr2ogr use
	 * A valid file with header without data is created to avoid this problem
	 * 
	 * @param target
	 * @throws IOException 
	 */
	private void createFalseCSV(File target) throws IOException {
		target.createNewFile() ;
		FileWriter fileWriter = new FileWriter(target) ;
		String header = "header1,header2,header3" ;
		fileWriter.append(header) ;
		fileWriter.flush() ;
		fileWriter.close() ;
	}

	/**
	 * get params
	 * 
	 * @param source
	 * @param target
	 * @param driver
	 * @param encode
	 * @return
	 */
	private String[] getArguments(File source, File target, String driver) {
		List<String> arguments = new ArrayList<String>() ;
		arguments.add(ogr2ogr) ;
	
		// Otherwise, some ogr2ogr versions transforms 01 to 1...
		if ( FilenameUtils.getExtension(source.getName()).toLowerCase().equals("gml") ){
			arguments.add("--config");
			arguments.add("GML_FIELDTYPES");
			arguments.add("ALWAYS_STRING");
		}
		
		arguments.add("-f") ;
		arguments.add(driver) ;
		/*
		 * Getting format-specific parameters 
		 */
		if ( driver.equals("CSV") ) {
			if ( hasSpatialColumn(source) ){
				// unsure conversion to WKT
				arguments.add("-lco") ;
				arguments.add("GEOMETRY=AS_WKT") ;
			}
			
			arguments.add("-lco") ;
			arguments.add("LINEFORMAT=CRLF") ;
		}
		/*
		 * Getting input/output files
		 */
		arguments.add(target.getAbsolutePath()) ;
		arguments.add(source.getAbsolutePath()) ;
		/*
		 * Getting source encoding
		 */
		String []args = new String[arguments.size()];
		arguments.toArray(args);
		return args ;
	}
	
	/**
	 * Indicates if a source file has a geometry column
	 * 
	 *  Note : This is used to avoid the different behaviors of ogr2ogr 
	 *  when treating dbf files
	 * 
	 * @param source
	 * @return
	 */
	private boolean hasSpatialColumn(File source){
		if ( ! FilenameUtils.getExtension(source.getName()).toLowerCase().equals("dbf") ){
			return true ;
		}
		// C'est un .dbf, est-ce qu'il y a un .shp?
		if ( 
			CompanionFileUtils.hasCompanionFile(source, "shp") 
		 || CompanionFileUtils.hasCompanionFile(source, "SHP")
		){
			return true ;
		}else{
			return false ;
		}	
	}
	
	
	/**
	 * Run command line
	 * 
	 * @throws IOException 
	 */
	private void runCommand( String[] args, String shapeEncoding ) throws IOException {
		Process process = null;
		try {
			/*
			 * Creating command
			 */
			String commandLine = commandToString(args) ;
			log.info(MARKER, commandLine);
			
			/*
			 * Executing command
			 */
			ProcessBuilder builder = new ProcessBuilder(args);
			builder.environment().put("SHAPE_ENCODING", shapeEncoding);

			process = builder.start();
			InputStream stderr = process.getErrorStream() ;
			process.waitFor() ;
			
			/*
			 * Reading error
			 */
			BufferedReader errorReader = new BufferedReader (new InputStreamReader(stderr));
			String line = null ;
			while ( ( line = errorReader.readLine() ) != null ){
				log.error( MARKER, line );
			}

			if ( process.exitValue() != 0 ){
				log.error(MARKER, "command fail!");
			}			
		} catch (IOException e1) {
			throw new RuntimeException("Echec dans l'appel de ogr2ogr");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Logs the execution of a command
	 * @param args
	 */
	private String commandToString( String[] args ){
		String message = "" ;
		for ( int i = 0; i < args.length; i++ ){
			if ( i == 0 || args[i].isEmpty() || ( args[i].charAt(0) == '-') || ( args[i].charAt(0) == '"') || ( args[i].charAt(0) == '\'') ){
				message += args[i]+" " ; 
			}else{
				message += "'"+args[i]+"' " ; 
			}
		}
		return message ;
	}
	
	/**
	 *  ogr2ogr ignores self-closing tags.
	 *  They are changed to empty tags
	 *  
	 * @param source
	 * @throws IOException 
	 */
	private void fixGML(File source) throws IOException{
		File backupedFile = new File( source.getPath()+".backup" ) ;
		source.renameTo(backupedFile) ;
		FixGML.replaceAutoclosedByEmpty(backupedFile, source);
	}
	

}
