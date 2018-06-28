package com.github.cheapmon.apc.droid.extract;

import android.support.test.espresso.core.deps.guava.base.Optional;
import android.support.test.uiautomator.UiObject2;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represent a single view on the page.
 */
class View {

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
  private final HashSet<String> text;

  /**
   * Child views of this view
   */
  private final List<View> children;

  /**
   * Instantiate new View from UiAutomator representation.
   */
  View(UiObject2 object) {
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
   * Merge another view into this view.
   *
   * @param otherView View to merge from
   */
  void merge(View otherView) {
    this.text.addAll(otherView.text);
    if (this.scrollable) {
      View container = this.children.get(0);
      View otherContainer = otherView.children.get(0);
      container.text.addAll(otherContainer.text);
      for (View child : otherContainer.children) {
        if (!container.children.contains(child)) {
          container.children.add(child);
        }
      }
    } else {
      for (int i = 0; i < this.children.size(); i++) {
        this.children.get(i).merge(otherView.children.get(i));
      }
    }
  }

  /**
   * Dump text of this View and its children.
   *
   * @return Resulting text
   */
  String dumpText() {
    return this.dump().toString().trim();
  }

  /**
   * Helper method for text dump.
   *
   * @return StringBuilder containing text
   */
  private StringBuilder dump() {
    StringBuilder builder = new StringBuilder();
    for (String string : this.text) {
      if (!string.equals("")) {
        builder.append(string).append(" ");
      }
    }
    if (this.children.size() <= 0 && builder.length() > 0) {
      builder.append("\n");
    } else {
      for (View childView : this.children) {
        builder.append(childView.dump());
      }
    }
    return builder;
  }

  /**
   * Convert to DOM element.
   *
   * @param document Document element is saved in
   * @return Resulting element
   */
  Element toElement(Document document) {
    Element view = document.createElement("view");
    view.setAttribute("class", this.className);
    view.setAttribute("package", this.packageName);
    view.setAttribute("clickable", String.valueOf(this.clickable));
    view.setAttribute("scrollable", String.valueOf(this.scrollable));
    view.setAttribute("text", this.text.stream().collect(Collectors.joining(", ")));
    for (View childView : this.children) {
      view.appendChild(childView.toElement(document));
    }
    return view;
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
  boolean isEquivalent(Object obj) {
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
