package fr.ign.validator.report;

import java.util.HashMap;
import java.util.Map;

import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.error.ValidatorError;

/**
 * 
 * Decorate a ReportBuilder to limit the maximum number of error reported for
 * each type (avoid to handle large file parsing)
 * 
 * @author FCerizay
 *
 */
public class FilteredReportBuilder implements ReportBuilder {
    /**
     * Original report builder
     */
    private ReportBuilder original;
    /**
     * Maximum number of error for each type
     */
    private int maxError;
    /**
     * Count map
     */
    private Map<String, Integer> countMap = new HashMap<>();

    /**
     * Constructor with an existing reportBuilder
     * 
     * @param original
     * @param maxError
     */
    public FilteredReportBuilder(ReportBuilder original, int maxError) {
        this.original = original;
        this.maxError = maxError;
    }

    @Override
    public void addError(ValidatorError error) {
        String countKey = getCountKey(error);
        Integer count = countMap.get(countKey);
        if (count == null) {
            count = 0;
        }
        count++;

        countMap.put(countKey, count);
        if (count > this.maxError) {
            return;
        }

        original.addError(error);
    }

    /**
     * Get count key taking in account xsdErrorCode for XSD_SCHEMA_ERROR.
     * 
     * @param error
     * @return
     */
    private String getCountKey(ValidatorError error) {
        ErrorCode errorCode = error.getCode();
        if (!CoreErrorCodes.XSD_SCHEMA_ERROR.equals(errorCode)) {
            return errorCode.toString();
        }
        return errorCode.toString() + "." + error.getXsdErrorCode();
    }

}
