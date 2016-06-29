/*
 * AbstractMultiplexQuery.java
 *
 * Copyright (c) 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.nomencurator.model.NamedObject;

import lombok.Getter;

/**
 * {@code AbstractMultiplexQuery} implements {@code ObjectExchanger}.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractMultiplexQuery<N extends NamedObject<?, ?>, T extends N>
    extends NamedObjectQuery<N, T>
    implements MultiplexQuery<N, T>
{
    protected List<ObjectQuery<N, T>> queries;

    protected Map<ObjectQuery<N, T>, Future<Collection<T>>> futures;

    protected volatile boolean inSubmission;

    protected abstract ObjectQuery<N, T> createQuery(QueryParameter<N, T> parameter, ObjectExchanger<N, T> source);
    
    public AbstractMultiplexQuery(QueryParameter<N, T> parameter, Collection<? extends ObjectExchanger<N, T>> sources)
    {
	super(parameter);
	inSubmission = false;
	if(parameter != null && sources != null && sources.size() > 0) {
	    queries = Collections.synchronizedList(new ArrayList<ObjectQuery<N, T>>(sources.size()));
	    synchronized(queries) {
		for(ObjectExchanger<N, T> source : sources) {
		    addQuery(createQuery(parameter, source));
		}
	    }
	}
    }

    public AbstractMultiplexQuery(Collection<? extends ObjectQuery<N, T>> queries)
    {
	super(null);
	this.queries = Collections.synchronizedList(new ArrayList<ObjectQuery<N, T>>(queries));
    }

    public boolean addQuery(ObjectQuery<N, T> query)
    {
	if(queries == null)
	    this.queries = Collections.synchronizedList(new ArrayList<ObjectQuery<N, T>>());

	synchronized(queries) {
	    if(query != null && !queries.contains(query)) {
		query.addQueryResultListener(this);
		return queries.add(query);
	    }
	}

	return false;
    }

    public boolean removeQuery(ObjectQuery<N, T> query)
    {
	if(queries == null || query == null)
	    return false;
	synchronized(queries) {
	    query.removeQueryResultListener(this);
	    return queries.remove(query);
	}
    }

    public void run()
    {
	call();
    }

    public Collection<T> call()
    {
	results = Collections.synchronizedList(new ArrayList<T>());
	if(queries != null && !queries.isEmpty()) {

	    synchronized(queries) {
		inSubmission = true;
		int size = queries.size();
		ExecutorService executor = Executors.newFixedThreadPool(size);
		futures = Collections.synchronizedMap(new HashMap<ObjectQuery<N, T>, Future<Collection<T>>>(size));
		for(ObjectQuery<N, T> query : queries) {
		    futures.put(query, executor.submit(query));
		}
		inSubmission = false;
	    }

	    fireQueryResultEvent(new QueryResultEvent<N, T>(this, futures.size(), queries.size()));
	}


	return results;
    }

    public void addQueryResultListener(QueryResultListener<N, T> listener)
    {
	if(listeners == null)
	    listeners = new QueryResultListenerAdaptor<N, T>();
	listeners.addQueryResultListener(listener);
    }

    public void removeQueryResultListener(QueryResultListener<N, T> listener)
    {
	if(listeners != null)
	    listeners.removeQueryResultListener(listener);
    }

    public void fireQueryResultEvent(QueryResultEvent<N, T> event)
    {
	if(listeners != null)
	    listeners.fireQueryResultEvent(event);
    }

    public void queryReturned(QueryResultEvent<N, T> event)
    {
	while(inSubmission) { ; }

	if(results == null)
	    results = new ArrayList<T>();

	synchronized(queries) {
	    synchronized(futures) {
		Collection<T> result = event.getResults();
		if(result != null) {
		    results.addAll(result);
		}
		futures.remove(event.getQuery());
	    }
	}

	fireQueryResultEvent(new QueryResultEvent<N, T>(this, futures.size(), queries.size()));
    }
}

