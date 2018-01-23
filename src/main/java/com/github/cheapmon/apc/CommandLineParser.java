package com.github.cheapmon.apc;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Command line options and parsing. <br><br>
 *
 * <ul>
 * <li>Help message</li>
 * <li>User input (application identification)</li>
 * <li>Device to use</li>
 * <li>Searching algorithm to use</li>
 * <li>(Model) extraction</li>
 * <li>Level of logging</li>
 * </ul>
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class CommandLineParser {

  /**
   * Parse options given by command line, check for validity, pass on for further use.
   *
   * @param args Command line arguments
   */
  public static void parse(String[] args) {
    try {
      CommandLine cl = new DefaultParser().parse(getOptions(), args);
      if (cl.hasOption("help")) {
        printUsage();
      }
    } catch (ParseException ex) {
      printUsage(ex.getMessage());
    }
  }

  /**
   * Definition of available command line arguments. <br><br>
   *
   * Run {@code ./apc.sh --help} to inspect.
   *
   * @return Command line options
   */
  private static Options getOptions() {
    Options options = new Options();
    options.addOption("h", "help", false, "This help message");
    OptionGroup inputOptions = new OptionGroup();
    inputOptions.addOption(
        Option.builder("i").longOpt("id").hasArgs().desc("App ids").build());
    inputOptions.addOption(
        Option.builder("f").longOpt("file").hasArg().desc("File containing app ids").build());
    options.addOptionGroup(inputOptions);
    options.addOption(
        Option.builder("d").longOpt("device").hasArgs().desc("Device to run extraction on")
            .build());
    options.addOption(
        Option.builder("s").longOpt("search").hasArgs().desc("Searching algorithm").build());
    options.addOption("m", "extract-model", false, "Extract model of app");
    options.addOption("l", "log-level", true, "Log level");
    return options;
  }

  /**
   * Display usage and exit.
   */
  private static void printUsage() {
    new HelpFormatter().printHelp("./apc.sh", getOptions(), true);
    System.exit(0);
  }

  /**
   * Display usage, show extra message and exit.
   *
   * @param msg Message to display
   */
  private static void printUsage(String msg) {
    System.out.println(msg);
    printUsage();
  }

}
