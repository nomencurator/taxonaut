/*
 * RoughSetAdaptor.java:  a rough set operator for NamedNodes
 *
 * Copyright (c) 2002, 2003, 2005, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.nomencurator.util.MapUtility;

/**
 * <CODE>RoughSetAdaptor</code> provides rough set operation
 * for <code>NamedNode</code>s
 *
 * @version 	22 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class RoughSetAdaptor
{
    /**
     * <CODE>Map</CODE> of which keys provide a list of literal values
     * included in the concept represented by this <CODE>NameUsage</CODE>
     */
    protected Map<String, NamedNode<?>> includants;

    /**
     * <CODE>Map</CODE> of which keys provide a list of literal values
     * excluded in the concept represented by this <CODE>NameUsage</CODE>
     */
    protected Map<String, NamedNode<?>> excludants;

    protected NamedNode<?> thisNode;

    protected MapUtility<String, NamedNode<?>> utility;

    public RoughSetAdaptor(NamedNode<?> node)
    {
	thisNode = node;
	utility = new MapUtility<String, NamedNode<?>>();
    }

    public void addIncludant(NamedNode<?> node)
    {
	if(node == null)
	    return;

	if(includants == null) {
	    includants = Collections.synchronizedMap(new HashMap<String, NamedNode<?>>());
	}

	String name = node.getLiteral();
	if(name != null && name.length() > 0) 
	    includants.put(name, node);

	NamedNode<?> parent = (NamedNode<?>)thisNode.getParent();
	if(parent != null)
	    parent.addIncludant(node);
    }

    public void removeIncludant(NamedNode<?> node)
    {
	if(includants == null || node == null)
	    return;
	
	String name = node.getLiteral();
	if(name != null && name.length() > 0)
	    includants.remove(name);

	NamedNode<?> parent = (NamedNode<?>)thisNode.getParent();
	if(parent != null)
	    parent.removeIncludant(node);
    }

    public Map<String, NamedNode<?>> getIncludants()
    {
	if(includants != null ||
	   thisNode.isLeaf())
	    return includants;

	includants = new HashMap<String, NamedNode<?>>();
	Enumeration<?> e = thisNode.children();

	NamedNode<?> n = null;
	while(e.hasMoreElements()) {
	    Object element = e.nextElement();
	    if (element instanceof NamedNode) {
		n = (NamedNode<?>)element;
		Map<String, NamedNode<?>> h = n.getIncludants();
		if(h != null)
		    includants.putAll(h);
		includants.put(n.getLiteral(), n);
	    }
	}

	return includants;
    }

    public void clearIncludants()
    {
	if(includants != null)
	    includants.clear();
	includants = null;
    }

    public void addExcludant(NamedNode<?> node)
    {
	if(node == null)
	    return;

	if(excludants == null) {
	    excludants = Collections.synchronizedMap(new HashMap<String, NamedNode<?>>());
	}

	String name = node.getLiteral();
	if(name != null && name.length() > 0)
	    excludants.put(name, node);

	if(thisNode.isLeaf())
	    return;

	Enumeration<?> e = thisNode.children();
	while(e.hasMoreElements()) {
	    Object element = e.nextElement();
	    ((NamedNode<?>)element).addExcludant(node);
	}
    }

    public void removeExcludant(NamedNode<?> node)
    {
	if(excludants == null || node == null)
	    return;

	String name = node.getLiteral();
	if(name != null && name.length() > 0)
	    excludants.remove(name);

	if(thisNode.isLeaf())
	    return;

	Enumeration<?> e = thisNode.children();
	while(e.hasMoreElements()) {
	    Object element = e.nextElement();
	    if (element instanceof NamedNode) {
		if (element instanceof NamedNode) {
		    ((NamedNode<?>)element).removeExcludant(node);
		}
	    }
	}
    }

    public Map<String, NamedNode<?>> getExcludants()
    {
	NamedNode<?> n = (NamedNode<?>)thisNode.getParent();
	if(excludants != null ||
	   n == null)
	    return excludants;

	excludants = new HashMap<String, NamedNode<?>>();
	Enumeration<?> e = n.children();
	Map<String, NamedNode<?>> h = null;
	while(e.hasMoreElements()) {
	    Object element = e.nextElement();
	    if (element instanceof NamedNode) {
		n = (NamedNode<?>)element;
		if(n == thisNode)
		    continue;
		h = n.getIncludants();
		if(h != null)
		    excludants.putAll(h);
	    }
	}

	n = (NamedNode<?>)thisNode.getParent().getParent();

	if(n != null) {
	    h = n.getExcludants();
	    if(h != null)
		excludants.putAll(h);
	}

	return excludants;
    }

    public void clearExcludants()
    {
	if(excludants != null)
	    excludants.clear();
	excludants = null;
    }


    public boolean isCompatible(NamedNode<?> node)
    {
	if(node == null)
	    return false;
	
	Map<String, NamedNode<?>> nodeIncludants = node.getIncludants();
	Map<String, NamedNode<?>> nodeExcludants = node.getExcludants();
	boolean compatibility = true;

	if(!thisNode.isLeaf() && 
	   (nodeExcludants != null))
	    compatibility &= !MapUtility.hasIntersection(getIncludants(), nodeExcludants);

	if((thisNode.getParent() != null) && 
	   nodeIncludants != null)
	    compatibility &= !MapUtility.hasIntersection(getExcludants(), nodeIncludants);

	return compatibility;
    }

    /**
     * Returns intersection of <CODE>a</CODE> and <CODE>b</CODE> as a
     * <CODE>Hashtable</CODE> containing elements contained both
     * <CODE>a</CODE> and <CODE>b</CODE>
     *
     * @param a <CODE>java.util.Hashtable</CODE> to be examined
     * @param b <CODE>java.util.Hashtable</CODE> to be examined
     *
     * @return <CODE>Hashtable</CODE> representing intersection
     * of <CODE>a</CODE> and <CODE>b</CODE>
     */
    /*
    public static Map<String, NamedNode> getIntersection(Map<String, ? extends NamedNode> a, Map<String, ? extends NamedNode> b)
    {
	if(a == null || b == null)
	    throw new IllegalArgumentException();

	Map<String, NamedNode> intersection = new HashMap<String, NamedNode>();
	Map<String, ? extends NamedNode> source = (a.size() > b.size())? b:a;
	Iterator<String> keys = source.keySet().iterator();

	while(keys.hasMoreElements()) {
	    String key = keys.nextElement();
	    if(a.containsKey(key) &&
	       b.containsKey(key))
		intersection.put(key, source.get(key));
	}

	return intersection;
    }
    */

    /**
     * Returns union of <CODE>a</CODE> and <CODE>b</CODE> as a
     * <CODE>Hashtable</CODE> containing all elements in <CODE>a</CODE> and <CODE>b</CODE>
     *
     * @param a <CODE>java.util.Hashtable</CODE> to be examined
     * @param b <CODE>java.util.Hashtable</CODE> to be examined
     *
     * @return <CODE>Hashtable</CODE> representing union
     * of <CODE>a</CODE> and <CODE>b</CODE>
     */
    /*
    public static Map<String, NamedNode> getUnion(Map<String, ? extends NamedNode> a, Map<String, ? extends NamedNode> b)
    {
	if(a == null || b == null)
	    throw new IllegalArgumentException();

	Map<String, NamedNode> union = new HashMap<String, NamedNode>();
	Iterator<String> keys = b.keySet().iterator();
	while(keys.hasMoreElements()) {
	    Object key = keys.nextElement();
	    union.put(key, b.get(key));
	}
	keys = a.keys();
	while(keys.hasMoreElements()) {
	    String key = keys.nextElement();
	    union.put(key, a.get(key));
	}
	return union;
    }
    */

    /**
     * Returns exclusive or of <CODE>a</CODE> and <CODE>b</CODE> as a
     * <CODE>Hashtable</CODE> containing elements in <CODE>a</CODE>
     * or <CODE>b</CODE> exclusively.
     *
     * @param a <CODE>java.util.Hashtable</CODE> to be examined
     * @param b <CODE>java.util.Hashtable</CODE> to be examined
     *
     * @return <CODE>Hashtable</CODE> representing exclusive or
     * of <CODE>a</CODE> and <CODE>b</CODE>
     */
    /*
    public static  Hashtable getXOR(java.util.Hashtable a, java.util.Hashtable b)
    {
	if(a == null || b == null)
	    throw new IllegalArgumentException();

	Hashtable xor = new Hashtable();

	Enumeration keys = a.keys();
	while(keys.hasMoreElements()) {
	    Object key = keys.nextElement();
	    if(!b.containsKey(key))
		xor.put(key, a.get(key));
	}

	keys = b.keys();
	while(keys.hasMoreElements()) {
	    Object key = keys.nextElement();
	    if(!a.containsKey(key))
		xor.put(key, b.get(key));
	}

	return xor;
    }
    */

    public Map<String, NamedNode<?>> getCrossSection(NamedNode<?> node)
    {
	Map<String, NamedNode<?>> crossSection = utility.intersection(getIncludants(), node.getExcludants());
	crossSection.putAll(utility.intersection(getExcludants(), node.getIncludants()));

	return crossSection;
    }

    public List<Map<String, NamedNode<?>>> getIntersection(NamedNode<?> node)
    {
	List<Map<String, NamedNode<?>>> roughSet = new ArrayList<Map<String, NamedNode<?>>>(2);
	roughSet.add(utility.intersection(getIncludants(), node.getIncludants()));
	roughSet.add(utility.union(getExcludants(), node.getExcludants()));
	return roughSet;
    }

    public List<Map<String, NamedNode<?>>> getUnion(NamedNode<?> node)
    {
	List<Map<String, NamedNode<?>>> roughSet = new ArrayList<Map<String, NamedNode<?>>>(2);
	roughSet.add(utility.union(getIncludants(), node.getIncludants()));
	roughSet.add(utility.intersection(getExcludants(), node.getExcludants()));

	return roughSet;
    }

    public double getCoverage(NamedNode<?> node)
    {
	if(node == null)
	    return 0d;

	int intersectionCount = 0;
	int unionCount = 0;
	
	Map<String, NamedNode<?>> h = node.getIncludants();
	if(!thisNode.isLeaf() && 
	   h != null) {
	    intersectionCount += utility.intersection(getIncludants(), h).size();
	    unionCount += utility.union(getIncludants(), h).size();
	}

	h = getExcludants();
	if((thisNode.getParent() != null) && 
	   h != null) {
	    intersectionCount += utility.intersection(getExcludants(), h).size();
	    unionCount += utility.union(getExcludants(), h).size();
	}

	if(unionCount == 0)
	    return 0d;

	return ((double)intersectionCount)/((double)unionCount);
    }

}
