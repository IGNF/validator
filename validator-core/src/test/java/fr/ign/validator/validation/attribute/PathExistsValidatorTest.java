package fr.ign.validator.validation.attribute;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.tools.ResourceHelper;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.model.type.PathType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class PathExistsValidatorTest {

    private Context context;
    private InMemoryReportBuilder report;
    private PathExistsValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new PathExistsValidator();

        context = new Context();
        File currentDirectory = ResourceHelper.getResourceFile(getClass(), "/");
        context.setCurrentDirectory(currentDirectory);

        report = new InMemoryReportBuilder();
        context.setReportBuilder(report);
    }

    @Test
    public void testExisting() {
        PathType type = new PathType();
        Attribute<File> attribute = new Attribute<File>(type, new File("jexiste.txt"));
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testExistingWithFragment() {
        PathType type = new PathType();
        Attribute<File> attribute = new Attribute<File>(type, new File("jexiste.txt#page=12"));
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testDoesntExists() {
        PathType type = new PathType();
        Attribute<File> attribute = new Attribute<File>(type, new File("je-nexiste-pas.txt"));
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());
    }

}
