/*
 * XMLWebService.java: provides methods to access to uBio XML Webservices
 *
 * Copyright (c) 2007, 2015, 2016, 2019 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP17300071, JP19K12711
 */

package org.ubio.io.xml;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import java.lang.reflect.Constructor;


import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;
import java.util.Objects;

import org.nomencurator.model.NameUsage;

import org.ubio.model.Author;
import org.ubio.model.Citation;
import org.ubio.model.ClassificationData;
import org.ubio.model.ClassificationBankObject;
import org.ubio.model.JuniorName;
import org.ubio.model.Language;
import org.ubio.model.Mapping;
import org.ubio.model.NamebankObject;
import org.ubio.model.NamebankPackage;
import org.ubio.model.Rank;
import org.ubio.model.RecordedName;
import org.ubio.model.SeniorName;
import org.ubio.model.ServiceData;
import org.ubio.model.ScientificName;
import org.ubio.model.VernacularName;

import org.ubio.io.ClassificationBankSearchResult;
import org.ubio.io.NamebankSearchResult;

import org.ubio.util.Base64Codex;
;
/**
 * {@code XMLWebService} provides a set of utility methods
 * to access to uBio using XML Webservices.
 *
 * @version 	03 Dec 2019
 * @author 	Nozomi `James' Ytow
 */
public class XMLWebService
{
    /**
     * Default base URL to access to the uBio Web services
     */
    public static final String BASE_URL = "http://www.ubio.org/webservices/service.php?function=";

    protected static final String UTF8="UTF-8";

    /**
     * Base URL to access to the Web services
     */
    protected String baseURL;

    /**
     * The key code to access to the uBio Web servieces
     */
    protected String keyCode;

    protected static final String[] sortKeys = {"nameString", "languageCode"};

    public static final int SORT_UNSPECIFIED = 0;
    public static final int SORT_BY_NAME = 1;
    public static final int SORT_BY_LANG = 2;

    public static final int UNSPECIFIED = 0;
    public static final int EXACT = 1;
    public static final int CONTAINS = 2;
    public static final int REGEX = 3;

    public static String RESULTS_KEY = "results";

    public static String VALUE_KEY = "value";

    public static String SERVICE_DATA_KEY = "serviceData";

    public static String NAMEBANK_ID_KEY = "namebankID";
    public static String NAME_STRING_KEY ="nameString";
    public static String FULLNAME_STRING_KEY = "fullNameString";

    public static String LANGUAGE_CODE_KEY = "languageCode";
    public static String LANGUAGE_NAME_KEY = "languageName";
    public static String PACKAGE_ID_KEY = "packageID";
    public static String PACKAGE_NAME_KEY ="packageName";
    public static String BASIONYM_UNIT_KEY = "basionymUnit";
    public static String NAMEBANK_ID_LINK_KEY = "namebankIDLink";
    public static String NAME_STRING_LINK_KEY = "nameStringLink";
    public static String FULLNAME_STRING_LINK_KEY = "fullNameStringLink";

    public static String NAME_STRING_QUALIFIER_KEY = "nameStringQualifier";
    public static String RANK_ID_KEY = "rankID";
    public static String RANK_NAME_KEY = "rankName";

    public static String BASIONYM_FLAG_KEY = "basionymFlag";
    public static String EXTINCT_FLAG_KEY = "extinctFlag";
    public static String HOMONYM_FLAG_KEY = "homonymFlag";
    public static String PARENT_ID_KEY = "parentID";

    public static String SENIOR_NAMES_KEY = "seniorNames";
    public static String JUNIOR_NAMES_KEY = "juniorNames";

    public static  String CLASSIFICATION_BANK_ID_KEY = "classificationBankID";
    public static  String CLASSIFICATION_TITLE_ID_KEY = "classificationTitleID";
    public static  String CLASSIFICATION_TITLE_KEY = "classificationTitle";

    /** Key to seniorClassificationBankID of JuniorName */
    public static  String SENIOR_CLASSIFICATION_BANK_ID_KEY = "seniorClassificationBankID";


    public XMLInputFactory factory = null;

    /**
     * Constructs a XMLWebService object 
     * connecting to default base URL
     * with given <code>keyCode</code>.
     *
     * @param keyCode key code to access to the uBio
     */
    public XMLWebService(String keyCode)
	throws Exception
    {
	this(BASE_URL, keyCode);
    }

    /**
     * Constructs a XMLWebService object 
     * connecting to <code>baseURL</code>
     * with given <code>keyCode</code>.
     *
     * @param baseURL URL to connect
     * @param keyCode key code to access to the uBio
     */
    public XMLWebService(String baseURL, String keyCode)
	throws Exception
    {
	if(keyCode == null ||
	   keyCode.length() == 0)
	    throw new Exception("org.ubio.XMLWebService requires a key code for uBio");
	setBaseURL(baseURL);
	setKeyCode(keyCode);
    }

    public void setBaseURL(String baseURL)
    {
	this.baseURL = baseURL;
    }

    public String getBaseURL()
    {
	return baseURL;
    }

    public void setKeyCode(String keyCode)
    {
	this.keyCode = keyCode;
    }
    
    public String getKeyCode()
    {
	return keyCode;
    }

