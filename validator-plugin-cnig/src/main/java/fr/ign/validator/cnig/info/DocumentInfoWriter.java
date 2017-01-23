package fr.ign.validator.cnig.info;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import fr.ign.validator.cnig.utils.InseeUtils;

/**
 * @author FCerizay
 *
 */
public class DocumentInfoWriter {

	/*
	 * Constante chaine de caractere Les differents nom des balises
	 */
	private static final String ELEMENT_DOCUMENT    = "document";
	private static final String ELEMENT_NAME        = "name";
	private static final String ELEMENT_TYPEREF     = "typeref";
	private static final String ELEMENT_GEOMETRY    = "geometry";
	
	private static final String ELEMENT_METADATA_FILE_IDENTIFIER = "metadataFileIdentifier";
	private static final String ELEMENT_METADATA_MD_IDENTIFIER = "metadataMdIdentifier";
	
	private static final String ELEMENT_TYPE        = "type";
	private static final String ELEMENT_LAYERS      = "layers";
	private static final String ELEMENT_LAYER       = "layer";
	private static final String ELEMENT_PDFS        = "pdfs";
	private static final String ELEMENT_PDF         = "pdf";
	private static final String ELEMENT_BBOX        = "bbox";

	/*
	 * Expression reguliere
	 */
	public static final String REGEXP_DATE        = "[0-9]{8}" ; 
	private static final String REGEXP_TYPE        = "(SUP|PLU|POS|CC)" ; 
	private static final String REGEXP_SUP         = InseeUtils.REGEXP_DEPARTEMENT + "_" + REGEXP_TYPE ; 
	public  static final String REGEXP_DU          = InseeUtils.REGEXP_COMMUNE + "_" + REGEXP_TYPE + "_" + REGEXP_DATE ;
	
	
	/**
	 * constructeur
	 */
	public DocumentInfoWriter(){
		
	}
	
	/**
	 * @param repertory
	 * @param infoFile 
	 * @param infoFile
	 * @throws JDOMException
	 * @throws IOException
	 */
	public void write (DocumentInfo documentInfo, File output) throws JDOMException, IOException{
		Element infoDOM = createDocumentElement(documentInfo) ;
		exportDOM( output, infoDOM);
	}


	/**
	 * Création de l'entrée <document>
	 * @param documentInfo
	 * @return
	 */
	public Element createDocumentElement(DocumentInfo documentInfo) {
		// <document>
		Element documentElement = new Element( ELEMENT_DOCUMENT ) ;
		
		/*
		 * liste recursive de tous les shapes du doc,
		 * utilisé pour calcul de bbox et proj du doc
		 */
		documentElement.addContent( createAttributeElements( documentInfo ) ) ;
		documentElement.addContent( createPdfElements( documentInfo ) ) ;
		documentElement.addContent( createLayersElements( documentInfo ) ) ;
		return documentElement;
	}
	
	/**
	 * @param documentInfo
	 * @param root
	 * @return
	 */
	private Collection<Element> createAttributeElements(DocumentInfo documentInfo) {
		
		Collection<Element> documentAttributes = new ArrayList<Element>() ;
		documentAttributes.add( createSimpleElement( ELEMENT_NAME, documentInfo.getName() ) ) ;
		
		/*
		 * Ajout du type de la bbox et de la projection
		 */
		documentAttributes.add( createSimpleElement( ELEMENT_TYPE, getType(documentInfo.getName()) ) ) ;
		documentAttributes.add( createSimpleElement( ELEMENT_BBOX, computeDocumentExtent(documentInfo.getDataLayers()) ) ) ;
			
		if ( documentInfo.getGeometry() != null ){
			documentAttributes.add( createSimpleElement(ELEMENT_GEOMETRY, documentInfo.getGeometry().toText()));
		}else{
			documentAttributes.add( createSimpleElement(ELEMENT_GEOMETRY, ""));
		}
		
		/*
		 * Ajout de la date, du code insee, du code de département
		 */
		documentAttributes.add( createSimpleElement( ELEMENT_TYPEREF, documentInfo.getTyperef() ) ) ;
		
		documentAttributes.add( createSimpleElement( ELEMENT_METADATA_FILE_IDENTIFIER, documentInfo.getMetadataFileIdentifier() ) ) ;
		documentAttributes.add( createSimpleElement( ELEMENT_METADATA_MD_IDENTIFIER, documentInfo.getMetadataMdIdentifier() ) ) ;
		
		return documentAttributes;
	}

