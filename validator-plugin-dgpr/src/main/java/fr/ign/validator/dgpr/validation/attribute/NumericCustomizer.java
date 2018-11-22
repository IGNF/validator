package fr.ign.validator.dgpr.validation.attribute;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.model.type.DoubleType;
import fr.ign.validator.model.type.IntegerType;

public class NumericCustomizer implements ValidatorListener {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("NumericCustomizer");

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		List<FileModel> fileModels = document.getDocumentModel().getFileModels();
		// looking for TableModel where to add Validator
		for (FileModel fileModel : fileModels) {
			if (!(fileModel instanceof TableModel)) {
				continue;
			}
			switch (fileModel.getName()) {
				case "N_prefixTri_COMMUNE_S_ddd":
					addTauxHabValidator(fileModel);
					break;
				case "N_prefixTri_COTE_VIT_DEB_P_ddd":
					// TODO
					break;
				case "N_prefixTri_CHAMP_VIT_P_ddd":
					addVitesseMinValidator(fileModel);
					break;
	
				default:
					break;
			}
		}

	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
	}

	private void addTauxHabValidator(FileModel fileModel) {
		// looking for N_prefixTri_COMMUNE_S_ddd.TX_HAB_SAI
		AttributeType<?> attribute = fileModel.getFeatureType().getAttribute("TX_HAB_SAI") ;
		if ( attribute == null ){
			return;
		}

		/* check attribute type and add custom validator */
		if ( attribute instanceof IntegerType ) {
			((IntegerType)attribute).addValidator(new TauxHabitantValidator());
		} else {
			throw new RuntimeException("TX_HAB_SAI de N_prefixTri_COMMUNE_S_ddd n'est pas configuré comme étant un entier");
		}
	}

	private void addVitesseMinValidator(FileModel fileModel) {
		// looking for N_prefixTri_COTE_VIT_DEB_P_ddd.VITESS_MIN
		AttributeType<?> attribute = fileModel.getFeatureType().getAttribute("VITESS_MIN");
		if ( attribute == null ) {
			return;
		}
		/* check attribute type and add custom validator */
		if ( attribute instanceof DoubleType ) {
			((DoubleType)attribute).addValidator(new VitesseMinValidator());
		} else {
			throw new RuntimeException("ERREUR a la configuration de la table");
		}
	}

}
