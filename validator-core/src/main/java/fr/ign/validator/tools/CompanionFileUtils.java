package fr.ign.validator.tools;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

/**
 * 
 * Utilitaire pour les fichiers accompagnants des fichiers d'extensions différentes (exemple : cpg)
 * 
 * @author MBorne
 *
 */
public class CompanionFileUtils {

	/**
	 * Renvoie un fichier compagnon pour un fichier.
	 * 
	 * @param file Le fichier de base (exemple : PARCELLE.shp)
	 * @param extension L'extension du fichier companion (exemple : cpg)
	 * @return PARCELLE.cpg
	 */
	public static File getCompanionFile(File source, String extension){
		File companionFile = new File( 
			source.getParent(), 
			FilenameUtils.getBaseName(source.getName())+"."+extension
		);
		return companionFile ;
	}
	
	/**
	 * 
	 * 
	 * @param file Le fichier de base (exemple : PARCELLE.shp)
	 * @param extension L'extension du fichier companion (exemple : cpg)
	 * @return boolean
	 */
	public static boolean hasCompanionFile(File source, String extension){
		
		File companionFile = getCompanionFile(source,extension);
	
		if ( companionFile.exists() ){
			return true;
		}
		return false ;
	}
	
	/**
	 * Supprime un fichier compagnon
	 * 
	 * @warning sensible à la casse
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
