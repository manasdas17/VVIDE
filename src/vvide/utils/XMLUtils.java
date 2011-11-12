/*
 * This file is part of the VVIDE project.
 * 
 * Copyright (C) 2011 Pavel Fischer rubbiroid@gmail.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package vvide.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import vvide.annotations.Export;
import vvide.annotations.Import;
import vvide.logger.Logger;
import vvide.utils.xmlitems.AbstractXMLItem;

/**
 * A XML Serializer/Parser
 */
public class XMLUtils {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * String with a items package
	 */
	private String itemPackage = XMLUtils.class.getPackage().getName()
		+ ".xmlitems.";
	/**
	 * XML Document Factory
	 */
	private DocumentBuilderFactory builderFactory;
	/**
	 * XML Document Builder
	 */
	private DocumentBuilder docBuilder;
	/**
	 * XML Document
	 */
	private Document document;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Return a specified item
	 * 
	 * @param itemName
	 *        string with the item
	 * @return AbstractXMLItem Object or null in the fall of any errors
	 */
	@SuppressWarnings( "unchecked" )
	public AbstractXMLItem getItem( String itemName ) {
		// Getting a class
		Class<? extends AbstractXMLItem> itemClass;
		AbstractXMLItem item = null;
		try {
			itemClass =
					(Class<? extends AbstractXMLItem>) Class
							.forName( itemPackage + itemName + "Item" );
			item = itemClass.newInstance();
			item.setXmlUtils( this );
		}
		catch ( Exception e ) {
			Logger.logError( XMLUtils.class.getName(), e );
		}
		return item;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public XMLUtils() {
		try {
			builderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = builderFactory.newDocumentBuilder();
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
		}
	}

	/**
	 * Save a specified object to the specified file.<br>
	 * The object itself has information about the fields to be saved
	 * 
	 * @param obj
	 *        an Object to save
	 * @param file
	 *        a file to save
	 */
	public void saveToXMLFile( Object obj, File file ) {
		saveToXMLFile( obj, file, true );
	}

	/**
	 * Save a specified object to the specified file.<br>
	 * If hasInfo not setted, the save way should be present in the XML item
	 * 
	 * @param obj
	 *        an Object to save
	 * @param file
	 *        a file to save
	 * @param hasInfo
	 *        the object has help annotation to save the info
	 */
	public void saveToXMLFile( Object obj, File file, boolean hasInfo ) {
		try {
			// Making an XML Element
			document = docBuilder.newDocument();

			Element objectEleement;
			if ( hasInfo ) {
				objectEleement = getElementFor( obj, document );
			}
			else {
				String simpleClassName = obj.getClass().getSimpleName();
				objectEleement = document.createElement( simpleClassName );
				objectEleement.setAttribute( AbstractXMLItem.typeAttributeName,
						simpleClassName );
				getItem( simpleClassName ).makeXMLElement( document,
						objectEleement, obj );
			}
			document.appendChild( objectEleement );
			// saving a file
			transformAndSave( document, file );
		}
		catch ( Exception e ) {
			Logger.logError( XMLUtils.class.getName(), e );
		}
	}

	/**
	 * Transform the document and save to file
	 * 
	 * @param document
	 *        document to transform
	 * @param file
	 *        file to save
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void transformAndSave( Document document, File file )
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException,
			FileNotFoundException, IOException {
		// TransformerFactory instance is used to create Transformer
		// objects.
		TransformerFactory factory = TransformerFactory.newInstance();
		factory.setAttribute( "indent-number", new Integer( 4 ) );
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty( OutputKeys.INDENT, "yes" );

		// create string from xml tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult( sw );
		DOMSource source = new DOMSource( document );
		transformer.transform( source, result );
		String xmlString = sw.toString();

		BufferedWriter bw =
				new BufferedWriter( new OutputStreamWriter(
						new FileOutputStream( file ) ) );
		bw.write( xmlString );
		bw.flush();
		bw.close();
	}

	/**
	 * Create an XML for the specified Element
	 * 
	 * @param obj
	 *        an object to build an element for
	 * @param document
	 *        a XML document
	 * @return Element for Document
	 * @throws DOMException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Element getElementFor( Object obj, Document document )
			throws DOMException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Class<?> objectClass = obj.getClass();
		String simpleClassName = objectClass.getSimpleName();
		Element element = document.createElement( simpleClassName );
		element.setAttribute( AbstractXMLItem.typeAttributeName,
				simpleClassName );

		for ( Method method : objectClass.getMethods() ) {
			// Filter non @Export Methods
			if ( !method.isAnnotationPresent( Export.class ) ) continue;

			// getting AbstractXMLItem
			Export annotation = ((Export) method.getAnnotation( Export.class ));
			Element subElement = document.createElement( annotation.tagName() );

			// getting object to save
			Object value = method.invoke( obj );
			// checking the type
			String type =
					(annotation.type().length() == 0) ? value.getClass()
							.getSimpleName() : annotation.type();
			subElement.setAttribute( AbstractXMLItem.typeAttributeName, type );

			getItem( annotation.type() ).makeXMLElement( document, subElement,
					value );

			element.appendChild( subElement );
		}
		return element;
	}

	/**
	 * Load an object from xml File
	 * 
	 * @param file
	 *        file to load
	 * @return loaded object
	 */
	public Object loadFromXMLFile( File file ) {
		try {
			return loadFromXMLStream( new FileInputStream( file ) );
		}
		catch ( FileNotFoundException e ) {
			Logger.logError( this, e );
			return null;
		}
	}

	/**
	 * Load an object from xml Stream
	 * 
	 * @param stream
	 *        stream to load
	 * @return loaded object
	 */
	public Object loadFromXMLStream( InputStream stream ) {
		Object returnObject = null;

		try {
			document = docBuilder.parse( stream );
			document.getDocumentElement().normalize();

			// parse the xml file
			Element root = (Element) document.getFirstChild();
			returnObject =
					getItem(
							root.getAttribute( AbstractXMLItem.typeAttributeName ) )
							.parseXMLElement( root );

		}
		catch ( Exception e ) {
			Logger.logError( "XMLUtils.loadFromXMLFile", e );
		}

		return returnObject;
	}

	/**
	 * Parse a specified xml Stream
	 * 
	 * @param stream
	 *        stream to parse
	 * @return Root Element of the tree
	 */
	public Element parseFromXMLStream( InputStream stream ) {
		Element root = null;
		try {
			document = docBuilder.parse( stream );
			document.getDocumentElement().normalize();

			// parse the xml file
			root = (Element) document.getFirstChild();

		}
		catch ( Exception e ) {
			Logger.logError( "XMLUtils.loadFromXMLFile", e );
		}

		return root;
	}

	/**
	 * Parse an element and return a parsed object
	 * 
	 * @param object
	 *        an instance of the object to fill with data from element
	 * @param element
	 *        a XMLElement to parse
	 * @return a parsed object
	 */
	public Object fillObjectFor( Object object, Element element ) {
		// build a hashMap of @Import methods
		HashMap<String, Method> methodMap = new HashMap<String, Method>();
		for ( Method method : object.getClass().getMethods() ) {
			if ( !method.isAnnotationPresent( Import.class ) ) continue;
			// getting AbstractXMLItem
			Import annotation = ((Import) method.getAnnotation( Import.class ));
			methodMap.put( annotation.tagName(), method );
		}

		// parse the xml
		for ( Element subElement : getElementNodeList( element.getChildNodes() ) ) {
			Method method = methodMap.get( subElement.getTagName() );
			if ( method == null ) {
				Logger.logError( "XMLUtils.fillObjectFor",
						"No @Import method found for the tag "
							+ subElement.getTagName() );
				continue;
			}
			try {
				method.invoke(
						object,
						getItem(
								subElement
										.getAttribute( AbstractXMLItem.typeAttributeName ) )
								.parseXMLElement( subElement ) );
			}
			catch ( Exception e ) {
				Logger.logError( "XMLUtils.fillObjectFor", e );
			}
		}

		return object;
	}

	/**
	 * Return a filtered (only ElementNodes) Nodes
	 * 
	 * @param childNodes
	 *        Nodelist with all nodes
	 * @return Vector with filtered nodes
	 */
	public Vector<Element> getElementNodeList( NodeList childNodes ) {
		Vector<Element> nodes = new Vector<Element>();
		Node node;
		for ( int i = 0; i < childNodes.getLength(); ++i ) {
			node = childNodes.item( i );
			if ( node.getNodeType() != Node.ELEMENT_NODE ) continue;
			nodes.add( (Element) node );
		}
		return nodes;
	}

	/**
	 * Return a text value of the node
	 */
	public String getNodeValue( Element item ) {
		Node text = item.getFirstChild();
		return (text == null) ? "" : text.getNodeValue().trim();
	}
}
