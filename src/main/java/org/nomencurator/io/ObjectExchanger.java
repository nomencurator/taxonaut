/*
 * ObjectExchanger.java: interface definition to exchange Objects with
 * a data source
 *
 * Copyright (c) 2006,2014, 2015 Nozomi `James' Ytow
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
 * <CODE>ObjectExchanger</CODE> defines an intereface to exchange
 * <CODE>Object</CODE>s of specified type with a data source
 *
 * @version 	07 Sep. 2015
 * @author 	Nozomi `James' Ytow
 */
public interface ObjectExchanger<N extends NamedObject<?, ?>, T extends N>
{
    /**
     * Returnes an array of <CODE>E</CODE>, each of which
     * matches to <CODE>queryParameter</CODE>, in the data source.
     * If there is no data of type <CODE>E</CODE> matches
     * to <CODE>queryParameter</CODE>, it returns a zero-sized array.
     *
     * @param queryParameter used to search in the data source
     *
     * @return array of <CODE>E</CODE> matching to <CODE>queryParameter</CODE>
     */
    //public Collection<NamedObject<N, T>> getObjects(QueryParameter<N, T> queryParameter);
    public Collection<T> getObjects(QueryParameter<N, T> queryParameter);

    /**
     * Returnes an array of <CODE>E</CODE>, each of which
     * matches to <CODE>queryParameter</CODE>, in the data source.
     * If there is no data of type <CODE>E</CODE> matches
     * to <CODE>queryParameter</CODE>, it returns a zero-sized array.
     *
     * @param queryParameter used to search in the data source
     *
     * @return array of <CODE>E</CODE> matching to <CODE>queryParameter</CODE>
     */
    //public Collection<NamedObject<N, T>> getObjects(String query);
    public Collection<T> getObjects(String query);

    /**
     * Returnes an array of <CODE>E</CODE>, each of which
     * matches to <CODE>queryParameter</CODE>, in the data source
     * using matching type <CODE>queryType</CODE>.
     * If there is no data of type <CODE>E</CODE> matchies
     * to <CODE>queryParameter</CODE>, it returns a zero-sized array.
     *
     * @param queryParameter used to search in the data source
     * @param queryType type of query to the data source
     *
     * @return <CODE>E</CODE> having <CODE>queryParameter</CODE>
     */
    //public Collection<NamedObject<N, T>> getObjects(String query, MatchingMode matchingMode);
    public Collection<T> getObjects(String query, MatchingMode matchingMode);

    /**
     * Sets <CODE>queryType</CODE> as default type of query
     *
     * @param queryType type of query
     */
    public void setDefaultMatchingMode(MatchingMode matchingMode);
    
    /**
     * Returns default matching mode
     *
     * @param defaul <tt>MatchingMode</tt>
     */
    public MatchingMode getDefaultMatchingMode();

    /**
     * Returns default query mode
     *
     * @param defaul <tt>QueryMode</tt>
     */
    //public QueryMode getDefaultQueryMode();

    //public ObjectExchanger<N, T> getExchanger(NamedObject<N, T> namedObject);
    public ObjectExchanger<?, ?> getExchanger(NamedObject<?, ?> namedObject);

    /**
     * Clears object cache if the <code>Exchanger</code> has.
     */
    public void clear();
}
