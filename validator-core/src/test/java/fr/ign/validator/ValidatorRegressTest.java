package fr.ign.validator;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.geotools.referencing.CRS;

import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.loader.ModelLoader;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.report.InMemoryReportBuilder;
import junit.framework.TestCase;

/**
 * 
 * TODO move to validator-garden-test
 * 
 * @author MBorne
 *
 */
public class ValidatorRegressTest extends TestCase {

	private Context context;
	private DocumentModel documentModel;
	private InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();

	@Override
	protected void setUp() throws Exception {
		this.context = new Context();
		context.setCoordinateReferenceSystem(CRS.decode("EPSG:4326"));
		context.setReportBuilder(reportBuilder);
		File documentModelPath = new File(getClass().getResource("/config/cnig_PLU_2014/files.xml").getPath()) ;
		ModelLoader modelLoader = new ModelLoader();
		documentModel = modelLoader.loadDocumentModel(documentModelPath);
	}
	
	
	public void testCnigPlu2014(){
		File documentPath = new File(getClass().getResource("/documents/41003_PLU_20130903").getPath()) ;
		
		// clear generated CSV
		{
			@SuppressWarnings("unchecked")
			Collection<File> csvFiles = FileUtils.listFiles(documentPath, new String[]{"csv"}, true);
			for (File file : csvFiles) {
				System.out.println(file);
				file.delete();
			}
		}

		
		Validator validator = new Validator(context);
		try {
			validator.validate(documentModel, documentPath);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals("ISO-8859-1", context.getEncoding().toString());
		
		List<ValidatorError> errors = reportBuilder.getErrors();
		assertEquals(10,errors.size());
	}
	
}
