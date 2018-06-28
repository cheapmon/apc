package com.github.cheapmon.apc.droid.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.widget.TextView;
import com.github.cheapmon.apc.droid.extract.Page;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

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
  private final int TIMEOUT = 2500;

  /**
   * Maximum number of scroll gestures performed on one container
   */
  private static final int SCROLL_MAX = 3;

  /**
   * Get new helper for certain application
   *
   * @param applicationID ID of application
   */
  public ExtractionHelper(String applicationID) throws DroidException {
    this.applicationID = applicationID;
    this.device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    this.launchContext = InstrumentationRegistry.getContext();
    this.launchIntent = this.launchContext.getPackageManager()
        .getLaunchIntentForPackage(this.applicationID);
    if (this.launchIntent == null) {
      throw new DroidException("Launch intent corrupted");
    }
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
      obj = this.find(d);
      this.click(obj);
    }
  }

  /**
   * Click view and wait for the screen to update.
   *
   * @param obj UI element to click
   */
  public void click(UiObject2 obj) {
    if (obj.getClassName().equals(TextView.class.getName())) {
      Rect bounds = obj.getVisibleBounds();
      int x = bounds.centerX();
      int y = bounds.centerY() + (bounds.bottom - bounds.top) / 4;
      this.device.click(x, y);
    } else {
      obj.click();
    }
    this.waitForUpdate();
  }

  /**
   * Get bounds of current Display.
   *
   * @return Display bounds
   */
  public Rect getDisplayBounds() {
    return this.getRoot().getVisibleBounds();
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
    BySelector drawerLayout = By.clazz(Pattern.compile(".*" + Pattern.quote(".DrawerLayout")));
    if (this.device.hasObject(drawerLayout)) {
      if (this.device.findObject(drawerLayout) != null
          && this.device.findObject(drawerLayout).getChildren().size() > 1) {
        // TODO: Find out which child is the navigation menu - This approach is suboptimal
        int area = Integer.MAX_VALUE;
        int smallestID = 0;
        List<UiObject2> children = this.device.findObject(drawerLayout).getChildren();
        for (int i = 0; i < children.size(); i++) {
          UiObject2 child = children.get(i);
          Rect bounds = child.getVisibleBounds();
          int newArea = (bounds.bottom - bounds.top) * (bounds.right - bounds.left);
          if (newArea < area) {
            area = newArea;
            smallestID = i;
          }
        }
        return this.device.findObject(drawerLayout).getChildren().get(smallestID);
      }
    }
    return this.device.findObject(By.pkg(this.applicationID).depth(0));
  }

  /**
   * Get page representation of current layout, dependent of id.
   *
   * @return Resulting page
   */
  public Page getPage() {
    Page page = new Page(this.getRoot());
    for (UiObject2 cont : this.getRoot().findObjects(By.scrollable(true))) {
      try {
        for (int i = 0; i < SCROLL_MAX; i++) {
          boolean canScroll = cont.scroll(Direction.DOWN, 1);
          if (!canScroll) {
            break;
          }
          page.merge(new Page(this.getRoot()));
        }
      } catch (NullPointerException | StaleObjectException ignored) {
      }
    }
    return page;
  }

  /**
   * Get page representation of current layout, independent of id.
   */
  public Page getPageFromAnyApp() {
    UiObject2 root = this.device.findObject(By.pkg(this.device.getCurrentPackageName()).depth(0));
    if (root != null) {
      return new Page(root);
    }
    return null;
  }

  /**
   * Get all clickable views of current layout.
   *
   * @return List of view selectors
   */
  public List<List<DroidSelector>> getClickable() {
    List<List<DroidSelector>> list = this.get(By.clickable(true));
    List<UiObject2> scrollContainer = this.getRoot().findObjects(By.scrollable(true));
    for (UiObject2 cont : scrollContainer) {
      try {
        for (int i = 0; i < SCROLL_MAX; i++) {
          boolean canScroll = cont.scroll(Direction.DOWN, 1);
          if (!canScroll) {
            break;
          }
          for (UiObject2 clickView : cont.findObjects(By.clickable(true))) {
            list.add(this.getSelector(clickView, i + 1));
          }
        }
      } catch (NullPointerException | StaleObjectException ignored) {
      }
    }
    return list;
  }

  /**
   * Get certain views of current layout.
   *
   * @param selector Selector of views
   * @return List of view selectors
   */
  public List<List<DroidSelector>> get(BySelector selector) {
    List<UiObject2> selectedViews = this.getRoot().findObjects(selector);
    List<List<DroidSelector>> list = new ArrayList<>();
    for (UiObject2 view : selectedViews) {
      list.add(this.getSelector(view, 0));
    }
    return list;
  }

  /**
   * Get selector for an UI element.
   *
   * @param obj UI element
   * @return Selector for element
   */
  public List<DroidSelector> getSelector(UiObject2 obj, int offset) {
    if (obj == null) {
      return null;
    }
    LinkedList<DroidSelector> list = new LinkedList<>();
    BySelector lastSelector = By.clickable(obj.isClickable()).scrollable(obj.isScrollable())
        .clazz(obj.getClassName()).pkg(obj.getApplicationPackage());
    Rect lastBounds = obj.getVisibleBounds();
    obj = obj.getParent();
    BySelector selector;
    Rect bounds = obj.getVisibleBounds();
    String text = obj.getText();
    while (obj != null && !obj.equals(this.getRoot().getParent())) {
      if (obj.isScrollable()) {
        selector = By.clickable(obj.isClickable()).scrollable(true)
            .clazz(obj.getClassName()).pkg(obj.getApplicationPackage());
      } else {
        selector = By.clickable(obj.isClickable()).scrollable(false)
            .clazz(obj.getClassName()).pkg(obj.getApplicationPackage()).hasChild(lastSelector);
      }
      if (obj.findObjects(lastSelector).size() > 1) {
        for (int i = 0; i < obj.findObjects(lastSelector).size(); i++) {
          UiObject2 o = obj.findObjects(lastSelector).get(i);
          if (o.getVisibleBounds().equals(lastBounds)) {
            // TODO: Only add offset for scrollable element
            list.addFirst(new DroidSelector(lastSelector, i, offset).setMeta(bounds, text));
            break;
          }
        }
      } else {
        list.addFirst(new DroidSelector(lastSelector, 0, offset).setMeta(bounds, text));
      }
      lastSelector = selector;
      lastBounds = obj.getVisibleBounds();
      bounds = obj.getVisibleBounds();
      text = obj.getText();
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
    UiObject2 obj = this.getRoot();
    for (DroidSelector selector : list) {
      obj = obj.findObjects(selector.getSelector()).get(selector.getPos());
      if (obj.isScrollable()) {
        for (int i = 0; i < selector.getOffset(); i++) {
          obj.scroll(Direction.DOWN, 1);
        }
      }
    }
    return obj;
  }

  /**
   * Wait until current layout has changed.
   */
  public void waitForUpdate() {
    this.device.waitForWindowUpdate(this.applicationID, this.TIMEOUT);
    this.device.wait(Until.hasObject(By.text(Pattern.compile("[A-z0-9]+"))), this.TIMEOUT);
  }

}
