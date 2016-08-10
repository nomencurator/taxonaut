/*
 * NubExchangerTest.java:  testcases of NubExchanger
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

import com.google.common.collect.Lists;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import java.text.DateFormat;

import org.gbif.api.model.checklistbank.NameUsageMatch;

import org.gbif.api.model.checklistbank.search.NameUsageSearchResult;
import org.gbif.api.model.checklistbank.search.NameUsageSuggestResult;

import org.gbif.api.vocabulary.NameType;
import org.gbif.api.vocabulary.Origin;
import org.gbif.api.vocabulary.TaxonomicStatus;

import org.nomencurator.api.gbif.SpeciesAPIClient;
import org.nomencurator.api.gbif.DatasetAPIClient;

import org.nomencurator.io.QueryMode;
import org.nomencurator.io.MatchingMode;
import org.nomencurator.io.NameUsageQueryParameter;


import org.nomencurator.io.gbif.NubExchanger;

import org.nomencurator.model.NamedObject;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import org.nomencurator.model.gbif.RankMap;

import org.nomencurator.model.gbif.NubNameUsage;


/**
 * Unit test for NubExchangerTest
 *
 * @version 	18 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class NubExchangerTest
{
    protected boolean debug = false;

    public void printCollections(String functionName, List<?> actual, List<?> expected) {
	if(!debug)
	    return ;

	int actualSize = actual.size();
	int expectedSize = expected.size();
	System.out.println("a/e = " + actualSize + "/" + expectedSize);
	int maxSize = actualSize > expectedSize ? actualSize : expectedSize;
	for(int i = 0; i < maxSize; i++) {
	    System.out.println(functionName + "(): [" + i  + "]");
	    if(i < expectedSize && i < actualSize) {
		String expectedString = expected.get(i).toString();
		int expectedHash = expected.get(i).hashCode();
		String actualString = actual.get(i).toString();
		int actualHash = actual.get(i).hashCode();
		//if(!expectedString.equals(actualString)) {
		if(expectedHash != actualHash ||
		   !(expected.equals(actual))) {
		    System.out.println("expected[" + i + "]");
		    System.out.println(expectedHash);
		    System.out.println(expectedString);
		    System.out.println("actual[" + i + "]");
		    System.out.println(actualHash);
		    System.out.println(actualString);
		}
	    }
	    else if(i < expectedSize) {
		System.out.println("expected[" + i + "]");
		System.out.println(expected.get(i).toString());
	    }
	    else if(i < actualSize) {
		System.out.println("actual[" + i + "]");
		System.out.println(actual.get(i).toString());
	    }
	}
    }

    public void printNubNameUsages(String functionName, List<? extends NameUsage> actual, List<? extends NameUsage> expected) {
	if(!debug)
	    return ;

	int actualSize = actual.size();
	int expectedSize = expected.size();
	System.out.println("a/e = " + actualSize + "/" + expectedSize);
	int maxSize = actualSize > expectedSize ? actualSize : expectedSize;
	for(int i = 0; i < maxSize; i++) {
	    System.out.println(functionName + "(): [" + i  + "]");
	    if(i < expectedSize && i < actualSize) {
		String expectedString = expected.get(i).toString();
		int expectedHash = expected.get(i).hashCode();
		String actualString = actual.get(i).toString();
		int actualHash = actual.get(i).hashCode();
		//if(!expectedString.equals(actualString)) {
		if(expectedHash != actualHash ||
		   !(expected.equals(actual))) {
		    System.out.println("expected[" + i + "]");
		    System.out.println(expectedHash);
		    System.out.println(((NubNameUsage)expected.get(i)).getScientificNameUsage());
		    System.out.println(((NubNameUsage)expected.get(i)).getNameUsageSearchResult());
		    System.out.println(((NubNameUsage)expected.get(i)).getDataSource());
		    System.out.println("actual[" + i + "]");
		    System.out.println(actualHash);
		    System.out.println(((NubNameUsage)actual.get(i)).getScientificNameUsage());
		    System.out.println(((NubNameUsage)actual.get(i)).getNameUsageSearchResult());
		    System.out.println(((NubNameUsage)actual.get(i)).getDataSource());
		}
	    }
	    else if(i < expectedSize) {
		System.out.println("expected[" + i + "]");
		System.out.println(((NubNameUsage)expected.get(i)).getScientificNameUsage());
	    }
	    else if(i < actualSize) {
		System.out.println("actual[" + i + "]");
		System.out.println(((NubNameUsage)actual.get(i)).getScientificNameUsage());
	    }
	}
    }

    @Test
    public void construct() {
	NubExchanger exchanger = new NubExchanger();
    }

    private NubNameUsage createNubNameUsage5231190() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	Locale locale = null;
	return new NubNameUsage(sut.get(5231190, locale));
    }

    @Test
    public void getObject() {
	NubExchanger exchanger = new NubExchanger();

	NubNameUsage expected = createNubNameUsage5231190();

	NubNameUsage actual = exchanger.getObject(5231190);

	assertThat(actual, is(expected));
    }

    @Test
    public void getObjects() {
	NubExchanger exchanger = new NubExchanger();

	NubNameUsage expected = createNubNameUsage5231190();

	NubNameUsage actual = null;
	Iterator<NubNameUsage> results = exchanger.getObjects("5231190", MatchingMode.EXACT).iterator();
	if(results.hasNext()) {
	    actual = results.next();
	}

	assertThat(actual, is(expected));
    }

    @Test
    public void test_getNameUsage_String_wo_Rank_EXACT() {
	NubExchanger exchanger = new NubExchanger();

	SpeciesAPIClient sut = new SpeciesAPIClient();
	List<org.gbif.api.model.checklistbank.NameUsage> results = sut.listByCanonicalName("Puma concolor", null, null);

	ArrayList<NameUsage> expected = new ArrayList<>(results.size());

	for(org.gbif.api.model.checklistbank.NameUsage result : results) {
	    expected.add(new NubNameUsage(result));
	}

	Collection<NameUsage<NubNameUsage>> actualResults = exchanger.getNameUsages("Puma concolor", null, MatchingMode.EXACT, false, false, false, null);

	ArrayList<NameUsage> actual = new ArrayList<>(actualResults.size());
	for(NameUsage<NubNameUsage> result : actualResults) {
	    actual.add(result);
	}

	// printCollections("getExactNameUsage_String_wo_Rank_EXCACT", actual, expected);

	assertThat(actual, is(expected));
    }


    @Test
    public void test_getNameUsage_String_wo_Rank_EXACT_via_Param() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	List<org.gbif.api.model.checklistbank.NameUsage> results = sut.listByCanonicalName("Puma concolor", null, null);

	ArrayList<NameUsage> expected = new ArrayList<>(results.size());

	for(org.gbif.api.model.checklistbank.NameUsage result : results) {
	    expected.add(new NubNameUsage(result));
	}

	NubExchanger exchanger = new NubExchanger();
	NameUsageQueryParameter parameter = new NameUsageQueryParameter<NubNameUsage>("Puma concolor", null, MatchingMode.EXACT);
	//NameUsageQueryParameter parameter = new NameUsageQueryParameter<NubNameUsage>("Puma concolor", null, 0, 0, null, null, Boolean.FALSE, MatchingMode.EXACT, QueryMode.NAMEUSAGES);
	parameter.setLiteral("Puma concolor");
	Collection<NamedObject<NubNameUsage>> actualResults = exchanger.getObjects(parameter);

	ArrayList<NameUsage> actual = new ArrayList<>(actualResults.size());
	for(NamedObject<NubNameUsage> result : actualResults) {
	    actual.add((NameUsage)result);
	}

	// printCollections("getExactNameUsage_String_wo_Rank_EXCACT", actual, expected);

	assertThat(actual, is(expected));
    }

    @Test
    public void test_getNameUsage_String_wo_Rank_FUZZY() {
	NubExchanger exchanger = new NubExchanger();

	SpeciesAPIClient sut = new SpeciesAPIClient();
	org.gbif.api.model.checklistbank.NameUsageMatch matchResult = sut.match("Puma concolor", null, null, false, true);
	org.gbif.api.model.checklistbank.NameUsage rawNub = sut.get(matchResult.getUsageKey());
	
	ArrayList<NameUsage> expected = new ArrayList<>();
	if(rawNub != null)
	    expected.add(new NubNameUsage(rawNub));
	if(matchResult != null) {
	    List<NameUsageMatch> alternatives = matchResult.getAlternatives();
	    int i = 0;
	    Locale locale = null;
	    for(NameUsageMatch match : alternatives) {
		rawNub = sut.get(match.getUsageKey(), locale);
		if(rawNub != null) {
		    expected.add(new NubNameUsage(rawNub));
		}
	    }
	}

	Collection<NameUsage<NubNameUsage>> actualResults = exchanger.getNameUsages("Puma concolor", null, MatchingMode.FUZZY, false, false, false, null);

	ArrayList<NameUsage> actual = new ArrayList<>(actualResults.size());
	for(NameUsage<NubNameUsage> result : actualResults) {
	    actual.add(result);
	}
	
	// printCollections("getExactNameUsage_String_wo_Rank_FUZZY", actual, expected);

	assertThat(actual, is(expected));
    }

    @Test
    public void test_getNameUsage_String_Rank_FULL_TEXT() {
	NubExchanger exchanger = new NubExchanger();

	SpeciesAPIClient sut = new SpeciesAPIClient();
	DatasetAPIClient datasetSource = new DatasetAPIClient();

	List<NameUsageSearchResult> results =
	    sut.fullTextSearch("Puma", RankMap.get(Rank.GENUS), 
			       null, null, null, null, null, null, null, null, null, null, null);

	ArrayList<NameUsage> expected = new ArrayList<>();
	ArrayList<org.gbif.api.model.checklistbank.NameUsage> expectedRaw = new ArrayList<>();

	if(results != null) {
	    for (NameUsageSearchResult result : results) {
 		org.gbif.api.model.checklistbank.NameUsage rawNub = sut.get(result.getKey());
		if(rawNub != null) {
		    NubNameUsage nub = new NubNameUsage(rawNub);
		    nub.setNameUsageSearchResult(result); 
		    UUID datasetUUID = rawNub.getDatasetKey();
		    if(datasetUUID != null) {
			nub.setDataset(datasetSource.get(datasetUUID));
		    }
		    expected.add(nub);
		    expectedRaw.add(rawNub);
		}
	    }
	}

	Collection<NameUsage<NubNameUsage>> actualResults = exchanger.getNameUsages("Puma", Rank.GENUS, MatchingMode.FULL_TEXT, false, false, false, null);

	ArrayList<NameUsage> actual = new ArrayList<>(actualResults.size());
	ArrayList<org.gbif.api.model.checklistbank.NameUsage> actualRaw = new ArrayList<>();
	List<NameUsageSearchResult> actualSearchResults =new ArrayList<>();
	for(NameUsage<NubNameUsage> result : actualResults) {
	    actual.add(result);
	    actualRaw.add(((NubNameUsage)result).getScientificNameUsage());
	    actualSearchResults.add(((NubNameUsage)result).getNameUsageSearchResult());
	}
	
	//printCollections("test_getNameUsage_String_Rank_FULL_TEXT", actual, expected);
	printNubNameUsages("test_getNameUsage_String_Rank_FULL_TEXT", actual, expected);
	
	assertThat(actual, is(expected));
    }

    @Test
    public void test_getNameUsage_String_wo_Rank_SUGGEST() {
	NubExchanger exchanger = new NubExchanger();

	SpeciesAPIClient sut = new SpeciesAPIClient();

	List<NameUsageSuggestResult> results =sut.suggest("Puma con", null, null);

	ArrayList<NameUsage> expected = new ArrayList<>(results.size());

	if(results != null) {
	    for (NameUsageSuggestResult result : results) {
 		org.gbif.api.model.checklistbank.NameUsage rawNub = sut.get(result.getKey());
		if(rawNub != null) {
		    NubNameUsage nub = new NubNameUsage(rawNub);
		    nub.setNameUsageSuggestResult(result); 
		    expected.add(nub);
		}
	    }
	}

	Collection<NameUsage<NubNameUsage>> actualResults = exchanger.getNameUsages("Puma con", null, MatchingMode.SUGGEST, false, false, false, null);

	ArrayList<NameUsage> actual = new ArrayList<>(actualResults.size());
	for(NameUsage<NubNameUsage> result : actualResults) {
	    actual.add(result);
	}

	//printCollections("test_getNameUsage_String_wo_Rank_SUGGEST", actual, expected);
	printNubNameUsages("test_getNameUsage_String_Rank_SUGGEST", actual, expected);

	assertThat(actual, is(expected));
    }

    @Test
    public void test_getNameUsage_String_wo_Rank_CONTAINS() {
	NubExchanger exchanger = new NubExchanger();
	Collection<NameUsage<NubNameUsage>> actualResults = null;
	actualResults = exchanger.getNameUsages("Lembus", null, MatchingMode.CONTAINS, false, false, false, null);

	assertTrue(true);
    }

    @Test
    public void test_getHigher() {
	NubExchanger exchanger = new NubExchanger();
	Collection<NameUsage<NubNameUsage>> actualResults = 
	    exchanger.getNameUsages("Lembus", null, MatchingMode.EXACT, false, false, false, null);

	for(NameUsage<NubNameUsage> result : actualResults) {
	    Collection<NameUsage<NubNameUsage>> highers = exchanger.getHigher(result, null, -1);
	}

	assertTrue(true);
    }

    /*
    @Test
    public void test_getLowerNameUsages() {
	NubExchanger exchanger = new NubExchanger();
	Collection<NameUsage<NubNameUsage>> actualResults = 
	    //exchanger.getNameUsages("Lembus", null, MatchingMode.EXACT);
	    //exchanger.getNameUsages("Cohnilembidae", null, MatchingMode.EXACT);
	    exchanger.getNameUsages("Hymenostomatida", null, MatchingMode.EXACT);

	for(NameUsage<NubNameUsage> result : actualResults) {
	    // if(924 != ((NubNameUsage)result).getScientificNameUsage().getKey()) continue;
	    long start = System.currentTimeMillis();
	    Collection<NameUsage<NubNameUsage>> children = exchanger.getLowerNameUsages(result, 1);
	    long finish = System.currentTimeMillis();
	    int numDescendants = ((NubNameUsage)result).getLowerNameUsages().size();
	    System.out.println("get " + numDescendants + " lowers in " + (double)(finish - start)/1000.0d + " s");
	}

	assertTrue(true);
    }

    @Test
    public void test_getPartialHierarchies() {
	NubExchanger exchanger = new NubExchanger();
	Collection<NameUsage<NubNameUsage>> actualResults = 
	    //exchanger.getNameUsages("Lembus", null, MatchingMode.EXACT);
	    //exchanger.getNameUsages("Cohnilembidae", null, MatchingMode.EXACT);
	    exchanger.getNameUsages("Hymenostomatida", null, MatchingMode.EXACT);

	for(NameUsage<NubNameUsage> result : actualResults) {
	    // if(924 != ((NubNameUsage)result).getScientificNameUsage().getKey()) continue;
	    int numDescendants = ((NubNameUsage)result).getScientificNameUsage().getNumDescendants();
	    long start = System.currentTimeMillis();
	    Collection<NameUsage<NubNameUsage>> children = exchanger.getPartialHierarchies(result);
	    long finish = System.currentTimeMillis();
	    System.out.println("get " + numDescendants + " partials in " + (double)(finish - start)/1000.0d + " s");
	}

	assertTrue(true);
    }
    */
}
