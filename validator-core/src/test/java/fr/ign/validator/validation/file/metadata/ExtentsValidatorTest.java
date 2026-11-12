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
    public void testValid() {
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
    public void testEmpty() {
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
    public void testInvalid() {
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

    private int countErrors(String west, String south, String east, String north) {
        report.getErrors().clear();
        Extent extent = new Extent();
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.setWestBoundLongitude(west);
        boundingBox.setSouthBoundLatitude(south);
        boundingBox.setEastBoundLongitude(east);
        boundingBox.setNorthBoundLatitude(north);
        extent.setBoundingBox(boundingBox);
        List<Extent> extents = new ArrayList<>();
        extents.add(extent);
        Metadata metadata = mock(Metadata.class);
        when(metadata.getExtents()).thenReturn(extents);
        new ExtentsValidator().validate(context, metadata);
        return report.getErrors().size();
    }

    @Test
    public void testBoundsIncluded() {
        assertEquals(0, countErrors("-180", "-90", "180", "90"));
    }

    @Test
    public void testLongitudeOutOfRange() {
        assertEquals(1, countErrors("-180.1", "0", "1", "1"));
        assertEquals(1, countErrors("0", "0", "180.1", "1"));
    }

    @Test
    public void testLatitudeOutOfRange() {
        assertEquals(1, countErrors("0", "-90.1", "1", "1"));
        assertEquals(1, countErrors("0", "0", "1", "90.1"));
    }

    @Test
    public void testSouthGreaterThanNorth() {
        assertEquals(1, countErrors("0", "10", "1", "5"));
    }

}
