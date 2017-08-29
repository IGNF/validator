package fr.ign.validator.model.type;

import org.junit.Test;

/**
 * 
 * @author CBouche
 *
 */
public class BooleanTypeTest extends AbstractTypeTest<Boolean> {

	public BooleanTypeTest(){
		super(new BooleanType());
	}

	@Test
	public void testCheckAttributeValue0() {
		bindValidate(context,false) ;
		assertTrue( reportBuilder.isValid() ) ;
	}

	@Test
	public void testCheckAttributeValue1() {
		bindValidate(context,new Boolean(false)) ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue2() {
		bindValidate(context, "f") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue3() {
		bindValidate(context, "F") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue4() {
		bindValidate(context, "n") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue5() {
		bindValidate(context,"N") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue6() {
		bindValidate(context, "0") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue7() {
		bindValidate(context, true)  ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue8() {
		bindValidate(context, new Boolean(true));
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue9() {
		bindValidate(context,"t") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue10() {
		bindValidate(context, "y") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue11() {
		bindValidate(context, "Y") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue12() {
		assertTrue( bindValidate(context, "1") );
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue13() {
		assertNull( bindValidate(context, "ce n'est pas un boolean") ) ;
		assertFalse( reportBuilder.isValid() ) ;
	}
	
}
