package fr.ign.validator.geometry;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.model.Projection;
import fr.ign.validator.tools.ResourceHelper;
import fr.ign.validator.tools.TableReader;

/**
 * This test relies on
 * validator-core/src/test/resources/projection/reference_postgis.csv to perform
 * test about coordinate transforms. Basically, it compares postgis and geotools
 * transforms.
 * 
 * @see validator-core/src/test/resources/projection/README.md
 * 
 * @author MBorne
 *
 */
public class ProjectionTransformTest {

    @Test
    public void test2154toCRS84() throws Exception {
        runTestFromTo("EPSG:2154", Projection.CODE_CRS84, 1.0e-7);
    }

    @Test
    public void testCRS84to2154() throws Exception {
        runTestFromTo(Projection.CODE_CRS84, "EPSG:2154", 1.0e-3);
    }

    @Test
    public void test32620toCRS84() throws Exception {
        runTestFromTo("EPSG:32620", Projection.CODE_CRS84, 1.0e-7);
    }

    @Test
    public void testCRS84to32620() throws Exception {
        runTestFromTo(Projection.CODE_CRS84, "EPSG:32620", 1.0e-3);
    }

    @Test
    public void test2972toCRS84() throws Exception {
        runTestFromTo("EPSG:2972", Projection.CODE_CRS84, 1.0e-7);
    }

    @Test
    public void testCRS84to2972() throws Exception {
        runTestFromTo(Projection.CODE_CRS84, "EPSG:2972", 1.0e-3);
    }

    @Test
    public void test2975toCRS84() throws Exception {
        runTestFromTo("EPSG:2975", Projection.CODE_CRS84, 1.0e-7);
    }

    @Test
    public void testCRS84to2975() throws Exception {
        runTestFromTo(Projection.CODE_CRS84, "EPSG:2975", 1.0e-3);
    }

    @Test
    public void test4471toCRS84() throws Exception {
        runTestFromTo("EPSG:4471", Projection.CODE_CRS84, 1.0e-7);
    }

    @Test
    public void testCRS84to4471() throws Exception {
        runTestFromTo(Projection.CODE_CRS84, "EPSG:4471", 1.0e-3);
    }

    /**
     * 
     * @param sourceSRID
     * @param targetSRID
     * @param tolerance
     * @throws Exception
     */
    private void runTestFromTo(String sourceSRID, String targetSRID, double tolerance) throws Exception {
        ProjectionList projectionRepository = ProjectionList.getInstance();
        Projection sourceProjection = projectionRepository.findByCode(sourceSRID);
        Projection targetProjection = projectionRepository.findByCode(targetSRID);
        GeometryTransform transformProjection = new ProjectionTransform(sourceProjection, targetProjection);

        File reference = ResourceHelper.getResourceFile(getClass(), "/projection/reference_postgis.csv");
        TableReader reader = TableReader.createTableReader(reference, StandardCharsets.UTF_8);
        int indexSource = reader.findColumnRequired(sourceSRID);
        int indexTarget = reader.findColumnRequired(targetSRID);

        GeometryReader geometryReader = new GeometryReader();
        while (reader.hasNext()) {
            String[] row = reader.next();
            String sourceWKT = row[indexSource];
            String expectedWKT = row[indexTarget];
            if (StringUtils.isEmpty(sourceWKT) || StringUtils.isEmpty(expectedWKT)) {
                continue;
            }

            Geometry source = geometryReader.read(sourceWKT);
            Geometry expectedTarget = geometryReader.read(expectedWKT);

            Geometry target = transformProjection.transform(source);
            double distance = target.distance(expectedTarget);
            Assert.assertTrue(
                "EPSG:" + sourceSRID + " to EPSG:" + targetSRID + " : distance : " + distance + " greater than "
                    + tolerance,
                distance <= tolerance
            );
        }
    }

}
