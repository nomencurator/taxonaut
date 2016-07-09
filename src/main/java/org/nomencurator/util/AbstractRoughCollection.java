/*
 * AbstractRoughCollection.java:  an abstract class to provide a rough set
 * using Collection
 *
 * Copyright (c) 2002, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * <code>AbstractRoughCollection</code> is an abstract class to provide
 * a Rough set using <tt>Collection</tt>.
 *
 * @version 	09 July 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractRoughCollection<E>
    implements RoughCollection<E>
{
    protected Collection<E> positive;
    protected Collection<E> boundary;
    protected Collection<E> negative;

    protected CollectionUtility<E> utility;

    protected AbstractRoughCollection()
    {
	positive = constructCollection();
	negative = constructCollection();
    }

    protected AbstractRoughCollection(int initialCapacity)
    {
	positive = constructCollection(initialCapacity);
	negative = constructCollection(initialCapacity);
    }

    protected AbstractRoughCollection(int initialCapacity, float loadFactor)
    {
	positive = constructCollection(initialCapacity, loadFactor);
	negative = constructCollection(initialCapacity, loadFactor);
    }

    protected AbstractRoughCollection(SortedSet<E> positive, SortedSet<E> negative)
    {
	this.positive = constructCollection(positive);
	this.negative = constructCollection(negative);
    }

    protected AbstractRoughCollection(Collection<? extends E> positive)
    {
	this.positive = constructCollection(positive);
    }

    protected AbstractRoughCollection(Collection<? extends E> positive, Collection<? extends E> negative)
    {
	this.positive = constructCollection(positive);
	this.negative = constructCollection(negative);
    }

    protected AbstractRoughCollection(Collection<? extends E> positive,
		    Collection<? extends E> negative,
		    Collection<? extends E> boundary)
    {
	this.positive = constructCollection(positive);
	this.negative = constructCollection(negative);
	this.boundary = constructCollection(boundary);
    }

    protected RoughCollection<E> constructRoughCollection()
    {
	return constructRoughCollection(null, null, null);
    }

    protected abstract RoughCollection<E> constructRoughCollection(int initialCapacity);

    protected abstract RoughCollection<E> constructRoughCollection(int initialCapacity, float loadFactor);

    protected RoughCollection<E> constructRoughCollection(Collection<? extends E> positive)
    {
	return constructRoughCollection(positive, null, null);
    }

    protected RoughCollection<E> constructRoughCollection(Collection<? extends E> positive,
							    Collection<? extends E> negative)
    {
	return constructRoughCollection(positive, negative, null);
    }

    protected abstract RoughCollection<E> constructRoughCollection(Collection<? extends E> positive,
							    Collection<? extends E> negative,
							    Collection<? extends E> boundary);

    public Collection<E> getPositive()
    {
	return positive;
    }

    public Collection<E> getBoundary()
    {
	return boundary;
    }

    public Collection<E> getNegative()
    {
	return negative;
    }

    public void setPositive(Collection<? extends E> positive)
    {
	this.positive = constructCollection(positive);
    }

    public void setBoundary(Collection<? extends E> boundary)
    {
	this.boundary = constructCollection(boundary);
    }

    public void setNegative(Collection<? extends E> negative)
    {
	this.negative = constructCollection(negative);
    }

    protected abstract Collection<E> constructCollection();

    protected abstract Collection<E> constructCollection(Collection<? extends E> collection);

    protected abstract Collection<E> constructCollection(int initialCapacity);

    protected Collection<E> constructCollection(int initialCapacity, float loadFactor)
    {
	return constructCollection(initialCapacity);
    }

    protected Collection<E> constructCollection(SortedSet<E> s)
    {
	return constructCollection((Collection<E>)s);
    }

    public Collection<E> getUpper()
    {
	if(utility == null)
	    utility = new CollectionUtility<E>();
	return utility.union(getPositive(), getBoundary());
    }

    public RoughCollection<E> intersection(RoughCollection<? extends E> a)
    {
	if(utility == null)
	    utility = new CollectionUtility<E>();

	Collection<E> tmp = utility.union(a.getNegative(), getNegative());
	RoughCollection<E> result = 
	    constructRoughCollection(utility.sub(utility.intersection(a.getPositive(), getPositive()), tmp),
				   utility.sub(utility.sub(tmp,a.getPositive()), getPositive()));
	tmp.clear();
	return result;
    }

    public RoughCollection<E> union (RoughCollection<? extends E> a)
    {
	if(a == null)
	    throw new IllegalArgumentException("RoughSet#union");

	if(utility == null)
	    utility = new CollectionUtility<E>();

	Collection<E> tmp = utility.union(a.getPositive(), getPositive());
	RoughCollection<E> result = 
	    constructRoughCollection(utility.sub(utility.sub(tmp, a.getNegative()), getNegative()),
				     utility.sub(utility.intersection(a.getNegative(), getNegative()), tmp));
	tmp.clear();
	return result;
    }

    public List<Collection<E>> crossSection(RoughCollection<? extends E> roughCollection)
    {
	List<Collection<E>> crossSection = new ArrayList<Collection<E>>();
	Set<E> emptySet = Collections.emptySet();
	if(roughCollection != null) {
	    if(utility == null)
		utility = new CollectionUtility<E>();
	    Collection<E> c =  utility.intersection(getPositive(), roughCollection.getNegative());
	    crossSection.add((c == null) ? emptySet : c);
	    c =  utility.intersection(getNegative(), roughCollection.getPositive());
	    crossSection.add((c == null) ? emptySet : c);
	}
	else {
	    crossSection.add(emptySet);
	    crossSection.add(emptySet);
	}
	return crossSection;
    }


    public boolean addPositive(E element)
    {
	return add(getPositive(), element);
    }

    public boolean addNegative(E element)
    {
	return add(getNegative(), element);
    }

    public boolean add(Collection<E> collection, E element)
    {
	return (collection != null && collection.add(element));
    }

    public boolean removePositive(E element)
    {
	return remove(getPositive(), element);
    }

    public boolean removeNegative(E element)
    {
	return remove(getNegative(), element);
    }

    public void clearPositive()
    {
	clear(getPositive());
    }

    public void clearNegative()
    {
	clear(getNegative());
    }

    public boolean addAllPositive(Collection<? extends E> collection)
    {
	return addAll(getPositive(), collection);
    }

    public boolean addAllNegative(Collection<? extends E> collection)
    {
	return addAll(getNegative(), collection);
    }

    public boolean addAll(RoughCollection<? extends E> roughCollection)
    {
	return (roughCollection != null &&
		(addAll(getPositive(), roughCollection.getPositive()) ||
		 addAll(getNegative(), roughCollection.getNegative()) ||
		 addAll(getBoundary(), roughCollection.getBoundary())));
    }

    protected boolean addAll(Collection<E> a, Collection<? extends E> b)
    {
	return (a != null && b != null && a.addAll(b));
    }


    public void clear()
    {
	clear(getPositive());
	clear(getNegative());
	clear(getBoundary());
    }

    protected void clear(Collection<?> c)
    {
	if(c != null)
	    c.clear();
    }

    public boolean contains(Object o)
    {
	return (contains(getPositive(), o) ||
		contains(getNegative(), o) ||
		contains(getBoundary(), o));
    }

    protected boolean contains(Collection<?> c, Object o)
    {
	return (c != null && c.contains(o));
    }

    public boolean containsAll(Collection<?> c)
    {
	if(utility == null)
	    utility = new CollectionUtility<E>();
	
	return utility.union(utility.union(getPositive(), getNegative()), getBoundary()).containsAll(c);
    }


    public boolean isCompatible(RoughCollection<? extends E> roughCollection)
    {
	if(roughCollection == null)
	    return true;

	if(utility == null)
	    utility = new CollectionUtility<E>();

	return (utility.intersection(getPositive(), roughCollection.getNegative()).isEmpty() &&
		utility.intersection(getNegative(), roughCollection.getPositive()).isEmpty());
    }

    public boolean isEmpty()
    {
	return (isEmpty(getPositive()) &&
		isEmpty(getNegative()) &&
		isEmpty(getBoundary()));
    }

    protected boolean isEmpty(Collection<?> c)
    {
	return (c == null || c.isEmpty());
    }

    public List<Iterator<E>> iterators()
    {
	List<Iterator<E>> iterators = new ArrayList<Iterator<E>>();
	Collection<E> collection = getPositive();
	if(collection != null)
	    iterators.add(collection.iterator());
	collection = getNegative();
	if(collection != null)
	    iterators.add(collection.iterator());
	collection = getBoundary();
	if(collection != null)
	    iterators.add(collection.iterator());

	return iterators;
    }

    public boolean isBoundary(Collection<?> c)
    {
	boolean isBoundary = true;

	Collection<E> collection = getBoundary();
	if(collection != null && collection.contains(c))
	    isBoundary &= true;

	if(utility == null)
	    utility = new CollectionUtility<E>();

	collection = getPositive();
	if(collection != null && CollectionUtility.hasIntersection(collection, c))
	    isBoundary &= false;

	collection = getNegative();
	if(collection != null && CollectionUtility.hasIntersection(collection, c))
	    isBoundary &= false;

	return isBoundary;
    }

    /**
     * Returns true if given <tt>Object</tt> falls in boundary, i.e. it does not have intersection with positive nor negative <tt>Collection</tt>.
     *
     * @param o <tt>Object</tt> to be exmained.
     *
     *  @return true if given <tt>Object</tt> falls in the bounary.
     */
    public boolean isBoundary(Object o)
    {
	boolean isBoundary = true;

	Collection<E> collection = getBoundary();
	if(collection != null && collection.contains(o))
	    isBoundary &= true;

	collection = getPositive();
	if(collection != null && collection.contains(o))
	    isBoundary &= false;

	collection = getNegative();
	if(collection != null && collection.contains(o))
	    isBoundary &= false;

	return isBoundary;
    }

    public boolean remove(Object o)
    {
	if(o == null)
	    return false;

	return (remove(getPositive(), o) ||
		remove(getNegative(), o)||
		remove(getBoundary(), o));
    }

    /**
     * Removes an <tt>Objecto</tt> from a <tt>Collection</tt>.
     *
     * @param c <tt>Collection</tt> containing objects to be tested.
     * @param o <tt>Object</tt> to be removed.
     *
     *  @return true if <tt>c</tt> is modified by the call of this method.
     */
    protected boolean remove(Collection<?> c, Object o)
    {
	return (c != null && c.remove(o));
    }


    public boolean removeAll(Collection<?> c)
    {
	if(c == null || c.isEmpty())
	    return false;

	return (removeAll(getPositive(), c) ||
		removeAll(getNegative(), c)||
		removeAll(getBoundary(), c));
    }

    public boolean removeAll(RoughCollection<?> c)
    {
	if(c == null)
	    return false;

	return (removeAll(getPositive(), c.getPositive()) ||
		removeAll(getNegative(), c.getNegative())||
		removeAll(getBoundary(), c.getBoundary()));
    }

    /**
     * Removes elements in <tt>b</tt> from <tt>a</tt>.
     *
     * @param a <tt>Collection</tt> containing objects to be tested.
     * @param b <tt>Collection</tt> containing objects to be removed.
     *
     *  @return true if <tt>a</tt> is modified by the call of this method.
     */
    protected boolean removeAll(Collection<?> a, Collection<?> b)
    {
	if(a != null && b != null)
	    return a.removeAll(b);

	return false;
    }

    public boolean retainAll(Collection<?> c)
    {
	if(c == null || c.isEmpty())
	    return false;
	return (retainAll(getPositive(), c) ||
		retainAll(getNegative(), c)||
		retainAll(getBoundary(), c));


    }

    public boolean retainAll(RoughCollection<?> c)
    {
	if(c == null)
	    return false;

	return (retainAll(getPositive(), c.getPositive()) ||
		retainAll(getNegative(), c.getNegative())||
		retainAll(getBoundary(), c.getBoundary()));
    }
    
    /**
     * Removes other than elements in <tt>b</tt> from <tt>a</tt>.
     *
     * @param a <tt>Collection</tt> containing objects to be tested.
     * @param b <tt>Collection</tt> containing objects to be retained.
     *
     *  @return true if <tt>a</tt> is modified by the call of this method.
     */
    protected boolean retainAll(Collection<?> a, Collection<?> b)
    {
	if(a != null && b != null)
	    return a.retainAll(b);

	return false;
    }

    public int size()
    {
	int size = 0;
	Collection<E> collection = getPositive();
	if(collection != null)
	    size += collection.size();

	collection = getNegative();
	if(collection != null)
	    size += collection.size();

	return size;
    }

    public Object[][] toArray()
    {
	Object[][] array = new Object[2][];
	Collection<E> tmp = getPositive();
	if(tmp == null) {
	    array[0] = new Object[0];
	}
	else {
	    array[0] = getPositive().toArray();
	}
	tmp = getNegative();
	if(tmp == null) {
	    array[1] = new Object[0];
	}
	else {
	    array[1] = getNegative().toArray();
	}

	return array;
    }

    @SuppressWarnings("unchecked")
    public <T> T[][] toArray(T[][] a)
    {
	if(a.length < 2) {
	    a = (T[][])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), 2);
	}
	else if (a.length > 2) {
	    a[2] = null;
	}

	Collection<E> tmp = getPositive();
	if(tmp == null) {
	    a[0] = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), 0);
	}
	else {
	    a[0] = getPositive().toArray(a[0]);
	}
	tmp = getNegative();
	if(tmp == null) {
	    a[1] = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), 0);
	}
	else {
	    a[1] = getNegative().toArray(a[1]);
	}

	return a;
    }

}
