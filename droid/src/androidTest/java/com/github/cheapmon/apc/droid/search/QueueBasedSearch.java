package com.github.cheapmon.apc.droid.search;

import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject2;
import com.github.cheapmon.apc.droid.extract.Model;
import com.github.cheapmon.apc.droid.extract.Page;
import com.github.cheapmon.apc.droid.util.DroidException;
import com.github.cheapmon.apc.droid.util.DroidSelector;
import com.github.cheapmon.apc.droid.util.ExtractionHelper;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Search for policy in app based on queue.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com>cheapmon</a>
 */
abstract class QueueBasedSearch implements SearchAlgorithm {

  /**
   * List of pages to check in search
   */
  final LinkedList<Page> pages = new LinkedList<>();

  /**
   * Search for policy.
   *
   * @param id Application identification
   * @return Policy text
   */
  @Override
  public String run(String id) throws DroidException {
    ExtractionHelper e = new ExtractionHelper(id);
    e.start();
    Model model = new Model(id, e.getDisplayBounds());
    Page page = e.getPage();
    this.pages.add(page);
    while (this.pages.size() > 0) {
      page = this.pages.remove();
      try {
        e.start(page.getPath());
      } catch (IndexOutOfBoundsException | NullPointerException | StaleObjectException ex) {
        continue;
      }
      List<List<DroidSelector>> list = e.getClickable();
      List<Page> newPages = new ArrayList<>(list.size());
      for (List<DroidSelector> d : list) {
        try {
          e.start(page.getPath());
          UiObject2 clickView = e.find(d);
          e.click(clickView);
          Page newPage = e.getPage();
          String txt = newPage.dumpText();
          if (SearchHelper.isPolicy(txt)) {
            return txt;
          }
          newPage.addToPath(page, d);
          boolean isNew;
          try {
            isNew = model.add(newPage, e.getActivityName());
          } catch (DroidException ex) {
            continue;
          }
          if (isNew) {
            newPages.add(newPage);
          }
        } catch (IndexOutOfBoundsException | NullPointerException | StaleObjectException ignored) {
        }
      }
      this.addToQueue(newPages);
    }
    return null;
  }

  /**
   * Add new pages to queue.
   *
   * @param list New pages
   */
  protected abstract void addToQueue(List<Page> list);

}
