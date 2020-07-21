package fr.ign.validator.cnig.model;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * Helper to manipulate document name according to CNIG naming conventions :
 * 
 * <ul>
 * <li>DU : {CodeTerritoire}_(PLU|PLUi|POS|CC|PSMV)_{YYYYMMDD}[_{CodeDU}]</li>
 * <li>SUP : {SirenGestionnaire}_{CategorieSUP}_{CodeTerritoire}_{YYYYMMDD}</li>
 * <li>SCOT : {SirenScot}_scot or {SirenScot}_scot_{YYYYMMDD}[_{CodeDU}]
 * </ul>
 * 
 * @author MBorne
 *
 */
public class DocumentName {

    public static final String REGEXP_INSEE_OR_SIREN = "(" + MunicipalityCode.REGEXP + "|" + SirenCode.REGEXP + ")";
    public static final String REGEXP_DU_TYPE = "(PLU|PLUi|POS|CC|PSMV)";
    public static final String REGEXP_YYYYMMDD = "[0-9]{8}";
    public static final String REGEXP_DU_CODE = "[A-Z]";
    public static final String REGEXP_SUP_CATEGORY = "[a-zA-Z0-9]+";
    public static final String REGEXP_TERRITORY = "[a-zA-Z0-9]+";
    /**
     * The value of the document name (ex 25349_PLU_20200101)
     */
    private String value;

    /**
     * The document type
     */
    private DocumentType documentType;

    /**
     * Code of the territory (not available for SCOT, related to manager)
     */
    private String territory;

    /**
     * SIREN code of the manager (SCOT, SUP)
     */
    private String manager;

    /**
     * SUP category
     */
    private String category;

    /**
     * The date of the document (YYYYMMDD)
     */
    private String date;

    /**
     * Code for partial document (CodeDU)
     */
    private String part;

    public DocumentName(String value) {
        this.value = value;
        parse();
    }

    /**
     * @return true if the document name valid
     */
    public boolean isValid() {
        return documentType != null;
    }

    /**
     * @return the document type
     */
    public DocumentType getDocumentType() {
        return documentType;
    }

    /**
     * @return the code for the territory
     */
    public String getTerritory() {
        return territory;
    }

    /**
     * @return the code for the manager
     */
    public String getManager() {
        return manager;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @return the part
     */
    public String getPart() {
        return part;
    }

    /**
     * Parse value to retrieve properties
     * 
     * @throws InvalidDocumentName
     */
    private void parse() {
        if (StringUtils.isEmpty(value)) {
            this.documentType = null;
            return;
        }
        if (value.matches(getRegexpSCOT())) {
            parseSCOT();
        } else if (value.matches(getRegexpDU())) {
            parseDU();
        } else if (value.matches(getRegexpSUP())) {
            parseSUP();
        }

    }

    private void parseDU() {
        String[] parts = value.split("_");
        this.territory = parts[0];
        this.documentType = DocumentType.find(parts[1]);
        this.date = parts[2];
        this.part = parts.length >= 4 ? parts[3] : null;
    }

    private void parseSCOT() {
        String[] parts = value.split("_");
        this.manager = parts[0];
        this.documentType = DocumentType.SCoT;
        this.date = parts.length >= 3 ? parts[2] : null;
        this.part = parts.length >= 4 ? parts[3] : null ;
    }

    private void parseSUP() {
        this.documentType = DocumentType.SUP;
        String[] parts = value.split("_");
        this.manager = parts[0];
        this.category = parts[1];
        this.territory = parts[2];
        this.date = parts[3];
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Get regexp for SCOT document name
     * 
     * @return
     */
    public static String getRegexpSCOT() {
        return "(?i)" + SirenCode.REGEXP
            + "_SCOT"
            + "(_" + REGEXP_YYYYMMDD + ")?" + "(_" + REGEXP_DU_CODE + ")?";
    }

    /**
     * Get pattern to validate the name of a PLU, PLUi, POS, CC, PSMV
     * 
     * @return
     */
    public static String getRegexpDU() {
        return "(?i)" + REGEXP_INSEE_OR_SIREN
            + "_" + REGEXP_DU_TYPE
            + "_" + DocumentName.REGEXP_YYYYMMDD
            + "(_" + REGEXP_DU_CODE + ")?";
    }

    /**
     * Get pattern to validate the name of a SUP
     * 
     * @return
     */
    public static String getRegexpSUP() {
        return "(?i)" + SirenCode.REGEXP
            + "_" + REGEXP_SUP_CATEGORY
            + "_" + REGEXP_TERRITORY
            + "_" + DocumentName.REGEXP_YYYYMMDD;
    }

}
