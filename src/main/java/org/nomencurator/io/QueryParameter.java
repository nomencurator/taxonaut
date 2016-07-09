/*
 * QueryParameter.java: a calss to specify parameters of ObjectExchanger querys
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

import java.util.Objects;

import org.nomencurator.model.NamedObject;

import lombok.Getter;
import lombok.Setter;

/**
 * <CODE>QueryParameter</CODE> provides a query parameter container.
 *
 * @version 	22 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class QueryParameter <T extends NamedObject<?>>
{
    /**/
    @Getter
	protected NamedObject<? extends T> filter;
    //protected E filter;

    @Setter
    protected String persistentID;

    @Setter
    protected String localKey;

    @Getter
    @Setter
    protected Boolean synchronous;

    @Getter
    @Setter
    protected MatchingMode matchingMode;

    @Getter
    @Setter
    protected QueryMode queryMode;

    public boolean equals(Object object)
    {
	if(this == object) return true;
	if(object == null) return false;
	if(getClass() != object.getClass()) return false;

	QueryParameter<?> that = (QueryParameter<?>) object;

	return super.equals(object) 
	    && Objects.equals(this.getFilter(), that.getFilter())
	    && Objects.equals(this.persistentID, that.persistentID)
	    && Objects.equals(this.localKey, that.localKey)
	    && Objects.equals(this.getSynchronous(), that.getSynchronous())
	    && Objects.equals(this.getMatchingMode(), that.getMatchingMode())
	    && Objects.equals(this.getQueryMode(), that.getQueryMode())
	    ;
    }

    public int hashCode() {
	return Objects.hash(super.hashCode(),
			    getFilter(),
			    persistentID,
			    localKey,
			    getSynchronous(),
			    getMatchingMode(),
			    getQueryMode()
			    );
    }

    public QueryParameter() {
	this(null, null, Boolean.FALSE, MatchingMode.EXACT, QueryMode.OBJECTS);
    }

    public QueryParameter(String localKey) {
	this(localKey, MatchingMode.EXACT);
    }

    public QueryParameter(String localKey, MatchingMode matchingMode) {
	this(null, localKey, Boolean.FALSE, matchingMode, QueryMode.OBJECTS);
    }

    public QueryParameter(NamedObject<? extends T> filter, Boolean synchronous, MatchingMode matchingMode, QueryMode queryMode) {
	setFilter(filter);
	setSynchronous(synchronous);
	setMatchingMode(matchingMode);
	setQueryMode(queryMode);
    }

    public QueryParameter(String localKey, Boolean synchronous, MatchingMode matchingMode, QueryMode queryMode) {
	this(null, localKey, synchronous, matchingMode, queryMode);
    }

    public QueryParameter(String persistentID, String localKey, Boolean synchronous, MatchingMode matchingMode, QueryMode queryMode) {
	setPersistentID(persistentID);
	setLocalKey(localKey);
	setSynchronous(synchronous);
	setMatchingMode(matchingMode);
	setQueryMode(queryMode);
    }

    public synchronized void setFilter(NamedObject<? extends T> filter)
    {
	if(filter != null) {
	    setPersistentID(null);
	    setLocalKey(null);
	}
	this.filter = filter;
    }

    public String getLocalKey()
    {
	return (localKey != null) ? localKey: ((filter != null) ? filter.getLocalKey(): null);
    }

    public String getPersisntetID()
    {
	return (persistentID != null) ? persistentID: ((filter != null) ? filter.getPersistentID(): null);
    }
}
