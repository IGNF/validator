package fr.ign.validator.cnig.tools;

import org.apache.commons.lang.StringUtils;

import fr.ign.validator.cnig.model.DocumentName;

/**
 *
 * Helper to validate IDURBA format according to CNIG standards v2017 or more.
 *
 * @author MBorne
 *
 */
public class IdurbaFormatSCOT implements IdurbaFormat {

    private static final String REGEXP_HELP = "<SIREN>_SCOT_<DATAPPRO>{_CodeDU}";

    @Override
    public boolean isValid(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        return value.matches(DocumentName.getRegexpSCOT());
    }

    @Override
    public boolean isValid(String idurba, String documentName) {
        if (StringUtils.isEmpty(idurba)) {
            return false;
        }
        return idurba.equalsIgnoreCase(documentName);
    }

    @Override
    public String getRegexpHelp() {
        return REGEXP_HELP;
    }

    @Override
    public String getRegexpHelp(String documentName) {
        return documentName.toUpperCase();
    }

}
