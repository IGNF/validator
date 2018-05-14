package fr.ign.validator.cnig.command;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import fr.ign.validator.cnig.utils.IdgestExtractor;
import fr.ign.validator.command.AbstractCommand;


/**
 * 
 * Extracts IDGEST for SUP (cnig pre-validator)
 * 
 * @author fcerizay
 *
 */
public class CnigExtractIdgestCommand extends AbstractCommand {
	
	public static final String NAME = "cnig_extract_idgest";
	
	/**
	 * Input SERVITUDE
	 */
	private File servitudeFile ;

	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	protected void buildCustomOptions(Options options) {
		// input
		{
			Option option = new Option("i", "input", true, "Fichier à convertir");
			option.setRequired(true);
			options.addOption(option);
		}
	}

	@Override
	protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
		this.servitudeFile = new File(commandLine.getOptionValue("input"));
	}

	@Override
	public void execute() throws Exception {
		// extract idgest from SERVITUDE file
		IdgestExtractor idgestExtractor = new IdgestExtractor();
		String idGest = idgestExtractor.findIdGest(servitudeFile);
		if ( StringUtils.isEmpty(idGest) ){
			throw new Exception("fail to read IdGest from "+servitudeFile);
		}
		
		// write results to file
		File resultFile = new File(servitudeFile.getParent(), "idGest.txt");
		FileUtils.writeStringToFile(resultFile, idGest);
	}

}

