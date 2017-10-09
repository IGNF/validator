package fr.ign.validator.string.transform;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringEscapeUtils;

import fr.ign.validator.error.ErrorFactory;
import fr.ign.validator.string.StringTransform;

/**
 * Replace codePoint a string
 * @author MBorne
 *
 */
public class StringSimplifier implements StringTransform {

	/**
	 * source char associated to replacement (LinkedHashMap keeps insertion order)
	 */
	private Map<String, String> replacements = new LinkedHashMap<>();
	
	
	/**
	 * Add replacement
	 * @param before
	 * @param after
	 */
	public void addReplacement(String before, String after){
		this.replacements.put(before, after);
	}
	
	/**
	 * Get replacement map
	 * @return
	 */
	public Map<String, String> getReplacements(){
		return this.replacements;
	}
	
	@Override
	public String transform(String value){
		if ( value == null || value.isEmpty() ){
			return value;
		}
		String result = value;
		for (String before : replacements.keySet()) {
			result = result.replaceAll(before, replacements.get(before));
		}
		return result;
	}
	
	/**
	 * Load common replacements stored in /simplify/common.csv
	 * @throws IOException
	 */
	public void loadCommon() {
		Reader reader = new InputStreamReader(
			ErrorFactory.class.getResourceAsStream("/simplify/common.csv"),
			StandardCharsets.UTF_8
		) ;
		try {
			loadCSV(reader);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Load charset specific replacements stored in /simplify/[CHARSET-NAME].csv
	 * @param charset
	 * @throws IOException
	 */
	public void loadCharset(Charset charset) {
		Reader reader = new InputStreamReader(
			ErrorFactory.class.getResourceAsStream("/simplify/"+charset.name()+".csv"),
			StandardCharsets.UTF_8
		) ;
		try {
			loadCSV(reader);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	
	/**
	 * Load mapping from CSV file (before,after,comment)
	 * @param file
	 * @throws IOException
	 */
	public void loadCSV(File file) throws IOException {
		CSVParser parser = CSVParser.parse(file, StandardCharsets.UTF_8, CSVFormat.RFC4180) ;
		loadCSV(parser);
		parser.close();
	}
	
	/**
	 * Load mapping from CSV file (before,after,comment)
	 * @param file
	 * @throws IOException
	 */
	public void loadCSV(Reader reader) throws IOException {
		CSVParser parser = new CSVParser(reader, CSVFormat.RFC4180) ;
		loadCSV(parser);
		parser.close();
	}
	
	/**
	 * Parser CSV
	 * @param parser
	 */
	private void loadCSV(CSVParser parser){
		Iterator<CSVRecord> iterator = parser.iterator();
		CSVRecord header = null;
		while ( iterator.hasNext() ){
			CSVRecord record = iterator.next();
			if ( header == null ){
				header = record;
				continue;
			}
			
			String before = StringEscapeUtils.unescapeJava(record.get(0));
			String after  = StringEscapeUtils.unescapeJava(record.get(1));
			addReplacement(before, after);
		}
	}
	
}
