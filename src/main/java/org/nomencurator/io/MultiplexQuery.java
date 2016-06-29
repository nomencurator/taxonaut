/*
 * MultiplexQuery.java
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

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.nomencurator.model.NamedObject;

import lombok.Getter;

/**
 * <CODE>MultiplexQuery</CODE> implements <tt>ObjectExchanger</tt>.
 *
 * @version 	08 July 2015
 * @author 	Nozomi `James' Ytow
 */
//public interface MultiplexQuery<N extends NamedObject<?, ?>, T extends N, E extends NamedObject<N, T>>
public interface MultiplexQuery<N extends NamedObject<?, ?>, T extends N>
    extends QueryResultListener<N, T>, ObjectQuery<N, T>
{
    //public boolean addQuery(Q query);
    public boolean addQuery(ObjectQuery<N, T> query);
    //public boolean addQuery(ObjectQuery query);

    //public boolean removeQuery(Q query);
    public boolean removeQuery(ObjectQuery<N, T> query);
    //public boolean removeQuery(ObjectQuery query);
}
