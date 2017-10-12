package fr.ign.validator.regress;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.DirectoryModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.model.file.PdfModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;
import junit.framework.TestCase;

public class ProjectARegressTest extends TestCase {

	private File documentPath ;
	
	private DocumentModel documentModel;
	
	@Override
	protected void setUp() throws Exception {
		documentPath = new File(getClass().getResource("/regress/document-a").getPath());
		
		documentModel = new DocumentModel();
		List<FileModel> fileModels = new ArrayList<FileModel>();
		{
			TableModel tableModel = new TableModel();
			tableModel.setName("COMMUNE");
			tableModel.setRegexp("commune");
			
			FeatureType featureType = new FeatureType();
			// INSEE
			{
				StringType attributeType = new StringType();
				attributeType.setName("INSEE");
				featureType.addAttribute(attributeType);
			}
			// NOM
			{
				StringType attributeType = new StringType();
				attributeType.setName("NOM");
				featureType.addAttribute(attributeType);
			}
			// WKT
			{
				GeometryType attributeType = new GeometryType();
				attributeType.setName("WKT");
				featureType.addAttribute(attributeType);
			}
			tableModel.setFeatureType(featureType);
			
			fileModels.add(tableModel);
		}
		{
			MetadataModel metadata = new MetadataModel();
			metadata.setName("metadata");
			metadata.setRegexp("metadata");
			fileModels.add(metadata);
		}
		{
			DirectoryModel directory = new DirectoryModel();
			directory.setName("a_directory");
			directory.setRegexp("a_directory");
			fileModels.add(directory);
		}
		{
			PdfModel directory = new PdfModel();
			directory.setName("a_file");
			directory.setRegexp("a_directory/a_file");
			fileModels.add(directory);
		}
		
		documentModel.setFileModels(fileModels);
	}

	
	
	public void testValidate() throws NoSuchAuthorityCodeException, FactoryException{
		Context context = new Context();
		context.setCurrentDirectory(documentPath);
		context.setCoordinateReferenceSystem(CRS.decode("EPSG:4326"));
		Document document = new Document(documentModel,documentPath);
		File validationDirectory = new File( documentPath.getParentFile(), "validation" ) ;
		context.setValidationDirectory( validationDirectory ) ;
		InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();
		context.setReportBuilder(reportBuilder);
		
		try {
			document.validate(context);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		List<ValidatorError> errors = reportBuilder.getErrors();
		assertEquals(2,errors.size());
		assertEquals(CoreErrorCodes.METADATA_SPATIALRESOLUTIONS_EMPTY,errors.get(0).getCode());
		assertEquals(CoreErrorCodes.METADATA_SPECIFICATIONS_EMPTY,errors.get(1).getCode());

		File expectedNormalized = new File( context.getDataDirectory(), "COMMUNE.csv");
		assertTrue(expectedNormalized.exists());		
		
		// from metadata
		assertEquals( StandardCharsets.ISO_8859_1, context.getEncoding() ) ;
	}
	
	
}
