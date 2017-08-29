package fr.ign.validator.model.type;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * 
 * @author CBouche
 *
 */
public class DateTypeTest extends AbstractTypeTest<Date> {
	
	public DateTypeTest() {
		super( new DateType() ) ;
	}

	
	@Test
	public void testCheckAttributeValue0() {		
		bindValidate(context, "15/12/1990") ;
		assertTrue(reportBuilder.isValid()) ;
	}
	
	@Test
	public void testCheckAttributeValue1() throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" ) ;
		Date date = simpleDateFormat.parse( "15/12/1990" ) ;
		bindValidate(context, date) ;
		assertTrue(reportBuilder.isValid()) ;
	}
	
	@Test
	public void testCheckAttributeValue2() {
		bindValidate(context, "ce n'est pas une date") ;
		assertFalse(reportBuilder.isValid()) ;
	}
	
	
	@Test
	public void testBindRegress() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" ) ;
		
		// input => expected dd/MM/yyyy (null for exception)
		Map<String,String> inputExpectedMap = new HashMap<String, String>();
		
		// illegal
		inputExpectedMap.put("not a date", null); 
		inputExpectedMap.put("20", null) ;
	

		// FORMAT_A = "yyyyMMdd";
		inputExpectedMap.put("20150712", "12/07/2015") ;		
		inputExpectedMap.put("20151717", null) ; // illegal month
		
		// FORMAT_B = "dd/MM/yyyy";
		inputExpectedMap.put("05/05/2015", "05/05/2015") ;
		inputExpectedMap.put("17/17/2015", null) ; // illegal month
		
		// FORMAT_C = "yyyy-MM-dd";
		inputExpectedMap.put("2015-12-25", "25/12/2015") ;
		inputExpectedMap.put("2015-17-25", null) ; // illegal month
		
		// FORMAT_D yyyy/MM/dd
		inputExpectedMap.put("2015/12/25", "25/12/2015") ;
		inputExpectedMap.put("2015/17/25", null) ; // illegal month

		// FORMAT_E = "yyyy" (format removed to avoid year 20151717)
		inputExpectedMap.put("2015", "01/01/2015") ;
		

		for (String input : inputExpectedMap.keySet()) {
			String expected = inputExpectedMap.get(input);
			
			String result = null;
			try {
				Date date = type.bind(input);
				result = simpleDateFormat.format(date);
			}catch (IllegalArgumentException e){
				//ignore
			}
			assertEquals(expected, result);
		}
	}

	
	
	@Test
	public void testFormat() throws ParseException{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" ) ;
		Date date = simpleDateFormat.parse( "15/12/1990" ) ;
		assertEquals( "19901215", type.format(date)) ;
	}
}
