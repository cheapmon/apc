package com.github.cheapmon.apc.droid.search;

import com.github.cheapmon.apc.droid.extract.Page;
import java.util.List;

/**
 * Search an app for a policy applying depth first search.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com>cheapmon</a>
 */
public class DepthFirstSearch extends QueueBasedSearch {

  /**
   * Add new pages to front of queue.
   *
   * @param list New pages
   */
  @Override
  protected void addToQueue(List<Page> list) {
    for (int i = list.size() - 1; i >= 0; i--) {
      this.pages.addFirst(list.get(i));
    }
  }

}
