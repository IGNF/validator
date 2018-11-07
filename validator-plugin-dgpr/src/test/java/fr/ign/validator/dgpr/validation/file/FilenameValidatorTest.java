package fr.ign.validator.dgpr.validation.file;

import java.io.File;
import java.io.IOException;

import fr.ign.validator.Context;
import fr.ign.validator.data.file.TableFile;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.dgpr.validation.file.FilenamePrefixValidator;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.report.InMemoryReportBuilder;
import junit.framework.TestCase;

public class FilenameValidatorTest extends TestCase {

	private Context context;
	private InMemoryReportBuilder report;
	private FilenamePrefixValidator validator;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		validator = new FilenamePrefixValidator();

		context = new Context();
		report = new InMemoryReportBuilder() ;
		context.setReportBuilder(report);

		context.setCurrentDirectory(new File("N_TRI_GREN2013_SIG_DI"));
	}

	public void testFilenameCorrect() throws IOException {
		File file = new File("N_TRI_GREN2013_CARTE_INOND_S_038.tab");
		TableModel tableModel = new TableModel();
		TableFile documentFile = new TableFile(tableModel, file);

		validator.validate(context, documentFile);
		assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());
	}

	public void testFilenameError() throws IOException {
		File file = new File("nom_de_fichier.tab");
		TableModel tableModel = new TableModel();
		TableFile documentFile = new TableFile(tableModel, file);

		validator.validate(context, documentFile);
		assertEquals(1, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());
		assertEquals("Le nom du fichier nom_de_fichier.tab ne contient pas le prefix TRI du repertoire N_TRI_GREN2013.", report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).get(0).getMessage());
	}
	
	public void testDirectorynameError() throws IOException {
		context.setCurrentDirectory(new File("N_TRI_GREN2013_mauvais_suffix"));
		
		File file = new File("N_TRI_GREN2013_CARTE_INOND_S_038.tab");
		TableModel tableModel = new TableModel();
		TableFile documentFile = new TableFile(tableModel, file);

		validator.validate(context, documentFile);
		assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());
	}
	
	public void testDirectoryAndFilenameError() throws IOException {
		context.setCurrentDirectory(new File("N_TRI_GREN2013_mauvais_suffix"));
		
		File file = new File("nom_de_fichier.tab");
		TableModel tableModel = new TableModel();
		TableFile documentFile = new TableFile(tableModel, file);

		validator.validate(context, documentFile);
		assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());
	}

}
