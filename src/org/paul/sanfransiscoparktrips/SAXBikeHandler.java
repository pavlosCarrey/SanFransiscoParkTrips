package org.paul.sanfransiscoparktrips;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXBikeHandler extends DefaultHandler {
	private HashMap<String, Bicycle> bikes;
	private String currentElement = "";
	private Bicycle bike;
	private StringBuilder currentText;
	private boolean isBike = false;
	
	public HashMap<String, Bicycle> getBikeInfo(InputSource is) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		
		parser.parse(is, this);
		return bikes;
	}
	
	@Override
	public void startDocument() throws SAXException {
		bikes = new HashMap<String, Bicycle>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		currentElement = qName;
		if(currentElement.equals("row") && attributes.getLength() != 0) {
			bike = new Bicycle();
			isBike = true;		
		}
		else if(currentElement.equals(Bicycle.LATITUDE)) {
			String latString = attributes.getValue("latitude");
			String lngString = attributes.getValue("longitude");
			bike.setLatitude(Double.parseDouble(latString));
			bike.setLongitude(Double.parseDouble(lngString));
			bikes.put(bike.getLocation(), bike);
		}
		else {
			currentText = new StringBuilder();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(currentElement.equals("response") || currentElement.equals("row") || isBike == false) {
			return;
		}
		
		String content = currentText.toString().replaceAll("\\s+$", "").replaceAll("\n", "");
		
		if (currentElement.equals(Bicycle.ADDRESS)) {
			bike.setAddress(content);
		}
		else if (currentElement.equals(Bicycle.LOCATION)) {
			bike.setLocation(content);
		}
		else if (currentElement.equals(Bicycle.RACKS)) {
			bike.setRacks(Integer.parseInt(content));
		}
		else if (currentElement.equals(Bicycle.SPACES)) {
			bike.setSpaces(Integer.parseInt(content));
		}
		currentElement = "";
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(currentText != null) {
			currentText.append(ch, start, length);
		}
	}
}
