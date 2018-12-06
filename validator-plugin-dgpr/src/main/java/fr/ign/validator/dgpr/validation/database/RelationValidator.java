package fr.ign.validator.dgpr.validation.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;

public class RelationValidator {

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
	public void validate(Context context, Document document, Database database) throws Exception {
		// context		
		this.context = context;
		this.database = database;
		
		//creates a two-dimensional array with the attributes that are referenced to in the model and the table associated with to each one
		String[][] references = {{"ID_S_INOND", "N_prefixTri_INONDABLE_suffixInond_S_ddd" },{"ID_TRI", "N_prefixTri_TRI_S_ddd"}};

		List<FileModel> fileModelsList = document.getDocumentModel().getFileModels();
		
		//For each table
		for (FileModel fileModel : fileModelsList) {
			if (!(fileModel instanceof TableModel)) {
				continue;
			}
			
			
			FeatureType featureType = fileModel.getFeatureType();
			
			List<AttributeType<?>> attributesList = featureType.getAttributes();
			
			//searching for the identifier attribute
			String identifierName = "";			
			for(AttributeType<?> attribute : attributesList) {
				if(attribute.isIdentifier()) {
					identifierName = attribute.getName();
					break;
				}
				
			}
			
			//Looking for attributes who are references
			for(AttributeType<?> attribute : attributesList) {
				//For each references
				for(int i=0; i<references.length; i++) {
					
					String refId = references[i][0];
					String refTab = references[i][1];

					//if the attribute name equals the reference name and the actual table is not the reference table, check the relation
					if(attribute.getName().equals(refId) && !fileModel.getName().equals(refTab)) {
						
						//if the table has no identifier, select only attribute.getName()
						String selection = attribute.getName();					
						if(!identifierName.equals("")) {
							selection = attribute.getName() + ", " + identifierName;
						}
						
						//get all the values in the reference table
						RowIterator table = database.query(
								"SELECT " + selection + " FROM " + fileModel.getName()
						);
						
						int indexIdRef = table.getColumn(attribute.getName());
						int indexAttId = table.getColumn(identifierName);
						
						//check if the attribute value exists in the reference table
						while (table.hasNext()) {
							String[] row = table.next();
							
							
							
							if(!validateRelation(refId, row[indexIdRef], refTab, fileModel.getName())) {
								//if the origin table has no identifier, the value of {ID_OBJECT} in the error message will be "sans identifiant"
								String attId = "sans identifiant";
								if(indexAttId != -1) {
									attId = row[indexAttId];
								}
								
								//if the value {row[indexIdRef]} does not exist in the table {refTab}, send error message
								context.report(context.createError(DgprErrorCodes.DGPR_RELATION_ERROR)
										.setMessageParam("ID_OBJECT", attId)
										.setMessageParam("ORIGIN_TABLE", fileModel.getName())
										.setMessageParam("ID_REF_VALUE", row[indexIdRef])
										.setMessageParam("ID_REF", refId)
										.setMessageParam("REFERENCE_TABLE", refTab)			
								);
							}
							
						}
						
					}
					
				}			
				
			}												
			
		}	
		
	}
	
	/**
	 * Check if the value {idRefValue} of the attribute {idRef} exists in the table {refTable}
	 * @param idRef
	 * @param idRefValue
	 * @param refTable
	 * @param originTable
	 * @return true if the value {idRefValue} of the attribute {idRef} exists in the table {refTable}, false otherwise
	 * @throws SQLException
	 */
	public boolean validateRelation(String idRef, String idRefValue, String refTable, String originTable/*, String originId*/) throws SQLException {
		RowIterator table = database.query(
				"SELECT " + idRef + " FROM " + refTable
		);

		int indexIdRef = table.getColumn(idRef);
		
		while (table.hasNext()) {
			String[] row = table.next();
			if(row[indexIdRef].equals(idRefValue)) {
				return true;
			}
			
		}

		return false;
		
	}

}
