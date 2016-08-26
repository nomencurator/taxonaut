/*
 * CollectionUtlity.java:  a housekeeping class to manage Collections
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;


/**
 * <tt>CollectionUtlity</tt> provieds set operations on <tt>Collection</tt>s
 *
 * @version 	27 Aug. 2016
 * @author 	Nozomi `James' Ytow
 */
public class CollectionUtility<T>
{
    protected CollectionConstructor<T> constructor;

    public CollectionUtility() {
	this(new HashSetConstructor<T>());
    }

    public CollectionUtility(CollectionConstructor<T> constructor) {
	setCollectionConstructor(constructor);
    }

    public void setCollectionConstructor(CollectionConstructor<T> constructor)
    {
	this.constructor = constructor;
    }
    
    /**
     * Returns true if <tt>Collection</CODE>s are disjoint each other.
     *
     * @param a <tt>Collection</tt> to be examined.
     * @param b the other <tt>Collection</tt> to be examined.
     *
     * @return true if given <tt>Collection</tt>s are disjoint each other
     */
    public static boolean isDisjoint(Collection<?> a, Collection<?> b)
    {
	if(a == null || b == null)
	    return true;

	Collection<?> tester = (a.size() < b.size())? a:b;
	Collection<?> testant = (a.size() < b.size())? b:a;

	for(Object o : tester) {
	    if(testant.contains(o))
		return false;
	}
	
	return true;
    }

    /**
     * Returns true if <tt>Collection</CODE>s have intersection.
     *
     * @param a <tt>Collection</tt> to be examined.
     * @param b the other <tt>Collection</tt> to be examined.
     *
     * @return true if given <tt>Collection</tt>s have common element
     */
    public static boolean hasIntersection(Collection<?> a, Collection<?> b)
    {
	return !isDisjoint(a, b);
    }

    /**
     * Return true if <tt>a</tt> contains at least one element
     * which is not contained in <COCE>b</tt>
     *
     * @param a <tt>Collection</tt> to be examined.
     * @param b <tt>Collection</tt> to be refered to
     *
     * @return true if <tt>s</tt> contains at least one elment which is
     * not contained in <tt>b</tt>
     */
    public static boolean hasOtherThan(Collection<?> a, Collection<?> b)
    {
	if(a.size() > b.size())
	    return true;

	return !b.containsAll(a);
    }

    protected Collection<T> constructCollection()
    {
	return constructor.constructCollection();
    }

    protected Collection<T> constructCollection(Collection<? extends T> collection)
    {
	return constructor.constructCollection(collection);
    }

    protected Collection<T> constructCollection(int initialCapacity)
    {
	return constructor.constructCollection(initialCapacity);
    }

    protected Collection<T> constructCollection(int initialCapacity, float loadFactor)
    {
	return constructor.constructCollection(initialCapacity, loadFactor);
    }

    protected Collection<T> constructCollection(Comparator<? super T> comparator)
    {
	return constructor.constructCollection(comparator);
    }

    protected Collection<T> constructCollection(SortedSet<T> s)
    {
	return constructor.constructCollection(s);
    }

    /**
     * Returns intersection of <tttt>a</tt> and <tt>b</tt> as a
     * <tt>HashSet</tt> containing elements shared by
     * <tt>a</tt> and <tt>b</tt>
     *
     * @param a <tt>Collection</tt> to be examined
     * @param b <tt>Collection</tt> to be examined
     *
     * @return <tt>HashSet</tt> representing intersection
     * of <tt>a</tt> and <tt>b</tt>
     */
    public Collection<T> intersection(Collection<? extends T> a, Collection<? extends T> b)
    {
	if(a == null)
	    a = Collections.emptySet();

	if(b == null)
	    b = Collections.emptySet();

	if(a == b)
	    return constructCollection(a);

	Collection<T> intersection = constructCollection((a.size() < b.size())? a:b);
	intersection.retainAll((a.size() < b.size())? b:a);

	return intersection;
    }

    /**
     * Returns union of <tt>a</tt> and <tt>b</tt> as a
     * <tt>HashSet</tt> containing all elements in <tt>a</tt> and <tt>b</tt>
     *
     * @param a <tt>Collection</tt> to be examined
     * @param b <tt>Collection</tt> to be examined
     *
     * @return <tt>HashSet</tt> representing union
     * of <tt>a</tt> and <tt>b</tt>
     */
    public Collection<T> union(Collection<? extends T> a, Collection<? extends T> b)
    {
	if(a == null)
	    a = Collections.emptySet();

	if(b == null)
	    b = Collections.emptySet();

	if(a == b)
	    return constructCollection(a);

	Collection<T> union = constructCollection((a.size() < b.size())? b:a);
	union.addAll((a.size() < b.size())? b:a);

	return union;
    }

    /**
     * Returns exclusive or of <tt>a</tt> and <tt>b</tt> as a
     * <tt>HashSet</tt> containing elements in <tt>a</tt> or
     * <tt>b</tt> exclusively.
     *
     * @param a <tt>Collection</tt> to be examined
     * @param b <tt>Collection</tt> to be examined
     *
     * @return <tt>HashSet</tt> representing exclusive or
     * of <tt>a</tt> and <tt>b</tt>
     */
    public Collection<T> xor(Collection<? extends T> a, Collection<? extends T> b)
    {
	if(a == b)
	    return Collections.emptySet();

	return union(sub(a, b), sub(b, a));
    }

    /**
     * Returns subtraction of <tt>b</tt> from <tt>a</tt> as a
     * <tt>HashSet</tt> containing elements only in <tt>a</tt>
     * but not in <tt>b</tt>.
     *
     * @param a <tt>Collection</tt> to be subtracted from. 
     * @param b <tt>Collection</tt> to be subtract.
     *
     * @return <tt>HashSet</tt> representing subtraction
     * of <tt>b</tt> from <tt>a</tt>
     */
    public Collection<T> sub(Collection<? extends T> a, Collection<? extends T> b)
    {
	if(a == null || a == b)
	    return Collections.emptySet();

	Collection<T> sub = constructCollection(a);
	if(b != null)
	    sub.removeAll(b);

	if(sub.size() == 0)
	    return Collections.emptySet();

	return sub;
    }

    public static <E> Collection<E> unique(Collection<? extends E> src, Collection<E> dest)
    {
	Set<E> set = new HashSet<>();
	for (E element : src) {
	    if (!set.contains(element)) {
		set.add(element);
		dest.add(element);
	    }
	}
	set.clear();
	return dest;
    }

}
