package com.github.cheapmon.apc.droid.search.strategy;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject2;
import android.view.View;
import android.widget.TextView;
import com.github.cheapmon.apc.droid.extract.Page;
import com.github.cheapmon.apc.droid.search.SearchHelper;
import com.github.cheapmon.apc.droid.util.DroidException;
import com.github.cheapmon.apc.droid.util.DroidSelector;
import com.github.cheapmon.apc.droid.util.ExtractionHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Look for keyword on first page.<br><br>
 *
 * If such object with this keyword exists, click on it. Return if policy is found.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com>cheapmon</a>
 */
public class TextViewStrategy implements SearchStrategy {

  /**
   * Path to page to begin search from
   */
  private List<List<DroidSelector>> path = new ArrayList<>();

  /**
   * Search for keywords and click.
   *
   * @param id Application identification
   * @return Policy page
   */
  @Override
  public Page search(String id) throws DroidException {
    ExtractionHelper e = new ExtractionHelper(id);
    e.start(this.path);
    try {
      UiObject2 obj = e.getRoot().findObject(
          By.clazz(TextView.class).clickable(true).text(SearchHelper.NAVIGATION_REGEX)
      );
      if (obj != null) {
        e.click(obj);
        obj = e.getRoot().findObject(
            By.clazz(View.class).clickable(true).text(Pattern.compile(".*(Daten|Privacy).*"))
        );
        if (obj != null) {
          e.click(obj);
        }
        Page page = new Page(e.getRoot());
        if (SearchHelper.isPolicy(page.dumpText())) {
          return page;
        } else {
          e.start(this.path);
        }
      }
    } catch (IndexOutOfBoundsException | StaleObjectException | NullPointerException ignored) {
    }
    return null;
  }

  /**
   * Search keywords, starting from a certain beginning point.
   *
   * @param id Application identification
   * @param path Path to beginning point
   * @return Policy page
   */
  public Page search(String id, List<List<DroidSelector>> path) throws DroidException {
    this.path = path;
    return this.search(id);
  }

}
