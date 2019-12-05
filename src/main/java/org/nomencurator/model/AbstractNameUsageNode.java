/*
 * AbstractNameUsageNode.java:  a Java implementation of NameUsageNode class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2014, 2015, 2016, 2019 Nozomi `James' Ytow
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
import java.util.Set;
import java.util.Vector;

import org.nomencurator.io.sql.NamedObjectConnection;

import org.w3c.dom.Element;

/**
 * An implementation of {@code NameUsageNode} in Nomencurator
 * data model.  It was referred to as NRnode in the original publication.
 * It wraps a {@code NameUsage} for efficient navigation over 
 * annotated {@code NameUsage}s.
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org</A>
 *
 * @version 	06 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractNameUsageNode<T extends NameUsageNode<?>>
    extends AbstractNameUsage <T>
    implements NameUsageNode<T>, PropertyChangeListener
{
    private static final long serialVersionUID = -5158717413357780340L;

    /**
     * {@code Hashtable} holding pointer to
     * {@code Annotation}s used to refer to
     * this object.
     */ 
    protected Set<Annotation> relevantAnnotations;

    /**
     * {@code Hashtable} holding pointers to
     * {@code Annotation} used to refer to
     * this object.  Link type of {@code Annotation}s
     * are used as keys.
     */ 
    protected Map<String, Set<Annotation>>annotationsByLinkType;

    /**
     * {@code Hashtable} holding pointer to
     * {@code NameUsageNode}s referring to or
     * referred to by this object.  
     * {@code NameUsageNode} is used as a key.
     */ 
    protected Set<NameUsageNode<?/* extends T*/>> relevantNodes;

    /**
     * {@code Hashtable} holding pointer to
     * {@code NameUsageNode}s referring to or
     * referred to by this object.  {@code Annotation}
     * referring to the {@code NameUsage} is used as a key.
     */ 
    protected Map<Annotation, Set<NameUsageNode<?/* extends T*/>>> nodesByAnnotation;

    /**
     * {@code Hashtable} holding pointer to
     * {@code NameUsageNode}s referring to or
     * referred to by this object.  Ascribed name
     * of {@code NameUsage} is used as a key.
     */ 
    protected Map<String, Map<NameUsageNode<?/* extends T*/>, Annotation>> nodesByName;


    /**
     * {@code Hashtable} holding pointer to
     * {@code NameUsageNode}s referring to or
     * referred to by this object.  The contents of
     * persistent ID, i.e. part of a PID other than
     * class name, of {@code NameUsageNode}
     * is used as a key.
     */ 
    protected Map<String, Map<NameUsageNode<?/* extends T*/>, Annotation>> nodesByPID;

    /** Constructs an "empty" {@code NameUsageNode} */
    protected AbstractNameUsageNode()
    {
	super();
    }

    /**
     * Constructs a <COCE>NameUsageNode} using
     * existing {@code NameUsage}
     *
     * @param nameUsage existing {@code NameUsage}
     * to be pointed by this object
     */
    protected AbstractNameUsageNode(NameUsage<?> nameUsage)
    {
	super(nameUsage);
	initializeReferants();
    }

    /**
     * Constructs an "empty" {@code NameUsageNode}
     * appeared in {@code appearance}
     *
     * @param appearance {@code Appearance} where the name used
     */
    protected AbstractNameUsageNode(Appearance apperance)
    {
	super(apperance);
    }

    /**
     * Constructs a {@code NameUsageNode} object having
     * {@code persistentID} as its representation,
     */
    protected AbstractNameUsageNode(String persistentID)
    {
	super(persistentID);
    }

    /**
     * Constructs a {@code NameUsageNode} based on
     * {@code nameUsage}
     */
    protected AbstractNameUsageNode(Name<? extends T> nameUsage)
    {
	super(nameUsage);
	initializeReferants();
    }

    /**
     * Constructs a {@code NameUsage} by giving
     * its attributes.
     *
     * @param rank {@code String} indicating name of rank
     * @param name {@code String} indicating ascribed name
     * @param auth {@code Name} of authoritative name usage
     * @param rec  {@code Name} of recording name usage
     * @param type boolean, true if the {@code NameUsage} is name bearing type
     * @param higher {@code Name} of higher taxon
     * @param lower array of lower taxa's {@code Name}s
     */
    protected <N extends T>AbstractNameUsageNode(String rank, String name,
				    Name<N> auth, Name<N> rec,
				    boolean type,
				    Name<N> higher, Name<N> [] lower)
    {
	super(rank, name, auth, rec, type, higher, lower);
    }

    /**
     * Constructs an {@code NameUage} object using XML data
     * given by {@code xml}
     *
     * @param xml {@code Element} specifying a {@code NameUsage}
     *
     */
    protected AbstractNameUsageNode(Element xml)
    {
	super(xml);
	initializeReferants();
    }

    /**
     * Constructs an {@code NameUage} object using XML data
     * given by {@code xml}
     *
     * @param xml {@code Element} specifying a {@code NameUsage}
     * @param appearance {@code Appearance} where the name used
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
     * Adds {@code Annotation} to the list of {@code Annotation}s made by this {@code NameUsage}
     * It returns true if the {@code annotation} added to the
     * list successfuly, or false if the {@code annotation} is
     * already in the list.
     *
     * @param annotation {@code Annotation} to be added to the list of {@code Annotation}s made by this {@code NameUsage}
     *
     * @return true if {@code annotation} was appended to the list of {@code Annotation}s made by this {@code NameUsage}
     * successfully, or false if {@code annotation} is already in the list
     */
    public boolean addAnnotation(Annotation annotation)
    {
	if(annotation == null)
	    throw new IllegalArgumentException(getClass().getName() + 
					       "#addAnnotation(): null argument");

	NameUsageNode<T> n = getNameUsageNode();
	if(n != this)
	    return n.addAnnotation(annotation);

	boolean ret = super.addAnnotation(annotation);

	if(ret)
	    addRelevantAnnotation(annotation);

	return ret;
    }

    /**
     * Removes {@code annotation} from the list of {@code Annotation}s made by this {@code NameUsage}
     *
     * @param annotation {@code NameUsage} to be removed fromthe list of {@code Annotation}s made by this {@code NameUsage}
     */
    public boolean removeAnnotation(Annotation annotation)
    {
	if(annotation == null)
	    /*
	    throw new IllegalArgumentException(getClass().getName() + 
					       "#removeAnnotation(): null argument");
	    */
	    return false;

	NameUsageNode<T> n = getNameUsageNode();
	if(n != this) {
	    return n.removeAnnotation(annotation);
	}

	return removeRelevantAnnotation(annotation) & super.removeAnnotation(annotation);
    }

    /**
     * Returns {@code Collection} of 
     * relevant {@code Annotations}, or null
     * if there is no {@code Annotations}
     * relevant to this {@code NameUsageNode}
     *
     * @return Collection of relevant {@code Annotation}s
     */
    public Collection<Annotation> getRelevantAnnotations()
    {
	NameUsageNode<T> n = getNameUsageNode();
	if(n != this)
	    return n.getRelevantAnnotations();

	return relevantAnnotations;
    }


    /**
     * Returns {@code Collection} of 
     * relevant {@code Annotations} with specified
     * {@code linkType}, or null
     * if there is no {@code Annotations} having
     * {@code linkType}.
     *
     * @param linkType link type of {@code Annotation} to be
     * returned
     *
     * @return Collection of {@code Annotation}s having
     * {@code linkType}
     */
    public Collection<Annotation> getRelevantAnnotations(String linkType)
    {
	NameUsageNode<T> n = getNameUsageNode();
	if(n != this)
	    return n.getRelevantAnnotations();

	return annotationsByLinkType.get(linkType);
    }

    /**
     * Adds {@code annotation} to the list of
     * {@code Annotation}s relevant to the
     * {@code NameUsage} and provide cross
     * references between relevant {@code NameUsage}s
     *
     * @param annotation {@code Annotation} to be added to
     * the list of relevant {@code Annotation}s
     *
     * @return number of {@code NameUsageNode} added
     */
    public int addRelevantAnnotation(Annotation annotation)
    {
	NameUsageNode<T> n = getNameUsageNode();
	if(n != this)
	    return n.addRelevantAnnotation(annotation);

	if(annotation == null)
	    throw new IllegalArgumentException(getClass().getName() + 
					       "#addRelevantAnnotation(): null argument");

	if(relevantAnnotations == null) {
	    relevantAnnotations   = Collections.synchronizedSet(new HashSet<Annotation>());
	    annotationsByLinkType = Collections.synchronizedMap(new HashMap<String, Set<Annotation>>());
	    nodesByAnnotation  = Collections.synchronizedMap(new HashMap<Annotation, Set<NameUsageNode<?/*T*/>>>());
	}

	relevantAnnotations.add(annotation);
	annotation.addPropertyChangeListener(this);

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
     * Removes {@code annotation} from the list of
     * {@code Annotation}s relevant to the
     * {@code NameUsage} and cross
     * references between relevant {@code NameUsage}s
     *
     * @param annotation {@code Annotatation} to be remvoed
     */
    public boolean removeRelevantAnnotation(Annotation annotation)
    {
	NameUsageNode<T> n = getNameUsageNode();
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
	annotation.removePropertyChangeListener(this);

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
     * Returns {@code Enumeration} of 
     * relevant {@code NameUsageNode}s, or null
     * if there is no {@code NameUsageNode}
     * relevant to this {@code NameUsageNode}
     *
     * @return Enumeration of relevant {@code NameUsageNode}
     * or null if none
     */
    public Collection<NameUsageNode<?/* extends T*/>> getRelevantNodes()
    {
	NameUsageNode<T> n = getNameUsageNode();
	if(n != this)
	    return n.getRelevantNodes();

	return relevantNodes;
    }

    /**
     * Returns {@code Collection} of 
     * relevant {@code NameUsageNode}s with
     * {@code name}, or null
     * if there is no {@code NameUsageNode}
     * ascribed as {@code name}
     *
     * @param name ascribed name of {@code NameUsageNode}s
     *
     * @return Collection of relevant {@code NameUsageNode}
     * with {@code name} or null if none
     */
    public Collection <NameUsageNode<?/* extends T*/>>getRelevantNodesOfName(String name)
    {
	NameUsageNode<T> n = getNameUsageNode();
	if(n != this)
	    return n.getRelevantNodesOfName(name);

	return nodesByName.get(name).keySet();
    }

    /**
     * Returns {@code Enumeration} of 
     * relevant {@code NameUsageNode}s with
     * persisntet ID {@code id}, or null
     * if there is no <ptt>NameUsageNode}
     * ascribed as {@code name}.
     * Class name part of {@code id} will be dropped
     * when looking for {@code NameUsageNode}s so
     * persistent ID of either {@code NameUsage}
     * {@code NameUsageNode} will accepted.
     *
     * @param id persisntet ID of {@code NameUsageNode}s
     *
     * @return Enumeration of relevant {@code NameUsageNode}
     * with {@code id} or null if none
     */
    public Collection<NameUsageNode<?/* extends T*/>> getRelevantNodesOfID(String id)
    {
	NameUsageNode<T> n = getNameUsageNode();
	if(n != this)
	    return n.getRelevantNodesOfName(id);

	return nodesByPID.get(getPersistentIDContents(id)).keySet();
    }

    /**
     * Adds {@code NameUsageNode}s relevant to 
     * {@code NameUsage} referred to by {@code annotation}
     *
     * @param annotation {@code Annotation} where relevant
     * {@code NameUsage} should be retrieved
     *
     * @param annotation {@code Annotation} to be added
     *
     * @return number of {@code NameUsageNode} added
     */
    public int addRelevantNodes(Annotation annotation)
    {
	NameUsageNode<T> n = getNameUsageNode();
	if(n != this)
	    return n.addRelevantNodes(annotation);

	Set<NameUsageNode<?/* extends T*/>> nodeSet = nodesByAnnotation.get(annotation);
	if(nodeSet == null) {
	    nodeSet = Collections.synchronizedSet(new HashSet<NameUsageNode<?/* extends T*/>>());
	    nodesByAnnotation.put(annotation, nodeSet);
	}

	int added = 0;
	added += addRelevantNodes(annotation, annotation.getAnnotators(), nodeSet);
	added += addRelevantNodes(annotation, annotation.getAnnotatants(), nodeSet);

	return added;
    }

    /**
     * Adds {@code NameUsageNode}s representing
     * {@code NameUsage}s enumerated in {@code nodes}
     * to the list of {@code NameUsageNode} relevant to this,
     * with pointer to {@code annotation}.
     *
     * @param annotation {@code Annotation} to be added
     * @param nodes {@code Enumeration} of {@code NameUsage}
     * to be added
     * @param nodeList {@code Hashtable} 
     * to hold {@code NameUsageNode}s 
     * relevant to the {@code annotation}
     *
     * @return number of {@code NameUsageNode} added
     */
    public int addRelevantNodes(Annotation annotation,
				Collection<? extends NameUsage<?>> nodes,
				Set<NameUsageNode<?>> nodeSet)
    {
	return addRelevantNodes(annotation, nodes.iterator(), nodeSet);
    }
    
    /**
     * Adds {@code NameUsageNode}s representing
     * {@code NameUsage}s enumerated in {@code nodes}
     * to the list of {@code NameUsageNode} relevant to this,
     * with pointer to {@code annotation}.
     *
     * @param annotation {@code Annotation} to be added
     * @param nodes {@code Iterator} of {@code NameUsage}
     * to be added
     * @param nodeList {@code Hashtable} 
     * to hold {@code NameUsageNode}s 
     * relevant to the {@code annotation}
     *
     * @return number of {@code NameUsageNode} added
     */
    public int addRelevantNodes(Annotation annotation,
				Iterator<? extends NameUsage<?>> nodes,
				Set<NameUsageNode<?>> nodeSet)
    {
	NameUsageNode<T> n = getNameUsageNode();
	if(n != this)
	    return n.addRelevantNodes(annotation, nodes, nodeSet);

	if(nodes == null || !nodes.hasNext())
	    return 0;
	
	if(relevantNodes == null) {
	    relevantNodes = Collections.synchronizedSet(new HashSet<NameUsageNode<?/* extends T*/>>());
	    nodesByName  = Collections.synchronizedMap(new HashMap<String, Map<NameUsageNode<?/* extends T*/>, Annotation>>());
	    nodesByPID  = Collections.synchronizedMap(new HashMap<String, Map<NameUsageNode<?/* extends T*/>, Annotation>>());
	}

	int i = 0;

	while(nodes.hasNext()) {
	    NameUsage<?> usage = nodes.next();
	    if(usage == null || usage == this || 
	       usage.getEntity() == getNameUsage().getEntity() ||
	       usage.getPersistentID().equals(getNameUsageID())
	       )
		continue;

	    NameUsageNode<?> node = getNameUsageNode(usage);

	    if(node.getPersistentID().equals(getPersistentID()))
		continue;

	    nodeSet.add(node);
	    relevantNodes.add(node);

	    String key = node.getLiteral();
	    Map<NameUsageNode<?/* extends T*/>, Annotation> annotationMap = nodesByName.get(key);
	    if(annotationMap == null) {
		annotationMap = Collections.synchronizedMap(new HashMap<NameUsageNode<?/* extends T*/>, Annotation>());
		nodesByName.put(key, annotationMap);
	    }
	    annotationMap.put(node, annotation);
	    
	    key = node.getPersistentIDContents();
	    annotationMap = nodesByPID.get(key);
	    if(annotationMap == null) {
		annotationMap = Collections.synchronizedMap(new HashMap<NameUsageNode<?/* extends T*/>, Annotation>());
		nodesByPID.put(key, annotationMap);
	    }
	    annotationMap.put(node, annotation);

	    i++;
	}

	return i;
    }

    /**
     * Removes {@code NameUsageNode}s relevant to
     * {@code annotation} from cross
     * references between them.
     *
     * @param annotation to which relevant {@code NameUsageNode}s 
     * to be removed
     *
     * @return number of removed {@code NameUsageNode}s 
     */
    public int removeRelevantNodes(Annotation annotation)
    {
	int i = 0;

	Set<NameUsageNode<?/* extends T*/>> keys = nodesByAnnotation.get(annotation);

	for(NameUsageNode<?/* extends T*/> node: keys) {
	    String key = node.getPersistentIDContents();
	    Map<NameUsageNode<?/* extends T*/>, Annotation> nodes = nodesByPID.get(key);
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
     * Removes {@code annotation} from the list of
     * {@code Annotation}s relevant to then
     * {@code NameUsage} and cross
     * references between relevant {@code NameUsage}s
     *
     * @param annotation {@code Annotation} of which 
     * relevant {@code NameUsageNode} to be remvoed
     * @param nodes {@code Hashtable} from which 
     * {@code NameUsageNode} relevant to 
     * {@code annotation} to be remvoed
     *
     */
    public int removeRelevantNodes(Annotation annotation, Map<? extends NameUsageNode<?/* extends T*/>, Annotation> nodes)
    {
	Set<NameUsageNode<?/* extends T*/>> toBeRemoved = new HashSet<NameUsageNode<?/* extends T*/>>();

	for(NameUsageNode<?/* extends T*/> key : nodes.keySet()) {
	    if(annotation == nodes.get(key)) {
		toBeRemoved.add(key);
	    }
	}

	int i = 0;
	for(NameUsageNode<?/* extends T*/> key : toBeRemoved) {
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
     * Returns persistent ID as a {@code NameUsage},
     * instead of as {@code NameUsageNode}
     *
     * @return String persisten ID of this as a {@code NameUsage}
     */
    public String getNameUsageID()
    {
	NameUsageNode<T> n = getNameUsageNode();
	if(n != this)
	    return n.getNameUsageID();

	if(entity != null)
	    return ((NameUsage)entity).getPersistentID();

	return getPersistentID();
    }

    /**
     * Returns {@code NameUsageNode} representing higher name usage
     *
     * @return {@code NameUsageNode} representing higher name usage
     */
    public NameUsageNode<T> getHigherNameUsageNode()
    {
	NameUsageNode<T> n = getNameUsageNode();
	if(n != this) 
	    return n.getHigherNameUsageNode();
	NameUsage<?> nameUsage = getHigherNameUsage();
	if(nameUsage == null ||
	   nameUsage instanceof NameUsageNode)
	    return getNameUsageNode(nameUsage);

	/*
	NameUsageNode node = 
	    getNameUsageNode(nameUsage)
	*/

	NameUsageNode<T> node = createNameUsageNode(nameUsage);
	setHigherNameUsage(node);
	return node;
    }

    /**
     * Sets {@code nameUsage} as higher name usage
     *
     * @param nameUsage {@code NameUsage} representing higher name uage
     */
    public boolean setHigherNameUsage(NameUsage<T> higherNameUsage)
    {
	NameUsageNode<T> n = getNameUsageNode();
	if(n != this) {
	    return n.setHigherNameUsage(higherNameUsage);
	}

	if(higherNameUsage != null &&  !isAssignableFrom(higherNameUsage) && isInstance(higherNameUsage)) {
	    higherNameUsage = createNameUsageNode(getNameUsage(higherNameUsage));
	}

	return super.setHigherNameUsage(higherNameUsage);

    }

    /**
     * Adds {@code nameUsage} to the list of lower taxa.
     * It returns true if the {@code nameUsage} added to the
     * list successfuly, or false if the {@code nameUsage} is
     * already in the list.
     *
     * @param nameUsage {@code NameUsage} to be added to the list of lower taxa
     *
     * @return true if {@code nameUsage} was appended to the list of lower taxa
     * successfully, or false if {@code nameUsage} is already in the list
     */
    public boolean addLowerNameUsage(NameUsage<T> nameUsage)
    {
	NameUsageNode<T> n = getNameUsageNode();
	if(n != this)
	    return n.addLowerNameUsage(nameUsage);

	if(isAssignableFrom(nameUsage))
	    return super.addLowerNameUsage(getNameUsageNode(nameUsage));

	return false;
    }

    /**
     * Sets {@code sensu} as authority {@code NameUsage} of this object.
     *
     * @param sensu {@code NameUsage} representing authority of this {@code NameUsage}
     */
    public void setSensu(NameUsage<?> sensu)
    {
	NameUsageNode<T> n = getNameUsageNode();

	if(n != this)
	    n.setSensu(sensu);
	else
	    super.setSensu(getNameUsageNode(sensu));
    }

    /**
     * Sets {@code type} as type of this {@code NameUsage}.
     * The  {@code type} may be null.
     *
     * @param type {@code NameUsage} to be designated as the type of
     * this {@code NameUsage}
     */
    public void setType(NameUsage<?/* extends T*/> typeUsage)
    {
	NameUsageNode<T> n = getNameUsageNode();
	if(n != this)
	    n.setTypeUsage(typeUsage);
	else
	    super.setTypeUsage(getNameUsageNode(typeUsage));
    }

    /**
     * Converts {@code usage} to {@code NameUsageNode}.
     * If {@code usage} is an instance of {@code NameUsageNode},
     * it does nothing.
     *
     * @param usage {@code NameUsage} to be converted.
     *
     * @return NameUsageNode representing {@code uasge}
     */
    public NameUsageNode<T> getNameUsageNode(Object object)
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

	//if(object instanceof NameUsage<?/* extends T*/>)
	if(object instanceof NameUsage)
	    return createNameUsageNode(getNameUsage(object));

	if(isAssignableFrom(object))
	    return getNameUsageNode(getClass().cast(object));
	else
	    return null;
    }

    public void propertyChange(PropertyChangeEvent event)
    {
	
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
    
    public AbstractNameUsageNode<T> create()
    {
	return createNameUsageNode();
    }

    protected abstract AbstractNameUsageNode<T> createNameUsageNode();

    protected abstract AbstractNameUsageNode<T> createNameUsageNode(NameUsage<?> nameUsage);

    protected abstract AbstractNameUsageNode<T> createNameUsageNode(String persistentID);

    protected AbstractNameUsage<T> createNameUsage()
    {
	return createNameUsageNode();
    }

    protected AbstractNameUsage<T> createNameUsage(NameUsage<?> nameUsage)
    {
	return createNameUsageNode(nameUsage);
    }

    protected AbstractNameUsage<T> createNameUsage(String persistentID)
    {
	return createNameUsageNode(persistentID);
    }

    protected AbstractNameUsage<T> createNameUsage(Name<?> name)
    {
	if (name instanceof NameUsage)
	    return createNameUsageNode((NameUsage<?>)name);
	return createNameUsage();
    }

    public NameUsageNode<T> clone()
    {
	AbstractNameUsageNode<T> node = create();
	copyTo(node);
	return node;
    }

    protected void copy(AbstractNameUsageNode<T> source)
    {
	source.copyTo(this);
    }

    protected void copyTo(AbstractNameUsageNode<T> dest)
    {
	super.copyTo((AbstractNameUsage<T>) dest);
    }

   /**
     * Returns {@code NameUsageNode} proxied by this {@code NameUsagNode}
     *
     * @return NameUsageNode proxied by this {@code NameUsagNode}
     */
    public NameUsageNode<T> getNameUsageNode()
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
