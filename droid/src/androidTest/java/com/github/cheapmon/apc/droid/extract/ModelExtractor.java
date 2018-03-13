package com.github.cheapmon.apc.droid.extract;

import android.graphics.Rect;
import android.support.test.uiautomator.UiObject2;
import android.text.TextUtils;
import com.github.cheapmon.apc.droid.util.DroidException;
import com.github.cheapmon.apc.droid.util.DroidLogger;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Extract a model of an app.<br><br>
 *
 * <ul>
 * <li>Go through app</li>
 * <li>Collect internal representation of layout</li>
 * <li>Stop at certain threshold</li>
 * </ul>
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class ModelExtractor {

  /**
   * Application to extract from
   */
  private final String id;

  /**
   * Helper for extraction
   */
  private final ExtractionHelper e;

  /**
   * Extract model for single application.
   *
   * @param id Application id
   */
  public ModelExtractor(String id) {
    this.id = id;
    this.e = new ExtractionHelper(id);
  }

  /**
   * Extract model.
   *
   * @return Extracted Model
   */
  public Model getModel() throws DroidException {
    e.start();
    Queue<Page> pages = new LinkedList<>();
    Model model = new Model();
    Page page = e.getPage();
    pages.add(page);
    outer:
    while (pages.size() > 0) {
      page = pages.remove();
      e.start(page.getPath());
      DroidLogger.log(TextUtils.join(", ", page.getPath()));
      DroidLogger.log(page.dumpText());
      int count = e.getStaticClickable().size();
      for (int i = 0; i < count; i++) {
        e.start(page.getPath());
        try {
          UiObject2 clickView = e.getStaticClickable().get(i);
          Rect rect = clickView.getVisibleBounds();
          clickView.click();
          e.waitForUpdate();
          Page newPage = e.getPage();
          boolean isNew = model.add(newPage, e.getActivityName());
          if (isNew && !e.getActivityName().startsWith(id)) {
            newPage.addToPath(page, rect);
            pages.add(newPage);
          }
        } catch (IndexOutOfBoundsException ex) {
          DroidLogger.log(ex.getMessage());
          continue outer;
        }
      }
    }
    return model;
  }

}
