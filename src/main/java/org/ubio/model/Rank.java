/*
 * Rank.java: provides rank data holder for uBio serviceData
 *
 * Copyright (c) 2008, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP17300071
 */

package org.ubio.model;

import java.util.Hashtable;

/**
 * {@code Rank} provides methods to handle rank element returned from uBio XML Webservices
 *
 * @version 	20 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class Rank
{
    protected static Hashtable<String, Rank> byName;
    protected static Hashtable<String, Rank> byId;
    protected int id;
    protected String name;

    public static Rank ZERO = new Rank(0, "");

    protected Rank(int id,
		   String name)
    {
	this.id = id;
	this.name = name;
	if(byName == null) {
	    byName = new Hashtable<String, Rank>();
	}
	if(byName.get(name) == null) {
	    byName.put(name, this);
	}
	if(byId == null) {
	    byId = new Hashtable<String, Rank>();
	}
	String idString = String.valueOf(id);
	if(byId.get(idString) == null) {
	    byId.put(idString, this);
	}
    }

    public static Rank get(int id)
    {
	if(byId == null)
	    return null;
	return byId.get(String.valueOf(id));
    }

    public static Rank get(String name)
    {
	if(byName == null)
	    return null;
	return byName.get(name);
    }

    public static Rank get(String id, String name)
    {
	return get(Integer.valueOf(id).intValue(), name);
    }

    public static Rank get(int id, String name)
    {
	Rank rank = get(id);
	if(rank == null) {
	    rank = new Rank(id, name);
	}

	return rank;
    }

    public int getId()
    {
	return id;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String name)
    {
	this.name = name;
    }
}
