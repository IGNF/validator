package fr.ign.validator.tools;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;

/**
 * Parse proxy string to get compatibility with HTTP_PROXY environment variables
 * 
 * @author MBorne
 *
 */
public class ProxyParser {

	/**
	 * Parse proxy provided as a single string with one of the following format 
	 * 
	 * <ul>
	 * 	<li>{proxyHost}:{proxyPort}</li>
	 *  <li>http://{proxyHost}:{proxyPort}</li>
	 *  <li>http://{proxyUser}:{proxyPassword}@{proxyHost}:{proxyPort}</li>  
	 * </ul>
	 * 
	 * @param proxy
	 * @return the corresponding system properties
	 * @throws ParseException
	 */
	public static Map<String,String> parse(String proxy) throws ParseException{
		if ( StringUtils.isEmpty(proxy) ){
			return new HashMap<>();
		}
		if ( ! proxy.contains("://") ){
			return parseProxyHost(proxy);
		}else{
			return parseUrl(proxy);
		}
	}

	/**
	 * Parse legacy format {proxyHost}:{proxyPort}
	 * @param proxy
	 * @param properties
	 * @return
	 * @throws ParseException
	 */
	private static Map<String, String> parseProxyHost(
		String proxy
	) throws ParseException {
		Map<String,String> properties = new HashMap<String,String>();
		if ( StringUtils.isEmpty(proxy) ){
			return properties;
		}
		String[] proxyParts = proxy.split(":");
		if (proxyParts.length != 2) {
			throw new ParseException("Invalid proxy, expecting {proxyUser}:{proxyPassword} or URL");
		}
		properties.put("proxyHost", proxyParts[0]);
		properties.put("proxyPort", proxyParts[1]);
		return properties;
	}


	/**
	 * Parse proxy defined as an URL
	 * @param proxy
	 * @param properties
	 * @return
	 * @throws ParseException
	 */
	private static Map<String, String> parseUrl(
		String proxy
	) throws ParseException {
		Map<String,String> properties = new HashMap<String,String>();
		if ( StringUtils.isEmpty(proxy) ){
			return properties;
		}
		try {
			URI uri = new URI(proxy);
			if ( uri.getPort() < 1 || StringUtils.isEmpty(uri.getHost()) ){
				throw new ParseException("Invalid proxy URL");
			}
			if ( ! uri.getScheme().equals("http") ){
				throw new ParseException("Invalid proxy URL (expecting http protocol)");
			}

			properties.put("proxyHost", uri.getHost());
			properties.put("proxyPort", ""+uri.getPort());

			String userInfo = uri.getUserInfo();
			if ( ! StringUtils.isEmpty(userInfo) ){
				String[] userInfoParts = userInfo.split(":");
				if (userInfoParts.length != 2) {
					throw new ParseException("Invalid proxy user");
				}
				properties.put("proxyUser", userInfoParts[0]);
				properties.put("proxyPassword", ""+userInfoParts[1]);
			}
			return properties;
		}catch(URISyntaxException e){
			throw new ParseException("Invalid proxy, expecting {proxyUser}:{proxyPassword} or URL");
		}
	}

}
