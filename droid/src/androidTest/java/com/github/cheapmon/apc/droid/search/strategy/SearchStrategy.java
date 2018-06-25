package com.github.cheapmon.apc.droid.search.strategy;

import com.github.cheapmon.apc.droid.extract.Page;
import com.github.cheapmon.apc.droid.util.DroidException;

public interface SearchStrategy {

  Page search(String id) throws DroidException;

}
