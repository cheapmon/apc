package com.github.cheapmon.apc.droid.install;

import android.content.Intent;
import android.net.Uri;
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
class GooglePlayHelper {

  /**
   * Device to interact with
   */
  private final UiDevice device;

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
  GooglePlayHelper() {
    this.device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    this.initContainers();
  }

  /**
   * Open Google Play page for application.
   *
   * @param id Application id
   */
  public void start(String id) {
    Uri uri = Uri.parse(String.format("market://details?id=%s", id));
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    InstrumentationRegistry.getContext().startActivity(intent);
    this.waitUntilHas("market", this.TIMEOUT * 5);
    this.waitForChange();
  }

  /**
   * Click container and wait until action is finished.
   *
   * @param containers Chain of containers, last one will be clicked
   */
  public void click(String... containers) {
    this.waitUntilHas(containers[0]);
    UiObject2 objectToClick = null;
    for (String container : containers) {
      if (objectToClick == null) {
        objectToClick = this.device.findObject(this.containers.get(container));
      } else {
        objectToClick = objectToClick.findObject(this.containers.get(container));
      }
    }
    objectToClick.click();
    this.waitForChange();
    this.waitUntilGone(containers[0]);
  }

  /**
   * Wait until the screen has changed.
   */
  private void waitForChange() {
    this.device.waitForWindowUpdate("com.android.vending", this.TIMEOUT);
  }

  /**
   * Wait until certain container has appeared.
   *
   * @param container Container
   */
  void waitUntilHas(String container) {
    this.device.wait(Until.hasObject(this.containers.get(container)), this.TIMEOUT);
  }

  /**
   * Wait certain time until certain container has appeared.
   *
   * @param container Container
   * @param timeout Time to wait
   */
  private void waitUntilHas(String container, int timeout) {
    this.device.wait(Until.hasObject(this.containers.get(container)), timeout);
  }

  /**
   * Wait until certain container is gone.
   *
   * @param container Container
   */
  void waitUntilGone(String container) {
    while (this.device.hasObject(this.containers.get(container))) {
      this.device.wait(Until.gone(this.containers.get(container)), this.TIMEOUT);
    }
  }

  /**
   * Check if container exists in UI.
   *
   * @param container Container to check
   * @return Whether or not the container exists
   */
  boolean has(String container) {
    return this.device.hasObject(this.containers.get(container));
  }

  /**
   * Init common containers used by the Google Play app.
   */
  private void initContainers() {
    this.containers = new HashMap<>();
    this.containers.put("market", By.pkg("com.android.vending"));
    this.containers.put("warningMessage", By.res("com.android.vending:id/warning_message_module"));
    this.containers.put("wifiMessage", By.res("com.android.vending:id/wifi_message"));
    this.containers.put("installMessage", By.res("com.android.vending:id/summary_dynamic_status")
        .clazz("android.widget.TextView"));
    this.containers.put("message", By.res("android:id/message"));
    this.containers.put("buttonContainer", By.res("com.android.vending:id/button_container"));
    this.containers.put("buttonPanel", By.res("com.android.vending:id/buttonPanel"));
    this.containers.put("downloadPanel", By.res("com.android.vending:id/download_progress_panel"));
    this.containers.put("appPermissions", By.res("com.android.vending:id/app_permissions"));
    this.containers.put("continueBar", By.res("com.android.vending:id/continue_button_bar"));
    this.containers.put("continueButton", By.res("com.android.vending:id/continue_button"));
    this.containers.put("skipButton", By.res("com.android.vending:id/not_now_button"));
    this.containers.put("button", By.clazz("android.widget.Button"));
    this.containers.put("firstButton", By.res("android:id/button1"));
  }

}
