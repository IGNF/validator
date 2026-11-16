package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class CharsetDetectorTest {

    @Test
    public void testDetectUTF8() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/sample-utf8.csv");
        Charset charset = CharsetDetector.detectCharset(srcFile);
        assertEquals(StandardCharsets.UTF_8, charset);
    }

    @Test
    public void testDetectLATIN1() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/sample-latin1.csv");
        Charset charset = CharsetDetector.detectCharset(srcFile);
        assertEquals(StandardCharsets.ISO_8859_1, charset);
    }

    @Test
    public void testBisDetectUTF8() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/AC2_ASSIETTE_SUP_S_014_utf8.csv");
        Charset charset = CharsetDetector.detectCharset(srcFile);
        assertEquals(StandardCharsets.UTF_8, charset);
    }

    @Test
    public void testBisDetectLATIN1() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/AC2_ASSIETTE_SUP_S_014_latin1.csv");
        Charset charset = CharsetDetector.detectCharset(srcFile);
        assertEquals(StandardCharsets.ISO_8859_1, charset);
    }

    @Test
    public void testGenerateurDetectUTF8() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/AC2_GENERATEUR_SUP_S_014_utf8.csv");
        Charset charset = CharsetDetector.detectCharset(srcFile);
        assertEquals(StandardCharsets.UTF_8, charset);
    }

    /**
     * A UTF-8 file with a character truncated in the middle (ex : DBF field length
     * limit) must still be detected as UTF-8.
     */
    @Test
    public void testDetectUTF8WithTruncatedChar() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/sample-utf8-truncated.csv");
        assertFalse(CharsetDetector.isValidUTF8(srcFile));
        Charset charset = CharsetDetector.detectCharset(srcFile);
        assertEquals(StandardCharsets.UTF_8, charset);
    }

}
