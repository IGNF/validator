package fr.ign.validator.dgpr.validation.database;

import java.sql.SQLException;
import java.util.ArrayList;
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

public class IdentifierValidator implements Validator<Database> {

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

	private void runValidation() throws SQLException {
		List<FileModel> fileModelsList = context.getDocumentModel().getFileModels();
		
		//For each table
		for (FileModel fileModel : fileModelsList) {
			if (!(fileModel instanceof TableModel)) {
				continue;
			}
			
			
			FeatureType featureType = fileModel.getFeatureType();
			
			List<AttributeType<?>> attributesList = featureType.getAttributes();
			
			List<String> idLabel = new ArrayList<String>();
			
			//Looking for attributes who are identifiers
			for(AttributeType<?> attribute : attributesList) {
				if(attribute.isIdentifier()) {
					idLabel.add(attribute.getName());
				}
			}
			
			//Log if no identifier is found
			if(idLabel.size() == 0){
				log.warn(MARKER, "[Error_model] No identifier in the table " + fileModel.getName());
				return;
			}
			
			//for each identifier of a given table, check if each value is unique
			for(String label : idLabel) {
				RowIterator table = database.query(
						"SELECT " + label + " AS id, Count(" + label + ") AS count FROM " + fileModel.getName() + " GROUP BY " + label
				);
				int indexId = table.getColumn("id");
				int indexCount =table.getColumn("count");

				while (table.hasNext()) {
					String[] row = table.next();
					
					int compte = Integer.parseInt(row[indexCount]);
					
					//if not unique, send error
					if(compte > 1)
					{
						context.report(context.createError(DgprErrorCodes.DGPR_IDENTIFIER_UNICITY)
								.setMessageParam("TABLE_NAME", fileModel.getName())
								.setMessageParam("ID_NAME", row[indexId])
								.setMessageParam("ID_COUNT", row[indexCount])
						);
					}
						
				}
				
			}												
			
		}	
	}

}
