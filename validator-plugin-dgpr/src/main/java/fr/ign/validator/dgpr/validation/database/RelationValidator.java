package fr.ign.validator.dgpr.validation.database;

import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.validation.Validator;

public class RelationValidator implements Validator<Database> {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("TopologicalGraphValidator");

	/**
	 * Context
	 */
	private Context context;

	/**
	 * Document
	 */
	private Database database;

	/**
	 * Check if there every ID is unique in a given table 
	 * @param context
	 * @param document
	 * @param database
	 * @throws Exception
	 */
	public void validate(Context context, Database database) {
		// context
		this.context = context;
		this.database = database;
		try {	
			runValidation();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private void runValidation() throws Exception {
		List<FileModel> fileModelsList = context.getDocumentModel().getFileModels();
		// For each table
		for (FileModel fileModel : fileModelsList) {
			if (!(fileModel instanceof TableModel)) {
				continue;
			}
			validateFileModel(fileModel);	
		}
	}

	/**
	 * Search for references attributes
	 * @param fileModel
	 * @throws SQLException
	 */
	private void validateFileModel(FileModel fileModel) throws SQLException {
		FeatureType featureType = fileModel.getFeatureType();

		List<AttributeType<?>> attributesList = featureType.getAttributes();

		// searching for the identifier attribute
		String identifierName = "";			
		for(AttributeType<?> attribute : attributesList) {
			if(attribute.isIdentifier()) {
				identifierName = attribute.getName();
				break;
			}
		}

		//Looking for attributes who are references
		for(AttributeType<?> attribute : attributesList) {
			if(!attribute.isReference()) {
				continue;
			}
			validateRelation(fileModel, attribute, identifierName);
		}

	}


	/**
	 * Join a table and a reference table to check if all relations are valid
	 * @param fileModel
	 * @param attribute
	 * @param identifierName
	 * @throws SQLException
	 */
	public void validateRelation(FileModel fileModel ,AttributeType<?> attribute, String identifierName) throws SQLException {
		String sql = "SELECT ";

		// if no identifier in the table
		if(!identifierName.equals("")) {
			sql +=  identifierName + " AS id, ";
		}

		sql +=  fileModel.getName() + "." + attribute.getName() + " AS ref, "
				+ attribute.getTableReference() + "." + attribute.getAttributeReference() + " AS id_table_ref"
				+ " FROM " + fileModel.getName() 
				+ " LEFT JOIN " + attribute.getTableReference() 
				+ " ON ref LIKE id_table_ref";

		// request to join the tables
		RowIterator table = database.query(sql);

		int indexIdTableRef = table.getColumn("id_table_ref");
		int indexId = table.getColumn("id");
		int indexRef = table.getColumn("ref");

		while (table.hasNext()) {
			String[] row = table.next();
			if(row[indexIdTableRef] == null || row[indexIdTableRef].equals("") ) {
				String idObject = "sans identifiant";
				if(indexId != -1) {
					idObject = row[indexId];
				}
				// if the value {row[indexIdTableRef]} does not exist in the reference table, send error message
				context.report(context.createError(DgprErrorCodes.DGPR_RELATION_ERROR)
						.setMessageParam("ID_OBJECT", idObject)
						.setMessageParam("ORIGIN_TABLE", fileModel.getName())
						.setMessageParam("ID_REF_VALUE", row[indexRef])
						.setMessageParam("ID_REF", attribute.getAttributeReference())
						.setMessageParam("REFERENCE_TABLE", attribute.getTableReference())			
				);
			}

		}

	}

}
