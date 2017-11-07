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
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.Resolution;

@RunWith(MockitoJUnitRunner.class)
public class SpatialResolutionsValidatorTest extends MetadataValidatorTestBase {

	@Test
	public void testValid(){
		List<Resolution> resolutions = new ArrayList<>();
		{
			Resolution resolution = new Resolution();
			resolution.setDenominator("12");
			resolutions.add(resolution);
		}
		{
			Resolution resolution = new Resolution();
			resolution.setDistance("12.5");
			resolutions.add(resolution);
		}
		Metadata metadata = mock(Metadata.class);
		when(metadata.getSpatialResolutions()).thenReturn(resolutions);
		
		SpatialResolutionsValidator validator = new SpatialResolutionsValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}
	
	@Test
	public void testEmpty(){
		List<Resolution> resolutions = new ArrayList<>();
		Metadata metadata = mock(Metadata.class);
		when(metadata.getSpatialResolutions()).thenReturn(resolutions);
		
		SpatialResolutionsValidator validator = new SpatialResolutionsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_SPATIALRESOLUTIONS_EMPTY, error.getCode());
	}
	
	@Test
	public void testInvalidDenominator(){
		List<Resolution> resolutions = new ArrayList<>();
		{
			Resolution resolution = new Resolution();
			resolution.setDenominator("12.5");
			resolutions.add(resolution);
		}
		Metadata metadata = mock(Metadata.class);
		when(metadata.getSpatialResolutions()).thenReturn(resolutions);
		
		SpatialResolutionsValidator validator = new SpatialResolutionsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_SPATIALRESOLUTION_INVALID_DENOMINATOR, error.getCode());
	}

	@Test
	public void testInvalidDistance(){
		List<Resolution> resolutions = new ArrayList<>();
		{
			Resolution resolution = new Resolution();
			resolution.setDistance("invalid");
			resolutions.add(resolution);
		}
		Metadata metadata = mock(Metadata.class);
		when(metadata.getSpatialResolutions()).thenReturn(resolutions);
		
		SpatialResolutionsValidator validator = new SpatialResolutionsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_SPATIALRESOLUTION_INVALID_DISTANCE, error.getCode());
	}

}
