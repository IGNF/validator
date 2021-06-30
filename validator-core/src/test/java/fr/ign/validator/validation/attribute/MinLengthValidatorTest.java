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

public class MinLengthValidatorTest {

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
        MinLengthValidator<String> validator = new MinLengthValidator<>();
        StringType type = new StringType();
        Attribute<String> attribute = new Attribute<String>(type, "abc");
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testStringTypeMoreThanLimit() {
        MinLengthValidator<String> validator = new MinLengthValidator<>();
        StringType type = new StringType();
        type.getConstraints().setMinLength(2);
        Attribute<String> attribute = new Attribute<String>(type, "abc");
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testStringTypeEqualLimit() {
        MinLengthValidator<String> validator = new MinLengthValidator<>();
        StringType type = new StringType();
        type.getConstraints().setMinLength(5);
        Attribute<String> attribute = new Attribute<String>(type, "abcde");
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testStringTypeLessThanLimit() {
        MinLengthValidator<String> validator = new MinLengthValidator<>();
        StringType type = new StringType();
        type.getConstraints().setMinLength(5);
        Attribute<String> attribute = new Attribute<String>(type, "abcd");
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());

        ValidatorError error = report.getErrors().get(0);
        Assert.assertEquals(
            "La taille de l'attribut (4) est en dessous la taille minimale autorisée (5).",
            error.getMessage()
        );
    }

    @Test
    public void testUrlTypeNull() {
        MinLengthValidator<URL> validator = new MinLengthValidator<>();
        UrlType type = new UrlType();
        type.getConstraints().setMinLength(10);
        Attribute<URL> attribute = new Attribute<URL>(type, null);
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testUrlTypeMoreThanLimit() throws MalformedURLException {
        MinLengthValidator<URL> validator = new MinLengthValidator<>();
        UrlType type = new UrlType();
        type.getConstraints().setMinLength(50);
        Attribute<URL> attribute = new Attribute<URL>(type, new URL("https://example.org/something"));
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());

        ValidatorError error = report.getErrors().get(0);
        Assert.assertEquals(
            "La taille de l'attribut (29) est en dessous la taille minimale autorisée (50).",
            error.getMessage()
        );
    }

    @Test
    public void testPathTypeNull() {
        MinLengthValidator<File> validator = new MinLengthValidator<>();
        PathType type = new PathType();
        type.getConstraints().setMinLength(5);
        Attribute<File> attribute = new Attribute<File>(type, null);
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testPathTypeLessThanLimit() {
        MinLengthValidator<File> validator = new MinLengthValidator<>();
        PathType type = new PathType();
        type.getConstraints().setMinLength(10);
        Attribute<File> attribute = new Attribute<File>(type, new File("abcd.pdf"));
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());

        ValidatorError error = report.getErrors().get(0);
        Assert.assertEquals(
            "La taille de l'attribut (8) est en dessous la taille minimale autorisée (10).",
            error.getMessage()
        );
    }

    @Test
    public void testFilenameTypeNull() {
        MinLengthValidator<File> validator = new MinLengthValidator<>();
        FilenameType type = new FilenameType();
        type.getConstraints().setMinLength(5);
        Attribute<File> attribute = new Attribute<File>(type, null);
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testFilenameTypeLessThanLimit() {
        MinLengthValidator<File> validator = new MinLengthValidator<>();
        FilenameType type = new FilenameType();
        type.getConstraints().setMinLength(10);
        Attribute<File> attribute = new Attribute<File>(type, new File("abcd.pdf"));
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());

        ValidatorError error = report.getErrors().get(0);
        Assert.assertEquals(
            "La taille de l'attribut (8) est en dessous la taille minimale autorisée (10).",
            error.getMessage()
        );
    }

}
