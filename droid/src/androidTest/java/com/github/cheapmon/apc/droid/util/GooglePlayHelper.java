package com.github.cheapmon.apc.droid.util;

import android.content.Intent;
import android.net.Uri;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import java.util.HashMap;

/**
 * Helper methods and utility for interaction with user interfaces.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class GooglePlayHelper {

  /**
   * Device to interact with
   */
  private UiDevice device;

  /**
   * Set of common UI containers
   */
  private HashMap<String, BySelector> containers;

  /**
   * Default timeout for actions on device
   */
  private final int TIMEOUT = 1000;

  /**
   * Create new helper.
   */
  public GooglePlayHelper() {
    device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    initContainers();
  }

  /**
   * Open Google Play page for application.
   *
   * @param id Application id
   * @throws RemoteException Device communication fails
   */
  public void start(String id) throws RemoteException {
    if (!device.isScreenOn()) {
      device.wakeUp();
    }
    InstrumentationRegistry.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
        Uri.parse(String.format("market://details?id=%s", id))));
  }

  /**
   * Click container and wait until action is finished.
   *
   * @param containers Chain of containers, last one will be clicked
   */
  public void click(String... containers) {
    device.wait(Until.gone(getContainer(containers[0])), TIMEOUT);
    UiObject2 objectToClick = null;
    for (String container : containers) {
      if (objectToClick == null) {
        objectToClick = device.findObject(getContainer(container));
      } else {
        objectToClick = objectToClick.findObject(getContainer(container));
      }
    }
    objectToClick.click();
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
    device.wait(Until.gone(getContainer(containers[0])), TIMEOUT);
  }

  /**
   * Wait until certain container is gone.
   *
   * @param container Container
   */
  public void waitUntilGone(String container) {
    while (device.hasObject(getContainer(container))) {
      device.wait(Until.gone(getContainer(container)), TIMEOUT);
    }
  }

  /**
   * Get an UI container by name.
   *
   * @param name Name of UI container
   * @return Container
   */
  public BySelector getContainer(String name) {
    return containers.get(name);
  }

  /**
   * Init common containers used by the Google Play app.
   */
  private void initContainers() {
    containers = new HashMap<>();
    containers.put("warningMessage", By.res("com.android.vending:id/warning_message_module"));
    containers.put("wifiMessage", By.res("com.android.vending:id/wifi_message"));
    containers.put("installMessage", By.res("com.android.vending:id/summary_dynamic_status")
        .clazz("android.widget.TextView"));
    containers.put("message", By.res("android:id/message"));
    containers.put("buttonContainer", By.res("com.android.vending:id/button_container"));
    containers.put("buttonPanel", By.res("com.android.vending:id/buttonPanel"));
    containers.put("downloadPanel", By.res("com.android.vending:id/download_progress_panel"));
    containers.put("appPermissions", By.res("com.android.vending:id/app_permissions"));
    containers.put("continueBar", By.res("com.android.vending:id/continue_button_bar"));
    containers.put("continueButton", By.res("com.android.vending:id/continue_button"));
    containers.put("skipButton", By.res("com.android.vending:id/not_now_button"));
    containers.put("button", By.clazz("android.widget.Button"));
    containers.put("firstButton", By.res("android:id/button1"));
  }

}
