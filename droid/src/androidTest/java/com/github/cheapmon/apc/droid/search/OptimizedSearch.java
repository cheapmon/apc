package com.github.cheapmon.apc.droid.search;

import com.github.cheapmon.apc.droid.extract.Page;
import com.github.cheapmon.apc.droid.search.strategy.NavMenuStrategy;
import com.github.cheapmon.apc.droid.search.strategy.RegistryPageStrategy;
import com.github.cheapmon.apc.droid.search.strategy.SearchStrategy;
import com.github.cheapmon.apc.droid.search.strategy.SmallMenuStrategy;
import com.github.cheapmon.apc.droid.search.strategy.TextViewStrategy;
import com.github.cheapmon.apc.droid.util.DroidException;
import com.github.cheapmon.apc.droid.util.DroidLogger;
import java.util.ArrayList;
import java.util.List;

/**
 * Search an app for a policy using optimized strategies.<br><br>
 *
 * Search is based on different strategies.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class OptimizedSearch implements SearchAlgorithm {

  /**
   * Different strategies for finding a policy.
   */
  private List<Class<? extends SearchStrategy>> strategies;

  /**
   * Run optimized search.
   *
   * @param id Application identification
   * @return Policy text
   * @throws DroidException Strategy instantiation fails
   */
  @Override
  public String run(String id) throws DroidException {
    DroidLogger.log(id);
    this.init();
    for (Class<? extends SearchStrategy> strategy : this.strategies) {
      try {
        DroidLogger.log(String.format("* %s", strategy.getSimpleName()));
        Page page = strategy.newInstance().search(id);
        if (page != null) {
          DroidLogger.log("-> Found text!");
          return page.dumpText();
        }
      } catch (InstantiationException | IllegalAccessException ex) {
        throw new DroidException("Search strategy could not be loaded", ex);
      }
    }
    // TODO: Add BFS as fallback.
    return null;
  }

  /**
   * Init strategies.
   */
  private void init() {
    this.strategies = new ArrayList<>();
    this.strategies.add(TextViewStrategy.class);
    this.strategies.add(RegistryPageStrategy.class);
    this.strategies.add(SmallMenuStrategy.class);
    this.strategies.add(NavMenuStrategy.class);
  }

}
