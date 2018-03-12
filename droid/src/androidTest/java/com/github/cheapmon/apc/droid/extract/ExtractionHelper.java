package com.github.cheapmon.apc.droid.extract;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import com.github.cheapmon.apc.droid.util.DroidException;
import java.io.IOException;
import java.util.List;

/**
 * Provide utility for model extraction.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class ExtractionHelper {

  /**
   * ID of application this helper belongs to
   */
  private final String applicationID;

  /**
   * Device this helper belongs to
   */
  private final UiDevice device;

  /**
   * Launcher context for application
   */
  private final Context launchContext;

  /**
   * Launcher intent for application
   */
  private final Intent launchIntent;

  /**
   * Timeout for application loading
   */
  private final int TIMEOUT = 5000;

  /**
   * Get new helper for certain application
   *
   * @param applicationID ID of application
   */
  public ExtractionHelper(String applicationID) {
    this.applicationID = applicationID;
    this.device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    this.launchContext = InstrumentationRegistry.getContext();
    this.launchIntent = launchContext.getPackageManager()
        .getLaunchIntentForPackage(this.applicationID);
    this.launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
  }

  /**
   * Start activity from scratch.
   */
  public void start() {
    launchContext.startActivity(launchIntent);
    device.wait(Until.hasObject(By.pkg(this.applicationID).depth(0)), TIMEOUT);
  }

  /**
   * Get current activity.
   *
   * @return Activity name
   */
  public String getActivityName() throws DroidException {
    try {
      String out = this.device.executeShellCommand("dumpsys window windows");
      for (String line : out.split("\n")) {
        if (line.contains("mFocusedApp=")) {
          for (String part : line.split(" ")) {
            if (part.matches("[A-z.]*/[A-z.]*")) {
              return part;
            }
          }
        }
      }
    } catch (IOException ex) {
      throw new DroidException("Shell command failed", ex);
    }
    throw new DroidException("Could not find current activity");
  }

  /**
   * Get root view of an applications layout.
   *
   * @return Root view
   */
  public UiObject2 getRoot() {
    return this.device.findObject(By.pkg(this.applicationID).depth(0));
  }

  /**
   * Get page representation of current layout.
   *
   * @return Resulting page
   */
  public Page getPage() {
    return new Page(getRoot());
  }

  /**
   * Get all clickable views of current layout.
   *
   * @return List of views
   */
  public List<UiObject2> getAllClickable() {
    return getRoot().findObjects(By.clickable(true));
  }

  /**
   * Wait until current layout has changed.
   */
  public void waitForUpdate() {
    this.device.waitForWindowUpdate(this.applicationID, TIMEOUT);
  }

}
