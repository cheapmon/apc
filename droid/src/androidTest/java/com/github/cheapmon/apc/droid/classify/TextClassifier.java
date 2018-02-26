package com.github.cheapmon.apc.droid.classify;

/**
 * Determine whether a piece of text has a certain class or not.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public interface TextClassifier {

  /**
   * Whether this text has this class or not.
   *
   * @param text The text to test
   * @return Text has class
   */
  boolean test(String text);

}
