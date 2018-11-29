package fr.ign.validator;

import java.io.File;

/**
 * 
 * TODO Perform massive replace to use fr.ign.validator.tools.ResourceHelper.getResourceFile
 * 
 * @author MBorne
 *
 */
@Deprecated
public class ResourceHelper {
	
	/**
	 * Get File corresponding to a given path
	 * @param path ex : "/config/cnig_PLU_2014/files.xml"
	 * @return
	 */
	public static File getResourcePath(String path){
		return fr.ign.validator.tools.ResourceHelper.getResourceFile(ResourceHelper.class, path);
	}

}
