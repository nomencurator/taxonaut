/*
 * MultiplexNameUsageQuery.java
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

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.nomencurator.model.NameUsage;

import lombok.Getter;

/**
 * {@code MultiplexNameUsageQuery} implements {@code ObjectExchanger}.
 *
 * @version 	29 June. 2016
 * @author 	Nozomi `James' Ytow
 */
public class MultiplexNameUsageQuery<N extends NameUsage<?, ?>, T extends N>
    extends AbstractMultiplexQuery<N, T>
{
    protected NamedObjectQuery<N, T> createQuery(QueryParameter<N, T> parameter, ObjectExchanger<N, T> source) {
	if(parameter instanceof NameUsageExchanger && source instanceof NameUsageExchanger)
	    return new NamedObjectQuery<N, T>((NameUsageQueryParameter<N, T>)parameter, (NameUsageExchanger<N, T>)source);
	return null;
    }

    public MultiplexNameUsageQuery(NameUsageQueryParameter<N, T> parameter, Collection<? extends NameUsageExchanger<N, T>> sources) {
	super(parameter, sources);
    }

    public MultiplexNameUsageQuery(Collection<? extends NamedObjectQuery<N, T>> queries)
    {
	super(queries);
    }
}

