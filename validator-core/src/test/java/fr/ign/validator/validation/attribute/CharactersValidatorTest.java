package fr.ign.validator.validation.attribute;

import java.io.File;
import java.nio.charset.StandardCharsets;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.string.StringFixer;
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
		context.setStringFixer(StringFixer.createFullStringFixer(StandardCharsets.ISO_8859_1));
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
	
	public void testSimplifyCharacter(){
		StringType type = new StringType();
		Attribute<String> attribute = type.newAttribute("a non latin character : œ");
		validator.validate(context, attribute);
		assertEquals(1, report.countErrors() ) ;
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.ATTRIBUTE_CHARACTERS_REPLACED, error.getCode());
		assertEquals(
			"La valeur (‘a non latin character : œ’) sera remplacée par (‘a non latin character : oe’) pour l’intégration des données.", 
			error.getMessage()
		);
	}

	public void testNonLatin1Characters(){
		StringType type = new StringType();
		Attribute<String> attribute = type.newAttribute("a non latin character : ᆦ");
		validator.validate(context, attribute);
		assertEquals(1, report.countErrors() ) ;
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.ATTRIBUTE_CHARACTERS_ILLEGAL, error.getCode());
		assertEquals(
			"La valeur (‘a non latin character : ᆦ’) contient des caractères interdits qui seront échappés (‘a non latin character : \\u11a6’).",
			error.getMessage()
		);
	}
	
	public void testForbiddenControl(){
		StringType type = new StringType();
		Attribute<String> attribute = type.newAttribute("a forbidden control : \u0001");
		validator.validate(context, attribute);
		assertEquals(1, report.countErrors() ) ;
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.ATTRIBUTE_CHARACTERS_ILLEGAL, error.getCode());
	}
}
