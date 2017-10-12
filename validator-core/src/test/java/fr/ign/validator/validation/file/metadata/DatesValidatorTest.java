package fr.ign.validator.validation.file.metadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.metadata.Date;
import fr.ign.validator.metadata.Metadata;

@RunWith(MockitoJUnitRunner.class)
public class DatesValidatorTest extends MetadataValidatorTestBase {
	
	@Test
	public void testValid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getDateOfLastRevision()).thenReturn(new Date("2001-01-01"));
		
		DatesValidator validator = new DatesValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}


	@Test
	public void testDateNotFound(){
		Metadata metadata = mock(Metadata.class);

		DatesValidator validator = new DatesValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_DATES_NOT_FOUND, error.getCode());	
	}
	
	@Test
	public void testDateOfPublicationInvalid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getDateOfPublication()).thenReturn(new Date("invalid"));

		DatesValidator validator = new DatesValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_DATEOFPUBLICATION_INVALID, error.getCode());	
	}
	
	@Test
	public void testDateOfLastRevisionInvalid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getDateOfLastRevision()).thenReturn(new Date("invalid"));

		DatesValidator validator = new DatesValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_DATEOFLASTREVISION_INVALID, error.getCode());	
	}
	
	@Test
	public void testDateOfCreationInvalid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getDateOfCreation()).thenReturn(new Date("invalid"));

		DatesValidator validator = new DatesValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_DATEOFCREATION_INVALID, error.getCode());	
	}

}
