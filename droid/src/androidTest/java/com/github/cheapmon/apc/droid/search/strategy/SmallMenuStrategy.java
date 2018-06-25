package com.github.cheapmon.apc.droid.search.strategy;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject2;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import com.github.cheapmon.apc.droid.extract.Page;
import com.github.cheapmon.apc.droid.search.SearchHelper;
import com.github.cheapmon.apc.droid.util.DroidException;
import com.github.cheapmon.apc.droid.util.DroidSelector;
import com.github.cheapmon.apc.droid.util.ExtractionHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Search an app for a policy by looking for a small menu.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com>cheapmon</a>
 */
public class SmallMenuStrategy implements SearchStrategy {

  /**
   * Helper for this class
   */
  private ExtractionHelper e;

  /**
   * Search policy
   *
   * @param id Application identification
   * @return Policy text
   */
  @Override
  public Page search(String id) throws DroidException {
    this.e = new ExtractionHelper(id);
    this.e.start();
    skipPanel();
    List<List<DroidSelector>> buttons = this.e.get(By.clazz(Button.class).clickable(true));
    buttons.addAll(this.e.get(By.clazz(ImageButton.class).clickable(true)));
    buttons.addAll(this.e.get(By.clazz(ImageView.class).clickable(true)));
    for (List<DroidSelector> list : buttons) {
      try {
        List<List<DroidSelector>> path = new ArrayList<>();
        this.e.start();
        skipPanel();
        UiObject2 obj = this.e.find(list);
        if (obj == null) {
          continue;
        }
        path.add(list);
        this.e.click(obj);
        if (!this.e.getActivityName().startsWith(id)) {
          continue;
        }
        obj = this.e.getRoot().findObject(
            By.clazz(FrameLayout.class)
                .hasChild(By.clazz(FrameLayout.class).hasChild(By.clazz(ListView.class)))
        );
        if (obj == null) {
          continue;
        }
        Page page;
        obj = obj.findObject(By.text(Pattern.compile(".*[Dd]aten.*")));
        if (obj == null) {
          this.e.start(path);
          obj = this.e.getRoot().findObject(By.text(Pattern.compile(".*[Hh]ilfe|[Üü]ber.*")));
          if (obj == null) {
            continue;
          }
          this.e.click(obj);
          obj = this.e.getRoot().findObject(By.text(Pattern.compile(".*[Dd]aten.*")));
          if (obj == null) {
            continue;
          }
          this.e.click(obj);
          this.e.waitForUpdate();
          page = this.e.getPageFromAnyApp();
          if (SearchHelper.isPolicy(page.dumpText())) {
            return page;
          }
        } else {
          this.e.click(obj);
          page = this.e.getPageFromAnyApp();
          if (SearchHelper.isPolicy(page.dumpText())) {
            return page;
          }
        }
      } catch (IndexOutOfBoundsException | StaleObjectException | NullPointerException ignored) {
      }
    }
    return null;
  }

  /**
   * Skip popup panels while searching.
   */
  private void skipPanel() {
    try {
      BySelector parentPanel = By.res(Pattern.compile(".*:id\\/parentPanel"));
      if (this.e.getRoot().hasObject(parentPanel)) {
        UiObject2 button = this.e.getRoot().findObject(parentPanel)
            .findObject(By.clazz(Button.class));
        if (button != null) {
          this.e.click(button);
        }
      }
    } catch (StaleObjectException ignored) {
    }
  }

}
