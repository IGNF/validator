package fr.ign.validator.pcrs.report;

import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.report.ReportBuilder;

/**
 * 
 * Decorate a ReportBuilder. Filter specific error (codes/message) -
 * FILE_UNEXPECTED - MULTITABLE_UNEXPECTED
 * 
 * @author cbouche
 *
 */
public class CodeFilteredReportBuilder implements ReportBuilder {

    /**
     * Original report builder
     */
    private ReportBuilder original;

    private List<String> authorizedTables = new ArrayList<String>();

    /**
     * Constructor with an existing reportBuilder
     * 
     * @param original
     * @param maxError
     */
    public CodeFilteredReportBuilder(ReportBuilder original) {
        this.original = original;
    }

    @Override
    public void addError(ValidatorError error) {
        ErrorCode errorCode = error.getCode();

        // TODO
        // 1. test error code
        // 2. test message

        if (errorCode.equals(CoreErrorCodes.FILE_UNEXPECTED)) {
            return;
        }

        if (errorCode.equals(CoreErrorCodes.MULTITABLE_UNEXPECTED)
            && error.getMessage().toLowerCase().matches(getAuthorizedRegexp().toLowerCase())) {
            return;
        }

        original.addError(error);
    }

    public void addAuthorizedTable(String tablename) {
        authorizedTables.add(tablename);
    }

    public String getAuthorizedRegexp() {
        return ".*'(" + String.join("|", authorizedTables) + ")(_.+)?'.*";
    }

}
