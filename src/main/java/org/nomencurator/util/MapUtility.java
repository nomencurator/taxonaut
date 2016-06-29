/*
 * MapUtlity.java:  a housekeeping class to manage Maps
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

package org.nomencurator.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * <tt>MapUtlity</tt> provieds a housekeeping class to manage Maps
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class MapUtility<K, V>
{
    public MapUtility() {}
    
    /**
     * Returns true if given <tt>Map</tt>s are disjoint each other.
     *
     * @param a <tt>Map</tt> to be examined
     * @param b the other <tt>Map</tt> to be examined
     *
     * @return true if given <tt>Map</tt>s are disjoint each other
     */
    public static boolean isDisjoint(Map<?, ?> a, Map<?, ?> b)
    {
	Collection<?> tester = (a.size() < b.size())? a.keySet() : b.keySet();
	Collection<?> testant = (a.size() < b.size())? b.keySet() : a.keySet();

	for(Object o : tester) {
	    if(testant.contains(o))
		return false;
	}

	tester = (a.size() < b.size())? a.values() : b.values();
	testant = (a.size() < b.size())? b.values() : a.values();

	for(Object o : tester) {
	    if(testant.contains(o))
		return false;
	}
	
	return true;
    }

    /**
     * Returns true if given <tt>Map</CODE>s have intersection.
     *
     * @param a <tt>Map</tt> to be examined.
     * @param b the other <tt>Map</tt> to be examined.
     *
     * @return true if given <tt>Map</tt>s have common element
     */
    public static boolean hasIntersection(Map<?, ?> a, Map<?, ?> b)
    {
	return !isDisjoint(a, b);
    }

    /**
     * Return true if <tt>a</tt> contains at least one mapping
     * which is not in <COCE>b</tt>
     *
     * @param a <tt>Map</tt> to be examined.
     * @param b <tt>Map</tt> to be refered to
     *
     * @return true if <tt>s</tt> contains at least one elment which is
     * not contained in <tt>b</tt>
     */
    public static boolean hasOtherThan(Map<?, ?> a, Map<?, ?> b)
    {
	if(a == null || b == null)
	    return false;

	if(a.size() > b.size())
	    return true;

	Set<?> keys = a.keySet();

	if(!b.keySet().containsAll(keys))
	    return true;

	for(Object key : keys) {
	    if(b.get(key) == null)
		return true;
	}

	return false;
    }

    /**
     * Returns intersection of <tttt>a</tt> and <tt>b</tt> as a
     * <tt>HashMap</tt> containing elements contained both
     * <tt>a</tt> and <tt>b</tt>
     *
     * @param a <tt>Map</tt> to be examined
     * @param b <tt>Map</tt> to be examined
     *
     * @return <tt>HashMap</tt> representing intersection
     * of <tt>a</tt> and <tt>b</tt>
     */
    public Map<K, V> intersection(Map<? extends K, ? extends V> a, Map<? extends K, ? extends V> b)
    {
	if(a == null || b == null || a.size() == 0 || b.size() == 0 )
	    return Collections.emptyMap();

	if(a == b)
	    new HashMap<K, V>(a);

	Map<K, V> intersection = new HashMap<K, V>();
	CollectionUtility<K> utility = new CollectionUtility<K>();
	Collection<K> keys = utility.intersection(a.keySet(), b.keySet());
	for(K key : keys) {
	    V aValue = a.get(key);
	    V bValue = b.get(key);
	    if(aValue != null && bValue != null && aValue.equals(bValue))
		intersection.put(key, aValue);
	}
	keys.clear();
	return intersection;
    }

    /**
     * Returns union of <tt>a</tt> and <tt>b</tt> as a
     * <tt>HashMap</tt> containing all elements in <tt>a</tt> and <tt>b</tt>
     *
     * @param a <tt>Map</tt> to be examined
     * @param b <tt>Map</tt> to be examined
     *
     * @return <tt>HashMap</tt> representing union
     * of <tt>a</tt> and <tt>b</tt>
     */
    public Map<K, V> union(Map<? extends K, ? extends V> a, Map<? extends K, ? extends V> b)
    {
	if(a == null || b == null || a.size() == 0 || b.size() == 0 )
	    return Collections.emptyMap();

	if(a == b)
	    return new HashMap<K, V>(a);

	Map<K, V> union = new HashMap<K, V>((a.size() < b.size())? b:a);
	union.putAll((a.size() < b.size())? b:a);

	return union;
    }

    /**
     * Returns exclusive or of <tt>a</tt> and <tt>b</tt> as a
     * <tt>HashMap</tt> containing elements in <tt>a</tt>
     * or <tt>b</tt> exclusively.
     *
     * @param a <tt>Map</tt> to be examined
     * @param b <tt>Map</tt> to be examined
     *
     * @return <tt>HashMap</tt> representing exclusive or
     * of <tt>a</tt> and <tt>b</tt>
     */
    public Map<K, V> xor(Map<? extends K, ? extends V> a, Map<? extends K, ? extends V> b)
    {
	if(a == b)
	    return Collections.emptyMap();

	return sub(union(a, b), intersection(a, b));
    }

    /**
     * Returns subtraction of <tt>b</tt> from <tt>a</tt> as a
     * <tt>Set</tt> containing elements only in <tt>a</tt>
     * but not in <tt>b</tt>.
     *
     * @param a <tt>Map</tt> to be subtracted from. 
     * @param b <tt>Map</tt> to be subtract.
     *
     * @return <tt>Set</tt> representing subtraction
     * of <tt>b</tt> from <tt>a</tt>
     */
    public Map<K, V> sub(Map<? extends K, ? extends V> a, Map<? extends K, ? extends V> b)
    {
	if(a == null || a == b)
	    return Collections.emptyMap();

	Set<K> keys = intersection(a, b).keySet();
	Map<K, V> sub = new HashMap<K, V>(a);
	for(K key : keys) {
	    sub.remove(key);
	}
	if(sub.isEmpty())
	    return Collections.emptyMap();

	return sub;
    }
}
