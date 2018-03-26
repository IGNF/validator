package fr.ign.validator.cnig.regress;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.referencing.CRS;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.xml.XmlModelManager;
import junit.framework.TestCase;

/**
 * 
 * Test de régression sur la validation avec le plugin CNIG activé avec contrôle de :
 * 
 * <ul>
 * 	 <li>La stabilité de cnig-info.xml<li>
 *   <li>La stabilité des erreurs (TODO : à améliorer)</li> 
 * </ul>
 * 
 * @author MBorne
 *
 */
public class CnigValidatorRegressTest extends TestCase {
	
	public static final Logger log = LogManager.getRootLogger() ;

	protected InMemoryReportBuilder report;
	
	@Override
	protected void setUp() throws Exception {
		report = new InMemoryReportBuilder() ;
	}
	
	
	private Context createContext(File documentPath) throws Exception{
		Context context = new Context();
		context.setReportBuilder(report);
		context.setCoordinateReferenceSystem(CRS.decode("EPSG:2154"));
		File validationDirectory = new File( documentPath.getParentFile(), "validation" ) ;
		context.setValidationDirectory( validationDirectory ) ;
		PluginManager pluginManager = new PluginManager();
		pluginManager.getPluginByName("CNIG").setup(context);		
		return context;
	}
	
	private DocumentModel getDocumentModel(String documentModelName) throws Exception {
		File documentModelPath = new File(getClass().getResource("/config/"+documentModelName+"/files.xml").getPath());
		XmlModelManager loader = new XmlModelManager();
		DocumentModel documentModel = loader.loadDocumentModel(documentModelPath);
		documentModel.setName(documentModelName);
		return documentModel;
	}

	/**
	 * Test PLU en standard 2014
	 * @throws Exception
	 */
	public void test41175_PLU_20140603() throws Exception {
		DocumentModel documentModel = getDocumentModel("cnig_PLU_2014");

		File documentPath = new File(getClass().getResource("/documents/41175_PLU_20140603").getPath());
		Context context = createContext(documentPath);
		Document document = new Document(documentModel,documentPath);
		try {
			document.validate(context);
			assertEquals("41175_PLU_20140603",document.getDocumentName());
			assertEquals(4,report.countErrors(ErrorLevel.ERROR));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		File producedInfosCnigPath = new File(getClass().getResource("/documents/validation/infos-cnig.xml").getPath());
		File expectedInfosCnigPath = new File(getClass().getResource("/documents/41175_PLU_20140603.infos-cnig.xml").getPath());

		String actual   = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		assertEquals(expected, actual);
	}

	/**
	 * Test CC en standard 2014
	 * @throws Exception
	 */
	public void test50545_CC_20130902() throws Exception {
		DocumentModel documentModel = getDocumentModel("cnig_CC_2014");

		File documentPath = new File(getClass().getResource("/documents/50545_CC_20130902").getPath());
		Context context = createContext(documentPath);
		Document document = new Document(documentModel,documentPath);
		try {
			document.validate(context);
			assertEquals("50545_CC_20130902",document.getDocumentName());
			assertEquals(3,report.countErrors(ErrorLevel.ERROR));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		File producedInfosCnigPath = new File(getClass().getResource("/documents/validation/infos-cnig.xml").getPath());
		File expectedInfosCnigPath = new File(getClass().getResource("/documents/50545_CC_20130902.infos-cnig.xml").getPath());

		String actual   = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test en standard 2017 (IDURBA change de format)
	 * @throws Exception
	 */
	public void test19182_CC_20150517() throws Exception {
		DocumentModel documentModel = getDocumentModel("cnig_CC_2017");

		File documentPath = new File(getClass().getResource("/documents/19182_CC_20150517").getPath());
		Context context = createContext(documentPath);
		Document document = new Document(documentModel,documentPath);
		try {
			document.validate(context);
			assertEquals("19182_CC_20150517",document.getDocumentName());
			assertEquals(0,report.countErrors(ErrorLevel.ERROR));		
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		File producedInfosCnigPath = new File(getClass().getResource("/documents/validation/infos-cnig.xml").getPath());
		File expectedInfosCnigPath = new File(getClass().getResource("/documents/19182_CC_20150517.infos-cnig.xml").getPath());

		String actual   = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		assertEquals(expected, actual);
	}


	public void testSUP_PM3_28() throws Exception {
		DocumentModel documentModel = getDocumentModel("cnig_SUP_PM3_2013");

		File documentPath = new File(getClass().getResource("/documents/110068012_PM3_28_20161104").getPath());
		Context context = createContext(documentPath);
		Document document = new Document(documentModel,documentPath);
		try {
			document.validate(context);
			assertEquals("110068012_PM3_28_20161104",document.getDocumentName());
			assertEquals(7,report.countErrors(ErrorLevel.ERROR));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		File producedInfosCnigPath = new File(getClass().getResource("/documents/validation/infos-cnig.xml").getPath());
		File expectedInfosCnigPath = new File(getClass().getResource("/documents/110068012_PM3_28_20161104.infos-cnig.xml").getPath());

		String actual   = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		assertEquals(expected, actual);
	}

}
