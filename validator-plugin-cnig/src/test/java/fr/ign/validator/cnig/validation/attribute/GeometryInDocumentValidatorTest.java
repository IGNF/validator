package fr.ign.validator.cnig.validation.attribute;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geotools.geometry.jts.WKTReader2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.io.ParseException;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.validation.CnigValidatorTestBase;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.geometry.ProjectionList;
import fr.ign.validator.model.Projection;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class GeometryInDocumentValidatorTest extends CnigValidatorTestBase {

    private GeometryInDocumentValidator validator;

    @Before
    public void setUp() {
        validator = new GeometryInDocumentValidator();

        report = new InMemoryReportBuilder();
        context = new Context();
        context.setReportBuilder(report);

        ProjectionList projectionRepository = ProjectionList.getInstance();
        Projection projection = projectionRepository.findByCode("EPSG:2154");
        context.setProjection(projection);
    }

    /**
     * Same result with the emprise prepared once (indexed segments) as with
     * Geometry.contains
     */
    @Test
    public void testPreparedEmprise() throws ParseException {
        WKTReader2 reader = new WKTReader2();
        Geometry documentEmprise = reader.read("POLYGON((0 0, 0 4, 4 4, 4 0, 0 0))");
        context.setDocumentEmprise(documentEmprise);
        GeometryInDocumentValidator preparedValidator = new GeometryInDocumentValidator(
            PreparedGeometryFactory.prepare(documentEmprise)
        );

        String[] inside = {
            "POLYGON((1 1, 1 2, 2 2, 2 1, 1 1))", "POINT(2 2)", "LINESTRING(1 1, 3 3)"
        };
        String[] outside = {
            "POLYGON((5 5, 5 7, 7 7, 7 5, 5 5))", "POLYGON((3 3, 3 5, 5 5, 5 3, 3 3))", "POINT(4 2)",
            "LINESTRING(1 1, 6 6)"
        };
        for (String wkt : inside) {
            preparedValidator.validate(context, new Attribute<Geometry>(new GeometryType(), wkt));
        }
        assertEquals(0, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_OUTSIDE_DOCUMENT_EMPRISE_ERROR));
        for (String wkt : outside) {
            preparedValidator.validate(context, new Attribute<Geometry>(new GeometryType(), wkt));
        }
        assertEquals(
            outside.length, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_OUTSIDE_DOCUMENT_EMPRISE_ERROR)
        );
    }

    @Test
    public void testGeometryInsideDocument() throws ParseException {

        String wktEmprise = "POLYGON((0 0, 0 4, 4 4, 4 0, 0 0))";

        WKTReader2 reader = new WKTReader2();
        Geometry documentEmprise = reader.read(wktEmprise);
        context.setDocumentEmprise(documentEmprise);

        String wkt = "POLYGON((1 1, 1 2, 2 2, 2 1, 1 1))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(documentEmprise);
        Assert.assertNotNull(geometry);
        assertEquals(0, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_OUTSIDE_DOCUMENT_EMPRISE_ERROR));
    }

    @Test
    public void testGeometryOutsideDocument() throws ParseException {

        String wktEmprise = "POLYGON((0 0, 0 4, 4 4, 4 0, 0 0))";

        WKTReader2 reader = new WKTReader2();
        Geometry documentEmprise = reader.read(wktEmprise);

        context.setDocumentEmprise(documentEmprise);

        String wkt = "POLYGON((5 5, 5 7, 5 5, 5 7, 5 5))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(documentEmprise);
        Assert.assertNotNull(geometry);
        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_OUTSIDE_DOCUMENT_EMPRISE_ERROR));
        List<ValidatorError> errors = report.getErrorsByCode(
            CnigErrorCodes.CNIG_GEOMETRY_OUTSIDE_DOCUMENT_EMPRISE_ERROR
        );
        assertEquals(
            String.format("La géométrie est détectée en dehors des limites du document d'urbanisme."),
            errors.get(0).getMessage()
        );
    }

}
