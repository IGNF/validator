package fr.ign.validator.cnig.command;

import java.io.IOException;
import java.util.ArrayList;

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
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.repository.DocumentModelRepository;
import fr.ign.validator.repository.xml.XmlDocumentModelRepository;

/**
 * Experimental - DocumentValidator customized for CNIG standards
 * 
 * <ul>
 * 	<li>"--version" option is removed, version is detected from documentName and metadata.specifications</li>
 * </ul>
 * 
 * TODO validate all current GPU documents with this command before replacing previous calls
 * 
 * @author MBorne
 *
 */
public class CnigDocumentValidatorCommand extends DocumentValidatorCommand {

	public static final String NAME = "cnig_document_validator";

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("CnigDocumentValidatorCommand");
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected void buildPluginsOption(Options options) {
	
	}
	
	@Override
	protected void parsePluginsOption(CommandLine commandLine) {
		this.plugins = new ArrayList<>();
		PluginManager pluginManager = new PluginManager();
		this.plugins.add(pluginManager.getPluginByName(CnigPlugin.NAME));
	}
	
	
	@Override
	protected void buildVersionOption(Options options) {
		// remove option
	}
	
	@Override
	protected void parseVersion(CommandLine commandLine) throws ParseException {
		// fallback version
		this.documentModelName = "not_found";
		
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

}
