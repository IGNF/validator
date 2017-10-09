package fr.ign.validator.string;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.string.transform.DoubleUtf8Decoder;
import fr.ign.validator.string.transform.EscapeForCharset;
import fr.ign.validator.string.transform.IsoControlEscaper;
import fr.ign.validator.string.transform.StringSimplifier;

/**
 * 
 * Compose transforms to fix a input string
 * 
 * @author MBorne
 */
public class StringFixer implements StringTransform {
	/**
	 * transforms list
	 */
	private List<StringTransform> transforms = new ArrayList<>();

	/**
	 * Add a transform
	 * @param transform
	 */
	public void addTransform(StringTransform transform){
		this.transforms.add(transform);
	}

	@Override
	public String transform(String value){
		String result = value ;
		if ( result == null || result.isEmpty() ){
			return result;
		}
		for (StringTransform transform : transforms) {
			result = transform.transform(result);
		}
		return result;
	}
	
	/**
	 * Create a full string fixer with an optional charset
	 * @param charset
	 * @return
	 */
	public static StringFixer createFullStringFixer(Charset charset){
		StringFixer stringFixer = new StringFixer();
	
		stringFixer.addTransform(new DoubleUtf8Decoder());

		StringSimplifier simplifier = new StringSimplifier();
		simplifier.loadCommon();
		if ( charset != null ){
			simplifier.loadCharset(charset);
		}
		stringFixer.addTransform(simplifier);

		stringFixer.addTransform(new IsoControlEscaper(true));
		if ( charset != null ){
			stringFixer.addTransform(new EscapeForCharset(charset));
		}

		return stringFixer;
	}
	

}
