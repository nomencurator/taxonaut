/*
 * AbstractCollectionConstructor.java:  an abstract implementation of CollectionConstructor
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
import java.util.SortedSet;


/**
 * <tt>AbstractCollectionConstructor</tt> provides an abstract implemantion of <tt>CollectionConstructor</tt>
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractCollectionConstructor<T>
    implements CollectionConstructor<T>
{
    protected AbstractCollectionConstructor() {}

    public Collection<T> constructCollection(int initialCapacity, float loadFactor)
    {
	return constructCollection(initialCapacity);
    }

    public Collection<T> constructCollection(Comparator<? super T> comparator)
    {
	return constructCollection();
    }

    public Collection<T> constructCollection(SortedSet<T> s)
    {
	return constructCollection(s.size());
    }
}
