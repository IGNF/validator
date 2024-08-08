package fr.ign.validator.cnig.sup.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.tools.ResourceHelper;

/**
 * Test class {@link FileLocatorTest}
 *
 * @author MBorne
 *
 */
public class FileLocatorTest {

    private FileLocator fileLocator;

    @Before
    public void setUp() throws Exception {
        File dataDirectory = ResourceHelper.getResourceFile(getClass(), "/jointure_sup/one2one/DATA");
        fileLocator = new FileLocator(dataDirectory);
    }

    @Test
    public void testFindByNameNotFound() {
        File file = fileLocator.findByName("NOT_FOUND");
        assertNull(file);
    }

    @Test
    public void testFindServitude() {
        File file = fileLocator.findServitudeFile();
        assertNotNull(file);
        assertEquals("SERVITUDE.csv", file.getName());
    }

    @Test
    public void testFindActe() {
        File file = fileLocator.findByName("ACTE_SUP");
        assertNotNull(file);
        assertEquals("ACTE_SUP.csv", file.getName());
    }

    @Test
    public void testFindServitudeActeSup() {
        File file = fileLocator.findByName("SERVITUDE_ACTE_SUP");
        assertNotNull(file);
        assertEquals("SERVITUDE_ACTE_SUP.csv", file.getName());
    }

    @Test
    public void testFindGenerateurs() {
        List<File> files = fileLocator.findByRegex("(?i).*_GENERATEUR_SUP_.*");
        assertEquals(1, files.size());
        File generateurFile = files.get(0);
        assertEquals("AC2_GENERATEUR_SUP_S.csv", generateurFile.getName());
    }

}
