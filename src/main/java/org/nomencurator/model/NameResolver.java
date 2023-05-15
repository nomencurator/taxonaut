/*
 * NameResolver.java:  a name resolver
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 2002, 2003, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.model;

import java.util.HashMap;

/**
 * <CODE>NameResolver</CODE> provides a mechanism to resolve indirect reference
 * by <CODE>Name</CODE>.
 *
 * @version 	02 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameResolver
    extends HashMap<Object, Object>
{ 
    private static final long serialVersionUID = -7314279459945931982L;

    public NameResolver()
    {
	super();
    }

    public NameResolver(int initialCapacity)
    {
	super(initialCapacity);
    }

    public NameResolver(int initialCapacity, float loadFactor)
    {
	super(initialCapacity, loadFactor);
    }

    public Object put(Name<?> name)
    {
	return put(name.getLiteral(), name);
    }

    public Object put(Object key, Object value)
    {
	if(!(value instanceof Name) ||
	   (!((Name)value).isNominal()))
	    return value;

	return super.put(key, value);
    }

    public Object get(Name<?> name)
    {
	return get(name.getLiteral());
    }

    public Object get(Object key)
    {
	if(key == null)
	    return null;
	Object o = super.get(key);
	if(o == null ||
	   !(o instanceof Name))
	    return o;

	return ((Name)o).getEntity();
    }

    public Name<?> resolve(Name<?> name)
    {
	if(name == null)
	    return name;
	return resolve(name.getLiteral(), name);
    }

    public Name<?> resolve(Object key, Name<?> name)
    {
	Object value = get(key);
	if(value == null ||
	   value == name ||
	   !(value instanceof Name))
	    return null;

	Name<?> unresolved = name.getName(value);
	unresolved.setEntity(name);
	remove(key);
	if(name.isNominal()) {
	    put(key, name);
	}
	return unresolved;
    }
}



