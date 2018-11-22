package fr.ign.validator.dgpr.validation.document;

import java.util.List;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.dgpr.validation.attribute.VitesseMinValidator;
import fr.ign.validator.dgpr.validation.attribute.DebLinMinValidator;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.model.type.DoubleType;

public class NumericConstraintsCustomizer implements ValidatorListener {

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		/*
		 * adding VitesseMinValidator to N_prefixTri_CHAMP_VIT_P_ddd.VITESS_MIN 
		 * adding DebLinMinValidator to N_prefixTri_ISO_DEB_S_ddd.DEBLIN_MIN
		 * 
		 * ...
		 */
		List<FileModel> fileModels = document.getDocumentModel().getFileModels();
		for (FileModel fileModel : fileModels) {
			if ( fileModel instanceof TableModel || fileModel.getName().equals("N_prefixTri_CHAMP_VIT_P_ddd")) {
				// on a le bon fichier
				AttributeType<?> attribute = fileModel.getFeatureType().getAttribute("VITESS_MIN");
				if ( attribute == null ) {
					continue;
				}
				/* check attribute type and add custom validator */
				if ( attribute instanceof DoubleType ) {
					((DoubleType)attribute).addValidator(new VitesseMinValidator());
				} else {
					throw new RuntimeException("ERREUR a la configuration de la table");
				}
			}
			
			if ( fileModel instanceof TableModel || fileModel.getName().equals("N_prefixTri_ISO_DEB_S_ddd")) {
				// on a le bon fichier
				AttributeType<?> attribute = fileModel.getFeatureType().getAttribute("DEBLIN_MIN");
				if ( attribute == null ) {
					continue;
				}
				/* check attribute type and add custom validator */
				if ( attribute instanceof DoubleType ) {
					((DoubleType)attribute).addValidator(new DebLinMinValidator());
				} else {
					throw new RuntimeException("ERREUR a la configuration de la table");
				}
			}

		}
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
	}

}
