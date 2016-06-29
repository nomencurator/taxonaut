/*
 * GBIFAPIClient.java:  a client implementation of GBIF SpeciesAPI
 *
 * Copyright (c) 2014, 2015 Nozomi `James' Ytow
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
//import com.fasterxml.jackson.core.type.TypeReference;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.Collection;

/**
 * <CODE>SpeciesAPI</CODE> implements a client interface to use GBIF SpeciesAPI.
 *
 * @version 	15 July 2015
 * @author Nozomi "James" Ytow
 */
public class GBIFAPIClient /* implements GBIFAPI */ {

    static protected String ZERO_CONNECTOR = "";
    static protected String REST_AMPERSAND = "&";
    //static public String CURRENT_VERSION = "v0.9";
    static public String CURRENT_VERSION = "v1";

    protected String version;
    protected String baseURL;

    protected ObjectMapper mapper;

    /*
     * Returns version of supported API
     *
     * @return version of supported API
     */
    public String getVersion() {
    	return version;
    }

    /*
     * Set verson of supported API
     *
     * @param verson of supported API
     */
    protected void setVersion(String version) {
    	this.version = version;
    }

    /*
     * Returns baseURL of GBIF API
     *
      @return baseURL of GBIF API
     */
    protected String getBaseURL() {
	return baseURL;
    }

    /*
     * Set baseURL of the API to specified URL
     *
     * @param baseURL to be set
     */
    protected void setBaseURL(String baseURL) {
	this.baseURL = baseURL;
    }

    /*
     * Returns REST-ish parametrs with given key concatinated with ampersand.
     *
     * @param key used in REST
     * @param parameters <CODE>Collection</CODE> of parameter values
     *
     * @return REST-ish representaion of parametrs
     */
    protected String getParameter(String key, Collection<String> parameters) {
	String parameter = null;
	if(parameters != null  &&  parameters.size() > 0) {
	    String connector = "";
	    try {
		key = URLEncoder.encode(key,"utf-8");
	    }
	    catch (UnsupportedEncodingException e) {
	    }
	    parameter = "";
	    for (String value : parameters) {
		if(value != null) {
		try {
		    parameter =  parameter  + connector + key + "=" + URLEncoder.encode(value, "utf-8");
		    if(connector.length() == 0) {
			connector = REST_AMPERSAND;
		    }
		}
		catch (UnsupportedEncodingException e) {
		}
		}
	    }
	}
	return parameter;
    }

    public GBIFAPIClient () {
	setVersion(CURRENT_VERSION);
	setBaseURL("http://api.gbif.org");
	mapper = new ObjectMapper();
    }
}
