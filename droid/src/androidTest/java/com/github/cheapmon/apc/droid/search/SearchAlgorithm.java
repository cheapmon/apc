package com.github.cheapmon.apc.droid.search;

import com.github.cheapmon.apc.droid.util.DroidException;

public interface SearchAlgorithm {

  String run(String id) throws DroidException;

}
