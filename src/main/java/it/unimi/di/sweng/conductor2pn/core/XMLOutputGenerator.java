package it.unimi.di.sweng.conductor2pn.core;

import it.unimi.di.sweng.conductor2pn.data.Arc;
import it.unimi.di.sweng.conductor2pn.data.Place;
import it.unimi.di.sweng.conductor2pn.data.TBNet;
import it.unimi.di.sweng.conductor2pn.data.Transition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.Writer;


public class XMLOutputGenerator {

    private static final String PNML = "pnml";
    private static final String NET = "net";
    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String CONSTRAINT = "constraint";
    private static final String DEFAULT_CONSTRAINT = "T0>=0";
    private static final String VALUE = "value";
    private static final String PLACE = "place";
    private static final String NAME = "name";
    private static final String INITIAL_MARKING = "initialMarking";
    private static final String SYMBOLIC_TIMESTAMPS = "symTimes";
    private static final String CAPACITY = "capacity";
    private static final String TRANSITION = "transition";
    private static final String RATE = "rate";
    private static final String TIMED = "timed";
    private static final String SEMANTICS = "semantic";
    private static final String MIN_TIME = "tmin";
    private static final String MAX_TIME = "tmax";
    private static final String INFINITE_SERVER = "infiniteServer";
    private static final String PRIORITY = "priority";
    private static final String ARC = "arc";
    private static final String INSCRIPTION = "inscription";
    private static final String TAGGED = "tagged";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String SOURCE = "source";
    private static final String TARGET = "target";

    private TBNet model;

    public XMLOutputGenerator(TBNet model) {
        this.model = model;
    }

    public void generate(Writer writer) throws ParserConfigurationException, TransformerException, TransformerConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element root = doc.createElement(PNML);
        doc.appendChild(root);

        Element net = doc.createElement(NET);
        net.setAttribute(ID, "Net-One");
        net.setAttribute(TYPE, "P/T net");
        root.appendChild(net);

        Element constraint = doc.createElement(CONSTRAINT);
        appendValue(doc, constraint, DEFAULT_CONSTRAINT);
        net.appendChild(constraint);

        for(Place p: model.getPlaces()) {
            Element placeElement = doc.createElement(PLACE);
            placeElement.setAttribute(ID, p.getName());

            appendElement(doc, placeElement, NAME, p.getName());
            Element initialMarking = appendElement(doc, placeElement, INITIAL_MARKING, Integer.toString(p.getTokens().size()));
            appendElement(doc, initialMarking, SYMBOLIC_TIMESTAMPS, p.getTokensAsString());
            appendElement(doc, placeElement, CAPACITY, "0");

            net.appendChild(placeElement);
        }

        for(Transition t: model.getTransitions()) {
            Element transitionElement = doc.createElement(TRANSITION);
            transitionElement.setAttribute(ID, t.getName());

            appendElement(doc, transitionElement, NAME, t.getName());
            appendElement(doc, transitionElement, RATE, "1.0");
            appendElement(doc, transitionElement, TIMED, TRUE);
            appendElement(doc, transitionElement, SEMANTICS, t.getSemanticsAsString());
            appendElement(doc, transitionElement, MIN_TIME, t.getMinTime());
            appendElement(doc, transitionElement, MAX_TIME, t.getMaxTime());
            appendElement(doc, transitionElement, INFINITE_SERVER, FALSE);
            appendElement(doc, transitionElement, PRIORITY, "1");

            net.appendChild(transitionElement);
        }

        for(Arc a: model.getArcs()) {
            Element arcElement = doc.createElement(ARC);
            arcElement.setAttribute(ID, a.getSource().getName() + " to " + a.getTarget().getName());
            arcElement.setAttribute(SOURCE, a.getSource().getName());
            arcElement.setAttribute(TARGET, a.getTarget().getName());

            appendElement(doc, arcElement, INSCRIPTION, "1");
            appendElement(doc, arcElement, TAGGED, FALSE);

            net.appendChild(arcElement);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        transformer.transform(source, new StreamResult(writer));
    }

    private Element appendElement(Document doc, Element father, String elementName, String valueText) {
        Element element = doc.createElement(elementName);
        appendValue(doc, element, valueText);
        father.appendChild(element);
        return element;
    }

    private void appendValue(Document doc, Element father, String valueText) {
        Element value = doc.createElement(VALUE);
        value.appendChild(doc.createTextNode(valueText));
        father.appendChild(value);
    }
}
