package fr.ign.validator.tools;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * 
 * Utility class validating & escaping characters
 * 
 * @author MBorne
 *
 */
public class Characters {

	private static CharsetEncoder latin1Encoder = Charset.forName("ISO-8859-1").newEncoder();

	/**
	 * Escape controls and non latin1 characters
	 * @param s
	 * @param options
	 * @return
	 */
	public static String escape(String s, CharacterValidationOptions options){
		String escaped = Characters.escapeControls(s.toString(),options.standardControlsAllowed);
		if ( options.ensureLatin1Compatibility ){
			escaped = Characters.escapeNonLatin1(escaped.toString());
		}
		return escaped;
	}
	
	/**
	 * Escape controls and non latin1 characters
	 * @param s
	 * @param options
	 * @return
	 */
	public static String normalize(String s, CharacterValidationOptions options){
		if ( s == null ){
			return null;
		}
		String normalized = replaceIsoControlsByEquivalents(s);
		normalized = escape(s, options);
		return normalized;
	}
	
	/**
	 * 
	 * Replace common misused isISOControl characters by LATIN1 supported equivalents
	 * 
	 * @param s
	 * @param standardControlsAllowed
	 * @return
	 */
	public static String replaceIsoControlsByEquivalents(String s){
		StringBuffer result = new StringBuffer();
		
		final int length = s.length();
		for (int offset = 0; offset < length; ) {
		   final int codePoint = s.codePointAt(offset);
		   //TODO replace some codePoints
		   result.append(new String(Character.toChars(codePoint)));
		   offset += Character.charCount(codePoint);
		}
		
		return result.toString();
	}
	
	
	
	/**
	 * 
	 * Escape control characters
	 * 
	 * @param s
	 * @param standardControlsAllowed
	 * @return
	 */
	public static String escapeControls(String s, boolean standardControlsAllowed){
		StringBuffer result = new StringBuffer();
		
		final int length = s.length();
		for (int offset = 0; offset < length; ) {
		   final int codePoint = s.codePointAt(offset);
		   /* test for control characters */
		   if ( Character.isISOControl(codePoint) ){
			   /* exception for standard controls */
			  if ( standardControlsAllowed && isStandardControl(codePoint) ){
				  result.append(new String(Character.toChars(codePoint)));
			  }else{
				  result.append( escapeControl(codePoint) );
			  }
		   }else{
			   result.append(new String(Character.toChars(codePoint)));
		   }
		   offset += Character.charCount(codePoint);
		}
		
		return result.toString();
	}
	

	
	/**
	 * Test if a character is a standard control :
	 * 
	 * <ul>
	 * 	<li>Backspace (\b)</li>
	 *  <li>Form feed (\f)</li>
	 *  <li>Newline (\n)</li>
	 *  <li>Carriage return (\r)</li>
	 *  <li>Tab (\t)</li>
	 * </ul>
	 * 
	 * @param codePoint
	 */
	public static boolean isStandardControl(int codePoint){
		switch (codePoint){
		case '\b':
		case '\f':
		case '\n':
		case '\r':
		case '\t':			
			return true;
		default:
			return false;
		}
	}


	/**
	 * 
	 * @param codePoint
	 * @return
	 */
	private static String escapeControl(int codePoint){
		switch (codePoint){
		case '\b':
			return "\\b";			
		case '\f':
			return "\\f";			
		case '\n':
			return "\\n";
		case '\r':
			return "\\r";
		case '\t':
			return "\\t";
		default:
			return toHexa(codePoint);
		}
	}
	
	

	/**
	 * Converts java string to a latin1 printable string
	 * @param s
	 * @return
	 */
	public static String escapeNonLatin1(String s){
		StringBuffer result = new StringBuffer();
		
		final int length = s.length();
		for (int offset = 0; offset < length; ) {
		   final int codePoint = s.codePointAt(offset);
		   /* test for control characters */
		   if( ! isConvertibleToLatin1(codePoint) ){
			   result.append( toHexa(codePoint) );
		   }else{
			   result.append(new String(Character.toChars(codePoint)));
		   }
		   offset += Character.charCount(codePoint);
		}
		
		return result.toString();
	}
	

	/**
	 * Test if a characters is convertible to latin1
	 * @param codePoint
	 * @return
	 */
	public static boolean isConvertibleToLatin1(int codePoint){
		String s = new String(Character.toChars(codePoint));
		return latin1Encoder.canEncode(s);
	}

	/**
	 * Converts character to hexa representation
	 * @param codePoint
	 * @return
	 */
	public static String toHexa(int codePoint) {
		return String.format("\\u%04x",codePoint);
	}

	
}
