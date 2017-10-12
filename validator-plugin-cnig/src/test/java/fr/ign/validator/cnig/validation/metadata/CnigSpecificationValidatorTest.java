package fr.ign.validator.cnig.validation.metadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.validation.CnigValidatorTestBase;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.Specification;

public class CnigSpecificationValidatorTest extends CnigValidatorTestBase {

	/*
	 * check validate specification found
	 */
	@Test
	public void testValid(){
		Metadata metadata = mock(Metadata.class);
		List<Specification> specifications = new ArrayList<>();
		{
			Specification specification = new Specification();
			specification.setDateType("publication");
			specification.setTitle("noise (ignored)");
			specifications.add(specification);
		}
		{
			Specification specification = new Specification();
			specification.setDateType("publication");
			specification.setTitle("CNIG PLU v2013");
			specifications.add(specification);
		}
		when(metadata.getSpecifications()).thenReturn(specifications);
		
		context.setCurrentDirectory(new File("/path/to/25349_PLU_20150101"));
		
		CnigSpecificationsValidator validator = new CnigSpecificationsValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}
	
	/**
	 * Test that not found specification are reported
	 */
	@Test
	public void testNotFound(){
		Metadata metadata = mock(Metadata.class);
		List<Specification> specifications = new ArrayList<>();
		{
			Specification specification = new Specification();
			specification.setDateType("publication");
			specification.setTitle("noise (invalid)");
			specifications.add(specification);
		}
		when(metadata.getSpecifications()).thenReturn(specifications);
		
		context.setCurrentDirectory(new File("/path/to/25349_PLU_20150101"));
		
		CnigSpecificationsValidator validator = new CnigSpecificationsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		assertEquals(
			CnigErrorCodes.CNIG_METADATA_SPECIFICATION_NOT_FOUND, 
			report.getErrors().get(0).getCode()
		);
	}
	
}
