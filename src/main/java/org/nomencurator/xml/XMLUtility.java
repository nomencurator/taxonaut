/*
 * XMLUtility.java: a set of methods to handle XML document
 *
 * Copyright (c) 2014, 2015 Nozomi `James' Ytow
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

package org.nomencurator.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * <tt>XMLUtility</tt> provides a set of methods to handle XML document
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class XMLUtility
{
    /**
     * get String Data
     * @param  xmlElement an <CODE>Element</CODE> of XML document
     * @return String representing contents of <CODE>xmlElement</CODE>
     */
    public static String getString(Element xmlElement)
    {
	Node node = xmlElement.getFirstChild();
	if(node == null)
	    return "";
	if(node instanceof Text) {
	    Text text = (Text)node;
	    if(text.getData() == null)
		return "";
	    else
		return text.getData().trim();
        }
	return getPseudoHTML(xmlElement);
    }

    public static String getPseudoHTML(Element xmlElement)
    {
	StringBuffer html = new StringBuffer();
	if(xmlElement.hasChildNodes()) {
	    NodeList nodes = xmlElement.getChildNodes();
	    int children = nodes.getLength();
	    for(int i = 0; i < children; i++)
		parsePseudoHTML(nodes.item(i), html);
	}
	return html.toString();
    }

    public static void parsePseudoHTML(Node e, StringBuffer html)
    {
	String nodeName = e.getNodeName();
	boolean toAppendTag = !(e instanceof Text);
	boolean isBR = nodeName.equals("BR");
	if(toAppendTag & !isBR){
	    html.append('<');
	    html.append(nodeName);
	    html.append('>');
	}
	if(!e.hasChildNodes() && !isBR) {
	    html.append(e.getNodeValue());
	}
	else {
	    NodeList nodes = e.getChildNodes();
	    int children = nodes.getLength();
	    for(int i = 0; i < children; i++)
		parsePseudoHTML(nodes.item(i), html);
	}
	if(toAppendTag){
	    if(isBR){
		html.append("<BR>");
	    }
	    else {
		html.append("</");
		html.append(nodeName);
		html.append(">\n");
	    }
	}
    }
}
