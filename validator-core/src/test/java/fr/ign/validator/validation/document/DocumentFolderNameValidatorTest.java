package fr.ign.validator.validation.document;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.tools.ResourceHelper;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.report.InMemoryReportBuilder;

public class DocumentFolderNameValidatorTest {

    private Context context;
    private InMemoryReportBuilder report;
    private DocumentFolderNameValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new DocumentFolderNameValidator();

        context = new Context();
        File currentDirectory = ResourceHelper.getResourceFile(getClass(), "/config-xml/geofla");
        context.setCurrentDirectory(currentDirectory);

        report = new InMemoryReportBuilder();
        context.setReportBuilder(report);
    }

    @Test
    public void testNoRegexp() {
        DocumentModel documentModel = new DocumentModel();
        File documentPath = new File("/my/path/to/130009970_PM1_59_20160623");
        Document document = new Document(documentModel, documentPath);
        validator.validate(context, document);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testGoodRegexp() {
        DocumentModel documentModel = new DocumentModel();
        documentModel.getConstraints().setFolderName(".*_PM1_((0)?2A|(0)?2B|[0-9]{2,3})_[0-9]{8}");

        File documentPath = new File("/my/path/to/130009970_PM1_59_20160623");
        Document document = new Document(documentModel, documentPath);
        validator.validate(context, document);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testGoodRegexpTrailingSlash() {
        DocumentModel documentModel = new DocumentModel();
        documentModel.getConstraints().setFolderName(".*_PM1_((0)?2A|(0)?2B|[0-9]{2,3})_[0-9]{8}");

        File documentPath = new File("/my/path/to/130009970_PM1_59_20160623/");
        Document document = new Document(documentModel, documentPath);
        validator.validate(context, document);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testCaseInsensitive() {
        DocumentModel documentModel = new DocumentModel();
        documentModel.getConstraints().setFolderName(".*_SCOT");

        File documentPath = new File("/my/path/to/123456789_scot/");
        Document document = new Document(documentModel, documentPath);
        validator.validate(context, document);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testBadRegexp() {
        DocumentModel documentModel = new DocumentModel();
        documentModel.getConstraints().setFolderName(".*_PM1_((0)?2A|(0)?2B|[0-9]{2,3})_[0-9]{8}");

        File documentPath = new File("/my/path/to/130009970_PM2_59_20160623");
        Document document = new Document(documentModel, documentPath);
        validator.validate(context, document);
        Assert.assertEquals(1, report.countErrors());
    }

}
