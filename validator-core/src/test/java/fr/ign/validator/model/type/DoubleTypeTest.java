package fr.ign.validator.model.type;

import org.junit.Test;

import junit.framework.TestCase;

public class DoubleTypeTest extends TestCase {

	private DoubleType attribute ;
	
	@Override
	protected void setUp() throws Exception {
		attribute = new DoubleType();
	}
	
	@Test
	public void testBindInteger(){
		assertEquals(-2.0,attribute.bind("-2").doubleValue());
		assertEquals(1.0,attribute.bind("1").doubleValue());
		assertEquals(5.0,attribute.bind("5").doubleValue());
		assertEquals(4751.0,attribute.bind("4751").doubleValue());
	}
	
	@Test
	public void testBindDecimals(){
		assertEquals(-1.123,attribute.bind("-1.123"));
		assertEquals(1.123,attribute.bind("1.123"));
		assertEquals(51.14558,attribute.bind("51.14558"));
		assertEquals(4751.78754,attribute.bind("4751.78754"));		
		assertEquals(4751.00000000000001,attribute.bind("4751.00000000000001"));		
	}
	
	public void testBindScientific(){
		assertEquals(-1545.45,attribute.bind("-1.54545e3").doubleValue());			
		assertEquals(1545.45,attribute.bind("1.54545e3").doubleValue());	
	}

	@Test
	public void testFormat(){
		assertEquals("-1.5",attribute.format(-1.5));
		assertEquals("1.5",attribute.format(1.5));
		assertEquals("1.57",attribute.format(1.57));
		assertEquals("4751.0000000001",attribute.format(4751.0000000001));
	}
	
	
	@Test
	public void testBindError(){
		assertBindThrow("t");
		assertBindThrow("false");
		assertBindThrow("a");
	}
	
	private void assertBindThrow(String input){
		boolean thrown = false ;
		try {
			attribute.bind(input).intValue();
		}catch(IllegalArgumentException e){
			thrown = true ;
		}
		assertTrue(
			"bind(\""+input+"\") should throw",
			thrown
		);
	}
	
}
