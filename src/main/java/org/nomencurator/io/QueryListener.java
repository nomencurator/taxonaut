/*
 * QueryListener.java: an EventListener handling a QueryEvent
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

import java.util.EventListener;

import org.nomencurator.model.NamedObject;

/**
 * {@code QueryListener} defines API to receive a {@code QueryEvent}.
 *
 * @version 	02 July 2016
 * @author 	Nozomi `James' Ytow
 */
public interface QueryListener<T extends NamedObject<?>>
    extends EventListener
{
    /**
     * <tt>QuerEvent</tt> handler.
     *
     * @param event <tt>QueryEvent</tt> to handle.
     */
    public void query(QueryEvent<T> event);
}
