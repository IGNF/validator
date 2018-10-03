package fr.ign.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import fr.ign.validator.process.CharsetPreProcess;
import fr.ign.validator.process.DocumentInfoExtractorPostProcess;
import fr.ign.validator.process.FilterMetadataPreProcess;
import fr.ign.validator.process.NormalizePostProcess;


public class ContextTest {

	/**
	 * Test behavior for default constructor
	 */
	@Test
	public void testDefaultConstructor(){
		Context context = new Context();
		/* default data encoding is UTF-8 */
		Assert.assertEquals("UTF-8",context.getEncoding().name());
		
		Assert.assertFalse(context.isFlatValidation());
	}


	@Test
	public void testDefaultListeners(){
		Context context = new Context();
		List<ValidatorListener> listeners = context.getValidatorListeners();
		Assert.assertEquals(4,listeners.size());
		// order is important between FilterMetadataPreProcess and CharsetPreProcess
		Assert.assertEquals(FilterMetadataPreProcess.class,listeners.get(0).getClass());
		Assert.assertEquals(CharsetPreProcess.class,listeners.get(1).getClass());
		Assert.assertEquals(NormalizePostProcess.class,listeners.get(2).getClass());
		Assert.assertEquals(DocumentInfoExtractorPostProcess.class,listeners.get(3).getClass());
	}
	
	@Test
	public void testAddListenerBefore(){
		ValidatorListener fakeListener = Mockito.mock(ValidatorListener.class);
		Context context = new Context();
		context.addListenerBefore(fakeListener,NormalizePostProcess.class);
		
		List<ValidatorListener> listeners = context.getValidatorListeners();
		Assert.assertEquals(5,listeners.size());
		// order is important between FilterMetadataPreProcess and CharsetPreProcess
		Assert.assertEquals(FilterMetadataPreProcess.class,listeners.get(0).getClass());
		Assert.assertEquals(CharsetPreProcess.class,listeners.get(1).getClass());
		Assert.assertSame(fakeListener,listeners.get(2));
		Assert.assertEquals(NormalizePostProcess.class,listeners.get(3).getClass());
		Assert.assertEquals(DocumentInfoExtractorPostProcess.class,listeners.get(4).getClass());
	}
}
