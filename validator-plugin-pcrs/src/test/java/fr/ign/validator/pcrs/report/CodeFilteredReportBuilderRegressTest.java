package fr.ign.validator.pcrs.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.report.InMemoryReportBuilder;

/**
 * Regress test for {@link FilteredReportBuilder}
 * 
 * @author MBorne
 *
 */
public class CodeFilteredReportBuilderRegressTest {

    private Context context;

    @Before
    public void setUp() {
        context = new Context();
    }

    @Test
    public void testCodeFiltering() {
        InMemoryReportBuilder original = new InMemoryReportBuilder();
        CodeFilteredReportBuilder filtered = new CodeFilteredReportBuilder(original);
        filtered.addAuthorizedTable("PlanCorpsRueSimplifie");
        filtered.addAuthorizedTable("AffleurantGeometriquePCRS");

        ValidatorError[] addedErrors = {
            context.createError(CoreErrorCodes.FILE_UNEXPECTED),
            context.createError(CoreErrorCodes.FILE_EMPTY),
            context.createError(CoreErrorCodes.MULTITABLE_UNEXPECTED).setMessage("'PlanCorpsRueSimplifie'"),
            context.createError(CoreErrorCodes.MULTITABLE_UNEXPECTED).setMessage("'AffleurantGeometriquePCRS_ligne'"),
            context.createError(CoreErrorCodes.MULTITABLE_UNEXPECTED).setMessage("'ligne_PlanCorpsRueSimplifie'"),
            context.createError(CoreErrorCodes.MULTITABLE_UNEXPECTED).setMessage("'NonAuthorizedTable'")
        };

        for (ValidatorError addedError : addedErrors) {
            filtered.addError(addedError);
        }
        assertEquals(1, original.getErrorsByCode(CoreErrorCodes.FILE_EMPTY).size());
        assertEquals(0, original.getErrorsByCode(CoreErrorCodes.FILE_UNEXPECTED).size());

        assertEquals(2, original.getErrorsByCode(CoreErrorCodes.MULTITABLE_UNEXPECTED).size());

    }

}
