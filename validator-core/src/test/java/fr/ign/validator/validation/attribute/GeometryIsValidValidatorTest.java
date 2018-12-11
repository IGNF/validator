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

		Attribute<Geometry> attribute = new Attribute<Geometry>(type, polygon);
		validator.validate(context, attribute);

		Assert.assertEquals(0, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		// Assert.assertEquals("", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}


	@Test
	public void testGeometryNotClose() throws ParseException {
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

		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		Assert.assertEquals("La géométrie de l'objet n'est pas topologiquement correcte (polygone en papillon, auto-intersection, etc.): RING_NOT_CLOSED.", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}



	@Test
	public void testGeometrySelfIntersect() throws ParseException{
		GeometryType type = new GeometryType();

		// SELF_INTERSECTION
		// POLYGON((0 0, 2 0, 1 1, 2 2, 3 1, 2 0, 4 0, 4 4, 0 4, 0 0));
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		coordinates.add(createCoordinate(0.0, 0.0));
		coordinates.add(createCoordinate(2.0, 0.0));
		coordinates.add(createCoordinate(1.0, 1.0));
		coordinates.add(createCoordinate(2.0, 2.0));
		coordinates.add(createCoordinate(3.0, 1.0));
		coordinates.add(createCoordinate(2.0, 4.0));
		coordinates.add(createCoordinate(4.0, 4.0));
		coordinates.add(createCoordinate(0.0, 4.0));
		coordinates.add(createCoordinate(0.0, 0.0));

		GeometryFactory factory = new GeometryFactory();
		LinearRing linearRing = new GeometryFactory().createLinearRing(coordinates.toArray(new Coordinate[coordinates.size()]));
	 	Polygon polygon = new Polygon(linearRing, null, factory);

		Attribute<Geometry> attribute = new Attribute<Geometry>(type, polygon);
		validator.validate(context, attribute);

		// !! should be 1
		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		Assert.assertEquals("La géométrie de l'objet n'est pas topologiquement correcte (polygone en papillon, auto-intersection, etc.): SELF_INTERSECTION.", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}


	@Test
	public void testGeometrySelfIntersect_2() {
		// POLYGON ((20 20, 120 20, 120 220, 180 220, 140 160, 200 160, 180 220, 240 220, 240 120, 20 120,  20 20))
		GeometryType type = new GeometryType();
		String wkt = "POLYGON ((20 20, 120 20, 120 220, 180 220, 140 160, 200 160, 180 220, 240 220, 240 120, 20 120,  20 20))";
		
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, wkt);
		validator.validate(context, attribute);

		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		Assert.assertEquals("La géométrie de l'objet n'est pas topologiquement correcte (polygone en papillon, auto-intersection, etc.): SELF_INTERSECTION.", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}


	@Test
	public void testGeometrySelfIntersect_3() throws ParseException{
		GeometryType type = new GeometryType();

		// ERROR PAPILLON
		// POLYGON((0 0, 0 1, 2 1, 2 2, 1 2, 1 0, 0 0));
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		coordinates.add(createCoordinate(0.0, 0.0));
		coordinates.add(createCoordinate(0.0, 1.0));
		coordinates.add(createCoordinate(2.0, 1.0));
		coordinates.add(createCoordinate(2.0, 2.0));
		coordinates.add(createCoordinate(1.0, 2.0));
		coordinates.add(createCoordinate(1.0, 0.0));
		coordinates.add(createCoordinate(0.0, 0.0));
		
		GeometryFactory factory = new GeometryFactory();
		LinearRing linearRing = new GeometryFactory().createLinearRing(coordinates.toArray(new Coordinate[coordinates.size()]));
	 	Polygon polygon = new Polygon(linearRing, null, factory);

		Attribute<Geometry> attribute = new Attribute<Geometry>(type, polygon);
		validator.validate(context, attribute);

		// !! should be 1
		Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
		Assert.assertEquals("La géométrie de l'objet n'est pas topologiquement correcte (polygone en papillon, auto-intersection, etc.): SELF_INTERSECTION.", report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).get(0).getMessage());
	}
	

	private Coordinate createCoordinate(double x, double y) {
		Coordinate coordinate = new Coordinate();
		coordinate.x = x;
		coordinate.y = y;
		return coordinate;
	}


}