    protected XMLStreamReader getQueryResult(String query)
    {
	if(factory == null)
	    factory = XMLInputFactory.newInstance();
	HttpURLConnection connection = null;
	try {
	    connection = 
		(HttpURLConnection)((new URL(baseURL + 
					     query +
					     "&keyCode=" +
					     getKeyCode())).openConnection());
	    connection.setRequestMethod("GET");
	    connection.connect();
	    
	    return factory.createXMLStreamReader(connection.getInputStream());
	}
	catch (Throwable t) {
	    t.printStackTrace();
	    return null;
	}
    }

    protected StringBuffer append(StringBuffer query, String key, boolean value)
    {
	query.append("&").append(key).append("=").append(value?"1":"0");
	return query;
    }

    protected StringBuffer append(StringBuffer query, String key, String value)
    {
	query.append("&").append(key).append("=").append(value);
	return query;
    }

    protected String namebankObjectQuery(String namebankID)
    {
	return ("namebank_object&namebankID=" + namebankID);
    }

    public NamebankObject namebankObject(int namebankID)
    {
	return namebankObject(Integer.toString(namebankID));
    }

    public NamebankObject namebankObject(String namebankID)
    {
	return namebankObject(getQueryResult(namebankObjectQuery(namebankID)));
    }

    protected NamebankObject namebankObject(XMLStreamReader xml)
    {
	return namebankObject(xml, "results");
    }

