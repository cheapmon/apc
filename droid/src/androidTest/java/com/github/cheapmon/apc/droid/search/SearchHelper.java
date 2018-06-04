package com.github.cheapmon.apc.droid.search;

import com.github.cheapmon.apc.droid.util.DroidLogger;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility for search algorithms.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class SearchHelper {

  private static final int WORD_COUNT = 2000;

  private static final int MATCH_COUNT = 80;

  public static final String[] PRIVACY_POLICY_KEYWORDS = {
      "privacy", "policy", "policies", "data", "term", "condition", "use", "tos", "tou", "pp",
      "collect", "eula", "legal", "personal", "save", "store", "daten", "schutz", "erklärung",
      "agb", "dse", "allgemeine", "geschäft", "bedingung", "richt", "linie", "information",
      "erheben", "sammeln", "verarbeiten", "erhoben", "speichern", "erfassen", "persönlich"
  };

  /**
   * Get algorithm class from given label.
   *
   * @param algorithm Algorithm label
   * @return Algorithm class
   */
  public static Class<? extends SearchAlgorithm> get(String algorithm) {
    HashMap<String, Class<? extends SearchAlgorithm>> algorithms = new HashMap<>();
    algorithms.put("BFS", BreadthFirstSearch.class);
    algorithms.put("DFS", DepthFirstSearch.class);
    algorithms.put("OS", OptimizedSearch.class);
    algorithms.put("RS", RandomizedSearch.class);
    return algorithms.get(algorithm);
  }

  public static boolean isPolicy(String text) {
    int matches = 0;
    if (StringUtils.split(text, " ").length > WORD_COUNT) {
      for (String keyword : PRIVACY_POLICY_KEYWORDS) {
        matches += StringUtils.countMatches(text, keyword);
        if (matches >= MATCH_COUNT) {
          DroidLogger.log(String.format("Matches: %d", matches));
          return true;
        }
      }
    }
    DroidLogger.log(String.format("Matches: %d", matches));
    return false;
  }
}
