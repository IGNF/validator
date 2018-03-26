package fr.ign.validator.string.transform;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import fr.ign.validator.string.StringTransform;
import fr.ign.validator.tools.Characters;

/**
 * 
 * Escape characters not supported by a given Charset
 * 
 * @author MBorne
 *
 */
public class EscapeForCharset implements StringTransform {

	private CharsetEncoder encoder ;
	
	public EscapeForCharset(Charset charset){
		this.encoder = charset.newEncoder();
	}

	/**
	 * Tests if a character is convertible to the given charset
	 * @param codePoint
	 * @return
	 */
	public boolean isConvertibleToCharset(int codePoint){
		String s = new String(Character.toChars(codePoint));
		return encoder.canEncode(s);
	}

	
	@Override
	public String transform(String value) {
		StringBuffer result = new StringBuffer();
		
		final int length = value.length();
		for (int offset = 0; offset < length; ) {
		   final int codePoint = value.codePointAt(offset);
		   /* test for control characters */
		   if( ! isConvertibleToCharset(codePoint) ){
			   result.append( Characters.toHexa(codePoint) );
		   }else{
			   result.append(new String(Character.toChars(codePoint)));
		   }
		   offset += Character.charCount(codePoint);
		}
		
		return result.toString();
	}

	
	
}


