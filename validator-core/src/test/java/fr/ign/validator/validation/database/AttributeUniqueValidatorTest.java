package fr.ign.validator.validation.database;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.SingleTableModel;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class AttributeUniqueValidatorTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();

    private Context context;

    @Before
    public void setUp() throws NoSuchAuthorityCodeException, FactoryException {
        context = new Context();
        context.setProjection("EPSG:4326");
        context.setReportBuilder(reportBuilder);
 
        // creates list of attributes
        List<AttributeType<?>> attributes = new ArrayList<>();
        // creates attribute "id" which is an identifier
        {
            AttributeType<String> attribute = new StringType();
            attribute.setName("ID");
            attribute.getConstraints().setUnique(true);
            attributes.add(attribute);
        }
        // creates attribute "relation_id" which is NOT an identifier
        {
            AttributeType<String> attribute = new StringType();
            attribute.setName("RELATION_ID");
            attributes.add(attribute);
        }
        AttributeType<Geometry> geometryType = new GeometryType();
        geometryType.setName("WKT");
        attributes.add(geometryType);
        

        SingleTableModel fileModel = new SingleTableModel();
        fileModel.setName("TEST");
        FeatureType featureType = new FeatureType();
        featureType.setAttributes(attributes);
        fileModel.setFeatureType(featureType);
        // creates a List<FileModel>
        List<FileModel> fileModels = new ArrayList<>();
        fileModels.add(fileModel);

        // creates a DocumentModel with the List<FileModel>
        DocumentModel documentModel = new DocumentModel();
        documentModel.setName("SAMPLE_MODEL");
        documentModel.setFileModels(fileModels);
        context.beginModel(documentModel);
    }

    @Test
    public void testValid() throws Exception {
        // creates an empty database
        File path = new File(folder.getRoot(), "document_database.db");
        Database database = new Database(path);

        // add the table TEST into the database
        database.query("CREATE TABLE TEST(id TEXT, relation_id TEXT, wkt TEXT);");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_1', 'relation_1');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_2', 'relation_2');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_3', 'relation_1');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_4', 'relation_3');");

        // check that the validator doesn't send any error
        AttributeUniqueValidator identifierValidator = new AttributeUniqueValidator();
        identifierValidator.validate(context, database);

        Assert.assertEquals(0, reportBuilder.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_NOT_UNIQUE).size());
    }

    @Test
    public void testNotUniq() throws Exception {
        // creates an empty database
        File path = new File(folder.getRoot(), "document_database.db");
        Database database = new Database(path);

        // add the table TEST into the database
        database.query("CREATE TABLE TEST(id TEXT, relation_id TEXT, wkt TEXT);");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_1', 'relation_1');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_2', 'relation_2');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_2', 'relation_1');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_2', 'relation_3');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_3', 'relation_2');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_3', 'relation_3');");

        // check that the identifierValidator sends two errors
        AttributeUniqueValidator identifierValidator = new AttributeUniqueValidator();
        identifierValidator.validate(context, database);

        Assert.assertEquals(2, reportBuilder.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_NOT_UNIQUE).size());

        List<ValidatorError> errors = reportBuilder.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_NOT_UNIQUE);
        {
        	
            ValidatorError error = errors.get(0);
            assertEquals("ID", error.getAttribute());
            assertEquals("TEST", error.getFileModel());
            assertEquals(ErrorScope.DIRECTORY, error.getScope());
            assertEquals(
                "La valeur 'test_2' est présente 3 fois pour le champ 'ID' de la table 'TEST'.",
                error.getMessage()
            );
            assertEquals("SAMPLE_MODEL", error.getDocumentModel());
        }
        {
            ValidatorError error = errors.get(1);
            assertEquals("ID", error.getAttribute());
            assertEquals("TEST", error.getFileModel());
            assertEquals(ErrorScope.DIRECTORY, error.getScope());
            assertEquals(
                "La valeur 'test_3' est présente 2 fois pour le champ 'ID' de la table 'TEST'.",
                error.getMessage()
            );
            assertEquals("SAMPLE_MODEL", error.getDocumentModel());
        }
    }

    @Test
    public void testUniqWKT() throws Exception {
        // creates an empty database
        File path = new File(folder.getRoot(), "document_database.db");
        Database database = new Database(path);

        // add the table TEST into the database
        database.query("CREATE TABLE TEST(id TEXT, relation_id TEXT, wkt TEXT);");
        database.query("INSERT INTO TEST(id, relation_id, wkt) VALUES ('test_1', '', 'POINT(1, 0)');");
        database.query("INSERT INTO TEST(id, relation_id, wkt) VALUES ('test_2', '', 'POINT(1, 1)');");


        // check that the identifierValidator sends two errors
        AttributeUniqueValidator identifierValidator = new AttributeUniqueValidator();
        identifierValidator.validate(context, database);

        Assert.assertEquals(0, reportBuilder.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_NOT_UNIQUE).size());
    }

    @Test
    public void testNotUniqdWKT() throws Exception {
    	// update model
        SingleTableModel model = (SingleTableModel) context.getDocumentModel().getFileModelByName("TEST");
        GeometryType type = (GeometryType) model.getFeatureType().getAttribute("WKT");
        type.getConstraints().setUnique(true);
        
        // creates an empty database
        File path = new File(folder.getRoot(), "document_database.db");
        Database database = new Database(path);
        

        // add the table TEST into the database
        database.query("CREATE TABLE TEST(id TEXT, relation_id TEXT, wkt TEXT);");
        database.query("INSERT INTO TEST(id, relation_id, wkt) VALUES ('test_1', '', 'POINT(1, 1)');");
        database.query("INSERT INTO TEST(id, relation_id, wkt) VALUES ('test_2', '', 'POINT(1, 1)');");
        database.query("INSERT INTO TEST(id, relation_id, wkt) VALUES ('test_4', '', 'POINT(2, 2)');");
        database.query("INSERT INTO TEST(id, relation_id, wkt) VALUES ('test_5', '', 'POINT(2, 2)');");
        database.query("INSERT INTO TEST(id, relation_id, wkt) VALUES ('test_6', '', 'POINT(2, 2)');");


        // check that the identifierValidator sends two errors
        AttributeUniqueValidator identifierValidator = new AttributeUniqueValidator();
        identifierValidator.validate(context, database);

        Assert.assertEquals(2, reportBuilder.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_NOT_UNIQUE).size());
    }

}
