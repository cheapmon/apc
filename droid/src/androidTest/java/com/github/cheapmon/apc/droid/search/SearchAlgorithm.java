package com.github.cheapmon.apc.droid.search;

import com.github.cheapmon.apc.droid.util.DroidException;

/**
 * Search an app for a policy text.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com>cheapmon</a>
 */
public interface SearchAlgorithm {

  /**
   * Search policy.
   *
   * @param id Application identification
   * @return Policy text
   * @throws DroidException Search algorithm failed
   */
  String run(String id) throws DroidException;

}
