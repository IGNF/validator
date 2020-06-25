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

@RunWith(MockitoJUnitRunner.class)
public class IdentifiersValidatorTest extends MetadataValidatorTestBase {

    @Test
    public void testValid() {
        Metadata metadata = mock(Metadata.class);
        List<String> identifiers = new ArrayList<>();
        identifiers.add("test identifier");
        when(metadata.getIdentifiers()).thenReturn(identifiers);

        IdentifiersValidator validator = new IdentifiersValidator();
        validator.validate(context, metadata);

        assertEquals(0, report.getErrors().size());
    }

    @Test
    public void testNotValidEmptyIdentifiers() {
        Metadata metadata = mock(Metadata.class);
        when(metadata.getIdentifiers()).thenReturn(new ArrayList<>());

        IdentifiersValidator validator = new IdentifiersValidator();
        validator.validate(context, metadata);

        assertEquals(1, report.getErrors().size());
        ValidatorError error = report.getErrors().get(0);
        assertEquals(CoreErrorCodes.METADATA_IDENTIFIER_NOT_FOUND, error.getCode());
    }

    @Test
    public void testNotValidEmptyString() {
        List<String> identifiers = new ArrayList<>();
        identifiers.add("");
        Metadata metadata = mock(Metadata.class);
        when(metadata.getIdentifiers()).thenReturn(identifiers);

        IdentifiersValidator validator = new IdentifiersValidator();
        validator.validate(context, metadata);

        assertEquals(1, report.getErrors().size());
        ValidatorError error = report.getErrors().get(0);
        assertEquals(CoreErrorCodes.METADATA_IDENTIFIER_NOT_FOUND, error.getCode());
    }

}
