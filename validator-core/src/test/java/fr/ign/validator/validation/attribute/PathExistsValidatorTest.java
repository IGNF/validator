package fr.ign.validator.validation.attribute;

import java.io.File;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.model.type.PathType;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.validation.attribute.PathExistsValidator;
import junit.framework.TestCase;

public class PathExistsValidatorTest extends TestCase {

	private Context context ;
	private InMemoryReportBuilder report ;
	private PathExistsValidator validator ;
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		validator = new PathExistsValidator();
		
		context = new Context();
		File currentDirectory = new File(getClass().getResource("/").getPath()) ;
		context.setCurrentDirectory(currentDirectory);
		
		report = new InMemoryReportBuilder() ;
		context.setReportBuilder(report);
	}

	
	public void testExisting(){
		PathType type = new PathType();
		Attribute<File> attribute = new Attribute<File>(type, new File("jexiste.txt"));
		validator.validate(context, attribute);
		assertEquals(0, report.countErrors() ) ;
	}
	
	
	public void testExistingWithFragment(){
		PathType type = new PathType();
		Attribute<File> attribute = new Attribute<File>(type, new File("jexiste.txt#page=12"));
		validator.validate(context, attribute);
		assertEquals(0, report.countErrors() ) ;
	}
	

	public void testDoesntExists(){
		PathType type = new PathType();
		Attribute<File> attribute = new Attribute<File>(type, new File("je-nexiste-pas.txt"));
		validator.validate(context, attribute);
		assertEquals(1, report.countErrors() ) ;
	}
	
	
}
