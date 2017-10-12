package fr.ign.validator.model.type;

import org.junit.Ignore;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.report.InMemoryReportBuilder;
import junit.framework.TestCase;

@Ignore
public class AbstractTypeTest<T> extends TestCase {

	protected AttributeType<T> type;
	protected Context context ;
	protected InMemoryReportBuilder reportBuilder ;
	
	public AbstractTypeTest(AttributeType<T> type) {
		this.type = type;
		type.setName("test");
		
		context = new Context() ;
		reportBuilder = new InMemoryReportBuilder() ; 
		context.setReportBuilder(reportBuilder);
	}

	/**
	 * bind and validate attribute
	 * @param context
	 * @param inputValue
	 * @return
	 */
	protected T bindValidate(Context context, Object inputValue) {
		try {
			Attribute<T> attribute = new Attribute<T>(type, inputValue);
			attribute.validate(context);
			return attribute.getBindedValue() ;
		}catch ( IllegalArgumentException e ){
			context.report(
				CoreErrorCodes.ATTRIBUTE_INVALID_FORMAT, 
				inputValue.toString(), 
				type.getTypeName()
			);
			return null ;
		}
	}

}
