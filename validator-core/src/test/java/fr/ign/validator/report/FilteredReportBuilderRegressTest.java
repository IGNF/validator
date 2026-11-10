package fr.ign.validator.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorLevel;
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

    @Test
    public void testLimitReachedIsReportedOnce() {
        InMemoryReportBuilder original = new InMemoryReportBuilder();
        FilteredReportBuilder filtered = new FilteredReportBuilder(original, 2);

        for (int i = 0; i < 5; i++) {
            filtered.addError(context.createError(CoreErrorCodes.FILE_EMPTY));
        }
        filtered.addError(context.createError(CoreErrorCodes.ATTRIBUTE_FILE_NOT_FOUND));

        assertEquals(2, original.getErrorsByCode(CoreErrorCodes.FILE_EMPTY).size());
        // reported once for FILE_EMPTY, nothing for ATTRIBUTE_FILE_NOT_FOUND
        List<ValidatorError> infos = original.getErrorsByCode(CoreErrorCodes.VALIDATOR_ERROR_LIMIT_REACHED);
        assertEquals(1, infos.size());
        assertEquals(ErrorLevel.INFO, infos.get(0).getLevel());
        assertTrue(infos.get(0).getMessage().contains("FILE_EMPTY"));
        assertTrue(infos.get(0).getMessage().contains("2"));
    }

}
