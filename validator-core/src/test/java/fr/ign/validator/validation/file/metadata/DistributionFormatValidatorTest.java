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
import fr.ign.validator.metadata.Format;
import fr.ign.validator.metadata.Metadata;

@RunWith(MockitoJUnitRunner.class)
public class DistributionFormatValidatorTest extends MetadataValidatorTestBase {

	@Test
	public void testValid() {
		Metadata metadata = mock(Metadata.class);
		List<Format> formats = new ArrayList<>();
		{
			Format format = new Format();
			format.setName("free text");
			formats.add(format);
		}
		when(metadata.getDistributionFormats()).thenReturn(formats);

		DistributionFormatsValidator validator = new DistributionFormatsValidator();
		validator.validate(context, metadata);

		assertEquals(0, report.getErrors().size());
	}

	@Test
	public void testEmpty() {
		Metadata metadata = mock(Metadata.class);
		List<Format> formats = new ArrayList<>();
		when(metadata.getDistributionFormats()).thenReturn(formats);

		DistributionFormatsValidator validator = new DistributionFormatsValidator();
		validator.validate(context, metadata);

		assertEquals(1, report.getErrors().size());
		assertEquals(
			CoreErrorCodes.METADATA_DISTRIBUTIONFORMATS_EMPTY, 
			report.getErrors().get(0).getCode()
		);
	}

	@Test
	public void testNotValidName() {
		Metadata metadata = mock(Metadata.class);
		List<Format> formats = new ArrayList<>();
		{
			Format format = new Format();
			format.setName("");
			formats.add(format);
		}
		when(metadata.getDistributionFormats()).thenReturn(formats);

		DistributionFormatsValidator validator = new DistributionFormatsValidator();
		validator.validate(context, metadata);

		assertEquals(1, report.getErrors().size());
		assertEquals(
			CoreErrorCodes.METADATA_DISTRIBUTIONFORMAT_NAME_NOT_FOUND, 
			report.getErrors().get(0).getCode()
		);
	}
	
}
