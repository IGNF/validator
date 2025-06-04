package fr.ign.validator.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class CharsetDetector {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("CharsetDetector");

    /**
     * Detects character encoding from data source between UTF-8 and LATIN1.
     *
     * Warning :
     * <ul>
     * <li>Returns UTF-8 first if characters are valid by UTF-8</li>
     * <li>Consequently, can return UTF-8 if text is ISO_8859_1 without accent</li>
     * </ul>
     *
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Charset detectCharset(File file) throws IOException {
        log.debug(MARKER, "detectCharset('{}')...", file.getAbsolutePath());
        if (isValidUTF8(file)) {
            log.debug(MARKER, "detectCharset('{}') : UTF-8", file.getAbsolutePath());
            return StandardCharsets.UTF_8;
        } else {
            log.debug(MARKER, "detectCharset('{}') : LATIN-1", file.getAbsolutePath());
            return StandardCharsets.ISO_8859_1;
        }
    }

    /**
     * Tests if file is encoded in UTF-8
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public static boolean isValidUTF8(File file) {
        return isValidCharset(file, StandardCharsets.UTF_8);
    }

    /**
     * Tests if file is encoded in given charset
     *
     * @param file
     * @param charset
     * @return
     */
    public static boolean isValidCharset(File file, Charset charset) {
        log.trace(MARKER, "isValidCharset('{}','{}')...", file.getAbsolutePath(), charset);
        CharsetDecoder cs = charset.newDecoder();
        BufferedReader in = null;
        try {
            in = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(file),
                    cs
                )
            );
            while (in.readLine() != null) {

            }
        } catch (Exception e) {
            log.debug(MARKER, "isValidCharset('{}','{}') : false", file.getAbsolutePath(), charset);
            return false;
        } finally {
            IOUtils.closeQuietly(in);
        }
        log.trace(MARKER, "isValidCharset('{}','{}') : true", file.getAbsolutePath(), charset);
        return true;
    }

}
