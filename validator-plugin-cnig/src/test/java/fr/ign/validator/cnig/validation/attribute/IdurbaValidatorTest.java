package fr.ign.validator.cnig.validation.attribute;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.ign.validator.cnig.validation.CnigValidatorTestBase;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.model.type.StringType;

public class IdurbaValidatorTest extends CnigValidatorTestBase {

	@Test
	public void testNotValid(){
		StringType type = new StringType();
		Attribute<String> attribute = new Attribute<String>(type,"test");
		IdurbaValidator validator = new IdurbaValidator();
		validator.validate(context, attribute);
		assertEquals(1,report.countErrors(ErrorLevel.WARNING));
	}

	@Test
	public void testValid(){
		StringType type = new StringType();
		Attribute<String> attribute = new Attribute<String>(type,"25349_20140101");
		IdurbaValidator validator = new IdurbaValidator();
		validator.validate(context, attribute);
		assertEquals(0,report.countErrors());
	}
	
}
