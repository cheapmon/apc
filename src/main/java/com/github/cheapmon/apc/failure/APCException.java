package com.github.cheapmon.apc.failure;

/**
 * Exception for APC.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class APCException extends Exception {

  /**
   * Create new exception. Provide additional message.
   *
   * @param message Message about failure
   */
  public APCException(String message) {
    super(message);
  }

  /**
   * Create new exception. Provide cause of failure and additional message.
   *
   * @param message Message about failure
   * @param cause Cause of failure
   */
  public APCException(String message, Throwable cause) {
    super(message, cause);
  }

}
