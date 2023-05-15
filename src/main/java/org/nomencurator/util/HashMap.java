/*
 * HashMap.java:  a java.util.HashMap with set operations
 *
 * Copyright (c) 2002, 2003, 2014, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
 */

package org.nomencurator.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * A customized {@code HashMap} with set operations
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class HashMap<K, V>
    extends java.util.HashMap<K, V>
    implements Map<K, V>
{
    private static final long serialVersionUID = -8160247108683505387L;

    /**
     * Constructs an empty {@code HashMap} with specified
     * {@code initialCapacity} and {@code loadFactor}.
     *
     * @param initialCapacity initial capacity of the {@code HashMap}
     * @param loadFactor load factor of the {@code HashMap}
     *
     * @exception IllegalArgumentException if {@code initialCapacity} less than zero
     * or {@code loadFactor} is non positive value
     */
    public HashMap(int initialCapacity, float loadFactor)
    {
	super(initialCapacity, loadFactor);
    }

    /**
     * Constructs an empty {@code HashMap} with specified
     * {@code initialCapacity} and default load factor (0.75)
     *
     * @param initialCapacity initial capacity of the {@code HashMap}
     *
     * @exception IllegalArgumentException if {@code initialCapacity} less than zero
     */
    public HashMap(int initialCapacity)
    {
	super(initialCapacity);
    }

    /**
     * Constructs an empty {@code HashMap} with default
     * initial capacity (11) and default load factor (0.75)
     *
     */
    public HashMap()
    {
	super();
    }

    /**
     * Constructs an {@code HashMap} having the mapping
     * same to given {@code m}
     *
     * @param m {@code Map} to be cpied.
     */
    public HashMap(java.util.Map<? extends K, ? extends V> m)
    {
	super(m);
    }

    /**
     * Returns true if this {@code HashMap} and {@code h}
     * are disjoint each other.
     *
     * @param h {@code java.util.Map} to be examined against to
     * this {@code HashMap}
     *
     * @return true if this {@code HashMap} and {@code h}
     * are disjoint each other
     */
    public boolean isDisjoint(java.util.Map<K, V> m)
    {
	java.util.Map<K, V> tester = (m.size() < size())?  m : this;
	java.util.Map<K, V> testant = (m.size() < size())? this : m;

	Iterator<K> key = tester.keySet().iterator();
	while(key.hasNext()) {
	    if(testant.containsKey(key.next()))
		return false;
	}
	
	return true;
    }

    /**
     * Returns true if values of this {@code HashMap} and {@code c}
     * are disjoint each other.
     *
     * @param c {@code Collection} to be examined against to
     * this {@code HashMap}
     *
     * @return true if values of this {@code HashMap} and {@code c}
     * are disjoint each other
     */
    public boolean isDisjoint(Collection<? extends V> c)
    {
	Collection<? extends V> tester = (c.size() < size())? c : this.values();
	Collection<? extends V> testant = (c.size() < size())? this.values() : c;

	for(V value : tester) {
	    if(testant.contains(value))
		return false;
	}
	
	return true;
    }

    /**
     * Returns true if this {@code HashMap} has intersection with
     * with {@code h}
     *
     * @param h {@code java.util.Map} to be examined against to
     * this {@code HashMap}
     *
     * @return true if this {@code HashMap} has an intersection
     * with {@code h}
     */
    public boolean hasIntersection(java.util.Map<K, V> m)
    {
	return !isDisjoint(m);
    }

    /**
     * Returns true if this {@code HashMap} has intersection with
     * with {@code h}
     *
     * @param h {@code java.util.Map} to be examined against to
     * this {@code HashMap}
     *
     * @return true if this {@code HashMap} has an intersection
     * with {@code h}
     */
    public boolean hasIntersection(Collection<V> c)
    {
	return !isDisjoint(c);
    }

    /**
     * Return true if this {@code HashMap} contains
     * at least one key which is not contained in <COCE>h}
     *
     * @param h {@code java.util.Map} to be examined against to
     * this {@code HashMap}
     *
     * @return true if this {@code HashMap} contains key which is
     * not contained in {@code h}
     */
    public boolean hasOtherThan(java.util.Map<K, V> m)
    {
	if(size() > m.size())
	    return true;

	Set<K> keys = keySet();
	for(K key : keys) {
	    if(!m.containsKey(key))
		return true;
	}

	return false;
    }

    /**
     * Returns true if this {@code HashMap} contains {@code h}.
     *
     * @param h {@code java.util.Map} to be examined against to
     * this {@code HashMap}
     *
     * @return true if this {@code HashMap} contains {@code h}
     */
    public boolean includes(java.util.Map<K, V> m)
    {
	if(m.size() > size())
	    return false;

	Iterator<K> key = m.keySet().iterator();
	while(key.hasNext()) {
	    if(!containsKey(key.next()))
		return false;
	}

	return true;
    }

    /**
     * Returns true if values this {@code HashMap} contains {@code c}.
     *
     * @param c {@code Collection} to be examined against to
     * this {@code HashMap}
     *
     * @return true if values of this {@code HashMap} contains {@code c}
     */
    public boolean includes(Collection<V> c)
    {
	if(c == null)
	    return false;

	return values().containsAll(c);
    }

    /**
     * Returns true if this {@code HashMap} contains {@code h}.
     *
     * @param h {@code java.util.Map} to be examined against to
     * this {@code HashMap}
     *
     * @return true if this {@code HashMap} contains {@code h}
     */
    public boolean excludes(java.util.Map<K, V> m)
    {
	Iterator<K> key = m.keySet().iterator();
	while(key.hasNext()) {
	    if(containsKey(key.next()))
		return false;
	}

	return true;
    }

    /**
     * Returns true if this {@code HashMap} is equlvalent to {@code h},
     * i.e. they contain eatch other
     *
     * @param h {@code java.util.Map} to be examined against to
     * this {@code HashMap}
     *
     * @return true if this {@code HashMap} is equivalent to {@code h}
     */
    public boolean equals(Object m)
    {
	if(!(m instanceof java.util.Map))
	    return false;

	/*
	return includes((java.util.Map<K, V>)m) &&
	    !hasOtherThan((java.util.Map<K, V>)m);
	*/
	return super.equals(m);
    }

    public int intersect(java.util.Map<K, V> m)
    {
	if(m == null)
	    return 0;

	Iterator<K> e = keySet().iterator();
	Collection<K> toBeRemoved = new ArrayList<K>();
	while(e.hasNext()) {
	    K key = e.next();
	    if(!m.containsKey(key))
		toBeRemoved.add(key);
	}
	int beforeRemoval = size();
	e = toBeRemoved.iterator();
	while(e.hasNext()) {
	    remove(e.next());
	}
	toBeRemoved.clear();

	return beforeRemoval - size();
    }

    public int add(java.util.Map<K, V> m)
    {
	if(m == null)
	    return 0;
	
	Set<K> keys = m.keySet();
	int added = 0;
	for(K key : keys)  {
	    if(!containsKey(key)) {
		put(key, m.get(key));
		added++;
	    }
	}

	return added;
    }

    /**
     * Returns intersection of {@code m} and {@code this} as a
     * {@code HashMap} containing elements contained both
     * {@code m} and {@code this}
     *
     * @param m {@code java.util.Map} to be examined
     *
     * @return {@code HashMap} representing intersection
     * of {@code m} and {@code this}
     */
    public Map<K, V> getIntersection(java.util.Map<K, V> m)
    {
	if(m == null)
	    throw new IllegalArgumentException();

	Map<K, V> intersection = new HashMap<K, V>();
	java.util.Map<K, V> source = (m.size() > size())? this:m;
	Iterator<K> keys = source.keySet().iterator();
	while(keys.hasNext()) {
	    K key = keys.next();
	    if(m.containsKey(key) && this.containsKey(key))
		intersection.put(key, source.get(key));
	}

	return intersection;
    }

    /**
     * Returns union of {@code m} and {@code this} as a
     * {@code HashMap} containing all elements in {@code a} and {@code this}.
     * If both <tt>m</tt> and <tt>this</tt> contain values mapped to the same key, mapping of this has priority.
     *
     * @param m {@code java.util.Map} to be examined
     *
     * @return {@code HashMap} representing union
     * of {@code m} and {@code this}
     */
    public Map<K, V> getUnion(java.util.Map<K, V> m)
    {
	if(m == null)
	    throw new IllegalArgumentException();

	Map<K, V> union = new HashMap<K, V>();
	Iterator<K> keys = m.keySet().iterator();
	while(keys.hasNext()) {
	    K key = keys.next();
	    union.put(key, m.get(key));
	}
	keys = this.keySet().iterator();
	while(keys.hasNext()) {
	    K key = keys.next();
	    union.put(key, this.get(key));
	}
	return union;
    }

    /**
     * Returns exclusive or of {@code m} and {@code this} as a
     * {@code HashMap} containing elements in {@code m}
     * or {@code this} exclusively.
     *
     * @param m {@code java.util.Map} to be examined
     *
     * @return {@code HashMap} representing exclusive or
     * of {@code m} and {@code this}
     */
    public Map<K, V> getXOR(java.util.Map<K, V> m)
    {
	if(m == null)
	    throw new IllegalArgumentException();

	Map<K, V> xor = new HashMap<K, V>();

	Iterator<K> keys = m.keySet().iterator();
	while(keys.hasNext()) {
	    K key = keys.next();
	    if(!this.containsKey(key))
		xor.put(key, m.get(key));
	}

	keys = this.keySet().iterator();
	while(keys.hasNext()) {
	    K key = keys.next();
	    if(!m.containsKey(key))
		xor.put(key, this.get(key));
	}

	return xor;
    }
}
