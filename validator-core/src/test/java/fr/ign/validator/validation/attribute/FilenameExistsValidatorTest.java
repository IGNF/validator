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

public class FilenameExistsValidatorTest {

    private Context context;
    private InMemoryReportBuilder report;
    private FilenameExistsValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new FilenameExistsValidator();

        context = new Context();
        File currentDirectory = ResourceHelper.getResourceFile(getClass(), "/geofla");
        context.setCurrentDirectory(currentDirectory);

        report = new InMemoryReportBuilder();
        context.setReportBuilder(report);
    }

    @Test
    public void testExisting() {
        PathType type = new PathType();
        Attribute<File> attribute = new Attribute<File>(type, new File("files.xml"));
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testExistingWithFragment() {
        PathType type = new PathType();
        Attribute<File> attribute = new Attribute<File>(type, new File("files.xml#page=15"));
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testExistingInSubdirectory() {
        PathType type = new PathType();
        Attribute<File> attribute = new Attribute<File>(type, new File("COMMUNE.xml"));
        validator.validate(context, attribute);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testExistingInSubdirectoryWithFragment() {
        PathType type = new PathType();
        Attribute<File> attribute = new Attribute<File>(type, new File("COMMUNE.xml#page=15"));
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

    @Test
    public void testDoesntExistsIllegalCharacters() {
        PathType type = new PathType();
        String illegal = new String(new int[] {
            0x0092
        }, 0, 1);
        Attribute<File> attribute = new Attribute<File>(type, new File("je-nexiste-pas.txt#page=15" + illegal));
        validator.validate(context, attribute);
        Assert.assertEquals(1, report.countErrors());
    }

}
