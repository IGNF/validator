package fr.ign.validator.model.file;

import java.io.File;

import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.file.DirectoryFile;
import fr.ign.validator.model.FileModel;

/**
 * 
 * Represents a directory (as a file with the type "directory")
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

	@Override
	public DocumentFile createDocumentFile(File path) {
		return new DirectoryFile(this,path);
	}

}
