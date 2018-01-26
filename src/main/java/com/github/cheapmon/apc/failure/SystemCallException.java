package com.github.cheapmon.apc.failure;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Exception for failed system calls.<br><br>
 *
 * Called commands and error message of call are provided for additional information.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class SystemCallException extends APCException {

  /**
   * Create new exception. Provide commands and error message.
   *
   * @param commands Commands of system call
   * @param errorStream Error output of system call
   */
  public SystemCallException(String[] commands, InputStream errorStream) {
    super(String.format("System call '%s' failed", String.join(" ", commands)));
    String msg = new BufferedReader(new InputStreamReader(errorStream)).lines()
        .collect(Collectors.joining("\n"));
    System.err.println(String.format("\nSomething went wrong:\n%s\n", msg));
  }

}
