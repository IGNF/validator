package fr.ign.validator.dgpr;

import fr.ign.validator.Context;
import fr.ign.validator.dgpr.validation.attribute.NumericCustomizer;
import fr.ign.validator.dgpr.process.CreateTheGeomColumn;
import fr.ign.validator.dgpr.process.CustomizeDatabaseValidation;
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
        context.addListener(new CustomizeDatabaseValidation());

        /*
         * Extends document validation - file name contains directory prefix
         */
        context.addListener(new DocumentPrefixValidator());

        /*
         * Extends document validation - Numeric validation
         */
        context.addListener(new NumericCustomizer());

        /*
         * Compute column the_geom to ease global database integration.
         */
        context.addListener(new CreateTheGeomColumn());
    }

}
