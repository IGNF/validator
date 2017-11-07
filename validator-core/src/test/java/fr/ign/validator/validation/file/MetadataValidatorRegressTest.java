package fr.ign.validator.validation.file;

import java.io.File;

import fr.ign.validator.Context;
import fr.ign.validator.data.file.MetadataFile;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.report.InMemoryReportBuilder;
import junit.framework.TestCase;

/**
 * High level regression test
 * @author MBorne
 *
 */
public class MetadataValidatorRegressTest extends TestCase {
	
	private Context context ;
	private InMemoryReportBuilder report ;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		context = new Context();
		File currentDirectory = new File(getClass().getResource("/metadata").getPath()) ;
		context.setCurrentDirectory(currentDirectory);
		
		report = new InMemoryReportBuilder() ;
		context.setReportBuilder(report);
	}

	public void test01(){
		MetadataModel fileModel = new MetadataModel();
		File filePath = new File(getClass().getResource("/metadata/01.xml").getPath()) ;
		MetadataFile documentFile = fileModel.createDocumentFile(filePath);
		documentFile.validate(context);
		assertEquals(0, report.countErrors() ) ;
	}
	

	public void test02(){
		MetadataModel fileModel = new MetadataModel() ;
		File filePath = new File(getClass().getResource("/metadata/02.xml").getPath()) ;

		MetadataFile documentFile = fileModel.createDocumentFile(filePath);
		documentFile.validate(context);
		
		assertEquals(14, report.countErrors() ) ;
		int i = 0 ;
		assertEquals(CoreErrorCodes.METADATA_FILEIDENTIFIER_NOT_FOUND, report.getErrors().get(i++).getCode());
		assertEquals(CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report.getErrors().get(i++).getCode());
		assertEquals(CoreErrorCodes.METADATA_LOCATOR_URL_NOT_FOUND, report.getErrors().get(i++).getCode());
		assertEquals(CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report.getErrors().get(i++).getCode());
		assertEquals(CoreErrorCodes.METADATA_LOCATOR_URL_NOT_FOUND, report.getErrors().get(i++).getCode());
		assertEquals(CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report.getErrors().get(i++).getCode());
		assertEquals(CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report.getErrors().get(i++).getCode());
		assertEquals(CoreErrorCodes.METADATA_LOCATOR_URL_NOT_FOUND, report.getErrors().get(i++).getCode());
		assertEquals(CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report.getErrors().get(i++).getCode());
		assertEquals(CoreErrorCodes.METADATA_LOCATOR_URL_NOT_FOUND, report.getErrors().get(i++).getCode());
		assertEquals(CoreErrorCodes.METADATA_IDENTIFIER_NOT_FOUND, report.getErrors().get(i++).getCode());
		assertEquals(CoreErrorCodes.METADATA_CHARACTERSET_NOT_FOUND, report.getErrors().get(i++).getCode());
		assertEquals(CoreErrorCodes.METADATA_SPATIALREPRESENTATIONTYPE_NOT_FOUND, report.getErrors().get(i++).getCode());
		assertEquals(CoreErrorCodes.METADATA_SPECIFICATIONS_EMPTY, report.getErrors().get(i++).getCode());
	}


	public void test03(){
		MetadataModel fileModel = new MetadataModel();
		File filePath = new File(getClass().getResource("/metadata/03.xml").getPath()) ;

		MetadataFile documentFile = fileModel.createDocumentFile(filePath);
		documentFile.validate(context);
		
		assertEquals(3, report.countErrors() ) ;
		
		int i = 0 ;
		assertEquals(
			CoreErrorCodes.METADATA_IDENTIFIER_NOT_FOUND, 
			report.getErrors().get(i++).getCode() 
		) ;
		assertEquals(
			CoreErrorCodes.METADATA_SPATIALRESOLUTIONS_EMPTY, 
			report.getErrors().get(i++).getCode() 
		) ;
		assertEquals(
			CoreErrorCodes.METADATA_SPECIFICATIONS_EMPTY, 
			report.getErrors().get(i++).getCode() 
		) ;
	}

	public void test04(){
		MetadataModel fileModel = new MetadataModel() ;
		File filePath = new File(getClass().getResource("/metadata/04.xml").getPath()) ;

		MetadataFile documentFile = fileModel.createDocumentFile(filePath);
		documentFile.validate(context);
		
		assertEquals(2, report.countErrors() ) ;
		int i = 0 ;
		assertEquals(
			CoreErrorCodes.METADATA_SPATIALRESOLUTIONS_EMPTY, 
			report.getErrors().get(i++).getCode() 
		) ;
		assertEquals(
			CoreErrorCodes.METADATA_SPECIFICATIONS_EMPTY, 
			report.getErrors().get(i++).getCode() 
		) ;
	}
	
	public void test05(){
		MetadataModel fileModel = new MetadataModel() ;
		File filePath = new File(getClass().getResource("/metadata/05.xml").getPath()) ;

		MetadataFile documentFile = fileModel.createDocumentFile(filePath);
		documentFile.validate(context);
		
		assertEquals(2, report.countErrors() ) ;
		int i = 0 ;
		assertEquals(
			CoreErrorCodes.METADATA_SPATIALREPRESENTATIONTYPE_NOT_FOUND, 
			report.getErrors().get(i++).getCode()
		);
		assertEquals(
			CoreErrorCodes.METADATA_SPATIALRESOLUTIONS_EMPTY, 
			report.getErrors().get(i++).getCode() 
		) ;
	}
}
