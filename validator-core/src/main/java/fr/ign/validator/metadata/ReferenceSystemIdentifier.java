package fr.ign.validator.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * Partial implementation of gmd:RS_Identifier
 *  
 * @warning not directly convertible to CRS 
 * see http://cnig.gouv.fr/wp-content/uploads/2014/07/Guide-de-saisie-des-%C3%A9l%C3%A9ments-de-m%C3%A9tadonn%C3%A9es-INSPIRE-v1.1.1.pdf#page=30&zoom=auto,-46,416
 * 
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_RS_Identifier.html">gmd:RS_Identifier</a>
 * @see <a href="http://kartenn.region-bretagne.fr/wiki/doku.php?id=pole_catalogage:guide_de_saisie:systeme_reference">Système de référence de la ressource (geobretagne)</a>
 *
 * @author MBorne
 *
 */
public class ReferenceSystemIdentifier {

	/**
	 * "free-text" code... (ex : "2154", "ETRS89 (EPSG:4258)", etc.)
	 */
	private String code ;
	
	/**
	 * 
	 */
	private String codeSpace ;

	/**
	 * URI extracted from Anchor's xlink:href
	 */
	private String uri ;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@JsonIgnore
	public String getCodeSpace() {
		return codeSpace;
	}

	public void setCodeSpace(String codeSpace) {
		this.codeSpace = codeSpace;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
