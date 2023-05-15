/*
 * QueryResultListener.java: an EventListener for a QueryResultEvent
 *
 * Copyright (c) 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP17300071
 */

package org.nomencurator.io;

import java.util.EventListener;

import org.nomencurator.model.NamedObject;

/**
 * {@code QueryResultListener} defines API to receive 
 * a {@code QueryResultEvent}..
 *
 * @version 	08 July 2016
 * @author 	Nozomi `James' Ytow
 */
public interface QueryResultListener<T extends NamedObject<?>>
    extends EventListener
{
    public void queryReturned(QueryResultEvent<T> event);
}
