/*
 * HashSetConstructor.java:  an implementation of CollectionConstructor
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.SortedSet;


/**
 * <tt>HashSetConstructor</tt> provides an implemantion of <tt>CollectionConstructor</tt>
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class HashSetConstructor<T>
    extends AbstractCollectionConstructor<T>
{
    public HashSetConstructor() {}

    public Collection<T> constructCollection()
    {
	return new HashSet<T>();
    }

    public Collection<T> constructCollection(Collection<? extends T> collection)
    {
	return new HashSet<T>(collection);
    }

    public Collection<T> constructCollection(int initialCapacity)
    {
	return new HashSet<T>(initialCapacity);
    }

    public Collection<T> constructCollection(int initialCapacity, float loadFactor)
    {
	return new HashSet<T>(initialCapacity, loadFactor);
    }

    public Collection<T> constructCollection(SortedSet<T> s)
    {
	return new HashSet<T>(s);
    }
}
