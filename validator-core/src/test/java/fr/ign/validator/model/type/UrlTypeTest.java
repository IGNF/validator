package fr.ign.validator.model.type;

import java.net.URL;

import org.junit.Test;


/**
 * 
 * @author CBouche
 *
 */
public class UrlTypeTest extends AbstractTypeTest<URL> {

	public UrlTypeTest() {
		super(new UrlType());
	}
	
	@Test
	public void testCheckAttributeValue() {
		bindValidate(context,"ce n'est pas une url") ;
		assertFalse( reportBuilder.isValid() );
	}
	
}
