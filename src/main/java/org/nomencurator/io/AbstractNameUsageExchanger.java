/*
 * AbstractDataExchanger.java
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

/**
 * {@code AbstractNameUsageExchanger} provides an abstract implementation of
 * {@code NameUsageExchanger}.
 *
 * @version 	29 June. 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractNameUsageExchanger<N extends NameUsage<?, ?>, T extends N>
    extends AbstractObjectExchanger<N, T>
    implements NameUsageExchanger<N, T>
{
    public Collection<T> getObjects(QueryParameter<N, T> queryParameter)
    {
	if(queryParameter == null)
	    return null;

	if(!(queryParameter instanceof NameUsageQueryParameter) || queryParameter.getQueryMode() == QueryMode.OBJECTS)
	    return getObjects(queryParameter.getLocalKey(), queryParameter.getMatchingMode());

	NameUsageQueryParameter<N, T> parameter =  (NameUsageQueryParameter<N, T>) queryParameter;

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
	    results = super.getObjects(parameter);
	    break;
	}
	return results;
    }
    
    // FIXME: reconsider argument type
    @SuppressWarnings("unchecked")
    protected List<T> getObjects(Collection<NameUsage<?, ?>> nameUsages) {
	if(nameUsages == null)
	    return null;
	List<T> objects = new ArrayList<T>(nameUsages.size());
       for(NameUsage<?, ?> nameUsage : nameUsages) {
	   objects.add((T)nameUsage);
	}
	return objects;
    }

    public Collection<NameUsage<?, ?>> getNameUsages(String query, Rank rank)
    {
	return getNameUsages(query, rank, getDefaultMatchingMode(), false, false, false, null);
    }

    public Collection<NameUsage<?, ?>> getNameUsages(String query, Rank rank, MatchingMode matchingMode, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	Collection<NameUsage<?, ?>> nameUsages = null;
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
	return getNameUsagesPostProcess(nameUsages, matchingMode, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    protected abstract Collection<NameUsage<?, ?>> getExactNameUsages(String query, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale);

    protected Collection<NameUsage<?, ?>> getContainerNameUsages(String query, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	return getExactNameUsages(query, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    protected Collection<NameUsage<?, ?>> getFuzzyNameUsages(String query, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	return getExactNameUsages(query, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    protected Collection<NameUsage<?, ?>> getFullTextNameUsages(String query, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	return getExactNameUsages(query, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    protected Collection<NameUsage<?,  ?>> getSuggestedNameUsages(String query, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	return getExactNameUsages(query, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    protected Collection<NameUsage<?, ?>> getNameUsagesPostProcess(Collection<NameUsage<?, ?>> nameUsages, MatchingMode matchingMode, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	return nameUsages;
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

    protected abstract void resolveHigherNameUsages(NameUsage<?, ?> nameUsage);

    public Collection<NameUsage<?, ?>> getHigher(NameUsage<?, ?> nameUsage)
    {
	return getHigher(nameUsage, null, 1);
    }

    // FIXME to aviod <?, ?> 
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Collection<NameUsage<?, ?>> getHigher(NameUsage<?, ?> nameUsage, Rank rank, int height)
    {
	NameUsage<?, ?> usage = nameUsage.getNameUsage();
	if(usage != nameUsage) {
	    return getHigher(usage, rank, height);
	}

	if (nameUsage == null)
	    return null;

	NameUsage<?, ?> current = (NameUsage<?, ?>)nameUsage;
	Rank currentRank = current.getRank();

	if(nameUsage != null 
	   && isLowerOrUnspecified(currentRank, rank)
	   && height != 0
	   ) {
	    resolveHigherNameUsages(current);
	    NameUsage/*<?, ?>*/ higher = current.getHigherNameUsage();
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

	Collection<NameUsage<?, ?>> result = new ArrayList<NameUsage<?, ?>>(1);
	result.add((NameUsage<?, ?>)current);

	return result;
    }

    /**
     * Resolve lower {@code NameUsage}s  just under the  {@code nameUsage}.
     * It should be to provied depending on each data source type.
     *
     * @param nameUsage of interest
     */
    protected abstract void resolveLowerNameUsages(NameUsage<?, ?> nameUsage);

    // FIXME to aviod <?, ?> 
    @SuppressWarnings("unchecked")
    public List<NameUsage<?, ?>> getLowerNameUsages(NameUsage<?, ?> nameUsage, Rank rank, int depth)
    {
	if(nameUsage == null)
	    return null;

	Rank currentRank = nameUsage.getRank();
        if(depth == 0 || (rank != null && ((!rank.equals(Rank.UNRANKED) && rank.equals(currentRank)) || rank.isHigher(currentRank))))
	    return new ArrayList<NameUsage<?, ?>>();
	    
	resolveLowerNameUsages(nameUsage);

	List<NameUsage<?, ?>> lowersUsages = ((NameUsage/*<?, ?>*/)nameUsage).getLowerNameUsages();
	List<NameUsage<?, ?>> lowers = null;
	if(lowersUsages != null && !lowersUsages.isEmpty()) {
	    lowers = new ArrayList<NameUsage<?, ?>>(lowersUsages.size());
	    for (NameUsage<?, ?> lowerUsage : lowersUsages) {
		NameUsage<?, ?> lower = (NameUsage<?, ?>)lowerUsage;
		lowers.add(lower);
		Rank lowerRank = lower.getRank();
		//if(rank == null || lowerRank == null || rank.equals(Rank.UNRANKED) || lowerRank.equals(Rank.UNRANKED) || lowerRank.equals(rank) || lowerRank.isLower(rank))
		//if(isLowerOrUnspecified(rank, lowerRank) || lowerRank.equals(rank))
		if(isHigherOrUnspecified(lowerRank, rank) || lowerRank.equals(rank))
		    getLowerNameUsages(lower, rank, depth<=0? depth:depth-1);
	    }
	}

	return lowers;
    }


    protected List<NameUsage<?, ?>> getNameUsages(Collection<? extends NamedObject<?, ?>> objects) {
	if(objects == null)
	    return null;
	List<NameUsage<?, ?>> nameUsages = new ArrayList<NameUsage<?, ?>>(objects.size());
	for (NamedObject<?,  ?> object : objects) {
	    if(object instanceof NameUsage) {
		nameUsages.add((NameUsage<?, ?>)object);
	    }
	}
	return nameUsages;
    }

    public Collection<NameUsage<?, ?>> getHierarchies(String query)
    {
	return getHierarchies(getNameUsages(getObjects(query)));
    }

    protected Collection<NameUsage<?, ?>> getHierarchies(Collection<? extends NameUsage<?, ?>> nameUsages)
    {
	List<NameUsage<?, ?>> hierarchies = Collections.synchronizedList(new ArrayList<NameUsage<?, ?>>());
	for (NameUsage<?, ?> nameUsage: nameUsages) {
	    hierarchies.addAll(getHierarchies(nameUsage));
	}
	return hierarchies;
    }

    public Collection<NameUsage<?, ?>> getHierarchies(NameUsage<?, ?> nameUsage)
    {
	return getPartialHierarchies(nameUsage);
    }

    @Override
    public Collection<NameUsage<?, ?>> integrateHierarchies(Collection<? extends NameUsage<?, ?>> nameUsages)
    {
	//List<NameUsage<?, ?>> hierarchies = Collections.synchronizedList(new ArrayList<NameUsage<?, ?>>());
	List<NameUsage<?, ?>> hierarchies = Collections.synchronizedList(new ArrayList<NameUsage<?, ?>>());
	//for (NameUsage<?, ?> nameUsage: nameUsages) {
	for (NameUsage<?, ?> nameUsage: nameUsages) {
	    if(!hierarchies.contains(nameUsage))
		hierarchies.add(nameUsage);
	}
	return hierarchies;
    }

    public Collection<NameUsage<?, ?>> getPartialHierarchies(Collection<? extends NameUsage<?, ?>> nameUsages)
    {
	List<NameUsage<?, ?>> hierarchies = Collections.synchronizedList(new ArrayList<NameUsage<?, ?>>());
	for (NameUsage<?, ?> nameUsage: nameUsages) {
	    hierarchies.addAll(getPartialHierarchies(nameUsage));
	}
	//return integrateHierarchies(hierarchies);
	return hierarchies;
    }

    public Collection<NameUsage<?, ?>> getPartialHierarchies(NameUsage<?, ?> nameUsage)
    {
	return getPartialHierarchies(nameUsage, 
				     null,
				     getDefaultHeight(),
				     null, 
				     getDefaultDepth());
    }

    public Collection<NameUsage<?, ?>> getPartialHierarchies(NameUsageQueryParameter<?, ?> parameter)
    {
	NameUsage<?, ?> nameUsage = parameter.getNameUsageFilter();
	getLowerNameUsages(nameUsage, parameter.getLower(), parameter.getDepth());
	return getHigher(nameUsage, parameter.getHigher(), parameter.getHeight());
    }

    public Collection<NameUsage<?, ?>> getPartialHierarchies(NameUsage<?, ?> nameUsage, Rank higher, int height, Rank lower, int depth)
    {
	getLowerNameUsages(nameUsage, lower, depth);
	return getHigher(nameUsage, higher, height);
    }

    public Collection<NameUsage<?, ?>> getPartialHierarchies(String query)
    {
	Collection<NameUsage<?, ?>> usages = getNameUsages(getObjects(query));
	if(usages == null)
	    return null;
	List<NameUsage<?, ?>> hierarchies = Collections.synchronizedList(new ArrayList<NameUsage<?, ?>>(usages.size()));
	for (NameUsage<?, ?> usage: usages) {
	    Collection<NameUsage<?, ?>> roots = getHierarchies(usage);
	    if(roots != null && ! roots.isEmpty()){
		for (NameUsage<?, ?> root : roots) {
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

    public Collection<NameUsage<?, ?>> getRoots(NameUsage<?, ?> nameUsage)
    {
	return getPartialHierarchies(nameUsage, null, FULL_HEIGHT, null, 0);
    }

    public Collection<NameUsage<?, ?>> getRoots(String query)
    {
	return getRoots(getNameUsages(getObjects(query)));
    }

    public Collection<NameUsage<?, ?>> getRoots(Collection<? extends NameUsage<?, ?>> nameUsages)
    {
	List<NameUsage<?, ?>> roots = Collections.synchronizedList(new ArrayList<NameUsage<?, ?>>());
	for (NameUsage<?, ?> nameUsage: nameUsages) {
	    roots.addAll(getRoots(nameUsage));
	}
	return roots;
    }

    @Override
    public Collection<NameUsage<?, ?>> getDescendentNames(NameUsage<?, ?> nameUsage) {
	if(nameUsage == null)
	    return null;

	List<NameUsage<?, ?>> nameUsages = new ArrayList<NameUsage<?, ?>>();

	Collection<NameUsage<?, ?>> descendents = getLowerNameUsages(nameUsage, null, FULL_DEPTH);

	if(descendents == null)
	    return nameUsages;

	Set<String> names = new HashSet<String>();
	for (NameUsage<?, ?> descendent : descendents) {
	    names.add(descendent.getLiteral());
	}
	descendents.clear();
	descendents = null;

	for(String name : names) {
	    Collection<NameUsage<?, ?>> result = getExactNameUsages(name, null, false, false, false, null);
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

    public Collection<NameUsage<?, ?>> getDescendentNames(String query) {
	if(query == null || query.length() == 0)
	    return null;

	List<NameUsage<?, ?>> nameUsages = new ArrayList<NameUsage<?, ?>>();

	Collection<NameUsage<?, ?>> seeds = getExactNameUsages(query, null, false, false, false, null);
	if(seeds == null)
	    return nameUsages;

	Set<NameUsage<?, ?>> nameUsageSet = new HashSet<NameUsage<?, ?>>();

	for (NameUsage<?, ?> seed : seeds) {
	    Collection<NameUsage<?, ?>> result = getDescendentNames(seed);

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

    public Collection<NameUsage<?, ?>> getRelevantNameUsages(String query) {
	if(query == null || query.length() == 0)
	    return null;
	
	List<NameUsage<?, ?>> nameUsages = new ArrayList<NameUsage<?, ?>>();
	
	Collection<NameUsage<?, ?>> seeds = getExactNameUsages(query, null, false, false, false, null);
	if(seeds == null)
	    return nameUsages;
	
	Set<NameUsage<?, ?>> nameUsageSet = new HashSet<NameUsage<?, ?>>();
	
	for (NameUsage<?, ?> seed : seeds) {
	    Collection<NameUsage<?, ?>> result = getRelevantNameUsages(seed);
	    
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

