package fr.ign.validator.model.file;

import fr.ign.validator.model.FileModel;

/**
 * 
 * Représente un fichier de type "dossier"
 * 
 * @author MBorne
 *
 */
public class DirectoryModel extends FileModel {
	
	public static final String TYPE = "directory" ;
	
	public DirectoryModel(){
		super();
	}

	@Override
	public String getType() {
		return TYPE ;
	}
	
	@Override
	public String getRegexpSuffix() {
		return "/";
	}

}
