package fr.ign.validator.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class FixGML {
	
	
	/**
	 * Remplace les balises autofermante dans un fichier
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
	 * Remplace les balises autofermante
	 * @param input
	 * @return 
	 */
	public static String replaceAutoclosedByEmpty(String input){
		return input.replaceAll("<(.*)/>", "<$1></$1>");
	}
	

}
