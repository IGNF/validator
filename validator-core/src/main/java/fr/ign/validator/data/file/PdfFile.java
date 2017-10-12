package fr.ign.validator.data.file;

import java.io.File;

import fr.ign.validator.Context;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.model.file.PdfModel;

public class PdfFile extends DocumentFile {

	private PdfModel fileModel ; 
	
	public PdfFile(PdfModel fileModel, File path) {
		super(path);
		this.fileModel = fileModel;
	}

	@Override
	public PdfModel getFileModel() {
		return fileModel;
	}

	@Override
	protected void validateContent(Context context) {
		// no content validation
	}

}
