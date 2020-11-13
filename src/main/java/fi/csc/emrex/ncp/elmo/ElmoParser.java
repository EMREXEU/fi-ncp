/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp.elmo;

import fi.csc.emrex.ncp.execption.NpcException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A class representing a single Elmo xml.
 *
 * @author salum
 */
@Slf4j
public class ElmoParser {

  private Document document;

  /**
   * Creates a dom model of elmo xml and adds elmo identifiers to courses
   */
  public ElmoParser(String elmo) {
    //Get the DOM Builder Factory

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //Get the DOM Builder
    DocumentBuilder builder;
    try {
      builder = factory.newDocumentBuilder();
      StringReader sr = new StringReader(elmo);
      InputSource s = new InputSource(sr);

      //Load and Parse the XML document
      //document contains the complete XML as a Tree.
      document = builder.parse(s);

      NodeList learnings = document.getElementsByTagName("learningOpportunitySpecification");
      for (int i = 0; i < learnings.getLength(); i++) {
        Element identifier = document.createElement("identifier");
        identifier.setAttribute("type", "elmo");
        identifier.setTextContent(String.valueOf(i));
        Element e = (Element) learnings.item(i);
        e.appendChild(identifier);
      }
      document.normalizeDocument();

    } catch (Exception ex) {
      Logger.getLogger(ElmoParser.class.getName()).log(Level.SEVERE, null, ex);
      log.info(ex.getMessage());
    }

  }

  /**
   * Complete XML of found Elmo
   *
   * @return String representation of Elmo-xml
   */
  public String getCourseData() {
    return getStringFromDoc(document);
  }

  /**
   * Elmo with a learning instance selection removes all learning opportunities not selected even if
   * a learning opprtunity has a child that is among the selected courses.
   *
   * @return String representation of Elmo-xml with selected courses
   */
  public String getCourseData(List<String> courses) throws NpcException {
    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      String copyElmo = this.getStringFromDoc(document);

      StringReader sr = new StringReader(copyElmo);
      InputSource s = new InputSource(sr);
      Document doc = docBuilder.parse(s);

      NodeList learnings = doc.getElementsByTagName("learningOpportunitySpecification");
      List<Node> removeNodes = new ArrayList<>();
      for (int i = 0; i < learnings.getLength(); i++) {
        Element specification = (Element) learnings.item(i);
        NodeList identifiers = specification.getElementsByTagName("identifier");
        for (int j = 0; j < identifiers.getLength(); j++) {
          Element id = (Element) identifiers.item(j);
          if (id.getParentNode() == specification) {
            if (id.hasAttribute("type") && id.getAttribute("type").equals("elmo")) {
              if (!courses.contains(id.getTextContent())) {
                removeNodes.add(specification);
              }
            }
          }
        }
      }
      for (Node remove : removeNodes) {
        Node parent = remove.getParentNode();
        if (parent != null) {
          parent.removeChild(remove);
        }
      }
      NodeList reports = doc.getElementsByTagName("report");
      for (int i = 0; i < reports.getLength(); i++) {
        Element report = (Element) reports.item(i);
        log.info("report " + i);
        NodeList learnList = report.getElementsByTagName("learningOpportunitySpecification");
        if (learnList.getLength() < 1) {
          log.info("report empty");
          report.getParentNode().removeChild(report);
        }
      }
      return getStringFromDoc(doc);

    } catch (SAXException | IOException | ParserConfigurationException ex) {
      throw new NpcException("Parsing course data failed", ex);
    }
  }

  private String getStringFromDoc(org.w3c.dom.Document doc) {
    DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
    LSSerializer lsSerializer = domImplementation.createLSSerializer();

    LSOutput lsOutput = domImplementation.createLSOutput();
    lsOutput.setEncoding(StandardCharsets.UTF_8.name());
    Writer stringWriter = new StringWriter();
    lsOutput.setCharacterStream(stringWriter);
    lsSerializer.write(doc, lsOutput);
    return stringWriter.toString();

//        return lsSerializer.writeToString(doc);
  }

  // just for testing
  private String getNodeString(Node node) {
    StringWriter writer = new StringWriter();
    try {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.transform(new DOMSource(node), new StreamResult(writer));
      String xml = writer.toString();
      return xml;
    } catch (Exception e) {
      log.error("Parsing note to String failed.", e);
    }
    return "No Node";
  }

}
