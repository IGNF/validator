package fr.ign.validator.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.model.file.TableModel;
import junit.framework.TestCase;

public class DocumentModelTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
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
	
	public void testFindFileModelByPathBadName(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("not_found.shp");
		FileModel fileModel = documentModel.FindFileModelByFilepath(path);
		assertNull(fileModel);
	}
	
	public void testFindFileModelByPathGoodNameBadPath(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("ACTE_SUP.shp");
		FileModel fileModel = documentModel.FindFileModelByFilepath(path);
		assertNull(fileModel);
	}
	
	
	public void testFindFileModelByPathGoodFilenameGoodPath(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("Donnees_geographiques/ACTE_SUP.dbf");
		FileModel fileModel = documentModel.FindFileModelByFilepath(path);
		assertNotNull(fileModel);
		assertEquals("ACTE_SUP", fileModel.getName());
	}
	
	public void testFindFileModelByPathLongerRegexp(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("Donnees_geographiques/SERVITUDE_ACTE_SUP.dbf");
		FileModel fileModel = documentModel.FindFileModelByFilepath(path);
		assertNotNull(fileModel);
		assertEquals("SERVITUDE_ACTE_SUP", fileModel.getName());
	}
	
	
	
	
	public void testFindFileModelByNameBadName(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("not_found.shp");
		FileModel fileModel = documentModel.findFileModelByFilename(path);
		assertNull(fileModel);
	}
	
	public void testFindFileModelByNameGoodNameBadPath(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("ACTE_SUP.shp");
		FileModel fileModel = documentModel.findFileModelByFilename(path);
		assertNull(fileModel);
	}
	
	
	public void testFindFileModelByNameGoodFilename(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("bad_folder/ACTE_SUP.dbf");
		FileModel fileModel = documentModel.findFileModelByFilename(path);
		assertNotNull(fileModel);
		assertEquals("ACTE_SUP", fileModel.getName());
	}
	
	public void testFindFileModelByNameLongerRegexp(){
		DocumentModel documentModel = createFakeDocumentModel();
		File path = new File("bad_folder/SERVITUDE_ACTE_SUP.dbf");
		FileModel fileModel = documentModel.findFileModelByFilename(path);
		assertNotNull(fileModel);
		assertEquals("SERVITUDE_ACTE_SUP", fileModel.getName());
	}

	
}
