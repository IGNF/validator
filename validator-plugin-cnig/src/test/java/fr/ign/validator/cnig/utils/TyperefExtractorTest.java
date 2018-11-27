package fr.ign.validator.cnig.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.cnig.idurba.impl.IdurbaHelperV1;


/**
 * Test sur l'extraction de typeref
 * @author MBorne
 *
 */
public class TyperefExtractorTest {

	private TyperefExtractor extractor ;
	
	@Before
	public void setUp() throws Exception {
		extractor = new TyperefExtractor(new IdurbaHelperV1()) ;
	}

	@Test
	public void testNotFound(){
		File docUrbaFile = new File( 
			getClass().getResource("/csv/DOC_URBA.csv").getPath() 
		);
		assertNull( extractor.findTyperef(docUrbaFile,"test") ) ;
	}

	@Test
	public void testFindStrictEquals(){
		File docUrbaFile = new File( 
			getClass().getResource("/csv/DOC_URBA.csv").getPath() 
		);
		assertEquals( "01", extractor.findTyperef(docUrbaFile,"50041_PLU_20130403") ) ;
		assertEquals( "01", extractor.findTyperef(docUrbaFile,"50648_POS_20030926") ) ;
	}
	
}
