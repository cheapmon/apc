package com.github.cheapmon.apc.droid.extract;

import java.util.ArrayList;
import java.util.List;

/**
 * Model of an app.<br><br>
 *
 * This model is tree-like. Every node corresponds to an activity and its possible layouts and links
 * to other activities reachable by clicking. Every layout is described in its text contents, object
 * hierarchy etc.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class Model {

  /**
   * All nodes of this model
   */
  private List<ModelNode> nodes;

  /**
   * Instantiate new model from root activity.
   *
   * @param activityName Name of root activity
   */
  public Model(Page page, String activityName) {
    ModelNode rootNode = new ModelNode(page, activityName);
    nodes = new ArrayList<>();
    nodes.add(rootNode);
  }

  /**
   * App single page to model.
   *
   * @param page Page to add
   * @param activityName Activity the page belongs to
   */
  public void add(Page page, String activityName) {
    for (ModelNode node : nodes) {
      if (node.add(page, activityName)) {
        return;
      }
    }
    nodes.add(new ModelNode(page, activityName));
  }

}
