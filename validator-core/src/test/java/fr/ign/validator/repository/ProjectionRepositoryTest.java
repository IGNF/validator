package fr.ign.validator.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.validator.model.Projection;

/**
 * 
 * TODO add control about validity domain (check WKT validity for example)
 * 
 * @author MBorne
 *
 */
public class ProjectionRepositoryTest {

    @Test
    public void testFindAll() {
        ProjectionRepository repository = ProjectionRepository.getInstance();
        List<Projection> projections = repository.findAll();
        Assert.assertTrue(projections.size() > 5);
    }

    @Test
    public void testFindByName() {
        ProjectionRepository repository = ProjectionRepository.getInstance();
        Projection projection = repository.findByCode("EPSG:2154");
        Assert.assertEquals("EPSG:2154", projection.getCode());
        Assert.assertEquals("http://www.opengis.net/def/crs/EPSG/0/2154", projection.getUri());
    }

    @Test
    public void testFindByUri() {
        ProjectionRepository repository = ProjectionRepository.getInstance();
        Projection projection = repository.findByUri("http://www.opengis.net/def/crs/EPSG/0/2154");
        Assert.assertEquals("EPSG:2154", projection.getCode());
        Assert.assertEquals("http://www.opengis.net/def/crs/EPSG/0/2154", projection.getUri());
    }

    /**
     * Ensure that all projection can be converted to geotool
     */
    @Test
    public void testGetCRS() {
        ProjectionRepository repository = ProjectionRepository.getInstance();
        List<Projection> projections = repository.findAll();
        for (Projection projection : projections) {
            Assert.assertNotNull(
                "fail to convert " + projection.getCode()
                    + " to geotool (add/check codeGeotool in src/main/resources/projection.json)",
                projection.getCRS()
            );
        }
    }

}
