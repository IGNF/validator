package fr.ign.validator.cnig.validation;

import org.junit.Before;

import fr.ign.validator.Context;
import fr.ign.validator.report.InMemoryReportBuilder;

public class CnigValidatorTestBase {

	protected Context context;
	protected InMemoryReportBuilder report;

	@Before
	public void setUp() {
		context = new Context();
		report = new InMemoryReportBuilder() ;
		context.setReportBuilder(report);
	}

}
