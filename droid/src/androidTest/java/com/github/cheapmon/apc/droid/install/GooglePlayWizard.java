package com.github.cheapmon.apc.droid.install;

import android.content.pm.PackageManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import com.github.cheapmon.apc.droid.util.DroidException;
import java.io.IOException;

/**
 * Install or remove Android application from Google Play.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class GooglePlayWizard {

  /**
   * State after execution
   */
  public enum InstallState {
    ALREADY_INSTALLED, FAILURE, SUCCESS
  }

  /**
   * Crawl helper for this class
   */
  private static final GooglePlayHelper g = new GooglePlayHelper();

  /**
   * Install application.<br><br>
   *
   * <ul>
   * <li>Find install buttons and click</li>
   * <li>Give permissions</li>
   * <li>Fail on warning message</li>
   * <li>Skip account completion</li>
   * <li>Allow big downloads</li>
   * <li>Wait for download and installation to finish</li>
   * </ul>
   *
   * @param id App id
   */
  public static InstallState install(String id) {
    if (installed(id)) {
      return InstallState.ALREADY_INSTALLED;
    }
    g.start(id);
    if (g.has("warningMessage")) {
      return InstallState.FAILURE;
    }
    g.click("buttonContainer", "button");
    if (g.has("wifiMessage")) {
      g.click("buttonPanel", "firstButton");
    }
    if (g.has("buttonPanel")) {
      g.click("buttonPanel", "button");
      g.click("skipButton");
    }
    if (g.has("appPermissions")) {
      g.click("appPermissions", "continueBar", "continueButton");
    }
    g.waitUntilGone("downloadPanel");
    g.waitUntilGone("installMessage");
    if (g.has("message")) {
      g.click("buttonPanel", "firstButton");
      return InstallState.FAILURE;
    }
    g.waitUntilHas("buttonContainer");
    return InstallState.SUCCESS;
  }

  /**
   * Remove application.<br><br>
   *
   * Click uninstall button and confirm.
   *
   * @param id Application id
   */
  public static void remove(String id) {
    g.start(id);
    g.click("buttonContainer", "button");
    g.click("buttonPanel", "firstButton");
    g.waitUntilGone("message");
    g.waitUntilHas("buttonContainer");
  }

  /**
   * Remove application via shell command.
   *
   * @param id Application id
   */
  public static void removeSilently(String id) throws DroidException {
    UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    try {
      device.executeShellCommand(String.format("pm uninstall %s", id));
    } catch (IOException ex) {
      throw new DroidException("Shell command failed", ex);
    }
  }

  /**
   * Check if an application can be installed.<br><br>
   *
   * Simply look for warning message about version.
   *
   * @param id Application id
   * @return Whether the app can be installed or not
   */
  public static boolean canBeInstalled(String id) {
    g.start(id);
    return !g.has("warningMessage");
  }

  /**
   * Check if an app is already installed on this device.<br><br>
   *
   * Credit: <a href="https://stackoverflow.com/a/28175210/6743101">Jonik</a>
   *
   * @param id App id
   * @return Whether the app is installed
   */
  private static boolean installed(String id) {
    try {
      InstrumentationRegistry.getContext().getPackageManager().getApplicationInfo(id, 0);
      return true;
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
  }

}
