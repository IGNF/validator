package fr.ign.validator.model.file;

import java.io.File;

import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.file.PdfFile;
import fr.ign.validator.model.FileModel;

/**
 * 
 * Represents a file with the extension ".pdf"
 * 
 * @author MBorne
 *
 */
public class PdfModel extends FileModel {
	
	public static final String TYPE = "pdf" ;
	
	public PdfModel(){
		super();
	}
	
	@Override
	public String getType() {
		return TYPE ;
	}
	
	@Override
	public String getRegexpSuffix() {
		return "\\.(pdf|PDF)";
	}
	
	@Override
	public DocumentFile createDocumentFile(File path) {
		return new PdfFile(this,path);
	}

}
