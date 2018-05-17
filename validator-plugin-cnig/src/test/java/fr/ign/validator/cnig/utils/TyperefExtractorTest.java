package fr.ign.validator.cnig.utils;

import java.io.File;

import fr.ign.validator.cnig.idurba.impl.IdurbaHelperV1;
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
		extractor = new TyperefExtractor(new IdurbaHelperV1()) ;
	}
	
	
	public void testNotFound(){
		File docUrbaFile = new File( 
			getClass().getResource("/csv/DOC_URBA.csv").getPath() 
		);
		assertNull( extractor.findTyperef(docUrbaFile,"test") ) ;
	}
	
	public void testFindStrictEquals(){
		File docUrbaFile = new File( 
			getClass().getResource("/csv/DOC_URBA.csv").getPath() 
		);
		assertEquals( "01", extractor.findTyperef(docUrbaFile,"50041_PLU_20130403") ) ;
		assertEquals( "01", extractor.findTyperef(docUrbaFile,"50648_POS_20030926") ) ;
	}
	
}
