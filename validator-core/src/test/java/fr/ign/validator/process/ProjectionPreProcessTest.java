package fr.ign.validator.process;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.geometry.ProjectionList;
import fr.ign.validator.model.Projection;
import fr.ign.validator.report.InMemoryReportBuilder;

public class ProjectionPreProcessTest {

    private Document document = Mockito.mock(Document.class);

    private InMemoryReportBuilder report;

    private ProjectionPreProcess process = new ProjectionPreProcess();

    @Before
    public void setUp() {
        report = new InMemoryReportBuilder();
    }

    @Test
    public void testCRS84() throws Exception {
        Context context = createContext(Projection.CODE_CRS84);
        process.beforeValidate(context, document);
        {
            List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.VALIDATOR_PROJECTION_INFO);
            assertEquals(1, errors.size());
        }
        {
            List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.VALIDATOR_PROJECTION_LATLON);
            assertEquals(0, errors.size());
        }
    }

    @Test
    public void testEpsg4326() throws Exception {
        Context context = createContext("EPSG:4326");
        process.beforeValidate(context, document);
        {
            List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.VALIDATOR_PROJECTION_INFO);
            assertEquals(1, errors.size());
        }
        {
            List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.VALIDATOR_PROJECTION_LATLON);
            assertEquals(1, errors.size());
        }
    }

    @Test
    public void testEpsg2154() throws Exception {
        Context context = createContext("EPSG:2154");
        process.beforeValidate(context, document);
        {
            List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.VALIDATOR_PROJECTION_INFO);
            assertEquals(1, errors.size());
        }
        {
            List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.VALIDATOR_PROJECTION_LATLON);
            assertEquals(0, errors.size());
        }
    }

    /**
     * Create validation context
     * 
     * @param crs
     * @return
     */
    private Context createContext(String crs) {
        Projection projection = ProjectionList.getInstance().findByCode(crs);
        Context context = new Context();
        context.setReportBuilder(report);
        context.setProjection(projection);
        return context;
    }

}
