package fr.ign.validator.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;

/**
 * Regress test for {@link FilteredReportBuilder}
 * 
 * @author MBorne
 *
 */
public class FilteredReportBuilderRegressTest {

    private Context context;

    @Before
    public void setUp() {
        context = new Context();
    }

    @Test
    public void testLimitPerCode() {
        InMemoryReportBuilder original = new InMemoryReportBuilder();
        FilteredReportBuilder filtered = new FilteredReportBuilder(original, 2);

        ValidatorError[] addedErrors = {
            context.createError(CoreErrorCodes.FILE_EMPTY),
            context.createError(CoreErrorCodes.FILE_EMPTY),
            context.createError(CoreErrorCodes.ATTRIBUTE_FILE_NOT_FOUND),
            context.createError(CoreErrorCodes.FILE_EMPTY),
            context.createError(CoreErrorCodes.ATTRIBUTE_FILE_NOT_FOUND),
            context.createError(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID)
        };
        for (ValidatorError addedError : addedErrors) {
            filtered.addError(addedError);
        }
        assertEquals(2, original.getErrorsByCode(CoreErrorCodes.FILE_EMPTY).size());
        {
            // first ones are reported
            assertSame(
                addedErrors[0],
                original.getErrorsByCode(CoreErrorCodes.FILE_EMPTY).get(0)
            );
            assertSame(
                addedErrors[1],
                original.getErrorsByCode(CoreErrorCodes.FILE_EMPTY).get(1)
            );
        }
        assertEquals(1, original.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
    }

    @Test
    public void testLimitPerSubCode() {
        InMemoryReportBuilder original = new InMemoryReportBuilder();
        FilteredReportBuilder filtered = new FilteredReportBuilder(original, 2);

        ValidatorError[] addedErrors = {
            context.createError(CoreErrorCodes.FILE_EMPTY),
            context.createError(CoreErrorCodes.FILE_EMPTY),
            context.createError(CoreErrorCodes.FILE_EMPTY),
            context.createError(CoreErrorCodes.XSD_SCHEMA_ERROR).setXsdErrorCode("A"),
            context.createError(CoreErrorCodes.XSD_SCHEMA_ERROR).setXsdErrorCode("A"),
            context.createError(CoreErrorCodes.XSD_SCHEMA_ERROR).setXsdErrorCode("A"),
            context.createError(CoreErrorCodes.XSD_SCHEMA_ERROR).setXsdErrorCode("B"),
            context.createError(CoreErrorCodes.XSD_SCHEMA_ERROR).setXsdErrorCode("B"),
            context.createError(CoreErrorCodes.XSD_SCHEMA_ERROR),
            context.createError(CoreErrorCodes.XSD_SCHEMA_ERROR),
            context.createError(CoreErrorCodes.XSD_SCHEMA_ERROR),
            context.createError(CoreErrorCodes.FILE_EMPTY),
        };
        for (ValidatorError addedError : addedErrors) {
            filtered.addError(addedError);
        }
        assertEquals(2, original.getErrorsByCode(CoreErrorCodes.FILE_EMPTY).size());
        // 2 xsdErrorCode="A", 2 xsdErrorCode="B" and 2 empty xsdErrorCode
        assertEquals(6, original.getErrorsByCode(CoreErrorCodes.XSD_SCHEMA_ERROR).size());
    }

}
