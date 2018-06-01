package com.github.cheapmon.apc.droid.extract;

import android.support.test.uiautomator.UiObject2;
import com.github.cheapmon.apc.droid.util.DroidSelector;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
   * Path to this page. List of elements clicked to reach this page.
   */
  private List<List<DroidSelector>> path;

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
    this.rootView = new View(obj);
    this.path = new ArrayList<>();
  }

  /**
   * Merge another page into this page.
   *
   * @param otherPage Page to merge from
   */
  public void merge(Page otherPage) {
    this.rootView.merge(otherPage.rootView);
  }

  /**
   * Add clicked elements to path.
   */
  public void addToPath(Page page, List<DroidSelector> selector) {
    this.path = new ArrayList<>(page.getPath());
    this.path.add(selector);
  }

  /**
   * Get path to this page.
   *
   * @return Path
   */
  public List<List<DroidSelector>> getPath() {
    return this.path;
  }

  /**
   * Dump full text found on layout.
   *
   * @return Plain text of layout
   */
  public String dumpText() {
    return this.rootView.dumpText();
  }

  /**
   * Convert to DOM element.
   *
   * @param document Document element is saved in
   * @return Resulting element
   */
  public Element toElement(Document document) {
    Element page = document.createElement("page");
    Element path = document.createElement("path");
    for (List<DroidSelector> list : this.path) {
      path.appendChild(this.fromSelector(document, list.get(list.size() - 1)));
    }
    page.appendChild(path);
    page.appendChild(this.rootView.toElement(document));
    return page;
  }

  /**
   * Create XML node from selector variables and string representation.
   *
   * @param selector Selector of element
   * @return XML node representation of element
   */
  private Element fromSelector(Document document, DroidSelector selector) {
    Element step = document.createElement("step");
    String[] l = selector.getSelector().toString().split("'");
    step.setAttribute("class", l[1].substring(2, l[1].length() - 2));
    step.setAttribute("pkg", l[3].substring(2, l[3].length() - 2));
    step.setAttribute("clickable", l[5]);
    step.setAttribute("scrollable", l[7]);
    step.setAttribute("n", String.valueOf(selector.getPos()));
    step.setAttribute("offset", String.valueOf(selector.getOffset()));
    step.setAttribute("bounds", selector.getBounds().toShortString());
    step.setAttribute("text", selector.getText());
    return step;
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
