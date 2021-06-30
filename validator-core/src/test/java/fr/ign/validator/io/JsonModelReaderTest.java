package fr.ign.validator.io;

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
    @Test
    public void testLoadDocumentModelFileNotFound() {
        File configDir = ResourceHelper.getResourceFile(getClass(), "/config-json");
        File documentModelPath = new File(configDir, "/not-found/files.json");
        boolean thrown = false;
        try {
            modelLoader.loadDocumentModel(documentModelPath);
        } catch (ModelNotFoundException e) {
            Assert.assertTrue(e.getMessage().contains("/not-found/files.json"));
            thrown = true;
        }
        Assert.assertTrue("ModelNotFoundException excepted", thrown);
    }

    /**
     * Ensure that ModelNotFoundException are thrown if DocumentModel URL doesn't
     * exists
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testLoadDocumentModelUrlNotFound() throws MalformedURLException {
        URL documentModelUrl = new URL("https://example.local/not-found/files.json");
        boolean thrown = false;
        try {
            modelLoader.loadDocumentModel(documentModelUrl);
        } catch (ModelNotFoundException e) {
            Assert.assertEquals("Model 'https://example.local/not-found/files.json' not found", e.getMessage());
            thrown = true;
        }
        Assert.assertTrue("ModelNotFoundException excepted", thrown);
    }

    /**
     * Ensure that ModelNotFoundException are thrown if a FeatureType is missing
     * 
     * @throws MalformedURLException
     */
//    @Test
//    public void testLoadDocumentModelFeatureTypeNotFound() throws MalformedURLException {
//        File documentModelPath = ResourceHelper.getResourceFile(
//            getClass(), "/config-xml/missing_feature_type/files.json"
//        );
//        boolean thrown = false;
//        try {
//            modelLoader.loadDocumentModel(documentModelPath);
//        } catch (ModelNotFoundException e) {
//            Assert.assertTrue(e.getMessage().contains("/missing_feature_type/types/MY_TABLE.json' not found"));
//            thrown = true;
//        }
//        Assert.assertTrue("ModelNotFoundException excepted", thrown);
//    }

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
        Assert.assertTrue(fileModel instanceof TableModel);
        Assert.assertEquals("ADRESSE(_+[0-9])?", fileModel.getPath());

        /* perform checks on FeatureType "adresse" */
        FeatureType featureType = fileModel.getFeatureType();
        Assert.assertNotNull(featureType);
        assertExceptedFeatureTypeAdresse(featureType);
    }

    @Test
    public void testLoadFeatureTypeAdresse() {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/config-json/adresse/types/ADRESSE.json");
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
                FeatureType featureType = fileModel.getFeatureType();
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
