package com.github.cheapmon.apc.droid.search;

import java.util.HashMap;

/**
 * Utility for search algorithms.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class AlgorithmHelper {

  /**
   * Get algorithm class from given label.
   *
   * @param algorithm Algorithm label
   * @return Algorithm class
   */
  public static Class<? extends SearchingAlgorithm> get(String algorithm) {
    HashMap<String, Class<? extends SearchingAlgorithm>> algorithms = new HashMap<>();
    algorithms.put("BFS", BreadthFirstSearch.class);
    algorithms.put("DFS", DepthFirstSearch.class);
    algorithms.put("OS", OptimizedSearch.class);
    algorithms.put("RS", RandomizedSearch.class);
    return algorithms.get(algorithm);
  }

}
