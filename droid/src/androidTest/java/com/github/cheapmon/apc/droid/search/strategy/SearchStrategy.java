package com.github.cheapmon.apc.droid.search.strategy;

import com.github.cheapmon.apc.droid.extract.Page;
import com.github.cheapmon.apc.droid.util.DroidException;

/**
 * Strategy for finding a policy in an app.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com>cheapmon</a>
 */
public interface SearchStrategy {

  /**
   * Search policy.
   *
   * @param id Application identification
   * @return Policy text
   * @throws DroidException Search fails
   */
  Page search(String id) throws DroidException;

}
