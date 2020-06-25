package fr.ign.validator.model.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class PathTypeTest extends AbstractTypeTest<File> {

    public PathTypeTest() {
        super(new PathType());
    }

    @Test
    public void testBindWithoutFragment() {
        String name = new String("a-file.txt");
        File binded = type.bind(name);
        assertEquals(name, type.format(binded));
    }

    @Test
    public void testBindWithoutFragmentAndIllegalChars() {
        char c = 0x0092;
        String name = new String("a-fil" + c + "e.txt");
        boolean thrown = false;
        try {
            type.bind(name);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue("bind should throw (illegal characters)", thrown);
    }

    @Test
    public void testBindWithIllegalCharsInFragment() {
        char c = 0x0092;
        String name = new String("a-file.txt#page=12" + c);
        boolean thrown = false;
        try {
            type.bind(name);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue("bind should throw (illegal characters)", thrown);
    }

    @Test
    public void testBindFormatWithFragment() {
        String name = new String("a-file.txt#page=12");
        File binded = type.bind(name);
        assertEquals(name, type.format(binded));
    }

}
