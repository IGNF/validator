package fr.ign.validator.tools;

import static org.junit.Assert.*;
import org.junit.Test;

public class CharactersTest {

    @Test
    public void testToHexa() {
        assertEquals("\\u00ff", Characters.toHexa(255));
    }

    @Test
    public void testToUri() {
        assertEquals("http://www.fileformat.info/info/unicode/char/0fb9/index.htm", Characters.toURI(4025));
    }

}
