/*
 * QueryResultEvent.java: Event encapsulate an query to a data source
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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP17300071
 */

package org.nomencurator.io;

import java.util.Collection;
import java.util.EventObject;

import lombok.Getter;

import org.nomencurator.model.NamedObject;

/**
 * {@code QueryResultEvent} provides an event to manage a query result
 *
 * @version 	09 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class QueryResultEvent<T extends NamedObject<?>>
    extends EventObject
{
    private static final long serialVersionUID = 6521107034448177275L;

    @Getter
    protected String message;

    @Getter
    protected int residue;

    @Getter
    protected int total;

    public QueryResultEvent(ObjectQuery<T> query) {
	this(query, 0, 0);
    }

    public QueryResultEvent(ObjectQuery<T> query, int residue, int total) {
	this(query, residue, total, null);
    }

    public QueryResultEvent(ObjectQuery<T> query, int residue, int total, String message) {
	super(query);
	this.residue = residue;
	this.total = total;
	this.message = message;
    }

    public ObjectQuery<T> getQuery()
    {
	Object source = getSource();
	if(source instanceof ObjectQuery) {
	    @SuppressWarnings("unchecked")
		ObjectQuery<T> query = (ObjectQuery<T>)source;
	    return query;
	}
	return null;
    }

    public Collection<T> getResults() {
	ObjectQuery<T> query = getQuery();
	if(query != null)
	    return query.getResults();

	return null;
    }

}
