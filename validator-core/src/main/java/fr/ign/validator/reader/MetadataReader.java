package fr.ign.validator.reader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/***
 * 
 * Lecteur de fiche de métadonnées
 * 
 * @author MBorne
 *
 */
public class MetadataReader {
	/**
	 * Le document XML
	 */
	private Document document ;
	
	/**
	 * Construction à partir d'un fichier
	 * @param file
	 * @throws JDOMException
	 * @throws IOException
	 */
	public MetadataReader(File file) throws JDOMException, IOException{
		SAXBuilder saxBuilder = new SAXBuilder();
		this.document = saxBuilder.build(file) ;
	}
	
	/**
	 * Construction à partir d'un Document XML
	 * @param document
	 */
	public MetadataReader(Document document){
		this.document = document ;
	}
	
	/**
	 * Renvoie le document XML
	 * @return
	 */
	public Document getDocument(){
		return this.document ;
	}
	
	
	/**
	 * Renvoie la valeur du fileIdentifier
	 * @return
	 */
	public String getFileIdentifier(){
		Element fileIdentifierElement = findChildByName( document.getRootElement(), "fileIdentifier");
		if ( null == fileIdentifierElement ){
			return null ;
		}
		return findChildByName( fileIdentifierElement, "CharacterString" ).getTextTrim() ;
	}

	/**
	 * Renvoie l'identifiant de la ressource 
	 * 
	 * /gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString
	 * 
	 * @return
	 */
	public String getMDIdentifier(){
		/*
			<gmd:MD_Metadata>
			  <gmd:identificationInfo>
			    <gmd:MD_DataIdentification>
			      <gmd:citation>
			        <gmd:CI_Citation>
			          <gmd:identifier>
			            <gmd:MD_Identifier>
			              <gmd:code>
			                <gco:CharacterString>fr-243500139-referentiels-cadastre-parcelle-type_batiments</gco:CharacterString>
			              </gmd:code>
			            </gmd:MD_Identifier>
			          </gmd:identifier>
			        </gmd:CI_Citation>
			      </gmd:citation>
			    </gmd:MD_DataIdentification>
			  </gmd:identificationInfo>
			</gmd:MD_Metadata>
		 */
		XPath xpath;
		try {
			xpath = XPath.newInstance("/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString");
			Element element = (Element) xpath.selectSingleNode(document) ;
			if ( null != element ){
				return element.getTextTrim() ;
			}
			return null ;
		} catch (JDOMException e) {
			return null ;
		}
	}
	
	
	
	/**
	 * Récupération du CharacterSetCode
	 * @return NULL si non trouvé
	 */
	public Charset getCharacterSetCode(){
		Element racine = document.getRootElement();

		/*
		 * lecture dans MD_CharacterSetCode
		 */
		Element characterSetCodeElement = findCharacterSetCodeElement( racine ) ;
		if ( characterSetCodeElement != null ){
			String isoEncoding = characterSetCodeElement.getAttributeValue("codeListValue") ;
			if ( isoEncoding.equals("utf8") ) {
				return StandardCharsets.UTF_8 ;
			} else if ( isoEncoding.equals("8859part1") ) {
				return StandardCharsets.ISO_8859_1 ;
			}
		}
		
		return null ;
	}
	
	/**
	 * Trouve la balise gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:characterSet/gmd:MD_CharacterSetCode
	 * @param root
	 * @return
	 */
	private Element findCharacterSetCodeElement(Element root){
		/*
		 * chemin complet :
		 *  gmd:MD_Metadata
		 *  gmd:identificationInfo
		 *  gmd:MD_DataIdentification
		 *  gmd:characterSet
		 *  gmd:MD_CharacterSetCode
		 */
		Element identificationInfoElement = findChildByName(root,"identificationInfo") ;
		if ( null == identificationInfoElement ){
			return null ;
		}
		Element dataIdentificationElement = findChildByName(identificationInfoElement,"MD_DataIdentification") ;
		if ( null == dataIdentificationElement ){
			return null ;
		}
		return findChildByName(dataIdentificationElement, "MD_CharacterSetCode");
	}
	
	/**
	 * find child by name
	 * @param root
	 * @param name
	 * @return
	 */
	private Element findChildByName(Element root, String name ){
		if ( root.getName().equals(name) ){
			return root ;
		}
		
		@SuppressWarnings("unchecked")
		List<Element> children = root.getChildren() ;
		for (Element child : children) {
			Element result = findChildByName( child, name );
			if ( result != null ){
				return result ;
			}
		}
		return null ;
	}
	

	/**
	 * Test si le fichier est un fichier de métadonnée (il contient une balise MD_Metadata)
	 * @param file
	 * @return
	 */
	public static boolean isMetadataFile( File file ){
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			Document document = saxBuilder.build( file );
			Element racine = document.getRootElement();
			return racine.getName().equals( "MD_Metadata" ) ;
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return false ;
	}
	
}
