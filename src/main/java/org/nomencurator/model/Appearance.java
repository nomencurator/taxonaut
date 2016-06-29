/*
 * Appearance.java: a Java implementation of Appearance class for the
 * Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 1999, 2002, 2003, 2004, 2014, 2015, 2016 Nozomi `James' Ytow
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
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.nomencurator.io.sql.NamedObjectConnection;

import org.nomencurator.util.ArrayUtility;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An implementation of Appearance data structure of Nomencurator data model
 *
 * @see 	org.nomencurator.model.NamedObject
 * @see 	org.nomencurator.model.NameUsage
 * @see 	org.nomencurator.model.Annotation
 * @see 	org.nomencurator.model.Publication
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class Appearance
    extends AbstractNamedObject<Appearance, Appearance>
    implements Serializable
{
    private static final long serialVersionUID = -7215421366823290810L;

    /**
     * <code>Vector</code> of <code>NameUsage</code>s
     * encoded by this <code>Appearance</code> object</code>
     */
    protected Collection<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> nameUsages;

    /**
     * <code>Vector</code> of <code>Annotation</code>s
     * encoded by this <code>Appearance</code> object</code>
     */
    protected Collection<Annotation> annotations;

    /** <code>Publication</code> where the appearance appeared */
    protected Publication publication;

    /** Index of page(s) in persistentID */
    public static final int PAGE = 0;

    /** Index of lines in persistentID */
    public static final int LINES = PAGE + 1;

    /** Index of publication in persistentID */
    public static final int PUBLICATION = LINES + 1;

    /** Index of author(s) in persistentID */
    public static final int AUTHOR = PUBLICATION + Publication.AUTHOR;
    /** Index of year in persistentID*/
    public static final int YEAR = PUBLICATION + Publication.YEAR;
    /** Index of title or ISXN in persistentID*/
    public static final int TITLE = PUBLICATION + Publication.TITLE;
    /** Index of volume in persistentID*/
    public static final int VOLUME = PUBLICATION + Publication.VOLUME;
    /** Index of number in persistentID*/
    public static final int ISSUE = PUBLICATION + Publication.ISSUE;
    /** Index of first page in persistentID*/
    public static final int FIRST_PAGE = PUBLICATION + Publication.FIRST_PAGE;
    /** Index of last page in persistentID*/
    public static final int LAST_PAGE = PUBLICATION + Publication.LAST_PAGE;
    /** number of items in the persistentID*/
    public static final int PID_ITEMS = LAST_PAGE;

    /** 
     * <code>String</code> represents the page where
     * the appearance appeared in the publication
     */
    protected String page;
     
    /**
     * <code>String</code> represents the lines on the page
     * where the appearance appeared.
     * It may represent either single line or a range.
     */
    protected String lines;

    /** <code>String</code> represents appearance of names itself */
    protected String appearance;

    /**
     * Constructs an empty <code>Appearance</code> object
     */
    public Appearance()
    {
	super();
	/*
	appearance = ANONYMOUS;
	page       = ANONYMOUS;
	lines      = ANONYMOUS;

	nameUsages = new Vector();
	annotations = new Vector();
	*/
    }

    /**
     * Constructs an <code>Appearance</code> having
     * <code>name</code> as its name, i.e. perisitent ID
     *
     * @param name <code>String</code> representing its name,
     * i.e. perisitent ID
     */
    public Appearance(String name)
    {
	super(name);
    }

    /**
     * Constructs a copy of given <code>name</code>. 
     *
     * @param name <code>Name</code> representation of an <code>Appearance</code>.  It can be either an <tt>Appearance</tt> or a <tt>NameUsage</tt>.
     */
    public Appearance(Name<?, ?> name)
    {
	//don't use NamedObject(String) constructor
	this();
	
	if(name instanceof Appearance){
	    Appearance a = (Appearance)name;

	    publication = a.publication;
	    page        = a.page;
	    lines       = a.lines;

	    appearance  = a.appearance;

	    nameUsages = a.getNameUsages();
	    annotations = a.getAnnotations();
	    
	}
	else
	    setPersistentID(name.getLiteral());
    }

    /**
     * Constructs an <code>Appearance</code> in
     * <CODE>publication</CODE>
     *
     * @param publication <code>Publication</code> where
     * this appeared
     */
    public Appearance(Publication publication)
    {
	this();
	setPublication(publication);
    }

    /**
     * Constructs a non-trivial <code>Appearance</code>.
     *
     * @param publication <code>Publication</code> where
     * this appeared
     */
    public Appearance(Publication publication,
		      String page,
		      String lines,
		      String appearance,
		      Collection<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> nameUsages,
		      Collection<Annotation> annotations) 
    {
	this(publication, page, lines, appearance);
	setNameUsages(nameUsages);
	setAnnotations(annotations);
    }

    /**
     * Constructs a non-trivial <code>Appearance</code>.
     *
     * @param publication <code>Publication</code> where
     * this appeared
     */
    public Appearance(Publication publication,
		      String page,
		      String lines,
		      String appearance,
		      NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>[] nameUsages,
		      Annotation[] annotations)
    {
	this(publication, page, lines, appearance);
	setNameUsages(nameUsages);
	setAnnotations(annotations);
    }
    /**
     * Constructs a non-trivial <code>Appearance</code>.
     *
     * @param publication <code>Publication</code> where
     * this appeared
     */
    public Appearance(Publication publication,
		      String page,
		      String lines,
		      String appearance)
    {
	this();
	
	this.publication = publication;

	this.page        = page;

	this.lines = lines;

	this.appearance  = appearance;
	
    }

    /**
     * Constructs an <CODE>Appearance</CODE> object using XML data
     * given by <CODE>xml</CODE>
     *
     * @param xml <CODE>Element</CODE> specifying an <CODE>Appearance</CODE>
     *
     */
    public Appearance(Element xml)
    {
	this(xml, null);
    }

    /**
     * Constructs an <CODE>Appearance</CODE> object using XML data
     * given by <CODE>xml</CODE>
     *
     * @param xml <CODE>Element</CODE> specifying an <CODE>Appearance</CODE>
     *
     */
    public Appearance(Element xml, Publication pub)
    {
	super();
	NodeList nodeList = xml.getChildNodes();
	int nodeCount = nodeList.getLength();
	String persistentID = null;
	int authorCount = 0;
	boolean toBeResolved = false;
	List<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> nameUsageList= null;
	List<Annotation> annotationList = null;

	for (int j = 0; j < nodeCount; j++) {
	    Node node = nodeList.item(j);
	    if(node.getNodeType () == Node.ELEMENT_NODE) {
		Element element = (Element)node;
		String tagName = element.getTagName();

		if (tagName.equals ("oid"))
		    persistentID = getString(element);
		else if(tagName.equals ("page"))
		    page =  getString(element);
		else if(tagName.equals ("lines"))
		    lines = getString(element);
		else if(tagName.equals ("appearance"))
		    appearance = getString(element);
		else if(tagName.equals ("publication")) {
		    String pID = getString(element);
		    Publication p = null;
		    if(pub != null &&
		       pub.getLiteral().equals(pID))
			p = pub;
		    else{
			p = (Publication)curator.get(pID);
			if(p != null) {
			    publication = p;
			    toBeResolved = true;
			}
			else {
			    p = new Publication();
			    p.setLiteral(pID);
			    curator.put(p);
			    p.addAppearance(this);
			}
		    }
		}
		else if(tagName.equals ("annotation")) {
		    String pID = getString(element);
		    Annotation a =
			(Annotation)curator.get(pID);
		    if(a != null) {
			if(annotationList == null)
			    annotationList = new ArrayList<Annotation>();
			annotationList.add(a);
			toBeResolved = true;
		    }
		    else {
			a = new Annotation();
			a.setLiteral(pID);
			curator.put(a);
			addAnnotation(a);
		    }
		    /*
		    LinkRecord rec = new LinkRecord(appearance.persistentID(), getString(element));
		    AppearanceAnnotation.getInstance().addLinkRecord(rec);
		    */
		}
		else if(tagName.equals ("nameusage")) {
		    String pID = getString(element);
		    NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> n = (NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>)curator.get(pID);
		    if(n != null) {
			if(nameUsageList== null)
			    nameUsageList= new ArrayList<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>>();
			nameUsageList.add(n);
			//addNameUsage(n);
			toBeResolved = true;
		    }
		    else {
			n = new DefaultNameUsage();
			n.setLiteral(pID);
			curator.put(n);
			addNameUsage(n);
		    }

		    /*
		    LinkRecord rec = new LinkRecord(appearance.persistentID(), getString(element));
		    AppearanceNameUsage.getInstance().addLinkRecord(rec);
		    */
		}
		else{}
            }
        }

	if(nameUsageList != null) {
	    setNameUsages(nameUsageList);
	    nameUsageList.clear();
	}

	if(annotationList != null) {
	    setAnnotations(annotationList);
	    annotationList.clear();
	}

	if(persistentID != null &&
	   !persistentID.equals(getLiteral()))
	    setLiteral(persistentID); //i.e. other key data are empty

	if(toBeResolved)
	    curator.resolve(this);
    }

    /**
     * Returns a persistent ID representing this <CODE>Appearance</CODE>
     * with specified  <CODE>separator</CODE>.  It contains class name header
     * if <CODE>withClassName</CODE> true.
     * The subclasses must provide this method.
     *
     * @param separator <CODE>String</CODE> to be used as the field separator
     * @param withClassName <CODE>boolean</CODE> specifying with or without
     * class name header
     *
     * @return String representing this <CODE>Appearance</CODE>
     */
    public String getPersistentID(String separator, boolean withClassName)
    {
	if(entity != null)
	    return getEntity().getPersistentID();

	StringBuffer pid = null;
	if(withClassName)
	    pid = getClassNameHeaderBuffer();
	else
	    pid = new StringBuffer();

	if(page != null)
	    pid.append(page);
	pid.append(separator);
	if(lines != null)
	    pid.append(lines);
	pid.append(separator);
	if(publication == null){
	    //request code here;
	}
	if(publication == null)
	    pid.append(new Publication().getPersistentID(separator, false));
	else{
	    pid.append(publication.getPersistentID(separator, false));
	}
	return pid.toString();
    }
    
    /**
     * Returnes number of fields separators in persistent ID
     *
     * @return int representing number of fields separators in persistent ID
     */ 
    public int getFieldSepartorsCount()
    {
	return 8; //after page, line and 6 from Publication
    }

    /**
     * Merges <code>namedObject</code> with this <code>Appearance</code>
     * if possible, but not yet implemented.
     * It returns true if merged.
     *
     * @param namedObject a <code>NamedObject</code> to be merged
     *
     * @return true if merged, or false if not mergiable
     */
    public boolean merge(NamedObject<?, ?> namedObject)
    {
	if(!(namedObject instanceof Appearance))
	    return false;
	return false; //not yet implemented
    }
    
    /**
     * Parses a <code>line</code> and sets values of this object accordint to it
     *
     * @param line <code>String</code> containing fragment of data to be set
     */
    public void parseLine(String line)
    {
	String s = peal(line);
	if(line.startsWith("page"))
	    page = s;
	else if(line.startsWith("lines"))
	    lines = s;
	else if(line.startsWith("appearance"))
	    appearance = s;
	else if(line.startsWith("Publication"))
	    publication = new Publication(s);
	else if(line.startsWith("NameUsage"))
	    addNameUsage(new DefaultNameUsage(s));
	else if(line.startsWith("Annotation"))
	    addAnnotation(new Annotation(s));
    }
    
    /**
     * Returns <code>String</code> representing appearance.

     * appearance extractor
     * when appearance is not yet given,
     * extract it from persistentID() automatically.
     * once extracted, it will be used in later request
     */
    public String getAppearance()
    {
	if(entity != null)
	    return getEntity().getAppearance();
	/*
	if(appearance == null) {
	    appearance = persistentID().substring(getClassNameHeader().length(), string.indexOf(fieldSeparator));
	}
	*/
	return appearance;
    }

    /**
     * Sets <code>appearance</code> as name appearance
     *
     * @param appearance <code>String</code> to be set
     */
    public void setAppearance(String appearance)
    {
	if(entity != null)
	    getEntity().setAppearance(appearance);

    	if(this.appearance == appearance ||
	   (appearance != null && appearance.equals(this.appearance)))
	    return;

   	this.appearance = appearance;
    }
    

    
    /**
     * Returns page.
     * It extracts page from persistentID() automatically
     * when page is not yet given.
     * Once extracted, it will be used in later request.
     *
     * @return page <code>String</code> representing page
     */
    public String getPages()
    {
	if(entity != null)
	    return getEntity().getPages();

	/*
	if(page != null)
	    return page;

	String p = getPersistentIDComponent(0);

	if(p.length() > 0)
	    page = p;

	return page;
	*/

	if(!isNominal())
	    return page;

	return getPersistentIDComponent(0);
    }

    /**
     * Sets <code>page</code>
     *
     * @param page <code>String</code> to be set as page
     */
    public void setPages(String page)
    {
	if(entity != null)
	    getEntity().setPages(page);

    	if(this.page == page ||
	   (page != null && page.equals(this.page)))
	    return;

    	this.page = page;
    }
    

    /**
     * Returns <code>String</code> representing lines where names are appeared.
     *
     * @return <code>String</code> representing lines where names are appeared.
     */
    public String getLines()
    {
	if(entity != null)
	    return getEntity().getLines();

	/*
	if(lines != null)
	    return lines;

	String l = getPersistentIDComponent(1);

	if(l.length() > 0)
	    lines = l;

	return lines;
	*/

	if(!isNominal())
	    return lines;

	return getPersistentIDComponent(1);
    }

    /**
     * Sets <code>lines</code>
     *
     * @param lines <code>String</code> to be set as lines
     */
    public void setLines(String lines)
    {
	if(entity != null)
	    getEntity().setLines(lines);

    	if(this.lines == lines ||
	   (lines != null && lines.equals(this.lines)))
	    return;

    	this.lines = lines;
    }

    /**
     * Returns <code>Publication</code> containing this appearance
     * @return Publication containing this appearance
     */
    public Publication getPublication()
    {
	if(entity != null)
	    return getEntity().getPublication();

	return (Publication)getSource();
    }
    
    /**
     * Sets publication as the container of this appearance.
     *
     * @param publication <code>Publication</code> to be set as the container of this appearance
     */
    public void setPublication(Publication publication)
    {
	if(entity != null)
	    getEntity().setPublication(publication);
	/*
	if(this.publication == publication ||
	   (this != null && publication != null &&
	    getEntity() == publication.getEntity()))
	    return;

	if(this.publication != null) {
	    //set null to avoid infinity loop
	    Publication p = this.publication;
	    this.publication =  null;
	    p.removeAppearance(this);
	}

	this.publication =  publication;
	if(this.publication != null)
	    this.publication.addAppearance(this);
	*/
	Publication p = getPublication();
	if(p == publication ||
	   (this != null && publication != null &&
	    p.getEntity() == publication.getEntity()))
	    return;

	if(p != null) {
	    //set null to avoid infinity loop
	    setSource(null);
	    p.removeAppearance(this);
	}

	setSource(publication);
	if(publication != null)
	    publication.addAppearance(this);
    }
    
    /**
     * Returns <code>Enumeration</code> of <code>Annotation</code>s
     * on the page in lines, or null if none
     *
     * @return Enumearation of <code>Annotation</code>s 
     * on the page in lines, or null if none
     */
    public Collection<Annotation> getAnnotations()
    {
	if(entity != null)
	    return getEntity().getAnnotations();

	if(annotations != null) {
	    synchronized(annotations) {
		return new ArrayList<Annotation>(annotations);
	    }
	}

	else
	    return new ArrayList<Annotation>(0);
    }

    /**
     * Sets annotations as the <code>Annotation</code> list
     * @param annotation <code>Vector</code> representing <code>Annotation</code> list
     */
    public void setAnnotations(Enumeration<Annotation> annotations)
    {
	List<Annotation> annotationList = null;
	if(annotations != null) {
	    annotationList = new ArrayList<Annotation>();
	    while(annotations.hasMoreElements()) {
		annotationList.add(annotations.nextElement());
	    }
	}
	
	setAnnotations(annotationList);
    }
    
    /**
     * Sets annotations as the <code>Annotation</code> list
     * @param annotation <code>Collection</code> representing <code>Annotation</code> list
     */
    public void setAnnotations(Collection<Annotation> annotations)
    {
	Appearance appearance = getEntity();
	if(appearance != this) {
	    appearance.setAnnotations(annotations);
	    return;
	}

	if(this.annotations != null)
	    removeAnnotations();

	if(annotations == null || annotations.isEmpty())
	    return;

	synchronized(annotations) {
	    if(this.annotations == null)
		this.annotations = Collections.synchronizedList(new ArrayList<Annotation>(annotations));
	    else
		this.annotations.addAll(annotations);
	}

	synchronized(this.annotations) {
		for (Annotation annotation : annotations) {
		    annotation.setAppearance(this);
		}
	}
    }
  
    /**
     * Sets annotations as the <code>Annotation</code> list
     * @param annotation <code>Vector</code> representing <code>Annotation</code> list
     */
    public void setAnnotations(Annotation[] annotations)
    {
	Appearance appearance = getEntity();
	if(appearance != this) {
	    appearance.setAnnotations(annotations);
	    return;
	}

	if(this.annotations != null)
	    removeAnnotations();

	if(annotations == null || annotations.length == 0)
	    return;

	synchronized(annotations) {
	    if(this.annotations == null)
		this.annotations = Collections.synchronizedList(new ArrayList<Annotation>(annotations.length));
	    synchronized(this.annotations) {
		for (Annotation annotation : annotations) {
		    addAnnotation(annotation);
		}
	    }
	}
    }
  
    /**
     * Add <code>annotation</code> to <code>Annotation</code> list
     * if it is not contained in the list.
     *
     * @param annotation <code>Annotation</code> to be added
     */
    public boolean addAnnotation(Annotation annotation)
    {
	Appearance appearance = getEntity();
	if(appearance != this) {
	    return appearance.addAnnotation(annotation);
	}

	if(annotation == null)
	    return false;
	
	if(this.annotations == null)
	    this.annotations = Collections.synchronizedList(new ArrayList<Annotation>());

	boolean result = false;
	synchronized(this.annotations) {
	    if(!this.annotations.contains(annotation)) {
		result = annotations.add(annotation);
	    }
	}

	if(result)
	    annotation.setAppearance(this);

	return result;
    }

    /**
     * Remove <code>annotation</code> from <code>Annotation</code> list
     *
     * @param annotation <code>Annotation</code> to be added
     */
    public boolean removeAnnotation(Annotation annotation)
    {
	Appearance appearance = getEntity();
	if(appearance != this) {
	    return appearance.removeAnnotation(annotation);
	}

	if(annotation == null || this.annotations == null || !this.annotations.contains(annotation))
	    return false;
	
	synchronized(this.annotations) {
	    annotation.setAppearance(null);
	    return annotations.add(annotation);
	}
    }

    /*
     * Removes all <tt>Annotation</tt>s 
     */
    public void removeAnnotations()
    {
	Appearance appearance = getEntity();
	if(appearance != this) {
	    appearance.removeAnnotations();
	    return;
	}

	if(annotations == null || annotations.isEmpty())
	    return;

	synchronized(annotations) {
	    for (Annotation annotation : annotations) {
		annotation.setAppearance(null);
	    }
	    annotations.clear();
	}
    }
  
    /**
     * Returns <code>Enumeration</code> of <code>NameUsage</code>s
     * appeared on the page in lines, or null if none
     *
     * @return <code>Vector</code> representing <code>NameUsage</code> list
     * appeared on the page in lines, or null if none
     */
    public Collection<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> getNameUsages()
    {
	Appearance appearance = getEntity();
	if(appearance != this) {
	    return appearance.getNameUsages();
	}

	if(this.nameUsages != null && !this.nameUsages.isEmpty())
	    return new ArrayList<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>>(nameUsages);
	else
	    return new ArrayList<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>>(0);
    }
    
    /**
     * Sets nameUsages as <code>NameUsage</code> list
     *
     * @param nameRecrods <code>Vector</code> representing <code>NameUsage</code> List
     */
    public void setNameUsages(Enumeration<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> nameUsages)
    {
	List<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> nameUsageList= null;
	if(nameUsages != null) {
	    nameUsageList = new ArrayList<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>>();
	    while(nameUsages.hasMoreElements()) {
		nameUsageList.add(nameUsages.nextElement());
	    }
	}

	setNameUsages(nameUsageList);
    }

    /**
     * Sets nameUsages as <code>NameUsage</code> list
     *
     * @param nameRecrods <code>Vector</code> representing <code>NameUsage</code> List
     */
    public void setNameUsages(Collection<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> nameUsages)
    {
	Appearance appearance = getEntity();
	if(appearance != this) {
	    appearance.setNameUsages(nameUsages);
	   return;
	}

	removeNameUsages();

	if(nameUsages == null || nameUsages.isEmpty())
	    return;

	if(this.nameUsages == null)
	    this.nameUsages = Collections.synchronizedList(new ArrayList<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>>(nameUsages));
	else
	    this.nameUsages.addAll(nameUsages);

	for(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage : nameUsages) {
	    nameUsage.setAppearance(this);
	}
    }

    /**
     * Sets nameUsages as <code>NameUsage</code> list
     *
     * @param nameRecrods <code>Vector</code> representing <code>NameUsage</code> List
     */
    public void setNameUsages(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>[] nameUsages)
    {
	Appearance appearance = getEntity();
	if(appearance != this) {
	    appearance.setNameUsages(nameUsages);
	   return;
	}

	removeNameUsages();

	if(nameUsages == null || nameUsages.length == 0)
	    return;

	if(this.nameUsages == null)
	    this.nameUsages = Collections.synchronizedList(new ArrayList<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>>(nameUsages.length));

	for(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage : nameUsages) {
	    addNameUsage(nameUsage);
	}
    }

    /**
     * Add <code>nameUsage</code> to <code>NameUsage</code> list
     * with returning true if it is not contained in the list.
     * Otherwise it returs false without re-addition of 
     * <code>nameUsage</code> to the list
     *
     * @param nameUsage <code>NameUsage</code> to be added
     *
     * @return true if <code>nameUsage</code> added to the list successfully,
     * or false if not.
     */
    public boolean addNameUsage(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage)
    {
	Appearance appearance = getEntity();
	if(appearance != this) {
	    return appearance.addNameUsage(nameUsage);
	}

	if(nameUsage == null)
	    return false;

	if(this.nameUsages == null)
	    this.nameUsages = Collections.synchronizedList(new ArrayList<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>>());

	boolean result = false;
	synchronized(this.nameUsages) {
	    if(!this.nameUsages.contains(nameUsage)) {
	    result = this.nameUsages.add(nameUsage);
	    }
	}
	if(result)
	    nameUsage.setAppearance(this);

	return result;
    }
  
    /**
     * Removes <CODE>nameUsage</CODE> from the list of <CODE>NameUsage</CODE>s
     *
     * @param nameUsage <CODE>NameUsage</CODE> to be removed from the list of <CODE>NameUsage</CODE>s
     */
    public void removeNameUsage(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage)
    {
	Appearance appearance = getEntity();
	if(appearance != this) {
	    appearance.removeNameUsage(nameUsage);
	    return;
	}

	if(nameUsage == null || this.nameUsages == null ||  !this.nameUsages.contains(nameUsage))
	    return;

	nameUsage.setAppearance(null);
	this.nameUsages.remove(nameUsage);
    }

    /**
     * Clear the list of <CODE>NameUsage</CODE>s
     */
    public void removeNameUsages()
    {
	Appearance appearance = getEntity();
	if(appearance != this) {
	    appearance.removeNameUsages();
	    return;
	}

	if(this.nameUsages == null)
	    return;

	synchronized(this.nameUsages) {
	    for(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage : nameUsages) {
		nameUsage.setAppearance(null);
	    }
	}

	this.nameUsages.clear();
    }

    /**
     * Returns an expression of this <code>Appearance</code> as XML <code>String</code>
     *
     * @return XML String of this <code>Appearance</code>
     */
    public String toXML()
    {
	if(entity != null)
	    return getEntity().toXML();

	StringBuffer buf = new StringBuffer();
	buf.append("<Appearance>\n");

	synchronized (this) {
	    buf.append("<oid>" + getPersistentID() + "</oid>\n");
	    
	    if(page != null && page.length() > 0)
		buf.append("<page>" + page + "</page>\n");
	    
	    if(lines != null && lines.length() > 0)
		buf.append("<lines>" + lines + "</lines>\n");
	    
	    if(appearance != null && appearance.length() > 0)
		buf.append("<appearance>" + appearance + "</appearance>\n");
	    
	    Publication publication = getPublication();
	    if(publication != null) {
		buf.append("<publication>" + publication.getPersistentID() + "</publication>\n");
	    }
	    
	    if(nameUsages != null) {
		for(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage : nameUsages)
		    buf.append("<nameusage>" + nameUsage.getPersistentID() + "</nameusage>\n");
	    }

	    if(annotations != null) {
		for (Annotation annotation : annotations)
		    buf.append("<annotation>" + annotation.getPersistentID() + "</annotation>\n");
	    }
	}

	buf.append("</Appearance>\n");
	    
        return buf.toString();
    }
    
    /**
     * Returns XML <code>String</code> of the all related <code>NamedObject</code>s
     *
     * @return XML <code>String</code> representing all <code>NamedObject</code>s
     * relationg to this <code>Appearance</code>
     */
    public String toRelatedXMLString()
    {
	if(entity != null)
	    return getEntity().toRelatedXMLString();

	// create XML String of the Appearance itself
	StringBuffer buf = new StringBuffer();
	synchronized(this) {
	    buf.append(toXML());

	    // create XML of the Publication
	    if(publication != null) {
		buf.append(publication.toXML());
	    }
	    
	    // create XML of the NameUsages
	    if(nameUsages != null) {
		for(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage : nameUsages) {
		    buf.append(nameUsage.toXML());
		}
	    }
	    
	    // create XML of the Annotations
	    if(annotations != null) {
		for(Annotation annotation : annotations) {
		    buf.append(annotation.toXML());
		}
	    }
	}
	return buf.toString();
    }

    /**
     * Sets valuse of this <CODE>NamedObject</CODE> to
     * <CODE>statement</CODE> using <CODE>connection</CODE>.
     * from specified <CODE>index</CODE> of the <CODE>statement</CODE>
     *
     * @param statement <CODE>PraredStatement</CODE> to which
     * value of the this <CODE>NamedObject</CODE> to be set
     * @param connection <CODE>NamedObjectConnection</CODE>
     * to be used to set values
     * @param index <CODE>int</CODE> from where values to be set
     * into the <CODE>statement</CODE>
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

    public String[] toSQL()
    {
	if(entity != null)
	    return getEntity().toSQL();

	String[] sql = new String[3];
	int i = 0;
	sql[i++] = getPersistentID();
	sql[i++] = page;
	//	sql[i++] = lines;
	sql[i++] = appearance;

	return sql;
    }

    /**
     * Returns a summarized expression of this <CODE>NamedObject</CODE>
     *
     * @return String representing summary of this <CODE>NamedObject</CODE>
     */
    public String getSummary()
    {
	if(entity != null)
	    return getEntity().getSummary();

	StringBuffer buffer = new StringBuffer();
	Publication publication = getPublication();
	if(publication != null)
	    publication.getCanonicalAuthorYearCitation(buffer);

	String p = getPages();
	if(p != null && p.length() > 0) {
	    if(buffer.length() > 0)
		buffer.append(' ');
	    buffer.append("p.").append(p); //need I18N?
	}

	String l = getLines();
	if(l != null && l.length() > 0) {
	    if(buffer.length() > 0)
		buffer.append(' ');
	    buffer.append("l.").append(l); //need I18N?
	}

	if(buffer.length() > 0)
	    return buffer.toString();
	else 
	    return "";
    }

}
