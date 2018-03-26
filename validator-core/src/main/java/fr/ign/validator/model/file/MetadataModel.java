package fr.ign.validator.model.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.data.file.MetadataFile;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.validation.Validator;
import fr.ign.validator.validation.file.metadata.AbstractValidator;
import fr.ign.validator.validation.file.metadata.CharacterSetValidator;
import fr.ign.validator.validation.file.metadata.ContactValidator;
import fr.ign.validator.validation.file.metadata.DatesValidator;
import fr.ign.validator.validation.file.metadata.DistributionFormatsValidator;
import fr.ign.validator.validation.file.metadata.ExtentsValidator;
import fr.ign.validator.validation.file.metadata.FileIdentifierValidator;
import fr.ign.validator.validation.file.metadata.IdentifierValidator;
import fr.ign.validator.validation.file.metadata.LanguageValidator;
import fr.ign.validator.validation.file.metadata.LineageValidator;
import fr.ign.validator.validation.file.metadata.LocatorsValidator;
import fr.ign.validator.validation.file.metadata.MetadataContactValidator;
import fr.ign.validator.validation.file.metadata.MetadataDateValidator;
import fr.ign.validator.validation.file.metadata.MetadataLanguageValidator;
import fr.ign.validator.validation.file.metadata.SpatialRepresentationTypeValidator;
import fr.ign.validator.validation.file.metadata.SpatialResolutionsValidator;
import fr.ign.validator.validation.file.metadata.SpecificationsValidator;
import fr.ign.validator.validation.file.metadata.TitleValidator;
import fr.ign.validator.validation.file.metadata.TopicCategoryValidator;
import fr.ign.validator.validation.file.metadata.TypeValidator;

/**
 * 
 * Represents a metadata file
 *  
 *
 */
public class MetadataModel extends FileModel {
	public static final String TYPE = "metadata" ;
	
	/**
	 * Metadata content validators
	 */
	private List<Validator<Metadata>> metadataValidators = new ArrayList<>();
	
	public MetadataModel(){
		super();
		registerDefaultValidators();
	}

	@Override
	public String getType() {
		return TYPE ;
	}
	
	@Override
	public String getRegexpSuffix() {
		return "\\.(xml|XML)";
	}

	@Override
	public MetadataFile createDocumentFile(File path) {
		return new MetadataFile(this,path);
	}

	public List<Validator<Metadata>> getMetadataValidators() {
		return metadataValidators;
	}
	
	public void addMetadataValidator(Validator<Metadata> validator){
		metadataValidators.add(validator);
	}

	private void registerDefaultValidators() {
		// fileIdentifier
		addMetadataValidator(new FileIdentifierValidator());
		
		// title
		addMetadataValidator(new TitleValidator());
		
		// abstract
		addMetadataValidator(new AbstractValidator());

		// type
		addMetadataValidator(new TypeValidator());
		
		// locators
		addMetadataValidator(new LocatorsValidator());

		// identifier
		addMetadataValidator(new IdentifierValidator());
		// language
		addMetadataValidator(new LanguageValidator());
		
		// topicCategory
		addMetadataValidator(new TopicCategoryValidator());

		// keywords
		// TODO

		// extents
		addMetadataValidator(new ExtentsValidator());
		
		// referenceSystemIdentifier
		// TODO

		// dateOfPublication
		// dateOfLastRevision
		// dateOfCreation
		addMetadataValidator(new DatesValidator());

		// characterSet
		addMetadataValidator(new CharacterSetValidator());

		// contraints
		// TODO

		// distributionFormats
		addMetadataValidator(new DistributionFormatsValidator());

		// spatialRepresentationType
		addMetadataValidator(new SpatialRepresentationTypeValidator());

		// lineage
		addMetadataValidator(new LineageValidator());

		// spatialResolutions
		addMetadataValidator(new SpatialResolutionsValidator());

		// specifications
		addMetadataValidator(new SpecificationsValidator());

		// contact
		addMetadataValidator(new ContactValidator());

		// metadataContact
		addMetadataValidator(new MetadataContactValidator());

		// metadataDate
		addMetadataValidator(new MetadataDateValidator());

		// metadataLanguage
		addMetadataValidator(new MetadataLanguageValidator());
	}
}
