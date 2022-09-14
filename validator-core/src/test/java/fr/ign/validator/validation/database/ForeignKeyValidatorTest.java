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
import fr.ign.validator.model.FeatureTypeConstraints;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.constraint.ForeignKeyConstraint;
import fr.ign.validator.model.file.SingleTableModel;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class ForeignKeyValidatorTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();

    private Context context;

    @Before
    public void setUp() throws NoSuchAuthorityCodeException, FactoryException {
        context = new Context();
        context.setProjection("EPSG:4326");
        context.setReportBuilder(reportBuilder);

        // creates a List<FileModel> with both FileModel
        List<FileModel> fileModels = new ArrayList<>();

        {
            // creates attributes "TYPE", "SUB_TYPE"
            AttributeType<String> attribute = new StringType();
            attribute.setName("TYPE");
            AttributeType<String> attribute2 = new StringType();
            attribute2.setName("SUB_TYPE");

            List<AttributeType<?>> attributes = new ArrayList<>();
            attributes.add(attribute);
            attributes.add(attribute2);

            // creates a FeatureType with both attributes
            FeatureType featureType = new FeatureType();
            featureType.setAttributes(attributes);

            SingleTableModel fileModel = new SingleTableModel();
            fileModel.setName("MY_REFERENCE");
            fileModel.setFeatureType(featureType);
            fileModels.add(fileModel);
        }


        {
            // creates attributes, "ID", "VALUE", "SUB_VALUE"
            AttributeType<String> attribute = new StringType();
            attribute.setName("ID");
            AttributeType<String> attribute2 = new StringType();
            attribute2.setName("VALUE");
            AttributeType<String> attribute3 = new StringType();
            attribute3.setName("SUB_VALUE");

            List<AttributeType<?>> attributes = new ArrayList<>();
            attributes.add(attribute);
            attributes.add(attribute2);
            attributes.add(attribute3);

            // creates a FeatureType with both attributes
            FeatureType featureType = new FeatureType();
            featureType.setAttributes(attributes);
            String foreignKey = "(VALUE, SUB_VALUE) REFERENCES MY_REFERENCE(TYPE, SUB_TYPE)";
            FeatureTypeConstraints constraints = new FeatureTypeConstraints();
            constraints.getForeignKeys().add(ForeignKeyConstraint.parseForeignKey(foreignKey));
            featureType.setConstraints(constraints);

            SingleTableModel fileModel = new SingleTableModel();
            fileModel.setName("MY_TABLE");
            fileModel.setFeatureType(featureType);
            fileModels.add(fileModel);
        }

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
        database.query("CREATE TABLE MY_TABLE(__id TEXT, __file TEXT, id TEXT, value TEXT, sub_value TEXT);");
        database.query("INSERT INTO MY_TABLE(id, value, sub_value) VALUES ('id1', 'type1', 'sub_type10');");
        database.query("INSERT INTO MY_TABLE(id, value, sub_value) VALUES ('id2', 'type1', 'sub_type15');");
        database.query("INSERT INTO MY_TABLE(id, value, sub_value) VALUES ('id3', 'type2', 'sub_type20');");
        database.query("INSERT INTO MY_TABLE(id, value, sub_value) VALUES ('id4', 'type2', 'sub_type20');");

        // add the table RELATION into the database
        database.query("CREATE TABLE MY_REFERENCE(__id TEXT, __file TEXT, type TEXT, sub_type TEXT);");
        database.query("INSERT INTO MY_REFERENCE(type, sub_type) VALUES ('type1', 'sub_type10');");
        database.query("INSERT INTO MY_REFERENCE(type, sub_type) VALUES ('type1', 'sub_type15');");
        database.query("INSERT INTO MY_REFERENCE(type, sub_type) VALUES ('type2', 'sub_type20');");

        // check that the validator doesn't send any error
        ForeignKeyValidator validator = new ForeignKeyValidator();
        validator.validate(context, database);

        Assert.assertEquals(0, reportBuilder.getErrorsByCode(CoreErrorCodes.TABLE_FOREIGN_KEY_NOT_FOUND).size());
    }

    @Test
    public void testNotValid() throws Exception {
        // creates an empty database
        File path = new File(folder.getRoot(), "document_database.db");
        Database database = new Database(path);

        // add the table TEST into the database
        database.query("CREATE TABLE MY_TABLE(__id TEXT, __file TEXT, id TEXT, value TEXT, sub_value TEXT);");
        database.query("INSERT INTO MY_TABLE(id, value, sub_value) VALUES ('id1', 'type1', 'sub_type1');");
        database.query("INSERT INTO MY_TABLE(id, value, sub_value) VALUES ('id2', 'type1', 'sub_type2');");
        database.query("INSERT INTO MY_TABLE(id, value, sub_value) VALUES ('id3', 'type2', 'sub_type1');");
        database.query("INSERT INTO MY_TABLE(id, value, sub_value) VALUES ('id4', 'type2', 'sub_type1');");

        // add the table RELATION into the database
        database.query("CREATE TABLE MY_REFERENCE(__id TEXT, __file TEXT, type TEXT, sub_type TEXT);");
        database.query("INSERT INTO MY_REFERENCE(type, sub_type) VALUES ('type1', 'sub_type1');");
        database.query("INSERT INTO MY_REFERENCE(type, sub_type) VALUES ('type1', 'sub_type2');");
        database.query("INSERT INTO MY_REFERENCE(type, sub_type) VALUES ('type2', 'sub_type5');");

        // check that the validator doesn't send any error
        ForeignKeyValidator validator = new ForeignKeyValidator();
        validator.validate(context, database);

        Assert.assertEquals(2, reportBuilder.getErrorsByCode(CoreErrorCodes.TABLE_FOREIGN_KEY_NOT_FOUND).size());

        List<ValidatorError> errors = reportBuilder.getErrorsByCode(CoreErrorCodes.TABLE_FOREIGN_KEY_NOT_FOUND);
        int index = 0;
        // check first error
        {
            ValidatorError error = errors.get(index++);
            assertEquals("--", error.getAttribute());
            assertEquals(null, error.getId());
            assertEquals("", error.getFeatureId());
            assertEquals("MY_TABLE", error.getFileModel());
            assertEquals(ErrorScope.FEATURE, error.getScope());
            assertEquals(
                "La correspondance (VALUE, SUB_VALUE) = (type2, sub_type1) n'est pas autorisée, car non présente dans la liste de référence MY_REFERENCE.",
                error.getMessage()
            );
            assertEquals("SAMPLE_MODEL", error.getDocumentModel());
        }

        {
            ValidatorError error = errors.get(index++);
            assertEquals("--", error.getAttribute());
            assertEquals(null, error.getId());
            assertEquals("", error.getFeatureId());
            assertEquals("MY_TABLE", error.getFileModel());
            assertEquals(ErrorScope.FEATURE, error.getScope());
            assertEquals(
                "La correspondance (VALUE, SUB_VALUE) = (type2, sub_type1) n'est pas autorisée, car non présente dans la liste de référence MY_REFERENCE.",
                error.getMessage()
            );
            assertEquals("SAMPLE_MODEL", error.getDocumentModel());
        }

    }

}
