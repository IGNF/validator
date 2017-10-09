package fr.ign.validator.tools;

/**
 * 
 * Utility class validating & escaping characters
 * 
 * @author MBorne
 *
 */
public class Characters {

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
	public static String escapeControl(int codePoint){
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
	 * Convert character to URI displaying details
	 * @param codePoint
	 * @return
	 */
	public static String toURI(int codePoint){
		String id = String.format("%04x",codePoint) ;
		return "http://www.fileformat.info/info/unicode/char/"+id+"/index.htm";
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
