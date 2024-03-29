/*
 * QueryEvent.java: Event encapsulating a query parameter
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

import java.util.Collection;
import java.util.EventObject;

import lombok.Getter;

import org.nomencurator.model.NamedObject;

/**
 * {@code QueryEvent} provides an event to manage a query parameter.
 *
 * @version 	02 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class QueryEvent<T extends NamedObject<?>>
    extends EventObject
{
    private static final long serialVersionUID = 4302605739900827696L;

    @Getter
    protected QueryParameter<T> queryParameter;

    public QueryEvent(Object source, QueryParameter<T> queryParameter)
    {
	super(source);
	this.queryParameter = queryParameter;
    }
}
