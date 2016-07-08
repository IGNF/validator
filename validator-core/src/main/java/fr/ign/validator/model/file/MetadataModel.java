package fr.ign.validator.model.file;

import fr.ign.validator.model.FileModel;
import fr.ign.validator.validation.MetadataValidator;

public class MetadataModel extends FileModel {
	public static final String TYPE = "metadata" ;
	
	public MetadataModel(){
		super();
		addValidator(new MetadataValidator());
	}
	
	@Override
	public String getType() {
		return TYPE ;
	}
	
	@Override
	public String getRegexpSuffix() {
		return "\\.(xml|XML)";
	}

	
}
