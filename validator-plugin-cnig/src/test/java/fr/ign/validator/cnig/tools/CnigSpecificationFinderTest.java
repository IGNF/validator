package fr.ign.validator.cnig.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.ign.validator.cnig.tools.CnigSpecificationFinder;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.Specification;

public class CnigSpecificationFinderTest {
	
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
		
		Specification result = CnigSpecificationFinder.findCnigSpecification(metadata);
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
		
		Specification result = CnigSpecificationFinder.findCnigSpecification(metadata);
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
		
		Specification result = CnigSpecificationFinder.findCnigSpecification(metadata);
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
		
		Specification result = CnigSpecificationFinder.findCnigSpecification(metadata);
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
		
		Specification result = CnigSpecificationFinder.findCnigSpecification(metadata);
		assertNull(result);
	}
	
	@Test
	public void testValidTitle() {
		assertTrue(CnigSpecificationFinder.isCnigSpecification("CNIG PLU v2014"));
		assertTrue(CnigSpecificationFinder.isCnigSpecification("CNIG CC v2014"));
		// case insensitive
		assertTrue(CnigSpecificationFinder.isCnigSpecification("cnig plu v2014"));
		assertTrue(CnigSpecificationFinder.isCnigSpecification("cnig cc v2014"));		
	}

	@Test	
	public void testNotValidTitle() {
		assertFalse(CnigSpecificationFinder.isCnigSpecification("CNIG BAD v2014"));
		assertFalse(CnigSpecificationFinder.isCnigSpecification("CNIG BAD v2014"));	
	}
}