	/**
	 * Création de l'élément <pdfs>
	 * @param documentInfo
	 * @return
	 */
	private Element createPdfElements(DocumentInfo documentInfo) {
		Element pdfs = new Element( ELEMENT_PDFS );
		for( DataFile pdfFile : documentInfo.getDataFiles() ){
			Element pdf = new Element( ELEMENT_PDF );
			pdf.addContent( createSimpleElement(ELEMENT_NAME, pdfFile.getName() ) ) ;
			pdfs.addContent(pdf);
		}
		return pdfs ;
	}
	
	
	/**
	 * Création de l'élément <layers>
	 * @param layerList
	 * @return
	 */
	private Element createLayersElements(DocumentInfo documentInfo) {
		Element shapes = new Element( ELEMENT_LAYERS );
		for( DataLayer layer : documentInfo.getDataLayers() ){
			Element shp = new Element( ELEMENT_LAYER );
			shp.addContent( createSimpleElement(ELEMENT_NAME, layer.getName() ) ) ;
			shp.addContent( createSimpleElement(ELEMENT_BBOX, layer.getLayerBbox() ) ) ;
			shapes.addContent(shp);
		}
		return shapes ;
	}



	/**
	 * @param elementName
	 * @param elementValue
	 * @return
	 */
	private Element createSimpleElement( String elementName, String elementValue ) {
			Element element = new Element(elementName) ;
			element.setText(elementValue) ;
			return element ;
	}
	
	/**
	 * 
	 * TODO déplacer dans DocumentInfoExtractor
	 * 
	 * @param repertory
	 * @return
	 */
	private String computeDocumentExtent (List <DataLayer > layerList) {
		
		if (!layerList.isEmpty()) {
			double xmin = Double.POSITIVE_INFINITY,	ymin = Double.POSITIVE_INFINITY,
					xmax = Double.NEGATIVE_INFINITY, ymax = Double.NEGATIVE_INFINITY;

			for (DataLayer layer : layerList) {
								
				if(!layer.getLayerBbox().equals("")){
					String bbox[]= layer.getLayerBbox().split(",");
					
					xmin=Math.min(xmin,Double.parseDouble(bbox[0]));
					ymin=Math.min(ymin,Double.parseDouble(bbox[1]));
					xmax=Math.max(xmax,Double.parseDouble(bbox[2]));
					ymax=Math.max(ymax,Double.parseDouble(bbox[3]));
				}
			}
			return xmin+","+ymin+","+xmax+","+ymax;
		}else{
			return "";
		}
	}


	/**
	 * Crée le fichier info-cnig.xml
	 * 
	 * @param fichier
	 * @param racineOut
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void exportDOM(File fichier, Element racineOut)throws FileNotFoundException, IOException {
		Document documentOut = new Document(racineOut);
		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		sortie.output(documentOut, new FileOutputStream(fichier.getPath()));
	}

	/**	

	 * Extraction du type de document du nom du dossier
	 * 
	 * TODO déplacer dans DocumentInfoExtractor
	 * 
	 * @param directoryName
	 * @return
	 */
	private String getType(String directoryName){
		Pattern pattern = Pattern.compile(REGEXP_DU);
		Matcher matcher = pattern.matcher(directoryName);
		
		if( matcher.matches()){
			String tstr[]=directoryName.split("_");
			return tstr[1];
		}
		
		pattern = Pattern.compile(REGEXP_SUP);
		matcher = pattern.matcher(directoryName);
		
		if( matcher.matches()){
			String tstr[]=directoryName.split("_");
			return tstr[1];
		}
	
		return "";
	}

	
}
