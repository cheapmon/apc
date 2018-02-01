package com.github.cheapmon.apc.droid.util;

/**
 * Exception for Droid.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class DroidException extends Exception {

  /**
   * Create new exception. Provide additional message.
   *
   * @param message Message about failure
   */
  public DroidException(String message) {
    super(message);
  }

  /**
   * Create new exception. Provide cause of failure and additional message.
   *
   * @param message Message about failure
   * @param cause Cause of failure
   */
  public DroidException(String message, Throwable cause) {
    super(message, cause);
  }

}
