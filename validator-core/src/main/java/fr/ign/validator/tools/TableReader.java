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
 * SHP/TAB file reader 
 * 
 * Note : 
 * <ul>
 * 		<li>Based on a csv conversion made by ogr2ogr</li>
 * 		<li>Puts csv file next to the original file</li>
 * </ul>
 *  
 * @author MBorne
 */
public class TableReader implements Iterator< String[] >{
	/**
	 * CSV file reader
	 */
	private CSVReader csvReader ;
	/**
	 * File header
	 */
	private String[] header ;
	
	/**
	 * 
	 * Reading file with given charset (validated by system).
	 * 
	 * 
	 * @param file
	 * @param charset
	 * @throws IOException 
	 */
	private TableReader(File csvFile, Charset charset) throws IOException{
		/*
		 * opening file
		 */
		csvReader = new CSVReader(csvFile, charset);
		readHeader();
	}

	/**
	 * Header reading
	 * 
	 * 
	 * Note :
	 * NULL or empty fields are filtered to avoid problems with files with only one column
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
	 * Finds the position of a column by its name in header
	 * 
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
	 * Creates a reader from a file and a charset
	 * 
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
	 * Converts to csv if needed
	 * 
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

		if ( csvFile.exists() ){
			csvFile.delete();
		}
		
		FileConverter converter = FileConverter.getInstance();
		converter.convertToCSV(file, csvFile);
		return csvFile ;
	}
	
}
