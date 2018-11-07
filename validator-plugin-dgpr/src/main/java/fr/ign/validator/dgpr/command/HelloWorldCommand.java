package fr.ign.validator.dgpr.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import fr.ign.validator.command.AbstractCommand;

/**
 *
 */
public class HelloWorldCommand extends AbstractCommand {

  public static final String NAME = "hello_world";

  String option;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  protected void buildCustomOptions(Options options) {
    // input
    {
      Option option = new Option("o", "option", true, "example option");
      option.setRequired(false);
      options.addOption(option);
    }
  }

  @Override
  protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
    if (commandLine.hasOption("option")){
      this.option = commandLine.getOptionValue("option");
    }
  }

  @Override
  public void execute() throws Exception {
    // do your stuff
    // for example sysout 'Hello <option>'
    if (this.option != null) {
      System.out.println("Hello World : "+this.option);
    } else {
      System.out.println("No options were provided");
    }
  }

}
