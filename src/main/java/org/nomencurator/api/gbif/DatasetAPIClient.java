/*
 * DatasetAPIClient.java:  a client implentation using DatasetAPIClient of GBIF
 *
 * Copyright (c) 2014, 2015, 2016, 2019, 2020, 2023 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP19K12711
 */

package org.nomencurator.api.gbif;

// for Jackson 1.x
// import org.codehaus.jackson.map.ObjectMapper;
// import org.codehaus.jackson.type.TypeReference;
// or Jackson 2.x
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.collect.Lists;

import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;

import javax.validation.constraints.NotNull;
import javax.annotation.Nullable;

import org.gbif.api.jackson.LicenseSerde;

//import org.gbif.ws.mixin.LicenseMixin;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;

import org.gbif.api.model.registry.Comment;
import org.gbif.api.model.registry.Contact;
import org.gbif.api.model.registry.Dataset;
import org.gbif.api.model.registry.Endpoint;
import org.gbif.api.model.registry.Grid;
import org.gbif.api.model.registry.Identifier;
import org.gbif.api.model.registry.MachineTag;
import org.gbif.api.model.registry.Metadata;
import org.gbif.api.model.registry.Network;
import org.gbif.api.model.registry.Tag;

import org.gbif.api.model.registry.eml.geospatial.BoundingBox;
import org.gbif.api.model.registry.eml.geospatial.GeospatialCoverage;

import org.gbif.api.service.registry.DatasetService;



import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.DatasetType;
import org.gbif.api.vocabulary.Language;
import org.gbif.api.vocabulary.License;
import org.gbif.api.vocabulary.IdentifierType;
import org.gbif.api.vocabulary.MetadataType;
import org.gbif.api.vocabulary.TagName;
import org.gbif.api.vocabulary.TagNamespace;


import org.nomencurator.api.gbif.jackson.mixin.BoundingBoxMixIn;
import org.nomencurator.api.gbif.jackson.mixin.DatasetMixIn;
import org.nomencurator.api.gbif.jackson.mixin.LanguageMixIn;

import lombok.Getter;

/**
 * <CODE>DatasetAPIClient</CODE> provides a set of functions to use GBIF Dataset API.
 * Only GET methods are funtional.
 *
 * @vesion 16 May. 2023
 * @author Nozomi "James" Ytow
 */
@JsonIgnoreProperties({"language"})
public class DatasetAPIClient extends GBIFAPIClient implements DatasetService {

    @Getter
    // URL of dataset API
    protected String datasetURL;

    @Getter
    // part of URL specifying dataset API, which shoud be used to compose <tt>datasetURL</tt> from base URL and version.
    protected String datasetURLEpithet;

    public DatasetAPIClient()
    {
	super();
	setDatasetURLEpithet("dataset");
	// IF codehaus
	/*
	mapper.getDeserializationConfig().addMixInAnnotations(Dataset.class, LicenseMixin.class);
	mapper.getDeserializationConfig().addMixInAnnotations(GeospatialCoverage.class, BoundingBoxMixIn.class);
	*/
	// END IF codehaus

	// IF FasterXML
	// mapper.addMixIn(Dataset.class, LicenseMixin.class);
	mapper.addMixIn(GeospatialCoverage.class, BoundingBoxMixIn.class);
	// mapper.addMixIn(Language.class, LanguageMixIn.class);
	mapper.addMixIn(Dataset.class, DatasetMixIn.class);
	// END IF FasterXML
	
    }

    /*
     * Set verson of supported API
     *
     * @param verson of supported API
     */
    protected void setVersion(String version) {
    	super.setVersion(version);
	setDatasetURL();
    }


    protected void setBaseURL(String baseURL) {
	super.setBaseURL(baseURL);
	setDatasetURL();
    }

    /*
     * Set Dataset API URL using baseURL and datasetURLEpithet
     *
     */
    protected void setDatasetURL() {
	setDatasetURLEpithet(getDatasetURLEpithet());
    }

    /*
     * Set Epithet of DatasetURL to specified URL.  It also sets datasetURL using the Epithet
     *
     * @param epithet to be set
     */
    protected void setDatasetURLEpithet(String epithet) {
	this.datasetURLEpithet = epithet;
	this.datasetURL = getBaseURL() + "/" + version + "/" + epithet;
    }

