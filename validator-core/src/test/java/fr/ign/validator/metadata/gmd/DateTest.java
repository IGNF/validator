package fr.ign.validator.metadata.gmd;

import org.junit.Test;

import fr.ign.validator.metadata.Date;
import junit.framework.TestCase;

public class DateTest extends TestCase {

	@Test
	public void testDate(){
		Date date = new Date("2015-08-28");
		assertTrue(date.isValid());
	}
	
	@Test
	public void testDate2(){
		Date date = new Date("-2015-08-28");
		assertTrue(date.isValid());
	}
	
	@Test
	public void testSeconds(){
		Date date = new Date("2015-08-28T12:48:06Z");
		assertTrue(date.isValid());
	}
	
	@Test
	public void testMicroSeconds(){
		Date date = new Date("2015-08-28T12:48:06.7163297Z");
		assertTrue(date.isValid());
	}
	
	// 2001-10-26T21:32:52
	@Test
	public void test1(){
		Date date = new Date("2001-10-26T21:32:52");
		assertTrue(date.isValid());
	}
	// 2001-10-26T21:32:52+02:00
	@Test
	public void test2(){
		Date date = new Date("2001-10-26T21:32:52+02:00");
		assertTrue(date.isValid());
	}
	// 2001-10-26T19:32:52Z
	@Test
	public void test3(){
		Date date = new Date("2001-10-26T19:32:52Z");
		assertTrue(date.isValid());
	}
	
	// 2001-10-26T19:32:52+00:00
	@Test
	public void test4(){
		Date date = new Date("2001-10-26T19:32:52+00:00");
		assertTrue(date.isValid());
	}

	// -2001-10-26T21:32:52
	@Test
	public void test5(){
		Date date = new Date("-2001-10-26T21:32:52");
		assertTrue(date.isValid());
	}
	
	@Test
	public void testNotValid1(){
		Date date = new Date("2015-08-28T12:48:0671AA63297Z");
		assertFalse(date.isValid());
	}
	
	@Test
	public void testNotValid2(){
		Date date = new Date("2001-26-10");
		assertFalse(date.isValid());
	}
}
