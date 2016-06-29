/*
 * NomenclatureCode.java:  an enumeration of nomenclature codes
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

package org.nomencurator.model.vocabulary;

import java.util.Locale;
import java.util.Locale.Builder;

import lombok.Getter;

/**
 * Enumeration of nomenclature codes
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public enum NomenclatureCode
{
    ICZN("iczn","International Code of Zoological Nomenclature",1905),
    ICBN("icbn","International Code of Botanical Nomenclature ",1905),
    ICNCP("icncp","International Code of Nomenclature for Cultivated Plants",1953),
    ICNB("icnb","International Code of Nomenclature of Bacteria",1975),
    ICTV("ictv","Rules of Virus Classification and Nomenclature",1991),
    ICN("icn","International Code of Nomenclature for algae, fungi, and plants",2011),
    BIOCODE("biocode","BioCode",1997),
    PHYLOCODE("phylocode","PhyloCode",2014);

    private Builder  builder; // = new Builder();

    @Getter
    private Locale locale;

    /*
    public Locale getLocale()
    {
	return locale;
    }
    */

    @Getter
    private String extension;

    @Getter
    private String name;

    @Getter
    private int since;

    NomenclatureCode(String extension, String name, int since) {
	//locale = new Builder().setLanguageTag("zxx").setExtension(Locale.PRIVATE_USE_EXTENSION,extension).build();
	if(builder == null)
	    builder = new Builder();
	synchronized(builder) {
	    builder.clear();
	    builder.setExtension(Locale.PRIVATE_USE_EXTENSION,extension);
	    locale = builder.build();
	}
	this.extension = extension;
	this.name = name;
	this.since = since;
    }

    public static Locale[] getLocales () {
	NomenclatureCode[] codes = values();
	Locale[] locales = new Locale[codes.length];
	int i = 0;
	for (NomenclatureCode code : codes) {
	    locales[i++] = code.getLocale();
	    //locales[i++] = code.locale;
	}

	return locales;
    }
}
