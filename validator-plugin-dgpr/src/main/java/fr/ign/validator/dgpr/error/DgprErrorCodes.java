package fr.ign.validator.dgpr.error;
import fr.ign.validator.error.ErrorCode;

/**
 *
 */
public class DgprErrorCodes {
	public static final ErrorCode DGPR_DOCUMENT_TEST_ERROR = ErrorCode.valueOf("DGPR_DOCUMENT_TEST_ERROR");
	public static final ErrorCode DGPR_DOCUMENT_PREFIX_ERROR = ErrorCode.valueOf("DGPR_DOCUMENT_PREFIX_ERROR");
	public static final ErrorCode DGPR_FILENAME_PREFIX_ERROR = ErrorCode.valueOf("DGPR_FILENAME_PREFIX_ERROR");
	public static final ErrorCode DGPR_VITESSE_MIN_ERROR = ErrorCode.valueOf("DGPR_VITESSE_MIN_ERROR");
	public static final ErrorCode DGPR_TX_HAB_SAI_ERROR = ErrorCode.valueOf("DGPR_TX_HAB_SAI_ERROR");
	public static final ErrorCode DGPR_AZIMUTH_ERROR = ErrorCode.valueOf("DGPR_AZIMUTH_ERROR");
	public static final ErrorCode DGPR_DEBLIN_MIN_ERROR = ErrorCode.valueOf("DGPR_DEBLIN_MIN_ERROR");
	public static final ErrorCode DGPR_DEBLIN_MAX_ERROR = ErrorCode.valueOf("DGPR_DEBLIN_MAX_ERROR");
	public static final ErrorCode DGPR_DEBLIN_ERROR = ErrorCode.valueOf("DGPR_DEBLIN_ERROR");
	public static final ErrorCode DGPR_INOND_INCLUSION_ERROR = ErrorCode.valueOf("DGPR_INOND_INCLUSION_ERROR");
	public static final ErrorCode DGPR_INOND_INCLUSION_INVALID_GEOM = ErrorCode.valueOf("DGPR_INOND_INCLUSION_INVALID_GEOM");
	public static final ErrorCode DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND = ErrorCode.valueOf("DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND");
	public static final ErrorCode DGPR_ISO_HT_INTERSECTS = ErrorCode.valueOf("DGPR_ISO_HT_INTERSECTS");
	public static final ErrorCode DGPR_ISO_HT_MIN_MAX_VALUE_UNCOVERED = ErrorCode.valueOf("DGPR_ISO_HT_MIN_MAX_VALUE_UNCOVERED");
	public static final ErrorCode DGPR_IDENTIFIER_UNICITY = ErrorCode.valueOf("DGPR_IDENTIFIER_UNICITY");
	public static final ErrorCode DGPR_MULTIPLE_ATTRIBUTES_IDENTIFIER = ErrorCode.valueOf("DGPR_MULTIPLE_ATTRIBUTES_IDENTIFIER");
}
