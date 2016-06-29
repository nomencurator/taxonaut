/*
 * Nomencurator.java:  a Java implementation of Nomencurator class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 1999, 2002, 2003, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.model;

import java.io.Serializable;

import java.util.Hashtable;
import java.util.HashMap;
import java.util.Vector;

import org.nomencurator.util.ObjectPool;

/**
 * <CODE>Nomencurator</CODE> provides pooled objects
 * with name resolver

 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org</A>
 *
 * @version 	24 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class Nomencurator
//    extends Hashtable 
    extends HashMap<String, Name<?, ?>>
    implements Serializable
{ 
    private static final long serialVersionUID = 5219702098778493261L;

    protected static Nomencurator instance;

    protected ObjectPool<Object, Object> pool;

    protected NameResolver resolver;

    public static Nomencurator getInstance()
    {
	if(instance == null)
	    instance = new Nomencurator();
	
	return instance;
    }

    protected Nomencurator()
    {
	this(Integer.MAX_VALUE/2);
    }

    protected Nomencurator(int size)
    {
	pool = new ObjectPool<Object, Object>(size);
	resolver = new NameResolver();
    }

    public Name<?, ?> put(Name<?, ?> obj)
    {
	String key = obj.getLiteral();
	if(obj.isNominal())
	    resolver.put(key, obj);
	else
	    pool.put(key, obj);
	return resolve(obj);
    }

    public Name<?, ?> get(String name)
    {
	Name<?, ?> n = (Name<?, ?>)pool.get(name);
	if(n == null)
	    n = (Name)resolver.get(name);
	return n;
    }

    public Name<?, ?> resolve(Name<?, ?> obj)
    {
	return resolver.resolve(obj);
    }

    public Name<?, ?> putToResolver(Object key, Name<?, ?> obj)
    {
	if(!obj.isNominal() || resolver.get(key) != null)
	    return (NamedObject)resolver.resolve(obj);
	return (NamedObject)resolver.put(key, obj);
    }


    public NamedObject<?, ?> resolve(Object key, Name<?, ?> name)
    {
	return (NamedObject)resolver.resolve(key, name);
    }

    public NamedObject<?, ?> putUnresolved(Name<?, ?> name)
    {
	return (NamedObject)resolver.put(name.getLiteral(), name);
    }

    public NamedObject<?, ?> getUnresolved(Object key)
    {
	return (NamedObject)resolver.get(key);
    }

}



