package fr.ign.validator.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.exception.ModelNotFoundException;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.StaticTable;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.constraint.ForeignKeyConstraint;
import fr.ign.validator.model.file.EmbeddedTableModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.model.file.MultiTableModel;
import fr.ign.validator.model.file.SingleTableModel;
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
     * Read DocumentModel from GpU
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testLoadDocumentModelFromGpU() throws MalformedURLException {
        URL documentModelUrl = new URL("https://www.geoportail-urbanisme.gouv.fr/standard/cnig_PLU_2017.json");
        DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelUrl);
        assertEquals("cnig_PLU_2017", documentModel.getName());
        assertIsValid(documentModel);

        List<SingleTableModel> tableModels = documentModel.getFileModels().stream()
            .filter((fileModel) -> {
                return (fileModel instanceof SingleTableModel);
            }).map((fileModel) -> {
                return (SingleTableModel) fileModel;
            }).collect(Collectors.toList());

        assertEquals(14, tableModels.size());
        for (TableModel tableModel : tableModels) {
            // FeatureType is defined
            assertNotNull(tableModel.getFeatureType());
            // FeatureType name matches tableModel name
            assertEquals(tableModel.getName(), tableModel.getFeatureType().getName());
        }
    }

    /**
     * Read DocumentModel from GpU
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testLoadDocumentModelWithExternalFeatureType() throws MalformedURLException {
        File documentModelPath = ResourceHelper.getResourceFile(getClass(), "/config-json/external-type/document.json");
        DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);
        assertIsValid(documentModel);
        assertEquals(1, documentModel.getFileModels().size());

        FileModel fileModel = documentModel.getFileModels().get(0);
        assertTrue(fileModel instanceof SingleTableModel);
        TableModel tableModel = (TableModel) fileModel;
        assertEquals("ZONE_URBA", fileModel.getName());

        FeatureType featureType = tableModel.getFeatureType();
        assertNotNull(featureType);
        assertEquals(fileModel.getName(), featureType.getName());
        assertEquals(9, featureType.getAttributeCount());
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
        Assert.assertTrue(fileModel instanceof SingleTableModel);
        FeatureType featureType = ((TableModel) fileModel).getFeatureType();
        Assert.assertNotNull(featureType);
        assertExceptedFeatureTypeAdresse(featureType);
    }

    /**
     * Read config-json/cnig_SUP_EL9_2013 and performs regress test
     */
    @Test
    public void testLoadDocumentModelCnigWithConstraints() {
        File documentModelPath = ResourceHelper.getResourceFile(
            getClass(), "/config-json/cnig_SUP_EL9_2013/files.json"
        );
        DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);
        assertIsValid(documentModel);

        Assert.assertEquals("cnig_SUP_EL9_2013", documentModel.getName());
        Assert.assertEquals(10, documentModel.getFileModels().size());

        /* perform checks on FileModel "adresse" */
        FileModel fileModel = documentModel.getFileModelByName("SERVITUDE");
        Assert.assertNotNull(fileModel);
        Assert.assertEquals("Donnees_geographiques/SERVITUDE(_[AB0-9]{3})?", fileModel.getPath());

        /* perform checks on FeatureType "adresse" */
        Assert.assertTrue(fileModel instanceof SingleTableModel);
        FeatureType featureType = ((TableModel) fileModel).getFeatureType();
        Assert.assertNotNull(featureType);
        assertExceptedFeatureTypeServitude(featureType);
    }


    /**
     * Read config-json/cnig_PLU_2017 and performs regress test
     */
    @Test
    public void testLoadForeignKeyConstraint() {
        File documentModelPath = ResourceHelper.getResourceFile(
            getClass(), "/config-json/cnig_PLU_2017/files.json"
        );
        DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);
        assertIsValid(documentModel);

        Assert.assertEquals("cnig_PLU_2017", documentModel.getName());
        Assert.assertEquals(33, documentModel.getFileModels().size());
        Assert.assertEquals(2, documentModel.getStaticTables().size());

        /*
         * perform checks on FileModel "INFO_SURF"
         * control constraint
         * (TYPEINF,STYPEINF) REFERENCES InformationUrbaType(TYPEINF,STYPEINF)
         */
        {
            FileModel fileModel = documentModel.getFileModelByName("INFO_SURF");
            Assert.assertNotNull(fileModel);
            Assert.assertTrue(fileModel instanceof SingleTableModel);
            FeatureType featureType = ((TableModel) fileModel).getFeatureType();
            Assert.assertNotNull(featureType);
            Assert.assertEquals(1, featureType.getConstraints().getForeignKeys().size());

            ForeignKeyConstraint foreignKeyConstraint = featureType.getConstraints().getForeignKeys().get(0);
            Assert.assertEquals("(TYPEINF,STYPEINF) REFERENCES InformationUrbaType(TYPEINF,STYPEINF)", foreignKeyConstraint.toString());
        }
        
        {
            FileModel fileModel = documentModel.getFileModelByName("PRESCRIPTION_SURF");
            Assert.assertNotNull(fileModel);
            Assert.assertTrue(fileModel instanceof SingleTableModel);
            FeatureType featureType = ((TableModel) fileModel).getFeatureType();
            Assert.assertNotNull(featureType);
            Assert.assertEquals(1, featureType.getConstraints().getForeignKeys().size());

            ForeignKeyConstraint foreignKeyConstraint = featureType.getConstraints().getForeignKeys().get(0);
            Assert.assertEquals("(TYPEPSC,STYPEPSC) REFERENCES PrescriptionUrbaType(TYPEPSC,STYPEPSC)", foreignKeyConstraint.toString());
        }

        /*
         * perform checks on StaticTable "InformationUrbaType"
         */
        {
        	StaticTable staticTable = documentModel.getStaticTableByName("InformationUrbaType");
            Assert.assertNotNull(staticTable);
            Assert.assertEquals("InformationUrbaType", staticTable.getName());
            Assert.assertEquals("./codes/InformationUrbaType.csv", staticTable.getPath());
            Assert.assertNotNull(staticTable.getUrl());
        }
        
        {
        	StaticTable staticTable = documentModel.getStaticTableByName("PrescriptionUrbaType");
            Assert.assertNotNull(staticTable);
            Assert.assertEquals("PrescriptionUrbaType", staticTable.getName());
            Assert.assertEquals("./codes/PrescriptionUrbaType.csv", staticTable.getPath());
            Assert.assertNotNull(staticTable.getUrl());
        }
        

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
     * Check FeatureType for /config-json/cnig_SUP_EL9_2013/types/SERVITUDE.json
     * 
     * @param featureType
     */
    private void assertExceptedFeatureTypeServitude(FeatureType featureType) {
        Assert.assertEquals("SERVITUDE", featureType.getName());
        Assert.assertEquals("Table contenant la liste des servitudes d'utilité publique", featureType.getDescription());
        Assert.assertEquals(15, featureType.getAttributeCount());
        Assert.assertFalse(featureType.isSpatial());

        Assert.assertEquals(2, featureType.getConstraints().getConditions().size());

        {
            String condition = featureType.getConstraints().getConditions().get(0);
            Assert.assertEquals("quiProd NOT NULL AND modeProd LIKE 'numérisation'", condition);
        }

        {
            String condition = featureType.getConstraints().getConditions().get(1);
            Assert.assertEquals("docSource NOT NULL AND modeProd LIKE 'numérisation'", condition);
        }

        int index = 0;
        {
            AttributeType<?> attribute = featureType.getAttribute(index++);
            Assert.assertEquals("MODEPROD", attribute.getName());
            Assert.assertEquals("String", attribute.getTypeName());
            Assert.assertTrue(
                attribute.getConstraints().isRequired()
            );
            Assert.assertFalse(
                attribute.getConstraints().isUnique()
            );
            String listOfvalue = String.join(",", attribute.getConstraints().getEnumValues());
            Assert.assertEquals("import,numérisation,reconstitution", listOfvalue);
        }

        index = 12;

        {
            AttributeType<?> attribute = featureType.getAttribute(index++);
            Assert.assertEquals("QUIPROD", attribute.getName());
            Assert.assertEquals("String", attribute.getTypeName());
            Assert.assertEquals("Organisme ayant numérisé la servitude", attribute.getDescription());
            Assert.assertFalse(
                attribute.getConstraints().isRequired()
            );
            Assert.assertFalse(
                attribute.getConstraints().isUnique()
            );
            Assert.assertEquals(
                Integer.valueOf(80),
                attribute.getConstraints().getMaxLength()
            );
        }

        index = 14;

        {
            AttributeType<?> attribute = featureType.getAttribute(index++);
            Assert.assertEquals("DOCSOURCE", attribute.getName());
            Assert.assertEquals("String", attribute.getTypeName());
            Assert.assertEquals("Document graphique ayant été numérisé", attribute.getDescription());
            Assert.assertFalse(
                attribute.getConstraints().isRequired()
            );
            Assert.assertFalse(
                attribute.getConstraints().isUnique()
            );
            Assert.assertEquals(
                Integer.valueOf(80),
                attribute.getConstraints().getMaxLength()
            );
        }

    }

    /**
     * Read config-json/pcrs-2.0 and performs regress test
     */
    @Test
    public void testLoadDocumentModelPCRS() {
        File documentModelPath = ResourceHelper.getResourceFile(
            getClass(), "/config-json/CNIG_PCRS_v2.0/document.json"
        );
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
            Assert.assertEquals(37, tableModels.size());
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
