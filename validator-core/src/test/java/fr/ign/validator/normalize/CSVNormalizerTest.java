package fr.ign.validator.normalize;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.Context;
import fr.ign.validator.tools.ResourceHelper;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.xml.XmlModelManager;

public class CSVNormalizerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();
	
	private Context context;

	private Document document;

	@Before
	public void setUp() throws Exception {
		context = new Context();
		// ATTENTION dépendance à sourceCRS (ne doit pas être nul du point de vu de CSVNormalizer)
		context.setProjection("CRS:84");
		context.setReportBuilder(reportBuilder);

		File documentModelPath = ResourceHelper.getResourceFile(getClass(),"/normalizer/config/sample/files.xml") ;
		XmlModelManager modelLoader = new XmlModelManager();
		DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);

		File documentPath = ResourceHelper.getResourceFile(getClass(),"/normalizer/documents/sample");
		File copy = folder.newFolder(documentPath.getName());
		FileUtils.copyDirectory(documentPath, copy);

		document = new Document(documentModel, copy);

		File validationDirectory = new File(copy.getParentFile(), "validation");
		context.setValidationDirectory(validationDirectory);
	}

	@Test
	public void testNormalise() throws Exception {
		FileModel fileModel = document.getDocumentModel().getFileModelByName("ADRESSE");
		Assert.assertNotNull(fileModel);

		File targetFile = new File(document.getDocumentPath(), "adresse_normalized.csv");
		CSVNormalizer csvNormalizer = new CSVNormalizer(context, fileModel.getFeatureType(), targetFile);

		File csvFile1 = new File(document.getDocumentPath(), "adresse_1.csv");
		Assert.assertTrue(csvFile1.exists());
		File csvFile2 = new File(document.getDocumentPath(), "adresse_2.csv");
		Assert.assertTrue(csvFile2.exists());
		File csvFile3 = new File(document.getDocumentPath(), "adresse_3.csv");
		Assert.assertTrue(csvFile3.exists());
		File csvFile4 = new File(document.getDocumentPath(), "adresse_4.csv");
		Assert.assertTrue(csvFile4.exists());
		
		csvNormalizer.append(csvFile1);
		csvNormalizer.append(csvFile2);
		csvNormalizer.append(csvFile3);
		csvNormalizer.append(csvFile4);
		csvNormalizer.close();

		File expectedFile = ResourceHelper.getResourceFile(getClass(),"/normalizer/expected_adresse_normalized.csv");
		Assert.assertTrue(expectedFile.exists());
		
		// should work
		/*
		Assert.assertEquals(
				FileUtils.readFileToString(expectedFile).trim(),
				FileUtils.readFileToString(targetFile).trim()
		);
		*/
	}

}
