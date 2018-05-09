package com.github.cheapmon.apc.droid.extract;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import com.github.cheapmon.apc.droid.util.DroidException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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
  private final int TIMEOUT = 1000;

  /**
   * Get new helper for certain application
   *
   * @param applicationID ID of application
   */
  public ExtractionHelper(String applicationID) {
    this.applicationID = applicationID;
    this.device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    this.launchContext = InstrumentationRegistry.getContext();
    this.launchIntent = this.launchContext.getPackageManager()
        .getLaunchIntentForPackage(this.applicationID);
    this.launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
  }

  /**
   * Start activity from scratch.
   */
  public void start() {
    this.launchContext.startActivity(this.launchIntent);
    this.device.wait(Until.hasObject(By.pkg(this.applicationID).depth(0)), this.TIMEOUT);
  }

  /**
   * Start activity and click list of views.
   *
   * @param path Path to view
   */
  public void start(List<List<DroidSelector>> path) {
    this.start();
    UiObject2 obj;
    for (List<DroidSelector> d : path) {
      obj = find(d);
      obj.click();
      waitForUpdate();
    }
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
    BySelector drawerLayout = By.clazz("android.support.v4.widget.DrawerLayout");
    if (this.device.hasObject(drawerLayout)) {
      if (this.device.findObject(drawerLayout) != null
          && this.device.findObject(drawerLayout).getChildren().size() > 1) {
        return this.device.findObject(drawerLayout).getChildren().get(1);
      }
    }
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
  public List<List<DroidSelector>> getClickable() {
    List<UiObject2> clickViews = getRoot().findObjects(By.clickable(true));
    List<List<DroidSelector>> list = new ArrayList<>();
    for (UiObject2 clickView : clickViews) {
      list.add(getSelector(clickView));
    }
    return list;
  }

  /**
   * Get selector for an UI element.
   *
   * @param obj UI element
   * @return Selector for element
   */
  public List<DroidSelector> getSelector(UiObject2 obj) {
    LinkedList<DroidSelector> list = new LinkedList<>();
    BySelector lastSelector = By.clickable(obj.isClickable()).scrollable(obj.isScrollable())
        .clazz(obj.getClassName()).pkg(obj.getApplicationPackage());
    Rect lastBounds = obj.getVisibleBounds();
    obj = obj.getParent();
    BySelector selector;
    while (obj != null && !obj.equals(getRoot().getParent())) {
      selector = By.clickable(obj.isClickable()).scrollable(obj.isScrollable())
          .clazz(obj.getClassName()).pkg(obj.getApplicationPackage()).hasChild(lastSelector);
      if (obj.findObjects(lastSelector).size() > 1) {
        for (int i = 0; i < obj.findObjects(lastSelector).size(); i++) {
          UiObject2 o = obj.findObjects(lastSelector).get(i);
          if (o.getVisibleBounds().equals(lastBounds)) {
            list.addFirst(new DroidSelector(lastSelector, i));
            break;
          }
        }
      } else {
        list.addFirst(new DroidSelector(lastSelector, 0));
      }
      lastSelector = selector;
      lastBounds = obj.getVisibleBounds();
      obj = obj.getParent();
    }
    return list;
  }

  /**
   * Find element in layout by its properties.
   *
   * @param list List of parent elements
   * @return Element
   */
  public UiObject2 find(List<DroidSelector> list) {
    UiObject2 obj = getRoot();
    for (DroidSelector selector : list) {
      obj = obj.findObjects(selector.s).get(selector.n);
    }
    return obj;
  }

  /**
   * Wait until current layout has changed.
   */
  public void waitForUpdate() {
    this.device.waitForWindowUpdate(this.applicationID, this.TIMEOUT);
  }

  /**
   * Select element by its properties and relative position.
   */
  protected class DroidSelector {

    /**
     * Selector of this element
     */
    private final BySelector s;

    /**
     * Relative position of this element in its parent container
     */
    private final int n;

    /**
     * Create new selector.
     *
     * @param s Elements selector
     * @param n Elements relative position
     */
    public DroidSelector(BySelector s, int n) {
      this.s = s;
      this.n = n;
    }

    /**
     * Put information about this selector.
     *
     * @return Information
     */
    @Override
    public String toString() {
      return String.format("%s, %s", this.s.toString(), this.n);
    }

  }

}
