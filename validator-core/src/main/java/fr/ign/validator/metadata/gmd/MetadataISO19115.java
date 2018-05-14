package fr.ign.validator.metadata.gmd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import fr.ign.validator.exception.InvalidMetadataException;
import fr.ign.validator.metadata.BoundingBox;
import fr.ign.validator.metadata.Constraint;
import fr.ign.validator.metadata.Date;
import fr.ign.validator.metadata.Extent;
import fr.ign.validator.metadata.Format;
import fr.ign.validator.metadata.Keywords;
import fr.ign.validator.metadata.LegalConstraint;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.OnlineResource;
import fr.ign.validator.metadata.ReferenceSystemIdentifier;
import fr.ign.validator.metadata.Resolution;
import fr.ign.validator.metadata.ResponsibleParty;
import fr.ign.validator.metadata.SecurityConstraint;
import fr.ign.validator.metadata.Specification;
import fr.ign.validator.metadata.code.CharacterSetCode;
import fr.ign.validator.metadata.code.LanguageCode;
import fr.ign.validator.metadata.code.ScopeCode;
import fr.ign.validator.metadata.code.SpatialRepresentationTypeCode;
import fr.ign.validator.metadata.code.TopicCategoryCode;

/***
 * 
 * ISO 19115 parser for INSPIRE profile and CNIG (french) profiles.
 * 
 * Note that elements starting with capital letters are "substituable" (so gmd:MD_DataIdentification is replaced by a joker "*")
 *  
 * @author MBorne
 *
 */
public class MetadataISO19115 implements Metadata {
	/**
	 * XML Document
	 */
	private Element metadataElement ;
	
