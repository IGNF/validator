package fr.ign.validator.dgpr.validation.database;


import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
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
import fr.ign.validator.tools.ResourceHelper;
import fr.ign.validator.xml.XmlModelManager;

public class RelationValidatorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();

	private Context context ;

	private Document document;
	
	@Before
	public void setUp() throws NoSuchAuthorityCodeException, FactoryException, JAXBException{
		context = new Context();
		context.setCoordinateReferenceSystem(CRS.decode("EPSG:4326"));
		context.setReportBuilder(reportBuilder);
		
		//creates attributes for table TEST and TEST_B
		AttributeType<String> attributeTest = new StringType();
		attributeTest.setIdentifier(true);
		attributeTest.setName("id");
		
		AttributeType<String> attributeTest2 = new StringType();
		attributeTest2.setName("ID_S_INOND");
		
		//creates attributes for table TEST_B				
		AttributeType<String> attributeTestB = new StringType();
		attributeTestB.setName("ID_TRI");
		
		//creates attributes for table N_prefixTri_INONDABLE_suffixInond_S_ddd				
		AttributeType<String> attributeInond = new StringType();
		attributeInond.setIdentifier(true);
		attributeInond.setName("ID_S_INOND");
		
		//creates attributes for table N_prefixTri_TRI_S_ddd				
		AttributeType<String> attributeTri = new StringType();
		attributeTri.setIdentifier(true);
		attributeTri.setName("ID_TRI");
		
		//creates list of attributes for table TEST
		List<AttributeType<?>> attributesTest = new ArrayList<>();
		attributesTest.add(attributeTest);
		attributesTest.add(attributeTest2);
		
		//creates list of attributes for table TEST_B
		List<AttributeType<?>> attributesTestB = new ArrayList<>();
		attributesTestB.add(attributeTest);
		attributesTestB.add(attributeTest2);
		attributesTestB.add(attributeTestB);
			
		//creates a FeatureType for table TEST
		FeatureType featureTypeTest = new FeatureType();
		featureTypeTest.setAttributes(attributesTest);
		
		//creates a FeatureType for table TEST_B
		FeatureType featureTypeTestB = new FeatureType();
		featureTypeTestB.setAttributes(attributesTestB);
		
		//creates a FeatureType for table N_prefixTri_INONDABLE_suffixInond_S_ddd
		FeatureType featureTypeInond = new FeatureType();
		featureTypeInond.addAttribute(attributeInond);
		
		//creates a FeatureType for table N_prefixTri_TRI_S_ddd
		FeatureType featureTypeTri = new FeatureType();
		featureTypeTri.addAttribute(attributeTri);
		
		//creates a FileModel with the first FeatureType
		FileModel fileModelTest = new TableModel();
		fileModelTest.setName("TEST");
		fileModelTest.setFeatureType(featureTypeTest);
		
		//creates a FileModel with the second FeatureType
		FileModel fileModelTestB = new TableModel();
		fileModelTestB.setName("TEST_B");
		fileModelTestB.setFeatureType(featureTypeTestB);
		
		//creates a FileModel with the third FeatureType
		FileModel fileModelInond = new TableModel();
		fileModelInond.setName("N_prefixTri_INONDABLE_suffixInond_S_ddd");
		fileModelInond.setFeatureType(featureTypeInond);
		
		//creates a FileModel with the fourth FeatureType
		FileModel fileModelTri = new TableModel();
		fileModelTri.setName("N_prefixTri_TRI_S_ddd");
		fileModelTri.setFeatureType(featureTypeTri);
		
		//creates a List<FileModel> with all FileModel
		List<FileModel> fileModels = new ArrayList<>();
		fileModels.add(fileModelTest);
		fileModels.add(fileModelTestB);
		fileModels.add(fileModelInond);
		fileModels.add(fileModelTri);
		
		//creates a DocumentModel with the List<FileModel>
		DocumentModel documentModel = new DocumentModel();
		documentModel.setFileModels(fileModels);
		
		//creates a Document with the DocumentModel
		document = new Document(documentModel, new File("document"));
	}

	@Test
	public void testOk() throws Exception {
		//creates an empty database
		File path = new File( folder.getRoot(), "document_database.db" );
		Database database = new Database(path);
		
		//add the table TEST into the database
		database.query("CREATE TABLE TEST(id TEXT, ID_S_INOND TEXT);");
		database.query("INSERT INTO TEST(id, ID_S_INOND) VALUES ('test_1', 's_inond_1');");
		database.query("INSERT INTO TEST(id, ID_S_INOND) VALUES ('test_2', 's_inond_2');");
		database.query("INSERT INTO TEST(id, ID_S_INOND) VALUES ('test_3', 's_inond_1');");
		database.query("INSERT INTO TEST(id, ID_S_INOND) VALUES ('test_4', 's_inond_3');");
		
		//add the table TEST_B into the database
		database.query("CREATE TABLE TEST_B(id TEXT, ID_S_INOND TEXT, ID_TRI TEXT);");
		database.query("INSERT INTO TEST_B(id, ID_S_INOND, ID_TRI) VALUES ('test_1', 's_inond_1', 'tri_1');");
		database.query("INSERT INTO TEST_B(id, ID_S_INOND, ID_TRI) VALUES ('test_2', 's_inond_2', 'tri_3');");
		database.query("INSERT INTO TEST_B(id, ID_S_INOND, ID_TRI) VALUES ('test_3', 's_inond_1', 'tri_2');");
		database.query("INSERT INTO TEST_B(id, ID_S_INOND, ID_TRI) VALUES ('test_4', 's_inond_3', 'tri_1');");
		
		//add the table RELATION into the database
		database.query("CREATE TABLE N_prefixTri_INONDABLE_suffixInond_S_ddd(ID_S_INOND TEXT);");
		database.query("INSERT INTO N_prefixTri_INONDABLE_suffixInond_S_ddd(ID_S_INOND) VALUES ('s_inond_1');");
		database.query("INSERT INTO N_prefixTri_INONDABLE_suffixInond_S_ddd(ID_S_INOND) VALUES ('s_inond_2');");
		database.query("INSERT INTO N_prefixTri_INONDABLE_suffixInond_S_ddd(ID_S_INOND) VALUES ('s_inond_3');");
		
		//add the table RELATION into the database
		database.query("CREATE TABLE N_prefixTri_TRI_S_ddd(ID_TRI TEXT);");
		database.query("INSERT INTO N_prefixTri_TRI_S_ddd(ID_TRI) VALUES ('tri_1');");
		database.query("INSERT INTO N_prefixTri_TRI_S_ddd(ID_TRI) VALUES ('tri_2');");
		database.query("INSERT INTO N_prefixTri_TRI_S_ddd(ID_TRI) VALUES ('tri_3');");
		
		//check that the relationValidator doesn't send any error 
		RelationValidator relationValidator = new RelationValidator();
		relationValidator.validate(context, document, database);

		Assert.assertEquals(0, reportBuilder.getErrorsByCode(DgprErrorCodes.DGPR_RELATION_ERROR).size());
	}
	
	@Test
	public void testRelationFail() throws Exception {
		//creates an empty database
		File path = new File( folder.getRoot(), "document_database.db" );
		Database database = new Database(path);
		
		//add the table TEST into the database
		database.query("CREATE TABLE TEST(id TEXT, ID_S_INOND TEXT);");
		database.query("INSERT INTO TEST(id, ID_S_INOND) VALUES ('test_1', 's_inond_1');");
		database.query("INSERT INTO TEST(id, ID_S_INOND) VALUES ('test_2', 's_inond_2');");
		database.query("INSERT INTO TEST(id, ID_S_INOND) VALUES ('test_3', 's_inond_1');");
		database.query("INSERT INTO TEST(id, ID_S_INOND) VALUES ('test_4', 's_inond_3');");
				
		//add the table TEST into the database
		database.query("CREATE TABLE TEST_B(id TEXT, ID_S_INOND TEXT, ID_TRI TEXT);");
		database.query("INSERT INTO TEST_B(id, ID_S_INOND, ID_TRI) VALUES ('test_b1', 's_inond_1', 'tri_1');");
		database.query("INSERT INTO TEST_B(id, ID_S_INOND, ID_TRI) VALUES ('test_b2', 's_inond_2', 'tri_3');");
		database.query("INSERT INTO TEST_B(id, ID_S_INOND, ID_TRI) VALUES ('test_b3', 's_inond_2', 'tri_2');");
		database.query("INSERT INTO TEST_B(id, ID_S_INOND, ID_TRI) VALUES ('test_b4', 's_inond_3', 'tri_1');");
				
		//add the table RELATION into the database
		database.query("CREATE TABLE N_prefixTri_INONDABLE_suffixInond_S_ddd(ID_S_INOND TEXT);");
		database.query("INSERT INTO N_prefixTri_INONDABLE_suffixInond_S_ddd(ID_S_INOND) VALUES ('s_inond_2');");
		database.query("INSERT INTO N_prefixTri_INONDABLE_suffixInond_S_ddd(ID_S_INOND) VALUES ('s_inond_3');");
				
		//add the table RELATION into the database
		database.query("CREATE TABLE N_prefixTri_TRI_S_ddd(ID_TRI TEXT);");
		database.query("INSERT INTO N_prefixTri_TRI_S_ddd(ID_TRI) VALUES ('tri_2');");
		database.query("INSERT INTO N_prefixTri_TRI_S_ddd(ID_TRI) VALUES ('tri_3');");
		
		//check that the relationValidator sends five errors 
		RelationValidator relationValidator = new RelationValidator();
		relationValidator.validate(context, document, database);

		Assert.assertEquals(5, reportBuilder.getErrorsByCode(DgprErrorCodes.DGPR_RELATION_ERROR).size());
		 
		ValidatorError refError0 = reportBuilder.getErrorsByCode(DgprErrorCodes.DGPR_RELATION_ERROR).get(0);
		ValidatorError refError1 = reportBuilder.getErrorsByCode(DgprErrorCodes.DGPR_RELATION_ERROR).get(1);
		ValidatorError refError2 = reportBuilder.getErrorsByCode(DgprErrorCodes.DGPR_RELATION_ERROR).get(2);
		ValidatorError refError3 = reportBuilder.getErrorsByCode(DgprErrorCodes.DGPR_RELATION_ERROR).get(3);
		ValidatorError refError4 = reportBuilder.getErrorsByCode(DgprErrorCodes.DGPR_RELATION_ERROR).get(4);
		
		Assert.assertEquals("L'objet test_1 de la table TEST fait référence à un objet s_inond_1 via l'attribut ID_S_INOND"
				+ " qui n'existe pas dans la table N_prefixTri_INONDABLE_suffixInond_S_ddd."
				, refError0.getMessage());
		
		Assert.assertEquals("L'objet test_3 de la table TEST fait référence à un objet s_inond_1 via l'attribut ID_S_INOND"
				+ " qui n'existe pas dans la table N_prefixTri_INONDABLE_suffixInond_S_ddd."
				, refError1.getMessage());
		
		Assert.assertEquals("L'objet test_b1 de la table TEST_B fait référence à un objet s_inond_1 via l'attribut ID_S_INOND"
				+ " qui n'existe pas dans la table N_prefixTri_INONDABLE_suffixInond_S_ddd."
				, refError2.getMessage());
		
		Assert.assertEquals("L'objet test_b1 de la table TEST_B fait référence à un objet tri_1 via l'attribut ID_TRI"
				+ " qui n'existe pas dans la table N_prefixTri_TRI_S_ddd."
				, refError3.getMessage());
		
		Assert.assertEquals("L'objet test_b4 de la table TEST_B fait référence à un objet tri_1 via l'attribut ID_TRI"
				+ " qui n'existe pas dans la table N_prefixTri_TRI_S_ddd."
				, refError4.getMessage());
	}

}
