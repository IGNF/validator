package fr.ign.validator.tools;

import java.io.File;

import junit.framework.TestCase;

/**
 * Test sur l'extraction de typeref
 * @author MBorne
 *
 */
public class TyperefExtractorTest extends TestCase {

	private TyperefExtractor extractor ;
	
	@Override
	protected void setUp() throws Exception {
		File docUrbaFile = new File( 
			getClass().getResource("/csv/DOC_URBA.csv").getPath() 
		);
		extractor = new TyperefExtractor(docUrbaFile) ;
	}
	
	
	public void testNotFound(){
		assertNull( extractor.findTyperef("test") ) ;
	}
	
	public void testFindStrictEquals(){
		assertEquals( "01", extractor.findTyperef("50041_PLU_20130403") ) ;
		assertEquals( "01", extractor.findTyperef("50648_POS_20030926") ) ;
	}
	
}
