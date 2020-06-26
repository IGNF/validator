package fr.ign.validator.cnig.tools;

/**
 * Allows IDURBA validation. Note that :
 * 
 * <ul>
 * <li>Only PLU, POS, CC, PLUi and PSMV documents are concerned</li>
 * <li>{@link IdurbaFormatV1} is relative to CNIG standard v2013 and v2014</li>
 * <li>{@link IdurbaFormatV2} is relative to CNIG standard v2017 or more</li>
 * <li>{@link IdurbaFormatFactory} allows the creation of an instance
 * corresponding to the DocumentModel version
 * <li>
 * </ul>
 * 
 * @author MBorne
 *
 */
public interface IdurbaFormat {

    /**
     * True if value is valid according to this format
     * 
     * @param value
     * @return
     */
    public boolean isValid(String value);

    /**
     * Get regexp to produce help message for the format
     * 
     * @return
     */
    public String getRegexpHelp();

    /**
     * Validates an idUrba according to a documentName
     * 
     * @param value
     * @param documentName
     * @return
     */
    public boolean isValid(String value, String documentName);

    /**
     * Get expected regexp for a given documentName
     * 
     * @param documentName
     * @return
     */
    public String getRegexpHelp(String documentName);

}
