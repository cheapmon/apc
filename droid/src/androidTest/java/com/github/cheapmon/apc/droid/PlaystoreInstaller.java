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
   * Install application.<br><br>
   *
   * Very basic approach. Click install buttons and give permissions. Fail on warning message.
   *
   * @param id App id
   * @throws RemoteException Device communication fails
   */
  public static InstallState install(String id) throws RemoteException {
    if (installed(id)) {
      return InstallState.ALREADY_INSTALLED;
    }
    openPlaystore(id);
    UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    if (device.hasObject(By.res("com.android.vending:id/warning_message_module"))) {
      return InstallState.FAILURE;
    }
    BySelector buttonContainerSelector = By.res("com.android.vending:id/button_container");
    device.findObject(buttonContainerSelector).findObject(By.clazz("android.widget.Button"))
        .click();
    device.wait(Until.gone(buttonContainerSelector), 5000);
    if (device.hasObject(By.res("com.android.vending:id/app_permissions"))) {
      device.findObject(By.res("com.android.vending:id/continue_button_bar"))
          .findObject(By.res("com.android.vending:id/continue_button"))
          .click();
    }
    device.wait(Until.hasObject(buttonContainerSelector), 5000);
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
    openPlaystore(id);
    UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    BySelector buttonContainerSelector = By.res("com.android.vending:id/button_container");
    BySelector buttonPanelSelector = By.res("com.android.vending:id/buttonPanel");
    device.findObject(buttonContainerSelector)
        .findObject(By.clazz("android.widget.Button")).click();
    device.wait(Until.hasObject(buttonPanelSelector), 5000);
    device.findObject(buttonPanelSelector).findObject(By.res("android:id/button1")).click();
    device.wait(Until.gone(buttonPanelSelector), 5000);
    device.wait(Until.hasObject(buttonContainerSelector), 5000);
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
