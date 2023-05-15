/*
 * RoughCollection.java:  a class to provide a rough set
 * for TaxoNote based on Nomencurator data model
 *
 * Copyright (c) 2002, 2014, 2015 Nozomi `James' Ytow
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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * <code>RoughCollection</code> is a class to compare
 * multiple hierarchies of names
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public interface RoughCollection<E>
{
    public Collection<E> getPositive();

    public void setPositive(Collection<? extends E> positive);

    public Collection<E> getBoundary();

    public void setBoundary(Collection<? extends E> boundary);

    public Collection<E> getNegative();

    public void setNegative(Collection<? extends E> negative);

    public Collection<E> getUpper();

    public RoughCollection<E> intersection(RoughCollection<? extends E> roughCollection);

    public RoughCollection<E> union(RoughCollection<? extends E> roughCollection);

    public List<Collection<E>> crossSection(RoughCollection<? extends E> roughCollection);

    public boolean addPositive(E element);

    public boolean removePositive(E element);

    public void clearPositive();

    public boolean addAllPositive(Collection<? extends E> collection);

    public boolean addNegative(E element);

    public boolean removeNegative(E element);

    public void clearNegative();

    public boolean addAllNegative(Collection<? extends E> collection);

    public boolean addAll(RoughCollection<? extends E> roughCollection);

    public void clear();

    /**
     * Returns true if the Rough set contains the specified <tt>Object</tt>.
     *
     * @param o <tt>Object</tt> to be examined
     *
     * @return true if the Rough set contains the specified <tt>Object</tt>.
     */
    public boolean contains(Object o);

    /**
     * Returns true if the Rough set contains all elements of specified <tt>Collection</tt>.
     *
     * @param c <tt>Collection</tt> containings elements to be examined
     *
     * @return true if the Rough set contains all elements of the specified <tt>Collection</tt>
     */
    public boolean containsAll(Collection<?> c);

    /**
     * Returns true if the Rough set is empty.
     *
     * @return true if the Rough set is empty.
     */
    public boolean isEmpty();

    /**
     * Returns a <tt>List</tt> of <tt>Iterator</tt>s to iterate each positive, negative or boundary <tt>Collection</tt>.
     *
     * @return a <tt>List</tt> of <tt>Iterator</tt>s to iterate each positive, negative or boundary <tt>Collection</tt>.
     */
    public List<Iterator<E>> iterators();

    /**
     * Returns true if given <tt>Collection</tt> falls in boundary, i.e. it does not have intersection with positive nor negative <tt>Collection</tt>.
     *
     * @param c <tt>Collection</tt> to be exmained.
     *
     *  @return true if given <tt>Collection</tt> falls in the bounary.
     */
    public boolean isBoundary(Collection<?> c);

    /**
     * Returns true if given <tt>Object</tt> falls in boundary, i.e. it does not have intersection with positive nor negative <tt>Collection</tt>.
     *
     * @param o <tt>Object</tt> to be exmained.
     *
     *  @return true if given <tt>Object</tt> falls in the bounary.
     */
    public boolean isBoundary(Object o);

    /**
     * Removes specified <tt>Object</tt> from the Rough set if it's either positive, negative or bounday <tt>Collection</tt>
     * contains the <tt>Object</tt>.
     *
     * @param o <tt>Object</tt> to be removed.
     *
     *  @return true if the <tt>Object</tt> removed from the Rough set by the call of this method.
     */
    public boolean remove(Object o);

    /**
     * Removes all elements in specified <tt>Collection</tt> from the Rough set
     * if it's either positive, negative or bounday <tt>Collection</tt> contains the <tt>Object</tt>.
     *
     * @param c <tt>Collection</tt> containing objects to be removed.
     *
     *  @return true if the Rough set is modified by the call of this method.
     */
    public boolean removeAll(Collection<?> c);

    /**
     * Removes all elements in specified <tt>RoghCollection</tt> from the Rough set
     * if it's corresponding positive, negative or bounday <tt>Collection</tt> contains the <tt>Object</tt>.
     *
     * @param c <tt>RoughCollection</tt> containing objects to be removed.
     *
     *  @return true if the Rough set is modified by the call of this method.
     */
    public boolean removeAll(RoughCollection<?> c);


    /**
     * Removes other than elements in specified <tt>Collection</tt> from the Rough set
     * if it's either positive, negative or bounday <tt>Collection</tt>.
     *
     * @param c <tt>Collection</tt> containing objects to be retained.
     *
     *  @return true if the Rough set is modified by the call of this method.
     */
    public boolean retainAll(Collection<?> c);

    /**
     * Removes other than elements in specified <tt>Collection</tt> from the Rough set
     * if it's corresponding positive, negative or bounday <tt>Collection</tt>.
     *
     * @param c <tt>Collection</tt> containing objects to be retained.
     *
     *  @return true if the Rough set is modified by the call of this method.
     */
    public boolean retainAll(RoughCollection<?> c);

    /**
     * Returns number of elements in the Rough set, i.e. total number of elements in
     * positive, negative and boundary sets.
     *
     * @return number of elements in positive, negative and boundary sets.
     */
    public int size();

    /**
     *  Returns an array of arrays containing all elements of each 
     * positive, negative and boundary sets.
     *
     * @return an array of  arrays containing all elements of each positive, negative and boundary sets.
     */
    public Object[][] toArray();

    /**
     * Returns an array of arrays containing all elements of each 
     * positive, negative and boundary sets.
     *
     * @return an array of  arrays containing all elements of each positive, negative and boundary sets.
     */
    public <T> T[][] toArray(T[][] a);

    public boolean isCompatible(RoughCollection<? extends E> roughCollection);
}
