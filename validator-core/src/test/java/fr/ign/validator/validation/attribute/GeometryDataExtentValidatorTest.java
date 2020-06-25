package fr.ign.validator.validation.attribute;

import org.geotools.geometry.jts.JTS;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class GeometryDataExtentValidatorTest {

    private InMemoryReportBuilder report;
    private GeometryDataExtentValidator validator;
    private Context context;

    @Before
    public void setUp() throws Exception {
        validator = new GeometryDataExtentValidator();

        report = new InMemoryReportBuilder();
        context = new Context();
        context.setReportBuilder(report);
    }

    @Test
    public void testNullGeometryNullDataExtent() {
        context.setNativeDataExtent(null);
        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, null);
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testNullGeometryWithDataExtent() {
        context.setNativeDataExtent(JTS.toGeometry(new Envelope(0.0, 1.0, 0.0, 1.0)));
        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, null);
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testValidGeometryWithDataExtent() {
        context.setNativeDataExtent(JTS.toGeometry(new Envelope(0.0, 1.0, 0.0, 1.0)));
        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, JTS.toGeometry(new Envelope(0.2, 0.8, 0.2, 0.8)));
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testNotContainedGeometryWithDataExtent() {
        context.setNativeDataExtent(JTS.toGeometry(new Envelope(0.0, 1.0, 0.0, 1.0)));
        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(
            type, JTS.toGeometry(new Envelope(-0.2, 0.8, 0.2, 0.8))
        );
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());
        Assert.assertEquals(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID_DATA_EXTENT, report.getErrors().get(0).getCode());
        Assert.assertEquals(ErrorLevel.ERROR, report.getErrors().get(0).getLevel());
    }
}