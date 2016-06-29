/*
 * QueryParameter.java: a calss to specify parameters of ObjectExchanger querys
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

import org.nomencurator.model.NamedObject;

import lombok.Getter;
import lombok.Setter;

/**
 * <CODE>QueryParameter</CODE> provides a query parameter container.
 *
 * @version 	09 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class NamedObjectQueryParameter <N extends NamedObject<?, ?>>
    extends QueryParameter <N, N>
{
    public NamedObjectQueryParameter() {
	super();
    }

    public NamedObjectQueryParameter(String localKey) {
	super(localKey);
    }

    public NamedObjectQueryParameter(String localKey, MatchingMode matchingMode) {
	super(localKey, matchingMode);
    }

    public NamedObjectQueryParameter(NamedObject<N, N> filter, Boolean synchronous, MatchingMode matchingMode, QueryMode queryMode) {
	super(filter, synchronous, matchingMode, queryMode);
    }

    public NamedObjectQueryParameter(String localKey, Boolean synchronous, MatchingMode matchingMode, QueryMode queryMode) {
	super(localKey, synchronous, matchingMode, queryMode);
    }

    public NamedObjectQueryParameter(String persistentID, String localKey, Boolean synchronous, MatchingMode matchingMode, QueryMode queryMode) {
	super(persistentID, localKey, synchronous, matchingMode, queryMode);
    }
}
