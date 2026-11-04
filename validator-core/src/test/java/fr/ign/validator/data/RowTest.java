package fr.ign.validator.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.geometry.ProjectionTransform;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.report.InMemoryReportBuilder;

/**
 * Test the geometry reading of a {@link Row} (read once, bounding box computed
 * on demand)
 */
public class RowTest {

    private static final String[] HEADER = {
        "nom", "WKT"
    };

    private static final String POLYGON = "POLYGON((700000 6600000,700100 6600000,700100 6600100,700000 6600100,700000 6600000))";

    private static final String BOWTIE = "POLYGON((700000 6600000,700100 6600100,700100 6600000,700000 6600100,700000 6600000))";

    private Context context;

    private InMemoryReportBuilder report;

    private FeatureTypeMapper mapping;

    @Before
    public void setUp() {
        report = new InMemoryReportBuilder();
        context = new Context();
        context.setReportBuilder(report);
        context.setProjection("EPSG:2154");

        FeatureType featureType = new FeatureType();
        AttributeType<?> nom = AttributeType.forName("String");
        nom.setName("nom");
        featureType.addAttribute(nom);
        AttributeType<?> wkt = AttributeType.forName("Polygon");
        wkt.setName("WKT");
        featureType.addAttribute(wkt);

        mapping = new FeatureTypeMapper(HEADER, featureType);
    }

    private Row validate(String wkt) {
        Row row = new Row(1, new String[] {
            "test", wkt
        }, mapping);
        context.beginModel(mapping.getFeatureType());
        row.validate(context);
        context.endModel(mapping.getFeatureType());
        return row;
    }

    private Envelope expectedBbox(String wkt) throws Exception {
        Geometry geometry = GeometryType.parse(wkt);
        return new ProjectionTransform(context.getProjection()).transform(geometry).getEnvelopeInternal();
    }

    @Test
    public void testValidGeometryBboxComputedOnDemand() throws Exception {
        Row row = validate(POLYGON);
        assertEquals(0, report.getErrors().size());
        Envelope bbox = row.getFeatureBbox();
        assertNotNull(bbox);
        assertEquals(expectedBbox(POLYGON), bbox);
        // CRS:84
        assertTrue(bbox.getMinX() > 3.0 && bbox.getMaxX() < 3.1);
        assertTrue(bbox.getMinY() > 46.4 && bbox.getMaxY() < 46.6);
        // computed once
        assertSame(bbox, row.getFeatureBbox());
    }

    @Test
    public void testInvalidGeometryReportedWithBbox() throws Exception {
        validate(BOWTIE);
        List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID);
        assertEquals(1, errors.size());
        assertEquals(expectedBbox(BOWTIE), errors.get(0).getFeatureBbox());
    }

    @Test
    public void testInvalidGeometryTypeReportedWithBbox() throws Exception {
        String wkt = "LINESTRING(700000 6600000,700100 6600100)";
        validate(wkt);
        List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID_FORMAT);
        assertEquals(1, errors.size());
        assertEquals(expectedBbox(wkt), errors.get(0).getFeatureBbox());
    }

    @Test
    public void testInvalidWktReportedWithoutBbox() {
        Row row = validate("POLYGON((not a wkt))");
        List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID_FORMAT);
        assertEquals(1, errors.size());
        assertNull(errors.get(0).getFeatureBbox());
        assertNull(row.getFeatureBbox());
    }

    @Test
    public void testInvalidStructureReportedWithoutBbox() {
        String[] wkts = {
            "POLYGON((700000 6600000,700100 6600000,700100 6600100,700000 6600100))",
            "LINESTRING(700000 6600000)"
        };
        for (String wkt : wkts) {
            Row row = validate(wkt);
            assertNull(row.getFeatureBbox());
        }
        assertEquals(2, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID_FORMAT).size());
    }

    @Test
    public void testExplicitBbox() {
        Row row = new Row(1, new String[] {
            "test", POLYGON
        }, mapping);
        Envelope bbox = new Envelope(1.0, 2.0, 3.0, 4.0);
        row.setFeatureBbox(bbox);
        assertSame(bbox, row.getFeatureBbox());
    }

    @Test
    public void testAttributeKeepsOriginalValue() throws Exception {
        @SuppressWarnings("unchecked")
        AttributeType<Geometry> type = (AttributeType<Geometry>) AttributeType.forName("Polygon");
        Geometry geometry = GeometryType.parse(POLYGON);
        Attribute<Geometry> attribute = type.newAttribute(POLYGON, geometry);
        // raw value for CharactersValidator
        assertEquals(POLYGON, attribute.getValue());
        // binding without parsing again
        assertSame(geometry, attribute.getBindedValue());
    }

}
