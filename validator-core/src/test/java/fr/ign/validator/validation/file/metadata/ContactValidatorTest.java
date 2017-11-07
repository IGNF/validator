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
import fr.ign.validator.metadata.ResponsibleParty;

@RunWith(MockitoJUnitRunner.class)
public class ContactValidatorTest extends MetadataValidatorTestBase {
	
	@Test
	public void testValid(){
		Metadata metadata = mock(Metadata.class);
		ResponsibleParty contact = new ResponsibleParty();
		when(metadata.getContact()).thenReturn(contact);
		
		ContactValidator validator = new ContactValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}
	
	@Test
	public void testEmpty(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getContact()).thenReturn(null);
		
		ContactValidator validator = new ContactValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(
			CoreErrorCodes.METADATA_CONTACT_NOT_FOUND,
			error.getCode()
		);
	}
	
}
