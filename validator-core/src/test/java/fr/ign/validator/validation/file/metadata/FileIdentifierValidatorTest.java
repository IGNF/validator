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

@RunWith(MockitoJUnitRunner.class)
public class FileIdentifierValidatorTest extends MetadataValidatorTestBase {

	@Test
	public void testValid1() {
		Metadata metadata = mock(Metadata.class);
		when(metadata.getFileIdentifier()).thenReturn("test-identifier");

		FileIdentifierValidator validator = new FileIdentifierValidator();
		validator.validate(context, metadata);

		assertEquals(0, report.getErrors().size());
	}
	
	@Test
	public void testValid2() {
		Metadata metadata = mock(Metadata.class);
		when(metadata.getFileIdentifier()).thenReturn("test:identifier");

		FileIdentifierValidator validator = new FileIdentifierValidator();
		validator.validate(context, metadata);

		assertEquals(0, report.getErrors().size());
	}

	@Test
	public void testNotFound() {
		Metadata metadata = mock(Metadata.class);
		when(metadata.getFileIdentifier()).thenReturn(null);

		FileIdentifierValidator validator = new FileIdentifierValidator();
		validator.validate(context, metadata);

		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_FILEIDENTIFIER_NOT_FOUND, error.getCode());
		assertEquals("Le champ \"fileIdentifier\" n'est pas renseigné.", error.getMessage());
	}

	@Test
	public void testEmpty() {
		Metadata metadata = mock(Metadata.class);
		when(metadata.getFileIdentifier()).thenReturn("");

		FileIdentifierValidator validator = new FileIdentifierValidator();
		validator.validate(context, metadata);

		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_FILEIDENTIFIER_NOT_FOUND, error.getCode());
		assertEquals("Le champ \"fileIdentifier\" n'est pas renseigné.", error.getMessage());
	}

	@Test
	public void testInvalidSpaces() {
		Metadata metadata = mock(Metadata.class);
		when(metadata.getFileIdentifier()).thenReturn("my fileidentifier");

		FileIdentifierValidator validator = new FileIdentifierValidator();
		validator.validate(context, metadata);

		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_FILEIDENTIFIER_INVALID, error.getCode());
		assertEquals(
			"Le champ \"fileIdentifier\" (my fileidentifier) ne correspond pas à l'expression régulière ([\\w-.:]+).",
			error.getMessage()
		);
	}


	@Test
	public void testInvalidSpecialChars() {
		Metadata metadata = mock(Metadata.class);
		when(metadata.getFileIdentifier()).thenReturn("special!");

		FileIdentifierValidator validator = new FileIdentifierValidator();
		validator.validate(context, metadata);

		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_FILEIDENTIFIER_INVALID, error.getCode());
		assertEquals(
			"Le champ \"fileIdentifier\" (special!) ne correspond pas à l'expression régulière ([\\w-.:]+).",
			error.getMessage()
		);
	}
	
}
