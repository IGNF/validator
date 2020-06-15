package fr.ign.validator.tools;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;

/**
 * Helper class to configure networking options (SSL, proxy,...)
 * 
 * @author MBorne
 *
 */
public class Networking {

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
		// TODO add support for noProxy
		// String noProxy = Networking.getEnvCaseInsensitive("no_proxy");

		// overwrite env with optional proxy option
		if (!StringUtils.isEmpty(proxy)) {
			httpProxy = proxy;
			httpsProxy = proxy;
		}

		if (!StringUtils.isEmpty(httpProxy) || !StringUtils.isEmpty(httpsProxy)) {
			systemSettings.put("proxySet", "true");
		}else{
			// clear proxy configuration
			systemSettings.remove("proxySet");
			String[] proxyVars = {"proxyHost","proxyPort","proxyUser","proxyPassword"};
			for (String proxyVar : proxyVars) {
				systemSettings.remove("http."+proxyVar);
				systemSettings.remove("https."+proxyVar);
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
		// TODO no_proxy -> nonProxyHosts (localhost|127.0.0.1|10.*.*.*|*.foo.com|etc)
	}

	/**
	 * Get HTTP_PROXY from environnment variables
	 * 
	 * @return
	 */
	private static String getEnvCaseInsensitive(String lowerCaseName) {
		if (System.getenv(lowerCaseName) != null) {
			return System.getenv(lowerCaseName);
		} else if (System.getenv(lowerCaseName.toUpperCase()) != null) {
			return System.getenv(lowerCaseName.toUpperCase());
		} else {
			return null;
		}
	}
}
