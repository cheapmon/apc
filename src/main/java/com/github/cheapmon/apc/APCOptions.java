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
   * Device extraction is run on.
   */
  private String device;

  /**
   * Path to search algorithm class used to extract.
   */
  private String algorithmPath;

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

  /**
   * Get device in use.
   *
   * @return Chosen device
   */
  public String getDevice() {
    return this.device;
  }

  /**
   * Set device to use.
   *
   * @param device New device
   */
  public void setDevice(String device) {
    this.device = device;
  }

  /**
   * Get path to search algorithm class in use.
   *
   * @return Path to chosen algorithm
   */
  public String getAlgorithmPath() {
    return this.algorithmPath;
  }

  /**
   * Set path to algorithm class to use.
   *
   * @param algorithmPath New path to algorithm class
   */
  public void setAlgorithmPath(String algorithmPath) {
    this.algorithmPath = algorithmPath;
  }

}
