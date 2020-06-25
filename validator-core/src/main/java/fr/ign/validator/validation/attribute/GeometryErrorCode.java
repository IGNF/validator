package fr.ign.validator.validation.attribute;

import org.locationtech.jts.operation.valid.TopologyValidationError;

import fr.ign.validator.code.CodeList;
import fr.ign.validator.code.CodeListValue;

public class GeometryErrorCode extends CodeListValue {

    private static final CodeList CODE_LIST = CodeList.getCodeList("GeometryErrorCode");

    public static GeometryErrorCode HOLE_OUTSIDE_SHELL = GeometryErrorCode.valueOf("HOLE_OUTSIDE_SHELL");
    public static GeometryErrorCode NESTED_HOLES = GeometryErrorCode.valueOf("NESTED_HOLES");
    public static GeometryErrorCode DISCONNECTED_INTERIOR = GeometryErrorCode.valueOf("DISCONNECTED_INTERIOR");
    public static GeometryErrorCode SELF_INTERSECTION = GeometryErrorCode.valueOf("SELF_INTERSECTION");
    public static GeometryErrorCode RING_SELF_INTERSECTION = GeometryErrorCode.valueOf("RING_SELF_INTERSECTION");
    public static GeometryErrorCode NESTED_SHELLS = GeometryErrorCode.valueOf("NESTED_SHELLS");
    public static GeometryErrorCode DUPLICATE_RINGS = GeometryErrorCode.valueOf("DUPLICATE_RINGS");
    public static GeometryErrorCode TOO_FEW_POINTS = GeometryErrorCode.valueOf("TOO_FEW_POINTS");
    public static GeometryErrorCode INVALID_COORDINATE = GeometryErrorCode.valueOf("INVALID_COORDINATE");
    public static GeometryErrorCode RING_NOT_CLOSED = GeometryErrorCode.valueOf("RING_NOT_CLOSED");
    public static GeometryErrorCode INVALID_WKT = GeometryErrorCode.valueOf("INVALID_WKT");
    public static GeometryErrorCode UNKNOWN = GeometryErrorCode.valueOf("UNKNOWN");

    private GeometryErrorCode(String value) {
        super(CODE_LIST, value);
    }

    public static GeometryErrorCode valueOf(String code) {
        if (code == null) {
            return null;
        }
        return new GeometryErrorCode(code);
    }

    /**
     * Get associated message
     * 
     * @return
     */
    public String getMessage() {
        return CODE_LIST.getDescription(getValue());
    }

    /**
     * Translate JTS code
     * 
     * @param errorType
     * @return
     */
    public static GeometryErrorCode valueOfJTS(int errorType) {
        switch (errorType) {
        case TopologyValidationError.DISCONNECTED_INTERIOR:
            return DISCONNECTED_INTERIOR;

        case TopologyValidationError.DUPLICATE_RINGS:
            return DUPLICATE_RINGS;

        case TopologyValidationError.HOLE_OUTSIDE_SHELL:
            return HOLE_OUTSIDE_SHELL;

        case TopologyValidationError.INVALID_COORDINATE:
            return INVALID_COORDINATE;

        case TopologyValidationError.NESTED_HOLES:
            return NESTED_HOLES;

        case TopologyValidationError.NESTED_SHELLS:
            return NESTED_SHELLS;

        case TopologyValidationError.RING_NOT_CLOSED:
            return RING_NOT_CLOSED;

        case TopologyValidationError.RING_SELF_INTERSECTION:
            return RING_SELF_INTERSECTION;

        case TopologyValidationError.SELF_INTERSECTION:
            return SELF_INTERSECTION;

        case TopologyValidationError.TOO_FEW_POINTS:
            return TOO_FEW_POINTS;

        default:
            return UNKNOWN;
        }
    }
}
