package com.github.cheapmon.apc.droid.util;

import android.graphics.Rect;
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
   * Bounds of this element (meta)
   */
  private Rect boundsExtra;

  /**
   * Text of this element (meta)
   */
  private String textExtra;

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
   * Add meta information of this element.<br><br>
   *
   * This information is added to the model, but isn't relevant for finding this element.
   *
   * @param bounds Bounds of Element
   * @param text Text of Element
   */
  public DroidSelector setMeta(Rect bounds, String text) {
    this.boundsExtra = bounds;
    this.textExtra = text;
    return this;
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
   * Get bounds of element.
   *
   * @return Bounds
   */
  public Rect getBounds() {
    return this.boundsExtra;
  }

  /**
   * Get text of element.
   *
   * @return Text
   */
  public String getText() {
    return (this.textExtra == null) ? "" : this.textExtra;
  }

  /**
   * Put information about this selector.
   *
   * @return Information
   */
  @Override
  public String toString() {
    return String.format("%s, %s, %s, %s, %s", this.selector.toString(), this.pos, this.offset,
        this.boundsExtra.toShortString(), this.textExtra);
  }

}
