package fr.ign.validator.model.type;

import org.junit.Test;

public class IntegerTypeTest extends AbstractTypeTest<Integer> {

	public IntegerTypeTest() {
		super(new IntegerType());
	}

	@Test
	public void testBindRealInteger(){
		assertEquals(1,type.bind("1").intValue());
		assertEquals(5,type.bind("5").intValue());
		assertEquals(4751,type.bind("4751").intValue());		
	}
	
	@Test
	public void testBindRoundableDouble(){
		assertEquals(1,type.bind("1.00000").intValue());
		assertEquals(50,type.bind("50.00000").intValue());
		assertEquals(4751,type.bind("4751.00000").intValue());		
		assertEquals(4751,type.bind("4751.00000000000001").intValue());		
	}
	
	@Test
	public void testBindNotRoundableDouble(){
		assertBindThrow("50.1");
		assertBindThrow("1.00001");		
		assertBindThrow("4751.000001");
	}
	
	
	@Test
	public void testBindError(){
		assertBindThrow("t");
		assertBindThrow("false");
	}
	
	private void assertBindThrow(String input){
		boolean thrown = false ;
		try {
			type.bind(input).intValue();
		}catch(IllegalArgumentException e){
			thrown = true ;
		}
		assertTrue(thrown);
	}
	
}
