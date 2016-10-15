/*
 * SpeciesAPIClient.java:  a client implementation of GBIF SpeciesAPI
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

package org.nomencurator.api.gbif;

// for Jackson 1.x
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
// or Jackson 2.x
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.core.type.TypeReference;

import com.google.common.collect.Multimap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.gbif.api.model.checklistbank.Description;
import org.gbif.api.model.checklistbank.NameUsage;
import org.gbif.api.model.checklistbank.NameUsageMatch;
import org.gbif.api.model.checklistbank.NameUsageMediaObject;
import org.gbif.api.model.checklistbank.NameUsageMetrics;
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

import org.gbif.api.model.common.LinneanClassification;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;

import org.gbif.api.model.common.search.Facet;
import org.gbif.api.model.common.search.FacetedSearchRequest;
import org.gbif.api.model.common.search.SearchParameter;
import org.gbif.api.model.common.search.SearchRequest;
import org.gbif.api.model.common.search.SearchResponse;

import org.gbif.api.service.checklistbank.NameUsageService;
import org.gbif.api.service.checklistbank.NameUsageMatchingService;
import org.gbif.api.service.checklistbank.NameUsageSearchService;

import org.gbif.api.vocabulary.Habitat;
import org.gbif.api.vocabulary.NameType;
import org.gbif.api.vocabulary.NomenclaturalStatus;
import org.gbif.api.vocabulary.Rank;
import org.gbif.api.vocabulary.TaxonomicStatus;
import org.gbif.api.vocabulary.ThreatStatus;

import org.gbif.checklistbank.ws.util.Constants;

import com.google.common.collect.Lists;

import org.nomencurator.api.gbif.jackson.NameUsageMixIn;

import org.nomencurator.api.gbif.model.checklistbank.Distribution;
import org.nomencurator.api.gbif.model.checklistbank.ParsedName;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import org.codehaus.jackson.map.DeserializationConfig;

@JsonIgnoreProperties(ignoreUnknown = true)

/**
 * <CODE>SpeciesAPI</CODE> implements a client interface to use GBIF SpeciesAPI.
 *
 * @version 	15 Oct. 2016
 * @author 	Nozomi `James' Ytow
 */
