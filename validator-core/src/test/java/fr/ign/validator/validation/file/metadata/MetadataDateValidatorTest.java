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
public class MetadataDateValidatorTest extends MetadataValidatorTestBase {
	
	@Test
	public void testValid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getMetadataDate()).thenReturn(new Date("2001-01-01"));
		
		MetadataDateValidator validator = new MetadataDateValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}

	@Test
	public void testNotFound(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getMetadataDate()).thenReturn(null);
		
		MetadataDateValidator validator = new MetadataDateValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_METADATADATE_NOT_FOUND, error.getCode());	
	}
	
	@Test
	public void testInvalid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getMetadataDate()).thenReturn(new Date("not valid"));
		
		MetadataDateValidator validator = new MetadataDateValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_METADATADATE_INVALID, error.getCode());	
	}


}
