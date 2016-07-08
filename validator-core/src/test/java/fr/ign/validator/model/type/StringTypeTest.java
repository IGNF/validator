package fr.ign.validator.model.type;

import java.util.ArrayList;

import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.report.InMemoryReportBuilder;
import junit.framework.TestCase;

/**
 * 
 * @author CBouche
 *
 */
public class StringTypeTest extends TestCase {

	private StringType attribute;
	protected Context context ;
	protected InMemoryReportBuilder reportBuilder ;

	@Override
	public void setUp() throws Exception {
		attribute = new StringType() ;
		attribute.setName("test");
		attribute.setListOfValues(new ArrayList<String>()) ;
		attribute.getListOfValues().add("aa") ;
		attribute.getListOfValues().add("b") ;
		attribute.getListOfValues().add("cccCCC") ;
		attribute.getListOfValues().add("96541") ;
		attribute.getListOfValues().add("9Cou") ;
		attribute.setRegexp("(aa|b|bb|cccCCC|[0-9]{5})") ;
		
		context = new Context() ;
		reportBuilder = new InMemoryReportBuilder() ; 
		context.setReportBuilder(reportBuilder);
	}

	@Override
	public void tearDown() throws Exception {
		attribute = null ;
	}
	
	
	@Test
	public void testCheckAttributeValue0() {
		// all is ok
		attribute.bindValidate(context,"b") ;
		assertTrue(reportBuilder.isValid());
	}
	
	@Test
	public void testCheckAttributeValue1() {
		// all is ok
		attribute.bindValidate(context,"aa") ;
		assertTrue(reportBuilder.isValid());
	}
	
	@Test
	public void testCheckAttributeValue2() {
		// trop long 
		attribute.bindValidate(context,"cccCCC") ;
		assertTrue(reportBuilder.isValid());
	}
	
	@Test
	public void testCheckAttributeValue3() {
		// n'appartient pas a la liste de valeur
		attribute.bindValidate(context,"bb") ;
		assertFalse(reportBuilder.isValid());
	}
	
	@Test
	public void testCheckAttributeValue4() {
		// all is ok
		attribute.bindValidate(context,"96541") ;
		assertTrue(reportBuilder.isValid());
	}
	
	@Test
	public void testCheckAttributeValue5() {
		// ne respecte pas la regexp
		attribute.bindValidate(context,"9Cou") ;
		assertFalse(reportBuilder.isValid());
	}

}
