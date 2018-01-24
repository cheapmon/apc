package com.github.cheapmon.apc;

/**
 * Save settings for extraction.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class APCOptions {

  /**
   * Mode of extraction. This will be either policy extraction or app model extraction.
   */
  public enum ExtractionMode {
    POLICY, MODEL
  }

  /**
   * List of applications to extract from.
   */
  private String[] ids;

  /**
   * Extraction mode for APC.
   */
  private ExtractionMode extractionMode;

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

  /**
   * Get the mode of extraction.
   *
   * @return Mode of extraction
   */
  public ExtractionMode getExtractionMode() {
    return this.extractionMode;
  }

  /**
   * Set the mode of extraction.
   *
   * @param extractionMode New mode
   */
  public void setExtractionMode(ExtractionMode extractionMode) {
    this.extractionMode = extractionMode;
  }

}
