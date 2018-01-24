package com.github.cheapmon.apc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Run commands on Android device by connecting to the Android Debug Bridge.
 *
 * <ul>
 * <li>List devices</li>
 * <li>Connect to specific device</li>
 * <li>(Un)install APK from computer or via Google play</li>
 * <li>Upload and retrieve files from device</li>
 * <li>Run Android test</li>
 * <li></li>
 * </ul>
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class ADBConnector {

  /**
   * Create a process from system call.
   *
   * @param commands System commands to run
   */
  private static InputStream build(String... commands) {
    try {
      Process process = new ProcessBuilder(commands).start();
      InputStream output = process.getInputStream();
      InputStream error = process.getErrorStream();
      int exitCode = process.waitFor();
      if (exitCode == 0) {
        return output;
      } else {
        return error;
      }
    } catch (InterruptedException | IOException ex) {
      System.out.println(ex.getMessage());
      return new ByteArrayInputStream("".getBytes());
    }
  }

}
