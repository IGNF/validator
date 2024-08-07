package fr.ign.validator.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import fr.ign.validator.string.transform.IsoControlEscaper;
import fr.ign.validator.string.transform.StringSimplifier;
import fr.ign.validator.tools.Characters;

/**
 *
 * Generate a unicode table to document string fixer
 *
 * @author MBorne
 *
 */
public class UnicodeTableCommand extends AbstractCommand {

    private static final String OPT_OUTPUT = "output";

    public static final String NAME = "unicode_table";

    private File outputFile;

    private int maxCodePoint = 10000;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Export a unicode table to document optional transforms (CSV)";
    }

    @Override
    protected void buildCustomOptions(Options options) {
        // output
        {
            Option option = new Option("O", OPT_OUTPUT, true, "Output CSV file");
            option.setRequired(false);
            option.setType(File.class);
            options.addOption(option);
        }
    }

    @Override
    protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
        if (commandLine.hasOption(OPT_OUTPUT)) {
            this.outputFile = (File) commandLine.getParsedOptionValue(OPT_OUTPUT);
        }
    }

    @Override
    public void execute() throws Exception {
        generateUnicodeTable();
    }

    private void generateUnicodeTable() throws Exception {
        IsoControlEscaper controlEscaper = new IsoControlEscaper(false);

        StringSimplifier simplifierCommon = new StringSimplifier();
        simplifierCommon.loadCommon();

        StringSimplifier simplifierLatin1 = new StringSimplifier();
        simplifierLatin1.loadCommon();
        simplifierLatin1.loadCharset(StandardCharsets.ISO_8859_1);

        OutputStream os = outputFile != null ? new FileOutputStream(outputFile) : stdout;
        CSVPrinter printer = new CSVPrinter(
            new BufferedWriter(
                new OutputStreamWriter(os, StandardCharsets.UTF_8)
            ), CSVFormat.RFC4180
        );
        try {
            printer.printRecord(
                "codePoint",
                "hexa",
                "escape_control",
                "name",
                "simplify_common",
                "simplify_latin1",
                "uri"
            );
            for (int codePoint = 0; codePoint < maxCodePoint; codePoint++) {
                String original = new String(Character.toChars(codePoint));

                String simplifiedCommon = simplifierCommon.transform(original);
                String simplifiedLatin1 = simplifierLatin1.transform(original);

                printer.printRecord(
                    codePoint,
                    Characters.toHexa(codePoint),
                    controlEscaper.transform(original),
                    Character.getName(codePoint),
                    simplifiedCommon.equals(original) ? "NOP" : simplifiedCommon,
                    simplifiedLatin1.equals(original) ? "NOP" : simplifiedLatin1,
                    Characters.toURI(codePoint)
                );
            }
        } finally {
            printer.close();
        }
    }

}
