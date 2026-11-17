package fr.ign.validator.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class ResponsiblePartyTest {

    private static boolean mailIsValid(String mail) {
        ResponsibleParty party = new ResponsibleParty();
        party.setElectronicMailAddress(mail);
        return party.mailIsValid();
    }

    @Test
    public void testNullMail() {
        assertFalse(mailIsValid(null));
    }

    /**
     * Same result as the previous implementation (".+@.+\\..+" with find())
     */
    @Test
    public void testSameAsRegex() {
        Pattern previous = Pattern.compile(".+@.+\\..+");
        for (String mail : Arrays.asList(
            "contact@ign.fr", "a@b.c", "a@b.", "a@.c", "@b.c", "a@bc", "abc", "", "a.b@c", "a.b@c.d",
            "@a@b.c", "a@@b.c", "a@b..c", "prenom.nom@sous.domaine.fr", "a@b.c.", "x @ y . z"
        )) {
            assertEquals(mail, previous.matcher(mail).find(), mailIsValid(mail));
        }
        assertFalse(mailIsValid("non_renseigne@exemple.fr"));
    }

    /**
     * the previous regex took ~23 s for 4000 '@'
     */
    @Test(timeout = 2000)
    public void testLongValue() {
        assertFalse(mailIsValid(StringUtils.repeat('@', 100000)));
        assertTrue(mailIsValid("a" + StringUtils.repeat('@', 100000) + "b.c"));
    }

}
