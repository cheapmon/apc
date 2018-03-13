package com.github.cheapmon.apc.droid.extract;

import android.support.test.uiautomator.StaleObjectException;
import com.github.cheapmon.apc.droid.util.DroidException;

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
    Model model = new Model(e.getPage(), e.getActivityName());
    int count = e.getStaticClickable().size();
    for (int i = 0; i < count; i++) {
      try {
        e.getStaticClickable().get(i).click();
        e.waitForUpdate();
        model.add(e.getPage(), e.getActivityName());
      } catch (StaleObjectException ex) {
        i--;
      }
      e.start();
    }
    return null;
  }

}
