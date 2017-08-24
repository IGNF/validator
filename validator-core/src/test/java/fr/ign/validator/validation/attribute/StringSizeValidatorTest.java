package fr.ign.validator.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.validation.attribute.StringSizeValidator;
import junit.framework.TestCase;

public class StringSizeValidatorTest extends TestCase {

	private StringSizeValidator validator ;
	
	private Context context ;
	
	private InMemoryReportBuilder report ;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		validator = new StringSizeValidator();
		
		context = new Context();
		report = new InMemoryReportBuilder() ;
		context.setReportBuilder(report);
	}

	public void testNoLimit(){
		StringType type = new StringType();
		Attribute<String> attribute = new Attribute<String>(type, "abc");
		validator.validate(context, attribute);
		assertEquals(0, report.countErrors() ) ;
	}
	
	public void testLess(){
		StringType type = new StringType();
		type.setSize(5);
		Attribute<String> attribute = new Attribute<String>(type, "abc");
		validator.validate(context, attribute);
		assertEquals(0, report.countErrors() ) ;
	}
	
	public void testEqual(){
		StringType type = new StringType();
		type.setSize(5);
		Attribute<String> attribute = new Attribute<String>(type, "abcde");
		validator.validate(context, attribute);
		assertEquals(0, report.countErrors() ) ;
	}
	
	public void testMore(){
		StringType type = new StringType();
		type.setSize(5);
		Attribute<String> attribute = new Attribute<String>(type, "abcdef");
		validator.validate(context, attribute);
		assertEquals(1, report.countErrors() ) ;
		
		ValidatorError error = report.getErrors().get(0);
		assertEquals( "La taille de l'attribut (6) dépasse la taille limite autorisée (5).", error.getMessage() );
	}
	
}
