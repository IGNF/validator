package fr.ign.validator.validation.attribute;

import java.io.File;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;
import junit.framework.TestCase;

public class CharactersValidatorTest extends TestCase {

	private Context context ;
	private InMemoryReportBuilder report ;
	private CharactersValidator<String> validator ;
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		validator = new CharactersValidator<String>();
		
		context = new Context();
		File currentDirectory = new File(getClass().getResource("/geofla").getPath()) ;
		context.setCurrentDirectory(currentDirectory);
		
		report = new InMemoryReportBuilder() ;
		context.setReportBuilder(report);
	}

	
	public void testAllowedCharacters(){
		StringType type = new StringType();
		Attribute<String> attribute = type.newAttribute("une chaîne accentuée avec des contrôles autorisés : \r\n autre ligne");
		validator.validate(context, attribute);
		assertEquals(0, report.countErrors() ) ;
	}
	
	public void testNonLatin1Characters(){
		StringType type = new StringType();
		Attribute<String> attribute = type.newAttribute("a non latin character : ᆦ");
		validator.validate(context, attribute);
		assertEquals(1, report.countErrors() ) ;
	}
	
	public void testForbiddenControl(){
		StringType type = new StringType();
		Attribute<String> attribute = type.newAttribute("a borbidden control : \u0001");
		validator.validate(context, attribute);
		assertEquals(1, report.countErrors() ) ;
	}
}
