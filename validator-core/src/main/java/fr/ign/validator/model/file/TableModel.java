package fr.ign.validator.model.file;

import java.io.File;

import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.file.TableFile;
import fr.ign.validator.model.FileModel;

/**
 * Represents a table associated to a FeatureType
 * 
 * @author MBorne
 *
 */
public class TableModel extends FileModel {
	public static final String TYPE = "table" ;
	
	public TableModel() {
		super();
	}

	@Override
	public String getType() {
		return TYPE ;
	}
	
	@Override
	public String getRegexpSuffix() {
		return "\\.(dbf|DBF|tab|TAB|gml|GML|csv|CSV)";
	}
	
	@Override
	public DocumentFile createDocumentFile(File path) {
		return new TableFile(this,path);
	}

}
