package com.github.cheapmon.apc.droid.search;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject2;
import com.github.cheapmon.apc.droid.util.DroidException;
import com.github.cheapmon.apc.droid.util.ExtractionHelper;
import java.util.List;
import java.util.Random;

/**
 * Search an app for a policy using random clicks and scrolls.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com>cheapmon</a>
 */
public class RandomizedSearch implements SearchAlgorithm {

  /**
   * Maximum number of clicks used until stopping search
   */
  private static final int CLICK_MAX = 1000;

  /**
   * Search policy.
   *
   * @param id Application identification
   * @return Policy text
   * @throws DroidException Activity name can't be found
   */
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
          e.click(clickList.get(r.nextInt(clickList.size())));
        }
        e.waitForUpdate();
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
