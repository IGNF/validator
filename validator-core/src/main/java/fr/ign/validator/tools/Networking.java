package fr.ign.validator.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Helper class to configure networking options (SSL, proxy,...)
 *
 * @author MBorne
 *
 */
public class Networking {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("Networking");

    /**
     * Timeout to establish a connection (ms)
     */
    public static final int CONNECT_TIMEOUT = 30 * 1000;

    /**
     * Timeout while waiting for data (ms)
     */
    public static final int READ_TIMEOUT = 120 * 1000;

    private Networking() {
        // disabled, class with static helpers
    }

    /**
     * Configure networking options (ssl, proxy,...) using environment variables for
     * proxy
     */
    public static void configureHttpClient() {
        Networking.configureHttpClient("");
    }

    /**
     * Configure networking options (ssl, proxy,...) using proxy from command line
     * options.
     *
     * @param proxy
     * @throws ParseException
     */
    public static void configureHttpClient(String proxy) {
        Properties systemSettings = System.getProperties();

        /* configure SSL */
        systemSettings.put("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        /* retreive proxy configuration from environment */
        String httpProxy = Networking.getEnvCaseInsensitive("http_proxy");
        String httpsProxy = Networking.getEnvCaseInsensitive("https_proxy");
        String noProxy = Networking.getEnvCaseInsensitive("no_proxy");

        // overwrite env with optional proxy option
        if (!StringUtils.isEmpty(proxy)) {
            httpProxy = proxy;
            httpsProxy = proxy;
        }

        if (!StringUtils.isEmpty(httpProxy) || !StringUtils.isEmpty(httpsProxy)) {
            systemSettings.put("proxySet", "true");
        } else {
            // clear proxy configuration
            systemSettings.remove("proxySet");
            String[] proxyVars = {
                "proxyHost", "proxyPort", "proxyUser", "proxyPassword", "nonProxyHosts"
            };
            for (String proxyVar : proxyVars) {
                systemSettings.remove("http." + proxyVar);
                systemSettings.remove("https." + proxyVar);
            }
        }
        // configure http.proxyHost, http.proxyPort,...
        {
            Map<String, String> properties = ProxyParser.parse(httpProxy);
            for (String key : properties.keySet()) {
                systemSettings.put("http." + key, properties.get(key));
            }
        }
        // configure https.proxyHost, https.proxyPort,...
        {
            Map<String, String> properties = ProxyParser.parse(httpsProxy);
            for (String key : properties.keySet()) {
                systemSettings.put("https." + key, properties.get(key));
            }
        }
        // configure nonProxyHosts
        if (!StringUtils.isEmpty(noProxy)) {
            systemSettings.setProperty("http.nonProxyHosts", ProxyParser.toNonProxyHosts(noProxy));
        }
    }

    /**
     * Get HTTP_PROXY from environnment variables
     *
     * @return
     */
    private static String getEnvCaseInsensitive(String lowerCaseName) {
        if (System.getenv(lowerCaseName) != null) {
            return System.getenv(lowerCaseName);
        } else if (System.getenv(lowerCaseName.toUpperCase(Locale.ROOT)) != null) {
            return System.getenv(lowerCaseName.toUpperCase(Locale.ROOT));
        } else {
            return null;
        }
    }

    /**
     * Same as url.openStream() with timeouts (default timeouts are infinite, a
     * server that doesn't respond would block the validation)
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static InputStream openStream(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        return connection.getInputStream();
    }

}
