package com.github.cheapmon.apc.droid;

import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.github.cheapmon.apc.droid.extract.Model;
import com.github.cheapmon.apc.droid.extract.ModelExtractor;
import com.github.cheapmon.apc.droid.install.GooglePlayWizard;
import com.github.cheapmon.apc.droid.install.GooglePlayWizard.InstallState;
import com.github.cheapmon.apc.droid.search.SearchAlgorithm;
import com.github.cheapmon.apc.droid.search.SearchHelper;
import com.github.cheapmon.apc.droid.util.DroidException;
import com.github.cheapmon.apc.droid.util.DroidLogger;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Run extraction on this device.<br><br>
 *
 * <ul>
 * <li>Install or remove Android app</li>
 * <li>Crawl interface of an app</li>
 * </ul>
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
@RunWith(AndroidJUnit4.class)
public class DroidMain {

  /**
   * Labels of Android apps
   */
  private String[] ids;

  /**
   * Extraction mode to use
   */
  private String mode;

  /**
   * Name of search algorithm to use in extraction
   */
  private String algorithm;

  /**
   * Entry point for Droid pipeline. Check for arguments. Configure extraction.
   */
  @Test
  public void main() throws DroidException {
    this.parseCommands();
    if (this.mode.equals("MODEL")) {
      for (String id : this.ids) {
        if (GooglePlayWizard.install(id) != InstallState.FAILURE) {
          Model model = new ModelExtractor(id).getModel();
          this.send(model.toXML(), id);
        }
        GooglePlayWizard.removeSilently(id);
      }
      this.send(null, null);
    } else {
      try {
        SearchAlgorithm algorithm = SearchHelper.get(this.algorithm).newInstance();
        for (String id : this.ids) {
          if (GooglePlayWizard.install(id) != InstallState.FAILURE) {
            String result = algorithm.run(id);
            if (result != null) {
              this.send(result, id);
            }
          }
          GooglePlayWizard.removeSilently(id);
        }
        this.send(null, null);
      } catch (InstantiationException | IllegalAccessException ex) {
        throw new DroidException("Running algorithm failed", ex);
      }
    }
  }

  /**
   * Parse arguments given to Droid.
   */
  private void parseCommands() throws DroidException {
    Bundle extras = InstrumentationRegistry.getArguments();
    try {
      this.ids = this.readIDFile(extras.getString("file"));
    } catch (FileNotFoundException ex) {
      throw new DroidException("Reading id file failed", ex);
    }
    this.mode = extras.getString("mode");
    this.algorithm = extras.getString("algorithm");
    DroidLogger.log("Droid");
    DroidLogger.space();
    DroidLogger.log("Found ids:");
    for (String id : this.ids) {
      DroidLogger.log(String.format("* %s", id));
    }
    DroidLogger.log(String.format("Extraction mode is %s", this.mode));
    DroidLogger.log(String.format("Using %s", this.algorithm));
    DroidLogger.space();
  }

  /**
   * Read ids from id file.
   *
   * @param pathToFile Path to file
   * @return Application IDs
   * @throws FileNotFoundException File read fails
   */
  private String[] readIDFile(String pathToFile) throws FileNotFoundException {
    Scanner scanner = new Scanner(new FileReader(pathToFile));
    StringBuilder builder = new StringBuilder();
    while (scanner.hasNext()) {
      builder.append(scanner.next()).append("\n");
    }
    scanner.close();
    return builder.toString().split("\n");
  }

  /**
   * Send extracted text or model to to host computer.
   *
   * @param txt Text
   * @param id Identification of app
   * @throws DroidException Sending fails
   */
  private void send(String txt, String id) throws DroidException {
    try {
      Socket s = new Socket("10.0.2.2", 2000);
      PrintWriter out = new PrintWriter(s.getOutputStream());
      if (txt == null && id == null) {
        out.print("\n");
        out.print("OK");
      } else {
        out.print(String.format("%s\n", id));
        out.print(txt);
        out.print("\n");
        out.print("---");
      }
      out.flush();
      out.close();
      s.close();
    } catch (IOException ex) {
      throw new DroidException("Sending txt failed", ex);
    }
  }

}
