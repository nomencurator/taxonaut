/*
 * SpeciesAPIClientTest.java:  testcases of SpeciesAPIClient
 *
 * Copyright (c) 2014 Nozomi `James' Ytow
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

package org.nomencurator.api.gbif;

import com.google.common.base.Objects;

import static com.google.common.collect.Lists.newArrayList;
import com.google.common.collect.Multimap;
import com.google.common.collect.ArrayListMultimap;


import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.UUID;

import org.gbif.api.model.checklistbank.Description;
// import org.gbif.api.model.checklistbank.Distribution;
//import org.gbif.api.model.checklistbank.Image;
import org.gbif.api.model.checklistbank.NameUsage;
import org.gbif.api.model.checklistbank.NameUsageMatch;
import org.gbif.api.model.checklistbank.NameUsageMediaObject;
import org.gbif.api.model.checklistbank.NameUsageMetrics;
//import org.gbif.api.model.checklistbank.ParsedName;
import org.gbif.api.model.checklistbank.Reference;
import org.gbif.api.model.checklistbank.SpeciesProfile;
import org.gbif.api.model.checklistbank.TypeSpecimen;
import org.gbif.api.model.checklistbank.VernacularName;
import org.gbif.api.model.checklistbank.VerbatimNameUsage;

import org.gbif.api.model.checklistbank.search.NameUsageSearchParameter;
import org.gbif.api.model.checklistbank.search.NameUsageSearchRequest;
import org.gbif.api.model.checklistbank.search.NameUsageSearchResult;
import org.gbif.api.model.checklistbank.search.NameUsageSuggestRequest;
import org.gbif.api.model.checklistbank.search.NameUsageSuggestResult;

import org.gbif.api.model.common.Identifier;
import org.gbif.api.model.common.LinneanClassification;
import org.gbif.api.model.common.MediaObject;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;

import org.gbif.api.model.common.search.SearchResponse;

import org.gbif.api.vocabulary.EstablishmentMeans;
import org.gbif.api.vocabulary.IdentifierType;
import org.gbif.api.vocabulary.Language;
import org.gbif.api.vocabulary.MediaType;
import org.gbif.api.vocabulary.NameType;
import org.gbif.api.vocabulary.OccurrenceStatus;
import org.gbif.api.vocabulary.Origin;
import org.gbif.api.vocabulary.Rank;
import org.gbif.api.vocabulary.TaxonomicStatus;


import org.nomencurator.api.gbif.SpeciesAPIClient;

import org.nomencurator.api.gbif.model.checklistbank.Distribution;
import org.nomencurator.api.gbif.model.checklistbank.ParsedName;
// import org.nomencurator.api.gbif.model.checklistbank.search.NameUsageSearchResult;


/**
 * Unit test for SpeciesAPIClientTest
 */
public class SpeciesAPIClientTest
{
    int debug = 0;

    public static Date parseDateTime(String dateTime) {
	return parseDateTime(dateTime, Locale.ENGLISH);
    }

    public static Date parseDateTime(String dateTime, Locale locale) {
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"/*, locale*/);
	ParsePosition pos = new ParsePosition(0);
	Date date = null;
	date = dateFormat.parse(dateTime, pos);
	return date;
    }

    protected void printObjects(String functionName, Object actual, Object expected) {
	if(debug == 0)
	    return ;

	System.out.println(functionName + "()");
	System.out.println("expected:");
	System.out.println(expected);
	System.out.println("actual:");
	System.out.println(actual);
    }


    protected void printCollections(String functionName, List<?> actual, List<?> expected) {
	if(debug == 0)
	    return ;

	int actualSize = actual.size();
	int expectedSize = expected.size();
	System.out.println("a/e = " + actualSize + "/" + expectedSize);
	int maxSize = actualSize > expectedSize ? actualSize : expectedSize;
	for(int i = 0; i < maxSize; i++) {
	    System.out.println(functionName + "(): [" + i  + "]");
	    if(i < expectedSize && i < actualSize) {
		String expectedString = expected.get(i).toString();
		String actualString = actual.get(i).toString();
		//if(!expectedString.equals(actualString)) {
		if(!expected.get(i).equals(actual.get(i))) {
		    System.out.println(expected.get(i).getClass() + "/" + actual.get(i).getClass());
		    System.out.println(expected.get(i).hashCode() + "/" + actual.get(i).hashCode());
		    System.out.println("expected[" + i + "]");
		    //System.out.println(expectedString);
		    System.out.println(expected.get(i));
		    System.out.println("actual[" + i + "]");
		    //System.out.println(actualString);
		    System.out.println(actual.get(i));
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

    @Test
    public void getVersion() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String expected = SpeciesAPIClient.CURRENT_VERSION;
	String actual = sut.getVersion();
	assertThat(actual, is(expected));
    }

    @Test
    public void setVersion() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String currentVersion = sut.getVersion();
	String expectedVersion = "v1.0";
	String expectedBaseURL = sut.getBaseURL().replace(currentVersion, expectedVersion);
	String expectedSpeciesURLEpithet = sut.getSpeciesURLEpithet();
	String expectedSpeciesURL = sut.getSpeciesURL().replace(currentVersion, expectedVersion);
	String expectedParserURLEpithet = sut.getParserURLEpithet();
	String expectedParserURL = sut.getParserURL().replace(currentVersion, expectedVersion);
	sut.setVersion(expectedVersion);
	String actual = sut.getVersion();
	assertThat(actual, is(expectedVersion));
	actual = sut.getBaseURL();
	assertThat(actual, is(expectedBaseURL));
	actual = sut.getSpeciesURLEpithet();
	assertThat(actual, is(expectedSpeciesURLEpithet));
	actual = sut.getSpeciesURL();
	assertThat(actual, is(expectedSpeciesURL));
	actual = sut.getParserURLEpithet();
	assertThat(actual, is(expectedParserURLEpithet));
	actual = sut.getParserURL();
	assertThat(actual, is(expectedParserURL));
    }

    @Test
    public void getBaseURL() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String expected = "http://api.gbif.org";
	String actual = sut.getBaseURL();
	assertThat(actual, is(expected));
    }

    @Test
    public void setBaseURL() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String defaultBaseURL = sut.getBaseURL();
	String expectedBaseURL = "http://foo.bar";
	String expectedSpeciesURLEpithet = sut.getSpeciesURLEpithet();
	String expectedSpeciesURL = sut.getSpeciesURL().replace(defaultBaseURL, expectedBaseURL);
	String expectedParserURLEpithet = sut.getParserURLEpithet();
	String expectedParserURL = sut.getParserURL().replace(defaultBaseURL, expectedBaseURL);
	sut.setBaseURL(expectedBaseURL);
	String actual = sut.getBaseURL();
	assertThat(actual, is(expectedBaseURL));
	actual = sut.getSpeciesURLEpithet();
	assertThat(actual, is(expectedSpeciesURLEpithet));
	actual = sut.getSpeciesURL();
	assertThat(actual, is(expectedSpeciesURL));
	actual = sut.getParserURLEpithet();
	assertThat(actual, is(expectedParserURLEpithet));
	actual = sut.getParserURL();
	assertThat(actual, is(expectedParserURL));
    }

    @Test
    public void getSpeciesURL() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String expected = "http://api.gbif.org/v1/species";
	String actual = sut.getSpeciesURL();
	assertThat(actual, is(expected));
    }

    /*
    @Test
    public void setSpeciesURL() {
	setSpeciesURLepithet(getSpeciesURLepithet() );
    }
    */

    @Test
    public void getSpeciesURLEpithet() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String expected = "species";
	String actual = sut.getSpeciesURLEpithet();
	assertThat(actual, is(expected));
    }

    @Test
    public void setSpeciesURLEpithet() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String defaultEpithet = sut.getSpeciesURLEpithet();
	String expectedEpithet = "epithet";
	String expectedSpeciesURL = sut.getSpeciesURL().replace(defaultEpithet, expectedEpithet);
	String expectedParserURLEpithet = sut.getParserURLEpithet();
	String expectedParserURL = sut.getParserURL();
	sut.setSpeciesURLEpithet(expectedEpithet);
	String actual = sut.getSpeciesURLEpithet();
	assertThat(actual, is(expectedEpithet));
	actual = sut.getSpeciesURL();
	assertThat(actual, is(expectedSpeciesURL));
	actual = sut.getParserURLEpithet();
	assertThat(actual, is(expectedParserURLEpithet));
	actual = sut.getParserURL();
	assertThat(actual, is(expectedParserURL));
    }

    @Test
    public void getParserURL() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String expected = "http://api.gbif.org/v1/parser";
	String actual = sut.getParserURL();
	assertThat(actual, is(expected));
    }

    /*
    @Test
    public void setParserURL() {

    }
    */

    @Test
    public void getParserURLEpithet() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String expected = "parser";
	String actual = sut.getParserURLEpithet();
	assertThat(actual, is(expected));
    }

    /*
    @Test
    pbulic void getParserURLepithet() {

    }
     */

    @Test
    public void setParserURLEpithet() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String defaultEpithet = sut.getParserURLEpithet();
	String expectedEpithet = "epithet";
	String expectedParserURL = sut.getParserURL().replace(defaultEpithet, expectedEpithet);
	String expectedSpeciesURLEpithet = sut.getSpeciesURLEpithet();
	String expectedSpeciesURL = sut.getSpeciesURL();
	sut.setParserURLEpithet(expectedEpithet);
	String actual = sut.getParserURLEpithet();
	assertThat(actual, is(expectedEpithet));
	actual = sut.getParserURL();
	assertThat(actual, is(expectedParserURL));
	actual = sut.getSpeciesURLEpithet();
	assertThat(actual, is(expectedSpeciesURLEpithet));
	actual = sut.getSpeciesURL();
	assertThat(actual, is(expectedSpeciesURL));
    }

    @Test
    public void getResourceURL_wo_modifier() {
	int usageKey = 1234;
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String expected = sut.getSpeciesURL() + "/" + usageKey;
	String actual = sut.getResourceURL(usageKey);
	assertThat(actual, is(expected));
    }

    @Test
    public void getResourceURL_w_modifier() {
	int usageKey = 1234;
	String modifier = "name";
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String expected = sut.getSpeciesURL() + "/" + usageKey + "/" + modifier;
	String actual = sut.getResourceURL(usageKey, modifier);
	assertThat(actual, is(expected));
    }


    @Test
    public void getParameter_wo_parameter() {
	String key = "key";
	Collection<String> parameter = null;
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String actual = sut.getParameter(key, parameter);
	assertThat(actual, is(nullValue()));
    }

    @Test
    public void getParameter_w_empty_parameter() {
	String key = "key";
	Collection<String> parameter = new Vector<String>();
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String actual = sut.getParameter(key, parameter);
	assertThat(actual, is(nullValue()));
    }

    @Test
    public void getParameter_w_nonempty_parameter() {
	String key = "key";
	Collection<String> parameter = new Vector<String>();
	parameter.add("4567");
	parameter.add("ABCD");
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String expected="key=4567&key=ABCD";
	String actual = sut.getParameter(key, parameter);
	assertThat(actual, is(expected));
    }

    private NameUsage createNameUsage5231190(Locale locale) {
	NameUsage nameUsage = new NameUsage();
	nameUsage.setKey(5231190);
	nameUsage.setKingdom("Animalia");
	nameUsage.setPhylum("Chordata");
	nameUsage.setClazz("Aves");
	nameUsage.setOrder("Passeriformes");
	nameUsage.setFamily("Passeridae");
	nameUsage.setGenus("Passer");
	nameUsage.setSpecies("Passer domesticus");
	nameUsage.setKingdomKey(1);
	nameUsage.setPhylumKey(44);
	nameUsage.setClassKey(212);
	nameUsage.setOrderKey(729);
	nameUsage.setFamilyKey(5264);
	nameUsage.setGenusKey(2492321);
	nameUsage.setSpeciesKey(5231190);
	nameUsage.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	nameUsage.setNubKey(5231190);
	nameUsage.setParentKey(2492321);
	nameUsage.setParent("Passer");
	nameUsage.setScientificName("Passer domesticus (Linnaeus, 1758)");
	nameUsage.setCanonicalName("Passer domesticus");
	if(locale != null) {
	    if(locale.equals(Locale.ENGLISH))
	       nameUsage.setVernacularName("House Sparrow");
	}
	nameUsage.setAuthorship(" (Linnaeus, 1758)");
	nameUsage.setNameType(NameType.WELLFORMED);
	nameUsage.setRank(Rank.SPECIES);
	nameUsage.setOrigin(Origin.SOURCE);
	nameUsage.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	//nameUsage.setNomenclaturalStatus();
	nameUsage.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	nameUsage.setNumDescendants(12);
	// nameUsage.setSourceId("119127243");
	nameUsage.setTaxonID("119127243");
	/*
	nameUsage.setSynonym(false);
	Identifier identifier = new Identifier();
	identifier.setKey(3191745);
	identifier.setUsageKey(5231190);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("119127243");
	// identifier.setType(IdentifierType.SOURCE_ID);
	List<Identifier> identifiers = newArrayList();
	identifiers.add(identifier);
	nameUsage.setIdentifiers(identifiers);
	*/
	nameUsage.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	nameUsage.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));

	return nameUsage;
    }


    @Test
    public void get_wo_locale() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	NameUsage expected = createNameUsage5231190(null);
	NameUsage actual = sut.get(5231190);

	printObjects("get_wo_locale", actual, expected);

	assertThat(actual, is(expected));
    }

    @Test
    public void get_w_null_locale () {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	NameUsage expected = createNameUsage5231190(null);
	NameUsage actual = sut.get(5231190, null);

	printObjects("get_w_null_locale", actual, expected);

	assertThat(actual, is(expected));
    }

    @Test
    public void get_w_not_null_locale () {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	NameUsage expected = createNameUsage5231190(Locale.ENGLISH);
	NameUsage actual = sut.get(5231190, Locale.ENGLISH);
	/*
	System.out.println("de:" + sut.get(5231190, Locale.GERMAN));
	System.out.println("de:" + sut.get(107881308, Locale.GERMAN));
	System.out.println("en:" + sut.get(107881308, Locale.ENGLISH));
	System.out.println("es:" + sut.get(5231190, new Locale("es")));
	System.out.println("sw:" + sut.get(5231190, new Locale("SWEDISH")));
	System.out.println("ja:" + sut.get(5231190, new Locale("ja")));
	System.out.println("kr:" + sut.get(5231190, new Locale("kr")));
	System.out.println("zh:" + sut.get(5231190, new Locale("zh")));
	*/

	printObjects("get_w_not_null_locale", actual, expected);

	assertThat(actual, is(expected));
    }

    @Test
    public void getParsedName() {
	SpeciesAPIClient sut = new SpeciesAPIClient();
	ParsedName expected = new ParsedName();
	expected.setKey(1864347);
	expected.setScientificName("Passer domesticus (Linnaeus, 1758)");
	expected.setAuthorsParsed(true);
	expected.setCanonicalNameWithMarker("Passer domesticus");
	expected.setCanonicalNameComplete("Passer domesticus (Linnaeus, 1758)");
	expected.setCanonicalName("Passer domesticus");
	expected.setType(NameType.WELLFORMED);
	expected.setBracketAuthorship("Linnaeus");
	expected.setBracketYear("1758");
	expected.setGenusOrAbove("Passer");
	expected.setSpecificEpithet("domesticus");
	ParsedName actual = sut.getParsedName(5231190);
	assertThat(actual, is(expected));
    }

    @Test
    public void getMetrics() {

	NameUsageMetrics expected = new NameUsageMetrics();
	expected.setKey(5231190);
	expected.setNumPhylum(0);
	expected.setNumClass(0);
	expected.setNumOrder(0);
	expected.setNumFamily(0);
	expected.setNumGenus(0);
	expected.setNumSubgenus(0);
	expected.setNumSpecies(1);
	expected.setNumChildren(12);
	expected.setNumDescendants(12);
	expected.setNumSynonyms(0);

	SpeciesAPIClient sut = new SpeciesAPIClient();
	NameUsageMetrics actual = sut.getMetrics(5231190);
	assertThat(actual, is(expected));
    }

    /*
    @Test
    public void getVerbatim() {
	String expect ="";
	SpeciesAPIClient sut = new SpeciesAPIClient();
	NameUsageMetrics result = sut.getVerbatim(5231190);
	String actual = result.toString();
	assertThat(actual, is(expected));
    }
    */

    @Test
    public void list_wo_page() {
	PagingResponse<NameUsage> expected = new PagingResponse<NameUsage>(0,20);
	expected.setEndOfRecords(true);
	List<NameUsage> results = newArrayList();
	expected.setResults(results);

	NameUsage result = new NameUsage();
	results.add(result);
	result.setKey(113490264);
	result.setKingdom("Plantae");
	result.setPhylum("Magnoliophyta");
	result.setClazz("Magnoliopsida");
	result.setOrder("Sapindales");
	result.setFamily("Sapindaceae");
	result.setGenus("Acer");
	result.setSpecies("Acer sterculiaceum");
	result.setKingdomKey(127971322);
	result.setPhylumKey(127971328);
	result.setClassKey(127971374);
	result.setOrderKey(127971477);
	result.setFamilyKey(127971478);
	result.setGenusKey(127971479);
	result.setSpeciesKey(113490264);
	result.setDatasetKey(UUID.fromString("00a0607f-fd7e-4268-9707-0f53aa265f1f"));
	result.setNubKey(7262989);
	result.setParentKey(127971479);
	result.setParent("Acer");
	result.setScientificName("Acer sterculiaceum");
	result.setCanonicalName("Acer sterculiaceum");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	/*
	Identifier identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(12524207);
	identifier.setUsageKey(113490264);
	identifier.setDatasetKey(UUID.fromString("00a0607f-fd7e-4268-9707-0f53aa265f1f"));
	identifier.setIdentifier("ICIMOD_Barsey_Plants_001");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("ICIMOD_Barsey_Plants_001");
	result.setModified(parseDateTime("2013-12-12T13:56:13.204+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	SpeciesAPIClient sut = new SpeciesAPIClient();
	PagingResponse<NameUsage> actual = sut.list(Locale.ENGLISH, UUID.fromString("00a0607f-fd7e-4268-9707-0f53aa265f1f"),"ICIMOD_Barsey_Plants_001",null);

	printCollections("list_wo_page", actual.getResults(), results);

	assertThat(actual, is(expected));
    }

    @Test
    public void listByCanonicalName() {
	PagingResponse<NameUsage> expected = new PagingResponse<NameUsage>(0,20);
	expected.setEndOfRecords(true);
	List<NameUsage> results = newArrayList();
	expected.setResults(results);

	NameUsage result = new NameUsage();
	results.add(result);
	result.setKey(2435099);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(2435099);
	result.setParentKey(2435098);
	result.setParent("Puma");
	result.setScientificName("Puma concolor (Linnaeus, 1771)");
	result.setCanonicalName("Puma concolor");
	result.setVernacularName("Cougar");
	result.setAuthorship(" (Linnaeus, 1771)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	// result.setNomenclaturalStatus(null);
	result.setPublishedIn("Mantissa Plantarum vol. 2 p. 266");
	result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	result.setNumDescendants(6);
	/*
	Identifier identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(1155098);
	identifier.setUsageKey(2435099);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("119806678");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("119806678");
	result.setTaxonID("119806678");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// expected[1]:
	result = new NameUsage();
	results.add(result);
	result.setKey(103371273);
	result.setKingdom("Animalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(134536083);
	result.setOrderKey(103371070);
	result.setFamilyKey(103371072);
	result.setGenusKey(103371272);
	result.setSpeciesKey(103371273);
	result.setDatasetKey(UUID.fromString("672aca30-f1b5-43d3-8a2b-c1606125fa1b"));
	result.setNubKey(2435099);
	result.setParentKey(103371272);
	result.setParent("Puma");
	result.setScientificName("Puma concolor Linnaeus, 1771");
	result.setCanonicalName("Puma concolor");
	result.setVernacularName("Cougar");
	result.setAuthorship("Linnaeus, 1771");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	// result.setNomenclaturalStatus(null);
	result.setRemarks("Reviewed by Currier (1983) as Felis concolor. Placed in Puma by Pocock (1917a), Weigel (1961), Hemmer (1978), and Kratochvíl (1982c). Synonyms allocated according to Culver et al. (2000).");
	result.setPublishedIn("Mantissa Plantarum vol. 2 p. 266");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(5663285);
	identifier.setUsageKey(103371273);
	identifier.setDatasetKey(UUID.fromString("672aca30-f1b5-43d3-8a2b-c1606125fa1b"));
	identifier.setIdentifier("14000204");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("14000204");
	result.setTaxonID("14000204");
	result.setModified(parseDateTime("2014-06-14T21:10:46.488+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// expected[2]:
	result = new NameUsage();
	results.add(result);
	result.setKey(106663137);
	result.setKingdom("Metazoa");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(106148414);
	result.setPhylumKey(106522535);
	result.setClassKey(106223020);
	result.setOrderKey(106151875);
	result.setFamilyKey(106661479);
	result.setGenusKey(105976359);
	result.setSpeciesKey(106663137);
	result.setDatasetKey(UUID.fromString("fab88965-e69d-4491-a04d-e3198b626e52"));
	result.setNubKey(2435099);
	result.setParentKey(105976359);
	result.setParent("Puma");
	result.setScientificName("Puma concolor");
	result.setCanonicalName("Puma concolor");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	// result.setNomenclaturalStatus(null);
	result.setNumDescendants(2);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6409797);
	identifier.setUsageKey(106663137);
	identifier.setDatasetKey(UUID.fromString("fab88965-e69d-4491-a04d-e3198b626e52"));
	identifier.setIdentifier("9696");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("9696");
	result.setTaxonID("9696");
	result.setModified(parseDateTime("2014-06-14T23:00:02.974+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// expected[3]:
	result = new NameUsage();
	results.add(result);
	result.setKey(107363645);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(107264513);
	result.setPhylumKey(107216119);
	result.setClassKey(107239517);
	result.setOrderKey(107240212);
	result.setFamilyKey(107240258);
	result.setGenusKey(107363530);
	result.setSpeciesKey(107363645);
	result.setDatasetKey(UUID.fromString("9ca92552-f23a-41a8-a140-01abaa31c931"));
	// result.setNubKey(2435099);
	result.setParentKey(107363530);
	result.setParent("Puma");
	result.setScientificName("Puma concolor (Linnaeus, 1771)");
	result.setCanonicalName("Puma concolor");
	result.setVernacularName("Cougar");
	result.setAuthorship(" (Linnaeus, 1771)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	// result.setNomenclaturalStatus(null);
	result.setNumDescendants(6);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(7147466);
	identifier.setUsageKey(107363645);
	identifier.setDatasetKey(UUID.fromString("9ca92552-f23a-41a8-a140-01abaa31c931"));
	identifier.setIdentifier("552479");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("552479");
	result.setTaxonID("552479");
	result.setLastInterpreted(parseDateTime("2014-11-25T19:39:17.702+0000"));
	// result.setSynonym(false);

	// expected[4]:
	result = new NameUsage();
	results.add(result);
	result.setKey(107729382);
	result.setKingdom("Metazoa");
	result.setPhylum("Chordata");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(107702209);
	result.setPhylumKey(107661531);
	result.setOrderKey(107658447);
	result.setFamilyKey(107679348);
	result.setGenusKey(107729381);
	result.setSpeciesKey(107729382);
	result.setDatasetKey(UUID.fromString("c33ce2f2-c3cc-43a5-a380-fe4526d63650"));
	result.setNubKey(2435099);
	result.setParentKey(107729381);
	result.setParent("Puma");
	result.setScientificName("Puma concolor");
	result.setCanonicalName("Puma concolor");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	// result.setNomenclaturalStatus(null);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(7513689);
	identifier.setUsageKey(107729382);
	identifier.setDatasetKey(UUID.fromString("c33ce2f2-c3cc-43a5-a380-fe4526d63650"));
	identifier.setIdentifier("51,265");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("51,265");
	result.setTaxonID("51,265");
	result.setModified(parseDateTime("2012-05-24T00:58:42.255+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// expected[5]:
	result = new NameUsage();
	results.add(result);
	result.setKey(110266965);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(124836057);
	result.setPhylumKey(124839309);
	result.setClassKey(124846085);
	result.setOrderKey(124846108);
	result.setFamilyKey(124846134);
	result.setGenusKey(124846146);
	result.setSpeciesKey(110266965);
	result.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	result.setNubKey(2435099);
	result.setParentKey(124846146);
	result.setParent("Puma");
	result.setScientificName("Puma concolor (Linnaeus, 1771)");
	result.setCanonicalName("Puma concolor");
	result.setVernacularName("Cougar");
	result.setAuthorship(" (Linnaeus, 1771)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	// result.setNomenclaturalStatus(null);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(11852991);
	identifier.setUsageKey(110266965);
	identifier.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	identifier.setIdentifier("18868");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("18868");
	result.setTaxonID("18868");
	result.setModified(parseDateTime("2013-03-13T15:45:40.862+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// expected[6]:
	result = new NameUsage();
	results.add(result);
	result.setKey(116851859);
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setGenusKey(137081251);
	result.setSpeciesKey(116851859);
	result.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	result.setNubKey(2435099);
	result.setParentKey(137081251);
	result.setParent("Puma");
	result.setScientificName("Puma concolor (Linnaeus, 1771)");
	result.setCanonicalName("Puma concolor");
	result.setVernacularName("Cougar");
	result.setAuthorship(" (Linnaeus, 1771)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	// result.setNomenclaturalStatus(null);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(13758038);
	identifier.setUsageKey(116851859);
	identifier.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	identifier.setIdentifier("78160");
	// identifier.setType(IdentifierType.SOURCE_ID);

	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(13758037);
	identifier.setUsageKey(116851859);
	identifier.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	identifier.setIdentifier("http://en.wikipedia.org/wiki/Cougar");
	identifier.setType(IdentifierType.URL);
	*/
	// result.setSourceId("78160");
	result.setTaxonID("78160");
	result.setModified(parseDateTime("2014-06-15T21:41:37.315+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);
	try {
	    result.setReferences(new URI("http://en.wikipedia.org/wiki/Cougar"));
	}
	catch (URISyntaxException e) {
	}

	// expected[7]:
	result = new NameUsage();
	results.add(result);
	result.setKey(119806678);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(127795487);
	result.setPhylumKey(127795699);
	result.setClassKey(127795847);
	result.setOrderKey(127796172);
	result.setFamilyKey(127796184);
	result.setGenusKey(127835041);
	result.setSpeciesKey(119806678);
	result.setDatasetKey(UUID.fromString("7ddf754f-d193-4cc9-b351-99906754a03b"));
	result.setConstituentKey(UUID.fromString("29675850-b60f-11e1-bd72-00145eb45e9a"));
	result.setNubKey(2435099);
	result.setParentKey(127835041);
	result.setParent("Puma");
	result.setScientificName("Puma concolor (Linnaeus, 1771)");
	result.setCanonicalName("Puma concolor");
	result.setVernacularName("Cougar");
	result.setAuthorship(" (Linnaeus, 1771)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	// result.setNomenclaturalStatus(null);
	result.setAccordingTo("Wozencraft W. C.");
	result.setNumDescendants(6);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(15981124);
	identifier.setUsageKey(119806678);
	identifier.setDatasetKey(UUID.fromString("7ddf754f-d193-4cc9-b351-99906754a03b"));
	identifier.setIdentifier("6862841");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(15981123);
	identifier.setUsageKey(119806678);
	identifier.setDatasetKey(UUID.fromString("7ddf754f-d193-4cc9-b351-99906754a03b"));
	identifier.setIdentifier("http://www.catalogueoflife.org/annual-checklist/details/species/id/6862841");
	identifier.setType(IdentifierType.URL);
	*/
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(15981122);
	identifier.setUsageKey(119806678);
	identifier.setDatasetKey(UUID.fromString("7ddf754f-d193-4cc9-b351-99906754a03b"));
	identifier.setIdentifier("urn:lsid:catalogueoflife.org:taxon:2cb0cb0d-328e-11e3-ae6e-020044200006:col20131017");
	identifier.setType(IdentifierType.LSID);
	*/
	// result.setSourceId("6862841");
	result.setTaxonID("6862841");
	result.setModified(parseDateTime("2013-12-12T19:03:49.643+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);
	try {
	    result.setReferences(new URI("http://www.catalogueoflife.org/annual-checklist/details/species/id/6862841"));
	}
	catch (URISyntaxException e) {
	}

	// expected[8]:
	result = new NameUsage();
	results.add(result);
	result.setKey(125812287);
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setOrderKey(137012553);
	result.setFamilyKey(137012570);
	result.setGenusKey(125793753);
	result.setSpeciesKey(125812287);
	result.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	result.setNubKey(2435099);
	result.setParentKey(125793753);
	result.setParent("Puma");
	result.setScientificName("Puma concolor (Linnaeus, 1771)");
	result.setCanonicalName("Puma concolor");
	result.setAuthorship(" (Linnaeus, 1771)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	// result.setNomenclaturalStatus(null);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(19726563);
	identifier.setUsageKey(125812287);
	identifier.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	identifier.setIdentifier("52452");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(19726562);
	identifier.setUsageKey(125812287);
	identifier.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	identifier.setIdentifier("http://de.wikipedia.org/wiki/Puma");
	identifier.setType(IdentifierType.URL);
	*/
	// result.setSourceId("52452");
	result.setTaxonID("52452");
	result.setModified(parseDateTime("2014-06-15T16:49:06.178+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);
	try {
	    result.setReferences(new URI("http://de.wikipedia.org/wiki/Puma"));
	}
	catch (URISyntaxException e) {
	}

	// expected[9]:
	result = new NameUsage();
	// results.add(result);
	result.setKey(128268019);
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setGenusKey(134901627);
	result.setSpeciesKey(128268019);
	result.setDatasetKey(UUID.fromString("e632b198-5b2f-47ee-b7a6-6531ea435fa3"));
	result.setNubKey(2435099);
	result.setParentKey(134901627);
	result.setParent("Puma");
	result.setScientificName("Puma concolor");
	result.setCanonicalName("Puma concolor");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	// result.setNomenclaturalStatus(null);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(22489001);
	identifier.setUsageKey(128268019);
	identifier.setDatasetKey(UUID.fromString("e632b198-5b2f-47ee-b7a6-6531ea435fa3"));
	identifier.setIdentifier("311910");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("311910");
	result.setTaxonID("311910");
	// result.setSynonym(false);

	// expected[10]:
	result = new NameUsage();
	results.add(result);
	result.setKey(131497865);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(140575610);
	result.setPhylumKey(140575611);
	result.setClassKey(140575841);
	result.setOrderKey(140575842);
	result.setFamilyKey(140575843);
	result.setGenusKey(140575845);
	result.setDatasetKey(UUID.fromString("4c881adf-82dc-4f31-aea9-20887ce7d7c9"));
	result.setNubKey(2435099);
	result.setParentKey(140575845);
	result.setParent("Puma");
	result.setScientificName("Puma concolor (Linnaeus, 1771)");
	result.setCanonicalName("Puma concolor");
	result.setAuthorship(" (Linnaeus, 1771)");
	result.setNameType(NameType.WELLFORMED);
	result.setOrigin(Origin.SOURCE);
	// result.setNomenclaturalStatus(null);
	result.setRemarks("Especie objeto de conservación");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(25732874);
	identifier.setUsageKey(131497865);
	identifier.setDatasetKey(UUID.fromString("4c881adf-82dc-4f31-aea9-20887ce7d7c9"));
	identifier.setIdentifier("urn:lsid:catalogueoflife.org:taxon:0a6fdfc6-0019-11e3-a4c6-569cdfeac142:col20130815");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("urn:lsid:catalogueoflife.org:taxon:0a6fdfc6-0019-11e3-a4c6-569cdfeac142:col20130815");
	result.setModified(parseDateTime("2014-07-07T15:32:26.215+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// expected[11]:
	result = new NameUsage();
	results.add(result);
	result.setKey(131879553);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(140565407);
	result.setPhylumKey(140565408);
	result.setClassKey(140565791);
	result.setOrderKey(140565795);
	result.setFamilyKey(140565798);
	result.setGenusKey(140565801);
	result.setDatasetKey(UUID.fromString("29886bf9-62d2-4208-87f6-109dd1972607"));
	result.setNubKey(2435099);
	result.setParentKey(140565801);
	result.setParent("Puma");
	result.setScientificName("Puma concolor (Linnaeus, 1771)");
	result.setCanonicalName("Puma concolor");
	result.setAuthorship(" (Linnaeus, 1771)");
	result.setNameType(NameType.WELLFORMED);
	result.setOrigin(Origin.SOURCE);
	// result.setNomenclaturalStatus(null);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(25930703);
	identifier.setUsageKey(131879553);
	identifier.setDatasetKey(UUID.fromString("29886bf9-62d2-4208-87f6-109dd1972607"));
	identifier.setIdentifier("urn:lsid:catalogueoflife.org:taxon:0a6fdfc6-0019-11e3-a4c6-569cdfeac142:col20130815");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("urn:lsid:catalogueoflife.org:taxon:0a6fdfc6-0019-11e3-a4c6-569cdfeac142:col20130815");
	result.setModified(parseDateTime("2014-07-07T15:29:20.058+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// actual[12]:
	result = new NameUsage();
	results.add(result);
	result.setKey(132390337);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(140567512);
	result.setPhylumKey(140567513);
	result.setClassKey(140567640);
	result.setOrderKey(140567646);
	result.setFamilyKey(140567647);
	result.setGenusKey(140567650);
	result.setDatasetKey(UUID.fromString("3408ce35-c482-4df0-9644-b8d4e6318f99"));
	result.setNubKey(2435099);
	result.setParentKey(140567650);
	result.setParent("Puma");
	result.setScientificName("Puma concolor Linnaeus, 1771");
	result.setCanonicalName("Puma concolor");
	result.setAuthorship("Linnaeus, 1771");
	result.setNameType(NameType.WELLFORMED);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	result.setTaxonID("urn:lsid:catalogueoflife.org:taxon:92e0ed20-d976-11e1-a7f8-569cdfeac142:col20130401");
	result.setModified(parseDateTime("2014-07-07T15:29:53.134+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// actual[13]:
	result = new NameUsage();
	results.add(result);
	result.setKey(135165191);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(101719444);
	result.setPhylumKey(102545028);
	result.setClassKey(102402290);
	result.setOrderKey(102303328);
	result.setFamilyKey(101148668);
	result.setGenusKey(102497493);
	result.setSpeciesKey(135165191);
	result.setDatasetKey(UUID.fromString("0938172b-2086-439c-a1dd-c21cb0109ed5"));
	result.setNubKey(2435099);
	result.setParentKey(102497493);
	result.setParent("Puma");
	result.setScientificName("Puma concolor (Linnaeus, 1771)");
	result.setCanonicalName("Puma concolor");
	result.setAuthorship(" (Linnaeus, 1771)");
	result.setNameType(NameType.WELLFORMED);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setOrigin(Origin.SOURCE);
	result.setRank(Rank.SPECIES);
	result.setAccordingTo("CoL2006/ITS");
	result.setNumDescendants(0);
	result.setTaxonID("10212875");
	result.setModified(parseDateTime("2014-06-15T15:45:33.692+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	//actual[14]:
	result = new NameUsage();
	results.add(result);
	result.setKey(137280675);
	result.setDatasetKey(UUID.fromString("0e61f8fe-7d25-4f81-ada7-d970bbb2c6d6"));
	result.setNubKey(2435099);
	result.setParentKey(137280674);
	result.setParent("Puma");
	result.setScientificName("Puma concolor (Linnaeus, 1771)");
	result.setCanonicalName("Puma concolor");
	result.setAuthorship(" (Linnaeus, 1771)");
	result.setNameType(NameType.WELLFORMED);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	result.setModified(parseDateTime("2014-06-16T03:20:29.626+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);
	try {
	    result.setReferences(new URI("http://inpn.mnhn.fr/espece/cd_nom/443624"));
	}
	catch (URISyntaxException e) {
	}
	result.setTaxonID("443624");

	// actual[15]:
	result = new NameUsage();
	results.add(result);
	result.setKey(138923348);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(140572660);
	result.setPhylumKey(140572661);
	result.setClassKey(140573043);
	result.setOrderKey(140573052);
	result.setFamilyKey(140573056);
	result.setGenusKey(140573060);
	result.setDatasetKey(UUID.fromString("cc056915-475e-4179-b12e-849acebc6ed9"));
	result.setNubKey(2435099);
	result.setParentKey(140573060);
	result.setParent("Puma");
	result.setScientificName("Puma concolor (Linnaeus, 1771)");
	result.setCanonicalName("Puma concolor");
	result.setAuthorship(" (Linnaeus, 1771)");
	result.setNameType(NameType.WELLFORMED);
	result.setNumDescendants(0);
	result.setModified(parseDateTime("2014-07-07T15:31:34.633+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result.setOrigin(Origin.SOURCE);
	result.setTaxonID("urn:lsid:catalogueoflife.org:taxon:d23229da-2dc5-11e0-98c6-2ce70255a436:col20120124");

	SpeciesAPIClient sut = new SpeciesAPIClient();
	PagingResponse<NameUsage> actual = sut.listByCanonicalName(Locale.ENGLISH, "Puma concolor", null, null);

	printCollections("listByCanonicalName", actual.getResults(), results);

	assertThat(actual, is(expected));
    }

    @Test
    public void listChildren() {
	PagingResponse<NameUsage> expected = new PagingResponse<NameUsage>(0,20);
	expected.setEndOfRecords(true);
	List<NameUsage> results = newArrayList();
	expected.setResults(results);

	NameUsage result = new NameUsage();
	results.add(result);
	result.setKey(5231191);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setSpeciesKey(5231190);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(5231191);
	result.setParentKey(5231190);
	result.setParent("Passer domesticus");
	result.setScientificName("Passer domesticus domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus domesticus");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBSPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	result.setNumDescendants(0);
	/*
	Identifier identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(3191746);
	identifier.setUsageKey(5231191);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("119127244");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("119127244");
	result.setTaxonID("119127244");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(6090823);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setSpeciesKey(5231190);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6090823);
	result.setParentKey(5231190);
	result.setParent("Passer domesticus");
	result.setScientificName("Passer domesticus subsp. bactrianus");
	result.setCanonicalName("Passer domesticus bactrianus");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBSPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Clements Checklist");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(3779573);
	identifier.setUsageKey(6090823);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("111381458");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("111381458");
	result.setTaxonID("111381458");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(6090862);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setSpeciesKey(5231190);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6090862);
	result.setParentKey(5231190);
	result.setParent("Passer domesticus");
	result.setScientificName("Passer domesticus subsp. balearoibericus");
	result.setCanonicalName("Passer domesticus balearoibericus");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBSPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Clements Checklist");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(3779607);
	identifier.setUsageKey(6090862);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("111381451");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("111381451");
	result.setTaxonID("111381451");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(6090836);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setSpeciesKey(5231190);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6090836);
	result.setParentKey(5231190);
	result.setParent("Passer domesticus");
	result.setScientificName("Passer domesticus subsp. biblicus");
	result.setCanonicalName("Passer domesticus biblicus");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBSPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Clements Checklist");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(3779585);
	identifier.setUsageKey(6090836);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("111381455");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("111381455");
	result.setTaxonID("111381455");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(6090829);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setSpeciesKey(5231190);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6090829);
	result.setParentKey(5231190);
	result.setParent("Passer domesticus");
	result.setScientificName("Passer domesticus subsp. hufufae");
	result.setCanonicalName("Passer domesticus hufufae");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBSPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Clements Checklist");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(3779578);
	identifier.setUsageKey(6090829);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("111381457");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("111381457");
	result.setTaxonID("111381457");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(6090812);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setSpeciesKey(5231190);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6090812);
	result.setParentKey(5231190);
	result.setParent("Passer domesticus");
	result.setScientificName("Passer domesticus subsp. hyrcanus");
	result.setCanonicalName("Passer domesticus hyrcanus");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBSPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Clements Checklist");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(3779565);
	identifier.setUsageKey(6090812);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("111381459");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("111381459");
	result.setTaxonID("111381459");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(5845699);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setSpeciesKey(5231190);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(5845699);
	result.setParentKey(5231190);
	result.setParent("Passer domesticus");
	result.setScientificName("Passer domesticus subsp. indicus");
	result.setCanonicalName("Passer domesticus indicus");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBSPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Clements Checklist");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(3676868);
	identifier.setUsageKey(5845699);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("111381456");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("111381456");
	result.setTaxonID("111381456");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(6090844);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setSpeciesKey(5231190);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6090844);
	result.setParentKey(5231190);
	result.setParent("Passer domesticus");
	result.setScientificName("Passer domesticus subsp. niloticus");
	result.setCanonicalName("Passer domesticus niloticus");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBSPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Clements Checklist");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(3779591);
	identifier.setUsageKey(6090844);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("111381454");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("111381454");
	result.setTaxonID("111381454");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(6090794);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setSpeciesKey(5231190);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6090794);
	result.setParentKey(5231190);
	result.setParent("Passer domesticus");
	result.setScientificName("Passer domesticus subsp. parkini");
	result.setCanonicalName("Passer domesticus parkini");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBSPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Clements Checklist");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(3779551);
	identifier.setUsageKey(6090794);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("111381462");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("111381462");
	result.setTaxonID("111381462");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(6090806);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setSpeciesKey(5231190);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6090806);
	result.setParentKey(5231190);
	result.setParent("Passer domesticus");
	result.setScientificName("Passer domesticus subsp. persicus");
	result.setCanonicalName("Passer domesticus persicus");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBSPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Clements Checklist");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(3779561);
	identifier.setUsageKey(6090806);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("111381460");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("111381460");
	result.setTaxonID("111381460");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(6090849);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setSpeciesKey(5231190);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6090849);
	result.setParentKey(5231190);
	result.setParent("Passer domesticus");
	result.setScientificName("Passer domesticus subsp. rufidorsalis");
	result.setCanonicalName("Passer domesticus rufidorsalis");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBSPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Clements Checklist");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(3779595);
	identifier.setUsageKey(6090849);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("111381453");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("111381453");
	result.setTaxonID("111381453");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(6090855);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setSpeciesKey(5231190);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6090855);
	result.setParentKey(5231190);
	result.setParent("Passer domesticus");
	result.setScientificName("Passer domesticus subsp. tingitanus");
	result.setCanonicalName("Passer domesticus tingitanus");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBSPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Clements Checklist");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(3779600);
	identifier.setUsageKey(6090855);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("111381452");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setSourceId("111381452");
	result.setTaxonID("111381452");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);


	SpeciesAPIClient sut = new SpeciesAPIClient();
	PagingResponse<NameUsage> actual = sut.listChildren(5231190, null, null);

	printCollections("listChildren", actual.getResults(), results);

	assertThat(actual, is(expected));
    }

    @Test
    public void listParents() {
	List<NameUsage> expected = newArrayList();
	NameUsage result = new NameUsage();
	expected.add(result);
	result.setKey(1);
	result.setKingdom("Animalia");
	result.setKingdomKey(1);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(1);
	result.setScientificName("Animalia");
	result.setCanonicalName("Animalia");
	result.setVernacularName("animals");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.KINGDOM);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	result.setNumDescendants(1913824);
	/*
	Identifier identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(1);
	identifier.setUsageKey(1);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("119459225");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("119459225");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	expected.add(result);
	result.setKey(44);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(44);
	result.setParentKey(1);
	result.setParent("Animalia");
	result.setScientificName("Chordata");
	result.setCanonicalName("Chordata");
	// result.setVernacularName("chordates");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.PHYLUM);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	result.setNumDescendants(162282);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(39);
	identifier.setUsageKey(44);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("119459276");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("119459276");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	expected.add(result);
	result.setKey(212);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(212);
	result.setParentKey(44);
	result.setParent("Chordata");
	result.setScientificName("Aves");
	result.setCanonicalName("Aves");
	result.setVernacularName("birds");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.CLASS);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	result.setNumDescendants(45856);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(195);
	identifier.setUsageKey(212);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("119459864");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("119459864");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	expected.add(result);
	result.setKey(729);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(729);
	result.setParentKey(212);
	result.setParent("Aves");
	result.setScientificName("Passeriformes");
	result.setCanonicalName("Passeriformes");
	//result.setVernacularName("Perching Birds");
	result.setVernacularName("Passerines");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.ORDER);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	result.setNumDescendants(24450);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(671);
	identifier.setUsageKey(729);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("119459865");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("119459865");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	expected.add(result);
	result.setKey(5264);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(5264);
	result.setParentKey(729);
	result.setParent("Passeriformes");
	result.setScientificName("Passeridae Illiger, 1811");
	result.setCanonicalName("Passeridae");
	result.setVernacularName("old world sparrows & snowfinches");
	result.setAuthorship("Illiger, 1811");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.FAMILY);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	result.setNumDescendants(164);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(5106);
	identifier.setUsageKey(5264);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("119459891");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("119459891");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	expected.add(result);
	result.setKey(2492321);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(2492321);
	result.setParentKey(5264);
	result.setParent("Passeridae");
	result.setScientificName("Passer Brisson, 1760");
	result.setCanonicalName("Passer");
	result.setAuthorship("Brisson, 1760");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setPublishedIn("Ornith. , 3, 72.");
	result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	result.setNumDescendants(76);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(1186011);
	identifier.setUsageKey(2492321);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("121023872");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("121023872");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	SpeciesAPIClient sut = new SpeciesAPIClient();
	//List<NameUsage> actual = sut.listParents(5231190, null);
	List<NameUsage> actual = sut.listParents(5231190, Locale.ENGLISH);

	printCollections("listParents", actual, expected);

	assertThat(actual, is(expected));
    }

    @Test
    public void listRelated() {
	List<NameUsage> expected = newArrayList();

	// expected[0]
	NameUsage result = new NameUsage();
	expected.add(result);
	result.setKey(117190020);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Ploceidae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(117195306);
	result.setPhylumKey(117201450);
	result.setClassKey(117187633);
	result.setOrderKey(117174493);
	result.setFamilyKey(117162718);
	result.setGenusKey(117197004);
	result.setSpeciesKey(117190020);
	result.setDatasetKey(UUID.fromString("1329753c-0537-451c-92a1-cddaa4534736"));
	result.setNubKey(5231190);
	result.setParentKey(117197004);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("New Zealand Inventory of Biodiversity");
	result.setNumDescendants(0);
	result.setTaxonID("C19E08EA-3F82-4DF8-BAE4-B1D804DC76D0");
	result.setModified(parseDateTime("2014-06-15T02:57:28.182+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// expected[1]
	result = new NameUsage();
	expected.add(result);
	result.setKey(117207287);
	result.setKingdom("Virus");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(117208777);
	result.setSpeciesKey(117207287);
	result.setDatasetKey(UUID.fromString("134eca5f-65ab-49a2-a229-3d0d35fcbefe"));
	result.setNubKey(5231190);
	result.setParentKey(117208777);
	result.setParent("Virus");
	result.setScientificName("Passer domesticus Linnaeus");
	result.setCanonicalName("Passer domesticus");
	// result.setVernacularName("House sparrow");
	result.setAuthorship("Linnaeus");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("New Zealand Organisms Register");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(13964318);
	identifier.setUsageKey(117207287);
	identifier.setDatasetKey(UUID.fromString("134eca5f-65ab-49a2-a229-3d0d35fcbefe"));
	identifier.setIdentifier("07EC2042-0164-4DC4-A43F-F16BAF7ABD38");
	// identifier.setType(IdentifierType.SOURCE_ID);

	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(13964317);
	identifier.setUsageKey(117207287);
	identifier.setDatasetKey(UUID.fromString("134eca5f-65ab-49a2-a229-3d0d35fcbefe"));
	identifier.setIdentifier("http://demo.nzor.org.nz/names/07EC2042-0164-4DC4-A43F-F16BAF7ABD38");
	identifier.setType(IdentifierType.URL);
	*/
	result.setTaxonID("07EC2042-0164-4DC4-A43F-F16BAF7ABD38");
	result.setModified(parseDateTime("2014-06-15T03:17:54.887+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);
	try {
	    result.setReferences(new URI("http://demo.nzor.org.nz/names/07EC2042-0164-4DC4-A43F-F16BAF7ABD38"));
	}
	catch (URISyntaxException e) {
	}


	// expected[2]
	result = new NameUsage();
	expected.add(result);
	result.setKey(116351961);
	result.setKingdom("Animalia");
	result.setKingdomKey(134723177);
	result.setDatasetKey(UUID.fromString("1bd42c2b-b58a-4a01-816b-bec8c8977927"));
	result.setNubKey(5231190);
	result.setParentKey(134723177);
	result.setParent("Animalia");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	// result.setVernacularName("House Sparrow");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(13157543);
	identifier.setUsageKey(116351961);
	identifier.setDatasetKey(UUID.fromString("1bd42c2b-b58a-4a01-816b-bec8c8977927"));
	identifier.setIdentifier("1185");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("1185");
	result.setModified(parseDateTime("2014-06-15T03:39:08.176+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// expected[3]
	result = new NameUsage();
	expected.add(result);
	result.setKey(126345167);
	result.setKingdom("Animalia");
	result.setClazz("Aves");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(134723802);
	result.setClassKey(134723803);
	result.setFamilyKey(134725254);
	result.setGenusKey(134725260);
	result.setSpeciesKey(126345167);
	result.setDatasetKey(UUID.fromString("d7435f14-dfc9-4aaa-bef3-5d1ed22d65bf"));
	result.setNubKey(5231190);
	result.setParentKey(134725260);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus");
	result.setCanonicalName("Passer domesticus");
	// result.setVernacularName("House Sparrow");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(20093830);
	identifier.setUsageKey(126345167);
	identifier.setDatasetKey(UUID.fromString("d7435f14-dfc9-4aaa-bef3-5d1ed22d65bf"));
	identifier.setIdentifier("09474");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("09474");
	result.setModified(parseDateTime("2014-06-15T03:45:57.317+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// expected[4]
	result = new NameUsage();
	expected.add(result);
	result.setKey(119127243);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(127795487);
	result.setPhylumKey(127795699);
	result.setClassKey(127796209);
	result.setOrderKey(127796210);
	result.setFamilyKey(127796236);
	result.setGenusKey(127837965);
	result.setSpeciesKey(119127243);
	result.setDatasetKey(UUID.fromString("7ddf754f-d193-4cc9-b351-99906754a03b"));
	result.setConstituentKey(UUID.fromString("29675850-b60f-11e1-bd72-00145eb45e9a"));
	result.setNubKey(5231190);
	result.setParentKey(127837965);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	// result.setVernacularName("House Sparrow");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("Peterson A. P.");
	result.setNumDescendants(1);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(15325615);
	identifier.setUsageKey(119127243);
	identifier.setDatasetKey(UUID.fromString("7ddf754f-d193-4cc9-b351-99906754a03b"));
	identifier.setIdentifier("11912084");
	// identifier.setType(IdentifierType.SOURCE_ID);
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(15325614);
	identifier.setUsageKey(119127243);
	identifier.setDatasetKey(UUID.fromString("7ddf754f-d193-4cc9-b351-99906754a03b"));
	identifier.setIdentifier("http://www.catalogueoflife.org/annual-checklist/details/species/id/11912084");
	identifier.setType(IdentifierType.URL);
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(15325613);
	identifier.setUsageKey(119127243);
	identifier.setDatasetKey(UUID.fromString("7ddf754f-d193-4cc9-b351-99906754a03b"));
	identifier.setIdentifier("urn:lsid:catalogueoflife.org:taxon:628acfcc-2ed2-11e2-a0dd-569cdfeac142:col20131017");
	identifier.setType(IdentifierType.LSID);
	*/
	result.setTaxonID("11912084");
	result.setModified(parseDateTime("2013-12-12T19:03:49.643+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);
	try {
	    result.setReferences(new URI("http://www.catalogueoflife.org/annual-checklist/details/species/id/11912084"));
	}
	catch (URISyntaxException e) {
	}

	// expected[5]
	result = new NameUsage();
	expected.add(result);
	result.setKey(103106406);
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setGenusKey(103281431);
	result.setSpeciesKey(103106406);
	result.setDatasetKey(UUID.fromString("90d9e8a6-0ce1-472d-b682-3451095dbc5a"));
	result.setNubKey(5231190);
	result.setParentKey(103281431);
	result.setParent("Passer");
	result.setBasionymKey(103176690);
	result.setBasionym("Fringilla domesticus Linnaeus, 1758");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setNumDescendants(0);
	/*
	Identifier identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(5278836);
	identifier.setUsageKey(103106406);
	identifier.setDatasetKey(UUID.fromString("90d9e8a6-0ce1-472d-b682-3451095dbc5a"));
	identifier.setIdentifier("97437");
	// identifier.setType(IdentifierType.SOURCE_ID);

	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(5278837);
	identifier.setUsageKey(103106406);
	identifier.setDatasetKey(UUID.fromString("90d9e8a6-0ce1-472d-b682-3451095dbc5a"));
	identifier.setIdentifier("http://www.faunaeur.org/full_results.php?id=97437");
	identifier.setType(IdentifierType.URL);
	*/
	result.setTaxonID("97437");
	result.setModified(parseDateTime("2014-06-14T21:04:19.241+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);
	try {
	    result.setReferences(new URI("http://www.faunaeur.org/full_results.php?id=97437"));
	}
	catch (URISyntaxException e) {
	}

	// actual[6]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(135534862);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(101719444);
	result.setPhylumKey(102545028);
	result.setClassKey(102188647);
	result.setOrderKey(102043334);
	result.setFamilyKey(101161112);
	result.setGenusKey(102372633);
	result.setSpeciesKey(135534862);
	result.setDatasetKey(UUID.fromString("0938172b-2086-439c-a1dd-c21cb0109ed5"));
	result.setNubKey(5231190);
	result.setParentKey(102372633);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("CoL2006/ITS");
	result.setNumDescendants(0);
	result.setTaxonID("10582565");
	result.setModified(parseDateTime("2014-06-15T15:45:33.692+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);


	// actual[7]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(106903667);
	result.setKingdom("Metazoa");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(106148414);
	result.setPhylumKey(106522535);
	result.setClassKey(106591045);
	result.setOrderKey(106621132);
	result.setFamilyKey(106624016);
	result.setGenusKey(106624125);
	result.setSpeciesKey(106304041);
	result.setDatasetKey(UUID.fromString("fab88965-e69d-4491-a04d-e3198b626e52"));
	result.setNubKey(5231190);
	result.setParentKey(106624125);
	result.setParent("Passer");
	result.setAcceptedKey(106304041);
	result.setAccepted("Passer domesticus");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	result.setTaxonID("e32730");
	result.setModified(parseDateTime("2014-06-14T23:00:02.974+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	result.setSynonym(true);

	// actual[8]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(106304041);
	result.setKingdom("Metazoa");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(106148414);
	result.setPhylumKey(106522535);
	result.setClassKey(106591045);
	result.setOrderKey(106621132);
	result.setFamilyKey(106624016);
	result.setGenusKey(106624125);
	result.setSpeciesKey(106304041);
	result.setDatasetKey(UUID.fromString("fab88965-e69d-4491-a04d-e3198b626e52"));
	result.setNubKey(5231190);
	result.setParentKey(106624125);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus");
	result.setCanonicalName("Passer domesticus");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(7);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6057361);
	identifier.setUsageKey(106304041);
	identifier.setDatasetKey(UUID.fromString("fab88965-e69d-4491-a04d-e3198b626e52"));
	identifier.setIdentifier("48849");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("48849");
	result.setModified(parseDateTime("2014-06-14T23:00:02.974+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// expected[9]:
	result = new NameUsage();
	//expected.add(result);
	result.setKey(107239200);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(107264513);
	result.setPhylumKey(107216119);
	result.setClassKey(107233360);
	result.setOrderKey(107237686);
	result.setFamilyKey(107364718);
	result.setGenusKey(107239199);
	result.setSpeciesKey(107239200);
	result.setDatasetKey(UUID.fromString("9ca92552-f23a-41a8-a140-01abaa31c931"));
	result.setNubKey(5231190);
	result.setParentKey(107239199);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	// result.setVernacularName("House Sparrow");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setNumDescendants(1);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(7023021);
	identifier.setUsageKey(107239200);
	identifier.setDatasetKey(UUID.fromString("9ca92552-f23a-41a8-a140-01abaa31c931"));
	identifier.setIdentifier("179628");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("179628");
	// result.setSynonym(false);

	// expected[10]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(110373340);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setKingdomKey(114986809);
	result.setPhylumKey(114986810);
	result.setClassKey(114986811);
	result.setOrderKey(114987031);
	result.setFamilyKey(114987115);
	result.setGenusKey(114987116);
	result.setDatasetKey(UUID.fromString("39131abf-97ea-41f2-9649-2e2e9c1a5d50"));
	result.setNubKey(5231190);
	result.setParentKey(114987116);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus");
	result.setCanonicalName("Passer domesticus");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(11923943);
	identifier.setUsageKey(110373340);
	identifier.setDatasetKey(UUID.fromString("39131abf-97ea-41f2-9649-2e2e9c1a5d50"));
	identifier.setIdentifier("1007");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setModified(parseDateTime("2012-05-23T20:56:20.151+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	result.setTaxonID("1007");
	// result.setSynonym(false);

	// expected[11]:
	result = new NameUsage();
	// expected.add(result);
	result.setKey(128818883);
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setGenusKey(134878846);
	result.setSpeciesKey(128818883);
	result.setDatasetKey(UUID.fromString("e632b198-5b2f-47ee-b7a6-6531ea435fa3"));
	result.setNubKey(5231190);
	result.setParentKey(134878846);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus");
	result.setCanonicalName("Passer domesticus");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(23039859);
	identifier.setUsageKey(128818883);
	identifier.setDatasetKey(UUID.fromString("e632b198-5b2f-47ee-b7a6-6531ea435fa3"));
	identifier.setIdentifier("922241");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("922241");
	// result.setSynonym(false);

	// expected[12]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(124750679);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(124836057);
	result.setPhylumKey(124839309);
	result.setClassKey(124843443);
	result.setOrderKey(124844209);
	result.setFamilyKey(124844946);
	result.setGenusKey(124844949);
	result.setSpeciesKey(124750679);
	result.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	result.setNubKey(5231190);
	result.setParentKey(124844949);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	// result.setVernacularName("House Sparrow");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(18688720);
	identifier.setUsageKey(124750679);
	identifier.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	identifier.setIdentifier("106008367");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("106008367");
	result.setModified(parseDateTime("2013-03-13T15:45:40.862+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// expected[13]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(5231190);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(212);
	result.setOrderKey(729);
	result.setFamilyKey(5264);
	result.setGenusKey(2492321);
	result.setSpeciesKey(5231190);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(5231190);
	result.setParentKey(2492321);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	// result.setVernacularName("House Sparrow");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	result.setNumDescendants(12);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(3191745);
	identifier.setUsageKey(5231190);
	identifier.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	identifier.setIdentifier("119127243");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("119127243");
	result.setModified(parseDateTime("2013-02-08T03:15:41.847+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// expected[14]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(131470469);
	result.setKingdom("Animalia");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Ploceidae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(131471028);
	result.setClassKey(131470787);
	result.setOrderKey(131470300);
	result.setFamilyKey(131470357);
	result.setGenusKey(131470450);
	result.setSpeciesKey(131470469);
	result.setDatasetKey(UUID.fromString("e9014a2a-a099-49e6-8a46-c7d888ab28d2"));
	result.setNubKey(5231190);
	result.setParentKey(131470450);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus (Linnaeus)");
	result.setCanonicalName("Passer domesticus");
	// result.setVernacularName("House Sparrow");
	result.setAuthorship(" (Linnaeus)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setAccordingTo("New Zealand Organisms Register Hosted Names");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(25691414);
	identifier.setUsageKey(131470469);
	identifier.setDatasetKey(UUID.fromString("e9014a2a-a099-49e6-8a46-c7d888ab28d2"));
	identifier.setIdentifier("34F79138-77E6-4C03-A8E3-53A00F40AA36");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("34F79138-77E6-4C03-A8E3-53A00F40AA36");
	result.setModified(parseDateTime("2014-06-15T08:00:01.494+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// actual[15]:
	/*
NameUsage{key=131483165, kingdom=Animalia, phylum=null, clazz=Aves, order=Passeriformes, family=Passeridae, genus=Passer, subgenus=null, species=Passer domesticus, kingdomKey=131483712, phylumKey=null, classKey=131482664, orderKey=131483170, familyKey=131483169, genusKey=131483164, subgenusKey=null, speciesKey=131483165, datasetKey=79f243f4-9c38-4305-997f-fa8ac3089dba, subDatasetKey=null, nubKey=5231190, parentKey=131483164, parent=Passer, proParteKey=null, acceptedKey=null, accepted=null, basionymKey=null, basionym=null, scientificName=Passer domesticus (Linnaeus, 1758), canonicalName=Passer domesticus, vernacularName=null, authorship= (Linnaeus, 1758), nameType=WELLFORMED, taxonomicStatus=null, nomenclaturalStatus=[], rank=SPECIES, publishedIn=null, accordingTo=null, numDescendants=0, isSynonym=false, origin=SOURCE, remarks=parentNameUsage in parentname, references=null, taxonID=20659, modified=null, lastCrawled=null, lastInterpreted=null}
	*/
	result = new NameUsage();
	expected.add(result);
	result.setKey(131483165);
	result.setKingdom("Animalia");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(131483712);
	result.setClassKey(131482664);
	result.setOrderKey(131483170);
	result.setFamilyKey(131483169);
	result.setGenusKey(131483164);
	result.setSpeciesKey(131483165);
	result.setDatasetKey(UUID.fromString("79f243f4-9c38-4305-997f-fa8ac3089dba"));
	result.setNubKey(5231190);
	result.setParentKey(131483164);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setRemarks("parentNameUsage in parentname");
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(25713917);
	identifier.setUsageKey(131483165);
	identifier.setDatasetKey(UUID.fromString("79f243f4-9c38-4305-997f-fa8ac3089dba"));
	identifier.setIdentifier("20659");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("20659");
	result.setModified(parseDateTime("2014-06-15T08:03:15.964+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// actual[16]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(107881308);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(107895884);
	result.setPhylumKey(107895862);
	result.setClassKey(107895812);
	result.setOrderKey(107895499);
	result.setFamilyKey(107894750);
	result.setGenusKey(107889491);
	result.setSpeciesKey(107881308);
	result.setDatasetKey(UUID.fromString("39653f3e-8d6b-4a94-a202-859359c164c5"));
	result.setNubKey(5231190);
	result.setParentKey(107889491);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	// result.setVernacularName("house sparrow");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(7622737);
	identifier.setUsageKey(107881308);
	identifier.setDatasetKey(UUID.fromString("39653f3e-8d6b-4a94-a202-859359c164c5"));
	identifier.setIdentifier("4602");
	// identifier.setType(IdentifierType.SOURCE_ID);

	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(7622736);
	identifier.setUsageKey(107881308);
	identifier.setDatasetKey(UUID.fromString("39653f3e-8d6b-4a94-a202-859359c164c5"));
	identifier.setIdentifier("http://species.be/en/4602");
	identifier.setType(IdentifierType.URL);
	*/
	result.setTaxonID("4602");
	result.setModified(parseDateTime("2014-06-15T16:39:05.568+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);
	try {
	result.setReferences(new URI("http://species.be/en/4602"));
	}
	catch (URISyntaxException e) {
	}

	// actual[17]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(125796751);
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setOrderKey(137015258);
	result.setFamilyKey(137015500);
	result.setGenusKey(137015501);
	result.setSpeciesKey(125796751);
	result.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	result.setNubKey(5231190);
	result.setParentKey(137015501);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(19695507);
	identifier.setUsageKey(125796751);
	identifier.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	identifier.setIdentifier("217589");
	// identifier.setType(IdentifierType.SOURCE_ID);

	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(19695506);
	identifier.setUsageKey(125796751);
	identifier.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	identifier.setIdentifier("http://de.wikipedia.org/wiki/Haussperling");
	identifier.setType(IdentifierType.URL);
	*/
	result.setTaxonID("217589");
	result.setModified(parseDateTime("2014-06-15T16:49:06.178+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);
	try {
	result.setReferences(new URI("http://de.wikipedia.org/wiki/Haussperling"));
	}
	catch (URISyntaxException e) {
	}

	// actual[18]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(116668229);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(116630539);
	result.setPhylumKey(116842680);
	result.setClassKey(137044782);
	result.setOrderKey(116840996);
	result.setFamilyKey(116848492);
	result.setGenusKey(137065574);
	result.setSpeciesKey(116668229);
	result.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	result.setNubKey(5231190);
	result.setParentKey(137065574);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus");
	result.setCanonicalName("Passer domesticus");
	// result.setVernacularName("House Sparrow");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(13477771);
	identifier.setUsageKey(116668229);
	identifier.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	identifier.setIdentifier("196147");
	// identifier.setType(IdentifierType.SOURCE_ID);

	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(13477770);
	identifier.setUsageKey(116668229);
	identifier.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	identifier.setIdentifier("http://en.wikipedia.org/wiki/House_Sparrow");
	identifier.setType(IdentifierType.URL);
	*/
	result.setTaxonID("196147");
	result.setModified(parseDateTime("2014-06-15T21:41:37.315+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);
	try {
	result.setReferences(new URI("http://en.wikipedia.org/wiki/House_Sparrow"));
	}
	catch (URISyntaxException e) {
	}

	// actual[19]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(111357780);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setKingdomKey(137087937);
	result.setPhylumKey(137087944);
	result.setClassKey(137087946);
	result.setDatasetKey(UUID.fromString("b351a324-77c4-41c9-a909-f30f77268bc4"));
	result.setNubKey(5231190);
	result.setParentKey(137087946);
	result.setParent("Aves");
	result.setScientificName("Passer domesticus");
	result.setCanonicalName("Passer domesticus");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(12409727);
	identifier.setUsageKey(111357780);
	identifier.setDatasetKey(UUID.fromString("b351a324-77c4-41c9-a909-f30f77268bc4"));
	identifier.setIdentifier("420");
	// identifier.setType(IdentifierType.SOURCE_ID);

	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(12409728);
	identifier.setUsageKey(111357780);
	identifier.setDatasetKey(UUID.fromString("b351a324-77c4-41c9-a909-f30f77268bc4"));
	identifier.setIdentifier("http://www.invasivespecies.net/database/species/ecology.asp?si=420&fr=1&sts=&lang=EN");
	identifier.setType(IdentifierType.URL);
	*/
	result.setTaxonID("420");
	result.setModified(parseDateTime("2014-06-15T21:53:31.459+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);
	try {
	    result.setReferences(new URI("http://www.invasivespecies.net/database/species/ecology.asp?si=420&fr=1&sts=&lang=EN"));
	}
	catch (URISyntaxException e) {
	}

	// expected[20]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(111381448);
	result.setKingdom("Animalia");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(137087968);
	result.setClassKey(137087969);
	result.setOrderKey(137087989);
	result.setSpeciesKey(111381448);
	result.setDatasetKey(UUID.fromString("47f16512-bf31-410f-b272-d151c996b2f6"));
	result.setNubKey(5231190);
	result.setParentKey(137087989);
	result.setParent("Passeriformes");
	result.setScientificName("Passer domesticus");
	result.setCanonicalName("Passer domesticus");
	// result.setVernacularName("House Sparrow");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(12433612);
	identifier.setUsageKey(111381448);
	identifier.setDatasetKey(UUID.fromString("47f16512-bf31-410f-b272-d151c996b2f6"));
	identifier.setIdentifier("31048");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("31048");
	result.setModified(parseDateTime("2014-06-15T21:56:57.165+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// actual[21]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(115030038);
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Aves");
	result.setOrder("Passeriformes");
	result.setFamily("Passeridae");
	result.setGenus("Passer");
	result.setSpecies("Passer domesticus");
	result.setKingdomKey(115107543);
	result.setPhylumKey(115107576);
	result.setClassKey(115107412);
	result.setOrderKey(115106786);
	result.setFamilyKey(115104824);
	result.setGenusKey(115087848);
	result.setSpeciesKey(115030038);
	result.setDatasetKey(UUID.fromString("de8934f4-a136-481c-a87a-b0b202b80a31"));
	result.setNubKey(5231190);
	result.setParentKey(115087848);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SPECIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(12544543);
	identifier.setUsageKey(115030038);
	identifier.setDatasetKey(UUID.fromString("de8934f4-a136-481c-a87a-b0b202b80a31"));
	identifier.setIdentifier("103038");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("103038");
	result.setModified(parseDateTime("2014-06-15T22:16:27.867+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	// actual[22]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(137277641);
	result.setDatasetKey(UUID.fromString("0e61f8fe-7d25-4f81-ada7-d970bbb2c6d6"));
	result.setNubKey(5231190);
	result.setParentKey(137277640);
	result.setParent("Passer");
	result.setScientificName("Passer domesticus (Linnaeus, 1758)");
	result.setCanonicalName("Passer domesticus");
	result.setAuthorship(" (Linnaeus, 1758)");
	result.setNameType(NameType.WELLFORMED);
	result.setOrigin(Origin.SOURCE);
	try{
	    result.setReferences(new URI("http://inpn.mnhn.fr/espece/cd_nom/4525"));
	}
	catch (URISyntaxException e) {
	}
	result.setTaxonID("4525");
	result.setModified(parseDateTime("2014-06-16T03:20:29.626+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	/*
NameUsage{key=138180561, kingdom=null, phylum=null, clazz=null, order=null, family=null, genus=null, subgenus=null, species=null, kingdomKey=null, phylumKey=null, classKey=null, orderKey=null, familyKey=null, genusKey=null, subgenusKey=null, speciesKey=null, datasetKey=84392896-f762-11e1-a439-00145eb45e9a, subDatasetKey=null, nubKey=5231190, parentKey=null, parent=null, proParteKey=null, acceptedKey=null, accepted=null, basionymKey=null, basionym=null, scientificName=Passer domesticus, canonicalName=Passer domesticus, vernacularName=null, authorship=, nameType=WELLFORMED, taxonomicStatus=null, nomenclaturalStatus=[], rank=null, publishedIn=null, accordingTo=null, numDescendants=0, isSynonym=false, origin=SOURCE, remarks=null, references=null, taxonID=null, modified=null, lastCrawled=null, lastInterpreted=null}
	*/

	// actual[23]:
	result = new NameUsage();
	expected.add(result);
	result.setKey(138180561);
	result.setDatasetKey(UUID.fromString("84392896-f762-11e1-a439-00145eb45e9a"));
	result.setNubKey(5231190);
	result.setScientificName("Passer domesticus");
	result.setCanonicalName("Passer domesticus");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setOrigin(Origin.SOURCE);
	result.setModified(parseDateTime("2014-06-16T05:16:47.995+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	SpeciesAPIClient sut = new SpeciesAPIClient();
	List<NameUsage> actual = sut.listRelated(5231190, null, null);

	printCollections("listRelated", actual, expected);

	assertThat(actual, is(expected));
    }

    @Test
    public void listRoot() {
	PagingResponse<NameUsage> expected = new PagingResponse<NameUsage>(0,20);
	expected.setEndOfRecords(false);
	List<NameUsage> results = newArrayList();

	NameUsage result = new NameUsage();
	results.add(result);

	// actual[0]:
	result.setKey(134674962);
	result.setKingdom("Plantae");
	result.setKingdomKey(134674962);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setNubKey(6);
	result.setScientificName("Plantae");
	result.setCanonicalName("Plantae");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.KINGDOM);
	result.setOrigin(Origin.DENORMED_CLASSIFICATION);
	result.setNumDescendants(75397);
	/*
	Identifier identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(18794147);
	identifier.setUsageKey(124856107);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107079779);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Plumbaginoideae");
	result.setCanonicalName("Plumbaginoideae");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBFAMILY);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(13);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6823347);
	identifier.setUsageKey(107079779);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("f1637");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("f1637");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107079780);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Staticoideae");
	result.setCanonicalName("Staticoideae");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBFAMILY);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(88);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6823348);
	identifier.setUsageKey(107079780);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("f1638");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("f1638");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107091387);
	result.setSubgenus("Ceratotropis");
	result.setSubgenusKey(107091387);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Ceratotropis");
	result.setCanonicalName("Ceratotropis");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBGENUS);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(20);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6834955);
	identifier.setUsageKey(107091387);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g18744");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g18744");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107091386);
	result.setSubgenus("Dolichovigna");
	result.setSubgenusKey(107091386);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setNubKey(2983915);
	result.setScientificName("Dolichovigna");
	result.setCanonicalName("Dolichovigna");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBGENUS);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(1);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6834954);
	identifier.setUsageKey(107091386);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g18743");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g18743");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107092499);
	result.setSubgenus("Glycine");
	result.setSubgenusKey(107092499);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setNubKey(5359646);
	result.setScientificName("Glycine");
	result.setCanonicalName("Glycine");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBGENUS);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(28);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6836067);
	identifier.setUsageKey(107092499);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g19931");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g19931");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107091380);
	result.setSubgenus("Haydonia");
	result.setSubgenusKey(107091380);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setNubKey(2943134);
	result.setScientificName("Haydonia");
	result.setCanonicalName("Haydonia");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBGENUS);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(3);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6834948);
	identifier.setUsageKey(107091380);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g18738");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g18738");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107091381);
	result.setSubgenus("Macrorhyncha");
	result.setSubgenusKey(107091381);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Macrorhyncha");
	result.setCanonicalName("Macrorhyncha");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBGENUS);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6834949);
	identifier.setUsageKey(107091381);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g18739");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g18739");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107091383);
	result.setSubgenus("Plectrotropis");
	result.setSubgenusKey(107091383);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setNubKey(7302211);
	result.setScientificName("Plectrotropis");
	result.setCanonicalName("Plectrotropis");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBGENUS);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(11);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6834951);
	identifier.setUsageKey(107091383);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g18740");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g18740");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107091388);
	result.setSubgenus("Sigmoidotropis");
	result.setSubgenusKey(107091388);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Sigmoidotropis");
	result.setCanonicalName("Sigmoidotropis");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBGENUS);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(18);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6834956);
	identifier.setUsageKey(107091388);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g18745");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g18745");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107092500);
	result.setSubgenus("Soja");
	result.setSubgenusKey(107092500);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setNubKey(7302421);
	result.setScientificName("Soja");
	result.setCanonicalName("Soja");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBGENUS);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(2);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6836068);
	identifier.setUsageKey(107092500);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g19932");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g19932");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107091369);
	result.setSubgenus("Vigna");
	result.setSubgenusKey(107091369);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setNubKey(2982372);
	result.setScientificName("Vigna");
	result.setCanonicalName("Vigna");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SUBGENUS);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(56);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6834937);
	identifier.setUsageKey(107091369);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g18728");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g18728");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107092242);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Meyerianae");
	result.setCanonicalName("Meyerianae");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SECTION);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(5);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6835810);
	identifier.setUsageKey(107092242);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g19667");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g19667");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107092246);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Ridleyanae");
	result.setCanonicalName("Ridleyanae");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SECTION);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(2);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6835814);
	identifier.setUsageKey(107092246);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g19670");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g19670");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107092247);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Schlechterianae");
	result.setCanonicalName("Schlechterianae");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SECTION);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(1);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6835815);
	identifier.setUsageKey(107092247);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g19671");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g19671");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107092243);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Australiensis");
	result.setCanonicalName("Australiensis");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SERIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(1);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6835811);
	identifier.setUsageKey(107092243);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g19668");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g19668");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107092244);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Brachyantha");
	result.setCanonicalName("Brachyantha");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SERIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(1);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6835812);
	identifier.setUsageKey(107092244);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g19669");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g19669");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107092520);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Chaenocarpa");
	result.setCanonicalName("Chaenocarpa");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SERIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(27);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6836088);
	identifier.setUsageKey(107092520);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g19951");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g19951");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107092517);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Clistocarpa");
	result.setCanonicalName("Clistocarpa");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SERIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(1);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6836085);
	identifier.setUsageKey(107092517);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g19949");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g19949");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	result = new NameUsage();
	//results.add(result);
	result.setKey(107092618);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Fuscoveratrum");
	result.setCanonicalName("Fuscoveratrum");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SERIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	/*
	identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(6836186);
	identifier.setUsageKey(107092618);
	identifier.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	identifier.setIdentifier("g20044");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	result.setTaxonID("g20044");
	// result.setSynonym(false);

	result = new NameUsage();
	results.add(result);
	result.setKey(107092620);
	result.setDatasetKey(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"));
	result.setScientificName("Fuscoveratrum");
	result.setCanonicalName("Fuscoveratrum");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.SERIES);
	result.setOrigin(Origin.SOURCE);
	result.setNumDescendants(0);
	result.setTaxonID("g20047");
	result.setModified(parseDateTime("2014-06-14T23:50:10.449+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	// result.setSynonym(false);

	expected.setResults(results);
	SpeciesAPIClient sut = new SpeciesAPIClient();
	PagingResponse<NameUsage> actual = sut.listRoot(UUID.fromString("66dd0960-2d7d-46ee-a491-87b9adcfe7b1"),null,null);

	printCollections("listRoot", actual.getResults(), results);

	assertThat(actual, is(expected));
    }

    /**
     * Creates response to http://api.gbif.org/v1/species/2376640/synonyms
     */
    private PagingResponse<NameUsage> createSynonyms2376640 ()
    {
	PagingResponse<NameUsage> expected = new PagingResponse<NameUsage>(0,20);
	expected.setEndOfRecords(true);
	List<NameUsage> results = newArrayList();
	expected.setResults(results);

	NameUsage result = new NameUsage();
	results.add(result);
	result.setKey(2382423);
	result.setNubKey(2382423);
	result.setTaxonID("109681242");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Actinopterygii");
	result.setOrder("Perciformes");
	result.setFamily("Eleotridae");
	result.setGenus("Gobiomorus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(204);
	result.setOrderKey(587);
	result.setFamilyKey(4268);
	result.setGenusKey(2376640);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setParentKey(4268);
	result.setParent("Eleotridae");
	result.setAcceptedKey(2376640);
	result.setAccepted("Gobiomorus Lacepède, 1800");
	result.setScientificName("Alvarius Girard, 1859");
	result.setCanonicalName("Alvarius");
	result.setAuthorship("Girard, 1859");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setPublishedIn("Proc. Acad. Nat. Sci. Philad. , 1859, 101.");
	result.setAccordingTo("World Register of Marine Species");
	result.setNumDescendants(0);
	result.setSynonym(true);

	result = new NameUsage();
	results.add(result);
	result.setKey(2386427);
	result.setNubKey(2386427);
	result.setTaxonID("109682229");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Actinopterygii");
	result.setOrder("Perciformes");
	result.setFamily("Eleotridae");
	result.setGenus("Gobiomorus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(204);
	result.setOrderKey(587);
	result.setFamilyKey(4268);
	result.setGenusKey(2376640);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setParentKey(4268);
	result.setParent("Eleotridae");
	result.setAcceptedKey(2376640);
	result.setAccepted("Gobiomorus Lacepède, 1800");
	result.setScientificName("Lembus Günther, 1859");
	result.setCanonicalName("Lembus");
	result.setAuthorship("Günther, 1859");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setPublishedIn("Cat. Fish. Brit. Mus. , 2, 501, 505.");
	result.setAccordingTo("World Register of Marine Species");
	result.setNumDescendants(0);
	result.setSynonym(true);

	result = new NameUsage();
	results.add(result);
	result.setKey(7009677);
	result.setNubKey(7009677);
	result.setTaxonID("115814451");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Actinopterygii");
	result.setOrder("Perciformes");
	result.setFamily("Eleotridae");
	result.setGenus("Gobiomorus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(204);
	result.setOrderKey(587);
	result.setFamilyKey(4268);
	result.setGenusKey(2376640);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setParentKey(4268);
	result.setParent("Eleotridae");
	result.setAcceptedKey(2376640);
	result.setAccepted("Gobiomorus Lacepède, 1800");
	result.setScientificName("Pelmatia Browne, 1789");
	result.setCanonicalName("Pelmatia");
	result.setAuthorship("Browne, 1789");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setAccordingTo("Interim Register of Marine and Nonmarine Genera");
	result.setNumDescendants(0);
	result.setSynonym(true);

	result = new NameUsage();
	results.add(result);
	result.setKey(4840870);
	result.setNubKey(4840870);
	result.setTaxonID("102265277");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Actinopterygii");
	result.setOrder("Perciformes");
	result.setFamily("Eleotridae");
	result.setGenus("Gobiomorus");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(204);
	result.setOrderKey(587);
	result.setFamilyKey(4268);
	result.setGenusKey(2376640);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setParentKey(4268);
	result.setParent("Eleotridae");
	result.setAcceptedKey(2376640);
	result.setAccepted("Gobiomorus Lacepède, 1800");
	result.setScientificName("Sobiomorus Fischer, 1808");
	result.setCanonicalName("Sobiomorus");
	result.setAuthorship("Fischer, 1808");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setPublishedIn("Tabl. Syn. Zoog. , ed. 2, Pisces, col. (14).");
	result.setAccordingTo("Interim Register of Marine and Nonmarine Genera");
	result.setNumDescendants(0);
	result.setSynonym(true);

	return expected;
    }


    /**
     * Creates response to http://api.gbif.org/v1/species/100710497/synonyms
     */
    private PagingResponse<NameUsage> createSynonyms100710497 ()
    {
	PagingResponse<NameUsage> expected = new PagingResponse<NameUsage>(0,20);
	expected.setEndOfRecords(true);
	List<NameUsage> results = newArrayList();
	expected.setResults(results);
	NameUsage result = new NameUsage();
	results.add(result);
	result.setKey(102382983);
	result.setNubKey(3204909);
	result.setTaxonID("1290714");
	result.setKingdom("Protista");
	result.setPhylum("Ciliophora");
	result.setClazz("Oligohymenophora");
	result.setOrder("Scuticociliatida");
	result.setFamily("Cohnilembidae");
	result.setGenus("Cohnilembus");
	result.setKingdomKey(102545060);
	result.setPhylumKey(102545140);
	result.setClassKey(102514394);
	result.setOrderKey(102143947);
	result.setFamilyKey(102238321);
	result.setGenusKey(100710497);
	result.setDatasetKey(UUID.fromString("0938172b-2086-439c-a1dd-c21cb0109ed5"));
	result.setParentKey(102238321);
	result.setParent("Cohnilembidae");
	result.setAcceptedKey(100710497);
	result.setAccepted("Cohnilembus Kahl, 1933");
	result.setScientificName("Lembus Cohn, 1866");
	result.setCanonicalName("Lembus");
	result.setAuthorship("Cohn, 1866");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRemarks("See Cohnilembus Kahl 1933. (Nomen. Zool.).");
	result.setPublishedIn("Z. wiss. Zool. , 16, 270.");
	result.setAccordingTo("SN2000 unverified; Nomenclator Zoologicus");
	result.setNumDescendants(0);
	result.setModified(parseDateTime("2014-06-15T15:45:33.692+0000"));
	result.setLastInterpreted(parseDateTime("2014-11-03T15:22:26.302+0000"));
	result.setSynonym(true);
	return expected;
    }

    /**
     * Creates response to http://api.gbif.org/v1/species/126926284/synonyms
     */
    private PagingResponse<NameUsage> createSynonyms126926284 () {
	PagingResponse<NameUsage> expected = new PagingResponse<NameUsage>(0,20);
	expected.setEndOfRecords(true);
	List<NameUsage> results = newArrayList();
	expected.setResults(results);
	NameUsage result = new NameUsage();
	results.add(result);

	result.setKey(100496585);
	result.setKingdom("Protista");
	result.setPhylum("Ciliophora");
	result.setClazz("Oligohymenophora");
	result.setOrder("Scuticociliatida");
	result.setFamily("Cohnilembidae");
	result.setKingdomKey(126940483);
	result.setPhylumKey(100533110);
	result.setClassKey(126940663);
	result.setOrderKey(126940683);
	result.setFamilyKey(126940686);
	result.setDatasetKey(UUID.fromString("714c64e3-2dc1-4bb7-91e4-54be5af4da12"));
	result.setNubKey(3204909);
	result.setParentKey(126940686);
	result.setParent("Cohnilembidae");
	result.setAcceptedKey(126926284);
	result.setAccepted("Cohnilembus");
	result.setScientificName("Lembus Cohn, 1866");
	result.setCanonicalName("Lembus");
	result.setAuthorship("Cohn, 1866");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setOrigin(Origin.SOURCE);
	result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRemarks("See Cohnilembus Kahl 1933. (Nomen. Zool.).");
	result.setPublishedIn("Z. wiss. Zool. , 16, 270.");
	result.setAccordingTo("SN2000 unverified; Nomenclator Zoologicus");
	result.setNumDescendants(0);
	/*
	Identifier identifier = new Identifier();
	result.addIdentifier(identifier);
	identifier.setKey(4467557);
	identifier.setUsageKey(100496585);
	identifier.setDatasetKey(UUID.fromString("714c64e3-2dc1-4bb7-91e4-54be5af4da12"));
	identifier.setIdentifier("1290714");
	// identifier.setType(IdentifierType.SOURCE_ID);
	*/
	// result.setTaxonID("1290714");
	result.setSynonym(true);

	return expected;
    }

    @Test
    public void listSynonyms() {
	PagingResponse<NameUsage> expected = createSynonyms100710497();

	SpeciesAPIClient sut = new SpeciesAPIClient();
	PagingResponse<NameUsage> actual = sut.listSynonyms(100710497,null,null);

	printCollections("listSynonyms", actual.getResults(), expected.getResults());
	assertThat(actual, is(expected));
    }

    @Test
    public void match() {
	List<NameUsageMatch> alternatives = newArrayList();

	NameUsageMatch expected = new 	NameUsageMatch();
	alternatives.add(expected);
	expected.setUsageKey(7295983);
	expected.setScientificName("Oenone Tul.");
	expected.setCanonicalName("Oenone");
	expected.setRank(Rank.GENUS);
	// expected.setSynonym(false);
	expected.setConfidence(60);
	expected.setNote("Individual confidence: name=52; classification=7; rank=0; accepted=1");
	expected.setMatchType(NameUsageMatch.MatchType.FUZZY);
	expected.setKingdom("Plantae");
	expected.setPhylum("Magnoliophyta");
	expected.setClazz("Magnoliopsida");
	expected.setOrder("Malpighiales");
	expected.setFamily("Podostemaceae");
	expected.setGenus("Oenone");
	expected.setKingdomKey(6);
	expected.setPhylumKey(49);
	expected.setClassKey(220);
	expected.setOrderKey(1414);
	expected.setFamilyKey(2418);
	expected.setGenusKey(7295983);

	expected = new NameUsageMatch();
	alternatives.add(expected);
	expected.setUsageKey(2492483);
	expected.setScientificName("Oenanthe Vieillot, 1816");
	expected.setCanonicalName("Oenanthe");
	expected.setRank(Rank.GENUS);
	// expected.setSynonym(false);
	expected.setConfidence(35);
	expected.setNote("Individual confidence: name=86; classification=-52; rank=0; accepted=1");
	expected.setMatchType(NameUsageMatch.MatchType.FUZZY);
	expected.setKingdom("Animalia");
	expected.setPhylum("Chordata");
	expected.setClazz("Aves");
	expected.setOrder("Passeriformes");
	expected.setFamily("Muscicapidae");
	expected.setGenus("Oenanthe");
	expected.setKingdomKey(1);
	expected.setPhylumKey(44);
	expected.setClassKey(212);
	expected.setOrderKey(729);
	expected.setFamilyKey(9322);
	expected.setGenusKey(2492483);

	expected = new NameUsageMatch();
	alternatives.add(expected);
	expected.setUsageKey(7236648);
	expected.setScientificName("Oenone Agassiz, 1846");
	expected.setCanonicalName("Oenone");
	expected.setRank(Rank.GENUS);
	// expected.setSynonym(false);
	expected.setConfidence(1);
	expected.setNote("Individual confidence: name=52; classification=-52; rank=0; accepted=1");
	expected.setMatchType(NameUsageMatch.MatchType.FUZZY);
	expected.setKingdom("Animalia");
	expected.setPhylum("Annelida");
	expected.setClazz("Polychaeta");
	expected.setGenus("Oenone");
	expected.setKingdomKey(1);
	expected.setPhylumKey(42);
	expected.setClassKey(256);
	expected.setGenusKey(7236648);

	expected = new NameUsageMatch();
	alternatives.add(expected);
	expected.setUsageKey(3258315);
	expected.setScientificName("Oenone Haliday, 1839");
	expected.setCanonicalName("Oenone");
	expected.setRank(Rank.GENUS);
	// expected.setSynonym(false);
	expected.setConfidence(1);
	expected.setNote("Individual confidence: name=52; classification=-52; rank=0; accepted=1");
	expected.setMatchType(NameUsageMatch.MatchType.FUZZY);
	expected.setKingdom("Animalia");
	expected.setPhylum("Arthropoda");
	expected.setClazz("Insecta");
	expected.setOrder("Hymenoptera");
	expected.setGenus("Oenone");
	expected.setKingdomKey(1);
	expected.setPhylumKey(54);
	expected.setClassKey(216);
	expected.setOrderKey(1457);
	expected.setGenusKey(3258315);

	expected = new NameUsageMatch();
	alternatives.add(expected);
	expected.setUsageKey(3247150);
	expected.setScientificName("Oenone Hartman, 1881");
	expected.setCanonicalName("Oenone");
	expected.setRank(Rank.GENUS);
	// expected.setSynonym(false);
	expected.setConfidence(1);
	expected.setNote("Individual confidence: name=52; classification=-52; rank=0; accepted=1");
	expected.setMatchType(NameUsageMatch.MatchType.FUZZY);
	expected.setKingdom("Animalia");
	expected.setPhylum("Mollusca");
	expected.setGenus("Oenone");
	expected.setKingdomKey(1);
	expected.setPhylumKey(52);
	expected.setGenusKey(3247150);

	expected = new NameUsageMatch();
	alternatives.add(expected);
	expected.setUsageKey(2321651);
	expected.setScientificName("Oenone Savigny, 1818");
	expected.setCanonicalName("Oenone");
	expected.setRank(Rank.GENUS);
	// expected.setSynonym(false);
	expected.setConfidence(1);
	expected.setNote("Individual confidence: name=52; classification=-52; rank=0; accepted=1");
	expected.setMatchType(NameUsageMatch.MatchType.FUZZY);
	expected.setKingdom("Animalia");
	expected.setPhylum("Annelida");
	expected.setClazz("Polychaeta");
	expected.setOrder("Eunicida");
	expected.setFamily("Oenonidae");
	expected.setGenus("Oenone");
	expected.setKingdomKey(1);
	expected.setPhylumKey(42);
	expected.setClassKey(256);
	expected.setOrderKey(865);
	expected.setFamilyKey(2052);
	expected.setGenusKey(2321651);

	expected = new NameUsageMatch();
	alternatives.add(expected);
	expected.setUsageKey(3256243);
	expected.setScientificName("Oenone Meyrick, 1890");
	expected.setCanonicalName("Oenone");
	expected.setRank(Rank.GENUS);
	// expected.setSynonym(true);
	expected.setConfidence(0);
	expected.setNote("Individual confidence: name=52; classification=-52; rank=0; accepted=0");
	expected.setMatchType(NameUsageMatch.MatchType.FUZZY);
	expected.setKingdom("Animalia");
	expected.setPhylum("Arthropoda");
	expected.setClazz("Insecta");
	expected.setOrder("Lepidoptera");
	expected.setFamily("Geometridae");
	expected.setGenus("Dirce");
	expected.setKingdomKey(1);
	expected.setPhylumKey(54);
	expected.setClassKey(216);
	expected.setOrderKey(797);
	expected.setFamilyKey(6950);
	expected.setGenusKey(1978340);

	expected = new NameUsageMatch();
	expected.setAlternatives(alternatives);
	expected.setUsageKey(3034893);
	expected.setScientificName("Oenanthe Linnaeus, 1753");
	expected.setCanonicalName("Oenanthe");
	expected.setRank(Rank.GENUS);
	// expected.setSynonym(false);
	expected.setConfidence(94);
	expected.setNote("Individual confidence: name=86; classification=7; rank=0; accepted=1; nextMatch=10");
	expected.setMatchType(NameUsageMatch.MatchType.FUZZY);
	expected.setKingdom("Plantae");
	expected.setPhylum("Magnoliophyta");
	expected.setClazz("Magnoliopsida");
	expected.setOrder("Apiales");
	expected.setFamily("Apiaceae");
	expected.setGenus("Oenanthe");
	expected.setKingdomKey(6);
	expected.setPhylumKey(49);
	expected.setClassKey(220);
	expected.setOrderKey(1351);
	expected.setFamilyKey(6720);
	expected.setGenusKey(3034893);

	LinneanClassification classification = new NameUsage();
	classification.setKingdom("Plantae");
	SpeciesAPIClient sut = new SpeciesAPIClient();
	NameUsageMatch actual = sut.match("Oenante", null, classification, false, true);

	assertThat(actual, is(expected));
    }

    @Test
    public void test_search() {

	SearchResponse<NameUsageSearchResult, NameUsageSearchParameter> expected = new SearchResponse<NameUsageSearchResult, NameUsageSearchParameter>(0, 20);
	List<NameUsageSearchResult> results = newArrayList();
	expected.setResults(results);
	expected.setEndOfRecords(false);
	expected.setCount(51L);

	// expected[0]:
	NameUsageSearchResult result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(2435098);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(2435098);
	result.setParentKey(9703);
	result.setParent("Felidae");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setScientificName("Puma Jardine, 1834");
	result.setCanonicalName("Puma");
	result.setAuthorship("Jardine, 1834");
	result.setPublishedIn("Natur. Libr. p. 266-267");
	result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	result.setNameType(NameType.WELLFORMED);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(17);
	result.setNumOccurrences(0);
	result.setTaxonID("121020604");
	result.setExtinct(false);
	// result.setMarine(false);
	Description description = new Description();
	result.getDescriptions().add(description);
	description.setDescription("Die Pumas (Puma) sind eine Raubtiergattung aus der Familie der Katzen (Felidae). Sie gehören aufgrund ihrer relativ geringen Größe zur Unterfamilie der Kleinkatzen. In dieser Gattung werden zwei lebende Arten zusammengefasst: der Puma (Puma concolor) und der Jaguarundi oder Wieselkatze (Puma yaguarondi). Eine ausgestorbene Art der Gattung ist mit Puma pardoides aus dem frühen Pleistozän Eurasiens bekannt.Der Jaguarundi wurde früher in einer eigenen Gattung, Herpailurus, eingeordnet. Aufgrund molekulargenetischer Untersuchungen wurde aber eine enge Verwandtschaft dieser zwei Arten festgestellt, sodass jüngere Systematiken sie in einer gemeinsamen Gattung zusammenfassen.Das Fell dieser Tiere ist kurz und dicht und meist gelbgrau oder rötlich gefärbt, erwachsene Tiere sind im Gegensatz zu vielen anderen Katzenarten nicht gefleckt. Beide Arten sind in Amerika heimisch.DNA-Untersuchungen haben überdies ergeben, dass der nächste lebende Verwandte dieser Gattung der Gepard ist. Aufgrund seines eigentümlichen Körperbaus wurde dieser in einer eigenen Unterfamilie innerhalb der Katzen, Acinonychinae geführt, eine Einteilung, die nach Entdeckung dieser Gemeinsamkeit nicht mehr aufrechterhalten werden kann.");
	description = new Description();
	result.getDescriptions().add(description);
	description.setDescription("DISPLAYTITLE Puma is a member of Felidae that contains the cougar (also known as the puma, among other names) and the jaguarundi, and may also include several poorly known Old World fossil representatives (for example, Puma pardoides, or \"Owen's panther,\" a large cougar-like cat of Eurasia's Pliocene).Hemmer, H. (1965). Studien an \"Panthera\" schaubi Viret aus dem Villafranchien von Saint-Vallier (Drôme). Neues Jahrbuch für Geologie und Paläontologie, Abhandlungen 122, 324–336.Hemmer, H., Kahlike, R.-D. & Vekua, A. K. (2004). The Old World puma Puma pardoides (Owen, 1846) (Carnivora: Felidae) in the Lower Villafranchian (Upper Pliocene) of Kvabebi (East Georgia, Transcaucasia) and its evolutionary and biogeographical significance. Neues Jahrbuch fur Geologie und Palaontologie, Abhandlungen 233, 197–233.");
	description = new Description();
	result.getDescriptions().add(description);
	//	description.setDescription("Puma concolor (Linnaeus, 1771) – Cougar Puma pardoides (Owen, 1846) – Owen's Panther Puma yagouaroundi (Geoffroy, 1803) – Jaguarundi");
	description.setDescription("Puma concolor (Linnaeus, 1771) – Cougar Puma pardoides (Owen, 1846) – Owen's Panther Puma yagouaroundi (Geoffroy, 1803) – Jaguarundi");

	VernacularName vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Pumas");
	vernacularName.setLanguage(Language.GERMAN);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("pumas");
	vernacularName.setLanguage(Language.ENGLISH);
	// result.setSynonym(false);
	// List<String> values = result.getDescriptionsSerialized().getValues();
	// values.add("Die Pumas (Puma) sind eine Raubtiergattung aus der Familie der Katzen (Felidae). Sie gehören aufgrund ihrer relativ geringen Größe zur Unterfamilie der Kleinkatzen. In dieser Gattung werden zwei lebende Arten zusammengefasst:der Puma (Puma concolor) und der Jaguarundi oder Wieselkatze (Puma yaguarondi). Eine ausgestorbene Art der Gattung ist mit Puma pardoides aus dem frühen Pleistozän Eurasiens bekannt.Der Jaguarundi wurde früher in einer eigenen Gattung, Herpailurus, eingeordnet. Aufgrund molekulargenetischer Untersuchungen wurde aber eine enge Verwandtschaft dieser zwei Arten festgestellt, sodass jüngere Systematiken sie in einer gemeinsamen Gattung zusammenfassen.Das Fell dieser Tiere ist kurz und dicht und meist gelbgrau oder rötlich gefärbt, erwachsene Tiere sind im Gegensatz zu vielen anderen Katzenarten nicht gefleckt. Beide Arten sind in Amerika heimisch.DNA-Untersuchungen haben überdies ergeben, dass der nächste lebende Verwandte dieser Gattung der Gepard ist. Aufgrund seines eigentümlichen Körperbaus wurde dieser in einer eigenen Unterfamilie innerhalb der Katzen, Acinonychinae geführt, eine Einteilung, die nach Entdeckung dieser Gemeinsamkeit nicht mehr aufrechterhalten werden kann.");
	// values.add("DISPLAYTITLE Puma is a member of Felidae that contains the cougar (also known as the puma, among other names) and the jaguarundi, and may also include several poorly known Old World fossil representatives (for example, Puma pardoides, or \"Owen's panther,\" a large cougar-like cat of Eurasia's Pliocene).Hemmer, H. (1965). Studien an \"Panthera\" schaubi Viret aus dem Villafranchien von Saint-Vallier (Drôme). Neues Jahrbuch für Geologie und Paläontologie, Abhandlungen 122, 324–336.Hemmer, H., Kahlike, R.-D. & Vekua, A. K. (2004). The Old World puma Puma pardoides (Owen, 1846) (Carnivora: Felidae) in the Lower Villafranchian (Upper Pliocene) of Kvabebi (East Georgia, Transcaucasia) and its evolutionary and biogeographical significance. Neues Jahrbuch fur Geologie und Palaontologie, Abhandlungen 233, 197–233.");
	// values.add("Puma concolor (Linnaeus, 1771) – Cougar Puma pardoides (Owen, 1846) – Owen's Panther Puma yagouaroundi (Geoffroy, 1803) – Jaguarundi");
	/* "vernacularNamesSerialized":{"values":["en # pumas","de # Pumas"]}, */
	/* "higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae"}}, */

	// expected[1]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(125793753);
	result.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	result.setNubKey(2435098);
	result.setParentKey(137012570);
	result.setParent("Felidae");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setOrderKey(137012553);
	result.setFamilyKey(137012570);
	result.setGenusKey(125793753);
	result.setScientificName("Puma Jardine, 1834");
	result.setCanonicalName("Puma");
	result.setAuthorship("Jardine, 1834");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(3);
	result.setNumOccurrences(0);
	result.setTaxonID("1562431");
	// result.setLink("http://de.wikipedia.org/wiki/Pumas");
	description = new Description();
	result.getDescriptions().add(description);
	description.setDescription("Die Pumas (Puma) sind eine Raubtiergattung aus der Familie der Katzen (Felidae). Sie gehören aufgrund ihrer relativ geringen Größe zur Unterfamilie der Kleinkatzen. In dieser Gattung werden zwei lebende Arten zusammengefasst: der Puma (Puma concolor) und der Jaguarundi oder Wieselkatze (Puma yaguarondi). Eine ausgestorbene Art der Gattung ist mit Puma pardoides aus dem frühen Pleistozän Eurasiens bekannt.Der Jaguarundi wurde früher in einer eigenen Gattung, Herpailurus, eingeordnet. Aufgrund molekulargenetischer Untersuchungen wurde aber eine enge Verwandtschaft dieser zwei Arten festgestellt, sodass jüngere Systematiken sie in einer gemeinsamen Gattung zusammenfassen.Das Fell dieser Tiere ist kurz und dicht und meist gelbgrau oder rötlich gefärbt, erwachsene Tiere sind im Gegensatz zu vielen anderen Katzenarten nicht gefleckt. Beide Arten sind in Amerika heimisch.DNA-Untersuchungen haben überdies ergeben, dass der nächste lebende Verwandte dieser Gattung der Gepard ist. Aufgrund seines eigentümlichen Körperbaus wurde dieser in einer eigenen Unterfamilie innerhalb der Katzen, Acinonychinae geführt, eine Einteilung, die nach Entdeckung dieser Gemeinsamkeit nicht mehr aufrechterhalten werden kann.");

	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Pumas");
	vernacularName.setLanguage(Language.GERMAN);
	// result.setSynonym(false);
	/* "descriptionsSerialized":{"values":["Die Pumas (Puma) sind eine Raubtiergattung aus der Familie der Katzen (Felidae). Sie gehören aufgrund ihrer relativ geringen Größe zur Unterfamilie der Kleinkatzen. In dieser Gattung werden zwei lebende Arten zusammengefasst:der Puma (Puma concolor) und der Jaguarundi oder Wieselkatze (Puma yaguarondi).Eine ausgestorbene Art der Gattung ist mit Puma pardoides aus dem frühen Pleistozän Eurasiens bekannt.Der Jaguarundi wurde früher in einer eigenen Gattung, Herpailurus, eingeordnet. Aufgrund molekulargenetischer Untersuchungen wurde aber eine enge Verwandtschaft dieser zwei Arten festgestellt, sodass jüngere Systematiken sie in einer gemeinsamen Gattung zusammenfassen.Das Fell dieser Tiere ist kurz und dicht und meist gelbgrau oder rötlich gefärbt, erwachsene Tiere sind im Gegensatz zu vielen anderen Katzenarten nicht gefleckt. Beide Arten sind in Amerika heimisch.DNA-Untersuchungen haben überdies ergeben, dass der nächste lebende Verwandte dieser Gattung der Gepard ist. Aufgrund seines eigentümlichen Körperbaus wurde dieser in einer eigenen Unterfamilie innerhalb der Katzen, Acinonychinae geführt, eine Einteilung, die nach Entdeckung dieser Gemeinsamkeit nicht mehr aufrechterhalten werden kann."]}, */
	/* "vernacularNamesSerialized":{"values":["de # Pumas"]}, */
	/* "higherClassificationMap":{"131717571":"Carnivora","131717588":"Felidae"}}, */


	//expected[2]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(116855020);
	result.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	result.setNubKey(2435098);
	result.setParentKey(137066566);
	result.setParent("Felidae");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(116630539);
	result.setPhylumKey(116842680);
	result.setClassKey(116665331);
	result.setOrderKey(116854775);
	result.setFamilyKey(137066566);
	result.setGenusKey(116855020);
	result.setScientificName("Puma Jardine, 1834");
	result.setCanonicalName("Puma");
	result.setAuthorship("Jardine, 1834");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(6);
	result.setNumOccurrences(0);
	result.setTaxonID("9125620");
	// result.setLink("http://en.wikipedia.org/wiki/Puma_%28genus%29");
	description = new Description();
	result.getDescriptions().add(description);
	description.setDescription("DISPLAYTITLE Puma is a member of Felidae that contains the cougar (also known as the puma, among other names) and the jaguarundi, and may also include several poorly known Old World fossil representatives (for example, Puma pardoides, or \"Owen's panther,\" a large cougar-like cat of Eurasia's Pliocene).Hemmer, H. (1965). Studien an \"Panthera\" schaubi Viret aus dem Villafranchien von Saint-Vallier (Drôme). Neues Jahrbuch für Geologie und Paläontologie, Abhandlungen 122, 324–336.Hemmer, H., Kahlike, R.-D. & Vekua, A. K. (2004). The Old World puma Puma pardoides (Owen, 1846) (Carnivora: Felidae) in the Lower Villafranchian (Upper Pliocene) of Kvabebi (East Georgia, Transcaucasia) and its evolutionary and biogeographical significance. Neues Jahrbuch fur Geologie und Palaontologie, Abhandlungen 233, 197–233.");
	description = new Description();
	result.getDescriptions().add(description);
	//	description.setDescription("Puma concolor (Linnaeus, 1771) – Cougar Puma pardoides (Owen, 1846) – Owen's Panther Puma yagouaroundi (Geoffroy, 1803) – Jaguarundi");
	description.setDescription("Puma concolor (Linnaeus, 1771) – Cougar Puma pardoides (Owen, 1846) – Owen's Panther Puma yagouaroundi (Geoffroy, 1803) – Jaguarundi");
	// result.setSynonym(false);
	/* "descriptionsSerialized":{"values":["DISPLAYTITLE Puma is a member of Felidae that contains the cougar (also known as the puma, among other names) and the jaguarundi, and may also include several poorly known Old World fossil representatives (for example, Puma pardoides, or \"Owen's panther,\" a large cougar-like cat of Eurasia's Pleistocene).Hemmer, H. (1965). Studien an \"Panthera\" schaubi Viret aus dem Villafranchien von Saint-Vallier (Drôme). Neues Jahrbuch für Geologie und Paläontologie, Abhandlungen 122, 324–336.Hemmer, H., Kahlike, R.-D. & Vekua, A. K. (2004). The Old World puma Puma pardoides (Owen, 1846) (Carnivora: Felidae) in the Lower Villafranchian (Upper Pliocene) of Kvabebi (East Georgia, Transcaucasia) and its evolutionary and biogeographical significance. Neues Jahrbuch fur Geologie und Palaontologie, Abhandlungen 233, 197–233.","Puma concolor (Linnaeus, 1771) – Cougar Puma pardoides (Owen, 1846) – Owen's Panther Puma yagouaroundi (Geoffroy, 1803) – Jaguarundi"]}, */
	/* "higherClassificationMap":{"116630539":"Animalia","116842680":"Chordata","116665331":"Mammalia","110609337":"Carnivora","116632354":"Felidae"}}, */

	//expected[3]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(107363530);
	result.setDatasetKey(UUID.fromString("9ca92552-f23a-41a8-a140-01abaa31c931"));
	result.setNubKey(2435098);
	result.setParentKey(107363517);
	result.setParent("Felinae");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(107264513);
	result.setPhylumKey(107216119);
	result.setClassKey(107239517);
	result.setOrderKey(107240212);
	result.setFamilyKey(107240258);
	result.setGenusKey(107363530);
	result.setScientificName("Puma Jardine, 1834");
	result.setCanonicalName("Puma");
	result.setAuthorship("Jardine, 1834");
	result.setNameType(NameType.WELLFORMED);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(16);
	result.setNumOccurrences(0);
	result.setTaxonID("552375");

	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("pumas");
	vernacularName.setLanguage(Language.ENGLISH);
	// result.setSynonym(false);
	/* "vernacularNamesSerialized":{"values":["en # pumas"]}, */
	/* "higherClassificationMap":{"107264513":"Animalia","107216119":"Chordata","107239517":"Mammalia","107240212":"Carnivora","107240258":"Felidae","107363517":"Felinae"}}, */

	//expected[X]:
	/*
	result = new NameUsageSearchResult();
	// results.add(result);
	result.setKey(134901627);
	result.setDatasetKey(UUID.fromString("e632b198-5b2f-47ee-b7a6-6531ea435fa3"));
	result.setNubKey(2435098);
	result.setGenus("Puma");
	result.setGenusKey(134901627);
	result.setScientificName("Puma");
	result.setCanonicalName("Puma");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(17);
	result.setNumOccurrences(0);
	// result.setSynonym(false);
	*/

	//expected[4]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(127835041);
	result.setDatasetKey(UUID.fromString("7ddf754f-d193-4cc9-b351-99906754a03b"));
	result.setNubKey(2435098);
	result.setParentKey(127796184);
	result.setParent("Felidae");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(127795487);
	result.setPhylumKey(127795699);
	result.setClassKey(127795847);
	result.setOrderKey(127796172);
	result.setFamilyKey(127796184);
	result.setGenusKey(127835041);
	result.setScientificName("Puma");
	result.setCanonicalName("Puma");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(16);
	result.setNumOccurrences(0);
	result.setTaxonID("14872053");

	// result.setSynonym(false);
	/* "higherClassificationMap":{"127795487":"Animalia","127795699":"Chordata","127795847":"Mammalia","127796172":"Carnivora","127796184":"Felidae"}}, */

	//expected[5]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(124846146);
	result.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	result.setNubKey(2435098);
	result.setParentKey(124846134);
	result.setParent("Felidae");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(124836057);
	result.setPhylumKey(124839309);
	result.setClassKey(124846085);
	result.setOrderKey(124846108);
	result.setFamilyKey(124846134);
	result.setGenusKey(124846146);
	result.setScientificName("Puma");
	result.setCanonicalName("Puma");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(5);
	result.setNumOccurrences(0);
	// result.setSynonym(false);
	/* "higherClassificationMap":{"124836057":"Animalia","124839309":"Chordata","124846085":"Mammalia","124846108":"Carnivora","124846134":"Felidae"}}, */

	//expected[6]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(105976359);
	result.setDatasetKey(UUID.fromString("fab88965-e69d-4491-a04d-e3198b626e52"));
	result.setNubKey(2435098);
	result.setParentKey(106154544);
	result.setParent("Felinae");
	result.setKingdom("Metazoa");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(106148414);
	result.setPhylumKey(106522535);
	result.setClassKey(106223020);
	result.setOrderKey(106151875);
	result.setFamilyKey(106661479);
	result.setGenusKey(105976359);
	result.setScientificName("Puma");
	result.setCanonicalName("Puma");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(3);
	result.setNumOccurrences(0);
	result.setTaxonID("146712");
	// result.setSynonym(false);
	/* "higherClassificationMap":{"106148414":"Metazoa","106522535":"Chordata","106223020":"Mammalia","106151875":"Carnivora","106661479":"Felidae","106154544":"Felinae"}}, */

	//expected[7]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(137081251);
	result.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	result.setNubKey(2435098);
	result.setGenus("Puma");
	result.setGenusKey(137081251);
	result.setScientificName("Puma");
	result.setCanonicalName("Puma");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(3);
	result.setNumOccurrences(0);

	// expected[8]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(107729381);
	result.setDatasetKey(UUID.fromString("c33ce2f2-c3cc-43a5-a380-fe4526d63650"));
	result.setNubKey(2435098);
	result.setParentKey(107679351);
	result.setParent("Felinae");
	result.setKingdom("Metazoa");
	result.setPhylum("Chordata");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(107702209);
	result.setPhylumKey(107661531);
	result.setOrderKey(107658447);
	result.setFamilyKey(107679348);
	result.setGenusKey(107729381);
	result.setScientificName("Puma");
	result.setCanonicalName("Puma");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(2);
	result.setNumOccurrences(0);
	result.setTaxonID("41,073");
	// result.setSynonym(false);
	/* "higherClassificationMap":{"107702209":"Metazoa","107661531":"Chordata","107658447":"Carnivora","107679348":"Felidae","107679351":"Felinae"}}, */

	//expected[Y]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(140573060);
	result.setDatasetKey(UUID.fromString("cc056915-475e-4179-b12e-849acebc6ed9"));
	result.setNubKey(2435098);
	result.setParentKey(140573056);
	result.setParent("Felidae");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(140572660);
	result.setPhylumKey(140572661);
	result.setClassKey(140573043);
	result.setOrderKey(140573052);
	result.setFamilyKey(140573056);
	result.setGenusKey(140573060);
	result.setScientificName("Puma");
	result.setCanonicalName("Puma");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(2);
	result.setNumOccurrences(0);
	// result.setSynonym(false);

	// actual[Z]:
	result = new NameUsageSearchResult();
	// results.add(result);
	result.setKey(131076089);
	result.setDatasetKey(UUID.fromString("e632b198-5b2f-47ee-b7a6-6531ea435fa3"));
	result.setNubKey(2435098);
	result.setParentKey(128015003);
	result.setParent("Viruses");
	result.setKingdom("Viruses");
	result.setGenus("Puma");
	result.setKingdomKey(128015003);
	result.setGenusKey(131076089);
	result.setScientificName("\"Puma \"");
	result.setCanonicalName("Puma");
	result.setAuthorship("");
	result.setNameType(NameType.DOUBTFUL);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(0);
	result.setNumOccurrences(0);
	result.setTaxonID("27783136");
	// result.setSynonym(false);
	/* "higherClassificationMap":{"128015003":"Viruses"}}, */

	// actual[10]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(140567650);
	result.setDatasetKey(UUID.fromString("3408ce35-c482-4df0-9644-b8d4e6318f99"));
	result.setNubKey(2435098);
	result.setParentKey(140567647);
	result.setParent("Felidae");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(140567512);
	result.setPhylumKey(140567513);
	result.setClassKey(140567640);
	result.setOrderKey(140567646);
	result.setFamilyKey(140567647);
	result.setGenusKey(140567650);
	result.setScientificName("Puma");
	result.setCanonicalName("Puma");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(1);
	result.setNumOccurrences(0);
	// result.setSynonym(false);


	/*
actual[14]:
NameUsageSearchResult{key=131076089, datasetKey=e632b198-5b2f-47ee-b7a6-6531ea435fa3, nubKey=2435098, parentKey=128015003, parent='Viruses', acceptedKey=null, accepted='null', basionymKey=null, basionym='null', isSynonym=false, kingdom='Viruses', phylum='null', clazz='null', order='null', family='null', genus='Puma', subgenus='null', species='null', kingdomKey=128015003, phylumKey=null, classKey=null, orderKey=null, familyKey=null, genusKey=131076089, subgenusKey=null, speciesKey=null, scientificName='"Puma "', canonicalName='Puma', authorship='', publishedIn='null', accordingTo='null', nameType=DOUBTFUL, taxonomicStatus=null, nomenclaturalStatus=[], rank=GENUS, origin=null, numDescendants=0, numOccurrences=0, taxonID='27783136', extinct=null, marine=null, threatStatuses=[], descriptions=[], vernacularNames=[]}
	*/

	// actual[11]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(140565801);
	result.setDatasetKey(UUID.fromString("29886bf9-62d2-4208-87f6-109dd1972607"));
	result.setNubKey(2435098);
	result.setParentKey(140565798);
	result.setParent("Felidae");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(140565407);
	result.setPhylumKey(140565408);
	result.setClassKey(140565791);
	result.setOrderKey(140565795);
	result.setFamilyKey(140565798);
	result.setGenusKey(140565801);
	result.setScientificName("Puma");
	result.setCanonicalName("Puma");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(1);
	result.setNumOccurrences(0);
	// result.setSynonym(false);

	// actual[12]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(140575845);
	result.setDatasetKey(UUID.fromString("4c881adf-82dc-4f31-aea9-20887ce7d7c9"));
	result.setNubKey(2435098);
	result.setParentKey(140575843);
	result.setParent("Felidae");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(140575610);
	result.setPhylumKey(140575611);
	result.setClassKey(140575841);
	result.setOrderKey(140575842);
	result.setFamilyKey(140575843);
	result.setGenusKey(140575845);
	result.setScientificName("Puma");
	result.setCanonicalName("Puma");
	result.setAuthorship("");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(1);
	result.setNumOccurrences(0);
	// result.setSynonym(false);

	/*
actual[15]:
NameUsageSearchResult{key=103371272, datasetKey=672aca30-f1b5-43d3-8a2b-c1606125fa1b, nubKey=2435098, parentKey=103371072, parent='Felidae', acceptedKey=null, accepted='null', basionymKey=null, basionym='null', isSynonym=false, kingdom='Animalia', phylum='null', clazz='null', order='Carnivora', family='Felidae', genus='Puma', subgenus='null', species='null', kingdomKey=134536083, phylumKey=null, classKey=null, orderKey=103371070, familyKey=103371072, genusKey=103371272, subgenusKey=null, speciesKey=null, scientificName='Puma Jardine, 1834', canonicalName='Puma', authorship='Jardine, 1834', publishedIn='Natur. Libr. p. 266-267', accordingTo='null', nameType=WELLFORMED, taxonomicStatus=null, nomenclaturalStatus=[], rank=GENUS, origin=null, numDescendants=16, numOccurrences=0, taxonID='14000203', extinct=null, marine=null, threatStatuses=[], descriptions=[], vernacularNames=[]}
	*/
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(103371272);
	result.setDatasetKey(UUID.fromString("672aca30-f1b5-43d3-8a2b-c1606125fa1b"));
	result.setNubKey(2435098);
	result.setParentKey(103371072);
	result.setParent("Felidae");
	result.setKingdom("Animalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(134536083);
	result.setOrderKey(103371070);
	result.setFamilyKey(103371072);
	result.setGenusKey(103371272);
	result.setScientificName("Puma Jardine, 1834");
	result.setCanonicalName("Puma");
	result.setAuthorship("Jardine, 1834");
	result.setPublishedIn("Natur. Libr. p. 266-267");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(16);
	result.setNumOccurrences(0);
	result.setTaxonID("14000203");
	// result.setSynonym(false);
	/* "higherClassificationMap":{"115140331":"Animalia","103371070":"Carnivora","103371072":"Felidae"}}, */

	/*
actual[16]:
NameUsageSearchResult{key=102497493, datasetKey=0938172b-2086-439c-a1dd-c21cb0109ed5, nubKey=2435098, parentKey=101148668, parent='Felidae', acceptedKey=null, accepted='null', basionymKey=null, basionym='null', isSynonym=false, kingdom='Animalia', phylum='Chordata', clazz='Mammalia', order='Carnivora', family='Felidae', genus='Puma', subgenus='null', species='null', kingdomKey=101719444, phylumKey=102545028, classKey=102402290, orderKey=102303328, familyKey=101148668, genusKey=102497493, subgenusKey=null, speciesKey=null, scientificName='Puma Jardine, 1834', canonicalName='Puma', authorship='Jardine, 1834', publishedIn='Nat. Library (Mamm. ), 4, 266.', accordingTo='SN2000/Wilson & Reeder, 1993; Nomenclator Zoologicus', nameType=WELLFORMED, taxonomicStatus=ACCEPTED, nomenclaturalStatus=[], rank=GENUS, origin=null, numDescendants=2, numOccurrences=0, taxonID='1405111', extinct=false, marine=false, threatStatuses=[], descriptions=[], vernacularNames=[]}
	*/

	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(102497493);
	result.setDatasetKey(UUID.fromString("0938172b-2086-439c-a1dd-c21cb0109ed5"));
	result.setNubKey(2435098);
	result.setParentKey(101148668);
	result.setParent("Felidae");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(101719444);
	result.setPhylumKey(102545028);
	result.setClassKey(102402290);
	result.setOrderKey(102303328);
	result.setFamilyKey(101148668);
	result.setGenusKey(102497493);
	result.setScientificName("Puma Jardine, 1834");
	result.setCanonicalName("Puma");
	result.setAuthorship("Jardine, 1834");
	result.setPublishedIn("Nat. Library (Mamm. ), 4, 266.");
	result.setAccordingTo("SN2000/Wilson & Reeder, 1993; Nomenclator Zoologicus");
	result.setNameType(NameType.WELLFORMED);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(2);
	result.setNumOccurrences(0);
	result.setTaxonID("1405111");
	result.setExtinct(false);
	// result.setMarine(false);
	// result.setSynonym(false);
	/* "higherClassificationMap":{"101719444":"Animalia","102545028":"Chordata","102402290":"Mammalia","102303328":"Carnivora","101148668":"Felidae"}}, */

	/*
actual[17]:
NameUsageSearchResult{key=128044165, datasetKey=e632b198-5b2f-47ee-b7a6-6531ea435fa3, nubKey=2435098, parentKey=128015003, parent='Viruses', acceptedKey=null, accepted='null', basionymKey=null, basionym='null', isSynonym=false, kingdom='Viruses', phylum='null', clazz='null', order='null', family='null', genus='puma', subgenus='null', species='null', kingdomKey=128015003, phylumKey=null, classKey=null, orderKey=null, familyKey=null, genusKey=128044165, subgenusKey=null, speciesKey=null, scientificName='puma', canonicalName='null', authorship='', publishedIn='null', accordingTo='null', nameType=SCINAME, taxonomicStatus=null, nomenclaturalStatus=[], rank=GENUS, origin=null, numDescendants=0, numOccurrences=0, taxonID='34428', extinct=null, marine=null, threatStatuses=[], descriptions=[], vernacularNames=[]}
	*/
	result = new NameUsageSearchResult();
	// results.add(result);
	result.setKey(128044165);
	result.setDatasetKey(UUID.fromString("e632b198-5b2f-47ee-b7a6-6531ea435fa3"));
	result.setNubKey(2435098);
	result.setParentKey(128015003);
	result.setParent("Viruses");
	result.setKingdom("Viruses");
	result.setGenus("puma");
	result.setKingdomKey(128015003);
	result.setGenusKey(128044165);
	result.setScientificName("puma");
	result.setAuthorship("");
	result.setNameType(NameType.SCINAME);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(0);
	result.setNumOccurrences(0);
	result.setTaxonID("34428");
	// result.setSynonym(false);
	/* "higherClassificationMap":{"128015003":"Viruses"}}, */

	/*
actual[18]:
NameUsageSearchResult{key=102350511, datasetKey=0938172b-2086-439c-a1dd-c21cb0109ed5, nubKey=3221678, parentKey=101584795, parent='Rhodospirillaceae', acceptedKey=null, accepted='null', basionymKey=null, basionym='null', isSynonym=false, kingdom='Bacteria', phylum='Proteobacteria', clazz='Alphaproteobacteria', order='Rhodospirillales', family='Rhodospirillaceae', genus='Inquilinus', subgenus='null', species='null', kingdomKey=102392280, phylumKey=102545096, classKey=101831183, orderKey=102317164, familyKey=101584795, genusKey=102350511, subgenusKey=null, speciesKey=null, scientificName='Inquilinus Coenye, Goris, Spilker, Vandamme & LiPuma, 2002', canonicalName='Inquilinus', authorship='', publishedIn='null', accordingTo='CoL2006/BIOS; Euzéby, 2008; WoRMS (Mar 2013)', nameType=SCINAME, taxonomicStatus=ACCEPTED, nomenclaturalStatus=[], rank=GENUS, origin=null, numDescendants=1, numOccurrences=0, taxonID='1258334', extinct=false, marine=null, threatStatuses=[], descriptions=[], vernacularNames=[]}
	*/

	// actual[16]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(102350511);
	result.setDatasetKey(UUID.fromString("0938172b-2086-439c-a1dd-c21cb0109ed5"));
	result.setNubKey(3221678);
	result.setParentKey(101584795);
	result.setParent("Rhodospirillaceae");
	result.setKingdom("Bacteria");
	result.setPhylum("Proteobacteria");
	result.setClazz("Alphaproteobacteria");
	result.setOrder("Rhodospirillales");
	result.setFamily("Rhodospirillaceae");
	result.setGenus("Inquilinus");
	result.setKingdomKey(102392280);
	result.setPhylumKey(102545096);
	result.setClassKey(101831183);
	result.setOrderKey(102317164);
	result.setFamilyKey(101584795);
	result.setGenusKey(102350511);
	result.setScientificName("Inquilinus Coenye, Goris, Spilker, Vandamme & LiPuma, 2002");
	result.setCanonicalName("Inquilinus");
	result.setAuthorship("");
	result.setAccordingTo("CoL2006/BIOS; Euzéby, 2008; WoRMS (Mar 2013)");
	result.setNameType(NameType.SCINAME);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(1);
	result.setNumOccurrences(0);
	result.setTaxonID("1258334");
	result.setExtinct(false);
	// result.setSynonym(false);
	/* "higherClassificationMap":{"102392280":"Bacteria","102545096":"Proteobacteria","101831183":"Alphaproteobacteria","102317164":"Rhodospirillales","101584795":"Rhodospirillaceae"}}, */


	/*
actual[19]:
NameUsageSearchResult{key=107363528, datasetKey=9ca92552-f23a-41a8-a140-01abaa31c931, nubKey=2435077, parentKey=107363517, parent='Felinae', acceptedKey=107363530, accepted='Puma Jardine, 1834', basionymKey=null, basionym='null', isSynonym=true, kingdom='Animalia', phylum='Chordata', clazz='Mammalia', order='Carnivora', family='Felidae', genus='Puma', subgenus='null', species='null', kingdomKey=107264513, phylumKey=107216119, classKey=107239517, orderKey=107240212, familyKey=107240258, genusKey=107363530, subgenusKey=null, speciesKey=null, scientificName='Herpailurus Severtzov, 1858', canonicalName='Herpailurus', authorship='Severtzov, 1858', publishedIn='null', accordingTo='null', nameType=WELLFORMED, taxonomicStatus=SYNONYM, nomenclaturalStatus=[], rank=GENUS, origin=null, numDescendants=0, numOccurrences=0, taxonID='552373', extinct=null, marine=null, threatStatuses=[], descriptions=[], vernacularNames=[VernacularName{vernacularName=jaguarundis, language=ENGLISH, lifeStage=null, sex=null, country=null, area=null, source=null, sourceTaxonKey=null, preferred=null, plural=null}]}
	*/
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(107363528);
	result.setDatasetKey(UUID.fromString("9ca92552-f23a-41a8-a140-01abaa31c931"));
	result.setNubKey(2435077);
	result.setParentKey(107363517);
	result.setParent("Felinae");
	result.setAcceptedKey(107363530);
	result.setAccepted("Puma Jardine, 1834");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(107264513);
	result.setPhylumKey(107216119);
	result.setClassKey(107239517);
	result.setOrderKey(107240212);
	result.setFamilyKey(107240258);
	result.setGenusKey(107363530);
	result.setScientificName("Herpailurus Severtzov, 1858");
	result.setCanonicalName("Herpailurus");
	result.setAuthorship("Severtzov, 1858");
	result.setNameType(NameType.WELLFORMED);
	result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(0);
	result.setNumOccurrences(0);
	result.setTaxonID("552373");
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("jaguarundis");
	vernacularName.setLanguage(Language.ENGLISH);
	result.setSynonym(true);
	/* "vernacularNamesSerialized":{"values":["en # jaguarundis"]}, */
	/* "higherClassificationMap":{"107264513":"Animalia","107216119":"Chordata","107239517":"Mammalia","107240212":"Carnivora","107240258":"Felidae","107363530":"Puma","107363517":"Felinae"}}, */

	// actual[16]:
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(2435077);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(2435077);
	result.setParentKey(9703);
	result.setParent("Felidae");
	result.setAcceptedKey(2435098);
	result.setAccepted("Puma Jardine, 1834");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setScientificName("Herpailurus Severtzov, 1858");
	result.setCanonicalName("Herpailurus");
	result.setAuthorship("Severtzov, 1858");
	result.setPublishedIn("Rev. Mag. Zool. , (2) 10, 385.");
	result.setAccordingTo("Integrated Taxonomic Information System");
	result.setNameType(NameType.WELLFORMED);
	result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(0);
	result.setNumOccurrences(0);
	result.setTaxonID("107363528");
	result.setExtinct(false);
	// result.setMarine(false);

	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("jaguarundis");
	vernacularName.setLanguage(Language.ENGLISH);
	result.setSynonym(true);
	/* "vernacularNamesSerialized":{"values":["en # jaguarundis"]},"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma"}}]  */


	/*
actual[17]
NameUsageSearchResult{key=100772213, datasetKey=0938172b-2086-439c-a1dd-c21cb0109ed5, nubKey=2435077, parentKey=101148668, parent='Felidae', acceptedKey=102497493, accepted='Puma Jardine, 1834', basionymKey=null, basionym='null', isSynonym=true, kingdom='Animalia', phylum='Chordata', clazz='Mammalia', order='Carnivora', family='Felidae', genus='Puma', subgenus='null', species='null', kingdomKey=101719444, phylumKey=102545028, classKey=102402290, orderKey=102303328, familyKey=101148668, genusKey=102497493, subgenusKey=null, speciesKey=null, scientificName='Herpailurus Sewertzow, 1858', canonicalName='Herpailurus', authorship='Sewertzow, 1858', publishedIn='Rev. Mag. Zool. , (2) 10, 385.', accordingTo='SN2000/Wilson & Reeder, 1993; Nomenclator Zoologicus', nameType=WELLFORMED, taxonomicStatus=SYNONYM, nomenclaturalStatus=[], rank=GENUS, origin=null, numDescendants=0, numOccurrences=0, taxonID='1015084', extinct=false, marine=false, threatStatuses=[], descriptions=[], vernacularNames=[]}
	 */
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(100772213);
	result.setDatasetKey(UUID.fromString("0938172b-2086-439c-a1dd-c21cb0109ed5"));
	result.setNubKey(2435077);
	result.setParentKey(101148668);
	result.setParent("Felidae");
	result.setAcceptedKey(102497493);
	result.setAccepted("Puma Jardine, 1834");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setKingdomKey(101719444);
	result.setPhylumKey(102545028);
	result.setClassKey(102402290);
	result.setOrderKey(102303328);
	result.setFamilyKey(101148668);
	result.setGenusKey(102497493);
	result.setScientificName("Herpailurus Sewertzow, 1858");
	result.setCanonicalName("Herpailurus");
	result.setAuthorship("Sewertzow, 1858");
	result.setPublishedIn("Rev. Mag. Zool. , (2) 10, 385.");
	result.setAccordingTo("SN2000/Wilson & Reeder, 1993; Nomenclator Zoologicus");
	result.setNameType(NameType.WELLFORMED);
	result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(0);
	result.setNumOccurrences(0);
	result.setTaxonID("1015084");
	result.setExtinct(false);
	// result.setMarine(false);
	result.setSynonym(true);

	/*
actual[18]
NameUsageSearchResult{key=2435269, datasetKey=d7dddbf4-2cf0-4f39-9b2a-bb099caae36c, nubKey=2435269, parentKey=9703, parent='Felidae', acceptedKey=null, accepted='null', basionymKey=null, basionym='null', isSynonym=false, kingdom='Animalia', phylum='Chordata', clazz='Mammalia', order='Carnivora', family='Felidae', genus='Acinonyx', subgenus='null', species='null', kingdomKey=1, phylumKey=44, classKey=359, orderKey=732, familyKey=9703, genusKey=2435269, subgenusKey=null, speciesKey=null, scientificName='Acinonyx Brookes, 1828', canonicalName='Acinonyx', authorship='Brookes, 1828', publishedIn='Cat. Anat. Zool. Mus. Joshua Brookes, London p. 16, 33', accordingTo='The Catalogue of Life, 3rd January 2011', nameType=WELLFORMED, taxonomicStatus=ACCEPTED, nomenclaturalStatus=[], rank=GENUS, origin=null, numDescendants=10, numOccurrences=0, taxonID='121039393', extinct=false, marine=false, threatStatuses=[], descriptions=[Description{type=null, language=null, description=Acinonyx aicha Geraads, 1997 † Acinonyx intermedius Thenius, 1954 † Acinonyx jubatus Schreber, 1775 – cheetah "Acinonyx kurteni" Christiansen and Mazák, 2008Christiansen, P.; Mazak, J. H. (2009): A primitive Late Pliocene cheetah, and evolution of the cheetah lineage, Proceedings of the National Academy of Sciences, 106(2): pp. 512–5 † (no longer a valid species, probably fraudulent)Knevitt, Oliver (January 9, 2011): 5 Greatest Palaeontology Fakes Of All Time #5: The Linxia Cheetah. Science 2.0Mazák JH (2012): Retraction for Christiansen and Mazák, A primitive Late Pliocene cheetah, and evolution of the cheetah lineage, Proc Natl Acad Sci USA, 109(37): pp. 15072 Acinonyx pardinensis Croizet e Joubert, 1828 - Giant cheetah †, source=null, sourceTaxonKey=null, creator=null, contributor=null, license=null}, Description{type=null, language=null, description=Acinonyx is a genus of mammals from the family Felidae. It is currently distributed in Africa and Asia, but at one time was also present in Europe. The cheetah is the only living species in the genus. Wozencraft (1993) put the genus Acinonyx in their own monophyletic subfamily, Acinonychinae. Salles (1992), Johnson & O'Brien (1997), Bininda-Emonds et al. (1999), and Mattern and McLennan (2000) consider Acinonyx, Puma concolor, and Puma (= Herpailurus) yagouaroundi as representatives of closely related sister groups., source=null, sourceTaxonKey=null, creator=null, contributor=null, license=null}], vernacularNames=[]}
	 */
	result = new NameUsageSearchResult();
	results.add(result);
	result.setKey(2435269);
	result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(2435269);
	result.setParentKey(9703);
	result.setParent("Felidae");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Acinonyx");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435269);
	result.setScientificName("Acinonyx Brookes, 1828");
	result.setCanonicalName("Acinonyx");
	result.setAuthorship("Brookes, 1828");
	result.setPublishedIn("Cat. Anat. Zool. Mus. Joshua Brookes, London p. 16, 33");
	result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	result.setNameType(NameType.WELLFORMED);
	result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(10);
	result.setNumOccurrences(0);
	result.setTaxonID("121039393");
	result.setExtinct(false);
	// result.setMarine(false);
	description = new Description();
	result.getDescriptions().add(description);
	description.setDescription("Acinonyx aicha Geraads, 1997 † Acinonyx intermedius Thenius, 1954 † Acinonyx jubatus Schreber, 1775 – cheetah \"Acinonyx kurteni\" Christiansen and Mazák, 2008Christiansen, P.; Mazak, J. H. (2009): A primitive Late Pliocene cheetah, and evolution of the cheetah lineage, Proceedings of the National Academy of Sciences, 106(2): pp. 512–5 † (no longer a valid species, probably fraudulent)Knevitt, Oliver (January 9, 2011): 5 Greatest Palaeontology Fakes Of All Time #5: The Linxia Cheetah. Science 2.0Mazák JH (2012): Retraction for Christiansen and Mazák, A primitive Late Pliocene cheetah, and evolution of the cheetah lineage, Proc Natl Acad Sci USA, 109(37): pp. 15072 Acinonyx pardinensis Croizet e Joubert, 1828 - Giant cheetah †");
	description = new Description();
	result.getDescriptions().add(description);
	description.setDescription("Acinonyx is a genus of mammals from the family Felidae. It is currently distributed in Africa and Asia, but at one time was also present in Europe. The cheetah is the only living species in the genus. Wozencraft (1993) put the genus Acinonyx in their own monophyletic subfamily, Acinonychinae. Salles (1992), Johnson & O'Brien (1997), Bininda-Emonds et al. (1999), and Mattern and McLennan (2000) consider Acinonyx, Puma concolor, and Puma (= Herpailurus) yagouaroundi as representatives of closely related sister groups.");
	// result.setSynonym(false);




	/*
actual[19]
NameUsageSearchResult{key=125812601, datasetKey=16c3f9cb-4b19-4553-ac8e-ebb90003aa02, nubKey=4833952, parentKey=137012570, parent='Felidae', acceptedKey=null, accepted='null', basionymKey=null, basionym='null', isSynonym=false, kingdom='null', phylum='null', clazz='null', order='Carnivora', family='Felidae', genus='Miracinonyx', subgenus='null', species='null', kingdomKey=null, phylumKey=null, classKey=null, orderKey=137012553, familyKey=137012570, genusKey=125812601, subgenusKey=null, speciesKey=null, scientificName='Miracinonyx Adams, 1979', canonicalName='Miracinonyx', authorship='Adams, 1979', publishedIn='null', accordingTo='null', nameType=WELLFORMED, taxonomicStatus=null, nomenclaturalStatus=[], rank=GENUS, origin=null, numDescendants=0, numOccurrences=0, taxonID='5313509', extinct=true, marine=null, threatStatuses=[], descriptions=[Description{type=null, language=null, description=Die Amerikanischen Geparde wurden zu Beginn als frühe Vertreter der Pumas angesehen, in den 1970er Jahren aber als nahe Verwandte des Gepards neu klassifiziert.Adams, Daniel B. (14. September 1979): The Cheetah: Native American, Science, 205(4411): pp. 1155–1158 Diese Theorie geht davon aus, dass sich die Vorfahren des Gepards aus der Linie der Pumas auf dem amerikanischen Kontinent (die Neue Welt) entwickelten und über die Beringbrücke wieder in die Alte Welt (vor allem Asien und Afrika) wanderten.Johnson, W.E., Eizirik, E., Pecon-Slattery, J., Murphy, W.J., Antunes, A., Teeling, E. & O'Brien, S.J. (2006-01-06): The Late Miocene radiation of modern Felidae: A genetic assessment, Science, 311(5757): pp. 73–77Andere Forschungen, wie etwa die von Ross Barnett, welche die Untersuchung der mitochondrialen DNA der Knochenfunde sowie eine neue Analyse der Morphologie mit einschloss, sehen die Amerikanischen Geparde als nahe Verwandte des Pumas, die Gepard-ähnliche Merkmale aufgrund von konvergenter Evolution entwickelten.Barnett, Ross (9. August 2005): Evolution of the extinct Sabretooths and the American cheetah-like cat, Current Biology, 15(15): pp. R589–R590 Ein weiterer naher Verwandter ist dabei der Jaguarundi, der jedoch eher kleinkatzenähnlich ist.Der vermutete amerikanische Ursprung der Geparde ist daher als äquivokal anzusehen, denn es wird angenommen, dass sie sich sowohl in der Alten Welt als auch in der Neuen Welt (Amerika) aus Puma-ähnlichen Vorfahren entwickelt haben. Dabei sollen sich Puma und Miracinonyx trumani vor etwa 3 Millionen Jahren von einer gleichen Vorfahrenlinie evolutionär abgespalten haben, die genaue Einordnung der zweiten Art Miracinonyx inexpectatus ist indes nicht endgültig geklärt, obwohl es sich wahrscheinlich um eine primitivere Art als Miracinonyx trumani handelt.Haaramo, Mikko (2005-11-15): Mikko's Phylogeny Archive - Felidae: Felinae – small catsVom Amerikanischen Gepard wurden bislang zwei Arten beschrieben: Miracinonyx trumani und Miracynonix inexpectatus. Teilweise wird auch eine dritte Art Miracinonyx studeri angeführt, diese gilt jedoch als jüngeres Synonym für Miracynonix trumani. Beide Arten waren dem heutigen Gepard sehr ähnlich, mit verkürzten runden Schädeln und vergrößerten Nasengängen für einen größeren Atemdurchsatz sowie speziell für schnelles Rennen proportionierte extrem lange Gliedmaßen und einem langen Schwanz. Ebenso soll das Gebiss gegenüber anderen Katzen stark verkleinert und relativ schwach gewesen sein. Dennoch wurden die Ähnlichkeiten nicht durch einen direkten gemeinsamen Vorfahren vererbt, sondern waren das Ergebnis von entweder paralleler oder konvergenter Evolution.Durch genetische und immunologische Untersuchungen konnte ermittelt werden, dass die heutigen Geparde wahrscheinlich alle von einer sehr kleinen Stammgruppe abstammen (→ genetischer Flaschenhals), die vor etwa 10.000 Jahren gelebt hat.M. Menotti-Raymond, S. J. O'Brien: Dating the genetic bottleneck of the African cheetah. In: Proceedings of the National Academy of Sciences of the United States of America. 1993; 90(8): 3172-3176Modellberechnung der genetischen Drift Damals starb der Amerikanische Gepard aus, und der gewöhnliche Gepard in Afrika und Asien entging diesem Schicksal offenbar nur knapp. Er breitete sich jedoch in den Savannen Afrikas und Asiens wieder aus und konnte daher bis in unsere Zeit überleben. Diese Untersuchung genießt in Fachkreisen hohes Ansehen und wird mittlerweile als klassisches Beispiel in der Populationsgenetik benutzt.Die genauen Gründe, die zum Aussterben der Amerikanischen Geparde geführt haben oder geführt haben könnten, sind trotz aller Forschung dazu bislang nicht abschließend aufgeklärt. Einige Experten vermuten zusätzlich zum genannten genetischen Flaschenhals durchaus auch weitere Einflüsse wie einsetzende Klimaveränderungen und eine verstärkte Nahrungskonkurrenz. Dafür spricht etwa der Umstand, dass etwa 10.000 Jahren vor unserer Zeitrechnung etliche andere Großtierarten Amerikas ausgestorben sind, wie etwa der Amerikanische Löwe., source=null, sourceTaxonKey=null, creator=null, contributor=null, license=null}, Description{type=null, language=null, description=Die Amerikanischen Geparden (Miracinonyx) sind eine ausgestorbene Gattung aus der Familie der Katzen (Felidae), die vom späten Pliozän bis zum späten Pleistozän (Jungpleistozän) 1.800.000 bis 11.000 Jahre vor unserer Zeitrechnung endemisch in Nordamerika lebte.PaleoBiology Database: Miracinonyx, basic info Es gab mindestens zwei Arten dieser Gattung, die von der Morphologie her dem heutigen Gepard ähnlich waren. Sie sind nur durch Skelett-Bruchstücke bekannt., source=null, sourceTaxonKey=null, creator=null, contributor=null, license=null}, Description{type=null, language=null, description=Miracinonyx trumani war den echten Geparden morphologisch am ähnlichsten. Er lebte in der Prärie sowie den weiten Ebenen des westlichen Nordamerikas und jagte sehr wahrscheinlich Huftiere der Great Plains, wie den heute noch lebenden Gabelbock. Es besteht Grund zur Annahme, dass seine maximal erreichbare Geschwindigkeit jener der heutigen Geparden nur wenig nachstand und mindestens um die 100 km/h gelegen haben dürfte.Die Nachstellung durch den Miracinonyx trumani könnte der Grund gewesen sein, weshalb die Gabelböcke die Fähigkeit entwickelten derart schnell zu laufen. Ihre maximale Höchstgeschwindigkeit von 97 km/h ist weit höher als nötig, um den noch heute lebenden amerikanischen Raubtieren wie dem Puma und dem Wolf zu entkommen.Byers, John (1998): American Pronghorn: Social Adaptations and the Ghosts of Predators Past: pp. 318. Chicago University PressDie Ähnlichkeit zwischen Miracinonyx trumani und dem Gepard ist ein Beispiel für parallele Evolution. Als weite Graslandschaften sowohl in Nordamerika als auch in Afrika während des Pleistozäns häufiger wurden, entwickelten sich Puma-ähnliche Katzenarten auf beiden Kontinenten zu schnellen Läufern, um die neu aufkommenden schnellen Pflanzenfresser jagen zu können. Die Krallen von Miracinonyx trumani entwickelten sich dabei derart, dass sie – wie auch beim Gepard – nur noch teilweise einziehbar waren, um die Bodengriffigkeit beim schnellen Rennen zu erhöhen., source=null, sourceTaxonKey=null, creator=null, contributor=null, license=null}, Description{type=null, language=null, description=Kleinkatzen Großkatzen, source=null, sourceTaxonKey=null, creator=null, contributor=null, license=null}, Description{type=null, language=null, description=Miracynonix inexpectatus war dem Puma ähnlicher als dem Gepard. Er hatte vollständig einziehbare Krallen und konnte aufgrund seines schlanken Körperbaus wahrscheinlich schneller laufen als der Puma. Eventuell konnte er noch klettern und hatte seinen Lebensraum weniger in der Prärie als in stärker bewaldeten Regionen. Aufgrund des etwas kühleren Lebensraumes könnte er ein längeres Fell gehabt haben., source=null, sourceTaxonKey=null, creator=null, contributor=null, license=null}], vernacularNames=[VernacularName{vernacularName=Amerikanische Geparden, language=GERMAN, lifeStage=null, sex=null, country=null, area=null, source=null, sourceTaxonKey=null, preferred=null, plural=null}]}
	 */
	result = new NameUsageSearchResult();
	//results.add(result);
	result.setKey(125812601);
	result.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	result.setNubKey(4833952);
	result.setParentKey(137012570);
	result.setParent("Felidae");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Miracinonyx");
	result.setOrderKey(137012553);
	result.setFamilyKey(137012570);
	result.setGenusKey(125812601);
	result.setScientificName("Miracinonyx Adams, 1979");
	result.setCanonicalName("Miracinonyx");
	result.setAuthorship("Adams, 1979");
	result.setNameType(NameType.WELLFORMED);
	result.setRank(Rank.GENUS);
	result.setNumDescendants(0);
	result.setNumOccurrences(0);
	result.setTaxonID("5313509");
	result.setExtinct(true);
	description = new Description();
	result.getDescriptions().add(description);
	description.setDescription("Die Amerikanischen Geparde wurden zu Beginn als frühe Vertreter der Pumas angesehen, in den 1970er Jahren aber als nahe Verwandte des Gepards neu klassifiziert.Adams, Daniel B. (14. September 1979): The Cheetah: Native American, Science, 205(4411): pp. 1155–1158 Diese Theorie geht davon aus, dass sich die Vorfahren des Gepards aus der Linie der Pumas auf dem amerikanischen Kontinent (die Neue Welt) entwickelten und über die Beringbrücke wieder in die Alte Welt (vor allem Asien und Afrika) wanderten.Johnson, W.E., Eizirik, E., Pecon-Slattery, J., Murphy, W.J., Antunes, A., Teeling, E. & O'Brien, S.J. (2006-01-06): The Late Miocene radiation of modern Felidae: A genetic assessment, Science, 311(5757): pp. 73–77Andere Forschungen, wie etwa die von Ross Barnett, welche die Untersuchung der mitochondrialen DNA der Knochenfunde sowie eine neue Analyse der Morphologie mit einschloss, sehen die Amerikanischen Geparde als nahe Verwandte des Pumas, die Gepard-ähnliche Merkmale aufgrund von konvergenter Evolution entwickelten.Barnett, Ross (9. August 2005): Evolution of the extinct Sabretooths and the American cheetah-like cat, Current Biology, 15(15): pp. R589–R590 Ein weiterer naher Verwandter ist dabei der Jaguarundi, der jedoch eher kleinkatzenähnlich ist.Der vermutete amerikanische Ursprung der Geparde ist daher als äquivokal anzusehen, denn es wird angenommen, dass sie sich sowohl in der Alten Welt als auch in der Neuen Welt (Amerika) aus Puma-ähnlichen Vorfahren entwickelt haben. Dabei sollen sich Puma und Miracinonyx trumani vor etwa 3 Millionen Jahren von einer gleichen Vorfahrenlinie evolutionär abgespalten haben, die genaue Einordnung der zweiten Art Miracinonyx inexpectatus ist indes nicht endgültig geklärt, obwohl es sich wahrscheinlich um eine primitivere Art als Miracinonyx trumani handelt.Haaramo, Mikko (2005-11-15): Mikko's Phylogeny Archive - Felidae: Felinae – small catsVom Amerikanischen Gepard wurden bislang zwei Arten beschrieben: Miracinonyx trumani und Miracynonix inexpectatus. Teilweise wird auch eine dritte Art Miracinonyx studeri angeführt, diese gilt jedoch als jüngeres Synonym für Miracynonix trumani. Beide Arten waren dem heutigen Gepard sehr ähnlich, mit verkürzten runden Schädeln und vergrößerten Nasengängen für einen größeren Atemdurchsatz sowie speziell für schnelles Rennen proportionierte extrem lange Gliedmaßen und einem langen Schwanz. Ebenso soll das Gebiss gegenüber anderen Katzen stark verkleinert und relativ schwach gewesen sein. Dennoch wurden die Ähnlichkeiten nicht durch einen direkten gemeinsamen Vorfahren vererbt, sondern waren das Ergebnis von entweder paralleler oder konvergenter Evolution.Durch genetische und immunologische Untersuchungen konnte ermittelt werden, dass die heutigen Geparde wahrscheinlich alle von einer sehr kleinen Stammgruppe abstammen (→ genetischer Flaschenhals), die vor etwa 10.000 Jahren gelebt hat.M. Menotti-Raymond, S. J. O'Brien: Dating the genetic bottleneck of the African cheetah. In: Proceedings of the National Academy of Sciences of the United States of America. 1993; 90(8): 3172-3176Modellberechnung der genetischen Drift Damals starb der Amerikanische Gepard aus, und der gewöhnliche Gepard in Afrika und Asien entging diesem Schicksal offenbar nur knapp. Er breitete sich jedoch in den Savannen Afrikas und Asiens wieder aus und konnte daher bis in unsere Zeit überleben. Diese Untersuchung genießt in Fachkreisen hohes Ansehen und wird mittlerweile als klassisches Beispiel in der Populationsgenetik benutzt.Die genauen Gründe, die zum Aussterben der Amerikanischen Geparde geführt haben oder geführt haben könnten, sind trotz aller Forschung dazu bislang nicht abschließend aufgeklärt. Einige Experten vermuten zusätzlich zum genannten genetischen Flaschenhals durchaus auch weitere Einflüsse wie einsetzende Klimaveränderungen und eine verstärkte Nahrungskonkurrenz. Dafür spricht etwa der Umstand, dass etwa 10.000 Jahren vor unserer Zeitrechnung etliche andere Großtierarten Amerikas ausgestorben sind, wie etwa der Amerikanische Löwe.");
	description = new Description();
	result.getDescriptions().add(description);
	description.setDescription("Die Amerikanischen Geparden (Miracinonyx) sind eine ausgestorbene Gattung aus der Familie der Katzen (Felidae), die vom späten Pliozän bis zum späten Pleistozän (Jungpleistozän) 1.800.000 bis 11.000 Jahre vor unserer Zeitrechnung endemisch in Nordamerika lebte.PaleoBiology Database: Miracinonyx, basic info Es gab mindestens zwei Arten dieser Gattung, die von der Morphologie her dem heutigen Gepard ähnlich waren. Sie sind nur durch Skelett-Bruchstücke bekannt.");
	description = new Description();
	result.getDescriptions().add(description);
	description.setDescription("Miracinonyx trumani war den echten Geparden morphologisch am ähnlichsten. Er lebte in der Prärie sowie den weiten Ebenen des westlichen Nordamerikas und jagte sehr wahrscheinlich Huftiere der Great Plains, wie den heute noch lebenden Gabelbock. Es besteht Grund zur Annahme, dass seine maximal erreichbare Geschwindigkeit jener der heutigen Geparden nur wenig nachstand und mindestens um die 100 km/h gelegen haben dürfte.Die Nachstellung durch den Miracinonyx trumani könnte der Grund gewesen sein, weshalb die Gabelböcke die Fähigkeit entwickelten derart schnell zu laufen. Ihre maximale Höchstgeschwindigkeit von 97 km/h ist weit höher als nötig, um den noch heute lebenden amerikanischen Raubtieren wie dem Puma und dem Wolf zu entkommen.Byers, John (1998): American Pronghorn: Social Adaptations and the Ghosts of Predators Past: pp. 318. Chicago University PressDie Ähnlichkeit zwischen Miracinonyx trumani und dem Gepard ist ein Beispiel für parallele Evolution. Als weite Graslandschaften sowohl in Nordamerika als auch in Afrika während des Pleistozäns häufiger wurden, entwickelten sich Puma-ähnliche Katzenarten auf beiden Kontinenten zu schnellen Läufern, um die neu aufkommenden schnellen Pflanzenfresser jagen zu können. Die Krallen von Miracinonyx trumani entwickelten sich dabei derart, dass sie – wie auch beim Gepard – nur noch teilweise einziehbar waren, um die Bodengriffigkeit beim schnellen Rennen zu erhöhen.");
	description = new Description();
	result.getDescriptions().add(description);
	description.setDescription("Kleinkatzen Großkatzen");
	description = new Description();
	result.getDescriptions().add(description);
	description.setDescription("Miracynonix inexpectatus war dem Puma ähnlicher als dem Gepard. Er hatte vollständig einziehbare Krallen und konnte aufgrund seines schlanken Körperbaus wahrscheinlich schneller laufen als der Puma. Eventuell konnte er noch klettern und hatte seinen Lebensraum weniger in der Prärie als in stärker bewaldeten Regionen. Aufgrund des etwas kühleren Lebensraumes könnte er ein längeres Fell gehabt haben.");

	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Amerikanische Geparden");
	vernacularName.setLanguage(Language.GERMAN);
	// result.setSynonym(false);


	// http://api.gbif.org/v1/species/search?q=Puma&rank=GENUS
	NameUsageSearchRequest searchRequest = new NameUsageSearchRequest();
	searchRequest.setQ("Puma");
	searchRequest.addRankFilter(Rank.GENUS);
	SpeciesAPIClient sut = new SpeciesAPIClient();

	SearchResponse<NameUsageSearchResult, NameUsageSearchParameter> actual = sut.search(searchRequest);
	printCollections("test_search", actual.getResults(), expected.getResults());

	assertThat(actual, is(expected));
    }

    /**
     * Tests suggest function in Searching Names subset of GBIF SpeciesAPI
     * using date available from
     * http://api.gbif.org/v1/species/suggest?datasetKey=d7dddbf4-2cf0-4f39-9b2a-bb099caae36c&amp;q=Puma%20con
     *
     */
    @Test
    public void suggest() {
	List<NameUsageSuggestResult> expected = newArrayList();
	NameUsageSuggestResult result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(2435099);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(2435099);
	result.setParentKey(2435098);
	result.setParent("Puma");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor (Linnaeus, 1771)");
	result.setCanonicalName("Puma concolor");
	// result.setAuthorship(" (Linnaeus, 1771)");
	// result.setPublishedIn("Mantissa Plantarum vol. 2 p. 266");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setRank(Rank.SPECIES);
	// result.setNumDescendants(6);
	// result.setNumOccurrences(0);
	// result.setSourceId("119806678");
	Description description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Sabana inundable");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Der Löwe von Amerika. Deutsche TV-Dokumentation von Ronald Tobias, WDR 2003, 44 Minuten");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Der Puma (Puma concolor) ist eine Katzenart Nord- und Südamerikas. In seinen Heimatländern ist er unter zahlreichen weiteren Namen bekannt; für viele davon gibt es auch im Deutschen eine Entsprechung: Silberlöwe, Berglöwe oder Kuguar (engl. cougar, aus frz. couguar). In den Vereinigten Staaten wird er auch panther genannt; ein Name, der im Deutschen hauptsächlich für Leoparden und Jaguare mit Melanismus verwendet wird. Der Name Puma ist aus dem Quechua entlehnt.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Der Puma ist ein Einzelgänger und meidet außer zur Paarungszeit seine Artgenossen. Die Größe des Streifgebietes hängt vom Nahrungsangebot und vom Zugang zu Geschlechtspartnern ab und reicht von 50 km² bis zu 1000 km². Die Reviere territorialer Männchen sind größer als die der Weibchen und überlappen sich in der Regel jeweils mit denen mehrerer Weibchen.Indem sie sich aus dem Weg gehen, sind Pumas untereinander sehr friedlich. Eine intensive Markierung der Reviere durch Kot- oder Harnmarken und zusätzlich durch Kratzspuren an den Bäumen durch die Männchen ist dabei hilfreich.Der Puma ist zwar in der Lage, auf kurzen Strecken sehr schnell zu laufen, er nutzt diese Fähigkeit jedoch nur sehr selten. Er erreicht dabei Geschwindigkeiten von 55 km/h bis 72 km/h.\"Cougar facts\" In: Canadian Geographic. Abgerufen am 21. April 2011. Verfehlt er ein Beutetier, jagt er nicht hinterher. Wird er, zum Beispiel von Wölfen, verfolgt, flüchtet er eher auf einen Baum, als dass er größere Strecken läuft. Ernährung Pumas erbeuten Säugetiere nahezu aller Größen. In Nordamerika zählen Elche, Hirsche und Rentiere zu den Beutetieren des Pumas, aber auch Mäuse, Ratten, Erdhörnchen, Skunks, Waschbären, Biber und Opossums sowie Schafe und junge Rinder. Mit 68 % Nahrungsanteil sind Hirsche jedoch seine häufigste Beute.\"Biogeographic variation of food habits and body size of the America puma\". In: Springer Link. Abgerufen am 21. April 2011. Auch andere Raubtiere wie Kojoten und Rotluchse kann der Puma überwältigen. Neben Säugetieren frisst der Puma auch Vögel und in manchen Gegenden Fische. Er ist jedoch kein Aasfresser und meidet auch Reptilien. Ebenso wie in Nordamerika stellen Hirsche, wie Weißwedelhirsche, Spießhirsche, Gabelhirsche und Sumpfhirsche, auch in Südamerika den Großteil der Pumabeute. Daneben erlegen Pumas in Südamerika allerlerei mittelgroße Säuger, wie Guanakos, Viscachas, Agutis und Gürteltiere. Insgesamt schlagen Pumas in den tropischen Teilen des Verbreitungsgebietes meist kleinere Beutetiere als in den nördlichen und südlichen Teilen. Dies ist vermutlich damit zu erklären, dass in den Tropen meist der größere Jaguar neben dem Puma vorkommt und letzterer dann auf kleinere Beute ausweicht.Um ein größeres Beutetier zu erlegen, schleicht sich der Puma zunächst heran. Aus kurzer Distanz springt er dem Tier auf den Rücken und bricht ihm mit einem kräftigen Biss in den Hals das Genick. Fortpflanzung Puma-Kätzchen Als Einzelgänger kommen Pumas nur zur Paarungszeit, die meist, aber nicht ausschließlich, zwischen November und Juni liegt, für maximal sechs Tage zusammen, ehe das Männchen das Weibchen einige Wochen vor der Geburt der Jungen wieder verlässt. Die Tragzeit beträgt etwa drei Monate. Ein Wurf hat zwischen einem und sechs, in der Regel aber zwei bis drei Junge. Das Geburtsgewicht liegt zwischen 230 und 450 Gramm, die Größe der Neugeborenen 20 bis 30 Zentimeter. Die Jungen nehmen nach etwa sechs bis sieben Wochen feste Nahrung auf und trennen sich etwa nach 20 Monaten von der Mutter.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Der nächste Verwandte des Pumas ist der Jaguarundi, der in jüngeren Systematiken ebenfalls in derselben Gattung, nämlich Pumas (Puma), geführt wird. Eine relativ enge Verwandtschaft besteht auch zum Gepard, der früher in einer separaten Unterfamilie innerhalb der Katzen geführt wurde, eine Einteilung, die nach phylogenetischen Gesichtspunkten nicht aufrechtzuerhalten ist. Außerdem wurden Ende der 1890er Pumaparde gezüchtet, welche einen Hybrid aus Puma und Leopard darstellen\"Pumapard & Puma/Jaguar Hybrids\", Abgerufen am 18. April 2012. Die Hybride waren kleinwüchsig und hatten eine stark verkürzte Lebenserwartung.Neuere Genuntersuchungen legen nahe, dass der Puma mit dem ausgestorbenen nordamerikanischen Geparden Miracinonyx relativ nah verwandt ist. Dieser gehörte allerdings nicht zur Unterfamilie Acinonychinae, der die heutigen Geparden zugeordnet werden.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Ein Berglöwe war auch tierischer Hauptdarsteller des Disney-Films Die Flucht des Pumas (Run, Cougar, Run, 1972).Der Puma hat sich in verschiedenen Disziplinen Rekorde eingeholt: So trägt er im Guinness Buch der Rekorde den Titel des Tieres mit den meisten Namen (40)The Guinness Book of World Records. 2004, S. 49, ist mit über fünf Metern das Säugetier mit den höchsten Sprüngen„Phänomene in der Tierwelt: Exklusive Wanderausstellung 'World Champions of Nature’“ Abgerufen am 21. April 2011. und hat das größte Verbreitungsgebiet in der gesamten westlichen Hemisphäre.Das Computer-Betriebssystem Mac OS X 10.1 von Apple trug den Codenamen Puma. Einige Jahre später wurde OS X 10.8 auf den Namen Mountain Lion (‚Berglöwe‘) getauft.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Puma Traditionell wurden 24 bis 32 Unterarten des Pumas unterschieden. Zwei im östlichen Nordamerika beheimatete Unterarten galten als ausgestorben. Als besonders bedroht galt der Florida-Panther (P. c. coryi), der in den Everglades-Sümpfen die Ausrottungswellen überlebt hat (siehe unten). Neuere molekulargenetische Untersuchungen (M. Culver u. a.) legen allerdings den Schluss nahe, die Spezies in nur sechs Unterarten zu gliedern und die Unterscheidung von mehr Unterarten einer wissenschaftlichen Grundlage entbehrt. Folgende sechs Unterarten sind mit den genetischen Befunden vereinbar:M. Culver, W. E. Johnson, J. Pecon-Slattery, S. J. O'Brien: Genomic ancestry of the American puma (Puma concolor). In: The Journal of Heredity. 91(3), 2000 Nordamerikanischer Puma (Puma concolor cougar), inklusive dem im März 2011 für Ausgestorben erklärten Östlichen Nordamerikanischen Puma\"U.S. Fish and Wildlife Service concludes eastern cougar extinct\". US Fish & Wildlife Service. Abgerufen am 21. April 2011. und dem Florida-Panther: Nordamerika, nördliches Mittelamerika Mittelamerikanischer Puma (Puma concolor costaricensis): Mittelamerika von Panama bis etwa Honduras Nördlicher Südamerikanischer Puma (Puma concolor concolor): Nordwesthälfte Südamerikas, nordwestlich der Mündung des Rio Tocantins, im Südwesten bis etwa zur Grenze Chiles und Argentiniens Östlicher Südamerikanischer Puma (Puma concolor capricornensis): Osthälfte Südamerikas von der Mündung des Rio Tocantins im Norden bis zur Mündung des Rio de la Plata Mittel-Südamerikanischer Puma (Puma concolor cabrerae): Südöstliches Südamerika zwischen dem Rio de la Plata und etwa dem Rio Negro in Argentinien, landeinwärts bis ins Gran Chaco-Gebiet Südlicher Südamerikanischer Puma (Puma concolor puma): Chile und PatagonienAlle Pumas Nordamerikas unterscheiden sich genetisch kaum voneinander und stellen eine weitgehend homogene Population dar. Die Populationen Mittel- und Südamerikas zeigen eine größere Variabilität. Diese Befunde werden darauf zurückgeführt, dass die Vorfahren der Nordamerikanischen Pumas erst vor rund 10.000 Jahren am Ende der letzten Eiszeit aus Südamerika einwanderten. Da um dieselbe Zeit in Amerika etliche Großtierarten ausstarben, wird vermutet, dass den Puma in Nordamerika dasselbe Schicksal ereilte, er sich allerdings in Südamerika halten konnte und von dort aus wieder etwas nach Norden vorstieß. Mit dieser Studie geht auch eine Neubewertung der ausgestorbenen und bedrohten Unterarten einher. Florida-PantherFlorida-Panther Obwohl der Florida-Panther nach den neuesten Untersuchungen nicht als eigene Unterart gilt, werden große Anstrengungen unternommen, diese Population zu erhalten. Sie galt kurzzeitig sogar als ausgestorben, wurde jedoch 1972 wiederentdeckt. Untersuchungen ergaben, dass nur noch weniger als 30 Tiere dieser eher kleinen, intensiv rot gefärbten Pumas lebten. Sie wiesen zudem Erscheinungen auf, die auf Inzuchtprobleme schließen lassen (Cow-lips, Knickschwanz). Daher wurden 1995 mehrere weibliche Pumas aus Texas eingeführt, um die Population der Florida-Panther zu stützen.Aussetzung von Texas-Pumas in Florida Diese zeugten mindestens 25 Nachkommen mit männlichen Florida-Panthern. Auch genetische Untersuchungen bestätigten später den Erfolg der Aussetzungen. Die aus Texas eingeführten Pumas wurden danach wieder aus der Population entfernt.Texas-Pumas aus Florida entferntDank genetischer Untersuchungen wurde festgestellt, dass bereits vor den offiziellen Puma-Aussetzungen 1995 ein Teil der Florida-Panther mit südamerikanischen Pumas hybridisierte. Wie diese Pumas nach Florida kamen, konnte nicht geklärt werden. Es dürfte sich entweder um entlaufene oder illegal ausgesetzte Tiere gehandelt haben, da eine natürliche Zuwanderung kaum möglich ist.Hybridisierung der Florida-Panther (PDF)Heute leben in Florida wieder zwischen 50 und 70 Pumas. 1995 waren es noch zwischen 30 und 50. Dies gilt als Erfolg des Umsiedlungsprogramms. Viele der Tiere tragen zu Forschungszwecken ein Senderhalsband.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Trittsiegel einer Pumapfote Pumas haben außer dem Menschen kaum Feinde zu fürchten. Lediglich Wölfe, Bären und Jaguare können gelegentlich junge oder kranke Pumas erbeuten. Obwohl sie unter Artenschutz stehen, werden Pumas von manchen Bauern gejagt, die um ihre Viehbestände fürchten. Der Gesamtbestand wird auf weniger als 50.000 erwachsene Tiere geschätzt. Die Art als Ganzes gilt laut der Roten Liste gefährdeter Arten der Weltnaturschutzunion IUCN als nicht gefährdet („Least Concern“).Der Puma ist eine scheue Katze, die menschliche Nähe für gewöhnlich meidet und vor Menschen meistens flieht; trotzdem kommt es gelegentlich zu Angriffen und Konflikten, z. B. wenn ein Puma Haustiere angreift.www.rcinet.ca: Lynn Desjardins: Teen saves dog from cougar attack (englisch) Zuletzt abgerufen 5. Juni 2013www.rcinet.ca: Marc Montgomery: More Couguar-Attacks in Canada (englisch). Zuletzt abgerufen 5. Juni 2013 In den Vereinigten Staaten soll es jährlich etwa vier solcher Vorfälle geben, die aber nur selten tödlich verlaufen. Opfer der Attacken sind meistens Kinder. Nur in Ausnahmefällen greift er Erwachsene an.Bei den indigenen Völkern Amerikas stand der Puma in hohem Ansehen. Sie schrieben ihm Eigenschaften wie Führungskraft, Kraft, Findigkeit, Treue, Engagement und Mut zu.Die weißen Kolonisten in Nordamerika bekämpften den Puma. Sie wollten nicht nur ihr Vieh vor ihm schützen, sondern jagten ihn auch, weil er eine beliebte Trophäe darstellte.Das Irokesisch sprechende nordamerikanische Volk der Erie wurde mit dem Puma in Verbindung gebracht, weil sich der Name Erie von Erielhonan herleitet, was auf Deutsch ‚Langer Schwanz‘ bedeutet. Damit wurden eigentlich Waschbärfelle und die Indianerstämme, die mit ihnen handelten, bezeichnet. Die Franzosen bezogen das Wort aber fälschlicherweise auf den Puma und nannten die Erie deswegen das Volk der Katze (Nation du Chat).");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Verbreitungsgebiet des Pumas.Rot: Heutige Verbreitung. In den heller gefärbten Regionen ist der Bestand ausgerottet oder stark ausgedünnt worden. Pumas waren früher über den größten Teil Nord- und Südamerikas verbreitet. Kein anderes Säugetier des amerikanischen Doppelkontinents hatte ein vergleichbar weit ausgedehntes Verbreitungsgebiet. Es reichte vom Süden Kanadas über Mittelamerika bis ins südliche Patagonien. Heute ist der Bestand stark ausgedünnt und auf von Menschen schwach besiedelte Gebiete reduziert. In den USA überlebten Pumas die Ausrottungswellen nur in den Rocky Mountains, Cascade Range, Coast Mountains, in den Wüsten und Halbwüsten des Südwestens und in den Everglades-Sümpfen Floridas. Durch Schutzmaßnahmen hat sich das Verbreitungsgebiet wieder erweitert, inzwischen gibt es beispielsweise auch im Gebiet der Großen Seen wieder Pumas.Pumas Cause Stir East of MississippiVictor Skinner: Photo shows cougar presence in Michigan. The Grand Rapids Press, 15. November 2009 In manchen Regionen des US-amerikanischen Westens scheuen Pumas auch die Nähe von Städten nicht mehr. In der kanadischen Provinz Québec tauchte der Puma nachweislich 2007 wieder auf, obwohl er dort seit 1938 als ausgerottet galt.Le cougar de l'est est présent au Québec (PDF, 38,3 kB).Pumas sind in nahezu allen Habitaten zu finden: Die Prärie, boreale, gemäßigte und tropische Wälder, Halbwüsten und Hochgebirge gehören allesamt zu den Lebensräumen dieser Katze.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Zwar wird der Puma taxonomisch nicht den Großkatzen, sondern den Kleinkatzen zugeordnet, gehört aber dennoch zu den größten Katzen. Die Schulterhöhe beträgt rund 60 cm bis 70 cmR. M. Nowak: Walker's Mammals of the World. Bd 1. Johns Hopkins University Press, Baltimore 1999, ISBN 0-8018-5789-9, S. 818 f., die Kopf-Rumpf-Länge schwankt zwischen 68 cm und 155 cm. Hinzu kommt der Schwanz mit einer Länge zwischen 60 cm und 97 cm. Männchen wiegen zwischen 53 kg und 72 kg, Weibchen in der Regel zwischen 34 kg und 48 kg. Für ein extrem großes Männchen wurde darüber hinaus ein Rekordgewicht von über 125 kg festgestellt.Mel Sunquist, Fiona Sunquist: Wildcats of the World. University of Chicago Press, 2002, ISBN 0226779998, S. 254  () Die um den Äquator herum beheimateten Pumas sind die kleinsten Formen, während jene im extremen Norden und Süden des Verbreitungsgebietes am größten sind.Das Fell ist kurz, dicht und einfarbig, im Farbton allerdings sehr variabel. Am häufigsten sind einerseits gelbbraune bis rötlichbraune oder andererseits silbergraue Pumas; das Kinn und die Brust, sowie die gesamte Unterseite sind stets weißlich. Die Schwanzspitze ist dunkel. Neugeborene Pumas sind beigefarben und kräftig gefleckt; die Fleckenzeichnung verblasst noch während des ersten Lebensjahrs. Aus Südamerika sind auch Schwärzlinge dokumentiert.M. E. Sunquist & F. C. Sunquist (2009). Family Felidae (Cats). In: Don E. Wilson , Russell A. Mittermeier (Hrsg.): Handbook of the Mammals of the World. Volume 1: Carnivores. Lynx Edicions, 2009, ISBN 978-84-96553-49-1, (S. 145 f.). Pumas haben fünf Zehen an den Vorderpfoten und vier an den Hinterpfoten. Sie können ihre Krallen einziehen.  PumasPumas sind sehr beweglich und kräftig. Pumas sind nachweislich in der Lage, vom Boden aus bis zu 5,5 m hoch in einen Baum zu springen. Die Tiere geben ein großes Spektrum unterschiedlicher Laute von sich, die sich zwischen den Geschlechtern unterscheiden. So dienen zwitschernde Laute offenbar der Kommunikation zwischen Müttern und ihrem Nachwuchs, während Schreie offenbar zum Paarungsverhalten gehören. Anders als die eigentlichen Großkatzen sind Pumas jedoch nicht in der Lage zu brüllen. Nordamerikanische Forscher wie Truman Everts beschreiben den Schrei des Pumas als menschenähnlich.Pumas erreichen in der Wildnis ein Alter von 8 bis 13 Jahren.K. Nowell & P. Jackson: „Wild Cats. Status Survey and Conservation Action Plan.“ (PDF; 24,6 MB). 1996, S. 132. Abgerufen am 21. April 2011. In Gefangenschaft werden Berglöwen über 20 Jahre alt, ein nordamerikanischer Puma namens Scratch ist sogar fast 30 Jahre alt geworden.Scratch. Cougar 1977-2007 (archiviert)");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The geographic range of the puma is the largest of any terrestrial mammal in the Western Hemisphere (Sunquist and Sunquist 2002), from Canada through the US, Central and South America to the southern tip of Chile.  While the puma is an adaptable cat, being found in every major habitat type of the Americas, including the high Andes (5,800 m in southern Peru: Sunquist and Sunquist 2002), it was eliminated from the entire eastern half of North America within 200 years following European colonization (Nowell and Jackson, 1996).  A remnant Endangered subpopulation persists in Florida, and records of pumas in northeastern Canada and the eastern US are on the rise, indicating possible recolonization (M. Kelly pers. comm. 2007).");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Included in CITES Appendix II (eastern and Central American subspecies (P. c. coryi, costaricensis and cougar) Appendix I). The species is protected across much of its range, with hunting prohibited in most of Argentina, and all of Brazil, Bolivia, Chile, Colombia, Costa Rica, French Guiana, Guatemala, Honduras, Nicaragua, Panama, Paraguay, Suriname, Venezuela and Uruguay, and hunting regulations in place in Canada, Mexico, Peru and the United States (Nowell and Jackson 1996).There is a need for the implementation of programs to mitigate conflict resolution for livestock depredation and to study the real effect of puma vs. jaguar depredation on livestock (IUCN Cats Red List workshop, 2007).  Puma occasionally kill humans - especially in North America.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The species is found in a broad range of habitats, in all forest types as well as lowland and montane desert.  Several studies have shown that habitat with dense understory vegetation is preferred, however, pumas can live in very open habitats with only a minimum of vegetative cover (Nowell and Jackson 1996).  Pumas co-occur with jaguars in much of their Latin American range, and may favor more open habitats than their larger competitor, although both can be found in dense forest (Sunquist and Sunquist 2002).Pumas are capable of taking large prey, but when available small to medium-sized prey are more important in their diet (in tropical portions of the range).  This is true of wild prey as well as livestock (IUCN Cats Red List workshop, 2007).  In North America, deer make up 60-80% of the puma's diet, and the mean weight of prey taken is 39-48 kg.  In Florida, however, where deer numbers are low, pumas take smaller prey including feral pigs, raccoons and armadilllos, and deer account for only about 1/3 of the diet (Sunquist and Sunquist 2002).Home range sizes of pumas vary considerably across their geographic distribution, and the smallest ranges tend to occur in areas where prey densities are high and prey are not migratory (Sunquist and Sunquist 2002).  In North America, home range sizes ranged from 32-1,031 km² (Lindzey et al. 1987).");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The Canadian population was rougly estimated at 3,500-5,000 and the western US population at 10,000 in the early 1990s (Nowell and Jackson, 1996).  The population of Central and South America is likely much higher, although it is unclear how abundant pumas are in the dense rainforest of the Amazon basin (Nowell and Jackson, 1996). The Florida subpopulation, numbering 70-80, is isolated, and has been supplemented by a reintroduction of pumas from Texas (Sunquist and Sunquist 2002). In Brazil it is considered Near Threatened but subspecies outside the Amazon basic are considered VU (Machado et al. 2005).  It is also considered Near Threatened in Peru (Inrena 2006), Argentina (Diaz and Ojeda 2000) and Colombia (Rodriguez-Mahecha et al., 2006), and Data Deficient (inadequately known) in Chile (CONAMA 2005). Density estimates include:Utah, US: 0.3-0.5/100 km² (Hemker et al. 1984)Idaho, US:  0.77-1.04/100 km² (Laundre and Clark 2003)Peru: 2.4/100 km² (Janson and Emmons 1990)Patagonia: 6/100 km² (Franklin et al. 1999)Pantanal 4.4/100 km² (Crawshaw and Quigley unpubl. in Nowell and Jackson 1996)Belize 2-5/100 km², in Argentina 0.5-0.8/100 km², Bolivia 5-8/100 km² (Kelly et al. in press)W Mexico 3-5/100 km² (Nunez et al. 1998)");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Pumas are threatened by habitat loss and fragmentation, and poaching of their wild prey base.  They are persecuted across their range by retaliatory hunting due to livestock depredation, and due to fear that they pose a threat to human life (IUCN Cats Red List workshop, 2007).  Pumas have killed a number of people in western Canada and the US in recent years.  Pumas are legally hunted in many western US states, although hunting was banned by popular referendum in California in 1990.  Road kills are the principal cause of mortality in the endangered Florida panther subpopulation, and heavily travelled roads are a major barrier to puma movements and dispersal (Sunquist and Sunquist 2002).");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Bosque de piedemonte");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Physical characteristics Cougars are slender and agile members of the cat family. They are the fourth-largest cat;Expanding Cougar Population The Cougar Net.org adults stand about  tall at the shoulders.Florida Panther Facts. Florida Panther Refuge Adult males are around  long nose-to-tail and females average , with overall ranges between  nose to tail suggested for the species in general.Mountain Lion (Puma concolor). Texas Parks and WildlifeEastern Cougar Fact Sheet. New York State Department of Environmental Conservat ion Of this length,  is comprised by the tail.Shivaraju, A. (2003) Puma concolor. Animal Diversity Web, University of Michigan Museum of Zoology. Retrieved on September 15, 2011. Males typically weigh 53 to 100 kg (115 to 220 lb), averaging 62 kg (137 lb). Females typically weigh between 29 and 64 kg (64 and 141 lb), averaging 42 kg (93 lb).Nowell, K. and Jackson, P (2006): Wild Cats. Status Survey and Conservation Action Plan. IUCN/SSC Cat Specialist Group. IUCN, Gland, SwitzerlandPuma concolor – Mountain Lion – Discover Life. Pick4.pick.uga.edu Cougar size is smallest close to the equator, and larger towards the poles.Iriarte, J. Agustin; Franklin, William L.; Johnson, Warren E. and Redford, Kent H. (1990): Biogeographic variation of food habits and body size of the America puma, Oecologia, 85(2): pp. 185  The largest recorded cougar, shot in Arizona, weighed 125.5 kg (276 lb) after its intestines were removed, indicating in life it could have weighed nearly 136.2 kg (300 lb).Brakefield, Tom (1993): Big Cats: Kingdom of Might. Voyageur Press Several male cougars in British Columbia weighed between 86.4 and 95.5 kg (190 to 210 lb).Spalding, D. J.: Cougar in British Columbia, British Columbia Fish and Wildlife BranchThe head of the cat is round and the ears are erect. Its powerful forequarters, neck, and jaw serve to grasp and hold large prey. It has five retractable claws on its forepaws (one a dewclaw) and four on its hind paws. The larger front feet and claws are adaptations to clutching prey.Cougar, Hinterland Who's Who. Canadian Wildlife Service and Canadian Wildlife FederationCougars can be almost as large as jaguars, but are less muscular and not as powerfully built; where their ranges overlap, the cougar tends to be smaller than average. Besides the jaguar, the cougar is on average larger than all felids outside of the Old World lion and tigers. Despite its size, it is not typically classified among the \"big cats\", as it cannot roar, lacking the specialized larynx and hyoid apparatus of Panthera.Weissengruber, GE (2002): Hyoid apparatus and pharynx in the lion (Panthera leo), jaguar (Panthera onca), tiger (Panthera tigris), cheetah (Acinonyx jubatus) and domestic cat (Felis silvestris f. catus), Journal of Anatomy, 201(3): pp. 195–209. Anatomical Society of Great Britain and Ireland Compared to \"big cats\", cougars are often silent with minimal communication through vocalizations outside of the mother-offspring relationship.Hornocker, Maurice G. and Negri, Sharon (December 15, 2009): Cougar: ecology and conservation: pp. 114–. University of Chicago Press Cougars sometimes voice low-pitched hisses, growls, and purrs, as well as chirps and whistles, many of which are comparable to those of domestic cats. They are well known for their screams, as referenced in some of their common names, although these screams are often misinterpreted to be the calls of other animals.About Eastern Cougars. Eastern Cougar Foundation Cougar coloring is plain (hence the Latin concolor) but can vary greatly between individuals and even between siblings. The coat is typically tawny, but ranges to silvery-grey or reddish, with lighter patches on the underbody, including the jaws, chin, and throat. Infants are spotted and born with blue eyes and rings on their tails; juveniles are pale, and dark spots remain on their flanks. Despite anecdotes to the contrary, all-black coloring (melanism) has never been documented in cougars.February 1, 2006: Black cougar more talk than fact. Tahlequah Daily Press The term \"black panther\" is used colloquially to refer to melanistic individuals of other species, particularly jaguars and leopards.Mutant Pumas, messybeast.comCougars have large paws and proportionally the largest hind legs in the cat family. This physique allows it great leaping and short-sprint ability. The cougar is able to leap as high as  in one bound, and as far as 40 to 45 ft (12 to 13.5 m) horizontally.Mountain Lion (Puma, Cougar), San Diego Zoo.org. Zoological Society of San DiegoCougar: Puma concolor: A Saskatchewan Species at Risk. Saskatoon Zoo Society, Canada.Cougar. bluelion.org.Hansen, Kevin (1990): Cougar: The American Lion. Mountain Lion Foundation The cougar's top running speed ranges between ,Cougar. Zoological Wildlife FoundationMountain Lion FAQ and Facts. Mountainlion.org. Retrieved on April 29, 2013. but is best adapted for short, powerful sprints rather than long chases. It is adept at climbing, which allows it to evade canine competitors. Although it is not strongly associated with water, it can swim.Mountain Lion, Felis concolor. Sierra Club Hunting and dietA successful generalist predator, the cougar will eat any animal it can catch, from insects to large ungulates (over 500 kg). Like all cats, it is an obligate carnivore, meaning it needs to feed exclusively on meat to survive. The mean weight of vertebrate prey (MWVP) was positively correlated (r=0.875) with puma body weight and inversely correlated (r=–0.836) with food niche breadth all across the Americas. In general, MWVP was lower in areas closer to the Equator. Its most important prey species are various deer species, particularly in North America; mule deer, white-tailed deer, and even bull elk are taken. Other species such as bighorn sheep, wild horses of Arizona, domestic horses, and domestic livestock such as cattle and sheep are also primary food bases in many areas.Turner, John W.; Morrison, Michael L. (2008): Influence of Predation by Mountain Lions on Numbers and Survivorship of a Feral Horse Population, The Southwestern Naturalist, 46(2): pp. 183–190 A survey of North America research found 68% of prey items were ungulates, especially deer. Only the Florida Panther showed variation, often preferring feral hogs and armadillos. Investigation in Yellowstone National Park showed that elk, followed by mule deer, were the cougar's primary targets; the prey base is shared with the park's gray wolves, with whom the cougar competes for resources.Wildlife: Wolves. Yellowstone National Park* Holly Akenson, James Akenson, Howard Quigley: Winter Predation and Interactions of Wolves and Cougars on Panther Creek in Central Idaho* John K. Oakleaf, Curt Mack, Dennis L. Murray: Winter Predation and Interactions of Cougars and Wolves in the Central Idaho Wilderness Another study on winter kills (November–April) in Alberta showed that ungulates accounted for greater than 99% of the cougar diet. Learned, individual prey recognition was observed, as some cougars rarely killed bighorn sheep, while others relied heavily on the species.Ross, R. (1993): Cougar predation on bighorn sheep in southwestern Alberta during winter, Canadian Journal of Zoology, 75(5): pp. 771–75In the Central and South American cougar range, the ratio of deer in the diet declines. Small to mid-size mammals are preferred, including large rodents such as the capybara. Ungulates accounted for only 35% of prey items in one survey, approximately half that of North America. Competition with the larger jaguar has been suggested for the decline in the size of prey items. Other listed prey species of the cougar include mice, porcupines, beavers, raccoons, hares.Whitaker, John O. The Audubon Society Field Guide to North American Mammals. Chanticleer Press, New York, 1980, pg. 598. ISBN 0-394-50762-2. Birds and small reptiles are sometimes preyed upon in the south, but this is rarely recorded in North America. Not all of their prey is listed here due to their large range.Though capable of sprinting, the cougar is typically an ambush predator. It stalks through brush and trees, across ledges, or other covered spots, before delivering a powerful leap onto the back of its prey and a suffocating neck bite. The cougar is capable of breaking the neck of some of its smaller prey with a strong bite and momentum bearing the animal to the ground.Kills are generally estimated at around one large ungulate every two weeks. The period shrinks for females raising young, and may be as short as one kill every three days when cubs are nearly mature at around 15 months. The cat drags a kill to a preferred spot, covers it with brush, and returns to feed over a period of days. It is generally reported that the cougar is not a scavenger, and will rarely consume prey it has not killed; but deer carcasses left exposed for study were scavenged by cougars in California, suggesting more opportunistic behavior.Bauer, Jim W. (2005): Scavenging behavior in Puma, The Southwestern Naturalist, 50(4): pp. 466–471 Reproduction and life cycleFemales reach sexual maturity between one-and-a-half to three years of age. They typically average one litter every two to three years throughout their reproductive lives,Cougar Discussion Group (January 27, 1999): Utah Cougar Management Plan (Draft). Utah Division of Wildlife Resources though the period can be as short as one year. Females are in estrus for about 8 days of a 23-day cycle; the gestation period is approximately 91 days. Females are sometimes reported as monogamous,Cougars in Canada (Just the Facts). Canadian Geographic Magazine but this is uncertain and polygyny may be more common.Hamilton, Matthew; Hundt, Peter and Piorkowski, Ryan: Mountain Lions. University of Wisconsin, Stevens Point Copulation is brief but frequent. Chronic stress can result in low reproductive rates when in captivity as well as in the field.Bonier, F., H. Quigley, S. Austad (2004): A technique for non-invasively detecting stress response in cougars, Wildlife Society Bulletin, 32(3): pp. 711–717 Only females are involved in parenting. Female cougars are fiercely protective of their cubs, and have been seen to successfully fight off animals as large as American black bears in their defense. Litter size is between one and six cubs; typically two. Caves and other alcoves that offer protection are used as litter dens. Born blind, cubs are completely dependent on their mother at first, and begin to be weaned at around three months of age. As they grow, they begin to go out on forays with their mother, first visiting kill sites, and after six months beginning to hunt small prey on their own. Kitten survival rates are just over one per litter. When cougars are born, they have spots, but they lose them as they grow, and by the age of 2 1/2 years, they will completely be goneStaying safe in cougar country. Wildlife.utah.govYoung adults leave their mother to attempt to establish their own territory at around two years of age and sometimes earlier; males tend to leave sooner. One study has shown high mortality amongst cougars that travel farthest from the maternal range, often due to conflicts with other cougars (intraspecific competition). Research in New Mexico has shown that \"males dispersed significantly farther than females, were more likely to traverse large expanses of non-cougar habitat, and were probably most responsible for nuclear gene flow between habitat patches.\"Sweanor, Linda (2000): Cougar Dispersal Patterns, Metapopulation Dynamics, and Conservation, Conservation Biology, 14(3): pp. 798–808Life expectancy in the wild is reported at eight to 13 years, and probably averages eight to 10; a female of at least 18 years was reported killed by hunters on Vancouver Island. Cougars may live as long as 20 years in captivity. One male North American cougar (P. c. couguar), named Scratch, was two months short of his 30th birthday when he died in 2007.Scratch, bigcatrescue.org Causes of death in the wild include disability and disease, competition with other cougars, starvation, accidents, and, where allowed, human hunting. Feline immunodeficiency virus, an endemic HIV-like virus in cats, is well-adapted to the cougar.Biek, Roman (2003): Epidemiology, Genetic Diversity, and Evolution of Endemic Feline Immunodeficiency Virus in a Population of Wild Cougars, Journal of Virology, 77(17): pp. 9578–89 Social structure and home rangeLike almost all cats, the cougar is a solitary animal. Only mothers and kittens live in groups, with adults meeting only to mate. It is secretive and crepuscular, being most active around dawn and dusk.Estimates of territory sizes vary greatly. Canadian Geographic reports large male territories of 150 to 1000 km2 (58 to 386 sq mi) with female ranges half the size. Other research suggests a much smaller lower limit of 25 km2 (10 sq mi), but an even greater upper limit of 1300 km2 (500 sq mi) for males. In the United States, very large ranges have been reported in Texas and the Black Hills of the northern Great Plains, in excess of 775 km2 (300 sq mi).Mahaffy, James (2004): Behavior of cougar in Iowa and the Midwest. Dordt College Male ranges may include or overlap with those of females but, at least where studied, not with those of other males, which serves to reduce conflict between cougars. Ranges of females may overlap slightly with each other. Scrape marks, urine, and feces are used to mark territory and attract mates. Males may scrape together a small pile of leaves and grasses and then urinate on it as a way of marking territory.Home range sizes and overall cougar abundance depend on terrain, vegetation, and prey abundance. One female adjacent to the San Andres Mountains, for instance, was found with a large range of 215 km2 (83 sq mi), necessitated by poor prey abundance. Research has shown cougar abundances from 0.5 animals to as much as 7 (in one study in South America) per 100 km2 (38 sq mi).Because males disperse farther than females and compete more directly for mates and territory, they are most likely to be involved in conflict. Where a subadult fails to leave his maternal range, for example, he may be killed by his father. When males encounter each other, they hiss, spit, and may engage in violent conflict if neither backs down. Hunting or relocation of the cougar may increase aggressive encounters by disrupting territories and bringing young, transient animals into conflict with established individuals.March 22, 2007: Mountain Lion (Felis concolor) study on Boulder Open Space, Letter to the Parks and Open Space Advisory Committee, Boulder, Colorado. Sinapu");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The World Conservation Union (IUCN) currently lists the cougar as a \"least concern\" species. The cougar is regulated under Appendix I of the Convention on International Trade in Endangered Species of Wild Fauna and Flora (CITES),Appendices I, II and III. Convention on International Trade in Endangered Species of Wild Fauna and Flora rendering illegal international trade in specimens or parts. In the United States east of the Mississippi River, the only unequivocally known cougar population is the Florida panther. Until 2011, the United States Fish and Wildlife Service (USFWS) recognized both an Eastern cougar (claimed to be a subspecies by some, denied by others)Bolgiano, Chris (August 1995): Mountain Lion:An Unnatural History of Pumas and People(Hardcover). Stackpole BooksEberhart, George M. (2002): Mysterious Creatures: A Guide to Cryptozoology, Volume 2: pp. 153–161. ABC-CLIO and the Florida panther, affording protection under the Endangered Species Act.1991: Eastern Cougar, Endangered and Threatened Species of the Southeastern United States (The Red Book). U.S. Fish and Wildlife Service1993: Florida Panther, Endangered and Threatened Species of the Southeastern United States (The Red Book). United States Fish and Wildlife Service Certain taxonomic authorities have collapsed both designations into the North American cougar, with Eastern or Florida subspecies not recognized, while a subspecies designation remains recognized by some conservation scientists. The most recent documented count for the Florida sub-population is 87 individuals, reported by recovery agencies in 2003.Florida Fish and Wildlife Conservation Commission. 2002–2003 Panther Genetfic Restoration Annual Report In March 2011, the USFWS declared the Eastern cougar extinct. However, with the taxonomic uncertainty about its existence as a subspecies as well as the possibility of eastward migration of cougars from the western range, the subject remains open.This uncertainty has been recognized by Canadian authorities. The Canadian federal agency called Committee on the Status of Endangered Wildlife in Canada rates its current data as \"insufficient\" to draw conclusions regarding the eastern cougar's survival, and says on its Web site \"Despite many sightings in the past two decades from eastern Canada, there are insufficient data to evaluate the taxonomy or assign a status to this cougar.\" Notwithstanding numerous reported sightings in Ontario, Quebec, New Brunswick and Nova Scotia, it has been said that the evidence is inconclusive: \". . . there may not be a distinct 'eastern' subspecies, and some sightings may be of escaped pets.\"Committee on Status of Endangered Wildlife in Canada. Cosewic.gc.ca. Retrieved on September 15, 2011.Eastern Cougar, Nature Canada. Naturecanada.ca. Retrieved on September 15, 2011.The cougar is also protected across much of the rest of its range. As of 1996, cougar hunting was prohibited in Argentina, Brazil, Bolivia, Chile, Colombia, Costa Rica, French Guiana, Guatemala, Honduras, Nicaragua, Panama, Paraguay, Suriname, Venezuela, and Uruguay. The cat had no reported legal protection in Ecuador, El Salvador, and Guyana. Regulated cougar hunting is still common in the United States and Canada, although they are protected from all hunting in the Yukon; it is permitted in every U.S. state from the Rocky Mountains to the Pacific Ocean, with the exception of California. Texas is the only state in the United States with a viable population of cougars that does not protect that population in some way. In Texas, cougars are listed as nuisance wildlife and any person holding a hunting or a trapping permit can kill a cougar regardless of the season, number killed, sex or age of the animal.TPWD: Mountain Lions. Tpwd.state.tx.us (July 16, 2007). Retrieved on September 15, 2011.  Killed animals are not required to be reported to Texas Parks and Wildlife Department. Conservation work in Texas is the effort of a non profit organization, Balanced Ecology Inc (BEI), as part of their Texas Mountain Lion Conservation Project. Cougars are generally hunted with packs of dogs, until the animal is 'treed'. When the hunter arrives on the scene, he shoots the cat from the tree at close range. The cougar cannot be legally killed without a permit in California except under very specific circumstances, such as when a cougar is in act of pursuing livestock or domestic animals, or is declared a threat to public safety. Permits are issued when owners can prove property damage on their livestock or pets.  For example, multiple dogs have been attacked and killed, sometimes while with the owner.  Many attribute this to the protection cougars have from being hunted and are now becoming desensitized to humans; most are removed from the population after the attacks have already occurred.   Statistics from the Department of Fish and Game indicate that cougar killings in California have been on the rise since 1970s with an average of over 112 cats killed per year from 2000 to 2006 compared to six per year in the 1970s. They also state on their website that there is a healthy number of cougars in California.  The Bay Area Puma Project aims to obtain information on cougar populations in the San Francisco Bay area and the animals' interactions with habitat, prey, humans, and residential communities.Bay Area Puma Project (BAPP). Felidae Conservation FundConservation threats to the species include persecution as a pest animal, environmental degradation and habitat fragmentation, and depletion of their prey base. Wildlife corridors and sufficient range areas are critical to the sustainability of cougar populations. Research simulations have shown that the animal faces a low extinction risk in areas of 2200 km2 (850 sq mi) or more. As few as one to four new animals entering a population per decade markedly increases persistence, foregrounding the importance of habitat corridors.Beier, Paul (1993): Determining Minimum Habitat Areas and Habitat Corridors for Cougars, Conservation Biology, 7(1): pp. 94–108On March 2, 2011, the United States Fish and Wildlife Service declared the Eastern cougar (Puma concolor couguar) officially extinct.Northeast Region, U.S. Fish and Wildlife Service. Fws.gov. Retrieved on September 15, 2011.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Distribution and habitat The cougar has the largest range of any wild land animal in the Americas. Its range spans 110 degrees of latitude, from northern Yukon in Canada to the southern Andes. It is one of only three cat species, along with the bobcat and Canada lynx, native to Canada. Its wide distribution stems from its adaptability to virtually every habitat type: it is found in all forest types, as well as in lowland and mountainous deserts. The cougar prefers regions with dense underbrush, but can live with little vegetation in open areas. Its preferred habitats include precipitous canyons, escarpments, rim rocks, and dense brush.The cougar was extirpated across much of its eastern North American range (with the exception of Florida) in the two centuries after European colonization, and faced grave threats in the remainder of its territory. Currently, it ranges across most western American states, the Canadian provinces of Alberta, Saskatchewan and British Columbia, and the Canadian territory of Yukon. There have been widely debated reports of possible recolonization of eastern North America.Marschall, Laurence A. (2005): Bookshelf, Natural Selections. Natural History Magazine DNA evidence has suggested its presence in eastern North America,Belanger, Joe (May 25, 2007): DNA tests reveal cougars roam region. London Free Press while a consolidated map of cougar sightings shows numerous reports, from the mid-western Great Plains through to eastern Canada.Board of Directors (2004): The \"Big\" Picture. The Cougar Network The Cougar Network methodology is recognized by the U.S. Fish and Wildlife Service. The Quebec wildlife services (known locally as MRNF) also considers cougar to be present in the province as a threatened species after multiple DNA tests confirmed cougar hair in lynx mating sites.2010: Your part in helping endangered species. Ministry of Wildlife and Natural Resources, Quebec, Canada The only unequivocally known eastern population is the Florida panther, which is critically endangered. There have been unconfirmed sightings in Elliotsville Plantation, Maine (north of Monson); and in New Hampshire, there have been unconfirmed sightings as early as 1997.Davidson, Rick (2009): NH Sightings Catamount. Beech River Books In 2009, the Michigan Department of Natural Resources confirmed a cougar sighting in Michigan's Upper Peninsula.Skinner, Victor (November 15, 2009): Photo shows cougar presence in Michigan, The Grand Rapids Press Typically, extreme-range sightings of cougars involve young males, which can travel great distances to establish ranges away from established males; all four confirmed cougar kills in Iowa since 2000 involved males.On April 14, 2008, police shot and killed a cougar on the north side of Chicago, Illinois. DNA tests were consistent with cougars from the Black Hills of South Dakota. Less than a year later, on March 5, 2009, a cougar was photographed and unsuccessfully tranquilized by state wildlife biologists in a tree near Spooner, Wisconsin, in the northwestern part of the state.The Indiana Department of Natural Resources used motion-sensitive cameras to confirm the presence of a cougar in Greene County in southern Indiana on May 7, 2010. Another sighting in late 2009 in Clay County in west-central Indiana was confirmed by the DNR.May 7, 2010: DNR confirms Indiana mountain lion sighting. WISHTV8.comOn June 10, 2011, a cougar was observed roaming near Greenwich, Connecticut. State officials at the time said they believed it was a released pet.  On June 11, 2011, a cougar, believed to be the same animal as the one observed in Greenwich, was killed by a car on the Wilbur Cross Parkway in Milford, Connecticut.  When wildlife officials examined the cougar's DNA, they concluded it was a wild cougar from the Black Hills of South Dakota, which had wandered at least 1,500 miles east over an indeterminate time period.David Baron The Cougar Behind Your Trash Can. New York Times. July 29, 2011In October 2012, a trail camera in Morgan County, Illinois snapped a photograph of a cougar, and on November 6, 2012 a trail camera in Pike County captured a photo also believed to be of a cougar.South of the Rio Grande, the International Union for the Conservation of Nature and Natural Resources (IUCN) lists the cat in every Central and South American country. While specific state and provincial statistics are often available in North America, much less is known about the cat in its southern range.Cougar facts. National Wildlife FederationThe cougar's total breeding population is estimated at less than 50,000 by the IUCN, with a declining trend. US state-level statistics are often more optimistic, suggesting cougar populations have rebounded. In Oregon, a healthy population of 5,000 was reported in 2006, exceeding a target of 3,000.2006: Cougar Management Plan, Wildlife Division: Wildlife Management Plans. Oregon Department of Fish and Wildlife California has actively sought to protect the cat and a similar number of cougars has been suggested, between 4,000 and 6,000.2004: Mountain Lions in California. California Department of Fish and GameIn 2012 research in Río Los Cipreses National Reserve, Chile, based in 18 motion-sensitive cameras counted a population of two males and two females, one of them with at least two cubs, in an area of 600 km2, that is 0.63 cougars every 100 km2.Research of Nicolás Guarda, supported by Conaf, Pontifical Catholic University of Chile,  and a private Enterprise. See article in Chilean newspaper La Tercera, Investigación midió por primera vez población de pumas en zona central, retrieved on January 28, 2013, in Spanish Language. Ecological role Aside from humans, no species preys upon mature cougars in the wild, although conflicts with other predators or scavengers occur.  The Yellowstone National Park ecosystem provides a fruitful microcosm to study inter-predator interaction in North America. Of the three large predators, the massive Grizzly Bear appears dominant, often although not always able to drive both the gray wolf pack and the cougar off their kills. One study found that  American black bears visited 24% of cougar kills in Yellowstone and Glacier National Parks, usurping 10% of carcasses. Bears gained up to 113%, and cougars lost up to 26%, of their respective daily energy requirements from these encounters.COSEWIC. Canadian Wildlife Service (2002): Assessment and Update Status Report on the Grizzly Bear (Ursus arctos). Environment Canada Accounts of cougars and black bears killing each other in fights to the death have been documented from the 19th century.S. C. Turnbo: A Fight to a Finish Between a Bear and Panther. thelibrary.orgS. C. Turnbo: Finding a Panther Guarding a Dead Bear. thelibrary.org In spite of the size and power of the cougar, there have also been accounts of both brown and black bears killing cougars, either in disputes or in self-defense.Cougar vs Bears Accounts. Everything about the Cougar/Mountain LionHornocker, M., and S. Negri (Eds.). (2009). Cougar: ecology and conservation. University of Chicago Press. Chicago, IL, ISBN 0226353443.The gray wolf and the cougar compete more directly for prey, especially in winter. While individually more powerful than the gray wolf, a solitary cougar may be dominated by the pack structure of the canines. Wolves can steal kills and occasionally kill the cat. One report describes a large pack of 7 to 11 wolves killing a female cougar and her kittens.Park wolf pack kills mother cougar. forwolves.org Conversely, lone wolves are at a disadvantage, and have been reported killed by cougars. Various accounts of cougars killing lone wolves, including a six-year old female, have also been documented.Wolf B4 Killed by Mountain Lion?. forwolves.orgAutopsy Indicates Cougar Killed Wolf. igorilla.comMountain lions kill collared wolves in Bitteroot. missoulian.com Wolves more broadly affect cougar population dynamics and distribution by dominating territory and prey opportunities, and disrupting the feline's behavior. Preliminary research in Yellowstone, for instance, has shown displacement of the cougar by wolves.Overview: Gray Wolves. Greater Yellowstone Learning Center In nearby Sun Valley, Idaho, a recent cougar/wolf encounter that resulted in the death of the cougar was documented.Predators clash above Elkhorn. Idaho Mountain Express One researcher in Oregon noted: \"When there is a pack around, cougars are not comfortable around their kills or raising kittens ... A lot of times a big cougar will kill a wolf, but the pack phenomenon changes the table.\"Cockle, Richard (October 29, 2006): Turf wars in Idaho's wilderness. The Oregonian Both species, meanwhile, are capable of killing mid-sized predators, such as bobcats and coyotes, and tend to suppress their numbers. Although cougars can kill coyotes, the latter have been documented attempting to prey on cougar cubs.Cougars vs. coyotes photos draw Internet crowd. missoulian.com In the southern portion of its range, the cougar and jaguar share overlapping territory.Hamdig, Paul: Sympatric Jaguar and Puma. Ecology Online Sweden The jaguar tends to take larger prey and the cougar smaller where they overlap, reducing the cougar's size and also further reducing the likelihood of direct competition. Of the two felines, the cougar appears best able to exploit a broader prey niche and smaller prey.Rodrigo Nuanaez, Brian Miller, and Fred Lindzey (2000): Food habits of jaguars and pumas in Jalisco, Mexico, Journal of Zoology, 252(3): pp. 373As with any predator at or near the top of its food chain, the cougar impacts the population of prey species. Predation by cougars has been linked to changes in the species mix of deer in a region. For example, a study in British Columbia observed that the population of mule deer, a favored cougar prey, was declining while the population of the less frequently preyed-upon white-tailed deer was increasing.Robinson, Hugh S. (2002): Cougar predation and population growth of sympatric mule deer and white-tailed deer, Canadian Journal of Zoology, 80(3): pp. 556–68 The Vancouver Island marmot, an endangered species endemic to one region of dense cougar population, has seen decreased numbers due to cougar and gray wolf predation.Bryant, Andrew A. (2005): Cougar predation and population growth of sympatric mule deer and white-tailed deer, Canadian Journal of Zoology, 83(5): pp. 674–82  Nevertheless, there is a measurable effect on the quality of deer populations by puma predation.Fountain, Henry (November 16, 2009) \"Observatory: When Mountain Lions Hunt, They Prey on the Weak\". New York Times.Weaver, John L.; Paquet, Paul C.; Ruggiero, Leonard F. (1996): Resilience and Conservation of Large Carnivores in the Rocky Mountains, Conservation Biology, 10(4): pp. 964In the southern part of South America, the puma is a top level predator that has controlled the population of guanaco and other species since prehistoric times.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The cougar (Puma concolor), also known as the mountain lion, puma, panther, mountain cat, or catamount, is a large cat of the family Felidae native to the Americas. Its range, from the Canadian Yukon to the southern Andes of South America, is the greatest of any large wild terrestrial mammal in the Western Hemisphere. An adaptable, generalist species, the cougar is found in most American habitat types. It is the second heaviest cat in the Western Hemisphere, after the jaguar. Solitary by nature and nocturnal,Cougars. US National Park Service. the cougar is most closely related to smaller felines and is nearer genetically to the domestic cat than true lions.An excellent stalk-and-ambush predator, the cougar pursues a wide variety of prey. Primary food sources include ungulates such as deer, elk, moose, and bighorn sheep, as well as domestic cattle, horses and sheep, particularly in the northern part of its range. It will also hunt species as small as insects and rodents. This cat prefers habitats with dense underbrush and rocky areas for stalking, but can also live in open areas. The cougar is territorial and survives at low population densities. Individual territory sizes depend on terrain, vegetation, and abundance of prey. While large, it is not always the apex predator in its range, yielding to the jaguar, grey wolf, American black bear, and grizzly bear. It is reclusive and usually avoids people. Fatal attacks on humans are rare, but have been trending upward in recent years as more people enter their territory.Excessive hunting following European colonization of the Americas and the ongoing human development of cougar habitat has caused populations to drop in most parts of its historical range. In particular, the cougar was extirpated in eastern North America in the beginning of the 20th century, except for an isolated subpopulation in Florida. However, in recent decades, breeding populations have moved east into the far western parts of the Dakotas, Nebraska, and Oklahoma. Transient males have been verified in Minnesota,Shot fells elusive cougar chased out of a culvert. StarTribune.com (December 1, 2011). Retrieved on April 29, 2013. Wisconsin, Iowa,Mountain Lion Shot Early Friday Morning in Monona County. Iowa Department of Natural Resources, iowadnr.gov (December 23, 2011)Mountain lion shot by police in Monona County - KWWL.com - News & Weather for Waterloo, Dubuque, Cedar Rapids & Iowa City, Iowa |. KWWL.com. Retrieved on April 29, 2013. the Upper Peninsula of Michigan, and Illinois, where a cougar was shot in the city limits of ChicagoThe Cougar Network – Using Science to Understand Cougar Ecology. Cougarnet.orgNovember 5, 2009: Trail cam photo of cougar in the eastern Upper Peninsula. Twitpic.comMay 10, 2010: Indiana confirms mountain lion in Green County. Poorboysoutdoors.com and, in at least one instance, observed as far east as Connecticut.July 26, 2011: Mountain lion killed in Conn. had walked from S. Dakota. USA TodayConnecticut Mountain Lion Likely Came From The Black Hills, NPR News");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("A pumapard is a hybrid animal resulting from a union between a cougar and a leopard. Three sets of these hybrids were bred in the late 1890s and early 1900s by Carl Hagenbeck at his animal park in Hamburg, Germany. Most did not reach adulthood. One of these was purchased in 1898 by Berlin Zoo. A similar hybrid in Berlin Zoo purchased from Hagenbeck was a cross between a male leopard and a female puma. Hamburg Zoo's specimen was the reverse pairing, the one in the black-and-white photo, fathered by a puma bred to an Indian leopardess.Whether born to a female puma mated to a male leopard, or to a male puma mated to a female leopard, pumapards inherit a form of dwarfism. Those reported grew to only half the size of the parents. They have a puma-like long body (proportional to the limbs, but nevertheless shorter than either parent), but short legs. The coat is variously described as sandy, tawny or greyish with brown, chestnut or \"faded\" rosettes.Geocites – Liger & Tigon Info");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("With its vast range across the length of the Americas, Puma concolor has dozens of names and various references in the mythology of the indigenous Americans and in contemporary culture. The cat has many names in English, of which cougar, puma and mountain lion are popular. \"Mountain lion\" was a term first used in writing in 1858 from the diary of George A. Jackson of Colorado.George A. Jackson (1935): George A. Jackson's Diary of 1858-1859, Colorado Magazine, 6: pp. 201–214 However, mountain lion is technically incorrect, as its range is not limited to mountain regions, nor can it roar like a true lion, the latter being of the genus Panthera. Other names include \"catamount\" (probably a contraction from \"cat of the mountain\"), \"panther\", \"mountain screamer\" and \"painter\". Lexicographers regard \"painter\" as a primarily upper-Southern US regional variant on \"panther\".painter, transcription of the American Heritage Dictionary, Bartleby.com  The word panther, while technically referring to all members of the genus Panthera, is commonly used to specifically designate the black panther, a melanistic jaguar or leopard, and the Florida panther, a subspecies of cougar (Puma concolor coryi).Puma concolor holds the Guinness record for the animal with the highest number of names, presumably due to its wide distribution across North and South America. It has over 40 names in English alone.2004: The Guinness Book of World Records: pp. 49\"Cougar\" may be borrowed from the archaic Portuguese çuçuarana; the term was originally derived from the Tupi language. A current form in Brazil is suçuarana. It may also be borrowed from the Guaraní language term guaçu ara or guazu ara. Less common Portuguese terms are onça-parda (lit. brown onça, in distinction of the black-spotted [yellow] one, onça-pintada, the jaguar) or leão-baio (lit. chestnut lion), or unusually non-native puma or leão-da-montanha, more common names for the animal when native to a region other than South America (especially for those who do not know that suçuaranas are found elsewhere but with a different name). People in rural regions often refer to both the cougar and to the jaguar as simply gata (lit. she-cat), and outside of the Amazon, both are colloquially referred to as simply onça by many people (that is also a name for the leopard in Angola).In the 17th century, German naturalist Georg Marcgrave named the cat the cuguacu ara. Marcgrave's rendering was reproduced by his associate, Dutch naturalist Willem Piso, in 1648. Cuguacu ara was then adopted by English naturalist John Ray in 1693.Words to the Wise, Take Our Word for it(205): pp. 2 The French naturalist Georges-Louis Leclerc, Comte de Buffon in 1774 (probably influenced by the word \"jaguar\") converted the cuguacu ara to cuguar, from when it was later modified to \"cougar\" in English.cougar. Merriam-Webster Dictionary Online1989: cougar, Oxford Dictionaries Online, Oxford University PressThe first English record of \"puma\" was in 1777, where it had come from the Spanish, who in turn borrowed it from the Peruvian Quechua language in the 16th century, where it means \"powerful\".The Puma. Projeto Puma Puma is also the most common name cross-linguistically.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("In mythologyThe grace and power of the cougar have been widely admired in the cultures of the indigenous peoples of the Americas. The Inca city of Cusco is reported to have been designed in the shape of a cougar, and the animal also gave its name to both Inca regions and people. The Moche people represented the puma often in their ceramics.Berrin, Katherine & Larco Museum. The Spirit of Ancient Peru:Treasures from the Museo Arqueológico Rafael Larco Herrera. New York: Thames and Hudson, 1997. The sky and thunder god of the Inca, Viracocha, has been associated with the animal.Tarmo, Kulmar: On the role of Creation and Origin Myths in the Development of Inca State and Religion, Electronic Journal of Folklore. Estonian Folklore InstituteIn North America, mythological descriptions of the cougar have appeared in the stories of the Hocąk language (\"Ho-Chunk\" or \"Winnebago\") of Wisconsin and Illinois Cougars, The Encyclopedia of Hočąk (Winnebago) Mythology. Retrieved: 2009/12/08. and the Cheyenne, amongst others. To the Apache and Walapai of Arizona, the wail of the cougar was a harbinger of death.Living with Wildlife: Cougars. USDA Wildlife Services The Algonquins and Ojibwe believe that the cougar lived in the underworld and was wicked, whereas it was a sacred animal among the Cherokee.Matthews, John and Matthews, Caitlín (2005): The Element Encyclopedia of Magical Creatures: pp. 364. HarperElement Livestock predationDuring the early years of ranching, cougars were considered on par with wolves in destructiveness. According to figures in Texas in 1990, 86 calves (0.0006% of a total of 13.4 million cattle & calves in Texas), 253 Mohair goats, 302 Mohair kids, 445 sheep (0.02% of a total of 2.0 million sheep & lambs in Texas) and 562 lambs (0.04% of 1.2 million lambs in Texas) were confirmed to have been killed by cougars that year.Cattle report 1990. National Agricultural Statistics ServiceSheep and Goats report 1990. National Agricultural Statistics Service In Nevada in 1992, cougars were confirmed to have killed 9 calves, 1 horse, 4 foals, 5 goats, 318 sheep and 400 lambs. In both cases, sheep were the most frequently attacked. Some instances of surplus killing have resulted in the deaths of 20 sheep in one attack.Mountain Lion Fact Sheet. Abundant Wildlife Society of North America A cougar's killing bite is applied to the back of the neck, head, or throat and inflict puncture marks with their claws usually seen on the sides and underside of the prey, sometimes also shredding the prey as they hold on.  Coyotes also typically bite the throat region but do not inflict the claw marks and farmers will normally see the signature zig-zag pattern that coyotes create as they feed on the prey whereas cougars typically drag in a straight line.  The work of a cougar is generally clean, differing greatly from the indiscriminate mutilation by coyotes and feral dogs. The size of the tooth puncture marks also helps distinguish kills made by cougars from those made by smaller predators.Cougar Predation – Description. Procedures for Evaluating Predation on Livestock and Wildlife Attacks on humans Due to the expanding human population, cougar ranges increasingly overlap with areas inhabited by humans. Attacks on humans are very rare, as cougar prey recognition is a learned behavior and they do not generally recognize humans as prey.McKee, Denise (2003): Cougar Attacks on Humans: A Case Report, Wilderness and Environmental Medicine, 14(3): pp. 169–73. Wilderness Medical Society Attacks on people, livestock, and pets may occur when a puma habituates to humans or is in a condition of severe starvation. Attacks are most frequent during late spring and summer, when juvenile cougars leave their mothers and search for new territory.Between 1890 and 1990, in North America there were 53 reported, confirmed attacks on humans, resulting in 48 nonfatal injuries and 10 deaths of humans (the total is greater than 53 because some attacks had more than one victim).Beier, Paul (1991): Cougar attacks on humans in United States and Canada, Wildlife Society Bulletin. Northern Arizon University By 2004, the count had climbed to 88 attacks and 20 deaths.Confirmed mountain lion attacks in the United States and Canada 1890 – present. Arizona Game and Fish DepartmentWithin North America, the distribution of attacks is not uniform. The heavily populated state of California has seen a dozen attacks since 1986 (after just three from 1890 to 1985), including three fatalities. Lightly populated New Mexico reported an attack in 2008, the first there since 1974.New Mexico Department of Game and Fish: Search continues for mountain lion that killed Pinos Altos man, June 23, 2008; Wounded mountain lion captured, killed near Pinos Altos, June 25, 2008; Second mountain lion captured near Pinos Altos, July 1, 2008As with many predators, a cougar may attack if cornered, if a fleeing human stimulates their instinct to chase, or if a person \"plays dead\". Standing still however may cause the cougar to consider a person easy prey.Subramanian, Sushma (April 14, 2009): Should You Run or Freeze When You See a Mountain Lion?. Scientific American  Exaggerating the threat to the animal through intense eye contact, loud but calm shouting, and any other action to appear larger and more menacing, may make the animal retreat. Fighting back with sticks and rocks, or even bare hands, is often effective in persuading an attacking cougar to disengage.1991: Safety Guide to Cougars, Environmental Stewardship Division. Government of British Columbia, Ministry of EnvironmentWhen cougars do attack, they usually employ their characteristic neck bite, attempting to position their teeth between the vertebrae and into the spinal cord. Neck, head, and spinal injuries are common and sometimes fatal. Children are at greatest risk of attack, and least likely to survive an encounter. Detailed research into attacks prior to 1991 showed that 64% of all victims – and almost all fatalities – were children. The same study showed the highest proportion of attacks to have occurred in British Columbia, particularly on Vancouver Island where cougar populations are especially dense. Preceding attacks on humans, cougars display aberrant behavior, such as activity during daylight hours, a lack of fear of humans, and stalking humans. There have sometimes been incidents of pet cougars mauling people.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The cougar is the largest of the small cats. It is placed in the subfamily Felinae, although its bulk characteristics are similar to those of the big cats in the subfamily Pantherinae. The family Felidae is believed to have originated in Asia about 11 million years ago. Taxonomic research on felids remains partial, and much of what is known about their evolutionary history is based on mitochondrial DNA analysis, as cats are poorly represented in the fossil record, and there are significant confidence intervals with suggested dates. In the latest genomic study of Felidae, the common ancestor of today's Leopardus, Lynx, Puma, Prionailurus, and Felis lineages migrated across the Bering land bridge into the Americas 8.0 to 8.5 million years ago (Mya). The lineages subsequently diverged in that order.Johnson, W.E., Eizirik, E., Pecon-Slattery, J., Murphy, W.J., Antunes, A., Teeling, E. & O'Brien, S.J. (January 6, 2006): The Late Miocene radiation of modern Felidae: A genetic assessment, Science, 311(5757): pp. 73–77 North American felids then invaded South America 3 Mya as part of the Great American Interchange, following formation of the Isthmus of Panama. The cougar was originally thought to belong in Felis (Felis concolor), the genus which includes the domestic cat. As of 1993, it is now placed in Puma along with the jaguarundi, a cat just a little more than a tenth its weight.Studies have indicated the cougar and jaguarundi are most closely related to the modern cheetah of Africa and western Asia,Culver, M. (2000): Genomic Ancestry of the American Puma, Journal of Heredity, 91(3): pp. 186–97 but the relationship is unresolved. The cheetah lineage is suggested to have diverged from the Puma lineage in the Americas (see American cheetah) and migrated back to Asia and Africa, while other research suggests the cheetah diverged in the Old World itself.Barnett, Ross (August 9, 2005): Evolution of the extinct Sabretooths and the American cheetah-like cat, Current Biology, 15(15): pp. R589–R590 The outline of small feline migration to the Americas is thus unclear.Recent studies have demonstrated a high level of genetic similarity among the North American cougar populations, suggesting they are all fairly recent descendants of a small ancestral group. Culver et al. suggest the original North American population of Puma concolor was extirpated during the Pleistocene extinctions some 10,000 years ago, when other large mammals, such as Smilodon, also disappeared. North America was then repopulated by a group of South American cougars. SubspeciesUntil the late 1980s, as many as 32 subspecies were recorded; however, a recent genetic study of mitochondrial DNA found many of these are too similar to be recognized as distinct at a molecular level. Following the research, the canonical Mammal Species of the World (3rd ed.) recognizes six subspecies, five of which are solely found in Latin America:Argentine puma  includes the previous subspecies and synonyms hudsonii and puma (Marcelli, 1922) Costa Rican cougar  Eastern South American cougar  includes the previous subspecies and synonyms acrocodia, borbensis, capricornensis, concolor (Pelzeln, 1883), greeni, and nigra North American cougar   includes the previous subspecies and synonyms arundivaga, aztecus, browni, californica, coryi, floridana, hippolestes, improcera, kaibabensis, mayensis, missoulensis, olympus, oregonensis, schorgeri, stanleyana, vancouverensis, and youngi Northern South American cougar  includes the previous subspecies and synonyms bangsi, incarum, osgoodi, soasoaranna, sussuarana, soderstromii, suçuaçuara, and wavula Southern South American puma  includes the previous subspecies and synonyms araucanus, concolor (Gay, 1847), patagonica, pearsoni, and puma (Trouessart, 1904)The status of the Florida panther, here collapsed into the North American cougar, remains uncertain. It is still regularly listed as subspecies P. c. coryi in research works, including those directly concerned with its conservation.Conroy, Michael J. (2006): Improving The Use Of Science In Conservation: Lessons From The Florida Panther, Journal of Wildlife Management, 70(1): pp. 1–7 Culver et al. noted low microsatellite variation in the Florida panther, possibly due to inbreeding; responding to the research, one conservation team suggests, \"the degree to which the scientific community has accepted the results of Culver et al. and the proposed change in taxonomy is not resolved at this time.\"The Florida Panther Recovery Team (January 31, 2006): Florida Panther Recovery Program (Draft). U.S. Fish and Wildlife Service");
	/*
	VernacularName vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Cougar");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Puma");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Puma");
	vernacularName.setLanguage(Language.SPANISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("mountain lion");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Cougar");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("puma");
	vernacularName.setLanguage(Language.UNKNOWN);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Puma");
	vernacularName.setLanguage(Language.GERMAN);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Puma");
	vernacularName.setLanguage(Language.UNKNOWN);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Cougar");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Cougar");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Puma");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Puma");
	vernacularName.setLanguage(Language.SPANISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Puma");
	vernacularName.setLanguage(Language.SPANISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("mountain lion");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("mountain lion");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Cougar");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Deer Tiger");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("León Americano");
	vernacularName.setLanguage(Language.SPANISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("León Bayo");
	vernacularName.setLanguage(Language.SPANISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("León Colorado");
	vernacularName.setLanguage(Language.SPANISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("León De Montaña");
	vernacularName.setLanguage(Language.SPANISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Mitzli");
	vernacularName.setLanguage(Language.SPANISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Mountain Lion");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Onza Bermeja");
	vernacularName.setLanguage(Language.SPANISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Puma");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Red Tiger");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Cougar");
	vernacularName.setLanguage(Language.ENGLISH);
	*/
	/* "descriptionsSerialized":{"values":["Sabana inundable","Der Löwe von Amerika. Deutsche TV-Dokumentation von Ronald Tobias, WDR 2003, 44 Minuten","Der Puma (Puma concolor) ist eine Katzenart Nord- und Südamerikas. In seinen Heimatländern ist er unter zahlreichen weiteren Namen bekannt; für viele davon gibt es auch im Deutschen eine Entsprechung: Silberlöwe, Berglöwe oder Kuguar (engl. cougar, aus frz. couguar). In den Vereinigten Staaten wird er auch panther genannt; ein Name, der im Deutschen hauptsächlich für Leoparden und Jaguare mit Melanismus verwendet wird. Der Name Puma ist aus dem Quechua entlehnt.","Der Puma ist ein Einzelgänger und meidet außer zur Paarungszeit seine Artgenossen. Die Größe des Streifgebietes hängt vom Nahrungsangebot und vom Zugang zu Geschlechtspartnern ab und reicht von 50 km² bis zu 1000 km². Die Reviere territorialer Männchen sind größer als die der Weibchen und überlappen sich in der Regel jeweils mit denen mehrerer Weibchen.Indem sie sich aus dem Weg gehen, sind Pumas untereinander sehr friedlich. Eine intensive Markierung der Reviere durch Kot- oder Harnmarken und zusätzlich durch Kratzspuren an den Bäumen durch die Männchen ist dabei hilfreich.Der Puma ist zwar in der Lage, auf kurzen Strecken sehr schnell zu laufen, er nutzt diese Fähigkeit jedoch nur sehr selten. Er erreicht dabei Geschwindigkeiten von 55 km/h bis 72 km/h.\"Cougar facts\" In: Canadian Geographic. Abgerufen am 21. April 2011. Verfehlt er ein Beutetier, jagt er nicht hinterher. Wird er, zum Beispiel von Wölfen, verfolgt, flüchtet er eher auf einen Baum, als dass er größere Strecken läuft. Ernährung Pumas erbeuten Säugetiere nahezu aller Größen. In Nordamerika zählen Elche, Hirsche und Rentiere zu den Beutetieren des Pumas, aber auch Mäuse, Ratten, Erdhörnchen, Skunks, Waschbären, Biber und Opossums sowie Schafe und junge Rinder. Mit 68 % Nahrungsanteil sind Hirsche jedoch seine häufigste Beute.\"Biogeographic variation of food habits and body size of the America puma\". In: Springer Link. Abgerufen am 21. April 2011. Auch andere Raubtiere wie Kojoten und Rotluchse kann der Puma überwältigen. Neben Säugetieren frisst der Puma auch Vögel und in manchen Gegenden Fische. Er ist jedoch kein Aasfresser und meidet auch Reptilien. Ebenso wie in Nordamerika stellen Hirsche, wie Weißwedelhirsche, Spießhirsche, Gabelhirsche und Sumpfhirsche, auch in Südamerika den Großteil der Pumabeute. Daneben erlegen Pumas in Südamerika allerlerei mittelgroße Säuger, wie Guanakos, Viscachas, Agutis und Gürteltiere. Insgesamt schlagen Pumas in den tropischen Teilen des Verbreitungsgebietes meist kleinere Beutetiere als in den nördlichen und südlichen Teilen. Dies ist vermutlich damit zu erklären, dass in den Tropen meist der größere Jaguar neben dem Puma vorkommt und letzterer dann auf kleinere Beute ausweicht.Um ein größeres Beutetier zu erlegen, schleicht sich der Puma zunächst heran. Aus kurzer Distanz springt er dem Tier auf den Rücken und bricht ihm mit einem kräftigen Biss in den Hals das Genick. Fortpflanzung Puma-Kätzchen Als Einzelgänger kommen Pumas nur zur Paarungszeit, die meist, aber nicht ausschließlich, zwischen November und Juni liegt, für maximal sechs Tage zusammen, ehe das Männchen das Weibchen einige Wochen vor der Geburt der Jungen wieder verlässt. Die Tragzeit beträgt etwa drei Monate. Ein Wurf hat zwischen einem und sechs, in der Regel aber zwei bis drei Junge. Das Geburtsgewicht liegt zwischen 230 und 450 Gramm, die Größe der Neugeborenen 20 bis 30 Zentimeter. Die Jungen nehmen nach etwa sechs bis sieben Wochen feste Nahrung auf und trennen sich etwa nach 20 Monaten von der Mutter.","Der nächste Verwandte des Pumas ist der Jaguarundi, der in jüngeren Systematiken ebenfalls in derselben Gattung, nämlich Pumas (Puma), geführt wird. Eine relativ enge Verwandtschaft besteht auch zum Gepard, der früher in einer separaten Unterfamilie innerhalb der Katzen geführt wurde, eine Einteilung, die nach phylogenetischen Gesichtspunkten nicht aufrechtzuerhalten ist. Außerdem wurden Ende der 1890er Pumaparde gezüchtet, welche einen Hybrid aus Puma und Leopard darstellen\"Pumapard & Puma/Jaguar Hybrids\", Abgerufen am 18. April 2012. Die Hybride waren kleinwüchsig und hatten eine stark verkürzte Lebenserwartung.Neuere Genuntersuchungen legen nahe, dass der Puma mit dem ausgestorbenen nordamerikanischen Geparden Miracinonyx relativ nah verwandt ist. Dieser gehörte allerdings nicht zur Unterfamilie Acinonychinae, der die heutigen Geparden zugeordnet werden.","Ein Berglöwe war auch tierischer Hauptdarsteller des Disney-Films Die Flucht des Pumas (Run, Cougar, Run, 1972).Der Puma hat sich in verschiedenen Disziplinen Rekorde eingeholt: So trägt er im Guinness Buch der Rekorde den Titel des Tieres mit den meisten Namen (40)The Guinness Book of World Records. 2004, S. 49, ist mit über fünf Metern das Säugetier mit den höchsten Sprüngen„Phänomene in der Tierwelt: Exklusive Wanderausstellung 'World Champions of Nature’“ Abgerufen am 21. April 2011. und hat das größte Verbreitungsgebiet in der gesamten westlichen Hemisphäre.Das Computer-Betriebssystem Mac OS X 10.1 von Apple trug den Codenamen Puma. Einige Jahre später wurde OS X 10.8 auf den Namen Mountain Lion (‚Berglöwe‘) getauft.","Puma Traditionell wurden 24 bis 32 Unterarten des Pumas unterschieden. Zwei im östlichen Nordamerika beheimatete Unterarten galten als ausgestorben. Als besonders bedroht galt der Florida-Panther (P. c. coryi), der in den Everglades-Sümpfen die Ausrottungswellen überlebt hat (siehe unten). Neuere molekulargenetische Untersuchungen (M. Culver u. a.) legen allerdings den Schluss nahe, die Spezies in nur sechs Unterarten zu gliedern und die Unterscheidung von mehr Unterarten einer wissenschaftlichen Grundlage entbehrt. Folgende sechs Unterarten sind mit den genetischen Befunden vereinbar:M. Culver, W. E. Johnson, J. Pecon-Slattery, S. J. O'Brien: Genomic ancestry of the American puma (Puma concolor). In: The Journal of Heredity. 91(3), 2000 Nordamerikanischer Puma (Puma concolor cougar), inklusive dem im März 2011 für Ausgestorben erklärten Östlichen Nordamerikanischen Puma\"U.S. Fish and Wildlife Service concludes eastern cougar extinct\". US Fish & Wildlife Service. Abgerufen am 21. April 2011. und dem Florida-Panther: Nordamerika, nördliches Mittelamerika Mittelamerikanischer Puma (Puma concolor costaricensis): Mittelamerika von Panama bis etwa Honduras Nördlicher Südamerikanischer Puma (Puma concolor concolor): Nordwesthälfte Südamerikas, nordwestlich der Mündung des Rio Tocantins, im Südwesten bis etwa zur Grenze Chiles und Argentiniens Östlicher Südamerikanischer Puma (Puma concolor capricornensis): Osthälfte Südamerikas von der Mündung des Rio Tocantins im Norden bis zur Mündung des Rio de la Plata Mittel-Südamerikanischer Puma (Puma concolor cabrerae): Südöstliches Südamerika zwischen dem Rio de la Plata und etwa dem Rio Negro in Argentinien, landeinwärts bis ins Gran Chaco-Gebiet Südlicher Südamerikanischer Puma (Puma concolor puma): Chile und PatagonienAlle Pumas Nordamerikas unterscheiden sich genetisch kaum voneinander und stellen eine weitgehend homogene Population dar. Die Populationen Mittel- und Südamerikas zeigen eine größere Variabilität. Diese Befunde werden darauf zurückgeführt, dass die Vorfahren der Nordamerikanischen Pumas erst vor rund 10.000 Jahren am Ende der letzten Eiszeit aus Südamerika einwanderten. Da um dieselbe Zeit in Amerika etliche Großtierarten ausstarben, wird vermutet, dass den Puma in Nordamerika dasselbe Schicksal ereilte, er sich allerdings in Südamerika halten konnte und von dort aus wieder etwas nach Norden vorstieß. Mit dieser Studie geht auch eine Neubewertung der ausgestorbenen und bedrohten Unterarten einher. Florida-PantherFlorida-Panther Obwohl der Florida-Panther nach den neuesten Untersuchungen nicht als eigene Unterart gilt, werden große Anstrengungen unternommen, diese Population zu erhalten. Sie galt kurzzeitig sogar als ausgestorben, wurde jedoch 1972 wiederentdeckt. Untersuchungen ergaben, dass nur noch weniger als 30 Tiere dieser eher kleinen, intensiv rot gefärbten Pumas lebten. Sie wiesen zudem Erscheinungen auf, die auf Inzuchtprobleme schließen lassen (Cow-lips, Knickschwanz). Daher wurden 1995 mehrere weibliche Pumas aus Texas eingeführt, um die Population der Florida-Panther zu stützen.Aussetzung von Texas-Pumas in Florida Diese zeugten mindestens 25 Nachkommen mit männlichen Florida-Panthern. Auch genetische Untersuchungen bestätigten später den Erfolg der Aussetzungen. Die aus Texas eingeführten Pumas wurden danach wieder aus der Population entfernt.Texas-Pumas aus Florida entferntDank genetischer Untersuchungen wurde festgestellt, dass bereits vor den offiziellen Puma-Aussetzungen 1995 ein Teil der Florida-Panther mit südamerikanischen Pumas hybridisierte. Wie diese Pumas nach Florida kamen, konnte nicht geklärt werden. Es dürfte sich entweder um entlaufene oder illegal ausgesetzte Tiere gehandelt haben, da eine natürliche Zuwanderung kaum möglich ist.Hybridisierung der Florida-Panther (PDF)Heute leben in Florida wieder zwischen 50 und 70 Pumas. 1995 waren es noch zwischen 30 und 50. Dies gilt als Erfolg des Umsiedlungsprogramms. Viele der Tiere tragen zu Forschungszwecken ein Senderhalsband.","Trittsiegel einer Pumapfote Pumas haben außer dem Menschen kaum Feinde zu fürchten. Lediglich Wölfe, Bären und Jaguare können gelegentlich junge oder kranke Pumas erbeuten. Obwohl sie unter Artenschutz stehen, werden Pumas von manchen Bauern gejagt, die um ihre Viehbestände fürchten. Der Gesamtbestand wird auf weniger als 50.000 erwachsene Tiere geschätzt. Die Art als Ganzes gilt laut der Roten Liste gefährdeter Arten der Weltnaturschutzunion IUCN als nicht gefährdet („Least Concern“).Der Puma ist eine scheue Katze, die menschliche Nähe für gewöhnlich meidet und vor Menschen meistens flieht; trotzdem kommt es gelegentlich zu Angriffen und Konflikten, z. B. wenn ein Puma Haustiere angreift.www.rcinet.ca: Lynn Desjardins: Teen saves dog from cougar attack (englisch) Zuletzt abgerufen 5. Juni 2013www.rcinet.ca: Marc Montgomery: More Couguar-Attacks in Canada (englisch). Zuletzt abgerufen 5. Juni 2013 In den Vereinigten Staaten soll es jährlich etwa vier solcher Vorfälle geben, die aber nur selten tödlich verlaufen. Opfer der Attacken sind meistens Kinder. Nur in Ausnahmefällen greift er Erwachsene an.Bei den indigenen Völkern Amerikas stand der Puma in hohem Ansehen. Sie schrieben ihm Eigenschaften wie Führungskraft, Kraft, Findigkeit, Treue, Engagement und Mut zu.Die weißen Kolonisten in Nordamerika bekämpften den Puma. Sie wollten nicht nur ihr Vieh vor ihm schützen, sondern jagten ihn auch, weil er eine beliebte Trophäe darstellte.Das Irokesisch sprechende nordamerikanische Volk der Erie wurde mit dem Puma in Verbindung gebracht, weil sich der Name Erie von Erielhonan herleitet, was auf Deutsch ‚Langer Schwanz‘ bedeutet. Damit wurden eigentlich Waschbärfelle und die Indianerstämme, die mit ihnen handelten, bezeichnet. Die Franzosen bezogen das Wort aber fälschlicherweise auf den Puma und nannten die Erie deswegen das Volk der Katze (Nation du Chat).","Verbreitungsgebiet des Pumas.Rot: Heutige Verbreitung. In den heller gefärbten Regionen ist der Bestand ausgerottet oder stark ausgedünnt worden. Pumas waren früher über den größten Teil Nord- und Südamerikas verbreitet. Kein anderes Säugetier des amerikanischen Doppelkontinents hatte ein vergleichbar weit ausgedehntes Verbreitungsgebiet. Es reichte vom Süden Kanadas über Mittelamerika bis ins südliche Patagonien. Heute ist der Bestand stark ausgedünnt und auf von Menschen schwach besiedelte Gebiete reduziert. In den USA überlebten Pumas die Ausrottungswellen nur in den Rocky Mountains, Cascade Range, Coast Mountains, in den Wüsten und Halbwüsten des Südwestens und in den Everglades-Sümpfen Floridas. Durch Schutzmaßnahmen hat sich das Verbreitungsgebiet wieder erweitert, inzwischen gibt es beispielsweise auch im Gebiet der Großen Seen wieder Pumas.Pumas Cause Stir East of MississippiVictor Skinner: Photo shows cougar presence in Michigan. The Grand Rapids Press, 15. November 2009 In manchen Regionen des US-amerikanischen Westens scheuen Pumas auch die Nähe von Städten nicht mehr. In der kanadischen Provinz Québec tauchte der Puma nachweislich 2007 wieder auf, obwohl er dort seit 1938 als ausgerottet galt.Le cougar de l'est est présent au Québec (PDF, 38,3 kB).Pumas sind in nahezu allen Habitaten zu finden: Die Prärie, boreale, gemäßigte und tropische Wälder, Halbwüsten und Hochgebirge gehören allesamt zu den Lebensräumen dieser Katze.","Zwar wird der Puma taxonomisch nicht den Großkatzen, sondern den Kleinkatzen zugeordnet, gehört aber dennoch zu den größten Katzen. Die Schulterhöhe beträgt rund 60 cm bis 70 cmR. M. Nowak: Walker's Mammals of the World. Bd 1. Johns Hopkins University Press, Baltimore 1999, ISBN 0-8018-5789-9, S. 818 f., die Kopf-Rumpf-Länge schwankt zwischen 68 cm und 155 cm. Hinzu kommt der Schwanz mit einer Länge zwischen 60 cm und 97 cm. Männchen wiegen zwischen 53 kg und 72 kg, Weibchen in der Regel zwischen 34 kg und 48 kg. Für ein extrem großes Männchen wurde darüber hinaus ein Rekordgewicht von über 125 kg festgestellt.Mel Sunquist, Fiona Sunquist: Wildcats of the World. University of Chicago Press, 2002, ISBN 0226779998, S. 254  () Die um den Äquator herum beheimateten Pumas sind die kleinsten Formen, während jene im extremen Norden und Süden des Verbreitungsgebietes am größten sind.Das Fell ist kurz, dicht und einfarbig, im Farbton allerdings sehr variabel. Am häufigsten sind einerseits gelbbraune bis rötlichbraune oder andererseits silbergraue Pumas; das Kinn und die Brust, sowie die gesamte Unterseite sind stets weißlich. Die Schwanzspitze ist dunkel. Neugeborene Pumas sind beigefarben und kräftig gefleckt; die Fleckenzeichnung verblasst noch während des ersten Lebensjahrs. Aus Südamerika sind auch Schwärzlinge dokumentiert.M. E. Sunquist & F. C. Sunquist (2009). Family Felidae (Cats). In: Don E. Wilson , Russell A. Mittermeier (Hrsg.): Handbook of the Mammals of the World. Volume 1: Carnivores. Lynx Edicions, 2009, ISBN 978-84-96553-49-1, (S. 145 f.). Pumas haben fünf Zehen an den Vorderpfoten und vier an den Hinterpfoten. Sie können ihre Krallen einziehen.  PumasPumas sind sehr beweglich und kräftig. Pumas sind nachweislich in der Lage, vom Boden aus bis zu 5,5 m hoch in einen Baum zu springen. Die Tiere geben ein großes Spektrum unterschiedlicher Laute von sich, die sich zwischen den Geschlechtern unterscheiden. So dienen zwitschernde Laute offenbar der Kommunikation zwischen Müttern und ihrem Nachwuchs, während Schreie offenbar zum Paarungsverhalten gehören. Anders als die eigentlichen Großkatzen sind Pumas jedoch nicht in der Lage zu brüllen. Nordamerikanische Forscher wie Truman Everts beschreiben den Schrei des Pumas als menschenähnlich.Pumas erreichen in der Wildnis ein Alter von 8 bis 13 Jahren.K. Nowell & P. Jackson: „Wild Cats. Status Survey and Conservation Action Plan.“ (PDF; 24,6 MB). 1996, S. 132. Abgerufen am 21. April 2011. In Gefangenschaft werden Berglöwen über 20 Jahre alt, ein nordamerikanischer Puma namens Scratch ist sogar fast 30 Jahre alt geworden.Scratch. Cougar 1977-2007 (archiviert)","The geographic range of the puma is the largest of any terrestrial mammal in the Western Hemisphere (Sunquist and Sunquist 2002), from Canada through the US, Central and South America to the southern tip of Chile.  While the puma is an adaptable cat, being found in every major habitat type of the Americas, including the high Andes (5,800 m in southern Peru: Sunquist and Sunquist 2002), it was eliminated from the entire eastern half of North America within 200 years following European colonization (Nowell and Jackson, 1996).  A remnant Endangered subpopulation persists in Florida, and records of pumas in northeastern Canada and the eastern US are on the rise, indicating possible recolonization (M. Kelly pers. comm. 2007).","Included in CITES Appendix II (eastern and Central American subspecies (P. c. coryi, costaricensis and cougar) Appendix I). The species is protected across much of its range, with hunting prohibited in most of Argentina, and all of Brazil, Bolivia, Chile, Colombia, Costa Rica, French Guiana, Guatemala, Honduras, Nicaragua, Panama, Paraguay, Suriname, Venezuela and Uruguay, and hunting regulations in place in Canada, Mexico, Peru and the United States (Nowell and Jackson 1996).There is a need for the implementation of programs to mitigate conflict resolution for livestock depredation and to study the real effect of puma vs. jaguar depredation on livestock (IUCN Cats Red List workshop, 2007).  Puma occasionally kill humans - especially in North America.","The species is found in a broad range of habitats, in all forest types as well as lowland and montane desert.  Several studies have shown that habitat with dense understory vegetation is preferred, however, pumas can live in very open habitats with only a minimum of vegetative cover (Nowell and Jackson 1996).  Pumas co-occur with jaguars in much of their Latin American range, and may favor more open habitats than their larger competitor, although both can be found in dense forest (Sunquist and Sunquist 2002).Pumas are capable of taking large prey, but when available small to medium-sized prey are more important in their diet (in tropical portions of the range).  This is true of wild prey as well as livestock (IUCN Cats Red List workshop, 2007).  In North America, deer make up 60-80% of the puma's diet, and the mean weight of prey taken is 39-48 kg.  In Florida, however, where deer numbers are low, pumas take smaller prey including feral pigs, raccoons and armadilllos, and deer account for only about 1/3 of the diet (Sunquist and Sunquist 2002).Home range sizes of pumas vary considerably across their geographic distribution, and the smallest ranges tend to occur in areas where prey densities are high and prey are not migratory (Sunquist and Sunquist 2002).  In North America, home range sizes ranged from 32-1,031 km² (Lindzey et al. 1987).","The Canadian population was rougly estimated at 3,500-5,000 and the western US population at 10,000 in the early 1990s (Nowell and Jackson, 1996).  The population of Central and South America is likely much higher, although it is unclear how abundant pumas are in the dense rainforest of the Amazon basin (Nowell and Jackson, 1996). The Florida subpopulation, numbering 70-80, is isolated, and has been supplemented by a reintroduction of pumas from Texas (Sunquist and Sunquist 2002). In Brazil it is considered Near Threatened but subspecies outside the Amazon basic are considered VU (Machado et al. 2005).  It is also considered Near Threatened in Peru (Inrena 2006), Argentina (Diaz and Ojeda 2000) and Colombia (Rodriguez-Mahecha et al., 2006), and Data Deficient (inadequately known) in Chile (CONAMA 2005). Density estimates include:Utah, US: 0.3-0.5/100 km² (Hemker et al. 1984)Idaho, US:  0.77-1.04/100 km² (Laundre and Clark 2003)Peru: 2.4/100 km² (Janson and Emmons 1990)Patagonia: 6/100 km² (Franklin et al. 1999)Pantanal 4.4/100 km² (Crawshaw and Quigley unpubl. in Nowell and Jackson 1996)Belize 2-5/100 km², in Argentina 0.5-0.8/100 km², Bolivia 5-8/100 km² (Kelly et al. in press)W Mexico 3-5/100 km² (Nunez et al. 1998)","Pumas are threatened by habitat loss and fragmentation, and poaching of their wild prey base.  They are persecuted across their range by retaliatory hunting due to livestock depredation, and due to fear that they pose a threat to human life (IUCN Cats Red List workshop, 2007).  Pumas have killed a number of people in western Canada and the US in recent years.  Pumas are legally hunted in many western US states, although hunting was banned by popular referendum in California in 1990.  Road kills are the principal cause of mortality in the endangered Florida panther subpopulation, and heavily travelled roads are a major barrier to puma movements and dispersal (Sunquist and Sunquist 2002).","Bosque de piedemonte","Physical characteristics Cougars are slender and agile members of the cat family. They are the fourth-largest cat;Expanding Cougar Population The Cougar Net.org adults stand about  tall at the shoulders.Florida Panther Facts. Florida Panther Refuge Adult males are around  long nose-to-tail and females average , with overall ranges between  nose to tail suggested for the species in general.Mountain Lion (Puma concolor). Texas Parks and WildlifeEastern Cougar Fact Sheet. New York State Department of Environmental Conservat ion Of this length,  is comprised by the tail.Shivaraju, A. (2003) Puma concolor. Animal Diversity Web, University of Michigan Museum of Zoology. Retrieved on September 15, 2011. Males typically weigh 53 to 100 kg (115 to 220 lb), averaging 62 kg (137 lb). Females typically weigh between 29 and 64 kg (64 and 141 lb), averaging 42 kg (93 lb).Nowell, K. and Jackson, P (2006): Wild Cats. Status Survey and Conservation Action Plan. IUCN/SSC Cat Specialist Group. IUCN, Gland, SwitzerlandPuma concolor – Mountain Lion – Discover Life. Pick4.pick.uga.edu Cougar size is smallest close to the equator, and larger towards the poles.Iriarte, J. Agustin; Franklin, William L.; Johnson, Warren E. and Redford, Kent H. (1990): Biogeographic variation of food habits and body size of the America puma, Oecologia, 85(2): pp. 185  The largest recorded cougar, shot in Arizona, weighed 125.5 kg (276 lb) after its intestines were removed, indicating in life it could have weighed nearly 136.2 kg (300 lb).Brakefield, Tom (1993): Big Cats: Kingdom of Might. Voyageur Press Several male cougars in British Columbia weighed between 86.4 and 95.5 kg (190 to 210 lb).Spalding, D. J.: Cougar in British Columbia, British Columbia Fish and Wildlife BranchThe head of the cat is round and the ears are erect. Its powerful forequarters, neck, and jaw serve to grasp and hold large prey. It has five retractable claws on its forepaws (one a dewclaw) and four on its hind paws. The larger front feet and claws are adaptations to clutching prey.Cougar, Hinterland Who's Who. Canadian Wildlife Service and Canadian Wildlife FederationCougars can be almost as large as jaguars, but are less muscular and not as powerfully built; where their ranges overlap, the cougar tends to be smaller than average. Besides the jaguar, the cougar is on average larger than all felids outside of the Old World lion and tigers. Despite its size, it is not typically classified among the \"big cats\", as it cannot roar, lacking the specialized larynx and hyoid apparatus of Panthera.Weissengruber, GE (2002): Hyoid apparatus and pharynx in the lion (Panthera leo), jaguar (Panthera onca), tiger (Panthera tigris), cheetah (Acinonyx jubatus) and domestic cat (Felis silvestris f. catus), Journal of Anatomy, 201(3): pp. 195–209. Anatomical Society of Great Britain and Ireland Compared to \"big cats\", cougars are often silent with minimal communication through vocalizations outside of the mother-offspring relationship.Hornocker, Maurice G. and Negri, Sharon (December 15, 2009): Cougar: ecology and conservation: pp. 114–. University of Chicago Press Cougars sometimes voice low-pitched hisses, growls, and purrs, as well as chirps and whistles, many of which are comparable to those of domestic cats. They are well known for their screams, as referenced in some of their common names, although these screams are often misinterpreted to be the calls of other animals.About Eastern Cougars. Eastern Cougar Foundation Cougar coloring is plain (hence the Latin concolor) but can vary greatly between individuals and even between siblings. The coat is typically tawny, but ranges to silvery-grey or reddish, with lighter patches on the underbody, including the jaws, chin, and throat. Infants are spotted and born with blue eyes and rings on their tails; juveniles are pale, and dark spots remain on their flanks. Despite anecdotes to the contrary, all-black coloring (melanism) has never been documented in cougars.February 1, 2006: Black cougar more talk than fact. Tahlequah Daily Press The term \"black panther\" is used colloquially to refer to melanistic individuals of other species, particularly jaguars and leopards.Mutant Pumas, messybeast.comCougars have large paws and proportionally the largest hind legs in the cat family. This physique allows it great leaping and short-sprint ability. The cougar is able to leap as high as  in one bound, and as far as 40 to 45 ft (12 to 13.5 m) horizontally.Mountain Lion (Puma, Cougar), San Diego Zoo.org. Zoological Society of San DiegoCougar: Puma concolor: A Saskatchewan Species at Risk. Saskatoon Zoo Society, Canada.Cougar. bluelion.org.Hansen, Kevin (1990): Cougar: The American Lion. Mountain Lion Foundation The cougar's top running speed ranges between ,Cougar. Zoological Wildlife FoundationMountain Lion FAQ and Facts. Mountainlion.org. Retrieved on April 29, 2013. but is best adapted for short, powerful sprints rather than long chases. It is adept at climbing, which allows it to evade canine competitors. Although it is not strongly associated with water, it can swim.Mountain Lion, Felis concolor. Sierra Club Hunting and dietA successful generalist predator, the cougar will eat any animal it can catch, from insects to large ungulates (over 500 kg). Like all cats, it is an obligate carnivore, meaning it needs to feed exclusively on meat to survive. The mean weight of vertebrate prey (MWVP) was positively correlated (r=0.875) with puma body weight and inversely correlated (r=–0.836) with food niche breadth all across the Americas. In general, MWVP was lower in areas closer to the Equator. Its most important prey species are various deer species, particularly in North America; mule deer, white-tailed deer, and even bull elk are taken. Other species such as bighorn sheep, wild horses of Arizona, domestic horses, and domestic livestock such as cattle and sheep are also primary food bases in many areas.Turner, John W.; Morrison, Michael L. (2008): Influence of Predation by Mountain Lions on Numbers and Survivorship of a Feral Horse Population, The Southwestern Naturalist, 46(2): pp. 183–190 A survey of North America research found 68% of prey items were ungulates, especially deer. Only the Florida Panther showed variation, often preferring feral hogs and armadillos. Investigation in Yellowstone National Park showed that elk, followed by mule deer, were the cougar's primary targets; the prey base is shared with the park's gray wolves, with whom the cougar competes for resources.Wildlife: Wolves. Yellowstone National Park* Holly Akenson, James Akenson, Howard Quigley: Winter Predation and Interactions of Wolves and Cougars on Panther Creek in Central Idaho* John K. Oakleaf, Curt Mack, Dennis L. Murray: Winter Predation and Interactions of Cougars and Wolves in the Central Idaho Wilderness Another study on winter kills (November–April) in Alberta showed that ungulates accounted for greater than 99% of the cougar diet. Learned, individual prey recognition was observed, as some cougars rarely killed bighorn sheep, while others relied heavily on the species.Ross, R. (1993): Cougar predation on bighorn sheep in southwestern Alberta during winter, Canadian Journal of Zoology, 75(5): pp. 771–75In the Central and South American cougar range, the ratio of deer in the diet declines. Small to mid-size mammals are preferred, including large rodents such as the capybara. Ungulates accounted for only 35% of prey items in one survey, approximately half that of North America. Competition with the larger jaguar has been suggested for the decline in the size of prey items. Other listed prey species of the cougar include mice, porcupines, beavers, raccoons, hares.Whitaker, John O. The Audubon Society Field Guide to North American Mammals. Chanticleer Press, New York, 1980, pg. 598. ISBN 0-394-50762-2. Birds and small reptiles are sometimes preyed upon in the south, but this is rarely recorded in North America. Not all of their prey is listed here due to their large range.Though capable of sprinting, the cougar is typically an ambush predator. It stalks through brush and trees, across ledges, or other covered spots, before delivering a powerful leap onto the back of its prey and a suffocating neck bite. The cougar is capable of breaking the neck of some of its smaller prey with a strong bite and momentum bearing the animal to the ground.Kills are generally estimated at around one large ungulate every two weeks. The period shrinks for females raising young, and may be as short as one kill every three days when cubs are nearly mature at around 15 months. The cat drags a kill to a preferred spot, covers it with brush, and returns to feed over a period of days. It is generally reported that the cougar is not a scavenger, and will rarely consume prey it has not killed; but deer carcasses left exposed for study were scavenged by cougars in California, suggesting more opportunistic behavior.Bauer, Jim W. (2005): Scavenging behavior in Puma, The Southwestern Naturalist, 50(4): pp. 466–471 Reproduction and life cycleFemales reach sexual maturity between one-and-a-half to three years of age. They typically average one litter every two to three years throughout their reproductive lives,Cougar Discussion Group (January 27, 1999): Utah Cougar Management Plan (Draft). Utah Division of Wildlife Resources though the period can be as short as one year. Females are in estrus for about 8 days of a 23-day cycle; the gestation period is approximately 91 days. Females are sometimes reported as monogamous,Cougars in Canada (Just the Facts). Canadian Geographic Magazine but this is uncertain and polygyny may be more common.Hamilton, Matthew; Hundt, Peter and Piorkowski, Ryan: Mountain Lions. University of Wisconsin, Stevens Point Copulation is brief but frequent. Chronic stress can result in low reproductive rates when in captivity as well as in the field.Bonier, F., H. Quigley, S. Austad (2004): A technique for non-invasively detecting stress response in cougars, Wildlife Society Bulletin, 32(3): pp. 711–717 Only females are involved in parenting. Female cougars are fiercely protective of their cubs, and have been seen to successfully fight off animals as large as American black bears in their defense. Litter size is between one and six cubs; typically two. Caves and other alcoves that offer protection are used as litter dens. Born blind, cubs are completely dependent on their mother at first, and begin to be weaned at around three months of age. As they grow, they begin to go out on forays with their mother, first visiting kill sites, and after six months beginning to hunt small prey on their own. Kitten survival rates are just over one per litter. When cougars are born, they have spots, but they lose them as they grow, and by the age of 2 1/2 years, they will completely be goneStaying safe in cougar country. Wildlife.utah.govYoung adults leave their mother to attempt to establish their own territory at around two years of age and sometimes earlier; males tend to leave sooner. One study has shown high mortality amongst cougars that travel farthest from the maternal range, often due to conflicts with other cougars (intraspecific competition). Research in New Mexico has shown that \"males dispersed significantly farther than females, were more likely to traverse large expanses of non-cougar habitat, and were probably most responsible for nuclear gene flow between habitat patches.\"Sweanor, Linda (2000): Cougar Dispersal Patterns, Metapopulation Dynamics, and Conservation, Conservation Biology, 14(3): pp. 798–808Life expectancy in the wild is reported at eight to 13 years, and probably averages eight to 10; a female of at least 18 years was reported killed by hunters on Vancouver Island. Cougars may live as long as 20 years in captivity. One male North American cougar (P. c. couguar), named Scratch, was two months short of his 30th birthday when he died in 2007.Scratch, bigcatrescue.org Causes of death in the wild include disability and disease, competition with other cougars, starvation, accidents, and, where allowed, human hunting. Feline immunodeficiency virus, an endemic HIV-like virus in cats, is well-adapted to the cougar.Biek, Roman (2003): Epidemiology, Genetic Diversity, and Evolution of Endemic Feline Immunodeficiency Virus in a Population of Wild Cougars, Journal of Virology, 77(17): pp. 9578–89 Social structure and home rangeLike almost all cats, the cougar is a solitary animal. Only mothers and kittens live in groups, with adults meeting only to mate. It is secretive and crepuscular, being most active around dawn and dusk.Estimates of territory sizes vary greatly. Canadian Geographic reports large male territories of 150 to 1000 km2 (58 to 386 sq mi) with female ranges half the size. Other research suggests a much smaller lower limit of 25 km2 (10 sq mi), but an even greater upper limit of 1300 km2 (500 sq mi) for males. In the United States, very large ranges have been reported in Texas and the Black Hills of the northern Great Plains, in excess of 775 km2 (300 sq mi).Mahaffy, James (2004): Behavior of cougar in Iowa and the Midwest. Dordt College Male ranges may include or overlap with those of females but, at least where studied, not with those of other males, which serves to reduce conflict between cougars. Ranges of females may overlap slightly with each other. Scrape marks, urine, and feces are used to mark territory and attract mates. Males may scrape together a small pile of leaves and grasses and then urinate on it as a way of marking territory.Home range sizes and overall cougar abundance depend on terrain, vegetation, and prey abundance. One female adjacent to the San Andres Mountains, for instance, was found with a large range of 215 km2 (83 sq mi), necessitated by poor prey abundance. Research has shown cougar abundances from 0.5 animals to as much as 7 (in one study in South America) per 100 km2 (38 sq mi).Because males disperse farther than females and compete more directly for mates and territory, they are most likely to be involved in conflict. Where a subadult fails to leave his maternal range, for example, he may be killed by his father. When males encounter each other, they hiss, spit, and may engage in violent conflict if neither backs down. Hunting or relocation of the cougar may increase aggressive encounters by disrupting territories and bringing young, transient animals into conflict with established individuals.March 22, 2007: Mountain Lion (Felis concolor) study on Boulder Open Space, Letter to the Parks and Open Space Advisory Committee, Boulder, Colorado. Sinapu","The World Conservation Union (IUCN) currently lists the cougar as a \"least concern\" species. The cougar is regulated under Appendix I of the Convention on International Trade in Endangered Species of Wild Fauna and Flora (CITES),Appendices I, II and III. Convention on International Trade in Endangered Species of Wild Fauna and Flora rendering illegal international trade in specimens or parts. In the United States east of the Mississippi River, the only unequivocally known cougar population is the Florida panther. Until 2011, the United States Fish and Wildlife Service (USFWS) recognized both an Eastern cougar (claimed to be a subspecies by some, denied by others)Bolgiano, Chris (August 1995): Mountain Lion:An Unnatural History of Pumas and People(Hardcover). Stackpole BooksEberhart, George M. (2002): Mysterious Creatures: A Guide to Cryptozoology, Volume 2: pp. 153–161. ABC-CLIO and the Florida panther, affording protection under the Endangered Species Act.1991: Eastern Cougar, Endangered and Threatened Species of the Southeastern United States (The Red Book). U.S. Fish and Wildlife Service1993: Florida Panther, Endangered and Threatened Species of the Southeastern United States (The Red Book). United States Fish and Wildlife Service Certain taxonomic authorities have collapsed both designations into the North American cougar, with Eastern or Florida subspecies not recognized, while a subspecies designation remains recognized by some conservation scientists. The most recent documented count for the Florida sub-population is 87 individuals, reported by recovery agencies in 2003.Florida Fish and Wildlife Conservation Commission. 2002–2003 Panther Genetfic Restoration Annual Report In March 2011, the USFWS declared the Eastern cougar extinct. However, with the taxonomic uncertainty about its existence as a subspecies as well as the possibility of eastward migration of cougars from the western range, the subject remains open.This uncertainty has been recognized by Canadian authorities. The Canadian federal agency called Committee on the Status of Endangered Wildlife in Canada rates its current data as \"insufficient\" to draw conclusions regarding the eastern cougar's survival, and says on its Web site \"Despite many sightings in the past two decades from eastern Canada, there are insufficient data to evaluate the taxonomy or assign a status to this cougar.\" Notwithstanding numerous reported sightings in Ontario, Quebec, New Brunswick and Nova Scotia, it has been said that the evidence is inconclusive: \". . . there may not be a distinct 'eastern' subspecies, and some sightings may be of escaped pets.\"Committee on Status of Endangered Wildlife in Canada. Cosewic.gc.ca. Retrieved on September 15, 2011.Eastern Cougar, Nature Canada. Naturecanada.ca. Retrieved on September 15, 2011.The cougar is also protected across much of the rest of its range. As of 1996, cougar hunting was prohibited in Argentina, Brazil, Bolivia, Chile, Colombia, Costa Rica, French Guiana, Guatemala, Honduras, Nicaragua, Panama, Paraguay, Suriname, Venezuela, and Uruguay. The cat had no reported legal protection in Ecuador, El Salvador, and Guyana. Regulated cougar hunting is still common in the United States and Canada, although they are protected from all hunting in the Yukon; it is permitted in every U.S. state from the Rocky Mountains to the Pacific Ocean, with the exception of California. Texas is the only state in the United States with a viable population of cougars that does not protect that population in some way. In Texas, cougars are listed as nuisance wildlife and any person holding a hunting or a trapping permit can kill a cougar regardless of the season, number killed, sex or age of the animal.TPWD: Mountain Lions. Tpwd.state.tx.us (July 16, 2007). Retrieved on September 15, 2011.  Killed animals are not required to be reported to Texas Parks and Wildlife Department. Conservation work in Texas is the effort of a non profit organization, Balanced Ecology Inc (BEI), as part of their Texas Mountain Lion Conservation Project. Cougars are generally hunted with packs of dogs, until the animal is 'treed'. When the hunter arrives on the scene, he shoots the cat from the tree at close range. The cougar cannot be legally killed without a permit in California except under very specific circumstances, such as when a cougar is in act of pursuing livestock or domestic animals, or is declared a threat to public safety. Permits are issued when owners can prove property damage on their livestock or pets.  For example, multiple dogs have been attacked and killed, sometimes while with the owner.  Many attribute this to the protection cougars have from being hunted and are now becoming desensitized to humans; most are removed from the population after the attacks have already occurred.   Statistics from the Department of Fish and Game indicate that cougar killings in California have been on the rise since 1970s with an average of over 112 cats killed per year from 2000 to 2006 compared to six per year in the 1970s. They also state on their website that there is a healthy number of cougars in California.  The Bay Area Puma Project aims to obtain information on cougar populations in the San Francisco Bay area and the animals' interactions with habitat, prey, humans, and residential communities.Bay Area Puma Project (BAPP). Felidae Conservation FundConservation threats to the species include persecution as a pest animal, environmental degradation and habitat fragmentation, and depletion of their prey base. Wildlife corridors and sufficient range areas are critical to the sustainability of cougar populations. Research simulations have shown that the animal faces a low extinction risk in areas of 2200 km2 (850 sq mi) or more. As few as one to four new animals entering a population per decade markedly increases persistence, foregrounding the importance of habitat corridors.Beier, Paul (1993): Determining Minimum Habitat Areas and Habitat Corridors for Cougars, Conservation Biology, 7(1): pp. 94–108On March 2, 2011, the United States Fish and Wildlife Service declared the Eastern cougar (Puma concolor couguar) officially extinct.Northeast Region, U.S. Fish and Wildlife Service. Fws.gov. Retrieved on September 15, 2011.","Distribution and habitat The cougar has the largest range of any wild land animal in the Americas. Its range spans 110 degrees of latitude, from northern Yukon in Canada to the southern Andes. It is one of only three cat species, along with the bobcat and Canada lynx, native to Canada. Its wide distribution stems from its adaptability to virtually every habitat type: it is found in all forest types, as well as in lowland and mountainous deserts. The cougar prefers regions with dense underbrush, but can live with little vegetation in open areas. Its preferred habitats include precipitous canyons, escarpments, rim rocks, and dense brush.The cougar was extirpated across much of its eastern North American range (with the exception of Florida) in the two centuries after European colonization, and faced grave threats in the remainder of its territory. Currently, it ranges across most western American states, the Canadian provinces of Alberta, Saskatchewan and British Columbia, and the Canadian territory of Yukon. There have been widely debated reports of possible recolonization of eastern North America.Marschall, Laurence A. (2005): Bookshelf, Natural Selections. Natural History Magazine DNA evidence has suggested its presence in eastern North America,Belanger, Joe (May 25, 2007): DNA tests reveal cougars roam region. London Free Press while a consolidated map of cougar sightings shows numerous reports, from the mid-western Great Plains through to eastern Canada.Board of Directors (2004): The \"Big\" Picture. The Cougar Network The Cougar Network methodology is recognized by the U.S. Fish and Wildlife Service. The Quebec wildlife services (known locally as MRNF) also considers cougar to be present in the province as a threatened species after multiple DNA tests confirmed cougar hair in lynx mating sites.2010: Your part in helping endangered species. Ministry of Wildlife and Natural Resources, Quebec, Canada The only unequivocally known eastern population is the Florida panther, which is critically endangered. There have been unconfirmed sightings in Elliotsville Plantation, Maine (north of Monson); and in New Hampshire, there have been unconfirmed sightings as early as 1997.Davidson, Rick (2009): NH Sightings Catamount. Beech River Books In 2009, the Michigan Department of Natural Resources confirmed a cougar sighting in Michigan's Upper Peninsula.Skinner, Victor (November 15, 2009): Photo shows cougar presence in Michigan, The Grand Rapids Press Typically, extreme-range sightings of cougars involve young males, which can travel great distances to establish ranges away from established males; all four confirmed cougar kills in Iowa since 2000 involved males.On April 14, 2008, police shot and killed a cougar on the north side of Chicago, Illinois. DNA tests were consistent with cougars from the Black Hills of South Dakota. Less than a year later, on March 5, 2009, a cougar was photographed and unsuccessfully tranquilized by state wildlife biologists in a tree near Spooner, Wisconsin, in the northwestern part of the state.The Indiana Department of Natural Resources used motion-sensitive cameras to confirm the presence of a cougar in Greene County in southern Indiana on May 7, 2010. Another sighting in late 2009 in Clay County in west-central Indiana was confirmed by the DNR.May 7, 2010: DNR confirms Indiana mountain lion sighting. WISHTV8.comOn June 10, 2011, a cougar was observed roaming near Greenwich, Connecticut. State officials at the time said they believed it was a released pet.  On June 11, 2011, a cougar, believed to be the same animal as the one observed in Greenwich, was killed by a car on the Wilbur Cross Parkway in Milford, Connecticut.  When wildlife officials examined the cougar's DNA, they concluded it was a wild cougar from the Black Hills of South Dakota, which had wandered at least 1,500 miles east over an indeterminate time period.David Baron The Cougar Behind Your Trash Can. New York Times. July 29, 2011In October 2012, a trail camera in Morgan County, Illinois snapped a photograph of a cougar, and on November 6, 2012 a trail camera in Pike County captured a photo also believed to be of a cougar.South of the Rio Grande, the International Union for the Conservation of Nature and Natural Resources (IUCN) lists the cat in every Central and South American country. While specific state and provincial statistics are often available in North America, much less is known about the cat in its southern range.Cougar facts. National Wildlife FederationThe cougar's total breeding population is estimated at less than 50,000 by the IUCN, with a declining trend. US state-level statistics are often more optimistic, suggesting cougar populations have rebounded. In Oregon, a healthy population of 5,000 was reported in 2006, exceeding a target of 3,000.2006: Cougar Management Plan, Wildlife Division: Wildlife Management Plans. Oregon Department of Fish and Wildlife California has actively sought to protect the cat and a similar number of cougars has been suggested, between 4,000 and 6,000.2004: Mountain Lions in California. California Department of Fish and GameIn 2012 research in Río Los Cipreses National Reserve, Chile, based in 18 motion-sensitive cameras counted a population of two males and two females, one of them with at least two cubs, in an area of 600 km2, that is 0.63 cougars every 100 km2.Research of Nicolás Guarda, supported by Conaf, Pontifical Catholic University of Chile,  and a private Enterprise. See article in Chilean newspaper La Tercera, Investigación midió por primera vez población de pumas en zona central, retrieved on January 28, 2013, in Spanish Language. Ecological role Aside from humans, no species preys upon mature cougars in the wild, although conflicts with other predators or scavengers occur.  The Yellowstone National Park ecosystem provides a fruitful microcosm to study inter-predator interaction in North America. Of the three large predators, the massive Grizzly Bear appears dominant, often although not always able to drive both the gray wolf pack and the cougar off their kills. One study found that  American black bears visited 24% of cougar kills in Yellowstone and Glacier National Parks, usurping 10% of carcasses. Bears gained up to 113%, and cougars lost up to 26%, of their respective daily energy requirements from these encounters.COSEWIC. Canadian Wildlife Service (2002): Assessment and Update Status Report on the Grizzly Bear (Ursus arctos). Environment Canada Accounts of cougars and black bears killing each other in fights to the death have been documented from the 19th century.S. C. Turnbo: A Fight to a Finish Between a Bear and Panther. thelibrary.orgS. C. Turnbo: Finding a Panther Guarding a Dead Bear. thelibrary.org In spite of the size and power of the cougar, there have also been accounts of both brown and black bears killing cougars, either in disputes or in self-defense.Cougar vs Bears Accounts. Everything about the Cougar/Mountain LionHornocker, M., and S. Negri (Eds.). (2009). Cougar: ecology and conservation. University of Chicago Press. Chicago, IL, ISBN 0226353443.The gray wolf and the cougar compete more directly for prey, especially in winter. While individually more powerful than the gray wolf, a solitary cougar may be dominated by the pack structure of the canines. Wolves can steal kills and occasionally kill the cat. One report describes a large pack of 7 to 11 wolves killing a female cougar and her kittens.Park wolf pack kills mother cougar. forwolves.org Conversely, lone wolves are at a disadvantage, and have been reported killed by cougars. Various accounts of cougars killing lone wolves, including a six-year old female, have also been documented.Wolf B4 Killed by Mountain Lion?. forwolves.orgAutopsy Indicates Cougar Killed Wolf. igorilla.comMountain lions kill collared wolves in Bitteroot. missoulian.com Wolves more broadly affect cougar population dynamics and distribution by dominating territory and prey opportunities, and disrupting the feline's behavior. Preliminary research in Yellowstone, for instance, has shown displacement of the cougar by wolves.Overview: Gray Wolves. Greater Yellowstone Learning Center In nearby Sun Valley, Idaho, a recent cougar/wolf encounter that resulted in the death of the cougar was documented.Predators clash above Elkhorn. Idaho Mountain Express One researcher in Oregon noted: \"When there is a pack around, cougars are not comfortable around their kills or raising kittens ... A lot of times a big cougar will kill a wolf, but the pack phenomenon changes the table.\"Cockle, Richard (October 29, 2006): Turf wars in Idaho's wilderness. The Oregonian Both species, meanwhile, are capable of killing mid-sized predators, such as bobcats and coyotes, and tend to suppress their numbers. Although cougars can kill coyotes, the latter have been documented attempting to prey on cougar cubs.Cougars vs. coyotes photos draw Internet crowd. missoulian.com In the southern portion of its range, the cougar and jaguar share overlapping territory.Hamdig, Paul: Sympatric Jaguar and Puma. Ecology Online Sweden The jaguar tends to take larger prey and the cougar smaller where they overlap, reducing the cougar's size and also further reducing the likelihood of direct competition. Of the two felines, the cougar appears best able to exploit a broader prey niche and smaller prey.Rodrigo Nuanaez, Brian Miller, and Fred Lindzey (2000): Food habits of jaguars and pumas in Jalisco, Mexico, Journal of Zoology, 252(3): pp. 373As with any predator at or near the top of its food chain, the cougar impacts the population of prey species. Predation by cougars has been linked to changes in the species mix of deer in a region. For example, a study in British Columbia observed that the population of mule deer, a favored cougar prey, was declining while the population of the less frequently preyed-upon white-tailed deer was increasing.Robinson, Hugh S. (2002): Cougar predation and population growth of sympatric mule deer and white-tailed deer, Canadian Journal of Zoology, 80(3): pp. 556–68 The Vancouver Island marmot, an endangered species endemic to one region of dense cougar population, has seen decreased numbers due to cougar and gray wolf predation.Bryant, Andrew A. (2005): Cougar predation and population growth of sympatric mule deer and white-tailed deer, Canadian Journal of Zoology, 83(5): pp. 674–82  Nevertheless, there is a measurable effect on the quality of deer populations by puma predation.Fountain, Henry (November 16, 2009) \"Observatory: When Mountain Lions Hunt, They Prey on the Weak\". New York Times.Weaver, John L.; Paquet, Paul C.; Ruggiero, Leonard F. (1996): Resilience and Conservation of Large Carnivores in the Rocky Mountains, Conservation Biology, 10(4): pp. 964In the southern part of South America, the puma is a top level predator that has controlled the population of guanaco and other species since prehistoric times.","The cougar (Puma concolor), also known as the mountain lion, puma, panther, mountain cat, or catamount, is a large cat of the family Felidae native to the Americas. Its range, from the Canadian Yukon to the southern Andes of South America, is the greatest of any large wild terrestrial mammal in the Western Hemisphere. An adaptable, generalist species, the cougar is found in most American habitat types. It is the second heaviest cat in the Western Hemisphere, after the jaguar. Solitary by nature and nocturnal,Cougars. US National Park Service. the cougar is most closely related to smaller felines and is nearer genetically to the domestic cat than true lions.An excellent stalk-and-ambush predator, the cougar pursues a wide variety of prey. Primary food sources include ungulates such as deer, elk, moose, and bighorn sheep, as well as domestic cattle, horses and sheep, particularly in the northern part of its range. It will also hunt species as small as insects and rodents. This cat prefers habitats with dense underbrush and rocky areas for stalking, but can also live in open areas. The cougar is territorial and survives at low population densities. Individual territory sizes depend on terrain, vegetation, and abundance of prey. While large, it is not always the apex predator in its range, yielding to the jaguar, grey wolf, American black bear, and grizzly bear. It is reclusive and usually avoids people. Fatal attacks on humans are rare, but have been trending upward in recent years as more people enter their territory.Excessive hunting following European colonization of the Americas and the ongoing human development of cougar habitat has caused populations to drop in most parts of its historical range. In particular, the cougar was extirpated in eastern North America in the beginning of the 20th century, except for an isolated subpopulation in Florida. However, in recent decades, breeding populations have moved east into the far western parts of the Dakotas, Nebraska, and Oklahoma. Transient males have been verified in Minnesota,Shot fells elusive cougar chased out of a culvert. StarTribune.com (December 1, 2011). Retrieved on April 29, 2013. Wisconsin, Iowa,Mountain Lion Shot Early Friday Morning in Monona County. Iowa Department of Natural Resources, iowadnr.gov (December 23, 2011)Mountain lion shot by police in Monona County - KWWL.com - News & Weather for Waterloo, Dubuque, Cedar Rapids & Iowa City, Iowa |. KWWL.com. Retrieved on April 29, 2013. the Upper Peninsula of Michigan, and Illinois, where a cougar was shot in the city limits of ChicagoThe Cougar Network – Using Science to Understand Cougar Ecology. Cougarnet.orgNovember 5, 2009: Trail cam photo of cougar in the eastern Upper Peninsula. Twitpic.comMay 10, 2010: Indiana confirms mountain lion in Green County. Poorboysoutdoors.com and, in at least one instance, observed as far east as Connecticut.July 26, 2011: Mountain lion killed in Conn. had walked from S. Dakota. USA TodayConnecticut Mountain Lion Likely Came From The Black Hills, NPR News","A pumapard is a hybrid animal resulting from a union between a cougar and a leopard. Three sets of these hybrids were bred in the late 1890s and early 1900s by Carl Hagenbeck at his animal park in Hamburg, Germany. Most did not reach adulthood. One of these was purchased in 1898 by Berlin Zoo. A similar hybrid in Berlin Zoo purchased from Hagenbeck was a cross between a male leopard and a female puma. Hamburg Zoo's specimen was the reverse pairing, the one in the black-and-white photo, fathered by a puma bred to an Indian leopardess.Whether born to a female puma mated to a male leopard, or to a male puma mated to a female leopard, pumapards inherit a form of dwarfism. Those reported grew to only half the size of the parents. They have a puma-like long body (proportional to the limbs, but nevertheless shorter than either parent), but short legs. The coat is variously described as sandy, tawny or greyish with brown, chestnut or \"faded\" rosettes.Geocites – Liger & Tigon Info","With its vast range across the length of the Americas, Puma concolor has dozens of names and various references in the mythology of the indigenous Americans and in contemporary culture. The cat has many names in English, of which cougar, puma and mountain lion are popular. \"Mountain lion\" was a term first used in writing in 1858 from the diary of George A. Jackson of Colorado.George A. Jackson (1935): George A. Jackson's Diary of 1858-1859, Colorado Magazine, 6: pp. 201–214 However, mountain lion is technically incorrect, as its range is not limited to mountain regions, nor can it roar like a true lion, the latter being of the genus Panthera. Other names include \"catamount\" (probably a contraction from \"cat of the mountain\"), \"panther\", \"mountain screamer\" and \"painter\". Lexicographers regard \"painter\" as a primarily upper-Southern US regional variant on \"panther\".painter, transcription of the American Heritage Dictionary, Bartleby.com  The word panther, while technically referring to all members of the genus Panthera, is commonly used to specifically designate the black panther, a melanistic jaguar or leopard, and the Florida panther, a subspecies of cougar (Puma concolor coryi).Puma concolor holds the Guinness record for the animal with the highest number of names, presumably due to its wide distribution across North and South America. It has over 40 names in English alone.2004: The Guinness Book of World Records: pp. 49\"Cougar\" may be borrowed from the archaic Portuguese çuçuarana; the term was originally derived from the Tupi language. A current form in Brazil is suçuarana. It may also be borrowed from the Guaraní language term guaçu ara or guazu ara. Less common Portuguese terms are onça-parda (lit. brown onça, in distinction of the black-spotted [yellow] one, onça-pintada, the jaguar) or leão-baio (lit. chestnut lion), or unusually non-native puma or leão-da-montanha, more common names for the animal when native to a region other than South America (especially for those who do not know that suçuaranas are found elsewhere but with a different name). People in rural regions often refer to both the cougar and to the jaguar as simply gata (lit. she-cat), and outside of the Amazon, both are colloquially referred to as simply onça by many people (that is also a name for the leopard in Angola).In the 17th century, German naturalist Georg Marcgrave named the cat the cuguacu ara. Marcgrave's rendering was reproduced by his associate, Dutch naturalist Willem Piso, in 1648. Cuguacu ara was then adopted by English naturalist John Ray in 1693.Words to the Wise, Take Our Word for it(205): pp. 2 The French naturalist Georges-Louis Leclerc, Comte de Buffon in 1774 (probably influenced by the word \"jaguar\") converted the cuguacu ara to cuguar, from when it was later modified to \"cougar\" in English.cougar. Merriam-Webster Dictionary Online1989: cougar, Oxford Dictionaries Online, Oxford University PressThe first English record of \"puma\" was in 1777, where it had come from the Spanish, who in turn borrowed it from the Peruvian Quechua language in the 16th century, where it means \"powerful\".The Puma. Projeto Puma Puma is also the most common name cross-linguistically.","In mythologyThe grace and power of the cougar have been widely admired in the cultures of the indigenous peoples of the Americas. The Inca city of Cusco is reported to have been designed in the shape of a cougar, and the animal also gave its name to both Inca regions and people. The Moche people represented the puma often in their ceramics.Berrin, Katherine & Larco Museum. The Spirit of Ancient Peru:Treasures from the Museo Arqueológico Rafael Larco Herrera. New York: Thames and Hudson, 1997. The sky and thunder god of the Inca, Viracocha, has been associated with the animal.Tarmo, Kulmar: On the role of Creation and Origin Myths in the Development of Inca State and Religion, Electronic Journal of Folklore. Estonian Folklore InstituteIn North America, mythological descriptions of the cougar have appeared in the stories of the Hocąk language (\"Ho-Chunk\" or \"Winnebago\") of Wisconsin and Illinois Cougars, The Encyclopedia of Hočąk (Winnebago) Mythology. Retrieved: 2009/12/08. and the Cheyenne, amongst others. To the Apache and Walapai of Arizona, the wail of the cougar was a harbinger of death.Living with Wildlife: Cougars. USDA Wildlife Services The Algonquins and Ojibwe believe that the cougar lived in the underworld and was wicked, whereas it was a sacred animal among the Cherokee.Matthews, John and Matthews, Caitlín (2005): The Element Encyclopedia of Magical Creatures: pp. 364. HarperElement Livestock predationDuring the early years of ranching, cougars were considered on par with wolves in destructiveness. According to figures in Texas in 1990, 86 calves (0.0006% of a total of 13.4 million cattle & calves in Texas), 253 Mohair goats, 302 Mohair kids, 445 sheep (0.02% of a total of 2.0 million sheep & lambs in Texas) and 562 lambs (0.04% of 1.2 million lambs in Texas) were confirmed to have been killed by cougars that year.Cattle report 1990. National Agricultural Statistics ServiceSheep and Goats report 1990. National Agricultural Statistics Service In Nevada in 1992, cougars were confirmed to have killed 9 calves, 1 horse, 4 foals, 5 goats, 318 sheep and 400 lambs. In both cases, sheep were the most frequently attacked. Some instances of surplus killing have resulted in the deaths of 20 sheep in one attack.Mountain Lion Fact Sheet. Abundant Wildlife Society of North America A cougar's killing bite is applied to the back of the neck, head, or throat and inflict puncture marks with their claws usually seen on the sides and underside of the prey, sometimes also shredding the prey as they hold on.  Coyotes also typically bite the throat region but do not inflict the claw marks and farmers will normally see the signature zig-zag pattern that coyotes create as they feed on the prey whereas cougars typically drag in a straight line.  The work of a cougar is generally clean, differing greatly from the indiscriminate mutilation by coyotes and feral dogs. The size of the tooth puncture marks also helps distinguish kills made by cougars from those made by smaller predators.Cougar Predation – Description. Procedures for Evaluating Predation on Livestock and Wildlife Attacks on humans Due to the expanding human population, cougar ranges increasingly overlap with areas inhabited by humans. Attacks on humans are very rare, as cougar prey recognition is a learned behavior and they do not generally recognize humans as prey.McKee, Denise (2003): Cougar Attacks on Humans: A Case Report, Wilderness and Environmental Medicine, 14(3): pp. 169–73. Wilderness Medical Society Attacks on people, livestock, and pets may occur when a puma habituates to humans or is in a condition of severe starvation. Attacks are most frequent during late spring and summer, when juvenile cougars leave their mothers and search for new territory.Between 1890 and 1990, in North America there were 53 reported, confirmed attacks on humans, resulting in 48 nonfatal injuries and 10 deaths of humans (the total is greater than 53 because some attacks had more than one victim).Beier, Paul (1991): Cougar attacks on humans in United States and Canada, Wildlife Society Bulletin. Northern Arizon University By 2004, the count had climbed to 88 attacks and 20 deaths.Confirmed mountain lion attacks in the United States and Canada 1890 – present. Arizona Game and Fish DepartmentWithin North America, the distribution of attacks is not uniform. The heavily populated state of California has seen a dozen attacks since 1986 (after just three from 1890 to 1985), including three fatalities. Lightly populated New Mexico reported an attack in 2008, the first there since 1974.New Mexico Department of Game and Fish: Search continues for mountain lion that killed Pinos Altos man, June 23, 2008; Wounded mountain lion captured, killed near Pinos Altos, June 25, 2008; Second mountain lion captured near Pinos Altos, July 1, 2008As with many predators, a cougar may attack if cornered, if a fleeing human stimulates their instinct to chase, or if a person \"plays dead\". Standing still however may cause the cougar to consider a person easy prey.Subramanian, Sushma (April 14, 2009): Should You Run or Freeze When You See a Mountain Lion?. Scientific American  Exaggerating the threat to the animal through intense eye contact, loud but calm shouting, and any other action to appear larger and more menacing, may make the animal retreat. Fighting back with sticks and rocks, or even bare hands, is often effective in persuading an attacking cougar to disengage.1991: Safety Guide to Cougars, Environmental Stewardship Division. Government of British Columbia, Ministry of EnvironmentWhen cougars do attack, they usually employ their characteristic neck bite, attempting to position their teeth between the vertebrae and into the spinal cord. Neck, head, and spinal injuries are common and sometimes fatal. Children are at greatest risk of attack, and least likely to survive an encounter. Detailed research into attacks prior to 1991 showed that 64% of all victims – and almost all fatalities – were children. The same study showed the highest proportion of attacks to have occurred in British Columbia, particularly on Vancouver Island where cougar populations are especially dense. Preceding attacks on humans, cougars display aberrant behavior, such as activity during daylight hours, a lack of fear of humans, and stalking humans. There have sometimes been incidents of pet cougars mauling people.","The cougar is the largest of the small cats. It is placed in the subfamily Felinae, although its bulk characteristics are similar to those of the big cats in the subfamily Pantherinae. The family Felidae is believed to have originated in Asia about 11 million years ago. Taxonomic research on felids remains partial, and much of what is known about their evolutionary history is based on mitochondrial DNA analysis, as cats are poorly represented in the fossil record, and there are significant confidence intervals with suggested dates. In the latest genomic study of Felidae, the common ancestor of today's Leopardus, Lynx, Puma, Prionailurus, and Felis lineages migrated across the Bering land bridge into the Americas 8.0 to 8.5 million years ago (Mya). The lineages subsequently diverged in that order.Johnson, W.E., Eizirik, E., Pecon-Slattery, J., Murphy, W.J., Antunes, A., Teeling, E. & O'Brien, S.J. (January 6, 2006): The Late Miocene radiation of modern Felidae: A genetic assessment, Science, 311(5757): pp. 73–77 North American felids then invaded South America 3 Mya as part of the Great American Interchange, following formation of the Isthmus of Panama. The cougar was originally thought to belong in Felis (Felis concolor), the genus which includes the domestic cat. As of 1993, it is now placed in Puma along with the jaguarundi, a cat just a little more than a tenth its weight.Studies have indicated the cougar and jaguarundi are most closely related to the modern cheetah of Africa and western Asia,Culver, M. (2000): Genomic Ancestry of the American Puma, Journal of Heredity, 91(3): pp. 186–97 but the relationship is unresolved. The cheetah lineage is suggested to have diverged from the Puma lineage in the Americas (see American cheetah) and migrated back to Asia and Africa, while other research suggests the cheetah diverged in the Old World itself.Barnett, Ross (August 9, 2005): Evolution of the extinct Sabretooths and the American cheetah-like cat, Current Biology, 15(15): pp. R589–R590 The outline of small feline migration to the Americas is thus unclear.Recent studies have demonstrated a high level of genetic similarity among the North American cougar populations, suggesting they are all fairly recent descendants of a small ancestral group. Culver et al. suggest the original North American population of Puma concolor was extirpated during the Pleistocene extinctions some 10,000 years ago, when other large mammals, such as Smilodon, also disappeared. North America was then repopulated by a group of South American cougars. SubspeciesUntil the late 1980s, as many as 32 subspecies were recorded; however, a recent genetic study of mitochondrial DNA found many of these are too similar to be recognized as distinct at a molecular level. Following the research, the canonical Mammal Species of the World (3rd ed.) recognizes six subspecies, five of which are solely found in Latin America:Argentine puma  includes the previous subspecies and synonyms hudsonii and puma (Marcelli, 1922) Costa Rican cougar  Eastern South American cougar  includes the previous subspecies and synonyms acrocodia, borbensis, capricornensis, concolor (Pelzeln, 1883), greeni, and nigra North American cougar   includes the previous subspecies and synonyms arundivaga, aztecus, browni, californica, coryi, floridana, hippolestes, improcera, kaibabensis, mayensis, missoulensis, olympus, oregonensis, schorgeri, stanleyana, vancouverensis, and youngi Northern South American cougar  includes the previous subspecies and synonyms bangsi, incarum, osgoodi, soasoaranna, sussuarana, soderstromii, suçuaçuara, and wavula Southern South American puma  includes the previous subspecies and synonyms araucanus, concolor (Gay, 1847), patagonica, pearsoni, and puma (Trouessart, 1904)The status of the Florida panther, here collapsed into the North American cougar, remains uncertain. It is still regularly listed as subspecies P. c. coryi in research works, including those directly concerned with its conservation.Conroy, Michael J. (2006): Improving The Use Of Science In Conservation: Lessons From The Florida Panther, Journal of Wildlife Management, 70(1): pp. 1–7 Culver et al. noted low microsatellite variation in the Florida panther, possibly due to inbreeding; responding to the research, one conservation team suggests, \"the degree to which the scientific community has accepted the results of Culver et al. and the proposed change in taxonomy is not resolved at this time.\"The Florida Panther Recovery Team (January 31, 2006): Florida Panther Recovery Program (Draft). U.S. Fish and Wildlife Service"]}*/
	/* ,"vernacularNamesSerialized":{"values":["en # Cougar","en # Puma","es # Puma","en # mountain lion","en # Cougar"," # puma","de # Puma"," # Puma","en # Cougar","en # Cougar","en # Puma","es # Puma","es # Puma","en # mountain lion","en # mountain lion","en # Cougar","en # Deer Tiger","es # León Americano","es # León Bayo","es # León Colorado","es # León De Montaña","es # Mitzli","en # Mountain Lion","es # Onza Bermeja","en # Puma","en # Red Tiger","en # Cougar"]}); */
	// // result.setSynonym(false);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(7193927);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(7193927);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor subsp. concolor");
	result.setCanonicalName("Puma concolor concolor");
	// result.setAuthorship("");
	// result.setPublishedIn("Mantissa Plantarum vol. 2 p. 266");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setTaxonID("119916764");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The northern South American cougar (Puma concolor concolor) is a subspecies of the cougar that lives in the northern part of South America, from Colombia and Venezuela to Peru and northern Brazil. It is the nominate subspecies of Puma concolor. It preys on birds, deer, sloths, mice, frogs, agoutis, and lizards.");
	/*
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("mountain lion");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("puma");
	vernacularName.setLanguage(Language.ENGLISH);
	*/
	/* "descriptionsSerialized":{"values":["The northern South American cougar (Puma concolor concolor) is a subspecies of the cougar that lives in the northern part of South America, from Colombia and Venezuela to Peru and northern Brazil. It is the nominate subspecies of Puma concolor. It preys on birds, deer, sloths, mice, frogs, agoutis, and lizards."]} */
	/* ,"vernacularNamesSerialized":{"values":["en # mountain lion","en # puma"]}); */
	// // result.setSynonym(false);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164589);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164589);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor anthonyi (Nelson & Goldman, 1931)");
	result.setCanonicalName("Puma concolor anthonyi");
	// result.setAuthorship(" (Nelson & Goldman, 1931)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119916924");
	// // result.setSynonym(false);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164590);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164590);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor couguar (Kerr, 1792)");
	result.setCanonicalName("Puma concolor couguar");
	// result.setAuthorship(" (Kerr, 1792)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119916780");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Eastern cougar (Puma concolor couguar) is considered by many puma biologists to be a subspecies of the North American cougar, while others believe recent genetic research suggests all North American cougars are a single subspecies. The eastern subspecies was deemed extinct by a U.S. Fish and Wildlife Service (USF&WS) evaluation in 2011, while a parallel Canadian organization has taken no position on the question.USF&WS officials believe that cougars found in eastern North America during recent years have genetic origins in South America (as escaped captives) or are from western North America (as wandering individuals). Others say these cats are surviving members of the eastern subspecies.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("In 1792, Robert Kerr of the Royal Physical Society and Royal Society of Surgeons assigned the name Felis couguar to eastern North America cougars north of Florida. John Audubon in 1851 believed that cougars in both North and South America were indistinguishable. The eastern cougar was first assigned to the subspecies Felis concolor couguar and the Florida panther (F. c. coryi). Young and Goldman based their description of the eastern subspecies on their examination of eight of the existing 26 historic specimens.In 1955, Jackson described a new subspecies, the Wisconsin puma (F. c. schorgeri), from a small sample of skulls.A 1981 taxonomy (Hall) accepted F. c. schorgeri, the Wisconsin puma, and also extended the range of the eastern puma into Nova Scotia and mapped the Florida panther’s (F. c. coryi) range as far north as South Carolina and southwestern Tennessee.Hall, E.R. 1981. The mammals of North America, second edition. John Wiley and Sons, New YorkIn 2000, Culver et al., recommended that based on recent genetic research, all North American cougars be classified as a single subspecies, Puma concolor couguar following the oldest named subspecies (Kerr in 1792).The 2005 edition of Mammal Species of the World followed Culver’s recommendations. This revision was made by Dr. W. Chris Wozencraft of Bethel University, Indiana, as the sole reviewer. However, the publication's Web site as of 2011, as well as that of its affiliate, the Smithsonian National Museum of Natural History, continued to mention the Puma concolor couguar (or eastern cougar) as a subspecies of Puma concolor.Puma concolor. Mammal Species of the World. Bucknell.eduNorth American Mammals: Puma concolor. Mnh.si.eduDr. Judith Eger, Royal Ontario Museum, Toronto, Ontario, chair of the American Society of Mammalogists checklist committee, believes that the Culver work was improper, as it offered no evaluation of the existing subspecies of the puma and failed to include morphological, ecological, and behavioral considerations. According to Eger, the Culver revision is only accepted by some puma biologists.The U.S. Fish and Wildlife Service continues to accept the Young and Goldman taxonomy. \"While more recent genetic information introduces significant ambiguities, a full taxonomic analysis is necessary to conclude that a revision to the Young and Goldman (1946) taxonomy is warranted,\" the agency said in 2011.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Until around 1990, reports of mountain lions in the Midwest and East were highly influenced by the \"Bigfoot factor,\" according to Mark Dowling, co-founder of the Eastern Cougar Network. \"None of it was really real,\" he said in an interview. But the situation has changed dramatically since that time according to Dowling, whose group collects and disseminates data on the shifting mountain lion population.August 5, 2003: Mountain Lions Headed for Atlantic City?, Insight on the News, 19(17): pp. 16–17Dowling said in 2003 that sightings in the eastern half of the nation, including Michigan, etc., were \"almost certainly\" escaped captives, but he added that the notion that (Western) cougars \"will eventually reach New Jersey\" is a reasonable prediction, in part due to increased populations of whitetail deer.However, some of these cougars found far in the east were established to be of western origin. As noted in an opinion piece by David Baron in the New York Times, concerning a cougar killed by a car in Connecticut in 2011:\"Wildlife officials, who at first assumed the cat was a captive animal that had escaped its owners, examined its DNA and concluded that it was a wild cougar from the Black Hills of South Dakota. It had wandered at least 1,500 miles before meeting its end at the front of an S.U.V. in Connecticut. That is one impressive walkabout.\"\"You have to appreciate this cat’s sense of irony, too. The cougar showed up in the East just three months after the Fish and Wildlife Service declared the eastern cougar extinct, a move that would exempt the officially nonexistent subspecies of the big cat from federal protection. Perhaps this red-state cougar traveled east to send a message to Washington: the federal government can make pronouncements about where cougars are not supposed to be found, but a cat’s going to go where a cat wants to go.\"");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The U.S. Fish and Wildlife Service in 2007 began a review of scientific and commercial information to determine the status of the endangered eastern cougar, the first review the service has done since publishing a recovery plan in 1982, according to a news release published by the Pennsylvania Game Commission. As part of the review, the USFWS sought information on the status of the eastern cougar in 21 states—from Maine to South Carolina and westward from Michigan to Tennessee—where the Endangered Species Act protects it.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("A consensus exists among wildlife officials in 21 eastern states that the eastern cougar subspecies has been extirpated from eastern United States. The federal government of Canada has taken no position on the subspecies' existence, continued or otherwise, and terms the evidence \"inconclusive.\"The U.S. Fish and Wildlife Service reviewed all available research and other information, and concluded in 2011 that the eastern cougar subspecies has been extinct since the 1930s, and recommended that it be removed from its list of endangered species. The agency used the 1946 taxonomy of S.P. Young and E.A. Goldman in defining the eastern cougar subspecies. While noting that some taxonomists in recent years have classified all North American cougars within a single subspecies, the agency's 2011 report said \"a full taxonomic analysis is necessary to conclude that a revision to the Young and Goldman (1946) taxonomy is warranted.\"The agency acknowledged the occasional presence of cougars in eastern North America, but believes these are of wanderers from western breeding ranges or escaped captives. Its review expressed skepticism that breeding populations exist north of Florida, noting, among other things, the lack of consistent road kill evidence comparable to known cougar ranges. However, the presence of cougars in the wild — whatever their taxonomy or origin — in eastern North America, continues to be controversial.Various residents of eastern North America, especially in rural regions, have reported as many as 10,000 cougar sightings since the 1960s and many continue to believe the subspecies has survived.Bruce Wright, a wildlife biologist and former student of Aldo Leopold popularized the idea that a breeding population of cougars persisted in northern New England and the Maritime provinces through a series of articles and books published between 1960 and 1973. Wright based his idea mostly on unconfirmed sightings, track photos and plaster casts, and photographs of pumas killed in New Brunswick in 1932 and in Maine in 1938.Bolgiano, C. (1995). Mountain lion: An unnatural history of pumas and people. Stackpole Books, Harrisburg, Pennsylvania, ISBN 0811710440.Since the 1970s, privately run groups have formed in nearly every state to compile and investigate records of cougar sightings. Many of these groups are convinced that breeding populations of cougars exist throughout the region. Some believe that a conspiracy to hide information or secretly reintroduce cougars is actively underway by state and federal governments. Some endeavor to promote the recovery of cougars in eastern North America. Large numbers of cougar sightings have been reliably reported throughout the Midwest.At least several dozen or more reported sightings have been confirmed by biologists, many of whom believe they are accounted for by escaped captives or individual members of the western subspecies who have wandered hundreds of miles from their established breeding ranges in the Dakotas or elsewhere in the west.Eastern U.S. reported sightings, many of which reviewed in the recent federal report, in various locations, including Michigan (See Upper Peninsula), Wisconsin, Southern Indiana, Illinois, Missouri, Kentucky, Connecticut, New York Maine, Massachusetts, New Hampshire, North Carolina, Virginia, Arkansas, Vermont, Alabama Louisiana.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("A 1998 study for Canada's national Committee on the Status of Endangered Wildlife in Canada concluded \"that there is no objective evidence (actual cougar specimens or other unequivocal confirmation) for the continuous presence of cougars since the last century anywhere in eastern Canada or the eastern United States outside of Florida.\".Scott, F.W. (1998). Update of COSEWIC status report on cougar (Felis concolor couguar), eastern population. Committee on the Status of Endangered Wildlife in Canada, Environment Canada, Ottawa, Ontario, Canada. Based on this, in 1999, the magazine Canadian Geographic reported that for the previous half century, a debate over whether or not Canada's eastern woods host a cougar species all its own has raged. \"Now the answer appears to be 'no.' Experts say past sightings were cases of mistaken identification.\"Harry Thurston (September–October 1999): Can the eastern cougar debate be laid to rest?, Canadian Geographic, 119(6): pp. 18However, the Canadian committee's Web site as of 2011 says that data is \"insufficient\" to draw conclusions regarding the subspecies’continued existence, or even whether it ever existed at all.In March 2011, an official with the Ontario Ministry of Natural Resources stated that cougars are present in the province.[1] This official said individual cougars in Ontario may be escaped zoo animals or pets or may have migrated from the western parts of North America.As in the eastern U.S., there have been numerous cougar sightings reported by Canadians in Ontario, Quebec, New Brunswick and Nova ScotiaThe privately run Ontario Puma Foundation estimates that there are 550 Pumas in the province and their numbers are increasing steadily to a sustainable population.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The North American cougar (Puma concolor couguar), is the cougar subspecies once commonly found in eastern North America and still prevalent in the western half of the continent. As well as several previous subspecies of cougar of the western United States and western Canada, Puma concolor couguar encompasses the remaining populations of the eastern cougar, where the cat was also known as the panther, the only unequivocally known of which is the critically endangered Florida panther population. Many extinct populations, such as the Wisconsin cougar, which was extirpated in 1925, are also included in the subspecies.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Several populations still exist and are thriving in the western United States, but the North American cougar was once commonly found in eastern portions of the United States and Canada.  It was believed to be extirpated in the early 1900s.  Cougars in Michigan were thought to have been killed off and extinct in the early 1900s.  Today there is evidence to support that cougars could be on the rise in Mexico and could have a substantial population in years to come.  Some mainstream scientists believe that small relict populations may exist (around 50 individuals), especially in the Appalachian Mountains and eastern Canada.  Recent scientific findings in hair traps in Fundy National Park in New Brunswick have confirmed the existence of at least three cougars in New Brunswick.  Some theories postulate that modern sightings and scientific data (hair samples) are from a feral breeding population of former pets, possibly hybridizing with native North American cougar remnants, or claim that cougars from the western United States have been rapidly expanding their range eastwards. The Ontario Puma Foundation estimates that there are currently 850 cougars in Ontario.Sightings of cougars in the eastern United States continue today, despite their status as extirpated. Cougars with offspring have been sighted in Maine, Vermont, New Hampshire, Massachusetts, Connecticut, and Michigan in the past fifteen years.Michigan Citizens Cougar Recognition, MCCR There have been verified cougar tracks and kills found in some states, including New York and Michigan.  New York has had numerous sightings in the Adirondack and Catskill Mountains, while Michigan has had numerous sightings across the state in the Upper Peninsula and now more commonly in the lower part of Michigan. Connecticut and Massachusetts have less sighting, and most are in Western Massachusetts and Northwestern Connecticut,  but more evidence is present, including the CT Cougar (a cougar killed in a highway in Connecticut in 2011) and DNA tests of scat in Central Massachusetts in the Quabbin Reservoir in 1997. Virginia has also had many sightings throughout the state. Most recently, New Jersey has seen its share of sightings, with eyewitness accounts dating back to 2007 that continue to increase each year. This may mean they are thriving in Ohio, Pennsylvania (since Michigan and New York have had their evident sightings), and west of New Jersey, in which the life-abundant Appalachian Mountains could support a number of cougars before crossing the Delaware River.Genetic analysis of DNA from a cougar sighting in Wisconsin in 2008 indicated that a cougar was in Wisconsin and that it was not captive.  It is speculated that the cougar migrated from a native population in the Black Hills of South Dakota; however, the genetic analysis could not affirm that hypothesis.  It is also uncertain whether there are other, perhaps breeding, cougars.  However, a second sighting was reported and tracks were documented in a nearby Wisconsin community.  Unfortunately, a genetic analysis could not be done and a determination could not be made.Hills Mountain Lion May Have Migrated To Wisconsin, CougarNetwork  This cougar later made its way south into the northern Chicago suburb of Wilmette. On April 14, 2008, a cougar triggered a flurry of reports before being cornered and killed in the Chicago neighborhood of Roscoe Village while officers tried to contain it. The cougar was the first sighted in the city limits of Chicago since the city was founded in 1833.In 2011, a cougar was sighted in Greenwich, Connecticut and later killed by an S.U.V. in Milford after allegedly travelling 1,500 miles from South Dakota.Mountain lion killed in Conn. had walked from S. Dakota. Content.usatoday.com (2011-07-26). Retrieved on 2012-12-29.While most may be former captive animals released or escaped, the possibility of a sustained breeding population either incumbent or from migration is not out of the question.Northeast Corfirmation Reports, CougarNetwork");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Wright, Bruce S. The Eastern Panther: A Question of Survival. Toronto: Clarke, Irwin and Company, 1972.");
	/*
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("eastern cougar");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("eastern puma");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Cougouar");
	vernacularName.setLanguage(Language.FRENCH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Eastern Cougar");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Eastern Panther");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Eastern Puma");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Puma De L'Est De L'amérique Du Nord");
	vernacularName.setLanguage(Language.FRENCH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Puma Oriental");
	vernacularName.setLanguage(Language.SPANISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Eastern cougar");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("North American cougar");
	vernacularName.setLanguage(Language.ENGLISH);
	*/
	/* "descriptionsSerialized":{"values":["Eastern cougar (Puma concolor couguar) is considered by many puma biologists to be a subspecies of the North American cougar, while others believe recent genetic research suggests all North American cougars are a single subspecies. The eastern subspecies was deemed extinct by a U.S. Fish and Wildlife Service (USF&WS) evaluation in 2011, while a parallel Canadian organization has taken no position on the question.USF&WS officials believe that cougars found in eastern North America during recent years have genetic origins in South America (as escaped captives) or are from western North America (as wandering individuals). Others say these cats are surviving members of the eastern subspecies.","In 1792, Robert Kerr of the Royal Physical Society and Royal Society of Surgeons assigned the name Felis couguar to eastern North America cougars north of Florida. John Audubon in 1851 believed that cougars in both North and South America were indistinguishable. The eastern cougar was first assigned to the subspecies Felis concolor couguar and the Florida panther (F. c. coryi). Young and Goldman based their description of the eastern subspecies on their examination of eight of the existing 26 historic specimens.In 1955, Jackson described a new subspecies, the Wisconsin puma (F. c. schorgeri), from a small sample of skulls.A 1981 taxonomy (Hall) accepted F. c. schorgeri, the Wisconsin puma, and also extended the range of the eastern puma into Nova Scotia and mapped the Florida panther’s (F. c. coryi) range as far north as South Carolina and southwestern Tennessee.Hall, E.R. 1981. The mammals of North America, second edition. John Wiley and Sons, New YorkIn 2000, Culver et al., recommended that based on recent genetic research, all North American cougars be classified as a single subspecies, Puma concolor couguar following the oldest named subspecies (Kerr in 1792).The 2005 edition of Mammal Species of the World followed Culver’s recommendations. This revision was made by Dr. W. Chris Wozencraft of Bethel University, Indiana, as the sole reviewer. However, the publication's Web site as of 2011, as well as that of its affiliate, the Smithsonian National Museum of Natural History, continued to mention the Puma concolor couguar (or eastern cougar) as a subspecies of Puma concolor.Puma concolor. Mammal Species of the World. Bucknell.eduNorth American Mammals: Puma concolor. Mnh.si.eduDr. Judith Eger, Royal Ontario Museum, Toronto, Ontario, chair of the American Society of Mammalogists checklist committee, believes that the Culver work was improper, as it offered no evaluation of the existing subspecies of the puma and failed to include morphological, ecological, and behavioral considerations. According to Eger, the Culver revision is only accepted by some puma biologists.The U.S. Fish and Wildlife Service continues to accept the Young and Goldman taxonomy. \"While more recent genetic information introduces significant ambiguities, a full taxonomic analysis is necessary to conclude that a revision to the Young and Goldman (1946) taxonomy is warranted,\" the agency said in 2011.","Until around 1990, reports of mountain lions in the Midwest and East were highly influenced by the \"Bigfoot factor,\" according to Mark Dowling, co-founder of the Eastern Cougar Network. \"None of it was really real,\" he said in an interview. But the situation has changed dramatically since that time according to Dowling, whose group collects and disseminates data on the shifting mountain lion population.August 5, 2003: Mountain Lions Headed for Atlantic City?, Insight on the News, 19(17): pp. 16–17Dowling said in 2003 that sightings in the eastern half of the nation, including Michigan, etc., were \"almost certainly\" escaped captives, but he added that the notion that (Western) cougars \"will eventually reach New Jersey\" is a reasonable prediction, in part due to increased populations of whitetail deer.However, some of these cougars found far in the east were established to be of western origin. As noted in an opinion piece by David Baron in the New York Times, concerning a cougar killed by a car in Connecticut in 2011:\"Wildlife officials, who at first assumed the cat was a captive animal that had escaped its owners, examined its DNA and concluded that it was a wild cougar from the Black Hills of South Dakota. It had wandered at least 1,500 miles before meeting its end at the front of an S.U.V. in Connecticut. That is one impressive walkabout.\"\"You have to appreciate this cat’s sense of irony, too. The cougar showed up in the East just three months after the Fish and Wildlife Service declared the eastern cougar extinct, a move that would exempt the officially nonexistent subspecies of the big cat from federal protection. Perhaps this red-state cougar traveled east to send a message to Washington: the federal government can make pronouncements about where cougars are not supposed to be found, but a cat’s going to go where a cat wants to go.\"","The U.S. Fish and Wildlife Service in 2007 began a review of scientific and commercial information to determine the status of the endangered eastern cougar, the first review the service has done since publishing a recovery plan in 1982, according to a news release published by the Pennsylvania Game Commission. As part of the review, the USFWS sought information on the status of the eastern cougar in 21 states—from Maine to South Carolina and westward from Michigan to Tennessee—where the Endangered Species Act protects it.","A consensus exists among wildlife officials in 21 eastern states that the eastern cougar subspecies has been extirpated from eastern United States. The federal government of Canada has taken no position on the subspecies' existence, continued or otherwise, and terms the evidence \"inconclusive.\"The U.S. Fish and Wildlife Service reviewed all available research and other information, and concluded in 2011 that the eastern cougar subspecies has been extinct since the 1930s, and recommended that it be removed from its list of endangered species. The agency used the 1946 taxonomy of S.P. Young and E.A. Goldman in defining the eastern cougar subspecies. While noting that some taxonomists in recent years have classified all North American cougars within a single subspecies, the agency's 2011 report said \"a full taxonomic analysis is necessary to conclude that a revision to the Young and Goldman (1946) taxonomy is warranted.\"The agency acknowledged the occasional presence of cougars in eastern North America, but believes these are of wanderers from western breeding ranges or escaped captives. Its review expressed skepticism that breeding populations exist north of Florida, noting, among other things, the lack of consistent road kill evidence comparable to known cougar ranges. However, the presence of cougars in the wild — whatever their taxonomy or origin — in eastern North America, continues to be controversial.Various residents of eastern North America, especially in rural regions, have reported as many as 10,000 cougar sightings since the 1960s and many continue to believe the subspecies has survived.Bruce Wright, a wildlife biologist and former student of Aldo Leopold popularized the idea that a breeding population of cougars persisted in northern New England and the Maritime provinces through a series of articles and books published between 1960 and 1973. Wright based his idea mostly on unconfirmed sightings, track photos and plaster casts, and photographs of pumas killed in New Brunswick in 1932 and in Maine in 1938.Bolgiano, C. (1995). Mountain lion: An unnatural history of pumas and people. Stackpole Books, Harrisburg, Pennsylvania, ISBN 0811710440.Since the 1970s, privately run groups have formed in nearly every state to compile and investigate records of cougar sightings. Many of these groups are convinced that breeding populations of cougars exist throughout the region. Some believe that a conspiracy to hide information or secretly reintroduce cougars is actively underway by state and federal governments. Some endeavor to promote the recovery of cougars in eastern North America. Large numbers of cougar sightings have been reliably reported throughout the Midwest.At least several dozen or more reported sightings have been confirmed by biologists, many of whom believe they are accounted for by escaped captives or individual members of the western subspecies who have wandered hundreds of miles from their established breeding ranges in the Dakotas or elsewhere in the west.Eastern U.S. reported sightings, many of which reviewed in the recent federal report, in various locations, including Michigan (See Upper Peninsula), Wisconsin, Southern Indiana, Illinois, Missouri, Kentucky, Connecticut, New York Maine, Massachusetts, New Hampshire, North Carolina, Virginia, Arkansas, Vermont, Alabama Louisiana.","A 1998 study for Canada's national Committee on the Status of Endangered Wildlife in Canada concluded \"that there is no objective evidence (actual cougar specimens or other unequivocal confirmation) for the continuous presence of cougars since the last century anywhere in eastern Canada or the eastern United States outside of Florida.\".Scott, F.W. (1998). Update of COSEWIC status report on cougar (Felis concolor couguar), eastern population. Committee on the Status of Endangered Wildlife in Canada, Environment Canada, Ottawa, Ontario, Canada. Based on this, in 1999, the magazine Canadian Geographic reported that for the previous half century, a debate over whether or not Canada's eastern woods host a cougar species all its own has raged. \"Now the answer appears to be 'no.' Experts say past sightings were cases of mistaken identification.\"Harry Thurston (September–October 1999): Can the eastern cougar debate be laid to rest?, Canadian Geographic, 119(6): pp. 18However, the Canadian committee's Web site as of 2011 says that data is \"insufficient\" to draw conclusions regarding the subspecies’continued existence, or even whether it ever existed at all.In March 2011, an official with the Ontario Ministry of Natural Resources stated that cougars are present in the province.[1] This official said individual cougars in Ontario may be escaped zoo animals or pets or may have migrated from the western parts of North America.As in the eastern U.S., there have been numerous cougar sightings reported by Canadians in Ontario, Quebec, New Brunswick and Nova ScotiaThe privately run Ontario Puma Foundation estimates that there are 550 Pumas in the province and their numbers are increasing steadily to a sustainable population.","The North American cougar (Puma concolor couguar), is the cougar subspecies once commonly found in eastern North America and still prevalent in the western half of the continent. As well as several previous subspecies of cougar of the western United States and western Canada, Puma concolor couguar encompasses the remaining populations of the eastern cougar, where the cat was also known as the panther, the only unequivocally known of which is the critically endangered Florida panther population. Many extinct populations, such as the Wisconsin cougar, which was extirpated in 1925, are also included in the subspecies.","Several populations still exist and are thriving in the western United States, but the North American cougar was once commonly found in eastern portions of the United States and Canada.  It was believed to be extirpated in the early 1900s.  Cougars in Michigan were thought to have been killed off and extinct in the early 1900s.  Today there is evidence to support that cougars could be on the rise in Mexico and could have a substantial population in years to come.  Some mainstream scientists believe that small relict populations may exist (around 50 individuals), especially in the Appalachian Mountains and eastern Canada.  Recent scientific findings in hair traps in Fundy National Park in New Brunswick have confirmed the existence of at least three cougars in New Brunswick.  Some theories postulate that modern sightings and scientific data (hair samples) are from a feral breeding population of former pets, possibly hybridizing with native North American cougar remnants, or claim that cougars from the western United States have been rapidly expanding their range eastwards. The Ontario Puma Foundation estimates that there are currently 850 cougars in Ontario.Sightings of cougars in the eastern United States continue today, despite their status as extirpated. Cougars with offspring have been sighted in Maine, Vermont, New Hampshire, Massachusetts, Connecticut, and Michigan in the past fifteen years.Michigan Citizens Cougar Recognition, MCCR There have been verified cougar tracks and kills found in some states, including New York and Michigan.  New York has had numerous sightings in the Adirondack and Catskill Mountains, while Michigan has had numerous sightings across the state in the Upper Peninsula and now more commonly in the lower part of Michigan. Connecticut and Massachusetts have less sighting, and most are in Western Massachusetts and Northwestern Connecticut,  but more evidence is present, including the CT Cougar (a cougar killed in a highway in Connecticut in 2011) and DNA tests of scat in Central Massachusetts in the Quabbin Reservoir in 1997. Virginia has also had many sightings throughout the state. Most recently, New Jersey has seen its share of sightings, with eyewitness accounts dating back to 2007 that continue to increase each year. This may mean they are thriving in Ohio, Pennsylvania (since Michigan and New York have had their evident sightings), and west of New Jersey, in which the life-abundant Appalachian Mountains could support a number of cougars before crossing the Delaware River.Genetic analysis of DNA from a cougar sighting in Wisconsin in 2008 indicated that a cougar was in Wisconsin and that it was not captive.  It is speculated that the cougar migrated from a native population in the Black Hills of South Dakota; however, the genetic analysis could not affirm that hypothesis.  It is also uncertain whether there are other, perhaps breeding, cougars.  However, a second sighting was reported and tracks were documented in a nearby Wisconsin community.  Unfortunately, a genetic analysis could not be done and a determination could not be made.Hills Mountain Lion May Have Migrated To Wisconsin, CougarNetwork  This cougar later made its way south into the northern Chicago suburb of Wilmette. On April 14, 2008, a cougar triggered a flurry of reports before being cornered and killed in the Chicago neighborhood of Roscoe Village while officers tried to contain it. The cougar was the first sighted in the city limits of Chicago since the city was founded in 1833.In 2011, a cougar was sighted in Greenwich, Connecticut and later killed by an S.U.V. in Milford after allegedly travelling 1,500 miles from South Dakota.Mountain lion killed in Conn. had walked from S. Dakota. Content.usatoday.com (2011-07-26). Retrieved on 2012-12-29.While most may be former captive animals released or escaped, the possibility of a sustained breeding population either incumbent or from migration is not out of the question.Northeast Corfirmation Reports, CougarNetwork","Wright, Bruce S. The Eastern Panther: A Question of Survival. Toronto: Clarke, Irwin and Company, 1972."]} */
	/* ,"vernacularNamesSerialized":{"values":["en # eastern cougar","en # eastern puma","fr # Cougouar","en # Eastern Cougar","en # Eastern Panther","en # Eastern Puma","fr # Puma De L'Est De L'amérique Du Nord","es # Puma Oriental","en # Eastern cougar","en # North American cougar"]}); */
	// // result.setSynonym(false);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164591);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164591);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor kaibabensis (Nelson & Goldman, 1931)");
	result.setCanonicalName("Puma concolor kaibabensis");
	// result.setAuthorship(" (Nelson & Goldman, 1931)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119925577");
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */


	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164592);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164592);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor oregonensis (Rafinesque, 1832)");
	result.setCanonicalName("Puma concolor oregonensis");
	// result.setAuthorship(" (Rafinesque, 1832)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119925580");
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164594);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164594);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor vancouverensis (Nelson & Goldman, 1932)");
	result.setCanonicalName("Puma concolor vancouverensis");
	// result.setAuthorship(" (Nelson & Goldman, 1932)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119925583");
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"} */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164599);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164599);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor azteca (Merriam, 1901)");
	result.setCanonicalName("Puma concolor azteca");
	// result.setAuthorship(" (Merriam, 1901)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119925570");
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164600);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164600);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor coryi (Bangs, 1899)");
	result.setCanonicalName("Puma concolor coryi");
	// result.setAuthorship(" (Bangs, 1899)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119925573");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The Florida panther is an endangered subspecies of cougar (Puma concolor) that lives in forests and swamps of southern Florida in the United States. Its current taxonomic status (Puma concolor coryi or Puma concolor couguar) is unresolved, but recent genetic research alone does not alter the legal conservation status. This species is also known as the cougar, mountain lion, puma, and catamount; but in the southeastern United States and particularly Florida, it is exclusively known as the panther.Males can weigh up to Florida Panther General Information. Florida Panther Society and live within a range that includes the Big Cypress National Preserve, Everglades National Park, and the Florida Panther National Wildlife Refuge.FLORIDA PANTHER. Division of Endangered Species, U.S. Fish and Wildlife Service. Last Retrieved 2007-01-30. This population, the only unequivocal cougar representative in the eastern United States, currently occupies 5% of its historic range. In the 1970s, there were an estimated 20 Florida panthers in the wild, and their numbers have increased to an estimated 100 to 160 as of 2011.msnbc.com Video Player. MSNBCIn 1982, the Florida panther was chosen as the Florida state animal.The State Animal: Florida Panther, Division of Historical Resources");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("It was formerly considered Critically Endangered by the IUCN, but it has not been listed since 2008. Recovery efforts are currently underway in Florida to conserve the state's remaining population of native panthers. This is a difficult task, as the panther requires contiguous areas of habitat — each breeding unit, consisting of one male and two to five females, requires about  of habitat.Florida Panther Recovery Plan. The Florida Panther Recovery Team, South Florida Ecological Services Office, U.S. Fish and Wildlife Service. Published 1995-03-13. Retrieved 2007-01-30. This animal is considered to be a conservational flagship because it is a major contributor to the keystone ecological and evolutionary processes in their environment. A population of 240 panthers would require 8,000 to  of habitat and sufficient genetic diversity in order to avoid inbreeding as a result of small population size. However, a study in 2006 estimated that there was about  free for the panthers.Kautz, Randy (2006): How much is enough? Landscape-scale conservation for the Florida panther, Biological Conservation, 130(1): pp. 118–133  The introduction of eight female cougars from a closely related Texas population has apparently been successful in mitigating inbreeding problems.Florida Panther and the Genetic Restoration Program. U.S. Fish and Wildlife Service. Retrieved 2007-01-30. One objective to panther recovery is establishing two additional populations within historic range, a goal that has been politically difficult.Pittman, Craig (December 18, 2008): Florida panthers need new territory, federal officials say, Tampa Bay Times Habitat ConservationThe conservation of Florida panther habitats is especially important because they rely on the protection of the forest, specifically hardwood hammock, cypress swamp, pineland and hardwood swamp, for their survival. Conservations strategies for Florida panthers tend to focus on their preferred morning habitats. However, GPS tracking has determined that habitat selection for panthers varies by time of day for all observed individuals, regardless of size or gender. They move from wetlands during the daytime, to prairie grasslands at night. The implications of these findings suggest that conservation efforts be focused on the full range of habitats utilized by Florida panther populations.Onorato, D.P. (2011): Habitat selection by critically endangered Florida panthers across the diel period: implications for land management and conservation, Animal Conservation, 14(11): pp. 196–205  Female panthers with cubs build dens for their litters in an equally wide variety of habitats, favoring dense scrub but also using grassland and marshland. Management controversyThe Florida panther has been at the center of a controversy over the science used to manage the species. There has been very strong disagreement between scientists about the location and nature of critical habitat. This in turn is linked to a dispute over management which involves property developers and environmental organizations.Gross L (2005): Why Not the Best? How Science Failed the Florida Panther, PLoS Biol, 3(9): pp. e333 Recovery agencies appointed a panel of four experts, the Florida Panther Scientific Review Team (SRT), to evaluate the soundness of the body of work used to guide panther recovery. The SRT identified serious problems in panther literature, including mis-citations and misrepresentation of data to support unsound conclusions.Beier, P; Vaughan, MR; Conroy, MJ and Quigley, H. 2003, An analysis of scientific literature related to the Florida panther: Submitted as final report for Project NG01-105, Florida Fish and Wildlife Conservation Commission, Tallahassee, FL.Beier, P; Vaughan, MR; Conroy, MJ and Quigley, H (2006): Evaluating scientific inferences about the Florida panther, Journal of Wildlife Management, 70: pp. 236–245Conroy, MJ, P Beier, H Quigley, and MR Vaughan (2006): Improving the use of science in conservation: lessons from the Florida panther, Journal of Wildlife Management, 70: pp. 1–7 A Data Quality Act (DQA) complaint brought by Public Employees for Environmental Responsibility (PEER) and Andrew Eller, a biologist with the U.S. Fish and Wildlife Service (USFWS), was successful in demonstrating that agencies continued to use incorrect data after it had been clearly identified as such.Information Quality Guidelines: Your Questions and Our Responses. U.S. Fish and Wildlife Service. Published 2005-03-21. Retrieved 2007-01-30. As a result of the DQA ruling, USFWS admitted errors in the science the agency was using and subsequently reinstated Eller, who had been fired by USFWS after filing the DQA complaint. In two white papers, environmental groups contended that habitat development was permitted that should not have been, and documented the link between incorrect data and financial conflicts of interest.Kostyack, J and Hill, K. 2005. Giving Away the Store.Kostyack, J and Hill, K. 2004. Discrediting a Decade of Panther Science: Implications of the Scientific Review Team Report. In January 2006, USFWS released a new Draft Florida Panther Recovery Plan for public review.Fish and Wildlife Service releases Draft Florida Panther Recovery Plan for public review. U.S. Fish and Wildlife Service. Published 2006-01-31. Retrieved 2007-01-30.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Florida Panthers are spotted at birth and typically have blue eyes. As the panther grows the spots fade and the coat becomes completely tan while the eyes typically take on a yellow hue. The panther's underbelly is a creamy white, with black tips on the tail and ears. Florida panthers lack the ability to roar, and instead make distinct sounds that include whistles, chirps, growls, hisses, and purrs. Florida panthers are mid-sized for the species, being smaller than cougars from Northern and Southern climes but larger than cougars from the neotropics. Adult female Florida panthers weigh  whereas the larger males weigh . Total length is from  and shoulder height is .WEC 167/UW176: Jaguar: Another Threatened Panther. Edis.ifas.ufl.edu. Retrieved on 2012-05-02.PantherNet : Handbook: Natural History : Physical Description. Floridapanthernet.org. Retrieved on 2012-05-02. Male panthers, on average, are 9.4% longer and 33.2% heavier than females.  This is because males grow at a faster rate than females and for a longer amount of time.Bartareau, T. (2013): Growth in body length and mass of the Florida panther: An evaluation of different models and sexual size dimorphism, Southeastern Naturalist, 12(1): pp. 27–40");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The Florida panther is a large carnivore whose diet consists of small animals like hares, mice, and waterfowl but also larger animals like storks, white-tailed deer, wild boar, and even the American Alligator. The hunting season of the panther is greatly affected by the the behavior of their prey, especially the deer. Deer are nocturnal in nature which makes hunting especially for this type of prey more of a success for panthers since they are nocturnal hunters. When hunting, panthers shift their hunting environment based on where the prey base is. The female panther, in particular is especially dependent on nutrition because their reproductive rates, home range size and movement behavior are affected by it.David S. Maehr, E. Darrell Land, Jayde C. Roof and J. Walter Mccown. Early Maternal Behavior in the Florida Panther (Felis concolor coryi). American Midland Naturalist , Vol. 122, No. 1 (Jul., 1989), pp. 34-43Kilgo, J. E. (n.d). Influences of Hunting on the Behavior of White-Tailed Deer: Implications for Conservation of the Florida Panther. 12(6), 1359-1365.   McBride, Roy; McBride, Cougar (December 2010): Predation of a Large Alligator by a Florida Panther, Southeastern Naturalist, 9(4): pp. 854–856 Maehr, D. S., & Deason, J. P. (2002). Wide-ranging carnivores and development permits: constructing a multi-scale model to evaluate impacts on the Florida panther. Clean Technologies and Environmental Policy, 3(4), 398-406.");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("Panther kittens are born in dens created by their mothers, often in dense scrub. The dens are chosen based on a variety of factors, including prey availability, and have been observed in a range of habitats. Kittens will spend the first 6–8 weeks of life in those dens, dependent on their mother.Benson, John F.; Lotz, Mark A.; Jansen, Deborah (February 2008): Natal Den Selection by Florida Panthers, Journal of Wildlife Management, 72(2): pp. 405–410 In the first 2–3 weeks, the mother will spend most of her time nursing the kittens; after this period, she will spend more time away from the den, to wean the cubs and to hunt prey to bring to the den. Once they are old enough to leave the den, they will hunt in the company of their mother. Male panthers will not be encountered frequently during this time, as female and male panthers generally avoid each other outside of breeding. Kittens are usually 2 months old when they begin hunting with their mothers, and 2 years old when they begin to hunt and live on their own.Maeher, D.S.; Land, E. Darrell; Roof, Jayde C.; McCown, J. Walter (July 1989): Early maternal behavior in the Florida panther, The American Midland Naturalist, 122(1): pp. 34–43");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The Florida panther has long been considered a unique subspecies of cougar, under the trinomial Puma concolor coryi (Felis concolor coryi in older listings), one of thirty-two subspecies once recognized. The Florida panther has been protected from legal hunting since 1958, and in 1967 it was listed as endangered by the U.S. Fish and Wildlife Service; it was added to the state's endangered species list in 1973.1993: Florida Panther, Endangered and Threatened Species of the Southeastern United States (The Red Book). U.S. Fish and Wildlife ServiceA genetic study of cougar mitochondrial DNA has reported that many of the supposed subspecies are too similar to be recognized as distinct,Culver, M. (2000): Genomic Ancestry of the American Puma, Journal of Heredity, 91(3): pp. 186–197 suggesting a reclassification of the Florida panther and numerous other subspecies into a single North American cougar (Puma concolor couguar). Following the research, the canonical Mammal Species of the World (3rd edition) ceased to recognize the Florida panther as a unique subspecies, collapsing it and others into the North American cougar.Despite these findings it is still listed as subspecies Puma concolor coryi in research works, including those directly concerned with its conservation.Conroy, Michael J. (2006): Improving The Use Of Science In Conservation: Lessons From The Florida Panther, Journal of Wildlife Management, 70(1): pp. 1–7 Responding to the research that suggested removing its subspecies status, the Florida Panther Recovery Team noted in 2007 \"the degree to which the scientific community has accepted the results of Culver et al. and the proposed change in taxonomy is not resolved at this time.\"The Florida Panther Recovery Team (2006-01-31): Florida Panther Recovery Program (Draft). U.S. Fish and Wildlife Service");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("A Florida panther is one of the main characters of the young adult novel Scat by Carl Hiaasen.A Florida panther in a sanctuary figures in the penultimate chapter of Humana Festa (2008) by Brazilian novelist Regina Rheda. In the English translation (2012), the animal is referred to as \"cougar\" to maintain effective wordplay.A Florida panther was a major character in the 1998 Boxcar Children book The Panther Mystery.A Florida panther is a major character in 1998 novel, Power, by Linda Hogan (New York: Norton and Co.)");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The Florida panther has a natural predator, the alligator. Humans also threaten it through poaching and wildlife control measures. Besides predation, the biggest threat to their survival is human encroachment. Historical persecution reduced this wide-ranging, large carnivore to a small area of south Florida. This created a tiny isolated population that became inbred (revealed by kinked tails, heart, and sperm problems).Sivlerstein, Alvin (1997): The Florida Panther: pp. 41+. Millbrook PressThe two highest causes of mortality for individual Florida panthers are automobile collisions and territorial aggression between panthers.The Florida Panther. Sierra Club Florida When these incidents injure the panthers, federal and Florida wildlife officials take them to White Oak Conservation in Yulee, Florida for recovery and rehabilitation until they are well enough to be reintroduced.BACK TO THE WILD. Friends of the Florida Panther Refuge Additionally, White Oak raises orphaned cubs and has done so for 12 individuals. Most recently, an orphaned brother and sister were brought to the center at 5 months old in 2011 after their mother was found dead in Collier County, Florida.Staats, Eric: Orphaned Florida panther kittens rescued. Naples Daily News After being raised, the male and female were released in early 2013 to the Rotenberger Wildlife Management Area and Collier County, respectively.Fleshler, David: First Florida panther released into Palm Beach County. Sun SentinelPrimary threats to the population as a whole include habitat loss, habitat degradation, and habitat fragmentation. Southern Florida is a fast-developing area and certain developments such as Ave Maria near Naples, are controversial for their location in prime panther habitat. Fragmentation by major roads has severely segmented the sexes of the Florida Panther as well. In a study done between the years of 1981 and 2004, it was seen that most panthers involved in car collisions were male.  However, females are much more reluctant to cross roads.  Therefore, roads separate habitat, and adult panthers.Schwab, Autumn (2011): Vehicle-related mortality and road crossing behavior of the Florida Panther, Applied Geography, 31(2): pp. 859–870Development and the Caloosahatchee River are major barriers to natural population expansion. While young males wander over extremely large areas in search of an available territory, females occupy home ranges close to their mothers. For this reason, panthers are poor colonizers and expand their range slowly despite occurrences of males far away from the core population. DiseaseAntigen analysis on select Florida panther populations has shown evidence of Feline Immunodeficiency Virus and Puma Lentivirus among certain individuals. The presence of these viruses is likely related to mating behaviors and territory sympatry. However, as Florida panthers have lower levels of the antibodies produced in response to FIV, it is difficult to find consistently positive results for the presence of infection.Miller, D.L. (2006): Feline Immunodeficiency Virus and Puma Lentivirus in Florida panthers (Puma concolor coryi): Epidemiology and Diagnostic Issues, Veterinary Research Communications, 30(3): pp. 307–317In the 2002-2003 capture season, Feline leukemia virus was first observed in two panthers. Further analysis determined an increase in FeLV positive panthers from January 1990 to April 2007. The virus is lethal, and its presence has resulted in efforts to inoculate the population. While there have been no new cases since July 2004, the virus does have potential for reintroduction.Cunningham, Mark (2008): EPIZOOTIOLOGY AND MANAGEMENT OF FELINE LEUKEMIA VIRUS IN THE FLORIDA PUMA, Journal of Wildlife Diseases, 44(3): pp. 537–552 ChemicalsExposure to a variety of chemical compounds in the environment have caused reproductive impairment to Florida panthers. Tests show that the differences between males and females in estradiol levels are insignificant, which suggests that males have been feminized due to chemical exposure. Feminized males are much less likely to reproduce, which represents a significant threat to a subspecies that already has a low population count and a lot of inbreeding. Chemical compounds that have created abnormalties in Florda panther reproduction include herbicides, pesticides, and fungicides such as benomyl, carbendazim, chlordecone, methoxychlor, methylmercury, fenarimol, methylmercury, and TCDD.Facemire, Charles (1995): Reproductive Impairment in the Florida Panther: Nature or Nurture?, Environmental Health Perspectives, 103(Supplement 4): pp. 79–86 Genetic DepletionOf all the puma subspecies, the Florida panther has the lowest genetic diversity. The low population of Florida panthers causes high rates of inbreeding, which can cause genetic defects including Cryptorchidism, cardiac defects, and weakened immune systems. Another effect of inbreeding is that it decreases genetic variance, thereby furthering the genetic depletion of the Florida panther. One of the morphological effects of inbreeding is the high frequency of a cowlick and a kinked tail. The frequency for a Florida panther to exhibit a cowlick is 94% compared to other pumas at 9%; the frequency for a Florida panther to exhibit a kinked tail is 88%, but only 27% for other puma subspecies. A proposed solution to increase genetic diversity of the Florida puma is to introduce the Texas puma to the Florida puma population.Roelke, Melody (1993): The consequences of demographic reduction and genetic depletion in the endangered Florida panther, Current Biology, 3(6): pp. 340–350 Genetic Depletion is not as big of a problem as it used to be, but is something that needs to be watched since the population is still in a fragile state. Vehicular CollisionsFlorida panthers live in home ranges between 190 km2 and 500 km2. Within these ranges are many roads and human constructions, which are regularly traveled on by Florida panthers and can result in their death by vehicular collision. Efforts to reduce vehicular collisions with the Florida panther include nighttime speed reduction zones, special roadsides, headlight reflectors, and  rumble strips. Another method of reducing collisions is the creation of wildlife corridors. Because wildlife corridors emulate the natural environment, animals are more likely to cross through a corridor rather than a road because a corridor provides more cover for prey and predators, and is safer to cross than a road.Land, Darrell (1996): WILDLIFE CROSSING DESIGNS AND USE BY FLORIDA PANTHERS AND OTHER WILDLIFE IN SOUTHWEST FLORIDA: pp. 323");
	/*
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Florida Panther");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Florida panther");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Cougar De Floride");
	vernacularName.setLanguage(Language.FRENCH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Florida Cougar");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Florida Panther");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Florida Puma");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("León De Florida");
	vernacularName.setLanguage(Language.SPANISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Puma De Florida");
	vernacularName.setLanguage(Language.SPANISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Puma De Floride");
	vernacularName.setLanguage(Language.FRENCH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Florida puma");
	vernacularName.setLanguage(Language.UNKNOWN);
	*/
	/* "descriptionsSerialized":{"values":["The Florida panther is an endangered subspecies of cougar (Puma concolor) that lives in forests and swamps of southern Florida in the United States. Its current taxonomic status (Puma concolor coryi or Puma concolor couguar) is unresolved, but recent genetic research alone does not alter the legal conservation status. This species is also known as the cougar, mountain lion, puma, and catamount; but in the southeastern United States and particularly Florida, it is exclusively known as the panther.Males can weigh up to Florida Panther General Information. Florida Panther Society and live within a range that includes the Big Cypress National Preserve, Everglades National Park, and the Florida Panther National Wildlife Refuge.FLORIDA PANTHER. Division of Endangered Species, U.S. Fish and Wildlife Service. Last Retrieved 2007-01-30. This population, the only unequivocal cougar representative in the eastern United States, currently occupies 5% of its historic range. In the 1970s, there were an estimated 20 Florida panthers in the wild, and their numbers have increased to an estimated 100 to 160 as of 2011.msnbc.com Video Player. MSNBCIn 1982, the Florida panther was chosen as the Florida state animal.The State Animal: Florida Panther, Division of Historical Resources","It was formerly considered Critically Endangered by the IUCN, but it has not been listed since 2008. Recovery efforts are currently underway in Florida to conserve the state's remaining population of native panthers. This is a difficult task, as the panther requires contiguous areas of habitat — each breeding unit, consisting of one male and two to five females, requires about  of habitat.Florida Panther Recovery Plan. The Florida Panther Recovery Team, South Florida Ecological Services Office, U.S. Fish and Wildlife Service. Published 1995-03-13. Retrieved 2007-01-30. This animal is considered to be a conservational flagship because it is a major contributor to the keystone ecological and evolutionary processes in their environment. A population of 240 panthers would require 8,000 to  of habitat and sufficient genetic diversity in order to avoid inbreeding as a result of small population size. However, a study in 2006 estimated that there was about  free for the panthers.Kautz, Randy (2006): How much is enough? Landscape-scale conservation for the Florida panther, Biological Conservation, 130(1): pp. 118–133  The introduction of eight female cougars from a closely related Texas population has apparently been successful in mitigating inbreeding problems.Florida Panther and the Genetic Restoration Program. U.S. Fish and Wildlife Service. Retrieved 2007-01-30. One objective to panther recovery is establishing two additional populations within historic range, a goal that has been politically difficult.Pittman, Craig (December 18, 2008): Florida panthers need new territory, federal officials say, Tampa Bay Times Habitat ConservationThe conservation of Florida panther habitats is especially important because they rely on the protection of the forest, specifically hardwood hammock, cypress swamp, pineland and hardwood swamp, for their survival. Conservations strategies for Florida panthers tend to focus on their preferred morning habitats. However, GPS tracking has determined that habitat selection for panthers varies by time of day for all observed individuals, regardless of size or gender. They move from wetlands during the daytime, to prairie grasslands at night. The implications of these findings suggest that conservation efforts be focused on the full range of habitats utilized by Florida panther populations.Onorato, D.P. (2011): Habitat selection by critically endangered Florida panthers across the diel period: implications for land management and conservation, Animal Conservation, 14(11): pp. 196–205  Female panthers with cubs build dens for their litters in an equally wide variety of habitats, favoring dense scrub but also using grassland and marshland. Management controversyThe Florida panther has been at the center of a controversy over the science used to manage the species. There has been very strong disagreement between scientists about the location and nature of critical habitat. This in turn is linked to a dispute over management which involves property developers and environmental organizations.Gross L (2005): Why Not the Best? How Science Failed the Florida Panther, PLoS Biol, 3(9): pp. e333 Recovery agencies appointed a panel of four experts, the Florida Panther Scientific Review Team (SRT), to evaluate the soundness of the body of work used to guide panther recovery. The SRT identified serious problems in panther literature, including mis-citations and misrepresentation of data to support unsound conclusions.Beier, P; Vaughan, MR; Conroy, MJ and Quigley, H. 2003, An analysis of scientific literature related to the Florida panther: Submitted as final report for Project NG01-105, Florida Fish and Wildlife Conservation Commission, Tallahassee, FL.Beier, P; Vaughan, MR; Conroy, MJ and Quigley, H (2006): Evaluating scientific inferences about the Florida panther, Journal of Wildlife Management, 70: pp. 236–245Conroy, MJ, P Beier, H Quigley, and MR Vaughan (2006): Improving the use of science in conservation: lessons from the Florida panther, Journal of Wildlife Management, 70: pp. 1–7 A Data Quality Act (DQA) complaint brought by Public Employees for Environmental Responsibility (PEER) and Andrew Eller, a biologist with the U.S. Fish and Wildlife Service (USFWS), was successful in demonstrating that agencies continued to use incorrect data after it had been clearly identified as such.Information Quality Guidelines: Your Questions and Our Responses. U.S. Fish and Wildlife Service. Published 2005-03-21. Retrieved 2007-01-30. As a result of the DQA ruling, USFWS admitted errors in the science the agency was using and subsequently reinstated Eller, who had been fired by USFWS after filing the DQA complaint. In two white papers, environmental groups contended that habitat development was permitted that should not have been, and documented the link between incorrect data and financial conflicts of interest.Kostyack, J and Hill, K. 2005. Giving Away the Store.Kostyack, J and Hill, K. 2004. Discrediting a Decade of Panther Science: Implications of the Scientific Review Team Report. In January 2006, USFWS released a new Draft Florida Panther Recovery Plan for public review.Fish and Wildlife Service releases Draft Florida Panther Recovery Plan for public review. U.S. Fish and Wildlife Service. Published 2006-01-31. Retrieved 2007-01-30.","Florida Panthers are spotted at birth and typically have blue eyes. As the panther grows the spots fade and the coat becomes completely tan while the eyes typically take on a yellow hue. The panther's underbelly is a creamy white, with black tips on the tail and ears. Florida panthers lack the ability to roar, and instead make distinct sounds that include whistles, chirps, growls, hisses, and purrs. Florida panthers are mid-sized for the species, being smaller than cougars from Northern and Southern climes but larger than cougars from the neotropics. Adult female Florida panthers weigh  whereas the larger males weigh . Total length is from  and shoulder height is .WEC 167/UW176: Jaguar: Another Threatened Panther. Edis.ifas.ufl.edu. Retrieved on 2012-05-02.PantherNet : Handbook: Natural History : Physical Description. Floridapanthernet.org. Retrieved on 2012-05-02. Male panthers, on average, are 9.4% longer and 33.2% heavier than females.  This is because males grow at a faster rate than females and for a longer amount of time.Bartareau, T. (2013): Growth in body length and mass of the Florida panther: An evaluation of different models and sexual size dimorphism, Southeastern Naturalist, 12(1): pp. 27–40","The Florida panther is a large carnivore whose diet consists of small animals like hares, mice, and waterfowl but also larger animals like storks, white-tailed deer, wild boar, and even the American Alligator. The hunting season of the panther is greatly affected by the the behavior of their prey, especially the deer. Deer are nocturnal in nature which makes hunting especially for this type of prey more of a success for panthers since they are nocturnal hunters. When hunting, panthers shift their hunting environment based on where the prey base is. The female panther, in particular is especially dependent on nutrition because their reproductive rates, home range size and movement behavior are affected by it.David S. Maehr, E. Darrell Land, Jayde C. Roof and J. Walter Mccown. Early Maternal Behavior in the Florida Panther (Felis concolor coryi). American Midland Naturalist , Vol. 122, No. 1 (Jul., 1989), pp. 34-43Kilgo, J. E. (n.d). Influences of Hunting on the Behavior of White-Tailed Deer: Implications for Conservation of the Florida Panther. 12(6), 1359-1365.   McBride, Roy; McBride, Cougar (December 2010): Predation of a Large Alligator by a Florida Panther, Southeastern Naturalist, 9(4): pp. 854–856 Maehr, D. S., & Deason, J. P. (2002). Wide-ranging carnivores and development permits: constructing a multi-scale model to evaluate impacts on the Florida panther. Clean Technologies and Environmental Policy, 3(4), 398-406.","Panther kittens are born in dens created by their mothers, often in dense scrub. The dens are chosen based on a variety of factors, including prey availability, and have been observed in a range of habitats. Kittens will spend the first 6–8 weeks of life in those dens, dependent on their mother.Benson, John F.; Lotz, Mark A.; Jansen, Deborah (February 2008): Natal Den Selection by Florida Panthers, Journal of Wildlife Management, 72(2): pp. 405–410 In the first 2–3 weeks, the mother will spend most of her time nursing the kittens; after this period, she will spend more time away from the den, to wean the cubs and to hunt prey to bring to the den. Once they are old enough to leave the den, they will hunt in the company of their mother. Male panthers will not be encountered frequently during this time, as female and male panthers generally avoid each other outside of breeding. Kittens are usually 2 months old when they begin hunting with their mothers, and 2 years old when they begin to hunt and live on their own.Maeher, D.S.; Land, E. Darrell; Roof, Jayde C.; McCown, J. Walter (July 1989): Early maternal behavior in the Florida panther, The American Midland Naturalist, 122(1): pp. 34–43","The Florida panther has long been considered a unique subspecies of cougar, under the trinomial Puma concolor coryi (Felis concolor coryi in older listings), one of thirty-two subspecies once recognized. The Florida panther has been protected from legal hunting since 1958, and in 1967 it was listed as endangered by the U.S. Fish and Wildlife Service; it was added to the state's endangered species list in 1973.1993: Florida Panther, Endangered and Threatened Species of the Southeastern United States (The Red Book). U.S. Fish and Wildlife ServiceA genetic study of cougar mitochondrial DNA has reported that many of the supposed subspecies are too similar to be recognized as distinct,Culver, M. (2000): Genomic Ancestry of the American Puma, Journal of Heredity, 91(3): pp. 186–197 suggesting a reclassification of the Florida panther and numerous other subspecies into a single North American cougar (Puma concolor couguar). Following the research, the canonical Mammal Species of the World (3rd edition) ceased to recognize the Florida panther as a unique subspecies, collapsing it and others into the North American cougar.Despite these findings it is still listed as subspecies Puma concolor coryi in research works, including those directly concerned with its conservation.Conroy, Michael J. (2006): Improving The Use Of Science In Conservation: Lessons From The Florida Panther, Journal of Wildlife Management, 70(1): pp. 1–7 Responding to the research that suggested removing its subspecies status, the Florida Panther Recovery Team noted in 2007 \"the degree to which the scientific community has accepted the results of Culver et al. and the proposed change in taxonomy is not resolved at this time.\"The Florida Panther Recovery Team (2006-01-31): Florida Panther Recovery Program (Draft). U.S. Fish and Wildlife Service","A Florida panther is one of the main characters of the young adult novel Scat by Carl Hiaasen.A Florida panther in a sanctuary figures in the penultimate chapter of Humana Festa (2008) by Brazilian novelist Regina Rheda. In the English translation (2012), the animal is referred to as \"cougar\" to maintain effective wordplay.A Florida panther was a major character in the 1998 Boxcar Children book The Panther Mystery.A Florida panther is a major character in 1998 novel, Power, by Linda Hogan (New York: Norton and Co.)","The Florida panther has a natural predator, the alligator. Humans also threaten it through poaching and wildlife control measures. Besides predation, the biggest threat to their survival is human encroachment. Historical persecution reduced this wide-ranging, large carnivore to a small area of south Florida. This created a tiny isolated population that became inbred (revealed by kinked tails, heart, and sperm problems).Sivlerstein, Alvin (1997): The Florida Panther: pp. 41+. Millbrook PressThe two highest causes of mortality for individual Florida panthers are automobile collisions and territorial aggression between panthers.The Florida Panther. Sierra Club Florida When these incidents injure the panthers, federal and Florida wildlife officials take them to White Oak Conservation in Yulee, Florida for recovery and rehabilitation until they are well enough to be reintroduced.BACK TO THE WILD. Friends of the Florida Panther Refuge Additionally, White Oak raises orphaned cubs and has done so for 12 individuals. Most recently, an orphaned brother and sister were brought to the center at 5 months old in 2011 after their mother was found dead in Collier County, Florida.Staats, Eric: Orphaned Florida panther kittens rescued. Naples Daily News After being raised, the male and female were released in early 2013 to the Rotenberger Wildlife Management Area and Collier County, respectively.Fleshler, David: First Florida panther released into Palm Beach County. Sun SentinelPrimary threats to the population as a whole include habitat loss, habitat degradation, and habitat fragmentation. Southern Florida is a fast-developing area and certain developments such as Ave Maria near Naples, are controversial for their location in prime panther habitat. Fragmentation by major roads has severely segmented the sexes of the Florida Panther as well. In a study done between the years of 1981 and 2004, it was seen that most panthers involved in car collisions were male.  However, females are much more reluctant to cross roads.  Therefore, roads separate habitat, and adult panthers.Schwab, Autumn (2011): Vehicle-related mortality and road crossing behavior of the Florida Panther, Applied Geography, 31(2): pp. 859–870Development and the Caloosahatchee River are major barriers to natural population expansion. While young males wander over extremely large areas in search of an available territory, females occupy home ranges close to their mothers. For this reason, panthers are poor colonizers and expand their range slowly despite occurrences of males far away from the core population. DiseaseAntigen analysis on select Florida panther populations has shown evidence of Feline Immunodeficiency Virus and Puma Lentivirus among certain individuals. The presence of these viruses is likely related to mating behaviors and territory sympatry. However, as Florida panthers have lower levels of the antibodies produced in response to FIV, it is difficult to find consistently positive results for the presence of infection.Miller, D.L. (2006): Feline Immunodeficiency Virus and Puma Lentivirus in Florida panthers (Puma concolor coryi): Epidemiology and Diagnostic Issues, Veterinary Research Communications, 30(3): pp. 307–317In the 2002-2003 capture season, Feline leukemia virus was first observed in two panthers. Further analysis determined an increase in FeLV positive panthers from January 1990 to April 2007. The virus is lethal, and its presence has resulted in efforts to inoculate the population. While there have been no new cases since July 2004, the virus does have potential for reintroduction.Cunningham, Mark (2008): EPIZOOTIOLOGY AND MANAGEMENT OF FELINE LEUKEMIA VIRUS IN THE FLORIDA PUMA, Journal of Wildlife Diseases, 44(3): pp. 537–552 ChemicalsExposure to a variety of chemical compounds in the environment have caused reproductive impairment to Florida panthers. Tests show that the differences between males and females in estradiol levels are insignificant, which suggests that males have been feminized due to chemical exposure. Feminized males are much less likely to reproduce, which represents a significant threat to a subspecies that already has a low population count and a lot of inbreeding. Chemical compounds that have created abnormalties in Florda panther reproduction include herbicides, pesticides, and fungicides such as benomyl, carbendazim, chlordecone, methoxychlor, methylmercury, fenarimol, methylmercury, and TCDD.Facemire, Charles (1995): Reproductive Impairment in the Florida Panther: Nature or Nurture?, Environmental Health Perspectives, 103(Supplement 4): pp. 79–86 Genetic DepletionOf all the puma subspecies, the Florida panther has the lowest genetic diversity. The low population of Florida panthers causes high rates of inbreeding, which can cause genetic defects including Cryptorchidism, cardiac defects, and weakened immune systems. Another effect of inbreeding is that it decreases genetic variance, thereby furthering the genetic depletion of the Florida panther. One of the morphological effects of inbreeding is the high frequency of a cowlick and a kinked tail. The frequency for a Florida panther to exhibit a cowlick is 94% compared to other pumas at 9%; the frequency for a Florida panther to exhibit a kinked tail is 88%, but only 27% for other puma subspecies. A proposed solution to increase genetic diversity of the Florida puma is to introduce the Texas puma to the Florida puma population.Roelke, Melody (1993): The consequences of demographic reduction and genetic depletion in the endangered Florida panther, Current Biology, 3(6): pp. 340–350 Genetic Depletion is not as big of a problem as it used to be, but is something that needs to be watched since the population is still in a fragile state. Vehicular CollisionsFlorida panthers live in home ranges between 190 km2 and 500 km2. Within these ranges are many roads and human constructions, which are regularly traveled on by Florida panthers and can result in their death by vehicular collision. Efforts to reduce vehicular collisions with the Florida panther include nighttime speed reduction zones, special roadsides, headlight reflectors, and  rumble strips. Another method of reducing collisions is the creation of wildlife corridors. Because wildlife corridors emulate the natural environment, animals are more likely to cross through a corridor rather than a road because a corridor provides more cover for prey and predators, and is safer to cross than a road.Land, Darrell (1996): WILDLIFE CROSSING DESIGNS AND USE BY FLORIDA PANTHERS AND OTHER WILDLIFE IN SOUTHWEST FLORIDA: pp. 323"]} */
	/* ,"vernacularNamesSerialized":{"values":["en # Florida Panther","en # Florida panther","fr # Cougar De Floride","en # Florida Cougar","en # Florida Panther","en # Florida Puma","es # León De Florida","es # Puma De Florida","fr # Puma De Floride"," # Florida puma"]} */
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164602);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164602);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor improcera (Phillips, 1912)");
	result.setCanonicalName("Puma concolor improcera");
	// result.setAuthorship(" (Phillips, 1912)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceid("119925576");
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164603);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164603);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor missoulensis (Goldman, 1943)");
	result.setCanonicalName("Puma concolor missoulensis");
	// result.setAuthorship(" (Goldman, 1943)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119925579");
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164604);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164604);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor stanleyana (Goldman, 1938)");
	result.setCanonicalName("Puma concolor stanleyana");
	// result.setAuthorship(" (Goldman, 1938)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119925582");
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164608);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164608);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor californica (May, 1896)");
	result.setCanonicalName("Puma concolor californica");
	// result.setAuthorship(" (May, 1896)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119925572");
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164610);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164610);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor hippolestes (Merriam, 1897)");
	result.setCanonicalName("Puma concolor hippolestes");
	// result.setAuthorship(" (Merriam, 1897)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119925575");
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164611);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164611);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor mayensis (Nelson & Goldman, 1929)");
	result.setCanonicalName("Puma concolor mayensis");
	// result.setAuthorship(" (Nelson & Goldman, 1929)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119925578");
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164613);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164613);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor mayensis (Nelson & Goldman, 1929)");
	result.setCanonicalName("Puma concolor schorgeri");
	// result.setAuthorship(" (Nelson & Goldman, 1929)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119925578");
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164618);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164618);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor browni (Merriam, 1903)");
	result.setCanonicalName("Puma concolor browni");
	// result.setAuthorship(" (Merriam, 1903)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setsourceId("119925571");
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */


	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164620);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164620);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	// result.setAcceptedKey(6164590);
	// result.setAccepted("Puma concolor couguar (Kerr, 1792)");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor cougar (Kerr, 1792)");
	result.setCanonicalName("Puma concolor cougar");
	// result.setAuthorship(" (Kerr, 1792)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119925574");
	// result.setSynonym(true);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164622);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164622);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor puma (Molina, 1782)");
	result.setCanonicalName("Puma concolor puma");
	// result.setAuthorship(" (Molina, 1782)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119916926");
	// // result.setSynonym(false);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(6164623);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164623);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor cabrerae Pocock, 1940");
	result.setCanonicalName("Puma concolor cabrerae");
	// result.setAuthorship("Pocock, 1940");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119916925");
	// // result.setSynonym(false);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	result = new NameUsageSuggestResult();
	//expected.add(result);
	result.setKey(6164624);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(6164624);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor costaricensis (Merriam, 1901)");
	result.setCanonicalName("Puma concolor costaricensis");
	// result.setAuthorship(" (Merriam, 1901)");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	// result.setSourceId("119916765");
	/*
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("Costa Rican puma");
	vernacularName.setLanguage(Language.ENGLISH);
	*/
	/* ,"vernacularNamesSerialized":{"values":["en # Costa Rican puma"]}); */
	// // result.setSynonym(false);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */

	/*
	result = new NameUsageSuggestResult();
	expected.add(result);
	result.setKey(7193927);
	// result.setDatasetKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	result.setNubKey(7193927);
	result.setParentKey(2435099);
	result.setParent("Puma concolor");
	result.setKingdom("Animalia");
	result.setPhylum("Chordata");
	result.setClazz("Mammalia");
	result.setOrder("Carnivora");
	result.setFamily("Felidae");
	result.setGenus("Puma");
	result.setSpecies("Puma concolor");
	result.setKingdomKey(1);
	result.setPhylumKey(44);
	result.setClassKey(359);
	result.setOrderKey(732);
	result.setFamilyKey(9703);
	result.setGenusKey(2435098);
	result.setSpeciesKey(2435099);
	// result.setScientificName("Puma concolor subsp. concolor");
	result.setCanonicalName("Puma concolor concolor");
	// result.setAuthorship("");
	// result.setPublishedIn("Mantissa Plantarum vol. 2 p. 266");
	// result.setAccordingTo("The Catalogue of Life, 3rd January 2011");
	// result.setNameType(NameType.WELLFORMED);
	// result.setTaxonomicStatus(TaxonomicStatus.ACCEPTED);
	result.setRank(Rank.SUBSPECIES);
	// result.setNumDescendants(0);
	// result.setNumOccurrences(0);
	result.setTaxonID("119916764");
	description = new Description();
	// result.getDescriptions().add(description);
	description.setDescription("The northern South American cougar (Puma concolor concolor) is a subspecies of the cougar that lives in the northern part of South America, from Colombia and Venezuela to Peru and northern Brazil. It is the nominate subspecies of Puma concolor. It preys on birds, deer, sloths, mice, frogs, agoutis, and lizards.");
	*/
	/*
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("mountain lion");
	vernacularName.setLanguage(Language.ENGLISH);
	vernacularName = new VernacularName();
	result.getVernacularNames().add(vernacularName);
	vernacularName.setVernacularName("puma");
	vernacularName.setLanguage(Language.ENGLISH);
	*/
	/* "descriptionsSerialized":{"values":["The northern South American cougar (Puma concolor concolor) is a subspecies of the cougar that lives in the northern part of South America, from Colombia and Venezuela to Peru and northern Brazil. It is the nominate subspecies of Puma concolor. It preys on birds, deer, sloths, mice, frogs, agoutis, and lizards."]} */
	/* ,"vernacularNamesSerialized":{"values":["en # mountain lion","en # puma"]}); */
	// // result.setSynonym(false);
	/* ,"higherClassificationMap":{"1":"Animalia","44":"Chordata","359":"Mammalia","732":"Carnivora","9703":"Felidae","2435098":"Puma","2435099":"Puma concolor"}}, */


	// http://api.gbif.org/v1/species/suggest?datasetKey=d7dddbf4-2cf0-4f39-9b2a-bb099caae36c&q=Puma%20con
	NameUsageSuggestRequest suggestRequest = new NameUsageSuggestRequest();
	suggestRequest.setQ("Puma con");
	suggestRequest.addParameter(NameUsageSearchParameter.DATASET_KEY, "d7dddbf4-2cf0-4f39-9b2a-bb099caae36c");

	SpeciesAPIClient sut = new SpeciesAPIClient();
	List<NameUsageSuggestResult> actual = sut.suggest(suggestRequest);

	printCollections("suggest", actual, expected);
	assertThat(actual, is(expected));
    }
    /*
    public List<NameUsageSearchResult> suggest(NameUsageSuggestRequest suggestRequest) {
	*/

    private List<ParsedName> createParsdNames() {
	List<ParsedName> expected = new Vector<ParsedName>();
	ParsedName pn = new ParsedName();
	expected.add(pn);
	pn.setScientificName("Abies alba Mill. sec. Markus D.");
	pn.setType(NameType.SCINAME);
	pn.setGenusOrAbove("Abies");
	pn.setSpecificEpithet("alba");
	pn.setAuthorsParsed(true);
	pn.setAuthorship("Mill.");
	pn.setSensu("sec. Markus D.");
	pn.setCanonicalNameWithMarker("Abies alba");
	pn.setCanonicalNameComplete("Abies alba Mill.");
	pn.setCanonicalName("Abies alba");

	pn = new ParsedName();
	expected.add(pn);
	pn.setScientificName("Abies pinsapo var. marocana (Trab.) Ceballos & Bolaño 1928");
	pn.setType(NameType.SCINAME);
	pn.setGenusOrAbove("Abies");
	pn.setSpecificEpithet("pinsapo");
	pn.setInfraSpecificEpithet("marocana");
	pn.setAuthorsParsed(true);
	pn.setAuthorship("Ceballos & Bolaño");
	pn.setYear("1928");
	pn.setBracketAuthorship("Trab.");
	pn.setCanonicalNameWithMarker("Abies pinsapo var. marocana");
	pn.setCanonicalNameComplete("Abies pinsapo var. marocana (Trab.) Ceballos & Bolaño, 1928");
	pn.setRankMarker("var.");
	pn.setCanonicalName("Abies pinsapo marocana");

	return expected;
    }

    @Test
    public void parse_String () {
	List<ParsedName> expected = newArrayList();
	expected.add(createParsdNames().get(0));
	SpeciesAPIClient sut = new SpeciesAPIClient();
	List<ParsedName> actual = sut.parse("Abies alba Mill. sec. Markus D.");
	assertThat(actual, is(expected));
    }

    @Test
    public void parse_String_array() {
	List<ParsedName> expected = createParsdNames();
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String[] names = new String[2];
	names[0] = "Abies alba Mill. sec. Markus D.";
	names[1] = "Abies pinsapo var. marocana (Trab.) Ceballos & Bolaño 1928";
	List<ParsedName> actual = sut.parse(names);
	assertThat(actual, is(expected));
    }

    @Test
    public void parse_by_POST() {

	List<ParsedName> expected = createParsdNames();
	SpeciesAPIClient sut = new SpeciesAPIClient();
	String names = "Abies alba Mill. sec. Markus D.";
	names = names + "\n";
	names = names + "Abies pinsapo var. marocana (Trab.) Ceballos & Bolaño 1928";

	List<ParsedName> result = sut.parse(sut.TEXT, names);
	String actual = result.toString();
	assertThat(actual, is(expected.toString()));
    }

    @Test
    public void getDescripitons() {
	List<Description> descriptions = newArrayList();

	// expected[0]
	Description description = new Description();
	// description.setKey(444629);
	description.setSourceTaxonKey(125796751);
	// description.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	description.setType("stimme");
	description.setLanguage(Language.GERMAN);
	description.setDescription("Der Gesang des Haussperlings wird nur vom Männchen vorgetragen und besteht aus einem monotonen, relativ lauten, rhythmischen „Tschilpen“ (meist einsilbig, auch „schielp“, „tschuip“, „tschirp“, manchmal auch zweisilbig wie „tschirrip“ oder „tschirrep“). Die Tonhöhe und die Anordnung der Elemente variieren von Vogel zu Vogel erheblich. Während des Singens vergrößert sich der Kehllatz. Analysen haben ergeben, dass diese Lautäußerungen komplex komponiert sind und sowohl individuelle Merkmale als auch Stimmungen darin codiert sein können. Als gesellige Vögel verfügen Haussperlinge über viele Rufe. Der übliche Warnruf bei Luftfeinden ist strukturell abweichend gegenüber anderen Sperlingsvögeln ein weiches, getrillertes „drüüü“, wobei dieser Ruf auch gelegentlich gegenüber größeren Nahrungskonkurrenten wie Möwen verwendet wird. Vor Bodenfeinden wird mit anhaltendem nasalen Rufen wie „kew kew“ oder auch „terrettett“ gewarnt. Zur Kopulation fordern Männchen und Weibchen mit leisen, gezogenen und nasalen Lauten auf, Weibchen verwenden dabei ein wiederholtes „djie“, der Kopulationsruf des Männchens ist ein wisperndes „iag iag“. Daneben gibt es einige weitere situationsabhängige Rufe, deren Dauer, Obertonstaffelung und -modulation recht verschieden gestaltet sein können (Stimmbeispiel).Freilebende Haussperlinge sind auch in der Lage, Alarmrufe von Staren und Amseln zu kopieren. Zudem zeigen jüngere Forschungen, dass die Alarmrufe anderer Vogelarten durchaus verstanden werden. Heute ist relativ unbekannt, dass Haussperlinge auch sehr lernfähige „Gesangsschüler“ sind. Im 18. Jahrhundert war es ein beliebtes Spiel, aufgezogenen Vögeln Lieder beizubringen. Es gibt eine stattliche Anzahl von Berichten und Belegen dafür, dass Haussperlinge, die beispielsweise in Gesellschaft von Kanarienvögeln aufgezogen wurden, deren rollendes Geträller perfekt erlernen, auch wenn sie dies mit ihrer rauen und lauten Stimme imitieren.HBV Band 14/1, P. d. domesticus, Stimme. Seite 61–67, siehe LiteraturHBV Band 14/1, P. d. domesticus. Verhalten. Seite 105–115, siehe Literaturnature-rings.de: Der es von den Dächern pfeift");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);
	/*
	// description.setKey(91928);
	// description.setUsageKey(116668229);
	// description.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	description.setType("distribution and habitat");
	description.setLanguage(Language.ENGLISH);
	description.setDescription("The House Sparrow originated in the Middle East and spread, along with agriculture, to most of Eurasia and parts of North Africa. Since the mid-nineteenth century, it has reached most of the world, due chiefly to deliberate introductions, but also through natural and shipborne dispersal. Its introduced range encompasses most of North America, Central America, southern South America, southern Africa, part of West Africa, Australia, New Zealand, and islands throughout the world. It has greatly extended its range in northern Eurasia since the 1850s, and continues to do so, as was shown by the colonisations around 1990 of Iceland and Rishiri Island, Japan. The extent of its range makes it the most widely distributed wild bird on the planet. IntroductionsThe House Sparrow has become highly successful in most parts of the world where it has been introduced. This is mostly due to its early adaptation to living with humans, and its adaptability to a wide range of conditions.Martin, Lynn B., II; Fitzgerald, Lisa (2005): A taste for novelty in invading house sparrows, Passer domesticus, Behavioral Ecology, 16(4): pp. 702–707 Other factors may include its robust immune response, compared to the Eurasian Tree Sparrow.Lee, Kelly A. (2005): Responding to inflammatory challenges is less costly for a successful avian invader, the house sparrow (Passer domesticus), than its less-invasive congener, Oecologia, 145(2): pp. 244–251 Where introduced, it can extend its range quickly, sometimes at a rate of over  per year. In many parts of the world it has been characterized as a pest, and poses a threat to native birds.Clergeau, Philippe; Levesque, Anthony; Lorvelec, Olivier (2004): The precautionary principle and biological invasion: the case of the House Sparrow on the Lesser Antilles, International Journal of Pest Management, 50(2): pp. 83–89 A few introductions have died out or been of limited success, such as those to Greenland and Cape Verde.The first of many successful introductions to North America occurred when birds from England were released in New York City, in 1852.Healy, Michael (2009): 'hardy/unkillable clichés': Exploring the Meanings of the Domestic Alien, Passer domesticus, Interdisciplinary Studies in Literature and Environment, 16(2): pp. 281–298. Oxford University Press The House Sparrow now occurs from the Northwest Territories to southern Panama, and it is one of the most abundant birds in North America.Franklin, K. (2007): The House Sparrow: Scourge or Scapegoat?, Naturalist News. Audubon Naturalist Society The House Sparrow was first introduced to Australia in 1863 at Melbourne and is common throughout the eastern part of the continent, but has been prevented from establishing itself in Western Australia, where every House Sparrow found in the state is killed.Massam, Marion: Sparrows, Farmnote(117/99). Agriculture Western Australia House Sparrows were introduced in New Zealand in 1859, and from there reached many of the Pacific islands, including Hawaii.In southern Africa birds of both the European subspecies domesticus and the Indian subspecies indicus were introduced around 1900. Birds of domesticus ancestry are confined to a few towns, while indicus birds have spread rapidly, reaching Tanzania in the 1980s. Despite this success, native relatives such as the Cape Sparrow also occur in towns, competing successfully with it.Summers-Smith, J. D. (1990): Granivorous birds in the agricultural landscape: pp. 11–29. Pánstwowe Wydawnictom NaukoweBrooke, R. K. (1997): The Atlas of Southern African Birds, 1. BirdLife South Africa In South America, it was first introduced near Buenos Aires around 1870, and quickly became common in most of the southern part of the continent. It now occurs almost continuously from Tierra del Fuego to the fringes of Amazonia, with isolated populations as far north as coastal Venezuela. HabitatThe House Sparrow is closely associated with human habitation and cultivation. It is not an obligate commensal of humans as some have suggested, as Central Asian birds usually breed away from humans in open country, and birds elsewhere are found away from humans.Hobbs, J. N. (1955): House Sparrow breeding away from Man, The Emu, 55(4): pp. 202Wodzicki, Kazimierz (May 1956): Breeding of the House Sparrow away from Man in New Zealand, Emu, 54: pp. 146–147 The only terrestrial habitats in which House Sparrows do not occur are dense forest and tundra. Well adapted to living around humans, it frequently lives and even breeds indoors, especially in factories, warehouses and zoos. It has been recorded breeding in an English coal mine  below ground, and feeding on the Empire State Building's observation deck at night.Brooke, R. K. (January 1973): House Sparrows Feeding at Night in New York, The Auk, 90(1): pp. 206 It reaches its greatest densities in urban centres, but its reproductive success is greater in suburbs, where insects are more abundant. On a larger scale, it is most abundant in wheat-growing areas such as the Midwestern United States.It tolerates a variety of climates, but prefers drier conditions, especially in moist tropical climates. It has several adaptations to dry areas, including a high salt toleranceMinock, Michael E. (1969): Salinity Tolerance and Discrimination in House Sparrows (Passer domesticus), The Condor, 71(1): pp. 79–80 and an ability to survive without water by ingesting berries. In most of eastern Asia the House Sparrow is entirely absent, replaced by the Eurasian Tree Sparrow.Melville, David S.; Carey, Geoff J. (998): Syntopy of Eurasian Tree Sparrow Passer montanus and House Sparrow P. domesticus in Inner Mongolia, China, Forktail, 13: pp. 125 Where these two species overlap, the House Sparrow is usually more common than the Eurasian Tree Sparrow, but one species may replace the other in a manner that Maud Doria Haviland described as \"random, or even capricious\". In most of its range the House Sparrow is extremely common, despite some declines, but in marginal habitats such as rainforest or mountain ranges, its distribution can be spotty.");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);
	*/

	// expected[1]
	description = new Description();
	// description.setKey(444630);
	description.setSourceTaxonKey(125796751);
	// description.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	description.setType("abstract");
	description.setLanguage(Language.GERMAN);
	description.setDescription("Der Haussperling (Passer domesticus) – auch Spatz genannt – ist eine Vogelart aus der Familie der Sperlinge (Passeridae) und einer der bekanntesten und am weitesten verbreiteten Singvögel. Der Spatz ist ein Kulturfolger und hat sich vor über 10.000 Jahren dem Menschen angeschlossen. Nach zahlreichen, teils beabsichtigten, teils unbeabsichtigten Einbürgerungen ist er mit Ausnahme der Tropen fast überall anzutreffen, wo Menschen sich das ganze Jahr aufhalten. Der weltweite Bestand wird auf etwa 500 Millionen Individuen geschätzt. Nach deutlichen Bestandsrückgängen in der zweiten Hälfte des 20. Jahrhunderts vor allem im Westen Mitteleuropas wurde die Art in die Vorwarnliste bedrohter Arten aufgenommen.");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);

	// expeced[2]
	description = new Description();
	// description.setKey(444631);
	description.setSourceTaxonKey(125796751);
	// description.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	description.setType("verhalten");
	description.setLanguage(Language.GERMAN);
	description.setDescription("Der Haussperling zeigt das ganze Jahr über ein geselliges und soziales Verhalten. Viele Verhaltensweisen des Haussperlings sind auf das Leben in der Gruppe ausgerichtet, und der Tagesablauf ist stark synchronisiert. AktivitätHaussperlinge werden während der bürgerlichen Dämmerung aktiv. Der Gesang beginnt im Mittel etwa 18&nbsp;Minuten vor Sonnenaufgang, wobei durch Bewölkung verursachte Helligkeitsunterschiede weitgehend ohne Einfluss bleiben. Das Ende der Aktivität liegt auch im Winter noch vor Sonnenuntergang.In mittleren Breiten werden gelegentlich nächtliche Aktivitäten beobachtet, zum Beispiel beim Insektenfang im Flutlicht von Industrieanlagen. Auch auf dem Empire State Building kann man mehr als 300&nbsp;Meter über dem Erdboden nachts jagende Spatzen entdecken. NahrungserwerbDie Nahrungsaufnahme erfolgt fast immer gesellig, auch während der Aufzucht der Jungen. Hierzu finden sich oft Schwärme, kleinere Trupps oder zumindest lose Verbände zusammen. In Getreidefeldern ist bei Trupps von etwa 20&nbsp;Vögeln die Nahrungsaufnahme am effizientesten, da die zur Sicherung verwendete Zeit in größeren Gemeinschaften kürzer wird, jedoch der Zeitaufwand für Auseinandersetzungen mit Artgenossen bei noch größeren Verbänden diesen Zeitgewinn mehr als aufwiegt. Wenn ein einzelner Haussperling eine Nahrungsquelle entdeckt, lockt er die anderen durch Rufe und wartet, bis er zu fressen beginnt. Dabei sind 75&nbsp;Prozent dieser „Pioniere“ Männchen. Manchmal werden Nahrungsbrocken bei Zerkleinerung mit Hilfe des Schnabels mit dem Fuß festgehalten, ähnlich dem Verhalten der Meisen. Größere Nahrungsstücke werden häufig auch transportiert und an anderer Stelle zerkleinert, auch im Nest.Vor allem bei in der Stadt lebenden Spatzen kann häufig das Absuchen von Kühlergrills parkender Autos nach toten Insekten beobachtet werden. Der Haussperling versucht sich gelegentlich auch als Luftjäger. Dabei startet er von einer Sitzwarte aus einen kurzen Jagdflug nach vorbeifliegenden Insekten. Dies wirkt zwar mühsam und nicht so elegant wie beispielsweise beim Grauschnäpper, führt aber dennoch nicht selten zum Erfolg. FortbewegungAm Boden bewegt sich der Haussperling fast immer beidbeinig hüpfend fort. Lediglich bei Annäherung an sehr nahe Objekte oder beim seitlichen Nachrücken auf Zweigen sind einzelne Schritte zu beobachten. Der Spatz hockt bei der Nahrungssuche oft flach auf den Läufen, so dass die Federn den Boden berühren. An senkrechten Hauswänden oder Stämmen klettert der Haussperling „rutschend“ und stützt sich auch auf den gespreizten Schwanz, hin und wieder sogar auf die halb geöffneten Flügel. In Zweigen bewegt er sich recht gewandt und kann dabei kopfüber um einen dünnen Zweig schwingen, ohne die Füße zu lösen. KomfortverhaltenHaussperlinge baden das ganze Jahr über, dabei ist Sonnenschein stark stimulierend. Vor dem etwa drei Minuten dauernden Bad wird oft getrunken. Staubbäder folgen häufig dem Bad oder wechseln damit ab. Die Bewegungen beim Staubbaden entsprechen denen beim Wasserbad. Meist erfolgt dieses Baden gemeinschaftlich nacheinander mit anschließender gemeinsamer Gefiederpflege. Gelegentlich wird die für das Staubbad genutzte Mulde auch mit einem Futterplätzen entsprechenden Drohverhalten gegen Artgenossen verteidigt. <gallery> Spatz Sandbad im Zoo Teil2 10prozent.ogg|Männchen beim Sandbad Spatzen beim Sandbaden.jpg|Haussperlinge beim Sandbad Family That Bathes Together.jpg|Haussperlingspaar beim gemeinsamen Bad </gallery> Territorial- und AggressionsverhaltenDer Haussperling verteidigt kein flächiges Brut- oder Nahrungsrevier, sondern nur die nächste Umgebung des Nests oder des Schlafplatzes. Zur Zeit der Fortpflanzung sind Weibchen in der Nähe des Nests gegenüber Männchen dominant, obwohl sie kleiner sind.Auseinandersetzungen mit Artgenossen werden hauptsächlich beim Nahrungserwerb, an Bade- und Schlafplätzen und am Nest beobachtet. Dabei werden fast 90&nbsp;Prozent der Konflikte zwischen Männchen ausgetragen. Aggressionen äußern sich oft durch frontales Drohen, wobei der Kopf tief vorgebeugt, der Schwanz gefächert und angehoben, die Rückenfedern gesträubt und die Flügel abgewinkelt werden. Bei höherer Intensität gibt es auch Kämpfe mit Vorwärtsbewegungen bei geöffnetem Schnabel und gegenseitigem Hacken, manchmal auch in der Luft. Aggressionen gegen andere Arten gibt es hauptsächlich bei Konkurrenz um Nistplätze, bei ausreichendem Angebot sind diese aber selten. Gelegentlich verhindern Haussperlinge die Ansiedlung anderer Höhlenbrüter in Nistkästen oder verdrängen sie daraus. Der Feldsperling, dessen Lebensraum sich teilweise mit dem des Haussperlings überschneidet, wird dabei vom Haussperling allein schon durch dessen früheren Brutbeginn verdrängt. FeindverhaltenWeibchen sind wachsamer und scheuer als Männchen. Die Fluchtdistanz bei Annäherung von Menschen ist vor allem in Städten niedriger, steigt jedoch bei zunehmender Truppgröße. Bei Bodenfeindalarm eilen Artgenossen herbei und folgen dem Feind hassend und warnend in Bäumen und Gebüsch. Auch Stare, vielerorts die einzigen überlegenen Nistplatzkonkurrenten, werden bei Inspektion potentieller Brutplätze schnarrend angehasst und manchmal vertrieben, in der Regel behalten im Konfliktfall aber die Stare die Oberhand.");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);


	// expeced[3]
	description = new Description();
	// description.setKey(444632);
	description.setSourceTaxonKey(125796751);
	// description.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	description.setType("haussperling und mensch");
	description.setLanguage(Language.GERMAN);
	description.setDescription("Der die Nähe des Menschen suchende Haussperling ist für viele Menschen der Inbegriff des Vogels überhaupt, da es meist der erste Vogel ist, den man als Kind richtig zu Gesicht bekommt. Das Verhältnis des Menschen ist zwiegespalten, lange wurde der lästige Haussperling bekämpft. Andererseits liegt er den Menschen auch am Herzen, auch wenn oder weil er klein und unscheinbar ist, und man traut ihm eine gute Portion Raffinesse zu. Etymologie und BenennungSowohl das Wort Sperling als auch die Koseform Spatz leiten sich vom althochdeutschen „sparo“ ab, und dieses hängt vermutlich wieder mit dem indogermanischen „spar“ wie „zappeln“ zusammen. Grund hierfür könnte das immer unruhig wirkende Verhalten des Haussperlings sein und auch sein beidbeiniges Umherhüpfen am Boden. Das englische „sparrow“ leitet sich auf die gleiche Weise her.NABU Deutschland: Der Haussperling, Vogel des Jahres 2002, Freund oder FeindDaneben besitzt der Spatz noch eine Reihe weiterer Namen, die teilweise nur lokale Bedeutung haben: Seine Vorliebe für Sämereien hat ihm die Namen Korndieb, Gerstendieb oder Speicherdieb eingetragen. In Norddeutschland wird er je nach Region Lüning, Lüntje, Lünk oder Dacklüün genannt, was soviel heißt wie „der Lärmende“. Wegen seiner Gewohnheit, in Misthaufen und Dung nach Körnern zu suchen, nennt man ihn auch Mistfink. Weitere Namen sind Leps und Mösche (von mussce, vulgärlateinisch von muscio = Spatz).Hans-Jürgen Martin, Tier und Natur, Sperlinge Geschichte als KulturfolgerVor über 10.000 Jahren schon, als die Menschen sesshaft wurden und die ersten Anfänge des Ackerbaus entwickelten, hatte sich der Haussperling bereits dem Menschen angeschlossen. Man geht auch davon aus, dass der Haussperling mit dem Anschluss an den Landwirtschaft betreibenden Menschen vom Zug- zum Standvogel wurde. Es wird auch vermutet, dass der Haussperling entsprechend der Ausbreitung der Landwirtschaft nordwestlich nach Europa vorgedrungen ist.Im Zuge der Entdeckung und Besiedlung der anderen Kontinente durch die Europäer wurde der Haussperling praktisch auf der ganzen Welt heimisch. Dabei lassen sich natürliche Ausweitungen des Verbreitungsgebiets im Gefolge des in unbesiedelte Gebiete vordringenden Menschen und Ausbreitung nach gezielter Einfuhr oder unbeabsichtigtem Transport vielfach nicht mehr unterscheiden.Nach Nordamerika beispielsweise gelangte der Spatz, als im Jahr 1852 europäische Auswanderer etwa 100 Vögel auf einem Friedhof in Brooklyn bei New York aussetzten. Nach etwa 20 weiteren Importen aus England und Deutschland mit über 1000 Vögeln und zahlreichen Verfrachtungen innerhalb des Landes besiedelte der Haussperling um die Wende zum 20. Jahrhundert bereits das gesamte Gebiet der Vereinigten Staaten. Die Art breitete sich damit schneller aus als der später eingeführte Star. Redewendungen und LegendenDa die Lebensräume von Haussperling und Mensch schon lange eng beieinander liegen, haben sich zahlreiche Redewendungen und Legenden entwickelt. Auch hierbei überwiegt das negative Image des Spatzen. Die bekanntesten sind folgende: „Ulmer Spatz“ im Ulmer Münster<br/><br/>Dreckspatz: Die Vorliebe für Staubbäder hat diese Bezeichnung verursacht.<br/><br/><br/><br/>Lieber den Spatz in der Hand als die Taube auf dem Dach: Dies soll ausdrücken, dass man sich lieber mit etwas Kleinem und sicher Erreichbarem zufriedengeben soll, als etwas Größeres und Wertvolleres zu begehren, dessen Erreichbarkeit ungewiss ist.<br/><br/><br/><br/>Mit Kanonen auf Spatzen schießen: Soll deutlich machen, dass man übertriebenen Aufwand betreibt, um etwas zu erreichen.<br/><br/><br/><br/>Die Spatzen pfeifen es von den Dächern: Ist ein Ausdruck für etwas, was längst kein Geheimnis mehr ist und sich überall herumgesprochen hat.<br/><br/><br/><br/>Ein Spatzenhirn haben: Für dieses Synonym für „dumm oder vergesslich sein“ musste der Spatz wohl als bekanntester kleiner Vogel Pate stehen. Indes sind Haussperlinge sogar relativ intelligente Vögel, denn sie waren beispielsweise die ersten Vögel, die in den 1930er Jahren in England den Meisen das Öffnen der Milchflaschen nachmachten.Vogelschutz-online e.&nbsp;V.: HaussperlingSchweizerische Vogelwarte: Vögel der Schweiz. Kohlmeise<br/><br/><br/><br/>Der Ulmer Spatz: Hier soll der Spatz als Ideengeber beim Bau des Münsters fungiert haben, indem er einen Strohhalm längs im Schnabel trug, nachdem die Ulmer vergeblich versucht hatten, einen großen Balken quer durch das Stadttor zu transportieren.<br/><br/> Der Spatz als SchädlingDer frühere Ruf des Haussperlings als Schädling ist vor allem auf seine Vorliebe für Körner zurückzuführen. Auch war der Spatz bis zum Beginn des 20. Jahrhunderts weit zahlreicher als heute. Dies führte beispielsweise dazu, dass König Friedrich der Große im 18. Jahrhundert ein Kopfgeld aussetzte, um die herrschaftlichen Felder vor den Spatzen zu schützen. Wegen der durch die Dezimierung der Sperlinge verursachten starken Ausbreitung der Insekten wurde dieses Kopfgeld jedoch bald wieder abgeschafft.NABU Deutschland: Der Haussperling - Vogel des Jahres 2002, Jahresvogelbroschüre, Seite 14 (PDF; 2,3&nbsp;MB)Verstärkte, kampagneartige Aktivitäten auf Basis häufig übertriebener oder pauschaler Schadensschätzungen gab es nach dem Zweiten Weltkrieg. Dabei wurde dem Haussperling mittels Spezialfallen, „Giftweizen“ oder durch anderweitigem systematischem Einsatz von Giftstoffen nachgestellt, wiederum unterstützt durch die Auslobung von Geldprämien. Diese Maßnahmen sorgten lokal für deutliche Dezimierungen des Bestandes, die Lücken waren aber nach zwei Jahren meist wieder geschlossen. Weit wirkungsvoller war dagegen die nicht als Bekämpfungsmaßnahme gedachte Umgestaltung des Lebensraums in der zweiten Hälfte des 20. Jahrhunderts (siehe Bestand und Bestandsentwicklung). Die Zeit der Spatzenverfolgung reichte bis in die zweite Hälfte des 20. Jahrhunderts hinein. Im Jahr 1965 wurden durch den DBV, der damaligen Vorläuferorganisation des NABU, spezielle Futterhäuschen mit den bemerkenswerten Namen „Kontraspatz“ oder „Spatznit“ vertrieben. Damit sollten die Haussperlinge von der Winterfütterung ausgeschlossen werden, da man die Spatzen als zu große Konkurrenz für die übrigen Singvögel ansah.Auch heute wird der Haussperling teilweise noch als Hygieneschädling betrachtet und findet sich deshalb auch in einem „Lexikon für Schädlinge“ wieder.Mult!Clean Umwelthygiene: Lexikon für Schädlinge. Haussperling Problematisch ist dabei insbesondere das Eindringen und Einnisten in Lebensmittelmärkten und Großküchen, da der Haussperling als potentieller Überträger von Krankheiten gilt. In Deutschland geraten hier das Bundesnaturschutzgesetz und die Lebensmittelhygieneverordnung in Konflikt, so dass die Rechtslage bei einer Bekämpfung der Sperlinge unklar ist.M. Felke, B. Kleinlogel: Der Haussperling – in Lebensmittelbetrieben nicht tolerierbar. In: Der Lebensmittelbrief. 11/2005 Große Medienresonanz erlangte der Abschuss eines Spatzes, der bei den Vorbereitungen des Domino Day 2005, einer Fernsehsendung des Senders RTL, in die Halle geflogen war und vor Sendebeginn bereits etliche Steine umgeworfen hatte. Rolle bei Übertragung von KrankheitenHaussperlinge wurden mit der Verbreitung einiger Krankheiten, die für den Menschen oder für domestizierte Tiere von Bedeutung sind, in Verbindung gebracht. Spatzen gelten als Überträger von Bakterien (beispielsweise Salmonellen) oder auch als Reservoirwirt bei der Verbreitung verschiedener Arboviren. Für die Ausbreitung eines Vertreters dieser Virengruppe, des St.-Louis-Enzephalitis-Virus in Nordamerika, wird dem Haussperling eine Schlüsselrolle unterstellt. Auch mit dem West-Nil-Virus wird der Haussperling in Verbindung gebracht. Die Charakteristik der Ausbreitung dieses Virus in den USA hat den Haussperling neben den Rabenvögeln und verschiedenen Zugvögeln in Verdacht gebracht, bei der Verbreitung eine entscheidende Rolle zu spielen.Ted. R. Anderson: Biology of the ubiquitous house sparrow. From genes to populations. Seite 427ff, siehe LiteraturJ. H. Rappole, Z. Hubálek: Migratory birds and West Nile virus. 2003Im Labor wurde nachgewiesen, dass Spatzen mit der besonders virulenten Form des Influenzavirus vom Typ H5N1 infiziert werden können, wenn sie auch nur schwach darauf reagieren. Außerhalb des Labors wurden bislang nur in Ostasien im unmittelbaren Umkreis massiv von Vogelgrippe H5N1 befallener Geflügelhaltungen infizierte Sperlinge entdeckt.L. E. L. Perkins, D. E. Swayne: Varied Pathogenicity of a Hong Kong-origin H5N1 Avian Influenza Virus in Four Passerine Species and Budgerigars. 2003 (PDF; 975&nbsp;kB)Trotz alldem ist zusammenfassend festzustellen, dass der Haussperling keine besondere Rolle bei der Übertragung für den Menschen gefährlicher Krankheitserreger spielt. Es wäre eher die Frage zu stellen, welche Auswirkungen vom Menschen verbreitete Krankheitserreger auf Sperlingspopulationen haben. Symbol der UnkeuschheitAuch wenn sich Haussperlinge nicht häufiger als andere sozial lebende Vogelarten paaren, brachte ihr Verhalten ihnen im Mittelalter den Ruf der Unkeuschheit ein. Dies lag wohl auch daran, dass die Paarung direkt vor den Augen der Menschen stattfand und Spatzen dabei geräuschvoller zu Werke gehen als manch andere Vögel. Man glaubte damals, dass Spatzen bei so vielen Begattungen höchstens ein Jahr leben könnten.Weit verbreitet war auch der Glaube, dass Spatzenfleisch den Liebesdrang steigere und zur Unzucht ansporne. Im alten Rom und noch früher, im Griechenland der Antike, wurden ähnliche Aberglauben beschrieben. Bei Aristophanes, einem griechischen Komödiendichter, ritten die sehnsüchtigen Frauen auf Spatzen von der Akropolis zu ihren Männern herab.");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);

	// expeced[4]
	description = new Description();
	// description.setKey(444634);
	description.setSourceTaxonKey(125796751);
	// description.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	description.setType("lebenserwartung und feinde");
	description.setLanguage(Language.GERMAN);
	description.setDescription("Die durchschnittliche Lebenserwartung geschlechtsreifer Haussperlinge beträgt 1,5 bis 2,3&nbsp;Jahre, bezieht man auch die Jungvögel mit ein, beträgt sie lediglich 9&nbsp;Monate. In der Stadt ist die Lebenserwartung höher als in ländlichen Gebieten, in den Niederlanden wurde bei einer Untersuchung festgestellt, dass im Bereich von Vororten 18&nbsp;Prozent der Spatzen 5 und mehr Jahre alt wurden, in ländlichen Gebieten hingegen nur 4&nbsp;Prozent. In Freiheit wurden durch Beringung verschiedentlich um die 14&nbsp;Jahre alte Haussperlinge nachgewiesen. In Gefangenschaft ist ein höheres Alter möglich, das bisher beobachtete maximale Alter beträgt angeblich 23&nbsp;Jahre.HBV Band 14/1, P. d. domesticus. Bruterfolg, Sterblichkeit, Alter. Seite 89–94, siehe LiteraturGefahr droht den in Freiheit lebenden Spatzen vor allem durch Predation und besonders in großen Städten auch durch den Straßenverkehr. Die größten Verluste mit 45 bis 56 Prozent der Gesamtmortalität erleiden die Altvögel während der Brutzeit. Zu den Bodenfeinden zählen Steinmarder und vor allem Katzen. Die den Spatzen jagenden Luftfeinde sind vor allem Sperber, Schleiereulen und Turmfalken. Dabei sind ausgefärbte Männchen mit ausgeprägtem Kehlfleck häufiger das Opfer von Greifvögeln. Haussperlinge sind vielerorts die Hauptbeute des Sperbers mit einem Anteil von bis über 50 Prozent. Aber auch für den Turmfalken stellen sie beispielsweise in Berlin die häufigste Vogelbeute dar.Sonja Kübler: Nahrungsökologie stadtlebender Vogelarten entlang eines Urbangradienten. Berlin 2005");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);

	// expeced[5]
	description = new Description();
	// description.setKey(444635);
	description.setSourceTaxonKey(125796751);
	// description.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	description.setType("nahrung");
	description.setLanguage(Language.GERMAN);
	description.setDescription("Haussperling, Männchen Weibchen bei der Nahrungssuche. Der Haussperling ernährt sich hauptsächlich von Sämereien und dabei vor allem von den Samen kultivierter Getreidearten, die in ländlichen Gebieten 75&nbsp;Prozent der Gesamtnahrung ausmachen können. Bevorzugt werden Weizen vor Hafer und Gerste. Regional und saisonal kann der Anteil der Samen von Wildgräsern und -kräutern den Getreideanteil erreichen oder übertreffen. Von Frühjahr bis Sommer spielt auch animalische Nahrung eine wichtige Rolle und kann bis zu 30&nbsp;Prozent der Gesamtnahrung ausmachen. Dabei handelt es sich um Insekten einschließlich deren Entwicklungsstadien sowie andere Wirbellose. Vor allem in der Stadt zeigen Spatzen ein opportunistisches Verhalten und werden zu Allesfressern. Besonders an Imbissständen und in Freiluftlokalen stellen sie das unter Beweis.HBV Band 14/1, P. d. domesticus. Nahrung. Seite 115–118, siehe LiteraturDie Jungen füttert der Haussperling in den ersten Tagen fast ausschließlich mit Raupen und anderen zerkleinerten Insekten. Wenn zu wenig tierische Nahrung zur Verfügung steht und beispielsweise ausschließlich Brot an die Nestlinge verfüttert wird, kann dies Verdauungsstörungen verursachen, die zum Tod der Nestlinge führen können. Mit zunehmendem Alter der Jungen verfüttern die Eltern dann mehr und mehr auch Sämereien, wobei der vegetarische Anteil auf ein Drittel steigt.");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);

	/*
	description = new Description();
	// description.setKey(91930);
	description.setSourceTaxonKey(116668229);
	// description.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	description.setType("relationships with humans");
	description.setLanguage(Language.ENGLISH);
	description.setDescription("The House Sparrow is closely associated with humans. They are believed to have become associated with humans around 10,000 years ago. Subspecies bactrianus is least associated with humans and considered to be evolutionarily closer to the ancestral non-commensal populations.Sætre, G.-P. (2012): Single origin of human commensalism in the house sparrow, Journal of Evolutionary Biology, 25(4): pp. 788–796 Usually, it is regarded as a pest, since it consumes agricultural products and spreads disease to humans and their domestic animals. Even birdwatchers often hold it in little regard because of its molestation of other birds. In most of the world the House Sparrow is not protected by law. Attempts to control House Sparrows include the trapping, poisoning, or shooting of adults; the destruction of their nests and eggs; or less directly, blocking nest holes and scaring off sparrows with noise, glue, or porcupine wire.Invasive Species Specialist Group: ISSG Database: Ecology of Passer domesticus However, the House Sparrow can be beneficial to humans as well, especially by eating insect pests, and attempts at the large-scale control of the House Sparrow have failed.The House Sparrow has long been used as a food item. From around 1560 to at least the 19th century in northern Europe, earthenware \"sparrow pots\" were hung from eaves to attract nesting birds so that the young could be readily harvested. Wild birds were trapped in nets in large numbers, and sparrow pie was a traditional dish, thought, because of the association of sparrows with lechery, to have aphrodisiac properties. Sparrows were also trapped as food for falconer's birds and zoo animals. In the early part of the twentieth century, sparrow clubs culled many millions of birds and eggs in an attempt to control numbers of this perceived pest, but with only a localised impact on numbers.House Sparrows have been kept as pets at many times in history, though they have no bright plumage or attractive songs, and raising them is difficult. StatusThe House Sparrow has an extremely large range and population, and is not seriously threatened by human activities, so it is assessed as Least Concern for conservation on the IUCN Red List. However, populations have been declining in many parts of the world.Daniels, R. J. Ranjit (2008): Can we save the sparrow?, Current Science, 95(11): pp. 1527–1528De Laet, J.; Summers-Smith, J. D. (2007): The status of the urban house sparrow Passer domesticus in north-western Europe: a review, Journal of Ornithology, 148(Supplement 2): pp. 275–278 These declines were first noticed in North America, where they were initially attributed to the spread of the House Finch, but have been most severe in Western Europe.Summers-Smith, J. Denis (2005): Changes in the House Sparrow Population in Britain, International Studies on Sparrows, 5: pp. 23–37 Declines have not been universal, as no serious declines have been reported from Eastern Europe, but have even occurred in Australia, where the House Sparrow was introduced recently.In Great Britain, populations peaked in the early 1970s, but have since declined by 68% overall,20 November 2008: Sparrow numbers 'plummet by 68%', BBC News and about 90% in some regions.House sparrow, ARKive In London, the House Sparrow almost disappeared from the central city. The numbers of House Sparrows in the Netherlands have dropped in half since the 1980s,van der Poel, Guus (29 January 2001): Concerns about the population decline of the House Sparrow Passer domesticus in the Netherlands so the House Sparrow is even considered an endangered species.Gould, Anne Blair (29 November 2004): House sparrow dwindling. Radio Nederland Wereldomroep This status which came to widespread attention after a female House Sparrow, referred to as the \"Dominomus\", was killed after knocking down dominoes arranged as part of an attempt to set a world record.19 November 2005: Sparrow death mars record attempt, BBC News These declines are not unprecedented, as similar reductions in population occurred when the internal combustion engine replaced horses in the 1920s and a major source of food in the form of grain spillage was lost.Bergtold, W. H. (1921): The English Sparrow (Passer domesticus) and the Automobile, The Auk, 38(2): pp. 244–250Various causes for the dramatic decreases in population have been proposed, including predation, in particular by Eurasian Sparrowhawks;MacLeod, Ross (23 March 2006): Mass-dependent predation risk as a mechanism for house sparrow declines?, Biology Letters, 2(1): pp. 43–46Bell, Christopher P.; Baker, Sam W.; Parkes, Nigel G.; Brooke, M. de L.; Chamberlain, Dan E. (2010): The Role of the Eurasian Sparrowhawk (Accipiter nisus) in the Decline of the House Sparrow (Passer domesticus) in Britain, The Auk, 127(2): pp. 411–420 electromagnetic radiation from mobile phones;Balmori, Alfonso; Hallberg, Örjan (2007): The Urban Decline of the House Sparrow (Passer domesticus): A Possible Link with Electromagnetic Radiation, Electromagnetic Biology and Medicine, 26(2): pp. 141–151 and diseases. A shortage of nesting sites caused by changes in urban building design is probably a factor, and conservation organisations have encouraged the use of special nest boxes for sparrows.De Laet, Jenny; Summers-Smith, Denis; Mallord, John (2009): Meeting on the Decline of the Urban House Sparrow Passer domesticus: Newcastle 2009 (24–25 Feb), International Studies on Sparrows, 33: pp. 17–32Chetan, S. Jawale (2012): Improved Design of Nest Box For Indian House Sparrow, Passer domesticus indicus, Bioscience Discovery, 3(1): pp. 97–100 A primary cause of the decline seems to be an insufficient supply of insect food for nestling sparrows.Peach, W. J.; Vincent, K. E.; Fowler, J. A.; Grice, P. V. (2008): Reproductive success of house sparrows along an urban gradient, Animal Conservation, 11(6): pp. 1–11 Declines in insect populations result from an increase of monoculture crops, the heavy use of pesticides, the replacement of native plants in cities with introduced plants and parking areas, and possibly the introduction of unleaded petrol, which produces toxic compounds such as methyl nitrite.Summers-Smith, J. Denis (September 2007): Is unleaded petrol a factor in urban House Sparrow decline?, British Birds, 100: pp. 558Protecting insect habitats on farms,Hole, D. G. (2002): Ecology and conservation of rural house sparrows, Ecology of Threatened Species. Royal Society for the Protection of BirdsHole, David G.; Whittingham, M. J.; Bradbury, Richard B.; Anderson, Guy Q. A.; Lee, Patricia L. M.; Wilson, Jeremy D.; Krebs, John R. (29 August 2002): Agriculture: Widespread local house-sparrow extinctions, Nature, 418(6901): pp. 931–932 and planting native plants in cities benefit the House Sparrow, as does establishing urban green spaces.Mukherjee, Sarah (20 November 2008): Making a garden sparrow-friendly, BBC News To raise awareness of threats to the House Sparrow, World Sparrow Day has been celebrated on 20 March across the world since 2010. Cultural associationsTo many people across the world, the House Sparrow is the most familiar wild animal and, because of its association with humans and familiarity, it is frequently used to represent the common and vulgar, or the lewd. One of the reasons for the introduction of House Sparrows throughout the world was their association with the European homeland of many immigrants. Birds usually described later as sparrows are referred to in many works of ancient literature and religious texts in Europe and western Asia. These references may not always refer specifically to the House Sparrow, or even to small, seed-eating birds, but later writers who were inspired by these texts often had the House Sparrow in mind. In particular, sparrows were associated by the ancient Greeks with Aphrodite, the goddess of love, due to their perceived lustfulness, an association echoed by later writers such as Chaucer and Shakespeare. Jesus's use of \"sparrows\" as an example of divine providence in the Gospel of Matthew also inspired later references, such as that in Shakespeare's Hamlet and the Gospel hymn His Eye Is on the Sparrow. <hiero>G37</hiero> The House Sparrow is only represented in ancient Egyptian art very rarely, but an Egyptian hieroglyph is based on it. The sparrow hieroglyph had no phonetic value and was used as a determinative in words to indicate small, narrow, or bad. An alternative view is that the hieroglyph meant \"a prolific man\" or \"the revolution of a year\".");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);
	*/

	// expeced[6]
	description = new Description();
	// description.setKey(444636);
	description.setSourceTaxonKey(125796751);
	// description.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	description.setType("fortpflanzung");
	description.setLanguage(Language.GERMAN);
	description.setDescription("Haussperlinge bei der Kopulation Die Geschlechtsreife tritt bei Haussperlingen am Ende des ersten Lebensjahres ein. Spatzen führen in der Regel eine lebenslange Dauerehe. Wenn ein Partner stirbt, finden Neuverpaarungen jedoch schnell statt. Vereinzelt kommt auch Bigynie (Polygynie) vor.In Mitteleuropa beginnt die hauptsächliche Brutzeit Ende April und reicht bis August. Die auf der Südhalbkugel beheimateten Haussperlinge haben ihre Brutperiode an die dortigen Jahreszeiten angepasst. In diesem Zeitraum werden zwei bis drei, selten sogar vier Bruten aufgezogen. Bei den Erst- und Zweitbruten werden aus gut einem Drittel der gelegten Eier flügge Jungvögel, bei den späteren Bruten ist es nur noch ein Fünftel. Darüber hinaus ist die Mortalität der Jungvögel nach dem Ausfliegen in den ersten Wochen gravierend. Nach einem Jahr leben in ländlichen Gebieten nur noch 20&nbsp;Prozent, in Stadthabitaten immerhin bis zu 40&nbsp;Prozent der Jungvögel. Für die hohe Sterblichkeit dürften vor allem Schwierigkeiten bei der selbstständigen Nahrungsbeschaffung und hohe Predation maßgeblich sein. Neststandort und NestDer Haussperling ist Nischen-, Höhlen- und Freibrüter mit starker Neigung zum gemeinschaftlichen Brüten. Er nistet manchmal auch allein, oft aber in lockeren Verbänden oder Kolonien, wobei die Nester dabei meist einen Mindestabstand von 50 Zentimetern aufweisen. Die vielfältige Nutzung aller geeigneten Strukturen als Neststandort sind Ausdruck der besonderen Anpassungsfähigkeit des Haussperlings. Als typische Nistplätze dienen geschützte Hohlräume an oder in der Nähe von Gebäuden, sei es unter losen Dachpfannen oder in Mauerlöchern oder Nischen unter dem Vordach. Aber auch Nistkästen, Schwalbennester oder Spechthöhlen werden ausgewählt. Gelegentlich kann man Sperlinge auch als Untermieter in Storchennestern finden, wobei diese dabei davon profitieren, dass sich ihre Luftfeinde nicht in die Nähe solcher Nester wagen. Besteht Nistplatzmangel, können auch Freinester in Bäumen oder Büschen angelegt werden, die mit einem Dach aus Halmen versehen werden. Die Nesthöhe bei Freibrütern liegt zwischen 3 und 8 Metern und damit im Mittel höher und für Predatoren unzugänglicher als beim Feldsperling. Freinester werden als die ursprüngliche Nistweise des Haussperlings angesehen.Unabhängig vom Ort der Nestanlage handelt es sich im Prinzip immer um ein Kugelnest mit seitlichem Eingang. Das Nest wird nicht besonders sorgfältig gebaut, das außen nicht bearbeitete Nistmaterial hängt meist lose herab. Spatzen verbauen fast alles, beispielsweise Stroh, Gras, Wolle, Papier oder Lumpen. Das Material wird dabei weniger durch Auswahl als durch seine Verfügbarkeit im Umkreis von 20 bis 50 Metern bestimmt. Die Nestmulde wird zur Auspolsterung mit feinen Halmen und Federn ausgekleidet. Freistehende Nester erreichen Fußballgröße, Nester in Nischen und Höhlen werden den Gegebenheiten angepasst und variieren beträchtlich in der Größe. Das Nest wird meist vom Männchen während der Balz begonnen, in Mitteleuropa frühestens ab Mitte März. Der Neuanlage von Nestern geht besonders bei Erstbrütern eine Phase ziellosen Umhertragens von Nistmaterial voraus. Beide Partner vollenden das Nest gemeinsam, am intensivsten in der Woche vor Legebeginn. Der Nestbau kann sich über Wochen hinziehen, nach Nestverlust kann aber in zwei bis drei Tagen Ersatz geschaffen werden.HBV Band 14/1, P. d. domesticus. Fortpflanzung. Seite 79–89, siehe Literatur Balz und PaarungDie Balz beginnt mit der Besetzung des Brutplatzes durch die Männchen, in Mitteleuropa teilweise schon ab Mitte Februar und vor allem im März. Bei der Partnerwahl spielt für das Weibchen sowohl ein möglichst geschützter Nistplatz als auch der beim Singen anschwellende Brustlatz des Männchens eine Rolle. Das unverpaarte Männchen wirbt mit aufgeplustertem Gefieder im engeren Nestbereich mit hohen „tschili“, „szilib“ oder ähnlichen Rufen. Bekundet ein Weibchen Interesse, zeigt ihm das Männchen den Nistplatz, indem es mit trockenen Halmen im Schnabel einschlüpft. Das Weibchen folgt dem Männchen durch kurzes Einschlüpfen und prüft den Nistplatz. Erst von diesem Moment an beginnt das Männchen den eigentlichen Gesang, das strukturarm und monoton wirkende Tschilpen, oft stundenlang vorzutragen.Auffällig bei Haussperlingen ist auch die Gruppenbalz. Diese beginnt durch rasante und lärmende Verfolgung eines Weibchens durch zwei bis acht Männchen. Meist in dichter Vegetation wird das Weibchen von den balzenden Männchen umringt und diese versuchen abwechselnd, das sich wehrende Weibchen in der Kloakenregion zu picken und zu kopulieren. Alle tschilpen erregt und lassen in diesem Moment jede Vorsicht vermissen. In der Regel kommt es nicht zur Kopulation. Das mit dem Weibchen verpaarte Männchen ist auch beteiligt und bleibt bis zum Schluss in der Nähe des Weibchens. Die Bedeutung der Gruppenbalz ist noch offen.Kopulationen im frühen Stadium der Fortpflanzungsperiode werden meist erfolglos vom Männchen gesucht. Dabei hüpft es mit gesträubtem Gefieder, hängenden Flügeln und aufgestelztem Schwanz hin und her. In der späteren fruchtbaren Phase ist es das Weibchen, das zur Paarung auffordert. Es duckt sich dabei waagrecht mit leicht erhobenem Schwanz und vibrierenden Flügeln. Weibchen können dabei manchmal 15 bis 20 Mal in der Stunde zur Kopulation auffordern. Bei koloniebrütenden Paaren sind wiederum die Männchen an häufigerer Kopulation zur Sicherung der eigenen Vaterschaft interessiert. Dieses Verhalten und auch das Bewachen des Weibchens durch das Männchen ist aber nur bedingt wirksam, in 8 bis 19&nbsp;Prozent der Fälle wurden Fremdkopulationen nachgewiesen. Gelege und BrutEier des Haussperlings im Muséum de Toulouse  Das Gelege besteht aus vier bis sechs Eiern mit einer durchschnittlichen Größe von 15&nbsp;×&nbsp;22 Millimetern und einem Gewicht von etwa 3&nbsp;Gramm. In Gestalt, Größe und Farbe sind die Eier sehr unterschiedlich, bei einem individuellen Weibchen aber recht konstant. Sie sind weiß bis schwach grünlich oder gräulich und mit grauen oder braunen Flecken versehen, wobei die Fleckung die Grundfarbe manchmal völlig verdeckt. Die letzten Eier eines Geleges sind nach Breite und Gewicht größer als die ersten, wobei dieser Unterschied bei späteren Bruten noch ausgeprägter ist. Daher sind die zuletzt geschlüpften Jungen im Vorteil.Das regelmäßige Brüten beginnt normalerweise nach Ablage des vorletzten Eies und dauert ab diesem Zeitpunkt gerechnet in der Regel zwischen 10 und 15&nbsp;Tagen. Die Brutdauer wird durch die Außentemperatur beeinflusst und ist deshalb bei der dritten Brut meist kürzer. Bei witterungsbedingten Brutunterbrechungen kann die Brutzeit auch bis zu 22&nbsp;Tagen andauern. Beide Partner brüten abwechselnd, wobei das Weibchen meist die Nacht auf dem Gelege verbringt. Während das Weibchen auf Nahrungssuche ist, hält das Männchen die Eier vermutlich nur warm, denn es hat keinen Brutfleck. Entwicklung der JungenWeibchen füttert sein gerade flügge gewordenes Küken Die geschlüpften Jungen werden durch beide Eltern gehudert und zu Beginn vor allem mit zerkleinerten Insekten, später zunehmend auch mit Sämereien gefüttert. In den ersten Tagen wird der Kot durch die Eltern verschluckt, später bis zu 20&nbsp;Meter weit hinausgetragen. Die Dauer der Nestlingszeit schwankt sehr stark, die Beobachtungen reichen von 11 bis 23&nbsp;Tagen, die Regel sind 14 bis 16&nbsp;Tage. Ungefähr nach dem vierten Tag sind die Augen der Jungen geöffnet, am 8. bis 9.&nbsp;Tag werden die Nestlinge durch zunehmendes Aufplatzen der Federkiele farbig.The birds of North America Online, House SparrowGehen beide Eltern verloren, so finden sich durch die intensiven Bettelrufe der Jungen animiert meistens stellvertretende Bruthelfer aus der Nachbarschaft, die die Jungen füttern, bis sie selbstständig sind. Alle Jungen verlassen das Nest innerhalb weniger Stunden und sind in der Regel schon gut flugfähig. Sie fressen bereits nach ein bis zwei Tagen ein wenig selbst und sind in der Regel nach 7 bis 10, spätestens nach 14&nbsp;Tagen selbstständig.");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);


	// expeced[7]
	description = new Description();
	// description.setKey(444637);
	description.setSourceTaxonKey(125796751);
	// description.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	description.setType("aussehen und merkmale");
	description.setLanguage(Language.GERMAN);
	description.setDescription("Kürzlich ausgeflogener Haussperling Der Haussperling ist ein kräftiger und etwas gedrungener Singvogel. Er wiegt ungefähr 30 Gramm und die Körperlänge liegt bei etwa 14 bis 16 Zentimeter – er ist damit ein wenig größer als der nah verwandte Feldsperling. Der Haussperling fällt besonders durch seinen großen Kopf und den kräftigen, konischen Schnabel auf. Die Länge der Flügel beträgt 71 bis 82 Millimeter, die Spannweite misst etwa 23 Zentimeter. Männchen und Weibchen unterscheiden sich deutlich in ihrer Färbung und sind im Gegensatz zum Feldsperling leicht zu unterscheiden.Die Männchen sind deutlich kontrastreicher gezeichnet als die Weibchen, sie haben eine schwarze oder dunkelgraue Kehle und einen schwarzen Brustlatz, der aber im Herbst nach der Mauser von helleren Federrändern verdeckt sein kann. Der Scheitel ist bleigrau und von einem kastanienbraunen Feld begrenzt, das vom Auge bis in den Nacken reicht. Die Wangen sind hellgrau bis weißlich. Der Rücken ist braun mit schwarzen Längsstreifen. Die Flügel sind ebenso gefärbt; eine weiße Flügelbinde ist deutlich erkennbar, eine zweite nur angedeutet. Brust und Bauch sind aschgrau. In Stadtzentren und Industriegebieten ist das Gefieder infolge von Verschmutzung meist weit weniger kontrastreich. Relativ häufig treten teilalbinotische Individuen auf.Älterer Nestling Die Weibchen sind unscheinbarer als die Männchen und matter braun, aber sehr fein gezeichnet. Die Oberseite ist hell graubraun, der Rücken schwarzbraun und gelbbraun gestreift. Der ebenfalls graubraune Kopf hat einen hellen Überaugenstreif, der vor allem hinter dem Auge deutlich ist. Jungvögel sehen wie Weibchen aus, sie sind nur etwas heller und gelblicher gefärbt. Sie bleiben, nachdem sie flügge geworden sind, einige Tage an den gelblichen Schnabelwülsten erkennbar. Federkleid und MauserDie Jugendmauser ist eine Vollmauser und beginnt im Alter von sechs bis acht Wochen. Damit die Mauser vor Beginn der ungünstigeren Witterungsperiode abgeschlossen ist, kann sie je nach Zeitpunkt der Geburt von durchschnittlich 82 auf 64&nbsp;Tage verringert sein. Die Jahresmauser der Altvögel ist ebenfalls eine Vollmauser. Sie findet in Mitteleuropa in den Monaten Juli oder August statt. Bei Gefahr oder Stress neigen Sperlinge auch zur Schockmauser. Das Sperlingsgefieder besteht vor der Mauser aus 3200&nbsp;Federn, die insgesamt 1,4&nbsp;Gramm wiegen. Unmittelbar nach der Mauser sind es ungefähr 3600&nbsp;Federn mit einem Gewicht von 1,9&nbsp;Gramm. Zur Pflege des Gefieders nehmen die Tiere Staubbäder, um sich vor Federparasiten zu schützen.HBV Band 14/1, P. d. domesticus. Mauser. Seite 60f, siehe Literatur Manfred Giebing: Der Haussperling, Vogel des Jahres 2002Männlicher Haussperling im Flug FlugHaussperlinge fliegen schnell und geradlinig, relativ niedrig und meist vom Nistplatz zu einem nahe gelegenen Baum oder Gebüsch. Dabei können sie Geschwindigkeiten von annähernd 60 Kilometern pro Stunde erreichen. Die Flügel schwingen in der Sekunde etwa 13 mal auf und ab. Der Distanzflug ist leicht wellenförmig mit fallenden Gleitphasen, in denen die Flügel leicht angelegt sind, der Flug ist dabei aber im Vergleich zu den Finkenarten flacher gewellt.HBV Band 14/1, P. d. domesticus. Verhalten. Seite 95–105, siehe Literatur");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);



	// expeced[8]
	description = new Description();
	// description.setKey(444638);
	description.setSourceTaxonKey(125796751);
	// description.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	description.setType("systematik");
	description.setLanguage(Language.GERMAN);
	description.setDescription("Taxonomische Einordnung der SperlingeFrüher glaubte man, dass die Familie der Sperlinge eng mit den afrikanischen Webervögeln verwandt sei, und ordnete demzufolge die Sperlinge als Unterfamilie (Passerinae) der Familie der Webervögel zu. Vergleiche der DNA-Sequenzen verschiedener Arten haben ergeben, dass auch Verwandtschaftsbeziehungen zu Stelzen, Piepern und Braunellen bestehen. Auch wenn dies weiterhin umstritten ist, sieht man die Sperlinge heute deshalb als eigenständige Familie (Passeridae). Diese Familie wird in vier Gattungen und 36 Arten unterteilt.HBV Band 14/1, P. domesticus. Seite 35–45, siehe LiteraturEinhard Bezzel: Kompendium der Vögel Mitteleuropas. Band II, Seite 584–589, siehe Literatur Entstehung der Art des HaussperlingsJohnston und Kitz vermuteten 1977, dass sich die Art des Haussperlings als ausgesprochener Kulturfolger mit der Sesshaftwerdung des Menschen und dem Betreiben von Ackerbau vor 10.000 Jahren im mittleren Osten entwickelte. Summers-Smith kam hingegen 1988 zu dem Schluss, der Haussperling sei eine der vielen eurasischen Arten der Gattung Passer, die während des Pleistozän von einem Ur-Sperling abstammt, der Lebensräume im östlichen Mittelmeerraum und im tropischen Afrika besiedelte, die durch das Tal des Nils oder den afrikanischen Grabenbruch verbunden waren. Er unterstellte, dass das Männchen dieses Ur-Sperlings bereits einen schwarzen Brustlatz hatte, was charakteristisch für alle heutigen paläarktischen und orientalischen Sperlinge ist. Dieser Ur-Sperling breitete sich sowohl west- als auch ostwärts im eurasischen Steppengürtel aus. Anschließend kam es durch den wiederholten Vorstoß und Rückgang der Gletscher zu periodischen Isolationen, was zu Anpassungsentwicklungen und der Aufspaltung in die heutigen Arten führte.Viele Indizien sprechen für diese wesentlich frühere Artentstehung. Der früheste datierte der relativ seltenen Fossilienfunde des Haussperlings wird auf 400.000&nbsp;BP datiert und wurde in einer Höhle bei Bethlehem in Palästina entdeckt. Dieses Fossil weist mehr Ähnlichkeiten mit dem Haussperling als mit den heutigen afrikanischen Sperlingen auf. Dieser und ein späterer weiterer fossiler Höhlenfund in derselben Gegend lässt vermuten, dass dieser Vorfahre des Spatzen bereits in der Nähe der Menschen der Altsteinzeit lebte.Molekulargenetische Untersuchungen zur Datierung der Aufspaltung der Sperlingsarten widersprechen sich beträchtlich. Eine 1988 durchgeführte Untersuchung der Genabschnitte von 15 polymorphen Isozymen datiert die Abspaltung des Haussperlings vom Weidensperling zwischen 105.000 und 122.000&nbsp;BP. Das Ergebnis einer im Jahr 2001 durchgeführten Analyse der mitochondrialen Gen-Sequenz des Cytochrom-b und weiterer mitochondrialer Pseudogene ist dagegen, dass die Aufspaltung der Sperlingsart und auch die Abspaltung des Weidensperlings während des Miozän oder Pliozän auftrat, also vor mehr als 2&nbsp;Millionen Jahren.Ted. R. Anderson: Biology of the ubiquitous house sparrow. From genes to populations. Seite 9–12; siehe Literatur Verwandte ArtenDer Weidensperling galt lange Zeit als Unterart des Haussperlings, heute sieht man den Weidensperling als eigene Art. Beide Arten leben auf der iberischen Halbinsel, dem Balkan und Teilen Nordafrikas weitgehend sympatrisch, ohne dass es zu Hybridisierungen kommt, was als Beleg der Eigenständigkeit der Arten gilt. Das Gefieder der Männchen und die Lautäußerungen weichen deutlich voneinander ab, ökologisch und ernährungsbiologisch stimmen die Arten aber weitgehend überein. Im gemeinsamen Verbreitungsgebiet besetzt der Haussperling Städte und Ortschaften und „überlässt“ dem Weidensperling die ländlichen Lebensräume. Kommen beide Arten alleine vor, besetzen sie jedoch ein ähnliches ökologisches Spektrum. Als ethologische Isolationsmechanismen werden Unterschiede in Gefiedermerkmalen, Nestbau, Stimme und Zugverhalten angesehen. Vor allem in Ostalgerien und Tunesien scheinen stellenweise diese Isolationsbarrieren aber weitgehend zusammengebrochen zu sein, aufgrund der Hybridisierung kommt es dort zu bezüglich Aussehen und Merkmalen sehr variablen Sperlingspopulationen.Italiensperling Auch der Italiensperling wird häufig als Hybridform von Haus- und Weidensperling angesehen, was heute allerdings auch stark bezweifelt wird. Neben der Darstellung als eigenständige Art wird der Italiensperling ebenfalls als Unterart des Haus- und ebenso des Weidensperlings eingeordnet, zudem auch als Hybridform ohne Zuordnung zu den Ursprungsarten (passer x italiae). Auch wenn molekularbiologische Untersuchungen heute widersprüchlich sind, spricht vieles für die Einstufung des Italiensperlings als Unterart des Weidensperlings. Ein Indiz hierfür ist auch, dass im Gegensatz zu dem abrupten geografischen Ausschluss von Italien- und Haussperling im Alpengebiet Italien- und Weidensperling in Mittel- und Süditalien durch eine breite fließende Übergangszone miteinander verbunden sind (weiteres siehe Italiensperling).Till Töpfer: The taxonomic status of the Italian Sparrow – Passer italiae (Vieillot 1817): Speciation by stabilised hybridisation? A critical analysis. Zotaxa 1325, 117–145. (Zusammenfassung (PDF; 19&nbsp;kB)) UnterartenFür den Haussperling werden derzeit rund 13 verschiedene Unterarten anerkannt, die in zwei deutlich unterschiedlichen Subspeziesgruppen zusammengefasst werden. Die Formen der domesticus-Gruppe sind dabei größer, mit längeren Flügeln und kräftigerem Schnabel. Die helleren Gefiederanteile, wie beispielsweise die Kopfseiten, sind grau getönt und weniger weiß, die kastanienbraunen Pigmente sind weniger kräftig als bei der östlichen indicus-Gruppe.Die indicus-Gruppe findet sich im Wesentlichen im oberen Niltal des Sudans, großen Teilen von Arabien, Süd-Afghanistans, Irans, Indiens, Sri Lankas und Burmas, rund um das kaspische Meer, in Zentralasien und im Himalaya. Das übrige Verbreitungsgebiet wird von Vertretern der domesticus-Gruppe besiedelt, auch praktisch alle eingebürgerten Populationen gehören dieser Gruppe an, die meisten davon entstammen der Nominatform.Im Ostiran sind beide Subspeziesgruppen in einer breiten Übergangszone miteinander verbunden. Neben weiteren anderen Berührungspunkten stehen beide Subspeziesgruppen auch in Zentralasien nordwärts bis Turkmenistan und Kasachstan in Kontakt. Bei letzterer Kontaktzone kommt es offensichtlich nicht zur Hybridisierung, die Subspeziesgruppen verhalten sich hier wie zwei Arten. Der dortige Kontakt ist erst relativ kürzlich durch eine östliche Expansion des Verbreitungsgebiets der der domesticus-Population angehörenden Nominatform zustande gekommen. Die Individuen der Nominatform sind dort deutlich größer als die der Unterart bactrianus als dortiger Vertreter der indicus-Gruppe. Von manchen Forschern wird die Unterart bactrianus, die Zugvogel ist und in Indien und Pakistan überwintert, deshalb auch als eigenständige Art gesehen („Indian Sparrow“ oder Passer indicus). Dies wird allerdings nicht allgemein anerkannt.HBV Band 14/1, P. domesticus. Geographische Variation. Seite 49–51, siehe LiteraturTed. R. Anderson: Biology of the ubiquitous house sparrow. From genes to populations. Seite 18f, siehe LiteraturDie Merkmale und die Verbreitung der einzelnen Unterarten sind dabei wie folgt: Weibchen der Unterart P. d. domesticus in Vilnius, Litauen Weibchen der Unterart P. d. indicus in Kalkutta, Indien Männchen der Unterart P. d. indicus, ebenfalls in Kalkutta<br/><br/>domesticus-Gruppe P. d. domesticus: Die Nominatform besiedelt Europa sowie Sibirien ostwärts bis zur pazifischen Küste, zum Amurland und bis nach Nordost-China. Zudem gehören die meisten der Neozoen der Nominatform an. Die Flügellänge beträgt 77 bis 83 Millimeter. P. d. tingitanus: Diese Unterart kommt im Nordwesten Afrikas vor und besiedelt Marokko bis Tunesien sowie Libyen bis zur Cyrenaika. Die Unterschiede zur Nominatform sind gering, der mittlere Abschnitt der Scheitelfedern ist ausgedehnt schwarz, was aber auch bei Exemplaren der Nominatform als individuelle Variation beobachtet werden kann. Ohrdecken und die Unterseite sind durchschnittlich weißer. P. d. balearoibericus, P. d. biblicus und P. d. mayaudi: Auch diese Unterarten ähneln der Nominatform sehr, sie sind nur etwas blasser. Sie kommen in Kleinasien und Zypern sowie südwärts bis zur Sinai-Halbinsel vor. P. d. persicus: Diese im Iran vorkommende Unterart ist noch etwas blasser als die Populationen Kleinasiens. P. d. niloticus: Die das Niltal südwärts bis zur sudanesischen Grenze besiedelnde Unterart ist ähnlich blass wie persicus, nur etwas kleiner mit einer Flügellänge von 72 bis 77 Millimetern. indicus-Gruppe P. d. rufidorsalis: Bei dieser im oberen Niltal des Sudan vorkommenden Unterart ist das Kastanienbraun der Oberseite deutlich dunkler, kräftiger und ausgedehnter als bei der benachbarten Unterart niloticus. Das Grau des Scheitels ist nach hinten weniger ausgedehnt und reicht gewöhnlich nicht bis zum Nacken. Der Schnabel ist zudem kleiner und die Flügellänge etwas kürzer, sie liegt zwischen 69 und 75 Millimetern. Eine besonders variable Population verbindet bei Wadi Halfa rufidorsalis mit niloticus. Diese Mischpopulation wird gelegentlich auch als eigene Unterart P. d. halfae bezeichnet. P. d. indicus: Große Teile Arabiens, Süd-Afghanistans, Indiens, Sri Lankas und Burmas werden von dieser etwas blasseren und mit einer Flügellänge von 70 bis 78 Millimetern wiederum etwas größeren Unterart bewohnt. P. d. hufufae: Bei dieser Population ist die Gefiederfärbung relativ grau, das Weiß der Unterseite ausgeprägter und das Kastanienbraun auf Flügel und Rücken reduziert. Sie bewohnt Ostarabien von der Provinz al-Hasa bis Oman. P. d. hyrcanus: Diese Unterart lebt in den kaspischen Tiefländern im Süden Aserbaidschans und im Nordiran. Der Rücken ist etwas dunkler kastanienbraun als bei indicus, der Scheitel ist wegen der schwarzen mittleren Federabschnitte vielfach dunkel gestrichelt. P. d. bactrianus: Die sich ostwärts an indicus anschließenden Populationen sind etwas blasser, das Grau des Scheitels ist nicht dunkel gestrichelt. Das Vorkommen umfasst Transkaspien und Zentralasien. P. d. parkini: Diese im Grenzgebiet des Himalaya von Afghanistan bis Nepal beheimatete Unterart ist oberseitig dunkler und kräftiger gefärbt, das Nackenband ist breiter als bei bactrianus und indicus, der Schnabel und die Flügellänge sind größer (76 bis 83 Millimeter). Allerdings lassen sich parkini und bactrianus nicht zuverlässig voneinander unterscheiden.");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);

	// expeced[9]
	description = new Description();
	descriptions.add(description);
	description.setSourceTaxonKey(125796751);
	description.setType("verbreitung und lebensraum");
	description.setLanguage(Language.GERMAN);
	description.setDescription("Verbreitungsgebiet des Haussperlings, auf Einbürgerungen zurückgehende Vorkommen in hellerem Grün VerbreitungDas ursprüngliche paläarktische und orientalische Verbreitungsgebiet hat sich nach zahlreichen Einbürgerungen in anderen Kontinenten seit Mitte des 19. Jahrhunderts fast auf den gesamten Globus ausgedehnt. Heute fehlt der Haussperling nur in den Polargebieten, Teilen Nordsibiriens, Chinas und Südostasiens, in Japan, Westaustralien, dem tropischen Afrika und Südamerika und dem nördlichsten Teil Amerikas. Er ist damit eine der weitest verbreiteten Vogelarten. Die nördliche Grenze des Verbreitungsgebiets schwankt zwischen dem 60. und dem 70. Breitengrad. Auf der Südhalbkugel wurden die Landmassen mit Ausnahme der Antarktis bis zu den südlichsten Ausläufern besiedelt, nur in Westaustralien wird konsequent versucht, eine Besiedlung zu unterbinden.In Europa gibt es Gebiete, in denen der Haussperling durch einen nahen Verwandten vertreten wird: Auf dem italienischen Festland sowie auf den Inseln Sizilien, Korsika und Kreta hat sich der ebenfalls die Nähe des Menschen suchende Italiensperling etabliert. Auf der iberischen Halbinsel, dem Balkan und in Teilen Nordafrikas lebt der Haussperling gemeinsam mit dem nahe verwandten Weidensperling, der noch kein so ausgesprochener Kulturfolger ist.HBV Band 14/1, P. domesticus. Verbreitung der Art. Seite 46–48, siehe Literatur LebensraumAls ursprüngliches Biotop vor dem Anschluss an den Menschen werden trockenwarme, lockere Baumsavannen vermutet, dies bleibt jedoch mangels gesicherter Daten spekulativ. Beim Vordringen nach Mitteleuropa war der Haussperling bereits Kulturfolger mit einer ausgeprägten Bindung an den Menschen. Deutlich wurde dies beispielsweise während der Devastierung Helgolands nach dem Zweiten Weltkrieg, während der mit den Menschen auch die Haussperlinge verschwanden und erst nach der Neubesiedlung ab 1952 wieder zurückkehrten.HBV Band 14/1, P. d. domesticus. Verbreitung in Mitteleuropa. Seite 67–70, siehe Literatur In milden Zonen werden allerdings auch menschenferne Habitate genutzt.Voraussetzungen für Brutvorkommen sind die ganzjährige Verfügbarkeit von Sämereien und Getreideprodukten und geeignete Nistplätze. Optimal sind Dörfer mit Landwirtschaft, Vorstadtbezirke, Stadtzentren mit großen Parkanlagen, zoologische Gärten, Vieh- oder Geflügelfarmen. Es werden aber auch außergewöhnliche Lebensräume besiedelt, wie beispielsweise von der Außenwelt abgeschlossene klimatisierte Flughafengebäude. Das höchstgelegene Brutvorkommen findet sich bei ungefähr 4.500&nbsp;m im Himalaya, das tiefste bei -86&nbsp;m im Death-Valley in Nordamerika.HBV Band 14/1, P. d. domesticus. Biotop. Seite 75–78, siehe Literatur WanderungenIn Europa ist der Haussperling fast ausschließlich Standvogel, in geringem Ausmaß auch Kurzstreckenzieher. Nicht dauernd von Menschen bewohnte Siedlungen im Alpenraum werden im Spätherbst oder Winter auch vom Haussperling geräumt. Die asiatische Unterart P. d. bactrianus wiederum ist ein Zugvogel und überwintert bei Zugdistanzen bis zu 2000 Kilometern in Pakistan und Indien. Die hauptsächlich im Himalaya beheimatete Form P. d. parkini ist Teilzieher.HBV Band 14/1, P. d. domesticus. Wanderungen. Seite 73–75, siehe LiteraturJochen Hölzinger: Die Vögel Baden-Württembergs. Band 3/2, Seite 504–516, siehe LiteraturNach der ersten Brutansiedlung sind die Haussperlinge der Nominatform sehr ortstreu, der Aktionsradius während der Brutzeit kann bei Stadtpopulationen lediglich 50 Meter betragen. Jungvögel streuen ungerichtet und schließen sich zunächst im Spätsommer anwachsenden Schwärmen an. Auch ein Teil der Altvögel schließt sich diesen Herbstschwärmen an, die in die Umgebung der Brutplätze ausstrahlen, um das dortige Nahrungsangebot zu nutzen. Die Altvögel kehren nach Auflösung der Schwärme meist bereits im Frühherbst wieder an ihren ursprünglichen Brutplatz zurück.");
	description.setLicense("CC-BY-SA 3.0");

	// expeced[10]
	description = new Description();
	descriptions.add(description);
	description.setSourceTaxonKey(125796751);
	description.setType("bestand und bestandsentwicklung");
	description.setLanguage(Language.GERMAN);
	description.setDescription("Weltweit wird der Bestand des Haussperlings auf 500&nbsp;Millionen Individuen geschätzt. Auch in Europa liegen nur recht ungenaue Schätzungen vor. In Deutschland ist der Haussperling trotz Bestandsrückgängen nach dem Buchfink der zweithäufigste Brutvogel.C. Sudfeldt, R. Dröschmeister, C. Grüneberg, S. Jaehne, A. Mitschke & J. Wahl: Vögel in Deutschland – 2008. DDA, Münster 2008 (online; PDF; 8,4&nbsp;MB) Laut Birdlife stellt sich der Bestand im deutschsprachigen Raum folgendermaßen dar:<br/><br/>Land  Anzahl Brutpaare  Zeitraum  Trend (%)  Deutschland  4.000.000 – 10.000.000  1995 – 1999  -20 bis -30  Liechtenstein  1.000 – 2.500  1998 – 2000  0 bis -20  Luxemburg  35.000 – 40.000  2002 – 2002  -20 bis -30  Österreich  350.000 – 700.000  1998 – 2002  0 bis -20  Schweiz  400.000 – 500.000  1998 – 2002  0 bis -20 Die Prozentangabe des Trends bezieht sich dabei auf einen Zeitraum von zehn Jahren. Veränderungen kleiner als 20&nbsp;Prozent werden dabei noch nicht als statistisch signifikant angesehen, da diese im Bereich natürlicher Schwankungen liegen.Aktuell wird der Haussperling mit 5,6 bis 11 Millionen Brutpaaren im Jahr 2008 als zweithäufigste Brutvogelart Deutschlands angesehen.Im Westen Mitteleuropas ist der Bestand in der zweiten Hälfte des 20.&nbsp;Jahrhunderts deutlich zurückgegangen. Dieser Rückgang ist für Beobachter offensichtlich, die sich beispielsweise an die großen Schwärme auf Getreidefeldern in den 1950er Jahren erinnern können. Allerdings ist der Rückgang des Bestands wegen des damaligen geringen Interesses an dieser Art und der fehlenden Daten aus dieser Zeit nur sehr lückenhaft dokumentiert. Wegen des Bestandsrückgangs wurde der Haussperling auch auf die Vorwarnliste der gefährdeten Arten aufgenommen, obwohl der Bestand absolut gesehen noch sehr hoch ist. Ebenfalls aufgrund dieser Entwicklung war der Spatz in Deutschland und in Österreich zum Vogel des Jahres 2002 gewählt worden.Die Gründe für diesen Rückgang sind vielschichtig, folgende Ursachen werden angegeben:<br/><br/>Moderne oder sanierte Gebäude bieten kaum noch Nischen oder Hohlräume, die als Brutplätze verwendet werden können. Durch den Einsatz effizienterer Erntemaschinen verbleibt weniger verwertbare Nahrung nach der Ernte auf den Feldern. Weitgehende Einstellung der offenen Nutztierhaltung Der vermehrte Einsatz von Pestiziden in der Landwirtschaft verringert das Angebot und die Qualität der animalischen Nahrung, die vor allem für die Nestlinge wichtig ist. Die gleiche negative Konsequenz hat ebenfalls der im Bereich von Städten und Vorstädten gestiegene Anteil der versiegelten Flächen und auch, dass dort vielerorts die natürliche Vegetation durch gebietsfremde Pflanzen (beispielsweise Ziersträucher) ersetzt wurde.<br/><br/>Die Situation hängt jedoch sehr von den lokalen Bedingungen ab. In verschiedenen europäischen Großstädten wie London, Paris, Warschau, Hamburg und München wurde in den letzten Jahren ein sehr starker Rückgang beobachtet. Eine besonders erschreckende Entwicklung wurde im Hamburger Stadtteil St. Georg festgestellt, wo zwischen 1983 und 1987 die Zahl der Haussperlinge von 490 auf 80 Vögel pro Quadratkilometer zurückging. Für München setzt sich dieser Trend besonders im Zentrum fort.Landesbund für Vogelschutz (LBV): Die Ergebnisse der Stunde der Wintervögel. Abgerufen am 20. Juni 2013. Positiver ist die Entwicklung in Berlin, wo sich die Rückgänge erst lokal in den Sanierungsgebieten abzeichnen und der Bestand mit 280 Vögeln pro Quadratkilometer im internationalen Vergleich einen Spitzenplatz einnimmt.NABU Berlin: Berlin – Eldorado für Spatzen HBV Band 14/1, P. d. domesticus. Bestand, Bestandsentwicklung. Seite 70–72, siehe Literatur Vincent 2005, Seite 256–259 und 265–270, siehe Literatur");
	description.setLicense("CC-BY-SA 3.0");


	// expeced[11]
	description = new Description();
	// description.setKey(129875);
	description.setSourceTaxonKey(111381448);
	// description.setDatasetKey(UUID.fromString("47f16512-bf31-410f-b272-d151c996b2f6"));
	description.setType("distribution");
	description.setLanguage(Language.UNKNOWN);
	descriptions.add(description);

	// expeced[12]
	description = new Description();
	// description.setKey(233362);
	description.setSourceTaxonKey(124750679);
	// description.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	description.setType("population");
	description.setLanguage(Language.ENGLISH);
	description.setDescription("The global population is estimated to number  &gt; c.540,000,000 individuals (Rich <em>et al</em>. 2004), while national population sizes have been estimated at c.100-100,000 breeding pairs in China and c.100-100,000 breeding pairs in Russia (Brazil 2009).");
	descriptions.add(description);

	// expeced[13]
	description = new Description();
	// description.setKey(91932);
	description.setSourceTaxonKey(116668229);
	// description.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	description.setType("taxonomy and systematics");
	description.setLanguage(Language.ENGLISH);
	description.setDescription("EtymologyThe House Sparrow was among the first animals to be given a scientific name in the modern system of biological classification, since it was described by Carl Linnaeus, in the 1758 10th edition of Systema Naturae. It was described from a type specimen collected in Sweden, with the name Fringilla domestica. Later the genus name Fringilla came to be used only for the Common Chaffinch and its relatives, and the House Sparrow has usually been placed in the genus Passer created by French zoologist Mathurin Jacques Brisson in 1760.The bird's scientific name and its usual English name have the same meaning. The Latin word passer, like the English word \"sparrow\", is a term for small active birds, coming from a root word referring to speed. The Latin word domesticus means \"belonging to the house\", like the common name a reference to its association with humans. The House Sparrow is also called by a number of alternative English names, including English Sparrow, chiefly in North America; and Indian Sparrow or Indian House Sparrow, for the birds of the Indian subcontinent and Central Asia. Dialectal names include sparr, sparrer, spadger, spadgick, and philip, mainly in southern England; spug and spuggy, mainly in northern England; spur and sprig, mainly in Scotland; and spatzie or spotsie, from the German Spatz, in North America. Taxonomy The genus Passer contains about 25 species, depending on the authority, 26 according to the Handbook of the Birds of the World.Summers-Smith, J. Denis (2009): Handbook of the Birds of the World. Volume 14: Bush-shrikes to Old World Sparrows. Lynx Edicions Most Passer species are dull-coloured birds with short square tails and stubby conical beaks, between  long. Mitochondrial DNA suggest that speciation in the genus occurred during the Pleistocene and earlier, while other evidence suggests speciation occurred 25,000 to 15,000 years ago.Arnaiz-Villena, Antonio; Gómez-Prieto, Pablo; Ruiz-de-Valle, Valentin (2009): Phylogeography of finches and sparrows, Nova Science PublishersAllende, Luis M.; Rubio, Isabel; Ruíz-del-Valle, Valentin; Guillén, Jesus; Martínez-Laso, Jorge; Lowy, Ernesto; Varela, Pilar; Zamora, Jorge; Arnaiz-Villena, Antonio (2001): The Old World sparrows (genus Passer) phylogeography and their relative abundance of nuclear mtDNA pseudogenes, Journal of Molecular Evolution, 53(2): pp. 144–154 Within Passer, the House Sparrow is part of the \"Palaearctic black-bibbed sparrows\" group and a close relative of the Mediterranean \"willow sparrows\".The taxonomy of the House Sparrow and its Mediterranean relatives is highly complicated. The common type of \"willow sparrow\" is the Spanish Sparrow, which resembles the House Sparrow in many respects. It frequently prefers wetter habitats than the House Sparrow, and it is often colonial and nomadic. In most of the Mediterranean, one or both species occur, with some degree of hybridisation. In North Africa, the two species hybridise extensively, forming highly variable mixed populations with a full range of characters from pure House Sparrows to pure Spanish Sparrows.Töpfer, Till (2006): The taxonomic status of the Italian Sparrow – Passer italiae (Vieillot 1817): Speciation by stabilised hybridisation? A critical analysis, Zootaxa, 1325: pp. 117–145Metzmacher, M. (1986): Moineaux domestiques et Moineaux espagnols, Passer domesticus et P. hispaniolensis, dans une région de l'ouest algérien : analyse comparative de leur morphologie externe, Le Gerfaut, 76: pp. 317–334In much of Italy there is a form apparently intermediate between the House and Spanish Sparrows, known as the Italian Sparrow. It resembles a hybrid between the two species, and is in other respects intermediate. Its specific status and origin are the subject of much debate. In the Alps, the Italian Sparrow intergrades over a roughly  strip with the House Sparrow, but to the south it intergrades over the southern half of Italy and some Mediterranean islands with the Spanish Sparrow. On the Mediterranean islands of Malta, Gozo, Crete, Rhodes, and Karpathos, there are other apparently intermediate birds of unknown status. Subspecies<br/><br/>A large number of subspecies have been named, of which twelve were recognised in the Handbook of the Birds of the World. These subspecies are divided into two groups, the Palaearctic domesticus group, and the Oriental indicus group. Several Middle Eastern subspecies, including Passer domesticus biblicus, are sometimes considered a third, intermediate group. The subspecies P. d. indicus was described as a species, and was considered to be distinct by many ornithologists during the nineteenth century.Vaurie, Charles; Koelz, Walter: Notes on some Ploceidae from western Asia, American Museum Novitates(1406)Migratory birds of the subspecies P. d. bactrianus in the indicus group were recorded overlapping with P. d. domesticus birds without hybridising in the 1970s, so the Soviet scientists Edward I. Gavrilov and M. N. Korelov proposed the separation of the indicus group as a separate species.Gavrilov, E. I. (1965): On hybridisation of Indian and House Sparrows, Bulletin of the British Ornithologists' Club, 85: pp. 112–114 However, indicus-group and domesticus-group birds intergrade in a large part of Iran, so this split is rarely recognised.In North America, House Sparrow populations are more differentiated than those in Europe.Johnston, Richard F.; Selander, Robert K (May–June 1973): Evolution in the House Sparrow. III. Variation in Size and Sexual Dimorphism in Europe and North and South America, The American Naturalist, 107(955): pp. 373–390 This variation follows predictable patterns, with birds at higher latitudes being larger and those in arid areas being paler.Johnston, Richard F.; Selander, Robert K. (March 1971): Evolution in the House Sparrow. II. Adaptive Differentiation in North American Populations, Evolution, 25(1): pp. 1–28. Society for the Study of Evolution However, it is not clear how much this is caused by evolution or by environment.Packard, Gary C. (March 1967): House Sparrows: Evolution of Populations from the Great Plains and Colorado Rockies, Systematic Zoology, 16(1): pp. 73–89. Society of Systematic BiologistsJohnston, R. F.; Selander, R. K. (1 May 1964): House Sparrows: Rapid Evolution of Races in North America, Science, 144(3618): pp. 548–550Selander, Robert K.; Johnston, Richard F. (1967): Evolution in the House Sparrow. I. Intrapopulation Variation in North America, The Condor, 69(3): pp. 217–258. Cooper Ornithological SocietyHamilton, Suzanne; Johnston, Richard F. (April 1978): Evolution in the House Sparrow—VI. Variability and Niche Width, The Auk, 95(2): pp. 313–323 Similar observations have been made in New Zealand,Baker, Allan J. (July 1980): Morphometric Differentiation in New Zealand Populations of the House Sparrow (Passer domesticus), Evolution, 34(4): pp. 638–653. Society for the Study of Evolution and in South Africa. The introduced House Sparrow populations may be distinct enough to merit subspecies status, especially in North America and southern Africa, and American ornithologist Harry Church Oberholser even gave the subspecies name plecticus to the paler birds of western North America.<br/><br/>domesticus group P. d. domesticus, the nominate subspecies, is found in most of Europe, across northern Asia to Sakhalin and Kamchatka. It is the most widely introduced subspecies. P. d. balearoibericus von Jordans, 1923, described from Majorca, is found in the Balearic Islands, southern France, the Balkans, and Anatolia. P. d. tingitanus (Loche, 1867), described from Algeria, is found in the Maghreb from Ajdabiya in Libya to Béni Abbès in Algeria, and to Morocco&#39;s Atlantic coast. It hybridises extensively with the Spanish Sparrow, especially in the eastern part of its range. P. d. niloticus Nicoll and Bonhote, 1909, described from Faiyum, Egypt, is found along the Nile north of Wadi Halfa, Sudan. It intergrades with bibilicus in the Sinai, and with rufidorsalis in a narrow zone around Wadi Halfa. It has been recorded in Somaliland. P. d. persicus Zarudny and Kudashev, 1916, described from the Karun River in Khuzestan, Iran, is found in the western and central Iran south of the Alborz mountains, intergrading with indicus in eastern Iran, and Afghanistan. P. d. biblicus Hartert, 1910, described from Palestine, is found in the Middle East from Cyprus and southeastern Turkey to the Sinai in the west and from Azerbaijan to Kuwait in the east. indicus group P. d. hyrcanus Zarudny and Kudashev, 1916, described from Gorgan, Iran, is found along the southern coast of the Caspian Sea from Gorgan to southeastern Azerbaijan. It intergrades with persicus in the Alborz mountains, and with bibilicus to the west. It is the subspecies with the smallest range. P. d. bactrianus Zarudny and Kudashev, 1916, described from Tashkent, is found in southern Kazakhstan to the Tian Shan and northern Iran and Afghanistan. It intergrades with persicus in Baluchistan and with indicus across central Afghanistan. Unlike most other House Sparrow subspecies, it is almost entirely migratory, wintering in the plains of the northern Indian subcontinent. It is found in open country rather than in settlements, which are occupied by the Eurasian Tree Sparrow in its range. There is an exceptional record from Sudan. P. d. parkini Whistler, 1920, described from Srinagar, Kashmir, is found in the western Himalayas from the Pamir Mountains to southeastern Nepal. It is migratory, like bactrianus. P. d. indicus Jardine and Selby, 1831, described from Bangalore, is found in the Indian subcontinent south of the Himalayas, in Sri Lanka, western Southeast Asia, eastern Iran, and southwestern Arabia as far as southern Israel. P. d. hufufae Ticehurst and Cheeseman, 1924, described from Hofuf in Saudi Arabia, is found in northeastern Arabia.Vaurie, Charles (1956): Systematic notes on Palearctic birds. No. 24, Ploceidae, the genera Passer, Petronia, and Montifringilla, American Museum Novitates(1814) P. d. rufidorsalis C. L. Brehm, 1855, described from Khartoum, Sudan, is found in the Nile valley from Wadi Halfa south to Renk in northern South Sudan, and in eastern Sudan, northern Ethiopia to the Red Sea coast in Eritrea. It has also been introduced to Mohéli in the Comoros.");
	/*
"EtymologyThe House Sparrow was among the first animals to be given a scientific name in the modern system of biological classification, since it was described by Carl Linnaeus, in the 1758 10th edition of Systema Naturae. It was described from a type specimen collected in Sweden, with the name Fringilla domestica. Later the genus name Fringilla came to be used only for the Common Chaffinch and its relatives, and the House Sparrow has usually been placed in the genus Passer created by French zoologist Mathurin Jacques Brisson in 1760.The bird's scientific name and its usual English name have the same meaning. The Latin word passer, like the English word \"sparrow\", is a term for small active birds, coming from a root word referring to speed. The Latin word domesticus means \"belonging to the house\", like the common name a reference to its association with humans. The House Sparrow is also called by a number of alternative English names, including English Sparrow, chiefly in North America; and Indian Sparrow or Indian House Sparrow, for the birds of the Indian subcontinent and Central Asia. Dialectal names include sparr, sparrer, spadger, spadgick, and philip, mainly in southern England; spug and spuggy, mainly in northern England; spur and sprig, mainly in Scotland; and spatzie or spotsie, from the German Spatz, in North America. TaxonomyThe genus Passer contains about 25 species, depending on the authority, 26 according to the Handbook of the Birds of the World.Summers-Smith, J. Denis (2009): Handbook of the Birds of the World. Volume 14: Bush-shrikes to Old World Sparrows. Lynx Edicions Most Passer species are dull-coloured birds with short square tails and stubby conical beaks, between  long. Mitochondrial DNA suggest that speciation in the genus occurred during the Pleistocene and earlier, while other evidence suggests speciation occurred 25,000 to 15,000 years ago.Arnaiz-Villena, A (2009): Phylogeography of finches and sparrows, Nova Science PublishersAllende, Luis M. (2001): The Old World sparrows (genus Passer) phylogeography and their relative abundance of nuclear mtDNA pseudogenes, Journal of Molecular Evolution, 53(2): pp. 144–154 Within Passer, the House Sparrow is part of the \"Palaearctic black-bibbed sparrows\" group and a close relative of the Mediterranean \"willow sparrows\".The taxonomy of the House Sparrow and its Mediterranean relatives is highly complicated. The common type of \"willow sparrow\" is the Spanish Sparrow, which resembles the House Sparrow in many respects. It frequently prefers wetter habitats than the House Sparrow, and it is often colonial and nomadic. In most of the Mediterranean, one or both species occur, with some degree of hybridisation. In North Africa, the two species hybridise extensively, forming highly variable mixed populations with a full range of characters from pure House Sparrows to pure Spanish Sparrows.Töpfer, Till (2006): The taxonomic status of the Italian Sparrow – Passer italiae (Vieillot 1817): Speciation by stabilised hybridisation? A critical analysis, Zootaxa, 1325: pp. 117–145Metzmacher, M. (1986): Moineaux domestiques et Moineaux espagnols, Passer domesticus et P. hispaniolensis, dans une région de l'ouest algérien : analyse comparative de leur morphologie externe, Le Gerfaut, 76: pp. 317–334In much of Italy there is a form apparently intermediate between the House and Spanish Sparrows, known as the Italian Sparrow. It resembles a hybrid between the two species, and is in other respects intermediate. Its specific status and origin are the subject of much debate. In the Alps, the Italian Sparrow intergrades over a roughly  strip with the House Sparrow, but to the south it intergrades over the southern half of Italy and some Mediterranean islands with the Spanish Sparrow. On the Mediterranean islands of Malta, Gozo, Crete, Rhodes, and Karpathos, there are other apparently intermediate birds of unknown status. Subspecies<br/><br/>A large number of subspecies have been named, of which twelve were recognised in the Handbook of the Birds of the World. These subspecies are divided into two groups, the Palaearctic domesticus group, and the Oriental indicus group. Several Middle Eastern subspecies, including Passer domesticus biblicus, are sometimes considered a third, intermediate group. The subspecies P. d. indicus was described as a species, and was considered to be distinct by many ornithologists during the nineteenth century.Vaurie, Charles: Notes on some Ploceidae from western Asia, American Museum Novitates(1406)Migratory birds of the subspecies P. d. bactrianus in the indicus group were recorded overlapping with P. d. domesticus birds without hybridising in the 1970s, so the Soviet scientists Edward I. Gavrilov and M. N. Korelov proposed the separation of the indicus group as a separate species.Gavrilov, E. I. (1965): On hybridisation of Indian and House Sparrows, Bulletin of the British Ornithologists' Club, 85: pp. 112–114 However, indicus-group and domesticus-group birds intergrade in a large part of Iran, so this split is rarely recognised.In North America, House Sparrow populations are more differentiated than those in Europe.Johnston, Richard F.; Selander, Robert K (May–June 1973): Evolution in the House Sparrow. III. Variation in Size and Sexual Dimorphism in Europe and North and South America, The American Naturalist, 107(955): pp. 373–390 This variation follows predictable patterns, with birds at higher latitudes being larger and those in arid areas being paler.Johnston, Richard F.; Selander, Robert K. (March 1971): Evolution in the House Sparrow. II. Adaptive Differentiation in North American Populations, Evolution, 25(1): pp. 1–28. Society for the Study of Evolution However, it is not clear how much this is caused by evolution or by environment.Packard, Gary C. (March 1967): House Sparrows: Evolution of Populations from the Great Plains and Colorado Rockies, Systematic Zoology, 16(1): pp. 73–89. Society of Systematic BiologistsJohnston, R. F. (1 May 1964): House Sparrows: Rapid Evolution of Races in North America, Science, 144(3618): pp. 548–550Selander, Robert K.; Johnston, Richard F. (1967): Evolution in the House Sparrow. I. Intrapopulation Variation in North America, The Condor, 69(3): pp. 217–258. Cooper Ornithological SocietyHamilton, Suzanne; Johnston, Richard F. (April 1978): Evolution in the House Sparrow—VI. Variability and Niche Width, The Auk, 95(2): pp. 313–323 Similar observations have been made in New Zealand,Baker, Allan J. (July 1980): Morphometric Differentiation in New Zealand Populations of the House Sparrow (Passer domesticus), Evolution, 34(4): pp. 638–653. Society for the Study of Evolution and in South Africa. The introduced House Sparrow populations may be distinct enough to merit subspecies status, especially in North America and southern Africa, and American ornithologist Harry Church Oberholser even gave the subspecies name plecticus to the paler birds of western North America.<br/><br/>domesticus group P. d. domesticus, the nominate subspecies, is found in most of Europe, across northern Asia to Sakhalin and Kamchatka. It is the most widely introduced subspecies. P. d. balearoibericus von Jordans, 1923, described from Majorca, is found in the Balearic Islands, southern France, the Balkans, and Anatolia. P. d. tingitanus (Loche, 1867), described from Algeria, is found in the Maghreb from Ajdabiya in Libya to Béni Abbès in Algeria, and to Morocco&#39;s Atlantic coast. It hybridises extensively with the Spanish Sparrow, especially in the eastern part of its range. P. d. niloticus Nicoll and Bonhote, 1909, described from Faiyum, Egypt, is found along the Nile north of Wadi Halfa, Sudan. It intergrades with bibilicus in the Sinai, and with rufidorsalis in a narrow zone around Wadi Halfa. It has been recorded in Somaliland. P. d. persicus Zarudny and Kudashev, 1916, described from the Karun River in Khuzestan, Iran, is found in the western and central Iran south of the Alborz mountains, intergrading with indicus in eastern Iran, and Afghanistan. P. d. biblicus Hartert, 1910, described from Palestine, is found in the Middle East from Cyprus and southeastern Turkey to the Sinai in the west and from Azerbaijan to Kuwait in the east. indicus group P. d. hyrcanus Zarudny and Kudashev, 1916, described from Gorgan, Iran, is found along the southern coast of the Caspian Sea from Gorgan to southeastern Azerbaijan. It intergrades with persicus in the Alborz mountains, and with bibilicus to the west. It is the subspecies with the smallest range. P. d. bactrianus Zarudny and Kudashev, 1916, described from Tashkent, is found in southern Kazakhstan to the Tian Shan and northern Iran and Afghanistan. It intergrades with persicus in Baluchistan and with indicus across central Afghanistan. Unlike most other House Sparrow subspecies, it is almost entirely migratory, wintering in the plains of the northern Indian subcontinent. It is found in open country rather than in settlements, which are occupied by the Eurasian Tree Sparrow in its range. There is an exceptional record from Sudan. P. d. parkini Whistler, 1920, described from Srinagar, Kashmir, is found in the western Himalayas from the Pamir Mountains to southeastern Nepal. It is migratory, like bactrianus. P. d. indicus Jardine and Selby, 1831, described from Bangalore, is found in the Indian subcontinent south of the Himalayas, in Sri Lanka, western Southeast Asia, eastern Iran, and southwestern Arabia as far as southern Israel. P. d. hufufae Ticehurst and Cheeseman, 1924, described from Hofuf in Saudi Arabia, is found in northeastern Arabia.Vaurie, Charles (1956): Systematic notes on Palearctic birds. No. 24, Ploceidae, the genera Passer, Petronia, and Montifringilla, American Museum Novitates(1814) P. d. rufidorsalis C. L. Brehm, 1855, described from Khartoum, Sudan, is found in the Nile valley from Wadi Halfa south to Renk in northern South Sudan, and in eastern Sudan, northern Ethiopia to the Red Sea coast in Eritrea. It has also been introduced to Mohéli in the Comoros.");
	*/
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);

	// expeced[14]
	description = new Description();
	// description.setKey(91929);
	description.setSourceTaxonKey(116668229);
	// description.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	description.setType("physiology");
	description.setLanguage(Language.ENGLISH);
	description.setDescription("House Sparrows express strong circadian rhythms of activity in the laboratory. They were among the first bird species to be seriously studied in terms of their circadian activity and photoperiodism, in part because of their availability and adaptability in captivity, but also because they can \"find their way\" and remain rhythmic in constant darkness. Menaker, M. (1972): Nonvisual light reception, Scientific American, 226(3): pp. 22–29Binkley, S. (1990): The clockwork sparrow: Time, clocks, and calendars in biological organisms. Prentice Hall Such studies have found that the pineal gland is a central part of the House Sparrow's circadian system: removal of the pineal eliminates the circadian rhythm of activity,Gaston, S.; Menaker, M. (1968): Pineal function: The biological clock in the sparrow, Science, 160(3832): pp. 1125–1127 and transplant of the pineal into another individual confers to this individual the rhythm phase of the donor bird.Zimmerman, W.; Menaker, M. (1979): The pineal gland: A pacemaker within the circadian system of the house sparrow, Proceedings of the National Academy of Sciences, 76: pp. 999–1003 The suprachiasmatic nuclei of the hypothalamus have also been shown to be an important component of the circadian system of house sparrows.Takahashi, J. S.; Menaker, M. (1982): Role of the suprachiasmatic nuclei in the circadian system of the house sparrow, Passer domesticus, Journal of Neuroscience, 2(6): pp. 815–828 The photoreceptors involved in the synchronization of the circadian clock to the external light-dark cycle are located in the brain and can be stimulated by light reaching them directly though the skull, as revealed by experiments in which blind sparrows, which normally can still synchronize to the light-dark cycle, failed to do so once India ink was injected as a screen under the skin on top of their skull.McMillan, J. P.; Keatts, H. C.; Menaker, M. (1975): On the role of eyes and brain photoreceptors in the sparrow: Entrainment to light cycles, Journal of Comparative Physiology, 102(3): pp. 251–256Similarly, even when blind, House Sparrows continue to be photoperiodic, i.e. show reproductive development when the days are long but not when the days are short. This response is stronger when the feathers on top of the head are plucked, and is eliminated when India ink is injected under the skin at the top of the head, showing that the photoreceptors involved in the photoperiodic response to daylength are located inside the brain.Menaker, M.; Roberts, R.; Elliott, J.; Underwood, H. (1970): Extraretinal light perception in the sparrow, III. The eyes do not participate in photoperiodic photoreception, Proceedings of the National Academy of Sciences, 67: pp. 320––325 House Sparrows have also been used in studies of nonphotic entrainment (i.e. synchronization to an external cycle other than light and dark): for example, in constant darkness, a situation in which the birds would normally reveal their endogenous, non-24-hour, \"free-running\" rhythms of activity, they instead show 24-hour periodicity if they are exposed to two hours of chirp playbacks every 24&nbsp;hours, matching their daily activity onsets with the daily playback onsets.Reebs, S.G. (1989): Acoustical entrainment of circadian activity rhythms in house sparrows: Constant light is not necessary, Ethology, 80: pp. 172–181 House Sparrows in constant dim light can also be entrained to a daily cycle of presence/absence of food.Hau, M.; Gwinner, E. (1992): Circadian entrainment by feeding cycles in house sparrows, Passer domesticus, Journal of Comparative Physiology A, 170(4): pp. 403–409 Finally, House Sparrows in constant darkness can be entrained to a cycle of high and low temperature, but only if the difference between the two temperatures is large (38 versus 6 degrees Celsius); some of the tested sparrows matched their activity to the warm phase, and others to the cold phase.Eskin, A. (1971): Biochronometry: pp. 55–80. National Academy of Sciences");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);

	// expeced[15]
	description = new Description();
	// description.setKey(91931);
	description.setSourceTaxonKey(116668229);
	// description.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	description.setType("survival");
	description.setLanguage(Language.ENGLISH);
	description.setDescription("In adult House Sparrows, annual survival is 45–65%. After fledging and leaving the care of their parents, young sparrows have a high mortality rate, which lessens as they grow older and more experienced. Only about 20–25% of birds hatched survive to their first breeding season. The oldest known wild House Sparrow lived for nearly two decades; it was found dead 19&nbsp;years and 9&nbsp;months after it was ringed in Denmark.European Longevity Records. EURING: The European Union for Bird Ringing The oldest recorded captive House Sparrow lived for 23&nbsp;years.AnAge entry for Passer domesticus, AnAge: the Animal Ageing and Longevity Database The typical ratio of males to females in a population is uncertain due to problems in collecting data, but a very slight preponderance of males at all ages is usual. Predation The House Sparrow's main predators are cats and birds of prey, but many other animals prey on them, including corvids, squirrels, and even humans, as the House Sparrow has been consumed by humans in many parts of the world, and still is in parts of the Mediterranean. Most species of bird of prey have been recorded preying on the House Sparrow in places where there are extensive records. Accipiters and the Merlin in particular are major predators, though cats are likely to have a greater impact on House Sparrow populations. The House Sparrow is also a common victim of roadkill; on European roads, it is the bird most frequently found dead.Erritzoe, J.; Mazgajski, T. D.; Rejt, L. (2003): Bird casualties on European roads – a review, Acta Ornithologica, 38(2): pp. 77–93 Parasites and diseaseThe House Sparrow is host to a huge number of parasites and diseases, and the effect of most is unknown. Ornithologist Ted R. Anderson listed thousands, noting that his list was incomplete. The commonly recorded bacterial pathogens of the House Sparrow are often those common in humans, and include Salmonella and Escherichia coli. Salmonella is common in the House Sparrow, and a comprehensive study of House Sparrow disease found it in 13% of sparrows tested. Salmonella epidemics in the spring and winter can kill large numbers of sparrows. The House Sparrow hosts avian pox and avian malaria, which it has spread to the native forest birds of Hawaii.van Riper, Charles III; van Riper, Sandra G.; Hansen, Wallace R. (2002): Epizootiology and Effect of Avian Pox on Hawaiian Forest Birds, The Auk, 119(4): pp. 929–942 Many of the diseases hosted by the House Sparrow are also present in humans and domestic animals, for which the House Sparrow acts as a reservoir host. Arboviruses such as the West Nile virus, which most commonly infect insects and mammals, survive winters in temperate areas by going dormant in birds such as the House Sparrow.Young, Emma (1 November 2000): Sparrow suspect, New Scientist There are a few records of disease extirpating House Sparrow populations, especially from Scottish islands, but this seems to be rare.The House Sparrow is infested by a number of external parasites, which usually cause little harm to adult sparrows. In Europe, the most common mite found on sparrows is Proctophyllodes, the most common ticks are Argas reflexus and Ixodes arboricola, and the most common flea on the House Sparrow is Ceratophyllus gallinae. A number of chewing lice occupy different niches on the House Sparrow's body. Menacanthus lice occur across the House Sparrow's body, where they feed on blood and feathers, while Brueelia lice feed on feathers and Philopterus fringillae occurs on the head.");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);


	// expeced[16]
	description = new Description();
	// description.setKey(91927);
	description.setSourceTaxonKey(116668229);
	// description.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	description.setType("description");
	description.setLanguage(Language.ENGLISH);
	description.setDescription("Measurements and shapeThe House Sparrow is typically about  long, ranging from . It is a compact bird with a full chest and a large rounded head. Its bill is stout and conical with a culmen length of , strongly built as an adaptation for eating seeds. Its tail is short, at  long. The wing chord is , and the tarsus is .House Sparrow, All About Birds. Cornell Lab of Ornithology In weight, the House Sparrow ranges from . Females usually are slightly smaller than males. The median weight on the European continent for both sexes is about , and in more southerly subspecies is around . Younger birds are smaller, males are larger during the winter, and females are larger during the breeding season. Birds at higher latitudes, colder climates, and sometimes higher altitudes are smaller (under Bergmann's rule), both between and within subspecies.Felemban, Hassan M. (1997): Morphological differences among populations of House Sparrows from different altitudes in Saudi Arabia, The Wilson Bulletin, 109(3): pp. 539–544 Plumage The plumage of the House Sparrow is mostly different shades of grey and brown. The sexes exhibit strong dimorphism: the female is mostly buffish above and below, while the male has boldly coloured head markings, a reddish back, and grey underparts. The male has a dark grey crown from the top of its bill to its back, and chestnut brown flanking its crown on the sides of its head. It has black around its bill, on its throat, and on the spaces between its bill and eyes (lores). It has a small white stripe between the lores and crown and small white spots immediately behind the eyes (postoculars), with black patches below and above them. The underparts are pale grey or white, as are the cheeks, ear coverts, and stripes at the base of the head. The upper back and mantle are a warm brown, with broad black streaks, while the lower back, rump and uppertail coverts are greyish-brown.The male is duller in fresh non-breeding plumage, with whitish tips on many feathers. Wear and preening expose many of the bright brown and black markings, including most of the black throat and chest patch, called the \"bib\" or \"badge\". The badge is variable in width and general size, and some scientists have suggested that patches signal social status or fitness. This hypothesis has led to a \"veritable 'cottage industry'\" of studies, which have only conclusively shown that patches increase in size with age. The male's bill is black in the breeding season and horn (dark grey) during the rest of the year. The female has no black markings or grey crown. Its upperparts and head are brown with darker streaks around the mantle and a distinct pale supercilium. Its underparts are pale grey-brown. The female's bill is brownish-grey, and becomes darker in breeding plumage, approaching the black of the male's bill.Juveniles are similar to the adult female but deeper brown below and paler above, with paler and less defined supercilia. Juveniles have broader buff feather edges, and tend to have looser, scruffier plumage, like moulting adults. Juvenile males tend to have darker throats and white postoculars like adult males, while juvenile female tend to have white throats. However, juveniles cannot be reliably sexed by plumage: some juvenile males lack any markings of the adult male, and some juvenile females have male features. The bills of young birds are light yellow to straw, paler than the female's bill. Immature males have paler versions of the adult male's markings, which can be very indistinct in fresh plumage. By their first breeding season, young birds generally are indistinguishable from other adults, though they may still be paler during their first year. VoiceMost House Sparrow vocalisations are variations on its short and incessant chirping call. Transcribed as chirrup, tschilp, or philip, this note is made as a contact call by flocking or resting birds, or by males to proclaim nest ownership and invite pairing. In the breeding season the male gives this call repetitively, with emphasis and speed but not much rhythm, forming what is described either as a song or an \"ecstatic call\", similar to a song. Young birds also give a true song, especially in captivity, a warbling similar to that of the European Greenfinch.Aggressive males give a trilled version of their call, transcribed as \"chur-chur-r-r-it-it-it-it\". This call is also used by females in the breeding season, to establish dominance over males while displacing them to feed young or incubate eggs. House Sparrows give a nasal alarm call, the basic sound of which is transcribed as quer, and a shrill chree call in great distress. Another vocalisation is the \"appeasement call\", a soft quee given to inhibit aggression, usually given between birds of a mated pair. These vocalisations are not unique to the House Sparrow, but are shared, with small variations, by all sparrows. Variation There is some variation in the twelve subspecies of House Sparrow, which are divided into two groups, the Oriental indicus group, and the Palaearctic domesticus group. Birds of the domesticus group have grey cheeks, while indicus group birds have white cheeks as well as bright colouration on the crown, a smaller bill, and a longer black bib. The subspecies Passer domesticus tingitanus differs little from the nominate subspecies, except in the worn breeding plumage of the male, in which the head is speckled with black and underparts are paler. P. d. balearoibericus is slightly paler than the nominate but darker than P. d. bibilicus. P. d. bibilicus is paler than most subspecies, but has the grey cheeks of domesticus group birds. The similar P. d. persicus is paler and smaller, and P. d. niloticus is nearly identical but smaller. Of the less widespread indicus group subspecies, P. d. hyrcanus is larger than P. d. indicus, P. d. hufufae is paler, P. d. bactrianus is larger and paler, and P. d. parkini is larger and darker with more black on the breast than any other subspecies. IdentificationThe House Sparrow can be confused with a number of other seed-eating birds, especially its relatives in the genus Passer. Many of these relatives are smaller, with an appearance that is neater or \"cuter\", as with the Dead Sea Sparrow. The dull-coloured female can often not be distinguished from other females, and is nearly identical to the those of the Spanish and Italian Sparrows. The Eurasian Tree Sparrow is smaller and more slender with a chestnut crown and a black patch on each cheek. The male Spanish Sparrow and Italian Sparrow are distinguished by their chestnut crowns. The Sind Sparrow is very similar but smaller, with less black on the male's throat and a distinct pale supercilium on the female.");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);

	// expeced[17]
	description = new Description();
	// description.setKey(91926);
	description.setSourceTaxonKey(116668229);
	// description.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	description.setType("behaviour");
	description.setLanguage(Language.ENGLISH);
	description.setDescription("Social behaviourThe House Sparrow is a very social bird. It is gregarious at all seasons when feeding, often forming flocks with other types of bird. It roosts communally, and its nests are usually grouped together in clumps, and it engages in social activities such as dust and water bathing, and \"social singing\", in which birds call together in bushes.McGillivray, W. Bruce (1980): Communal Nesting in the House Sparrow, Journal of Field Ornithology, 51(4): pp. 371–372 The House Sparrow feeds mostly on the ground, but it flocks in trees and bushes. At feeding stations and nests, female House Sparrows are dominant despite their smaller size, and in reproductive period (usually spring or summer), being dominant, they can fight  for males. Johnston, Richard F. (1969): Aggressive Foraging Behavior in House Sparrows, The Auk, 86(3): pp. 558–559Kalinoski, Ronald (1975): Intra- and Interspecific Aggression in House Finches and House Sparrows, The Condor, 77(4): pp. 375–384 Sleep and roostingHouse Sparrows sleep with the bill tucked underneath the scapular feathers.Reebs, S. G.; Mrosovsky, N. (1990): Photoperiodism in house sparrows: testing for induction with nonphotic zeitgebers, Physiological Zoology, 63: pp. 587–599 Outside of the reproductive season, they often roost communally in trees or shrubs. There is much communal chirping before and after the birds settle in the roost in the evening, as well as before the birds leave the roost in the morning. Some congregating sites separate from the roost may be visited by the birds prior to settling in for the night. Body maintenanceDust or water bathing is common and often occurs in groups. Anting is rare.Potter, E. F. (1970): Anting in wild birds, its frequency and probable purpose, Auk, 87(4): pp. 692–713 Head scratching is done with the leg over the drooped wing. Feeding As an adult, the House Sparrow mostly feeds on the seeds of grains and weeds, but it is opportunistic and adaptable, and eats whatever foods are available. In towns and cities it often scavenges for food in garbage containers and congregates in the outdoors of restaurants and other eating establishments to feed on leftover food and crumbs. It can perform complex tasks to obtain food, such as opening automatic doors to enter supermarkets, clinging to hotel walls to watch vacationers on their balconies,Kalmus, H. (1984): Wall clinging: energy saving by the House Sparrow Passer domesticus, Ibis, 126(1): pp. 72–74 and nectar robbing kowhai flowers.Stidolph, R. D. H. (1974): The Adaptable House Sparrow, Notornis, 21(1): pp. 88 In common with many other birds, the House Sparrow requires grit to digest the harder items in its diet. Grit can be either stone, often grains of masonry, or the shells of eggs or snails; oblong and rough grains are preferred.Gionfriddo, James P.; Best, Louis B. (1995): Grit Use by House Sparrows: Effects of Diet and Grit Size, The Condor, 97(1): pp. 57–67Several studies of the House Sparrow in temperate agricultural areas have found the proportion of seeds in its diet to be about 90%. It will eat almost any seeds, but where it has a choice, it prefers oats and wheat. In urban areas, the House Sparrow feeds largely on food provided directly or indirectly by humans, such as bread, though it prefers raw seeds.Gavett, Ann P.; Wakeley, James S. (1986): Diets of House Sparrows in Urban and Rural Habitats, The Wilson Bulletin, 98 The House Sparrow also eats some plant matter besides seeds, including buds, berries, and fruits such as grapes and cherries.Walsberg, Glenn E. (1975): Digestive Adaptations of Phainopepla nitens Associated with the Eating of Mistletoe Berries, The Condor, 77(2): pp. 169–174. Cooper Ornithological Society In temperate areas, the House Sparrow has an unusual habit of tearing flowers, especially yellow ones, in the spring.Animals form another important part of the House Sparrow's diet, chiefly insects, of which beetles, caterpillars, dipteran flies, and aphids are especially important. Various non-insect arthropods are eaten, as are molluscs and crustaceans where available, earthworms, and even vertebrates such as lizards and frogs. Young House Sparrows are fed mostly on insects until about fifteen days after hatching. They are also given small quantities of seeds, spiders, and grit. In most places, grasshoppers and crickets are the most abundant foods of nestlings. True bugs, ants, sawflies, and beetles are also important, but House Sparrows will take advantage of whatever foods are abundant to feed their young.Anderson, Ted R. (1977): Reproductive Responses of Sparrows to a Superabundant Food Supply, The Condor, 79(2): pp. 205–208. Cooper Ornithological SocietyIvanov, Bojidar (1990): Granivorous birds in the agricultural landscape: pp. 179–197. Pánstwowe Wydawnictom Naukowe House Sparrows have been observed stealing prey from other birds, including American Robins. LocomotionThe House Sparrow's flight is direct (not undulating) and flapping, averaging  and about 15&nbsp;wingbeats per second.Schnell, G. D.; Hellack, J. J. (1978): Flight speeds of Brown Pelicans, Chimney Swifts, and other birds, Bird-Banding, 49(2): pp. 108–112 On the ground, the House Sparrow typically hops rather than walking. It can swim when pressed to do so, by pursuit from predators. Captive birds have been recorded diving and swimming short distances underwater. Dispersal and migrationMost House Sparrows do not move more than a few kilometres during their lifetime. However, there is limited migration in all regions. Some young birds disperse long distances, especially on coasts, and mountain birds move to lower elevations in winter.Broun, Maurice (1972): Apparent migratory behavior in the House Sparrow, The Auk, 89(1): pp. 187–189Waddington, Don C.; Cockrem, John F. (1987): Homing ability of the House Sparrow, Notornis, 34(1) Two subspecies, bactrianus and parkini, are predominately migratory. Unlike the birds in sedentary populations that migrate, birds of migratory subspecies prepare for migration by putting on weight. Breeding House Sparrows can breed in the breeding season immediately following their hatching, and sometimes attempt to do so. Some birds breeding for the first time in tropical areas are only a few months old and still have juvenile plumage. Birds breeding for the first time are rarely successful in raising young, and reproductive success increases with age, as older birds breed earlier in the breeding season, and fledge more young.Hatch, Margret I.; Westneat, David F. (2007): Age-related patterns of reproductive success in house sparrows Passer domesticus, Journal of Avian Biology, 38(5): pp. 603–611 As the breeding season approaches, hormone releases trigger enormous increases in the size of the sexual organs and changes in day length lead males to start calling by nesting sites. The timing of mating and egg-laying varies geographically, and between specific locations and years. This is because a sufficient supply of insects is needed for egg formation and feeding nestlings.Males take up nesting sites before the breeding season, by frequently calling beside them. Unmated males start nest construction and call particularly frequently to attract females. When a female approaches a male during this period, the male displays by moving up and down while drooping and shivering his wings, pushing up his head, raising and spreading his tail, and showing his bib. Males may try to mate with females while calling or displaying. In response, a female will adopt a threatening posture and attack a male before flying away, pursued by the male. The male displays in front of her, attracting other males, who also pursue and display to the female. This group display usually does not immediately result in copulations. Other males usually do not copulate with the female.Brackbill, Hervey (1969): Two Male House Sparrows Copulating on Ground with Same Female, The Auk, 86(1): pp. 146 Copulation is typically initiated by the female giving a soft dee-dee-dee call to the male. Birds of a pair copulate frequently until the female is laying eggs, and the male mounts the female repeatedly each time a pair mates.The House Sparrow is monogamous, and typically mates for life. Birds from pairs often engage in extra-pair copulations, so about 15% of House Sparrow fledglings are unrelated to their mother's mate. Male House Sparrows guard their mates carefully to avoid being cuckolded, and most extra-pair copulation occurs away from nest sites. Males may sometimes have multiple mates, and bigamy is mostly limited by aggression between females. Many birds do not find a nest and a mate, and instead may serve as helpers around the nest for mated pairs, a role which increases the chances of being chosen to replace a lost mate. Lost mates of both sexes can be replaced quickly during the breeding season.Anderson, T. R. (1990): Granivorous birds in the agricultural landscape: pp. 87–94. Pánstwowe Wydawnictom Naukowe The formation of a pair and the bond between the two birds is tied to the holding of a nest site, though paired House Sparrows can recognise each other away from the nest. Nesting Nest sites are varied, though cavities are preferred. Nests are most frequently built in the eaves and other crevices of houses. Holes in cliffs and banks, or in tree hollows are also used. A sparrow sometimes excavates its own nests in sandy banks or rotten branches, but more frequently uses the nests of other birds such as those of swallows in banks and cliffs, and old tree cavity nests. It usually uses deserted nests, though sometimes it usurps active ones.Gowaty, Patricia Adair (Summer 1984): House Sparrows Kill Eastern Bluebirds, Journal of Field Ornithology, 55(3): pp. 378–380 Tree hollows are more commonly used in North America than in Europe, putting the sparrows in competition with bluebirds and other North American cavity nesters, and thereby contributing to their population declines.Especially in warmer areas, the House Sparrow may build its nests in the open, on the branches of trees, especially evergreens and hawthorns, or in the nests of large birds such as storks or magpies. In open nesting sites, breeding success tends to be lower, since breeding begins late and the nest can easily be destroyed or damaged by storms. Less common nesting sites include street lights and neon signs, favoured for their warmth; and the old open-topped nests of other songbirds, which are then domed over.The nest is usually domed, though it may lack a roof in enclosed sites. It has an outer layer of stems and roots, a middle layer of dead grass and leaves, and a lining of feathers, as well as of paper and other soft materials.Indykiewicz, Piotr (1990): Granivorous birds in the agricultural landscape: pp. 95–121. Pánstwowe Wydawnictom Naukowe Nests typically have external dimensions of 20 × 30&nbsp;cm (8 × 12&nbsp;in), but their size varies greatly. The building of the nest is initiated by the unmated male while displaying to females. The female assists in building, but is less active than the male. Some nest building occurs throughout the year, especially after moult in autumn. In colder areas House Sparrows build specially created roost nests, or roost in street lights, to avoid losing heat during the winter.Jansen, R. R. (1983): House Sparrows build roost nests, The Loon, 55: pp. 64–65 House Sparrows do not hold territories, but they defend their nests aggressively against intruders of the same sex.House Sparrows' nests support a wide range of scavenging insects, including nest flies such as Neottiophilum praestum, Protocalliphora blowflies,Neottiophilum praeustum, NatureSpot and over 1,400 species of beetle.Sustek, Zbyšek; Hokntchova, Daša (1983): The beetles (Coleoptera) in the nests of Delichon urbica in Slovakia, Acta Rerum Naturalium Musei Nationalis Slovaci, Bratislava, XXIX: pp. 119–134 Eggs and young<br/><br/>Clutches usually comprise four or five eggs, though numbers from one to ten have been recorded. At least two clutches are usually laid, and up to seven a year may be laid in the tropics or four a year in temperate latitudes. When fewer clutches are laid in a year, especially at higher latitudes, the number of eggs per clutch is greater. Central Asian House Sparrows, which migrate and have only one clutch a year, average 6.5&nbsp;eggs in a clutch. Clutch size is also affected by environmental and seasonal conditions, female age, and breeding density.Some intraspecific brood parasitism occurs, and instances of unusually large numbers of eggs in a nest may be the result of females laying eggs in the nests of their neighbours. Such foreign eggs are sometimes recognised and ejected by females. The House Sparrow is a victim of interspecific brood parasites, but only rarely, since it usually uses nests in holes too small for parasites to enter, and it feeds its young foods unsuitable for young parasites. In turn, the House Sparrow has once been recorded as a brood parasite of the American Cliff Swallow.Stoner, Dayton (December 1939): Parasitism of the English Sparrow on the Northern Cliff Swallow, Wilson Bulletin, 51(4)<br/><br/>The eggs are white, bluish-white, or greenish-white, spotted with brown or grey.Lowther, Peter E.; Cink, Calvin L. (2006): House Sparrow (Passer domesticus), The Birds of North America Online Subelliptical in shape, they range from  in length and  in width, have an average mass of ,BTO Bird facts: House Sparrow. British Trust for Ornithology and an average surface area of .Paganelli, C. V.; Olszowka, A.; Ali, A. (1974): The Avian Egg: Surface Area, Volume, and Density, The Condor, 76(3): pp. 319–325. Cooper Ornithological Society Eggs from the tropical subspecies are distinctly smaller. Eggs begin to develop with the deposition of yolk in the ovary a few days before ovulation. In the day between ovulation and laying, egg white forms, followed by eggshell. Eggs laid later in a clutch are larger, as are those laid by larger females, and egg size is hereditary. Eggs decrease slightly in size from laying to hatching. The yolk comprises 25% of the egg, the egg white 68%, and the shell 7%. Eggs are watery, being 79% liquid, and otherwise mostly protein.The female develops a brood patch of bare skin and plays the main part in incubating the eggs. The male helps, but can only cover the eggs rather than truly incubate them. The female spends the night incubating during this period, while the male roosts near the nest. Eggs hatch at the same time, after a short incubation period lasting 11–14&nbsp;days, and exceptionally for as many as 17 or as few as 9.Groschupf, Kathleen (2001): The Sibley Guide to Bird Life and Behaviour: pp. 562–564. Christopher HelmNice, Margaret Morse (1953): The Question of Ten-day Incubation Periods, The Wilson Bulletin, 65(2): pp. 81–93 The length of the incubation period decreases as ambient temperature increases later in the breeding season.Young House Sparrows typically remain in the nest for 11 to 23 days, normally 14 to 16 days. During this time, they are fed by both parents. As newly hatched House Sparrows do not have sufficient insulation they are brooded for a few days, or longer in cold conditions. The parents swallow the droppings produced by the hatchlings during the first few days; later, the droppings are moved up to  away from the nest.Der es von den Dächern pfeift: Der Haussperling (Passer domesticus). nature-rings.deThe chicks' eyes open after about four days and, at an age of about eight days, the young birds get their first down. If both parents perish, the ensuing intensive begging sounds of the young will often attract replacement parents who feed them until they can sustain themselves.Giebing, Manfred (31 October 2006): Der Haussperling: Vogel des Jahres 2002 All the young in the nest leave it during the same period of a few hours. At this stage they are normally able to fly. They start feeding themselves partly after one or two days, and sustain themselves completely after 7 to 10 days, 14 at the latest.");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);

	// expeced[18]
	description = new Description();
	// description.setKey(91925);
	description.setSourceTaxonKey(116668229);
	// description.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	description.setType("abstract");
	description.setLanguage(Language.ENGLISH);
	description.setDescription("The House Sparrow (Passer domesticus) is a bird of the sparrow family Passeridae, found in most parts of the world. A small bird, it has a typical length of  and a weight of . Females and young birds are coloured pale brown and grey, and males have brighter black, white, and brown markings. One of about 25&nbsp;species in the genus Passer, the House Sparrow is native to most of Europe, the Mediterranean region, and much of Asia. Its intentional or accidental introductions to many regions, including parts of Australia, Africa, and the Americas, make it the most widely distributed wild bird.The House Sparrow is strongly associated with human habitations, and can live in urban or rural settings. Though found in widely varied habitats and climates, it typically avoids extensive woodlands, grasslands, and deserts away from human development. It feeds mostly on the seeds of grains and weeds, but it is an opportunistic eater and commonly eats insects and many other foods. Its predators include domestic cats, hawks, owls, and many other predatory birds and mammals.Because of its numbers, ubiquity and association with human settlements, the sparrow is culturally prominent. It is extensively, and usually unsuccessfully, persecuted as an agricultural pest, but it has also often been kept as a pet as well as being a food item and a symbol of lust and sexual potency, as well as of commonness and vulgarity. Though it is widespread and abundant, its numbers have declined in some areas. The animal's conservation status is listed as Least Concern on the IUCN Red List.");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);

	// expeced[19]
	/*
	description = new Description();
	// description.setKey(444639);
	description.setSourceTaxonKey(125796751);
	// description.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	description.setType("verbreitung und lebensraum");
	description.setLanguage(Language.GERMAN);
	description.setDescription("Verbreitungsgebiet des Haussperlings, auf Einbürgerungen zurückgehende Vorkommen in hellerem Grün VerbreitungDas ursprüngliche paläarktische und orientalische Verbreitungsgebiet hat sich nach zahlreichen Einbürgerungen in anderen Kontinenten seit Mitte des 19. Jahrhunderts fast auf den gesamten Globus ausgedehnt. Heute fehlt der Haussperling nur in den Polargebieten, Teilen Nordsibiriens, Chinas und Südostasiens, in Japan, Westaustralien, dem tropischen Afrika und Südamerika und dem nördlichsten Teil Amerikas. Er ist damit eine der weitest verbreiteten Vogelarten. Die nördliche Grenze des Verbreitungsgebiets schwankt zwischen dem 60. und dem 70. Breitengrad. Auf der Südhalbkugel wurden die Landmassen mit Ausnahme der Antarktis bis zu den südlichsten Ausläufern besiedelt, nur in Westaustralien wird konsequent versucht, eine Besiedlung zu unterbinden.In Europa gibt es Gebiete, in denen der Haussperling durch einen nahen Verwandten vertreten wird: Auf dem italienischen Festland sowie auf den Inseln Sizilien, Korsika und Kreta hat sich der ebenfalls die Nähe des Menschen suchende Italiensperling etabliert. Auf der iberischen Halbinsel, dem Balkan und in Teilen Nordafrikas lebt der Haussperling gemeinsam mit dem nahe verwandten Weidensperling, der noch kein so ausgesprochener Kulturfolger ist.HBV Band 14/1, P. domesticus. Verbreitung der Art. Seite 46–48, siehe Literatur LebensraumAls ursprüngliches Biotop vor dem Anschluss an den Menschen werden trockenwarme, lockere Baumsavannen vermutet, dies bleibt jedoch mangels gesicherter Daten spekulativ. Beim Vordringen nach Mitteleuropa war der Haussperling bereits Kulturfolger mit einer ausgeprägten Bindung an den Menschen. Deutlich wurde dies beispielsweise während der Devastierung Helgolands nach dem Zweiten Weltkrieg, während der mit den Menschen auch die Haussperlinge verschwanden und erst nach der Neubesiedlung ab 1952 wieder zurückkehrten.HBV Band 14/1, P. d. domesticus. Verbreitung in Mitteleuropa. Seite 67–70, siehe Literatur In milden Zonen werden allerdings auch menschenferne Habitate genutzt.Voraussetzungen für Brutvorkommen sind die ganzjährige Verfügbarkeit von Sämereien und Getreideprodukten und geeignete Nistplätze. Optimal sind Dörfer mit Landwirtschaft, Vorstadtbezirke, Stadtzentren mit großen Parkanlagen, zoologische Gärten, Vieh- oder Geflügelfarmen. Es werden aber auch außergewöhnliche Lebensräume besiedelt, wie beispielsweise von der Außenwelt abgeschlossene klimatisierte Flughafengebäude. Das höchstgelegene Brutvorkommen findet sich bei ungefähr 4.500&nbsp;m im Himalaya, das tiefste bei -86&nbsp;m im Death-Valley in Nordamerika.HBV Band 14/1, P. d. domesticus. Biotop. Seite 75–78, siehe Literatur WanderungenIn Europa ist der Haussperling fast ausschließlich Standvogel, in geringem Ausmaß auch Kurzstreckenzieher. Nicht dauernd von Menschen bewohnte Siedlungen im Alpenraum werden im Spätherbst oder Winter auch vom Haussperling geräumt. Die asiatische Unterart P. d. bactrianus wiederum ist ein Zugvogel und überwintert bei Zugdistanzen bis zu 2000 Kilometern in Pakistan und Indien. Die hauptsächlich im Himalaya beheimatete Form P. d. parkini ist Teilzieher.HBV Band 14/1, P. d. domesticus. Wanderungen. Seite 73–75, siehe LiteraturJochen Hölzinger: Die Vögel Baden-Württembergs. Band 3/2, Seite 504–516, siehe LiteraturNach der ersten Brutansiedlung sind die Haussperlinge der Nominatform sehr ortstreu, der Aktionsradius während der Brutzeit kann bei Stadtpopulationen lediglich 50 Meter betragen. Jungvögel streuen ungerichtet und schließen sich zunächst im Spätsommer anwachsenden Schwärmen an. Auch ein Teil der Altvögel schließt sich diesen Herbstschwärmen an, die in die Umgebung der Brutplätze ausstrahlen, um das dortige Nahrungsangebot zu nutzen. Die Altvögel kehren nach Auflösung der Schwärme meist bereits im Frühherbst wieder an ihren ursprünglichen Brutplatz zurück.");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);
	*/
	description = new Description();
	description.setSourceTaxonKey(116668229);
	description.setType("relationships with humans");
	description.setLanguage(Language.ENGLISH);
	description.setDescription("The House Sparrow is closely associated with humans. They are believed to have become associated with humans around 10,000 years ago. Subspecies bactrianus is least associated with humans and considered to be evolutionarily closer to the ancestral non-commensal populations.Sætre, G.-P.; Riyahi, S.; Alibadian, M.; Hermansen, J. S.; Hogner, S.; Olsson, U.; Rojas, M. F. G.; Sæther, S. A.; Trier, C. N.; Elgvin, T. O. (2012): Single origin of human commensalism in the house sparrow, Journal of Evolutionary Biology, 25(4): pp. 788–796 Usually, it is regarded as a pest, since it consumes agricultural products and spreads disease to humans and their domestic animals. Even birdwatchers often hold it in little regard because of its molestation of other birds. In most of the world the House Sparrow is not protected by law. Attempts to control House Sparrows include the trapping, poisoning, or shooting of adults; the destruction of their nests and eggs; or less directly, blocking nest holes and scaring off sparrows with noise, glue, or porcupine wire.Invasive Species Specialist Group: ISSG Database: Ecology of Passer domesticus However, the House Sparrow can be beneficial to humans as well, especially by eating insect pests, and attempts at the large-scale control of the House Sparrow have failed.The House Sparrow has long been used as a food item. From around 1560 to at least the 19th century in northern Europe, earthenware \"sparrow pots\" were hung from eaves to attract nesting birds so that the young could be readily harvested. Wild birds were trapped in nets in large numbers, and sparrow pie was a traditional dish, thought, because of the association of sparrows with lechery, to have aphrodisiac properties. Sparrows were also trapped as food for falconer's birds and zoo animals. In the early part of the twentieth century, sparrow clubs culled many millions of birds and eggs in an attempt to control numbers of this perceived pest, but with only a localised impact on numbers.House Sparrows have been kept as pets at many times in history, though they have no bright plumage or attractive songs, and raising them is difficult. StatusThe House Sparrow has an extremely large range and population, and is not seriously threatened by human activities, so it is assessed as Least Concern for conservation on the IUCN Red List. However, populations have been declining in many parts of the world.Daniels, R. J. Ranjit (2008): Can we save the sparrow?, Current Science, 95(11): pp. 1527–1528De Laet, J.; Summers-Smith, J. D. (2007): The status of the urban house sparrow Passer domesticus in north-western Europe: a review, Journal of Ornithology, 148(Supplement 2): pp. 275–278 These declines were first noticed in North America, where they were initially attributed to the spread of the House Finch, but have been most severe in Western Europe.Summers-Smith, J. Denis (2005): Changes in the House Sparrow Population in Britain, International Studies on Sparrows, 5: pp. 23–37 Declines have not been universal, as no serious declines have been reported from Eastern Europe, but have even occurred in Australia, where the House Sparrow was introduced recently.In Great Britain, populations peaked in the early 1970s, but have since declined by 68% overall,20 November 2008: Sparrow numbers 'plummet by 68%', BBC News and about 90% in some regions.House sparrow, ARKive In London, the House Sparrow almost disappeared from the central city. The numbers of House Sparrows in the Netherlands have dropped in half since the 1980s,van der Poel, Guus (29 January 2001): Concerns about the population decline of the House Sparrow Passer domesticus in the Netherlands so the House Sparrow is even considered an endangered species.Gould, Anne Blair (29 November 2004): House sparrow dwindling. Radio Nederland Wereldomroep This status came to widespread attention after a female House Sparrow, referred to as the \"Dominomus\", was killed after knocking down dominoes arranged as part of an attempt to set a world record.19 November 2005: Sparrow death mars record attempt, BBC News These declines are not unprecedented, as similar reductions in population occurred when the internal combustion engine replaced horses in the 1920s and a major source of food in the form of grain spillage was lost.Bergtold, W. H. (April 1921): The English Sparrow (Passer domesticus) and the Automobile, The Auk, 38(2): pp. 244–250Various causes for the dramatic decreases in population have been proposed, including predation, in particular by Eurasian Sparrowhawks;MacLeod, Ross; Barnett, Phil; Clark, Jacquie; Cresswell, Will (23 March 2006): Mass-dependent predation risk as a mechanism for house sparrow declines?, Biology Letters, 2(1): pp. 43–46Bell, Christopher P.; Baker, Sam W.; Parkes, Nigel G.; Brooke, M. de L.; Chamberlain, Dan E. (2010): The Role of the Eurasian Sparrowhawk (Accipiter nisus) in the Decline of the House Sparrow (Passer domesticus) in Britain, The Auk, 127(2): pp. 411–420 electromagnetic radiation from mobile phones;Balmori, Alfonso; Hallberg, Örjan (2007): The Urban Decline of the House Sparrow (Passer domesticus): A Possible Link with Electromagnetic Radiation, Electromagnetic Biology and Medicine, 26(2): pp. 141–151 and diseases. A shortage of nesting sites caused by changes in urban building design is probably a factor, and conservation organisations have encouraged the use of special nest boxes for sparrows.De Laet, Jenny; Summers-Smith, Denis; Mallord, John (2009): Meeting on the Decline of the Urban House Sparrow Passer domesticus: Newcastle 2009 (24–25 Feb), International Studies on Sparrows, 33: pp. 17–32Chetan, S. Jawale (2012): Improved Design of Nest Box For Indian House Sparrow, Passer domesticus indicus, Bioscience Discovery, 3(1): pp. 97–100 A primary cause of the decline seems to be an insufficient supply of insect food for nestling sparrows.Peach, W. J.; Vincent, K. E.; Fowler, J. A.; Grice, P. V. (2008): Reproductive success of house sparrows along an urban gradient, Animal Conservation, 11(6): pp. 1–11 Declines in insect populations result from an increase of monoculture crops, the heavy use of pesticides, the replacement of native plants in cities with introduced plants and parking areas, and possibly the introduction of unleaded petrol, which produces toxic compounds such as methyl nitrite.Summers-Smith, J. Denis (September 2007): Is unleaded petrol a factor in urban House Sparrow decline?, British Birds, 100: pp. 558Protecting insect habitats on farms,Hole, D. G.; Whittingham, M. J.; Bradbury, R. B.; Anderson, G. Q. A.; Lee, P. L. M.; Wilson, J. D.; Krebs, J. R. (2002): Ecology and conservation of rural house sparrows, Ecology of Threatened Species. Royal Society for the Protection of BirdsHole, David G.; Whittingham, M. J.; Bradbury, Richard B.; Anderson, Guy Q. A.; Lee, Patricia L. M.; Wilson, Jeremy D.; Krebs, John R. (29 August 2002): Agriculture: Widespread local house-sparrow extinctions, Nature, 418(6901): pp. 931–932 and planting native plants in cities benefit the House Sparrow, as does establishing urban green spaces.Mukherjee, Sarah (20 November 2008): Making a garden sparrow-friendly, BBC News To raise awareness of threats to the House Sparrow, World Sparrow Day has been celebrated on 20 March across the world since 2010. Cultural associationsTo many people across the world, the House Sparrow is the most familiar wild animal and, because of its association with humans and familiarity, it is frequently used to represent the common and vulgar, or the lewd. One of the reasons for the introduction of House Sparrows throughout the world was their association with the European homeland of many immigrants. Birds usually described later as sparrows are referred to in many works of ancient literature and religious texts in Europe and western Asia. These references may not always refer specifically to the House Sparrow, or even to small, seed-eating birds, but later writers who were inspired by these texts often had the House Sparrow in mind. In particular, sparrows were associated by the ancient Greeks with Aphrodite, the goddess of love, due to their perceived lustfulness, an association echoed by later writers such as Chaucer and Shakespeare. Jesus's use of \"sparrows\" as an example of divine providence in the () also inspired later references, such as that in Shakespeare's Hamlet and the Gospel hymn His Eye Is on the Sparrow. <hiero>G37</hiero> The House Sparrow is only represented in ancient Egyptian art very rarely, but an Egyptian hieroglyph is based on it. The sparrow hieroglyph had no phonetic value and was used as a determinative in words to indicate small, narrow, or bad. An alternative view is that the hieroglyph meant \"a prolific man\" or \"the revolution of a year\".");
	description.setLicense("CC-BY-SA 3.0");
	descriptions.add(description);

	PagingResponse<Description> expected = new PagingResponse<Description>(0,20);
	expected.setResults(descriptions);
	expected.setEndOfRecords(false);

	SpeciesAPIClient sut = new SpeciesAPIClient();
	PagingResponse<Description> actual = sut.getDescripitons(5231190, null);

	printCollections("getDescripitons", actual.getResults(), expected.getResults());

	assertThat(actual, is(expected));
    }

    @Test
    public void getDistributions() {
	PagingResponse<Distribution> expected = new PagingResponse<Distribution>(0,20);
	expected.setEndOfRecords(false);
	List<Distribution> results = newArrayList();
	expected.setResults(results);

	Distribution distribution = new Distribution();
	// distribution.setKey(234734);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("AD");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.NATIVE);

	results.add(distribution);
	distribution = new Distribution();
	// distribution.setKey(234657);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("AE");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.NATIVE);

	results.add(distribution);
	distribution = new Distribution();
	// distribution.setKey(234660);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("AF");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.NATIVE);

	results.add(distribution);
	distribution = new Distribution();
	// distribution.setKey(234685);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("AI");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.INTRODUCED);

	results.add(distribution);
	distribution = new Distribution();
	// distribution.setKey(234735);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("AL");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.NATIVE);

	results.add(distribution);
	distribution = new Distribution();
	// distribution.setKey(234737);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("AM");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.NATIVE);

	results.add(distribution);
	distribution = new Distribution();
	// distribution.setKey(234686);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("AR");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.INTRODUCED);

	results.add(distribution);
	distribution = new Distribution();
	// distribution.setKey(234738);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("AT");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.NATIVE);

	results.add(distribution);
	distribution = new Distribution();
	// distribution.setKey(234647);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("AU");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.INTRODUCED);
	results.add(distribution);

	distribution = new Distribution();
	// distribution.setKey(234687);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("AW");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.INTRODUCED);
	results.add(distribution);

	distribution = new Distribution();
	// distribution.setKey(234739);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("AZ");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.NATIVE);
	results.add(distribution);

	distribution = new Distribution();
	// distribution.setKey(234741);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("BA");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.NATIVE);
	results.add(distribution);

	distribution = new Distribution();
	// distribution.setKey(234743);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("BE");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.NATIVE);
	results.add(distribution);

	distribution = new Distribution();
	// distribution.setKey(234745);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("BG");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.NATIVE);
	results.add(distribution);

	distribution = new Distribution();
	// distribution.setKey(234747);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("BH");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.NATIVE);
	results.add(distribution);

	distribution = new Distribution();
	// distribution.setKey(234688);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("BM");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.INTRODUCED);
	results.add(distribution);

	distribution = new Distribution();
	// distribution.setKey(234689);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("BO");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.INTRODUCED);
	results.add(distribution);

	distribution = new Distribution();
	// distribution.setKey(234690);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("BQ");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.INTRODUCED);
	results.add(distribution);

	distribution = new Distribution();
	// distribution.setKey(234691);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("BR");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.INTRODUCED);
	results.add(distribution);

	distribution = new Distribution();
	// distribution.setKey(234692);
	distribution.setSourceTaxonKey(124750679);
	// distribution.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	distribution.setCountry("BS");
	distribution.setStatus(OccurrenceStatus.PRESENT);
	distribution.setEstablishmentMeans(EstablishmentMeans.INTRODUCED);
	results.add(distribution);

	SpeciesAPIClient sut = new SpeciesAPIClient();
	PagingResponse<Distribution> actual = sut.getDistributions(5231190, null);

	printCollections("getDistributions", actual.getResults(), expected.getResults());

	assertThat(actual, is(expected));
    }

    @Test
    public void getMedia() {
	PagingResponse<NameUsageMediaObject> expected = new PagingResponse<NameUsageMediaObject>(0,20);
	expected.setEndOfRecords(true);
	List<NameUsageMediaObject> results = new ArrayList<NameUsageMediaObject>();
	expected.setResults(results);
	NameUsageMediaObject medium = new NameUsageMediaObject();
	medium.setType(MediaType.StillImage);
	medium.setFormat("image/jpeg");
	try {
	    medium.setIdentifier(new URI("http://upload.wikimedia.org/wikipedia/commons/d/d9/House_sparrowII.jpg"));
	    medium.setReferences(new URI("http://commons.wikimedia.org/wiki/File:House_sparrowII.jpg"));
	}
	catch (URISyntaxException e) {
	}
	medium.setTitle("Haussperling (Männchen)");
	medium.setLicense("GFDL-1.2");
	results.add(medium);
	SpeciesAPIClient sut = new SpeciesAPIClient();

	PagingResponse<NameUsageMediaObject> actual = sut.getMedia(5231190, null);

	printCollections("getMedia", actual.getResults(), expected.getResults());

	assertThat(actual, is(expected));
    }

    @Test
    public void getReferences() {
	PagingResponse<Reference> expected = new PagingResponse<Reference>(0,20);
	expected.setEndOfRecords(true);
	List<Reference> results = newArrayList();
	expected.setResults(results);
	Reference result = new Reference();
	results.add(result);
	// result.setKey(1131103);
	result.setSourceTaxonKey(117190020);
	// result.setUsageKey(117190020);
	// result.setDatasetKey(UUID.fromString("1329753c-0537-451c-92a1-cddaa4534736"));
	result.setCitation("Compiled by Worthy, T. H. ; Holdaway, R. N. ; Tennyson, A. J. D. , King, C. M. ; Roberts, C. D. ; Bell, B. D. ; Fordyce, R. E. ; Nicol, R. S. ; Worthy, T. H. ; Paulin, C. D. ; Hitchmough, R. A. ; Keyes, I. W. ; Baker, A. N. ; Stewart, A. L. ; Hiller, N. ; McDowall, R. M. ; Holdaway, R. N. ; McPhee, R. P. ; Schwarzhans, W. W. ; Tennyson, A. J. D. ; Rust, R. ; Macadie, I. 24: Phylum CHORDATA: lancelets, fishes, amphibians, reptiles, birds, mammals, Living and recently extinct birds. <i>In</i>: New Zealand Inventory of Biodiversity Volume 1.");

	result = new Reference();
	results.add(result);
	// result.setKey(1164394);
	result.setSourceTaxonKey(117207287);
	// result.setUsageKey(117207287);
	// result.setDatasetKey(UUID.fromString("134eca5f-65ab-49a2-a229-3d0d35fcbefe"));
	result.setCitation("Compiled by Worthy, T. H. ; Holdaway, R. N. ; Tennyson, A. J. D. , King, C. M. ; Roberts, C. D. ; Bell, B. D. ; Fordyce, R. E. ; Nicol, R. S. ; Worthy, T. H. ; Paulin, C. D. ; Hitchmough, R. A. ; Keyes, I. W. ; Baker, A. N. ; Stewart, A. L. ; Hiller, N. ; McDowall, R. M. ; Holdaway, R. N. ; McPhee, R. P. ; Schwarzhans, W. W. ; Tennyson, A. J. D. ; Rust, R. ; Macadie, I. 24: Phylum CHORDATA: lancelets, fishes, amphibians, reptiles, birds, mammals, Living and recently extinct birds. <i>In</i>: New Zealand Inventory of Biodiversity Volume 1.");

	result = new Reference();
	results.add(result);
	// result.setKey(1164395);
	result.setSourceTaxonKey(117207287);
	// result.setUsageKey(117207287);
	// result.setDatasetKey(UUID.fromString("134eca5f-65ab-49a2-a229-3d0d35fcbefe"));
	result.setCitation("NZIB February 2012");

	result = new Reference();
	results.add(result);
	// result.setKey(1131104);
	result.setSourceTaxonKey(117190020);
	// result.setUsageKey(117190020);
	// result.setDatasetKey(UUID.fromString("1329753c-0537-451c-92a1-cddaa4534736"));
	result.setCitation("NZIB June 2012");

	result = new Reference();
	results.add(result);
	// result.setKey(1164396);
	result.setSourceTaxonKey(117207287);
	// result.setUsageKey(117207287);
	// result.setDatasetKey(UUID.fromString("134eca5f-65ab-49a2-a229-3d0d35fcbefe"));
	result.setCitation("NZOR Hosted June 2012");

	result = new Reference();
	results.add(result);
	// result.setKey(1494400);
	result.setSourceTaxonKey(131470469);
	// result.setUsageKey(131470469);
	// result.setDatasetKey(UUID.fromString("e9014a2a-a099-49e6-8a46-c7d888ab28d2"));
	result.setCitation("NZOR Hosted June 2012");

	SpeciesAPIClient sut = new SpeciesAPIClient();
	PagingResponse<Reference> actual = sut.getReferences(5231190, null);
	assertThat(actual, is(expected));
    }

    @Test
    public void getSpeciesProfiles() {
	PagingResponse<SpeciesProfile> expected = new PagingResponse<SpeciesProfile>(0,20);
	expected.setEndOfRecords(true);
	List<SpeciesProfile> results = newArrayList();
	expected.setResults(results);

	SpeciesProfile result = new SpeciesProfile();
	results.add(result);
	result.setSourceTaxonKey(111381448);
	result.setExtinct(false);

	result = new SpeciesProfile();
	results.add(result);
	result.setSourceTaxonKey(135534862);
	// result.setMarine(false);
	result.setExtinct(false);


	SpeciesAPIClient sut = new SpeciesAPIClient();
	PagingResponse<SpeciesProfile> actual = sut.getSpeciesProfiles(5231190, null);


	printCollections("getSpeciesProfiles", actual.getResults(), expected.getResults());

	assertThat(actual, is(expected));
    }

    @Test
    public void getVernacularNames() {
	PagingResponse<VernacularName> expected = new PagingResponse<VernacularName>(0,20);
	expected.setEndOfRecords(false);
	List<VernacularName> results = newArrayList();
	expected.setResults(results);

	// expected[0]
	VernacularName result = new VernacularName();
	// result.setKey(775757);
	result.setSourceTaxonKey(111357780);
	// result.setDatasetKey(UUID.fromString("b351a324-77c4-41c9-a909-f30f77268bc4"));
	result.setVernacularName("English sparrow");
	result.setLanguage(Language.UNKNOWN);
	result.setPreferred(false);
	results.add(result);

	// expected[1]
	result = new VernacularName();
	// result.setKey(775758);
	result.setSourceTaxonKey(111357780);
	// result.setDatasetKey(UUID.fromString("b351a324-77c4-41c9-a909-f30f77268bc4"));
	result.setVernacularName("Europese huismuis");
	result.setLanguage(Language.UNKNOWN);
	result.setPreferred(false);
	results.add(result);

	// expected[2]
	result = new VernacularName();
	// result.setKey(775759);
	result.setSourceTaxonKey(111357780);
	// result.setDatasetKey(UUID.fromString("b351a324-77c4-41c9-a909-f30f77268bc4"));
	result.setVernacularName("Gorrion domestico");
	result.setLanguage(Language.UNKNOWN);
	result.setPreferred(false);
	results.add(result);

	// expected[3]
	result = new VernacularName();
	// result.setKey(305563);
	result.setSourceTaxonKey(116351961);
	// result.setDatasetKey(UUID.fromString("1bd42c2b-b58a-4a01-816b-bec8c8977927"));
	result.setVernacularName("Gorrión Común");
	result.setLanguage(Language.SPANISH);
	result.setPreferred(false);
	results.add(result);

	// expected[4]
	result = new VernacularName();
	// result.setKey(518432);
	result.setSourceTaxonKey(119127243);
	// result.setDatasetKey(UUID.fromString("7ddf754f-d193-4cc9-b351-99906754a03b"));
	result.setVernacularName("Gorrión casero");
	result.setLanguage(Language.SPANISH);
	result.setPreferred(false);
	results.add(result);

	// expected[5]
	/*
	result = new VernacularName();
	// result.setKey(232488);
	result.setSourceTaxonKey(107239200);
	result.setSourceTaxonKey(107239200);
	// result.setDatasetKey(UUID.fromString("9ca92552-f23a-41a8-a140-01abaa31c931"));
	result.setVernacularName("Gorrión casero");
	result.setLanguage(Language.SPANISH);
	result.setPreferred(false);
	results.add(result);
	*/

	// expected[6]
	result = new VernacularName();
	// result.setKey(305571);
	result.setSourceTaxonKey(116351961);
	// result.setDatasetKey(UUID.fromString("1bd42c2b-b58a-4a01-816b-bec8c8977927"));
	result.setVernacularName("Gråsparv");
	result.setLanguage(Language.SWEDISH);
	result.setPreferred(false);
	results.add(result);

	// expected[7]
	result = new VernacularName();
	// result.setKey(414046);
	result.setSourceTaxonKey(106304041);
	// result.setDatasetKey(UUID.fromString("fab88965-e69d-4491-a04d-e3198b626e52"));
	result.setVernacularName("Haussperling");
	result.setLanguage(Language.UNKNOWN);
	result.setPreferred(false);
	results.add(result);

	// expected[8]
	result = new VernacularName();
	// result.setKey(451536);
	result.setSourceTaxonKey(107881308);
	// result.setDatasetKey(UUID.fromString("39653f3e-8d6b-4a94-a202-859359c164c5"));
	result.setVernacularName("Haussperling");
	result.setLanguage(Language.GERMAN);
	result.setPreferred(false);
	results.add(result);

	// expected[9]
	result = new VernacularName();
	// result.setKey(305561);
	result.setSourceTaxonKey(116351961);
	// result.setDatasetKey(UUID.fromString("1bd42c2b-b58a-4a01-816b-bec8c8977927"));
	result.setVernacularName("Haussperling");
	result.setLanguage(Language.GERMAN);
	result.setPreferred(false);
	results.add(result);
	
	/*
	// expected[10]
	result = new VernacularName();
	// result.setKey(456730);
	result.setSourceTaxonKey(125796751);
	// result.setDatasetKey(UUID.fromString("16c3f9cb-4b19-4553-ac8e-ebb90003aa02"));
	result.setVernacularName("Haussperling");
	result.setLanguage(Language.GERMAN);
	result.setPreferred(false);
	results.add(result);
	*/

	// expected[11]
	/*
	result = new VernacularName();
	// result.setKey(232489);
	result.setSourceTaxonKey(107239200);
	// result.setDatasetKey(UUID.fromString("9ca92552-f23a-41a8-a140-01abaa31c931"));
	result.setVernacularName("House Sparrow");
	result.setLanguage(Language.ENGLISH);
	result.setPreferred(false);
	results.add(result);
	*/
	result = new VernacularName();
	result.setSourceTaxonKey(125796751);
	result.setVernacularName("Haussperling");
	result.setLanguage(Language.GERMAN);
	result.setPreferred(false);
	results.add(result);

	// expected[12]
	result = new VernacularName();
	// result.setKey(305562);
	result.setSourceTaxonKey(116351961);
	// result.setDatasetKey(UUID.fromString("1bd42c2b-b58a-4a01-816b-bec8c8977927"));
	result.setVernacularName("House Sparrow");
	result.setLanguage(Language.ENGLISH);
	result.setPreferred(false);
	results.add(result);

	// expected[13]
	result = new VernacularName();
	result.setSourceTaxonKey(111381448);
	result.setVernacularName("House Sparrow");
	result.setLanguage(Language.ENGLISH);
	result.setPreferred(false);
	results.add(result);

	// expected[14]
	result = new VernacularName();
	// result.setKey(518433);
	result.setSourceTaxonKey(119127243);
	// result.setDatasetKey(UUID.fromString("7ddf754f-d193-4cc9-b351-99906754a03b"));
	result.setVernacularName("House Sparrow");
	result.setLanguage(Language.ENGLISH);
	result.setPreferred(false);
	results.add(result);

	// expected[15]
	result = new VernacularName();
	// result.setKey(474437);
	result.setSourceTaxonKey(116668229);
	// result.setDatasetKey(UUID.fromString("cbb6498e-8927-405a-916b-576d00a6289b"));
	result.setVernacularName("House Sparrow");
	result.setLanguage(Language.ENGLISH);
	result.setPreferred(false);
	results.add(result);

	// expected[16]
	result = new VernacularName();
	result.setSourceTaxonKey(131470469);
	result.setVernacularName("House Sparrow");
	result.setLanguage(Language.ENGLISH);
	result.setPreferred(false);
	results.add(result);

	// expected[17]
	result = new VernacularName();
	// result.setKey(518435);
	result.setSourceTaxonKey(119127243);
	// result.setDatasetKey(UUID.fromString("7ddf754f-d193-4cc9-b351-99906754a03b"));
	result.setVernacularName("House Sparrow");
	result.setLanguage(Language.ENGLISH);
	result.setPreferred(false);
	results.add(result);

	// expected[18]
	result = new VernacularName();
	// result.setKey(518434);
	result.setSourceTaxonKey(119127243);
	// result.setDatasetKey(UUID.fromString("7ddf754f-d193-4cc9-b351-99906754a03b"));
	result.setVernacularName("House Sparrow");
	result.setLanguage(Language.ENGLISH);
	result.setPreferred(false);
	results.add(result);

	// expected[19]
	result = new VernacularName();
	// result.setKey(820054);
	result.setSourceTaxonKey(126345167);
	// result.setDatasetKey(UUID.fromString("d7435f14-dfc9-4aaa-bef3-5d1ed22d65bf"));
	result.setVernacularName("House Sparrow");
	result.setLanguage(Language.ENGLISH);
	result.setPreferred(false);
	results.add(result);

	result = new VernacularName();
	// result.setKey(337317);
	result.setSourceTaxonKey(124750679);
	// result.setDatasetKey(UUID.fromString("19491596-35ae-4a91-9a98-85cf505f1bd3"));
	result.setVernacularName("House Sparrow");
	result.setLanguage(Language.ENGLISH);
	result.setPreferred(false);
	results.add(result);

	result = new VernacularName();
	result.setSourceTaxonKey(117207287);
	result.setVernacularName("House sparrow");
	result.setLanguage(Language.ENGLISH);
	result.setPreferred(false);
	results.add(result);

	SpeciesAPIClient sut = new SpeciesAPIClient();
	PagingResponse<VernacularName> actual = sut.getVernacularNames(5231190, null);

	printCollections("getVernacularNames", actual.getResults(), expected.getResults());

	assertThat(actual, is(expected));
    }

    @Test
    public void getTypeSpecimens() {
	PagingResponse<TypeSpecimen> expected = new PagingResponse<TypeSpecimen>(0,20);
	expected.setEndOfRecords(true);
	List<TypeSpecimen> results = newArrayList();
	expected.setResults(results);
	TypeSpecimen result = new TypeSpecimen();
	result.setTypeDesignatedBy("Perkins, R. C. L. 1899. Orthoptera. Fauna Hawaiiensis, Orth. 2:27");
	result.setScientificName("Nesogryllus stridulans Perkins, 1899");
	results.add(result);

	SpeciesAPIClient sut = new SpeciesAPIClient();
	PagingResponse<TypeSpecimen> actual = sut.getTypeSpecimens(131684623, null);

	printCollections("getTypeSpecimens", actual.getResults(), expected.getResults());

	assertThat(actual, is(expected));
    }
}
