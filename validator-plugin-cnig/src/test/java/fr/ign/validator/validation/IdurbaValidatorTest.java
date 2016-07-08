package fr.ign.validator.validation;

import fr.ign.validator.Context;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.model.Attribute;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;
import junit.framework.TestCase;

public class IdurbaValidatorTest extends TestCase {

	protected Context context ;
	private IdurbaValidator validator ;
	protected InMemoryReportBuilder report ;
	
	@Override
	protected void setUp() {
		context = new Context();
		report = new InMemoryReportBuilder();
		context.setReportBuilder(report);
		validator = new IdurbaValidator();
	}
	
	public void testNotValid(){
		StringType type = new StringType();
		Attribute<String> attribute = type.newAttribute("test");
		validator.validate(context, attribute);
		assertEquals(1,report.countErrors(ErrorLevel.WARNING));
	}
	
	public void testValid(){
		StringType type = new StringType();
		Attribute<String> attribute = type.newAttribute("25349_20140101");
		validator.validate(context, attribute);
		assertEquals(0,report.countErrors());
	}
	
}
