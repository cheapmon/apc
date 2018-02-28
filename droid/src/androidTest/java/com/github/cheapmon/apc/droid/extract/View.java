package com.github.cheapmon.apc.droid.extract;

import android.support.test.espresso.core.deps.guava.base.Optional;
import android.support.test.uiautomator.UiObject2;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Represent a single view on the page.
 */
public class View {

  /**
   * Class of this View
   */
  private final String className;

  /**
   * Package of this view
   */
  private final String packageName;

  /**
   * Whether this view can be clicked
   */
  private final boolean clickable;

  /**
   * Whether this view can be scrolled
   */
  private final boolean scrollable;

  /**
   * Collection of possible text contents of this view
   */
  private HashSet<String> text;

  /**
   * Child views of this view
   */
  private List<View> children;

  /**
   * Instantiate new View from UiAutomator representation.
   */
  public View(UiObject2 object) {
    this.className = Optional.fromNullable(object.getClassName()).or("");
    this.packageName = Optional.fromNullable(object.getApplicationPackage()).or("");
    this.text = new HashSet<>();
    this.text.add(Optional.fromNullable(object.getText()).or(""));
    this.clickable = object.isClickable();
    this.scrollable = object.isScrollable();
    this.children = new ArrayList<>();
    for (UiObject2 child : object.getChildren()) {
      this.children.add(new View(child));
    }
  }

  /**
   * Determine whether this View is the same as another.<br><br>
   *
   * Two Views are equal when their properties and children are equal.
   *
   * @param obj View to check for equality
   * @return View is the same
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof View)) {
      return false;
    }
    View otherView = (View) obj;
    if (this.children.size() != otherView.children.size()) {
      return false;
    }
    for (int i = 0; i < this.children.size(); i++) {
      if (this.children.get(i).equals(otherView.children.get(i))) {
        continue;
      }
      return false;
    }
    return this.className.contentEquals(otherView.className) &&
        this.packageName.contentEquals(otherView.packageName) &&
        this.text.containsAll(otherView.text) &&
        this.clickable == (otherView.clickable) &&
        this.scrollable == (otherView.scrollable);
  }

  /**
   * Determine whether this view is equivalent to another.<br><br>
   *
   * Two views are equivalent when their properties and children match except for contents of scroll
   * containers.
   *
   * @param obj View to check for equivalency
   * @return View is equivalent
   */
  public boolean isEquivalent(Object obj) {
    if (!(obj instanceof View)) {
      return false;
    }
    View otherView = (View) obj;
    boolean content = this.className.contentEquals(otherView.className) &&
        this.packageName.contentEquals(otherView.packageName) &&
        this.clickable == otherView.clickable &&
        this.scrollable == otherView.scrollable;
    if (this.scrollable && otherView.scrollable) {
      return content;
    } else {
      if (this.children.size() != otherView.children.size()) {
        return false;
      }
      for (int i = 0; i < this.children.size(); i++) {
        if (this.children.get(i).isEquivalent(otherView.children.get(i))) {
          continue;
        }
        return false;
      }
      return content;
    }
  }

}
