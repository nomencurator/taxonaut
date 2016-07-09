/*
 * MultiplexNamedObjectQuery.java
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

import java.util.Collection;

import org.nomencurator.model.NamedObject;

/**
 * {@code MultiplexNamedObjectQuery} implements {@code ObjectExchange}.
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class MultiplexNamedObjectQuery<T extends NamedObject<?>>
    extends AbstractMultiplexQuery<T>
{
    protected NamedObjectQuery<T> createQuery(QueryParameter<T> parameter, ObjectExchanger<T> source)
    {
	return new NamedObjectQuery<T>(parameter, source);
    }

    public MultiplexNamedObjectQuery(QueryParameter<T> parameter, Collection<? extends ObjectExchanger<T>> sources)
    {
	super(parameter, sources);
    }

    public MultiplexNamedObjectQuery(Collection<? extends NamedObjectQuery<T>> queries)
    {
	super(queries);
    }
}

