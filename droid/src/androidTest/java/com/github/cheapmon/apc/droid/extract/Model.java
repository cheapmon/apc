package com.github.cheapmon.apc.droid.extract;

import android.graphics.Rect;
import com.github.cheapmon.apc.droid.util.DroidException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Model of an app.<br><br>
 *
 * This model is tree-like. Every node corresponds to an activity and its possible layouts and links
 * to other activities reachable by clicking. Every layout is described in its text contents, object
 * hierarchy etc.
 *
 * @author <a href="mailto:simon.kaleschke.leipzig@gmail.com">cheapmon</a>
 */
public class Model {

  /**
   * Application identification this model belongs to
   */
  private final String id;

  /**
   * All nodes of this model
   */
  private final List<ModelNode> nodes;

  /**
   * Display bounds of model
   */
  private final Rect bounds;

  /**
   * Display bounds of model
   */
  private Rect bounds;

  /**
   * Instantiate new model from scratch.
   *
   * @param id App identification model belongs to
   * @param bounds Display bounds of model
   */
  public Model(String id, Rect bounds) {
    this.id = id;
    this.bounds = bounds;
    this.nodes = new ArrayList<>();
  }

  /**
   * Add single page to model.
   *
   * @param page Page to add
   * @param activityName Activity the page belongs to
   */
  public boolean add(Page page, String activityName) {
    for (ModelNode node : this.nodes) {
      if (node.add(page, activityName)) {
        return false;
      }
    }
    this.nodes.add(new ModelNode(page, activityName));
    return true;
  }

  /**
   * Convert model to string containing its XML representation.
   *
   * @return XML String
   * @throws DroidException Transformation fails
   */
  public String toXML() throws DroidException {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document document = documentBuilder.newDocument();
      Element root = document.createElement("model");
      root.setAttribute("id", this.id);
      root.setAttribute("bounds", this.bounds.toShortString());
      document.appendChild(root);
      for (ModelNode node : this.nodes) {
        root.appendChild(node.toElement(document));
      }
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(document);
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.transform(source, result);
      return writer.toString();
    } catch (ParserConfigurationException | TransformerException ex) {
      throw new DroidException("XML transformation failed", ex);
    }
  }

}
