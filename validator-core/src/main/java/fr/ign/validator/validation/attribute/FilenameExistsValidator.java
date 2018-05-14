package fr.ign.validator.validation.attribute;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

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
public class FilenameExistsValidator implements Validator<Attribute<File>> {

	@Override
	public void validate(Context context, Attribute<File> attribute) {
		File path = attribute.getBindedValue() ;
		
		if ( null == path ){
			return ;
		}

		/*
		 * Search of corresponding file
		 */
		File root = context.getCurrentDirectory() ;
		String filename = filterFragment( path.toString() ) ;

		List<File> correspondingFiles = findFilesByFilename(root,filename) ;
		if ( correspondingFiles.isEmpty() ){
			context.report(
				CoreErrorCodes.ATTRIBUTE_FILE_NOT_FOUND, 
				path.toString()
			);
		}
	}
	
	/**
	 * Recursive search of a file in a directory
	 * 
	 * @param root
	 * @param filename
	 * @return
	 */
	private List<File> findFilesByFilename(File root, String filename){
		List<File> result = new ArrayList<File>() ;
		
		@SuppressWarnings("unchecked")
		Collection<File> files = FileUtils.listFiles(root, null, true) ;
		for (File file : files) {
			if ( file.getName().equals(filename) ){
				result.add(file) ;
			}
		}
		
		return result ;
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
