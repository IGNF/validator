package fr.ign.validator.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import fr.ign.validator.exception.InvalidCharsetException;

/**
 * Lecture des fichiers SHP/TAB 
 * 
 * Note : 
 * <ul>
 * 	<li>S'appuie sur une conversion CSV réalisée à l'aide de ogr2ogr.</li>
 *  <li>Place le fichier CSV à côté de l'original</li>
 * </ul>
 *  
 *  
 * @author MBorne
 */
public class TableReader implements Iterator< String[] >{
	/**
	 * Le lecteur pour le fichier CSV
	 */
	private CSVReader csvReader ;
	/**
	 * L'entête du fichier
	 */
	private String[] header ;
	
	/**
	 * 
	 * Lecture du fichier avec une charset spécifiée. Le système
	 * valide la charset passée en paramètre
	 * 
	 * @param file
	 * @param charset
	 * @throws IOException 
	 */
	private TableReader(File csvFile, Charset charset) throws IOException{
		// ouverture du fichier
		csvReader = new CSVReader(csvFile, charset);
		readHeader();
	}

	/**
	 * Lecture de l'entête
	 * 
	 * Remarque les champs null ou vide sont filtrés en raison de la présence
	 * d'entête posant problème en sortie de ogr2ogr (IDSUP,) sur des fichiers avec
	 * une seule colonne...
	 * 
	 * @throws IOException 
	 */
	private void readHeader() throws IOException{
		if ( ! csvReader.hasNext() ){
			throw new IOException("Impossible de lire l'entête");
		}
		String[] fields = csvReader.next() ;
		List<String> filteredFields = new ArrayList<String>();
		for (String field : fields) {
			if ( field == null || field.isEmpty() ){
				continue ;
			}
			filteredFields.add(field);
		}
		header = filteredFields.toArray(new String[filteredFields.size()]);
	}
	
	
	/**
	 * @return the header
	 */
	public String[] getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(String[] header) {
		this.header = header;
	}
	
	@Override
	public boolean hasNext() {
		return csvReader.hasNext() ;
	}

	@Override
	public String[] next() {
		return csvReader.next() ;
	}

	@Override
	public void remove() {
		csvReader.remove();
	}
	
	/**
	 * Trouve la position d'une colonne dans l'entête
	 * @param string
	 * @return
	 */
	public int findColumn(String name) {
		String regexp = "(?i)"+name ;
		for ( int index = 0; index < header.length; index++) {
			if ( header[index].matches(regexp) ){
				return index ;
			}
		}
		return -1;
	}
	
	/**
	 * Création d'un lecteur à partir d'un fichier et d'une charset
	 * @param file
	 * @param charset
	 * @return
	 * @throws IOException 
	 */
	public static TableReader createTableReader(File file, Charset charset) throws IOException, InvalidCharsetException{
		File csvFile = convertToCSV(file);
		if ( ! CharsetDetector.isValidCharset(csvFile, charset) ) {
			throw new InvalidCharsetException(
				String.format("Le fichier {} n'est pas valide pour la charset {}",
					csvFile.toString(),
					charset.toString()
				)
			);
		}
		return new TableReader(csvFile, charset) ;
	}
	
	
	public static TableReader createTableReaderDetectCharset(File file) throws IOException{
		File csvFile = convertToCSV(file);
		Charset charset = CharsetDetector.detectCharset(csvFile) ;
		return new TableReader(csvFile, charset) ;
	}
	
	
	
	/**
	 * Effectue une conversion en CSV au besoin
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static File convertToCSV(File file) throws IOException{
		if ( FilenameUtils.getExtension( file.getName() ).toLowerCase().equals("csv") ){
			return file ;
		}
		File csvFile = new File(
			file.getParent(),
			FilenameUtils.getBaseName(file.getName())+".csv"
		);
		
		/*
		 * TODO : mettre une option pour éviter 
		 *  plusieurs conversions quand c'est possible
		 */
		if ( csvFile.exists() ){
			csvFile.delete();
		}
		
		FileConverter converter = new FileConverter() ;
		converter.convertToCSV(file, csvFile);
		return csvFile ;
	}
	
}
