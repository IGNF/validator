package fr.ign.validator.cnig.process.info;

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

import fr.ign.validator.cnig.process.CnigInfoExtractorPostProcess;
import fr.ign.validator.data.DataFile;
import fr.ign.validator.data.DataLayer;
import fr.ign.validator.utils.InseeUtils;
import fr.ign.validator.data.DataDirectory;

/**
 * @author FCerizay
 *
 */
public class InfoWriter {

	/*
	 * Constante chaine de caractere Les differents nom des balises
	 */
	private static final String ELEMENT_DIRECTORY   = "directory";
	private static final String ELEMENT_DOCUMENT    = "document";
	private static final String ELEMENT_DOCUMENTS   = "documents";
	private static final String ELEMENT_NAME        = "name";
	private static final String ELEMENT_INSEE       = "insee";
	private static final String ELEMENT_TYPEREF     = "typeref";
	
	private static final String ELEMENT_METADATA_FILE_IDENTIFIER = "metadataFileIdentifier";
	private static final String ELEMENT_METADATA_MD_IDENTIFIER = "metadataMdIdentifier";
	
	private static final String ELEMENT_TYPE        = "type";
	private static final String ELEMENT_DATE        = "date";
	private static final String ELEMENT_LAYERS      = "layers";
	private static final String ELEMENT_LAYER       = "layer";
	private static final String ELEMENT_PDFS        = "pdfs";
	private static final String ELEMENT_PDF         = "pdf";
	private static final String ELEMENT_DEPARTEMENT = "departement";
	private static final String ELEMENT_BBOX        = "bbox";
	private static final String ELEMENT_PROJECTION 	= "projection";
	private static final String ELEMENT_PARENT 		= "parent";

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
	public InfoWriter(){
		
	}
	
	/**
	 * @param repertory
	 * @param infoFile 
	 * @param infoFile
	 * @throws JDOMException
	 * @throws IOException
	 */
	public void write (DataDirectory repertory, File output) throws JDOMException, IOException{
		Element infoDOM = createDOM( repertory) ;
		exportDOM( output, infoDOM);
	}
	
	/**
	 * 
	 * @param repertory
	 * @return
	 */
	public Element createDOM( DataDirectory registerRepertory) {
		// <directory>
		Element rootelement = new Element( ELEMENT_DIRECTORY ) ;
		// <name>50599_PLU_20130121</name>
		rootelement.addContent( createSimpleElement(ELEMENT_NAME,registerRepertory.getName() ) ) ;
		// <layers>
		rootelement.addContent( createTablesElement( registerRepertory ) ) ;
		// <documents>
		rootelement.addContent( createDocumentsElement( registerRepertory ) ) ;
		return rootelement;
	}

	/**
	 * 
	 * @param repertory
	 * @return
	 */
	public Element createDocumentsElement(DataDirectory repertory) {
		//<documents>
		Element documentsElement = new Element( ELEMENT_DOCUMENTS ) ;
		for (DataDirectory subRepertory : repertory.getRegisterSubRepertories() ) {
			// <document>
			documentsElement.addContent( createDocumentElement( subRepertory ) ) ;
		}
		return documentsElement;
	}

	/**
	 * Création de l'entrée <document>
	 * @param subRepertory
	 * @return
	 */
	public Element createDocumentElement(DataDirectory subRepertory) {
		// <document>
		Element documentElement = new Element( ELEMENT_DOCUMENT ) ;
		
		/*
		 * liste recursive de tous les shapes du doc,
		 * utilisé pour calcul de bbox et proj du doc
		 */
		List <DataLayer > layerList = getLayerList(subRepertory);
		/*
		 * liste recursive de tous les pdf du doc, avec leur parent renseigné
		 */
		List <DataFile > fileList = getFileList(subRepertory);

		documentElement.addContent( createAttributeElements( subRepertory,layerList ) ) ;
		documentElement.addContent( createPdfElements( fileList ) ) ;
		documentElement.addContent( createLayersElements( layerList ) ) ;
		return documentElement;
	}

	
	/**
	 * @param repertory
	 * @return
	 */
	private List<DataFile> getFileList(DataDirectory repertory) {
		List <DataFile > fileList = new ArrayList <DataFile >();
		
		if ( ! repertory.getRegisterFiles().isEmpty() ) {
			for (DataFile file : repertory.getRegisterFiles() ) {
				file.setParent(repertory.getName());
				fileList.add(file);
			}
		}
		if ( ! repertory.getRegisterSubRepertories().isEmpty() ) {
			for (DataDirectory subRepertory : repertory.getRegisterSubRepertories() ) {
				fileList.addAll(getFileList(subRepertory));
			}
		}
		return fileList;	
	}
	

	/**
	 * @param repertory
	 * @return
	 */
	private List<DataLayer> getLayerList(DataDirectory repertory) {
		List <DataLayer > layerList = new ArrayList <DataLayer >();
		if ( ! repertory.getRegisterLayers().isEmpty() ) {
			layerList.addAll(repertory.getRegisterLayers());
		}
		if ( ! repertory.getRegisterSubRepertories().isEmpty() ) {
			for (DataDirectory subRepertory : repertory.getRegisterSubRepertories() ) {
				layerList.addAll(getLayerList(subRepertory));
			}
		}
		return layerList;	
	}

