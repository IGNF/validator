package fr.ign.validator.string;

/**
 * Transform a string to an other
 * @author MBorne
 *
 */
public interface StringTransform {
	
	/**
	 * Transform string to output value
	 * @param value
	 * @return
	 */
	public String transform(String value);
	
}
