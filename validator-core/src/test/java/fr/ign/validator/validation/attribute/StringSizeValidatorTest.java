package fr.ign.validator.validation.attribute;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class StringSizeValidatorTest {

	private StringSizeValidator validator ;
	
	private Context context ;
	
	private InMemoryReportBuilder report ;
	
	@Before
	public void setUp() throws Exception {
		validator = new StringSizeValidator();
		
		context = new Context();
		report = new InMemoryReportBuilder() ;
		context.setReportBuilder(report);
	}

	@Test
	public void testNoLimit(){
		StringType type = new StringType();
		Attribute<String> attribute = new Attribute<String>(type, "abc");
		validator.validate(context, attribute);
		Assert.assertEquals(0, report.countErrors() ) ;
	}

	@Test
	public void testLess(){
		StringType type = new StringType();
		type.setSize(5);
		Attribute<String> attribute = new Attribute<String>(type, "abc");
		validator.validate(context, attribute);
		Assert.assertEquals(0, report.countErrors() ) ;
	}

	@Test
	public void testEqual(){
		StringType type = new StringType();
		type.setSize(5);
		Attribute<String> attribute = new Attribute<String>(type, "abcde");
		validator.validate(context, attribute);
		Assert.assertEquals(0, report.countErrors() ) ;
	}

	@Test
	public void testMore(){
		StringType type = new StringType();
		type.setSize(5);
		Attribute<String> attribute = new Attribute<String>(type, "abcdef");
		validator.validate(context, attribute);
		Assert.assertEquals(1, report.countErrors() ) ;
		
		ValidatorError error = report.getErrors().get(0);
		Assert.assertEquals( "La taille de l'attribut (6) dépasse la taille limite autorisée (5).", error.getMessage() );
	}
	
}
