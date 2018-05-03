package com.github.cheapmon.apc.droid.extract;

import android.graphics.Rect;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;
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
  public Model getModel() {
    this.e.start();
    Queue<Page> pages = new LinkedList<>();
    Model model = new Model(this.id);
    Page page = this.e.getPage();
    pages.add(page);
    outer:
    while (pages.size() > 0) {
      page = pages.remove();
      this.e.start(page.getPath());
      int count = this.e.getStaticClickable().size();
      for (int i = 0; i < count; i++) {
        this.e.start(page.getPath());
        try {
          UiObject2 clickView = this.e.getStaticClickable().get(i);
          Rect rect = clickView.getVisibleBounds();
          clickView.click();
          this.e.waitForUpdate();
          Page newPage = this.e.getPage();
          boolean isNew = model.add(newPage, this.e.getActivityName());
          if (isNew) {
            newPage.addToPath(page, rect);
            pages.add(newPage);
          }
        } catch (Exception ex) {
          Log.e("DroidMain", "Something went wrong.", ex);
          continue outer;
        }
      }
    }
    return model;
  }

}
