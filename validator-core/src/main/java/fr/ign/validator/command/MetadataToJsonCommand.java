package fr.ign.validator.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.gmd.MetadataISO19115;

/**
 * Convert ISO 19115 metadata to JSON
 * 
 * TODO support input as a File or Directory
 * 
 * @author MBorne
 *
 */
public class MetadataToJsonCommand extends AbstractCommand {

	public static final String NAME = "metadata_to_json";

	private File inputFile;
	
	private File outputFile;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void execute() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		Metadata metadata = MetadataISO19115.readFile(inputFile);
		objectMapper.writeValue(getOutputStream(), metadata);
	}

	
	private PrintStream getOutputStream() throws FileNotFoundException{
		if ( outputFile == null ){
			return System.out;
		}
		if ( outputFile.exists() ){
			outputFile.delete();
		}
		return new PrintStream(outputFile);
	}
	
	
	@Override
	protected void buildCustomOptions(Options options) {
		// input
		{
			Option option = new Option("i", "input", true, "Input file (xml)");
			option.setRequired(true);
			option.setType(File.class);
			options.addOption(option);
		}
		// output
		{
			Option option = new Option("o", "output", true, "Output file (json)");
			option.setRequired(false);
			option.setType(File.class);
			options.addOption(option);
		}
	}

	@Override
	protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
		inputFile  = (File) commandLine.getParsedOptionValue("input");
		outputFile = (File) commandLine.getParsedOptionValue("output");
	}

}
