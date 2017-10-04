package fr.ign.validator.tools.internal;

public class MisusedCharacterFixer {

	/**
	 * \u0092 => '
	 * \u0096 => -
	 * 
	 * \u009c => "œ" ou "oe" suivant option latin1
	 * 
	 * 
	 * @param value
	 * @return
	 */
	public static String fixMisused(String value, boolean ensureLatin1Compatibility ){
		// http://www.fileformat.info/info/unicode/char/0092/index.htm
		value = value.replaceAll("", "'");
		// http://www.fileformat.info/info/unicode/char/0096/index.htm
		value = value.replaceAll("", "-");
		// http://www.fileformat.info/info/unicode/char/0085/index.htm
		value = value.replaceAll("\u0085", "...");
		
		// LEFT SINGLE QUOTATION MARK - http://www.fileformat.info/info/unicode/char/2018/index.htm
		value = value.replaceAll("\u2018", "'");
		// RIGHT SINGLE QUOTATION MARK - http://www.fileformat.info/info/unicode/char/2018/index.htm
		value = value.replaceAll("\u2019", "'");

		// EN DASH http://www.fileformat.info/info/unicode/char/2013/index.htm
		value = value.replaceAll("\u2013", "'-");

		if ( ensureLatin1Compatibility ){
			value = value.replaceAll("", "oe");
			value = value.replaceAll("\u0152", "OE");
		}else{
			value = value.replaceAll("", "œ");
			value = value.replaceAll("\u0152", "Œ");
		}

		return value;
	}
	
}
