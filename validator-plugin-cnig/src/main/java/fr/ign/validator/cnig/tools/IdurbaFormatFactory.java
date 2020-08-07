package fr.ign.validator.cnig.tools;

import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.cnig.model.DocumentModelName;
import fr.ign.validator.model.DocumentModel;

/**
 * Helper to create {@link IdurbaFormat} format instances.
 * 
 * @author MBorne
 */
public class IdurbaFormatFactory {

    /**
     * Get supported formats
     * 
     * @return
     */
    public static List<IdurbaFormat> getFormats() {
        List<IdurbaFormat> result = new ArrayList<IdurbaFormat>(3);
        result.add(new IdurbaFormatV1());
        result.add(new IdurbaFormatV2());
        result.add(new IdurbaFormatSCOT());
        return result;
    }

    /**
     * Create format corresponding to the version of the DocumentModel.
     * 
     * @param documentModel
     * @return
     */
    public static IdurbaFormat getFormat(DocumentModel documentModel) {
        String documentModelName = documentModel.getName();

        String documentType = DocumentModelName.getDocumentType(documentModelName);
        if (documentType == null || documentType.equalsIgnoreCase("SUP")) {
            return null;
        }

        if (documentType.equalsIgnoreCase("SCOT")) {
            if (documentModelName.equalsIgnoreCase("cnig_SCOT_2013")) {
                return null;
            } else {
                return new IdurbaFormatSCOT();
            }
        }

        String version = DocumentModelName.getVersion(documentModelName);
        if (version.equals("2013") || version.equals("2014")) {
            return new IdurbaFormatV1();
        } else {
            return new IdurbaFormatV2();
        }
    }

}
