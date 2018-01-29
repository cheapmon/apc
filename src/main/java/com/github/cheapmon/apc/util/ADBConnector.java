package com.github.cheapmon.apc.util;

import com.github.cheapmon.apc.failure.APCException;
import com.github.cheapmon.apc.failure.SystemCallException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
   */
  public static void buildDroid(boolean rebuild) {
    if (rebuild || Files
        .notExists(Paths.get(".", "droid", "build", "outputs", "apk", "droid-debug.apk"))) {
      ProjectConnection connection = GradleConnector.newConnector()
          .forProjectDirectory(new File("./droid")).connect();
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
