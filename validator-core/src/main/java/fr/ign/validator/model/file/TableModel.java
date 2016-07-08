package fr.ign.validator.model.file;

import fr.ign.validator.model.FileModel;
import fr.ign.validator.validation.TableValidator;

/**
 * Représente une table associée à une structure (FeatureType)
 * @author MBorne
 *
 */
public class TableModel extends FileModel {
	public static final String TYPE = "table" ;
	
	public TableModel() {
		super();
		addValidator(new TableValidator());
	}
	
	@Override
	public String getType() {
		return TYPE ;
	}
	
	
	@Override
	public String getRegexpSuffix() {
		return "\\.(dbf|DBF|tab|TAB|gml|GML|csv|CSV)";
	}
}
