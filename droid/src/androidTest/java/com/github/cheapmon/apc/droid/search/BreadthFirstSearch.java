package com.github.cheapmon.apc.droid.search;

import com.github.cheapmon.apc.droid.extract.Page;
import java.util.List;

/**
 * Search an app by applying breadth first search.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com>cheapmon</a>
 */
public class BreadthFirstSearch extends QueueBasedSearch {

  /**
   * Add new pages to end of queue.
   *
   * @param list New pages
   */
  @Override
  protected void addToQueue(List<Page> list) {
    this.pages.addAll(list);
  }

}