	/**
	 * @param repertory
	 * @param root
	 * @return
	 */
	public Collection<Element> createAttributeElements(DataDirectory repertory,List<DataLayer> layerList) {
		
		Collection<Element> documentAttributes = new ArrayList<Element>() ;
		documentAttributes.add( createSimpleElement( ELEMENT_NAME, repertory.getName() ) ) ;
		
		/*
		 * Ajout du type de la bbox et de la projection
		 */
		documentAttributes.add( createSimpleElement( ELEMENT_TYPE, getType(repertory.getName()) ) ) ;
		documentAttributes.add( createSimpleElement( ELEMENT_PROJECTION, CnigInfoExtractorPostProcess.CRS_PROJECTION_CODE ) ) ;
		documentAttributes.add( createSimpleElement( ELEMENT_BBOX, computeDocumentExtent( layerList) ) ) ;
		

		/*
		 * Ajout de la date, du code insee, du code de département
		 */

		documentAttributes.add( createSimpleElement( ELEMENT_INSEE, getCodeInsee(repertory.getName()) ) ) ;
		documentAttributes.add( createSimpleElement( ELEMENT_DATE, getDate(repertory.getName()) ) ) ;

		documentAttributes.add( createSimpleElement( ELEMENT_DEPARTEMENT, getDepartement(repertory.getName()) ) ) ;
		
		documentAttributes.add( createSimpleElement( ELEMENT_TYPEREF, repertory.getTyperef() ) ) ;
		
		documentAttributes.add( createSimpleElement( ELEMENT_METADATA_FILE_IDENTIFIER, repertory.getMetadataFileIdentifier() ) ) ;
		documentAttributes.add( createSimpleElement( ELEMENT_METADATA_MD_IDENTIFIER, repertory.getMetadataMdIdentifier() ) ) ;
		
		return documentAttributes;
	}


	public Element createPdfElements(List<DataFile> fileList) {
		Element pdfs = new Element( ELEMENT_PDFS );
		if ( ! fileList.isEmpty() ) {
			for( DataFile pdfFile : fileList ){
				Element pdf = new Element( ELEMENT_PDF );
				pdf.addContent( createSimpleElement(ELEMENT_NAME, pdfFile.getName() ) ) ;
				pdf.addContent( createSimpleElement(ELEMENT_PARENT, pdfFile.getParent() ) ) ;
				pdfs.addContent(pdf);
			}
		}
		return pdfs ;
	}
	
	
	/**
	 * Création de l'élément <layers>
	 * @param layerList
	 * @return
	 */
	public Element createLayersElements(List<DataLayer> layerList) {
		Element shapes = new Element( ELEMENT_LAYERS );
		if ( ! layerList.isEmpty() ) {
			for( DataLayer layer : layerList ){
				Element shp = new Element( ELEMENT_LAYER );
				shp.addContent( createSimpleElement(ELEMENT_NAME, layer.getName() ) ) ;
				shp.addContent( createSimpleElement(ELEMENT_BBOX, layer.getLayerBbox() ) ) ;
				shapes.addContent(shp);
			}
		}
		return shapes ;
	}

	
	

	/**
	 * @param subRepertory
	 * @return
	 */
	public Element createTablesElement(DataDirectory repertory) {
		Element shapes = new Element( ELEMENT_LAYERS );
		if ( ! repertory.getRegisterLayers().isEmpty() ) {
			for (DataLayer layer : repertory.getRegisterLayers() ) {
				Element shp = new Element( ELEMENT_LAYER );
				shp.addContent( createSimpleElement(ELEMENT_NAME, layer.getName() ) ) ;
				shp.addContent( createSimpleElement(ELEMENT_BBOX, layer.getLayerBbox() ) ) ;
				shapes.addContent(shp);
			}
		}
		return shapes ;
	}

	/**
	 * @param elementName
	 * @param elementValue
	 * @return
	 */
	public Element createSimpleElement( String elementName, String elementValue ) {
			Element element = new Element(elementName) ;
			element.setText(elementValue) ;
			return element ;
	}
	
	/**
	 * @param repertory
	 * @return
	 */
	public String computeDocumentExtent (List <DataLayer > layerList) {
		
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
	public void exportDOM(File fichier, Element racineOut)throws FileNotFoundException, IOException {
		Document documentOut = new Document(racineOut);
		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		sortie.output(documentOut, new FileOutputStream(fichier.getPath()));
	}
	
	
	/**
	 * Extraction du code INSEE à partir du nom du dossier
	 * @param directoryName
	 * @return
	 */
	public String getCodeInsee(String directoryName) {
		Pattern pattern = Pattern.compile(REGEXP_DU);
		Matcher matcher = pattern.matcher(directoryName);
		
		if( matcher.matches()){
			String tstr[]=directoryName.split("_");
			return tstr[0];
		}
		
		return "";
	}
	
	/**
	 * Extraction du type de document du nom du dossier
	 * 
	 * @param directoryName
	 * @return
	 */
	public String getType(String directoryName){
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
	
	
	/**
	 * Extraction de la date à partir du nom du dossier
	 * @param directoryName
	 * @return
	 */
	public String getDate(String directoryName){
		Pattern pattern = Pattern.compile(REGEXP_DU);
		Matcher matcher = pattern.matcher(directoryName);
		
		if( matcher.matches()){
			String tstr[]=directoryName.split("_");
			return tstr[2];
		}
		
		return "";
	}
	
	/**
	 * Extraction du département à partir du nom du dossier
	 * @param directoryName
	 * @return
	 */
	public String getDepartement(String directoryName){
		Pattern pattern = Pattern.compile(REGEXP_SUP);
		Matcher matcher = pattern.matcher(directoryName);
		
		if( matcher.matches()){
			String tstr[]=directoryName.split("_");
			if(tstr[0].length()==2)
				return "0"+tstr[0];
			else
				return tstr[0];
		};
		return "";
	}
	
}
