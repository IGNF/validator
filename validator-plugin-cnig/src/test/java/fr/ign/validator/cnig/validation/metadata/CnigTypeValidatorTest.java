package fr.ign.validator.cnig.validation.metadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.validation.CnigValidatorTestBase;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.code.ScopeCode;

public class CnigTypeValidatorTest extends CnigValidatorTestBase {

	/*
	 * check valid for "dataset"
	 */
	@Test
	public void testValidDataset(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getType()).thenReturn(ScopeCode.valueOf("dataset"));
		
		CnigTypeValidator validator = new CnigTypeValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}

	/*
	 * check valid for "serie"
	 */
	@Test
	public void testValidSerie(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getType()).thenReturn(ScopeCode.valueOf("serie"));
		
		CnigTypeValidator validator = new CnigTypeValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}

	/*
	 * check not valid for serie
	 */
	@Test
	public void testInvalid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getType()).thenReturn(ScopeCode.valueOf("feature"));
		
		CnigTypeValidator validator = new CnigTypeValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		assertEquals(
			CnigErrorCodes.CNIG_METADATA_TYPE_INVALID, 
			report.getErrors().get(0).getCode()
		);
	}

	@Test
	public void testEmptyNoErrorDuplication(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getType()).thenReturn(null);
		
		CnigTypeValidator validator = new CnigTypeValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}

	
}
