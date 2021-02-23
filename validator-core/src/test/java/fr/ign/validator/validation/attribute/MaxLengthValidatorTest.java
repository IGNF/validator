package fr.ign.validator.validation.attribute;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.type.FilenameType;
import fr.ign.validator.model.type.PathType;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.model.type.UrlType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class MaxLengthValidatorTest {

    private Context context;

    private InMemoryReportBuilder report;

    @Before
    public void setUp() throws Exception {
        context = new Context();
        report = new InMemoryReportBuilder();
        context.setReportBuilder(report);
    }

    @Test
    public void testStringTypeNoLimit() {
        MaxLengthValidator<String> validator = new MaxLengthValidator<>();
        StringType type = new StringType();
        Attribute<String> attribute = new Attribute<String>(type, "abc");
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testStringTypeLessThanLimit() {
        MaxLengthValidator<String> validator = new MaxLengthValidator<>();
        StringType type = new StringType();
        type.getConstraints().setMaxLength(5);
        Attribute<String> attribute = new Attribute<String>(type, "abc");
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testStringTypeEqualLimit() {
        MaxLengthValidator<String> validator = new MaxLengthValidator<>();
        StringType type = new StringType();
        type.getConstraints().setMaxLength(5);
        Attribute<String> attribute = new Attribute<String>(type, "abcde");
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testStringTypeMoreThanLimit() {
        MaxLengthValidator<String> validator = new MaxLengthValidator<>();
        StringType type = new StringType();
        type.getConstraints().setMaxLength(5);
        Attribute<String> attribute = new Attribute<String>(type, "abcdef");
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());

        ValidatorError error = report.getErrors().get(0);
        Assert.assertEquals("La taille de l'attribut (6) dépasse la taille limite autorisée (5).", error.getMessage());
    }

    @Test
    public void testUrlTypeNull() {
        MaxLengthValidator<URL> validator = new MaxLengthValidator<>();
        UrlType type = new UrlType();
        type.getConstraints().setMaxLength(10);
        Attribute<URL> attribute = new Attribute<URL>(type, null);
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testUrlTypeMoreThanLimit() throws MalformedURLException {
        MaxLengthValidator<URL> validator = new MaxLengthValidator<>();
        UrlType type = new UrlType();
        type.getConstraints().setMaxLength(10);
        Attribute<URL> attribute = new Attribute<URL>(type, new URL("https://example.org/something"));
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());

        ValidatorError error = report.getErrors().get(0);
        Assert.assertEquals(
            "La taille de l'attribut (29) dépasse la taille limite autorisée (10).", error.getMessage()
        );
    }

    @Test
    public void testPathTypeNull() {
        MaxLengthValidator<File> validator = new MaxLengthValidator<>();
        PathType type = new PathType();
        type.getConstraints().setMaxLength(5);
        Attribute<File> attribute = new Attribute<File>(type, null);
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testPathTypeMoreThanLimit() {
        MaxLengthValidator<File> validator = new MaxLengthValidator<>();
        PathType type = new PathType();
        type.getConstraints().setMaxLength(5);
        Attribute<File> attribute = new Attribute<File>(type, new File("abcd.pdf"));
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());

        ValidatorError error = report.getErrors().get(0);
        Assert.assertEquals("La taille de l'attribut (8) dépasse la taille limite autorisée (5).", error.getMessage());
    }

    @Test
    public void testFilenameTypeNull() {
        MaxLengthValidator<File> validator = new MaxLengthValidator<>();
        FilenameType type = new FilenameType();
        type.getConstraints().setMaxLength(5);
        Attribute<File> attribute = new Attribute<File>(type, null);
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testFilenameTypeMoreThanLimit() {
        MaxLengthValidator<File> validator = new MaxLengthValidator<>();
        FilenameType type = new FilenameType();
        type.getConstraints().setMaxLength(5);
        Attribute<File> attribute = new Attribute<File>(type, new File("abcd.pdf"));
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());

        ValidatorError error = report.getErrors().get(0);
        Assert.assertEquals("La taille de l'attribut (8) dépasse la taille limite autorisée (5).", error.getMessage());
    }

}
