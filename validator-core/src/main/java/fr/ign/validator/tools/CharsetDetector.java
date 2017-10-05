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
	 * Détecte l'encodage des caractères d'une source de données parmi UTF-8 et LATIN1.
	 * 
	 * Attention :
	 * <ul>
	 *   <li>Renvoie prioritairement UTF-8 si les caractères sont valides au sens d'UTF-8</li>
     *   <li>Peut donc renvoyer UTF_8 pour de l'ISO_8859_1 en l'absence d'accent</li> 
	 * </ul>
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
	 * Test si le fichier est encodé en UTF-8
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static boolean isValidUTF8( File file ) {
		return isValidCharset(file, StandardCharsets.UTF_8) ;
	}
	
	/**
	 * Test si le fichier est encodé dans la charset en paramètre
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

