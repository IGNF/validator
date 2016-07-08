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
public class UrlTypeTest extends TestCase {
	private UrlType attribute;
	protected Context context ;
	protected InMemoryReportBuilder reportBuilder ;
	
	public UrlTypeTest() {
		attribute = new UrlType() ;
		attribute.setName("test");
		
		context = new Context() ;
		reportBuilder = new InMemoryReportBuilder() ; 
		context.setReportBuilder(reportBuilder);
	}
	
	@Test
	public void testCheckAttributeValue() {
//		Properties systemSettings = System.getProperties();
//		systemSettings.put("proxySet", "true");
//		systemSettings.put("http.proxyHost", "proxy.ign.fr");
//		systemSettings.put("http.proxyPort", "3128");
		attribute.bindValidate(context,"ce n'est pas une url") ;
		assertFalse( reportBuilder.isValid() );
//		assertTrue( property.validate("http://www.google.fr/") );
	}
	
}
