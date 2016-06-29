/*
 * AbstractQueryManager.java: an interface to manage multiple ObjectExchangers
 *
 * Copyright (c) 2014, 2015 Nozomi `James' Ytow
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
import java.util.List;

import org.nomencurator.model.NamedObject;

/**
 * <CODE>AbstractQueryManager</CODE> manages queries to multiple
 * <CODE>DataExchanger</CODE>s
 *
 * @version 	08 July 2015
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractQueryManager <N extends NamedObject<?, ?>, T extends N, 
//										   E extends NamedObject<N, T>,
											     X extends ObjectExchanger<N, T>//,
											     //P extends QueryParameter<N, T>
											     //Q extends ObjectQuery<N, T>
											     >
    implements QueryManager<N, T, X>
//public abstract class AbstractQueryManager <E extends NamedObject<?, ?>, X extends ObjectExchanger<E>>
//    implements QueryManager<E, X>
{
    //protected List<X> sources;
    protected Collection<X> sources;
    //protected Collection<? exntends ObjectExchanger<?, ?>> sources;

    protected boolean synchronous;
    
    public synchronized boolean addSource(X source)
	//public synchronized boolean addSource(ObjectExchanger<?, ?> source)
    {
	boolean result = false;
	if(source != null) {
	    if(sources == null)
		sources = Collections.synchronizedList(new ArrayList<X>());
	    //sources = Collections.synchronizedList(new ArrayList<ObjectExchanger<?, ?>>());
	    synchronized (sources) {
		// source.addQueryResultListener(this);
		// source.setDefaultMatchingMode(getDefaultMatchingMode());
		result |= sources.add(source);
	    }
	}
	return result;
    }

    public synchronized boolean removeSource(X source)
	//public synchronized boolean removeSource(ObjectExchanger<?, ?> source)
    {
	boolean result = false;
	if(source != null) {
	    synchronized (sources) {
		result |= sources.remove(source);
		// source.removeQueryResultListener(this);
	    }
	}

	return result;
    }

    public boolean setSynchronous(boolean synchronous)
    {
	this.synchronous = synchronous;
	return true;
    }

    public boolean isSynchronous()
    {
	return synchronous;
    }
}
