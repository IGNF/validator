package fr.ign.validator.cnig.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.ign.validator.cnig.model.DocumentName;

/**
 * 
 * Helper to validate IDURBA format according to CNIG standards v2013 and v2014.
 *
 * @author MBorne
 *
 */
public class IdurbaFormatV1 implements IdurbaFormat {

    private static final String REGEXP_HELP = "<INSEE/SIREN><DATAPPRO>";

    @Override
    public boolean isValid(String value) {
        if (null == value) {
            return false;
        }
        return value.matches(getRegexp());
    }

    /**
     * Get regexp to validate the format
     * 
     * @return
     */
    public String getRegexp() {
        String result = DocumentName.REGEXP_INSEE_OR_SIREN;
        result += "(_?)";// optional _
        result += DocumentName.REGEXP_YYYYMMDD;
        return result;
    }

    @Override
    public boolean isValid(String idurba, String documentName) {
        if (null == idurba) {
            return false;
        }
        return idurba.matches(getRegexp(documentName));
    }

    /**
     * Gets regexp to find idUrba
     * 
     * @param documentName
     * @return
     */
    private String getRegexp(String documentName) {
        Pattern pattern = Pattern.compile("(?i)" + DocumentName.getRegexpDU());
        Matcher matcher = pattern.matcher(documentName);

        if (matcher.matches()) {
            String parts[] = documentName.split("_");
            return parts[0] + ".*" + parts[2] + ".*";
        } else {
            return null;
        }
    }

    @Override
    public String getRegexpHelp() {
        return REGEXP_HELP;
    }

    @Override
    public String getRegexpHelp(String documentName) {
        return getRegexp(documentName);
    }

}
