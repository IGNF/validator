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
import fr.ign.validator.metadata.code.CharacterSetCode;

@RunWith(MockitoJUnitRunner.class)
public class CharacterSetValidatorTest extends MetadataValidatorTestBase {

	@Test
	public void testValid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getCharacterSet()).thenReturn(CharacterSetCode.valueOf("8859part1"));
		
		CharacterSetValidator validator = new CharacterSetValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}
	
	@Test
	public void testNotFound(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getCharacterSet()).thenReturn(null);
		
		CharacterSetValidator validator = new CharacterSetValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_CHARACTERSET_NOT_FOUND, error.getCode());
	}
	
	@Test
	public void testInvalid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getCharacterSet()).thenReturn(CharacterSetCode.valueOf("invalid"));
		
		CharacterSetValidator validator = new CharacterSetValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_CHARACTERSET_INVALID, error.getCode());
	}

}
