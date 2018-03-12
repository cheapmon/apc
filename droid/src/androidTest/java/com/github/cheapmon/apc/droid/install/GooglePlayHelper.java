package com.github.cheapmon.apc.droid.install;

import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import com.github.cheapmon.apc.droid.util.DroidException;
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
   * Default timeout for Google Play reload
   */
  private final int RELOAD_TIMEOUT = 10;

  /**
   * Number of times Google Play has been reloaded
   */
  private int reloadCount = 0;

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
   * @throws DroidException Device communication fails
   */
  public void start(String id) throws DroidException {
    Uri uri = Uri.parse(String.format("market://details?id=%s", id));
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    InstrumentationRegistry.getContext().startActivity(intent);
    waitUntilHas("market", TIMEOUT * 5);
    waitForChange();
  }

  /**
   * Click container and wait until action is finished.
   *
   * @param containers Chain of containers, last one will be clicked
   */
  public void click(String... containers) {
    waitUntilHas(containers[0]);
    UiObject2 objectToClick = null;
    for (String container : containers) {
      if (objectToClick == null) {
        objectToClick = device.findObject(this.containers.get(container));
      } else {
        objectToClick = objectToClick.findObject(this.containers.get(container));
      }
    }
    objectToClick.click();
    waitForChange();
    waitUntilGone(containers[0]);
  }

  /**
   * Wait until the screen has changed.
   */
  public void waitForChange() {
    device.waitForWindowUpdate("com.android.vending", TIMEOUT);
  }

  /**
   * Wait until certain container has appeared.
   *
   * @param container Container
   */
  public void waitUntilHas(String container) {
    device.wait(Until.hasObject(this.containers.get(container)), TIMEOUT);
  }

  /**
   * Wait certain time until certain container has appeared.
   *
   * @param container Container
   * @param timeout Time to wait
   */
  public void waitUntilHas(String container, int timeout) {
    device.wait(Until.hasObject(this.containers.get(container)), timeout);
  }

  /**
   * Wait until certain container is gone.
   *
   * @param container Container
   */
  public void waitUntilGone(String container) {
    while (device.hasObject(this.containers.get(container))) {
      device.wait(Until.gone(this.containers.get(container)), TIMEOUT);
    }
  }

  /**
   * Check if container exists in UI.
   *
   * @param container Container to check
   * @return Whether or not the container exists
   */
  public boolean has(String container) {
    return device.hasObject(this.containers.get(container));
  }

  /**
   * Init common containers used by the Google Play app.
   */
  private void initContainers() {
    containers = new HashMap<>();
    containers.put("market", By.pkg("com.android.vending"));
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
