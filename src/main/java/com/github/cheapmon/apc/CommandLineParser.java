package com.github.cheapmon.apc;

import com.github.cheapmon.apc.APCOptions.ExtractionMode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
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
   * @return Parsed options
   */
  public static APCOptions parse(String[] args) {
    APCOptions options = new APCOptions();
    try {
      CommandLine cl = new DefaultParser().parse(getOptions(), args);
      if (cl.hasOption("help")) {
        printUsage();
      }
      String[] ids = getIDs(cl.getOptionValues("id"), cl.getOptionValue("file"));
      ExtractionMode extractionMode = getMode(cl.hasOption("extract-model"));
      String device = getDevice(cl.getOptionValue("device"));
      options.setIds(ids);
      options.setExtractionMode(extractionMode);
      options.setDevice(device);
    } catch (ParseException ex) {
      printUsage(ex.getMessage());
    }
    return options;
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
    options.addOption(Option.builder("i").longOpt("id").hasArgs().desc("App ids").build());
    options.addOption("f", "file", true, "File containing app ids, separated by newlines");
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
   * Get list of applications to crawl for textual information.<br><br>
   *
   * Apps to extract are input either directly via command line or via one file. If neither is
   * given, APC halts. When given both, the file is favored.<br><br>
   *
   * In a file, ids are expected to be listed separated by newlines.
   *
   * @param ids Given IDs
   * @param file Given file containing ids
   * @return IDs of apps to crawl
   */
  private static String[] getIDs(String[] ids, String file) {
    if (file == null) {
      if (ids == null) {
        printUsage("Please supply at least one application id.");
      } else {
        return ids;
      }
    } else {
      try {
        return Files.readAllLines(Paths.get(file)).toArray(new String[0]);
      } catch (IOException ex) {
        printUsage("Could not read ID file. Please check for errors.");
      }
    }
    return new String[0];
  }

  /**
   * Get mode of extraction used when crawling applications.<br><br>
   *
   * Simply checks whether the command line option "extract-model" has been set.
   *
   * @param option Command line has option
   * @return Mode of extraction
   */
  private static ExtractionMode getMode(boolean option) {
    if (option) {
      return ExtractionMode.MODEL;
    } else {
      return ExtractionMode.POLICY;
    }
  }

  /**
   * Get device extraction is run on.<br><br>
   *
   * Input is a device label. If none is given, defaults to first device in list. If no device
   * is attached or the given label is incorrect, APC halts.
   *
   * @param device Device label given by user
   * @return Device label chosen by APC
   */
  private static String getDevice(String device) {
    String[] deviceList = ADBConnector.deviceList();
    if (deviceList.length == 0) {
      printUsage(
          "Please attach at least one Android device and check if Android debugging is active.");
    }
    if (device == null) {
      return deviceList[0];
    }
    if (Arrays.asList(deviceList).contains(device)) {
      return device;
    } else {
      System.out
          .println("Given device label is incorrect. Please check for errors.\nAvailable devices:");
      for (String dev : deviceList) {
        System.out.println(String.format("* %s", dev));
      }
      System.exit(0);
    }
    return "";
  }

  /**
   * Display usage and exit.
   */
  private static void printUsage() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setOptionComparator(null);
    formatter.printHelp("./apc.sh", getOptions(), true);
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
