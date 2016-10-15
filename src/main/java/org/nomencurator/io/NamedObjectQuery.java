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

import java.io.IOException;

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
 * @version 	15 Oct. 2016
 * @author 	Nozomi `James' Ytow
 */
public class NamedObjectQuery<T extends NamedObject<?>>
    implements ObjectQuery<T>
{
    @Getter
	protected ObjectExchanger<T> exchanger;

    @Getter
	protected QueryParameter<T> parameter;

    protected QueryResultListenerAdaptor<T> listeners;

    @Getter
	protected volatile Collection<T> results;

    protected NamedObjectQuery(QueryParameter<T> parameter)
    {
	this.parameter = parameter;
    }

    public NamedObjectQuery(QueryParameter<T> parameter, ObjectExchanger<T> exchanger)
    {
	this(parameter);
	this.exchanger = exchanger;
    }

    public void run()
	throws IOException
    {
	call();
    }

    public Collection<T> call()
	throws IOException
    {
	results = exchanger.getObjects(parameter);

	if(listeners != null)
	    listeners.fireQueryResultEvent(new QueryResultEvent<T>(this, 0, 1));

	return results;
    }

    public void addQueryResultListener(QueryResultListener<T> listener)
    {
	if(listeners == null)
	    listeners = new QueryResultListenerAdaptor<T>();
	listeners.addQueryResultListener(listener);
    }

    public void removeQueryResultListener(QueryResultListener<T> listener)
    {
	if(listeners != null)
	    listeners.removeQueryResultListener(listener);
    }

    protected void fireQueryResultEvent(QueryResultEvent<T> event)
    {
	if(listeners != null)
	    listeners.fireQueryResultEvent(event);
    }
}

