/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp.elmo;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.lang.model.element.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 *
 * @author salum
 */
public class ElmoParser {

    private Document document;

    public ElmoParser(String elmo) {
        //Get the DOM Builder Factory

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //Get the DOM Builder
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();

            InputSource s = new InputSource(new StringReader(elmo));

            //Load and Parse the XML document
            //document contains the complete XML as a Tree.
            document = builder.parse(s);

            NodeList learnings = document.getElementsByTagName("learningOpportunitySpecification");
            for (int i = 0; i < learnings.getLength(); i++) {
                Element identifier = document.createElement("indentifier");
                identifier.setAttribute("type", "elmo");
                identifier.setTextContent( String.valueOf(i));
                Element e = (Element) learnings.item(i);
                e.appendChild(identifier);
            }

        } catch (Exception ex) {
            Logger.getLogger(ElmoParser.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        }

    }

    public String getCourseData() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        org.w3c.dom.Element rootElement = doc.createElement("elmo");
        Node learner;
        NodeList learners = document.getElementsByTagName("learner");
        learner = doc.importNode(learners.item(0), true);
        rootElement.appendChild(learner);
        NodeList learnings = document.getElementsByTagName("learningOpportunitySpecification");
        for (int i = 0; i < learnings.getLength(); i++) {
            Node newNode = doc.importNode(learnings.item(i), true);
            rootElement.appendChild(newNode);
        }

        doc.appendChild(rootElement);
        return getStringFromDoc(doc);

    }

    private String getStringFromDoc(org.w3c.dom.Document doc) {
        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        return lsSerializer.writeToString(doc);
    }
}
