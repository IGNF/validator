package fr.ign.validator.dgpr.validation.database;


import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.geotools.referencing.CRS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.database.Database;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.tools.ResourceHelper;
import fr.ign.validator.xml.XmlModelManager;

public class IdentifierValidatorTest {

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
		
		AttributeType<String> attribute = new StringType();
		attribute.setIdentifier(true);
		attribute.setName("id");
		
		List<AttributeType<?>> attributes = new ArrayList<>();
		attributes.add(attribute);
		
		FeatureType featureType = new FeatureType();
		featureType.setAttributes(attributes);
		
		FileModel fileModel = new TableModel();
		fileModel.setName("TEST");
		fileModel.setFeatureType(featureType);
		
		List<FileModel> fileModels = new ArrayList<>();
		fileModels.add(fileModel);
		
		DocumentModel documentModel = new DocumentModel();
		documentModel.setFileModels(fileModels);
		
		document = new Document(documentModel, new File("document"));
	}

	@Test
	public void testOk() throws Exception {
		File path = new File( folder.getRoot(), "document_database.db" );
		Database database = new Database(path);
		database.query("CREATE TABLE TEST(id TEXT, name TEXT);");
		database.query("INSERT INTO TEST(id, name) VALUES ('1', 'name01');");
		
		IdentifierValidator identifierValidator = new IdentifierValidator();
		identifierValidator.validate(context, document, database);
		
		// reportBuilder.getErrorsByCode(code);
	}

}
