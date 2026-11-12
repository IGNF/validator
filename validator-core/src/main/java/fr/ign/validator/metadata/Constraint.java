package fr.ign.validator.metadata;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 *
 * Implementation of gmd:MD_Constraints
 *
 * TODO broke hierarchy?
 *
 * @see <a href=
 *      "http://www.datypic.com/sc/niem21/e-gmd_MD_Constraints.html">gmd:MD_Constraints</a>
 * @see <a href=
 *      "http://inspire.ec.europa.eu/documents/Metadata/MD_IR_and_ISO_20131029.pdf#page=51">INSPIRE_GUIDELINE
 *      - 2.9 Constraints related to access and use (p51)</a>
 * @author MBorne
 *
 */
@JsonPropertyOrder({
    "type", "useLimitations", "useLimitationUrls"
})
public class Constraint {

    /**
     * gmd:useLimitation
     */
    @JsonInclude(Include.NON_EMPTY)
    private List<String> useLimitations = new ArrayList<>();

    /**
     * URLs (xlink:href) of gmx:Anchor elements found in gmd:useLimitation, in the
     * same order as useLimitations (null when the limitation is not an anchor or
     * has no href)
     */
    @JsonInclude(Include.NON_EMPTY)
    private List<String> useLimitationUrls = new ArrayList<>();

    /**
     * Get GMD class name to simplify inspection
     *
     * @return
     */
    public String getType() {
        return "MD_Constraints";
    }

    public List<String> getUseLimitations() {
        return useLimitations;
    }

    public void setUseLimitations(List<String> useLimitations) {
        this.useLimitations = useLimitations;
    }

    public List<String> getUseLimitationUrls() {
        return useLimitationUrls;
    }

    public void setUseLimitationUrls(List<String> useLimitationUrls) {
        this.useLimitationUrls = useLimitationUrls;
    }

}
