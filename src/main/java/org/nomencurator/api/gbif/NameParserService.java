/*
 * NameParserService.java:  an interface definition of name parsing SpeciesAPI of GBIF
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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP19K12711
 */

package org.nomencurator.api.gbif;

import java.util.List;

import org.nomencurator.api.gbif.model.checklistbank.ParsedName;


/**
 * <CODE>NameParserService</CODE> defines an interface to use Name Parser of  GBIF SpeciesAPI.
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public interface NameParserService  {

    /* 
      *  Content type of JSON
      */
    public static String JSON = "json";

    /* 
      *  Content type of plain text
      */
    public static String TEXT = "text/plain";

    /* 
      *  Content type of from data
      */
    public static String FORM_DATA = "multipart/form-data";

    /*
      * Returns parsed name using HTTP GET
      *
      * @param name name to be parsed
      * @return list of pased names
      */
    public List<ParsedName> parse(String name);

    /*
      * Returns parsed names using HTTP GET
      *
      * @param names names to be parsed
      * @return list of pased names
      */
    public List<ParsedName> parse(String [ ] names);

    /*
      * Returns parsed names of given content type using HTTP POST 
      *
      * @param contentType content type, one of JSON, TEXT, FORM_DATA
      * @param names names to be parsed
      * @return list of pased names
      */
    public List<ParsedName> parse(String contentType, String names);

}
