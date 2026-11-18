package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CharsetDetectorTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * A file without line breaks is read by blocks (not as a single line in
     * memory), until its end.
     */
    @Test
    public void testIsValidUTF8WithoutLineBreak() throws IOException {
        byte[] content = new byte[5 * 1024 * 1024];
        Arrays.fill(content, (byte) 'a');
        // "é" in UTF-8
        content[1000] = (byte) 0xC3;
        content[1001] = (byte) 0xA9;
        File valid = folder.newFile("valid.csv");
        Files.write(valid.toPath(), content);
        assertTrue(CharsetDetector.isValidUTF8(valid));

        // invalid byte at the end of the file
        content[content.length - 1] = (byte) 0xFF;
        File invalid = folder.newFile("invalid.csv");
        Files.write(invalid.toPath(), content);
        assertFalse(CharsetDetector.isValidUTF8(invalid));
    }

    /**
     * Results are cached, but a file modified afterwards is analysed again.
     */
    @Test
    public void testResultsAreNotStaleWhenFileIsModified() throws IOException {
        File file = folder.newFile("rewritten.csv");
        Files.write(file.toPath(), "id;nom\n1;caf\u00e9\n".getBytes(StandardCharsets.UTF_8));
        assertEquals(StandardCharsets.UTF_8, CharsetDetector.detectCharset(file));
        assertTrue(CharsetDetector.isValidUTF8(file));
        // same call again (from the cache)
        assertEquals(StandardCharsets.UTF_8, CharsetDetector.detectCharset(file));
        assertTrue(CharsetDetector.isValidUTF8(file));

        // same path, other content and size
        Files.write(file.toPath(), "id;nom\n1;caf\u00e9 cr\u00e8me\n".getBytes(StandardCharsets.ISO_8859_1));
        assertEquals(StandardCharsets.ISO_8859_1, CharsetDetector.detectCharset(file));
        assertFalse(CharsetDetector.isValidUTF8(file));
    }

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
