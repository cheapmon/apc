package com.github.cheapmon.apc.util;

import com.github.cheapmon.apc.failure.APCException;
import com.github.cheapmon.apc.failure.SystemCallException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
   */
  public static String[] deviceList() throws APCException {
    return new BufferedReader(new InputStreamReader(build("adb", "devices"))).lines().skip(1)
        .filter(line -> line.contains("device")).map(line -> line.split("\\s+")[0])
        .toArray(String[]::new);
  }

  /**
   * Create a process from system call.
   *
   * @param commands System commands to run
   * @return Output or error of finished process
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
