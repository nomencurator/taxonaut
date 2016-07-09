/*
 * NameAnalyzer.java:  a utility crass to analyze taxon name
 *
 * Copyright (c) 2003, 2004, 2014, 2015, 2016 Nozomi `James' Ytow
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import org.nomencurator.model.vocabulary.NomenclatureCode;
import org.nomencurator.model.vocabulary.NamingConvention;

/**
 * {@code NameAnalyzer}> provides utility methods to analize taxon names
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameAnalyzer
{
    public static final int ZOOLOGY = 0;
    public static final int BOTANY = 1;
    public static final int ALGAE = 2;
    public static final int FUNGI = 3;
    public static final int BACTERIOLOGY = 4;
    public static final int VIROLOGY = 5;
    public static final int STRICT = -2;
    public static final int ALL = -1;

    //protected static Map<NamingConvention, <Map<Rank, String>> namingConventions = HashMap<NamingConvention, Map<Rank, String>>();
    protected static Map<NamingConvention, Map<Rank, String>> namingConventions;

    /*
    protected static Map<Rank, String>zoology = new HashMap<Rank, String>();
    protected static Map<Rank, String>botany = new HashMap<Rank, String>();
    protected static Map<Rank, String>algae = new HashMap<Rank, String>();
    protected static Map<Rank, String>fungi = new HashMap<Rank, String>();
    protected static Map<Rank, String>bacteriology = new HashMap<Rank, String>();
    protected static Map<Rank, String>virology = new HashMap<Rank, String>();
    */
    

    protected static final String[][] zoologicalEndings = 
	new String[][]{
	    {"superfamily", "oidea"},
	    {"family", "idae"},
	    {"subfamily", "inae"},
	    {"tribe", "ini"},
	};

    protected static final String[][] botanicalEndings = 
	new String[][]{
	    {"division", "phyta"},
	    {"subdivision", "phytina"},
	    {"class", "opsida"},
	    {"subclass", "idae"},
	    {"order", "ales"},
	    {"suborder", "ineae"},
	    {"family", "aceae"},
	    {"subfamily", "oideae"},
	    {"tribe", "eae"},
	    {"subtribe", "inae"}
	};

    protected static final String[][] algalEndings =
	new String[][] {
	    {"class", "phyceae"},
	    {"subclass", "phycidae"}
	};

    protected static final String[][] fungusEndings =
	new String[][]{
	    {"division", "mycota"},
	    {"subdivision", "mycotina"},
	    {"class", "mycetes"},
	    {"subclass", "mycetidae"}
	};

    protected static final String[][] bacterialEndings =
	new String[][] {
	    {"order", "ales"},
	    {"suborder", "ineae"},
	    {"family", "aceae"},
	    {"subfamily", "oideae"},
	    {"tribe", "eae"},
	    {"subtribe", "inae"}
	};

    protected static final String[][] virologicalEndings =
	new String[][] {
	    {"family", "viridae"},
	    {"subfamily", "virinae"},
	    {"genus", "virus"},
	};
    

    protected static void initialize(Map<Rank, String> table,
				     String[][] endings)
    {
	initialize(table, endings, null);
    }

    protected static void initialize(Map<Rank, String> table,
				     String[][] endings,
				     String[][] replacements)
    {
	for(int i = 0; i < endings.length; i++) {
	    table.put(Rank.get(endings[i][0]), endings[i][1]);
	    table.put(Rank.get(endings[i][1]), endings[i][0]);
	}
	if(replacements == null)
	    return;
	for(int i = 0; i < replacements.length; i++) {
	    table.remove(table.get(replacements[i][0]));
	    table.put(Rank.get(replacements[i][0]), replacements[i][1]);
	    table.put(Rank.get(replacements[i][1]), replacements[i][0]);
	}
    }

    static {
	namingConventions = Collections.synchronizedMap(new HashMap<NamingConvention, Map<Rank, String>>());

	Map<Rank, String>map = Collections.synchronizedMap(new HashMap<Rank, String>());
	namingConventions.put(NamingConvention.ZOOLOGY, map);
	initialize(map, zoologicalEndings);

	map = Collections.synchronizedMap(new HashMap<Rank, String>());
	namingConventions.put(NamingConvention.BOTANY, map);
	initialize(map, botanicalEndings);

	map = Collections.synchronizedMap(new HashMap<Rank, String>());
	namingConventions.put(NamingConvention.ALGAE, map);
	initialize(map, botanicalEndings, algalEndings);

	map = Collections.synchronizedMap(new HashMap<Rank, String>());
	namingConventions.put(NamingConvention.FUNGI, map);
	initialize(map, botanicalEndings, fungusEndings);

	map = Collections.synchronizedMap(new HashMap<Rank, String>());
	namingConventions.put(NamingConvention.BACTERIOLOGY, map);
	initialize(map, bacterialEndings);

	map = Collections.synchronizedMap(new HashMap<Rank, String>());
	namingConventions.put(NamingConvention.VIROLOGY, map);
	initialize(map, virologicalEndings);
    }

    public static String getBaseName(String name, String rank, NamingConvention convention)
    {
	return getBaseName(name, Rank.get(rank), convention);
    }

    public static String getBaseName(String name, Rank rank, NamingConvention convention)
    {
	if(convention == null) {
	    String baseName = null;
	    Set<NamingConvention> keys = namingConventions.keySet();
	    for(NamingConvention cnv : keys) {
		baseName = getBaseName(name, rank, cnv);
		if(baseName != null)
		    break;
	    }
	    return (baseName == null)? name:baseName;
	}

	String ending = namingConventions.get(convention).get(rank);
	if(ending == null)
	    return name;

	int index = name.lastIndexOf(ending);
	if(index < 0)
	    return name;
	return name.substring(0, index);
    }

    public static String getBaseName(NameUsage<?> usage, NamingConvention convention)
    {
	return getBaseName(usage.getLiteral(), usage.getRankLiteral(), convention);
    }

    public static String getBaseName(NameUsage<?> usage)
    {
	NamingConvention convention = null;
	Locale code = usage.getLocale();
	if(code != null) {
	    if(code.equals(NomenclatureCode.ICZN.getLocale()))
		convention = NamingConvention.ZOOLOGY;
	    else if(code.equals(NomenclatureCode.ICBN.getLocale()))
		convention = NamingConvention.BOTANY;
	}
	return getBaseName(usage, convention);
    }
}
