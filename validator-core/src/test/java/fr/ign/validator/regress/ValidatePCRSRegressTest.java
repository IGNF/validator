package fr.ign.validator.regress;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.io.JsonModelReader;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.tools.Networking;
import fr.ign.validator.tools.ResourceHelper;

/**
 *
 * Regress test with sample document
 *
 * @author MBorne
 *
 */
public class ValidatePCRSRegressTest {

    protected Context context;
    protected InMemoryReportBuilder report;
    protected DocumentModel documentModel;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        context = new Context();
        /* create report */
        report = new InMemoryReportBuilder();
        context.setReportBuilder(report);
        context.setNormalizeEnabled(true);

        /* load document model */
        {
            JsonModelReader modelReader = new JsonModelReader();
            documentModel = modelReader.loadDocumentModel(
                ResourceHelper.getResourceFile(getClass(), "/config-json/pcrs-2.0/files.json")
            );
        }

        /* to access XML schema */
        Networking.configureHttpClient();
    }

    @Test
    public void testLyon01() throws Exception {
        File documentPath = getSampleDocument("pcrs-lyon-01");

        Document document = new Document(documentModel, documentPath);
        File validationDirectory = new File(documentPath.getParentFile(), "validation");
        validationDirectory.mkdirs();
        context.setValidationDirectory(validationDirectory);

        document.validate(context);

        assertEquals(0, report.getErrorsByLevel(ErrorLevel.ERROR).size());
        assertEquals(0, report.getErrorsByLevel(ErrorLevel.WARNING).size());
    }

    /**
     * Get sample document copied on
     * 
     * @param documentName
     * @return
     * @throws IOException
     */
    public File getSampleDocument(String documentName) throws IOException {
        File sourcePath = ResourceHelper.getResourceFile(
            ValidatePCRSRegressTest.class,
            "/documents/" + documentName
        );
        File documentPath = folder.newFolder(documentName);
        FileUtils.copyDirectory(sourcePath, documentPath);
        return documentPath;
    }
}
