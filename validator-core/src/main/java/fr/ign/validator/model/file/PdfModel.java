package fr.ign.validator.model.file;

import fr.ign.validator.model.FileModel;

/**
 * 
 * Représente un fichier avec une extension .pdf
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

}
