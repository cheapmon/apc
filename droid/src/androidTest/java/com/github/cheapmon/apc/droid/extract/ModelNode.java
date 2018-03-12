package com.github.cheapmon.apc.droid.extract;

import java.util.ArrayList;
import java.util.List;

/**
 * Collect all pages in a certain activity.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com>cheapmon</a>
 */
class ModelNode {

  /**
   * Activity this node belongs to
   */
  private final String activityName;

  /**
   * Pages of this node
   */
  private List<Page> pages;

  /**
   * Create new node.
   *
   * @param activityName Activity for new node
   */
  public ModelNode(Page page, String activityName) {
    this.activityName = activityName;
    this.pages = new ArrayList<>();
    this.pages.add(page);
  }

  /**
   * Add single page to this node or its children.
   *
   * @param page Page to add
   * @param activityName Activity the page belongs to
   */
  public boolean add(Page page, String activityName) {
    if (this.activityName.equals(activityName)) {
      for (Page p : pages) {
        if (page.equals(p)) {
          return true;
        }
        if (page.isEquivalent(p)) {
          p.merge(page);
          return true;
        }
      }
      this.pages.add(page);
      return true;
    }
    return false;
  }

}
