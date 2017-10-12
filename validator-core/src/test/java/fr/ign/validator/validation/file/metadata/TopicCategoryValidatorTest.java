package fr.ign.validator.validation.file.metadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.code.TopicCategoryCode;

@RunWith(MockitoJUnitRunner.class)
public class TopicCategoryValidatorTest extends MetadataValidatorTestBase {

	@Test
	public void testValid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getTopicCategory()).thenReturn(TopicCategoryCode.valueOf("planningCadastre"));
		
		TopicCategoryValidator validator = new TopicCategoryValidator();
		validator.validate(context, metadata);
		
		assertEquals(0, report.getErrors().size());
	}
	
	@Test
	public void testNotFound(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getTopicCategory()).thenReturn(null);
		
		TopicCategoryValidator validator = new TopicCategoryValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_TOPICCATEGORY_NOT_FOUND, error.getCode());
	}
	
	@Test
	public void testInvalid(){
		Metadata metadata = mock(Metadata.class);
		when(metadata.getTopicCategory()).thenReturn(TopicCategoryCode.valueOf("invalid"));
		
		TopicCategoryValidator validator = new TopicCategoryValidator();
		validator.validate(context, metadata);
		
		assertEquals(1, report.getErrors().size());
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CoreErrorCodes.METADATA_TOPICCATEGORY_INVALID, error.getCode());
	}

}
