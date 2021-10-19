package fr.ign.validator.cnig.validation.attribute;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.validation.CnigValidatorTestBase;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.geometry.GeometryComplexityThreshold;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class GeometryIsStreamableValidatorTest extends CnigValidatorTestBase {
	
    private GeometryIsStreamableValidator validator;

    @Before
    public void setUp() {
        validator = new GeometryIsStreamableValidator();

        report = new InMemoryReportBuilder();
        context = new Context();
        context.setReportBuilder(report);
        
        context.setComplexityThreshold(new GeometryComplexityThreshold(
        		50000, 500, 500, 0.1,
        		200000, 1000, 1000, 10
		));
    }


    @Test
    public void testGeometryOk() throws ParseException {

        String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);
    }


    @Test
    public void testThresholdWarning() throws ParseException {
        
        context.setComplexityThreshold(new GeometryComplexityThreshold(
        		4, 1, 1, 0.1,
        		200000, 1000, 1000, 10
		));
    	
        String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);
        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING));
        assertEquals(
        		"La compléxité géométrique approche les seuils tolérés. Nombre de sommets > 4.",
        		report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING).get(0).getMessage()
        );
    }


    @Test
    public void testThresholdError() throws ParseException {
        
        context.setComplexityThreshold(new GeometryComplexityThreshold(
        		4, 1, 1, 0.1,
        		4, 1, 1, 0.1
		));
    	
        String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);
        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR));
        assertEquals(
        		"La compléxité géométrique dépasse les seuils tolérés. Nombre de sommets > 4.",
        		report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR).get(0).getMessage()
        );
    }

}
