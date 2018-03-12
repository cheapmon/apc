package com.github.cheapmon.apc.droid.extract;

import android.graphics.Rect;
import android.support.test.uiautomator.UiObject2;
import java.util.List;

/**
 * Represent a piece of layout data.<br><br>
 *
 * Internal representation of an apps single layout. Every static view is captured. Additionally,
 * all views inside an scrolling container are (ideally) collected into one large container.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class Page {

  /**
   * Path to this page. List of coordinates of view clicked to reach this page.
   */
  private List<Rect> path;

  /**
   * Root View of this page.
   */
  private final View rootView;

  /**
   * Create new Page from UiAutomator representation.
   *
   * @param obj UiAutomator object of root view
   */
  public Page(UiObject2 obj) {
    rootView = new View(obj);
  }

  /**
   * Merge another page into this page.
   *
   * @param otherPage Page to merge from
   */
  public void merge(Page otherPage) {
    rootView.merge(otherPage.rootView);
  }

  /**
   * Dump full text found on layout.
   *
   * @return Plain text of layout
   */
  public String dumpText() {
    return rootView.dumpText();
  }

  /**
   * Check this page for equality with another page.
   *
   * @param obj Page to check equality with
   * @return Whether the page is equal
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Page)) {
      return false;
    }
    View otherRootView = ((Page) obj).rootView;
    return otherRootView.equals(this.rootView);
  }

  /**
   * Check this page for equivalency with another page.
   *
   * @param obj Page to check
   * @return Whether the pages are equivalent
   */
  public boolean isEquivalent(Object obj) {
    if (!(obj instanceof Page)) {
      return false;
    }
    View otherRootView = ((Page) obj).rootView;
    return otherRootView.isEquivalent(this.rootView);
  }

}
