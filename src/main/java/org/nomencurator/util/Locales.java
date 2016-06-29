/*
 * Locales.java:  an utilty to mange Locales
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

package org.nomencurator.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.Map;
import java.util.Set;

/**
 * <code>Locales</code> provides a map of Locales
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class Locales
{
    /** Map between language tag and Locale */
    private static Map<String, Locale> byLanguageTag = Collections.synchronizedMap(new HashMap<String, Locale>());

    /** Map between language and Set of Locales */
    private static Map<String, Set<Locale>> byLanguage = Collections.synchronizedMap(new HashMap<String, Set<Locale>>());

    /** Nested Map betwen language, country and Locales */
    private static Map<String, Map<String, Set<Locale>>> byLanguageCountry = Collections.synchronizedMap(new HashMap<String, Map<String, Set<Locale>>>());

    /** Nested Map betwen language, country, variant and Locales */
    private static Map<String, Map<String, Map<String, Set<Locale>>>> byLanguageCountryVariant = Collections.synchronizedMap(new HashMap<String, Map<String, Map<String, Set<Locale>>>>());

    /** Locale Builder */
    private static Builder builder;

    private Locales() {};

    /**
     *  Regists a Locale to Locales if not yet
     * 
     * @param locale Locale to regist
     * @return Locale registered, or null if given locale is null.  It is different from given locale if equal Locale is already registered.
     */
    public static Locale put(Locale locale)
    {
	if(locale == null)
	    return null;

	Set<Locale> locales = null;
	Map<String, Set<Locale>> map = null;
	Map<String, Map<String, Set<Locale>>> mapMap = null;

	String tag = locale.toLanguageTag();
	Locale l = get(tag);
	if(l != null) {
	    locale = l;
	}
	else {
	    // language tag is expected to be unique....
	    synchronized(byLanguageTag) {
		byLanguageTag.put(tag, locale);
	    }

	    // get Set of Locales indexed by its language
	    String language = locale.getLanguage();
	    synchronized(byLanguage) {
		locales = byLanguage.get(language);
		// create and add if no Set is indexed by the language
		if(locales == null) {
		    locales = Collections.synchronizedSet(new HashSet<Locale>());
		    byLanguage.put(language, locales);
		}
	    }

	    // add the locale to the Set
	    synchronized(locales) {
		locales.add( locale);
	    }

	    String country = locale.getCountry();
	    if(country != null) {
		synchronized(byLanguageCountry) {
		    map = byLanguageCountry.get(language);
		    if(map == null) {
			map = Collections.synchronizedMap(new HashMap<String, Set<Locale>>());
			byLanguageCountry.put(language, map);
		    }
		}

		synchronized(map) {
		    locales = map.get(country);
		    if(locales == null) {
			locales = Collections.synchronizedSet(new HashSet<Locale>());
			map.put(country, locales);
		    }
		}
		
		synchronized(locales) {
		    locales.add( locale);
		}

		String variant = locale.getVariant();
		if(variant != null) {
		    synchronized(byLanguageCountryVariant) {
			mapMap = byLanguageCountryVariant.get(language);
			if(mapMap == null) {
			    mapMap = Collections.synchronizedMap(new HashMap<String, Map<String, Set<Locale>>>());
			    byLanguageCountryVariant.put(language, mapMap);
			}
		    }

		    synchronized(mapMap) {
			map = mapMap.get(country);
			if(map == null) {
			    map = Collections.synchronizedMap(new HashMap<String, Set<Locale>>());
			    mapMap.put(country, map);
			}
		    }

		    synchronized(map) {
			locales = map.get(variant);
			if(locales == null) {
			    locales = Collections.synchronizedSet(new HashSet<Locale>());
			    map.put(variant, locales);
			}
		    }

		    synchronized(locales) {
			locales.add(locale);
		    }
		}
	    }
	}
	return locale;
    }

    public static Locale get(String languageTag)
    {
	if(languageTag == null)
	    return null;

	Locale locale = byLanguageTag.get(languageTag);
	if(locale == null) {
	    locale = Locale.forLanguageTag(languageTag);
	    if(locale != null)
		put(locale);
	}
	return locale;
    }

    public static Set<Locale> language(String language)
    {
	if(language == null)
	    return null;

	Set<Locale> locales = byLanguage.get(language);

	if(locales != null)
	    return locales;

	if(builder == null)
	    builder = new Locale.Builder();
	synchronized(builder) {
	    builder.clear();
	    builder.setLanguage(language);
	    put(builder.build());
	}

	return byLanguage.get(language);
    }

    public static Set<Locale> get(String language, String country)
    {
	if(country == null)
	    return language(language);
		
	Map<String, Set<Locale>> map = byLanguageCountry.get(language);
	if(map == null) {
	    map = new HashMap<String, Set<Locale>>();
	    byLanguageCountry.put(language, map);
	}
	Set<Locale> locales = map.get(country);
	if(locales != null)
	    return locales;

	if(builder == null)
	    builder = new Locale.Builder();
	synchronized(builder) {
	    builder.clear();
	    builder.setLanguage(language);
	    builder.setRegion(country);
	    put(builder.build());
	}

	return map.get(country);
    }

    public static Set<Locale> get(String language, String country, String variant)
    {
	if(variant == null)
	    return get(language, country);

	Map<String, Map<String, Set<Locale>>> mapMap = byLanguageCountryVariant.get(language);
	if(mapMap == null) {
	    mapMap = Collections.synchronizedMap(new HashMap<String, Map<String, Set<Locale>>>());
	    byLanguageCountryVariant.put(language, mapMap);
	}
	Map<String, Set<Locale>> map = mapMap.get(country);
	if(map == null) {
	    map = Collections.synchronizedMap(new HashMap<String, Set<Locale>>());
	    mapMap.put(country, map);
	}
	Set<Locale> locales = map.get(variant);
	if(locales != null)
	    return locales;

	if(builder == null)
	    builder = new Locale.Builder();
	synchronized(builder) {
	    builder.clear();
	    builder.setLanguage(language);
	    builder.setRegion(country);
	    builder.setVariant(variant);
	    put(builder.build());
	}

	return map.get(variant);
    }
}
