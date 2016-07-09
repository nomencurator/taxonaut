/*
 * NameQuery.java:  an interface representing a name relevant query
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

package org.nomencurator.gui.swing;

import java.util.Collection;
import java.util.Locale;

import org.nomencurator.io.MatchingMode;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

/**
 * {@code NameQuery} provides an interface representing a query relevant to names.
 *
 * @version 	02 July 2016
 * @author 	Nozomi `James' Ytow
 */
public interface NameQuery
{
    /**
     * Sets {@code locale} as the locale of this {@code NameQuery}.
     *
     * @param locale to set
     */
    public void setLocale(Locale locale);

    /**
     * Gets {@code Locale} of  this {@code NameQuery}.
     *
     * @return Locale of the {@code NameQuery}
     */
    public Locale getLocale();

    /**
     * Search for {@code NameUsage} of given {@code rank},
     * {@code name}, {@code authority} and {@code year},
     * and returns result message {@code String}.
     */
    public String getNames(String name, Rank rank, String authority, String year, MatchingMode queryType);

    //public String getNames(NameUsage<?>[] names, String rank, String name, String authority, String year, MatchingMode queryType);

    public String getNames(Collection<? extends NameUsage<?>> names, String name, Rank rank, String authority, String year, MatchingMode queryType);
}
