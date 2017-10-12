package fr.ign.validator.validation.file.metadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.metadata.Date;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.Specification;

@RunWith(MockitoJUnitRunner.class)
public class SpecificationsValidatorTest extends MetadataValidatorTestBase {

	@Test
	public void testValid(){
		List<Specification> specifications = new ArrayList<>();
		{
			Specification specification = new Specification();
			specification.setTitle("test");
			specification.setDate(new Date("2017-01-01"));
			specifications.add(specification);
		}
		Metadata metadata = mock(Metadata.class);
		when(metadata.getSpecifications()).thenReturn(specifications);

		SpecificationsValidator validator = new SpecificationsValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}
	
	@Test
	public void testEmpty(){
		List<Specification> specifications = new ArrayList<>();
		
		Metadata metadata = mock(Metadata.class);
		when(metadata.getSpecifications()).thenReturn(specifications);

		SpecificationsValidator validator = new SpecificationsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		assertEquals(
			CoreErrorCodes.METADATA_SPECIFICATIONS_EMPTY,
			report.getErrors().get(0).getCode() 
		);
	}
	
	@Test
	public void testTitleNotFound(){
		List<Specification> specifications = new ArrayList<>();
		{
			Specification specification = new Specification();
			//specification.setTitle("test");
			specification.setDate(new Date("2017-01-01"));
			specifications.add(specification);
		}
		Metadata metadata = mock(Metadata.class);
		when(metadata.getSpecifications()).thenReturn(specifications);

		SpecificationsValidator validator = new SpecificationsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		assertEquals(
			CoreErrorCodes.METADATA_SPECIFICATION_TITLE_NOT_FOUND,
			report.getErrors().get(0).getCode() 
		);
	}
	
	@Test
	public void testDateNotFound(){
		List<Specification> specifications = new ArrayList<>();
		{
			Specification specification = new Specification();
			specification.setTitle("test");
			//specification.setDate(new Date("2017-01-01"));
			specifications.add(specification);
		}
		Metadata metadata = mock(Metadata.class);
		when(metadata.getSpecifications()).thenReturn(specifications);

		SpecificationsValidator validator = new SpecificationsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		assertEquals(
			CoreErrorCodes.METADATA_SPECIFICATION_DATE_NOT_FOUND,
			report.getErrors().get(0).getCode() 
		);
	}


	@Test
	public void testDegreeInvalid(){
		List<Specification> specifications = new ArrayList<>();
		{
			Specification specification = new Specification();
			specification.setTitle("test");
			specification.setDate(new Date("2017-01-01"));
			specification.setDegree("conforme"); // (geonetwork UI)
			specifications.add(specification);
		}
		Metadata metadata = mock(Metadata.class);
		when(metadata.getSpecifications()).thenReturn(specifications);

		SpecificationsValidator validator = new SpecificationsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		assertEquals(
			CoreErrorCodes.METADATA_SPECIFICATION_DEGREE_INVALID,
			report.getErrors().get(0).getCode() 
		);
	}


}
