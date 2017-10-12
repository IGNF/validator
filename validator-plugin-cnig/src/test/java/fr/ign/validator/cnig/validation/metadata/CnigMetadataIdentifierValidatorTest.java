package fr.ign.validator.cnig.validation.metadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Test;

import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.validation.CnigValidatorTestBase;
import fr.ign.validator.metadata.Metadata;

public class CnigMetadataIdentifierValidatorTest extends CnigValidatorTestBase {

	/*
	 * check validate document name
	 */
	@Test
	public void testValid(){
		Metadata metadata = mock(Metadata.class);
		String value = "https://www.geoportail-urbanisme.gouv.fr/document/25349_PLU_20010101";
		when(metadata.getIdentifier()).thenReturn(value);
		
		context.setCurrentDirectory(new File("/path/to/25349_PLU_20010101"));
		
		CnigMetadataIdentifierValidator validator = new CnigMetadataIdentifierValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}

	/*
	 * check that core error is not duplicated 
	 */
	@Test
	public void testEmptyIdentifier(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getIdentifier()).thenReturn(null);
		
		context.setCurrentDirectory(new File("/path/to/25349_PLU_20010101"));
		
		CnigMetadataIdentifierValidator validator = new CnigMetadataIdentifierValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}
	
	/*
	 * check that bad document name are reported
	 */
	@Test
	public void testBadName(){
		Metadata metadata = mock(Metadata.class);
		String value = "25349_PLU_20010101";
		when(metadata.getIdentifier()).thenReturn(value);
		
		context.setCurrentDirectory(new File("/path/to/bad_name"));
		
		CnigMetadataIdentifierValidator validator = new CnigMetadataIdentifierValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		assertEquals(
			CnigErrorCodes.CNIG_METADATA_IDENTIFIER_INVALID, 
			report.getErrors().get(0).getCode()
		);
		assertEquals(
			"Le champ \"identificateur de ressource unique\" (25349_PLU_20010101) n'est pas égal à \"https://www.geoportail-urbanisme.gouv.fr/document/bad_name\"", 
			report.getErrors().get(0).getMessage()
		);
	}
	
}
