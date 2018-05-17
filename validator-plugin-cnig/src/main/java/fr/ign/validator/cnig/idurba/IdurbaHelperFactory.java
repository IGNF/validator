package fr.ign.validator.cnig.idurba;

import fr.ign.validator.cnig.idurba.impl.IdurbaHelperV1;
import fr.ign.validator.cnig.idurba.impl.IdurbaHelperV2;
import fr.ign.validator.cnig.utils.DocumentModelNameUtils;
import fr.ign.validator.model.DocumentModel;

public class IdurbaHelperFactory {

	/**
	 * Get implementation according to document model
	 * @param documentModel
	 * @return
	 */
	public static IdurbaHelper getInstance(DocumentModel documentModel){
		String documentModelName = documentModel.getName();
		
		String documentType = DocumentModelNameUtils.getDocumentType(documentModelName);
		if ( documentType == null || documentType.equalsIgnoreCase("SUP") || documentType.equalsIgnoreCase("SCOT") ){
			return null;
		}
	
		String version = DocumentModelNameUtils.getVersion(documentModelName);
		if ( version.equals("2013") || version.equals("2014") ){
			return new IdurbaHelperV1();
		}else {
			return new IdurbaHelperV2();
		}
	}

}
