package com.kozzztya.cycletraining.utils;

import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class XMLParser {
    //Returns the entire XML document
    public Document getDocument(InputStream inputStream) {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(inputStream);
            document = builder.parse(inputSource);
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        return document;
    }

}
