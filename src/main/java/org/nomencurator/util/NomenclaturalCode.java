/*
 * NomenclaturalCode.java:  an extension to java.util.Locale to support nomenclatural codes
 *
 * Copyright (c) 2015 Nozomi `James' Ytow
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.Getter;

/**
 * <code>NomenclaturalCode</code> provides extended Locales for nomenclatural codes
 *
 * @version 	08 Sep. 2015
 * @author 	Nozomi `James' Ytow
 */
public class NomenclaturalCode
{
    private String RESOURCE_PATH = "org.nomencurator.resources.NomenclaturalCode";

    private static Builder builder = new Builder();

    /** Langage name for ICZN locale */
    public static final String ICZN_LANG = "qaa";

    /** Locale for ICZN */
    public static final Locale ICZN = builder.setLanguage(ICZN_LANG).build();

    /** Langage name for ICBN locale */
    public static final String ICBN_LANG = "qab";

    /** Locale for ICBN */
    public static final Locale ICBN = builder.setLanguage(ICBN_LANG).build();

    /** Locale for ICN */
    public static final Locale ICN = ICBN;

    /** Langage name for ICNB locale */
    public static final String ICNB_LANG = "qac";

    /** Locale for ICNB */
    public static final Locale ICNB = builder.setLanguage(ICNB_LANG).build();

    /** Langage name for ICNCB locale */
    public static final String ICNCB_LANG = "qad";

    /** Locale for ICNCB */
    public static final Locale ICNCB = builder.setLanguage(ICNCB_LANG).build();

    /** Langage name for ICTV locale */
    public static final String ICTV_LANG = "qae";

    /** Locale for ICTC */
    public static final Locale ICTV = builder.setLanguage(ICTV_LANG).build();

    /** Langage name for BioCode locale */
    public static final String BIOCODE_LANG = "qaf";

    /** Locale for BioCode */
    public static final Locale BIOCODE = builder.setLanguage(BIOCODE_LANG).build();

    /** Langage name for PhyloCode locale */
    public static final String PHYLOCODE_LANG = "qag";

    /** Locale for PhyloCode */
    public static final Locale PHYLOCODE = builder.setLanguage(PHYLOCODE_LANG).build();

    private static Map<Locale, String>languageKeys = Collections.synchronizedMap(new HashMap<Locale, String>());
    private static List<Locale> codes = Collections.synchronizedList(new ArrayList<Locale>());

    private static synchronized void put(Locale locale, String resourceKey)
    {
	codes.add(locale);
	languageKeys.put(locale, resourceKey);
    }

    private static synchronized void remove(Locale locale)
    {
	languageKeys.remove(locale);
	codes.remove(locale);
    }

    public static Locale[] getCodes()
    {
	return codes.toArray(new Locale[codes.size()]);
    }

    private static NomenclaturalCode code = new NomenclaturalCode();
    
    static {
	put(ICZN,"ICZN");
	put(ICBN,"ICBN");
	put(ICNB,"ICNB");
	put(ICNCB,"ICNCB");
	put(ICTV,"ICTV");
	put(BIOCODE,"BioCode");
	put(PHYLOCODE, "PhyloCode");
    }

    private NomenclaturalCode() 
    {
    }

    public static NomenclaturalCode getCode()
    {
	return code;
    }

    public String getDisplayName(Locale locale, Locale inLocale)
    {
	String languageToDisplay = languageKeys.get(locale);
	if (languageToDisplay == null)
	    return locale.getDisplayName(inLocale);

	try {
	    languageToDisplay = ResourceBundle.getBundle(RESOURCE_PATH, inLocale).getString(languageToDisplay);
	}
	catch(MissingResourceException e) {

	}
	if (languageToDisplay == null)
	    languageToDisplay = languageKeys.get(locale);
	
	return languageToDisplay;
    }


    public String getDisplayLanguage(Locale locale, Locale inLocale)
    {
	String languageToDisplay = languageKeys.get(locale);
	if (languageToDisplay == null)
	    return locale.getDisplayLanguage(inLocale);

	try {
	    languageToDisplay = ResourceBundle.getBundle(RESOURCE_PATH, inLocale).getString(languageToDisplay);
	}
	catch(MissingResourceException e) {

	}
	if (languageToDisplay == null)
	    languageToDisplay = languageKeys.get(locale);
	
	return languageToDisplay;
    }
}
