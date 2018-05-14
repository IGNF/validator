package fr.ign.validator.cnig.validation.metadata;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Document;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.ReferenceSystemIdentifier;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.validation.Validator;

/**
 * Ensures that ReferenceSystemIdentifier is defined according to guidelines :
 * 
 * http://cnig.gouv.fr/wp-content/uploads/2017/12/171219_Consignes_saisie_metadonnees_DU_v2017.pdf#page=10
 *  
 * @author MBorne
 *
 */
public class CnigMetadataReferenceSystemIdentifierValidator implements Validator<Metadata>, ValidatorListener {
	
	private Map<String, String> codesToUri = new HashMap<>();
	
	public CnigMetadataReferenceSystemIdentifierValidator(){
		//Metropolitan France 2154 RGF93LAMB93
		codesToUri.put("EPSG:2154", "http://www.opengis.net/def/crs/EPSG/0/2154");
		codesToUri.put("IGNF:RGF93LAMB93", "http://registre.ign.fr/ign/IGNF/crs/IGNF/RGF93LAMB93");
		
		//Guadeloupe 32620 WGS84UTM20
		//Martinique 32620 WGS84UTM20
		codesToUri.put("EPSG:32620", "http://www.opengis.net/def/crs/EPSG/0/32620");
		codesToUri.put("IGNF:WGS84UTM20", "http://registre.ign.fr/ign/IGNF/crs/IGNF/WGS84UTM20");
		
		//Guyane 2972 RGFG95UTM22
		codesToUri.put("EPSG:2972", "http://www.opengis.net/def/crs/EPSG/0/2972");
		codesToUri.put("IGNF:RGFG95UTM22", "http://registre.ign.fr/ign/IGNF/crs/IGNF/RGFG95UTM22");

		//La Réunion 2975 RGR92UTM40S
		codesToUri.put("EPSG:2975", "http://www.opengis.net/def/crs/EPSG/0/2975");
		codesToUri.put("IGNF:RGR92UTM40S", "http://registre.ign.fr/ign/IGNF/crs/IGNF/RGR92UTM40S");
		
		//Mayotte 4471 RGM04UTM38S
		codesToUri.put("EPSG:4471", "http://www.opengis.net/def/crs/EPSG/0/4471");
		codesToUri.put("IGNF:RGM04UTM38S", "http://registre.ign.fr/ign/IGNF/crs/IGNF/RGM04UTM38S");
				
		//Saint-Pierre-et-Miquelon 4467 RGSPM06U21
		codesToUri.put("EPSG:4467", "http://www.opengis.net/def/crs/EPSG/0/4467");
		codesToUri.put("IGNF:RGSPM06U21", "http://registre.ign.fr/ign/IGNF/crs/IGNF/RGSPM06U21");
	}
	
	@Override
	public void validate(Context context, Metadata metadata) {
		ReferenceSystemIdentifier referenceSystemIdentifier = metadata.getReferenceSystemIdentifier();
		if ( referenceSystemIdentifier == null ){
			context.report(
				CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_NOT_FOUND
			);
			return ;
		}
		String code = referenceSystemIdentifier.getCode();
		if ( StringUtils.isEmpty(code) ){
			context.report(
				CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_CODE_NOT_FOUND
			);
			return ;
		}
		if ( ! codesToUri.containsKey(code) ){
			context.report(
				CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_CODE_INVALID,
				code,
				StringUtils.join(codesToUri.keySet(),", ")
			);
			return ;
		}
		

		String uri = referenceSystemIdentifier.getUri();
		if ( StringUtils.isEmpty(uri) ){
			context.report(
				CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_NOT_FOUND
			);
			return ;
		}
		String expectedUri = codesToUri.get(code);
		if ( ! uri.equals(expectedUri) ){
			context.report(
				CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_UNEXPECTED,
				uri,
				expectedUri
			);
			return ;
		}
	}

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		for (FileModel fileModel : context.getDocumentModel().getFileModels()) {
			if ( ! (fileModel instanceof MetadataModel) ){
				continue;
			}
			((MetadataModel)fileModel).addMetadataValidator(this);
		}
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		
	}

	
}