    protected Map<String, String> keyValueMap(XMLStreamReader xml, String terminator)
    {
	String elementName = null;
	String cdata = null;
	Map<String, String> cache = new HashMap<String, String>();
	try {
	    while(xml.hasNext() && 
		  (elementName == null ||
		   !xml.isEndElement() || !elementName.equals(terminator))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    if(cdata != null) {
			cache.put(elementName, cdata);
		    }
		    cdata = null;
		    break;
		case XMLStreamReader.CDATA:
		    break;
		case XMLStreamReader.CHARACTERS:
		    cdata = new String(xml.getTextCharacters(),
				       xml.getTextStart(),
				       xml.getTextLength());
		    break;
		}
	    }
	}
	catch(XMLStreamException e) {
	}
	return cache;
    }

    protected RecordedName setValues(RecordedName object, Map<String, String> keyValueMap)
    {
	String value = null;
	if (object == null)
	    object =new RecordedName();
	value = keyValueMap.get(NAMEBANK_ID_KEY);
	if (value != null)
	    object.setNamebankID(Integer.valueOf(value).intValue());
	value = keyValueMap.get(NAME_STRING_KEY);
	if (value != null)
	    object.setNameString(value);
	value = keyValueMap.get(FULLNAME_STRING_KEY);
	if (value != null)
	    object.setFullNameString(value);

	return object;
    }

    protected VernacularName setValues(VernacularName object, Map<String, String> keyValueMap)
    {
	String value = null;
	if (object == null)
	    object =new VernacularName();
	object = (VernacularName)setValues((RecordedName)object, keyValueMap);
	value = keyValueMap.get(LANGUAGE_CODE_KEY);
	if (value != null)
	    object.setLanguageCode(value);
	value = keyValueMap.get(LANGUAGE_NAME_KEY);
	if (value != null)
	    object.setLanguageName(value);
	value = keyValueMap.get(PACKAGE_ID_KEY);
	if (value != null)
	    object.setPackageID(Integer.valueOf(value).intValue());
	value = keyValueMap.get(PACKAGE_NAME_KEY);
	if (value != null)
	    object.setPackageName(value);
	value = keyValueMap.get(BASIONYM_UNIT_KEY);
	if (value != null)
	    object.setBasionymUnit(Integer.valueOf(value).intValue());
	value = keyValueMap.get(NAMEBANK_ID_LINK_KEY);
	if (value != null)
	    object.setNamebankIDLink(Integer.valueOf(value).intValue());
	value = keyValueMap.get(NAME_STRING_LINK_KEY);
	if (value != null)
	    object.setNameStringLink(value);
	value = keyValueMap.get(FULLNAME_STRING_LINK_KEY);
	if (value != null)
	    object.setFullNameStringLink(value);

	return object;
    }

    protected ScientificName setValues(ScientificName object, Map<String, String> keyValueMap)
    {
	String value = null;
	if (object == null)
	    object =new ScientificName();
	object = (ScientificName)setValues((VernacularName)object, keyValueMap);
	value = keyValueMap.get(NAME_STRING_QUALIFIER_KEY);
	if (value != null)
	    object.setNameStringQualifier(value);
	value = keyValueMap.get(RANK_ID_KEY);
	if (value != null)
	    object.setRankID(Integer.valueOf(value).intValue());
	value = keyValueMap.get(RANK_NAME_KEY);
	if (value != null)
	    object.setRankName(value);

	return object;
    }

    protected SeniorName setValues(SeniorName object, Map<String, String> keyValueMap)
    {
	String value = null;
	if (object == null)
	    object =new SeniorName();
	value = keyValueMap.get(CLASSIFICATION_BANK_ID_KEY);
	if (value != null)
	    object.setClassificationBankID(Integer.valueOf(value).intValue());
	value = keyValueMap.get(CLASSIFICATION_TITLE_ID_KEY);
	if (value != null)
	    object.setClassificationTitleID(Integer.valueOf(value).intValue());
	value = keyValueMap.get(CLASSIFICATION_TITLE_KEY);
	if (value != null)
	    object.setClassificationTitle(value);

	return object;
    }

    protected JuniorName setValues(JuniorName object, Map<String, String> keyValueMap)
    {
	String value = null;
	if (object == null)
	    object =new JuniorName();
	object = (JuniorName)setValues((SeniorName)object, keyValueMap);
	value = keyValueMap.get(SENIOR_CLASSIFICATION_BANK_ID_KEY);
	if (value != null)
	    object.setSeniorClassificationBankID(Integer.valueOf(value).intValue());

	return object;
    }


    protected NamebankObject namebankObject(XMLStreamReader xml, String terminator)
    {
	String elementName = null;
	String cdata = null;
	Hashtable<String, String> cache = new Hashtable<String, String>();
	NamebankObject nbo = null;
	List<RecordedName> synonyms = null;
	List<VernacularName> vernaculars = null;
	List<Citation> citations = null;
	List<Mapping> mappings = null;
	try {
	    while(xml.hasNext() && 
		  (elementName == null ||
		   !xml.isEndElement() || !elementName.equals(terminator))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    if("homotypicSynonyms".equals(elementName)) {
			synonyms = getRecordedNames(xml, "homotypicSynonyms");
		    }
		    else if("vernacularNames".equals(elementName)) {
			vernaculars = getVernacularNames(xml, "vernacularNames");
		    }
		    else if("citations".equals(elementName)) {
			citations = getCitations(xml);
		    }
		    else if("mappings".equals(elementName)) {
			mappings = getMappings(xml);
		    }
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    if(cdata != null) {
			cache.put(elementName, cdata);
		    }
		    cdata = null;
		    break;
		case XMLStreamReader.CDATA:
		    break;
		case XMLStreamReader.CHARACTERS:
		    cdata = new String(xml.getTextCharacters(),
				       xml.getTextStart(),
				       xml.getTextLength());
		    break;
		}
	    }
	    String id = cache.get("namebankID");
	    nbo = NamebankObject.get(id);
	    String value = decode(cache.get("nameString"));
	    if(value != null && nbo.getNameString() == null)
		nbo.setNameString(value);
	    id = cache.get("languageCode");
	    if(value != null) {
		nbo.setLanguage(Language.get(id, cache.get("languageName")));
	    }

	    id = cache.get("rankID");
	    if(id != null) {
		nbo.setRank(Rank.get(id, 
				     cache.get("rankName")));
	    }

	    value = decode(cache.get("fullNameString"));
	    if(value != null) {
		nbo.setFullNameString(value);
	    }

	    id = cache.get("packageID");
	    if(id != null) {
		nbo.setNamebankPackage(NamebankPackage.get(id, 
							   cache.get("packageName")));
	    }

	    id = cache.get("basionymUnit");
	    if(id != null) {
		nbo.setBasionymUnit(Integer.valueOf(id));
		nbo.setBasionymUnitObject(NamebankObject.get(id));
	    }

	    value = cache.get("basionymFlag");
	    if (value != null) {
		nbo.setBasionym(!value.equals("0"));
	    }

	    value = cache.get("nameStringQualifier");
	    if (value != null) {
		nbo.setNameStringQualifier(value);
	    }

	    value = cache.get("extinctFlag");
	    if (value != null) {
		nbo.setExtinct(!value.equals("0"));
	    }

	    value = cache.get("homonymFlag");
	    if (value != null) {
		nbo.setHomonym(!value.equals("0"));
	    }
	    id = cache.get("parentID");
	    if (id != null) {
		nbo.setParent(NamebankObject.get(id));
	    }

	    cache.clear();

	    if (synonyms != null) {
		nbo.setHomotypicSynonyms(synonyms);
	    }
	    if (vernaculars != null) {
		nbo.setVernacularNames(vernaculars);
	    }
	    if (citations != null) {
		nbo.setCitations(citations);
	    }
	    if (mappings != null) {
		nbo.setMappings(mappings);
	    }
	}
	catch(XMLStreamException e) {
	}

	return nbo;
    }

    protected List<NamebankObject> namebankObjects(XMLStreamReader xml,
						  String terminator)
    {
	List<NamebankObject> list = new ArrayList<NamebankObject>();
	String elementName = null;
	try {
	    while(xml.hasNext() && 
		  (elementName == null ||
		   !xml.isEndElement() || !elementName.equals(terminator))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    if("value".equals(elementName)) {
			NamebankObject obj = 
			    namebankObject(xml, "value");
			if(obj != null) {
			    list.add(obj);
			}
		    }
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    break;
		case XMLStreamReader.CDATA:
		    break;
		}
	    }
	}
	catch(XMLStreamException e) {
	}

	return list;
    }

    protected RecordedName recordedName(XMLStreamReader xml, 
					String terminator)
    {
	RecordedName recordedName = null;
	Map<String, String> keyValueMap = keyValueMap(xml, terminator);
	if (!keyValueMap.isEmpty()) {
	    recordedName = setValues(new RecordedName(), keyValueMap);
	}
	return recordedName;
    }

    protected List<RecordedName> getRecordedNames(XMLStreamReader xml, 
					      String terminator)
    {
	
	List<RecordedName> v = new ArrayList<RecordedName>();
	String elementName = null;
	try {
	    while(xml.hasNext() && 
		  (elementName == null ||
		   !xml.isEndElement() || !elementName.equals(terminator))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    if("value".equals(elementName)) {
			RecordedName obj = recordedName(xml, "value");
			if(obj != null) {
			    v.add(obj);
			}
		    }
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    break;
		case XMLStreamReader.CDATA:
		    break;
		}
	    }
	}
	catch(XMLStreamException e) {
	}

	return v;
    }

    protected VernacularName vernacularName(XMLStreamReader xml, 
					String terminator)
    {
	VernacularName vernacularName = null;
	Map<String, String> keyValueMap = keyValueMap(xml, terminator);
	if (!keyValueMap.isEmpty()) {
	    vernacularName = setValues(new VernacularName(), keyValueMap);
	}
	return vernacularName;
    }

    protected List<VernacularName> getVernacularNames(XMLStreamReader xml, String terminator)
    {
	List<VernacularName> v = new ArrayList<VernacularName>();
	String elementName = null;
	try {
	    while(xml.hasNext() && 
		  (elementName == null ||
		   !xml.isEndElement() || !elementName.equals(terminator))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    if("value".equals(elementName)) {
			VernacularName obj = vernacularName(xml, "value");
			if(obj != null) {
			    v.add(obj);
			}
		    }
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    break;
		case XMLStreamReader.CDATA:
		    break;
		}
	    }
	}
	catch(XMLStreamException e) {
	    e.printStackTrace();
	}

	return v;
    }

    protected ScientificName scientificName(XMLStreamReader xml, 
					String terminator)
    {
	ScientificName scientificName = null;
	Map<String, String> keyValueMap = keyValueMap(xml, terminator);
	if (!keyValueMap.isEmpty()) {
	    scientificName = setValues(new ScientificName(), keyValueMap);
	}
	return scientificName;
    }

    protected List<ScientificName> scientificNames(XMLStreamReader xml, String terminator)
    {
	List<ScientificName> v = new ArrayList<ScientificName>();
	String elementName = null;
	try {
	    while(xml.hasNext() && 
		  (elementName == null ||
		   !xml.isEndElement() || !elementName.equals(terminator))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    if("value".equals(elementName)) {
			ScientificName obj = scientificName(xml, "value");
			if(obj != null) {
			    v.add(obj);
			}
		    }
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    break;
		case XMLStreamReader.CDATA:
		    break;
		}
	    }
	}
	catch(XMLStreamException e) {
	    e.printStackTrace();
	}

	return v;
    }

    protected SeniorName seniorName(XMLStreamReader xml, 
					String terminator)
    {
	SeniorName seniorName = null;
	Map<String, String> keyValueMap = keyValueMap(xml, terminator);
	if (!keyValueMap.isEmpty()) {
	    seniorName = setValues(new SeniorName(), keyValueMap);
	}
	return seniorName;
    }

    protected List<SeniorName> seniorNames(XMLStreamReader xml, String terminator)
    {
	List<SeniorName> objects = new ArrayList<SeniorName>();
	String elementName = null;
	try {
	    while(xml.hasNext() && 
		  (elementName == null ||
		   !xml.isEndElement() || !elementName.equals(terminator))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    if(VALUE_KEY.equals(elementName)) {
			SeniorName object = seniorName(xml, VALUE_KEY);
			if(object != null) {
			    objects.add(object);
			}
		    }
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    break;
		case XMLStreamReader.CDATA:
		    break;
		}
	    }
	}
	catch(XMLStreamException e) {
	}

	return objects;
    }

    protected JuniorName juniorName(XMLStreamReader xml, 
					String terminator)
    {
	JuniorName juniorName = null;
	Map<String, String> keyValueMap = keyValueMap(xml, terminator);
	if (!keyValueMap.isEmpty()) {
	    juniorName = setValues(new JuniorName(), keyValueMap);
	}
	return juniorName;
    }

    protected List<JuniorName> juniorNames(XMLStreamReader xml, String terminator)
    {
	List<JuniorName> objects = new ArrayList<JuniorName>();
	String elementName = null;
	try {
	    while(xml.hasNext() && 
		  (elementName == null ||
		   !xml.isEndElement() || !elementName.equals(terminator))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    if(VALUE_KEY.equals(elementName)) {
			JuniorName object = juniorName(xml, VALUE_KEY);
			if(object != null) {
			    objects.add(object);
			}
		    }
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    break;
		case XMLStreamReader.CDATA:
		    break;
		}
	    }
	}
	catch(XMLStreamException e) {
	}

	return objects;
    }


    protected List<Citation> getCitations(XMLStreamReader xml)
    {
	return null;
    }

    protected List<Mapping> getMappings(XMLStreamReader xml)
    {
	return null;
    }

    protected String classificationBankObjectQuery(String classificationBankID,
						   boolean includeLowerTaxa,
						   boolean includeHigherTaxa,
						   boolean includeBibliographies,
						   boolean includeSynonyms)
    {
	StringBuffer query = 
	    new StringBuffer("classificationbank_object&hierarchiesID=");
	query.append(classificationBankID);
	append(query, "childrenFlag", includeLowerTaxa);
	append(query, "ancestryFlag", includeHigherTaxa);
	append(query, "justificationsFlag", includeBibliographies);
	append(query, "synonymsFlag", includeSynonyms);
	return query.toString();
    }

    public ClassificationBankObject classificationBankObject(int classificationBankID)
    {
	return classificationBankObject(classificationBankID, 1, 1, 1, 1);
    }

    public ClassificationBankObject classificationBankObject(int classificationBankID,
							     int includeLowerTaxa,
							     int includeHigherTaxa,
							     int includeBibliographies,
							     int includeSynonyms)
    {
	return classificationBankObject(String.valueOf(classificationBankID),
					includeLowerTaxa,
					includeHigherTaxa,
					includeBibliographies,
					includeSynonyms);
    }

    public ClassificationBankObject classificationBankObject(int classificationBankID,
							     boolean includeLowerTaxa,
							     boolean includeHigherTaxa,
							     boolean includeBibliographies,
							     boolean includeSynonyms)
    {
	return classificationBankObject(String.valueOf(classificationBankID),
					includeLowerTaxa,
					includeHigherTaxa,
					includeBibliographies,
					includeSynonyms);
    }
					
    public ClassificationBankObject classificationBankObject(String classificationBankID,
							     int includeLowerTaxa,
							     int includeHigherTaxa,
							     int includeBibliographies,
							     int includeSynonyms)
    {
	return classificationBankObject(classificationBankID,
					   getQueryResult(classificationBankObjectQuery(classificationBankID,
											includeLowerTaxa==1,
											includeHigherTaxa==1,
											includeBibliographies==1,
											includeSynonyms==1)));
    }

    public ClassificationBankObject classificationBankObject(String classificationBankID,
						      boolean includeLowerTaxa,
						      boolean includeHigherTaxa,
						      boolean includeBibliographies,
						      boolean includeSynonyms)
    {
	return classificationBankObject(classificationBankID,
					   getQueryResult(classificationBankObjectQuery(classificationBankID,
											includeLowerTaxa,
											includeHigherTaxa,
											includeBibliographies,
											includeSynonyms)));
    }

    protected ClassificationBankObject classificationBankObject(String classificationBankID,
								   XMLStreamReader xml)
    {
	return classificationBankObject(classificationBankID, xml, "results");
    }

    protected ClassificationBankObject classificationBankObject(String classificationBankID,
								   XMLStreamReader xml,
								   String terminator)
    {
	String elementName = null;
	String cdata = null;
	long classificationTitleID = -1L;
	String classificationTitle = null;
	ClassificationData cls = null;
	Rank rank = null;
	String rankID = null;
	String rankName = null;
	NamebankObject recordedName = null;
	Vector<ClassificationBankObject> v = new Vector<ClassificationBankObject>();
	try {
	    while(xml.hasNext() && 
		  (elementName == null ||
		   !xml.isEndElement() || !elementName.equals(terminator))) {
		elementName = xml.getName().toString();
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    if(elementName.equals("serviceData")) {
			ServiceData svd = 
			    getServiceData(xml, "serviceData");
		    }
		    else if(elementName.equals("recordedName")) {
			recordedName = 
			    namebankObject(xml, "recordedName");
		    }
		    else if(elementName.equals("ancestry")) {
		    }
		    else if(elementName.equals("children")) {
		    }
		    else if(elementName.equals("classificationData")) {
			cls = getClassification(xml, "classificationData");
		    }
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    if(elementName.equals("rankID")) {
			rankID = cdata;
		    }
		    else if(elementName.equals("rankName")) {
			rankName = cdata;
		    }
		    cdata = null;
		    break;
		    //case XMLStreamReader.CDATA:
		case XMLStreamReader.CHARACTERS:
		    cdata = new String(xml.getTextCharacters(),
				       xml.getTextStart(),
				       xml.getTextLength());
		    break;
		}
	    }
	    rank = Rank.get(rankID);
	    if(rank != null) {
		if(rankName != null) {
		    rank.setName(rankName);
		}
	    }
	}
	catch(XMLStreamException e) {
	    e.printStackTrace();
	}

	ClassificationBankObject obj = 
	    ClassificationBankObject.get(classificationBankID);
	if(obj == null) {
	    obj = new ClassificationBankObject(Integer.valueOf(classificationBankID).intValue());
	}

	if(cls != null)
	    obj.setClassificationData(cls);
	obj.setRank(rank);

	return obj;
    }

    protected String namebankSearchQuery(String name,
					 String authorship,
					 String year,
					 int sortKey,
					 boolean includeScientificNames,
					 boolean includeVernacularNames)
    {
	StringBuffer query = 
	    new StringBuffer("namebank_search&searchName=");
	query.append(name);
	append(query, "searchAuth", authorship);
	append(query, "searchYear", year);
	switch(sortKey) {
	case SORT_BY_NAME:
	case SORT_BY_LANG:
	    append(query, "order", sortKeys[sortKey]);
	    break;
	default:
	}
	append(query, "sci", includeScientificNames? "1":"0");
	append(query, "vern", includeVernacularNames? "1":"0");
	return query.toString();
    }

    public NamebankSearchResult  namebankSearch(String name)
    {
	return namebankSearch(name, null, null, SORT_UNSPECIFIED, true, true);
    }

    public NamebankSearchResult namebankSearch(String name,
					    String authorship,
					    String year,
					    int sortKey,
					    boolean includeScientificNames,
					    boolean includeVernacularNames)
    {
	return namebankSearch(getQueryResult(namebankSearchQuery(name,
								 authorship,
								 year,
								 sortKey,
								 includeScientificNames,
								 includeVernacularNames)));
    }

    public NamebankSearchResult namebankObjects(String name,
						 String authorship,
						 String year,
						 int sortKey,
						 boolean includeScientificNames,
						 boolean includeVernacularNames)
    {
	return namebankSearch(name, authorship, year, sortKey,
			      includeScientificNames,
			      includeVernacularNames);
    }

    protected NamebankSearchResult namebankSearch(XMLStreamReader xml)
    {
	return namebankSearch(xml, RESULTS_KEY);
    }

    protected NamebankSearchResult namebankSearch(XMLStreamReader xml,
						String terminator)
    {
	String elementName = null;
	String cdata = null;
	List<ScientificName> scientificNames = null;
	List<VernacularName> vernaculars = null;
	ServiceData serviceData = null;
	try {
	    while(xml.hasNext() && 
		  (elementName == null ||
		   !xml.isEndElement() || !elementName.equals(terminator))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    if(elementName.equals("serviceData")) {
			serviceData = getServiceData(xml, "serviceData");
		    }
		    else if("scientificNames".equals(elementName)) {
			scientificNames = scientificNames(xml, "scientificNames");
		    }
		    else if("vernacularNames".equals(elementName)) {
			vernaculars = getVernacularNames(xml, "vernacularNames");
		    }
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    break;
		case XMLStreamReader.CHARACTERS:
		    //case XMLStreamReader.CDATA:
		    cdata = new String(xml.getTextCharacters(),
				       xml.getTextStart(),
				       xml.getTextLength());
		    break;
		}
	    }
	}
	catch(XMLStreamException e) {
	}

	return  new NamebankSearchResult(serviceData, scientificNames, vernaculars);
    }

    protected String classificationBankSearchQuery(String namebankID,
						  String classificationTitleID)
    {
	StringBuffer query = 
	    new StringBuffer("classificationbank_search&namebankID=");
	query.append(namebankID);
	if(classificationTitleID != null &&
	   classificationTitleID.length() > 0) {
	    append(query, "classificationTitleID", classificationTitleID);
	}
	return query.toString();
    }

    public ClassificationBankSearchResult classificationBankSearch(int namebankID)
    {
	return classificationBankSearch(Integer.toString(namebankID));
    }

    public ClassificationBankSearchResult classificationBankSearch(int namebankID,
						       int classificationTitleID)
    {
	return classificationBankSearch(Integer.toString(namebankID),
					Integer.toString(classificationTitleID));
    }

    public ClassificationBankSearchResult classificationBankSearch(String namebankID)
    {
	return classificationBankSearch(getQueryResult(classificationBankSearchQuery(namebankID,
										     null)));
    }

    public ClassificationBankSearchResult classificationBankSearch(String namebankID,
						       String classificationTitleID)
    {
	return classificationBankSearch(getQueryResult(classificationBankSearchQuery(namebankID,
										     classificationTitleID)));
    }

    protected ClassificationBankSearchResult classificationBankSearch(XMLStreamReader xml)
    {
	String elementName = null;
	ServiceData serviceData = null;
	List<SeniorName> seniorNames = null;
	List<JuniorName> juniorNames = null;
	try {
	    while(xml.hasNext() && ( //!xml.isEndElement() ||
		  elementName == null ||
		  !elementName.equals(RESULTS_KEY))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    if(elementName.equals(SERVICE_DATA_KEY)) {
			serviceData = getServiceData(xml, SERVICE_DATA_KEY);
		    }
		    else if(elementName.equals(SENIOR_NAMES_KEY)) {
			seniorNames = seniorNames(xml, SENIOR_NAMES_KEY);
		    }
		    else if(elementName.equals(JUNIOR_NAMES_KEY)) {
			seniorNames = seniorNames(xml, JUNIOR_NAMES_KEY);
		    }
		    break;
		case XMLStreamReader.END_ELEMENT:
		    break;
		case XMLStreamReader.CHARACTERS:
		    break;
		    }
	    }
	}
	catch(XMLStreamException e) {
	}

	return new ClassificationBankSearchResult(serviceData, seniorNames, juniorNames);
    }

    protected List<ClassificationBankObject> classificationBankObjectSearch(String namebankID, XMLStreamReader xml)
    {
	NamebankObject n = NamebankObject.get(Integer.valueOf(namebankID));

	String elementName = null;
	String cdata = null;
	int classificationBankID = 0;
	int classificationTitleID =  0;
	String classificationTitle = null;
	ClassificationBankObject obj = null;
	ClassificationData cls = null;
	List<ClassificationBankObject> v = new ArrayList<ClassificationBankObject>();
	try {
	    while(xml.hasNext() && ( //!xml.isEndElement() ||
		  elementName == null ||
		  !SENIOR_NAMES_KEY.equals(elementName))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    if(elementName.equals("value")) {
			cls = ClassificationData.get(classificationTitleID);
			if(!cls.isSaturated()) {
			    cls.setClassificationTitle(classificationTitle);
			}
			obj = ClassificationBankObject.get(classificationBankID);
			obj.setClassificationData(cls);
			obj.setRecordedName(n);
			v.add(obj);
			classificationBankID = 0;
			classificationTitleID = 0;
			classificationTitle = null;
		    }
		    else if(cdata != null) {
			if(elementName.equals("classificationBankID")) {
			    classificationBankID = Integer.valueOf(cdata);
			}
			else if(elementName.equals("classificationTitleID")) {
			    classificationTitleID = Integer.valueOf(cdata);
			}
			else if(elementName.equals("classificationTitleID")) {
			    classificationTitle = decode(cdata);
			}
		    }
		    cdata = null;
		    break;
		case XMLStreamReader.CHARACTERS:
		    //case XMLStreamReader.CDATA:
		    cdata = new String(xml.getTextCharacters(),
				       xml.getTextStart(),
				       xml.getTextLength());
		    break;
		    }
	    }
	}
	catch(XMLStreamException e) {
	}

	return v;
    }

    protected String findITQuery(String urlOrFreeText,
				 boolean isURL,
				 boolean strict,
				 double threshold)
    {
	StringBuffer query = 
	    new StringBuffer("findIT");
	if(isURL) {
	    append(query, "url", urlOrFreeText);
	}
	else {
	    append(query, "freeText", urlOrFreeText);
	}
	append(query, "strict", strict? "1":"0");
	if(threshold >= 0.0 &&
	   threshold <= 1.0) {
	    append(query, "strict", Double.toString(threshold));
	}
	
	return query.toString();
    }

    public XMLStreamReader findIT(String urlOrFreeText,
			      boolean isURL,
			      boolean strict,
			      double threshold)
    {
	return findIT(getQueryResult(findITQuery(urlOrFreeText,
						 isURL,
						 strict,
						 threshold)));
    }

    protected XMLStreamReader findIT(XMLStreamReader xml)
    {
	return xml;
    }

    protected String taxonFinderQuery(String urlOrFreeText,
				      boolean isURL,
				      boolean includeLinks)
    {
	StringBuffer query = 
	    new StringBuffer("taxonFinder");
	if(isURL) {
	    append(query, "url", urlOrFreeText);
	}
	else {
	    append(query, "freeText", urlOrFreeText);
	}
	append(query, "includeLinks", includeLinks? "1":"0");
	
	return query.toString();
    }

    public XMLStreamReader taxonFinder(String urlOrFreeText,
				   boolean isURL,
				   boolean includeLinks)
    {
	return taxonFinder(getQueryResult(taxonFinderQuery(urlOrFreeText,
							   isURL,
							   includeLinks)));
    }

    protected XMLStreamReader taxonFinder(XMLStreamReader xml)
    {
	return xml;
    }

    protected String namebankPackageListQuery(String packageID)
    {
	StringBuffer query = 
	    new StringBuffer("namebank_packageList");
	if(packageID != null &&
	   packageID.length() > 0) {
	    append(query, "packageID", packageID);
	}

	return query.toString();
    }

    public Collection<NamebankPackage> packageList()
    {
	return packageList(getQueryResult(namebankPackageListQuery(null)));
    }

    public Collection<NamebankPackage> packageList(int packageID)
    {
	return packageList(getQueryResult(namebankPackageListQuery(Integer.toString(packageID))));
    }

    public Collection<NamebankPackage> packageList(String packageID)
    {
	return packageList(getQueryResult(namebankPackageListQuery(packageID)));
    }

    protected Collection<NamebankPackage> packageList(XMLStreamReader xml)
    {
	return null;
    }

    protected String classificationListQuery(String classificationTitleID)
    {
	StringBuffer query = 
	    new StringBuffer("namebank_packageList");
	if(classificationTitleID != null &&
	   classificationTitleID.length() > 0) {
	    append(query, "classificationTitleID", classificationTitleID);
	}
	
	return query.toString();
    }

    public Vector<ClassificationData> getClassificationList()
    {
	return getClassificationList("");
    }

    public Vector<ClassificationData> getClassificationList(String classificationTitleID)
    {
	return getClassificationList(getQueryResult(classificationListQuery(classificationTitleID)));
    }

    protected Vector<ClassificationData> getClassificationList(XMLStreamReader xml)
    {
	return null;
    }

    protected ClassificationData getClassification(XMLStreamReader xml, String terminator)
    {
	String elementName = null;
	String cdata = null;
	ClassificationData c = null;
	Hashtable<String, String> cache = new Hashtable<String, String>();
	try {
	    while(xml.hasNext() && 
		  (elementName == null ||
		   !xml.isEndElement() || !elementName.equals(terminator))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    if(cdata != null) {
			cache.put(elementName, cdata);
		    }
		    cdata = null;
		    break;
		case XMLStreamReader.CHARACTERS:
		    //case XMLStreamReader.CDATA:
		    cdata = new String(xml.getTextCharacters(),
				       xml.getTextStart(),
				       xml.getTextLength());
		    break;
		}
	    }
	    String idString = cache.get("classificationTitleID");
	    String title = decode(cache.get("classificationTitle"));
	    String description = decode(cache.get("classificationDescription"));
	    String parentString = cache.get("classificationTitleIDParent");
	    String rootString = cache.get("classificationRoot");
	    int id = (idString==null)?0:Integer.valueOf(idString);
	    int parentID = (parentString==null)?0:Integer.valueOf(parentString);
	    int rootID = (parentString==null)?0:Integer.valueOf(rootString);
	    ClassificationData parent = ClassificationData.get(parentID);
	    NamebankObject root = NamebankObject.get(rootID);
	    
	    c = ClassificationData.get(id);
	    if(!c.isSaturated()) {
		c.setClassificationTitle(title);
		c.setClassificationDescription(description);
		c.setParent(parent);
		c.setClassificationTitleIDParent(parentID);
		c.setRoot(root);
		c.setClassificationRoot(rootID);
		c.setSaturated(true);
	    }
	}
	catch(XMLStreamException e) {
	    e.printStackTrace();
	}

	return c;
    }

    protected String languageListQuery()
    {
	return "language_list";
    }

    public List<Language> languageList()
    {
	return languageList(getQueryResult(languageListQuery()));
    }

    protected List<Language> languageList(XMLStreamReader xml)
    {
	List<Language> languages = new ArrayList<Language>();
	String elementName = null;
	String cdata = null;
	try {
	    while(xml.hasNext() && 
		  (elementName == null ||
		   !xml.isEndElement() || !RESULTS_KEY.equals(elementName))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    if(cdata != null) {
			String[] values = cdata.split("|");
			languages.add(Language.get(values[0], values[1]));
		    }
		    cdata = null;
		    break;
		case XMLStreamReader.CDATA:
		    break;
		case XMLStreamReader.CHARACTERS:
		    cdata = new String(xml.getTextCharacters(),
				       xml.getTextStart(),
				       xml.getTextLength());
		    break;
		}
	    }
	}
	catch(XMLStreamException e) {
	}

	return null;
    }

    protected String synonymListQuery(String classificationBankID)
    {
	StringBuffer query = 
	    new StringBuffer("synonym_list&hierarchiesID=");
	query.append(classificationBankID);
	return query.toString();
    }

    public List<? extends NameUsage<?>> getSynonymList(String classificationBankID)
    {
	return getSynonymList(getQueryResult(synonymListQuery(classificationBankID)));
    }

    protected List<? extends NameUsage<?>> getSynonymList(XMLStreamReader xml)
    {
	return null;
    }

    protected String namebankSearchAuthorsQuery(String author,
						int qualifier,
						int limit)
    {
	StringBuffer query = 
	    new StringBuffer("namebank_search_authors&searchAuth=");
	query.append(author);
	switch(qualifier) {
	case EXACT:
	case CONTAINS:
	case REGEX:
	    append(query, "searchQualifier", Integer.toString(qualifier));
	    break;
	default:
	    break;
	}

	if(limit >= 1 && limit <= 6) {
	    append(query, "limitBP", Integer.toString(limit));
	}
	return query.toString();
    }

    public Vector<Author> getAuthorVariants(String author,
					    int qualifier,
					    int limit)
    {
	return namebankSearchAuthors(getQueryResult(namebankSearchAuthorsQuery(author,
									  qualifier,
									  limit)));	
    }

    protected Vector<Author> namebankSearchAuthors(XMLStreamReader xml)
    {
	return null;
    }

    protected String namebankObjectAuthorsQuery(int groupID)
    {
	StringBuffer query = 
	    new StringBuffer("namebank_object_authors&groupID=");
	query.append(Integer.toString(groupID));
	return query.toString();
    }

    public Vector<Author> getAuthorVariants(int groupID)
    {
	return namebankObjectAuthors(getQueryResult(namebankObjectAuthorsQuery(groupID)));	
    }

    protected Vector<Author> namebankObjectAuthors(XMLStreamReader xml)
    {
	return null;
    }

    public Author getAuthor(XMLStreamReader xml)
    {
	int depth = 1;
	String elementName = null;
	String cdata = null;
	Author author = null;
	try {
	    while(xml.hasNext() && ( //!xml.isEndElement() ||
		  elementName == null ||
		  !elementName.equals("value"))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    break;
		case XMLStreamReader.END_ELEMENT:
		    if(cdata != null) {
			elementName = xml.getName().toString();
			if("authorID".equals(elementName)) {
			    if(author == null)
				author = new Author();
			    author.setId(Integer.valueOf(cdata));
			}
			if("groupID".equals(elementName)) {
			    if(author == null)
				author = new Author();
			    author.setGroup(Integer.valueOf(cdata));
			}
			else if("authorString".equals(elementName)) {
			    if(author == null)
				author = new Author();
			    author.setAuthorString(cdata);
			}
			else if("attributeID".equals(elementName)) {
			    if(author == null)
				author = new Author();
			    author.setAttribute(Integer.valueOf(cdata));
			}
			cdata = null;
		    }
		    break;
		case XMLStreamReader.CHARACTERS:
		    //case XMLStreamReader.CDATA:
		    cdata = new String(xml.getTextCharacters(),
				       xml.getTextStart(),
				       xml.getTextLength());
		    break;
		}
	    }
	}
	catch(XMLStreamException e) {
	    e.printStackTrace();
	}
	return author;
    }

    protected ServiceData getServiceData(XMLStreamReader xml, String terminator)
    {
	String elementName = null;
	String cdata = null;
	ServiceData serviceData = null;
	Hashtable<String, String> cache = new Hashtable<String, String>();
	try {
	    while(xml.hasNext() && 
		  (elementName == null ||
		   !xml.isEndElement() || !elementName.equals(terminator))) {
		switch(xml.next()) {
		case XMLStreamReader.START_ELEMENT:
		    elementName = xml.getName().toString();
		    break;
		case XMLStreamReader.END_ELEMENT:
		    elementName = xml.getName().toString();
		    if(cdata != null) {
			cache.put(elementName, cdata);
		    }
		    cdata = null;
		    break;
		case XMLStreamReader.CHARACTERS:
		    cdata = new String(xml.getTextCharacters(),
				       xml.getTextStart(),
				       xml.getTextLength());
		    break;
		}
	    }
	    serviceData = new ServiceData(cache.get("currentVersion"),
					  cache.get("dateStamp"),
					  cache.get("timeStamp"));
	}
	catch(XMLStreamException e) {
	    e.printStackTrace();
	}
	cache.clear();

	return serviceData;
    }

    public static String encode(String text)
    {
	return Base64Codex.encode(text);
    }

    public static String decode(String base64)
    {
	return Base64Codex.decode(base64);
    }
}
