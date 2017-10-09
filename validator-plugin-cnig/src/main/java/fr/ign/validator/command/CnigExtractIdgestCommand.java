package fr.ign.validator.command;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

import fr.ign.validator.cnig.utils.IdgestExtractor;


/**
 * 
 * Extraction de IDGEST pour les SUP (pré-validateur CNIG)
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
	public int execute(){
		// extract idgest from SERVITUDE file
		IdgestExtractor idgestExtractor = new IdgestExtractor();
		String idGest = idgestExtractor.findIdGest(servitudeFile);
		if ( idGest == null || idGest.isEmpty() ){
			System.err.println("fail to read IdGest from "+servitudeFile);
			return 1;
		}
		
		// write results to file
		File resultFile = new File(servitudeFile.getParent(), "idGest.txt");
		try {
			FileUtils.writeStringToFile(resultFile, idGest);
		} catch (IOException e) {
			System.err.println("fail to write "+idGest+" to "+resultFile);
			return 1;
		}
		
		return 0;
	}

}

