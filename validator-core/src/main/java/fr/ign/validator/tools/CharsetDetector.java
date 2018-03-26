package fr.ign.validator.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;


public class CharsetDetector {

	/**
	 * Detects character encoding from data source between UTF-8 and LATIN1.
	 * 
	 * Warning : 
	 * <ul>
	 * 		<li>Returns UTF-8 first if characters are valid by UTF-8</li>
	 * 		<li>Consequently, can return UTF-8 if text is ISO_8859_1 without accent</li>
	 * </ul>
	 * 
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Charset detectCharset(File file) throws IOException {
		if ( isValidUTF8(file) ){
			return StandardCharsets.UTF_8 ;
		}else{
			return StandardCharsets.ISO_8859_1 ;
		}
	}

	/**
	 * Tests if file is encoded in UTF-8
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static boolean isValidUTF8( File file ) {
		return isValidCharset(file, StandardCharsets.UTF_8) ;
	}
	
	/**
	 * Tests if file is encoded in given charset
	 * @param file
	 * @param charset
	 * @return
	 */
	public static boolean isValidCharset(File file, Charset charset){
		try {
			CharsetDecoder cs = charset.newDecoder();
			BufferedReader in = new BufferedReader(
				new InputStreamReader(
					new FileInputStream(file),
					cs
				)
			);
			while ( in.readLine() != null ){
				
			}
			in.close();
		} catch (UnsupportedEncodingException e) {
			return false ;
		} catch (FileNotFoundException e) {
			return false ;
		} catch (IOException e) {
			return false;
		}
		return true; 
	}
	
}

