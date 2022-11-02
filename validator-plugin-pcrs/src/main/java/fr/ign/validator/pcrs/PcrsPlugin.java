package fr.ign.validator.pcrs;

import fr.ign.validator.Context;

import fr.ign.validator.plugin.Plugin;
import fr.ign.validator.process.DocumentInfoExtractorPostProcess;

/**
 * Customizes validator for PCRS standard validation
 * 
 * @author CBouche
 *
 */
public class PcrsPlugin implements Plugin {

    public static final String NAME = "PCRS";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setup(Context context) {
        context.setFlatValidation(true);
    }

}
