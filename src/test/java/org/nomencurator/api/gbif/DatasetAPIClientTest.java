/*
 * DatasetAPIClientTest.java:  testcases of DatasetAPIClient
 *
 * Copyright (c) 2014 Nozomi `James' Ytow
 * All rights reserved.
 */

/*
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions, and the following disclaimer,
 *    without modification, immediately at the beginning of the file.
 * 2. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * Where this Software is combined with software released under the terms of
 * either the GNU Public License ("GPL") or the Mozilla Public Licence ("MPL"),
 * and the terms of the GPL/MPL would require the combined work to also be
 * released under the terms of the GPL/MPL, the terms and conditions of this
 * License will apply in addition to those of the GPL/MPL with the exception
 * of any terms or conditions of this License that conflict with, or are
 * expressly prohibited by, the GPL/MPL.
 *
 * In short, the software is licenced under either of CC BY-SA, or optionally GPL/MPL.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
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

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.common.search.SearchResponse;

import org.gbif.api.model.registry.Citation;
import org.gbif.api.model.registry.Dataset;
import org.gbif.api.model.registry.Endpoint;
import org.gbif.api.model.registry.Identifier;
import org.gbif.api.model.registry.MachineTag;
import org.gbif.api.model.registry.Tag;

import org.gbif.api.vocabulary.EndpointType;
import org.gbif.api.vocabulary.EstablishmentMeans;
import org.gbif.api.vocabulary.DatasetType;
import org.gbif.api.vocabulary.IdentifierType;
import org.gbif.api.vocabulary.Language;
import org.gbif.api.vocabulary.MediaType;
import org.gbif.api.vocabulary.NameType;
import org.gbif.api.vocabulary.OccurrenceStatus;
import org.gbif.api.vocabulary.Origin;
import org.gbif.api.vocabulary.Rank;
import org.gbif.api.vocabulary.TaxonomicStatus;


import org.nomencurator.api.gbif.DatasetAPIClient;

/**
 * Unit test for DatasetAPIClientTest
 */
