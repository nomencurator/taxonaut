/*
 * Map.java:  a Map with set operations
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
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A customized Map with set operations
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public interface Map<K, V>
    extends java.util.Map<K, V>
{
    /**
     * Returns true if this <CODE>HashMap</CODE> and <CODE>m</CODE>
     * are disjoint each other.
     *
     * @param m <CODE>java.util.Map</CODE> to be examined against to
     * this <CODE>HashMap</CODE>
     *
     * @return true if this <CODE>HashMap</CODE> and <CODE>m</CODE>
     * are disjoint each other
     */
    public boolean isDisjoint(java.util.Map<K, V> m);

    /**
     * Returns true if values of this <CODE>HashMap</CODE> and <CODE>c</CODE>
     * are disjoint each other.
     *
     * @param c <CODE>java.util.Set</CODE> to be examined against to
     * this <CODE>HashMap</CODE>
     *
     * @return true if values of this <CODE>HashMap</CODE> and <CODE>c</CODE>
     * are disjoint each other
     */
    public boolean isDisjoint(Collection<? extends V> c);

    /**
     * Returns true if this <CODE>HashMap</CODE> has intersection with
     * with <CODE>m</CODE>
     *
     * @param m <CODE>java.util.Map</CODE> to be examined against to
     * this <CODE>HashMap</CODE>
     *
     * @return true if this <CODE>HashMap</CODE> has an intersection
     * with <CODE>m</CODE>
     */
    public boolean hasIntersection(java.util.Map<K, V> m);

    /**
     * Returns true if values of this <CODE>HashMap</CODE> has intersection
     * with <CODE>c</CODE>
     *
     * @param c <CODE>java.util.Collection</CODE> to be examined against to
     * this <CODE>HashMap</CODE>
     *
     * @return true if values of this <CODE>HashMap</CODE> has an intersection
     * with <CODE>c</CODE>
     */
    public boolean hasIntersection(Collection<V> c);

    /**
     * Return true if this <CODE>HashMap</CODE> contains
     * at least one key which is not contained in <COCE>h</CODE>
     *
     * @param m <CODE>java.util.Map</CODE> to be examined against to
     * this <CODE>HashMap</CODE>
     *
     * @return true if this <CODE>HashMap</CODE> contains key which is
     * not contained in <CODE>m</CODE>
     */
    public boolean hasOtherThan(java.util.Map<K, V> m);

    /**
     * Returns true if this <CODE>HashMap</CODE> contains <CODE>m</CODE>.
     *
     * @param m <CODE>java.util.Map</CODE> to be examined against to
     * this <CODE>HashMap</CODE>
     *
     * @return true if this <CODE>HashMap</CODE> contains <CODE>m</CODE>
     */
    public boolean includes(java.util.Map<K, V> m);

    /**
     * Returns true if values this <CODE>HashMap</CODE> contains <CODE>c</CODE>.
     *
     * @param c <CODE>Collection</CODE> to be examined against to
     * this <CODE>HashMap</CODE>
     *
     * @return true if values of this <CODE>HashMap</CODE> contains <CODE>c</CODE>
     */
    public boolean includes(Collection<V> c);

    /**
     * Returns true if this <CODE>HashMap</CODE> contains <CODE>m</CODE>.
     *
     * @param m <CODE>java.util.Map</CODE> to be examined against to
     * this <CODE>HashMap</CODE>
     *
     * @return true if this <CODE>HashMap</CODE> contains <CODE>m</CODE>
     */
    public boolean excludes(java.util.Map<K, V> m);

    /**
     * Returns true if this <CODE>HashMap</CODE> is equlvalent to <CODE>m</CODE>,
     * i.e. they contain eatch other
     *
     * @param m <CODE>java.util.Map</CODE> to be examined against to
     * this <CODE>HashMap</CODE>
     *
     * @return true if this <CODE>HashMap</CODE> is equivalent to <CODE>m</CODE>
     */
    public boolean equals(Object m);

    public int intersect(java.util.Map<K, V> m);

    public int add(java.util.Map<K, V> m);

    /**
     * Returns intersection of <CODE>m</CODE> and <CODE>this</CODE> as a
     * <CODE>HashMap</CODE> containing elements contained both
     * <CODE>m</CODE> and <CODE>this</CODE>
     *
     * @param m <CODE>java.util.Map</CODE> to be examined
     *
     * @return <CODE>HashMap</CODE> representing intersection
     * of <CODE>m</CODE> and <CODE>this</CODE>
     */
    public Map<K, V> getIntersection(java.util.Map<K, V> m);

    /**
     * Returns union of <CODE>m</CODE> and <CODE>this</CODE> as a
     * <CODE>HashMap</CODE> containing all elements in <CODE>a</CODE> and <CODE>this</CODE>.
     * If both <tt>m</tt> and <tt>this</tt> contain values mapped to the same key, mapping of this has priority.
     *
     * @param m <CODE>java.util.Map</CODE> to be examined
     *
     * @return <CODE>HashMap</CODE> representing union
     * of <CODE>m</CODE> and <CODE>this</CODE>
     */
    public Map<K, V> getUnion(java.util.Map<K, V> m);

    /**
     * Returns exclusive or of <CODE>a</CODE> and <CODE>b</CODE> as a
     * <CODE>HashMap</CODE> containing elements in <CODE>a</CODE>
     * or <CODE>b</CODE> exclusively.
     *
     * @param a <CODE>java.util.Map</CODE> to be examined
     * @param b <CODE>java.util.Map</CODE> to be examined
     *
     * @return <CODE>HashMap</CODE> representing exclusive or
     * of <CODE>a</CODE> and <CODE>b</CODE>
     */
    public Map<K, V> getXOR(java.util.Map<K, V> m);
}
