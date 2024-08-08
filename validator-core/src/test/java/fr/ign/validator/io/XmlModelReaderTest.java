package fr.ign.validator.io;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.ign.validator.exception.ModelNotFoundException;
import fr.ign.validator.model.AttributeConstraints;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.FileModel.MandatoryMode;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.file.SingleTableModel;
import fr.ign.validator.tools.ResourceHelper;

public class XmlModelReaderTest {

    private ModelReader modelLoader;

    @Before
    public void setUp() throws Exception {
        modelLoader = new XmlModelReader();
    }

    /**
     * Ensure that ModelNotFoundException are thrown if DocumentModel file doesn't
     * exists
     */
    @Test
    public void testLoadDocumentModelFileNotFound() {
        File configDir = ResourceHelper.getResourceFile(getClass(), "/config-xml");
        File documentModelPath = new File(configDir, "/not-found/files.xml");
        boolean thrown = false;
        try {
            modelLoader.loadDocumentModel(documentModelPath);
        } catch (ModelNotFoundException e) {
            Assert.assertTrue(e.getMessage().contains("/not-found/files.xml"));
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
        URL documentModelUrl = new URL("https://example.local/not-found/files.xml");
        boolean thrown = false;
        try {
            modelLoader.loadDocumentModel(documentModelUrl);
        } catch (ModelNotFoundException e) {
            Assert.assertEquals("Model 'https://example.local/not-found/files.xml' not found", e.getMessage());
            thrown = true;
        }
        Assert.assertTrue("ModelNotFoundException excepted", thrown);
    }

    /**
     * Ensure that ModelNotFoundException are thrown if a FeatureType is missing
     *
     * @throws MalformedURLException
     */
    @Test
    public void testLoadDocumentModelFeatureTypeNotFound() throws MalformedURLException {
        File documentModelPath = ResourceHelper.getResourceFile(
            getClass(), "/config-xml/missing_feature_type/files.xml"
        );
        boolean thrown = false;
        try {
            modelLoader.loadDocumentModel(documentModelPath);
        } catch (ModelNotFoundException e) {
            Assert.assertTrue(e.getMessage().contains("/missing_feature_type/types/MY_TABLE.xml' not found"));
            thrown = true;
        }
        Assert.assertTrue("ModelNotFoundException excepted", thrown);
    }

    /**
     * Read cnig_PLU_2014 and performs regress test
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testLoadDocumentModelCnigPlu2014() {
        File documentModelPath = ResourceHelper.getResourceFile(getClass(), "/config-xml/cnig_PLU_2014/files.xml");
        DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);
        assertIsValid(documentModel);

        Assert.assertEquals("cnig_PLU_2014", documentModel.getName());
        Assert.assertEquals(31, documentModel.getFileModels().size());
    }

    /**
     * Read adresse model and performs regress test
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testLoadDocumentModelAdresse() {
        File documentModelPath = ResourceHelper.getResourceFile(getClass(), "/config-xml/adresse/files.xml");
        DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);
        assertIsValid(documentModel);

        Assert.assertEquals("adresse", documentModel.getName());
        Assert.assertEquals(1, documentModel.getFileModels().size());

        FileModel fileModel = documentModel.getFileModels().get(0);
        Assert.assertEquals("ADRESSE", fileModel.getName());

        Assert.assertTrue(fileModel instanceof SingleTableModel);
        FeatureType featureType = ((TableModel) fileModel).getFeatureType();
        Assert.assertNotNull(featureType);
        Assert.assertEquals(2, featureType.getAttributeCount());

        int index = 0;
        {
            AttributeType<?> attribute = featureType.getAttribute(index++);
            Assert.assertEquals("ID", attribute.getName());
            Assert.assertEquals("String", attribute.getTypeName());
            Assert.assertTrue(attribute.getConstraints().isUnique());
        }
        {
            AttributeType<?> attribute = featureType.getAttribute(index++);
            Assert.assertEquals("ADRESSE", attribute.getName());
            Assert.assertEquals("String", attribute.getTypeName());
            Assert.assertFalse(attribute.getConstraints().isUnique());
        }
    }

    /**
     * Load DocumentModel /config-xml/sample-document/files.xml and performs checks
     */
    @Test
    public void testLoadDocumentModelSampleDocument() {
        File documentModelPath = ResourceHelper.getResourceFile(getClass(), "/config-xml/sample-document/files.xml");
        DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);
        assertIsValid(documentModel);

        // name
        Assert.assertEquals("ccccc_CC_dddddddd", documentModel.getName());
        // DocumentContraints
        Assert.assertEquals(
            "[0-9]{5}_CC_[0-9]{8}",
            documentModel.getConstraints().getFolderName()
        );
        Assert.assertNull(
            documentModel.getConstraints().getMetadataSpecification()
        );

        // fileModels
        Assert.assertEquals(3, documentModel.getFileModels().size());
        int index = 0;
        {
            FileModel fileModel = documentModel.getFileModels().get(index++);
            Assert.assertEquals("SIMPLE", fileModel.getName());
            Assert.assertTrue(fileModel instanceof SingleTableModel);
            FeatureType featureType = ((TableModel) fileModel).getFeatureType();
            Assert.assertNotNull(featureType);
            Assert.assertEquals(MandatoryMode.WARN, fileModel.getMandatory());
            Assert.assertEquals("SIMPLE", featureType.getName());
        }
        {
            FileModel fileModel = documentModel.getFileModels().get(index++);
            Assert.assertEquals("Donnees_geographiques", fileModel.getName());
            Assert.assertEquals(MandatoryMode.ERROR, fileModel.getMandatory());
        }
        {
            FileModel fileModel = documentModel.getFileModels().get(index++);
            Assert.assertEquals("COMMUNE", fileModel.getName());
            Assert.assertEquals(MandatoryMode.WARN, fileModel.getMandatory());

            Assert.assertTrue(fileModel instanceof SingleTableModel);
            FeatureType featureType = ((TableModel) fileModel).getFeatureType();
            Assert.assertNotNull(featureType);
            Assert.assertEquals("COMMUNE", featureType.getName());
            assertExceptedFeatureTypeCommune(featureType);
        }
    }

