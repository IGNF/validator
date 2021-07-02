package fr.ign.validator.cnig.validation.metadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.validation.CnigValidatorTestBase;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.ReferenceSystemIdentifier;

public class CnigMetadataReferenceSystemIdentifierValidatorTest extends CnigValidatorTestBase {

    @Test
    public void testNotFound() {
        Metadata metadata = mock(Metadata.class);
        when(metadata.getReferenceSystemIdentifier()).thenReturn(null);

        CnigMetadataReferenceSystemIdentifierValidator validator = new CnigMetadataReferenceSystemIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(1, report.getErrors().size());
        assertEquals(
            CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_NOT_FOUND,
            report.getErrors().get(0).getCode()
        );
    }

    @Test
    public void testCodeNotFound() {
        Metadata metadata = mock(Metadata.class);
        ReferenceSystemIdentifier rsi = new ReferenceSystemIdentifier();
        // rsi.setCode("EPSG:2154");
        rsi.setUri("http://www.opengis.net/def/crs/EPSG/0/2154");
        when(metadata.getReferenceSystemIdentifier()).thenReturn(rsi);

        CnigMetadataReferenceSystemIdentifierValidator validator = new CnigMetadataReferenceSystemIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(1, report.getErrors().size());
        assertEquals(
            CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_CODE_NOT_FOUND,
            report.getErrors().get(0).getCode()
        );
    }

    @Test
    public void testCodeNotValid() {
        Metadata metadata = mock(Metadata.class);
        ReferenceSystemIdentifier rsi = new ReferenceSystemIdentifier();
        rsi.setCode("EPSG:999999999");
        rsi.setUri("http://www.opengis.net/def/crs/EPSG/0/2154");
        when(metadata.getReferenceSystemIdentifier()).thenReturn(rsi);

        CnigMetadataReferenceSystemIdentifierValidator validator = new CnigMetadataReferenceSystemIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(1, report.getErrors().size());
        assertEquals(
            CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_CODE_INVALID,
            report.getErrors().get(0).getCode()
        );
    }

    @Test
    public void testUriNotFoundInMetadata() {
        Metadata metadata = mock(Metadata.class);
        ReferenceSystemIdentifier rsi = new ReferenceSystemIdentifier();
        rsi.setCode("EPSG:2154");
        // rsi.setUri("http://www.opengis.net/def/crs/EPSG/0/2154");
        when(metadata.getReferenceSystemIdentifier()).thenReturn(rsi);

        CnigMetadataReferenceSystemIdentifierValidator validator = new CnigMetadataReferenceSystemIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(1, report.getErrors().size());
        assertEquals(
            CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_NOT_FOUND,
            report.getErrors().get(0).getCode()
        );
    }

    @Test
    public void testUriNotSupported() {
        Metadata metadata = mock(Metadata.class);
        ReferenceSystemIdentifier rsi = new ReferenceSystemIdentifier();
        rsi.setCode("EPSG:20135");
        rsi.setUri("http://www.opengis.net/def/crs/EPSG/0/20135");
        when(metadata.getReferenceSystemIdentifier()).thenReturn(rsi);

        CnigMetadataReferenceSystemIdentifierValidator validator = new CnigMetadataReferenceSystemIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(1, report.getErrors().size());
        assertEquals(
            CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_UNEXPECTED,
            report.getErrors().get(0).getCode()
        );
    }

    @Test
    public void testCodeUnexpected() {
        Metadata metadata = mock(Metadata.class);
        ReferenceSystemIdentifier rsi = new ReferenceSystemIdentifier();
        rsi.setCode("EPSG:2154");
        rsi.setUri("http://www.opengis.net/def/crs/EPSG/0/4326");
        when(metadata.getReferenceSystemIdentifier()).thenReturn(rsi);

        CnigMetadataReferenceSystemIdentifierValidator validator = new CnigMetadataReferenceSystemIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(1, report.getErrors().size());
        assertEquals(
            CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_CODE_INVALID,
            report.getErrors().get(0).getCode()
        );
        assertEquals(
            "Le code de projection (EPSG:2154) du \"Référentiel de coordonnées\" ne correspond pas à la valeur attendue (EPSG:4326) pour l'URI (http://www.opengis.net/def/crs/EPSG/0/4326).",
            report.getErrors().get(0).getMessage()
        );
    }

    @Test
    public void testValid() {
        Metadata metadata = mock(Metadata.class);
        ReferenceSystemIdentifier rsi = new ReferenceSystemIdentifier();
        rsi.setCode("EPSG:2154");
        rsi.setUri("http://www.opengis.net/def/crs/EPSG/0/2154");
        when(metadata.getReferenceSystemIdentifier()).thenReturn(rsi);

        CnigMetadataReferenceSystemIdentifierValidator validator = new CnigMetadataReferenceSystemIdentifierValidator();
        validator.validate(context, metadata);

        assertEquals(0, report.getErrors().size());
    }
}
