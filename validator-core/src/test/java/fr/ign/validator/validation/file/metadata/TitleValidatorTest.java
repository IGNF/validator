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
public class TitleValidatorTest extends MetadataValidatorTestBase {

	@Test
	public void testValid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getTitle()).thenReturn("test title");
		
		TitleValidator validator = new TitleValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}
	
	@Test
	public void testNotValid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getTitle()).thenReturn(null);
		
		TitleValidator validator = new TitleValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_TITLE_NOT_FOUND, error.getCode());
	}

}
