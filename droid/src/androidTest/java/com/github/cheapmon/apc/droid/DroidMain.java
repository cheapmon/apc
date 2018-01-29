package com.github.cheapmon.apc.droid;

import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.github.cheapmon.apc.droid.search.AlgorithmHelper;
import com.github.cheapmon.apc.droid.search.SearchingAlgorithm;
import com.github.cheapmon.apc.droid.util.DroidLogger;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Run extraction on this device.<br><br>
 *
 * <ul>
 * <li>Install or remove Android app</li>
 * <li>Crawl interface of an app</li>
 * </ul>
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
@RunWith(AndroidJUnit4.class)
public class DroidMain {

  /**
   * Labels of Android apps
   */
  private String[] ids;

  /**
   * Extraction mode to use
   */
  private String mode;

  /**
   * Name of search algorithm to use in extraction
   */
  private String algorithm;

  /**
   * Entry point for Droid pipeline. Check for arguments. Configure extraction.
   */
  @Test
  public void main() throws IllegalAccessException, InstantiationException {
    parseCommands();
    SearchingAlgorithm algorithm = AlgorithmHelper.get(this.algorithm).newInstance();
  }

  /**
   * Parse arguments given to Droid.
   */
  private void parseCommands() {
    Bundle extras = InstrumentationRegistry.getArguments();
    this.ids = extras.getString("ids", "").split(",");
    this.mode = extras.getString("mode");
    this.algorithm = extras.getString("algorithm");
    DroidLogger.log("Droid");
    DroidLogger.space();
    DroidLogger.log("Found ids:");
    for (String id : this.ids) {
      DroidLogger.log(String.format("* %s", id));
    }
    DroidLogger.log(String.format("Extraction mode is %s", this.mode));
    DroidLogger.log(String.format("Using %s", this.algorithm));
    DroidLogger.space();
  }

}
