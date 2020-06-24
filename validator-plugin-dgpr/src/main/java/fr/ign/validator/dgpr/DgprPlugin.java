package fr.ign.validator.dgpr;

import fr.ign.validator.Context;
import fr.ign.validator.dgpr.validation.attribute.NumericCustomizer;
import fr.ign.validator.dgpr.process.LoadDocumentDatabasePostProcess;
import fr.ign.validator.dgpr.validation.document.DocumentPrefixValidator;
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
        /*
         * All database Validation
         */
        context.addListener(new LoadDocumentDatabasePostProcess());

        /*
         * Extends document validation - file name contains directory prefix
         */
        context.addListener(new DocumentPrefixValidator());

        /*
         * Extends document validation - Numeric validation
         */
        context.addListener(new NumericCustomizer());
    }

}
