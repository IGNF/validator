package fr.ign.validator;

import java.io.File;

public class ResourceHelper {
	
	/**
	 * Get File corresponding to a given path
	 * @param path ex : "/config/cnig_PLU_2014/files.xml"
	 * @return
	 */
	public static File getResourcePath(String path){
		try {
			return new File(ResourceHelper.class.getResource(path).getPath());
		}catch( NullPointerException e ){
			throw new RuntimeException(
				"Resource '"+path+"' not found"
			);
		}
	}

}
