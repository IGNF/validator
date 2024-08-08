package fr.ign.validator.metadata;

/**
 *
 * Partial implementation of gmd:CI_OnlineResource
 *
 * @see <a href=
 *      "http://www.datypic.com/sc/niem21/e-gmd_CI_OnlineResource.html">gmd:CI_OnlineResource</a>
 *
 * @author MBorne
 *
 */
public class OnlineResource {

    private String url;

    private String name;

    private String protocol;

    private String protocolUrl;

    private String applicationProfile;

    private String applicationProfileUrl;

    private String description;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocolUrl() {
        return protocolUrl;
    }

    public void setProtocolUrl(String protocolUrl) {
        this.protocolUrl = protocolUrl;
    }

    public String getApplicationProfile() {
        return applicationProfile;
    }

    public void setApplicationProfile(String applicationProfile) {
        this.applicationProfile = applicationProfile;
    }

    public String getApplicationProfileUrl() {
        return applicationProfileUrl;
    }

    public void setApplicationProfileUrl(String applicationProfileUrl) {
        this.applicationProfileUrl = applicationProfileUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
