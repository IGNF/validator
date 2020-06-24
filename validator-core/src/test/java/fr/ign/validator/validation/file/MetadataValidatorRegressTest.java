package fr.ign.validator.validation.file;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.data.file.MetadataFile;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.tools.ResourceHelper;

/**
 * High level regression test
 * 
 * @author MBorne
 *
 */
public class MetadataValidatorRegressTest {

    private Context context;
    private InMemoryReportBuilder report;

    @Before
    public void setUp() throws Exception {
        context = new Context();
        File currentDirectory = ResourceHelper.getResourceFile(getClass(), "/metadata");
        context.setCurrentDirectory(currentDirectory);

        report = new InMemoryReportBuilder();
        context.setReportBuilder(report);
    }

    @Test
    public void test01() {
        MetadataModel fileModel = new MetadataModel();
        File filePath = ResourceHelper.getResourceFile(getClass(), "/metadata/01.xml");
        MetadataFile documentFile = fileModel.createDocumentFile(filePath);
        documentFile.validate(context);
        assertEquals(0, report.countErrors());
    }

    @Test
    public void test02() {
        MetadataModel fileModel = new MetadataModel();
        File filePath = ResourceHelper.getResourceFile(getClass(), "/metadata/02.xml");

        MetadataFile documentFile = fileModel.createDocumentFile(filePath);
        documentFile.validate(context);

        assertEquals(14, report.countErrors());
        int i = 0;
        assertEquals(CoreErrorCodes.METADATA_FILEIDENTIFIER_NOT_FOUND, report.getErrors().get(i++).getCode());
        assertEquals(CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report.getErrors().get(i++).getCode());
        assertEquals(CoreErrorCodes.METADATA_LOCATOR_URL_NOT_FOUND, report.getErrors().get(i++).getCode());
        assertEquals(CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report.getErrors().get(i++).getCode());
        assertEquals(CoreErrorCodes.METADATA_LOCATOR_URL_NOT_FOUND, report.getErrors().get(i++).getCode());
        assertEquals(CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report.getErrors().get(i++).getCode());
        assertEquals(CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report.getErrors().get(i++).getCode());
        assertEquals(CoreErrorCodes.METADATA_LOCATOR_URL_NOT_FOUND, report.getErrors().get(i++).getCode());
        assertEquals(CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report.getErrors().get(i++).getCode());
        assertEquals(CoreErrorCodes.METADATA_LOCATOR_URL_NOT_FOUND, report.getErrors().get(i++).getCode());
        assertEquals(CoreErrorCodes.METADATA_IDENTIFIER_NOT_FOUND, report.getErrors().get(i++).getCode());
        assertEquals(CoreErrorCodes.METADATA_CHARACTERSET_NOT_FOUND, report.getErrors().get(i++).getCode());
        assertEquals(
            CoreErrorCodes.METADATA_SPATIALREPRESENTATIONTYPE_NOT_FOUND, report.getErrors().get(i++).getCode()
        );
        assertEquals(CoreErrorCodes.METADATA_SPECIFICATIONS_EMPTY, report.getErrors().get(i++).getCode());
    }

    @Test
    public void test03() {
        MetadataModel fileModel = new MetadataModel();
        File filePath = ResourceHelper.getResourceFile(getClass(), "/metadata/03.xml");

        MetadataFile documentFile = fileModel.createDocumentFile(filePath);
        documentFile.validate(context);

        assertEquals(3, report.countErrors());

        int i = 0;
        assertEquals(
            CoreErrorCodes.METADATA_IDENTIFIER_NOT_FOUND,
            report.getErrors().get(i++).getCode()
        );
        assertEquals(
            CoreErrorCodes.METADATA_SPATIALRESOLUTIONS_EMPTY,
            report.getErrors().get(i++).getCode()
        );
        assertEquals(
            CoreErrorCodes.METADATA_SPECIFICATIONS_EMPTY,
            report.getErrors().get(i++).getCode()
        );
    }

    @Test
    public void test04() {
        MetadataModel fileModel = new MetadataModel();
        File filePath = ResourceHelper.getResourceFile(getClass(), "/metadata/04.xml");

        MetadataFile documentFile = fileModel.createDocumentFile(filePath);
        documentFile.validate(context);

        assertEquals(2, report.countErrors());
        int i = 0;
        assertEquals(
            CoreErrorCodes.METADATA_SPATIALRESOLUTIONS_EMPTY,
            report.getErrors().get(i++).getCode()
        );
        assertEquals(
            CoreErrorCodes.METADATA_SPECIFICATIONS_EMPTY,
            report.getErrors().get(i++).getCode()
        );
    }

    @Test
    public void test05() {
        MetadataModel fileModel = new MetadataModel();
        File filePath = ResourceHelper.getResourceFile(getClass(), "/metadata/05.xml");

        MetadataFile documentFile = fileModel.createDocumentFile(filePath);
        documentFile.validate(context);

        assertEquals(2, report.countErrors());
        int i = 0;
        assertEquals(
            CoreErrorCodes.METADATA_SPATIALREPRESENTATIONTYPE_NOT_FOUND,
            report.getErrors().get(i++).getCode()
        );
        assertEquals(
            CoreErrorCodes.METADATA_SPATIALRESOLUTIONS_EMPTY,
            report.getErrors().get(i++).getCode()
        );
    }
}
