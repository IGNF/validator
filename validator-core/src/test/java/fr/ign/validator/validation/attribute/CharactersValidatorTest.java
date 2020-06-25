package fr.ign.validator.validation.attribute;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.tools.ResourceHelper;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.string.StringFixer;

public class CharactersValidatorTest {

    private Context context;
    private InMemoryReportBuilder report;
    private CharactersValidator<String> validator;

    @Before
    public void setUp() throws Exception {
        validator = new CharactersValidator<String>();

        context = new Context();
        context.setStringFixer(StringFixer.createFullStringFixer(StandardCharsets.ISO_8859_1));
        File currentDirectory = ResourceHelper.getResourceFile(getClass(), "/config-xml/geofla");
        context.setCurrentDirectory(currentDirectory);

        report = new InMemoryReportBuilder();
        context.setReportBuilder(report);
    }

    @Test
    public void testAllowedCharacters() {
        StringType type = new StringType();
        Attribute<String> attribute = type.newAttribute(
            "une chaîne accentuée avec des contrôles autorisés : \r\n autre ligne"
        );
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testSimplifyCharacter() {
        StringType type = new StringType();
        Attribute<String> attribute = type.newAttribute("a non latin character : œ");
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());
        ValidatorError error = report.getErrors().get(0);
        Assert.assertEquals(CoreErrorCodes.ATTRIBUTE_CHARACTERS_REPLACED, error.getCode());
        Assert.assertEquals(
            "La valeur (‘a non latin character : œ’) sera remplacée par (‘a non latin character : oe’) pour l’intégration des données.",
            error.getMessage()
        );
    }

    @Test
    public void testNonLatin1Characters() {
        StringType type = new StringType();
        Attribute<String> attribute = type.newAttribute("a non latin character : ᆦ");
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());
        ValidatorError error = report.getErrors().get(0);
        Assert.assertEquals(CoreErrorCodes.ATTRIBUTE_CHARACTERS_ILLEGAL, error.getCode());
        Assert.assertEquals(
            "La valeur (‘a non latin character : ᆦ’) contient des caractères interdits qui seront échappés (‘a non latin character : \\u11a6’).",
            error.getMessage()
        );
    }

    @Test
    public void testForbiddenControl() {
        StringType type = new StringType();
        Attribute<String> attribute = type.newAttribute("a forbidden control : \u0001");
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());
        ValidatorError error = report.getErrors().get(0);
        Assert.assertEquals(CoreErrorCodes.ATTRIBUTE_CHARACTERS_ILLEGAL, error.getCode());
    }
}
