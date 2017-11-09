package fr.ign.validator.string;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import fr.ign.validator.string.transform.DoubleUtf8Decoder;
import fr.ign.validator.string.transform.EscapeForCharset;
import fr.ign.validator.string.transform.IsoControlEscaper;
import fr.ign.validator.string.transform.StringSimplifier;

/**
 * 
 * Compose transforms to fix a input string applying operation in the given order :
 * 
 * <ul>
 * 	<li>Detect and fix double UTF-8 encoding relative to LATIN1</li>
 *  <li>Simplify characters for a better support by fonts (common) or by a given charset (ex : LATIN1)</li>
 *  <li>Escape controls characters and characters not supported by a given charset</li>
 * </ul>
 * 
 * @author MBorne
 */
public class StringFixer implements StringTransform {
	
	/**
	 * Try to find and revert double utf8 encoding for UTF-8 declared as LATIN1
	 */
	private boolean utf8Fixed = false;

	/**
	 * Enable common character simplification are applied
	 * @see /validator-core/src/main/resources/simplify/common.csv
	 */
	private boolean commonSimplified = false ;

	/**
	 * Enable character simplification relative to this charset (null if disabled)
	 * @see /validator-core/src/main/resources/simplify/[CHARSET].csv
	 */
	private Charset charsetSimplified = null ;

	/**
	 * Characters not supported by this charset are escaped
	 */
	private Charset charsetEscaped = null;

	/**
	 * ISO controls that are not standard controls are escaped
	 */
	private boolean controlEscaped = false ;
	
	/**
	 * Transforms (cached, null if not yet generated)
	 * @param options
	 */
	private List<StringTransform> transforms ;

	/**
	 * Create a full string fixer for a given charset (simplify & escape)
	 * @param charset
	 * @return
	 */
	public static StringFixer createFullStringFixer(Charset charset){
		StringFixer stringFixer = new StringFixer();
		
		stringFixer.setUtf8Fixed(true);
		
		stringFixer.setCommonSimplified(true);
		stringFixer.setCharsetSimplified(charset);

		stringFixer.setCharsedEscaped(charset);
		stringFixer.setControlEscaped(true);
		
		return stringFixer;
	}

	public boolean isUtf8Fixed() {
		return utf8Fixed;
	}
	public void setUtf8Fixed(boolean utf8Fixed) {
		this.utf8Fixed = utf8Fixed;
		transforms = null;
	}

	
	public boolean isControlEscaped() {
		return controlEscaped;
	}
	public void setControlEscaped(boolean controlEscaped) {
		this.controlEscaped = controlEscaped;
		transforms = null;
	}
	
	
	public Charset getCharsetEscaped() {
		return charsetEscaped;
	}
	public void setCharsedEscaped(Charset charsetEscaped) {
		this.charsetEscaped = charsetEscaped;
		transforms = null;
	}
	
	
	public boolean isCommonSimplified() {
		return commonSimplified;
	}
	public void setCommonSimplified(boolean commonSimplified) {
		this.commonSimplified = commonSimplified;
		transforms = null;
	}

	public Charset getCharsetSimplified() {
		return charsetSimplified;
	}
	public void setCharsetSimplified(Charset charsetSimplified) {
		this.charsetSimplified = charsetSimplified;
		transforms = null;
	}


	@Override
	public String transform(String value){
		String result = value ;
		if ( StringUtils.isEmpty(result) ){
			return result;
		}
		for (StringTransform transform : getTransforms()) {
			result = transform.transform(result);
		}
		return result;
	}

	
	/**
	 * Get transforms
	 * @return
	 */
	private List<StringTransform> getTransforms(){
		buildTransformsIfRequired();
		return transforms;
	}
	
	/**
	 * Build transforms according to options in a given order
	 * 
	 * <ul>
	 * 	<li>double utf-8 fixing</li>
	 *  <li>simplification</li>
	 *  <li>escape controls</li>
	 *  <li>escape charset specific</li>
	 * </ul>
	 * 
	 */
	private void buildTransformsIfRequired(){
		if ( transforms != null ){
			return ;
		}
		transforms = new ArrayList<>();
		
		if ( utf8Fixed ){
			addTransform(new DoubleUtf8Decoder());
		}

		StringSimplifier simplifier = new StringSimplifier();
		if ( commonSimplified ){
			simplifier.loadCommon();
		}
		if ( charsetSimplified != null ){
			simplifier.loadCharset(charsetSimplified);
		}
		addTransform(simplifier);

		if ( controlEscaped ){
			addTransform(new IsoControlEscaper(true));
		}
		
		if ( charsetEscaped != null ){
			addTransform(new EscapeForCharset(charsetEscaped));
		}
	}
	
	
	/**
	 * Add a transform
	 * @param transform
	 */
	private void addTransform(StringTransform transform){
		this.transforms.add(transform);
	}



}
