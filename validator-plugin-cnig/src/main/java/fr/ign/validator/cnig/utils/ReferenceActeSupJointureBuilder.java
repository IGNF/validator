package fr.ign.validator.cnig.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.utils.internal.DatabaseJointureSUP;
import fr.ign.validator.tools.TableReader;

/**
 * Reports files referenced by "actes" on "générateurs" and "assiettes"
 * 
 * TODO :
 * <ul>
 * 	<li>Move to cnig.sup.ComputeColumnFile</li>
 *  <li>Take dataDir as constructor parameter</li>
 *  <li>Improve and add regress tests</li>
 *  <li>Cleanup process</li>
 * </ul>
 *  
 * @author MBorne
 *
 */
public class ReferenceActeSupJointureBuilder {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("ReferenceActeSupJointureBuilder") ;
	
	public static final String COLUMN_FICHIER = "fichier" ;
	
	enum FileType {
		GENERATEUR,
		ASSIETTE
	} ;
	
	
	/**
	 * Validation context
	 */
	private Context context ;
		
	/**
	 * 
	 * @param context
	 */
	public ReferenceActeSupJointureBuilder(Context context){
		this.context = context ;		
	}
	
	public File getDataDirectory(){
		return context.getDataDirectory() ;
	}
	
	/**
	 * Get temp directory 
	 */
	public File getTempDirectory(){
		return new File(getDataDirectory(),"tmp");
	}
	
	
	/**
	 * Recherche d'un fichier par son nom court
	 * @param name
	 * @return
	 */
	public File findFile(String name){
		String[] extensions = { "csv", "CSV" } ;
		@SuppressWarnings("unchecked")
		Collection<File> files = FileUtils.listFiles(getDataDirectory(), extensions, true) ;
		for (File file : files) {
			if ( FilenameUtils.getBaseName( file.getName() ).equals(name) ){
				return file ;
			}
		}
		return null ;
	}

	
	/**
	 * Recherche de fichiers par expression régulière
	 * @param regexp
	 * @return
	 */
	public List<File> findRegexpFiles(String regexp) {
		List<File> results = new ArrayList<File>() ;
		
		String[] extensions = { "csv", "CSV" } ;
		@SuppressWarnings("unchecked")
		Collection<File> files = FileUtils.listFiles(getDataDirectory(), extensions, true) ;

		for (File file : files) {
			if ( ! file.getName().matches(regexp) ) {
				continue ;
			}
			
			results.add(file) ;
		}
		return results ;
	}
	
	
	/**
	 * effectue la jointure
	 * @throws IOException 
	 */
	public void run() throws Exception {
		
		/*
		 * Recherche des fichiers decrivant les actes
		 * ACTE_SUP : idActe, nomFichier
		 * SERVITUDE_ACTE_SUP : idSup, id
		 */
		File actesFile = findFile("ACTE_SUP") ;
		if (actesFile == null) {
			log.error(MARKER, "Pas de fichier de reference sur les actes");
			return ;
		}
		File servitudesFile = findFile( "SERVITUDE_ACTE_SUP") ;
		if (servitudesFile == null) {
			log.error(MARKER, "Pas de fichier de reference sur les servitudes");
			return ;
		}
		
		/*
		 * Recherche des Generateur de servitude a mettre a jour
		 * Cat_Generateur_Sup_s : idGen, idSup
		 * Formation de la jointure
		 * - idGen
		 * - nomFichier
		 */
		List<File> generateursFiles = findRegexpFiles("(?i).*_GENERATEUR_SUP_.*") ;
		if ( generateursFiles.isEmpty() ) {
			log.error(MARKER, "Aucun generateur à mettre a jour");
			return ;
		}
		
		/*
		 * Recherche des Assiette de servitude a mettre a jour
		 * Cat_Assiette_Sup_s : idAss, idGen
		 */
		List<File> assiettesFiles = findRegexpFiles("(?i).*_ASSIETTE_SUP_.*") ;
		if ( assiettesFiles.isEmpty() ) {
			log.error(MARKER, "Aucune assiette à mettre a jour");
			return ;
		}
		
		try {
			performJointure(actesFile,servitudesFile,generateursFiles,assiettesFiles);
		} catch (SQLException e) {
			log.error(MARKER, "Une erreur s'est produite lors de la réalisation de la jointure : "+e.getMessage());
			return ;
		}	
	}

	/**
	 * Réalisation de la jointure
	 * @param actesFile
	 * @param servitudesFile
	 * @param generateursFiles
	 * @param assiettesFiles
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private void performJointure(File actesFile, File servitudesFile,
			List<File> generateursFiles, List<File> assiettesFiles) throws Exception
	{
		/*
		 * Création de la base de données
		 */
		File tempDirectory = getTempDirectory() ;
		log.info(MARKER,"Création du répertoire temporaire {}...",tempDirectory);
		tempDirectory.mkdir();
		
		log.info(MARKER,"Initialisation de la base de données...");
		DatabaseJointureSUP database = new DatabaseJointureSUP(tempDirectory);
		
