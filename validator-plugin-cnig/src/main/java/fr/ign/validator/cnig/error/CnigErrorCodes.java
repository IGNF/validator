package fr.ign.validator.cnig.error;

import fr.ign.validator.error.ErrorCode;

/**
 * CNIG specific error codes
 *
 * @author MBorne
 *
 */
public class CnigErrorCodes {
    public static final ErrorCode CNIG_INSEE_INVALID = ErrorCode.valueOf("CNIG_INSEE_INVALID");
    public static final ErrorCode CNIG_TXT_REGEXP_INVALID = ErrorCode.valueOf("CNIG_TXT_REGEXP_INVALID");
    public static final ErrorCode CNIG_SIREN_INVALID = ErrorCode.valueOf("CNIG_SIREN_INVALID");

    public static final ErrorCode CNIG_DOCUMENT_NO_PDF = ErrorCode.valueOf("CNIG_DOCUMENT_NO_PDF");
    public static final ErrorCode CNIG_PIECE_ECRITE_ONLY_PDF = ErrorCode.valueOf("CNIG_PIECE_ECRITE_ONLY_PDF");
    public static final ErrorCode CNIG_FILE_EXTENSION_INVALID = ErrorCode.valueOf("CNIG_FILE_EXTENSION_INVALID");

    /**
     * DU - Reported when ZONE_URBA.IDURBA doesn't match any format
     */
    public static final ErrorCode CNIG_IDURBA_INVALID = ErrorCode.valueOf("CNIG_IDURBA_INVALID");
    /**
     * DU - Reported when IDURBA doesn't the excepted value for the document name
     */
    public static final ErrorCode CNIG_IDURBA_UNEXPECTED = ErrorCode.valueOf("CNIG_IDURBA_UNEXPECTED");
    /**
     * DU - Reported when no row with the expected IDURBA is found in DOC_URBA
     */
    public static final ErrorCode CNIG_IDURBA_NOT_FOUND = ErrorCode.valueOf("CNIG_IDURBA_NOT_FOUND");
    /**
     * DU - Reported when multiple rows with the expected IDURBA are found in
     * DOC_URBA
     */
    public static final ErrorCode CNIG_IDURBA_MULTIPLE_FOUND = ErrorCode.valueOf("CNIG_IDURBA_MULTIPLE_FOUND");
    /**
     * DU/PLUi- Reported when less than one row are found in DOC_URBA_COM
     */
    public static final ErrorCode CNIG_DOC_URBA_COM_UNEXPECTED_SIZE = ErrorCode.valueOf(
        "CNIG_DOC_URBA_COM_UNEXPECTED_SIZE"
    );

    /**
     * SCOT - Reported when number of rows in PERIMETRE_SCOT is not 1
     */
    public static final ErrorCode CNIG_PERIMETRE_SCOT_UNEXPECTED_SIZE = ErrorCode.valueOf(
        "CNIG_PERIMETRE_SCOT_UNEXPECTED_SIZE"
    );

    /**
     * SUP - Reported when IDGEN is not unique in GENERATEUR_SUP_P/L/S tables
     */
    public static final ErrorCode CNIG_SUP_IDGEN_NOT_UNIQUE = ErrorCode.valueOf(
        "CNIG_SUP_IDGEN_NOT_UNIQUE"
    );
    /**
     * SUP - Reported when IDASS is not unique in ASSIETTE_SUP_P/L/S tables
     */
    public static final ErrorCode CNIG_SUP_IDASS_NOT_UNIQUE = ErrorCode.valueOf(
        "CNIG_SUP_IDASS_NOT_UNIQUE"
    );
    /**
     * SUP - Reported when IDGEN referenced by an ASSIETTE_SUP_P/L/S table is not
     * found in GENERATEUR_SUP_P/L/S table.
     */
    public static final ErrorCode CNIG_SUP_IDGEN_NOT_FOUND = ErrorCode.valueOf(
        "CNIG_SUP_IDGEN_NOT_FOUND"
    );
    /**
     * SUP - reported when no generateur / assiette file found
     */
    public static final ErrorCode CNIG_GENERATEUR_SUP_NOT_FOUND = ErrorCode.valueOf("CNIG_GENERATEUR_SUP_NOT_FOUND");
    public static final ErrorCode CNIG_ASSIETTE_SUP_NOT_FOUND = ErrorCode.valueOf("CNIG_ASSIETTE_SUP_NOT_FOUND");

    public static final ErrorCode CNIG_METADATA_SPECIFICATION_NOT_FOUND = ErrorCode.valueOf(
        "CNIG_METADATA_SPECIFICATION_NOT_FOUND"
    );

    public static final ErrorCode CNIG_METADATA_TYPE_INVALID = ErrorCode.valueOf("CNIG_METADATA_TYPE_INVALID");
    public static final ErrorCode CNIG_METADATA_DATEOFLASTREVISION_NOT_FOUND = ErrorCode.valueOf(
        "CNIG_METADATA_DATEOFLASTREVISION_NOT_FOUND"
    );

    public static final ErrorCode CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_NOT_FOUND = ErrorCode.valueOf(
        "CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_NOT_FOUND"
    );
    public static final ErrorCode CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_CODE_NOT_FOUND = ErrorCode.valueOf(
        "CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_CODE_NOT_FOUND"
    );
    public static final ErrorCode CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_CODE_INVALID = ErrorCode.valueOf(
        "CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_CODE_INVALID"
    );
    public static final ErrorCode CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_NOT_FOUND = ErrorCode.valueOf(
        "CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_NOT_FOUND"
    );
    public static final ErrorCode CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_UNEXPECTED = ErrorCode.valueOf(
        "CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_UNEXPECTED"
    );

    public static final ErrorCode CNIG_METADATA_KEYWORD_NOT_FOUND = ErrorCode.valueOf(
        "CNIG_METADATA_KEYWORD_NOT_FOUND"
    );
    public static final ErrorCode CNIG_METADATA_KEYWORD_INVALID = ErrorCode.valueOf(
        "CNIG_METADATA_KEYWORD_INVALID"
    );

    /**
     * Report geometry not suitable for web broadcasting
     */
    public static final ErrorCode CNIG_GEOMETRY_COMPLEXITY_WARNING = ErrorCode.valueOf(
        "CNIG_GEOMETRY_COMPLEXITY_WARNING"
    );
    public static final ErrorCode CNIG_GEOMETRY_COMPLEXITY_ERROR = ErrorCode.valueOf(
        "CNIG_GEOMETRY_COMPLEXITY_ERROR"
    );

    /**
     * Report geometry outsite the declared document emprise
     */
    public static final ErrorCode CNIG_GEOMETRY_OUTSIDE_DOCUMENT_EMPRISE_ERROR = ErrorCode.valueOf(
        "CNIG_GEOMETRY_OUTSIDE_DOCUMENT_EMPRISE_ERROR"
    );

    /**
     * To report "88" instead of "088"
     */
    public static final ErrorCode CNIG_SUP_BAD_TERRITORY_CODE = ErrorCode.valueOf(
        "CNIG_SUP_BAD_TERRITORY_CODE"
    );

}
