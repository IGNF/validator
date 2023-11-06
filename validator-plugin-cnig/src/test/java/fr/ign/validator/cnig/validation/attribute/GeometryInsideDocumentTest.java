package fr.ign.validator.cnig.validation.attribute;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geotools.geometry.jts.WKTReader2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
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

public class GeometryInsideDocumentTest extends CnigValidatorTestBase {

    private GeometryInsideDocumentValidator validator;

    @Before
    public void setUp() {
        validator = new GeometryInsideDocumentValidator();

        report = new InMemoryReportBuilder();
        context = new Context();
        context.setReportBuilder(report);

        ProjectionList projectionRepository = ProjectionList.getInstance();
        Projection projection = projectionRepository.findByCode("EPSG:2154");
        context.setProjection(projection);
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
