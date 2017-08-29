package fr.ign.validator.model.type;

import org.junit.Test;

public class DoubleTypeTest extends AbstractTypeTest<Double> {

	public DoubleTypeTest() {
		super(new DoubleType());
	}
	
	@Test
	public void testBindInteger(){
		assertEquals(-2.0,type.bind("-2").doubleValue());
		assertEquals(1.0,type.bind("1").doubleValue());
		assertEquals(5.0,type.bind("5").doubleValue());
		assertEquals(4751.0,type.bind("4751").doubleValue());
	}
	
	@Test
	public void testBindDecimals(){
		assertEquals(-1.123,type.bind("-1.123"));
		assertEquals(1.123,type.bind("1.123"));
		assertEquals(51.14558,type.bind("51.14558"));
		assertEquals(4751.78754,type.bind("4751.78754"));		
		assertEquals(4751.00000000000001,type.bind("4751.00000000000001"));		
	}
	
	public void testBindScientific(){
		assertEquals(-1545.45,type.bind("-1.54545e3").doubleValue());			
		assertEquals(1545.45,type.bind("1.54545e3").doubleValue());	
	}

	@Test
	public void testFormat(){
		assertEquals("-1.5",type.format(-1.5));
		assertEquals("1.5",type.format(1.5));
		assertEquals("1.57",type.format(1.57));
		assertEquals("4751.0000000001",type.format(4751.0000000001));
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
			type.bind(input).intValue();
		}catch(IllegalArgumentException e){
			thrown = true ;
		}
		assertTrue(
			"bind(\""+input+"\") should throw",
			thrown
		);
	}
	
}
