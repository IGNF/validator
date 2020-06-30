package fr.ign.validator.cnig.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.tools.ResourceHelper;

/**
 * Test sur l'extraction de typeref
 * 
 * @author MBorne
 *
 */
public class DocUrbaFilterTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private File docUrbaFile;

    @Before
    public void setUp() throws Exception {
        docUrbaFile = testFolder.newFile("DOC_URBA.csv");
        FileUtils.copyFile(
            ResourceHelper.getResourceFile(getClass(), "/csv/DOC_URBA.csv"),
            docUrbaFile
        );
    }

    @Test
    public void testIdUrbaFileNotFound() {
        DocUrbaFilter filter = new DocUrbaFilter(new IdurbaFormatV1(), "test");
        DocUrbaFilter.Result result = filter.process(docUrbaFile);
        assertNotNull(result);
        assertFalse(result.isIdurbaFound());
        // default value for typeref
        assertEquals("01", result.typeref);
    }

    @Test
    public void testFindStrictEquals() throws IOException {
        DocUrbaFilter filter = new DocUrbaFilter(new IdurbaFormatV1(), "50041_PLU_20130403");
        DocUrbaFilter.Result result = filter.process(docUrbaFile);
        assertTrue(result.isIdurbaFound());
        assertEquals("50041_20130403", result.idurba);
        assertEquals("01", result.typeref);
        List<String> lines = FileUtils.readLines(docUrbaFile, StandardCharsets.UTF_8);
        assertEquals(2, lines.size());
    }

    @Test
    public void testIdurbaNotFound() throws IOException {
        DocUrbaFilter filter = new DocUrbaFilter(new IdurbaFormatV1(), "99999_PLU_20130403");
        DocUrbaFilter.Result result = filter.process(docUrbaFile);
        assertFalse(result.isIdurbaFound());
        assertNull(result.idurba);
        assertEquals("01", result.typeref);
        List<String> lines = FileUtils.readLines(docUrbaFile, StandardCharsets.UTF_8);
        assertEquals(1, lines.size());
    }

}
