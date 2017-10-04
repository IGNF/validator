package fr.ign.validator.tools.internal;

import java.awt.Point;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Correction des chaînes de caractères doublement encodées en UTF-8. Procède par recherche
 * des séquences mal-encodées (non efficace si on est certain que la chaîne de caractères est mal encodée).
 * 
 * @author MBorne
 *
 */
public class DoubleUtf8Fixer {
	/**
	 * codePoint unicode maximum testé
	 */
	public static final int MAX_CODE_POINT = 10000;
	
	private Map<Integer, String> codePointToBadEncoding = new HashMap<>();

	/**
	 * Les premiers caractères apparaissant en cas de double encodage associé à minCodePoint et maxCodePoint (optimisation
	 *  pour éviter de rechercher sans raison des remplacements)
	 */
	private Map<Character, Point> charsOfInterest = new HashMap<>();
	
	public DoubleUtf8Fixer(){
		for ( int codePoint = MAX_CODE_POINT; codePoint >= 128; codePoint-- ){
			String s = new String(Character.toChars(codePoint));
			String dirty = DoubleUtf8Fixer.utf8DeclaredAsLatin1(s);
			codePointToBadEncoding.put(codePoint, dirty);

			Character firstChar = dirty.charAt(0);
			if ( ! charsOfInterest.containsKey(firstChar) ){
				charsOfInterest.put(firstChar, new Point(codePoint, codePoint));
			}else{
				Point minMaxCodePoint = charsOfInterest.get(firstChar);
				minMaxCodePoint.x = Math.min(minMaxCodePoint.x, codePoint);
				minMaxCodePoint.y = Math.max(minMaxCodePoint.y, codePoint);				
			}
		}
	}
	
		
	/**
	 * "è" => "Ã¨"
	 * @param s
	 * @return
	 */
	public static String utf8DeclaredAsLatin1(String s){
		try {
			return new String(s.getBytes("utf-8"), "latin1");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Try to revert double encoding in UTF-8 string
	 * @param value
	 * @return
	 */
	public String fixUtf8DeclaredAsLatin1(String value){
		String result = value;

		for (Character charOfInterest : charsOfInterest.keySet()) {
			if ( result.indexOf(charOfInterest) >= 0 ){
				Point minMaxCodePoint = charsOfInterest.get(charOfInterest);
				for ( int codePoint = minMaxCodePoint.y; codePoint >= minMaxCodePoint.x; codePoint-- ){
					String dirty = codePointToBadEncoding.get(codePoint);
					String s = new String(Character.toChars(codePoint));
					result = result.replaceAll(dirty, s);
				}
			}
		} 
		return result;
	}

	
}
