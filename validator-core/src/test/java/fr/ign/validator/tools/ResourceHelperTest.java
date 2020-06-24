package fr.ign.validator.tools;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import com.sun.xml.bind.CycleRecoverable.Context;

public class ResourceHelperTest {

    /**
     * Get validator-core/src/test/resources/jexiste.txt
     */
    @Test
    public void testGetCoreTestResource() {
        File file = ResourceHelper.getResourceFile(getClass(), "/jexiste.txt");
        assertTrue(file.exists());
    }

    /**
     * Try to get missing validator-core/src/test/resources/jexiste-pas.txt
     */
    @Test
    public void testGetMissingCoreTestResource() {
        boolean exceptionThown = false;
        try {
            ResourceHelper.getResourceFile(getClass(), "/jexiste-pas.txt");
        } catch (RuntimeException e) {
            Assert.assertEquals(
                "Resource '/jexiste-pas.txt' not found",
                e.getMessage()
            );
            exceptionThown = true;
        }
        assertTrue(exceptionThown);
    }

    /**
     * Get validator-core/src/main/resources/error-code.json
     * 
     * @throws IOException
     */
    @Test
    public void testGetCoreMainResource() throws IOException {
        File file = ResourceHelper.getResourceFile(Context.class, "/error-code.json");
        Assert.assertTrue(file.exists());
        String content = FileUtils.readFileToString(file);
        assertTrue(content.contains("FILE_EMPTY"));
    }

}
