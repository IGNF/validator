package fr.ign.validator.cnig.validation.attribute;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.idurba.impl.IdurbaHelperV1;
import fr.ign.validator.cnig.idurba.impl.IdurbaHelperV2;
import fr.ign.validator.cnig.validation.CnigValidatorTestBase;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.type.StringType;

public class IdurbaValidatorTest extends CnigValidatorTestBase {

	@Test
	public void testNotValid(){
		StringType type = new StringType();
		Attribute<String> attribute = new Attribute<String>(type,"test");
		IdurbaValidator validator = new IdurbaValidator(new IdurbaHelperV1());
		validator.validate(context, attribute);
		assertEquals(1,report.countErrors(ErrorLevel.WARNING));
	}

	@Test
	public void testValidV1(){
		StringType type = new StringType();
		Attribute<String> attribute = new Attribute<String>(type,"25349_20140101");
		IdurbaValidator validator = new IdurbaValidator(new IdurbaHelperV1());
		validator.validate(context, attribute);
		assertEquals(0,report.countErrors());
	}

	@Test
	public void testValidNotValidV1(){
		StringType type = new StringType();
		Attribute<String> attribute = new Attribute<String>(type,"25349_PLU_20140101");
		IdurbaValidator validator = new IdurbaValidator(new IdurbaHelperV1());
		validator.validate(context, attribute);
		assertEquals(1,report.countErrors());
		
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CnigErrorCodes.CNIG_IDURBA_INVALID, error.getCode());
		assertEquals("La valeur du champ \"IDURBA\" (25349_PLU_20140101) ne respecte pas le format attendu (<INSEE/SIREN><DATAPPRO>).", error.getMessage());
	}
	
	@Test
	public void testValidV2(){
		StringType type = new StringType();
		Attribute<String> attribute = new Attribute<String>(type,"25349_PLU_20140101");
		IdurbaValidator validator = new IdurbaValidator(new IdurbaHelperV2());
		validator.validate(context, attribute);
		assertEquals(0,report.countErrors());
	}

	@Test
	public void testValidNotValidV2(){
		StringType type = new StringType();
		Attribute<String> attribute = new Attribute<String>(type,"25349_20140101");
		IdurbaValidator validator = new IdurbaValidator(new IdurbaHelperV2());
		validator.validate(context, attribute);
		assertEquals(1,report.countErrors());
		
		ValidatorError error = report.getErrors().get(0);
		assertEquals(CnigErrorCodes.CNIG_IDURBA_INVALID, error.getCode());
		assertEquals("La valeur du champ \"IDURBA\" (25349_20140101) ne respecte pas le format attendu (<INSEE/SIREN>_<TYPEDOC>_<DATAPPRO>{_CodeDU}).", error.getMessage());
	}
	
}
