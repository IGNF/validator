package fr.ign.validator.model.type;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.report.InMemoryReportBuilder;
import junit.framework.TestCase;

/**
 * 
 * @author CBouche
 *
 */
public class DateTypeTest extends TestCase {
	private DateType attribute;
	protected Context context ;
	protected InMemoryReportBuilder reportBuilder ;
	
	public DateTypeTest() {
		attribute = new DateType() ;
		attribute.setName("test");
		
		context = new Context() ;
		reportBuilder = new InMemoryReportBuilder() ; 
		context.setReportBuilder(reportBuilder);
	}

	
	@Test
	public void testCheckAttributeValue0() throws ParseException {		
		attribute.bindValidate(context, "15/12/1990") ;
		assertTrue(reportBuilder.isValid()) ;
	}
	
	@Test
	public void testCheckAttributeValue1() throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" ) ;
		Date date = simpleDateFormat.parse( "15/12/1990" ) ;
		attribute.bindValidate(context, date) ;
		assertTrue(reportBuilder.isValid()) ;
	}
	
	@Test
	public void testCheckAttributeValue2() throws ParseException {
		attribute.bindValidate(context, "ce n'est pas une date") ;
		assertFalse(reportBuilder.isValid()) ;
	}
	
	@Test
	public void testFormat() throws ParseException{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" ) ;
		Date date = simpleDateFormat.parse( "15/12/1990" ) ;
		assertEquals( "19901215", attribute.format(date)) ;
	}
}
