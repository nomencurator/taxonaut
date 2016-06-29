/*
 * SetUtility.java:  a set of utlity methods to handle Sets
 *
 * Copyright (c) 2002, 2003, 2014, 2015 Nozomi `James' Ytow
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

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

/**
 * A set of utlity meothds to handle <tt>Set</tt>s
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class SetUtility
{
    private SetUtility() {}

    /**
     * Returns true if this <CODE>Hashtable</CODE> and <CODE>h</CODE>
     * are disjoint each other.
     *
     * @param h <CODE>java.util.Hashtable</CODE> to be examined against to
     * this <CODE>Hashtable</CODE>
     *
     * @return true if this <CODE>Hashtable</CODE> and <CODE>h</CODE>
     * are disjoint each other
     */
    public static boolean isDisjoint(Set<?> a, Set<?> b)
    {
	Set<?> tester = (a.size() < b.size())? a:b;
	Set<?> testant = (a.size() < b.size())? b:a;

	for(Object o : tester) {
	    if(testant.contains(o))
		return false;
	}
	
	return true;
    }

    /**
     * Returns true if this <CODE>Hashtable</CODE> has intersection with
     * with <CODE>h</CODE>
     *
     * @param h <CODE>java.util.Hashtable</CODE> to be examined against to
     * this <CODE>Hashtable</CODE>
     *
     * @return true if this <CODE>Hashtable</CODE> has an intersection
     * with <CODE>h</CODE>
     */
    public static boolean hasIntersection(Set<?> a, Set<?> b)
    {
	return !isDisjoint(a, b);
    }

    /**
     * Return true if this <CODE>Hashtable</CODE> contains
     * at least one key which is not contained in <COCE>h</CODE>
     *
     * @param h <CODE>java.util.Hashtable</CODE> to be examined against to
     * this <CODE>Hashtable</CODE>
     *
     * @return true if this <CODE>Hashtable</CODE> contains key which is
     * not contained in <CODE>h</CODE>
     */
    public static boolean hasOtherThan(Set<?> a, Set<?> b)
    {
	if(a.size() > b.size())
	    return true;

	return !b.containsAll(a);
    }

    /**
     * Returns intersection of <ttCODE>a</CODE> and <CODE>b</CODE> as a
     * <CODE>Hashtable</CODE> containing elements contained both
     * <CODE>a</CODE> and <CODE>b</CODE>
     *
     * @param a <CODE>java.util.Hashtable</CODE> to be examined
     * @param b <CODE>java.util.Hashtable</CODE> to be examined
     *
     * @return <CODE>Hashtable</CODE> representing intersection
     * of <CODE>a</CODE> and <CODE>b</CODE>
     */
    public static Set<?> intersection(Set<?> a, Set<?> b)
    {
	if(a == null || b == null || a.size() == 0 || b.size() == 0 )
	    return Collections.EMPTY_SET;

	Set<Object> intersection = new HashSet<Object>();
	Set<?> tester = (a.size() < b.size())? a:b;
	Set<?> testant = (a.size() < b.size())? b:a;

	for(Object o : tester) {
	    if(testant.contains(o))
		intersection.add(o);
	}

	if(intersection.size() == 0) 
	    return Collections.EMPTY_SET;

	return intersection;
    }

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
    public static Set<?> union(Set<?> a, Set<?> b)
    {
	if(a == null || b == null || a.size() == 0 || b.size() == 0 )
	    return Collections.EMPTY_SET;

	Set<Object> union = new HashSet<Object>((a.size() < b.size())? b:a);
	Set<?> src = (a.size() < b.size())? b:a;

	for(Object o : src) {
	    if(!union.contains(o)) 
		union.add(o);
	}
	return union;
    }

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
    public static Set<?> xor(Set<?> a, Set<?> b)
    {
	if(a == null || b == null)
	    return Collections.EMPTY_SET;

	Set<Object> xor = new HashSet<Object>();

	for(Object obj : a) {
	    if(!b.contains(obj))
		xor.add(obj);
	}
	for(Object obj : b) {
	    if(!a.contains(obj))
		xor.add(obj);
	}
	return xor;
    }

    public static Set<?> sub(Set<?> a, Set<?> b)
    {
	if(a == null)
	    return Collections.EMPTY_SET;

	if(b == null)
	    return new HashSet<Object>(a);

	Set<Object> sub = new HashSet<Object>();
	for(Object obj : a) {
	    if(!b.contains(obj))
		sub.add(obj);
	}
	return sub;
    }

}
