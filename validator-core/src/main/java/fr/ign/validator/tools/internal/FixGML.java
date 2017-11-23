package fr.ign.validator.tools.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * 
 * Replace autoclosed XML elements by empty elements (otherway, ogr2ogr ignores fields so validator reports missing fields)
 *  
 * @author MBorne
 *
 */
public class FixGML {
	
	/**
	 * Performs replacement in a file
	 * @param input
	 * @throws IOException 
	 */
	public static void replaceAutoclosedByEmpty(File input,File output) throws IOException{
		BufferedReader br = new BufferedReader(
			new InputStreamReader(
				new FileInputStream(input),
				StandardCharsets.UTF_8
			)
		);
		
		BufferedWriter writer = new BufferedWriter(
			new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8)
		);
	
		String line = br.readLine() ;
		while ( line != null ){
			String newLine = replaceAutoclosedByEmpty(line) ;
			writer.write(newLine+"\r\n");
			line = br.readLine() ;
		}
		
		br.close();
		writer.flush();
		writer.close();
	}
	
	
	/**
	 * Performs replacement in a string
	 * @param input
	 * @return 
	 */
	public static String replaceAutoclosedByEmpty(String input){
		return input.replaceAll("<(.*)/>", "<$1></$1>");
	}
	

}
