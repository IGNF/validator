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
    public void testPointThresholdWarning() throws ParseException {

        context.setComplexityThreshold(new GeometryComplexityThreshold(
        		4, 1, 1, 10,
        		6, 1, 1, 10
		));
    	
        String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);
        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING));
        assertEquals(
        		"La compléxité géométrique approche les seuils tolérés. Nombre de sommets 5 > 4.",
        		report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING).get(0).getMessage()
        );
    }


    @Test
    public void testPointThresholdError() throws ParseException {
        
        context.setComplexityThreshold(new GeometryComplexityThreshold(
        		3, 1, 1, 100,
        		4, 1, 1, 100
		));
    	
        String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);
        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR));
        assertEquals(
        		"La compléxité géométrique dépasse les seuils tolérés. Nombre de sommets 5 > 4.",
        		report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR).get(0).getMessage()
        );
    }


    @Test
    public void testHolesThresholdWarning() throws ParseException {

        context.setComplexityThreshold(new GeometryComplexityThreshold(
        		10, 0, 1, 100,
        		10, 1, 1, 100
		));

        String wkt = "POLYGON((20 35, 10 30, 10 10, 30 5, 45 20, 20 35), (30 20, 20 15, 20 25, 30 20))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);
        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING));
        assertEquals(
        		"La compléxité géométrique approche les seuils tolérés. Nombre d’anneaux 1 > 0.",
        		report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING).get(0).getMessage()
        );
    }


    @Test
    public void testHolesThresholdError() throws ParseException {

        context.setComplexityThreshold(new GeometryComplexityThreshold(
        		10, 0, 1, 100,
        		10, 0, 1, 100
		));

        String wkt = "POLYGON((20 35, 10 30, 10 10, 30 5, 45 20, 20 35), (30 20, 20 15, 20 25, 30 20))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);
        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR));
        assertEquals(
        		"La compléxité géométrique dépasse les seuils tolérés. Nombre d’anneaux 1 > 0.",
        		report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR).get(0).getMessage()
        );
    }


    @Test
    public void testPartThresholdWarning() throws ParseException {

        context.setComplexityThreshold(new GeometryComplexityThreshold(
        		14, 1, 1, 100,
        		14, 1, 2, 100
		));

        String wkt = "MULTIPOLYGON(((40 40, 20 45, 45 30, 40 40)), ((20 35, 10 30, 10 10, 30 5, 45 20, 20 35), (30 20, 20 15, 20 25, 30 20)))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);
        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING));
        assertEquals(
        		"La compléxité géométrique approche les seuils tolérés. Nombre de parties 2 > 1.",
        		report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING).get(0).getMessage()
        );
    }


    @Test
    public void testDensityWarning() throws ParseException {

        context.setComplexityThreshold(new GeometryComplexityThreshold(
        		15, 1, 1, 0.3,
        		15, 1, 1, 0.4
		));

        String wkt = "POLYGON((40 40, 20 45, 21 44, 22 43, 23 42, 24 41, 25 40, 25 40, 26 39, 27 38, 28 37, 29 36, 30 35, 31 34, 40 40))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);
        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING));
        assertEquals(
        		"La compléxité géométrique approche les seuils tolérés. Nombre moyen de point par m 0,319227 > 0,300000.",
        		report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING).get(0).getMessage()
        );
    }


    @Test
    public void testDensityError() throws ParseException {

        context.setComplexityThreshold(new GeometryComplexityThreshold(
        		15, 1, 1, 0.3,
        		15, 1, 1, 0.3
		));

        String wkt = "POLYGON((40 40, 20 45, 21 44, 22 43, 23 42, 24 41, 25 40, 25 40, 26 39, 27 38, 28 37, 29 36, 30 35, 31 34, 40 40))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);
        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR));
        assertEquals(
        		"La compléxité géométrique dépasse les seuils tolérés. Nombre moyen de point par m 0,319227 > 0,300000.",
        		report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR).get(0).getMessage()
        );
    }

}
