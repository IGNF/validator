package fr.ign.validator.model.type;

import java.util.ArrayList;

import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.report.InMemoryReportBuilder;

/**
 * 
 * @author CBouche
 *
 */
public class StringTypeTest extends AbstractTypeTest<String> {

	public StringTypeTest(){
		super(new StringType());
	}
	

	@Override
	public void setUp() throws Exception {
		type = new StringType() ;
		type.setName("test");
		type.setListOfValues(new ArrayList<String>()) ;
		type.getListOfValues().add("aa") ;
		type.getListOfValues().add("b") ;
		type.getListOfValues().add("cccCCC") ;
		type.getListOfValues().add("96541") ;
		type.getListOfValues().add("9Cou") ;
		type.setRegexp("(aa|b|bb|cccCCC|[0-9]{5})") ;
		
		context = new Context() ;
		reportBuilder = new InMemoryReportBuilder() ; 
		context.setReportBuilder(reportBuilder);
	}

	@Override
	public void tearDown() throws Exception {
		type = null ;
	}
	
	
	@Test
	public void testCheckAttributeValue0() {
		// all is ok
		bindValidate(context,"b") ;
		assertTrue(reportBuilder.isValid());
	}
	
	@Test
	public void testCheckAttributeValue1() {
		// all is ok
		bindValidate(context,"aa") ;
		assertTrue(reportBuilder.isValid());
	}
	
	@Test
	public void testCheckAttributeValue2() {
		// too long 
		bindValidate(context,"cccCCC") ;
		assertTrue(reportBuilder.isValid());
	}
	
	@Test
	public void testCheckAttributeValue3() {
		// doesn't belong to list of values
		bindValidate(context,"bb") ;
		assertFalse(reportBuilder.isValid());
	}
	
	@Test
	public void testCheckAttributeValue4() {
		// all is ok
		bindValidate(context,"96541") ;
		assertTrue(reportBuilder.isValid());
	}
	
	@Test
	public void testCheckAttributeValue5() {
		// doesn't match regexp
		bindValidate(context,"9Cou") ;
		assertFalse(reportBuilder.isValid());
	}

}
