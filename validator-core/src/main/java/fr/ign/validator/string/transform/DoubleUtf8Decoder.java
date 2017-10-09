package fr.ign.validator.string.transform;

import java.awt.Point;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import fr.ign.validator.string.StringTransform;

/**
 * 
 * Correction des chaînes de caractères doublement encodées en UTF-8. Procède par recherche
 * des séquences mal-encodées (non efficace si on est certain que la chaîne de caractères est mal encodée).
 * 
 * @author MBorne
 *
 */
public class DoubleUtf8Decoder implements StringTransform {
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
	
	public DoubleUtf8Decoder(){
		for ( int codePoint = MAX_CODE_POINT; codePoint >= 128; codePoint-- ){
			String s = new String(Character.toChars(codePoint));
			String dirty = DoubleUtf8Decoder.utf8DeclaredAsLatin1(s);
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

	@Override
	public String transform(String value){
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

	
	/**
	 * "è" => "Ã¨"
	 * @param s
	 * @return
	 */
	public static String utf8DeclaredAsLatin1(String s){
		return new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
	}
}