		log.info(MARKER,"Chargement du fichier {}...",actesFile);
		database.loadFileActe(actesFile);
		log.info(MARKER,"Chargement du fichier {}...",servitudesFile);
		database.loadFileServitude(servitudesFile);
		for (File generateursFile : generateursFiles) {
			log.info(MARKER,"Chargement du fichier {}...",generateursFile);
			database.loadFileGenerateur(generateursFile);
		}
		for (File assiettesFile : assiettesFiles) {
			log.info(MARKER,"Chargement du fichier {}...",assiettesFile);
			database.loadFileAssiette(assiettesFile);
		}
		
		/*
		 * Ajout de la colonne "fichiers" aux générateurs
		 */
		for (File generateursFile : generateursFiles) {
			log.info(MARKER,"Jointure sur le fichier {}...",generateursFile);
			addColumnFichiers(database, FileType.GENERATEUR, generateursFile) ;
		}
		
		/*
		 * Ajout de la colonne "fichiers" aux assiettes
		 */
		for (File assiettesFile : assiettesFiles) {
			log.info(MARKER,"Jointure sur le fichier {}...",assiettesFile);
			addColumnFichiers(database, FileType.ASSIETTE, assiettesFile) ;
		}
	}
	
	/**
	 * Ajout de la colonne "fichiers" au fichier generateursFile
	 * @param database
	 * @param generateursFile
	 * @throws IOException 
	 */
	private void addColumnFichiers(DatabaseJointureSUP database, FileType fileType, File file) throws Exception {
		/*
		 * lecture des métadonnées du fichier en entrée
		 */
		TableReader reader = TableReader.createTableReader(file,StandardCharsets.UTF_8) ;
		String[] inputHeader  = reader.getHeader() ;

		String idColumnName  = getIdColumnName(fileType) ;
		int    idColumnIndex = reader.findColumn(idColumnName);
		if ( idColumnIndex < 0 ){
			log.error(MARKER,"Impossible de trouver la colonne identifiant dans {}...",file);
			return ;
		}
		
		/*
		 * Création du fichier résultant
		 */
		File newFile = new File(getTempDirectory(),file.getName());
		log.debug(MARKER,"Création du fichier {}...",newFile);
		
		String[] outputHeader = createOutputHeader(inputHeader) ;		
		BufferedWriter fileWriter = new BufferedWriter(
			new OutputStreamWriter(new FileOutputStream(newFile), StandardCharsets.UTF_8)
		);
		CSVPrinter printer = new CSVPrinter(fileWriter, CSVFormat.RFC4180) ;
		printer.printRecord(outputHeader);

		while ( reader.hasNext() ){
			String[] inputRow  = reader.next() ;
			
			String id = inputRow[idColumnIndex] ;
			List<String> fichiers = getValueColumnFichiers(database,fileType,id) ;
			String[] outputRow = arrayAppend(inputRow, concat(fichiers)) ;
			printer.printRecord(outputRow);
		}
		
		printer.close(); 
		
		// replace file
		log.debug(MARKER,"Remplacement du fichier {} par {}...",file,newFile);
		file.delete();
		newFile.renameTo(file) ;
	}
	
	/**
	 * Concatène la liste des fichiers avec des pipes "|"
	 * @param fichiers
	 * @return
	 */
	private String concat(List<String> fichiers){
		StringBuilder builder = new StringBuilder();
		for ( int i = 0 ; i < fichiers.size(); i++ ){
			if ( i != 0 ){
				builder.append('|') ;
			}
			builder.append(fichiers.get(i)) ;
		}
		return builder.toString() ;
	}
	
	/**
	 * Renvoie la liste des fichiers en effectuant une jointure
	 * @param database
	 * @param fileType
	 * @param id
	 * @return
	 */
	private List<String> getValueColumnFichiers(DatabaseJointureSUP database, FileType fileType, String id) {
		switch ( fileType ){
		case GENERATEUR:
			return database.findFichiersByGenerateur(id) ;
		case ASSIETTE:
			return database.findFichiersByAssiette(id) ;
		default:
			throw new IllegalArgumentException("Unexpected fileType : "+fileType);		
		}
	}

	/**
	 * Renvoie le nom de la colonne identifiant en fonction du type de fichier
	 * @param fileType
	 * @return
	 */
	private String getIdColumnName(FileType fileType){
		switch ( fileType ){
		case GENERATEUR:
			return "idGen" ;
		case ASSIETTE:
			return "idAss" ;
		default:
			throw new IllegalArgumentException("Unexpected fileType : "+fileType);		
		}
	}
	
	
	
	/**
	 * Création de l'entête de sortie
	 * @param inputHeader
	 * @return
	 */
	private String[] createOutputHeader(String[] inputHeader){
		return arrayAppend(inputHeader,COLUMN_FICHIER);
	}
	
	/**
	 * Ajout d'une valeur au tableau
	 * @param inputArray
	 * @param value
	 * @return
	 */
	private String[] arrayAppend(String[] inputArray,String value){
		String[] outputArray = new String[inputArray.length+1];
		for ( int i = 0; i < inputArray.length; i++){
			outputArray[i] = inputArray[i];
		}
		outputArray[outputArray.length-1] = value ;
		return outputArray ;
	}
	

}
