/*
 * Affiliation.java:  a Java implementation of Affiliation class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 1999, 2002, 2003, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069, JP17300071
 */

package org.nomencurator.model;

import java.io.Serializable;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.nomencurator.xml.XMLUtility;

/**
 * An implementation of <code>Affiliation</code> in Nomencurator
 * data model.  It implements <CODE>NamedObject</CODE> by extending
 * <CODE>AbstractNamedObject</CODE> for easiar implementation of
 * its GUI
 *
 * @see org.nomencurator.model.NamedObject
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org</A>
 *
 * @version 	24 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class Affiliation
    implements Serializable
{
    private static final long serialVersionUID = -9112153393819691914L;

    /** <code>Author</code> of which affiliation is represented by this object<code> */
    protected Author author;

    /** <code>String</code> representing name of insitute */
    String institute;

    /** Date as <coce>Calendar</code> when the <code>author</code> affiliateted to the institute etc. */
    protected Calendar from;

    /** Date as <coce>Calendar </code> when the <code>author</code> stopped to be affiliateted to the institute etc. */
    protected Calendar until;

    /** Overrides superclas' <CODE>entity</CODE>
	//protected Affiliation entity;

    /** Constructs an "empty" <code>Affiliation</code> */
    public Affiliation()
    {
	this(null, null, null, null);
    }
    
    /**
     * Constructs a non-trivial <code>Affiliation</code>.
     *
     * @param author <code>Author</code> of which affiliation should be represented by this object
     * @param institute name of institute where <code>author</code> affiliated
     * @param from  <code>Calendar</code> when <code>author</code> affiliated to the <code>institute</code>
     * @param until <code>Calendar</code> when <code>author</code> unaffiliated from the <code>institute</code>
     */
    public Affiliation(Author author, String institute,
		       Calendar from, Calendar until)
    {
	super();
	
	this.author = author;
	this.institute = institute;
	this.from   = from;
	this.until  = until;
    }

    /**
     * Constructs an <CODE>Affiliation</CODE> object using XML data
     * given by <CODE>xml</CODE>
     *
     * @param xml <CODE>Element</CODE> specifying <CODE>Affiliation</CODE>
     *
     */
    public Affiliation(Element xml)
    {
	NodeList childNodes = xml.getChildNodes();
	int subChildCount = childNodes.getLength();
	
	String persistentID = null;
	for (int j = 0; j < subChildCount; j++) {
	    Node node = childNodes.item(j);
	    if(node.getNodeType () == Node.ELEMENT_NODE) {
		Element element = (Element)node;
		String tagName = element.getTagName();

		if(tagName.equals ("institution")) {
		    // TBD
		    // AffiliationInstitution.getInstance().addLinkRecord(rec);
		}
		else if(tagName.equals ("from")) {
		    from = new GregorianCalendar();
		    from.setTime(java.sql.Date.valueOf(XMLUtility.getString(element)));
		}
		else if(tagName.equals ("until")) {
		    until = new GregorianCalendar();
		    until.setTime(java.sql.Date.valueOf(XMLUtility.getString(element)));
		}
		else{}
            }
        }
    }
    
    /**
     * Returns <code>Calendar</code> when the author affiliated to the institute
     *
     * @return Calendar when the author affiliated to the institute
     */
    public Calendar getFrom()
    {
	return from;
    }
    
    /**
     * Sets <code>from</code> as <code>Calendar</code> when the author affiliated to the institute
     *
     * @param from <code>Calendar</code> when the author affiliated to the institute
     */
    public void setFrom(Calendar from)
    {
	if(this.from == from)
	    return;

	this.from = from;
    }
    
    /**
     * Returns <code>Calendar</code> when the author unaffiliated from the institute
     *
     * @return Calendar when the author unaffiliated from the institute
     */
    public Calendar getUntil()
    {
	return until;
    }
    
    /**
     * Sets <code>until</code> as <code>Calendar</code> when the author unaffiliated from the institute
     *
     * @param until <code>Calendar</code> when the author unaffiliated from the institute
     */
    public void setUntil(Calendar until)
    {
	if(this.until == until)
	    return;

	this.until = until;
    }
    
    /**
     * Returns <code>Author</code> whose affiliation is represented by this
     *
     * @return Author whose affiliation is represented by this
     */
    public Author getAuthor()
    {
	return author;
    }
    
    /**
     * Sets <code>author</code> whose affiliation is represented by this
     *
     * @param author <code>Author</code> whose affiliation is represented by this
     */
    public void setAuthor(Author author)
    {
	if(this.author == author)
	    return;

	this.author = author;
    }
    
    /**
     * Returns <code>String</code> representing name of the institution
     *
     * @return String representing name of the institution
     */
    public String getInstitute()
    {
	return institute;
    }
    
    /**
     * Sets <code>institute</code> as name of the institution
     *
     * @param institute <code>String</code> representing name of the institution
     */
    public void setInstitute(String institute)
    {
	if(this.institute == institute)
	    return;

	this.institute = institute;
    }
    
    /**
     * Returns XML <code>String</code> representing this object
     *
     * @return XML <code>String</code> representing this object
     */
    public StringBuffer toXML()
    {
	return toXML(new StringBuffer());
    }
	
    /**
     * Returns XML <code>String</code> representing this object
     *
     * @return XML <code>String</code> representing this object
     */
    public StringBuffer toXML(StringBuffer buf)
    {
	buf.append("<Affiliation>\n");
	Author auther = getAuthor();
	if(author != null) {
    	    buf.append("<author>").append(author.getPersistentID()).append("</author>\n");
    	}
	buf.append("<institute>").append(getInstitute()).append("</institute>\n");
	if(getFrom() != null) {
    	    buf.append("<from>").append(getFrom().toString()).append("</from>\n");
    	}
    	if(getUntil() != null) {
    	    buf.append("<until>").append(getUntil().toString()).append("</until>\n");
    	}
	buf.append("</Affiliation>\n");
	
        return buf;
    }
}
