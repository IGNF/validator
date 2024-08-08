package fr.ign.validator.validation.file.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.tools.ResourceHelper;

@RunWith(MockitoJUnitRunner.class)
public class FileIdentifierValidatorTest extends MetadataValidatorTestBase {

    @Test
    public void testValid1() {
        Metadata metadata = mock(Metadata.class);
        when(metadata.getFileIdentifier()).thenReturn("test-identifier");

        FileIdentifierValidator validator = new FileIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(0, report.getErrors().size());
    }

    @Test
    public void testValid2() {
        Metadata metadata = mock(Metadata.class);
        when(metadata.getFileIdentifier()).thenReturn("test:identifier");

        FileIdentifierValidator validator = new FileIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(0, report.getErrors().size());
    }

    @Test
    public void testNotFound() {
        Metadata metadata = mock(Metadata.class);
        when(metadata.getFileIdentifier()).thenReturn(null);

        FileIdentifierValidator validator = new FileIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(1, report.getErrors().size());
        ValidatorError error = report.getErrors().get(0);
        assertEquals(CoreErrorCodes.METADATA_FILEIDENTIFIER_NOT_FOUND, error.getCode());
        assertEquals("Le champ \"fileIdentifier\" n'est pas renseigné.", error.getMessage());
    }

    @Test
    public void testEmpty() {
        Metadata metadata = mock(Metadata.class);
        when(metadata.getFileIdentifier()).thenReturn("");

        FileIdentifierValidator validator = new FileIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(1, report.getErrors().size());
        ValidatorError error = report.getErrors().get(0);
        assertEquals(CoreErrorCodes.METADATA_FILEIDENTIFIER_NOT_FOUND, error.getCode());
        assertEquals("Le champ \"fileIdentifier\" n'est pas renseigné.", error.getMessage());
    }

    @Test
    public void testValidClean() {
        Metadata metadata = mock(Metadata.class);
        when(metadata.getFileIdentifier()).thenReturn("fr-000080230-CC20110701");

        FileIdentifierValidator validator = new FileIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(0, report.getErrors().size());
    }

    public void testValidWithExtension() {
        Metadata metadata = mock(Metadata.class);
        when(metadata.getFileIdentifier()).thenReturn("fr-200034684-88104plu20071210.xml");

        FileIdentifierValidator validator = new FileIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(0, report.getErrors().size());
    }

    @Test
    public void testValidWithSpaces() {
        Metadata metadata = mock(Metadata.class);
        when(metadata.getFileIdentifier()).thenReturn("my fileidentifier");

        FileIdentifierValidator validator = new FileIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(0, report.getErrors().size());
    }

    @Test
    public void testInvalidSpecialChars() {
        Metadata metadata = mock(Metadata.class);
        when(metadata.getFileIdentifier()).thenReturn("special!");

        FileIdentifierValidator validator = new FileIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(1, report.getErrors().size());
        ValidatorError error = report.getErrors().get(0);
        assertEquals(CoreErrorCodes.METADATA_FILEIDENTIFIER_INVALID, error.getCode());
        assertEquals(
            "Le champ \"fileIdentifier\" (special!) ne correspond pas aux caractères autorisés.",
            error.getMessage()
        );
    }

    /**
     * Ensure that all fileIdentifiers in are valid in
     * "sample-fileidentifiers-valid.txt" file
     *
     * @throws IOException
     */
    @Test
    public void testRegressSampleFileidentifiersValid() throws IOException {
        File file = ResourceHelper.getResourceFile(getClass(), "/metadata/sample-fileidentifiers-valid.txt");
        List<String> fileIdentifiers = FileUtils.readLines(file, StandardCharsets.UTF_8);
        FileIdentifierValidator validator = new FileIdentifierValidator();
        for (String fileIdentifier : fileIdentifiers) {
            assertTrue(
                String.format("'%s' should be valid", fileIdentifier),
                validator.isValid(fileIdentifier)
            );
        }
    }

}
