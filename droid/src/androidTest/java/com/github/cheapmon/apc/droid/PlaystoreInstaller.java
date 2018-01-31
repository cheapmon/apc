package com.github.cheapmon.apc.droid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    BySelector warningMessage = By.res("com.android.vending:id/warning_message_module");
    BySelector wifiMessage = By.res("com.android.vending:id/wifi_message");
    BySelector installMessage = By.res("com.android.vending:id/summary_dynamic_status")
        .clazz("android.widget.TextView");
    BySelector message = By.res("android:id/message");
    BySelector buttonContainer = By.res("com.android.vending:id/button_container");
    BySelector buttonPanel = By.res("com.android.vending:id/buttonPanel");
    BySelector downloadPanel = By.res("com.android.vending:id/download_progress_panel");
    BySelector appPermissions = By.res("com.android.vending:id/app_permissions");
    BySelector continueBar = By.res("com.android.vending:id/continue_button_bar");
    BySelector continueButton = By.res("com.android.vending:id/continue_button");
    BySelector skipButton = By.res("com.android.vending:id/not_now_button");
    BySelector button = By.clazz("android.widget.Button");
    BySelector firstButton = By.res("android:id/button1");
    UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    if (installed(id)) {
      return InstallState.ALREADY_INSTALLED;
    }
    openPlaystore(id);
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
    if (device.hasObject(warningMessage)) {
      return InstallState.FAILURE;
    }
    device.wait(Until.hasObject(buttonContainer), TIMEOUT);
    device.findObject(buttonContainer).findObject(button).click();
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
    device.wait(Until.gone(buttonContainer), TIMEOUT);
    if (device.hasObject(wifiMessage)) {
      device.findObject(buttonPanel).findObject(firstButton).click();
      device.waitForWindowUpdate("com.android.vending", TIMEOUT);
      device.wait(Until.gone(wifiMessage), TIMEOUT);
    }
    if (device.hasObject(buttonPanel)) {
      device.findObject(buttonPanel).findObject(button).click();
      device.waitForWindowUpdate("com.android.vending", TIMEOUT);
      device.wait(Until.hasObject(skipButton), TIMEOUT);
      device.findObject(skipButton).click();
      device.waitForWindowUpdate("com.android.vending", TIMEOUT);
      device.wait(Until.gone(buttonPanel), TIMEOUT);
    }
    if (device.hasObject(appPermissions)) {
      device.findObject(continueBar).findObject(continueButton).click();
      device.waitForWindowUpdate("com.android.vending", TIMEOUT);
      device.wait(Until.gone(appPermissions), TIMEOUT);
    }
    device.wait(Until.hasObject(downloadPanel), TIMEOUT);
    while (device.hasObject(downloadPanel)) {
      device.wait(Until.gone(downloadPanel), TIMEOUT);
    }
    while (device.hasObject(installMessage)) {
      device.wait(Until.gone(installMessage), TIMEOUT);
    }
    if (device.hasObject(message)) {
      device.findObject(buttonPanel).findObject(firstButton).click();
      device.waitForWindowUpdate("com.android.vending", TIMEOUT);
      device.wait(Until.gone(message), TIMEOUT);
      return InstallState.FAILURE;
    }
    device.wait(Until.hasObject(buttonContainer), TIMEOUT);
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
    BySelector message = By.res("android:id/message");
    BySelector buttonContainer = By.res("com.android.vending:id/button_container");
    BySelector buttonPanel = By.res("com.android.vending:id/buttonPanel");
    BySelector button = By.clazz("android.widget.Button");
    BySelector firstButton = By.res("android:id/button1");
    UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    openPlaystore(id);
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
    device.wait(Until.hasObject(buttonContainer), TIMEOUT);
    device.findObject(buttonContainer).findObject(button).click();
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
    device.wait(Until.hasObject(buttonPanel), TIMEOUT);
    device.findObject(buttonPanel).findObject(firstButton).click();
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
    device.wait(Until.gone(buttonPanel), TIMEOUT);
    device.wait(Until.hasObject(message), TIMEOUT);
    while (device.hasObject(message)) {
      device.wait(Until.gone(message), TIMEOUT);
    }
    device.wait(Until.hasObject(buttonContainer), TIMEOUT);
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
