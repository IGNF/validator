package fr.ign.validator.validation.attribute;

import org.geotools.geometry.jts.JTS;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.report.InMemoryReportBuilder;
import junit.framework.TestCase;

public class GeometryDataExtentValidatorTest extends TestCase {
	
	private InMemoryReportBuilder report ;
	private GeometryDataExtentValidator validator ;
	private Context context;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		validator = new GeometryDataExtentValidator();
		
		report = new InMemoryReportBuilder() ;
		context = new Context();
		context.setReportBuilder(report);
	}

	public void testNullGeometryNullDataExtent(){
		context.setNativeDataExtent(null);
		GeometryType type = new GeometryType();
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, null);
		validator.validate(context, attribute);
		assertEquals(0, report.countErrors() ) ;
	}
	
	public void testNullGeometryWithDataExtent(){
		context.setNativeDataExtent(JTS.toGeometry(new Envelope(0.0,1.0,0.0,1.0)));
		GeometryType type = new GeometryType();
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, null);
		validator.validate(context, attribute);
		assertEquals(0, report.countErrors() ) ;
	}
	
	public void testValidGeometryWithDataExtent(){
		context.setNativeDataExtent(JTS.toGeometry(new Envelope(0.0,1.0,0.0,1.0)));
		GeometryType type = new GeometryType();
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, JTS.toGeometry(new Envelope(0.2,0.8,0.2,0.8)));
		validator.validate(context, attribute);
		assertEquals(0, report.countErrors() ) ;
	}
	
	public void testNotContainedGeometryWithDataExtent(){
		context.setNativeDataExtent(JTS.toGeometry(new Envelope(0.0,1.0,0.0,1.0)));
		GeometryType type = new GeometryType();
		Attribute<Geometry> attribute = new Attribute<Geometry>(type, JTS.toGeometry(new Envelope(-0.2,0.8,0.2,0.8)));
		validator.validate(context, attribute);
		assertEquals(1, report.countErrors() ) ;
		assertEquals(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID_DATA_EXTENT,report.getErrors().get(0).getCode());
		assertEquals(ErrorLevel.ERROR,report.getErrors().get(0).getLevel());
	}
}