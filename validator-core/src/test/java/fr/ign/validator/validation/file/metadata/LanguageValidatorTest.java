package fr.ign.validator.validation.file.metadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.code.LanguageCode;

@RunWith(MockitoJUnitRunner.class)
public class LanguageValidatorTest extends MetadataValidatorTestBase {

	@Test
	public void testValid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getLanguage()).thenReturn(LanguageCode.valueOf("fre"));
		
		LanguageValidator validator = new LanguageValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}
	
	@Test
	public void testNotFound(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getLanguage()).thenReturn(null);
		
		LanguageValidator validator = new LanguageValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_LANGUAGE_NOT_FOUND, error.getCode());
	}
	
	@Test
	public void testInvalid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getLanguage()).thenReturn(LanguageCode.valueOf("invalid"));
		
		LanguageValidator validator = new LanguageValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_LANGUAGE_INVALID, error.getCode());
		assertEquals(
			"Le champ \"langue de la ressource\" (invalid) ne correspond pas à une valeur autorisée (bul, cze, dan, dut, eng, est, fin, fre, ger, gle, gre, hun, ita, lav, lit, mlt, pol, por, rum, slo, slv, spa, swe).",
			error.getMessage()
		);
	}

}
