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
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.file.SingleTableModel;
import fr.ign.validator.model.type.DoubleType;

public class NumericCustomizer implements ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("NumericCustomizer");

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        List<FileModel> fileModels = document.getDocumentModel().getFileModels();
        // looking for TableModel where to add Validator
        for (FileModel fileModel : fileModels) {
            if (!(fileModel instanceof SingleTableModel)) {
                continue;
            }
            TableModel tableModel = (TableModel) fileModel;
            switch (fileModel.getName()) {
            case "N_prefixTri_COTE_VIT_DEB_P_ddd":
                addDebLinValidator(tableModel);
                addAzimuthValidator(tableModel);
                break;
            case "N_prefixTri_CHAMP_VIT_P_ddd":
                addVitesseMinValidator(tableModel);
                break;

            case "N_prefixTri_ISO_DEB_S_ddd":
                addDebLinMinValidator(tableModel);
                addDebLinMaxValidator(tableModel);
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

    private void addDebLinMaxValidator(TableModel fileModel) {
        // looking for N_prefixTri_ISO_DEB_S_ddd.DEBLIN_MAX
        AttributeType<?> attribute = fileModel.getFeatureType().getAttribute("DEBLIN_MAX");
        if (attribute == null) {
            return;
        }

        /* check attribute type and add custom validator */
        if (attribute instanceof DoubleType) {
            ((DoubleType) attribute).addValidator(new DebLinMaxValidator());
        } else {
            throw new RuntimeException(
                "DEBLIN_MAX de N_prefixTri_ISO_DEB_S_ddd n'est pas configuré comme étant un double"
            );
        }
    }

    private void addDebLinMinValidator(TableModel fileModel) {
        // looking for N_prefixTri_ISO_DEB_S_ddd.DEBLIN_MIN or
        // N_prefixTri_COTE_VIT_DEB_P_ddd.DEBLIN

        AttributeType<?> attribute = fileModel.getFeatureType().getAttribute("DEBLIN_MIN");

        if (attribute == null) {
            return;
        }

        /* check attribute type and add custom validator */
        if (attribute instanceof DoubleType) {
            ((DoubleType) attribute).addValidator(new DebLinMinValidator());
        } else {
            throw new RuntimeException("L'attribut n'est pas du type attendu");
        }
    }

    private void addDebLinValidator(TableModel fileModel) {
        // looking for N_prefixTri_ISO_DEB_S_ddd.DEBLIN_MIN or
        // N_prefixTri_COTE_VIT_DEB_P_ddd.DEBLIN

        AttributeType<?> attribute = fileModel.getFeatureType().getAttribute("DEBLIN");

        if (attribute == null) {
            return;
        }

        /* check attribute type and add custom validator */
        if (attribute instanceof DoubleType) {
            ((DoubleType) attribute).addValidator(new DebLinValidator());
        } else {
            throw new RuntimeException("L'attribut n'est pas du type attendu");
        }
    }

    private void addAzimuthValidator(TableModel fileModel) {
        // looking for N_prefixTri_COTE_VIT_DEB_P_ddd
        AttributeType<?> attribute = fileModel.getFeatureType().getAttribute("AZIMUTH");
        if (attribute == null) {
            return;
        }
        /* check attribute type and add custom validator */
        if (attribute instanceof DoubleType) {
            ((DoubleType) attribute).addValidator(new AzimuthValidator());
        } else {
            throw new RuntimeException(
                "AZIMUTH de N_prefixTri_COTE_VIT_DEB_P_ddd n'est pas configuré comme étant un double"
            );
        }
    }

    private void addVitesseMinValidator(TableModel fileModel) {
        // looking for N_prefixTri_COTE_VIT_DEB_P_ddd.VITESS_MIN
        AttributeType<?> attribute = fileModel.getFeatureType().getAttribute("VITESS_MIN");
        if (attribute == null) {
            return;
        }
        /* check attribute type and add custom validator */
        if (attribute instanceof DoubleType) {
            ((DoubleType) attribute).addValidator(new VitesseMinValidator());
        } else {
            throw new RuntimeException("ERREUR a la configuration de la table");
        }
    }

}
