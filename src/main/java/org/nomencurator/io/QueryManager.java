/*
 * QueryManager.java: an interface to manage multiple ObjectExchangers
 *
 * Copyright (c) 2006, 2014, 2015 Nozomi `James' Ytow
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

import org.nomencurator.util.ArrayUtility;

/**
 * <CODE>QueryManager</CODE> manages queries to multiple
 * <CODE>DataExchanger</CODE>s
 *
 * @version 	10 July 2015
 * @author 	Nozomi `James' Ytow
 */
//public interface QueryManager <N extends NamedObject<?, ?>, T extends N>
//    extends QueryResultListener<N, T>, ObjectExchanger<N, T>
public interface QueryManager <N extends NamedObject<?, ?>, T extends N,
								      //								      E extends NamedObject<N, T>,
								      X extends ObjectExchanger<N, T>//,
										//P extends QueryParameter<N, T>
								      //								      Q extends ObjectQuery<N, T>>
//public interface QueryManager <E extends NamedObject<?, ?>>,
//					 X extends ObjectExchanger<E>
										>
{
    //public MultiplexQuery<N, T, Q> getQuery(P parameter);
    public MultiplexQuery<N, T> getQuery(QueryParameter<N, T> parameter);
    //public MultiplexQuery<E> getQuery(QueryParameter<E> parameter);

    public boolean addSource(X source);
    //public boolean addSource(ObjectExchanger<E> source);
    //public boolean addSource(ObjectExchanger<?, ?> source);

    public boolean removeSource(X source);
    //public boolean removeSource(ObjectExchanger<E> source);
    //public boolean removeSource(ObjectExchanger<?, ?> source);

    public boolean setSynchronous(boolean synchronous);

    public boolean isSynchronous();
}
