package fr.ign.validator.cnig.model;

/**
 * 
 * Helper to manipulate document name according to CNIG naming conventions
 * 
 * @author MBorne
 *
 */
public class DocumentName {

    public static final String REGEXP_INSEE_OR_SIREN = "(" + MunicipalityCode.REGEXP + "|" + SirenCode.REGEXP + ")";
    public static final String REGEXP_DU_TYPE = "(PLU|PLUi|POS|CC|PSMV)";
    public static final String REGEXP_YYYYMMDD = "[0-9]{8}";
    public static final String REGEXP_DU_CODE = "[A-Z]";

    /**
     * Get pattern to validate the name of a PLU, PLUi, POS, CC, PSMV
     * 
     * @return
     */
    public static String getRegexpDU() {
        return REGEXP_INSEE_OR_SIREN
            + "_" + REGEXP_DU_TYPE
            + "_" + DocumentName.REGEXP_YYYYMMDD
            + "(_" + REGEXP_DU_CODE + ")?";
    }

}
