package fr.ign.validator;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class ResourceHelperTest {
	
	@Test
	public void testGetResourcePath(){
		File file = ResourceHelper.getResourcePath("/jexiste.txt");
		Assert.assertTrue(file.exists());
	}

	@Test	
	public void testGetResourcePathMissing(){
		boolean exceptionThown = false;
		try {
			ResourceHelper.getResourcePath("/jexiste-pas.txt");			
		}catch(RuntimeException e){
			Assert.assertEquals(
				"Resource '/jexiste-pas.txt' not found",
				e.getMessage()
			);
			exceptionThown = true;
		}
		Assert.assertTrue(exceptionThown);
	}
	
}
