package fr.ign.validator.model.type;

import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.report.InMemoryReportBuilder;
import junit.framework.TestCase;

/**
 * 
 * @author CBouche
 *
 */
public class BooleanTypeTest extends TestCase {
	
	private BooleanType attribute;
	protected Context context ;
	protected InMemoryReportBuilder reportBuilder ;
	
	public BooleanTypeTest(){
		attribute = new BooleanType() ;
		attribute.setName("test");
		
		context = new Context() ;
		reportBuilder = new InMemoryReportBuilder() ; 
		context.setReportBuilder(reportBuilder);
	}
	
	
	@Test
	public void testCheckAttributeValue0() {
		Boolean v = attribute.bind(false);
		attribute.validate(context,v);
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue1() {
		attribute.bindValidate(context,new Boolean(false)) ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue2() {
		attribute.bindValidate(context, "f") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue3() {
		attribute.bindValidate(context, "F") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue4() {
		attribute.bindValidate(context, "n") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue5() {
		attribute.bindValidate(context,"N") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue6() {
		attribute.bindValidate(context, "0") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue7() {
		attribute.bindValidate(context, true)  ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue8() {
		attribute.bindValidate(context, new Boolean(true));
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue9() {
		attribute.bindValidate(context,"t") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue10() {
		attribute.bindValidate(context, "y") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue11() {
		attribute.bindValidate(context, "Y") ;
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue12() {
		assertTrue(attribute.bindValidate(context, "1"));
		assertTrue( reportBuilder.isValid() ) ;
	}
	
	
	@Test
	public void testCheckAttributeValue13() {
		assertNull( attribute.bindValidate(context, "ce n'est pas un boolean") ) ;
		assertFalse( reportBuilder.isValid() ) ;
	}
	
}
