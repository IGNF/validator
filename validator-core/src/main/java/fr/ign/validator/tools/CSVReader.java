package fr.ign.validator.tools;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * CSV file reader
 * 
 * @author MBorne
 *
 */
public class CSVReader implements Iterator< String[] >{
	private Iterator<CSVRecord> iterator ;
	
	/**
	 * Reading file with given charset
	 * @param file
	 * @param charset
	 * @throws IOException 
	 */
	public CSVReader( File file, Charset charset ) throws IOException{
		CSVParser parser = CSVParser.parse(file, charset, CSVFormat.RFC4180) ;
		this.iterator = parser.iterator() ;
	}
	
	public CSVReader( Reader reader, Charset charset ) throws IOException {  
		CSVParser parser = new CSVParser(reader, CSVFormat.RFC4180) ;
		this.iterator = parser.iterator() ;
	}

	
	@Override
	public boolean hasNext() {
		return iterator.hasNext() ;
	}

	@Override
	public String[] next() {
		CSVRecord row = iterator.next() ;
		return toArray(row);
	}

	/**
	 * 
	 * @param row
	 * @return
	 */
	private String[] toArray(CSVRecord row) {
		String[] result = new String[row.size()];
		for ( int i = 0; i < row.size(); i++ ){
			result[i] = nullifyEmptyString( trimString( row.get(i) ) ) ;
		}
		return result ;
	}

	/**
	 * Trims a string
	 * @param value
	 * @return
	 */
	private String trimString(String value){
		if ( null == value ){
			return null ;
		}
		return value.trim();
	}
	
	
	/**
	 * Converts to NULL an empty string
	 * @param value
	 */
	private String nullifyEmptyString(String value){
		if ( null == value || value.isEmpty() ){
			return null ;
		}else{
			return value ;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
