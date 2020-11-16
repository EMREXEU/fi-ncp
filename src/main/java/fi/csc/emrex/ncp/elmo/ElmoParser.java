/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp.elmo;

import fi.csc.emrex.ncp.execption.NpcException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
  public String getAllCourseData() {
    return XmlUtil.getStringFromDoc(document);
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
      String copyElmo = XmlUtil.getStringFromDoc(document);

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
      return XmlUtil.getStringFromDoc(doc);

    } catch (SAXException | IOException | ParserConfigurationException ex) {
      throw new NpcException("Parsing course data failed", ex);
    }
  }

}
