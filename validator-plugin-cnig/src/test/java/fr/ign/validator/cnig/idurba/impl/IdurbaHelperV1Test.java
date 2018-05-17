package fr.ign.validator.cnig.idurba.impl;

import junit.framework.TestCase;

public class IdurbaHelperV1Test extends TestCase {
	
	private IdurbaHelperV1 idurbaHelper;
	
	@Override
	protected void setUp() throws Exception {
		this.idurbaHelper = new IdurbaHelperV1();
	}
	
	public void testValidInseeWithSeparator(){
		assertTrue(idurbaHelper.isValid("25349_20140101"));
		assertTrue(idurbaHelper.isValid("2B111_20140101"));
	}

	public void testValidInseeWithoutSeparator(){
		assertTrue(idurbaHelper.isValid("2534920140101"));
	}
	
	public void testNotValid(){
		assertFalse(idurbaHelper.isValid(null)) ;
		assertFalse(idurbaHelper.isValid("a254"));
		assertFalse(idurbaHelper.isValid("25349X_20140101")); // bad insee
		assertFalse(idurbaHelper.isValid("25349_201401015")); // bad date
	}
	
	public void testNotValidStyle2017(){
		assertFalse(idurbaHelper.isValid("25349_CC_20180101"));
	}
	
	public void testGetHelpFormat(){
		assertEquals("<INSEE/SIREN><DATAPPRO>", idurbaHelper.getHelpFormat());
	}	
	
	public void testGetHelpExpected(){
		assertEquals("25349.*20180101.*", idurbaHelper.getHelpExpected("25349_PLU_20180101"));
	}

}

