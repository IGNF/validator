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
public class LineageValidatorTest extends MetadataValidatorTestBase {
	
	@Test
	public void testValid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getLineage()).thenReturn("test lineage");
		
		LineageValidator validator = new LineageValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}
	
	@Test
	public void testNotValid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getLineage()).thenReturn(null);

		LineageValidator validator = new LineageValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_LINEAGE_NOT_FOUND, error.getCode());	
	}

}
