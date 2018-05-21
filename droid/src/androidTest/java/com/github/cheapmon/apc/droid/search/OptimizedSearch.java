package com.github.cheapmon.apc.droid.search;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import java.util.regex.Pattern;

public class OptimizedSearch implements SearchAlgorithm {

  private UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

  private static final String[] keywords = {
      "daten", "data", "privat", "privacy", "hilfe", "help", "info", "einstellung", "setting",
      "über", "about"
  };

  @Override
  public String run(String id) {
    // STARTUP
    Context launchContext = InstrumentationRegistry.getContext();
    Intent launchIntent = launchContext.getPackageManager()
        .getLaunchIntentForPackage(id);
    launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    launchContext.startActivity(launchIntent);
    this.device.wait(Until.hasObject(By.pkg(id).depth(0)), 5000);
    // LOOK FOR NAVIGATION MENU
    nav:
    if (this.device.hasObject(By.clazz(Pattern.compile(".*\\.DrawerLayout")))) {
      UiObject2 navButton = this.device.findObject(By.clazz(Pattern.compile(".*Button")));
      if (navButton != null) {
        navButton.click();
      } else {
        break nav;
      }
      UiObject2 navMenu = this.device.findObject(By.clazz(".*\\.DrawerLayout")).getChildren()
          .get(1);
      UiObject2 navElement = null;
      for (int c = 0; c < 3 && navElement == null; c++) {
        navElement = findNavElement(navMenu);
        navMenu.scroll(Direction.DOWN, 1);
      }
      if (navElement != null) {
        navElement.click();
      } else {
        break nav;
      }
    }
    return null;
  }

  private UiObject2 findNavElement(UiObject2 nav) {
    for (String keyword : keywords) {
      String firstLetter = keyword.substring(0, 1);
      String pattern = ".*[" + firstLetter.toUpperCase()
          + firstLetter + "]" + keyword.substring(1) + ".*";
      UiObject2 navElement = nav.findObject(By.text(Pattern.compile(pattern)));
      if (navElement != null) {
        return navElement;
      }
    }
    return null;
  }

}