    protected StringBuffer getResourceURL(UUID datasetKey) {
	StringBuffer resourceURL = new StringBuffer(getDatasetURL());

	if(datasetKey != null) {
	    try {
		resourceURL.append("/").append(URLEncoder.encode(datasetKey.toString(),"utf-8"));
	    }
	    catch (UnsupportedEncodingException e) {
		resourceURL = new StringBuffer(getDatasetURL());
	    }
	}
	return resourceURL;
    }

    public UUID create(@NotNull Dataset entity) { return null; }

    public void delete(@NotNull UUID key) {}

    public Dataset get(@NotNull UUID key) {
	StringBuffer resourceURL =  getResourceURL(key);

	Dataset response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()), 
					new TypeReference<Dataset>() {});
	}
	catch (MalformedURLException e) {
	    e.printStackTrace(System.out);
	    System.out.println("MalformedURLException");
	}
	catch (IOException e) {
	    e.printStackTrace(System.out);
	    System.out.println("IOException");
	}

	return response;
    }

    public Map<UUID, String> getTitles(Collection<UUID> keys)
    {
	Map<UUID, String> titles = new HashMap<UUID, String>(keys == null ? 0 : keys.size());
	if (keys != null && !keys.isEmpty()) {
	    for (UUID key : keys) {
		Dataset dataset = get(key);
		if (dataset != null) {
		    titles.put(key, dataset.getTitle());
		}
	    }
	}

	return titles;
    }

    public List<Dataset> list() {
	PagingResponse<Dataset> response = list((Pageable)null);
	if(response == null)
	    return null;

	List<Dataset> datasets = Lists.newArrayList(response.getResults());
	boolean isInitial = false;
	int limit = -1;
	while(Pager.isEndOfRecords(response)) {
	    if(isInitial) {
		limit = Pager.getMaxLimit(response);
	    }
	    response.setOffset(response.getOffset() + response.getLimit());
	    if(isInitial) {
		response.setLimit(limit);
		isInitial = false;
	    }

	    response = list(response);
	    datasets.addAll(response.getResults());
	}

	return datasets;
    }

    public PagingResponse<Dataset> list(@Nullable Pageable page) {
	StringBuffer resourceURL =  new StringBuffer(getDatasetURL());

	if(page != null)
	    resourceURL.append("?").append(Pager.get(page));

	PagingResponse<Dataset> response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()), 
					new TypeReference<PagingResponse<Dataset>>() {});
	}
	catch (MalformedURLException e) {
	    e.printStackTrace(System.out);
	    System.out.println("MalformedURLException");
	}
	catch (IOException e) {
	    e.printStackTrace(System.out);
	    System.out.println("IOException");
	}
	return response;
    }

    public List<Dataset> search(String query) {
	PagingResponse<Dataset> response = search(query, (Pageable)null);
	if(response == null)
	    return null;

	List<Dataset> datasets = Lists.newArrayList(response.getResults());
	while(Pager.isEndOfRecords(response)) {
	    response.setOffset(response.getOffset() + response.getLimit());
	    response = search(query, response);
	    datasets.addAll(response.getResults());
	}

	return datasets;
    }

    public PagingResponse<Dataset> search(String query, @Nullable Pageable page)
    {
	StringBuffer resourceURL =  new StringBuffer(getDatasetURL());
	if((query != null && query.length() > 0) || page != null) {
	    String connector = ZERO_CONNECTOR;
	    resourceURL.append("?");
	    if(query != null && query.length() > 0) {
		String backup = resourceURL.toString();
		try {
		    resourceURL.append("q=").append(URLEncoder.encode(query,"utf-8"));
		    connector = REST_AMPERSAND;
		} catch (UnsupportedEncodingException e) {
		    resourceURL = new StringBuffer(backup);
		}
	    }
	    if(page != null) {
		    resourceURL.append(connector).append(Pager.get(page));
	    }
	}

	PagingResponse<Dataset> response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()), 
					new TypeReference<PagingResponse<Dataset>>() {});
	}
	catch (MalformedURLException e) {
	    e.printStackTrace(System.out);
	    System.out.println("MalformedURLException");
	}
	catch (IOException e) {
	    e.printStackTrace(System.out);
	    System.out.println("IOException");
	}
	return response;
    }

    public List<Dataset> listByIdentifier(IdentifierType type, String identifier) 
    {
	PagingResponse<Dataset> response = listByIdentifier(type, identifier, (Pageable)null);
	if(response == null)
	    return null;

	List<Dataset> datasets = Lists.newArrayList(response.getResults());
	while(Pager.isEndOfRecords(response)) {
	    response.setOffset(response.getOffset() + response.getLimit());
	    response = listByIdentifier(type, identifier, response);
	    datasets.addAll(response.getResults());
	}

	return datasets;
    }

    public PagingResponse<Dataset> listByIdentifier(IdentifierType type, String identifier, @Nullable Pageable page)
    {
	StringBuffer resourceURL =  new StringBuffer(getDatasetURL());
	if(type != null || (identifier != null && identifier.length() > 0) || page!= null) {
	    String connector = ZERO_CONNECTOR;
	    resourceURL.append("?");
	    String backup = resourceURL.toString();
	    if(type != null) {
		try {
		    resourceURL.append("identifierType=").append(URLEncoder.encode(type.toString(), "utf-8"));
		    connector = REST_AMPERSAND;
		} catch (UnsupportedEncodingException e) {
		    resourceURL = new StringBuffer(backup);
		}
	    }
	    if(identifier != null && identifier.length() > 0) {
		backup = resourceURL.toString();
		try {
		    resourceURL.append(connector).append("identifier=").append(URLEncoder.encode(identifier, "utf-8"));
		    connector = REST_AMPERSAND;
		} catch (UnsupportedEncodingException e) {
		    resourceURL = new StringBuffer(backup);
		}
	    }
	    if(page != null) {
		resourceURL.append(connector).append(Pager.get(page));
	    }
	}

	PagingResponse<Dataset> response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()), 
					new TypeReference<PagingResponse<Dataset>>() {});
	}
	catch (MalformedURLException e) {
	    e.printStackTrace(System.out);
	    System.out.println("MalformedURLException");
	}
	catch (IOException e) {
	    e.printStackTrace(System.out);
	    System.out.println("IOException");
	}
	return response;
    }


    public List<Dataset> listByIdentifier(String identifier) 
    {
	return listByIdentifier((IdentifierType)null, identifier);
    }

    public  PagingResponse<Dataset> listByIdentifier(String identifier, @Nullable Pageable page)
    {
	return listByIdentifier((IdentifierType)null, identifier, page);
    }

    public void update(@NotNull Dataset entity) {}

    public List<Dataset> listConstituents(UUID datasetKey)
    {
	PagingResponse<Dataset> response = listConstituents(datasetKey, null);
	if(response == null)
	    return null;

	List<Dataset> datasets = Lists.newArrayList(response.getResults());
	while(Pager.isEndOfRecords(response)) {
	    response.setOffset(response.getOffset() + response.getLimit());
	    response = listConstituents(datasetKey, response);
	    datasets.addAll(response.getResults());
	}

	return datasets;

    }

    public PagingResponse<Dataset> listConstituents(UUID datasetKey, @Nullable Pageable page)
    {
	StringBuffer resourceURL =  getResourceURL(datasetKey);
	resourceURL.append("/").append("constituents");

	if(page != null)
	    resourceURL.append("?").append(Pager.get(page));

	PagingResponse<Dataset> response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()), 
					new TypeReference<PagingResponse<Dataset>>() {});
	}
	catch (MalformedURLException e) {
	    e.printStackTrace(System.out);
	    System.out.println("MalformedURLException");
	}
	catch (IOException e) {
	    e.printStackTrace(System.out);
	    System.out.println("IOException");
	}
	return response;
    }

    public PagingResponse<Dataset> listConstituents(@Nullable Pageable page)
    {
	return null;
    }

    public PagingResponse<Dataset> listByCountry(Country country, @Nullable DatasetType type, @Nullable Pageable page)
    {
	return null;
    }

    public PagingResponse<Dataset> listByType(DatasetType type, @Nullable Pageable page)
    {
	return null;
    }

    public List<Metadata> listMetadata(UUID datasetKey, @Nullable MetadataType type)
    {
	return null;
    }

    public List<Network> listNetworks(UUID datasetKey)
    {
	return null;
    }

    public Metadata getMetadata(int metadataKey)
    {
	return null;
    }

    public void deleteMetadata(int metadataKey){}

    public Metadata insertMetadata(UUID datasetKey, InputStream document)
    {
	return null;
    }

    public InputStream getMetadataDocument(UUID datasetKey)
    {
	return null;
    }

    public InputStream getMetadataDocument(int metadataKey)
    {
	return null;
    }

    public PagingResponse<Dataset> listDeleted(@Nullable Pageable page)
    {
	return null;
    }

    public PagingResponse<Dataset> listDuplicates(@Nullable Pageable page)
    {
	return null;
    }

    public PagingResponse<Dataset> listDatasetsWithNoEndpoint(@Nullable Pageable page)
    {
	return null;
    }

    public PagingResponse<Dataset> listByDOI(String doi,
                                  @Nullable Pageable page)
    {
	StringBuffer resourceURL =  new StringBuffer(getDatasetURL());
	resourceURL.append("/doi/");	
	if((Objects.nonNull(doi) && doi.length() > 0) || Objects.nonNull(page)) {
	    String connector = ZERO_CONNECTOR;
	    if(Objects.nonNull(doi) && doi.length() > 0) {
		String backup = resourceURL.toString();
		try {
		    resourceURL.append(connector).append(URLEncoder.encode(doi, "utf-8"));
		    connector = REST_AMPERSAND;
		} catch (UnsupportedEncodingException e) {
		    resourceURL = new StringBuffer(backup);
		}
	    }
	    if(page != null) {
		resourceURL.append(connector).append(Pager.get(page));
	    }
	}

	PagingResponse<Dataset> response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()), 
					new TypeReference<PagingResponse<Dataset>>() {});
	}
	catch (MalformedURLException e) {
	    e.printStackTrace(System.out);
	    System.out.println("MalformedURLException");
	}
	catch (IOException e) {
	    e.printStackTrace(System.out);
	    System.out.println("IOException");
	}
	return response;
    }

    public List<Grid> listGrids(UUID datasetKey)
    {
	return null;
    }

    // MachineTagService via NetworkEntityService<Dataset>
    public int addMachineTag(@NotNull UUID targetEntityKey, @NotNull MachineTag machineTag) { return -1;}
    public int addMachineTag(@NotNull UUID targetEntityKey, @NotNull String namespace, @NotNull String name,@NotNull String value) { return -1;}
    public int addMachineTag(@NotNull UUID targetEntityKey, @NotNull TagName tagName, @NotNull String value) { return -1;}
    public void deleteMachineTag(@NotNull UUID targetEntityKey, int machineTagKey){}
    public void deleteMachineTags(@NotNull UUID targetEntityKey, @NotNull String namespace){}
    public void deleteMachineTags(@NotNull UUID targetEntityKey, @NotNull String namespace, @NotNull String name){}
    public void deleteMachineTags(@NotNull UUID targetEntityKey, @NotNull TagName tagName) {}
    public void deleteMachineTags(@NotNull UUID targetEntityKey, @NotNull TagNamespace tagNamespace) {}

    public List<MachineTag> listMachineTags(@NotNull UUID targetEntityKey)
    {
	StringBuffer resourceURL =  getResourceURL(targetEntityKey);
	resourceURL.append("/").append("machinetag");

	List<MachineTag> response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()), 
					new TypeReference<List<MachineTag>>() {});
	}
	catch (MalformedURLException e) {
	    e.printStackTrace(System.out);
	    System.out.println("MalformedURLException");
	}
	catch (IOException e) {
	    e.printStackTrace(System.out);
	    System.out.println("IOException");
	}
	return response;
    }

    public PagingResponse<Dataset>	listByMachineTag(String namespace, String name, String value, Pageable page)
    {
	return null;
    }

    // TagService via NetworkEntityService<Dataset>
    public int addTag(@NotNull UUID targetEntityKey, @NotNull String value) {return -1;}
    public int addTag(@NotNull UUID targetEntityKey, @NotNull Tag tag) {return -1;}
    public void deleteTag(@NotNull UUID taggedEntityKey, int tagKey) {}

    public List<Tag> listTags(@NotNull UUID taggedEntityKey, @Nullable String owner)
    {
	StringBuffer resourceURL =  getResourceURL(taggedEntityKey);
	resourceURL.append("/").append("tag");
	if(owner != null && owner.length() > 0) {
	    String backup = resourceURL.toString();
	    try {
		resourceURL.append("?owner=").append(URLEncoder.encode(owner, "utf-8"));
	    } catch (UnsupportedEncodingException e) {
		resourceURL = new StringBuffer(backup);
	    }
	}

	List<Tag> response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()), 
					new TypeReference<List<Tag>>() {});
	}
	catch (MalformedURLException e) {
	    e.printStackTrace(System.out);
	    System.out.println("MalformedURLException");
	}
	catch (IOException e) {
	    e.printStackTrace(System.out);
	    System.out.println("IOException");
	}
	return response;
    }

    // CommentService
    public int addComment(@NotNull UUID targetEntityKey, @NotNull Comment comment) {return -1;}
    public void deleteComment(@NotNull UUID targetEntityKey, int commentKey) {} 

    public List<Comment> listComments(@NotNull UUID targetEntityKey)
    {
	StringBuffer resourceURL =  getResourceURL(targetEntityKey);
	resourceURL.append("/").append("comment");

	List<Comment> response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()), 
					new TypeReference<List<Comment>>() {});
	}
	catch (MalformedURLException e) {
	    e.printStackTrace(System.out);
	    System.out.println("MalformedURLException");
	}
	catch (IOException e) {
	    e.printStackTrace(System.out);
	    System.out.println("IOException");
	}
	return response;
    }

    // IdentifierService
    public int addIdentifier(@NotNull UUID targetEntityKey, @NotNull Identifier identifier){return -1;}
    public void deleteIdentifier(@NotNull UUID targetEntityKey, int identifierKey){}
    public List<Identifier> listIdentifiers(@NotNull UUID targetEntityKey)
    {
	StringBuffer resourceURL =  getResourceURL(targetEntityKey);
	resourceURL.append("/").append("identifier");

	List<Identifier> response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()), 
					new TypeReference<List<Identifier>>() {});
	}
	catch (MalformedURLException e) {
	    e.printStackTrace(System.out);
	    System.out.println("MalformedURLException");
	}
	catch (IOException e) {
	    e.printStackTrace(System.out);
	    System.out.println("IOException");
	}
	return response;
    }

    // EndpointService
    public int addEndpoint(@NotNull UUID targetEntityKey, @NotNull Endpoint endpoint){return -1;}
    public void deleteEndpoint(@NotNull UUID targetEntityKey, int endpointKey){}
    public List<Endpoint> listEndpoints(@NotNull UUID targetEntityKey)
    {
	StringBuffer resourceURL =  getResourceURL(targetEntityKey);
	resourceURL.append("/").append("endpoint");

	List<Endpoint> response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()), 
					new TypeReference<List<Endpoint>>() {});
	}
	catch (MalformedURLException e) {
	    e.printStackTrace(System.out);
	    System.out.println("MalformedURLException");
	}
	catch (IOException e) {
	    e.printStackTrace(System.out);
	    System.out.println("IOException");
	}
	return response;
    }

    // ContactService
    public int addContact(@NotNull UUID targetEntityKey, @NotNull Contact contact){return -1;}
    public void deleteContact(@NotNull UUID targetEntityKey, int contactKey){}
    public List<Contact> listContacts(@NotNull UUID targetEntityKey)
    {
	StringBuffer resourceURL =  getResourceURL(targetEntityKey);
	resourceURL.append("/").append("contact");

	List<Contact> response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()), 
					new TypeReference<List<Contact>>() {});
	}
	catch (MalformedURLException e) {
	    e.printStackTrace(System.out);
	    System.out.println("MalformedURLException");
	}
	catch (IOException e) {
	    e.printStackTrace(System.out);
	    System.out.println("IOException");
	}
	return response;
    }

    public void updateContact(@NotNull UUID targetEntityKey, @NotNull Contact contact){}
}
