package fr.ign.validator.tools;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

/**
 * 
 * Allows to manipulate file with same name and different extension (my_table.shp, my_table.dbf, my_table.cpg,...)
 * 
 * Note that all methods are case sensitive
 * 
 * @author MBorne
 *
 */
public class CompanionFileUtils {

	/**
	 * Finds companion file 
	 * 
	 * @param source Reference file (ex : PARCELLE.dbf)
	 * @param extension companion file extension (ex: shp)
	 * @return companion file (ex : PARCELLE.shp)
	 */
	public static File getCompanionFile(File source, String extension){
		File companionFile = new File( 
			source.getParent(), 
			FilenameUtils.getBaseName(source.getName())+"."+extension
		);
		return companionFile ;
	}
	
	/**
	 * Tests if a companion file exists
	 * @param source
	 * @param extension
	 * @return
	 */
	public static boolean hasCompanionFile(File source, String extension){
		
		File companionFile = getCompanionFile(source,extension);
	
		if ( companionFile.exists() ){
			return true;
		}
		return false ;
	}
	
	/**
	 * Removes a companion file
	 * 
	 * @param source
	 * @param extension
	 * @return
	 */
	public static void removeCompanionFile(File source, String extension){
		File companionFile = getCompanionFile(source,extension);
		if ( companionFile.exists() ){
			companionFile.delete() ;
		}
	}
	
}
