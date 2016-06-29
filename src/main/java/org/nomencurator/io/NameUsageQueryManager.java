/*
 * NameUsageQueryManager.java: an EventListener implementaiton
 * for a QueryResultEvent
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

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

/**
 * {@code NameUsageQueryManager} manages queries to multiple
 * {@code NameUsageExchanger}s
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameUsageQueryManager<N extends NameUsage<?, ?>>
    extends AbstractQueryManager<N, N, NameUsageExchanger<N, N>>
{

    public MultiplexQuery<N, N> getQuery(QueryParameter<N, N> parameter) {
	if(parameter != null && parameter instanceof NameUsageQueryParameter && sources != null)
	    return new MultiplexNameUsageQuery<N, N>((NameUsageQueryParameter<N, N>)parameter, sources);
	else 
	    return null;
	
    }
}

