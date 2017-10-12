package fr.ign.validator.data.file;

import java.io.File;

import fr.ign.validator.Context;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.exception.InvalidMetadataException;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.gmd.MetadataISO19115;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.validation.Validator;

public class MetadataFile extends DocumentFile {

	private MetadataModel fileModel;

	public MetadataFile(MetadataModel fileModel, File path) {
		super(path);
		this.fileModel = fileModel;
	}

	@Override
	public MetadataModel getFileModel() {
		return fileModel;
	}

	@Override
	protected void validateContent(Context context) {
		try {
			Metadata metadata = MetadataISO19115.readFile(getPath());
			for (Validator<Metadata> validator : getFileModel().getMetadataValidators()) {
				validator.validate(context, metadata);
			}
		} catch (InvalidMetadataException e) {
			context.report( 
				CoreErrorCodes.METADATA_INVALID_FILE,
				context.relativize(getPath())
			);
		}
	}

}
