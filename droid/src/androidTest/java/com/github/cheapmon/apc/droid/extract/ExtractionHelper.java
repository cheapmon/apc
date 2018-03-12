package com.github.cheapmon.apc.droid.extract;

import android.support.test.uiautomator.UiDevice;
import com.github.cheapmon.apc.droid.util.DroidException;
import com.github.cheapmon.apc.droid.util.DroidLogger;
import java.io.IOException;

/**
 * Provide utility for model extraction.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class ExtractionHelper {

  /**
   * Get current activity on running device.
   *
   * @param device Device to search
   * @return Activity name
   */
  public static String getActivityName(UiDevice device) throws DroidException {
    try {
      String out = device.executeShellCommand("dumpsys window windows");
      for (String line : out.split("\n")) {
        if (line.contains("mFocusedApp=")) {
          for (String part : line.split(" ")) {
            if (part.matches("[A-z.]*/[A-z.]*")) {
              return part;
            }
          }
        }
      }
    } catch (IOException ex) {
      throw new DroidException("Shell command failed", ex);
    }
    throw new DroidException("Could not find current activity");
  }

}
