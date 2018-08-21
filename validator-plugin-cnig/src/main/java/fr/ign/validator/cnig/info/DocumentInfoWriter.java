package fr.ign.validator.cnig.info;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import fr.ign.validator.cnig.utils.EnveloppeUtils;

/**
 * @author FCerizay
 *
 */
public class DocumentInfoWriter {

	/*
	 * constant strings : tag names
	 */
	private static final String ELEMENT_DOCUMENT    = "document";
	private static final String ELEMENT_NAME        = "name";
	private static final String ELEMENT_STANDARD    = "standard";
	private static final String ELEMENT_TYPEREF     = "typeref";
	
	private static final String ELEMENT_METADATA_FILE_IDENTIFIER = "metadataFileIdentifier";
	private static final String ELEMENT_METADATA_MD_IDENTIFIER = "metadataMdIdentifier";
	
	private static final String ELEMENT_TYPE        = "type";
	private static final String ELEMENT_LAYERS      = "layers";
	private static final String ELEMENT_LAYER       = "layer";
	private static final String ELEMENT_PDFS        = "pdfs";
	private static final String ELEMENT_PDF         = "pdf";
	private static final String ELEMENT_BBOX        = "bbox";

	
	/**
	 * Constructor
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
	 * Creating <document> element
	 * 
	 * @param documentInfo
	 * @return
	 */
	public Element createDocumentElement(DocumentInfo documentInfo) {
		// <document>
		Element documentElement = new Element( ELEMENT_DOCUMENT ) ;
		
		/*
		 * recursive list of all shapes related to document,
		 * used to compute document bbox and projection
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
		documentAttributes.add( createSimpleElement( ELEMENT_STANDARD, documentInfo.getStandard() ) ) ;
		/*
		 * Adding bbox and projection types
		 */
		documentAttributes.add( createSimpleElement( ELEMENT_TYPE, documentInfo.getType() ) ) ;
		documentAttributes.add( createSimpleElement( ELEMENT_BBOX, EnveloppeUtils.format(documentInfo.getDocumentExtent()) ) ) ;

		/*
		 * Adding date, insee code and departement code
		 */
		documentAttributes.add( createSimpleElement( ELEMENT_TYPEREF, documentInfo.getTyperef() ) ) ;
		
		documentAttributes.add( createSimpleElement( ELEMENT_METADATA_FILE_IDENTIFIER, documentInfo.getMetadataFileIdentifier() ) ) ;
		documentAttributes.add( createSimpleElement( ELEMENT_METADATA_MD_IDENTIFIER, documentInfo.getMetadataMdIdentifier() ) ) ;
		
		return documentAttributes;
	}

	/**
	 * Creating <pdfs> element
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
	 * Creating <layers> element
	 * @param layerList
	 * @return
	 */
	private Element createLayersElements(DocumentInfo documentInfo) {
		Element shapes = new Element( ELEMENT_LAYERS );
		for( DataLayer layer : documentInfo.getDataLayers() ){
			Element shp = new Element( ELEMENT_LAYER );
			shp.addContent( createSimpleElement(ELEMENT_NAME, layer.getName() ) ) ;
			shp.addContent( createSimpleElement(ELEMENT_BBOX, EnveloppeUtils.format(layer.getBoundingBox()) ) ) ;
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
	 * Creates info-cnig.xml file
	 * 
	 * @param fichier
	 * @param racineOut
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void exportDOM(File fichier, Element racineOut) throws FileNotFoundException, IOException {
		Document documentOut = new Document(racineOut);
		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		sortie.output(documentOut, new FileOutputStream(fichier.getPath()));
	}

	
}
