package fr.ign.validator.cnig.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.Context;


public class ReferenceActeSupJointureBuilderTest {

	private Context context ;
	
	private ReferenceActeSupJointureBuilder jointureBuilder  ;
	
	@Before
	public void setUp() throws Exception {
		context = new Context();
		File currentDirectory = new File(getClass().getResource("/jointure_sup_one2one").getPath()) ;
		context.setCurrentDirectory(currentDirectory);
		
		File validationDirectory = new File(getClass().getResource("/jointure_sup_one2one/validation").getPath()) ;
		context.setValidationDirectory(validationDirectory);
		
		jointureBuilder = new ReferenceActeSupJointureBuilder(context) ;
	}
	
	@Test
	public void testFindActe(){
		File file = jointureBuilder.findFile("ACTE_SUP") ;
		assertNotNull(file);
		assertEquals("ACTE_SUP.csv", file.getName());
	} 
	
	@Test	
	public void testFindServiceActeSup(){
		File file = jointureBuilder.findFile("SERVITUDE_ACTE_SUP") ;
		assertNotNull(file);
		assertEquals("SERVITUDE_ACTE_SUP.csv", file.getName());
	}
	
	@Test	
	public void testFindRegexpFiles(){
		List<File> files = jointureBuilder.findRegexpFiles("(?i).*_GENERATEUR_SUP_.*") ;
		assertEquals(1,files.size());
		File generateurFile = files.get(0);
		assertEquals("AC2_GENERATEUR_SUP_S.csv", generateurFile.getName());
	}

}
