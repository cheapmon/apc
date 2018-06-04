package com.github.cheapmon.apc.droid.search;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject2;
import com.github.cheapmon.apc.droid.util.DroidException;
import com.github.cheapmon.apc.droid.util.DroidLogger;
import com.github.cheapmon.apc.droid.util.ExtractionHelper;
import java.util.List;
import java.util.Random;

public class RandomizedSearch implements SearchAlgorithm {

  private static final int CLICK_MAX = 1000;

  @Override
  public String run(String id) throws DroidException {
    Random r = new Random();
    ExtractionHelper e = new ExtractionHelper(id);
    e.start();
    e.waitForUpdate();
    for (int i = 0; i < CLICK_MAX; i++) {
      try {
        List<UiObject2> scrollList = e.getRoot().findObjects(By.scrollable(true));
        if (scrollList.size() > 0) {
          scrollList.get(r.nextInt(scrollList.size())).scroll(Direction.DOWN, r.nextFloat());
        }
        List<UiObject2> clickList = e.getRoot().findObjects(By.clickable(true));
        if (clickList.size() > 0) {
          clickList.get(r.nextInt(clickList.size())).click();
        }
        e.waitForUpdate();
        DroidLogger.log(e.getActivityName());
        if (e.getActivityName().startsWith(id)) {
          String txt = e.getPage().dumpText();
          if (SearchHelper.isPolicy(txt)) {
            return txt;
          }
        } else {
          e.start();
        }
      } catch (ArrayIndexOutOfBoundsException | NullPointerException | StaleObjectException ex) {
        e.start();
      }
    }
    return null;
  }

}
