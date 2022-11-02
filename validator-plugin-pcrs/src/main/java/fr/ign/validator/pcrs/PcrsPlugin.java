package fr.ign.validator.pcrs;

import fr.ign.validator.Context;

import fr.ign.validator.plugin.Plugin;
import fr.ign.validator.process.DocumentInfoExtractorPostProcess;

/**
 * Customizes validator for CNIG standard validation
 * 
 * @see <a href=
 *      "http://www.geoportail-urbanisme.gouv.fr/standard">http://www.geoportail-urbanisme.gouv.fr/standard</a>
 * 
 * @author MBorne
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
        context.setNormalizeEnabled(true);
    }

}
