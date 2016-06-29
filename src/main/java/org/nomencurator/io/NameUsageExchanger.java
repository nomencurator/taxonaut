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
 * <CODE>NameUsageExchanger</CODE> defines an intereface to exchange
 * <CODE>NameUsages</CODE>s of specified type with a data source
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public interface NameUsageExchanger<N extends NameUsage<?, ?>, T extends N>
    extends ObjectExchanger<N, T>
{
    public final int EXCLUDE_SCIENTIFIC_NAMES = 0x100;
    public final int EXCLUDE_VERNACULAR_NAMES = 0x200;

    public final int FULL_HEIGHT = -1;
    public final int FULL_DEPTH = -1;

    /**
     * Returnes a <tt>Collection</tt> of <CODE>NameUsage</CODE>s having
     * the name and <CODE>rank</CODE>.  <CODE>rank</CODE>
     * may be ignored depending on the data source.
     *
     * @param query to be searched in the data source, a name literal or name literals separated by "|"
     * @param rank to be searchied in the data source
     *
     * @return NameUsage having <CODE>name</CODE>
     * and <CODE>rank</CODE>, or null if it doesn't exist in
     * the data source.
     */
    public Collection<NameUsage<?, ?>> getNameUsages(String query, Rank rank);

    /**
     * Returnes an array of <CODE>NameUsage</CODE> having
     * <CODE>name</CODE> and <CODE>rank</CODE>.  <CODE>rank</CODE>
     * may be ignored depending on the data source.
     *
     * @param query to be searched in the data source, a name literal or name literals separated by "|"
     * @param rank to be searchied in the data source
     * @param matchingMode specifies the method for literal matching
     *
     * @return NameUsage having <CODE>name</CODE>
     * and <CODE>rank</CODE>, or null if it doesn't exist in
     * the data source.
     */
    public Collection<NameUsage<?, ?>> getNameUsages(String query, Rank rank, MatchingMode matchingMode, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale);

    /**
     * Returns higher <CODE>NameUsage</CODE> above given
     * <CODE>nameUsage</CODE> until to specified <tt>rank</tt>
     * or to specified <tt>height</tt>
     * as the highest <tt>NameUsage</tt> of the higher <tt>NameUsage</tt>s
     * path, or null if null <CODE>nameUsage</CODE> is specified.
     * Specifying both null <tt>rank</tt> and negative <tt>height</tt>
     * results full path to the root name usage.
     *
     * @param nameUsage of interest
     * @param rank of the haigher name usage on the returend path
     * @param height of returend path
     *
     * @return NameUsage highest  <CODE>nameUsage</CODE> at the specified
     * <tt>height</tt>.
     */
    public Collection<NameUsage<?, ?>> getHigher(NameUsage<?, ?> nameUsage, Rank rank, int height);

    /**
     * Returns the highest <CODE>NameUsage</CODE> in the hierarchy
     * containing <CODE>nameUsage</CODE>.
     *
     * @param nameUsage of interest
     *
     * @return NameUsage representing highest taxon of the hierarchy.
     */
    public Collection<NameUsage<?, ?>> getHigher(NameUsage<?, ?> nameUsage);

    /**
     * Returns the highest <CODE>NameUsage</CODE>s in each hierarchy
     * containing <CODE>nameUsage</CODE>.
     *
     * @param nameUsage of interest
     *
     * @return NameUsage representing highest name usage containing the <tt>nameUsage</tt>
     */
    public Collection<NameUsage<?, ?>> getRoots(NameUsage<?, ?> nameUsage);

    /**
     * Returns the highest <CODE>NameUsage</CODE>s in each hierarchy
     * containing <CODE>NameUsage</CODE>s of <CODE>name</CODE>.
     *
     * @param query name of interest, a name literal or name literals separated by "|"
     *
     * @return array of NameUsage representing highest taxa of the hierarchies
     */
    public Collection<NameUsage<?, ?>> getRoots(String query);

    /**
     * Returns a list of <CODE>NameUsage</CODE>s
     * having <CODE>nameUsage</CODE> as their higher <CODE>NameUsage</CODE>.
     * Eacth <CODE>NameUsage</CODE> in the returned List may contain its lower
     * <CODE>NameUsage</CODE>s until specified higher one of <CODE>rank</CODE>
     * or <tt>depth</tt>
     * by recursive access to the data source, or in  full depth if <tt>rank</tt> is null
     * and <tt>depth</tt> is negative.
     *
     * @param nameUsage of interest
     * @param rank limit of recursive search for lower <CODE>NameUsage</CODE>s.
     * @param depth limit of recursive search for lower <CODE>NameUsage</CODE>s.
     */
    public List<NameUsage<?, ?>> getLowerNameUsages(NameUsage<?, ?> nameUsage, Rank rank, int depth);

    /**
     * Returns the full hierarchy containing
     * <CODE>nameUsage</CODE>.
     *
     * @param nameUsage of interest
     *
     * @return NameUsage representing highest taxon of returned hierarchy,
     * or null if <CODE>nameUsage</CODE> doesn't exit in the data source.
     */
    public Collection<NameUsage<?, ?>> getHierarchies(NameUsage<?, ?> nameUsage);


    /**
     * Reduces redundancy in partial hierarchies given by <tt>nameUsages</tt>.
     * 
     */
    public Collection<NameUsage<?, ?>> integrateHierarchies(Collection<? extends NameUsage<?, ?>> nameUsages);

    /**
     * Returns full hierarchies containing <CODE>NameUsage</CODE>s
     * of <CODE>name</CODE>.
     *
     * @param name of interest
     *
     * @return Array of <COCE>NameUsage</CODE>s representing highest taxon of returned hierarchy,
     * or null if <CODE>NameUsage</CODE> of <CODE>name</CODE> doesn't exit
     * in the data source.
     */
    public Collection<NameUsage<?, ?>> getHierarchies(String name);

    /**
     * Returns a part of the hierarchy containing <CODE>nameUsage</CODE>.
     * It contains all taxa under the <CODE>nameUsage</CODE> and full
     * path to the highest taxon in the hierarhcy but not taxa side 
     * of the higher taxon path.  The returned <CODE>NameUsage</CODE>
     * could be the <CODE>nameUsage</CODE> itself if <CODE>nameUsage</CODE>
     * is the highest taxon of the hierarhcy.
     *
     * @param nameUsage of interest
     *
     * @return NameUsage representing highest taxon of returned hierarchy,
     * or null if <CODE>nameUsage</CODE> doesn't exit in the data source.
     */
    public Collection<NameUsage<?, ?>> getPartialHierarchies(NameUsage<?, ?> nameUsage);

    /**
     * Returns a part of the hierarchy containing <CODE>nameUsage</CODE>
     * with higher taxa upto <CODE>height</CODE> levels above and
     * lower taxa down to <CODE>depth</CODE> levels below.
     * The returned <CODE>NameUsage</CODE>
     * could be the <CODE>nameUsage</CODE> itself if <CODE>nameUsage</CODE>
     * is the highest taxon of the hierarhcy.
     *
     * @param nameUsage of interest
     * @param height levels of returned higher taxa
     * @param depth levels of returned lower taxa
     *
     * @return NameUsage representing highest taxon of returned hierarchy,
     * or null if <CODE>nameUsage</CODE> doesn't exit in the data source.
     */
    public Collection<NameUsage<?, ?>> getPartialHierarchies(NameUsageQueryParameter<?, ?> parameter);


    /**
     * Returns a part of the hierarchy containing <CODE>nameUsage</CODE>
     * with upto <tt>heigher</tt> rank taxa and
     * lower taxa down to rank of <CODE>lower</CODE> at lowest.
     * The returned <CODE>NameUsage</CODE>
     * could be the <CODE>nameUsage</CODE> itself if <CODE>nameUsage</CODE>
     * is the highest taxon of the hierarhcy.
     *
     * @param nameUsage of interest
     * @param heigher rank of highest taxa to be returned, or null to return in full height.
     * @param height levels of returned higher taxa
     * @param lower rank of lowest taxa to be returned, or null to return in full depth
     * @param depth levels of returned lower taxa
     *
     * @return NameUsage representing highest taxon of returned hierarchy,
     * or null if <CODE>nameUsage</CODE> doesn't exit in the data source.
     */
    public Collection<NameUsage<?, ?>> getPartialHierarchies(NameUsage<?, ?> nameUsage, Rank heigher, int height, Rank lower, int depth);

    /**
     * Returns partial hierarchies containing <CODE>NameUsage</CODE>
     * of <CODE>name</CODE>.
     * Each partial hierarhy contains all taxa just below the <CODE>NameUsage</CODE>
     * of the <CODE>name</CODE> and full path to the highest taxon in
     * the hierarhcy but not taxa side of the higher taxon path. 
     *
     * @param query of interest, a name literal or name literals separated by "|"
     *
     * @return <tt>Collection</tt> of <COCE>NameUsage</CODE>s representing highest taxon of returned hierarchy,
     * or null if <CODE>NameUsage</CODE> of <CODE>name</CODE> doesn't exit
     * in the data source.
     */
    public Collection<NameUsage<?, ?>> getPartialHierarchies(String query);

    /**
     * Returns a collection of <CODE>NameUsage</CODE> relevant to
     * given <CODE>nameUsage</CODE>, or null if the method is not suppored by the data source.
     * Null parameter returns null also.
     *
     * @param nameUsage of interest
     *
     * @return <tt>Collection</tt> of <tt>NameUsage</tt> relevant
     * to the <CODE>nameUsage</CODE>, or null if the method is not suppored by the data source.
     * Null parameter returns null also.
     * 
     */
    public Collection<NameUsage<?, ?>> getRelevantNameUsages(NameUsage<?, ?> nameUsage);

    /**
     * Returns a collection of <CODE>NameUsage</CODE> relevant to
     * name usages of the <CODE>query</CODE>, or null if the method is not suppored by the data source.
     * Null parameter returns null also.
     *
     * @param query name of interest, a name literal or name literals separated by "|"
     *
     * @return <tt>Collection</tt> of <tt>NameUsage</tt> relevant
     * to name usages of the name, or null if the method is not suppored by the data source.
     * Null parameter returns null also.
     */
    public Collection<NameUsage<?, ?>> getRelevantNameUsages(String query);

    /**
     * Returns a collections of <CODE>NameUsage</CODE> which have
     * names shared with descendent name usaages of the <CODE>nameUsage</CODE>.
     *
     * @param nameUsage of interest
     *
     * @return <tt>Collection</tt> of <tt>NameUsage</tt> having names shared
     * with descendent name usages of the <CODE>nameUsage</CODE>.
     */
    public Collection<NameUsage<?, ?>> getDescendentNames(NameUsage<?, ?> nameUsage);

    /**
     * Returns a collections of <CODE>NameUsage</CODE> which have
     * names shared with descendent name usaages of the <CODE>query</CODE>.
     *
     * @param query name of interest, a name literal or name literals separated by "|"
     *
     * @return <tt>Collection</tt> of <tt>NameUsage</tt> having names shared
     * with descendent name usages of name usages having the name.
     */
    public Collection<NameUsage<?, ?>> getDescendentNames(String query);

    /**
     * Returns a collections of <CODE>NameUsage</CODE> which is treated
     * as a synonym of the <CODE>nameUsage</CODE>.   The result may contain
     * usages of accepted names if the <CODE>nameUsage</CODE> is a (junior) synonym.
     *
     * @param nameUsage of interest
     *
     * @return <tt>Collection</tt> of <tt>NameUsage</tt> which is treated
     * as synonum of the <CODE>nameUsage</CODE>.
     */
    //public Collection<NameUsage<N, T>> getSynonyms(NameUsage<N, T> nameUsage);

    /**
     * Sets <CODE>depth</CODE> as default depth in hierarchical search
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
     * Sets <CODE>height</CODE> as default height in hierarchical search
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
     * Sets <tt>synonymMode</tt> as default <tt>SynonymMode</tt>.
     *
     * @param synonymMode default value to set
     */
    // public void setDefaultSynonymMode(SynonymMode synonymMode);

    /**
     * Returns default <tt>SynonymMode</tt>.
     *
     * @return <tt>SynonymMode</tt> to be used to search
     */
    // public SynonymMode getDefaultSynonymMode();
}
