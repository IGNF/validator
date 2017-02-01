package fr.ign.validator.cnig.info;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.geotools.referencing.CRS;
import org.junit.Ignore;

import fr.ign.validator.Context;
import fr.ign.validator.Validator;
import fr.ign.validator.loader.ModelLoader;
import fr.ign.validator.model.Document;
import fr.ign.validator.model.DocumentModel;
import junit.framework.TestCase;

/**
 * 
 * TODO rendre ces tests moins sensibles et plus facile à diagnostiquer
 * @author MBorne
 *
 */
public class InfoExtractorTest extends TestCase {

    @Ignore
	public void testInfoExtractorDU_41175() throws Exception {
		File documentModelPath = new File(getClass().getResource("/config/cnig_PLU_2014/files.xml").getPath());
		ModelLoader loader = new ModelLoader();
		DocumentModel documentModel = loader.loadDocumentModel(documentModelPath);

		Context context = new Context();
		context.setCoordinateReferenceSystem(CRS.decode("EPSG:2154"));

		File documentPath = new File(getClass().getResource("/DU_41175/41175_PLU_20140603").getPath());
		Validator validator = new Validator(context);
		try {
			Document document = validator.validate(documentModel, documentPath);
			assertEquals("41175_PLU_20140603",document.getDocumentName());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		File producedInfosCnigPath = new File(getClass().getResource("/DU_41175/validation/infos-cnig.xml").getPath());
		File expectedInfosCnigPath = new File(getClass().getResource("/DU_41175/expected-infos-cnig.xml").getPath());

		String actual   = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		//TODO rendre moins sensible
		//assertEquals(expected, actual);
	}

    @Ignore
	public void testInfoExtractorDU_50545() throws Exception {
		File documentModelPath = new File(getClass().getResource("/config/cnig_CC_2014/files.xml").getPath());
		ModelLoader loader = new ModelLoader();
		DocumentModel documentModel = loader.loadDocumentModel(documentModelPath);

		Context context = new Context();
		context.setCoordinateReferenceSystem(CRS.decode("EPSG:2154"));

		File documentPath = new File(getClass().getResource("/DU_50545/50545_CC_20130902").getPath());
		Validator validator = new Validator(context);
		try {
			Document document = validator.validate(documentModel, documentPath);
			assertEquals("50545_CC_20130902",document.getDocumentName());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		File producedInfosCnigPath = new File(getClass().getResource("/DU_50545/validation/infos-cnig.xml").getPath());
		File expectedInfosCnigPath = new File(getClass().getResource("/DU_50545/expected-infos-cnig.xml").getPath());

		String actual   = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		//TODO rendre moins sensible
		//assertEquals(expected, actual);
	}

    @Ignore
	public void testInfoExtractorSUP_PM3_28() throws Exception {
		File documentModelPath = new File(getClass().getResource("/config/cnig_SUP_PM3_2013/files.xml").getPath());
		ModelLoader loader = new ModelLoader();
		DocumentModel documentModel = loader.loadDocumentModel(documentModelPath);

		Context context = new Context();
		context.setCoordinateReferenceSystem(CRS.decode("EPSG:2154"));

		File documentPath = new File(getClass().getResource("/SUP_PM3_28/110068012_PM3_28_20161104").getPath());
		Validator validator = new Validator(context);
		try {
			Document document = validator.validate(documentModel, documentPath);
			assertEquals("110068012_PM3_28_20161104",document.getDocumentName());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		File producedInfosCnigPath = new File(getClass().getResource("/SUP_PM3_28/validation/infos-cnig.xml").getPath());
		File expectedInfosCnigPath = new File(getClass().getResource("/SUP_PM3_28/expected-infos-cnig.xml").getPath());

		String actual   = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		
		//TODO rendre moins sensible
		//assertEquals(expected, actual);
	}

}
