package com.github.cheapmon.apc.droid;

import android.support.test.runner.AndroidJUnit4;
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
   * Entry point for Droid pipeline. Check for arguments. Configure extraction.
   */
  @Test
  public void main() {
    DroidLogger.log("Droid");
  }

}