public class SpeciesAPIClient
    extends GBIFAPIClient
    implements SpeciesAPI /*NameUsageService, NameUsageMatchingService, NameUsageSearchService*/ {

    private String speciesURL;
    private String speciesURLEpithet;
    private String parserURL;
    private String parserURLEpithet;

    /**
     * Returns <code>HttpURLConnection</code> targeting given URL
     * with Accept-Language property setted to the language code of the locale.
     *
     * @param resouceURL String representing target URL
     * @param locales an array of locales of which language code to be specified as Accept-Language, or unspeficied if null
     * @return connetcion to the target URL
     */
    protected HttpURLConnection getConnection(String resourceURL, @Nullable List<Locale> locales)
	throws IOException, MalformedURLException
    {
	Locale[] localeArray = null;
	if (locales != null &&  !locales.isEmpty())
	    localeArray = locales.toArray(new Locale[locales.size()]);
	return getConnection(resourceURL, localeArray);
    }

    /**
     * Returns <code>HttpURLConnection</code> targeting given URL
     * with Accept-Language property setted to the language code of the locale.
     *
     * @param resouceURL String representing target URL
     * @param locales of which language code to be specified as Accept-Language, or unspeficied if null
     * @return connetcion to the target URL
     */
    protected HttpURLConnection getConnection(String resourceURL, @Nullable Locale ... locales)
	throws IOException, MalformedURLException
    {
	HttpURLConnection connection = null;
	URL url = new URL(resourceURL);
	connection = (HttpURLConnection)url.openConnection();
	if (locales != null && locales.length > 0) {
	    StringBuffer languages = new StringBuffer();
	    for (Locale locale : locales) {
		if (locale != null) {
		    if (languages.length() > 0)
			languages.append(',');
		    languages.append(locale.getLanguage());
		}
	    }
	    connection.setRequestProperty("Accept-Language", languages.toString());
	}
	return connection;
    }

    protected static List<Locale> getList(Locale locale)
    {
	List<Locale> locales = null;
	if (locale != null) {
	    locales = new ArrayList<Locale>(1);
	    locales.add(locale);
	}
	return locales;
    }

    protected static Locale[] getArray (Locale locale)
    {
	Locale[] localeArray = null;
	if (locale != null) {
	    localeArray = new Locale[1];
	    localeArray[0] = locale;
	}
	return localeArray;
    }

    protected static Locale[] getArray (List<Locale> locales)
    {
	Locale[] localeArray = null;
	if (locales != null || !locales.isEmpty())
	    localeArray = locales.toArray(new Locale[locales.size()]);
	return localeArray;
    }

    /**
     * Converts {@code locale} to REST style String representing the language.
     *
     * @param locale {@code Locale} to convert.
     * @return String representing langaues of <tt>locales</tt> in REST style.
     */
    protected String getLanguage(Locale locale)
    {
	if (locale == null)
	    return null;
	String language = locale.getLanguage();
	try {
	    if (language != null && language.length() > 0 )
		language = URLEncoder.encode(language,"utf-8");
	}
	catch (UnsupportedEncodingException e) {
	    language = null;
	}
	return language;
    }

    /**
     * Converts <tt>locales</tt> to REST style String representing languages.
     *
     * @param locales <tt>Locale</tt> or its array to be converted to.
     * @return String representing langaues of <tt>locales</tt> in REST style.
     */
    protected String getLanguages(List<Locale> locales)
    {
	if (locales == null || locales.isEmpty())
	    return null;

	StringBuffer languages = new StringBuffer();

	for (Locale locale : locales) {
	    if (locale != null) {
		String language = getLanguage(locale);
		if (language != null && language.length() > 0 ) {
		    if (languages.length() > 0) {
			languages.append(REST_AMPERSAND);
		    }
		    languages.append("language=").append(language);
		}
	    }
	}
	return languages.toString();
    }

    /**
     * Converts <tt>locales</tt> to REST style String representing languages.
     *
     * @param locales <tt>Locale</tt> or its array to be converted to.
     * @return String representing langaues of <tt>locales</tt> in REST style.
     */
    protected String getLanguages(Locale ... locales)
    {
	if (locales == null || locales.length == 0)
	    return null;

	StringBuffer languages = new StringBuffer();

	for (Locale locale : locales) {
	    if (locale != null) {
		String language = getLanguage(locale);
		if (language != null && language.length() > 0 ) {
		    if (languages.length() > 0) {
			languages.append(REST_AMPERSAND);
		    }
		    languages.append("language=").append(language);
		}
	    }
	}
	return languages.toString();
    }

    /**
     * Set verson of supported API
     *
     * @param verson of supported API
     */
    protected void setVersion(String version) {
	super.setVersion(version);
	setSpeciesURL();
	setParserURL();
    }

    /**
     * Set baseURL of the API to specified URL
     *
     * @param baseURL to be set
     */
    protected void setBaseURL(String baseURL) {
	super.setBaseURL(baseURL);
	setSpeciesURL();
	setParserURL();
    }

    /**
     * Returns URL of SpeciesAPI
     *
     * @return URL of SpeciesAPI
     */
    protected String getSpeciesURL() {
	return speciesURL;
    }

    /**
     * Setup Species API URL using baseURL and speciesURLEpithet
     *
     */
    protected void setSpeciesURL() {
	setSpeciesURLEpithet(getSpeciesURLEpithet() );
    }

    /**
     * Returns epithet of SpeciesAPI URL
     *
     * @param epithet of SpeciesAPI URL
     */
    protected String getSpeciesURLEpithet() {
	return speciesURLEpithet;
    }

    /**
     * Set Epithet of SpeciesURL to specified URL.  It also sets speciesURL using the Epithet
     *
     * @param epithet to be set
     */
    protected void setSpeciesURLEpithet(String epithet) {
	this.speciesURLEpithet = epithet;
	this.speciesURL = baseURL + "/" + version + "/" + epithet;
    }

    /**
     * Returns URL of ParserAPI
     *
     * @return URL of ParserAPI
     */
    protected String getParserURL() {
	return parserURL;
    }

    /**
     * Set Parser API URL using baseURL and parserURLEpithet
     *
     */
    protected void setParserURL() {
	setParserURLEpithet(getParserURLEpithet() );
    }

    /**
     * Returns epithet of ParserAPI URL
     *
     * @param epithet of ParserAPI URL
     */
    protected String getParserURLEpithet() {
	return parserURLEpithet;
    }

    /**
     * Set epithet of ParserURL to specified URL.  It also sets parserURL using the epithet
     *
     * @param epithet to be set
     */
    protected void setParserURLEpithet(String epithet) {
	this.parserURLEpithet = epithet;
	this.parserURL = baseURL + "/" + version + "/" + epithet;
    }

    /**
     * Returns <tt>StringBuffer</tt> to compose resource URL
     * to access record of <tt>usageKey</tt>.
     *
     * @param usageKey ID of target record
     * @return StringBuffer to construct resoruce URL
     */
    protected StringBuffer getResourceURL(int usageKey)
    {
	StringBuffer buffer = new StringBuffer(getSpeciesURL());
	return  buffer.append("/").append(usageKey);
    }

    /**
     * Returns <tt>StringBuffer</tt> to compose resource URL
     * to access information speified by <tt>modifier</tt> on record of <tt>usageKey</tt>.
     *
     * @param usageKey ID of target record
     * @param modifier <tt>String</tt> specifies type of information
     * @return StringBuffer to construct resoruce URL
     */
    protected StringBuffer getResourceURL(int usageKey, @Nullable String modifier) {
	StringBuffer resourceURL = getResourceURL(usageKey);
	if (modifier != null && !modifier.isEmpty()) {
	  try {
	      modifier = URLEncoder.encode(modifier, "utf-8");
	      if (modifier != null && modifier.length() > 0)
		  resourceURL.append("/").append(modifier);
	  }
	  catch (UnsupportedEncodingException e) {
	  }
	}
	return resourceURL;
    }

    /**
     * Constructs client object to use GBIF SpeciesAPI.
     */
    public SpeciesAPIClient () {
	super();
	setSpeciesURLEpithet(Constants.SPECIES_PATH);
	setParserURLEpithet("parser");
	 mapper.getDeserializationConfig().addMixInAnnotations(NameUsage.class, NameUsageMixIn.class);
	// just in case
	mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public NameUsage get(int usageKey)
	throws MalformedURLException, IOException
    {
	return get(usageKey, (Locale[])null);
    }

    @Override
    public NameUsage get(int usageKey, Locale locale)
    {
	NameUsage nameUsage = null;
	try {
	    nameUsage = get(usageKey, getArray(locale));
	}
	catch (MalformedURLException e) {
	}
	catch (IOException e) {
	}
	return nameUsage;
    }


    public NameUsage get(int usageKey, @Nullable Locale ... locales)
	throws MalformedURLException, IOException
    {
	NameUsage nameUsage = null;
	StringBuffer resourceURL = new StringBuffer(getResourceURL(usageKey));
	String languages = getLanguages(locales);
	if(languages != null && languages.length() > 0) {
	    resourceURL.append("?").append(languages);
	}

	HttpURLConnection connection = null;
	MalformedURLException mx = null;
	IOException iox = null;
	try {
	    connection = getConnection(resourceURL.toString(), locales);
	    nameUsage = mapper.readValue(connection.getInputStream(), NameUsage.class);
	}
	catch (MalformedURLException e) {
	    mx = e;
	}
	catch (IOException e) {
	    iox = e;
	}
	finally {
	    if (connection != null)
		connection.disconnect();
	    if (mx != null)
		throw mx;
	    if (iox != null)
		throw iox;
	}

	return nameUsage;
    }

    public ParsedName getParsedName(int usageKey) {
	ParsedName parsedName = null;
	try {
	    parsedName = mapper.readValue(new URL(getResourceURL(usageKey, "name").toString()), ParsedName.class);
	}
	catch (MalformedURLException e) {
	}
	catch (IOException e) {
	}
	return parsedName;
    }

    public NameUsageMetrics getMetrics(int usageKey)
    {
	NameUsageMetrics metrics = null;
	try {
	    metrics = mapper.readValue(new URL(getResourceURL(usageKey, "metrics").toString()), NameUsageMetrics.class);
	}
	catch (MalformedURLException e) {
	}
	catch (IOException e) {
	}
	return metrics;
    }

    public VerbatimNameUsage getVerbatim(int usageKey)
    {
	VerbatimNameUsage vnc = null;
	try {
	    vnc = mapper.readValue(new URL(getResourceURL(usageKey, "verbatim").toString()), VerbatimNameUsage.class);
	}
	catch (MalformedURLException e) {
	}
	catch (IOException e) {
	}
	return vnc;
    }

    public List<NameUsage> list(@Nullable UUID datasetKey, @Nullable String sourceId, @Nullable Locale ... locales)
	throws IOException
    {
	PagingResponse<NameUsage> response = list(null, datasetKey,sourceId, locales);
	if (response == null)
	    return null;
	List<NameUsage> nameUsages = Lists.newArrayList(response.getResults());
	boolean isInitial = true;
	int limit = -1;
	while(!Pager.isEndOfRecords(response)) {
	    if(isInitial) {
		limit = Pager.getMaxLimit(response);
	    }
	    response.setOffset(response.getOffset() + response.getLimit());
	    if(isInitial) {
		response.setLimit(limit);
		isInitial = false;
	    }
	    response = list(response, datasetKey,sourceId, locales);
	    nameUsages.addAll(response.getResults());
	}
	return nameUsages;
    }

    public PagingResponse<NameUsage> list(Locale locale, @Nullable UUID datasetKey, @Nullable String sourceId, @Nullable Pageable page)
    {
	PagingResponse<NameUsage> response = null;
	try {
	    response = list(page, datasetKey, sourceId, locale);
	}
	catch (IOException e) {
	}
	return response;
    }


    public PagingResponse<NameUsage> list(@Nullable Pageable page, @Nullable UUID datasetKey, @Nullable String sourceId,
					  @Nullable Locale ... locales)
	throws IOException
    {
	StringBuffer resourceURL =  new StringBuffer(speciesURL);
	if ((locales != null && locales.length > 0) || datasetKey != null || sourceId != null || page != null)  {
	    String connector = "";
	    resourceURL.append("?");
	    if(locales != null && locales.length > 0) {
		String languages = getLanguages(locales);
		if (languages != null && languages.length() > 0) {
		    resourceURL.append(languages);
		    connector = REST_AMPERSAND;
		}
	    }
	    if(datasetKey != null) {
		try {
		    String uuid = URLEncoder.encode(datasetKey.toString(),"utf-8");
		    if (uuid != null && uuid.length() > 0) {
			resourceURL.append(connector).append("datasetKey=").append(uuid);
			if (connector.length() == 0) {
			    connector = REST_AMPERSAND;
			}
		    }
		}
		catch (UnsupportedEncodingException e) {
		}
	    }
	    if(sourceId != null) {
		try {
		    String id = URLEncoder.encode(sourceId,"utf-8");
		    if (id != null && id.length() > 0) {
			resourceURL.append(connector).append("sourceId=").append(id);
			if (connector.length() == 0) {
			    connector = REST_AMPERSAND;
			}
		    }
		}
		catch (UnsupportedEncodingException e) {
		}
	    }
	    if(page != null) {
		resourceURL.append(connector).append(Pager.get(page));
	    }

	}
	PagingResponse<NameUsage> response = null;
	try {
	    HttpURLConnection connection = getConnection(resourceURL.toString(), locales);
	    response = mapper.readValue(connection.getInputStream(),
					new TypeReference<PagingResponse<NameUsage>>() {});
	    connection.disconnect();
	}
	catch (MalformedURLException e) {
	}
	catch (IOException e) {
	}
	return response;
    }

    public List<NameUsage> listByCanonicalName(String canonicalName, @Nullable List<Locale> locales, @Nullable UUID ... datasetKey)
	throws IOException
    {
	PagingResponse<NameUsage> response = listByCanonicalName(null, canonicalName, locales, datasetKey);
	if (response == null)
	    return null;
	List<NameUsage> nameUsages = Lists.newArrayList(response.getResults());
	boolean isInitial = true;
	int limit = -1;
	while(!Pager.isEndOfRecords(response)) {
	    if (isInitial) {
		limit = Pager.getMaxLimit(response);
	    }
	    response.setOffset(response.getOffset() + response.getLimit());
	    if (isInitial) {
		response.setLimit(limit);
		isInitial = false;
	    }
	    response = listByCanonicalName(response, canonicalName, locales, datasetKey);
	    nameUsages.addAll(response.getResults());
	}

	return nameUsages;
    }

    public PagingResponse<NameUsage> listByCanonicalName(Locale locale, String canonicalName, @Nullable Pageable page,
						       @Nullable UUID ... datasetKey)
    {
	PagingResponse<NameUsage> response = null;
	try {
	    response = listByCanonicalName(page, canonicalName, getList(locale), datasetKey);
	}
	catch (IOException e) {
	}

	return response;
    }


    public PagingResponse<NameUsage> listByCanonicalName(
							 @Nullable Pageable page,
							 String canonicalName,
							 @Nullable List<Locale> locales,
							 @Nullable UUID ... datasetKey
							 )
      throws IOException
    {
	StringBuffer resourceURL =  new StringBuffer(speciesURL);
	if ((locales != null  && locales.size() > 0) || canonicalName != null || page != null || datasetKey != null)  {
	    String connector = "";
	    resourceURL.append("?");
	    if(locales != null && locales.size() > 0) {
		String languages = getLanguages(locales.toArray(new Locale[locales.size()]));
		if(languages != null && languages.length() > 0) {
		    resourceURL.append("language=").append(languages);
		    connector = REST_AMPERSAND;
		}
	    }
	    if(canonicalName != null) {
		try {
		    String name = URLEncoder.encode(canonicalName,"utf-8");
		    if (name != null && name.length() > 0) {
			resourceURL.append(connector).append("name=").append(name);
			if (connector.length() == 0) {
			    connector = REST_AMPERSAND;
			}
		    }
		}
		catch (UnsupportedEncodingException e) {
		}
	  }
	  if(page != null) {
	      resourceURL.append(connector).append(Pager.get(page));
	      if (connector.length() == 0) {
		  connector = REST_AMPERSAND;
	      }
	  }
	  if(datasetKey != null) {
	      for (UUID key : datasetKey) {
		  try {
		      String uuid = URLEncoder.encode(key.toString(),"utf-8");
		      if (uuid != null && uuid.length() > 0) {
			  resourceURL.append(connector).append("datasetKey=").append(uuid);
			  if (connector.length() == 0) {
			      connector = REST_AMPERSAND;
			  }
		      }
		  }
		  catch (UnsupportedEncodingException e) {
		  }
	      }
	  }
      }

      PagingResponse<NameUsage> response = null;
      HttpURLConnection connection = getConnection(resourceURL.toString(), locales);
      response = mapper.readValue(connection.getInputStream(),
				  new TypeReference<PagingResponse<NameUsage>>() {});
      connection.disconnect();

      return response;
  }

    @Override
    public List<NameUsage> listChildren(int parentKey, @Nullable Locale ... locales) {
	PagingResponse<NameUsage> response = listChildren(null, parentKey, locales);
	if (response == null) {
	    return null;
	}
	List<NameUsage> result = Lists.newArrayList(response.getResults());
	boolean isInitial = true;
	int limit = -1;
	while(!Pager.isEndOfRecords(response)) {
	    if(isInitial) {
		limit = Pager.getMaxLimit(response);
	    }
	    response.setOffset(response.getOffset() + response.getLimit());
	    if(isInitial) {
		response.setLimit(limit);
		isInitial = false;
	    }
	    response = listChildren(response, parentKey, locales);
	    result.addAll(response.getResults());
	}
	return result;
    }

    public PagingResponse<NameUsage> listChildren(int parentKey, Locale locale, @Nullable Pageable page)
    {
	return listChildren(page, parentKey, locale);
    }


    public PagingResponse<NameUsage> listChildren(@Nullable Pageable page, int parentKey, @Nullable Locale ... locales)
    {
      StringBuffer resourceURL = getResourceURL(parentKey, "children");
      if ((locales != null && locales.length > 0) ||  page != null)  {
	  String connector = "";
	  resourceURL.append("?");
	  if(locales != null && locales.length > 0) {
	      String languages = getLanguages(locales);
	      if (languages != null && languages.length() > 0) {
		  resourceURL.append(languages);
		  connector = REST_AMPERSAND;
	      }
	  }
	  if(page != null) {
	      resourceURL.append(connector).append(Pager.get(page));
	  }
      }

      PagingResponse<NameUsage> response = null;
      try {
	  HttpURLConnection connection = getConnection(resourceURL.toString(), locales);
	  response = mapper.readValue(connection.getInputStream(),
				      new TypeReference<PagingResponse<NameUsage>>() {});
	  connection.disconnect();
      }
      catch (MalformedURLException e) {
      }
      catch (IOException e) {
      }

      return response;
  }

    public List<NameUsage> listParents(int usageKey, Locale locale) {
	return listParents(usageKey, getArray(locale));
    }

    public List<NameUsage> listParents(int usageKey, Locale ... locales)
    {
	StringBuffer resourceURL = getResourceURL(usageKey, "parents");
	if(locales != null && locales.length > 0) {
	    String languages = getLanguages(locales);
	    if (languages != null && languages.length() > 0) {
		resourceURL.append("?").append(languages);
	    }
	}

	List<NameUsage> list = null;
	try {
	    HttpURLConnection connection = getConnection(resourceURL.toString(), locales);
	    list = mapper.readValue(connection.getInputStream(),
				    new TypeReference<List<NameUsage>>() {});
	    connection.disconnect();
	}
	catch (MalformedURLException e) {
	}
	catch (IOException e) {
	}
	return list;
    }

    protected List<Locale> getLocaleList(Locale locale)
    {
	List<Locale> locales = locale == null ? null : new ArrayList<Locale>(1);
	if(locales != null)
	    locales.add(locale);
	return locales;
    }

    public List<NameUsage> listRelated(int nubKey, @Nullable Locale locale, @Nullable UUID... datasetKey)
    {
	return listRelated(nubKey, getLanguage(locale), getLocaleList(locale), datasetKey);
    }

    public List<NameUsage> listRelated(int nubKey, @Nullable List<Locale> locales, @Nullable UUID... datasetKey)
    {
	return listRelated(nubKey, getLanguages(locales), locales, datasetKey);
    }

    protected List<NameUsage> listRelated(int nubKey, @Nullable String languages, @Nullable List<Locale> locales, @Nullable UUID... datasetKey)
    {
	PagingResponse<NameUsage> response = listRelated(nubKey, languages, locales, null, datasetKey);
	if (response == null)
	    return null;
	List<NameUsage> result = Lists.newArrayList(response.getResults());
	boolean isInitial = true;
	int limit = -1;
	while(!Pager.isEndOfRecords(response)) {
	    if(isInitial) {
		limit = Pager.getMaxLimit(response);
	    }
	    response.setOffset(response.getOffset() + response.getLimit());
	    if(isInitial) {
		response.setLimit(limit);
		isInitial = false;
	    }
	    response = listRelated(nubKey, languages, locales, response, datasetKey);
	    result.addAll(response.getResults());
	}
	return result;
    }

    public PagingResponse<NameUsage> listRelated(int nubKey, @Nullable Locale locale, @Nullable Pageable page, @Nullable UUID... datasetKey)
    {
	return listRelated(nubKey, getLanguage(locale), getLocaleList(locale), page, datasetKey);
    }

    public PagingResponse<NameUsage> listRelated(int nubKey, @Nullable List<Locale> locales, @Nullable Pageable page, @Nullable UUID... datasetKey)
    {
	return listRelated(nubKey, getLanguages(locales), locales, page, datasetKey);
    }

    protected PagingResponse<NameUsage> listRelated(int nubKey, @Nullable String languages, @Nullable List<Locale> locales, @Nullable Pageable page, @Nullable UUID... datasetKey)
    {
	StringBuffer resourceURL = getResourceURL(nubKey, "related");
	if ((languages != null && languages.length() > 0) ||  datasetKey != null)  {
	    String connector = "";
	    resourceURL.append("?");
	    if (languages != null && languages.length() >0) {
		resourceURL.append(languages);
		connector = REST_AMPERSAND;
	    }
	    if(datasetKey != null) {
		for (UUID key : datasetKey) {
		    try {
			String uuid = URLEncoder.encode(key.toString(),"utf-8");
			if (uuid != null && uuid.length() > 0) {
			    resourceURL.append(connector).append("datasetKey=").append(uuid);
			    if (connector.length() == 0) {
				connector = REST_AMPERSAND;
			    }
			}
		    }
		    catch (UnsupportedEncodingException e) {
		    }
		}
	    }
	}

	PagingResponse<NameUsage> response = null;
	try {
	    HttpURLConnection connection = getConnection(resourceURL.toString(), locales);
	    response = mapper.readValue(connection.getInputStream(),
					new TypeReference<List<NameUsage>>() {});
	    connection.disconnect();
	}
	catch (MalformedURLException e) {
	}
	catch (IOException e) {
	}
	return response;
    }

    @Override
    public List<NameUsage> listRoot(UUID datasetKey, @Nullable Locale ... locales) {
	PagingResponse<NameUsage> response = listRoot(null, datasetKey, locales);
	if (response == null)
	    return null;
	List<NameUsage> result = Lists.newArrayList(response.getResults());
	boolean isInitial = true;
	int limit = -1;
	while(!Pager.isEndOfRecords(response)) {
	    if(isInitial) {
		limit = Pager.getMaxLimit(response);
	    }
	    response.setOffset(response.getOffset() + response.getLimit());
	    if(isInitial) {
		response.setLimit(limit);
		isInitial = false;
	    }
	    response = listRoot(response, datasetKey, locales);
	    result.addAll(response.getResults());
	}
	return result;
    }

    public PagingResponse<NameUsage> listRoot(UUID datasetKey, Locale  locale, @Nullable Pageable page) {
	return listRoot(page, datasetKey, locale);
    }

    public PagingResponse<NameUsage> listRoot(@Nullable Pageable page, UUID datasetKey, @Nullable Locale ... locales)
    {
	StringBuffer resourceURL = new StringBuffer(speciesURL);
	resourceURL.append("/root/").append(datasetKey);
	if ((locales != null && locales.length > 0) ||  page != null)  {
	    String connector = "";
	    resourceURL.append("?");
	    if (locales != null && locales.length > 0) {
		String languages = getLanguages(locales);
		if (languages != null && languages.length() > 0) {
		    resourceURL.append(languages);
		    connector = REST_AMPERSAND;
		}
	    }
	    if(page != null) {
		resourceURL.append(connector).append(Pager.get(page));
	    }
	}

	PagingResponse<NameUsage> response = null;
	try {
	    HttpURLConnection connection = getConnection(resourceURL.toString(), locales);
	    response = mapper.readValue(connection.getInputStream(),
					new TypeReference<PagingResponse<NameUsage>>() {});
	    connection.disconnect();
	}
	catch (MalformedURLException e) {
	}
	catch (IOException e) {
	}
	return response;
    }

    public List<NameUsage> listSynonyms(int usageKey, @Nullable Locale ... locales) {
	PagingResponse<NameUsage> response = listSynonyms(null, usageKey,  locales);
	if (response == null) {
	    return null;
	}
	List<NameUsage> result = Lists.newArrayList(response.getResults());
	boolean isInitial = true;
	int limit = -1;
	while(!Pager.isEndOfRecords(response)) {
	    if(isInitial) {
		limit = Pager.getMaxLimit(response);
	    }
	    response.setOffset(response.getOffset() + response.getLimit());
	    if(isInitial) {
		response.setLimit(limit);
		isInitial = false;
	    }
	    response = listSynonyms(response, usageKey,  locales);
	    result.addAll(response.getResults());
	}
	return result;
    }

    public PagingResponse<NameUsage> listSynonyms(int usageKey, Locale locale, @Nullable Pageable page)
    {
	return listSynonyms(page, usageKey, locale);
    }

    public PagingResponse<NameUsage> listSynonyms(@Nullable Pageable page, int usageKey, @Nullable Locale ... locales)
    {
	StringBuffer resourceURL = getResourceURL(usageKey, "synonyms");
	if ((locales != null && locales.length > 0) ||  page != null)  {
	    String connector = "";
	    resourceURL.append("?");
	    if (locales != null && locales.length > 0) {
		String languages = getLanguages(locales);
		if (languages != null && languages.length() > 0) {
		    resourceURL.append(languages);
		    connector = REST_AMPERSAND;
		}
	    }
	    if(page != null) {
		resourceURL.append(connector).append(Pager.get(page));
	    }
	}

	PagingResponse<NameUsage> response = null;
	try {
	    HttpURLConnection connection = getConnection(resourceURL.toString(), locales);
	    response = mapper.readValue(connection.getInputStream(),
					new TypeReference<PagingResponse<NameUsage>>() {});
	    connection.disconnect();
	}
	catch (MalformedURLException e) {
	}
	catch (IOException e) {
	}
	return response;
    }

    protected boolean appendClassification(StringBuffer buffer, String connector, String rankName, String name)
    {
	boolean appended = false;
	try {
	    if (name != null && name.length() > 0)
		name = URLEncoder.encode(name,"utf-8");
	    if (name != null && name.length() > 0) {
		buffer.append(connector).append(rankName).append("=").append(name);
		appended = true;
	    }
	}
	catch (UnsupportedEncodingException e) {
	}
	return appended;
    }

  public NameUsageMatch match(String scientificName, @Nullable Rank rank, @Nullable LinneanClassification classification,
		       boolean strict, boolean verbose)
    {
	StringBuffer resourceURL = new StringBuffer(speciesURL);
	resourceURL.append("/match");
	String connector = REST_AMPERSAND;
	resourceURL.append("?");
	try {
	    String name = URLEncoder.encode(scientificName,"utf-8");
	    if (name != null && name.length() > 0) {
		resourceURL.append("name=").append(name);
	    }
	}
	catch (UnsupportedEncodingException e) {
	}
	if(strict) {
	    resourceURL.append(connector).append("strict=").append(Boolean.toString(strict));
	}

	if(verbose) {
	    resourceURL.append(connector).append("verbose=").append(Boolean.toString(verbose));
	}

	if (rank != null) {
	    try {
		String rankName = URLEncoder.encode(rank.name(),"utf-8");
		if (rankName != null && rankName.length() > 0) {
		    resourceURL.append(connector).append("rank=").append(rankName);
		}
	    }
	    catch (UnsupportedEncodingException e) {
	    }
	}

	if (classification != null) {
	    appendClassification(resourceURL, connector, "kingdom", classification.getKingdom());
	    appendClassification(resourceURL, connector, "phylum", classification.getPhylum());
	    appendClassification(resourceURL, connector, "class", classification.getClazz());
	    appendClassification(resourceURL, connector, "order",classification.getOrder());
	    appendClassification(resourceURL, connector, "family", classification.getFamily());
	    appendClassification(resourceURL, connector, "genus", classification.getGenus ());
	    appendClassification(resourceURL, connector, "subgenus", classification.getSubgenus ());
	}

	NameUsageMatch response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()),  NameUsageMatch.class);
	}
	catch (MalformedURLException e) {
	}
	catch (IOException e) {
	}
	return response;
    }

    public List<NameUsageSearchResult> fullTextSearch(String name, Rank rank, Integer higherTaxonKey,
						      TaxonomicStatus status, Boolean isExtinct, Habitat habitat, ThreatStatus threat,
						      NameType nameType, NomenclaturalStatus nomenclaturalStatus, Boolean highlight,
						      Set<NameUsageSearchParameter> facet, Integer facetMinCount, Boolean facetMultiselect,
						      UUID ... datasetKey)
    {
	NameUsageSearchRequest searchRequest = new NameUsageSearchRequest();
	searchRequest.setQ(name);
	if(rank != null)
	    searchRequest.addRankFilter(rank);
	if(status != null)
	    searchRequest.addTaxonomicStatusFilter(status);
	if(higherTaxonKey != null)
	    searchRequest.addHigherTaxonFilter(higherTaxonKey);
	if(isExtinct != null)
	    searchRequest.addExtinctFilter(isExtinct.booleanValue());
	if(habitat != null)
	    searchRequest.addHabitatFilter(habitat);
	if(threat != null)
	    searchRequest.addThreatStatusFilter(threat);
	if(nameType != null)
	    searchRequest.addParameter(NameUsageSearchParameter.NAME_TYPE, nameType);
	if(nomenclaturalStatus != null)
	    searchRequest.addTaxonomicStatusFilter(nomenclaturalStatus);
	if(facet != null)
	    searchRequest.setFacets(facet);
	if(facetMinCount != null)
	    searchRequest.setFacetMinCount(facetMinCount);
	if(facetMultiselect != null)
	    searchRequest.setMultiSelectFacets(facetMultiselect.booleanValue());

	if(datasetKey != null) {
	    for (UUID key : datasetKey) {
		searchRequest.addChecklistFilter(key);
	    }
	}

	return fullTextSearch(searchRequest);
    }

    public List<NameUsageSearchResult> fullTextSearch(NameUsageSearchRequest searchRequest) {
	List<NameUsageSearchResult> result = Lists.newArrayList();
	SearchResponse<NameUsageSearchResult, NameUsageSearchParameter> response = null;

	boolean isInitial = true;
	do {
	    response = search(searchRequest);
	    if(response == null)
		break;
	    result.addAll(response.getResults());
	    int limit = response.getLimit();
	    if (isInitial) {
		limit = Pager.getMaxLimit(response);
		isInitial = false;
	    }
	    searchRequest.setLimit(limit);
	    searchRequest.setOffset(response.getOffset() + limit);
	}
	while(!Pager.isEndOfRecords(response));

	return result;
    }


    @Override
    public SearchResponse<NameUsageSearchResult, NameUsageSearchParameter> search(NameUsageSearchRequest searchRequest)
    {
	// fixme
	// mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	StringBuffer resourceURL = new StringBuffer(speciesURL);
	resourceURL.append( "/search");
	try {
	    if (searchRequest != null) {
		String request = URLEncoder.encode(searchRequest.getQ(), "utf-8");
		if (request != null && request.length() > 0)
		    resourceURL.append("?q=").append(request);
	    }
	}
	catch (UnsupportedEncodingException e) {
	}
	String connector = REST_AMPERSAND;

	Multimap<NameUsageSearchParameter, String> parameters = searchRequest.getParameters();

	StringBuffer parameter = new StringBuffer();
	Collection<String> values = parameters.get(NameUsageSearchParameter.DATASET_KEY);
	String value = null;
	if (values != null && !values.isEmpty()) {
	    value = getParameter("datasetKey", values);
	    if(value != null && value.length() > 0) {
		parameter.append(connector).append(value);
	    }
	}
	values = parameters.get(NameUsageSearchParameter.RANK);
	if (values != null && !values.isEmpty()) {
	    value = getParameter("rank", values);
	    if(value != null && value.length() > 0) {
		parameter.append(connector).append(value);
	    }
	}
	values = parameters.get(NameUsageSearchParameter.HIGHERTAXON_KEY);
	if (values != null && !values.isEmpty()) {
	    value = getParameter("highertaxonKey", values);
	    if(value != null && value.length() > 0) {
		parameter.append(connector).append(value);
	    }
	}
	values = parameters.get(NameUsageSearchParameter.STATUS);
	if (values != null && !values.isEmpty()) {
	    value = getParameter("status", values);
	    if(value != null && value.length() > 0) {
		parameter.append(connector).append(value);
	    }
	}
	values = parameters.get(NameUsageSearchParameter.HABITAT);
	if (values != null && !values.isEmpty()) {
	    value = getParameter("habitat", values);
	    if(value != null && value.length() > 0) {
		parameter.append(connector).append(value);
	    }
	}
	values = parameters.get(NameUsageSearchParameter.THREAT);
	if (values != null && !values.isEmpty()) {
	    value = getParameter("threat", values);
	    if(value != null && value.length() > 0) {
		parameter.append(connector).append(value);
	    }
	}
	values = parameters.get(NameUsageSearchParameter.NOMENCLATURAL_STATUS);
	if (values != null && !values.isEmpty()) {
	    value = getParameter("nomenclaturalStatus", values);
	    if(value != null && value.length() > 0) {
		parameter.append(connector).append(value);
	    }
	}
	values = parameters.get(NameUsageSearchParameter.NAME_TYPE);
	if (values != null && !values.isEmpty()) {
	    value = getParameter("nameType", values);
	    if(value != null && value.length() > 0) {
		parameter.append(connector).append(value);
	    }
	}


	if(searchRequest.isHighlight()) {
	    parameter.append(connector).append("hi=true");
	}
	Set<NameUsageSearchParameter> facets = searchRequest.getFacets();
	if(facets != null && facets.size() > 0) {
	    /*
	      if(searchRequest.isFacetsOnly()) {
	      parameter.append(connector).append("facet_only=true");
	      }
	    */
	    if(searchRequest.isMultiSelectFacets()) {
		parameter.append(connector).append("facet_multiselect=true");
	    }
	    Integer facetMinCount = searchRequest.getFacetMinCount();
	    if(facetMinCount != null) {
		parameter.append(connector).append("facet_mincount=").append(facetMinCount.toString());
	    }
	    for (NameUsageSearchParameter facet:facets) {
		parameter.append(connector).append("facet=").append(facet.name());
	    }
	}

	if(parameter.length() > 0) {
	    resourceURL.append(parameter);
	}

	resourceURL.append(connector).append(Pager.get(searchRequest));

	SearchResponse<NameUsageSearchResult, NameUsageSearchParameter> response = null;
	try {
	    response = mapper.readValue(new URL(resourceURL.toString()),
					new TypeReference<SearchResponse<NameUsageSearchResult, NameUsageSearchParameter>>() {});
	    //	  			      new TypeReference<SearchResponse<org.nomencurator.api.gbif.model.checklistbank.search.NameUsageSearchResult, NameUsageSearchParameter>>() {});
	}
	catch (MalformedURLException e) {
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
	return response;
    }

    public List<NameUsageSuggestResult> suggest(String name, Rank rank, @Nullable UUID ... datasetKey)
    {
	NameUsageSuggestRequest suggestRequest = new NameUsageSuggestRequest();
	suggestRequest.setQ(name);
	if(rank != null)
	    suggestRequest.addParameter(NameUsageSearchParameter.RANK, rank.name());
	if(datasetKey != null) {
	    for (UUID key : datasetKey) {
		suggestRequest.addParameter(NameUsageSearchParameter.DATASET_KEY, key.toString());
	    }
	}

	return suggest(suggestRequest);
    }

    //    public List<NameUsage> suggest(NameUsageSuggestRequest suggestRequest) {
    public List<NameUsageSuggestResult> suggest(NameUsageSuggestRequest suggestRequest)
    {
	// fixme
	// mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	StringBuffer resourceURL = new StringBuffer(speciesURL);
	resourceURL.append("/suggest");
	try {
	    String suggestion = URLEncoder.encode(suggestRequest.getQ(),"utf-8");
	    if (suggestion != null && suggestion.length() > 0)
		resourceURL.append("?q=").append(suggestion);
	}
	catch (UnsupportedEncodingException e) {
      }

      String connector = REST_AMPERSAND;

      Multimap<NameUsageSearchParameter, String> parameters = suggestRequest.getParameters();

      StringBuffer parameter = new StringBuffer();
      Collection<String> values = parameters.get(NameUsageSearchParameter.DATASET_KEY);
      String value = null;
      if (values != null && !values.isEmpty()) {
	  value = getParameter("datasetKey", values);
	  if(value != null && value.length() > 0) {
	      parameter.append(connector).append(value);
	  }
      }
      values = parameters.get(NameUsageSearchParameter.RANK);
      if (values != null && !values.isEmpty()) {
	  value = getParameter("rank", values);
	  if(value != null && value.length() > 0) {
	      parameter.append(connector).append(value);
	  }
      }

      if(parameter.length() > 0) {
	  resourceURL.append(parameter);
      }

      List<NameUsageSuggestResult> list = null;
      try {
	  list = mapper.readValue(new URL(resourceURL.toString()),
				      new TypeReference<List<NameUsageSuggestResult>>() {});
				  //				      new TypeReference<List<org.nomencurator.api.gbif.model.checklistbank.search.NameUsageSearchResult>>() {});
      }
      catch (MalformedURLException e) {
      }
      catch (IOException e) {
	  e.printStackTrace();
      }
      return list;
    }

    public List<ParsedName> parse(String name) {
	if (name == null || name.length() == 0)
	    return null;

	String[] names = new String[1];
	names[0] = name;
	return parse (names);
    }

    /**
      * Returns parsed names using HTTP GET
      *
      * @param names names to be parsed
      * @return list of pased names
      */
    public List<ParsedName> parse(String[ ] names) {
	if (names == null || names.length == 0)
	    return null;

	StringBuffer resourceURL = new StringBuffer(parserURL);
	resourceURL.append("/name");
	String connector = "?";
	for (String name : names) {
	    try {
		if(name != null && name.length() > 0) {
		    name = URLEncoder.encode(name,"utf-8");
		    if(name != null && name.length() > 0) {
			resourceURL.append(connector).append("name=").append(name);
			connector = REST_AMPERSAND;
		    }
		}
	    }
	    catch (UnsupportedEncodingException e) {
	    }
	}

	List<ParsedName> list = null;
	try {
	    list = mapper.readValue(new URL(resourceURL.toString()),
				    new TypeReference<List<ParsedName>>() {});
	}
	catch (MalformedURLException e) {
	}
	catch (IOException e) {
	}

	return list;
    }

    /**
      * Returns parsed names of given content type using HTTP POST
      *
      * @param contentType content type, one of JSON, TEXT, FORM_DATA
      * @param names names to be parsed
      * @return list of pased names
      */
    public List<ParsedName> parse(String contentType, String names) {
	if (contentType == null || contentType.length() == 0 ||
	    names == null || names .length() == 0 )
	    return null;

	List<ParsedName> list = null;
	HttpURLConnection connection = null;
	try {
	    URL url = new URL(parserURL +  "/name");
	    connection = (HttpURLConnection)url.openConnection();
	    connection.setDoOutput(true);
	    connection.setUseCaches(false);
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("Content-Type", contentType);
	    PrintWriter writer = new PrintWriter(connection.getOutputStream());
	    writer.print(names);
	    writer.close();
	    list = mapper.readValue(connection.getInputStream(),
				    new TypeReference<List<ParsedName>>() {});
	    connection.disconnect();
	}
	catch (ProtocolException e) {
	}
	catch (MalformedURLException e) {
	}
	catch (IOException e) {
	}
	return list;
    }

    @Override
    public List<Description> getDescripitons(int usageKey) {
	PagingResponse<Description> response = getDescripitons(usageKey, null);
	if (response == null)
	    return null;
	List<Description> result = Lists.newArrayList(response.getResults());
	boolean isInitial = true;
	int limit = -1;
	while(!Pager.isEndOfRecords(response)) {
	    if(isInitial) {
		limit = Pager.getMaxLimit(response);
	    }
	    response.setOffset(response.getOffset() + response.getLimit());
	    if(isInitial) {
		response.setLimit(limit);
		isInitial = false;
	    }
	    response = getDescripitons(usageKey, response);
	    result.addAll(response.getResults());
	}
	return result;
    }

    /**
     * Returns all descriptions for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return description pages
     */
    public PagingResponse<Description> getDescripitons(int usageKey, @Nullable Pageable page) {
      StringBuffer resourceURL = getResourceURL(usageKey, "descriptions");
      if (page != null)  {
	  resourceURL.append("?").append(Pager.get(page));
      }

      PagingResponse<Description> response = null;
      try {
	  response = mapper.readValue(new URL(resourceURL.toString()),
				      new TypeReference<PagingResponse<Description>>() {});
      }
      catch (MalformedURLException e) {
      }
      catch (IOException e) {
      }
      return response;
    }

    @Override
    public List<Distribution> getDistributions(int usageKey) {
	PagingResponse<Distribution> response = getDistributions(usageKey, null);
	if (response == null)
	    return null;
	List<Distribution> result = Lists.newArrayList(response.getResults());
	boolean isInitial = true;
	int limit = -1;
	while(!Pager.isEndOfRecords(response)) {
	    if(isInitial) {
		limit = Pager.getMaxLimit(response);
	    }
	    response.setOffset(response.getOffset() + response.getLimit());
	    if(isInitial) {
		response.setLimit(limit);
		isInitial = false;
	    }
	    response = getDistributions(usageKey, response);
	    result.addAll(response.getResults());
	}
	return result;
    }

    /**
     * Returns all distributions for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return distribution pages
     */
    public PagingResponse<Distribution> getDistributions(int usageKey, @Nullable Pageable page) {
      StringBuffer resourceURL = getResourceURL(usageKey, "distributions");
      if (page != null)  {
	  resourceURL.append("?").append(Pager.get(page));
      }

      PagingResponse<Distribution> response = null;
      try {
	  response = mapper.readValue(new URL(resourceURL.toString()),
				      new TypeReference<PagingResponse<Distribution>>() {});
      }
      catch (MalformedURLException e) {
      }
      catch (IOException e) {
      }
      return response;
    }

    //    public List<Image> getImages(int usageKey) {
    public List<NameUsageMediaObject> getMedia(int usageKey) {
	//PagingResponse<Image> response = getImages(usageKey, null);
	PagingResponse<NameUsageMediaObject> response = getMedia(usageKey, null);
	if (response == null) {
	    return null;
	}
	//List<Image> result = Lists.newArrayList(response.getResults());
	List<NameUsageMediaObject> result = Lists.newArrayList(response.getResults());
	boolean isInitial = true;
	int limit = -1;
	while(!Pager.isEndOfRecords(response)) {
		if(isInitial) {
			limit = Pager.getMaxLimit(response);
		}
	    response.setOffset(response.getOffset() + response.getLimit());
	   if(isInitial) {
		response.setLimit(limit);
		isInitial = false;
	   }
	    response = getMedia(usageKey, response);
	    result.addAll(response.getResults());
	}
	return result;
    }


    /**
     * Returns all images for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return image pages
     */
    //public PagingResponse<Image> getImages(int usageKey, @Nullable Pageable page) {
    public PagingResponse<NameUsageMediaObject> getMedia(int usageKey, @Nullable Pageable page) {
      StringBuffer resourceURL = getResourceURL(usageKey, "media");
      if (page != null)  {
	  resourceURL.append("?").append(Pager.get(page));
      }

      //      PagingResponse<Image> response = null;
      PagingResponse<NameUsageMediaObject> response = null;
      try {
	  response = mapper.readValue(new URL(resourceURL.toString()),
				      //				      new TypeReference<PagingResponse<Image>>() {});
				      new TypeReference<PagingResponse<NameUsageMediaObject>>() {});
      }
      catch (MalformedURLException e) {
      }
      catch (IOException e) {
      }
      catch (Throwable e) {
      }
      return response;
    }

    public List<Reference> getReferences(int usageKey) {
	PagingResponse<Reference> response = getReferences(usageKey, null);
	if (response == null)
	    return null;
	List<Reference> result = Lists.newArrayList(response.getResults());
	boolean isInitial = true;
	int limit = -1;
	while(!Pager.isEndOfRecords(response)) {
		if(isInitial) {
			limit = Pager.getMaxLimit(response);
		}
	    response.setOffset(response.getOffset() + response.getLimit());
	   if(isInitial) {
		response.setLimit(limit);
		isInitial = false;
	   }
	    response = getReferences(usageKey, response);
	    result.addAll(response.getResults());
	}
	return result;
    }

    /**
     * Returns all references for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return reference pages
     */
    public PagingResponse<Reference> getReferences(int usageKey, @Nullable Pageable page) {
      StringBuffer resourceURL = getResourceURL(usageKey, "references");
      if (page != null)  {
	  resourceURL.append("?").append(Pager.get(page));
      }

      PagingResponse<Reference> response = null;
      try {
	  response = mapper.readValue(new URL(resourceURL.toString()),
				      new TypeReference<PagingResponse<Reference>>() {});
      }
      catch (MalformedURLException e) {
      }
      catch (IOException e) {
      }
      return response;
    }

    public List<SpeciesProfile> getSpeciesProfiles(int usageKey) {
	PagingResponse<SpeciesProfile> response = getSpeciesProfiles(usageKey, null) ;
	if (response == null)
	    return null;
	List<SpeciesProfile> result = Lists.newArrayList(response.getResults());
	boolean isInitial = true;
	int limit = -1;
	while(!Pager.isEndOfRecords(response)) {
		if(isInitial) {
			limit = Pager.getMaxLimit(response);
		}
	    response.setOffset(response.getOffset() + response.getLimit());
	   if(isInitial) {
		response.setLimit(limit);
		isInitial = false;
	   }
	    response = getSpeciesProfiles(usageKey, response) ;
	    result.addAll(response.getResults());
	}
	return result;
    }


    /**
     * Returns all species profiles for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return SpeciesProfile pages
     */
    public PagingResponse<SpeciesProfile> getSpeciesProfiles(int usageKey, @Nullable Pageable page)
    {
      StringBuffer resourceURL = getResourceURL(usageKey, "speciesProfiles");
      if (page != null)  {
	  resourceURL.append("?").append(Pager.get(page));
      }

      PagingResponse<SpeciesProfile> response = null;
      try {
	  response = mapper.readValue(new URL(resourceURL.toString()),
				      new TypeReference<PagingResponse<SpeciesProfile>>() {});
      }
      catch (MalformedURLException e) {
      }
      catch (IOException e) {
      }
      return response;
    }

    @Override
    public List<VernacularName> getVernacularNames(int usageKey) {
	PagingResponse<VernacularName> response =getVernacularNames(usageKey, null);
	if (response == null)
	    return null;
	List<VernacularName> results = Lists.newArrayList(response.getResults());
	boolean isInitial = true;
	int limit = -1;
	while(!Pager.isEndOfRecords(response)) {
	    if(isInitial) {
		limit = Pager.getMaxLimit(response);
	    }
	    response.setOffset(response.getOffset() + response.getLimit());
	   if(isInitial) {
		response.setLimit(limit);
		isInitial = false;
	   }
	    response = getVernacularNames(usageKey, response);
	    results.addAll(response.getResults());
	}
	return results;
    }

    /**
     * Returns all vernacular names for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return VernacularName pages
     */
    public PagingResponse<VernacularName> getVernacularNames(int usageKey, @Nullable Pageable page) {
      StringBuffer resourceURL = getResourceURL(usageKey, "vernacularNames");
      if (page != null)  {
	  resourceURL.append("?").append(Pager.get(page));
      }

      PagingResponse<VernacularName> response = null;
      try {
	  response = mapper.readValue(new URL(resourceURL.toString()),
				      new TypeReference<PagingResponse<VernacularName>>() {});
      }
      catch (MalformedURLException e) {
      }
      catch (IOException e) {
      }
      return response;
    }

    public List<TypeSpecimen> getTypeSpecimens (int usageKey) {
	PagingResponse<TypeSpecimen> response = getTypeSpecimens(usageKey, null);
	if (response == null)
	    return null;
	List<TypeSpecimen> typeSpecimens = Lists.newArrayList(response.getResults());
	boolean isInitial = true;
	while(!Pager.isEndOfRecords(response)) {
	    int limit = -1;
	    if(isInitial) {
		limit = Pager.getMaxLimit(response);
	    }
	    response.setOffset(response.getOffset() + response.getLimit());
	    if(isInitial) {
		response.setLimit(limit);
		isInitial = false;
	    }
	    response = getTypeSpecimens(usageKey, response);
	    typeSpecimens.addAll(response.getResults());
	}
	return typeSpecimens;
    }

    /**
     * Returns all type specimens for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return TypeSpecimen pages
     */
    public PagingResponse<TypeSpecimen> getTypeSpecimens(int usageKey, @Nullable Pageable page) {
      StringBuffer resourceURL = getResourceURL(usageKey, "typeSpecimens");
      if (page != null)  {
	  resourceURL.append("?").append(Pager.get(page));
      }

      PagingResponse<TypeSpecimen> response = null;
      try {
	  response = mapper.readValue(new URL(resourceURL.toString()),
				      new TypeReference<PagingResponse<TypeSpecimen>>() {});
      }
      catch (MalformedURLException e) {
      }
      catch (IOException e) {
      }
      return response;
    }

    public List<NameUsage> listCombinations(int basionymKey, Locale locale)
    {
	Locale[] locales = new Locale[1];
	locales[0] = locale;
	return listCombinations(basionymKey, locales);
    }

    public List<NameUsage> listCombinations(int basionym, Locale ... locales)
    {
      StringBuffer resourceURL = getResourceURL(basionym, Constants.COMBINATIONS_PATH);
      if (locales != null && locales.length > 0)  {
	  String connector = "";
	  resourceURL.append("?");
	  if (locales != null && locales.length > 0) {
	      String languages = getLanguages(locales);
	      if (languages != null && languages.length() > 0) {
		  resourceURL.append(languages);
		  connector = REST_AMPERSAND;
	      }
	  }
      }

      List<NameUsage> response = null;
      try {
	  HttpURLConnection connection = getConnection(resourceURL.toString(), locales);
	  response = mapper.readValue(connection.getInputStream(),
				      new TypeReference<List<NameUsage>>() {});
	  connection.disconnect();
      }
      catch (MalformedURLException e) {
      }
      catch (IOException e) {
      }
      return response;
    }
}