public class DatasetAPIClientTest
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
		if(!expectedString.equals(actualString)) {
		    System.out.println("expected[" + i + "]");
		    System.out.println(expectedString);
		    System.out.println("actual[" + i + "]");
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

    @Test
    public void test_DatasetAPIClient() {
	DatasetAPIClient sut = new DatasetAPIClient();
	assertTrue(true);
    }

    @Test
    public void test_create() {
	DatasetAPIClient sut = new DatasetAPIClient();
	UUID actual = sut.create(new Dataset());
	UUID expected = null;
	assertThat(actual, is(expected));
    }

    public void test_delete() {
	DatasetAPIClient sut = new DatasetAPIClient();
	sut.delete(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	assertTrue(true);
    }

    protected Dataset getBackboneTaxonomy() 
    {
	Dataset d = new Dataset();
	d.setKey(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	d.setInstallationKey(UUID.fromString("9960c792-f762-11e1-a439-00145eb45e9a"));
	d.setPublishingOrganizationKey(UUID.fromString("e196c8d6-f795-463c-80c4-310dd14ee50b"));
	d.setExternal(false);
	d.setNumConstituents(0);
	d.setType(DatasetType.CHECKLIST);
	d.setTitle("GBIF Backbone Taxonomy");
	d.setAlias("nub");
	d.setDescription("The GBIF Backbone Taxonomy, often called the Nub taxonomy, is a single synthetic management classification with the goal of covering all names GBIF is dealing with.     It's the taxonomic backbone that allows GBIF to integrate name based information from different resources, no matter if these are occurrence datasets, species pages, names from nomenclators or external sources like EOL, Genbank or IUCN.     This backbone allows taxonomic search, browse and reporting operations across all those resources in a consistent way and to provide means to crosswalk names from one source to another.     It is updated regulary through an automated process in which the Catalogue of Life acts as a starting point also providing the complete higher classification above families.        The backbone makes use of the following primary sources for it's names:    <ul>    <li>The Catalogue of Life, 3rd January 2011 (<i>2275479</i>)    <li>Interim Register of Marine and Nonmarine Genera (<i>633839</i>)    <li>International Plant Names Index (<i>597619</i>)    <li>Index Fungorum (<i>287971</i>)    <li>World Register of Marine Species (<i>184257</i>)    <li>Fauna Europaea (<i>112305</i>)    <li>Integrated Taxonomic Information System (<i>64263</i>)    <li>NUB Generator [autonym] (<i>63460</i>)    <li>Paleobiology Database (<i>41689</i>)    <li>IRMNG Homonym List (<i>37432</i>)    <li>Euro+Med Plantbase (<i>14275</i>)    <li>The National Checklist of Taiwan (<i>12298</i>)    <li>The Clements Checklist (<i>11389</i>)    <li>IUCN Red List of Threatened Species (<i>10914</i>)    <li>Afromoths, online datbase of Afrotropical moth species (Lepidoptera) (<i>10861</i>)    <li>GRIN Taxonomy for Plants (<i>9047</i>)    <li>Belgian Species List (<i>8972</i>)    <li>Psocodea Species File (<i>6612</i>)    <li>Mammal Species of the World, 3rd edition (<i>4885</i>)    <li>Catalogue of Afrotropical Bees (<i>4695</i>)    <li>Endemic species in Taiwan (<i>3920</i>)    <li>Orthoptera Species File (<i>3744</i>)    <li>Coreoidea Species File (<i>3396</i>)    <li>NUB Generator [implicit canonical] (<i>3129</i>)    <li>Aphid Species File (<i>3026</i>)    <li>Dermaptera Species File (<i>2637</i>)    <li>Database of Vascular Plants of Canada (VASCAN) (<i>1307</i>)    <li>IOC World Bird Names (<i>684</i>)    <li>Plecoptera Species File (<i>542</i>)    <li>ICTV Master Species List (<i>342</i>)    <li>Phasmida Species File (<i>308</i>)    <li>Mantodea Species File (<i>275</i>)    <li>Blattodea Species File (<i>251</i>)    <li>World Typhlocybinae database (<i>247</i>)    <li>Coleorrhyncha Species File (<i>101</i>)    <li>Embioptera Species File (<i>97</i>)    <li>True Fruit Flies (Diptera, Tephritidae) of the Afrotropical Region (<i>27</i>)    <li>COA Wildlife Conservation List (<i>25</i>)    <li>Taiwanese IUCN species list (<i>24</i>)    <li>Invasive Alien Species in Belgium - HARMONIA database (<i>2</i>)    </ul>");
	d.setLanguage(Language.ENGLISH);
	try {
	    d.setHomepage(new URI("http://ecat-dev.gbif.org/about/nub/"));
	    d.setLogoUrl(new URI("http://cdn.gbif.org/img/header/logo.png"));
	} catch (URISyntaxException e) { }
	
	Citation citation = new Citation();
	d.setCitation(citation);
	citation.setText("The Global Biodiversity Information Facility: GBIF Backbone Taxonomy");
	d.setLockedForAutoUpdate(false);
	d.setCreatedBy("registry-migration.gbif.org");
	d.setModifiedBy("markus");
	d.setCreated(parseDateTime("2011-03-02T11:06:25.000+0000"));
	d.setModified(parseDateTime("2014-08-26T12:25:09.149+0000"));
	Endpoint endpoint = new Endpoint();
	d.getEndpoints().add(endpoint);
	endpoint.setKey(3567);
	endpoint.setType(EndpointType.DWC_ARCHIVE);
	try {
	    endpoint.setUrl(new URI("http://ecat-dev.gbif.org/repository/export/checklist1.zip"));
	} catch (URISyntaxException e) { }
	endpoint.setCreatedBy("registry-migration.gbif.org");
	endpoint.setModifiedBy("registry-migration.gbif.org");
	endpoint.setCreated(parseDateTime("2012-07-03T15:45:11.000+0000"));
	endpoint.setModified(parseDateTime("2012-07-03T15:45:11.000+0000"));
	MachineTag machineTag = new MachineTag();
	d.getMachineTags().add(machineTag);
	machineTag.setKey(127077);
	machineTag.setNamespace("crawler.gbif.org");
	machineTag.setName("crawl_attempt");
	machineTag.setValue("1");
	machineTag.setCreatedBy("crawler.gbif.org");
	machineTag.setCreated(parseDateTime("2014-08-26T12:25:09.461+0000"));
	Tag tag = new Tag();
	d.getTags().add(tag);
	tag.setKey(36);
	tag.setValue("nub");
	tag.setCreatedBy("registry-migration.gbif.org");
	tag.setCreated(parseDateTime("2011-03-02T11:06:25.000+0000"));
	tag = new Tag();
	d.getTags().add(tag);
	tag.setKey(37);
	tag.setValue("gbif");
	tag.setCreatedBy("registry-migration.gbif.org");
	tag.setCreated(parseDateTime("2011-03-02T11:06:25.000+0000"));
	Identifier identifier = new Identifier();
	d.getIdentifiers().add(identifier);
	identifier.setKey(11963);
	identifier.setType(IdentifierType.GBIF_PORTAL);
	identifier.setIdentifier("1");
	identifier.setCreatedBy("registry-migration.gbif.org");
	identifier.setCreated(parseDateTime("2012-09-05T14:04:22.000+0000"));
	d.setDataLanguage(Language.ENGLISH);
	d.setPubDate(parseDateTime("2013-07-01T22:00:00.000+0000"));

	return d;
    }

    public void test_get() {
	DatasetAPIClient sut = new DatasetAPIClient();
	Dataset expected = getBackboneTaxonomy();
	Dataset actual = sut.get(UUID.fromString("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c"));
	assertThat(actual, is(expected));
    }

    /*
    public List<Dataset> list() {
    public PagingResponse<Dataset> list(@Nullable Pageable page) {
    public List<Dataset> search(String query) {
    public PagingResponse<Dataset> search(String query, @Nullable Pageable page)
    public List<Dataset> listByIdentifier(IdentifierType type, String identifier) 
    public PagingResponse<Dataset> listByIdentifier(IdentifierType type, String identifier, @Nullable Pageable page)
    public List<Dataset> listByIdentifier(String identifier) 
    public  PagingResponse<Dataset> listByIdentifier(String identifier, @Nullable Pageable page)
    public void update(@NotNull Dataset entity) {}
    public List<Dataset> listConstituents(UUID datasetKey)
    public PagingResponse<Dataset> listConstituents(UUID datasetKey, @Nullable Pageable page)
    public PagingResponse<Dataset> listConstituents(@Nullable Pageable page)
    public PagingResponse<Dataset> listByCountry(Country country, @Nullable DatasetType type, @Nullable Pageable page)
    public PagingResponse<Dataset> listByType(DatasetType type, @Nullable Pageable page)
    public List<Metadata> listMetadata(UUID datasetKey, @Nullable MetadataType type)
    public List<Network> listNetworks(UUID datasetKey)
    public Metadata getMetadata(int metadataKey)
    public void deleteMetadata(int metadataKey){}
    public Metadata insertMetadata(UUID datasetKey, InputStream document)
    public InputStream getMetadataDocument(UUID datasetKey)
    public InputStream getMetadataDocument(int metadataKey)
    public PagingResponse<Dataset> listDeleted(@Nullable Pageable page)
    public PagingResponse<Dataset> listDuplicates(@Nullable Pageable page)
    public PagingResponse<Dataset> listDatasetsWithNoEndpoint(@Nullable Pageable page)
    public int addMachineTag(@NotNull UUID targetEntityKey, @NotNull MachineTag machineTag) { return -1;}
    public int addMachineTag(@NotNull UUID targetEntityKey, @NotNull String namespace, @NotNull String name,@NotNull String value) { return -1;}
    public void deleteMachineTag(@NotNull UUID targetEntityKey, int machineTagKey){}
    public void deleteMachineTags(@NotNull UUID targetEntityKey, @NotNull String namespace){}
    public void deleteMachineTags(@NotNull UUID targetEntityKey, @NotNull String namespace, @NotNull String name){}
    public List<MachineTag> listMachineTags(@NotNull UUID targetEntityKey)
    public int addTag(@NotNull UUID targetEntityKey, @NotNull String value) {return -1;}
    public int addTag(@NotNull UUID targetEntityKey, @NotNull Tag tag) {return -1;}
    public void deleteTag(@NotNull UUID taggedEntityKey, int tagKey) {}
    public List<Tag> listTags(@NotNull UUID taggedEntityKey, @Nullable String owner)
    public int addComment(@NotNull UUID targetEntityKey, @NotNull Comment comment) {return -1;}
    public void deleteComment(@NotNull UUID targetEntityKey, int commentKey) {} 
    public List<Comment> listComments(@NotNull UUID targetEntityKey)
    public int addIdentifier(@NotNull UUID targetEntityKey, @NotNull Identifier identifier){return -1;}
    public void deleteIdentifier(@NotNull UUID targetEntityKey, int identifierKey){}
    public List<Identifier> listIdentifiers(@NotNull UUID targetEntityKey)
    public int addEndpoint(@NotNull UUID targetEntityKey, @NotNull Endpoint endpoint){return -1;}
    public void deleteEndpoint(@NotNull UUID targetEntityKey, int endpointKey){}
    public List<Endpoint> listEndpoints(@NotNull UUID targetEntityKey)
    public int addContact(@NotNull UUID targetEntityKey, @NotNull Contact contact){return -1;}
    public void deleteContact(@NotNull UUID targetEntityKey, int contactKey){}
    public List<Contact> listContacts(@NotNull UUID targetEntityKey)
    public void updateContact(@NotNull UUID targetEntityKey, @NotNull Contact contact){}
    */
}
