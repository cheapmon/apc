package com.github.cheapmon.apc.droid;

import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;
import com.github.cheapmon.apc.droid.util.GooglePlayHelper;

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
  private static GooglePlayHelper g = new GooglePlayHelper();

  /**
   * Default timeout for actions on device.
   */
  private static final int TIMEOUT = 1000;

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
   * @throws RemoteException Device communication fails
   */
  public static InstallState install(String id) throws RemoteException {
    UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    if (installed(id)) {
      return InstallState.ALREADY_INSTALLED;
    }
    g.start(id);
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
    if (device.hasObject(g.getContainer("warningMessage"))) {
      return InstallState.FAILURE;
    }
    g.click("buttonContainer", "button");
    if (device.hasObject(g.getContainer("wifiMessage"))) {
      g.click("buttonPanel", "firstButton");
    }
    if (device.hasObject(g.getContainer("buttonPanel"))) {
      g.click("buttonPanel", "button");
      g.click("skipButton");
    }
    if (device.hasObject(g.getContainer("appPermissions"))) {
      g.click("appPermissions", "continueBar", "continueButton");
    }
    device.wait(Until.hasObject(g.getContainer("downloadPanel")), TIMEOUT);
    while (device.hasObject(g.getContainer("downloadPanel"))) {
      device.wait(Until.gone(g.getContainer("downloadPanel")), TIMEOUT);
    }
    while (device.hasObject(g.getContainer("installMessage"))) {
      device.wait(Until.gone(g.getContainer("installMessage")), TIMEOUT);
    }
    if (device.hasObject(g.getContainer("message"))) {
      g.click("buttonPanel", "firstButton");
      return InstallState.FAILURE;
    }
    device.wait(Until.hasObject(g.getContainer("buttonContainer")), TIMEOUT);
    return InstallState.SUCCESS;
  }

  /**
   * Remove application.<br><br>
   *
   * Click uninstall button and confirm.
   *
   * @param id Application id
   * @throws RemoteException Device communication fails
   */
  public static void remove(String id) throws RemoteException {
    UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    g.start(id);
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
    g.click("buttonContainer", "button");
    g.click("buttonPanel", "firstButton");
    device.wait(Until.hasObject(g.getContainer("message")), TIMEOUT);
    while (device.hasObject(g.getContainer("message"))) {
      device.wait(Until.gone(g.getContainer("message")), TIMEOUT);
    }
    device.wait(Until.hasObject(g.getContainer("buttonContainer")), TIMEOUT);
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
