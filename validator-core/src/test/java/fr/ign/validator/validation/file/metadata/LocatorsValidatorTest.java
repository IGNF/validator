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
import fr.ign.validator.metadata.OnlineResource;

@RunWith(MockitoJUnitRunner.class)
public class LocatorsValidatorTest extends MetadataValidatorTestBase {

	@Test
	public void testValid(){
		List<OnlineResource> locators = new ArrayList<>();
		{
			OnlineResource locator = new OnlineResource();
			locator.setName("Service WMS");
			locator.setProtocol("TODO");
			locator.setUrl("http://localhost/wms");
			locators.add(locator);
		}
		Metadata metadata = mock(Metadata.class);
		when(metadata.getLocators()).thenReturn(locators);
		
		LocatorsValidator validator = new LocatorsValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}
	
	@Test
	public void testLocatorNameNotFound(){
		List<OnlineResource> locators = new ArrayList<>();
		{
			OnlineResource locator = new OnlineResource();
			locator.setName(null);
			locator.setProtocol("TODO");
			locator.setUrl("http://localhost/wms");
			locators.add(locator);
		}
		Metadata metadata = mock(Metadata.class);
		when(metadata.getLocators()).thenReturn(locators);
		
		LocatorsValidator validator = new LocatorsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_LOCATOR_NAME_NOT_FOUND, error.getCode());
	}
	
	@Test
	public void testLocatorProtocolNotFound(){
		List<OnlineResource> locators = new ArrayList<>();
		{
			OnlineResource locator = new OnlineResource();
			locator.setName("TODO");
			locator.setProtocol(null);
			locator.setUrl("http://localhost/wms");
			locators.add(locator);
		}
		Metadata metadata = mock(Metadata.class);
		when(metadata.getLocators()).thenReturn(locators);
		
		LocatorsValidator validator = new LocatorsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, error.getCode());
	}
	
	@Test
	public void testLocatorUrlNotFound(){
		List<OnlineResource> locators = new ArrayList<>();
		{
			OnlineResource locator = new OnlineResource();
			locator.setName("Name");
			locator.setProtocol("TODO");
			locator.setUrl(null);
			locators.add(locator);
		}
		Metadata metadata = mock(Metadata.class);
		when(metadata.getLocators()).thenReturn(locators);
		
		LocatorsValidator validator = new LocatorsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_LOCATOR_URL_NOT_FOUND, error.getCode());
	}

	
	@Test
	public void testEmptyLocators(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getLocators()).thenReturn(new ArrayList<OnlineResource>());
		
		LocatorsValidator validator = new LocatorsValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_LOCATORS_EMPTY, error.getCode());	
	}

}
