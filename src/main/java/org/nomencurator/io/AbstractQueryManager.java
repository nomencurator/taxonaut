/*
 * AbstractQueryManager.java: an interface to manage multiple ObjectExchangers
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
import java.util.List;

import org.nomencurator.model.NamedObject;

/**
 * {@code AbstractQueryManager} manages queries to multiple
 * {@code DataExchanger}s
 *
 * @version 	02 July 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractQueryManager <T extends NamedObject<?>,
						      X extends ObjectExchanger<T>//,
								//P extends QueryParameter<T>
								//Q extends ObjectQuery<T>
								>
    implements QueryManager<T, X>
{
    protected Collection<X> sources;

    protected boolean synchronous;
    
    public synchronized boolean addSource(X source)
    {
	boolean result = false;
	if(source != null) {
	    if(sources == null)
		sources = Collections.synchronizedList(new ArrayList<X>());
	    synchronized (sources) {
		result |= sources.add(source);
	    }
	}
	return result;
    }

    public synchronized boolean removeSource(X source)
    {
	boolean result = false;
	if(source != null) {
	    synchronized (sources) {
		result |= sources.remove(source);
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
