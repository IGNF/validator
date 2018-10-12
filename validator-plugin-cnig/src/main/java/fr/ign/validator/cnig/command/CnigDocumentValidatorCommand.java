package fr.ign.validator.cnig.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.cnig.CnigPlugin;
import fr.ign.validator.cnig.standard.DocumentModelFinder;
import fr.ign.validator.command.DocumentValidatorCommand;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.FileModel.MandatoryMode;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.repository.DocumentModelRepository;
import fr.ign.validator.repository.xml.XmlDocumentModelRepository;

/**
 * 
 * Experimental - DocumentValidator customized to detect CNIG standard version
 * according to document name and metadata specification.
 * 
 * This command is not production ready but it allows massive validation on existing
 * documents to avoid break changes.
 * 
 * @author MBorne
 *
 */
public class CnigDocumentValidatorCommand extends DocumentValidatorCommand {

	public static final String NAME = "cnig_document_validator";
	
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("CnigDocumentValidatorCommand");
	
	public static final String FALLBACK_VERSION = "cnig_metadata";
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected void buildPluginsOption(Options options) {
		/* disable --plugin option */	
	}
	
	@Override
	protected void parsePluginsOption(CommandLine commandLine) {
		/* Force CnigPlugin activation */
		this.plugins = new ArrayList<>();
		PluginManager pluginManager = new PluginManager();
		this.plugins.add(pluginManager.getPluginByName(CnigPlugin.NAME));
	}

	
	@Override
	protected void buildVersionOption(Options options) {
		/* remove --version option */
	}

	@Override
	protected void parseVersion(CommandLine commandLine) throws ParseException {
		/* fallback built-in version */
		this.documentModelName = FALLBACK_VERSION;
		
		log.info(MARKER, "Searching CNIG standard for {}", documentPath);
		DocumentModelRepository repository = new XmlDocumentModelRepository(configDir);
		DocumentModelFinder documentModelFinder = new DocumentModelFinder(repository);
		try {
			DocumentModel documentModel = documentModelFinder.findByDocumentPath(documentPath);
			if ( documentModel != null ){
				this.documentModelName = documentModel.getName();
			}
		} catch (IOException e) {
			log.info(MARKER, "Fail to load DocumentModel");
			log.error(MARKER, e.getMessage());
		}

		log.info(MARKER, "CNIG version : {}", documentModelName);
	}
	
	@Override
	protected DocumentModel loadDocumentModel() throws IOException {
		if ( this.documentModelName.equals(FALLBACK_VERSION) ){
			DocumentModel documentModel = new DocumentModel();
			documentModel.setName(FALLBACK_VERSION);
			/* add metadata model */
			List<FileModel> fileModels = new ArrayList<>();
			{
				FileModel fileModel = new MetadataModel();
				fileModel.setName("METADONNEES");
				fileModel.setRegexp("[^\\/]*");
				fileModel.setMandatory(MandatoryMode.ERROR);
				fileModels.add(fileModel);
			}
			return documentModel;
		}
		return super.loadDocumentModel();
	}


}
