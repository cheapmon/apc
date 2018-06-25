package com.github.cheapmon.apc.droid.search.strategy;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject2;
import com.github.cheapmon.apc.droid.extract.Page;
import com.github.cheapmon.apc.droid.util.DroidException;
import com.github.cheapmon.apc.droid.util.DroidSelector;
import com.github.cheapmon.apc.droid.util.ExtractionHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Search policy in an app by looking for a text on the registry page.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com>cheapmon</a>
 */
public class RegistryPageStrategy implements SearchStrategy {

  /**
   * Search policy.
   *
   * @param id Application identification
   * @return Policy text
   * @throws DroidException Text view search failed
   */
  @Override
  public Page search(String id) throws DroidException {
    boolean finished = false;
    while (!finished) {
      try {
        ExtractionHelper e = new ExtractionHelper(id);
        e.start();
        e.waitForUpdate();
        List<List<DroidSelector>> path = new ArrayList<>();
        List<List<DroidSelector>> buttons = e.get(By.clickable(true).text(
            Pattern.compile(".*(([Ss]ign up)|([Rr]egister)|([Rr]egistrieren)|([Ll]ogin)).*")
        ));
        for (List<DroidSelector> list : buttons) {
          UiObject2 obj = e.find(list);
          if (obj != null) {
            e.click(obj);
            List<List<DroidSelector>> p = new ArrayList<>(path);
            p.add(list);
            Page result = new TextViewStrategy().search(id, p);
            if (result != null) {
              return result;
            } else {
              e.start();
            }
          }
        }
        finished = true;
      } catch (StaleObjectException | ArrayIndexOutOfBoundsException ignored) {
      }
    }
    return null;
  }

}
