/*
 * AbstractObjectExchanger.java
 *
 * Copyright (c) 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.io;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nomencurator.model.NamedObject;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code AbstractObjectExchanger} implements {@code ObjectExchanger}.
 *
 * @version 	15 Oct. 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractObjectExchanger<T extends NamedObject<?>>
    implements ObjectExchanger<T>
{
    protected static Map<NamedObject<?>, ObjectExchanger<?>> responsibeExchanger
	= Collections.synchronizedMap(new HashMap<NamedObject<?>, ObjectExchanger<?>>());

    protected MatchingMode defaultMatchingMode;

    /*
    @Getter
    @Setter
    protected QueryMode defaultQeuryMode;
    */

    @Getter
    @Setter
    protected String orSeparator;

    protected AbstractObjectExchanger()
    {
	defaultMatchingMode = MatchingMode.EXACT;
	// defaultQeuryMode = QueryMode.OBJECTS;
	setOrSeparator("/|\\|");
    }

    protected String[] splitQuery(String query)
    {
	if(query == null)
	    return null;
	String[] splitted = query.split(getOrSeparator());
	List<String> results = new ArrayList<String>();
	for(int i = 0; i < splitted.length; i++) {
	    splitted[i] = splitted[i].trim();
	    if(splitted[i] != null && splitted[i].length() > 0)
		results.add(splitted[i]);
	}

	return results.toArray(splitted);
    }

    public Collection<T> getObjects(QueryParameter<T> filter)
	throws IOException
    {
	if(filter == null)
	    return null;

	return getObjects(filter.getLocalKey(), filter.getMatchingMode());
    }
    
    //public Collection<NamedObject<N, T>> getObjects(String query)
    public Collection<T> getObjects(String query)
	throws IOException
    {
	return getObjects(query, getDefaultMatchingMode());
    }

    public void setDefaultMatchingMode(MatchingMode matchingMode) { }

    public MatchingMode getDefaultMatchingMode()
    {
	return MatchingMode.EXACT;
    }

    protected void deposit(NamedObject<?> namedObject)
    {
	responsibeExchanger.put(namedObject, this);	
    }

    public ObjectExchanger<?> getExchanger(NamedObject<?> namedObject) {
	return responsibeExchanger.get(namedObject);
    }
}

