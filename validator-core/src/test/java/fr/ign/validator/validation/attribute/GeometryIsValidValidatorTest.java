package fr.ign.validator.validation.attribute;

import java.util.ArrayList;
import java.util.List;

import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;

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
		context.setCoordinateReferenceSystem(CRS.decode("EPSG:4326"));
	}


	@Test
	public void testGeometryOk() throws ParseException{
		GeometryType type = new GeometryType();
		String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))";
		
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		Assert.assertNotNull(attribute.getBindedValue());
		Assert.assertEquals(0, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
	}


	@Test
	public void testGeometryHoleOk() {
		GeometryType type = new GeometryType();
		String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0), (0.5 0.5, 0.5 1, 1 1, 1 0.5, 0.5 0.5));";
		
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		Assert.assertNotNull(attribute.getBindedValue());
		Assert.assertEquals(0, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
	}


	@Test
	public void testGeometryHoleOutsideShell() {
		GeometryType type = new GeometryType();
		String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0), (3 3, 3 4, 4 4, 4 3, 3 3));";

		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		Assert.assertNotNull(attribute.getBindedValue());
		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		Assert.assertEquals("La géométrie de l'objet n'est pas topologiquement correcte (polygone en papillon, auto-intersection, etc.): HOLE_OUTSIDE_SHELL.", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}


	@Test
	public void testGeometryNestedHole() {
		GeometryType type = new GeometryType();
		String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0), (.5 .5, .5 1, 1 1, 1 .5, .5 .5), (.75 .75, .75 .8, .8 .8, .8 .75, .75 .75));";

		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		Assert.assertNotNull(attribute.getBindedValue());
		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		Assert.assertEquals("La géométrie de l'objet n'est pas topologiquement correcte (polygone en papillon, auto-intersection, etc.): NESTED_HOLES.", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}


	@Test
	public void testGeometryDisconnectedInterior() {
		GeometryType type = new GeometryType();
		String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0), (0 1, 1 1.5, 2 1, 1 .5, 0 1));";

		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		Assert.assertNotNull(attribute.getBindedValue());
		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		Assert.assertEquals("La géométrie de l'objet n'est pas topologiquement correcte (polygone en papillon, auto-intersection, etc.): DISCONNECTED_INTERIOR.", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}


	@Test
	public void testGeometrySelfIntersect() {
		GeometryType type = new GeometryType();
		String wkt = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0), (0 0, 1 0, 1 1, 0 1, 0 0))";
		
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		Assert.assertNotNull(attribute.getBindedValue());
		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		Assert.assertEquals("La géométrie de l'objet n'est pas topologiquement correcte (polygone en papillon, auto-intersection, etc.): SELF_INTERSECTION.", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}


	@Test
	public void testGeometryRingSelfIntersect() {
		GeometryType type = new GeometryType();
		String wkt = "POLYGON ((0 0, 2 0, 0 2, 2 2, 0 0))";
		
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		Assert.assertNotNull(attribute.getBindedValue());
		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		// should be ring self intersect
		// Assert.assertEquals("La géométrie de l'objet n'est pas topologiquement correcte (polygone en papillon, auto-intersection, etc.): SELF_INTERSECTION.", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}


	@Test
	public void testGeometryInnerRingSelfIntersect() {
		GeometryType type = new GeometryType();
		String wkt = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0), (1 1.5, 1 1, 1.5 1.5, 1.5 1, 1 1.5))";
		
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		Assert.assertNotNull(attribute.getBindedValue());
		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		// should be ring self intersect
		// Assert.assertEquals("", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}


	@Test
	public void testGeometryInnerNestedShell() {
		GeometryType type = new GeometryType();
		String wkt = "MULTIPOLYGON (((0 0, 2 0, 2 2, 0 2, 0 0)), ((.5 .5, 1 .5, 1 1, .5 1, .5 .5)))";
		
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		Assert.assertNotNull(attribute.getBindedValue());
		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		Assert.assertEquals("La géométrie de l'objet n'est pas topologiquement correcte (polygone en papillon, auto-intersection, etc.): NESTED_SHELLS.", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}


	@Test
	public void testGeometryOuterDuplicateRings() {
		GeometryType type = new GeometryType();
		String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0, 0 2, 2 2, 2 0, 0 0))";
		
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		Assert.assertNotNull(attribute.getBindedValue());
		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		Assert.assertEquals("La géométrie de l'objet n'est pas topologiquement correcte (polygone en papillon, auto-intersection, etc.): DUPLICATE_RINGS.", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}


	@Test
	public void testGeometryShellDuplicateRings() {
		GeometryType type = new GeometryType();
		String wkt = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0), (0 0, 1 .1, .1 1, 0 0), (0 0, 1 .1, .1 1, 0 0))";
		
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		Assert.assertNotNull(attribute.getBindedValue());
		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		Assert.assertEquals("La géométrie de l'objet n'est pas topologiquement correcte (polygone en papillon, auto-intersection, etc.): DUPLICATE_RINGS.", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}


	@Test
	public void testGeometryFewPoints() {
		GeometryType type = new GeometryType();
		String wkt = "POLYGON ((0 0))";
		
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		// should be 1 ?
		// the geometry is not enough valid to build a jts geometry from wkt
		// may be we have to look out for another error message
		Assert.assertNull(attribute.getBindedValue());
		Assert.assertEquals(0, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
	}


	@Test
	public void testGeometryInvalidCoordinate() {
		GeometryType type = new GeometryType();
		String wkt = "POLYGON ((0 0, 2 0, 2 2, 0 NaN, null 0))";
		
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);
		

		// should be 1 ?
		// the geometry is not enough valid to build a jts geometry from wkt
		// may be we have to look out for another error message
		Assert.assertNull(attribute.getBindedValue());
		Assert.assertEquals(0, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
	}


	@Test
	public void testGeometryRingNotClose() {
		GeometryType type = new GeometryType();
		String wkt = "POLYGON ((0 0, 2 0, 2 2, 0 2))";
		
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		// should be 1 ?
		// the geometry is not enough valid to build a jts geometry from wkt
		// may be we have to look out for another error message
		Assert.assertNull(attribute.getBindedValue());
		Assert.assertEquals(0, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
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
		LinearRing linearRing = new GeometryFactory().createLinearRing(coordinates.toArray(new Coordinate[coordinates.size()]));
	 	Polygon polygon = new Polygon(linearRing, null, factory);
		
	 	Coordinate coordinate = polygon.getCoordinates()[polygon.getCoordinates().length - 1];
		coordinate.x += 0.001;
		coordinate.y += 0.001;
		
	 	Attribute<Geometry> attribute = new Attribute<Geometry>(type, polygon);
		validator.validate(context, attribute);

		Assert.assertNotNull(attribute.getBindedValue());
		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		Assert.assertEquals("La géométrie de l'objet n'est pas topologiquement correcte (polygone en papillon, auto-intersection, etc.): RING_NOT_CLOSED.", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}

	private Coordinate createCoordinate(double x, double y) {
		Coordinate coordinate = new Coordinate();
		coordinate.x = x;
		coordinate.y = y;
		return coordinate;
	}


}
