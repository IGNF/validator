package fr.ign.validator.tools;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import fr.ign.validator.tools.internal.FixGML;
import junit.framework.TestCase;

public class FixGMLTest extends TestCase {
	
	@Test
	public void testRegexp(){
		String input = "AAAAAA<gp-urba:NOMFIC/>BBBBB" ;
		String output = FixGML.replaceAutoclosedByEmpty(input) ;
		assertEquals("AAAAAA<gp-urba:NOMFIC></gp-urba:NOMFIC>BBBBB",output);
	}
		
	
	public void testFixFile(){
		File input = new File(getClass().getResource("/fixgml/ZONE_URBA.gml").getPath()) ;
		File output = new File(input.getParentFile(), "ZONE_URBA_FIXED.gml");
		try {
			String inputContent = org.apache.commons.io.FileUtils.readFileToString(input,"UTF-8");
			assertTrue( inputContent.contains("<gp-urba:NOMFIC/>") ) ;			
			FixGML.replaceAutoclosedByEmpty(input,output);
			String outputContent = org.apache.commons.io.FileUtils.readFileToString(output,"UTF-8");
			assertTrue( outputContent.contains("<gp-urba:NOMFIC></gp-urba:NOMFIC>") ) ;			
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Ensures file stays encoded in UTF-8
	 */
	public void testFixFileUtf8(){
		File input = new File(getClass().getResource("/fixgml/EL10_ASSIETTE_SUP_S_FR.gml").getPath()) ;
		File output = new File(input.getParentFile(), "EL10_ASSIETTE_SUP_S_FR_FIXED.gml");
		try {
			String inputContent = org.apache.commons.io.FileUtils.readFileToString(input,"UTF-8");
			assertTrue( inputContent.contains("<GPU:MODEGEOASS>Egal au générateur</GPU:MODEGEOASS>") ) ;			
			FixGML.replaceAutoclosedByEmpty(input,output);
			String outputContent = org.apache.commons.io.FileUtils.readFileToString(output,"UTF-8");
			assertTrue( outputContent.contains("<GPU:MODEGEOASS>Egal au générateur</GPU:MODEGEOASS>") ) ;			
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	

}
