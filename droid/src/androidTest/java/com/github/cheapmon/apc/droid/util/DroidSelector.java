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
  private final BySelector selector;

  /**
   * Relative position of this element in its parent container
   */
  private final int pos;

  /**
   * Offset of this element in its ancestors<br><br>
   *
   * If this element is positioned in a scroll container and only visible upon the nth scroll, its
   * offset is n.
   */
  private final int offset;

  /**
   * Create new selector.
   *
   * @param selector Elements selector
   * @param pos Elements relative position
   */
  public DroidSelector(BySelector selector, int pos, int offset) {
    this.selector = selector;
    this.pos = pos;
    this.offset = offset;
  }

  /**
   * Get selector of element.
   *
   * @return Selector
   */
  public BySelector getSelector() {
    return this.selector;
  }

  /**
   * Get relative position of element
   *
   * @return Position
   */
  public int getPos() {
    return this.pos;
  }

  /**
   * Get offset of element.
   *
   * @return Offset
   */
  public int getOffset() {
    return this.offset;
  }

  /**
   * Put information about this selector.
   *
   * @return Information
   */
  @Override
  public String toString() {
    return String.format("%s, %s, %s", this.selector.toString(), this.pos, this.offset);
  }

}
