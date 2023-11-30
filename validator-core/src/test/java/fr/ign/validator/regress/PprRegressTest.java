package fr.ign.validator.regress;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.database.Database;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.io.JsonModelReader;
import fr.ign.validator.io.ModelReader;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.file.MultiTableModel;
import fr.ign.validator.report.InMemoryReportBuilder;

/**
 * 
 * Regress test for some GpU documents.
 * 
 * @author MBorne
 *
 */
public class PprRegressTest {

    public static final Logger log = LogManager.getRootLogger();

    protected InMemoryReportBuilder report;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        report = new InMemoryReportBuilder();
    }

    /**
     * Create validation context
     *
     * @param documentPath
     * @return
     * @throws Exception
     */
    private Context createContext(File documentPath) throws Exception {
        Context context = new Context();
        context.setReportBuilder(report);
        context.setProjection("EPSG:2154");
        File validationDirectory = new File(documentPath.getParentFile(), "validation");
        context.setValidationDirectory(validationDirectory);

        return context;
    }

    private DocumentModel getDocumentModel() throws Exception {
        File documentModelPath = new File(
            getClass().getResource("/config-json/cnig_PPR_2023/document.json").getPath()
        );
        ModelReader loader = new JsonModelReader();
        DocumentModel documentModel = loader.loadDocumentModel(documentModelPath);
        documentModel.setName("cnig_PPR_2023");
        return documentModel;
    }

    private File getSampleDocument(String documentName, TemporaryFolder folder) throws IOException {
        File sourcePath = new File(getClass().getResource("/geopackage/" + documentName).getPath());
        File documentPath = folder.newFolder(documentName);
        FileUtils.copyDirectory(sourcePath, documentPath);
        return documentPath;
    }

    @Test
    public void testModel() throws Exception {
        DocumentModel documentModel = getDocumentModel();

        assertEquals("cnig_PPR_2023", documentModel.getName());
        assertEquals(1, documentModel.getFileModels().size());

        MultiTableModel data = (MultiTableModel) documentModel.getFileModelByName("DONNEES");
        assertEquals(5, data.getTableModels().size());

        FeatureType featureType = data.getTableModels().get(0).getFeatureType();

        assertEquals("typeppr_codegaspar_perimetre_s", featureType.getName());
        assertEquals(5, featureType.getAttributes().size());

        @SuppressWarnings("unchecked")
        AttributeType<String> attributeType = (AttributeType<String>) featureType.getAttribute(1);
        assertEquals("codeprocedure", attributeType.getName());
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testValidDREAL69() throws Exception {
        /*
         * validate
         */
        DocumentModel documentModel = getDocumentModel();
        File documentPath = getSampleDocument("pprt_69dreal20090005", folder);
        Context context = createContext(documentPath);
        Document document = new Document(documentModel, documentPath);

        document.validate(context);

        /*
         * check basic points
         */
        Assert.assertEquals(StandardCharsets.UTF_8, context.getEncoding());
        Assert.assertEquals("pprt_69dreal20090005", document.getDocumentName());

        /*
         * check errors
         */

        assertEquals(25, report.getErrorsByLevel(ErrorLevel.ERROR).size());
        assertEquals(0, report.getErrorsByLevel(ErrorLevel.WARNING).size());

        /*
         * Database checks
         */
        Database database = Database.createDatabase(context, false);
        assertEquals(1, database.getCount("typeppr_codegaspar_perimetre_s"));
        assertEquals(1, database.getCount("typeppr_codegaspar_procedure"));
        assertEquals(1, database.getCount("typeppr_codegaspar_referenceinternet"));
        assertEquals(10, database.getCount("typeppr_codegaspar_zonereglementairefoncier_s"));
        assertEquals(25, database.getCount("typeppr_codegaspar_zonereglementaireurba_s"));
    }

}
