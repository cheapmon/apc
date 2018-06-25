package com.github.cheapmon.apc.droid.util;

import android.util.Log;
import com.github.cheapmon.apc.droid.DroidMain;

/**
 * Logger for Droid.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class DroidLogger {

  /**
   * Print break line to log.
   */
  public static void space() {
    Log.i(DroidMain.class.getSimpleName(), "----------------------------------------");
  }

  /**
   * Write log for given class.
   *
   * @param klass Given class
   * @param msg Message to write
   */
  public static void log(Class<?> klass, String msg) {
    Log.i(klass.getSimpleName(), msg);
  }

  /**
   * Write log message.
   *
   * @param msg Message to write
   */
  public static void log(String msg) {
    Log.i(DroidMain.class.getSimpleName(), msg);
  }

  /**
   * Write object to log.
   *
   * @param object Object to write
   */
  public static void log(Object object) {
    Log.i(DroidMain.class.getSimpleName(), object.toString());
  }

}
