/*
 * ObjectQuery.java
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

// import java.util.ArrayList;
import java.util.Collection;
// import java.util.Collections;
// import java.util.List;

import java.util.concurrent.Callable;

import org.nomencurator.model.NamedObject;

import lombok.Getter;

/**
 * <CODE>ObjectQuery</CODE> implements <tt>ObjectExchanger</tt>.
 *
 * @version 	10 July 2015
 * @author 	Nozomi `James' Ytow
 */
//public interface ObjectQuery<E extends NamedObject<?, ?>>
//    extends Callable<Collection<E>>
public interface ObjectQuery<N extends NamedObject<?, ?>, T extends N>
								    //				       extends Callable<Collection<NamedObject<N, T>>>
				       extends Callable<Collection<T>>
{
    public ObjectExchanger<N, T> getExchanger();
    //public ObjectExchanger<E> getExchanger();

    public QueryParameter<N, T> getParameter();
    //public QueryParameter<E> getParameter();

    //public Collection<NamedObject<N, T>> getResults();
    public Collection<T> getResults();
    //public Collection<Collection<E>> getResults();

    public void addQueryResultListener(QueryResultListener<N, T> listener);
    //public void addQueryResultListener(QueryResultListener<E> listener);
    
    public void removeQueryResultListener(QueryResultListener<N, T> listener);
    //public void removeQueryResultListener(QueryResultListener<E> listener);
}

