package com.bfb.pos.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.kxml2.io.KXmlSerializer;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

public class XMLUtil {

	/**
	 * @param element
	 * @param name
	 * @return
	 */
	public static String getAttributeValue(Element element, String name) {
		return element.getAttributeValue(null, name);
	}

	/**
	 * @param element
	 * @param name
	 * @return
	 */
	public static String getChildValue(Element element, String name) {
		try{
			Element child = element.getElement(null, name);
			if (child != null) {
				String value = child.getAttributeValue(null, "value");
				if (value == null && child.getChildCount() > 0)
					value = child.getChild(0).toString();
				return value;
			}
		}catch(Exception e){
		}
		return null;
	}

	/**
	 * @param element
	 * @param name
	 * @param autoCreate
	 * @return
	 */
	public static Element getElement(Element element, String name, boolean autoCreate) {
		Element child = null;
		try {
			// Gets the child element with the given name.
			child = element.getElement(null, name);
		} catch (Exception e) {}
		// if failure ,new Element
		if (child == null && autoCreate) {
			child = new Element();
			child.setName(name);
			element.addChild(Node.ELEMENT, child);
		}
		return child;
	}

	/**
	 * @param element
	 * @param name
	 * @return
	 */
	public static Element[] getElements(Element element, String name) {
		Vector vector = new Vector();
		int count = element.getChildCount();
		for (int i = 0; i < count; i ++) {
			Element child = element.getElement(i);
			if (child == null)
				continue;
			
			if (name.equals(child.getName()))
				vector.addElement(child);
		}
		Element[] results = new Element[vector.size()];
		for (int i = 0; i < vector.size(); i ++) {
			results[i] = (Element)vector.elementAt(i);
		}
		return results;
	}

	/**
	 * @param element
	 * @return
	 * @throws IOException
	 */
	public static String toString(Element element) throws IOException {
		KXmlSerializer serializer = new KXmlSerializer ();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		serializer.setOutput(os, "UTF-8");
		element.write(serializer);
		serializer.flush();
		return new String(os.toByteArray(), "UTF-8");
	}

	/**
	 * @param element
	 * @param name
	 * @param value
	 */
	public static void setChildValue(Element element, String name, String value) {
		Element child = getElement(element, name, true);
		child.setAttribute(null, "value", value);
	}
	
	public static void setParamElement(Element root, String key, String value) {
		Element element = new Element();
		element.setName("param");
		element.setAttribute(null, "key", key);
		element.setAttribute(null, "value", value);
		root.addChild(Node.ELEMENT, element);
	}

}
