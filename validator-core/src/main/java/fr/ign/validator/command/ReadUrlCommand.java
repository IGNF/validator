package fr.ign.validator.command;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * 
 * Read URL to check proxy configuration
 * 
 * @author MBorne
 */
public class ReadUrlCommand extends AbstractCommand {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("ReadUrlCommand");

    public static final String NAME = "read_url";

    private String url;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void execute() throws Exception {
        URL url = new URL(this.url);
        InputStream in = url.openStream();
        try {
            InputStreamReader inR = new InputStreamReader(in);
            BufferedReader buf = new BufferedReader(inR);
            String line;
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
            }
        } finally {
            in.close();
        }
    }

    @Override
    protected void buildCustomOptions(Options options) {
        {
            Option option = new Option("url", "url", true, "URL to test");
            option.setRequired(true);
            options.addOption(option);
        }
    }

    @Override
    protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
        this.url = commandLine.getOptionValue("url");
        log.info("URL : {}", this.url);
    }

}
