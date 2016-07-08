package fr.ign.validator.cnig.tools;

import java.util.HashMap;
import java.util.Map;

import fr.ign.validator.tools.ReferenceActeSupTools;
import junit.framework.TestCase;

/**
 * 
 * @author CBouche
 *
 */
public class ReferenceActeSupToolsTest extends TestCase {
	
	Map<String, String> actes ;
	Map<String, String> servitudes ;
	Map<String, String> generateurs ;
	Map<String, String> assiettes ;
	
	@Override
	protected void setUp() throws Exception {
		actes = new HashMap<String, String>() ;
		
		actes.put("10", "fichier1.pdf") ;
		actes.put("11", "fichier2.pdf") ;
		actes.put("12", "fichier3.pdf") ;
		actes.put("13", "fichier4.pdf") ;

		servitudes = new HashMap<String, String>() ;
		
		servitudes.put("100", "10|11") ;
		servitudes.put("101", "13|12") ;
		servitudes.put("102", "11") ;
		servitudes.put("103", "15|10|11|12") ;
		servitudes.put("104", "12;13") ;
		servitudes.put("105", "15") ;
		servitudes.put("106", "9") ;
		servitudes.put("107", "10;11") ;

		generateurs = new HashMap<String, String>() ;
		
		generateurs.put("204", "100|101") ;
		generateurs.put("205", "100") ;
		generateurs.put("206", "101") ;
		generateurs.put("207", "102") ;
		generateurs.put("208", "103") ;

		assiettes = new HashMap<String, String>() ;
		
		assiettes.put("308", "204") ;
		assiettes.put("309", "204") ;
		assiettes.put("310", "207") ;
		assiettes.put("311", "208") ;
	}
	
	
	@Override
	protected void tearDown() throws Exception {
		actes = null ;
		servitudes = null ;
		generateurs = null ;
		assiettes = null ;
	}
	
	public void testleftjoin0() {
		
		Map<String, String> join0 = ReferenceActeSupTools.leftjoin(servitudes, actes) ;
		assertTrue(join0.get("103").equals("fichier1.pdf|fichier2.pdf|fichier3.pdf"));
	}
	
	public void testleftjoin1() {
		
		Map<String, String> join0 = ReferenceActeSupTools.leftjoin(servitudes, actes) ;
		Map<String, String> join1 = ReferenceActeSupTools.leftjoin(generateurs, join0) ;
		
		assertTrue(join1.get("208").equals("fichier1.pdf|fichier2.pdf|fichier3.pdf"));
	}
	
	public void testleftjoin2() {
		
		Map<String, String> join = ReferenceActeSupTools.leftjoin(servitudes, actes) ;
		join = ReferenceActeSupTools.leftjoin(generateurs, join) ;
		join = ReferenceActeSupTools.leftjoin(assiettes, join) ;
		
		assertTrue(join.get("308").equals("fichier1.pdf|fichier2.pdf|fichier4.pdf|fichier3.pdf"));
		assertTrue(join.get("309").equals("fichier1.pdf|fichier2.pdf|fichier4.pdf|fichier3.pdf"));
		assertTrue(join.get("310").equals("fichier2.pdf"));
		assertTrue(join.get("311").equals("fichier1.pdf|fichier2.pdf|fichier3.pdf"));
	}
}
