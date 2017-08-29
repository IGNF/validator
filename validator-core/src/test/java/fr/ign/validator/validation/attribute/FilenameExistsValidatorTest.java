package fr.ign.validator.validation.attribute;

import java.io.File;

import junit.framework.TestCase;
import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.model.type.PathType;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.validation.attribute.FilenameExistsValidator;


public class FilenameExistsValidatorTest extends TestCase {

	private Context context ;
	private InMemoryReportBuilder report ;
	private FilenameExistsValidator validator ;
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		validator = new FilenameExistsValidator();
		
		context = new Context();
		File currentDirectory = new File(getClass().getResource("/geofla").getPath()) ;
		context.setCurrentDirectory(currentDirectory);
		
		report = new InMemoryReportBuilder() ;
		context.setReportBuilder(report);
	}

	
	public void testExisting(){
		PathType type = new PathType();
		Attribute<File> attribute = new Attribute<File>(type, new File("files.xml"));
		validator.validate(context, attribute);
		assertEquals(0, report.countErrors() ) ;
	}
	public void testExistingWithFragment(){
		PathType type = new PathType();
		Attribute<File> attribute = new Attribute<File>(type, new File("files.xml#page=15"));
		validator.validate(context, attribute);
		assertEquals(0, report.countErrors() ) ;
	}
	
	
	public void testExistingInSubdirectory(){
		PathType type = new PathType();
		Attribute<File> attribute = new Attribute<File>(type, new File("COMMUNE.xml"));
		validator.validate(context, attribute);
		assertEquals(0, report.countErrors() ) ;
	}
	
	public void testExistingInSubdirectoryWithFragment(){
		PathType type = new PathType();
		Attribute<File> attribute = new Attribute<File>(type, new File("COMMUNE.xml#page=15"));
		validator.validate(context, attribute);
		assertEquals(0, report.countErrors() ) ;
	}
	

	public void testDoesntExists(){
		PathType type = new PathType();
		Attribute<File> attribute = new Attribute<File>(type, new File("je-nexiste-pas.txt"));
		validator.validate(context, attribute);
		assertEquals(1, report.countErrors() ) ;
	}

	public void testDoesntExistsIllegalCharacters(){
		PathType type = new PathType();
		String illegal = new String(new int[] { 0x0092 }, 0, 1);
		Attribute<File> attribute = new Attribute<File>(type, new File("je-nexiste-pas.txt#page=15"+illegal));
		validator.validate(context, attribute);
		assertEquals(1, report.countErrors() ) ;
	}
	
}
