/*
 * AbstractNameUsageExchanger.java
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

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.nomencurator.io.QueryMode.*;
import org.nomencurator.io.MatchingMode.*;

import org.nomencurator.model.NamedObject;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import org.nomencurator.util.CollectionUtility;

/**
 * {@code AbstractNameUsageExchanger} provides an abstract implementation of
 * {@code NameUsageExchanger}.
 *
 * @version 	15 Oct.. 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractNameUsageExchanger<T extends NameUsage<?>>
    extends AbstractObjectExchanger<T>
    implements NameUsageExchanger<T>
{
    public Collection<T> getObjects(QueryParameter<T> queryParameter)
	throws IOException
    {
	if(queryParameter == null)
	    return null;

	if(!(queryParameter instanceof NameUsageQueryParameter) || queryParameter.getQueryMode() == QueryMode.OBJECTS)
	    return getObjects(queryParameter.getLocalKey(), queryParameter.getMatchingMode());

	NameUsageQueryParameter<T> parameter =  (NameUsageQueryParameter<T>) queryParameter;

	Collection<T> results = null;

	switch(parameter.getQueryMode()) {
	case NAMEUSAGES:
           results = getObjects(getNameUsages(parameter.getLiteral(), parameter.getRank(), parameter.getMatchingMode(), parameter.isIncludeBasionyms(), parameter.isIncludeSynonyms(), parameter.isIncludeVernaculars(), parameter.getLocale()));
	    break;
	case HIGHER:
	    results = getObjects(getHigher(parameter.getNameUsageFilter(), parameter.getRank(), parameter.getHeight()));
	    break;
	case ROOTS:
	    results = getObjects(getRoots(parameter.getNameUsageFilter()));
	    break;
	case PARTIAL_HIERARCHIES:
	    results = getObjects(getPartialHierarchies(parameter));
	    break;
	case HIERARCHIES:
	    results = getObjects(getHierarchies(parameter.getNameUsageFilter()));
	    break;
	case LOWER_NAMEUSAGES:
	    results = getObjects(getLowerNameUsages(parameter.getNameUsageFilter(), parameter.getRank(), parameter.getDepth()));
	    break;
	case RELEVANT:
	    results = getObjects(getRelevantNameUsages(parameter.getNameUsageFilter()));
	    break;
	case DESCENDANT_NAMES:
	    results = getObjects(getDescendentNames(parameter.getNameUsageFilter()));
	    break;
	default:
	    results = super.getObjects(queryParameter);
	    break;
	}
	return results;
    }
    
    // FIXME: reconsider argument type
    protected List<T> getObjects(Collection<? extends NameUsage<T>> nameUsages)
	throws IOException
    {
	if(nameUsages == null)
	    return null;
	List<T> objects = new ArrayList<>(nameUsages.size());
       for(NameUsage<T> nameUsage : nameUsages) {
	   @SuppressWarnings("unchecked")
	       T usage = (T)nameUsage;
	   objects.add(usage);
	}
	return objects;
    }

    public Collection<NameUsage<T>> getNameUsages(String query, Rank rank)
	throws IOException
    {
	return getNameUsages(query, rank, null);
    }

    public Collection<NameUsage<T>> getNameUsages(String query, Rank rank, Collection<String> filter)
	throws IOException
    {
	return getNameUsages(query, rank, filter, getDefaultMatchingMode(), false, false, false, null);
    }

    public Collection<NameUsage<T>> getNameUsages(String query, Rank rank, MatchingMode matchingMode, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
	throws IOException
    {
	return getNameUsages(query, rank, null, matchingMode, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    public Collection<NameUsage<T>> getNameUsages(String query, Rank rank, Collection<String> filter, MatchingMode matchingMode, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
	throws IOException
    {
	Collection<NameUsage<T>> nameUsages = null;
	switch(matchingMode) {
	case EXACT:
	    nameUsages = getExactNameUsages(query, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
	    break;
	case CONTAINS:
	    nameUsages = getContainerNameUsages(query, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
	    break;
	case FUZZY:
	    nameUsages = getFuzzyNameUsages(query, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
	    break;
	case FULL_TEXT:
	    nameUsages = getFullTextNameUsages(query, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
	    break;
	case SUGGEST:
	    nameUsages = getSuggestedNameUsages(query, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
	    break;
	default:
	    break;
	}
	return getNameUsagesPostProcess(nameUsages, filter, matchingMode, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    protected abstract Collection<NameUsage<T>> getExactNameUsages(String query, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale) throws IOException;

    protected Collection<NameUsage<T>> getContainerNameUsages(String query, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
	throws IOException
    {
	return getExactNameUsages(query, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    protected Collection<NameUsage<T>> getFuzzyNameUsages(String query, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
	throws IOException
    {
	return getExactNameUsages(query, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    protected Collection<NameUsage<T>> getFullTextNameUsages(String query, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
	throws IOException
    {
	return getExactNameUsages(query, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    protected Collection<NameUsage<T>> getSuggestedNameUsages(String query, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
	throws IOException
    {
	return getExactNameUsages(query, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    protected Collection<NameUsage<T>> getNameUsagesPostProcess(Collection<NameUsage<T>> nameUsages, Collection<String> filter, MatchingMode matchingMode, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
	throws IOException
    {
	if (filter == null || filter.isEmpty() || nameUsages == null || nameUsages.isEmpty())
	    return nameUsages;
	Collection<NameUsage<T>> result = new ArrayList<NameUsage<T>>();
	for (NameUsage<T> nameUsage : nameUsages) {
	    String literal = nameUsage.getLiteral();
	    for (String key : filter) {
		if (literal.contains(key)) {
		    result.add(nameUsage);
		    break;
		}
	    }
	}
	return result;
    }

    /**
     * Returns true if {@code lower} is equals to or lower than {@code higher},
     * or either of parameters are unspecified.
     *
     * @param lower {@code Rank} to be examined
     * @param higher {@code Rank} to be examined
     * @retrurn true if {@code lower} is equal to or lower than {@code higher}
     */
     public static boolean isEqualLowerOrUnspecified(Rank lower, Rank higher)
    {
	return higher == null
	    || lower == null
	    || higher.equals(Rank.UNRANKED)
	    || lower.equals(Rank.UNRANKED)
	    || !(lower.isHigher(higher));
    }


    /**
     * Returns true if {@code lower} is lower than {@code higher}
     * or either of parameters are unspecified.
     *
     * @param lower {@code Rank} to be examined
     * @param higher {@code Rank} to be examined
     * @retrurn true if {@code lower} is lower than {@code higher}
     */
     public static boolean isLowerOrUnspecified(Rank lower, Rank higher)
    {
	return higher == null
	    || lower == null
	    || higher.equals(Rank.UNRANKED)
	    || lower.equals(Rank.UNRANKED)
	    || lower.isLower(higher);
    }

    /**
     * Returns true if {@code higher} is equals to or higher than {@code lower},
     * or either of parameters are unspecified.
     *
     * @param higher {@code Rank} to be examined
     * @param lower {@code Rank} to be examined
     * @retrurn true if {@code higher} is equal to or higher than {@code lower}
     */
    public static boolean isEqualsHigherOrUnspecified(Rank higher, Rank lower)
    {
	return lower == null
	    || higher == null
	    || lower.equals(Rank.UNRANKED)
	    || higher.equals(Rank.UNRANKED)
	    || !(higher.isLower(lower));
    }

    /**
     * Returns true if {@code higher} is higher than {@code lower}
     * or either of parameters are unspecified.
     *
     * @param higher {@code Rank} to be examined
     * @param lower {@code Rank} to be examined
     * @retrurn true if {@code higher} is higher than {@code lower}
     */
    public static boolean isHigherOrUnspecified(Rank higher, Rank lower)
    {
	return lower == null
	    || higher == null
	    || lower.equals(Rank.UNRANKED)
	    || higher.equals(Rank.UNRANKED)
	    || higher.isHigher(lower);
    }

    /**
     * Resolve higher {@code NameUsage}  just above the  {@code nameUsage}.
     * It should be to provied depending on each data source type.
     *
     * @param nameUsage of interest
     */

    protected abstract void resolveHigherNameUsages(NameUsage<T> nameUsage) throws IOException;


    public Collection<NameUsage<T>> getHigher(NameUsage<T> nameUsage)
	throws IOException
    {
	return getHigher(nameUsage, null, 1);
    }

    public Collection<NameUsage<T>> getHigher(NameUsage<T> nameUsage, Rank rank, int height)
	throws IOException
    {
	if (nameUsage == null)
	    return null;

	NameUsage<T> usage = nameUsage.getNameUsage();
	if(usage != nameUsage) {
	    return getHigher(usage, rank, height);
	}

	NameUsage<T> current = nameUsage;
	Rank currentRank = current.getRank();

	if(nameUsage != null 
	   && isLowerOrUnspecified(currentRank, rank)
	   && height != 0
	   ) {
	    resolveHigherNameUsages(current);
	    NameUsage<T> higher = current.getHigherNameUsage();
	    while(higher != null
		  && isLowerOrUnspecified(currentRank, rank)
		  && (height < 0 || height > 0)
		  ) {
		current.setHigherNameUsage(higher);
		current = higher;
		currentRank = current.getRank();
		resolveHigherNameUsages(current);
		higher = current.getHigherNameUsage();
		if(height > 0)
		    height--;
	    }
	}

	Collection<NameUsage<T>> result = new ArrayList<>(1);
	result.add(current);

	return result;
    }

    /**
     * Resolve lower {@code NameUsage}s  just under the  {@code nameUsage}.
     * It should be to provied depending on each data source type.
     *
     * @param nameUsage of interest
     */
    protected abstract void resolveLowerNameUsages(NameUsage<T> nameUsage) throws IOException;

    // FIXME to aviod <?> 
    public List<NameUsage<T>> getLowerNameUsages(NameUsage<T> nameUsage, Rank rank, int depth)
	throws IOException
    {
	if(nameUsage == null)
	    return null;

	Rank currentRank = nameUsage.getRank();
        if(depth == 0 || (rank != null && ((!rank.equals(Rank.UNRANKED) && rank.equals(currentRank)) || rank.isHigher(currentRank))))
	    //return new ArrayList<N>();
	    return new ArrayList<NameUsage<T>>();
	    
	resolveLowerNameUsages(nameUsage);

	// It is unclear that whey casting is necessary.
	List<NameUsage<T>> lowersUsages = nameUsage.getLowerNameUsages();
	List<NameUsage<T>> lowers = null;
	if(lowersUsages != null && !lowersUsages.isEmpty()) {
	    lowers = new ArrayList<NameUsage<T>>(lowersUsages.size());
	    for (NameUsage<T> lowerUsage : lowersUsages) {
		NameUsage<T> lower = lowerUsage;
		lowers.add(lower);
		Rank lowerRank = lower.getRank();
		if(isHigherOrUnspecified(lowerRank, rank) || lowerRank.equals(rank))
		    getLowerNameUsages(lower, rank, depth<=0? depth:depth-1);
	    }
	}

	return lowers;
    }

    /**
     * Gathers {@code NameUsage<T>} from given {@code objects}.
     *
     * @param objects {@code Collection} containing {@code NamedObject}s.
     * @return gathered {@code Collection} of {@code NameUsage<T>}s.
     */
    protected List<NameUsage<T>> getNameUsages(Collection<? extends NamedObject<?>> objects)
	throws IOException
    {
	if(objects == null)
	    return null;
	List<NameUsage<T>> nameUsages = new ArrayList<>(objects.size());
	for (NamedObject<?> object : objects) {
	    if(object instanceof NameUsage) {
		@SuppressWarnings("unchecked")
		    NameUsage<T> usage = (NameUsage<T>)object;
		nameUsages.add(usage);
	    }
	}
	return nameUsages;
    }

    public Collection<NameUsage<T>> getHierarchies(String query)
	throws IOException
    {
	return getHierarchies(getNameUsages(getObjects(query)));
    }

    protected Collection<NameUsage<T>> getHierarchies(Collection<? extends NameUsage<T>> nameUsages)
	throws IOException
    {
	List<NameUsage<T>> hierarchies = Collections.synchronizedList(new ArrayList<NameUsage<T>>());
	for (NameUsage<T> nameUsage: nameUsages) {
	    hierarchies.addAll(getHierarchies(nameUsage));
	}
	return hierarchies;
    }

    public Collection<NameUsage<T>> getHierarchies(NameUsage<T> nameUsage)
	throws IOException
    {
	return getPartialHierarchies(nameUsage);
    }

    @Override
	public Collection<NameUsage<?>> integrateHierarchies(Collection<? extends NameUsage<?>> nameUsages)
	throws IOException
    {
	List<NameUsage<?>> hierarchies = new ArrayList<>();
	for (NameUsage<?> nameUsage: nameUsages) {
	    if(!hierarchies.contains(nameUsage))
		hierarchies.add(nameUsage);
	}
	return hierarchies;
    }

    public Collection<NameUsage<T>> getPartialHierarchies(Collection<? extends NameUsage<T>> nameUsages)
	throws IOException
    {
	List<NameUsage<T>> hierarchies = Collections.synchronizedList(new ArrayList<NameUsage<T>>());
	for (NameUsage<T> nameUsage: nameUsages) {
	    hierarchies.addAll(getPartialHierarchies(nameUsage));
	}
	return hierarchies;
    }

    public Collection<NameUsage<T>> getPartialHierarchies(NameUsage<T> nameUsage)
	throws IOException
    {
	return getPartialHierarchies(nameUsage, 
				     null,
				     getDefaultHeight(),
				     null, 
				     getDefaultDepth());
    }

    public Collection<NameUsage<T>> getPartialHierarchies(NameUsageQueryParameter<T> parameter)
	throws IOException
    {
	NameUsage<T> nameUsage = parameter.getNameUsageFilter();
	Rank higher = parameter.getHigher();
	int height = parameter.getHeight();
	Rank lower = parameter.getLower();
	int depth =parameter.getDepth();
	getLowerNameUsages(nameUsage, lower, depth);
	return CollectionUtility.unique(getHigher(nameUsage, higher, height), new ArrayList<NameUsage<T>>());
    }

    public Collection<NameUsage<T>> getPartialHierarchies(NameUsage<T> nameUsage, Rank higher, int height, Rank lower, int depth)
	throws IOException
    {
	getLowerNameUsages(nameUsage, lower, depth);
	return getHigher(nameUsage, higher, height);
    }

    public Collection<NameUsage<T>> getPartialHierarchies(String query)
	throws IOException
    {
	Collection<NameUsage<T>> usages = getNameUsages(getObjects(query));
	if(usages == null)
	    return null;
	List<NameUsage<T>> hierarchies = Collections.synchronizedList(new ArrayList<NameUsage<T>>(usages.size()));
	for (NameUsage<T> usage: usages) {
	    Collection<NameUsage<T>> roots = getHierarchies(usage);
	    if(roots != null && ! roots.isEmpty()){
		for (NameUsage<T> root : roots) {
		    if(root != null)
			hierarchies.add(root);
		}
	    }
	}
	/*
	if(hierarchies.isEmpty())
	    return null;
	*/
	return hierarchies;
    }

    public Collection<NameUsage<T>> getRoots(NameUsage<T> nameUsage)
	throws IOException
    {
	return getPartialHierarchies(nameUsage, null, FULL_HEIGHT, null, 0);
    }

    public Collection<NameUsage<T>> getRoots(String query)
	throws IOException
    {
	return getRoots(getNameUsages(getObjects(query)));
    }

    public Collection<NameUsage<T>> getRoots(Collection<? extends NameUsage<T>> nameUsages)
	throws IOException
    {
	List<NameUsage<T>> roots = Collections.synchronizedList(new ArrayList<NameUsage<T>>());
	for (NameUsage<T> nameUsage: nameUsages) {
	    roots.addAll(getRoots(nameUsage));
	}
	return roots;
    }

    @Override
	public Collection<NameUsage<T>> getDescendentNames(NameUsage<T> nameUsage)
	throws IOException
    {
	if(nameUsage == null)
	    return null;

	List<NameUsage<T>> nameUsages = new ArrayList<NameUsage<T>>();

	Collection<NameUsage<T>> descendents = getLowerNameUsages(nameUsage, null, FULL_DEPTH);

	if(descendents == null)
	    return nameUsages;

	Set<String> names = new HashSet<String>();
	for (NameUsage<T> descendent : descendents) {
	    names.add(descendent.getLiteral());
	}
	descendents.clear();
	descendents = null;

	for(String name : names) {
	    Collection<NameUsage<T>> result = getExactNameUsages(name, null, false, false, false, null);
	    if(result != null) {
		nameUsages.addAll(result);
		result.clear();
		result = null;
	    }
	}
	names.clear();
	names = null;
	return nameUsages;
    }

    public Collection<NameUsage<T>> getDescendentNames(String query)
	throws IOException
    {
	if(query == null || query.length() == 0)
	    return null;

	List<NameUsage<T>> nameUsages = new ArrayList<NameUsage<T>>();

	Collection<NameUsage<T>> seeds = getExactNameUsages(query, null, false, false, false, null);
	if(seeds == null)
	    return nameUsages;

	Set<NameUsage<T>> nameUsageSet = new HashSet<NameUsage<T>>();

	for (NameUsage<T> seed : seeds) {
	    Collection<NameUsage<T>> result = getDescendentNames(seed);

	    if(result != null) {
		nameUsageSet.addAll(result);
		result.clear();
		result = null;
	    }
	}
	seeds.clear();
	seeds = null;

	nameUsages.addAll(nameUsageSet);
	nameUsageSet.clear();
	nameUsageSet = null;

	return nameUsages;
    }

    public Collection<NameUsage<T>> getRelevantNameUsages(String query)
	throws IOException
    {
	if(query == null || query.length() == 0)
	    return null;
	
	List<NameUsage<T>> nameUsages = new ArrayList<NameUsage<T>>();
	
	Collection<NameUsage<T>> seeds = getExactNameUsages(query, null, false, false, false, null);
	if(seeds == null)
	    return nameUsages;
	
	Set<NameUsage<T>> nameUsageSet = new HashSet<NameUsage<T>>();
	
	for (NameUsage<T> seed : seeds) {
	    Collection<NameUsage<T>> result = getRelevantNameUsages(seed);
	    
	    if(result != null) {
		nameUsageSet.addAll(result);
		result.clear();
		result = null;
	    }
	}
	seeds.clear();
	seeds = null;
	
	nameUsages.addAll(nameUsageSet);
	nameUsageSet.clear();
	nameUsageSet = null;
	
	return nameUsages;
    }
    
    /*
    public void addQueryResultListener(QueryResultListener listener)
    {
	if(adaptor == null)
	    adaptor = new QueryResultListenerAdaptor();
	adaptor.addQueryResultListener(listener);
    }

    public void removeQueryResultListener(QueryResultListener listener)
    {
	if(adaptor != null)
	    adaptor.removeQueryResultListener(listener);
    }

    //protected void fireQueryResultEvent(QueryResultEvent event)
    public void fireQueryResultEvent(QueryResultEvent event)
    {
	if(adaptor != null)
	    adaptor.fireQueryResultEvent(event);
    }
    */

    public void setDefaultDepth(int depth) { }

    public void setDefaultHeight(int height) { }

    public int getDefaultDepth()
    {
	return FULL_DEPTH;
    }

    public int getDefaultHeight()
    {
	return FULL_HEIGHT;
    }
}

