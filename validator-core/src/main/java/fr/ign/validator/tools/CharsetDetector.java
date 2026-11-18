package fr.ign.validator.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class CharsetDetector {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("CharsetDetector");

    /**
     * Maximum number of results kept in the caches.
     */
    private static final int CACHE_SIZE = 256;

    /**
     * Results of {@link #isValidCharset(File, Charset)} : a file is read several
     * times during a validation (validation, normalization, statistics, plugins) and
     * each reader would decode it entirely to check the charset.
     *
     * The key contains the path, the last modification date and the size of the
     * file, a modified file is detected again.
     */
    private static final Map<String, Boolean> VALID_CACHE = newCache();

    /**
     * Results of {@link #detectCharset(File)}.
     */
    private static final Map<String, Charset> DETECT_CACHE = newCache();

    private static <V> Map<String, V> newCache() {
        return Collections.synchronizedMap(new LinkedHashMap<String, V>(16, 0.75f, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
                return size() > CACHE_SIZE;
            }
        });
    }

    private static String cacheKey(File file, Charset charset) {
        return file.getAbsolutePath() + '|' + file.lastModified() + '|' + file.length() + '|'
            + (charset == null ? "" : charset.name());
    }

    /**
     * Detects character encoding from data source between UTF-8 and LATIN1.
     *
     * Warning :
     * <ul>
     * <li>Returns UTF-8 first if characters are valid by UTF-8</li>
     * <li>Consequently, can return UTF-8 if text is ISO_8859_1 without accent</li>
     * <li>Returns UTF-8 if the file is mostly valid UTF-8 (more valid multi-byte
     * sequences than invalid ones). A few invalid sequences (ex : text truncated in
     * the middle of a multi-byte character to fit a DBF field length) must not lead
     * to read the whole file as LATIN1 (double encoding of every accented
     * character).</li>
     * </ul>
     *
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Charset detectCharset(File file) throws IOException {
        String key = cacheKey(file, null);
        Charset cached = DETECT_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        Charset charset = doDetectCharset(file);
        DETECT_CACHE.put(key, charset);
        return charset;
    }

    private static Charset doDetectCharset(File file) throws IOException {
        log.debug(MARKER, "detectCharset('{}')...", file.getAbsolutePath());
        Utf8Stats stats = computeUtf8Stats(file);
        if (stats.isMostlyValid()) {
            log.debug(
                MARKER, "detectCharset('{}') : UTF-8 ({} valid / {} invalid multi-byte sequence(s))",
                file.getAbsolutePath(), stats.validSequences, stats.invalidSequences
            );
            return StandardCharsets.UTF_8;
        } else {
            log.debug(
                MARKER, "detectCharset('{}') : LATIN-1 ({} valid / {} invalid multi-byte sequence(s))",
                file.getAbsolutePath(), stats.validSequences, stats.invalidSequences
            );
            return StandardCharsets.ISO_8859_1;
        }
    }

    /**
     * Counts of UTF-8 multi-byte sequences in a file.
     */
    static class Utf8Stats {
        long validSequences = 0;
        long invalidSequences = 0;

        /**
         * True if there is no invalid sequence or if valid multi-byte sequences are
         * more frequent than invalid ones.
         *
         * Note that a LATIN1 text with accents contains almost no valid UTF-8
         * multi-byte sequence (an accented char followed by an ASCII char is invalid).
         */
        boolean isMostlyValid() {
            return invalidSequences == 0 || validSequences > invalidSequences;
        }
    }

    /**
     * Counts valid and invalid UTF-8 multi-byte sequences in a file (streaming).
     *
     * @param file
     * @return
     * @throws IOException
     */
    static Utf8Stats computeUtf8Stats(File file) throws IOException {
        Utf8Stats stats = new Utf8Stats();
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[8192];
            // remaining continuation bytes expected for the current sequence
            int remaining = 0;
            int n;
            while ((n = in.read(buffer)) != -1) {
                for (int i = 0; i < n; i++) {
                    int b = buffer[i] & 0xFF;
                    if (remaining > 0) {
                        if ((b & 0xC0) == 0x80) {
                            remaining--;
                            if (remaining == 0) {
                                stats.validSequences++;
                            }
                            continue;
                        }
                        // incomplete sequence, current byte starts a new one
                        stats.invalidSequences++;
                        remaining = 0;
                    }
                    if (b < 0x80) {
                        continue;
                    } else if (b >= 0xC2 && b <= 0xDF) {
                        remaining = 1;
                    } else if (b >= 0xE0 && b <= 0xEF) {
                        remaining = 2;
                    } else if (b >= 0xF0 && b <= 0xF4) {
                        remaining = 3;
                    } else {
                        // unexpected continuation byte or invalid lead byte
                        stats.invalidSequences++;
                    }
                }
            }
            if (remaining > 0) {
                stats.invalidSequences++;
            }
        }
        return stats;
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
        String key = cacheKey(file, charset);
        Boolean cached = VALID_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        boolean valid = doIsValidCharset(file, charset);
        VALID_CACHE.put(key, valid);
        return valid;
    }

    private static boolean doIsValidCharset(File file, Charset charset) {
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
            // read by blocks (readLine would keep a whole file without line breaks in
            // memory)
            char[] buffer = new char[8192];
            while (in.read(buffer) != -1) {
                // decoding errors are thrown by the decoder
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
