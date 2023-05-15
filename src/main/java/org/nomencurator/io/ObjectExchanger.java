/*
 * ObjectExchanger.java: interface definition to exchange Objects with
 * a data source
 *
 * Copyright (c) 2006,2014, 2015, 2016 Nozomi `James' Ytow
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

import java.io.IOException;

import java.util.Collection;

import org.nomencurator.model.NamedObject;

/**
 * {@code ObjectExchanger} defines an intereface to exchange
 * {@code Object}s of specified type with a data source
 *
 * @version 	15 Oct. 2016
 * @author 	Nozomi `James' Ytow
 */
public interface ObjectExchanger<T extends NamedObject<?>>
{
    /**
     * Returnes an array of {@code E}, each of which
     * matches to {@code queryParameter}, in the data source.
     * If there is no data of type {@code E} matches
     * to {@code queryParameter}, it returns a zero-sized array.
     *
     * @param queryParameter used to search in the data source
     *
     * @return array of {@code E} matching to {@code queryParameter}
     */
    public Collection<T> getObjects(QueryParameter<T> queryParameter) throws IOException;

    /**
     * Returnes an array of {@code E}, each of which
     * matches to {@code queryParameter}, in the data source.
     * If there is no data of type {@code E} matches
     * to {@code queryParameter}, it returns a zero-sized array.
     *
     * @param queryParameter used to search in the data source
     *
     * @return array of {@code E} matching to {@code queryParameter}
     */
    public Collection<T> getObjects(String query) throws IOException;

    /**
     * Returnes an array of {@code E}, each of which
     * matches to {@code queryParameter}, in the data source
     * using matching type {@code queryType}.
     * If there is no data of type {@code E} matchies
     * to {@code queryParameter}, it returns a zero-sized array.
     *
     * @param queryParameter used to search in the data source
     * @param queryType type of query to the data source
     *
     * @return {@code E} having {@code queryParameter}
     */
    public Collection<T> getObjects(String query, MatchingMode matchingMode) throws IOException;

    /**
     * Sets {@code queryType} as default type of query
     *
     * @param queryType type of query
     */
    public void setDefaultMatchingMode(MatchingMode matchingMode);
    
    /**
     * Returns default matching mode
     *
     */
    public MatchingMode getDefaultMatchingMode();

    /**
     * Returns default query mode
     */
    //public QueryMode getDefaultQueryMode();

    public ObjectExchanger<?> getExchanger(NamedObject<?> namedObject);

    /**
     * Clears object cache if the <code>Exchanger</code> has.
     */
    public void clear();
}
