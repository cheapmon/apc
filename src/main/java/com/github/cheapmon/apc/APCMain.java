package com.github.cheapmon.apc;

import com.github.cheapmon.apc.failure.APCException;
import com.github.cheapmon.apc.util.ADBConnector;
import com.github.cheapmon.apc.util.CommandLineParser;

/**
 * APC extracts privacy policies from Android applications.<br><br>
 *
 * Main pipeline: <ul>
 * <li>Parse command line options</li>
 * <li>Generate APK with Android test and load onto device</li>
 * <li>Test classes extract policies or a model of the application</li>
 * <li>Retrieve and store extracted information</li>
 * </ul><br><br>
 *
 * This project consists of a main module and an Android submodule. The main module handles basic
 * setup and communication with an Android device, while the Android submodule extracts textual
 * information via user-selected methods.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class APCMain {

  /**
   * Implementation of main pipeline (see above).
   *
   * @param args Command line arguments
   * @throws APCException APC fails
   */
  public static void main(String[] args) throws APCException {
    APCOptions options = CommandLineParser.parse(args);
    ADBConnector.buildDroid(options.getRebuild());
    ADBConnector connector = new ADBConnector(options.getDevice());
    connector.install();
    connector.runTests(options);
    connector.remove();
  }

}
