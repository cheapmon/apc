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

public class DepthFirstSearch implements SearchAlgorithm {

  @Override
  public String run(String id) {
    ExtractionHelper e = new ExtractionHelper(id);
    e.start();
    LinkedList<Page> pages = new LinkedList<>();
    Model model = new Model(id, e.getDisplayBounds());
    Page page = e.getPage();
    pages.add(page);
    while (pages.size() > 0) {
      page = pages.remove();
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
          clickView.click();
          e.waitForUpdate();
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
      for(int i=newPages.size()-1; i>=0; i--) {
        pages.addFirst(newPages.get(i));
      }
    }
    return null;

  }
}
