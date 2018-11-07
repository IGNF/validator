package fr.ign.validator.dgpr;

import fr.ign.validator.Context;
import fr.ign.validator.dgpr.process.SomeActionPostProcess;
import fr.ign.validator.dgpr.validation.document.DocumentPrefixValidator;
import fr.ign.validator.dgpr.validation.document.SomeTestValidator;
import fr.ign.validator.dgpr.validation.file.FilenamePrefixValidator;
import fr.ign.validator.plugin.Plugin;

/**
 *
 */
public class DgprPlugin implements Plugin {

	public static final String NAME = "DGPR";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setup(Context context) {
		// Post process has to be ordered
		context.addListener(new SomeActionPostProcess());

		/*
		 * extends validation
		 */
		context.addListener(new SomeTestValidator());

		// file name contains directory prefix
		context.addListener(new DocumentPrefixValidator());
		context.addListener(new FilenamePrefixValidator());
	}

}
