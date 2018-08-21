package fr.ign.validator.regress;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.geotools.referencing.CRS;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.xml.XmlModelManager;
import junit.framework.TestCase;

/**
 * @author MBorne
 *
 */
public class ValidateRegressTest extends TestCase {

	private Context context;
	private DocumentModel documentModel;
	private InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();

	@Override
	protected void setUp() throws Exception {
		this.context = new Context();
		context.setCoordinateReferenceSystem(CRS.decode("EPSG:4326"));
		context.setReportBuilder(reportBuilder);
		File documentModelPath = new File(getClass().getResource("/config/cnig_PLU_2014/files.xml").getPath()) ;
		XmlModelManager modelLoader = new XmlModelManager();
		documentModel = modelLoader.loadDocumentModel(documentModelPath);
	}
	
	
	public void testCnigPlu2014(){
		File documentPath = new File(getClass().getResource("/documents/41003_PLU_20130903").getPath()) ;
		
		// clear generated CSV
		{
			@SuppressWarnings("unchecked")
			Collection<File> csvFiles = FileUtils.listFiles(documentPath, new String[]{"csv"}, true);
			for (File file : csvFiles) {
				file.delete();
			}
		}

		
		Document document = new Document(documentModel,documentPath);
		File validationDirectory = new File( documentPath.getParentFile(), "validation" ) ;
		context.setValidationDirectory( validationDirectory ) ;
		try {
			document.validate(context);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals("ISO-8859-1", context.getEncoding().toString());
		
		List<ValidatorError> errors = reportBuilder.getErrors();
		assertEquals(11,errors.size());
	}
	
}
