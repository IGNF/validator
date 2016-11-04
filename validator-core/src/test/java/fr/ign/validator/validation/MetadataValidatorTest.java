package fr.ign.validator.validation;

import java.io.File;
import fr.ign.validator.Context;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.DocumentFile;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.error.ValidatorError;
import junit.framework.TestCase;

public class MetadataValidatorTest extends TestCase {
	
	private Context context ;
	private InMemoryReportBuilder report ;
	private MetadataValidator validator ;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		validator = new MetadataValidator();
		
		context = new Context();
		File currentDirectory = new File(getClass().getResource("/metadata").getPath()) ;
		context.setCurrentDirectory(currentDirectory);
		
		report = new InMemoryReportBuilder() ;
		context.setReportBuilder(report);
	}

	public void testFileIdentifier(){
		
		FileModel fileModel = FileModel.newFileModelByType( "metadata") ;
		File filePath = new File(getClass().getResource("/metadata/fr-000053015-plu20140908.xml").getPath()) ;
		DocumentFile documentFile = new DocumentFile(fileModel,filePath);
		
		validator.validate(context, documentFile);
		
		assertEquals(0, report.countErrors() ) ;
	}
	
	public void testWithoutMDIdentifier(){
		
		FileModel fileModel = FileModel.newFileModelByType( "metadata") ;
		File filePath = new File(getClass().getResource("/metadata/fr-210800405-08042-plu20140213-without-MD_Identifier.xml").getPath()) ;
		DocumentFile documentFile = new DocumentFile(fileModel,filePath);
		
		validator.validate(context, documentFile);
		
		assertEquals(1, report.countErrors() ) ;
		
		assertEquals(
				"METADATA_MD_IDENTIFIER_NOT_FOUND|DIRECTORY|WARNING|Le champ \"MD_Identifier\" de la fiche de métadonnée fr-210800405-08042-plu20140213-without-MD_Identifier.xml n'est pas renseigné.", 
				report.getErrors().get(0).toString() 
				) ;
	}

	public void testEmptyFileIdentifier(){
		
		FileModel fileModel = FileModel.newFileModelByType( "metadata") ;
		File filePath = new File(getClass().getResource("/metadata/fr-130010622-SUP-A9-42.xml").getPath()) ;
		DocumentFile documentFile = new DocumentFile(fileModel,filePath);
		
		validator.validate(context, documentFile);
		
		assertEquals(2, report.countErrors() ) ;
		
		assertEquals(
				"METADATA_FILEIDENTIFIER_NOT_FOUND|DIRECTORY|ERROR|Le champ \"FileIdentifier\" de la fiche de métadonnée fr-130010622-SUP-A9-42.xml n'est pas renseigné.", 
				report.getErrors().get(0).toString() 
				) ;
		
		assertEquals(
				"METADATA_MD_IDENTIFIER_NOT_FOUND|DIRECTORY|WARNING|Le champ \"MD_Identifier\" de la fiche de métadonnée fr-130010622-SUP-A9-42.xml n'est pas renseigné.", 
				report.getErrors().get(1).toString() 
				) ;
	}

}