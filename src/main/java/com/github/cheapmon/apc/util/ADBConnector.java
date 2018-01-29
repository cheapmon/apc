package com.github.cheapmon.apc.util;

import com.github.cheapmon.apc.failure.APCException;
import com.github.cheapmon.apc.failure.SystemCallException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

/**
 * Run commands on Android device by connecting to the Android Debug Bridge.<br><br>
 *
 * <ul>
 * <li>List devices</li>
 * <li>Connect to specific device</li>
 * <li>(Un)install APK from computer or via Google play</li>
 * <li>Upload and retrieve files from device</li>
 * <li>Run Android test</li>
 * </ul>
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class ADBConnector {

  /**
   * Path to Android submodule
   */
  private static final Path DROID = Paths.get(".", "droid");

  /**
   * Path to debug APK built by Android submodule
   */
  private static final Path DEBUG_APK = Paths
      .get(".", "droid", "build", "outputs", "apk", "droid-debug.apk");

  /**
   * Path to test APK built by Android submodule
   */
  private static final Path TEST_APK = Paths
      .get(".", "droid", "build", "outputs", "apk", "droid-debug-androidTest.apk");

  /**
   * Path to debug APK destination on remote device
   */
  private static final String DEBUG_DEST = "/data/local/tmp/com.github.cheapmon.apc.droid";

  /**
   * Path to test APK destination on remote device
   */
  private static final String TEST_DEST = "/data/local/tmp/com.github.cheapmon.apc.droid.test";

  /**
   * Label of device for this connection
   */
  private final String device;

  /**
   * Connect to remote device.
   *
   * @param device Device label
   */
  public ADBConnector(String device) {
    this.device = device;
  }

  /**
   * List all available Android (virtual) devices attached to the computer.<br><br>
   *
   * Note that this will only list devices that have Android debugging enabled.
   *
   * @return Device list
   * @throws APCException System call fails
   */
  public static String[] deviceList() throws APCException {
    return new BufferedReader(new InputStreamReader(build("adb", "devices"))).lines().skip(1)
        .filter(line -> line.contains("device")).map(line -> line.split("\\s+")[0])
        .toArray(String[]::new);
  }

  /**
   * Build debugging and testing binary in Android submodule.
   *
   * @param rebuild Whether to rebuild test files
   */
  public static void buildDroid(boolean rebuild) {
    if (rebuild || Files.notExists(DEBUG_APK)) {
      ProjectConnection connection = GradleConnector.newConnector()
          .forProjectDirectory(new File(DROID.toAbsolutePath().toString())).connect();
      try {
        connection.newBuild().forTasks("assembleDebug", "assembleAndroidTest").run();
      } finally {
        connection.close();
      }
      APCLogger.info(ADBConnector.class, "Successfully built APK");
      APCLogger.space();
    } else {
      APCLogger.info(ADBConnector.class, "APK already built, skipping");
      APCLogger.space();
    }
  }

  /**
   * Install required APC test files on device.
   *
   * @throws APCException Installation fails
   */
  public void install() throws APCException {
    File debugAPK = new File(DEBUG_APK.toAbsolutePath().toString());
    File testAPK = new File(TEST_APK.toAbsolutePath().toString());
    buildADB("push", debugAPK.getAbsolutePath(), DEBUG_DEST);
    buildADB("shell", "pm", "install", "-t", "-r", DEBUG_DEST);
    buildADB("push", testAPK.getAbsolutePath(), TEST_DEST);
    buildADB("shell", "pm", "install", "-t", "-r", TEST_DEST);
    APCLogger.info(ADBConnector.class, String.format("Installed APK on device %s", this.device));
    APCLogger.space();
  }

  /**
   * Remove APC files from device.
   *
   * @throws APCException Removing fails
   */
  public void remove() throws APCException {
    buildADB("shell", "pm", "uninstall", "com.github.cheapmon.apc.droid");
    buildADB("shell", "pm", "uninstall", "com.github.cheapmon.apc.droid.test");
    APCLogger.info(ADBConnector.class, "Removed all APC files from device");
    APCLogger.space();
  }

  /**
   * Run ADB command on device.
   *
   * @param commands Android Debug Bridge commands to run
   * @return Output or error of finished process
   * @throws APCException Building process fails
   */
  private InputStream buildADB(String... commands) throws APCException {
    return build(Stream
        .concat(Arrays.stream(new String[]{"adb", "-s", this.device}), Arrays.stream(commands))
        .toArray(String[]::new));
  }

  /**
   * Create a process from system call.
   *
   * @param commands System commands to run
   * @return Output or error of finished process
   * @throws APCException Building process fails
   */
  private static InputStream build(String... commands) throws APCException {
    try {
      Process process = new ProcessBuilder(commands).start();
      InputStream output = process.getInputStream();
      InputStream error = process.getErrorStream();
      int exitCode = process.waitFor();
      if (exitCode == 0) {
        return output;
      } else {
        throw new SystemCallException(commands, error);
      }
    } catch (InterruptedException | IOException ex) {
      throw new APCException("Building process for system call failed", ex);
    }
  }

}
