package com.github.cheapmon.apc.droid.search;

import java.util.HashMap;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility for search algorithms.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class SearchHelper {

  /**
   * Keywords to match a privacy policy
   */
  private static final String[] PRIVACY_POLICY_KEYWORDS = {
      "privacy", "policy", "policies", "data", "term", "condition", "use", "tos", "tou", "pp",
      "collect", "eula", "legal", "personal", "save", "store", "daten", "schutz", "erklärung",
      "agb", "dse", "allgemeine", "geschäft", "bedingung", "richt", "linie", "information",
      "erheben", "sammeln", "verarbeiten", "erhoben", "speichern", "erfassen", "persönlich"
  };

  /**
   * Keywords to look for in the UI
   */
  private static final String[] NAVIGATION_KEYWORDS = {
      "daten", "data", "privat", "privacy", "hilfe", "help", "support", "info", "einstellung",
      "setting", "über", "about", "impressum", "service", "nutzung", "terms", "bedingung",
      "rechtlich", "legal"
  };

  /**
   * Navigation keywords compiled into regex pattern
   */
  public static final Pattern NAVIGATION_REGEX = getNavRegex();

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

  /**
   * Compile keywords to regex.
   *
   * @return Regex pattern
   */
  private static Pattern getNavRegex() {
    StringBuilder result = new StringBuilder(".*(");
    for (int i = 0; i < NAVIGATION_KEYWORDS.length; i++) {
      String keyword = NAVIGATION_KEYWORDS[i];
      String firstLetter = keyword.substring(0, 1);
      String rest = keyword.substring(1);
      result.append(String.format("[%s%s]%s", firstLetter.toUpperCase(), firstLetter, rest));
      if (i < NAVIGATION_KEYWORDS.length - 1) {
        result.append("|");
      }
    }
    result.append(").*");
    return Pattern.compile(result.toString());
  }

  /**
   * Check if a given text is a policy.<br><br>
   *
   * Policy check is entirely keyword-based. Substituting this for a classification might be worth
   * looking into. Constants can be adjusted.
   *
   * @param text Text to check
   * @return Whether the text is a privacy policy
   */
  public static boolean isPolicy(String text) {
    final double WORD_THRESHOLD = 500;
    final double RATIO_THRESHOLD = 0.04;
    double words = StringUtils.split(text, " ").length;
    if (words > WORD_THRESHOLD) {
      double matches = 0;
      for (String keyword : PRIVACY_POLICY_KEYWORDS) {
        matches += StringUtils.countMatches(text.toLowerCase(), keyword);
        if (matches / words >= RATIO_THRESHOLD) {
          return true;
        }
      }
    }
    return false;
  }
}
