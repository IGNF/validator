package fr.ign.validator.dgpr.validation.database;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

import fr.ign.validator.Context;
import fr.ign.validator.report.InMemoryReportBuilder;

public class GraphTopologyValidatorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();

	private Context context;

	private WKTReader format;

	private WKTWriter writer;


	@Before
	public void setUp() {
		format = new WKTReader();

		writer = new WKTWriter();

		context = new Context();
		context.setReportBuilder(reportBuilder);

		// set projection ??
	}


	private Geometry getGeometry(String wkt) {
		try {
			Geometry geometry = format.read( wkt ) ;
			return geometry ;
		} catch (Exception e) {
			throw new IllegalArgumentException(
					String.format("Format de géométrie invalide : {}",wkt)					
			);
		}
	}
	
	
	private Geometry getUnion(String[] wkts) {
		List<Geometry> geometries = new ArrayList<Geometry>();
		for (String wkt : wkts) {
			Geometry geometry = getGeometry(wkt);
			geometries.add(geometry);
		}
		GeometryFactory geometryFactory = new GeometryFactory();
		GeometryCollection geometryCollection = (GeometryCollection) geometryFactory.buildGeometry(geometries);
		Geometry union = geometryCollection.union();
		return union;
	}


	@Test
	public void testUnion() {
		String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))";
		Geometry geometry = getGeometry(wkt);
		
		String[] wktParts = {
				"POLYGON((0 0, 1 0, 1 2, 0 2, 0 0))",
				"POLYGON((1 0, 2 0, 2 2, 1 2, 1 0))"
		};
		Geometry partUnion = getUnion(wktParts);

		String wktPartUnion = writer.write(partUnion);

		// Expected geometry
		Assert.assertEquals("POLYGON ((1 0, 0 0, 0 2, 1 2, 2 2, 2 0, 1 0))", wktPartUnion);
		// Expected equals
		Assert.assertTrue(partUnion.equalsTopo(geometry));
		Assert.assertTrue(geometry.equalsTopo(partUnion));
	}


	@Test
	public void testBuffer() {
		Geometry geometry = getGeometry("POLYGON((-1 -1, 1 -1, 1 1, -1 1, -1 -1))");
		Geometry buffer1 = geometry.buffer(1);
		Geometry buffer10 = geometry.buffer(10);

		String geometry_1 = writer.write(geometry.buffer(1));
		String geometry_10 = writer.write(geometry.buffer(10));
		String geometry_01 = writer.write(geometry.buffer(0.1));

		Assert.assertNotEquals(geometry_10, geometry_1);
		Assert.assertNotEquals(geometry_01, geometry_1);
		Assert.assertTrue(buffer1.getArea() * 10 < buffer10.getArea());
	}


	@Test
	public void testBufferContains() {
		Geometry geometry = getGeometry("POLYGON((-2 -2, 2 -2, 2 2, -2 2, -2 -2))");
		Geometry buffer = getGeometry("POLYGON((-1 -1, 1 -1, 1 1, -1 1, -1 -1))").buffer(1);

		Assert.assertTrue(geometry.contains(buffer));
	}


	@Test
	public void testBufferHoleCovered() {
		Geometry bufferNoHole = getGeometry("POLYGON((-1 -1, 1 -1, 1 1, -1 1, -1 -1))").buffer(1);
		Geometry bufferHole = getGeometry("POLYGON((-1 -1, 1 -1, 1 1, -1 1, -1 -1), (0 0, 0.1 0, 0.1 0.1, 0 0.1, 0 0))").buffer(1);

		Assert.assertTrue(bufferNoHole.equalsTopo(bufferHole));
	}


	@Test
	public void testBufferHoleNotCovered() {
		Geometry bufferNoHole = getGeometry("POLYGON((-1 -1, 1 -1, 1 1, -1 1, -1 -1))").buffer(1);
		Geometry bufferHole = getGeometry("POLYGON((-1 -1, 1 -1, 1 1, -1 1, -1 -1), (0 0, 0.1 0, 0.1 0.1, 0 0.1, 0 0))").buffer(0.01);

		Assert.assertFalse(bufferNoHole.equalsTopo(bufferHole));
	}


	@Test
	public void testGeometryTopologyEquals() {
		String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))";
		Geometry geometry = getGeometry(wkt);
		
		String[] wktParts = {
				"POLYGON((0 0, 1 0, 1 2, 0 2, 0 0))",
				"POLYGON((1 0, 2 0, 2 2, 1 2, 1 0))"
		};
		Geometry union = getUnion(wktParts);

		// should be 'equals', share boundary, share interior
		// cast geometry is not mandatory
		Assert.assertTrue(geometry.equals((Geometry) union));
		Assert.assertTrue(union.equals((Geometry) geometry));

		Assert.assertTrue(geometry.equalsTopo((Geometry) union));
		Assert.assertTrue(union.equalsTopo((Geometry) geometry));

		Assert.assertTrue(geometry.contains(union));
		Assert.assertTrue(union.contains(geometry));
		
		// this test
		Assert.assertTrue(geometry.equalsTopo(union));
		Assert.assertTrue(union.equalsTopo(geometry));

		// should not be exactly equals
		// more point in the union geometry 
		Assert.assertFalse(geometry.equalsNorm((Geometry) union));
		Assert.assertFalse(union.equalsNorm((Geometry) geometry));

		Assert.assertFalse(geometry.equalsExact((Geometry) union));
		Assert.assertFalse(union.equalsExact((Geometry) geometry));

		Assert.assertFalse(geometry.equalsExact((Geometry) union, 10));
		Assert.assertFalse(union.equalsExact((Geometry) geometry, 10));
	}


	@Test
	public void testWithNoTolerance() {
		String wkt = "POLYGON((0 0, 0 2.00001, 2.00001 2.00001, 2.00001 0, 0 0))";
		Geometry geometry = getGeometry(wkt);
		
		String[] wktParts = {
				"POLYGON((0 0, 1 0, 1 2, 0 2, 0 0))",
				"POLYGON((1 0, 2 0, 2 2, 1 2, 1 0))"
		};
		Geometry union = getUnion(wktParts);
		
		// should not be 'equals'
		Assert.assertFalse(geometry.equals(union));
		Assert.assertFalse(union.equals(geometry));
	}


	@Test
	public void testEqualsWithBufferTolerance() {
		String wkt = "POLYGON((0 0, 0 2.00001, 2.00001 2.00001, 2.00001 0, 0 0))";
		Geometry geometry = getGeometry(wkt);
		
		String[] wktParts = {
				"POLYGON((0 0, 1 0, 1 2, 0 2, 0 0))",
				"POLYGON((1 0, 2 0, 2 2, 1 2, 1 0))"
		};
		Geometry union = getUnion(wktParts);
		
		// each buffer element should be contain in one other
		// each buffer element should not have any holes
		Assert.assertTrue(geometry.buffer(0.0001).contains(union));
		Assert.assertTrue(union.buffer(0.0001).contains(geometry));
		Assert.assertEquals(0, ((Polygon) union).getNumInteriorRing());
		Assert.assertEquals(0, ((Polygon) geometry).getNumInteriorRing());

		// should not be 'equals'
		Assert.assertFalse(geometry.equals((Geometry) union));
		Assert.assertFalse(union.equals((Geometry) geometry));
	}


	@Test
	public void testNotEqualsWithBufferTolerance() {
		String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))";
		Geometry geometry = getGeometry(wkt);
		
		String[] wktParts = {
				"POLYGON((0 0, 1 0, 1 2, 0 2, 0 0), (0.01 0.01, 0.02 0.01, 0.02 0.02, 0.01 0.02, 0.01 0.01))",
				"POLYGON((1 0, 2 0, 2 2, 1 2, 1 0))"
		};
		Geometry union = getUnion(wktParts);
		
		// should not be equal
		// one element still have holes !
		Assert.assertFalse(geometry.equals(union));
		Assert.assertFalse(union.equals(geometry));
		Assert.assertEquals(1, ((Polygon) union).getNumInteriorRing());
		Assert.assertEquals(0, ((Polygon) geometry).getNumInteriorRing());

		// should be close
		Assert.assertTrue(geometry.buffer(0.01).contains(union));
		Assert.assertTrue(union.buffer(0.01).contains(geometry));
	}


	@Test
	public void testWithValidatorMethod() {
		String wkt = "POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))";
		Geometry geometry = getGeometry(wkt);
		
		String[] wktParts = {
				"POLYGON((0 0, 1 0, 1 2, 0 2, 0 0), (.01 .01, .02 .01, .02 .02, .01 .02, .01 .01))",
				"POLYGON((1 0, 2 0, 2 2, 1 2, 1 0))"
		};
		Geometry union = getUnion(wktParts);
		
		
		Assert.assertTrue(GraphTopologyValidator.topologyEqualsWithTolerance(geometry, union, 1));
		Assert.assertTrue(GraphTopologyValidator.topologyEqualsWithTolerance(geometry, union, 0.1));
		Assert.assertTrue(GraphTopologyValidator.topologyEqualsWithTolerance(geometry, union, 0.01));
		Assert.assertFalse(GraphTopologyValidator.topologyEqualsWithTolerance(geometry, union, 0.001));
		Assert.assertFalse(GraphTopologyValidator.topologyEqualsWithTolerance(geometry, union, 0.00001));
	}

}
