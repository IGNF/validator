package fr.ign.validator.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.FileModel.MandatoryMode;

/**
 * Adapte la sérialization/désérialisation XML des FileModel et leur hiérarchie
 * @author MBorne
 *
 */
public class FileModelAdapter extends XmlAdapter<FileModelAdapter.AdaptedFileModel, FileModel> {

	@Override
	public FileModel unmarshal(AdaptedFileModel adaptedFileModel) throws Exception {
		if ( null == adaptedFileModel ){
			return null ;
		}
		FileModel fileModel = FileModel.newFileModelByType( adaptedFileModel.type ) ;
		if ( null == fileModel ){
			throw new Exception(String.format(
				"attribut type invalide dans balise <file> : {}",adaptedFileModel.type
			));
		}
		fileModel.setName(adaptedFileModel.name);
		fileModel.setRegexp(adaptedFileModel.path);
		fileModel.setMandatory(adaptedFileModel.mandatory);
		return fileModel ;
	}
	
	@Override
	public AdaptedFileModel marshal(FileModel fileModel) throws Exception {
		if ( null == fileModel ){
			return null ;
		}
		AdaptedFileModel adaptedFileModel = new AdaptedFileModel() ;
		adaptedFileModel.type = fileModel.getType() ;
		adaptedFileModel.name = fileModel.getName() ;
		adaptedFileModel.path = fileModel.getRegexp() ;
		adaptedFileModel.mandatory = fileModel.getMandatory() ;
		return adaptedFileModel ;
	}
	
	
	public static class AdaptedFileModel {
		public String name ;
		public String type ;
		public String path ;
		public MandatoryMode mandatory = MandatoryMode.WARN ;
	}

}
