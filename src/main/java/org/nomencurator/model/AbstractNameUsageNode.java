/*
 * AbstractNameUsageNode.java:  a Java implementation of NameUsageNode class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;

//import org.nomencurator.sql.NamedObjectConnection;
import org.nomencurator.io.sql.NamedObjectConnection;

import org.w3c.dom.Element;

/**
 * An implementation of <code>NameUsageNode</code> in Nomencurator
 * data model.  It was referred to as NRnode in the original publication.
 * It wraps a <code>NameUsage</code> for efficient navigation over 
 * annotated <code>NameUsage</code>s.
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org</A>
 *
 * @version 	24 June 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractNameUsageNode<N extends NameUsageNode<?, ?>, T extends N>
    extends AbstractNameUsage <N, T>
    implements NameUsageNode<N, T>, Observer 
{
    private static final long serialVersionUID = -5158717413357780340L;

    /**
     * <tt>Hashtable</tt> holding pointer to
     * <tt>Annotation</tt>s used to refer to
     * this object.
     */ 
    protected Set<Annotation> relevantAnnotations;

    /**
     * <tt>Hashtable</tt> holding pointers to
     * <tt>Annotation</tt> used to refer to
     * this object.  Link type of <tt>Annotation</tt>s
     * are used as keys.
     */ 
    protected Map<String, Set<Annotation>>annotationsByLinkType;

    /**
     * <tt>Hashtable</tt> holding pointer to
     * <tt>NameUsageNode</tt>s referring to or
     * referred to by this object.  
     * <tt>NameUsageNode</tt> is used as a key.
     */ 
    protected Set<NameUsageNode<N, ? extends T>> relevantNodes;

    /**
     * <tt>Hashtable</tt> holding pointer to
     * <tt>NameUsageNode</tt>s referring to or
     * referred to by this object.  <tt>Annotation</tt>
     * referring to the <tt>NameUsage</tt> is used as a key.
     */ 
    protected Map<Annotation, Set<NameUsageNode<N, ? extends T>>> nodesByAnnotation;

    /**
     * <tt>Hashtable</tt> holding pointer to
     * <tt>NameUsageNode</tt>s referring to or
     * referred to by this object.  Ascribed name
     * of <tt>NameUsage</tt> is used as a key.
     */ 
    protected Map<String, Map<NameUsageNode<N, ? extends T>, Annotation>> nodesByName;


    /**
     * <tt>Hashtable</tt> holding pointer to
     * <tt>NameUsageNode</tt>s referring to or
     * referred to by this object.  The contents of
     * persistent ID, i.e. part of a PID other than
     * class name, of <tt>NameUsageNode</tt>
     * is used as a key.
     */ 
    protected Map<String, Map<NameUsageNode<N, ? extends T>, Annotation>> nodesByPID;

    /** Constructs an "empty" <code>NameUsageNode</code> */
    protected AbstractNameUsageNode()
    {
	super();
    }

    /**
     * Constructs a <COCE>NameUsageNode</tt> using
     * existing <tt>NameUsage</tt>
     *
     * @param nameUsage existing <tt>NameUsage</tt>
     * to be pointed by this object
     */
    protected AbstractNameUsageNode(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage)
    {
	super(nameUsage);
	initializeReferants();
    }

    /**
     * Constructs an "empty" <code>NameUsageNode</code>
     * appeared in <tt>appearance</tt>
     *
     * @param appearance <tt>Appearance</tt> where the name used
     */
    protected AbstractNameUsageNode(Appearance apperance)
    {
	super(apperance);
    }

    /**
     * Constructs a <code>NameUsageNode</code> object having
     * <code>persistentID</code> as its representation,
     */
    protected AbstractNameUsageNode(String persistentID)
    {
	super(persistentID);
    }

    /**
     * Constructs a <code>NameUsageNode</code> based on
     * <code>nameUsage</code>
     */
    protected AbstractNameUsageNode(Name<N, ? extends T> nameUsage)
    {
	super(nameUsage);
	initializeReferants();
    }

    /**
     * Constructs a <tt>NameUsage</tt> by giving
     * its attributes.
     *
     * @param rank <tt>String</tt> indicating name of rank
     * @param name <tt>String</tt> indicating ascribed name
     * @param auth <tt>Name</tt> of authoritative name usage
     * @param rec  <tt>Name</tt> of recording name usage
     * @param type boolean, true if the <tt>NameUsage</tt> is name bearing type
     * @param higher <tt>Name</tt> of higher taxon
     * @param lower array of lower taxa's <tt>Name</tt>s
     */
    protected AbstractNameUsageNode(String rank, String name,
				    Name<N, T> auth, Name<N, T> rec,
				    boolean type,
				    Name<N, T> higher, Name<N, T> [] lower)
    {
	super(rank, name, auth, rec, type, higher, lower);
    }

    /**
     * Constructs an <tt>NameUage</tt> object using XML data
     * given by <tt>xml</tt>
     *
     * @param xml <tt>Element</tt> specifying a <tt>NameUsage</tt>
     *
     */
    protected AbstractNameUsageNode(Element xml)
    {
	super(xml);
	initializeReferants();
    }

    /**
     * Constructs an <tt>NameUage</tt> object using XML data
     * given by <tt>xml</tt>
     *
     * @param xml <tt>Element</tt> specifying a <tt>NameUsage</tt>
     * @param appearance <tt>Appearance</tt> where the name used
     */
    protected AbstractNameUsageNode(Element xml, Appearance ap)
    {
	super(xml, ap);
	initializeReferants();
    }

    /**
     * Reteruns class name without package part
     *
     * @return class name without package part
     */
    public String getClassName()
    {
	return "NameUsageNode";
    }

    public String getPersistentID()
    {
	StringBuffer buffer = getClassNameHeaderBuffer();
	String pid = super.getPersistentID();
	buffer.append(pid.substring(pid.indexOf(classNameSeparator) + classNameSeparator.length()));
	return buffer.toString();
    }

    protected void initializeReferants()
    {
	Collection<Annotation> annotations = getAnnotations();
	if(annotations == null)
	    return;
	for(Annotation annotation : annotations)
	    addRelevantAnnotation(annotation);
    }

    /**
     * Adds <code>Annotation</code> to the list of <code>Annotation</code>s made by this <code>NameUsage</code>
     * It returns true if the <code>annotation</code> added to the
     * list successfuly, or false if the <code>annotation</code> is
     * already in the list.
     *
     * @param annotation <code>Annotation</code> to be added to the list of <code>Annotation</code>s made by this <code>NameUsage</code>
     *
     * @return true if <code>annotation</code> was appended to the list of <code>Annotation</code>s made by this <code>NameUsage</code>
     * successfully, or false if <code>annotation</code> is already in the list
     */
    public boolean addAnnotation(Annotation annotation)
    {
	if(annotation == null)
	    throw new IllegalArgumentException(getClass().getName() + 
					       "#addAnnotation(): null argument");

	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this)
	    return n.addAnnotation(annotation);

	boolean ret = super.addAnnotation(annotation);

	if(ret)
	    addRelevantAnnotation(annotation);

	return ret;
    }

    /**
     * Removes <code>annotation</code> from the list of <code>Annotation</code>s made by this <code>NameUsage</code>
     *
     * @param annotation <code>NameUsage</code> to be removed fromthe list of <code>Annotation</code>s made by this <code>NameUsage</code>
     */
    public boolean removeAnnotation(Annotation annotation)
    {
	if(annotation == null)
	    /*
	    throw new IllegalArgumentException(getClass().getName() + 
					       "#removeAnnotation(): null argument");
	    */
	    return false;

	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this) {
	    return n.removeAnnotation(annotation);
	}

	return removeRelevantAnnotation(annotation) & super.removeAnnotation(annotation);
    }

    /**
     * Returns <tt>Collection</tt> of 
     * relevant <tt>Annotations</tt>, or null
     * if there is no <tt>Annotations</tt>
     * relevant to this <tt>NameUsageNode</tt>
     *
     * @return Collection of relevant <tt>Annotation</tt>s
     */
    public Collection<Annotation> getRelevantAnnotations()
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this)
	    return n.getRelevantAnnotations();

	return relevantAnnotations;
    }


    /**
     * Returns <tt>Collection</tt> of 
     * relevant <tt>Annotations</tt> with specified
     * <tt>linkType</tt>, or null
     * if there is no <tt>Annotations</tt> having
     * <tt>linkType</tt>.
     *
     * @param linkType link type of <tt>Annotation</tt> to be
     * returned
     *
     * @return Collection of <tt>Annotation</tt>s having
     * <tt>linkType</tt>
     */
    public Collection<Annotation> getRelevantAnnotations(String linkType)
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this)
	    return n.getRelevantAnnotations();

	return annotationsByLinkType.get(linkType);
    }

    /**
     * Adds <tt>annotation</tt> to the list of
     * <tt>Annotation</tt>s relevant to the
     * <tt>NameUsage</tt> and provide cross
     * references between relevant <tt>NameUsage</tt>s
     *
     * @param annotation <tt>Annotation</tt> to be added to
     * the list of relevant <tt>Annotation</tt>s
     *
     * @return number of <tt>NameUsageNode</tt> added
     */
    public int addRelevantAnnotation(Annotation annotation)
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this)
	    return n.addRelevantAnnotation(annotation);

	if(annotation == null)
	    throw new IllegalArgumentException(getClass().getName() + 
					       "#addRelevantAnnotation(): null argument");

	if(relevantAnnotations == null) {
	    relevantAnnotations   = Collections.synchronizedSet(new HashSet<Annotation>());
	    annotationsByLinkType = Collections.synchronizedMap(new HashMap<String, Set<Annotation>>());
	    nodesByAnnotation     = Collections.synchronizedMap(new HashMap<Annotation, Set<NameUsageNode<N, ? extends T>>>());
	}

	relevantAnnotations.add(annotation);
	annotation.addObserver(this);

	String linkType = annotation.getLinkType();
	Set<Annotation> byLinkType = annotationsByLinkType.get(linkType);
	if(byLinkType == null) {
	    byLinkType = Collections.synchronizedSet(new HashSet<Annotation>());
	    annotationsByLinkType.put(linkType, byLinkType);
	}
	byLinkType.add(annotation);

	return addRelevantNodes(annotation);
    }

    /**
     * Removes <tt>annotation</tt> from the list of
     * <tt>Annotation</tt>s relevant to the
     * <tt>NameUsage</tt> and cross
     * references between relevant <tt>NameUsage</tt>s
     *
     * @param annotation <tt>Annotatation</tt> to be remvoed
     */
    public boolean removeRelevantAnnotation(Annotation annotation)
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this) {
	    return n.removeRelevantAnnotation(annotation);
	}

	if(annotation == null)
	    /*
	    throw new IllegalArgumentException(getClass().getName() + 
					       "#removeRelevantAnnotation(): null argument");
	    */
	    return false;

	removeRelevantNodes(annotation);

	String linkType = annotation.getLinkType();
	Set<Annotation> byLinkType = annotationsByLinkType.get(linkType);
	if(byLinkType == null)
	    return false;

	byLinkType.remove(annotation);
	if(byLinkType.isEmpty()) {
	    annotationsByLinkType.remove(linkType);
	}

	boolean result = relevantAnnotations.remove(annotation);
	annotation.deleteObserver(this);

	/*
	if(relevantAnnotations.isEmpty()) {
	    relevantAnnotations = null;
	    annotationsByLinkType = null;
	    nodesByAnnotation = null;
	}
	*/

	return result;
    }

    /**
     * Returns <tt>Enumeration</tt> of 
     * relevant <tt>NameUsageNode</tt>s, or null
     * if there is no <tt>NameUsageNode</tt>
     * relevant to this <tt>NameUsageNode</tt>
     *
     * @return Enumeration of relevant <tt>NameUsageNode</tt>
     * or null if none
     */
    //public Collection<NameUsageNode<? extends N, ? extends T>> getRelevantNodes()
    public Collection<NameUsageNode<N, ? extends T>> getRelevantNodes()
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this)
	    return n.getRelevantNodes();

	return relevantNodes;
    }

    /**
     * Returns <tt>Collection</tt> of 
     * relevant <tt>NameUsageNode</tt>s with
     * <tt>name</tt>, or null
     * if there is no <tt>NameUsageNode</tt>
     * ascribed as <tt>name</tt>
     *
     * @param name ascribed name of <tt>NameUsageNode</tt>s
     *
     * @return Collection of relevant <tt>NameUsageNode</tt>
     * with <tt>name</tt> or null if none
     */
    public Collection <NameUsageNode<N, ? extends T>>getRelevantNodesOfName(String name)
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this)
	    return n.getRelevantNodesOfName(name);

	return nodesByName.get(name).keySet();
    }

    /**
     * Returns <tt>Enumeration</tt> of 
     * relevant <tt>NameUsageNode</tt>s with
     * persisntet ID <tt>id</tt>, or null
     * if there is no <ptt>NameUsageNode</tt>
     * ascribed as <tt>name</tt>.
     * Class name part of <tt>id</tt> will be dropped
     * when looking for <tt>NameUsageNode</tt>s so
     * persistent ID of either <tt>NameUsage</tt>
     * <tt>NameUsageNode</tt> will accepted.
     *
     * @param id persisntet ID of <tt>NameUsageNode</tt>s
     *
     * @return Enumeration of relevant <tt>NameUsageNode</tt>
     * with <tt>id</tt> or null if none
     */
    public Collection<NameUsageNode<N, ? extends T>> getRelevantNodesOfID(String id)
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this)
	    return n.getRelevantNodesOfName(id);

	return nodesByPID.get(getPersistentIDContents(id)).keySet();
    }

    /**
     * Adds <tt>NameUsageNode</tt>s relevant to 
     * <tt>NameUsage</tt> referred to by <tt>annotation</tt>
     *
     * @param annotation <tt>Annotation</tt> where relevant
     * <tt>NameUsage</tt> should be retrieved
     *
     * @param annotation <tt>Annotation</tt> to be added
     *
     * @return number of <tt>NameUsageNode</tt> added
     */
    public int addRelevantNodes(Annotation annotation)
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this)
	    return n.addRelevantNodes(annotation);

	Set<NameUsageNode<N, ? extends T>> nodeSet = nodesByAnnotation.get(annotation);
	if(nodeSet == null) {
	    nodeSet = Collections.synchronizedSet(new HashSet<NameUsageNode<N, ? extends T>>());
	    nodesByAnnotation.put(annotation, nodeSet);
	}

	int added = 0;
	added += addRelevantNodes(annotation, annotation.getAnnotators(), nodeSet);
	added += addRelevantNodes(annotation, annotation.getAnnotatants(), nodeSet);

	return added;
    }

    /**
     * Adds <tt>NameUsageNode</tt>s representing
     * <tt>NameUsage</tt>s enumerated in <tt>nodes</tt>
     * to the list of <tt>NameUsageNode</tt> relevant to this,
     * with pointer to <tt>annotation</tt>.
     *
     * @param annotation <tt>Annotation</tt> to be added
     * @param nodes <tt>Enumeration</tt> of <tt>NameUsage</tt>
     * to be added
     * @param nodeList <tt>Hashtable</tt> 
     * to hold <tt>NameUsageNode</tt>s 
     * relevant to the <tt>annotation</tt>
     *
     * @return number of <tt>NameUsageNode</tt> added
     */
    public int addRelevantNodes(Annotation annotation,
				Collection<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> nodes,
				Set<NameUsageNode<N, ? extends T>> nodeSet)
    {
	return addRelevantNodes(annotation, nodes.iterator(), nodeSet);
    }
    
    /**
     * Adds <tt>NameUsageNode</tt>s representing
     * <tt>NameUsage</tt>s enumerated in <tt>nodes</tt>
     * to the list of <tt>NameUsageNode</tt> relevant to this,
     * with pointer to <tt>annotation</tt>.
     *
     * @param annotation <tt>Annotation</tt> to be added
     * @param nodes <tt>Enumeration</tt> of <tt>NameUsage</tt>
     * to be added
     * @param nodeList <tt>Hashtable</tt> 
     * to hold <tt>NameUsageNode</tt>s 
     * relevant to the <tt>annotation</tt>
     *
     * @return number of <tt>NameUsageNode</tt> added
     */
    public int addRelevantNodes(Annotation annotation,
				Iterator<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> nodes,
				Set<NameUsageNode<N, ? extends T>> nodeSet)
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this)
	    return n.addRelevantNodes(annotation, nodes, nodeSet);

	if(nodes == null || !nodes.hasNext())
	    return 0;
	
	if(relevantNodes == null) {
	    relevantNodes = Collections.synchronizedSet(new HashSet<NameUsageNode<N, ? extends T>>());
	    nodesByName  = Collections.synchronizedMap(new HashMap<String, Map<NameUsageNode<N, ? extends T>, Annotation>>());
	    nodesByPID  = Collections.synchronizedMap(new HashMap<String, Map<NameUsageNode<N, ? extends T>, Annotation>>());
	}

	int i = 0;

	while(nodes.hasNext()) {
	    NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> usage = nodes.next();
	    if(usage == null || usage == this || 
	       usage.getEntity() == getNameUsage().getEntity() ||
	       usage.getPersistentID().equals(getNameUsageID())
	       )
		continue;

	    NameUsageNode<N, T> node = getNameUsageNode(usage);

	    if(node.getPersistentID().equals(getPersistentID()))
		continue;

	    nodeSet.add(node);
	    relevantNodes.add(node);

	    String key = node.getLiteral();
	    Map<NameUsageNode<N, ? extends T>, Annotation> annotationMap = nodesByName.get(key);
	    if(annotationMap == null) {
		annotationMap = Collections.synchronizedMap(new HashMap<NameUsageNode<N, ? extends T>, Annotation>());
		nodesByName.put(key, annotationMap);
	    }
	    annotationMap.put(node, annotation);
	    
	    key = node.getPersistentIDContents();
	    annotationMap = nodesByPID.get(key);
	    if(annotationMap == null) {
		annotationMap = Collections.synchronizedMap(new HashMap<NameUsageNode<N, ? extends T>, Annotation>());
		nodesByPID.put(key, annotationMap);
	    }
	    annotationMap.put(node, annotation);

	    i++;
	}

	return i;
    }

    /**
     * Removes <tt>NameUsageNode</tt>s relevant to
     * <tt>annotation</tt> from cross
     * references between them.
     *
     * @param annotation to which relevant <tt>NameUsageNode</tt>s 
     * to be removed
     *
     * @return number of removed <tt>NameUsageNode</tt>s 
     */
    public int removeRelevantNodes(Annotation annotation)
    {
	int i = 0;

	Set<NameUsageNode<N, ? extends T>> keys = nodesByAnnotation.get(annotation);

	for(NameUsageNode<N, ? extends T> node: keys) {
	    String key = node.getPersistentIDContents();
	    Map<NameUsageNode<N, ? extends T>, Annotation> nodes = nodesByPID.get(key);
	    i += removeRelevantNodes(annotation, nodes);
	    if(nodes.isEmpty())
		nodesByPID.remove(key);

	    key = node.getLiteral();
	    nodes = nodesByName.get(key);
	    i += removeRelevantNodes(annotation, nodes);
	    if(nodes.isEmpty())
		nodesByName.remove(key);
		
	}

	nodesByAnnotation.get(annotation).clear();
	nodesByAnnotation.remove(annotation);
	
	return i;
    }

    /**
     * Removes <tt>annotation</tt> from the list of
     * <tt>Annotation</tt>s relevant to then
     * <tt>NameUsage</tt> and cross
     * references between relevant <tt>NameUsage</tt>s
     *
     * @param annotation <tt>Annotation</tt> of which 
     * relevant <tt>NameUsageNode</tt> to be remvoed
     * @param nodes <tt>Hashtable</tt> from which 
     * <tt>NameUsageNode</tt> relevant to 
     * <tt>annotation</tt> to be remvoed
     *
     */
    public int removeRelevantNodes(Annotation annotation, Map<NameUsageNode<N, ? extends T>, Annotation> nodes)
    {
	Set<NameUsageNode<N, ? extends T>> toBeRemoved = new HashSet<NameUsageNode<N, ? extends T>>();

	for(NameUsageNode<N, ? extends T> key : nodes.keySet()) {
	    if(annotation == nodes.get(key)) {
		toBeRemoved.add(key);
	    }
	}

	int i = 0;
	for(NameUsageNode<N, ? extends T> key : toBeRemoved) {
	    nodes.remove(key);
	    i++;
	}
	
	toBeRemoved.clear();
	
	return i;
    }

    protected /*static*/ String convertToNameUsageNodePID(String nameUsagePID)
    {
	StringBuffer pid = getClassNameHeaderBuffer();
	pid.append(getPersistentIDContents(nameUsagePID));
	return pid.toString();
    }

    /**
     * Returns persistent ID as a <tt>NameUsage</tt>,
     * instead of as <tt>NameUsageNode</tt>
     *
     * @return String persisten ID of this as a <tt>NameUsage</tt>
     */
    public String getNameUsageID()
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this)
	    return n.getNameUsageID();

	if(entity != null)
	    return ((NameUsage)entity).getPersistentID();

	return getPersistentID();
    }

    /**
     * Returns <code>NameUsageNode</code> representing higher name usage
     *
     * @return <code>NameUsageNode</code> representing higher name usage
     */
    public NameUsageNode<N, T> getHigherNameUsageNode()
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this) 
	    return n.getHigherNameUsageNode();

	NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage = getHigherNameUsage();
	if(nameUsage == null ||
	   nameUsage instanceof NameUsageNode)
	    return getNameUsageNode(nameUsage);

	/*
	NameUsageNode node = 
	    getNameUsageNode(nameUsage)
	*/

	NameUsageNode<N, T> node = createNameUsageNode(nameUsage);
	setHigherNameUsage(node);
	return node;
    }

    /**
     * Sets <code>nameUsage</code> as higher name usage
     *
     * @param nameUsage <code>NameUsage</code> representing higher name uage
     */
    public boolean setHigherNameUsage(NameUsage<N, T> higherNameUsage)
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this) {
	    return n.setHigherNameUsage(higherNameUsage);
	}

	if(higherNameUsage != null &&  !isAssignableFrom(higherNameUsage) && isInstance(higherNameUsage)) {
	    higherNameUsage = createNameUsageNode(getNameUsage(higherNameUsage));
	}

	return super.setHigherNameUsage(higherNameUsage);

    }

    /**
     * Adds <code>nameUsage</code> to the list of lower taxa.
     * It returns true if the <code>nameUsage</code> added to the
     * list successfuly, or false if the <code>nameUsage</code> is
     * already in the list.
     *
     * @param nameUsage <code>NameUsage</code> to be added to the list of lower taxa
     *
     * @return true if <code>nameUsage</code> was appended to the list of lower taxa
     * successfully, or false if <code>nameUsage</code> is already in the list
     */
    public boolean addLowerNameUsage(NameUsage<N, T> nameUsage)
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this)
	    return n.addLowerNameUsage(nameUsage);

	if(isAssignableFrom(nameUsage))
	    return super.addLowerNameUsage(getNameUsageNode(nameUsage));

	return false;
    }

    /**
     * Sets <code>sensu</code> as authority <code>NameUsage</code> of this object.
     *
     * @param sensu <code>NameUsage</code> representing authority of this <code>NameUsage</code>
     */
    public void setSensu(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> sensu)
    {
	NameUsageNode<N, T> n = getNameUsageNode();

	if(n != this)
	    n.setSensu(sensu);
	else
	    super.setSensu(getNameUsageNode(sensu));
    }

    /**
     * Sets <tt>type</tt> as type of this <tt>NameUsage</tt>.
     * The  <tt>type</tt> may be null.
     *
     * @param type <tt>NameUsage</tt> to be designated as the type of
     * this <tt>NameUsage</tt>
     */
    public void setType(NameUsage<N, ? extends T> typeUsage)
    {
	NameUsageNode<N, T> n = getNameUsageNode();
	if(n != this)
	    n.setTypeUsage(typeUsage);
	else
	    super.setTypeUsage(getNameUsageNode(typeUsage));
    }

    /**
     * Converts <tt>usage</tt> to <tt>NameUsageNode</tt>.
     * If <tt>usage</tt> is an instance of <tt>NameUsageNode</tt>,
     * it does nothing.
     *
     * @param usage <tt>NameUsage</tt> to be converted.
     *
     * @return NameUsageNode representing <tt>uasge</tt>
     */
    public NameUsageNode<N, T> getNameUsageNode(Object object)
    {
	if(object == null)
	    return null;

	if(object instanceof NameUsageNode)
	    return getNameUsageNode(object);

	// one shouldn't expect well-named NameUsage argument because
	// it may be under construction
	/*
	NameUsageNode node = 
	    (NameUsageNode)Nomencurator.getInstance().get(convertToNameUsageNodePID(usage.getPersistentID()));

	if(node == null) {
	    node = createNameUsageNode(usage);
	    Nomencurator.getInstance().put(node);
	}
	*/

	//if(object instanceof NameUsage<? extends N, ? extends T>)
	if(object instanceof NameUsage)
	    return createNameUsageNode(getNameUsage(object));

	if(isAssignableFrom(object))
	    return getNameUsageNode(getClass().cast(object));
	else
	    return null;
    }

    public void update(Observable obs, Object arg)
    {
	
    }


    /**
     * Sets valuse of this <tt>NamedObject</tt> to
     * <tt>statement</tt> using <tt>connection</tt>.
     * from specified <tt>index</tt> of the <tt>statement</tt>
     *
     * @param statement <tt>PraredStatement</tt> to which
     * value of the this <tt>NamedObject</tt> to be set
     * @param connection <tt>NamedObjectConnection</tt>
     * to be used to set values
     * @param index <tt>int</tt> from where values to be set
     * into the <tt>statement</tt>
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
    
    public AbstractNameUsageNode<N, T> create()
    {
	return createNameUsageNode();
    }

    protected abstract AbstractNameUsageNode<N, T> createNameUsageNode();

    protected abstract AbstractNameUsageNode<N, T> createNameUsageNode(NameUsage<?, ?> nameUsage);

    protected abstract AbstractNameUsageNode<N, T> createNameUsageNode(String persistentID);

    protected AbstractNameUsage<N, T> createNameUsage()
    {
	return createNameUsageNode();
    }

    protected AbstractNameUsage<N, T> createNameUsage(NameUsage<?, ?> nameUsage)
    {
	return createNameUsageNode(nameUsage);
    }

    protected AbstractNameUsage<N, T> createNameUsage(String persistentID)
    {
	return createNameUsageNode(persistentID);
    }

    protected AbstractNameUsage<N, T> createNameUsage(Name<?, ?> name)
    {
	if (name instanceof NameUsage)
	    return createNameUsageNode((NameUsage<?, ?>)name);
	return createNameUsage();
    }

    public NameUsageNode<N, T> clone()
    {
	AbstractNameUsageNode<N, T> node = create();
	copyTo(node);
	return node;
    }

    protected void copy(AbstractNameUsageNode<N, T> source)
    {
	source.copyTo(this);
    }

    protected void copyTo(AbstractNameUsageNode<N, T> dest)
    {
	super.copyTo((AbstractNameUsage<N, T>) dest);
    }

   /**
     * Returns <tt>NameUsageNode</tt> proxied by this <tt>NameUsagNode</tt>
     *
     * @return NameUsageNode proxied by this <tt>NameUsagNode</tt>
     */
    public NameUsageNode<N, T> getNameUsageNode()
    {
	if(entity == null || !isAssignableFrom(entity))
	    return this;
	/*
	if(entity instanceof NameUsageNode) {
	    //return ((NameUsage)entity).getNameUsage();
	    return getNameUsageNode(entity);
	}
	return null;
	*/
	return getNameUsageNode(entity).getNameUsageNode();
    }

}
