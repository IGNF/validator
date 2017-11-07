package fr.ign.validator.cnig.validation.metadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.validation.CnigValidatorTestBase;
import fr.ign.validator.metadata.Date;
import fr.ign.validator.metadata.Metadata;

public class CnigMetadataDateOfLastRevisionValidatorTest extends CnigValidatorTestBase {

	@Test
	public void testValid(){
		Metadata metadata = mock(Metadata.class);
		Date date = new Date("2001-01-01");
		when(metadata.getDateOfLastRevision()).thenReturn(date);
		
		CnigMetadataDateOfLastRevisionValidator validator = new CnigMetadataDateOfLastRevisionValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}
	
	@Test
	public void testNotValid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getDateOfLastRevision()).thenReturn(null);
		
		CnigMetadataDateOfLastRevisionValidator validator = new CnigMetadataDateOfLastRevisionValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		assertEquals(
			CnigErrorCodes.CNIG_METADATA_DATEOFLASTREVISION_NOT_FOUND, 
			report.getErrors().get(0).getCode()
		);
	}
	
}
