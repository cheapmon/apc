package com.github.cheapmon.apc.util;

import com.github.cheapmon.apc.APCOptions;
import com.github.cheapmon.apc.APCOptions.Algorithm;
import com.github.cheapmon.apc.APCOptions.ExtractionMode;
import com.github.cheapmon.apc.failure.APCException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
   * @throws APCException Finding device fails
   */
  public static APCOptions parse(String[] args) throws APCException {
    APCOptions options = new APCOptions();
    try {
      CommandLine cl = new DefaultParser().parse(getOptions(), args);
      if (cl.hasOption("help")) {
        printUsage();
      }
      Path file = getFile(cl.getOptionValues("id"), cl.getOptionValue("file"));
      ExtractionMode extractionMode = getMode(cl.hasOption("extract-model"));
      String device = getDevice(cl.getOptionValue("device"));
      Algorithm algorithm = getAlgorithm(cl.getOptionValue("search"));
      boolean rebuild = cl.hasOption("clean");
      options.setFile(file);
      options.setExtractionMode(extractionMode);
      options.setDevice(device);
      options.setAlgorithm(algorithm);
      options.setRebuild(rebuild);
      APCLogger.logo();
      APCLogger.space();
      APCLogger.info(CommandLineParser.class, String.format("* Found %s application ids",
          Files.lines(file).count()));
      APCLogger
          .info(CommandLineParser.class, String.format("* Extraction mode is %s", extractionMode));
      APCLogger.info(CommandLineParser.class, String.format("* Using device %s", device));
      APCLogger.info(CommandLineParser.class, String.format("* Using %s", algorithm));
      if (rebuild) {
        APCLogger.info(CommandLineParser.class, "* Clean and Rebuild");
      }
      APCLogger.space();
    } catch (ParseException ex) {
      printUsage(ex.getMessage());
    } catch (IOException ex) {
      throw new APCException("Reading file size failed", ex);
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
    options.addOption("f", "file", true, "File containing app ids");
    options.addOption("d", "device", true, "Device to run extraction on");
    options.addOption("s", "search", true, "Search algorithm");
    options.addOption("m", "extract-model", false, "Extract model of app");
    options.addOption("c", "clean", false, "Rebuild tests");
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
   * @throws APCException ID file corrupted
   */
  private static Path getFile(String[] ids, String file) throws APCException {
    if (file == null) {
      if (ids == null) {
        printUsage("Please supply at least one application id.");
        return null;
      }
    } else {
      try {
        ids = Files.readAllLines(Paths.get(file)).toArray(new String[0]);
      } catch (IOException ex) {
        throw new APCException("Reading id file failed", ex);
      }
    }
    try {
      ids = filterIDs(ids);
      Path newFile = Paths.get(".", "ids.txt");
      Files.write(newFile, Stream.of(ids).collect(Collectors.joining("\n")).getBytes());
      return newFile;
    } catch (IOException ex) {
      throw new APCException("Creating id file failed", ex);
    }
  }

  /**
   * Remove incorrect labels from list of applications.<br><br>
   *
   * Send GET request to Google Play and check response code.
   *
   * @param ids App ids
   * @return Filtered list
   */
  private static String[] filterIDs(String[] ids) {
    ids = Stream.of(ids).filter(id -> {
      try {
        String USER_AGENT = "Mozilla/5.0";
        String url = String.format("https://play.google.com/store/apps/details?id=%s", id);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        APCLogger.debug(CommandLineParser.class, url);
        APCLogger.debug(CommandLineParser.class, String.valueOf(con.getResponseCode()));
        return con.getResponseCode() == 200;
      } catch (IOException ex) {
        APCLogger.debug(CommandLineParser.class, ex.getMessage());
        return false;
      }
    }).toArray(String[]::new);
    if (ids.length == 0) {
      printUsage("Please supply at least one correct application id.");
    }
    return ids;
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
   * @throws APCException Communication with ADB fails
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
   * Input is an algorithm label. If none is given, defaults to optimized search. When the label is
   * incorrect, APC halts.
   *
   * @param algorithmString Algorithm label given by user
   * @return Algorithm label chosen by APC
   */
  private static Algorithm getAlgorithm(String algorithmString) {
    if (algorithmString == null) {
      return Algorithm.OS;
    } else {
      for (Algorithm algorithm : Algorithm.values()) {
        if (algorithmString.equalsIgnoreCase(algorithm.toString())) {
          return algorithm;
        }
      }
      System.out.println("Given algorithm label is incorrect. Please check for errors.");
      System.out.println("Available algorithms:");
      for (Algorithm algorithm : Algorithm.values()) {
        System.out.println(String.format("* %s", algorithm));
      }
      System.exit(0);
    }
    return Algorithm.OS;
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
