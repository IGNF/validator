package fr.ign.validator.validation.document;

import java.io.File;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.validation.document.DocumentDirectoryNameValidator;
import junit.framework.TestCase;

public class DocumentDirectoryNameValidatorTest extends TestCase {
	
	private Context context ;
	private InMemoryReportBuilder report ;
	private DocumentDirectoryNameValidator validator ;
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		validator = new DocumentDirectoryNameValidator();
		
		context = new Context();
		File currentDirectory = new File(getClass().getResource("/geofla").getPath()) ;
		context.setCurrentDirectory(currentDirectory);
		
		report = new InMemoryReportBuilder() ;
		context.setReportBuilder(report);
	}

	
	public void testNoRegexp(){
		DocumentModel documentModel = new DocumentModel();
		File documentPath = new File("/my/path/to/130009970_PM1_59_20160623");
		Document document = new Document(documentModel,documentPath);
		validator.validate(context, document);
		assertEquals(0, report.countErrors() ) ;
	}
	
	public void testGoodRegexp(){
		DocumentModel documentModel = new DocumentModel();
		documentModel.setRegexp(".*_PM1_((0)?2A|(0)?2B|[0-9]{2,3})_[0-9]{8}");

		File documentPath = new File("/my/path/to/130009970_PM1_59_20160623");
		Document document = new Document(documentModel,documentPath);
		validator.validate(context, document);
		assertEquals(0, report.countErrors() ) ;
	}
	
	public void testGoodRegexpTrailingSlash(){
		DocumentModel documentModel = new DocumentModel();
		documentModel.setRegexp(".*_PM1_((0)?2A|(0)?2B|[0-9]{2,3})_[0-9]{8}");

		File documentPath = new File("/my/path/to/130009970_PM1_59_20160623/");
		Document document = new Document(documentModel,documentPath);
		validator.validate(context, document);
		assertEquals(0, report.countErrors() ) ;
	}
	
	public void testCaseInsensitive(){
		DocumentModel documentModel = new DocumentModel();
		documentModel.setRegexp(".*_SCOT");

		File documentPath = new File("/my/path/to/123456789_scot/");
		Document document = new Document(documentModel,documentPath);
		validator.validate(context, document);
		assertEquals(0, report.countErrors() ) ;
	}
	
	
	public void testBadRegexp(){
		DocumentModel documentModel = new DocumentModel();
		documentModel.setRegexp(".*_PM1_((0)?2A|(0)?2B|[0-9]{2,3})_[0-9]{8}");

		File documentPath = new File("/my/path/to/130009970_PM2_59_20160623");
		Document document = new Document(documentModel,documentPath);
		validator.validate(context, document);
		assertEquals(1, report.countErrors() ) ;
	}
	
	
	
}
