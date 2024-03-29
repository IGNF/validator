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
import fr.ign.validator.geometry.GeometryThreshold;
import fr.ign.validator.geometry.ProjectionList;
import fr.ign.validator.model.Projection;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class GeometryComplexityValidatorTest extends CnigValidatorTestBase {

    private GeometryComplexityValidator validator;

    @Before
    public void setUp() {
        validator = new GeometryComplexityValidator();

        report = new InMemoryReportBuilder();
        context = new Context();
        context.setReportBuilder(report);

        context.setComplexityThreshold(
            new GeometryComplexityThreshold(
                new GeometryThreshold(-1, 500, 500, 0.1, 50000),
                new GeometryThreshold(200000, 1000, 1000, 10, 50000)
            )
        );

        ProjectionList projectionRepository = ProjectionList.getInstance();
        Projection projection = projectionRepository.findByCode("EPSG:2154");
        context.setProjection(projection);
    }

    @Test
    public void testGeometryOk() throws ParseException {

        String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);
        assertEquals(0, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR));
        assertEquals(0, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING));
    }

    @Test
    public void testPointNoError() throws ParseException {

        String wkt = "Point(40 40)";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);
        assertEquals(0, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR));
        assertEquals(0, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING));
    }

    @Test
    public void testPointCount() throws ParseException {
        String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);

        context.getComplexityThreshold().getErrorThreshold().setPointCount(4);
        validator.validate(context, attribute);

        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR));
        assertEquals(
            String.format(
                "La compléxité géométrique dépasse les seuils tolérés. Nombre de sommets 5 > 4."
            ),
            report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR).get(0).getMessage()
        );

        context.getComplexityThreshold().getWarningThreshold().setPointCount(4);
        context.getComplexityThreshold().getErrorThreshold().setPointCount(10);
        validator.validate(context, attribute);

        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING));

        assertEquals(
            String.format(
                "La compléxité géométrique approche les seuils tolérés. Nombre de sommets 5 > 4."
            ),
            report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING).get(0).getMessage()
        );
    }

    @Test
    public void testPartCount() throws ParseException {

        String wkt = "MULTIPOLYGON(((40 40, 20 45, 45 30, 40 40)), ((20 35, 10 30, 10 10, 30 5, 45 20, 20 35), (30 20, 20 15, 20 25, 30 20)))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);

        context.getComplexityThreshold().getErrorThreshold().setPartCount(1);
        validator.validate(context, attribute);

        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR));
        assertEquals(
            String.format(
                "La compléxité géométrique dépasse les seuils tolérés. Nombre de parties 2 > 1."
            ),
            report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR).get(0).getMessage()
        );

        context.getComplexityThreshold().getErrorThreshold().setPartCount(2);
        context.getComplexityThreshold().getWarningThreshold().setPartCount(1);
        validator.validate(context, attribute);

        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING));
        assertEquals(
            String.format(
                "La compléxité géométrique approche les seuils tolérés. Nombre de parties 2 > 1."
            ),
            report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING).get(0).getMessage()
        );
    }

    @Test
    public void testHoleCount() throws ParseException {
        String wkt = "POLYGON((20 35, 10 30, 10 10, 30 5, 45 20, 20 35), (30 20, 20 15, 20 25, 30 20))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        Geometry geometry = attribute.getBindedValue();
        Assert.assertNotNull(geometry);

        context.getComplexityThreshold().getErrorThreshold().setRingCount(0);
        validator.validate(context, attribute);

        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR));
        assertEquals(
            String.format(
                "La compléxité géométrique dépasse les seuils tolérés. Nombre d’anneaux 1 > 0."
            ),
            report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR).get(0).getMessage()
        );

        context.getComplexityThreshold().getErrorThreshold().setRingCount(1);
        context.getComplexityThreshold().getWarningThreshold().setRingCount(0);

        validator.validate(context, attribute);

        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING));
        assertEquals(
            String.format(
                "La compléxité géométrique approche les seuils tolérés. Nombre d’anneaux 1 > 0."
            ),
            report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING).get(0).getMessage()
        );
    }

    @Test
    public void testDensity() throws ParseException {

        String wkt = "POLYGON((40 40, 20 45, 21 44, 22 43, 23 42, 24 41, 25 40, 25 40, 26 39, 27 38, 28 37, 29 36, 30 35, 31 34, 40 40))";

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        Geometry geometry = attribute.getBindedValue();
        Assert.assertNotNull(geometry);

        context.getComplexityThreshold().getErrorThreshold().setDensity(0.3);
        context.getComplexityThreshold().getErrorThreshold().setRingPointCount(8);
        validator.validate(context, attribute);

        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR));
        assertEquals(
            String.format(
                "La compléxité géométrique dépasse les seuils tolérés. Nombre de sommets > 8 et nombre moyen de point par m %f > %f.",
                0.319227f, 0.300000f
            ),
            report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR).get(0).getMessage()
        );

        context.getComplexityThreshold().getErrorThreshold().setRingPointCount(50000);
        context.getComplexityThreshold().getWarningThreshold().setDensity(0.3);
        context.getComplexityThreshold().getWarningThreshold().setRingPointCount(8);
        validator.validate(context, attribute);

        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING));
        assertEquals(
            String.format(
                "La compléxité géométrique approche les seuils tolérés. Nombre de sommets > 8 et nombre moyen de point par m %f > %f.",
                0.319227f, 0.300000f
            ),
            report.getErrorsByCode(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING).get(0).getMessage()
        );
    }

    @Test
    public void testEPSG4326Warning() throws ParseException {

        context.setComplexityThreshold(
            new GeometryComplexityThreshold(
                new GeometryThreshold(-1, 1, 1, 0.03, 24),
                new GeometryThreshold(24, 1, 1, 0.03, 24)
            )
        );

        // TODO make it work with geodetic distance
        // http://gitlab.dockerforge.ign.fr/dscr/deneigeuse-v2/blob/master/src/roadgraph/helper/geom.cpp#L257-295
        // find a equivalent in geotool
        // https://github.com/geotools/geotools/blob/main/modules/library/referencing/src/main/java/org/geotools/referencing/datum/DefaultEllipsoid.java

        String wkt = "POLYGON((7.26600766 43.70223413, 7.26631879 43.70096984, 7.26664066 43.70095433,"
            + "7.26676940 43.70097760, 7.26683378 43.70083022, 7.26710200 43.70095433,"
            + "7.26738095 43.70086125, 7.26743459 43.70100087, 7.26756334 43.70086125,"
            + "7.26764917 43.70096208, 7.26771354 43.70117151, 7.26747751 43.70131888,"
            + "7.26705908 43.70147401, 7.26714491 43.70158260, 7.26708054 43.70171446,"
            + "7.26682305 43.70187734, 7.26678013 43.70198593, 7.26673722 43.70214105,"
            + "7.26668357 43.70220311, 7.26655483 43.70227291, 7.26642608 43.70228842,"
            + "7.26631879 43.70230394, 7.26611495 43.70236599, 7.26600766 43.70223413))";

        ProjectionList projectionRepository = ProjectionList.getInstance();
        Projection projection = projectionRepository.findByCode("CRS:84");
        context.setProjection(projection);

        GeometryType type = new GeometryType();
        Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
        validator.validate(context, attribute);
        Geometry geometry = attribute.getBindedValue();

        Assert.assertNotNull(geometry);
    }

}
