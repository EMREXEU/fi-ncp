/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp.elmo;

import java.io.StringReader;
import java.io.StringWriter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import org.xml.sax.InputSource;
/**
 * A class representing a single Elmo xml.
 * @author salum
 */
public class ElmoParser {

    private Document document;
/**
 * Creates a dom model of elmo xml and adds elmo identifiers to courses
 * @param elmo 
 */
    public ElmoParser(String elmo) {
        //Get the DOM Builder Factory

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //Get the DOM Builder
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            StringReader sr =new StringReader(elmo);
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

        } catch (Exception ex) {
            Logger.getLogger(ElmoParser.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        }

    }
    /**
     * Complete XML of found Elmo
     * @return String representation of Elmo-xml
     * @throws ParserConfigurationException 
     */
    public String getCourseData() throws ParserConfigurationException {
        return getStringFromDoc(document);
    }
/**
 * Elmo with a learning instance selection
 * @param courses
 * @return String representation of Elmo-xml with selected courses
 * @throws ParserConfigurationException 
 */
    public String getCourseData(List<String> courses) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("elmo");
        //NodeList learners = document.getElementsByTagName("learner");
        NodeList reports = document.getDocumentElement().getElementsByTagName("report");
        for (int k = 0; k < reports.getLength(); k++) {
            Element report = doc.createElement("report");
            NodeList children = reports.item(k).getChildNodes();
     
            for (int i = 0; i < children.getLength(); i++) {
                Node item = children.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    if (item.getNodeName() != "learningOpportunitySpecification") {
                        Node newNode = doc.importNode(children.item(i), true);
                        report.appendChild(newNode);
                    }
                }
            }
            
            NodeList learnings = document.getElementsByTagName("learningOpportunitySpecification");
            for (int i = 0; i < learnings.getLength(); i++) {
                Element specification = (Element) learnings.item(i);
                NodeList identifiers = specification.getElementsByTagName("identifier");
                for (int j = 0; j < identifiers.getLength(); j++) {
                    Element id = (Element) identifiers.item(j);
                    if (id.getParentNode() == specification) {
                        if (id.hasAttribute("type") && id.getAttribute("type").equals("elmo")) {
                            if (courses.contains(id.getTextContent())) {
                                Node newNode = doc.importNode(learnings.item(i), true);
                                report.appendChild(newNode);
                            }

                        }
                    }
                }
            }
            rootElement.appendChild(report);
        }
        doc.appendChild(rootElement);
        return getStringFromDoc(doc);

    }

    private String getStringFromDoc(org.w3c.dom.Document doc) {
        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        return lsSerializer.writeToString(doc);
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
            System.out.println(e);
        }
        return "No Node";
    }

}
