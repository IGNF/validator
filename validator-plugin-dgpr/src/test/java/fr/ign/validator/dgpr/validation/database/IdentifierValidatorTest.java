package fr.ign.validator.dgpr.validation.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class IdentifierValidatorTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();

    private Context context;

    @Before
    public void setUp() throws NoSuchAuthorityCodeException, FactoryException, JAXBException {
        context = new Context();
        context.setProjection("EPSG:4326");
        context.setReportBuilder(reportBuilder);

        // creates attribute "id" which is an identifier
        AttributeType<String> attribute = new StringType();
        attribute.setIdentifier(true);
        attribute.setName("id");

        // creates attribute "relation_id" which is NOT an identifier
        AttributeType<String> attribute2 = new StringType();
        attribute2.setIdentifier(false);
        attribute2.setName("relation_id");

        // creates list of attributes
        List<AttributeType<?>> attributes = new ArrayList<>();
        attributes.add(attribute);
        attributes.add(attribute2);

        // creates a FeatureType with both attributes
        FeatureType featureType = new FeatureType();
        featureType.setAttributes(attributes);

        // creates a FeatureType with only 'id' attribute
        FeatureType featureType2 = new FeatureType();
        featureType2.addAttribute(attribute);

        // creates a FileModel with the first Feature Type
        FileModel fileModel = new TableModel();
        fileModel.setName("TEST");
        fileModel.setFeatureType(featureType);

        // reates a FileModel with the second Feature Type
        FileModel fileModel2 = new TableModel();
        fileModel2.setName("RELATION");
        fileModel2.setFeatureType(featureType2);

        // creates a List<FileModel> with both FileModel
        List<FileModel> fileModels = new ArrayList<>();
        fileModels.add(fileModel);
        fileModels.add(fileModel2);

        // creates a DocumentModel with the List<FileModel>
        DocumentModel documentModel = new DocumentModel();
        documentModel.setFileModels(fileModels);
        context.beginModel(documentModel);
    }

    @Test
    public void testOk() throws Exception {
        // creates an empty database
        File path = new File(folder.getRoot(), "document_database.db");
        Database database = new Database(path);

        // add the table TEST into the database
        database.query("CREATE TABLE TEST(id TEXT, relation_id TEXT);");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_1', 'relation_1');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_2', 'relation_2');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_3', 'relation_1');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_4', 'relation_3');");

        // add the table RELATION into the database
        database.query("CREATE TABLE RELATION(id TEXT);");
        database.query("INSERT INTO RELATION(id) VALUES ('relation_1');");
        database.query("INSERT INTO RELATION(id) VALUES ('relation_2');");
        database.query("INSERT INTO RELATION(id) VALUES ('relation_3');");

        // check that the identifierValidator doesn't send any error
        IdentifierValidator identifierValidator = new IdentifierValidator();
        identifierValidator.validate(context, database);

        Assert.assertEquals(0, reportBuilder.getErrorsByCode(DgprErrorCodes.DGPR_IDENTIFIER_UNICITY).size());
    }

    @Test
    public void testUnicityFail() throws Exception {
        // creates an empty database
        File path = new File(folder.getRoot(), "document_database.db");
        Database database = new Database(path);

        // add the table TEST into the database
        database.query("CREATE TABLE TEST(id TEXT, relation_id TEXT);");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_1', 'relation_1');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_2', 'relation_2');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_2', 'relation_1');");
        database.query("INSERT INTO TEST(id, relation_id) VALUES ('test_4', 'relation_3');");

        // add the table RELATION into the database
        database.query("CREATE TABLE RELATION(id TEXT);");
        database.query("INSERT INTO RELATION(id) VALUES ('relation_1');");
        database.query("INSERT INTO RELATION(id) VALUES ('relation_2');");
        database.query("INSERT INTO RELATION(id) VALUES ('relation_3');");
        database.query("INSERT INTO RELATION(id) VALUES ('relation_3');");
        database.query("INSERT INTO RELATION(id) VALUES ('relation_3');");

        // check that the identifierValidator sends two errors
        IdentifierValidator identifierValidator = new IdentifierValidator();
        identifierValidator.validate(context, database);

        Assert.assertEquals(2, reportBuilder.getErrorsByCode(DgprErrorCodes.DGPR_IDENTIFIER_UNICITY).size());

        ValidatorError idError0 = reportBuilder.getErrorsByCode(DgprErrorCodes.DGPR_IDENTIFIER_UNICITY).get(0);
        ValidatorError idError1 = reportBuilder.getErrorsByCode(DgprErrorCodes.DGPR_IDENTIFIER_UNICITY).get(1);

        Assert.assertEquals(
            "Problème dans la table TEST : l'identifiant 'test_2' est présent 2 fois.", idError0.getMessage()
        );
        Assert.assertEquals(
            "Problème dans la table RELATION : l'identifiant 'relation_3' est présent 3 fois.", idError1.getMessage()
        );
    }

}
