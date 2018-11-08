package fr.ign.validator.dgpr.validation.document;

import java.io.File;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.report.InMemoryReportBuilder;
import junit.framework.TestCase;

public class DocumentPrefixValidatorTest extends TestCase {

	private Context context;
	private InMemoryReportBuilder report;
	private DocumentPrefixValidator validator;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		validator = new DocumentPrefixValidator();

		context = new Context();
		report = new InMemoryReportBuilder() ;
		context.setReportBuilder(report);
	}

	public void testDocumentOk() {
		DocumentModel documentModel = new DocumentModel();
		File documentPath = new File("N_TRI_GREN2013_SIG_DI");

		Document document = new Document(documentModel, documentPath);

		validator.validate(context, document);

		assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR).size());
	}

	public void testDocumentError() {
		DocumentModel documentModel = new DocumentModel();
		File documentPath = new File("N_TRI_GREN2013_mauvais_suffix");

		Document document = new Document(documentModel, documentPath);

		validator.validate(context, document);

		assertEquals(1, report.getErrorsByCode(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR).size());
	}

}
