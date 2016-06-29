/*
 * NamedObjectQuery.java
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

// import java.util.ArrayList;
import java.util.Collection;
// import java.util.Collections;
// import java.util.List;

import java.util.concurrent.Callable;

import org.nomencurator.model.NamedObject;

import lombok.Getter;

/**
 * {@code NamedObjectQuery} implements {@code ObjectExchanger}.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NamedObjectQuery<N extends NamedObject<?, ?>, T extends N>
    implements ObjectQuery<N, T>
{
    @Getter
	protected ObjectExchanger<N, T> exchanger;

    @Getter
	protected QueryParameter<N, T> parameter;

    protected QueryResultListenerAdaptor<N, T> listeners;

    @Getter
	protected volatile Collection<T> results;

    protected NamedObjectQuery(QueryParameter<N, T> parameter)
    {
	this.parameter = parameter;
    }

    public NamedObjectQuery(QueryParameter<N, T> parameter, ObjectExchanger<N, T> exchanger)
    {
	this(parameter);
	this.exchanger = exchanger;
    }

    public void run()
    {
	call();
    }

    public Collection<T> call()
    {
	results = exchanger.getObjects(parameter);

	if(listeners != null)
	    listeners.fireQueryResultEvent(new QueryResultEvent<N, T>(this, 0, 1));

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

    protected void fireQueryResultEvent(QueryResultEvent<N, T> event)
    {
	if(listeners != null)
	    listeners.fireQueryResultEvent(event);
    }
}

