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
    public static final ErrorCode CNIG_SIREN_INVALID = ErrorCode.valueOf("CNIG_SIREN_INVALID");

    public static final ErrorCode CNIG_DOCUMENT_NO_PDF = ErrorCode.valueOf("CNIG_DOCUMENT_NO_PDF");

    /**
     * DU - Reported when ZONE_URBA.IDURBA doesn't match any format
     */
    public static final ErrorCode CNIG_IDURBA_INVALID = ErrorCode.valueOf("CNIG_IDURBA_INVALID");
    /**
     * DU - Reported when IDURBA doesn't the excepted value for the document name
     */
    public static final ErrorCode CNIG_IDURBA_UNEXPECTED = ErrorCode.valueOf("CNIG_IDURBA_UNEXPECTED");
    /**
     * DU - Reported when no row with the expected IDURBA for is found in ZONE_URBA
     */
    public static final ErrorCode CNIG_IDURBA_NOT_FOUND = ErrorCode.valueOf("CNIG_IDURBA_NOT_FOUND");

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
}
