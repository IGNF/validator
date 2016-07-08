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



/**
 * 
 * Outils de conversion de format reposant sur GDAL - ogr2ogr.
 * 
 * TODO contrôler la version de ogr2ogr et banir 1.11.0 (bug WKT 8000 caractères)
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
	
	
	/**
	 * @brief path to ogr2ogr executable
	 */
	private String ogr2ogr = System.getProperty("ogr2ogr_path", "ogr2ogr") ;
		
	/**
	 * Default constructor
	 */
	public FileConverter() {
		
	}
	
	/**
	 * Renvoie la version de GDAL
	 * @return null si la commande ogr2ogr --version échoue
	 */
	public String getVersion(){
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
	 * Conversion d'un fichier source en CSV.
	 * 
	 * Attention : Dans la mesure où GDAL ne fait pas les conversions 
	 *  de manière uniforme entre SHP et TAB, on veille ici à ce que GDAL ne tente pas 
	 *  tenter de convertir l'encodage des données (on laisse GDAL croire que les données sont en UTF-8)
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
		// patch sur les fichiers GML
		if ( FilenameUtils.getExtension(source.getName()).toLowerCase().equals("gml") ){
			fixGML(source);
		}
		// suppression des cpg
		CompanionFileUtils.removeCompanionFile(source, "cpg"); 
		CompanionFileUtils.removeCompanionFile(source, "CPG");

		String version = getVersion() ;
		log.info(MARKER, "{} => {} (gdal {})", source, target, version );
		
		String[] args = getArguments(source, target, "CSV") ;
		runCommand(args,ENCODING_UTF8);
		/*
		 * Controle que le fichier de sortie est bien crée
		 */
		if ( ! target.exists() ) {
			log.error(MARKER, "Impossible de créer le fichier de sortie {}", target.getName());
			createFalseCSV(target) ;
		}
	}
	
	
	/**
	 * Conversion d'un fichier source en shapefile avec pour encodage LATIN1
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
		 * Controle que le fichier de sortie est bien crée
		 */
		if ( ! target.exists() ) {
			log.error(MARKER, "Impossible de créer le fichier de sortie {}", target.getName());
			createFalseCSV(target) ;
		}
		/*
		 * Génération du fichier .cpg
		 */
		File cpgFile = CompanionFileUtils.getCompanionFile(target,"cpg") ;
		FileUtils.writeStringToFile(cpgFile, ENCODING_LATIN1);
	}

	
	/**
	 * Tout fichier csv invalide bloque l'usage d'ogr2ogr
	 * On construit un fichier valide, avec entete, sans enregistrement pour eviter ce souci
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
	 * @param source
	 * @param target
	 * @param driver
	 * @param encode
	 * @return
	 */
	private String[] getArguments(File source, File target, String driver) {
		/*
		 * Initialisation de la commande
		 */
		List<String> arguments = new ArrayList<String>() ;
		arguments.add(ogr2ogr) ;
		arguments.add("-f") ;
		arguments.add(driver) ;
		/*
		 * Recuperation des parametres specifique au format
		 */
		if ( driver.equals("CSV") ) {
			if ( hasSpatialColumn(source) ){
				arguments.add("-lco") ;
				arguments.add("GEOMETRY=AS_WKT") ;
			}
			
			arguments.add("-lco") ;
			arguments.add("LINEFORMAT=CRLF") ;
		}
		/*
		 * Recuperation des fichiers d'entree/sortie
		 */
		arguments.add(target.getAbsolutePath()) ;
		arguments.add(source.getAbsolutePath()) ;
		/*
		 * Recuperation de l'encodage source
		 */
		String []args = new String[arguments.size()];
		arguments.toArray(args);
		return args ;
	}
	
	/**
	 * Indique si le fichier source à une colonne géométrique
	 * 
	 * Remarque : Utilisé pour éviter des variations de comportement
	 *  sur ogr2ogr dans le cas des DBF.
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
			 * Creation de la commande
			 */
			String commandLine = commandToString(args) ;
			log.info(MARKER, commandLine);
			
			/*
			 * Execution de la commande
			 */
			ProcessBuilder builder = new ProcessBuilder(args);
			builder.environment().put("SHAPE_ENCODING", shapeEncoding);
			process = builder.start();
			InputStream stderr = process.getErrorStream() ;
			process.waitFor() ;
			
			/*
			 * Lecture de l'erreur
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
	 * Log l'exécution d'une commande
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
	 * OGR2OGR ignore les balises autofermante. On transforme ici
	 *  les balises autofermantes en balise vide.
	 * @param source
	 * @throws IOException 
	 */
	private void fixGML(File source) throws IOException{
		File backupedFile = new File( source.getPath()+".backup" ) ;
		source.renameTo(backupedFile) ;
		FixGML.replaceAutoclosedByEmpty(backupedFile, source);
	}
	

}
