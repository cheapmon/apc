package com.github.cheapmon.apc.droid.util;

import android.support.test.uiautomator.StaleObjectException;
import com.github.cheapmon.apc.droid.GooglePlayWizard;
import com.github.cheapmon.apc.droid.GooglePlayWizard.InstallState;

/**
 * Simple test to check endurance of Google Play installation and removal.<br><br>
 *
 * Every app is installed and then removed, counting attempts and failures. Also mentions total
 * time consumption.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class StressTest {

  /**
   * Run test.
   *
   * @param ids Application ids
   * @return Evaluation information
   */
  public static String run(String[] ids) {
    double startTime = System.nanoTime();
    int size = ids.length;
    int attempts = 0;
    int totalAttempts = 0;
    int longestAttempt = 0;
    int failures = 0;
    int notAvailable = 0;
    StringBuilder builder = new StringBuilder("StressTest").append("\n");
    builder.append("------------------------------------------").append("\n");
    for (int i = 0; i < ids.length; i++) {
      attempts++;
      totalAttempts++;
      try {
        String id = ids[i];
        InstallState install = GooglePlayWizard.install(id);
        if (install == InstallState.FAILURE) {
          notAvailable++;
          attempts = 0;
          builder.append(String.format("install %s", install)).append("\n");
          builder.append("------------------------------------------").append("\n");
          continue;
        }
        InstallState remove = GooglePlayWizard.remove(id);
        if (attempts > longestAttempt) {
          longestAttempt = attempts;
        }
        attempts = 0;
        builder.append(String.format("[%s/%s] %s (Attempt #%s)", i + 1, size, id, attempts));
        builder.append("\n");
        builder.append(String.format("install %s", install)).append("\n");
        builder.append(String.format("remove %s", remove)).append("\n");
        builder.append("------------------------------------------").append("\n");
      } catch (DroidException | NullPointerException | StaleObjectException ex) {
        failures++;
        i--;
      }
    }
    builder.append(String.format("Tested %s ids, totaling %s attempts, the longest being %s tries.",
        size, totalAttempts, longestAttempt)).append("\n");
    builder.append(String.format("%s could not be installed, leaving %s to work with.",
        notAvailable, size - notAvailable)).append("\n");
    builder.append(String.format("There were a total of %s failures.", failures)).append("\n");
    builder.append(String
        .format("Finished in %s minutes.", (System.nanoTime() - startTime) / 60_000_000_000.0))
        .append("\n");
    return builder.toString();
  }

}
