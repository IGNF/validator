package fr.ign.validator.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.exception.ModelNotFoundException;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.EmbeddedTableModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.model.file.MultiTableModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.tools.ResourceHelper;

/**
 * 
 * JsonReaderModel regress tests based on samples in /resources/config-json/
 * 
 * @author MBorne
 *
 */
public class JsonModelReaderTest {

    private ModelReader modelLoader;

    @Before
    public void setUp() throws Exception {
        modelLoader = new JsonModelReader();
    }

    /**
     * Ensure that ModelNotFoundException are thrown if DocumentModel file doesn't
     * exists
     */
    @Test(expected = ModelNotFoundException.class)
    public void testLoadDocumentModelFileNotFound() {
        File configDir = ResourceHelper.getResourceFile(getClass(), "/config-json");
        File documentModelPath = new File(configDir, "/not-found/files.json");
        modelLoader.loadDocumentModel(documentModelPath);
    }

    /**
     * Ensure that ModelNotFoundException are thrown if DocumentModel URL doesn't
     * exists
     * 
     * @throws MalformedURLException
     */
    @Test(expected = ModelNotFoundException.class)
    public void testLoadDocumentModelUrlNotFound() throws MalformedURLException {
        URL documentModelUrl = new URL("https://example.local/not-found/files.json");
        modelLoader.loadDocumentModel(documentModelUrl);
    }

    /**
     * Read config-json/adresse and performs regress test
     */
    @Test
    public void testLoadDocumentModelAdresse() {
        File documentModelPath = ResourceHelper.getResourceFile(getClass(), "/config-json/adresse/files.json");
        DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);
        assertIsValid(documentModel);

        Assert.assertEquals("adresse", documentModel.getName());
        Assert.assertEquals(1, documentModel.getFileModels().size());

        /* perform checks on FileModel "adresse" */
        FileModel fileModel = documentModel.getFileModelByName("ADRESSE");
        Assert.assertNotNull(fileModel);
        Assert.assertEquals("ADRESSE(_+[0-9])?", fileModel.getPath());

        /* perform checks on FeatureType "adresse" */
        Assert.assertTrue(fileModel instanceof TableModel);
        FeatureType featureType = ((TableModel) fileModel).getFeatureType();
        Assert.assertNotNull(featureType);
        assertExceptedFeatureTypeAdresse(featureType);
    }

    @Test
    public void testLoadFeatureTypeAdresse() {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/config-json/adresse/table-models/ADRESSE.json");
        FeatureType featureType = modelLoader.loadFeatureType(srcFile);
        assertExceptedFeatureTypeAdresse(featureType);
    }

    /**
     * Check FeatureType for /config-json/adresse/types/ADRESSE.json
     * 
     * @param featureType
     */
    private void assertExceptedFeatureTypeAdresse(FeatureType featureType) {
        Assert.assertEquals("ADRESSE", featureType.getName());
        Assert.assertEquals("Table d'identifiant et adresse", featureType.getDescription());
        Assert.assertEquals(3, featureType.getAttributeCount());
        Assert.assertFalse(featureType.isSpatial());

        int index = 0;
        {
            AttributeType<?> attribute = featureType.getAttribute(index++);
            Assert.assertEquals("ID", attribute.getName());
            Assert.assertEquals("Integer", attribute.getTypeName());
            Assert.assertEquals("Identifiant unique", attribute.getDescription());
            Assert.assertTrue(
                attribute.getConstraints().isRequired()
            );
            Assert.assertTrue(
                attribute.getConstraints().isUnique()
            );
        }

        {
            AttributeType<?> attribute = featureType.getAttribute(index++);
            Assert.assertEquals("ADRESSE", attribute.getName());
            Assert.assertEquals("String", attribute.getTypeName());
            Assert.assertEquals("Le libellé de l'adresse", attribute.getDescription());
            Assert.assertFalse(
                attribute.getConstraints().isRequired()
            );
            Assert.assertFalse(
                attribute.getConstraints().isUnique()
            );
            Assert.assertEquals(
                Integer.valueOf(5),
                attribute.getConstraints().getMinLength()
            );
            Assert.assertEquals(
                Integer.valueOf(254),
                attribute.getConstraints().getMaxLength()
            );
        }

        {
            AttributeType<?> attribute = featureType.getAttribute(index++);
            Assert.assertEquals("HOMEPAGE", attribute.getName());
            Assert.assertEquals("Url", attribute.getTypeName());
            Assert.assertEquals("Page de présentation", attribute.getDescription());
            Assert.assertFalse(
                attribute.getConstraints().isRequired()
            );
            Assert.assertFalse(
                attribute.getConstraints().isUnique()
            );
        }
    }

    /**
     * Read config-json/pcrs-2.0 and performs regress test
     */
    @Test
    public void testLoadDocumentModelPCRS() {
        File documentModelPath = ResourceHelper.getResourceFile(getClass(), "/config-json/pcrs-2.0/files.json");
        DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);
        assertIsValid(documentModel);

        Assert.assertEquals(2, documentModel.getFileModels().size());

        // check METADONNEES
        {
            FileModel fileModel = documentModel.getFileModelByName("METADONNEES");
            assertNotNull(fileModel);
            assertTrue(fileModel instanceof MetadataModel);
        }

        // check DONNEES
        {
            FileModel fileModel = documentModel.getFileModelByName("DONNEES");
            assertNotNull(fileModel);
            assertTrue(fileModel instanceof MultiTableModel);
            Assert.assertEquals(
                "https://cnigfr.github.io/PCRS/schemas/CNIG_PCRS_v2.0.xsd",
                fileModel.getXsdSchema().toString()
            );

            // check embedded tables
            MultiTableModel multiTableModel = (MultiTableModel) fileModel;
            List<EmbeddedTableModel> tableModels = multiTableModel.getTableModels();
            Assert.assertEquals(17, tableModels.size());
        }
    }

    /**
     * Performs basic consistency checks on DocumentModel
     * 
     * TODO avoid code duplication with XmlModelReaderTest
     * 
     * @param documentModel
     */
    private void assertIsValid(DocumentModel documentModel) {
        List<FileModel> fileModels = documentModel.getFileModels();
        for (FileModel fileModel : fileModels) {
            Assert.assertNotNull(fileModel.getName());
            Assert.assertNotNull(fileModel.getMandatory());
            if (fileModel instanceof TableModel) {
                FeatureType featureType = ((TableModel) fileModel).getFeatureType();
                Assert.assertNotNull(featureType);
                assertIsValid(featureType);
            }
        }
    }

    /**
     * Performs basic consistency checks on FeatureType
     * 
     * @param documentModel
     */
    private void assertIsValid(FeatureType featureType) {
        Assert.assertFalse(StringUtils.isEmpty(featureType.getName()));
        Assert.assertFalse(
            "featureType must have at least one attribute",
            featureType.getAttributeCount() == 0
        );
    }

}
