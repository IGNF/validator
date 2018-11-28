package fr.ign.validator.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.validator.model.file.TableModel;

public class DocumentModelTest {

	/**
	 * Helper to create a fake DocumentModel
	 * @return
	 */
	private DocumentModel createFakeDocumentModel(){
		DocumentModel documentModel = new DocumentModel();
		List<FileModel> fileModels = new ArrayList<>();
		{
			FileModel fileModel = new TableModel();
			fileModel.setName("ACTE_SUP");
			fileModel.setRegexp("Donnees_geographiques/([a-zA-Z0-9]*_)?ACTE_SUP");
			fileModels.add(fileModel);
		}
		{
			FileModel fileModel = new TableModel();
			fileModel.setName("SERVITUDE_ACTE_SUP");			
			fileModel.setRegexp("Donnees_geographiques/([a-zA-Z0-9]*_)?SERVITUDE_ACTE_SUP");
			fileModels.add(fileModel);
		}
		documentModel.setFileModels(fileModels);
		return documentModel;
	}
	
	@Test
	public void testFindFileModelByPathBadName(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("not_found.shp");
		FileModel fileModel = documentModel.FindFileModelByFilepath(path);
		Assert.assertNull(fileModel);
	}
	
	@Test
	public void testFindFileModelByPathGoodNameBadPath(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("ACTE_SUP.shp");
		FileModel fileModel = documentModel.FindFileModelByFilepath(path);
		Assert.assertNull(fileModel);
	}
	
	@Test
	public void testFindFileModelByPathGoodFilenameGoodPath(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("Donnees_geographiques/ACTE_SUP.dbf");
		FileModel fileModel = documentModel.FindFileModelByFilepath(path);
		Assert.assertNotNull(fileModel);
		Assert.assertEquals("ACTE_SUP", fileModel.getName());
	}
	
	@Test
	public void testFindFileModelByPathLongerRegexp(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("Donnees_geographiques/SERVITUDE_ACTE_SUP.dbf");
		FileModel fileModel = documentModel.FindFileModelByFilepath(path);
		Assert.assertNotNull(fileModel);
		Assert.assertEquals("SERVITUDE_ACTE_SUP", fileModel.getName());
	}

	@Test
	public void testFindFileModelByNameBadName(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("not_found.shp");
		FileModel fileModel = documentModel.findFileModelByFilename(path);
		Assert.assertNull(fileModel);
	}

	@Test
	public void testFindFileModelByNameGoodNameBadPath(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("ACTE_SUP.shp");
		FileModel fileModel = documentModel.findFileModelByFilename(path);
		Assert.assertNull(fileModel);
	}

	@Test
	public void testFindFileModelByNameGoodFilename(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("bad_folder/ACTE_SUP.dbf");
		FileModel fileModel = documentModel.findFileModelByFilename(path);
		Assert.assertNotNull(fileModel);
		Assert.assertEquals("ACTE_SUP", fileModel.getName());
	}

	@Test
	public void testFindFileModelByNameLongerRegexp(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("bad_folder/SERVITUDE_ACTE_SUP.dbf");
		FileModel fileModel = documentModel.findFileModelByFilename(path);
		Assert.assertNotNull(fileModel);
		Assert.assertEquals("SERVITUDE_ACTE_SUP", fileModel.getName());
	}

	
}
