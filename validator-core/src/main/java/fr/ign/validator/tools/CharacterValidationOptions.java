package fr.ign.validator.tools;

/**
 * 
 * Character validation options
 * 
 * @author MBorne
 *
 */
public class CharacterValidationOptions {
	/**
	 * Allow standard controls (\t, \r, \n, etc.)
	 */
	public boolean standardControlsAllowed ;
	/**
	 * Ensure that unicode codePoint is allowed in LATIN1
	 */
	public boolean ensureLatin1Compatibility ;

	public CharacterValidationOptions(){
		this.standardControlsAllowed     = true;
		this.ensureLatin1Compatibility = true;
	}

	
}
