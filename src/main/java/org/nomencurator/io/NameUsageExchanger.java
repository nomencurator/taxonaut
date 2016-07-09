/*
 * NameUsageExchanger.java: interface definiton to exchange NameUsages with
 * a data source
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

package org.nomencurator.io;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.Rank;

/**
 * {@code NameUsageExchanger} defines an intereface to exchange
 * {@code NameUsages}s of specified type with a data source
 *
 * @version 	02 July 2016
 * @author 	Nozomi `James' Ytow
 */
public interface NameUsageExchanger<T extends NameUsage<?>>
    extends ObjectExchanger<T>
{
    public final int EXCLUDE_SCIENTIFIC_NAMES = 0x100;
    public final int EXCLUDE_VERNACULAR_NAMES = 0x200;

    public final int FULL_HEIGHT = -1;
    public final int FULL_DEPTH = -1;

    /**
     * Returnes a {@code Collection} of {@code NameUsage}s having
     * the name and {@code rank}.  {@code rank}
     * may be ignored depending on the data source.
     *
     * @param query to be searched in the data source, a name literal or name literals separated by "|"
     * @param rank to be searchied in the data source
     *
     * @return NameUsage having {@code name}
     * and {@code rank}, or null if it doesn't exist in
     * the data source.
     */
    public Collection<NameUsage<?>> getNameUsages(String query, Rank rank);

    /**
     * Returnes an array of {@code NameUsage} having
     * {@code name} and {@code rank}.  {@code rank}
     * may be ignored depending on the data source.
     *
     * @param query to be searched in the data source, a name literal or name literals separated by "|"
     * @param rank to be searchied in the data source
     * @param matchingMode specifies the method for literal matching
     *
     * @return NameUsage having {@code name}
     * and {@code rank}, or null if it doesn't exist in
     * the data source.
     */
    public Collection<NameUsage<?>> getNameUsages(String query, Rank rank, MatchingMode matchingMode, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale);

    /**
     * Returns higher {@code NameUsage} above given
     * {@code nameUsage} until to specified {@code rank}
     * or to specified {@code height}
     * as the highest {@code NameUsage} of the higher {@code NameUsage}s
     * path, or null if null {@code nameUsage} is specified.
     * Specifying both null {@code rank} and negative {@code height}
     * results full path to the root name usage.
     *
     * @param nameUsage of interest
     * @param rank of the haigher name usage on the returend path
     * @param height of returend path
     *
     * @return NameUsage highest  {@code nameUsage} at the specified
     * {@code height}.
     */
    public Collection<NameUsage<?>> getHigher(NameUsage<?> nameUsage, Rank rank, int height);


    /**
     * Returns the highest {@code NameUsage} in the hierarchy
     * containing {@code nameUsage}.
     *
     * @param nameUsage of interest
     *
     * @return NameUsage representing highest taxon of the hierarchy.
     */
    public Collection<NameUsage<?>> getHigher(NameUsage<?> nameUsage);

    /**
     * Returns the highest {@code NameUsage}s in each hierarchy
     * containing {@code nameUsage}.
     *
     * @param nameUsage of interest
     *
     * @return NameUsage representing highest name usage containing the {@code nameUsage}
     */
    public Collection<NameUsage<?>> getRoots(NameUsage<?> nameUsage);

    /**
     * Returns the highest {@code NameUsage}s in each hierarchy
     * containing {@code NameUsage}s of {@code name}.
     *
     * @param query name of interest, a name literal or name literals separated by "|"
     *
     * @return array of NameUsage representing highest taxa of the hierarchies
     */
    public Collection<NameUsage<?>> getRoots(String query);

    /**
     * Returns a list of {@code NameUsage}s
     * having {@code nameUsage} as their higher {@code NameUsage}.
     * Eacth {@code NameUsage} in the returned List may contain its lower
     * {@code NameUsage}s until specified higher one of {@code rank}
     * or {@code depth}
     * by recursive access to the data source, or in  full depth if {@code rank} is null
     * and {@code depth} is negative.
     *
     * @param nameUsage of interest
     * @param rank limit of recursive search for lower {@code NameUsage}s.
     * @param depth limit of recursive search for lower {@code NameUsage}s.
     */
    public <N extends NameUsage<?>> List<N> getLowerNameUsages(N nameUsage, Rank rank, int depth);

    /**
     * Returns the full hierarchy containing
     * {@code nameUsage}.
     *
     * @param nameUsage of interest
     *
     * @return NameUsage representing highest taxon of returned hierarchy,
     * or null if {@code nameUsage} doesn't exit in the data source.
     */
    public Collection<NameUsage<?>> getHierarchies(NameUsage<?> nameUsage);



    /**
     * Reduces redundancy in partial hierarchies given by {@code nameUsages}.
     * 
     */
    public Collection<NameUsage<?>> integrateHierarchies(Collection<? extends NameUsage<?>> nameUsages);

    /**
     * Returns full hierarchies containing {@code NameUsage}s
     * of {@code name}.
     *
     * @param name of interest
     *
     * @return Array of <COCE>NameUsage}s representing highest taxon of returned hierarchy,
     * or null if {@code NameUsage} of {@code name} doesn't exit
     * in the data source.
     */
    public Collection<NameUsage<?>> getHierarchies(String name);

    /**
     * Returns a part of the hierarchy containing {@code nameUsage}.
     * It contains all taxa under the {@code nameUsage} and full
     * path to the highest taxon in the hierarhcy but not taxa side 
     * of the higher taxon path.  The returned {@code NameUsage}
     * could be the {@code nameUsage} itself if {@code nameUsage}
     * is the highest taxon of the hierarhcy.
     *
     * @param nameUsage of interest
     *
     * @return NameUsage representing highest taxon of returned hierarchy,
     * or null if {@code nameUsage} doesn't exit in the data source.
     */
    public Collection<NameUsage<?>> getPartialHierarchies(NameUsage<?> nameUsage);

    /**
     * Returns a part of the hierarchy containing {@code nameUsage}
     * with higher taxa upto {@code height} levels above and
     * lower taxa down to {@code depth} levels below.
     * The returned {@code NameUsage}
     * could be the {@code nameUsage} itself if {@code nameUsage}
     * is the highest taxon of the hierarhcy.
     *
     * @param nameUsage of interest
     * @param height levels of returned higher taxa
     * @param depth levels of returned lower taxa
     *
     * @return NameUsage representing highest taxon of returned hierarchy,
     * or null if {@code nameUsage} doesn't exit in the data source.
     */
    public Collection<NameUsage<?>> getPartialHierarchies(NameUsageQueryParameter<?> parameter);


    /**
     * Returns a part of the hierarchy containing {@code nameUsage}
     * with upto {@code heigher} rank taxa and
     * lower taxa down to rank of {@code lower} at lowest.
     * The returned {@code NameUsage}
     * could be the {@code nameUsage} itself if {@code nameUsage}
     * is the highest taxon of the hierarhcy.
     *
     * @param nameUsage of interest
     * @param heigher rank of highest taxa to be returned, or null to return in full height.
     * @param height levels of returned higher taxa
     * @param lower rank of lowest taxa to be returned, or null to return in full depth
     * @param depth levels of returned lower taxa
     *
     * @return NameUsage representing highest taxon of returned hierarchy,
     * or null if {@code nameUsage} doesn't exit in the data source.
     */
    public Collection<NameUsage<?>> getPartialHierarchies(NameUsage<?> nameUsage, Rank heigher, int height, Rank lower, int depth);

    /**
     * Returns partial hierarchies containing {@code NameUsage}
     * of {@code name}.
     * Each partial hierarhy contains all taxa just below the {@code NameUsage}
     * of the {@code name} and full path to the highest taxon in
     * the hierarhcy but not taxa side of the higher taxon path. 
     *
     * @param query of interest, a name literal or name literals separated by "|"
     *
     * @return {@code Collection} of <COCE>NameUsage}s representing highest taxon of returned hierarchy,
     * or null if {@code NameUsage} of {@code name} doesn't exit
     * in the data source.
     */
    public Collection<NameUsage<?>> getPartialHierarchies(String query);

    /**
     * Returns a collection of {@code NameUsage} relevant to
     * given {@code nameUsage}, or null if the method is not suppored by the data source.
     * Null parameter returns null also.
     *
     * @param nameUsage of interest
     *
     * @return {@code Collection} of {@code NameUsage} relevant
     * to the {@code nameUsage}, or null if the method is not suppored by the data source.
     * Null parameter returns null also.
     * 
     */
    public Collection<NameUsage<?>> getRelevantNameUsages(NameUsage<?> nameUsage);

    /**
     * Returns a collection of {@code NameUsage} relevant to
     * name usages of the {@code query}, or null if the method is not suppored by the data source.
     * Null parameter returns null also.
     *
     * @param query name of interest, a name literal or name literals separated by "|"
     *
     * @return {@code Collection} of {@code NameUsage} relevant
     * to name usages of the name, or null if the method is not suppored by the data source.
     * Null parameter returns null also.
     */
    public Collection<NameUsage<?>> getRelevantNameUsages(String query);

    /**
     * Returns a collections of {@code NameUsage} which have
     * names shared with descendent name usaages of the {@code nameUsage}.
     *
     * @param nameUsage of interest
     *
     * @return {@code Collection} of {@code NameUsage} having names shared
     * with descendent name usages of the {@code nameUsage}.
     */
    public Collection<NameUsage<?>> getDescendentNames(NameUsage<?> nameUsage);

    /**
     * Returns a collections of {@code NameUsage} which have
     * names shared with descendent name usaages of the {@code query}.
     *
     * @param query name of interest, a name literal or name literals separated by "|"
     *
     * @return {@code Collection} of {@code NameUsage} having names shared
     * with descendent name usages of name usages having the name.
     */
    public Collection<NameUsage<?>> getDescendentNames(String query);

    /**
     * Returns a collections of {@code NameUsage} which is treated
     * as a synonym of the {@code nameUsage}.   The result may contain
     * usages of accepted names if the {@code nameUsage} is a (junior) synonym.
     *
     * @param nameUsage of interest
     *
     * @return {@code Collection} of {@code NameUsage} which is treated
     * as synonum of the {@code nameUsage}.
     */
    //public Collection<NameUsage<T>> getSynonyms(NameUsage<T> nameUsage);

    /**
     * Sets {@code depth} as default depth in hierarchical search
     *
     * @param depth default depth to search down to
     */
    public void setDefaultDepth(int depth);

    /**
     * Returns default depth of hiearhcy aearch
     *
     * @return default depth to search down to
     */
    public int getDefaultDepth();

    /**
     * Sets {@code height} as default height in hierarchical search
     *
     * @param height default height to search upto
     */
    public void setDefaultHeight(int height);

    /**
     * Returns default height in hierarchical search
     *
     * @return default height to search upto
     */
    public int getDefaultHeight();

    /**
     * Sets {@code synonymMode} as default {@code SynonymMode}.
     *
     * @param synonymMode default value to set
     */
    // public void setDefaultSynonymMode(SynonymMode synonymMode);

    /**
     * Returns default {@code SynonymMode}.
     *
     * @return {@code SynonymMode} to be used to search
     */
    // public SynonymMode getDefaultSynonymMode();
}
