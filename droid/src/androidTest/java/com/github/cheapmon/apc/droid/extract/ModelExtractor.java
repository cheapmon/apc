package com.github.cheapmon.apc.droid.extract;

import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject2;
import com.github.cheapmon.apc.droid.util.DroidException;
import com.github.cheapmon.apc.droid.util.DroidSelector;
import com.github.cheapmon.apc.droid.util.ExtractionHelper;
import java.util.LinkedList;
import java.util.List;
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
    while (pages.size() > 0) {
      page = pages.remove();
      try {
        this.e.start(page.getPath());
      } catch (IndexOutOfBoundsException | NullPointerException | StaleObjectException ex) {
        continue;
      }
      List<List<DroidSelector>> list = this.e.getClickable();
      for (List<DroidSelector> d : list) {
        try {
          this.e.start(page.getPath());
          UiObject2 clickView = this.e.find(d);
          clickView.click();
          this.e.waitForUpdate();
          Page newPage = this.e.getPage();
          newPage.addToPath(page, d);
          boolean isNew;
          try {
            isNew = model.add(newPage, this.e.getActivityName());
          } catch (DroidException ex) {
            continue;
          }
          if (isNew) {
            pages.add(newPage);
          }
        } catch (IndexOutOfBoundsException | NullPointerException | StaleObjectException ignored) {
        }
      }
    }
    return model;
  }
}


