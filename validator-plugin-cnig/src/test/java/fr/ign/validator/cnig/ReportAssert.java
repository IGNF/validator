package fr.ign.validator.cnig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.report.InMemoryReportBuilder;

/**
 * Helper class to write test counting errors in report.
 * 
 * @author MBorne
 *
 */
public class ReportAssert {

    /**
     * Check error count for a given level
     * 
     * @param expected
     * @param level
     * @param report
     */
    public static void assertCount(int expected, ErrorLevel level, InMemoryReportBuilder report) {
        List<ValidatorError> errors = report.getErrorsByLevel(level);
        if (errors.size() != expected) {
            String message = "Expecting " + expected + " " + level.toString() + "(s), found " + errors.size() + " "
                + getStatsByCode(errors);
            throw new AssertionError(message);
        }
    }

    /**
     * Check error count for a given code
     * 
     * @param expected
     * @param level
     * @param report
     */
    public static void assertCount(int expected, ErrorCode code, InMemoryReportBuilder report) {
        List<ValidatorError> errors = report.getErrorsByCode(code);
        if (errors.size() != expected) {
            String message = "Expecting " + expected + " " + code.toString() + " error(s), found " + errors.size() + " "
                + getStatsByCode(report.getErrors());
            throw new AssertionError(message);
        }
    }

    /**
     * Get counts by code
     * 
     * @param errors
     * @return
     */
    private static String getStatsByCode(List<ValidatorError> errors) {
        Map<ErrorCode, Integer> counts = new HashMap<>();
        for (ValidatorError error : errors) {
            Integer current = counts.get(error.getCode());
            counts.put(error.getCode(), current != null ? current + 1 : 1);
        }
        return counts.toString();
    }

}
