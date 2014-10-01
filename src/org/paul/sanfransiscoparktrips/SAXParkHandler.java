package org.paul.sanfransiscoparktrips;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParkHandler extends DefaultHandler {
	private HashMap<String, Park> parks;
	private String currentElement = "";
	private Park park;
	private StringBuilder currentText;
	private boolean isPark = false;
	
	public HashMap<String, Park> getParkInfo(InputSource is) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		
		parser.parse(is, this);
		
		return parks;
	}
	
	@Override
	public void startDocument() throws SAXException {
		parks = new HashMap<String, Park>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		currentElement = qName;
		if(currentElement.equals("row") && attributes.getLength() != 0) {
			if(!attributes.getValue("_id").equals("1")) {
				park = new Park();
				isPark = true;
			}			
		}
		else if(currentElement.equals(Park.LATITUDE)) {
			String latString = attributes.getValue("latitude");
			String lngString = attributes.getValue("longitude");
			park.setLatitude(Double.parseDouble(latString));
			park.setLongitude(Double.parseDouble(lngString));
			parks.put(park.getName(), park);
		}
		else {
			currentText = new StringBuilder();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		if(currentElement.equals("response") || currentElement.equals("row") || isPark == false) {
			return;
		}
		
		String content = currentText.toString();
		
		if (currentElement.equals(Park.NAME)) {
			park.setName(content);
		}
		else if (currentElement.equals(Park.TYPE)) {
			park.setType(content);
		}
		else if (currentElement.equals(Park.MANAGER)) {
			park.setManager(content);
		}
		else if (currentElement.equals(Park.EMAIL)) {
			park.setEmail(content);
		}
		else if (currentElement.equals(Park.PHONE)) {
			park.setPhone(content);
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
