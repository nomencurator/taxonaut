/*
 * Annotation.java:  a Java implementation of Annotation class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 1999, 2002, 2003, 2004, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.model;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.text.Collator;
import java.text.CollationKey;
import java.text.RuleBasedCollator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import org.nomencurator.io.sql.NamedObjectConnection;

import org.nomencurator.util.ArrayUtility;
import org.nomencurator.util.CollectionUtility;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <code>Annotation</code> implements Annotation data structure in Nomencurator data model
 *
 * @see org.nomencurator.model.Appearance
 * @see org.nomencurator.model.NameUsage
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class Annotation
    extends AbstractNamedObject <Annotation, Annotation>
    implements Serializable
{
    private static final long serialVersionUID = -8067577727257921739L;

    public static final String DEFAULT_LINK_TYPE = "refer";
    
    /** type of linkage between {@code NameUsage}s */
    protected String linkType;

    /** Vector containing annotator {@code NameUsage}s */
    protected Collection<NameUsage<?, ?>> annotators;

    /** Vector containing annotatant {@code NameUsage}s */
    protected Collection<NameUsage<?, ?>> annotatants;

    /** {@code Appearance} encoding this {@code Annotation} */
    protected Appearance appearance;

    // Locale.US is required to be implemented
    // as available locales of java.text.Collator
    protected static RuleBasedCollator collator = 
	(RuleBasedCollator)Collator.getInstance(Locale.US);

    /** <tt>CollectionUtility</tt> to handle <tt>Collection</tt>s */
    protected static CollectionUtility<NameUsage<?, ?>> utility =
	new CollectionUtility<NameUsage<?, ?>>();

    /**
     * Constructs an empty {@code Annotation} object
     */
    public Annotation()
    {
	this(DEFAULT_LINK_TYPE, null, new ArrayList<NameUsage<?, ?>>(), new ArrayList<NameUsage<?, ?>>());
    }
    
    /**
     * Constructs an {@code Annotation} having
     * {@code name} as its name, i.e. perisitent ID
     *
     * @param name {@code String} representing its name,
     * i.e. perisitent ID
     */
    public Annotation(String name)
    {
	this();
	setPersistentID(name);
    }
    

    /**
     * Constructs a copy of given {@code name}
     *
     * @param name {@code Name} representation of an {@code Annotation}
     */
    public Annotation(Name<Annotation, Annotation> name)
    {
	//don't use NamedObject(String) constructor
	this();
	
	if(name instanceof Annotation) {
	    Annotation a = (Annotation)name;
	    a = a.getEntity();
	    setLinkType(a.getLinkType());
	    setAppearance(a.getAppearance());
	    annotators  = new HashSet<NameUsage<?, ?>>(a.annotators);
	    annotatants = new HashSet<NameUsage<?, ?>>(a.annotatants);
	}
	else
	    setPersistentID(name.getLiteral());
    }

    /**
     * Constructs a deep copy of {@CODE annotation}
     *
     * @param annotation {@CODE Annotation} to be copied deeply
     */
    public Annotation(Annotation annotation)
    {
	this();
	if(annotation == null)
	    return;

	annotation = annotation.getEntity();

	setLinkType(annotation.getLinkType());
	setAppearance(annotation.getAppearance());

	annotators  = new HashSet<NameUsage<?, ?>>(annotation.annotators);
	annotatants = new HashSet<NameUsage<?, ?>>(annotation.annotatants);
    }
  
    /**
     * Constructs a non-trivial {@code Annotation}.
     *
     * @param type {@code String} representing linkage type
     * @param citation {@code Apperance} encoding this {@code Annotation}
     * @param annotators {@code Vector} containing annotating {@code NameUsage}s
     * @param annotatants {@code Vector} containing annotated {@code NameUsage}s
     */
    public Annotation(String type, Appearance citation,
		      Collection<NameUsage<?, ?>> annotators, Collection<NameUsage<?, ?>> annotatants)
    {
	super();
	linkType = type;
	appearance = citation;
	
	this.annotators  = new HashSet<NameUsage<?, ?>>(annotators);
	this.annotatants = new HashSet<NameUsage<?, ?>>(annotatants);
    }

    /**
     * Constructs an {@CODE Annotaion} object using XML data
     * given by {@CODE xml}
     *
     * @param xml {@CODE Element} specifying an {@CODE Annotation}
     *
     */
    public Annotation(Element xml)
    {
	this(xml, null);
    }

    /**
     * Constructs an {@CODE Annotaion} object using XML data
     * given by {@CODE xml}
     *
     * @param xml {@CODE Element} specifying an {@CODE Annotation}
     *
     */
    public Annotation(Element xml, Appearance ap)
    {
	super();
	NodeList nodeList = xml.getChildNodes();
	int nodeCount = nodeList.getLength();
	String persistentID = null;
	int authorCount = 0;
	boolean toBeResolved = false;
	List<NameUsage<?, ?>> annotatorBuffer = null;
	List<NameUsage<?, ?>> annotatantBuffer = null;

	for (int j = 0; j < nodeCount; j++) {
	    Node node = nodeList.item(j);
	    if(node.getNodeType () == Node.ELEMENT_NODE) {
		Element element = (Element)node;
		String tagName = element.getTagName();

		if (tagName.equals ("oid"))
		    persistentID = getString(element);
		else if(tagName.equals ("linktype"))
		    linkType =  getString(element);
		else if(tagName.equals ("appearance")) {
		    String pID = getString(element);
		    if(ap != null &&
		       ap.getLiteral().equals(pID)) {
			appearance = ap;
			toBeResolved = true;
		    }
		    else{
			ap =
			    (Appearance)curator.get(pID);
			if(ap != null) {
			    appearance = ap;
			    toBeResolved = true;
			}
			else {
			    appearance = new Appearance();
			    appearance.setLiteral(pID);
			    curator.put(appearance);
			    appearance.addAnnotation(this);
			}
		    }
		    /*
		    LinkRecord rec = new LinkRecord(getString(ele), annotation.getPersistentID());
		    AppearanceAnnotation.getInstance().addLinkRecord(rec);
		    */
		}
		else if(tagName.equals ("from")) {
		    String pID = getString(element);
		    NameUsage<?, ?> n = (NameUsage<?, ?>)curator.get(pID);
		    if(n != null) {
			if(annotatorBuffer == null)
			    annotatorBuffer = new ArrayList<NameUsage<?, ?>>();
			//annotatorBuffer.addElement(n);
			toBeResolved = true;
		    }
		    else {
			n = new DefaultNameUsage();
			n.setLiteral(pID);
			curator.put(n);
			//addAnnotator(n);
		    }
		    annotatorBuffer.add(n);

		    /*
		    LinkRecord rec = new LinkRecord(annotation.getPersistentID(), getString(element);
		    AnnotationFrom.getInstance().addLinkRecord(rec);
		    */
		}
		else if(tagName.equals ("to")) {
		    String pID = getString(element);
		    NameUsage<?, ?> n = (NameUsage<?, ?>)curator.get(pID);
		    if(n != null) {
			if(annotatantBuffer == null)
			    annotatantBuffer = new ArrayList<NameUsage<?, ?>>();
			//annotatantBuffer.addElement(n);
			toBeResolved = true;
		    }
		    else {
			n = new DefaultNameUsage();
			n.setLiteral(pID);
			curator.put(n);
			//addAnnotatant(n);
		    }
		    annotatantBuffer.add(n);

		    /*
		    LinkRecord rec = new LinkRecord(annotation.getPersistentID(), getString(element);
		    AnnotationTo.getInstance().addLinkRecord(rec);
		    */
		}
		else{}
            }
        }

	if(persistentID != null &&
	   !persistentID.equals(getLiteral()))
	    setLiteral(persistentID); //i.e. other key data are empty

	if(annotatorBuffer != null) {
	    annotators = new HashSet<NameUsage<?, ?>>(annotatorBuffer);
	}

	if(annotatantBuffer != null) {
	    annotatants = new HashSet<NameUsage<?, ?>>(annotatantBuffer);
	}

	if(toBeResolved)
	    curator.resolve(this);
    }

    public Annotation clone()
    {
	Annotation a = new Annotation();

	a.setLinkType(getLinkType());
	a.setAppearance(getAppearance());

	Annotation source = getEntity();
	a.annotators = new HashSet<NameUsage<?, ?>>(source.annotators);
	a.annotatants = new HashSet<NameUsage<?, ?>>(source.annotatants);

	return a;
    }
  

    
    /**
     * Returns a persistent ID representing this {@CODE Annotation}
     * with specified  {@CODE separator}.  It contains class name header
     * if {@CODE withClassName} true.
     * The subclasses must provide this method.
     *
     * @param separator {@CODE String} to be used as the field separator
     * @param withClassName {@CODE boolean} specifying with or without
     * class name header
     *
     * @return String representing this {@CODE Annotation}
     */
    public String getPersistentID(String separator, boolean withClassName)
    {
	if(entity != null)
	    return getEntity().getPersistentID(separator, withClassName);

	StringBuffer buffer = null;
	
	if(withClassName)
	    buffer = getClassNameHeaderBuffer();
	else
	    buffer = new StringBuffer();

	if(linkType != null && linkType.length() > 0)
	    buffer.append(linkType);
	buffer.append(separator);

	if(annotators != null && !annotators.isEmpty()) {
	    CollationKey[] annotatorNameKeys = null;
	    synchronized(annotators) {
		int size = annotators.size();
		Iterator<NameUsage<?, ?>> nameUsages = getAnnotators();
		annotatorNameKeys = new CollationKey[size];
		for(int i = 0; i < size; i++) {
		    annotatorNameKeys[i] =
			collator.getCollationKey(nameUsages.next().getLiteral());
		}
	    }
	    Arrays.sort(annotatorNameKeys);
	    buffer.append(annotatorNameKeys[0].getSourceString());
	}
	buffer.append(separator);

	if(appearance == null){
	    //request code here;
	}
	if(appearance == null) {
	    buffer.append(new Appearance().getPersistentID(separator, false));
	}
	else{
	    buffer.append(appearance.getPersistentID(separator, false));
	}

	return buffer.toString();
    }
    
    /**
     * Returnes number of fields separators in persistent ID
     *
     * @return int representing number of fields separators in persistent ID
     */ 
    public int getFieldSepartorsCount()
    {
	return 10; //linktype, annotator, + 8 from Appearance
    }

    /**
     * Merges {@code namedObject} with this {@code Annotation}
     * if possible, but not yet implemented.
     * It returns true if merged.
     *
     * @param namedObject a {@code NamedObject} to be merged
     *
     * @return true if merged, or false if not mergiable
     */
    public boolean merge(NamedObject<?, ?> namedObject)
    {
	if(!(namedObject instanceof Annotation))
	    return false;
	return false; //not yet implemented
    }
    
    /**
     * Parses a {@code line} and sets values of this object accordint to it
     *
     * @param line {@code String} containing fragment of data to be set
     */
    public void parseLine(String line)
    {
	Annotation a = getEntity();
	if(a != this) {
	    a.parseLine(line);
	    return;
	}

	String s = peal(line);
	if(line.startsWith("reason"))
	    linkType = s;
	else if(line.startsWith("annotators")) {
	    addAnnotator(new DefaultNameUsage(s));
	}
	else if(line.startsWith("annotatants")) {
	    addAnnotatant(new DefaultNameUsage(s)); 
	}
	else if(line.startsWith("appearance"))
	    appearance  = new Appearance(s);
    }

    /**
     * Returns {@code String} representing link type
     *
     * @return String representing link type
     */
    public String getLinkType()
    {
	if(entity != null)
	    return getEntity().getLinkType();

	if(!isNominal())
	    return linkType;

	return getPersistentIDComponent(0);
    }

    /**
     * Sets {@code type} as linkage type of this {@code Annotation}
     * if it is different from current link type.
     *
     * @param type {@code String} representing linkage type to be set
     */
    public void setLinkType(String type)
    {
	if(entity != null)
	    getEntity().setLinkType(type);

	if(type != null &&  type.equals(linkType))
	    return;

	linkType = type;
    }

    /**
     * Returns {@code Appearance} encoding this {@code Appearance}
     *
     * @return {@code Appearance} encoding this {@code Appearance}
     */
    public Appearance getAppearance()
    {
	if(entity != null)
	    return getEntity().getAppearance();

	return appearance;
    }

    /**
     * Sets {@code appearance} as {@code Appearance} encoding this {@code Appearance}
     * if it is different from what currently stored.
     *
     * @param {@code appearance} {@code Appearance} to be set as encoder of this {@code Appearance}
     */
    public void setAppearance(Appearance appearance)
    {
	if(entity != null)
	    getEntity().setAppearance(appearance);

	if(this.appearance == appearance)
	    return;

	this.appearance =  appearance;
    }

    /**
     * Returns a {@code Collection} of annotator {@code NameUsage}s,
     * or null if annotator list is empty.  It is a copy of the collection, not the collection
     * itself.
     *
     * @return Collection containing annotator {@code NameUsage}s
     * or null if annotator list is empty
     */ 
    public Iterator<NameUsage<?, ?>> getAnnotators()
    {
	Annotation annotation = getEntity();
	if(annotation != this)
	    return annotation.getAnnotators();

	if(annotators == null || annotators.isEmpty())
	    return null;

	return annotators.iterator();
    }

    /**
     * Sets contents of {@code nameUsages} as annotator {@code NameUsage}s.
     *
     * @param nameUsages {@code Collection} containing annotator {@code NameUsage}s
     */ 
    public void setAnnotators(Collection<? extends NameUsage<?, ?>> nameUsages)
    {
	Annotation annotation = getEntity();
	if(annotation != this) {
	    annotation.setAnnotators(nameUsages);
	    return;
	}

	annotators = new HashSet<NameUsage<?, ?>>(nameUsages);
	for(NameUsage<?, ?> n : nameUsages) {
	    n.addAnnotation(this);
	}
    }

    /**
     * Adds a {@code NameUsage} as an annotator of this {@code Annotation}s
     * with returning true if it is not contained in the annotator list.
     * Otherwise it returs false without re-addition of 
     * {@code nameUsage} to the list
     *
     * @param {@code nameUsage} {@code NameUsage} to be an annotator of this {@code Annotation}s
     *
     * @return true if {@code nameUsage} added to the list successfully,
     * or false if not.
     */ 
    public boolean addAnnotator(NameUsage<?, ?> nameUsage)
    {
	Annotation annotation = getEntity();
	if(annotation != this)
	    return annotation.addAnnotator(nameUsage);

	if(nameUsage == null)
	    return false;

	if(annotators == null)
	    annotators = Collections.synchronizedSet(new HashSet<NameUsage<?, ?>>());

	synchronized(annotators) {
	    if(annotators.contains(nameUsage))
		return false;
	    annotators.add(nameUsage);
	}
	nameUsage.addAnnotation(this);

	return true;
    }
    
    /**
     * Removes a {@code nameUsage} from annotator list of this {@code Annotation}s
     *
     * @param {@code nameUsage} {@code NameUsage} to be removed from annotator list of this {@code Annotation}s
     */ 
    public void removeAnnotator(NameUsage<?, ?> nameUsage)
    {
	Annotation annotation = getEntity();
	if(annotation != this) {
	    annotation.removeAnnotator(nameUsage);
	    return;
	}

	synchronized(annotators) {
	    if(nameUsage == null || annotators == null || !annotators.contains(nameUsage))
		return;

	    nameUsage.removeAnnotation(this);
	    annotators.remove(nameUsage);

	}
    }
    
    /**
     * Removes all {@code NameUsage}s from annotator list of this {@code Annotation}s
     */ 
    public void clearAnnotators()
    {
	Annotation annotation = getEntity();
	if(annotation != this) {
	    annotation.clearAnnotators();
	    return;
	}

	if(annotators == null || annotators.isEmpty())
	    return;

	for(NameUsage<?, ?> n:annotators) {
	    n.removeAnnotation(this);
	}
	annotators.clear();
	annotators = null;
    }

    /**
     * Returns an {@code Enumeration} of annotatant {@code NameUsage}s,
     * or null if annotatants list is empty
     *
     * @return Enumeration containing annotatant {@code NameUsage}s, or
     * null if annotatants list is empty
     */ 
    public Iterator<NameUsage<?, ?>> getAnnotatants()
    {
	Annotation a = getEntity();
	if(a != this)
	    return a.getAnnotatants();

	if(annotatants == null || annotatants.isEmpty())
	    return null;

	return annotatants.iterator();
    }

    /**
     * Sets contents of {@code nameUsages} as annotatant {@code NameUsage}s.
     *
     * @param nameUsages {@code Collection} containing annotatnt {@code NameUsage}s
     */ 
    public void setAnnotatants(Collection<? extends NameUsage<?, ?>> nameUsages)
    {
	Annotation annotation = getEntity();
	if(annotation != this) {
	    annotation.setAnnotatants(nameUsages);
	    return;
	}

	annotatants = new HashSet<NameUsage<?, ?>>(nameUsages);
    }
    
    /**
     * Adds a {@code NameUsage} as an annotatant of this {@code Annotation}s
     * with returning true if it is not contained in the annotatant list.
     * Otherwise it returs false without re-addition of 
     * {@code nameUsage} to the list
     *
     * @param {@code nameUsage} {@code NameUsage} to be an annotatant of this {@code Annotation}s
     *
     * @return true if {@code nameUsage} added to the list successfully,
     * or false if not.
     */ 
    public boolean addAnnotatant(NameUsage<?, ?> nameUsage)
    {
	Annotation annotation = getEntity();
	if(annotation != this)
	    return annotation.addAnnotatant(nameUsage);

	if(nameUsage == null)
	    return false;

	if(annotatants == null)
	    annotatants = Collections.synchronizedSet(new HashSet<NameUsage<?, ?>>());

	synchronized(annotatants) {
	    if(annotatants.contains(nameUsage))
		return false;
	    
	    annotatants.add(nameUsage);
	}

	return true;
    }
    
    /**
     * Removes a {@code nameUsage} from annotatant list of this {@code Annotation}s
     *
     * @param {@code nameUsage} {@code NameUsage} to be removed from annotatant list of this {@code Annotation}s
     */ 
    public void removeAnnotatant(NameUsage<?, ?> nameUsage)
    {
	Annotation annotation = getEntity();
	if(annotation != this) {
	    annotation.removeAnnotatant(nameUsage);
	    return;
	}

	synchronized(annotatants) {
	    if(nameUsage == null || annotatants == null || !annotatants.contains(nameUsage))
		return;

	    annotatants.remove(nameUsage);
	}
    }

    /**
     * Removes all {@code NameUsage}s from annotatants list of this {@code Annotation}s
     */ 
    public void clearAnnotatants()
    {
	Annotation annotation = getEntity();
	if(annotation != this) {
	    annotation.clearAnnotatants();
	    return;
	}

	if(annotatants == null || annotatants.isEmpty())
	    return;

	annotatants.clear();
	annotatants = null;
    }
    
    /**
     * Returns an expression of this {@code Annotation} as XML {@code String}
     *
     * @return XML String of this {@code Annotation}
     */
    public String toXMLString()
    {
	if(entity != null)
	    return getEntity().toXMLString();

	StringBuffer buf = new StringBuffer();
	    
	buf.append("<Annotation>\n<oid>");
	buf.append(getPersistentID());
	buf.append("</oid>\n<linktype>");
	buf.append(linkType);
	buf.append("</linktype>\n<appearance>");
	buf.append(appearance);
	buf.append("</appearance>\n");
	if(annotators != null) {
            for(NameUsage<?, ?> n: annotators) {
    	        buf.append("<from>");
		buf.append(n.getPersistentID());
		buf.append("</from>\n");
	    }
	}
	if(annotatants != null) {
            for(NameUsage<?, ?> n: annotatants) {
    	        buf.append("<to>");
		buf.append(n.getPersistentID());
		buf.append("</to>\n");
	    }
	}
	buf.append("</Annotation>\n");
	
        return buf.toString();
    }

    /**
     * Sets valuse of this {@code NamedObject} to
     * {@code statement} using {@code connection}.
     * from specified {@code index} of the {@code statement}
     *
     * @param statement {@code PraredStatement} to which
     * value of the this {@code NamedObject} to be set
     * @param connection {@code NamedObjectConnection}
     * to be used to set values
     * @param index {@code int} from where values to be set
     * into the {@code statement}
     *
     * @return int index of the next parameter to be set if there is
     *
     * @exception SQLException
     */
    public int setValues(PreparedStatement statement,
			 NamedObjectConnection<?> connection,
			 int index)
	throws SQLException
    {
	return connection.setValues(statement, this, index);
    }

}
