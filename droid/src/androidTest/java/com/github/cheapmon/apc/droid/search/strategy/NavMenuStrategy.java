package com.github.cheapmon.apc.droid.search.strategy;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject2;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.github.cheapmon.apc.droid.extract.Page;
import com.github.cheapmon.apc.droid.search.SearchHelper;
import com.github.cheapmon.apc.droid.util.DroidException;
import com.github.cheapmon.apc.droid.util.DroidSelector;
import com.github.cheapmon.apc.droid.util.ExtractionHelper;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Search an app for a policy by searching keywords in the navigation menu.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com>cheapmon</a>
 */
public class NavMenuStrategy implements SearchStrategy {

  /**
   * Extraction helper for this class
   */
  private ExtractionHelper e;

  /**
   * Search policy.
   *
   * @param id Application identification
   * @return Policy text
   * @throws DroidException Reading activity name failed
   */
  @Override
  public Page search(String id) throws DroidException {
    this.e = new ExtractionHelper(id);
    this.e.start();
    LinkedList<Page> pages = new LinkedList<>();
    this.findButtonsOnFirstPage(pages);
    return this.searchByKeywords(pages, id);
  }

  /**
   * Find button views on first page of app and add new pages to model.
   *
   * @param pages List to add new pages to
   */
  private void findButtonsOnFirstPage(LinkedList<Page> pages) {
    List<UiObject2> buttons = this.e.getRoot().findObjects(By.clazz(Button.class));
    buttons.addAll(this.e.getRoot().findObjects(By.clazz(ImageButton.class)));
    buttons.addAll(this.e.getRoot().findObjects(By.clazz(ImageView.class)));
    List<List<DroidSelector>> buttonSelectors = new ArrayList<>();
    for (UiObject2 obj : buttons) {
      buttonSelectors.add(this.e.getSelector(obj, 0));
    }
    for (List<DroidSelector> selector : buttonSelectors) {
      try {
        this.e.start();
        UiObject2 obj = this.e.find(selector);
        if (obj == null) {
          continue;
        }
        obj.click();
        Page newPage = this.e.getPageFromAnyApp();
        newPage.addToPath(newPage, selector);
        pages.add(newPage);
      } catch (IndexOutOfBoundsException | StaleObjectException | NullPointerException ignored) {
      }
    }
  }

  /**
   * Search app for clickable views containing certain keywords.
   *
   * @param pages List of pages to start from
   * @param id Application identification
   * @return Page containing policy
   */
  private Page searchByKeywords(LinkedList<Page> pages, String id)
      throws DroidException {
    while (pages.size() > 0) {
      Page page = pages.remove(0);
      try {
        this.e.start(page.getPath());
      } catch (IndexOutOfBoundsException | NullPointerException | StaleObjectException ex) {
        continue;
      }
      List<List<DroidSelector>> list = this.getNavViews();
      for (List<DroidSelector> d : list) {
        try {
          this.e.start(page.getPath());
          UiObject2 clickView = this.e.find(d);
          this.e.click(clickView);
          Page newPage = this.e.getPageFromAnyApp();
          if (SearchHelper.isPolicy(newPage.dumpText())) {
            return newPage;
          }
          if (!this.e.getActivityName().startsWith(id) || page.getPath().size() > 4) {
            continue;
          }
          newPage.addToPath(page, d);
          if (newPage.dumpText().toLowerCase().contains("datenschutz")) {
            pages.addFirst(newPage);
          } else {
            pages.addLast(newPage);
          }
        } catch (IndexOutOfBoundsException | NullPointerException | StaleObjectException ignored) {
        }
      }
    }
    return null;
  }

  /**
   * Get clickable views containing navigation keywords in current layout.
   *
   * @return View selectors
   */
  private List<List<DroidSelector>> getNavViews() {
    final int SCROLL_MAX = 3;
    List<List<DroidSelector>> list = new ArrayList<>();
    List<UiObject2> foundObjects = this.e.getRoot().findObjects(
        By.clickable(true).text(SearchHelper.NAVIGATION_REGEX)
    );
    foundObjects.addAll(this.e.getRoot().findObjects(
        By.clickable(true).hasDescendant(By.text(SearchHelper.NAVIGATION_REGEX))
    ));
    for (UiObject2 obj : foundObjects) {
      list.add(this.e.getSelector(obj, 0));
    }
    List<UiObject2> scrollContainer = this.e.getRoot().findObjects(By.scrollable(true));
    for (UiObject2 cont : scrollContainer) {
      try {
        for (int i = 0; i < SCROLL_MAX; i++) {
          boolean canScroll = cont.scroll(Direction.DOWN, 1);
          if (!canScroll) {
            break;
          }
          foundObjects = this.e.getRoot().findObjects(
              By.clickable(true).text(SearchHelper.NAVIGATION_REGEX)
          );
          foundObjects.addAll(this.e.getRoot().findObjects(
              By.clickable(true).hasDescendant(By.text(SearchHelper.NAVIGATION_REGEX))
          ));
          for (UiObject2 obj : foundObjects) {
            list.add(this.e.getSelector(obj, i + 1));
          }
        }
      } catch (NullPointerException | StaleObjectException ignored) {
      }
    }
    return list;
  }

}
