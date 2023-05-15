/*
 * QueryResultListenerAdaptor.java: an EventListener implementaiton
 * for a QueryEvent
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.nomencurator.model.NamedObject;

/**
 * {@code QueryResultListenerAdapter} provides an adaptor
 * to handle events to {@code QueryResultListener}.
 *
 * @version 	02 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class QueryResultListenerAdaptor<T extends NamedObject<?>>
{
    // EventListenerList is unhappy with type T which could be an interface but not a class
    protected List<QueryResultListener<T>> listeners;

    public synchronized void addQueryResultListener(QueryResultListener<T> listener)
    {
	if(listeners == null)
	    listeners = Collections.synchronizedList(new ArrayList<QueryResultListener<T>>());
	listeners.add(listener);
    }

    public synchronized void removeQueryResultListener(QueryResultListener<T> listener)
    {
	if(listeners != null)
	    listeners.remove(listener);
    }

    public void fireQueryResultEvent(QueryResultEvent<T> event)
    {
	if(listeners == null || listeners.isEmpty())
	    return;

	synchronized(listeners) {
	    for(QueryResultListener<T> listener : listeners) {
		listener.queryReturned(event);
	    }
	}
    }
}
