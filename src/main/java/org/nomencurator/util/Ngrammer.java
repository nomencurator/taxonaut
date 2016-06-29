/*
 * Ngrammer.java:  a class to provide a N-gram search
 *
 * Copyright (c) 2005, 2014, 2015 Nozomi `James' Ytow
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
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import org.nomencurator.model.Name;
import org.nomencurator.model.NamedObject;

/**
 * <tt>Ngrammer</code> provides a N-gram
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class Ngrammer
{
    protected Map<String, Set<Object>> nGram;

    protected Set<Object> items;

    protected int keyLength;

    public static int DEFAULT_KEY_LENGTH = 3;

    protected double minimumScore;

    public Ngrammer()
    {
	this(DEFAULT_KEY_LENGTH);
    }

    public Ngrammer(int keyLength)
    {
	setKeyLength(keyLength);
    }

    public void setKeyLength(int keyLength)
    {
	if(keyLength == this.keyLength)
	    return;

	synchronized(this) {

	    this.keyLength = keyLength;

	    if(nGram != null) {
		Collection<Set<Object>> values = nGram.values();
		for(Set<Object> nGram : values) {
		    nGram.clear();
		}
	    
		nGram.clear();

		for(Object item: items)
		    put(item);
	    }
	}
    }

    public int getKeyLength()
    {
	return keyLength;
    }

    public void put(Object object)
    {
	if(object == null
	   || items.contains(object)
	   )
	    return;

	if(items == null)
	    items = Collections.synchronizedSet(new HashSet<Object>());
	items.add(object);

	nGramize(object);
    }

    protected void nGramize(Object object)
    {
	String key = null;
	if(object instanceof Name)
	    key = ((Name<?, ?>)object).getLiteral();
	else
	    key = object.toString();

	if(nGram == null)
	    nGram = Collections.synchronizedMap(new HashMap<String, Set<Object>>());

	if(key.length() <= keyLength) {
	    put(key, object);
	    return;
	}

	int limit = key.length() - keyLength;
	int i = 0;
	for(; i < limit; i++)
	    put(key.substring(i, i+keyLength), object);

	limit = key.length();
	for(; i < limit; i++)
	    put(key.substring(i), object);
    }

    protected void put(String key, Object object)
    {
	Set<Object> set = nGram.get(key);
	if(set == null) {
	    set = Collections.synchronizedSet(new HashSet<Object>());
	    nGram.put(key, set);
	}
	set.add(object);
    }
}
