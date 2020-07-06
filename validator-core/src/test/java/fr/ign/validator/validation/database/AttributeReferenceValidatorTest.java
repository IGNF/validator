package fr.ign.validator.validation.database;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class AttributeReferenceValidatorTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();

    private Context context;

    @Before
    public void setUp() throws NoSuchAuthorityCodeException, FactoryException {
        context = new Context();
        context.setProjection("CRS:84");
        context.setReportBuilder(reportBuilder);
        DocumentModel documentModel = createTestDocumentModel();
        context.beginModel(documentModel);
    }

    /**
     * Create a sample DocumentModel with USER associated to a single TEAM.
     * 
     * @return
     */
    private DocumentModel createTestDocumentModel() {
        DocumentModel documentModel = new DocumentModel();
        documentModel.setName("USER_TEAM");

        // TEAM
        {
            TableModel tableModel = new TableModel();
            tableModel.setName("TEAM");
            FeatureType featureType = new FeatureType();
            featureType.setName("TEAM");
            {
                StringType attribute = new StringType();
                attribute.setName("ID");
                attribute.getConstraints().setUnique(true);
                featureType.addAttribute(attribute);
            }
            {
                StringType attribute = new StringType();
                attribute.setName("NAME");
                featureType.addAttribute(attribute);
            }
            tableModel.setFeatureType(featureType);
            documentModel.getFileModels().add(tableModel);
        }

        // USER
        {
            TableModel tableModel = new TableModel();
            tableModel.setName("USER");
            FeatureType featureType = new FeatureType();
            featureType.setName("USER");
            {
                StringType attribute = new StringType();
                attribute.setName("ID");
                attribute.getConstraints().setUnique(true);
                featureType.addAttribute(attribute);
            }
            {
                StringType attribute = new StringType();
                attribute.setName("TEAM_ID");
                attribute.getConstraints().setReference("TEAM.ID");
                featureType.addAttribute(attribute);
            }
            {
                StringType attribute = new StringType();
                attribute.setName("NAME");
                featureType.addAttribute(attribute);
            }
            tableModel.setFeatureType(featureType);
            documentModel.getFileModels().add(tableModel);
        }

        return documentModel;
    }

    @Test
    public void testValid() throws Exception {
        // creates an empty database
        File path = new File(folder.getRoot(), "document_database.db");
        Database database = new Database(path);

        // add the table TEST into the database
        database.query("CREATE TABLE TEAM(id TEXT, name TEXT);");
        database.query("INSERT INTO TEAM(id, name) VALUES ('t1', 'team_1');");
        database.query("INSERT INTO TEAM(id, name) VALUES ('t2', 'team_2');");

        // add the table TEST_B into the database
        database.query("CREATE TABLE USER(id TEXT, team_id TEXT, name TEXT);");
        database.query("INSERT INTO USER(id, team_id, name) VALUES ('u1', 't1', 'user_1');");
        database.query("INSERT INTO USER(id, team_id, name) VALUES ('u2', 't1', 'user_2');");
        database.query("INSERT INTO USER(id, team_id, name) VALUES ('u3', 't2', 'user_3');");
        database.getConnection().commit();

        // check that the relationValidator doesn't send any error
        AttributeReferenceValidator validator = new AttributeReferenceValidator();
        validator.validate(context, database);

        Assert.assertEquals(0, reportBuilder.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_REFERENCE_NOT_FOUND).size());
    }

    @Test
    public void testNotValid() throws Exception {
        // creates an empty database
        File path = new File(folder.getRoot(), "document_database.db");
        Database database = new Database(path);

        // add the table TEST into the database
        database.query("CREATE TABLE TEAM(id TEXT, name TEXT);");
        database.query("INSERT INTO TEAM(id, name) VALUES ('t1', 'team_1');");
        database.query("INSERT INTO TEAM(id, name) VALUES ('t2', 'team_2');");

        // add the table TEST_B into the database
        database.query("CREATE TABLE USER(id TEXT, team_id TEXT, name TEXT);");
        database.query("INSERT INTO USER(id, team_id, name) VALUES ('u1', 't1', 'user_1');");
        database.query("INSERT INTO USER(id, team_id, name) VALUES ('u2', 't6', 'user_2');");
        database.query("INSERT INTO USER(id, team_id, name) VALUES ('u3', 't2', 'user_3');");
        database.getConnection().commit();

        // check that the relationValidator doesn't send any error
        AttributeReferenceValidator validator = new AttributeReferenceValidator();
        validator.validate(context, database);

        Assert.assertEquals(1, reportBuilder.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_REFERENCE_NOT_FOUND).size());

        List<ValidatorError> errors = reportBuilder.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_REFERENCE_NOT_FOUND);
        int index = 0;
        {
            ValidatorError error = errors.get(index++);
            assertEquals(
                "La référence USER.TEAM_ID n'est pas validée. Le champ TEAM.ID ne prend pas la valeur 't6'.",
                error.getMessage()
            );
            assertEquals(
                "u2",
                error.getFeatureId()
            );
        }
    }

}
