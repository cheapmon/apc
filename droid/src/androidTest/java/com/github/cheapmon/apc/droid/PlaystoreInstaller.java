package com.github.cheapmon.apc.droid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;
import com.github.cheapmon.apc.droid.util.CrawlHelper;

/**
 * Install or remove Android application from Google Play.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class PlaystoreInstaller {

  /**
   * State after execution
   */
  public enum InstallState {
    ALREADY_INSTALLED, FAILURE, SUCCESS
  }

  /**
   * Crawl helper for this class
   */
  private static CrawlHelper crawlHelper = new CrawlHelper();

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
    openPlaystore(id);
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
    if (device.hasObject(crawlHelper.getGooglePlayContainer("warningMessage"))) {
      return InstallState.FAILURE;
    }
    device.wait(Until.hasObject(crawlHelper.getGooglePlayContainer("buttonContainer")), TIMEOUT);
    device.findObject(crawlHelper.getGooglePlayContainer("buttonContainer"))
        .findObject(crawlHelper.getGooglePlayContainer("button")).click();
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
    device.wait(Until.gone(crawlHelper.getGooglePlayContainer("buttonContainer")), TIMEOUT);
    if (device.hasObject(crawlHelper.getGooglePlayContainer("wifiMessage"))) {
      device.findObject(crawlHelper.getGooglePlayContainer("buttonPanel"))
          .findObject(crawlHelper.getGooglePlayContainer("firstButton")).click();
      device.waitForWindowUpdate("com.android.vending", TIMEOUT);
      device.wait(Until.gone(crawlHelper.getGooglePlayContainer("wifiMessage")), TIMEOUT);
    }
    if (device.hasObject(crawlHelper.getGooglePlayContainer("buttonPanel"))) {
      device.findObject(crawlHelper.getGooglePlayContainer("buttonPanel"))
          .findObject(crawlHelper.getGooglePlayContainer("button")).click();
      device.waitForWindowUpdate("com.android.vending", TIMEOUT);
      device.wait(Until.hasObject(crawlHelper.getGooglePlayContainer("skipButton")), TIMEOUT);
      device.findObject(crawlHelper.getGooglePlayContainer("skipButton")).click();
      device.waitForWindowUpdate("com.android.vending", TIMEOUT);
      device.wait(Until.gone(crawlHelper.getGooglePlayContainer("buttonPanel")), TIMEOUT);
    }
    if (device.hasObject(crawlHelper.getGooglePlayContainer("appPermissions"))) {
      device.findObject(crawlHelper.getGooglePlayContainer("continueBar"))
          .findObject(crawlHelper.getGooglePlayContainer("continueButton")).click();
      device.waitForWindowUpdate("com.android.vending", TIMEOUT);
      device.wait(Until.gone(crawlHelper.getGooglePlayContainer("appPermissions")), TIMEOUT);
    }
    device.wait(Until.hasObject(crawlHelper.getGooglePlayContainer("downloadPanel")), TIMEOUT);
    while (device.hasObject(crawlHelper.getGooglePlayContainer("downloadPanel"))) {
      device.wait(Until.gone(crawlHelper.getGooglePlayContainer("downloadPanel")), TIMEOUT);
    }
    while (device.hasObject(crawlHelper.getGooglePlayContainer("installMessage"))) {
      device.wait(Until.gone(crawlHelper.getGooglePlayContainer("installMessage")), TIMEOUT);
    }
    if (device.hasObject(crawlHelper.getGooglePlayContainer("message"))) {
      device.findObject(crawlHelper.getGooglePlayContainer("buttonPanel"))
          .findObject(crawlHelper.getGooglePlayContainer("firstButton")).click();
      device.waitForWindowUpdate("com.android.vending", TIMEOUT);
      device.wait(Until.gone(crawlHelper.getGooglePlayContainer("message")), TIMEOUT);
      return InstallState.FAILURE;
    }
    device.wait(Until.hasObject(crawlHelper.getGooglePlayContainer("buttonContainer")), TIMEOUT);
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
    openPlaystore(id);
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
    device.wait(Until.hasObject(crawlHelper.getGooglePlayContainer("buttonContainer")), TIMEOUT);
    device.findObject(crawlHelper.getGooglePlayContainer("buttonContainer"))
        .findObject(crawlHelper.getGooglePlayContainer("button")).click();
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
    device.wait(Until.hasObject(crawlHelper.getGooglePlayContainer("buttonPanel")), TIMEOUT);
    device.findObject(crawlHelper.getGooglePlayContainer("buttonPanel"))
        .findObject(crawlHelper.getGooglePlayContainer("firstButton")).click();
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
    device.wait(Until.gone(crawlHelper.getGooglePlayContainer("buttonPanel")), TIMEOUT);
    device.wait(Until.hasObject(crawlHelper.getGooglePlayContainer("message")), TIMEOUT);
    while (device.hasObject(crawlHelper.getGooglePlayContainer("message"))) {
      device.wait(Until.gone(crawlHelper.getGooglePlayContainer("message")), TIMEOUT);
    }
    device.wait(Until.hasObject(crawlHelper.getGooglePlayContainer("buttonContainer")), TIMEOUT);
  }

  /**
   * Open Google Play Page for application.
   *
   * @param id Application id
   * @throws RemoteException Device communication fails
   */
  private static void openPlaystore(String id) throws RemoteException {
    UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    if (!device.isScreenOn()) {
      device.wakeUp();
    }
    Context context = InstrumentationRegistry.getContext();
    context.startActivity(
        new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("market://details?id=%s", id))));
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
