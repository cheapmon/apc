package com.github.cheapmon.apc.util;

import com.github.cheapmon.apc.APCOptions;
import com.github.cheapmon.apc.APCOptions.ExtractionMode;
import com.github.cheapmon.apc.failure.APCException;
import java.io.File;
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
 * Command line options and parsing.<br><br>
 *
 * <ul>
 * <li>Help message</li>
 * <li>User input (application identification)</li>
 * <li>Device to use</li>
 * <li>Searching algorithm to use</li>
 * <li>(Model) extraction</li>
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
  public static APCOptions parse(String[] args) throws APCException {
    APCOptions options = new APCOptions();
    try {
      CommandLine cl = new DefaultParser().parse(getOptions(), args);
      if (cl.hasOption("help")) {
        printUsage();
      }
      String[] ids = getIDs(cl.getOptionValues("id"), cl.getOptionValue("file"));
      ExtractionMode extractionMode = getMode(cl.hasOption("extract-model"));
      String device = getDevice(cl.getOptionValue("device"));
      String algorithmPath = getAlgorithmPath(cl.getOptionValue("search"));
      options.setIds(ids);
      options.setExtractionMode(extractionMode);
      options.setDevice(device);
      options.setAlgorithmPath(algorithmPath);
      APCLogger
          .info(CommandLineParser.class, String.format("Found %s application ids", ids.length));
      APCLogger
          .info(CommandLineParser.class, String.format("Extraction mode is %s", extractionMode));
      APCLogger.info(CommandLineParser.class, String.format("Using device %s", device));
      APCLogger.info(CommandLineParser.class,
          String.format("Using %s", new File(algorithmPath).getName()));
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
    options.addOption("d", "device", true, "Device to run extraction on");
    options.addOption("s", "search", true, "Searching algorithm");
    options.addOption("m", "extract-model", false, "Extract model of app");
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
  private static String getDevice(String device) throws APCException {
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
      System.out.println("Given device label is incorrect. Please check for errors.");
      System.out.println("Available devices:");
      for (String dev : deviceList) {
        System.out.println(String.format("* %s", dev));
      }
      System.exit(0);
    }
    return "";
  }

  /**
   * Get search algorithm used when performing extraction.<br><br>
   *
   * Input is an algorithm label, which is a short version of the simple class name of the
   * algorithm. If none is given, defaults to optimized search. When the label is incorrect,
   * APC halts.<br><br>
   *
   * Short name of an algorithm is all capital letters in the filename. The full file path is
   * returned.<br><br>
   *
   * Java modules can't be dependant on Android modules, so reflections or service loaders can't
   * be used here. Simply searches the submodule for files with the correct file name. Please note
   * that this is just a workaround and does not safely guarantee extensibility, since this won't
   * check for correct implementations, etc.
   *
   * @param algorithmPath Algorithm label given by user
   * @return Full path of algorithm chosen by APC
   */
  private static String getAlgorithmPath(String algorithmPath) throws APCException {
    if (algorithmPath == null) {
      return Paths
          .get(".", "droid", "src", "androidTest", "java", "com", "github", "cheapmon", "apc",
              "droid", "search", "OptimizedSearch.java").toAbsolutePath().toString();
    }
    try {
      File[] files = Files.walk(Paths.get(".", "droid"))
          .filter(file -> file.getFileName().toString().endsWith("Search.java"))
          .map(path -> new File(path.toAbsolutePath().toString()))
          .toArray(File[]::new);
      for (File file : files) {
        if (file.getName().replaceAll("[a-z\\.]+", "").toLowerCase()
            .equalsIgnoreCase(algorithmPath)) {
          return file.getAbsolutePath();
        }
      }
      System.out.println("Given algorithm label is incorrect. Please check for errors.");
      System.out.println("Available search algorithms:");
      for (File file : files) {
        String name = file.getName().replaceAll(".java", "");
        String shortName = name.replaceAll("[a-z]+", "").toLowerCase();
        System.out.println(String.format("* %-4s %s", shortName, name));
      }
      System.exit(0);
    } catch (IOException ex) {
      throw new APCException("Filtering submodule folders failed", ex);
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
