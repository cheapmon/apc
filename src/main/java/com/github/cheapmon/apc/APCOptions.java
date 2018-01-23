package com.github.cheapmon.apc;

/**
 * Save settings for extraction.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class APCOptions {

  /**
   * List of applications to extract from.
   */
  private String[] ids;

  /**
   * Get all application IDs.
   *
   * @return IDs
   */
  public String[] getIds() {
    return this.ids;
  }

  /**
   * Set application IDs.
   *
   * @param ids New IDs
   */
  public void setIds(String[] ids) {
    this.ids = ids;
  }

}
