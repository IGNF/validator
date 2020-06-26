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
public class IdurbaFormatV2 implements IdurbaFormat {

    private static final String REGEXP_HELP = "<INSEE/SIREN>_<TYPEDOC>_<DATAPPRO>{_CodeDU}";

    @Override
    public boolean isValid(String value) {
        if (null == value) {
            return false;
        }
        return value.matches(DocumentName.getRegexpDU());
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
        return documentName;
    }

}
