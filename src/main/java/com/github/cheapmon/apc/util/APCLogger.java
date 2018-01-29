package com.github.cheapmon.apc.util;

import com.github.cheapmon.apc.APCMain;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Logger for APC.<br><br>
 *
 * This is used to avoid instancing a logger in every class.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class APCLogger {

  /**
   * List of loggers for different classes.
   */
  private static List<Logger> loggerList = new ArrayList<Logger>();

  /**
   * Print break line to log.
   */
  public static void space() {
    info(APCMain.class, "----------------------------------------");
  }

  /**
   * Write log for a given class, on the level INFO.
   *
   * @param klass Given class
   * @param msg Message to log
   */
  public static void info(Class<?> klass, String msg) {
    log(klass, msg, Level.INFO);
  }

  /**
   * Write log for a given class, on the level DEBUG.
   *
   * @param klass Given class
   * @param msg Message to log
   */
  public static void debug(Class<?> klass, String msg) {
    log(klass, msg, Level.DEBUG);
  }

  /**
   * Write log for a given class, on a given level.<br><br>
   *
   * Keeps track of already instantiated loggers.
   *
   * @param klass Given class
   * @param msg Message to log
   * @param level Given level
   */
  private static void log(Class<?> klass, String msg, Level level) {
    for (Logger logger : loggerList) {
      if (logger.getName().equals(klass.getName())) {
        logger.log(level, msg);
        return;
      }
    }
    Logger newLogger = LogManager.getLogger(klass);
    newLogger.log(level, msg);
    loggerList.add(newLogger);
  }

}
