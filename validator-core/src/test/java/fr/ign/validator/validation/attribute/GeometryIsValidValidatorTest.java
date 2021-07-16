package fr.ign.validator.validation.attribute;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class GeometryIsValidValidatorTest {

    private InMemoryReportBuilder report;

    private GeometryIsValidValidator validator;

    private Context context;

    @Before
    public void setUp() throws Exception {
        validator = new GeometryIsValidValidator();

        report = new InMemoryReportBuilder();
        context = new Context();
        context.setReportBuilder(report);
    }

    /**
     * bind and validate attribute
     * 
     * @param context
     * @param wkt
     * @return
     */
    protected Geometry bindValidate(String wkt) {
        GeometryType type = new GeometryType();
        try {
            Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
            attribute.validate(context);
            return attribute.getBindedValue();
        } catch (IllegalArgumentException e) {
            context.report(
                context.createError(CoreErrorCodes.ATTRIBUTE_INVALID_FORMAT)
                    .setMessageParam("VALUE", wkt.toString())
                    .setMessageParam("EXPECTED_TYPE", type.getTypeName())
            );
            return null;
        }
    }

    @Test
    public void testGeometryOk() throws ParseException {
        String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNotNull(geometry);
        Assert.assertEquals(0, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
    }

    @Test
    public void testGeometryHoleOk() {
        String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0), (0.5 0.5, 0.5 1, 1 1, 1 0.5, 0.5 0.5))";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNotNull(geometry);
        Assert.assertEquals(0, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
    }

    @Test
    public void testGeometryHoleOutsideShell() {
        String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0), (3 3, 3 4, 4 4, 4 3, 3 3))";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNotNull(geometry);
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
        Assert.assertEquals(
            "La géométrie de l'objet n'est pas topologiquement correcte. Un contour intérieur (trou) est en dehors du contour extérieur du polygone (HOLE_OUTSIDE_SHELL).",
            report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage()
        );
    }

    @Test
    public void testGeometryNestedHole() {
        String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0), (.5 .5, .5 1, 1 1, 1 .5, .5 .5), (.75 .75, .75 .8, .8 .8, .8 .75, .75 .75));";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNotNull(geometry);
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
        Assert.assertEquals(
            "La géométrie de l'objet n'est pas topologiquement correcte. Un contour intérieur (trou) est inclus dans un autre contour intérieur du même polygone (NESTED_HOLES).",
            report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage()
        );
    }

    @Test
    public void testGeometryDisconnectedInterior() {
        String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0), (0 1, 1 1.5, 2 1, 1 .5, 0 1));";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNotNull(geometry);
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
        Assert.assertEquals(
            "La géométrie de l'objet n'est pas topologiquement correcte. L'intérieur du polygone est disjoint, à cause d'un ou plusieurs contours intérieurs contigus (DISCONNECTED_INTERIOR).",
            report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage()
        );
    }

    @Test
    public void testGeometrySelfIntersect() {
        String wkt = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0), (0 0, 1 0, 1 1, 0 1, 0 0))";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNotNull(geometry);
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
        Assert.assertEquals(
            "La géométrie de l'objet n'est pas topologiquement correcte. Un contour intérieur (trou) ou extérieur s'auto-intersecte ou intersecte un autre contour du même polygone (RING_SELF_INTERSECTION, SELF_INTERSECTION).",
            report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage()
        );
    }

    @Test
    public void testGeometryRingSelfIntersect() {
        String wkt = "POLYGON ((0 0, 2 0, 0 2, 2 2, 0 0))";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNotNull(geometry);
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
        Assert.assertEquals(
            "La géométrie de l'objet n'est pas topologiquement correcte. Un contour intérieur (trou) ou extérieur s'auto-intersecte ou intersecte un autre contour du même polygone (RING_SELF_INTERSECTION, SELF_INTERSECTION).",
            report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage()
        );
    }

    @Test
    public void testGeometryInnerRingSelfIntersect() {
        String wkt = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0), (1 1.5, 1 1, 1.5 1.5, 1.5 1, 1 1.5))";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNotNull(geometry);
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
        Assert.assertEquals(
            "La géométrie de l'objet n'est pas topologiquement correcte. Un contour intérieur (trou) ou extérieur s'auto-intersecte ou intersecte un autre contour du même polygone (RING_SELF_INTERSECTION, SELF_INTERSECTION).",
            report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage()
        );
    }

    @Test
    public void testGeometryInnerNestedShell() {
        String wkt = "MULTIPOLYGON (((0 0, 2 0, 2 2, 0 2, 0 0)), ((.5 .5, 1 .5, 1 1, .5 1, .5 .5)))";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNotNull(geometry);
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
        Assert.assertEquals(
            "La géométrie de l'objet n'est pas topologiquement correcte. Un polygone est inclus dans un autre polygone du même multi-polygone (NESTED_SHELLS).",
            report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage()
        );
    }

    @Test
    public void testGeometryOuterDuplicateRings() {
        String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0, 0 2, 2 2, 2 0, 0 0))";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNotNull(geometry);
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
        Assert.assertEquals(
            "La géométrie de l'objet n'est pas topologiquement correcte. Un contour intérieur (trou) ou extérieur est en doublon (DUPLICATE_RINGS).",
            report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage()
        );
    }

    @Test
    public void testGeometryShellDuplicateRings() {
        String wkt = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0), (0 0, 1 .1, .1 1, 0 0), (0 0, 1 .1, .1 1, 0 0))";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNotNull(geometry);
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
        Assert.assertEquals(
            "La géométrie de l'objet n'est pas topologiquement correcte. Un contour intérieur (trou) ou extérieur est en doublon (DUPLICATE_RINGS).",
            report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage()
        );
    }

    @Test
    public void testGeometryFewPoints() {
        String wkt = "POLYGON ((0 0))";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNull(geometry);
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID_FORMAT).size());
        Assert.assertEquals(
            "La géométrie ne peut pas être lue (coordonnée invalide, type géométrique non supporté, nombre de point insuffisant,...)",
            report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID_FORMAT).get(0).getMessage()
        );
    }

    @Test
    public void testGeometryInvalidCoordinate() {
        String wkt = "POLYGON ((0 0, 2 0, 2 2, 0 NaN, null 0))";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNull(geometry);
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID_FORMAT).size());
        Assert.assertEquals(
            "La géométrie ne peut pas être lue (coordonnée invalide, type géométrique non supporté, nombre de point insuffisant,...)",
            report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID_FORMAT).get(0).getMessage()
        );
    }

    @Test
    public void testGeometryRingNotClose() {
        String wkt = "POLYGON ((0 0, 2 0, 2 2, 0 2))";
        Geometry geometry = bindValidate(wkt);
        Assert.assertNull(geometry);
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID_FORMAT).size());
        Assert.assertEquals(
            "La géométrie ne peut pas être lue (coordonnée invalide, type géométrique non supporté, nombre de point insuffisant,...)",
            report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID_FORMAT).get(0).getMessage()
        );
    }

    @Test
    public void testGeometryRingNotClose_2() throws ParseException {
        GeometryType type = new GeometryType();

        // NOT_CLOSE
        // POLYGON((0 0, 0 2, 2 2, 0 2, 0 0));
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        coordinates.add(createCoordinate(0.0, 0.0));
        coordinates.add(createCoordinate(2.0, 0.0));
        coordinates.add(createCoordinate(2.0, 2.0));
        coordinates.add(createCoordinate(0.0, 2.0));
        coordinates.add(createCoordinate(0.0, 0.0));

        GeometryFactory factory = new GeometryFactory();
        LinearRing linearRing = new GeometryFactory().createLinearRing(
            coordinates.toArray(new Coordinate[coordinates.size()])
        );
        Polygon polygon = new Polygon(linearRing, null, factory);

        Coordinate coordinate = polygon.getCoordinates()[polygon.getCoordinates().length - 1];
        coordinate.x += 0.001;
        coordinate.y += 0.001;

        Attribute<Geometry> attribute = new Attribute<Geometry>(type, polygon);
        validator.validate(context, attribute);

        Assert.assertNotNull(attribute.getBindedValue());
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
        Assert.assertEquals(
            "La géométrie de l'objet n'est pas topologiquement correcte. La géométrie ne contient pas assez de points (au moins 2 pour une ligne, 3 pour un polygone), contient un contour non-fermé, ou bien contient des coordonnées invalides (valeur NULL) (RING_NOT_CLOSED, INVALID_COORDINATE, TOO_FEW_POINTS, INVALID_WKT).",
            report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage()
        );
    }

    private Coordinate createCoordinate(double x, double y) {
        return new Coordinate(x, y);
    }

}
