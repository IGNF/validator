package fr.ign.validator.tools;

import fr.ign.validator.tools.internal.DoubleUtf8Fixer;
import fr.ign.validator.tools.internal.MisusedCharacterFixer;

/**
 * Utilitaire de correction des chaînes de caractères permettant :
 * 
 * <ul>
 * 	<li>De corriger les doubles encodages UTF-8</li>
 *  <li>De remplacer les caractères unicode rarement supporté par des équivalents</li>
 *  <li>De remplacer les caractères de contrôle ("non affichable") par de l'hexadecimal</li>
 *  <li>D'assurer la compatibilité avec LATIN1</li>
 * </ul>
 * 
 * @author MBorne
 */
public class StringFixer {

	/**
	 * Indique s'il faut essayer de corriger les caractères doublement encodés en UTF-8 suite à des 
	 * erreurs de déclaration
	 */
	private boolean fixDirtyUtf8 = false;
	
	/**
	 * Remplace les caractères usuellement utilisé à tord :
	 * 
	 * \u0092 => '
	 * 
	 */
	private boolean fixMisused = false;
	
	/**
	 * Indique s'il faut transformer les caractères de contrôles (à l'exception des caractères de contrôle standard)
	 */
	private boolean fixControls  = false;
	/**
	 * Indique s'il faut transformer les caractères non compatible avec LATIN1
	 */
	private boolean fixNonLatin1 = false;

	
	private DoubleUtf8Fixer doubleUtf8Fixer = new DoubleUtf8Fixer();

	/**
	 * Applique les corrections sur la chaîne de caractères
	 * @param value
	 * @return
	 */
	public String fixString(String value){
		String result = value ;
		if ( result == null || result.isEmpty() ){
			return result;
		}
		if ( fixDirtyUtf8 ){
			result = doubleUtf8Fixer.fixUtf8DeclaredAsLatin1(result);
		}
		if ( fixMisused ){
			result = MisusedCharacterFixer.fixMisused( result, fixNonLatin1 );
		}

		if ( fixControls ){
			result = Characters.escapeControls(result, true);
		}
		if ( fixNonLatin1 ){
			result = Characters.escapeNonLatin1(result);
		}
		return result;
	}


	public void setFixDirtyUtf8(boolean fixDirtyUtf8) {
		this.fixDirtyUtf8 = fixDirtyUtf8;
	}


	public void setFixMisused(boolean fixMisused) {
		this.fixMisused = fixMisused;
	}

	public void setFixControls(boolean fixControls) {
		this.fixControls = fixControls;
	}

	public void setFixNonLatin1(boolean fixNonLatin1) {
		this.fixNonLatin1 = fixNonLatin1;
	}

	
}
