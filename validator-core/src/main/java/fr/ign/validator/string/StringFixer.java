package fr.ign.validator.string;

import java.util.ArrayList;
import java.util.List;

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

}
