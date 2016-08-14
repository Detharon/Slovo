package com.dth.service;

import com.dth.entity.WordOccurrence;
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

public class XmlExportWords implements ExportWords {

    private final StreamResult streamResult;

    public XmlExportWords(StreamResult streamResult) throws ParserConfigurationException {
        this.streamResult = streamResult;
    }

    @Override
    public void export(List<WordOccurrence> words) throws ExportFailedException {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root element (words)
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("words");
            doc.appendChild(rootElement);

            int id = 1;
            for (WordOccurrence word : words) {
                // word occurrence element
                Element wordOccurrenceElement = doc.createElement("wordOccurrence");
                wordOccurrenceElement.setAttribute("id", String.valueOf(id));
                rootElement.appendChild(wordOccurrenceElement);

                // word element
                Element wordElement = doc.createElement("word");
                wordElement.appendChild(doc.createTextNode(word.getWord()));
                wordOccurrenceElement.appendChild(wordElement);

                // count element
                Element countElement = doc.createElement("count");
                countElement.appendChild(doc.createTextNode(String.valueOf(word.getCount())));
                wordOccurrenceElement.appendChild(countElement);

                id++;
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            transformer.transform(source, streamResult);
        } catch (ParserConfigurationException | TransformerException ex) {
            throw new ExportFailedException(ex);
        } 
    }
}
