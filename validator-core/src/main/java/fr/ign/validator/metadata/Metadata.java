package fr.ign.validator.metadata;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import fr.ign.validator.metadata.code.CharacterSetCode;
import fr.ign.validator.metadata.code.LanguageCode;
import fr.ign.validator.metadata.code.ScopeCode;
import fr.ign.validator.metadata.code.SpatialRepresentationTypeCode;
import fr.ign.validator.metadata.code.TopicCategoryCode;

/**
 * 
 * ISO 19115 profile for INSPIRE and CNIG
 * 
 * @author MBorne
 *
 */
@JsonPropertyOrder({ 
	"fileIdentifier",
	
	"title", 
	"abstract", 
	"type", 
	"locators",
	"identifier",
	"language",
	"topicCategory",
	
	"keywords",
	"referenceSystemIdentifier",

	"dateOfPublication",
	"dateOfLastRevision",
	"dateOfCreation",

	"characterSet",
	"spatialRepresentationType",
	"lineage",
	"spatialResolutions",
	"specifications",
	"contact",
	
	"metadataContact",
	"metadataDate",
	"metadataLanguage",
	
	"extents",
	"constraints",
	"distributionFormats"
})
public interface Metadata {

	/**
	 * GEONETWORK REQUIREMENT - The identifier of the metadata (File identifier)
	 * @return
	 */
	public String getFileIdentifier();

	/**
	 * INSPIRE GUIDELINE - 2.2 Identification / 2.2.1 Resource title (p17)
	 * @return
	 */
	public String getTitle();

	/**
	 * INSPIRE GUIDELINE - 2.2 Identification / 2.2.2 Resource abstract (p18)
	 * @return
	 */
	public String getAbstract();

	/**
	 * INSPIRE GUIDELINE - 2.2 Identification / 2.2.3 Resource Type (p20)
	 * @return
	 */
	public ScopeCode getType();
	
	/**
	 * INSPIRE GUIDELINE - 2.2 Identification / 2.2.4 Resource locator (p21)
	 * @return
	 */
	public List<OnlineResource> getLocators();
	
	/**
	 * INSPIRE GUIDELINE - 2.2 Identification / 2.2.5 Unique resource identifier (p24)
	 * @return
	 */	
	public String getIdentifier();
	
	/**
	 * INSPIRE GUIDELINE - 2.2 Identification / 2.2.7 Resource langage (p26)
	 * @return
	 */
	public LanguageCode getLanguage();

	/**
	 * INSPIRE GUIDELINE - 2.3 Classification of spatial data and services / 2.3.1 Topic category (p27)
	 * @return
	 */
	public TopicCategoryCode getTopicCategory();
	
	/**
	 * INSPIRE GUIDELINE - 2.3 Classification of spatial data and services / 2.4 Keyword (p31)
	 * @return
	 */
	public List<Keywords> getKeywords();
	
	/**
	 * INSPIRE GUIDELINE - 2.5 Geographic location / 2.5.1 Geographic bounding box (p35)
	 * 
	 * (could be extending to support INSPIRE GUIDELINE - 2.6 Temporal reference)
	 * 
	 * @return
	 */
	public List<Extent> getExtents();	
	
	/**
	 * CNIG_MD_DU - Encodage - (p7)
	 * @return
	 */
	public ReferenceSystemIdentifier getReferenceSystemIdentifier();

	/**
	 * INSPIRE GUIDELINE - 2.6 Temporal reference / 2.6.2 Date of publication (p38)
	 * @return
	 */
	public Date getDateOfPublication();
	
	/**
	 * INSPIRE GUIDELINE - 2.6 Temporal reference / 2.6.3 Date of last revision (p40)
	 * @return
	 */
	public Date getDateOfLastRevision();
	
	/**
	 * INSPIRE GUIDELINE - 2.6 Temporal reference / 2.6.4 Date of creation (p38)
	 * @return
	 */
	public Date getDateOfCreation();


	/**
	 * Resource CharacterSet
	 * 
	 * TODO support other INSPIRE codes
	 * 
	 * @see http://cnig.gouv.fr/wp-content/uploads/2014/01/Guide-de-saisie-des-éléments-de-métadonnées-INSPIRE-v1.1-final.pdf#page=103 to support other codes
	 * 
	 * @return NULL if not find
	 */
	public CharacterSetCode getCharacterSet();

	/**
	 * INSPIRE_GUIDELINE - 2.9 Constraints related to access and use (p51-55)
	 * @return
	 */
	public List<Constraint> getConstraints();

	/**
	 * CNIG_MD_DU - Encodage (p6)
	 * @return
	 */
	public List<Format> getDistributionFormats();

	/**
	 * INSPIRE GUIDELINE - 2.7 Quality and validity / 2.7.1 Lineage (p42)
	 * @return
	 */
	public String getLineage();

	/**
	 * CNIG_MD_DU - Type de représentation géographique - (p7)
	 * @return
	 */
	public SpatialRepresentationTypeCode getSpatialRepresentationType();	

	/**
	 * INSPIRE GUIDELINE - 2.7 Quality and validity / 2.7.2 Spatial resolution (p45)
	 * @return
	 */
	public List<Resolution> getSpatialResolutions();

	/**
	 * Gets specifications
	 * INSPIRE GUIDELINE - 2.8 Conformity / 2.8.2 Specification (p49)
	 * INSPIRE GUIDELINE - 2.8 Conformity / 2.8.1 Degree (p48)
	 * @return
	 */
	public List<Specification> getSpecifications();

	/**
	 * INSPIRE GUIDELINE - 2.10 Responsible organisation (p55)
	 * @return
	 */
	public ResponsibleParty getContact();

	/**
	 * INSPIRE GUIDELINE - 2.11 Metadata on metadata / 2.11.1 Metadata point of contact (p55)
	 * @return
	 */
	public ResponsibleParty getMetadataContact();

	/**
	 * INSPIRE GUIDELINE - 2.11 Metadata on metadata / 2.11.2 Metadata date (p60)
	 * @return
	 */
	public Date getMetadataDate();

	/**
	 * INSPIRE GUIDELINE - 2.11.3 Metadata language / 2.11.3 Metadata langage (p60)
	 * @return
	 */
	public LanguageCode getMetadataLanguage();


}


