/*
 * ObjectPool.java:  a pool of Objects
 *
 * Copyright (c) 2003, 2015, 2016 Nozomi `James' Ytow
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>ObjectPool</code> provides a pool of Objects.
 *
 * @version 	24 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class ObjectPool<K, V>
//    extends Hashtable
    extends HashMap<K, V>
{
    private static final long serialVersionUID = 5692634007630985123L;

    protected int maxCapacity;

    //protected Vector objects;
    protected List<V> objects;
    
    //protected Hashtable indices;
    protected Map<V, Integer> indices;

    public ObjectPool()
    {
	this(11);
    }

    public ObjectPool(int maxCapacity)
    {
	this(maxCapacity, 11, 0.75f);
    }

    public ObjectPool(int initialCapacity,
		      float loadFactor)
    {
	this(initialCapacity, initialCapacity, loadFactor);
    }

    public ObjectPool(int maxCapacity,
		      int initialCapacity,
		      float loadFactor)
    {
	super(initialCapacity, loadFactor);
	this.maxCapacity = maxCapacity;
	//objects = new Vector(initialCapacity);
	objects = Collections.synchronizedList(new ArrayList<V>(initialCapacity));
	//indices = new Hashtable(initialCapacity, loadFactor);
	indices = Collections.synchronizedMap(new HashMap<V, Integer>(initialCapacity));
    }

    //public Object put(Object key, Object value)
    public V put(K key,  V value)
    {
	V o = null;
	synchronized (this) {
	    o = super.get(key);
	    int index = objects.size();
	    if(o != null) {
		//objects.remove(((Integer)indices.get(o)).intValue());
		objects.remove(indices.get(o).intValue());
		indices.remove(o);
		index--;
	    }
	    if(index == maxCapacity) {
		o = objects.get(0);
		indices.remove(o);
		objects.remove(0);
		index--;
	    }
	    //objects.addElement(value);
	    objects.add(value);
	    indices.put(value, new Integer(index));

	    o = super.put(key, value);
	}

	return  o;
    }
}



