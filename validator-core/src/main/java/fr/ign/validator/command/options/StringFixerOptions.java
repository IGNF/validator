package fr.ign.validator.command.options;

import java.nio.charset.Charset;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import fr.ign.validator.string.StringFixer;

public class StringFixerOptions {

	/**
	 * Define command line options to configure StringFixer
	 * 
	 * @param options
	 */
	public static void buildOptions(Options options) {
		{
			Option option = new Option(null, "string-all", true,
				"Apply string-fix-utf8, string-simplify-common and string-simplify-charset for the given charset");
			option.setRequired(false);
			option.setArgName("CHARSET");
			options.addOption(option);
		}
		{
			Option option = new Option(null, "string-fix-utf8", false,
				"Fix double UTF-8 encoding characters (suppose a bad LATIN1 declaration)");
			option.setRequired(false);
			options.addOption(option);
		}
		{
			Option option = new Option(null, "string-simplify-common", false,
				"Replace unicode characters by equivalents for a better support by fonts");
			option.setRequired(false);
			options.addOption(option);
		}
		{
			Option option = new Option(null, "string-simplify-charset", true,
				"Replace unicode characters by equivalents for a given charset (ex : œ is replaced by oe for LATIN1)");
			option.setRequired(false);
			option.setArgName("CHARSET");
			options.addOption(option);
		}

		{
			Option option = new Option(null, "string-escape-controls", false,
				"Espace control caracters to hexadecimal form (\\uXXXX)");
			option.setRequired(false);
			options.addOption(option);
		}
		{
			Option option = new Option(null, "string-escape-charset", true,
				"Espace characters not supported by a given charset to hexadecimal form (\\uXXXX)");
			option.setRequired(false);
			option.setArgName("CHARSET");
			options.addOption(option);
		}
	}

	/**
	 * Parse command line to build StringFixer
	 * 
	 * @param commandLine
	 * @return
	 */
	public static StringFixer parseCommandLine(CommandLine commandLine) {
		if (commandLine.hasOption("string-all")) {
			String charsetName = commandLine.getOptionValue("string-all");
			Charset charset = Charset.forName(charsetName);
			return StringFixer.createFullStringFixer(charset);
		}

		StringFixer stringFixer = new StringFixer();
		
		// double utf-8 fixing
		stringFixer.setUtf8Fixed(commandLine.hasOption("string-fix-utf8"));

		// simplification
		stringFixer.setCommonSimplified(commandLine.hasOption("string-simplify-common"));
		if ( commandLine.hasOption("string-simplify-charset") ){
			String charsetName = commandLine.getOptionValue("string-simplify-charset");
			stringFixer.setCharsetSimplified(Charset.forName(charsetName));
		}
		
		// escaping
		stringFixer.setControlEscaped(commandLine.hasOption("string-escape-controls"));
		if ( commandLine.hasOption("string-escape-charset") ){
			String charsetName = commandLine.getOptionValue("string-escape-charset") ;
			stringFixer.setCharsedEscaped(Charset.forName(charsetName));
		}

		return stringFixer;
	}

}
