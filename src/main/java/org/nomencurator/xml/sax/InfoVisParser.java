/*
 * InfoVisParser.java:  a SAX parser for InfoVis 2003 data
 *
 * Copyright (c) 2003, 2004, 2014, 2019 Nozomi `James' Ytow
 * All rights reserved.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nomencurator.xml.sax;

import java.io.IOException;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.nomencurator.model.Appearance;
import org.nomencurator.model.DefaultNameUsage;
import org.nomencurator.model.DefaultNameUsageNode;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.Publication;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * {@code InfoVisParser} provides a SAX parser for InfoVis 2003 contest data
 *
 * @see <A HREF="https://www.cs.umd.edu/hcil/iv03contest/">https://www.cs.umd.edu/hcil/iv03contest/</A>
 *
 * @version 	03 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public class InfoVisParser
    implements ContentHandler
{
    protected static String defaultParserName =
	"org.apache.xerces.parsers.SAXParser";

    protected Locator locator;

    protected SAXParserFactory parserFactory;
    protected SAXParser parser;

    protected XMLReader reader;

    protected Map<String, String> namespaceMappings;

    /**
     * <code>viewName</code> is the name of the hierarchy that is encoded in the file.
     * This is often described by the names of the authors who wrote the publication
     * containing the hierarchy.
     */ 
    protected String viewName;

    protected DefaultNameUsage root;

    protected DefaultNameUsage current;

    protected DefaultNameUsage parent;


    protected Publication publication;

    protected Appearance appearance;

    protected boolean isPreviousBranch;
    protected boolean isPreviousLeaf;

    /**
     * The constructor - creates an InfoVis2003 XML usind default parser
     */
    public InfoVisParser()
	throws ParserConfigurationException, SAXException
    {
	parserFactory = SAXParserFactory.newInstance();
	parser = parserFactory.newSAXParser();
	reader = parser.getXMLReader();
	reader.setContentHandler(this);

	initialize();
    }

    public void initialize()
    {
	isPreviousBranch = false;
	isPreviousLeaf = false;
	current = null;
	parent = null;
	root = null;
	/*
	if(namespaceMappings != null)
	    namespaceMappings.clear();
	*/
    }
    
    /**
     * Sets view name
     * 
     * @param vName The name of the view that this hierarchy represents, often the name of
     * the file or names of the authors of the publication containing the hierarchy.
     */
    public void setViewName(String viewName)
    {
	this.viewName = viewName;
    }
    
    /**
     * Having declared an instance of the InfoVis2003 class, <code>parseFile</code>
     * should be called to read the file. This method will create an XML DOM representation
     * of the file and a <code>TreeWalker</code> that can be used to walk the XML DOM.
     * 
     * @param parseFile The name of the file containing the hierarchy, represented in XML in
     * the InfoVis 2003 format.
     */
    public boolean parse(String xmlURI)
    //throws IOException, SAXException
    {
	try {
	    InputSource source =
		new InputSource(xmlURI);
	    setViewName(xmlURI);
	    publication = new Publication();
	    publication.setCitationTitle(xmlURI);
	    appearance = new Appearance(publication);
	    if(namespaceMappings == null)
		namespaceMappings = Collections.synchronizedMap(new HashMap<String, String>());
	    else
		namespaceMappings.clear();
	    reader.parse(source);
	    return true;
	}
	catch (SAXException sax) {
	}
	catch (IOException iox) {
	}
	return false;
    }

    public void setDocumentLocator(Locator locator)
    {
	this.locator = locator;
    }

    public void startDocument()
	throws SAXException
    {
    }

    public void endDocument()
	throws SAXException
    {
	if(namespaceMappings != null)
	    namespaceMappings.clear();
    }

    public void processingInstruction(String target, String data)
	throws SAXException
    {
    }

    public void startPrefixMapping(String prefix, String uri)
	throws SAXException
    {
	if(namespaceMappings == null)
	    return;

	namespaceMappings.put(uri, prefix);
    }

    public void endPrefixMapping(String prefix)
	throws SAXException
    {
	if(namespaceMappings == null)
	    return;

	Set<String> toBeRemoved = new HashSet<String>();
	Set<String> keys = namespaceMappings.keySet();
	for(String uri : keys) {
	    String thePrefix = namespaceMappings.get(uri);
	    if(prefix.equals(thePrefix))
		toBeRemoved.add(uri);
	}
	if(toBeRemoved.isEmpty())
	    return;
	for(String key : toBeRemoved) {
	    namespaceMappings.remove(key);
	}
	toBeRemoved.clear();
    }

    public void startElement(String namespaceURI,
			     String localName,
			     String qName,
			     Attributes attributes)
	throws SAXException
    {
	if(localName.equals("leaf")) {
	    if(isPreviousBranch) {
		if(parent != null &&
		   current != null &&
		   current != parent &&
		   !parent.contains(current))
		parent.addLowerNameUsage(current);

		parent = current;
	    }
	    isPreviousBranch = false;
	    current = 
		//new DefaultNameUsageNode(/*appearance*/);
		new DefaultNameUsage(/*appearance*/);
	}
	else if(localName.equals("branch")) {

	    if(parent != null &&
	       current != null && 
	       current != parent &&
	       !parent.contains(current) &&
	       isPreviousBranch) {
		parent.addLowerNameUsage(current);
	    }

	    if(isPreviousBranch)
		parent = current;
	    current = 
		new DefaultNameUsage(/*appearance*/);
		//new DefaultNameUsageNode(/*appearance*/);
	    if(root == null)
		root = current;
	    isPreviousBranch = true;
	}

	else if(localName.equals("attribute")) {
	    if(attributes.getValue(0).equals("latin_name")) {
		current.setLiteral(attributes.getValue(1));
	    }
	    else if (attributes.getValue(0).equals("rank")) {
		current.setRankLiteral(attributes.getValue(1).toLowerCase());
	    }
	}

    }

    public void endElement(String namespaceURI,
			     String localName,
			     String qName)
	throws SAXException
    {
	if(localName.equals("branch")){
	    if(!isPreviousBranch) {
		current = parent;
		parent = (DefaultNameUsage)current.getHigherNameUsage();
	    }

	    current = parent;
	    if(current != null)
		parent = (DefaultNameUsage)current.getHigherNameUsage();
	    if(!isPreviousBranch && parent == null)
		parent = current;
	    isPreviousBranch = true;
	}
	else if(localName.equals("leaf")) {
	    try{
		if(parent != null && 
		   current != parent && !parent.contains(current))
		    parent.addLowerNameUsage(current);
		isPreviousBranch = false;
	    }
	    catch(Throwable e) {
		e.printStackTrace();
	    }
	}
    }

    public void ignorableWhitespace(char[] ch, int start, int length)
	throws SAXException
    {
    }

    public void skippedEntity(String name)
	throws SAXException
    {
    }

    public void characters(char[] ch, int start, int length)
	throws SAXException
    {
    }

    public static void main(String[] args)
    {
	try {
	    InfoVisParser parser = 
		new InfoVisParser();
	    parser.parse(args[0]);
	}
	catch(Exception e) {
	}
    }

    public DefaultNameUsage getRoot()
    {
	return root;
    }
}
