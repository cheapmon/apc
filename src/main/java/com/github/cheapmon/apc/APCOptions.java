package com.github.cheapmon.apc;

import java.nio.file.Path;

/**
 * Save settings for extraction.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class APCOptions {

  /**
   * Mode of extraction. This will be either policy extraction or app model extraction
   */
  public enum ExtractionMode {
    POLICY, MODEL
  }

  /**
   * Search algorithms used by APC
   */
  public enum Algorithm {
    BFS, DFS, RS, OS
  }

  /**
   * List of applications to extract from
   */
  private Path file;

  /**
   * Extraction mode for APC
   */
  private ExtractionMode extractionMode;

  /**
   * Device extraction is run on
   */
  private String device;

  /**
   * Search algorithm used to extract
   */
  private Algorithm algorithm;

  /**
   * Whether to rebuild test files
   */
  private boolean rebuild;

  /**
   * Get path to file with application IDs.
   *
   * @return Path to file
   */
  public Path getFile() {
    return this.file;
  }

  /**
   * Set path to file with application IDs.
   *
   * @param file Path to file
   */
  public void setFile(Path file) {
    this.file = file;
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
   * Get algorithm in use.
   *
   * @return Chosen algorithm
   */
  public Algorithm getAlgorithm() {
    return this.algorithm;
  }

  /**
   * Set algorithm to use.
   *
   * @param algorithm New algorithm
   */
  public void setAlgorithm(Algorithm algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * Get whether to rebuild.
   *
   * @return Rebuild or not
   */
  public boolean getRebuild() {
    return this.rebuild;
  }

  /**
   * Set whether to rebuild.
   *
   * @param rebuild Rebuild or not
   */
  public void setRebuild(boolean rebuild) {
    this.rebuild = rebuild;
  }

}
