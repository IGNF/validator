package fr.ign.validator.geometry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import fr.ign.validator.model.Projection;

/**
 * 
 * @author cbouche
 *
 */
public class GeometryLengthTest {

    private GeometryReader reader = new GeometryReader();

    @Test
    public void testPointLength() throws ParseException, MismatchedDimensionException, FactoryException,
        TransformException {

        Geometry geometry = reader.read("POINT(3 4)");

        assertTrue(geometry instanceof Point);
        assertFalse(geometry.isEmpty());

        ProjectionList projectionRepository = ProjectionList.getInstance();
        Projection sourceProjection = projectionRepository.findByCode("EPSG:2154");

        Double length = GeometryLength.getPerimeter(geometry, sourceProjection);
        // length must be 0m
        assertTrue(length == 0);
    }

    @Test
    public void testLineStringLength() throws ParseException, MismatchedDimensionException, FactoryException,
        TransformException {

        String wkt = "LineString(0 0, 2 0, 2 2, 0 2, 0 0)";
        Geometry geometry = reader.read(wkt);

        assertTrue(geometry instanceof LineString);
        assertFalse(geometry.isEmpty());

        ProjectionList projectionRepository = ProjectionList.getInstance();
        Projection sourceProjection = projectionRepository.findByCode("EPSG:2154");

        Double length = GeometryLength.getPerimeter(geometry, sourceProjection);
        // length must be 8m
        assertTrue(8 - length < 1);
    }

    @Test
    public void testPolygonLength() throws ParseException, MismatchedDimensionException, FactoryException,
        TransformException {

        String wkt = "POLYGON((0 0, 2 0, 2 2, 0 2, 0 0))";
        Geometry geometry = reader.read(wkt);

        assertTrue(geometry instanceof Polygon);
        assertFalse(geometry.isEmpty());

        ProjectionList projectionRepository = ProjectionList.getInstance();
        Projection sourceProjection = projectionRepository.findByCode("EPSG:2154");

        Double length = GeometryLength.getPerimeter(geometry, sourceProjection);
        // length must be 8m
        assertTrue(8 - length < 1);
    }

    @Test
    public void testWGS84PointLength() throws ParseException, MismatchedDimensionException, FactoryException,
        TransformException {

        Geometry geometry = reader.read("POINT(2.3471045 48.8556728)");

        assertTrue(geometry instanceof Point);
        assertFalse(geometry.isEmpty());

        ProjectionList projectionRepository = ProjectionList.getInstance();
        Projection sourceProjection = projectionRepository.findByCode("CRS:84");

        Double length = GeometryLength.getPerimeter(geometry, sourceProjection);
        // length must be 0m
        assertTrue(length == 0);
    }

    @Test
    public void testWGS84LineStringLength() throws ParseException, MismatchedDimensionException, FactoryException,
        TransformException {

        String wkt = "LINESTRING(2.3497009 48.857232, 2.3505377 48.857402, 2.3515248 48.857585)";
        Geometry geometry = reader.read(wkt);

        assertTrue(geometry instanceof LineString);
        assertFalse(geometry.isEmpty());

        ProjectionList projectionRepository = ProjectionList.getInstance();
        Projection sourceProjection = projectionRepository.findByCode("CRS:84");
        Double length = GeometryLength.getPerimeter(geometry, sourceProjection);
        // length must be ~139m
        assertTrue(Math.abs(139 - length) < 1);
    }

    @Test
    public void testWGS84PolygonLength() throws ParseException, MismatchedDimensionException, FactoryException,
        TransformException {

        String wkt = "POLYGON((2.3471045 48.8556728, 2.3483169 48.8556728, 2.3483169 48.8572117, 2.3471045 48.8572117, 2.3471045 48.8556728))";
        Geometry geometry = reader.read(wkt);
        assertTrue(geometry instanceof Polygon);
        assertFalse(geometry.isEmpty());

        ProjectionList projectionRepository = ProjectionList.getInstance();
        Projection sourceProjection = projectionRepository.findByCode("CRS:84");

        Double length = GeometryLength.getPerimeter(geometry, sourceProjection);
        // length must be ~520m
        assertTrue(Math.abs(520 - length) < 1);
    }

}