    /**
     * Check FeatureType definition for
     * /config-xml/sample-document/types/COMMUNE.xml
     *
     * @param featureType
     */
    private void assertExceptedFeatureTypeCommune(FeatureType featureType) {
        int index = 0;
        {
            AttributeType<?> attributeType = featureType.getAttribute(index++);
            Assert.assertEquals("INSEE", attributeType.getName());
            Assert.assertEquals("String", attributeType.getTypeName());
            AttributeConstraints contraints = attributeType.getConstraints();
            Assert.assertEquals(true, contraints.isRequired());
            Assert.assertEquals(true, contraints.isUnique());
            Assert.assertEquals("[0-9]{5}", contraints.getPattern());
            Assert.assertNull(contraints.getReference());
            Assert.assertNull(contraints.getEnumValues());
        }
        {
            AttributeType<?> attributeType = featureType.getAttribute(index++);
            Assert.assertEquals("CODE_DEPT", attributeType.getName());
            Assert.assertEquals("String", attributeType.getTypeName());
            AttributeConstraints constraints = attributeType.getConstraints();
            Assert.assertEquals(true, constraints.isRequired());
            Assert.assertNull(constraints.getPattern());
            Assert.assertEquals(false, constraints.isUnique());
            Assert.assertNull(constraints.getReference());
            Assert.assertNotNull(constraints.getEnumValues());
            Assert.assertEquals("01,02", Strings.join(constraints.getEnumValues(), ','));
        }
        {
            AttributeType<?> attributeType = featureType.getAttribute(index++);
            Assert.assertEquals("DETRUIT", attributeType.getName());
            Assert.assertEquals("Boolean", attributeType.getTypeName());
            AttributeConstraints constraints = attributeType.getConstraints();
            Assert.assertEquals(false, constraints.isRequired());
            Assert.assertNull(constraints.getPattern());
            Assert.assertEquals(false, constraints.isUnique());
            Assert.assertNull(constraints.getReference());
            Assert.assertNull(constraints.getEnumValues());
        }
    }

    @Test
    public void testLoadFeatureTypeWithCDATA() {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/config-xml/sample-document/types/SIMPLE.xml");

        FeatureType featureType = modelLoader.loadFeatureType(srcFile);
        Assert.assertEquals("SIMPLE", featureType.getName());
        Assert.assertEquals("TABLE TEST", featureType.getDescription());
        Assert.assertEquals(3, featureType.getAttributeCount());
        Assert.assertTrue(featureType.isSpatial());

        int index = 0;
        {
            AttributeType<?> attribute = featureType.getAttribute(index++);
            Assert.assertEquals("ID", attribute.getName());
            Assert.assertEquals("Integer", attribute.getTypeName());

            AttributeConstraints constraints = attribute.getConstraints();
            Assert.assertTrue(constraints.isRequired());
            Assert.assertFalse(constraints.isUnique());
            Assert.assertNull(constraints.getPattern());
            Assert.assertNull(constraints.getEnumValues());
        }

        {
            AttributeType<?> attribute = featureType.getAttribute(index++);
            Assert.assertEquals("NAME", attribute.getName());
            Assert.assertEquals("String", attribute.getTypeName());

            AttributeConstraints constraints = attribute.getConstraints();
            Assert.assertTrue(constraints.isRequired());
            Assert.assertFalse(constraints.isUnique());
            Assert.assertNull(constraints.getPattern());
            Assert.assertNull(constraints.getEnumValues());
        }

        {
            AttributeType<?> attribute = featureType.getAttribute(index++);
            Assert.assertEquals("GEOMETRY", attribute.getName());
            Assert.assertEquals("Geometry", attribute.getTypeName());

            AttributeConstraints constraints = attribute.getConstraints();
            Assert.assertFalse(constraints.isRequired());
            Assert.assertFalse(constraints.isUnique());
            Assert.assertNull(constraints.getPattern());
            Assert.assertNull(constraints.getEnumValues());
        }

    }

    /**
     * Performs basic consistency checks on DocumentModel
     *
     * @param documentModel
     */
    private void assertIsValid(DocumentModel documentModel) {
        List<FileModel> fileModels = documentModel.getFileModels();
        for (FileModel fileModel : fileModels) {
            Assert.assertNotNull(fileModel.getName());
            Assert.assertNotNull(fileModel.getMandatory());
            if (fileModel instanceof SingleTableModel) {
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
