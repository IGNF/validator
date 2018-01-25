package fr.ign.validator.metadata.gmd;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import fr.ign.validator.exception.InvalidMetadataException;
import fr.ign.validator.metadata.Constraint;
import fr.ign.validator.metadata.Extent;
import fr.ign.validator.metadata.Format;
import fr.ign.validator.metadata.Keywords;
import fr.ign.validator.metadata.LegalConstraint;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.OnlineResource;
import fr.ign.validator.metadata.ReferenceSystemIdentifier;
import fr.ign.validator.metadata.ResponsibleParty;
import fr.ign.validator.metadata.SecurityConstraint;
import fr.ign.validator.metadata.Specification;
import fr.ign.validator.metadata.code.LanguageCode;
import fr.ign.validator.metadata.code.ScopeCode;
import fr.ign.validator.metadata.code.SpatialRepresentationTypeCode;
import fr.ign.validator.metadata.code.TopicCategoryCode;
import junit.framework.TestCase;

public class MetadataISO19115Test extends TestCase {

	protected Metadata getMetadataFromResource(String path){
		File file = new File(getClass().getResource(path).getPath()) ;
		assertNotNull("ressource "+path+" non trouvée",file);
		try {
			Metadata reader = MetadataISO19115.readFile(file);
			return reader;
		}catch( InvalidMetadataException e ){
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void testMissingCharsetIsNull(){
		Metadata metadata = getMetadataFromResource("/metadata/missing-charset.xml");
		assertNull( metadata.getCharacterSet() ) ;
	}
	
	
	public void testIsMetadataFile(){
		File okFile = new File(getClass().getResource("/metadata/missing-charset.xml").getPath()) ;
		File nokFile1 = new File(getClass().getResource("/metadata/not-a-metadatafile.xml").getPath()) ;
		File nokFile2 = new File(getClass().getResource("/metadata/not-an-xml-file.xml").getPath()) ;
		
		assertTrue( MetadataISO19115.isMetadataFile(okFile) ) ;
		assertFalse( MetadataISO19115.isMetadataFile(nokFile1) ) ;
		assertFalse( MetadataISO19115.isMetadataFile(nokFile2) ) ;
	}
	
	public void testRegress01(){
		Metadata metadata = getMetadataFromResource("/metadata/01.xml");
		
		assertEquals("2015-08-28T12:48:06.7163297Z", metadata.getMetadataDate().getValue());
		assertEquals(
			LanguageCode.valueOf("fre"), 
			metadata.getMetadataLanguage()
		);
		assertEquals("Plan local d'urbanisme de La Baconnière en Mayenne", metadata.getTitle());
		assertEquals("Un résumé simple pour la fiche 01.xml", metadata.getAbstract());
		assertEquals(ScopeCode.valueOf("dataset"), metadata.getType());

		assertEquals( "urn:isogeo:metadata:uuid:66484d70-3f8c-44cf-b0e5-98ac84426a2c", metadata.getFileIdentifier() ) ;
		
		
		assertEquals( "66484d70-3f8c-44cf-b0e5-98ac84426a2c", metadata.getIdentifier() ) ;
		assertEquals( 
			TopicCategoryCode.valueOf("planningCadastre"), 
			metadata.getTopicCategory() 
		) ;

		assertEquals( "8859part1", metadata.getCharacterSet().getValue() ) ;
		assertEquals( StandardCharsets.ISO_8859_1, metadata.getCharacterSet().getCharset() ) ;
		
		// constraints
		{
			List<Constraint> constraints = metadata.getConstraints();
			assertEquals(1, constraints.size());
			{
				assertEquals("LegalConstraint", constraints.get(0).getClass().getSimpleName());
				LegalConstraint constraint = (LegalConstraint) constraints.get(0);
				assertEquals(1, constraint.getUseLimitations().size());
				assertEquals("Pas de restriction d’accès public selon INSPIRE", constraint.getUseLimitations().get(0));
				assertEquals(0, constraint.getUseConstraints().size());
				assertEquals(1, constraint.getAccessConstraints().size());
				assertEquals("otherRestrictions", constraint.getAccessConstraints().get(0));
				assertEquals(1, constraint.getOtherConstraints().size());
				assertEquals("Informations non opposables au tiers.", constraint.getOtherConstraints().get(0));
			}
		}
		
		
		{
			List<Extent> extents = metadata.getExtents();
			assertEquals(1,extents.size());
			assertEquals( 
				"-0.954544,48.150182,-0.847686,48.202573", 
				extents.get(0).getBoundingBox().toString() 
			) ;
		}

		{
			ReferenceSystemIdentifier rsi = metadata.getReferenceSystemIdentifier() ;
			assertNotNull(rsi);
			assertEquals("urn:ogc:def:crs:EPSG:2154", rsi.getCode());
			assertNull(rsi.getCodeSpace());
			assertNull(rsi.getUri());
		}
		
		assertEquals(
			"Document d’urbanisme numérisé conformément aux prescriptions nationales du CNIG, contrôlé par la DDT 53 (Direction départementale des territoires de la Mayenne). Ce lot de données a été numérisé à partir du PCI Vecteur.", 
			metadata.getLineage() 
		) ;
		

		
		// getMetadataContact
		{
			ResponsibleParty contact = metadata.getMetadataContact();
			assertNotNull(contact);
			assertEquals("Partenaire TEST", contact.getOrganisationName());
			assertEquals("sig@partenaire.fr", contact.getElectronicMailAddress());
			assertEquals("pointOfContact", contact.getRole());
		}
		

		assertEquals(
			LanguageCode.valueOf("fre"),
			metadata.getLanguage()
		);
		
		// spatialResolutions
		{
			assertEquals(1,metadata.getSpatialResolutions().size());
			assertEquals("5000",metadata.getSpatialResolutions().get(0).getDenominator());			
		}

		
		// specifications
		{
			List<Specification> specifications = metadata.getSpecifications();
			assertEquals(2,specifications.size());
			
			assertEquals("CNIG PLU v2014", specifications.get(0).getTitle());
			assertEquals("2014-10-02", specifications.get(0).getDate().toString());
			assertEquals("publication", specifications.get(0).getDateType());
			assertEquals("true", specifications.get(0).getDegree());
			
			assertEquals("Guide INSPIRE sur l'usage des sols", specifications.get(1).getTitle());
			assertEquals("2014-10-02", specifications.get(0).getDate().toString());
			assertEquals("false", specifications.get(1).getDegree());
		}
		
		
		assertEquals(
			SpatialRepresentationTypeCode.valueOf("vector"),
			metadata.getSpatialRepresentationType()
		);

		// getResourceContact
		{
			ResponsibleParty contact = metadata.getContact();
			assertNotNull(contact);
			assertEquals("Mairie de La Baconnière", contact.getOrganisationName());
			assertEquals("mairie-de-la-baco@wanadoo.fr", contact.getElectronicMailAddress());
			assertEquals("pointOfContact", contact.getRole());
		}
		
		// dates
		assertEquals("2004-09-03Z",metadata.getDateOfPublication().toString());
		assertEquals("2004-09-03Z", metadata.getDateOfCreation().toString());
		assertEquals("2014-09-08Z", metadata.getDateOfLastRevision().toString());
		
		
		// getKeywords (9 without thesaurusName, 1 with thesaurusName)
		{
			List<Keywords> keywords = metadata.getKeywords();
			assertEquals(2,keywords.size());
			
			assertEquals( 
				"plan local d'urbanisme;plu;planification;urbanisme logement;la baconnière;documents d'urbanisme;cce;mayenne;urbanisme", 
				StringUtils.join(keywords.get(0).getKeywords(),';') 
			);
			assertNull( keywords.get(0).getThesaurusName() );
			assertNull( keywords.get(0).getThesaurusDate() );
			
			assertEquals( 
				"Usage des sols", 
				StringUtils.join(keywords.get(1).getKeywords(),';')  
			);
			assertEquals( "GEMET - INSPIRE themes, version 1.0", keywords.get(1).getThesaurusName() );
			assertEquals( "2008-06-01", keywords.get(1).getThesaurusDate().toString() );
		}
		
		
		// distributionFormats
		List<Format> distributionFormats = metadata.getDistributionFormats();
		assertEquals(1,distributionFormats.size());
		{
			Format distributionFormat = distributionFormats.get(0);
			assertNotNull(distributionFormat);
			assertEquals("Shapefile", distributionFormat.getName());
			assertEquals("1.0", distributionFormat.getVersion());			
		}
		// onlineResources
		{
			List<OnlineResource> onlineResources = metadata.getLocators();
			assertEquals(3,onlineResources.size());
			assertEquals(
				"http://wxs-gpu.mongeoportail.ign.fr/externe/vkd1evhid6jdj5h4hkhyzjto/wms/v?",
				onlineResources.get(0).getUrl()
			);
			assertEquals(
				"OGC:WMS",
				onlineResources.get(0).getProtocol()
			);
			assertEquals(
				"Service de visualisation du Géoportail de l'Urbanisme",
				onlineResources.get(0).getName()
			);
		}
	}
	
	
	
	
	/**
	 * Empty fileIdentifier, empty MD_Identifier, no characterSetCode
	 */
	public void testRegress02(){
		Metadata metadata = getMetadataFromResource("/metadata/02.xml");
		
		assertEquals("2016-09-30",metadata.getMetadataDate().getValue());
		assertEquals(
			LanguageCode.valueOf("fre"), 
			metadata.getMetadataLanguage()
		);
		
		assertEquals( "", metadata.getFileIdentifier() ) ;
		assertEquals( "", metadata.getIdentifier() ) ;
		
		assertEquals( 
			TopicCategoryCode.valueOf("planningCadastre"), 
			metadata.getTopicCategory() 
		) ;
		assertNull( metadata.getCharacterSet() ) ;

		// constraints
		{
			List<Constraint> constraints = metadata.getConstraints();
			assertEquals(1, constraints.size());
			{
				assertEquals("LegalConstraint", constraints.get(0).getClass().getSimpleName());
				LegalConstraint constraint = (LegalConstraint) constraints.get(0);
				assertEquals(1, constraint.getUseLimitations().size());
				assertEquals("Licence Ouverte 1.0 http://www.data.gouv.fr/Licence-Ouverte-Open-Licence.", constraint.getUseLimitations().get(0));
				assertEquals(0, constraint.getUseConstraints().size());
				assertEquals(1, constraint.getAccessConstraints().size());
				assertEquals("otherRestrictions", constraint.getAccessConstraints().get(0));
				assertEquals(1, constraint.getOtherConstraints().size());
				assertEquals("Pas de restriction d'accès public selon INSPIRE", constraint.getOtherConstraints().get(0));
			}
		}
		
		{
			List<Extent> extents = metadata.getExtents();
			assertEquals(1,extents.size());
			assertEquals( 
				"3.18713903,45.02106857,5.55596375,46.00650406", 
				extents.get(0).getBoundingBox().toString() 
			) ;
		}

		assertNull( metadata.getReferenceSystemIdentifier() ) ;

		assertEquals(
			"Généalogie non renseignée", 
			metadata.getLineage() 
		) ;
		
		assertEquals("Servitudes - Lot A9 relatives aux zones agricoles protégées de la Loire", metadata.getTitle());
		assertEquals("Un résumé sur plusieurs lignes pour la ressource 02.xml", metadata.getAbstract()) ;
		assertEquals(ScopeCode.valueOf("dataset"), metadata.getType());
		
		//getMetadataContact
		{
			ResponsibleParty contact = metadata.getMetadataContact();
			assertNotNull(contact);
			assertEquals("DDT 42 (Direction départementale des territoires de la Loire)", contact.getOrganisationName());
			assertEquals("ddddt-gpu-sup@loire.gouv.fr", contact.getElectronicMailAddress());
			assertEquals("pointOfContact", contact.getRole());
			
			assertEquals("0477438000",contact.getPhone());
			assertEquals("2, avenue Grüner CS 90509",contact.getDeliveryPoint());
			assertEquals("Saint-Etienne Cedex 1",contact.getCity());
			assertEquals("42007",contact.getPostalCode());
			assertEquals("France",contact.getCountry());
			assertEquals("https://lannuaire.service-public.fr/auvergne-rhone-alpes/loire/ddt-42218-01",contact.getOnlineResourceUrl());
			assertEquals("Consulter l'annuaire du service public", contact.getHoursOfService());
		}
		
		assertEquals(
			LanguageCode.valueOf("fre"),
			metadata.getLanguage()
		);
		
		// spatialResolutions
		{
			assertEquals(1,metadata.getSpatialResolutions().size());
			assertEquals("5000",metadata.getSpatialResolutions().get(0).getDenominator());			
		}

		// specifications
		{
			List<Specification> specifications = metadata.getSpecifications();
			assertEquals(0,specifications.size());
		}

		// getResourceContact
		{
			ResponsibleParty contact = metadata.getContact();
			assertNotNull(contact);
			assertEquals("Organisation propriétaire de la données", contact.getOrganisationName());
			assertEquals("ac@exemple.fr", contact.getElectronicMailAddress());
			assertEquals("owner", contact.getRole());
		}
		
		// dates
		assertEquals("2016-09-30",metadata.getDateOfPublication().toString());
		assertEquals("2016-09-30", metadata.getDateOfCreation().toString());
		assertEquals("2016-09-30", metadata.getDateOfLastRevision().toString());
		
		// getKeywords 
		{
			List<Keywords> keywords = metadata.getKeywords();
			assertEquals(4,keywords.size());

			assertEquals( "Aménagement Urbanisme/Assiette Servitude", StringUtils.join(keywords.get(0).getKeywords(),';') );
			assertEquals( "Arborescence thématique de la COVADIS", keywords.get(0).getThesaurusName() );
			assertEquals( "2016-09-30", keywords.get(0).getThesaurusDate().toString() );
			
			assertEquals( "données ouvertes", StringUtils.join(keywords.get(1).getKeywords(),';') );
			assertNull( keywords.get(1).getThesaurusName() );
			assertNull( keywords.get(1).getThesaurusDate() );
			
			assertEquals( 
				"Zones de gestion, de restriction ou de réglementation et unités de déclaration", 
				StringUtils.join(keywords.get(2).getKeywords(),';')
			);
			assertEquals( "GEMET - INSPIRE Themes", keywords.get(2).getThesaurusName() );
			assertEquals( "2008-06-01", keywords.get(2).getThesaurusDate().toString() );
			
			assertEquals( 
				"Parcelles cadastrales", 
				StringUtils.join(keywords.get(3).getKeywords(),';')
			);
			assertEquals( "GEMET - INSPIRE Themes", keywords.get(3).getThesaurusName() );
			assertEquals( "2008-06-01", keywords.get(3).getThesaurusDate().toString() );
		}
		
		
		// distributionFormats
		List<Format> distributionFormats = metadata.getDistributionFormats();
		assertEquals(1,distributionFormats.size());
		{
			Format distributionFormat = distributionFormats.get(0);
			assertNotNull(distributionFormat);
			assertEquals("MapInfo TAB", distributionFormat.getName());
			assertEquals("", distributionFormat.getVersion());			
		}
		
		// onlineResources
		{
			List<OnlineResource> onlineResources = metadata.getLocators();
			assertEquals(5,onlineResources.size());
			assertEquals(
				"",
				onlineResources.get(0).getUrl()
			);
			assertNull(
				onlineResources.get(0).getProtocol()
			);
			assertEquals(
				"Vue HTML des métadonnées sur internet",
				onlineResources.get(0).getName()
			);
		}
	}
	
	
	@Test
	public void testRegress03(){
		Metadata metadata = getMetadataFromResource("/metadata/03.xml");
		
		assertEquals("2014-09-26",metadata.getMetadataDate().getValue());
		assertEquals(
			LanguageCode.valueOf("fre"), 
			metadata.getMetadataLanguage()
		);
		
		assertEquals( "fr-210800405-08042-plu20140213", metadata.getFileIdentifier() ) ;
		assertNull( metadata.getIdentifier() ) ;
		assertEquals( 
			TopicCategoryCode.valueOf("planningCadastre"), 
			metadata.getTopicCategory() 
		) ;
		assertEquals("8859part1",metadata.getCharacterSet().getValue());
		
		// constraints
		{
			List<Constraint> constraints = metadata.getConstraints();
			assertEquals(4, constraints.size());
			{
				assertEquals("Constraint", constraints.get(0).getClass().getSimpleName());
				List<String> useLimitations = constraints.get(0).getUseLimitations();
				assertEquals(1, useLimitations.size());
				assertEquals("Conditions inconnues.", useLimitations.get(0));
			}
			{
				assertEquals("LegalConstraint", constraints.get(1).getClass().getSimpleName());
				LegalConstraint constraint = (LegalConstraint)constraints.get(1);
				assertEquals(0, constraint.getUseLimitations().size());
				assertEquals(0, constraint.getUseConstraints().size());
				assertEquals(1, constraint.getAccessConstraints().size());
				assertEquals("copyright", constraint.getAccessConstraints().get(0));
				assertEquals(0, constraint.getOtherConstraints().size());
			}
			{
				assertEquals("LegalConstraint", constraints.get(2).getClass().getSimpleName());
				LegalConstraint constraint = (LegalConstraint)constraints.get(2);
				assertEquals(0, constraint.getUseLimitations().size());
				assertEquals(0, constraint.getUseConstraints().size());
				assertEquals(1, constraint.getAccessConstraints().size());
				assertEquals("otherRestrictions", constraint.getAccessConstraints().get(0));
				assertEquals(1, constraint.getOtherConstraints().size());
				assertEquals("Pas de restriction d’accès publique", constraint.getOtherConstraints().get(0));
			}
			{
				assertEquals("SecurityConstraint", constraints.get(3).getClass().getSimpleName());
				SecurityConstraint constraint = (SecurityConstraint)constraints.get(3);
				assertEquals(0, constraint.getUseLimitations().size());
				assertEquals("unclassified", constraint.getClassification());
			}
		}
		
		{
			List<Extent> extents = metadata.getExtents();
			assertEquals(4,extents.size());
			assertEquals( 
				"4.5,49.5,4.8,49.8", 
				extents.get(0).getBoundingBox().toString()
			) ;
			assertEquals( 
				"4.01898628193261,49.2429346596619,5.42439068264001,50.150576343463", 
				extents.get(1).getBoundingBox().toString()
			) ;
		}
		
		{
			ReferenceSystemIdentifier rsi = metadata.getReferenceSystemIdentifier() ;
			assertNotNull(rsi);
			assertEquals("Lambert 93", rsi.getCode());
			assertEquals("EPSG", rsi.getCodeSpace());
			assertNull(rsi.getUri());
		}

		assertEquals(
			"Mise à jour du PLU de Balaives et Butz en 2014", 
			metadata.getLineage() 
		) ;
		
		assertEquals("Plan local d'urbanisme (PLU) - Balaives-et-Butz - approbation du 13/02/2014", metadata.getTitle());
		assertEquals("Plan local d'urbanisme (PLU) de la commune de Balaives-et-Butz (code INSEE 08042) approuvé le 13/02/2014", metadata.getAbstract()) ;
		assertEquals(ScopeCode.valueOf("dataset"), metadata.getType());
		
		assertEquals(
			LanguageCode.valueOf("fre"),
			metadata.getLanguage()
		);

		// spatialResolutions
		{
			assertEquals(0,metadata.getSpatialResolutions().size());		
		}
		
		// specifications
		{
			List<Specification> specifications = metadata.getSpecifications();
			assertEquals(0,specifications.size());
		}		

		// getResourceContact
		
		// dates
		assertNull(metadata.getDateOfPublication());
		assertEquals("2014-02-13", metadata.getDateOfCreation().toString());
		assertNull(metadata.getDateOfLastRevision());
		
		// getKeywords 
		{
			List<Keywords> keywords = metadata.getKeywords();
			assertEquals(2,keywords.size());
			
			assertEquals( 
				"PLU ; Plan d'occupation du sol ; Balaives-et-Butz ; urbanisme", 
				StringUtils.join(keywords.get(0).getKeywords(),';')
			);
			assertNull( keywords.get(0).getThesaurusName() );
			assertNull( keywords.get(0).getThesaurusDate() );
			
			assertEquals( "Usage des sols", StringUtils.join(keywords.get(1).getKeywords(),';') );
			assertEquals( "GEMET inspire themes - version 1.0", keywords.get(1).getThesaurusName() );
			assertEquals( "2008-06-01", keywords.get(1).getThesaurusDate().toString() );
		}

		
		// distributionFormats
		List<Format> distributionFormats = metadata.getDistributionFormats();
		assertEquals(1,distributionFormats.size());
		{
			Format distributionFormat = distributionFormats.get(0);
			assertNotNull(distributionFormat);
			assertEquals("ESRI Shapefile", distributionFormat.getName());
			assertEquals("", distributionFormat.getVersion());			
		}
		
		// onlineResources
		{
			List<OnlineResource> onlineResources = metadata.getLocators();
			assertEquals(1,onlineResources.size());
			assertEquals(
				"http://mairie-balaives-et-butz.fr/PLU",
				onlineResources.get(0).getUrl()
			);
			assertEquals(
				"WWW:LINK-1.0-http--link",
				onlineResources.get(0).getProtocol()
			);
			assertEquals(
				"site de la mairie de Belaives-Et-Butz",
				onlineResources.get(0).getName()
			);
		}
	}
	
	@Test
	public void testRegress04(){
		//Note : file encoding = ISO-8859-1, data-encoding=UTF-8
		Metadata metadata = getMetadataFromResource("/metadata/04.xml");
		
		assertEquals("2014-09-26",metadata.getMetadataDate().getValue());
		assertEquals(
			LanguageCode.valueOf("fre"), 
			metadata.getMetadataLanguage()
		);
		
		assertEquals( "fr-210800405-08042-plu20140213", metadata.getFileIdentifier() ) ;
		assertEquals( "fr-210800405-08042plu20140213", metadata.getIdentifier() ) ;
		assertEquals( 
			TopicCategoryCode.valueOf("planningCadastre"), 
			metadata.getTopicCategory() 
		) ;
		assertEquals( "8859part1", metadata.getCharacterSet().getValue() ) ;
		
		// constraints
		assertEquals( 4, metadata.getConstraints().size() ) ;
		
		{
			List<Extent> extents = metadata.getExtents();
			assertEquals(4,extents.size());
			assertEquals( 
				"4.5,49.5,4.8,49.8", 
				extents.get(0).getBoundingBox().toString() 
			) ;
		}

		{
			ReferenceSystemIdentifier rsi = metadata.getReferenceSystemIdentifier() ;
			assertNotNull(rsi);
			assertEquals("Lambert 93", rsi.getCode());
			assertEquals("EPSG", rsi.getCodeSpace());
			assertNull(rsi.getUri());
		}
		
		assertEquals(
			"Mise à jour du PLU de Balaives et Butz en 2014", 
			metadata.getLineage() 
		) ;
		
		assertEquals( "Plan local d'urbanisme (PLU) - Balaives-et-Butz - approbation du 13/02/2014", metadata.getTitle());
		assertEquals("Plan local d'urbanisme (PLU) de la commune de Balaives-et-Butz (code INSEE 08042) approuvé le 13/02/2014", metadata.getAbstract()) ;
		assertEquals(ScopeCode.valueOf("dataset"), metadata.getType());
		
		assertEquals(
			LanguageCode.valueOf("fre"),
			metadata.getLanguage()
		);

		// spatialResolutions
		{
			assertEquals(0,metadata.getSpatialResolutions().size());	
		}
		
		// specifications
		{
			List<Specification> specifications = metadata.getSpecifications();
			assertEquals(0,specifications.size());
		}		

		// getResourceContact
		assertNull(metadata.getDateOfPublication());
		assertEquals("2014-02-13", metadata.getDateOfCreation().toString());
		assertNull(metadata.getDateOfLastRevision());

		// getKeywords 
		{
			List<Keywords> keywords = metadata.getKeywords();
			assertEquals(2,keywords.size());
			
			assertEquals( 
				"PLU ; Plan d'occupation du sol ; Balaives-et-Butz ; urbanisme", 
				StringUtils.join(keywords.get(0).getKeywords(),';')
			);
			assertNull( keywords.get(0).getThesaurusName() );
			assertNull( keywords.get(0).getThesaurusDate() );

			assertEquals( 
				"Usage des sols", 
				StringUtils.join(keywords.get(1).getKeywords(),';')
			);
			assertEquals( "GEMET inspire themes - version 1.0", keywords.get(1).getThesaurusName() );
			assertEquals( "2008-06-01", keywords.get(1).getThesaurusDate().toString() );
		}
		
		
		// distributionFormats
		List<Format> distributionFormats = metadata.getDistributionFormats();
		assertEquals(1,distributionFormats.size());
		{
			Format distributionFormat = distributionFormats.get(0);
			assertNotNull(distributionFormat);
			assertEquals("ESRI Shapefile", distributionFormat.getName());
			assertEquals("", distributionFormat.getVersion());			
		}		
	}
	

	public void testRegress05(){
		Metadata metadata = getMetadataFromResource("/metadata/05.xml");
		
		assertEquals("2017-08-11T11:36:17.183+02:00", metadata.getMetadataDate().getValue());
		assertEquals(
			LanguageCode.valueOf("fre"), 
			metadata.getMetadataLanguage()
		);
		
		assertEquals( "fr-120066022-ldd-f7d6f581-31f7-43b7-857f-0687297c25c8", metadata.getFileIdentifier() ) ;
		assertEquals( "fr-120066022-orphan-residentifier-beed2702-07b7-4e4e-89fb-04b882e55334", metadata.getIdentifier() ) ;
		
		assertEquals( 
			TopicCategoryCode.valueOf("planningCadastre"), 
			metadata.getTopicCategory() 
		) ;
		assertEquals( "8859part15", metadata.getCharacterSet().getValue() ) ;
		
		// constraints
		assertEquals( 2, metadata.getConstraints().size() ) ;
		

		{
			List<Extent> extents = metadata.getExtents();
			assertEquals(1,extents.size());
			assertEquals( 
				"2.048286199569702,42.691341400146484,0.4412877559661865,43.92110061645508", 
				extents.get(0).getBoundingBox().toString() 
			) ;
		}
		
		{
			ReferenceSystemIdentifier rsi = metadata.getReferenceSystemIdentifier() ;
			assertNotNull(rsi);
			assertEquals("2154", rsi.getCode());
			assertEquals("EPSG", rsi.getCodeSpace());
			assertEquals("http://www.opengis.net/def/crs/EPSG/0/2154", rsi.getUri());
		}

		assertEquals(
			"Généalogie non renseignée", 
			metadata.getLineage() 
		) ;
		
		assertEquals("Servitude d'utilité publique (SUP) - Haute-Garonne", metadata.getTitle());
		assertEquals("Les Servitudes d’Utilité Publique peuvent-être classées en quatre catégories, selon leurs objectifs :* les servitudes relatives à la conservation du patrimoine ;* les servitudes relatives à l’utilisation de certaines ressources et équipements ;* les servitudes relatives à la Défense Nationale ;* les servitudes relatives à la salubrité et sécurité publique.Ce standard de données offre un cadre technique décrivant en détail la façon de dématérialiser ces servitudes en une base de données géographiques qui soit exploitable par un outil SIG et interopérable pour des territoires distincts.Le périmètre de ce standard de données englobe les notions relatives aux servitudes : les actes juridiques les instituant, les gestionnaires, les générateurs et les assiettes.Simultanément à l’élaboration de ce standard de données COVADIS, s’est tenu un groupe de travail sous l’égide de la DGALN dont l’objectif était la production de fiches méthodologiques décrivant les fondements juridiques et les aspects géomatiques de chaque catégorie de servitude. Ces fiches complètent par une description métier plus détaillée de chaque servitude le présent standard et sont en ligne sur le site du PND Urbanisme.Ce standard COVADIS a été élaboré à partir des catégories de SUP normalisées et du modèle CNIG SUP de 2007. Ce standard fait suite et vient compléter celui sur les PLU validé en 2010.Ce standard de données est dorénavant à utiliser au sein du MEDDTL et du MAAPRAT pour stocker, exploiter et échanger les données issues de la dématérialisation des SUP.", metadata.getAbstract());
		assertEquals(ScopeCode.valueOf("dataset"), metadata.getType());
		
		// getMetadataContact
		{
			ResponsibleParty contact = metadata.getMetadataContact();
			assertNotNull(contact);
			assertEquals("DDT 31 (Direction Départementale des Territoires de Haute-Garonne)", contact.getOrganisationName());
			assertEquals("ddt-adl-sig@haute-garonne.gouv.fr", contact.getElectronicMailAddress());
			assertEquals("pointOfContact", contact.getRole());
		}

		assertEquals(
			LanguageCode.valueOf("fre"),
			metadata.getLanguage()
		);
		
		// spatialResolutions
		{
			assertEquals(0,metadata.getSpatialResolutions().size());
		}
		
		// specifications
		{
			List<Specification> specifications = metadata.getSpecifications();
			assertEquals(1,specifications.size());
			
			assertEquals("Servitude-d-utilite-publique-SUP", specifications.get(0).getTitle());
			assertEquals("2014-07-03", specifications.get(0).getDate().toString());
			assertEquals("false", specifications.get(0).getDegree());			
		}
		
		assertNull(metadata.getSpatialRepresentationType());
		
		// getResourceContact
		{
			ResponsibleParty contact = metadata.getContact();
			assertNotNull(contact);
			assertEquals("DDT 31 (Direction Départementale des Territoires de Haute-Garonne)", contact.getOrganisationName());
			assertEquals("ddt-adl-sig@haute-garonne.gouv.fr", contact.getElectronicMailAddress());
			assertEquals("pointOfContact", contact.getRole());
		}
		
		// dates
		assertEquals("2015-03-06",metadata.getDateOfPublication().toString());
		assertNull(metadata.getDateOfCreation());
		assertNull(metadata.getDateOfLastRevision());
		
		// getKeywords (9 without thesaurusName, 1 with thesaurusName)
		{
			List<Keywords> keywords = metadata.getKeywords();
			assertEquals(3,keywords.size());
			
			assertEquals( 
				"Zones de gestion, de restriction ou de réglementation et unités de déclaration", 
				StringUtils.join(keywords.get(0).getKeywords(),';')
			);
			assertEquals( "GEMET - INSPIRE themes, version 1.0", keywords.get(0).getThesaurusName() );
			assertEquals( "2008-06-01", keywords.get(0).getThesaurusDate().toString() );
			
			assertEquals( 
				"Aménagement Urbanisme/Assiette Servitude", 
				StringUtils.join(keywords.get(1).getKeywords(),';')
			);
			assertEquals( "Arborescence thématique de la COVADIS", keywords.get(1).getThesaurusName() );
			assertEquals( "2009-04-06", keywords.get(1).getThesaurusDate().toString() );

			assertEquals( 
				"données ouvertes", 
				StringUtils.join(keywords.get(2).getKeywords(),';')
			);
			assertNull( keywords.get(2).getThesaurusName() );			
			assertNull( keywords.get(2).getThesaurusName() );
		}
		
		
		// distributionFormats
		List<Format> distributionFormats = metadata.getDistributionFormats();
		assertEquals(1,distributionFormats.size());
		{
			Format distributionFormat = distributionFormats.get(0);
			assertNotNull(distributionFormat);
			assertEquals("MapInfo TAB", distributionFormat.getName());
			assertEquals("", distributionFormat.getVersion());			
		}
		// onlineResources
		{
			List<OnlineResource> onlineResources = metadata.getLocators();
			assertEquals(5,onlineResources.size());
			assertEquals(
				"http://catalogue.geo-ide.developpement-durable.gouv.fr/catalogue/apps/search/?uuid=fr-120066022-ldd-f7d6f581-31f7-43b7-857f-0687297c25c8",
				onlineResources.get(0).getUrl()
			);
			assertEquals(
				"WWW:DOWNLOAD-1.0-http--download",
				onlineResources.get(0).getProtocol()
			);
			assertEquals(
				"Vue HTML des métadonnées sur internet",
				onlineResources.get(0).getName()
			);
		}
	}


}
