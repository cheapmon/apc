package com.github.cheapmon.apc.droid.util;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import java.util.HashMap;

/**
 * Helper methods and utility for interaction with user interfaces.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class CrawlHelper {

  /**
   * Set of common UI containers
   */
  private HashMap<String, BySelector> containers;

  /**
   * Create new helper.
   */
  public CrawlHelper() {
    initGooglePlayContainers();
  }

  /**
   * Get an UI container by name.
   *
   * @param name Name of UI container
   * @return Container
   */
  public BySelector getGooglePlayContainer(String name) {
    return containers.get(name);
  }

  /**
   * Init common containers used by the Google Play app.
   */
  private void initGooglePlayContainers() {
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
