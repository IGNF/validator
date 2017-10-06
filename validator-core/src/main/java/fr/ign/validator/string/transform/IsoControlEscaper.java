package fr.ign.validator.string.transform;

import fr.ign.validator.string.StringTransform;
import fr.ign.validator.tools.Characters;

/**
 * 
 * Escape characters marked as ISO controls
 * 
 * @author MBorne
 *
 */
public class IsoControlEscaper implements StringTransform {

	/**
	 * Ignores standard controls?
	 * @see standard controls definition {@link Characters#isStandardControl}
	 */
	private boolean standardControlsAllowed ;
	
	/**
	 * @param standardControlsAllowed
	 */
	public IsoControlEscaper(boolean standardControlsAllowed){
		this.standardControlsAllowed = standardControlsAllowed;
	}
	
	@Override
	public String transform(String s) {
		StringBuffer result = new StringBuffer();
		
		final int length = s.length();
		for (int offset = 0; offset < length; ) {
		   final int codePoint = s.codePointAt(offset);
		   /* test for control characters */
		   if ( Character.isISOControl(codePoint) ){
			   /* exception for standard controls */
			  if ( standardControlsAllowed && Characters.isStandardControl(codePoint) ){
				  result.append(new String(Character.toChars(codePoint)));
			  }else{
				  result.append( Characters.escapeControl(codePoint) );
			  }
		   }else{
			   result.append(new String(Character.toChars(codePoint)));
		   }
		   offset += Character.charCount(codePoint);
		}
		
		return result.toString();
	}

	
	
}
