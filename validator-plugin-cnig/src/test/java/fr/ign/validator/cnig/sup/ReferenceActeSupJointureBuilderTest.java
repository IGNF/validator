package fr.ign.validator.cnig.sup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.cnig.process.internal.ReferenceActeSupJointureBuilder;
import fr.ign.validator.tools.ResourceHelper;


public class ReferenceActeSupJointureBuilderTest {

	private ReferenceActeSupJointureBuilder jointureBuilder  ;
	
	@Before
	public void setUp() throws Exception {
		File dataDirectory = ResourceHelper.getResourceFile(getClass(),"/jointure_sup/one2one/DATA");
		jointureBuilder = new ReferenceActeSupJointureBuilder(dataDirectory) ;
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
