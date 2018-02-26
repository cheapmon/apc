package com.github.cheapmon.apc.droid.extract;

import android.graphics.Rect;
import java.util.List;

/**
 * Represent a piece of layout data.<br><br>
 *
 * Internal representation of an apps single layout. Every static view is captured. Additionally,
 * all views inside an scrolling container are (ideally) collected into one large container.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class Page {

  /**
   * Path to this page. List of coordinates of view clicked to reach this page.
   */
  private List<Rect> path;

}
