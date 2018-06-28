package com.github.cheapmon.apc.droid.util;

import android.util.Log;
import com.github.cheapmon.apc.droid.DroidMain;
import com.github.cheapmon.apc.droid.install.GooglePlayWizard;
import com.github.cheapmon.apc.droid.install.GooglePlayWizard.InstallState;

/**
 * Simple test to check endurance of Google Play installation and removal.<br><br>
 *
 * Every app is installed and then removed, counting attempts and failures. Also mentions total time
 * consumption.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
class StressTest {

  /**
   * Run test.
   *
   * @param ids Application ids
   */
  public static void run(String[] ids) {
    double startTime = System.nanoTime();
    int size = ids.length;
    int attempts = 0;
    int totalAttempts = 0;
    int longestAttempt = 0;
    int failures = 0;
    int notAvailable = 0;
    write("StressTest\n");
    write("------------------------------------------\n");
    for (int i = 0; i < ids.length; i++) {
      attempts++;
      totalAttempts++;
      try {
        String id = ids[i];
        InstallState install = GooglePlayWizard.install(id);
        if (install == InstallState.FAILURE) {
          notAvailable++;
          write("[%s/%s] %s (Attempt #%s)\n", i + 1, size, id, attempts);
          write("install %s\n", install);
          write("------------------------------------------\n");
          attempts = 0;
          continue;
        }
        GooglePlayWizard.removeSilently(id);
        if (attempts > longestAttempt) {
          longestAttempt = attempts;
        }
        write("[%s/%s] %s (Attempt #%s)\n", i + 1, size, id, attempts);
        write("install %s\n", install);
        write("remove %s\n", InstallState.SUCCESS);
        write("------------------------------------------\n");
        attempts = 0;
      } catch (Exception ex) {
        Log.w(DroidMain.class.getSimpleName(), ex);
        failures++;
        i--;
      }
    }
    write("Tested %s ids, totaling %s attempts, the longest being %s tries.\n", size, totalAttempts,
        longestAttempt);
    write("%s could not be installed, leaving %s to work with.\n", notAvailable,
        size - notAvailable);
    write("There were a total of %s failures.\n", failures);
    write("Finished in %s minutes.\n", (System.nanoTime() - startTime) / 60_000_000_000.0);
    write("------------------------------------------\n");
  }

  private static void write(String msg, Object... args) {
    msg = String.format(msg, args);
    DroidLogger.log(msg);
  }

}
