package com.github.cheapmon.apc.droid.util;

import android.support.test.uiautomator.BySelector;

/**
 * Select element by its properties and relative position.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class DroidSelector {

  /**
   * Selector of this element
   */
  private final BySelector s;

  /**
   * Relative position of this element in its parent container
   */
  private final int n;

  /**
   * Create new selector.
   *
   * @param s Elements selector
   * @param n Elements relative position
   */
  public DroidSelector(BySelector s, int n) {
    this.s = s;
    this.n = n;
  }

  /**
   * Get selector of element.
   *
   * @return Selector
   */
  public BySelector getS() {
    return this.s;
  }

  /**
   * Get relative position of element
   *
   * @return Position
   */
  public int getN() {
    return this.n;
  }

  /**
   * Put information about this selector.
   *
   * @return Information
   */
  @Override
  public String toString() {
    return String.format("%s, %s", this.s.toString(), this.n);
  }

}
