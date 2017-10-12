package fr.ign.validator.data.file;

import java.io.File;

import fr.ign.validator.Context;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.model.file.DirectoryModel;

public class DirectoryFile extends DocumentFile {

	private DirectoryModel fileModel;
	
	public DirectoryFile(DirectoryModel fileModel, File path) {
		super(path);
		this.fileModel = fileModel;
	}
	
	@Override
	public DirectoryModel getFileModel() {
		return fileModel;
	}

	@Override
	protected void validateContent(Context context) {
		// no specific validation
	}
	
}
