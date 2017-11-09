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
import fr.ign.validator.metadata.BoundingBox;
import fr.ign.validator.metadata.Extent;
import fr.ign.validator.metadata.Metadata;

@RunWith(MockitoJUnitRunner.class)
public class ExtentsValidatorTest extends MetadataValidatorTestBase {

	@Test
	public void testValid(){
		List<Extent> extents = new ArrayList<>();
		{
			Extent extent = new Extent();
			BoundingBox boundingBox = new BoundingBox();
			boundingBox.setEastBoundLongitude("-1.0");
			boundingBox.setWestBoundLongitude(" 1.0");
			boundingBox.setSouthBoundLatitude("-1.0");
			boundingBox.setNorthBoundLatitude(" 1.0");			
			extent.setBoundingBox(boundingBox);
			extents.add(extent);
		}
		Metadata metadata = mock(Metadata.class);
		when(metadata.getExtents()).thenReturn(extents);
		
		ExtentsValidator validator = new ExtentsValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}
	
	@Test
	public void testEmpty(){
		List<Extent> extents = new ArrayList<>();
		
		Metadata metadata = mock(Metadata.class);
		when(metadata.getExtents()).thenReturn(extents);
		
		ExtentsValidator validator = new ExtentsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_EXTENTS_EMPTY, error.getCode());
	}
	
	@Test
	public void testInvalid(){
		List<Extent> extents = new ArrayList<>();
		{
			Extent extent = new Extent();
			BoundingBox boundingBox = new BoundingBox();
			boundingBox.setEastBoundLongitude("-1,0"); // comma instead of dot
			boundingBox.setWestBoundLongitude(" 1.0");
			boundingBox.setSouthBoundLatitude("-1.0");
			boundingBox.setNorthBoundLatitude(" 1.0");			
			extent.setBoundingBox(boundingBox);
			extents.add(extent);
		}
		
		Metadata metadata = mock(Metadata.class);
		when(metadata.getExtents()).thenReturn(extents);
		
		ExtentsValidator validator = new ExtentsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_EXTENT_INVALID, error.getCode());
		assertEquals(
			"Rectangle de délimitation géographique (1/1) : les coordonnées ne sont pas valides ( 1.0,-1.0,-1,0, 1.0).", 
			error.getMessage()
		);
	}

}
