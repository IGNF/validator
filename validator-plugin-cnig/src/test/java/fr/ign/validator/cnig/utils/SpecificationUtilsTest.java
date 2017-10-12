package fr.ign.validator.cnig.utils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.Specification;

public class SpecificationUtilsTest {
	
	/**
	 * Ensure that strict matching works
	 */
	@Test
	public void testFindCnigSpecificationFound(){
		Metadata metadata = mock(Metadata.class);
		List<Specification> specifications = new ArrayList<>();
		{
			Specification specification = new Specification();
			specification.setDateType("publication");
			specification.setTitle("CNIG PLU v2013");
			specifications.add(specification);
		}
		when(metadata.getSpecifications()).thenReturn(specifications);
		
		Specification result = SpecificationUtils.findCnigSpecification(metadata);
		assertNotNull(result);
		assertSame(specifications.get(0),result);
	}
	
	/**
	 * Ensure that matching is case insensitive
	 */
	@Test
	public void testFindCnigSpecificationFoundLowerCase(){
		Metadata metadata = mock(Metadata.class);
		List<Specification> specifications = new ArrayList<>();
		{
			Specification specification = new Specification();
			specification.setDateType("publication");
			specification.setTitle("cnig PLU v2013");
			specifications.add(specification);
		}
		when(metadata.getSpecifications()).thenReturn(specifications);
		
		Specification result = SpecificationUtils.findCnigSpecification(metadata);
		assertNotNull(result);
		assertSame(specifications.get(0),result);
	}
	

	/**
	 * Ensure no crash on null title or null dateType
	 */
	@Test
	public void testFindCnigSpecificationEmpty(){
		Metadata metadata = mock(Metadata.class);
		List<Specification> specifications = new ArrayList<>();
		{
			Specification specification = new Specification();
			specification.setDateType(null);
			specification.setTitle("not valid");
			specifications.add(specification);
		}
		{
			Specification specification = new Specification();
			specification.setDateType("publication"); // valid
			specification.setTitle(null);
			specifications.add(specification);
		}
		when(metadata.getSpecifications()).thenReturn(specifications);
		
		Specification result = SpecificationUtils.findCnigSpecification(metadata);
		assertNull(result);
	}
	
	/**
	 * Ensure that bad dateType is ignored
	 */
	@Test
	public void testFindCnigSpecificationBadDateType(){
		Metadata metadata = mock(Metadata.class);
		List<Specification> specifications = new ArrayList<>();
		{
			Specification specification = new Specification();
			specification.setDateType("revision");
			specification.setTitle("CNIG PLU v2013");
			specifications.add(specification);
		}
		when(metadata.getSpecifications()).thenReturn(specifications);
		
		Specification result = SpecificationUtils.findCnigSpecification(metadata);
		assertNull(result);
	}
	
	/**
	 * Ensure that 
	 */
	@Test
	public void testFindCnigSpecificationBadName(){
		Metadata metadata = mock(Metadata.class);
		List<Specification> specifications = new ArrayList<>();
		{
			Specification specification = new Specification();
			specification.setDateType("publication");
			specification.setTitle("something else");
			specifications.add(specification);
		}
		when(metadata.getSpecifications()).thenReturn(specifications);
		
		Specification result = SpecificationUtils.findCnigSpecification(metadata);
		assertNull(result);
	}
}
