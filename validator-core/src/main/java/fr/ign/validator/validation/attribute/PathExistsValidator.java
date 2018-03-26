package fr.ign.validator.validation.attribute;

import java.io.File;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validates that the path exists for PathAttribute type attributes
 * 
 * @author MBorne
 *
 */
public class PathExistsValidator implements Validator<Attribute<File>> {

	@Override
	public void validate(Context context, Attribute<File> attribute) {
		File path = attribute.getBindedValue() ;
		
		if ( null == path ){
			return ;
		}
		
		File absolutePath = new File(
			context.getCurrentDirectory(), 
			filterFragment( path.toString() )
		) ;

		if ( ! absolutePath.exists() ){
			context.report(
				CoreErrorCodes.ATTRIBUTE_PATH_NOT_FOUND, 
				path.toString()
			);
		}
	}
	
	/**
	 * Remove fragment from URI (#page=125)
	 * @param path
	 * @return
	 */
	private String filterFragment(String path){
		int position = path.lastIndexOf('#') ;
		if ( position >= 0 ){
			return path.substring(0, position);
		}else{
			return path ;
		}
	}


}
