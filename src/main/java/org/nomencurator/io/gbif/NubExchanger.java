/*
 * NubExchanger.java:  a GBIF CheklistBank NameUsage exchagner with cache
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

package org.nomencurator.io.gbif;

import org.gbif.api.model.checklistbank.VernacularName;
import org.gbif.api.model.checklistbank.NameUsageMatch;

import org.gbif.api.model.checklistbank.search.NameUsageSearchRequest;
import org.gbif.api.model.checklistbank.search.NameUsageSearchResult;
import org.gbif.api.model.checklistbank.search.NameUsageSuggestResult;

import org.gbif.api.model.registry.Dataset;

import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.Language;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

//import org.gbif.api.vocabulary.Rank;

import org.nomencurator.io.AbstractNameUsageExchanger;
import org.nomencurator.io.MatchingMode;
import org.nomencurator.io.MatchingMode.*;
import org.nomencurator.io.QueryMode;
import org.nomencurator.io.QueryMode.*;
import org.nomencurator.io.QueryParameter;

import org.nomencurator.model.AbstractNameUsage;
import org.nomencurator.model.Annotation;
import org.nomencurator.model.Appearance;
import org.nomencurator.model.Author;
import org.nomencurator.model.Name;
import org.nomencurator.model.NamedObject;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Publication;
import org.nomencurator.model.Rank;

import org.nomencurator.model.gbif.NubNameUsage;
import org.nomencurator.model.gbif.NubNameUsageMatchQuery;
import org.nomencurator.model.gbif.NubNameUsageMatchScore;
import org.nomencurator.model.gbif.VernacularNameUsage;
import org.nomencurator.model.gbif.RankMap;

import org.nomencurator.api.gbif.SpeciesAPIClient;
import org.nomencurator.api.gbif.DatasetAPIClient;

import org.nomencurator.api.gbif.model.checklistbank.ParsedName;


import org.nomencurator.util.Locales;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.Getter;

/**
 * {@code NubExchanger} provides a GBIF CheklistBank NameUsage exchagner with cache.
 *
 * @version	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NubExchanger
    extends AbstractNameUsageExchanger<NubNameUsage, NubNameUsage> 
{
    @Getter
    private SpeciesAPIClient dataSource;
    
    @Getter
    private DatasetAPIClient datasetSource;
    
    protected static Map<Integer, org.gbif.api.model.checklistbank.NameUsage> scientificNameUsages;

    protected static  Map<Integer, NubNameUsage> nubNameUsages;
    protected Map<Integer, VernacularName> vernacularNames;
    protected static Map<Integer, VernacularNameUsage> vernacularNameUsages;
    protected static Map<NubNameUsageMatchQuery, Map<Integer, NubNameUsageMatchScore>> queryScores;
    protected static Map<Integer, Map<NubNameUsageMatchQuery, NubNameUsageMatchScore>> scores;

    protected Map<Integer, NubNameUsage> vernacularNameNubs;
    protected Map<String, Set<NubNameUsage>> nubNameNubs;

    protected static Map<UUID, Dataset> datasets;

    static {
	nubNameUsages = Collections.synchronizedMap(new HashMap<Integer, NubNameUsage>());
	vernacularNameUsages = Collections.synchronizedMap(new HashMap<Integer, VernacularNameUsage>());
	datasets = Collections.synchronizedMap(new HashMap<UUID, Dataset>());
    }

    public NubExchanger() {
	this(new SpeciesAPIClient(), new DatasetAPIClient());
    };

    public NubExchanger(SpeciesAPIClient dataSource, DatasetAPIClient datasetSource) {
	this.dataSource = dataSource;
	this.datasetSource = datasetSource;
    };

    //public Collection<NamedObject<NubNameUsage, NubNameUsage>> getObjects(String query, MatchingMode matchingMode)
    public Collection<NubNameUsage> getObjects(String query, MatchingMode matchingMode)
    {
	String[] localKeys = splitQuery(query);
	if(localKeys == null)
	    return null;

	/*
	Collection<NamedObject<NubNameUsage, NubNameUsage>> results =
	    new ArrayList<NamedObject<NubNameUsage, NubNameUsage>>();
	*/
	Collection<NubNameUsage> results = new ArrayList<NubNameUsage>();

	for(String localKey : localKeys) {
	    Integer usageKey = null;
	    if(localKey != null && localKey.startsWith(NubNameUsage.getPidPrefix()))
		usageKey = Integer.valueOf(localKey.substring(NubNameUsage.getPidPrefix().length()));
	    else if(localKey != null) {
		try {
		    usageKey = Integer.valueOf(localKey);
		}
		catch (NumberFormatException e) {
		}
	    }
	    if (usageKey != null) {
		NubNameUsage result = getObject(usageKey);
		if(result != null)
		    results.add(result);
	    }
	}
	return results;
    }

    public NubNameUsage getObject(Integer usageKey)
    {
	if(usageKey == null)
	    return null;

	if(nubNameUsages == null)
	    nubNameUsages = Collections.synchronizedMap(new HashMap<Integer, NubNameUsage>());

	NubNameUsage nameUsage = null;
	org.gbif.api.model.checklistbank.NameUsage scientificNameUsage = null;
	int key = usageKey.intValue();
	synchronized(nubNameUsages) {
	    nameUsage = nubNameUsages.get(usageKey);
	}
	if(nameUsage == null) {
	    nameUsage = getObject(dataSource.get(key));
	    /*
	    scientificNameUsage = dataSource.get(key);
	    if(scientificNameUsage != null) {
		nameUsage = new NubNameUsage(scientificNameUsage);
		if(nameUsage != null) {
		    UUID datasetKey = scientificNameUsage.getDatasetKey();
		    if(datasetKey != null) {
			nameUsage.setDataset(getDataset(datasetKey));
		    }
		    synchronized(nubNameUsages) {
			NubNameUsage reconfirm = nubNameUsages.get(usageKey);
			if(reconfirm == null) {
			    nubNameUsages.put(usageKey, nameUsage);
			}
			else {
			    nameUsage = reconfirm;
			}
		    }
		}
	    }
	    */
	}

	/*
	if(scientificNameUsage != null) {
	    List<VernacularNameUsage> toAdd = resolveVernacularNameUsages(key);

	    if(!toAdd.isEmpty()) {
		Annotation annotation = new Annotation();
		annotation.setLinkType("vernacular");
		annotation.addAnnotator(nameUsage);
		annotation.setAnnotatants(toAdd);
	    }

	    toAdd.clear();
	}
	*/

	return nameUsage;
    }

    protected Dataset getDataset(UUID datasetKey)
    {
	if(datasetKey == null)
	    return null;

	Dataset dataset = null;
	synchronized(datasets) {
	    dataset = datasets.get(datasetKey);
	}

	if(dataset == null) {
	    dataset = datasetSource.get(datasetKey);
	    if(dataset != null) {
		synchronized(datasets) {
		    Dataset reconfirm = datasets.get(datasetKey);
		    if(reconfirm != null) {
			dataset = reconfirm;
		    }
		    datasets.put(datasetKey, dataset);
		}
	    }
	}

	return dataset;
    }

    protected Collection<NubNameUsage> getNewLiterals(List<org.gbif.api.model.checklistbank.NameUsage> nameUsages, Set<Integer> nubKeys, Set<String> knowns, Set<String> newLiterals)
    {
	if (nameUsages == null)
	    return null;
	Collection<NubNameUsage> results = new ArrayList<NubNameUsage>();
	for (org.gbif.api.model.checklistbank.NameUsage nameUsage : nameUsages) {
	    int nubKey = nameUsage.getKey();
	    NubNameUsage nubUsage = getObject(nubKey);
	    if (!nubKeys.contains(nubKey)) {
		nubKeys.add(nubKey);
		String literal = nubUsage.getLiteral();
		if (!knowns.contains(literal))
		    newLiterals.add(literal);
		results.add(nubUsage);
	    }
	}
	return results;

    }

    protected Collection<NubNameUsage> getSynonyms(org.gbif.api.model.checklistbank.NameUsage nub, Locale locale, Set<Integer> nubKeys, Set<String> knowns, Set<String> newLiterals)
    {
	if (nub == null)
	    return null;
	Collection<NubNameUsage> results = new ArrayList<NubNameUsage>();

	int acceptedKey = nub.getKey();
	if (nub.isSynonym()) {
	    acceptedKey = nub.getAcceptedKey();
	    NubNameUsage nameUsage = getObject(acceptedKey);
	    if (! nubKeys.contains(acceptedKey)) {
		nubKeys.add(acceptedKey);
		String literal = nameUsage.getLiteral();
		if (!knowns.contains(literal)) {
		    newLiterals.add(literal);
		}
		results.add(nameUsage);
	    }
	}
	results.addAll(getNewLiterals(dataSource.listSynonyms(acceptedKey, locale), nubKeys, knowns, newLiterals));

	return results;
    }


    @Override protected Collection<NameUsage<?, ?>> getExactNameUsages(String query, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	String[] queries = splitQuery(query);
	Set<String> literals = new HashSet<String>();
	for (String literal : queries) {
	    /*
	    String parsed = getParsedCanonicalName(literal);
	    if(parsed != null && parsed.length() > 0)
		literal = parsed;
	    */
	    literals.add(literal);
	}

	return getExactNameUsages(new HashSet<String>(), literals, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    protected Collection<NameUsage<?, ?>> getExactNameUsages(Set<String> knowns, Set<String> literals, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	if (literals == null)
	    return null;

	List<NameUsage<?, ?>> nameUsagesOfRank = new ArrayList<NameUsage<?, ?>>();

	if (literals.isEmpty())
	    return nameUsagesOfRank;

	locale = Locale.ROOT.equals(locale)? null : locale;

	Set<Integer> nubKeys = new HashSet<Integer>();
	Set<Integer> visitedKeys = new HashSet<Integer>();
	Set<String> newLiterals = new HashSet<String>();

	int newCommers = 0;

	Set<String> canonicalNames = new HashSet<String>();
	for (String literal : literals) {
	    canonicalNames.addAll(getParsedCanonicalNames(literal, locale));
	}

	for (String canonicalName : canonicalNames) {
	    if (canonicalName == null || canonicalName.length() == 0)
		continue;

	    List<org.gbif.api.model.checklistbank.NameUsage> result = dataSource.listByCanonicalName(canonicalName, null, (UUID[])null);
	    knowns.add(canonicalName);

	    for(org.gbif.api.model.checklistbank.NameUsage nub: result) {
		NubNameUsage nameUsage = getObject(nub);
		Rank nubRank = nameUsage.getRank();
		int nubKey = nub.getKey();
		int acceptedKey = nubKey;
		if((rank == null || nubRank == null || rank.equals(nubRank))
		   && (!nubKeys.contains(nubKey))) {
			nameUsagesOfRank.add(nameUsage);
			nubKeys.add(nubKey);
		}
		if (includeBasionyms) {
		    Collection<NubNameUsage> basionyms =  getNewLiterals(dataSource.listCombinations(nubKey, locale), nubKeys, knowns, newLiterals);
		    for (NameUsage<?, ?> basionym : basionyms) {
			Rank basionymRank = basionym.getRank();
			if (rank == null || basionymRank == null || rank.equals(basionymRank)) {
			    nameUsagesOfRank.add(basionym);
			    if (includeSynonyms) {
			    }
			    if (includeVernaculars) {
			    }
			}
		    }
		    basionyms.clear();
		}
		if (includeSynonyms) {
		    Collection<NubNameUsage> synonyms =  getSynonyms(nub, locale, nubKeys, knowns, newLiterals);
		    for (NameUsage<?, ?> synonym:  synonyms) {
			Rank synonymRank = synonym.getRank();
			if (rank == null || synonymRank == null || rank.equals(synonymRank)) {
			    nameUsagesOfRank.add(synonym);
			}
			if (includeVernaculars) {
			}
		    }
		}
		
		if (includeVernaculars) {
		    /*
		      List<VernacularNameUsage> vernaculars = resolveVernacularNameUsages(nubKey);
		    */
		}
	    }
	    result.clear();
	}

	if (!newLiterals.isEmpty()) {
	    Collection<NameUsage<?, ?>> synonyms =
		getExactNameUsages(knowns, newLiterals, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
	    if (synonyms != null && !synonyms.isEmpty()) {
		for (NameUsage<?, ?> synonym : synonyms) {
		    Integer nubKey = Integer.valueOf(synonym.getLocalKey());
		    if (!nubKeys.contains(nubKey)) {
			nubKeys.add(nubKey);
			nameUsagesOfRank.add(synonym);
		    }
		}
	    }
	}

	return nameUsagesOfRank;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Collection<NameUsage<?, ?>> getContainerNameUsages(String query, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	String[] literals = splitQuery(query);
	if (literals == null)
	    return null;

	List<NameUsage<?, ?>> nameUsages = new ArrayList<NameUsage<?, ?>>();
	for(String literal : literals) {
	    Collection<NameUsage<?, ?>> seeds = getExactNameUsages(literal, rank, includeBasionyms, includeSynonyms, includeVernaculars, locale);
	    if(seeds != null) {
		for(NameUsage nub :seeds) {
		    Integer parentKey = ((NubNameUsage)nub).getParentKey();
		    if(parentKey != null) {
			NubNameUsage parent = getObject(parentKey);
			if(parent != null) {
			    nub.setHigherNameUsage(parent);
			    if(!nameUsages.contains(parent)) {
				nameUsages.add(parent);
			    }
			}
		    }
		}
		seeds.clear();
	    }
	}

	return nameUsages;
    }


    protected Collection<NameUsage<?, ?>> getFuzzyNameUsages(String query, Rank rank, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	return getFuzzyNameUsages(query, rank, false, true, null, null);
    }

    protected Collection<NameUsage<?, ?>> getFuzzyNameUsages(String query, Rank rank, boolean strict, boolean verbose, Rank rankScope, String nameScope)
    {
	String[] literals = splitQuery(query);
	if (literals == null)
	    return null;

	org.gbif.api.model.checklistbank.NameUsage scope = null;
	if(rankScope != null && nameScope != null) {
	    scope = new org.gbif.api.model.checklistbank.NameUsage();
	    scope.setCanonicalName(nameScope);
	    scope.setScientificName(nameScope);
	    scope.setRank(RankMap.get(rankScope));
	}
	List<NameUsage<?, ?>> nameUsages = new ArrayList<NameUsage<?, ?>>();
	for(String literal : literals) {
	    NubNameUsageMatchQuery nubQuery = new NubNameUsageMatchQuery(literal, rank == null? null: RankMap.get(rank), scope, strict, verbose);
	    NameUsageMatch matchResult = dataSource.match(literal, RankMap.get(rank), scope, strict, verbose);

	    if(matchResult != null) {
		NubNameUsage nameUsage = cache(nubQuery, matchResult);
		if(nameUsage != null)
		    nameUsages.add(nameUsage);
		List<NameUsageMatch> alternatives = matchResult.getAlternatives();
		if(alternatives != null) {
		    for(NameUsageMatch alternative : alternatives) {
			nameUsage = cache(nubQuery, alternative);
			if(nameUsage != null)
			    nameUsages.add(nameUsage);
		    }
		    alternatives.clear();
		}
	    }
	}

	return nameUsages;
    }

    protected NubNameUsage cache(NubNameUsageMatchQuery query, NameUsageMatch matchResult)
    {
	if(query == null || matchResult == null)
	    return null;
	NubNameUsage nameUsage = null;
	Integer key = matchResult.getUsageKey();

	// the key could be null if the match result gives multiple equal match
	if(key != null) {
	    nameUsage = getObject(key);
	    if(nameUsage != null) {
		NubNameUsageMatchScore theScore = new NubNameUsageMatchScore(nameUsage, query, matchResult);
		key = Integer.valueOf(nameUsage.getLocalKey());
		
		if(queryScores == null) {
		    queryScores = Collections.synchronizedMap(new HashMap<NubNameUsageMatchQuery, Map<Integer, NubNameUsageMatchScore>>());
		}
		
		synchronized(queryScores) {
		    Map<Integer, NubNameUsageMatchScore> queryScore = queryScores.get(query);
		    if(queryScore == null) {
			queryScore = Collections.synchronizedMap(new HashMap<Integer, NubNameUsageMatchScore>());
			queryScores.put(query, queryScore);
		    }
		    queryScore.put(key, theScore);
		}
		
		
		if(scores == null) {
		    scores = Collections.synchronizedMap(new HashMap<Integer, Map<NubNameUsageMatchQuery, NubNameUsageMatchScore>>());
		}
		
		synchronized(scores) {
		    Map<NubNameUsageMatchQuery, NubNameUsageMatchScore> score = scores.get(key);
		    if(score == null) {
			score = Collections.synchronizedMap(new HashMap<NubNameUsageMatchQuery, NubNameUsageMatchScore>());
			scores.put(key, score);
		    }
		    score.put(query, theScore);
		}
	    }
	}

	return nameUsage;
    }

    protected Collection<NameUsage<?, ?>> getFullTextNameUsages(String query, Rank rank, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	String[] literals = splitQuery(query);
	if (literals == null)
	    return null;

	List<NameUsage<?, ?>> nameUsages = null;
	for (String literal : literals) {
	    List<NameUsageSearchResult> results = dataSource.fullTextSearch(literal, RankMap.get(rank), 
									    null, null, null, null, null, null, null, null, null, null, null);
	    if(results != null) {
		nameUsages = new ArrayList<NameUsage<?, ?>>(results.size());
		for (NameUsageSearchResult result : results) {
		    NubNameUsage nameUsage = getObject(result.getKey());
		    if(nameUsage == null) {
			nameUsage = new NubNameUsage(result);
			nubNameUsages.put(result.getKey(), nameUsage);
		    }
		    //else
		    nameUsage.setNameUsageSearchResult(result);
		    nameUsages.add(nameUsage);
		}
		results.clear();
	    }
	}
	return nameUsages;
    }

    protected Collection<NameUsage<?, ?>> getSuggestedNameUsages(String query, Rank rank, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	String[] literals = splitQuery(query);
	if (literals == null)
	    return null;

	List<NameUsage<?, ?>> nameUsages = null;
	for (String literal : literals) {
	    List<NameUsageSuggestResult> results = dataSource.suggest(literal, RankMap.get(rank), (UUID[])null);
	    if(results != null) {
		nameUsages = new ArrayList<NameUsage<?, ?>>(results.size());
		for (NameUsageSuggestResult result : results) {
		    NubNameUsage nameUsage = getObject(result.getKey());
		    if(nameUsage == null) {
			nameUsage = new NubNameUsage(result);
			nubNameUsages.put(result.getKey(), nameUsage);
		    }
		    nameUsage.setNameUsageSuggestResult(result);
		    nameUsages.add(nameUsage);
		}
		results.clear();
	    }
	}
	return nameUsages;
    }

    // FIXME to use e.g. <E extends NameUsage<?, ?>> instead of NameUsage<?, ?>
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void resolveLowerNameUsages(NameUsage<?, ?> nameUsage)
    {
	String key = nameUsage.getLocalKey();
	if(key == null || key.length() == 0) {
	    return;
	}
	List<org.gbif.api.model.checklistbank.NameUsage> children = dataSource.listChildren(Integer.valueOf(key).intValue(), (Locale[])null);

	NameUsage childUsage = null;

	if(children != null) {
	    for(org.gbif.api.model.checklistbank.NameUsage child: children) {
		childUsage = getObject(child);
		if(childUsage != null) {
		    nameUsage.addLowerNameUsage(childUsage);
		    List<VernacularNameUsage> virnacularNameUsages = resolveVernacularNameUsages(child.getKey().intValue());
		    if(virnacularNameUsages != null) {
			Annotation annotation = new Annotation();
			annotation.setLinkType("vernacular");
			annotation.addAnnotator(childUsage);
			annotation.setAnnotatants(virnacularNameUsages);
		    }
		}
	    }
	}
    }

    protected void resolveHigherNameUsages(NameUsage<?, ?> nameUsage)
    {
	// unnecessary because higher path is embedded
    }

    // FIXME to use e.g. <E extends NameUsage<?, ?>> instead of NameUsage<?, ?>
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Collection<NameUsage<?, ?>> getHigher(NameUsage<?, ?> nameUsage, Rank rank, int height)
    {
	if(nameUsage == null) {
	    return null;
	}

	NameUsage usage = nameUsage.getNameUsage();
	if(usage != nameUsage) {
	    return getHigher(usage, rank, height);
	}

	List<org.gbif.api.model.checklistbank.NameUsage> highers = 
	    dataSource.listParents(((NubNameUsage)usage).getScientificNameUsage().getKey(), (Locale[])null);

	if(highers == null)
	    return null;

	height = (height < 0) ? 0 : highers.size() - height;

	Rank currentRank = nameUsage.getRank();

	for (int i = highers.size(); (i > 0 && i > height  && isLowerOrUnspecified(currentRank, rank)); ) {
	    NameUsage<?, ?> higher = (NameUsage<?, ?>)getObject(highers.get(--i));
	    if(higher != null ) {
		currentRank = higher.getRank();
		if (isEqualLowerOrUnspecified(currentRank, rank)) {
		    usage.setHigherNameUsage(higher);
		    usage = higher;
		}
	    }
	}

	highers.clear();

	List<NameUsage<?, ?>> results =  new ArrayList<NameUsage<?, ?>>(1);
	results.add((NameUsage<?, ?>)usage);

	return results;
    }


    protected NubNameUsage getObject(org.gbif.api.model.checklistbank.NameUsage scientificNameUsage)
    {
	NubNameUsage nameUsage = null;
	if(scientificNameUsage != null) {
	    Integer key = scientificNameUsage.getKey();
	    synchronized(nubNameUsages) {
		nameUsage = nubNameUsages.get(key);
	    }
	    if(nameUsage == null) {
		nameUsage = new NubNameUsage(scientificNameUsage);
		if(nameUsage != null) {
		    UUID datasetUUID = scientificNameUsage.getDatasetKey();
		    if(datasetUUID != null) {
			nameUsage.setDataset(getDataset(datasetUUID));
		    }
		    synchronized(nubNameUsages) {
			NubNameUsage reconfirm = nubNameUsages.get(key);
			if(reconfirm == null) {
			    nubNameUsages.put(key, nameUsage);
			}
			else {
			    nameUsage = reconfirm;
			}
		    }
		}
	    }

	    List<VernacularNameUsage> toAdd = resolveVernacularNameUsages(key);

	    if(!toAdd.isEmpty()) {
		Annotation annotation = new Annotation();
		annotation.setLinkType("vernacular");
		annotation.addAnnotator(nameUsage);
		annotation.setAnnotatants(toAdd);
	    }

	    toAdd.clear();
	}

	return nameUsage;
    }

    protected List<VernacularNameUsage> resolveVernacularNameUsages(int key)
    {
	List<VernacularNameUsage> result = new ArrayList<VernacularNameUsage>();
	if(vernacularNames != null && !vernacularNames.isEmpty())  {
	    List<VernacularName> vernacularNames = dataSource.getVernacularNames(key);
	    for(VernacularName vn: vernacularNames) {
		Integer sourceTaxonKey = vn.getSourceTaxonKey();
		if(sourceTaxonKey != null && sourceTaxonKey.equals(key)) {
		    VernacularNameUsage vnu = null;
		    synchronized(vernacularNameUsages) {
			Integer hashCode = Integer.valueOf(vn.hashCode());
			vnu = vernacularNameUsages.get(hashCode);
			if(vnu == null) {
			    vnu = new VernacularNameUsage(vn);
			    vernacularNameUsages.put(hashCode, vnu);
			}
		    }
		    result.add(vnu);
		}
	    }
	    vernacularNames.clear();
	}
	return result;
    }

    protected Collection<String> getParsedCanonicalNames(String name, Locale locale)
    {
	List<ParsedName> parsed = dataSource.parse(name);
	if (parsed == null || parsed.size() == 0 || (parsed.size() == 1 && parsed.get(0).getCanonicalName().length() == 0)) {
	    if (parsed != null) 
		parsed = new ArrayList<ParsedName>();
	    
	    name = name.toLowerCase();
	    NameUsageSearchRequest searchRequest = new NameUsageSearchRequest();
	    searchRequest.setQ(name);
	    List<NameUsageSearchResult> results = dataSource.fullTextSearch(searchRequest);

	    if (results != null && results.size() > 0) {
		String language = (locale == null) ? null : locale.getLanguage();
		Set<String> canonicalNames = new HashSet<String>();
		for (NameUsageSearchResult result : results) {
		    List<VernacularName> vernacularNames = result.getVernacularNames();
		    if (vernacularNames == null || vernacularNames.size() == 0)
			continue;
		    Iterator<VernacularName> iterator = vernacularNames.iterator();
		    NameUsageSearchResult vernacular = null;
		    while (vernacular == null && iterator.hasNext()) {
			VernacularName toTest = iterator.next();
			String vernacularName = toTest.getVernacularName();
			if (vernacularName != null
			    && vernacularName.toLowerCase().indexOf(name) != -1
			    && (locale == null || Locale.ROOT.equals(locale) || toTest.getLanguage().getLocale().getLanguage().equals(language)))
			    vernacular = result;
		    }
		    if (vernacular != null) {
			String canonicalName = result.getCanonicalName();
			if (canonicalName != null && canonicalName.length() > 0)
			    canonicalNames.add(canonicalName);
		    }
		}
		if (!canonicalNames.isEmpty()) {
		    for (String canonicalName : canonicalNames) {
			parsed.addAll(dataSource.parse(canonicalName));
		    }
		}
	    }
	}
	
	List<String> cannonicalNames = new ArrayList<String>(parsed.size());
	parsed.forEach(parsedName ->  cannonicalNames.add(parsedName.getCanonicalName()));
	parsed.clear();
	
	return cannonicalNames;
    }

    protected String getParsedCanonicalName(String name)
    {
	List<ParsedName> parsed = dataSource.parse(name);
	String parsedName = null;
	if(parsed.size() == 1) {
	    parsedName = parsed.get(0).getCanonicalName();
	}
	else {
	    Set<String> parsedNames = new HashSet<String>(parsed.size());
	    for(ParsedName p : parsed) {
		parsedNames.add(p.getCanonicalName());
	    }
	    if(parsedNames.size() == 1) {
		Iterator<String> iterator = parsedNames.iterator();
		parsedName = iterator.next();
	    }
	    parsedNames.clear();
	}
	parsed.clear();

	return parsedName;
    }

    protected Collection<NameUsage<?, ?>> getNameUsagesPostProcess(Collection<NameUsage<?, ?>> nameUsages, int queryType)
    {
	return nameUsages;
    }

    protected Integer getNubKey(NubNameUsage nameUsage) {
	if(nameUsage == null)
	    return null;

	Integer key = null;

	org.gbif.api.model.checklistbank.NameUsage scientificNameUsage = nameUsage.getScientificNameUsage();
	if(scientificNameUsage != null) {
	    key = scientificNameUsage.getAcceptedKey();
	    if(key != null) {
		NubNameUsage accepted = getObject(key);
		if(accepted != null) {
		    key = getNubKey(accepted);
		} else {
		    key = null;
		}
	    }
	    if(key == null) {
		key = scientificNameUsage.getNubKey();
	    }
 	}

	if(key == null) {
	    NameUsageSearchResult nameUsageSearchResult = nameUsage.getNameUsageSearchResult();
	    if(nameUsageSearchResult != null) {
		key = nameUsageSearchResult.getAcceptedKey();
		if(key != null) {
		    NubNameUsage accepted = getObject(key);
		    if(accepted != null) {
			key = getNubKey(accepted);
		    } else {
			key = null;
		    }
		}
		if(key == null) {
		    key = nameUsageSearchResult.getNubKey();
		}
	    }
	}

	if(key == null) {
	    NameUsageSuggestResult nameUsageSuggestResult = nameUsage.getNameUsageSuggestResult();
	    if(nameUsageSuggestResult != null) {
		key = nameUsageSuggestResult.getNubKey();
	    }
	}

	return key;
    }
	

    public Collection<NameUsage<?, ?>> getRelevantNameUsages(NameUsage<?, ?> nameUsage) {
	if(nameUsage == null || !(nameUsage instanceof NubNameUsage))
	    return null;

	Integer key = getNubKey((NubNameUsage)nameUsage);

	if(key == null) {
	    String keyString = nameUsage.getLocalKey();
	    if(keyString == null || keyString.length() == 0) {
		return null;
	    }
	    key = Integer.valueOf(keyString);
	}

	if(key == null)
	    return null;

	List<org.gbif.api.model.checklistbank.NameUsage> results = dataSource.listRelated(key.intValue(), (Locale)null);
	if(results == null) 
	    return  null;
	List<NameUsage<?, ?>> nameUsages = null;
	if(results.size() == 0) {
	    nameUsages = Collections.emptyList();
	} else {
	    nameUsages = new ArrayList<NameUsage<?, ?>>(results.size());
	    for(org.gbif.api.model.checklistbank.NameUsage result: results) {
		NubNameUsage nub = getObject(result);
		if(nub != null)
		    nameUsages.add(nub);
	    }
	results.clear();
	}
	return nameUsages;
    }

    //public Collection<NameUsage> integrateHierarchies(Collection<? extends NameUsage> nameUsages)
    @Override
    public Collection<NameUsage<?, ?>> integrateHierarchies(Collection<? extends NameUsage<?, ?>> nameUsages)
    {
	Collection<NameUsage<?, ?>> hierarchies = super.integrateHierarchies(nameUsages);
	List<NameUsage<?, ?>> integrated = new ArrayList<NameUsage<?, ?>>();
	// Map<UUID, List<NameUsage>> datasets =  new HashMap<UUID, List<NameUsage>>();
	Set<String> localKeys = new HashSet<String>();
	for(NameUsage<?, ?> nameUsage: hierarchies) {
	    NubNameUsage nub = null;
	    if(nameUsage instanceof NubNameUsage)
		nub = (NubNameUsage)nameUsage;
	    if(nub == null && nameUsage instanceof NameUsage) {
		Object entity = nameUsage.getEntity();
		if(entity instanceof NubNameUsage)
		    nub = (NubNameUsage)entity;
	    }
	    if(nub == null) {
		integrated.add(nameUsage);
	    }
	    else {
		UUID datasetKey = nub.getDatasetKey();
		String localKey = nub.getLocalKey();
		String viewName = nub.getViewName();
		if(!localKeys.contains(localKey)) {
		    localKeys.add(localKey);
		    integrated.add(nameUsage);
		}
	    }
	}
	return hierarchies;
    }

    protected void clear(Iterator<?> target)
    {
	if (target != null) {
	    while (target.hasNext()) {
		Object object = target.next();
		if (object instanceof Collection) {
		    clear((Collection)object);
		}
		else if (object instanceof Map) {
		    clear((Map)object);
		}
	    }
	}
    }

    protected void clear(Map<?, ?> target)
    {
	if (target != null) {
	    clear(target.values().iterator());
	    target.clear();
	}
    }

    protected void clear(Collection<?> target)
    {
	if (target != null) {
	    clear(target.iterator());
	    target.clear();
	}
    }

    @Override
    public void clear()
    {
	clear(scientificNameUsages);
	clear(vernacularNames);
	clear(vernacularNameUsages);
	clear(queryScores);
	clear(scores);
	clear(vernacularNameNubs);
	clear(nubNameNubs);
	clear(datasets);
    }
}
