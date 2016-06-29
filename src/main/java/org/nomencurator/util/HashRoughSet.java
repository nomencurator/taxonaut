/*
 * HashRoughSet.java:  an abstract class to provide a rough set
 * using Collection
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
import java.util.HashSet;

/**
 * <code>HashRoughSet</code> is a Rough set using <tt>HashSet</tt>.
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class HashRoughSet<E>
    extends AbstractRoughCollection<E>
{
    public HashRoughSet()
    {
	super();
    }

    public HashRoughSet(int initialCapacity)
    {
	super(initialCapacity);
    }

    public HashRoughSet(int initialCapacity, float loadFactor)
    {
	super(initialCapacity, loadFactor);
    }

    public HashRoughSet(Collection<? extends E> positive)
    {
	super(positive);
    }

    protected HashRoughSet(Collection<? extends E> positive, Collection<? extends E> negative)
    {
	super(positive, negative);
    }

    protected HashRoughSet(Collection<? extends E> positive,
		    Collection<? extends E> negative,
		    Collection<? extends E> boundary)
    {
	super(positive, negative, boundary);
    }

    protected Collection<E> constructCollection()
    {
	return new HashSet<E>();
    }

    protected Collection<E> constructCollection(Collection<? extends E> collection)
    {
	return new HashSet<E>(collection);
    }

    protected Collection<E> constructCollection(int initialCapacity)
    {
	return new HashSet<E>(initialCapacity);
    }

    protected Collection<E> constructCollection(int initialCapacity, float loadFactor)
    {
	return new HashSet<E>(initialCapacity, loadFactor);
    }

    protected RoughCollection<E> constructRoughCollection(int initialCapacity)
    {
	return new HashRoughSet<E>(initialCapacity);
    }

    protected RoughCollection<E> constructRoughCollection(int initialCapacity, float loadFactor)
    {
	return new HashRoughSet<E>(initialCapacity, loadFactor);
    }

    protected RoughCollection<E> constructRoughCollection(Collection<? extends E> positive,
							    Collection<? extends E> negative,
							    Collection<? extends E> boundary)
    {
	return new HashRoughSet<E>(positive, negative, boundary);
    }
}
