package fr.ign.validator.model.type;

import org.junit.Test;
import static org.junit.Assert.*;


public class DoubleTypeTest extends AbstractTypeTest<Double> {

	private static final double EPSILON = 1.0e-30;
	
	public DoubleTypeTest() {
		super(new DoubleType());
	}
	
	@Test
	public void testBindInteger(){
		assertEquals(-2.0,type.bind("-2").doubleValue(),EPSILON);
		assertEquals(1.0,type.bind("1").doubleValue(),EPSILON);
		assertEquals(5.0,type.bind("5").doubleValue(),EPSILON);
		assertEquals(4751.0,type.bind("4751").doubleValue(),EPSILON);
	}

	@Test
	public void testBindDecimals(){
		assertEquals(-1.123,type.bind("-1.123").doubleValue(),EPSILON);
		assertEquals(1.123,type.bind("1.123").doubleValue(),EPSILON);
		assertEquals(51.14558,type.bind("51.14558").doubleValue(),EPSILON);
		assertEquals(4751.78754,type.bind("4751.78754").doubleValue(),EPSILON);		
		assertEquals(4751.00000000000001,type.bind("4751.00000000000001").doubleValue(),EPSILON);		
	}

	@Test
	public void testBindScientific(){
		assertEquals(-1545.45,type.bind("-1.54545e3").doubleValue(),EPSILON);			
		assertEquals(1545.45,type.bind("1.54545e3").doubleValue(),EPSILON);	
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