	/**
	 * Create reader from file
	 * 
	 * @param file
	 * @throws JDOMException
	 * @throws IOException
	 */
	public MetadataISO19115(Element metadataElement) throws InvalidMetadataException {
		this.metadataElement = metadataElement;
	}
	
	
	/**
	 * Create metadata reader from file
	 * 
	 * @param file
	 * @return
	 * @throws InvalidMetadataException 
	 */
	public static MetadataISO19115 readFile(File file) throws InvalidMetadataException{
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			Document document = saxBuilder.build(file) ;
			return new MetadataISO19115(document.getRootElement());
		} catch (JDOMException | IOException e) {
			throw new InvalidMetadataException("invalid metadata exception",e);
		}
	}

	/**
	 * Tests if the given file is a metadata file (XML file with root element named MD_Metadata)
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isMetadataFile( File file ){
		// TODO check presence of ./gmd:identificationInfo (MD_Metadata could be renamed in profiles)
		try {
			MetadataISO19115 reader = MetadataISO19115.readFile(file);
			Element rootElement = reader.getMetadataElement();
			return rootElement.getName().equals( "MD_Metadata" ) ;
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return false ;
	}	

	
	/**
	 * Retrieves the underlying XML document
	 * 
	 * @return
	 */
	protected Element getMetadataElement(){
		return this.metadataElement;
	}


	@Override
	public String getFileIdentifier(){
		// fileIdentifier
		String path = "./gmd:fileIdentifier/*";
		return findValue(path, metadataElement);
	}
	
	@Override
	public String getTitle(){
		// identificationInfo[1]/*/citation/*/title
		String path = "./gmd:identificationInfo/*/gmd:citation/*/gmd:title/*";
		return findValue(path, metadataElement);
	}


	@Override
	public String getAbstract(){
		// identificationInfo[1]/*/abstract
		String path = "./gmd:identificationInfo/*/gmd:abstract/*";
		return findValue(path, metadataElement);
	}
	
	@Override
	public ScopeCode getType(){
		// hierarchyLevel
		String path = "./gmd:hierarchyLevel/*/@codeListValue";
		return ScopeCode.valueOf(findValue(path, metadataElement));
	}
	

	@Override
	public List<OnlineResource> getLocators(){
		List<OnlineResource> result = new ArrayList<>();

		// distributionInfo/*/transferOptions/*/onLine/*/linkage
		String path = "./gmd:distributionInfo/*/gmd:transferOptions/*/gmd:onLine/*";
		List<Element> onlineResourceElements = findElements(path,metadataElement);
		for (Element onlineResourceElement : onlineResourceElements) {
			OnlineResource onlineResource = new OnlineResource();
			onlineResource.setName(findValue("./gmd:name/*", onlineResourceElement));
			onlineResource.setProtocol(findValue("./gmd:protocol/*", onlineResourceElement));
			onlineResource.setUrl(findValue("./gmd:linkage/gmd:URL", onlineResourceElement));
			result.add(onlineResource);
		}
		
		return result;
	}


	@Override
	public String getIdentifier(){
		// identificationInfo[1]/*/citation/*/identifier
		String path = "./gmd:identificationInfo/*/gmd:citation/*/gmd:identifier/*/gmd:code/*";
		return findValue(path, metadataElement);
	}


	@Override
	public LanguageCode getLanguage(){
		// identificationInfo[1]/*/language
		String[] paths = {
			"./gmd:identificationInfo/*/gmd:language/*/@codeListValue",
			"./gmd:identificationInfo/*/gmd:language/*"
		};
		return LanguageCode.valueOf(findValue(paths, metadataElement));
	}


	@Override
	public TopicCategoryCode getTopicCategory(){
		// identificationInfo[1]/*/topicCategory
		String path = "./gmd:identificationInfo/*/gmd:topicCategory/*";
		return TopicCategoryCode.valueOf(
			findValue(path, metadataElement
		));
	}
	

	@Override
	public List<Keywords> getKeywords(){
		// identificationInfo[1]/*/descriptiveKeywords/*/keyword
		// identificationInfo[1]/*/descriptiveKeywords/*/thesaurusName
		
		List<Keywords> result = new ArrayList<>();
		
		String path = "./gmd:identificationInfo/*/gmd:descriptiveKeywords/*";
		List<Element> keywordElements = findElements(path, metadataElement);
		for (Element keywordElement : keywordElements) {
			Keywords descriptiveKeyword = new Keywords();
			descriptiveKeyword.setKeywords(
				findValues("./gmd:keyword/*", keywordElement)
			);
			descriptiveKeyword.setThesaurusName(
				findValue("./gmd:thesaurusName/*/gmd:title/*", keywordElement)
			);
			descriptiveKeyword.setThesaurusDate(
				parseCitationDate(findSingleElement("./gmd:thesaurusName/*/gmd:date", keywordElement))
			);
			result.add(descriptiveKeyword);
		}
		return result;
	}
	

	@Override
	public List<Extent> getExtents(){
		// identificationInfo[1]/*/extent/*/geographicElement/*/westBoundLongitude
		// identificationInfo[1]/*/extent/*/geographicElement/*/eastBoundLongitude
		// identificationInfo[1]/*/extent/*/geographicElement/*/southBoundLatitude
		// identificationInfo[1]/*/extent/*/geographicElement/*/northBoundLatiTude
		
		String path = "./gmd:identificationInfo/*/gmd:extent/*/gmd:geographicElement";
		List<Element> geographicElementNodes = findElements(path, metadataElement);

		List<Extent> result = new ArrayList<>();
		for (Element geographicElementNode : geographicElementNodes) {
			Extent extent = new Extent();
			
			String westBoundLongitude = findValue("./*/gmd:westBoundLongitude/*", geographicElementNode);
			String eastBoundLongitude = findValue("./*/gmd:eastBoundLongitude/*", geographicElementNode);
			String southBoundLatitude = findValue("./*/gmd:southBoundLatitude/*", geographicElementNode);
			String northBoundLatitude = findValue("./*/gmd:northBoundLatitude/*", geographicElementNode);
			if ( westBoundLongitude == null || eastBoundLongitude == null || southBoundLatitude == null || northBoundLatitude == null){
				continue;
			}
			BoundingBox boundingBox = new BoundingBox();
			boundingBox.setWestBoundLongitude(westBoundLongitude);
			boundingBox.setEastBoundLongitude(eastBoundLongitude);
			boundingBox.setSouthBoundLatitude(southBoundLatitude);
			boundingBox.setNorthBoundLatitude(northBoundLatitude);
			extent.setBoundingBox(boundingBox);
			result.add(extent);
		}
		return result;
	}
	
	
	/**
	 * WARNING : MD_SpatialRepresenationTypeCode code list doesn't seems to be in XSD... CNIG_MD_DU could require "vecteur" instead of "vector"
	 * 
	 * @see https://joinup.ec.europa.eu/discussion/geodcat-ap-how-encode-spatial-representation-type
	 */
	@Override
	public ReferenceSystemIdentifier getReferenceSystemIdentifier(){
		String path = "./gmd:referenceSystemInfo/*/gmd:referenceSystemIdentifier/*";
		
		Element rsiElement = findSingleElement(path, metadataElement);
		if ( rsiElement == null ){
			return null;
		}
		ReferenceSystemIdentifier result = new ReferenceSystemIdentifier();
		result.setCode(findValue("./gmd:code/*",rsiElement));
		result.setUri(findValue("./gmd:code/*/@xlink:href",rsiElement));
		result.setCodeSpace(findValue("./gmd:codeSpace/*", rsiElement));
		return result;
	}

	@Override
	public Date getDateOfPublication() {
		// identificationInfo[1]/*/citation/*/date[./*/dateType/*/text()='publication’/*/date
		return getLastDateByType("publication");
	}


	@Override
	public Date getDateOfLastRevision() {
		// identificationInfo[1]/*/citation/*/date[./*/dateType/*/text()='revision']/*/date
		return getLastDateByType("revision");
	}


	@Override
	public Date getDateOfCreation() {
		// identificationInfo[1]/*/citation/*/date[./*/dateType/*/text()='creation']/*/date
		return getLastDateByType("creation");
	}

	/**
	 * Finds last resource date for a given type (publication, revision, creation)
	 * 
	 * @param type
	 * @return
	 */
	private Date getLastDateByType(String type){
		String path = "./gmd:identificationInfo/*/gmd:citation/*/gmd:date";
		List<Element> dateElements = findElements(path, metadataElement);
		
		Date result = null;
		for (Element dateElement : dateElements) {
			String dateType = findValue("./*/gmd:dateType/*/@codeListValue", dateElement);
			if ( dateType == null || ! dateType.equals(type) ){
				continue;
			}
			Date candidate = parseCitationDate(dateElement);
			if ( candidate == null ){
				continue;
			}
			if ( result == null || result.compareTo(candidate) < 0 ){
				result = candidate;
			}
		}
		return result;
	}

	/**
	 * Parses Date from dateElement (a gmd:date Element in gmd:citation)
	 * 
	 * @param dateElement
	 * @return
	 */
	private Date parseCitationDate(Element dateElement){
		if ( dateElement == null ){
			return null;
		}
		String value = findValue("./*/gmd:date/*", dateElement);
		if ( value != null ){
			return new Date(value);
		}else{
			return null;
		}
	}


	@Override
	public CharacterSetCode getCharacterSet(){
		String code = findValue(
			"./gmd:identificationInfo/*/gmd:characterSet/*/@codeListValue",
			metadataElement
		);
		return CharacterSetCode.valueOf(code);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public List<Constraint> getConstraints(){
		//identificationInfo[1]/*/resourceConstraints
		
		List<Element> resourceConstraintElements = findElements(
			"./gmd:identificationInfo/*/gmd:resourceConstraints", 
			metadataElement
		);
		
		List<Constraint> result = new ArrayList<>();
		for (Element resourceConstraintElement : resourceConstraintElements) {
			Constraint constraint = parseResourceConstraint(resourceConstraintElement);
			result.add(constraint);
		}
		
		return result;
	}

	/**
	 * gmd:resourceConstraints element
	 * @param resourceConstraintElement
	 * @return
	 */
	private Constraint parseResourceConstraint(Element resourceConstraintElement) {
		// useLimitations (common to Constraint, SecurityConstraint and LegalConstraint)
		List<String> useLimitations = findValues("./*/gmd:useLimitation/*", resourceConstraintElement);

		/*
		 * SecurityConstraint
		 */
		String classification = findValue(
			"./*/gmd:classification/*/@codeListValue", 
			resourceConstraintElement
		);
		/*
		 * LegalConstraint
		 */
		List<String> accessConstraints = findValues(
			"./*/gmd:accessConstraints/*/@codeListValue",
			resourceConstraintElement
		);
		List<String> useConstraints = findValues(
			"./*/gmd:useConstraints/*/@codeListValue",
			resourceConstraintElement
		);
		List<String> otherConstraints = findValues(
			"./*/gmd:otherConstraints/*",
			resourceConstraintElement
		);

		if ( classification != null ){
			SecurityConstraint constraint = new SecurityConstraint();
			constraint.setUseLimitations(useLimitations);
			constraint.setClassification(classification);
			return constraint;
		}else if ( ! accessConstraints.isEmpty() || ! useConstraints.isEmpty() || ! otherConstraints.isEmpty() ){
			LegalConstraint constraint = new LegalConstraint();
			constraint.setUseLimitations(useLimitations);
			constraint.setAccessConstraints(accessConstraints);
			constraint.setUseConstraints(useConstraints);
			constraint.setOtherConstraints(otherConstraints);
			return constraint;
		}else{
			Constraint constraint = new Constraint();
			constraint.setUseLimitations(useLimitations);
			return constraint;
		}
	}

	
	/**
	 * gmd:identificationInfo - gmd:spatialRepresentationType (ex : vector)
	 * @return
	 */
	@Override
	public SpatialRepresentationTypeCode getSpatialRepresentationType(){
		String path = "./gmd:identificationInfo/*/gmd:spatialRepresentationType/*/@codeListValue";
		return SpatialRepresentationTypeCode.valueOf(
			findValue(path, metadataElement)
		);
	}

	/**
	 * Get distribution formats
	 * @return
	 */
	@Override
	public List<Format> getDistributionFormats(){
		List<Element> distributionFormatElements = findElements(
			"./gmd:distributionInfo/*/gmd:distributionFormat",
			metadataElement
		);
		
		List<Format> result = new ArrayList<>();
		for (Element distributionFormatElement : distributionFormatElements) {
			Format format = new Format();
			format.setName(findValue("./gmd:MD_Format/gmd:name/gco:CharacterString", distributionFormatElement));
			format.setVersion(findValue("./gmd:MD_Format/gmd:version/gco:CharacterString", distributionFormatElement));
			result.add(format);
		}

		return result;
	}

	@Override
	public String getLineage(){
		// dataQualityInfo/*/lineage/*/statement
		String path = "./gmd:dataQualityInfo/*/gmd:lineage/*/gmd:statement/*";
		return findValue(path, metadataElement);
	}


	/**
	 * INSPIRE GUIDELINE - 2.7 Quality and validity / 2.7.2 Spatial resolution (p45)
	 * 
	 * TODO multiplicity & dual mode (equivalent scale or distance...)
	 * 
	 * @return
	 */
	@Override
	public List<Resolution> getSpatialResolutions(){
		// identificationInfo[1]/*/spatialResolution/*/equivalentScale/*/denominator (equivalent scale)
		// identificationInfo[1]/*/spatialResolution/*/distance (distance)
		
		List<Element> spatialResolutionElements = findElements(
			"./gmd:identificationInfo/*/gmd:spatialResolution", 
			metadataElement
		);
		
		List<Resolution> result = new ArrayList<>();
		for (Element spatialResolutionElement : spatialResolutionElements) {
			Resolution resolution = new Resolution();
			resolution.setDenominator(findValue(
				"./*/gmd:equivalentScale/*/gmd:denominator/*",
				spatialResolutionElement
			));
			resolution.setDistance(findValue(
				"./*/gmd:distance/*",
				spatialResolutionElement
			));
			result.add(resolution);
		}

		return result;
	}

	/**
	 * INSPIRE GUIDELINE - 2.8 Conformity / 2.8.2 Specification (p49)
     * CNIG_MD_DU - 7) Conformité / Spécification (p12)
	 * @return
	 */
	@Override
	public List<Specification> getSpecifications(){
		// dataQualityInfo/*/report/*/result/*/specification
		// dataQualityInfo/*/report/*/result/*/pass
		
		String path = "./gmd:dataQualityInfo/*/gmd:report/*/gmd:result/*/gmd:specification";
		List<Element> specificationElements = findElements(path, metadataElement);
		
		List<Specification> result = new ArrayList<>();
		for (Element specificationElement : specificationElements) {
			Specification specification = new Specification();
			specification.setTitle(findValue(
				"./*/gmd:title/*",
				specificationElement
			));
			Element dateElement = findSingleElement("./*/gmd:date", specificationElement);
			specification.setDate(parseCitationDate(dateElement));
			specification.setDateType(findValue("./*/gmd:dateType/*/@codeListValue", dateElement));
			// ../gmd:pass/gco:Boolean
			specification.setDegree(findValue(
				"../gmd:pass/*",
				specificationElement
			));
			result.add(specification);
		}
		return result;
	}
	
	

	/**
	 * INSPIRE GUIDELINE - 2.10 Responsible organisation (p55)
	 * @return
	 */
	@Override
	public ResponsibleParty getContact(){
		String path = "./gmd:identificationInfo/*/gmd:pointOfContact";
		Element contactElement = findSingleElement(path, metadataElement);
		if ( contactElement == null ){
			return null;
		}
		return parseResponsibleParty(contactElement);		
	}



	@Override
	public ResponsibleParty getMetadataContact(){
		// contact
		String path = "./gmd:contact";
		Element contactElement = findSingleElement(path, metadataElement);
		if ( contactElement == null ){
			return null;
		}
		return parseResponsibleParty(contactElement);
	}

	

	@Override
	public Date getMetadataDate(){
		// dateStamp
		String value = findValue("./gmd:dateStamp/*", metadataElement);
		if ( value == null || value.isEmpty() ){
			return null;
		}
		return new Date(value);
	}
	

	@Override
	public LanguageCode getMetadataLanguage(){
		// language
		String[] paths = {
			"./gmd:language/*/@codeListValue",
			"./gmd:language/*"
		};
		return LanguageCode.valueOf( findValue(paths, metadataElement) );
	}


	/**
	 * Create an xpath query
	 * @param path
	 * @return
	 * @throws JDOMException
	 */
	private XPath createXPath(String path) throws JDOMException {
		XPath xpath = XPath.newInstance(path);
		xpath.addNamespace("gmd", "http://www.isotc211.org/2005/gmd"); 
		xpath.addNamespace("gco", "http://www.isotc211.org/2005/gco");
		xpath.addNamespace("gmx", "http://www.isotc211.org/2005/gmx");
		xpath.addNamespace("xlink", "http://www.w3.org/1999/xlink");
		return xpath;
	}

	/**
	 * Finds text value for a given path (selectSingleNode converted to string)
	 * 
	 * @param path
	 * @param context
	 * @return
	 */
	private String findValue(String path, Object context){
		try {
			XPath xpath = createXPath(path);
			Object node = xpath.selectSingleNode(context) ;
			return extractNodeValue(node);
		}catch(JDOMException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Finds a text value according with alternative paths
	 * 
	 * @param paths
	 * @param context
	 * @return
	 */
	private String findValue(String[] paths, Object context){
		for (String path : paths) {
			String candidate = findValue(path,context);
			if ( candidate != null ){
				return candidate;
			}
		}
		return null;
	}

	
	/**
	 * Finds text values for a given path (selectSingleNodes converted to strings)
	 * 
	 * @param path
	 * @param context
	 * @return
	 */
	private List<String> findValues(String path, Object context){
		List<String> result = new ArrayList<>();
		try {
			XPath xpath = createXPath(path);
			@SuppressWarnings("unchecked")
			List<Object> nodes = xpath.selectNodes(context) ;
			for (Object node : nodes) {
				result.add(extractNodeValue(node));
			}
			return result;
		}catch(JDOMException e){
			throw new RuntimeException(e);
		}
	}


	/**
	 * 
	 * Extracts text value from XML node
	 * 
	 * @see {@link XPath.selectSingleNode} for types
	 * 
	 * @param node
	 * @return
	 */
	private String extractNodeValue(Object node) {
		if ( node == null ){
			return null;
		}else if ( node instanceof Text ){
			Text element = (Text)node;
			return element.getTextNormalize();
		}else if ( node instanceof Element ){
			Element element = (Element)node;
			return element.getTextNormalize();
		}else if ( node instanceof Attribute ){
			Attribute attribute = (Attribute)node;
			return attribute.getValue();
		}else{
			throw new RuntimeException("unexpected item type : "+node.getClass().getName());
		}
	}
	
	
	/**
	 * Finds a single node
	 * 
	 * @param path
	 * @return
	 */
	private Element findSingleElement(String path,Object context){
		try {
			XPath xpath = createXPath(path);
			return (Element)xpath.selectSingleNode(context) ;
		}catch(JDOMException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Finds multiple nodes
	 * 
	 * @param path
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Element> findElements(String path,Object context){
		try {
			XPath xpath = createXPath(path);
			return xpath.selectNodes(context) ;
		}catch(JDOMException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param contactElement
	 * @return
	 */
	private ResponsibleParty parseResponsibleParty(Element contactElement){
		ResponsibleParty contact = new ResponsibleParty();
		contact.setOrganisationName(
			findValue("./*/gmd:organisationName/*", contactElement) 
		);
		contact.setElectronicMailAddress(
			findValue("./*/gmd:contactInfo/*/gmd:address/*/gmd:electronicMailAddress/*",contactElement)
		);
		contact.setRole(
			findValue("./*/gmd:role/gmd:CI_RoleCode/@codeListValue",contactElement)
		);

		contact.setPhone(
			findValue("./*/gmd:contactInfo/*/gmd:phone/*/gmd:voice/*",contactElement)
		);
		
		contact.setDeliveryPoint(
			findValue("./*/gmd:contactInfo/*/gmd:address/*/gmd:deliveryPoint/*",contactElement)
		);
		contact.setCity(
			findValue("./*/gmd:contactInfo/*/gmd:address/*/gmd:city/*",contactElement)
		);
		contact.setPostalCode(
			findValue("./*/gmd:contactInfo/*/gmd:address/*/gmd:postalCode/*",contactElement)
		);
		contact.setCountry(
			findValue("./*/gmd:contactInfo/*/gmd:address/*/gmd:country/*",contactElement)
		);

	
		contact.setHoursOfService(
			findValue("./*/gmd:contactInfo/*/gmd:hoursOfService/*",contactElement)
		);
		
		contact.setOnlineResourceUrl(
			findValue("./*/gmd:contactInfo/*/gmd:onlineResource/*/gmd:linkage/*",contactElement)
		);

		return contact;
	}
	
}
