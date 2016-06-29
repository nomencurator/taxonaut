/*
 * Rank.java:  a Java implementation of Rank class
 * for TaxoNote, an user interface model for Nomencurator
 *
 * Copyright (c) 2001, 2002, 2003, 2004, 2005, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import lombok.Getter;

/**
 * Rank provides rank tracking without parsing name of rank
 *
 * @version 	21 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class Rank
    extends Observable
    implements Serializable
{
    private static final long serialVersionUID = 2805935755406472820L;

    @Getter
    protected static int minLength = Integer.MAX_VALUE;

    @Getter
    protected static int maxLength = Integer.MIN_VALUE;


    /** Name of the rank */
    protected String name;

    /** abbreviated name of the rank */
    protected String abbreviation;
    
    /** Pointer to higher Rank which may be null */ 
    protected Rank higher;

    /** Pointer to lower Rank which may be null */
    protected Rank lower;

    /** Pointer to equivalent Rank provieds rank synonymy */
    protected Rank equivalent;

    /** boolean indicating whether it is optional rank */
    protected boolean optional;

    /** Code relevant information */
    protected int codeCoverage;

    /** Constant representing ICBN */
    public static final int ICBN = 1;

    /** Constant representing ICNB */
    public static final int ICNB = 2;

    /** Constant representing ICNCP */
    public static final int ICNCP = 4;

    /** Constant representing ICNV */
    public static final int ICNV = 8;

    /** Constant representing ICZN */
    public static final int ICZN = 0x10;

    /** Code relevant information */
    protected int namingConvention;

    /** Constant representing zoological naming convention */
    public static final int ZOOLOGICAL = 1;

    /** Constant representing botanical naming convention */
    public static final int BOTANICAL = 2;

    /** Constant representing algal naming convention */
    public static final int ALGAL = 4;

    /** Constant representing fungus naming convention */
    public static final int FUNGUS = 8;

    /** Constant representing bacteriological naming convention */
    public static final int BACTRIOLOGICAL = 0x10;

    /** Constant representing virological naming convention */
    public static final int VIROLOGICAL = 0x20;

    /** name endings */
    protected String[] endings;

    //public static final String UNRANKED = "unranked";

    /**
     * Name of full ranks, i.e. Rank has non-null higher and 
     * lower value.
     */
    static String fullyLinkedNames[][] = {
	{"domain", "dom"},
	{"kingdom", "kdm"},
	{"phylum", "phy"},
	{"class", "cls"},
	{"order", "ord"},
	{"family", "fam"},
	{"tribe", "tribe"},
	{"genus", "gen"},
	{"section", "sect"},
	{"series", "ser"},
	{"species", "sp"},
	{"variety", "v"},
	{"form", "f"}
    };

    /**
     * sysnonymys of ranks
     */
    static String equivalents[][] = {
	{"division", "div", "phylum"},
	{"nothogenus", "nothogen","genus"},
	{"nothospecies", "nothosp", "species"},
	{"aggrigate species", "agg", "species"},
	{"cultivar group", "", "cultivar"}
    };

    /**
     * sysnonymys of ranks
     */
    static String partiallyLinkedHigherRankName[][] = {
	{"subkingdom", "subkdm", "kingdom"},
	{"subphylum", "subphy", "phylum"},
	{"subclass", "subcls", "class"},
	{"infraclass", "infcls", "subclass"},
	{"suborder", "subord", "order"},
	{"infraorder", "inford", "suborder"},
	{"subfamily", "subfam", "family"},
	{"subtribe", "subtribe", "tribe"},
	{"subgenus", "subgen", "genus"},
	{"subsection", "subsect", "section"},
	{"subseries", "subser", "series"},
	{"infragenus", "infragen", "subgenus"},
	{"strain", "strain", "species"},
	{"cultivar", "cv", "species"},
	{"subspecies", "subsp", "species"},
	{"infrasubspeices", "infrasubsp", "subspecies"},
	{"subvariety", "subvar", "variety"},
	{"subform", "subf", "form"}
    };

    /**
     * Sysnonymys of ranks
     */
    static String partiallyLinkedLowerRankName[][] = {
	{"superphylum", "supphy", "phylum"},
	{"superclass", "supcls", "class"},
	{"superorder", "supord", "order"},
	{"superfamily", "supfam", "family"},
	{"supergenus", "supgen", "genus"}
    };

    /**
     * <code>Hashtable</code> to hold <code>Rank</code>s
     */
    static Map<String, Rank> ranks = new HashMap<String, Rank>();

    /**
     * <code>Hashtable</code> to hold abbreviations of rank name
     */
    static Map<String, String> abbreviations = new HashMap<String, String>();

    /**
     * <code>Vector</code> to hold <code>Rank</code>s
     */
    static List<Rank> sortedRanks = Collections.synchronizedList(new ArrayList<Rank>());

    static RankObserver rankObserver = new RankObserver();



    static {
	/*
	 * Put deafult <code>Rank</code>s to the <code>Hashtable</code>
	 * with making links to upper and lower <code>Rank</code>s
	 */
	int rankIndex = 0;
	Rank currentRank = new Rank(fullyLinkedNames[rankIndex][0]);
	currentRank.setAbbreviation(fullyLinkedNames[rankIndex][1]);
	abbreviations.put(fullyLinkedNames[rankIndex][0], fullyLinkedNames[rankIndex][1]);
	for(++rankIndex; rankIndex < fullyLinkedNames.length; rankIndex++) {
	    ranks.put(currentRank.getName(), currentRank);
	    sortedRanks.add(currentRank);
	    Rank nextRank = new Rank(fullyLinkedNames[rankIndex][0]);
	    nextRank.setAbbreviation(fullyLinkedNames[rankIndex][1]);
	    currentRank.setLower(nextRank);
	    nextRank.setHigher(currentRank);
	    currentRank = nextRank;
	    nextRank = null;
	    abbreviations.put(fullyLinkedNames[rankIndex][0], fullyLinkedNames[rankIndex][1]);
	}
	ranks.put(currentRank.getName(), currentRank);
	sortedRanks.add(currentRank);

	/*
	 * Create and put partially linked <code>Rank</code>s to the
	 * <code>Hashtable</code>
	 */
	for(rankIndex = 0; rankIndex < partiallyLinkedHigherRankName.length; rankIndex++) {
	    currentRank = new Rank(partiallyLinkedHigherRankName[rankIndex][0], null, null);
	    currentRank.setAbbreviation(partiallyLinkedHigherRankName[rankIndex][1]);
	    Rank higherRank = get(partiallyLinkedHigherRankName[rankIndex][2]);
	    currentRank.setHigher(higherRank);
	    currentRank.setOptional(true);
	    /*
	    ranks.put(currentRank.getName(), currentRank);
	    sortedRanks.insertElementAt(currentRank, sortedRanks.indexOf(higherRank) + 1);
	    */
	    put(currentRank);
	    abbreviations.put(partiallyLinkedHigherRankName[rankIndex][0],
			      partiallyLinkedHigherRankName[rankIndex][1]);
	}
	for(rankIndex = 0; rankIndex < partiallyLinkedLowerRankName.length; rankIndex++) {
	    currentRank = new Rank(partiallyLinkedLowerRankName[rankIndex][0], null, null);
	    currentRank.setAbbreviation(partiallyLinkedHigherRankName[rankIndex][1]);
	    Rank lowerRank = get(partiallyLinkedLowerRankName[rankIndex][2]);
	    currentRank.setLower(lowerRank);
	    currentRank.setOptional(true);
	    /*
	    ranks.put(currentRank.getName(), currentRank);
	    sortedRanks.insertElementAt(currentRank, sortedRanks.indexOf(lowerRank));
	    */
	    put(currentRank);
	    abbreviations.put(partiallyLinkedLowerRankName[rankIndex][0],
			      partiallyLinkedLowerRankName[rankIndex][1]);
	}

	/*
	 * Create and put equivalent <code>Rank</code>s to the
	 * <code>Hashtable</code>
	 */
	for(rankIndex = 0; rankIndex < equivalents.length; rankIndex++) {
	    currentRank = new Rank(equivalents[rankIndex][0], 
				   get(equivalents[rankIndex][2]));
	    currentRank.setAbbreviation(partiallyLinkedHigherRankName[rankIndex][1]);
	    ranks.put(currentRank.getName(), currentRank);
	    sortedRanks.add(sortedRanks.indexOf(currentRank.getEquivalent()) + 1, currentRank);
	    //    put(currentRank);
	    abbreviations.put(equivalents[rankIndex][0],
			      equivalents[rankIndex][1]);
	}

	// pseudo rank for specimen
	currentRank = new Rank("specimen");
	ranks.put("specimen", currentRank);
	sortedRanks.add(currentRank);
	
	//anonymous rank
	currentRank = new Rank("");
	ranks.put("", currentRank);
	
	get("tribe").setOptional(true);
	get("section").setOptional(true);
	get("series").setOptional(true);

	currentRank = null;

    }

    public static final Rank UNRANKED  = ranks.get("");
    public static final Rank DOMAIN  = ranks.get("domain");
    public static final Rank KINGDOM = ranks.get("kingdom");
    public static final Rank PHYLUM  = ranks.get("phylum");
    public static final Rank CLASS   = ranks.get("class");
    public static final Rank ORDER   = ranks.get("order");
    public static final Rank FAMILY  = ranks.get("family");
    public static final Rank TRIBE   = ranks.get("tribe"); 
    public static final Rank GENUS   = ranks.get("genus");
    public static final Rank SECTION = ranks.get("section");
    public static final Rank SERIES  = ranks.get("series");
    public static final Rank SPECIES = ranks.get("species"); 
    public static final Rank VARIETY = ranks.get("variety"); 
    public static final Rank FORM    = ranks.get("form");
    public static final Rank SPECIMEN = ranks.get("specimen");


    /**
     * Constructs a species <code>Rank</code> as default.
     */
    public Rank()
    {
	//this("species");
	this("");
    }

    /**
     * Constructs a <code>Rank</code> with given <code>name</code>
     * pointing itself as both higher and lower <code>Rank</code>
     *
     * @param name name of the rank
     */
    public Rank(String name)
    {
	this(name, false);
    }

    /**
     * Constructs a <code>Rank</code> with given <code>name</code>
     * pointing itself as both higher and lower <code>Rank</code>
     *
     * @param name name of the rank
     */
    public Rank(String name, boolean autoLink)
    {
	this(name, null, null);

	Rank higher = null;
	Rank lower  = null;

	if(!autoLink) {
	    higher = this;
	    lower  = this;
	}
	else {
	    if(name.startsWith("super")) {
		lower = get(name.substring(5));
		if(lower != null)
		    put(this);
	    }
	    else if (name.startsWith("sub")) {
		higher = get(name.substring(3));
		if(higher != null)
		    put(this);
	    }
	    else if(name.startsWith("infra")) {
		String baseRankName = name.substring(5);
		higher = get(baseRankName);
		if(higher != null) {
		    String nextRankName = new String("sub" + baseRankName);
		    higher = get(nextRankName);
		    if(higher == null) {
			higher = get(baseRankName);
			Rank subRank = new Rank(nextRankName, null, null);
			subRank.setHigher(higher);
			put(subRank);
			higher = subRank;
		    }
		    put(this);
		}
	    }
	}
	setHigher(higher);
	setLower(lower);
    }

    /**
     * Construts a <code>Rank</code> with given <code>name</code>
     * pointing to an <code>equivalent</code> <code>Rank</code>
     *
     * @param name name of the rank
     * @param equivalent equivalent <code>Rank</code>
     */
    public Rank(String name, Rank equivalent)
    {
	this(name, null, null, equivalent);
    }

    /**
     * Construts a <code>Rank</code> with given <code>name</code>
     * pointing to <code>higher</code> and <code>lower</code>
     * <code>Rank</code>s
     *
     * @param name name of the rank
     * @param higher higher <code>Rank</code>
     * @param lower lower <code>Rank</code>
     */
    public Rank(String name, Rank higher, Rank lower)
    {
	this(name, higher, lower, null);
    }

    /**
     * Construts a <code>Rank</code> with given <code>name</code>
     * pointing to <code>higher</code>, <code>lower</code> and
     * <code>equivalent</code> <code>Rank</code>s
     *
     * @param name name of the rank
     * @param higher higher <code>Rank</code>
     * @param lower lower <code>Rank</code>
     * @param equivalent equivalent <code>Rank</code>
     */
    public Rank(String name, Rank higher, Rank lower, Rank equivalent)
    {
	setEquivalent(equivalent);
	setHigher(higher);
	setLower(lower);
	setName(name);
    }

    /**
     * Replaces a <code>Rank</code> in the <code>Hashtable</code>
     * with given <code>rank</code>
     *
     * @param rank to be replaced
     *
     */
    public static void put(Rank rank)
    {
	if(rank ==  null)
	    return;

	if(ranks.get(rank.getName()) != null)
	    return;

	ranks.put(rank.getName().toLowerCase(), rank);

	Rank next = rank.getEquivalent();
	if(next == null) {
	    next = rank.getHigher();
	    if(next != null) {
		sortedRanks.add(sortedRanks.indexOf(next) + 1, rank);
	    }
	    else {
		next = rank.getLower();
		if(next != null) {
		    sortedRanks.add(sortedRanks.indexOf(next), rank);
		}
		else {
		    next = rank.getHigher();
		    if(next != null) {
			sortedRanks.add(sortedRanks.indexOf(next) + 1, rank);
		    }
		    else {
			sortedRanks.add(sortedRanks.size(), rank);
		    }
		}
	    }
	}
	else {
	    sortedRanks.add(sortedRanks.indexOf(next) + 1, rank);
	}
	rank.addObserver(rankObserver);
	rank.setChanged();
	//	rank.notifyObservers(getRankNameArray());
	rank.notifyObservers(rank);
    }

    /**
     * Removes <code>rank</code> from the <code>Hashtable</code>
     *
     * @param rank to be removed
     *
     */
    public static void remove(Rank rank)
    {
	if(rank == null)
	    return;

	sortedRanks.remove(rank);

	ranks.remove(rank.getName());
    }

    /**
     * Removes <code>Rank</code> having <code>rankName</code> from the <code>Hashtable</code>
     *
     * @param rankName name of <code>Rank</code> to be removed
     *
     */
    public static void remove(String rankName)
    {
	remove(get(rankName));
    }

    /**
     * Returns a <code>Rank</code> of given <code>name</code>
     * in the <code>Hashtable</code>
     *
     * @return a <code>Rank</code> of given <code>name</code> or null
     * if no <code>Rank</code> in the <code>Hashtable</code> has the <code>name</code>.
     */
    public static Rank get(String name)
    {
	if(name == null)
	    return null;


	Rank rank = ranks.get(name);
	if(rank == null) {
	    name = name.toLowerCase();
	    rank = ranks.get(name);
	}
	if(rank == null) {
	    String baseName = null;
	    if(name.startsWith("sup")) {
		if(name.startsWith("super"))
		    baseName = name.substring("super".length());
		else {
		    baseName = name.substring("sup".length());
		    name = new StringBuffer("super").append(baseName).toString();
		}
		rank = ranks.get(baseName);
		if(rank != null) {
		    rank = new Rank(name, null, rank);
		    ranks.put(name, rank);
		    baseName = new StringBuffer("super").append(baseName).toString();
		    abbreviations.put(name, baseName);
		}
	    }
	    else if(name.startsWith("sub")) {
		baseName = name.substring("sub".length());
		rank = ranks.get(baseName);
		if(rank != null) {
		    rank = new Rank(name, rank, null);
		    ranks.put(name, rank);
		    abbreviations.put(name, name);
		}
	    }
	    else if(name.startsWith("inf")) {
		if(name.startsWith("infra"))
		    baseName = name.substring("infra".length());
		else {
		    baseName = name.substring("inf".length());
		    name = new StringBuffer("infra").append(baseName).toString();
		}
		String subName = 
		    new StringBuffer("sub").append(baseName).toString();
		rank = ranks.get(subName);
		if(rank == null) {
		    rank = ranks.get(baseName);
		    if(rank != null) {
			rank = new Rank(subName, rank, null);
			ranks.put(name, rank);
			abbreviations.put(subName, subName);
		    }
		}
		if(rank != null) {
		    rank = new Rank(name, rank, null);
		    ranks.put(name, rank);
		    subName = new StringBuffer("inf").append(subName).toString();
		    abbreviations.put(name, subName);
		}
	    }
	       
	}

	return rank;
    }

    /**
     * Sets name to be given <code>name</code>
     * and replace <code>Rank</code> in the <code>Hashtable</code> 
     * keyed by the name to this
     *
     * @param name to be set
     */
    public void setName(String name)
    {
	if(name != null)
	    name = name.trim().toLowerCase(Locale.ENGLISH);
	if(name != null && name.equals(this.name))
	    return;

	this.name = name;
	if(this.name != null) {
	    int length = this.name.length();
	    if(length > maxLength)
		maxLength = length;
	    if(length < minLength)
		minLength = length;
	}

	setChanged();
	notifyObservers(this.name);
    }

    /**
     * Returns the name of the <code>Rank</code>
     *
     * @return name <code>String</code> of the <code>Rank</code>
     */
    public String getName()
    {
	return name;
    }

    /**
     * Sets abbreviation to be given <code>abbreviation</code>
     * and replace <code>Rank</code> in the <code>Hashtable</code> 
     * keyed by the abbreviation to this
     *
     * @param abbreviation to be set
     */
    public void setAbbreviation(String abbreviation)
    {
	if(abbreviation != null && abbreviation.equals(this.abbreviation))
	    return;

	this.abbreviation = abbreviation;

	setChanged();
	notifyObservers(this.abbreviation);
    }

    /**
     * Returns the abbreviation of the <code>Rank</code>
     *
     * @return abbreviation <code>String</code> of the <code>Rank</code>
     */
    public String getAbbreviation()
    {
	return abbreviation;
    }

    public boolean isOptional()
    {
	return optional;
    }

    public void setOptional(boolean state)
    {
	optional = state;
    }

    /**
     * Sets higher rank to be given <code>higher</code> <code>Rank</code>.
     *
     * @param higher <code>Rank</code>
     */
    public void setHigher(Rank higher)
    {
	this.higher = higher;
    }

    /**
     * Returns higher rank of this <code>Rank</code>.
     *
     * @return higher <code>Rank</code>, of null if no
     * <code>Rank</code> is pointed as higher <code>Rank</code>
     */
    public Rank getHigher()
    {
	return higher;
    }

    /**
     * Sets lower rank to be given <code>lower</code> <code>Rank</code>.
     *
     * @param lower <code>Rank</code>
     */
    public void setLower(Rank lower)
    {
	this.lower = lower;
    }

    /**
     * Returns lower rank of this <code>Rank</code>.
     *
     * @return lower <code>Rank</code>, of null if no
     * <code>Rank</code> is pointed as lower <code>Rank</code>
     */
    public Rank getLower()
    {
	return lower;
    }

    /**
     * Sets equivalent rank to be given <code>equivalent</code> <code>Rank</code>.
     *
     * @param equivalent <code>Rank</code>
     */
    public void setEquivalent(Rank equivalent)
    {
	this.equivalent = equivalent;
    }

    /**
     * Returns a <code>Rank</code> equivalent to this <code>Rank</code>.
     *
     * @return <code>Rank</code> equivalent to this, or null if no
     * <code>Rank</code> is equivalent to this
     */
    public Rank getEquivalent()
    {
	return equivalent;
    }

    /**
     * Returns int indicating coverage by Codes
     *
     * @return int indicating coverage by Codes
     */
    public int getCodeCoverage()
    {
	return codeCoverage;
    }

    /**
     * Sets <CODE>coverage</CODE> as coverage by Codes
     *
     * @param coverage <CODE>int</CODE> indicating coverage by Codes
     */
    public void setCodeCoverage(int coverage)
    {
	codeCoverage = coverage;
    }

    /**
     * Returns int indicating naming convention of th <CODE>Rank</CODE>
     *
     * @return int indicating naming convention
     */
    public int getNamingConvention()
    {
	return namingConvention;
    }

    /**
     * Sets <CODE>convention</CODE> as naming convention for the <CODE>Rank</CODE>
     *
     * @param convention <CODE>int</CODE> indicating naming convention
     */
    public void setNamingConvention(int convention)
    {
	namingConvention = convention;
    }


    /**
     * Returns true if the <CODE>Rank</CODE> is
     * covered by <CODE>code</CODE>
     *
     * @param code <CODE>int</CODE> indicating a Code
     *
     * @return ture if the <CODE>Rank</CODE> is covered by <CODE>code</CODE>
     */
    public boolean isCovered(int code)
    {
	return ((codeCoverage & code) != 0);
    }

    public String[] getEndings()
    {
	if(endings == null)
	    return null;
	String[] copy = new String[endings.length];
	System.arraycopy(endings, 0, copy, 0, endings.length);
	return copy;
    }

    public void setEndings(String[] endings)
    {
	if(this.endings == endings)
	    return;

	if (endings == null) {
	    for(int i = 0; i < this.endings.length; i++)
		this.endings[i] = null;
	    this.endings = null;
	    return;
	}

	if(this.endings == null || this.endings.length != endings.length) {
	    if(this.endings != null) {
		for(int i = 0; i < this.endings.length; i++)
		    this.endings[i] = null;
	    }
	    this.endings = new String[endings.length];
	}

	System.arraycopy(endings, 0, this.endings, 0, endings.length);
    }

    /**
     * Returns <CODE>String</CODE> representing name ending
     * for taxa of the <CODE>Rank</CODE> in given
     * <CODE>convention</CODE>, or null if it is not available.
     *
     * @param convention <CODE>int</CODE> representing a naming convention
     *
     * @return String representing name ending
     * for taxa of the <CODE>Rank</CODE> in
     * <CODE>convention</CODE>, or null
     * if it is not available.
     */
    public String getEnding(int convention)
    {
	String ending = null;
	if(endings != null) {
	    switch(convention) {
	    case ZOOLOGICAL:
		ending = endings[0];
		break;
	    case BOTANICAL:
		ending = endings[1];
		break;
	    case ALGAL:
		ending = endings[2];
		break;
	    case FUNGUS:
		ending = endings[3];
		break;
	    case BACTRIOLOGICAL:
		ending = endings[4];
		break;
	    case VIROLOGICAL:
		ending = endings[5];
		break;
	    }
	}
	return ending;
    }

    /**
     * Returns <CODE>String</CODE> representing name ending
     * for taxa of the <CODE>Rank</CODE> in given
     * <CODE>convention</CODE>, or null if it is not available.
     *
     * @param convention <CODE>int</CODE> representing a naming convention
     *
     * @return String representing name ending
     * for taxa of the <CODE>Rank</CODE> in
     * <CODE>convention</CODE>, or null
     * if it is not available.
     */
    public void setEnding(String ending, int convention)
    {
	if(endings != null) {
	    endings = new String[6];
	}

	switch(convention) {
	case ZOOLOGICAL:
	    endings[0] = ending;
	    break;
	case BOTANICAL:
	    endings[1] = ending;
	    break;
	case ALGAL:
	    endings[2] = ending;
	    break;
	case FUNGUS:
	    endings[3] = ending;
	    break;
	case BACTRIOLOGICAL:
	    endings[4] = ending;
	    break;
	case VIROLOGICAL:
	    endings[5] = ending;
	    break;
	}
    }

    /**
     * Returns whether this is fully linked to both higher and lower <code>Rank</code>s.
     * It also tries its equivalent <code>Rank</code> if given.
     *
     * @return true if both higher and lower <code>Rank</code> are non null,
     * or false if either is null
     */
    public boolean isFullyLinkedRank()
    {
	if(higher != null && lower != null)
	    return true;

	if(equivalent == null)
	    return false;

	return equivalent.isFullyLinkedRank();
    }

    /**
     * Returns nearest fully linked <code>Rank</code>
     *
     * @return nearest fully linked <code>Rank</code>
     */
    public Rank getFullyLinkedRank()
    {
	if(isFullyLinkedRank())
	    return this;

	if(equivalent != null)
	    return equivalent.getFullyLinkedRank();

	if(higher != null) 
	    return getHigher().getFullyLinkedRank();
	else if(lower != null)
	    return getLower().getFullyLinkedRank();
	else
	    return this; //or null ?
    }

    /**
     * Returns nearest fully linked <code>Rank</code>
     *
     * @return nearest fully linked <code>Rank</code>
     */
    public Rank getNonOptionalHigherRank()
    {
	if(isHighest() && !isOptional())
	    return this;
	Rank target = this;
	Rank higher = null;
	do {
	    higher = target.getHigher();
	    if(higher == null)
		higher =  target.getLower().getHigher();
	    if(higher.isHighest() && !higher.isOptional())
		return higher;
	    if(higher == null ||
	       (higher == target && target.isOptional()))
		higher = target.getLower().getFullyLinkedRank().getHigher();
	    target = higher.getFullyLinkedRank();
	} while(target.isOptional() && !target.isHighest());
	
	return target;
    }

    /**
     * Returns nearest fully linked <code>Rank</code>
     *
     * @return nearest fully linked <code>Rank</code>
     */
    public Rank getNonOptionalLowerRank()
    {
	if(isLowest() && !isOptional())
	    return this;
	Rank target = this;
	Rank lower = null;
	do {
	    lower = target.getLower();
	    if(lower == null)
		lower = target.getHigher().getLower();
	    if(lower.isLowest() && !lower.isOptional()) 
		return lower;
	    if(lower == null ||
	       (lower == target && target.isOptional())) {
		lower = target.getHigher().getFullyLinkedRank().getLower();
	    }
	    target = lower.getFullyLinkedRank();
	} while(target.isOptional() && !target.isLowest());
	
	return target;
    }

    /**
     * Returns whether this is the highest rank
     *
     * @return true if this is the highest rank
     */
    public boolean isHighest()
    {
	return (higher == this);
    }

    /**
     * Returns whether this is the lowest rank
     *
     * @return true if this is the lowest rank
     */
    public boolean isLowest()
    {
	return (lower == this);
    }

    /**
     * Returns whether this is higher than <code>rank</code>
     *
     * @param rank name of <code>Rank</code> to be compared
     *
     * @return true if this is higher than given <code>rank</code>
     */
    public boolean isHigher(String rank)
    {
	return isHigher(get(rank));
    }

    /**
     * Returns whether this is higher than given <code>rank</code>
     *
     * @param rank to be compared
     *
     * @return true if this is higher than given <code>rank</code>
     */
    public boolean isHigher(Rank rank)
    {
	if(rank == null || rank == this)
	    return false;

	Rank[]  path1 = getRankPath(this.getFullyLinkedRank());
	Rank[]  path2 = getRankPath(rank.getFullyLinkedRank());

	if(path1.length < path2.length)
	    return true;
	else if (path1.length > path2.length)
	    return false;

	path1 = getRankPath(this);
	path2 = getRankPath(rank);

	if(path1.length < path2.length)
	    return true;
	else if (path1.length > path2.length)
	    return false;

	Rank r1 = getFullyLinkedRank();
	Rank r2 = rank.getFullyLinkedRank();
	if(r1 == r2.getHigher())
	    return true; 
	else if (r2 == r1.getHigher())
	    return false;

	return false; //is it right?
    }

    /**
     * Returns whether this is lower than given <code>rank</code>
     *
     * @param rank name to be compared
     *
     * @return true if this is lower than given <code>rank</code>
     */
    public boolean isLower(String rank)
    {
	return isLower(get(rank));
    }

    /**
     * Returns whether this is lower than given <code>rank</code>
     *
     * @param rank to be compared
     *
     * @return true if this is lower than given <code>rank</code>
     */
    public boolean isLower(Rank rank)
    {
	if(rank == null || rank == this)
	    return false;

	Rank[]  path1 = getRankPath(this.getFullyLinkedRank());
	Rank[]  path2 = getRankPath(rank.getFullyLinkedRank());

	if(path1.length > path2.length)
	    return true;
	else if (path1.length < path2.length)
	    return false;

	path1 = getRankPath(this);
	path2 = getRankPath(rank);

	if(path1.length > path2.length)
	    return true;
	else if (path1.length < path2.length)
	    return false;

	Rank r1 = getFullyLinkedRank();
	Rank r2 = rank.getFullyLinkedRank();
	if(r1 == r2.getHigher())
	    return true; 
	else if (r2 == r1.getHigher())
	    return false;

	return false; //?
    }

    /**
     * Returns descending path from highest rank to
     * given <code>rank</code>
     *
     * @param rank to be tracked from top rank
     *
     * @return an Array of <code>Rank</code> indicating path from top to
     * given <code>rank</code>
     */
    public static Rank[] getRankPath(Rank rank)
    {
	if(rank == null)
	    return null;

	List<Rank> ascendingPath = new ArrayList<Rank>();

	if(!rank.isFullyLinkedRank()) {
	    if(rank.getHigher() == null) {
		ascendingPath.add(rank);
		rank = rank.getFullyLinkedRank().getHigher();
	    }
	    else {
		while(!rank.isFullyLinkedRank()) {
		    ascendingPath.add(rank);
		    rank = rank.getHigher();
		}
	    }
	}
	ascendingPath.add(rank);
	while(rank != null && !rank.isHighest()) {
	    rank = rank.getHigher();
	    ascendingPath.add(rank);
	}

	int i = ascendingPath.size();
	Rank[] rankArray = new Rank[i];
	Iterator<Rank> iterator = ascendingPath.iterator();
	while(iterator.hasNext()) {
	    rankArray[--i] = iterator.next();
	}

	return rankArray;
    }

    @Override
    public boolean equals(Object object) {
	if(object == this) return true;
	if(object == null) return false;
	if(getClass() != object.getClass()) return false;

	final Rank theOther = (Rank) object;
	return super.equals(object)
	    && Objects.equals(this.getName(), theOther.getName())
	    && Objects.equals(this.getAbbreviation(), theOther.getAbbreviation())
	    && Objects.equals(this.isOptional(), theOther.isOptional())
	    && Objects.equals(this.getCodeCoverage(),theOther.getCodeCoverage())
	    ;
    }

    @Override
    public int hashCode() {
	return Objects.hash(super.hashCode(),
			    getName(),
			    getAbbreviation(),
			    isOptional(),
			    getCodeCoverage()
			    );
    }


    /**
     * Returnes an array of rank names appropriate to <code>TextListModel</code>.
     * 
     * @return Array of <code>String</code> representing rank names appropriate to <code>TextListModel</code>.
     */
    public static final String[][] getRankNameArray()
    {
	Iterator<Rank> iterator = sortedRanks.iterator();
	String names[][] = new String[sortedRanks.size()][1];
	int index = 0;
	while(iterator.hasNext()) {
	    names[index++] = new String[]{iterator.next().getName()};
	}
	return names;
    }

    /**
     * Returns <code>String</code> representing rank name
     *
     * @return String representing rank name
     */
    public String toString()
    {
	return getName();
    }

    /**
     * Adds <code>observer</code> to <code>Observer</code> list
     * 
     * @param observer <code>Observer</code> to be added to the list
     */
    public void addObserver(Observer observer)
    {
	rankObserver.addObserver(observer);
    }

    /**
     * Removes <code>observer</code> from <code>Observer</code> list
     * 
     * @param observer <code>Observer</code> to be removed from the list
     */
    public void deleteObserver(Observer observer)
    {
	rankObserver.deleteObserver(observer);
    }

    /**
     * Adds <code>observer</code> to <code>Observer</code> list
     * observing this
     * 
     * @param observer <code>Observer</code> to be added to the list
     */
    public void addObserver(RankObserver observer)
    {
	super.addObserver(observer);
    }

    /**
     * Removes <code>observer</code> from <code>Observer</code> list
     * observing this
     * 
     * @param observer <code>Observer</code> to be removed from the list
     */
    public void deleteObserver(RankObserver observer)
    {
	super.deleteObserver(observer);
    }

    /**
     * Returns highest <code>Rank</code>
     *
     * @return <code>Rank</code> at top of rank hierarchy
     */
    public static Rank getHighest()
    {
	return sortedRanks.get(0);
    }

    /**
     * Returns a List of sorted <CODE>Rank</CODE>s
     *
     * @return List of sorted <CODE>Rank</CODE>s
     */
    public static final List<Rank> getSortedRanks()
    {
	return sortedRanks;
    }

    /**
     * Returns an array of sorted <CODE>Rank</CODE>s
     *
     * @return array of sorted <CODE>Rank</CODE>s
     */
    public static Rank[] getSortedArray()
    {
	int ranks = sortedRanks.size();
	Rank[] sorted = new Rank[ranks];
	System.arraycopy(sortedRanks.toArray(), 0, sorted, 0, ranks);
	return sorted;
    }

    /**
     * Returns an array of <CODE>String</CODE>s
     * representing sorted <CODE>Rank</CODE>s' name
     *
     * @return array of <CODE>Rank</CODE>s' name
     */
    public static String[] getRankNames()
    {
	String[] sortedRankNames = new String[sortedRanks.size()];
	int index = 0;
	Iterator<Rank> iterator = sortedRanks.iterator();
	while(iterator.hasNext()) {
	    sortedRankNames[index++] = iterator.next().getName();
	}
	return sortedRankNames;
    }

    /**
     * Returns the <CODE>Observerable</CODE> observing
     * <CODE>Rank</CODE> providing a satic proxy to observe
     * <CODE>Rank</CODE>
     *
     * @return Observable observing <CODE>Rank</CODE>
     */
    public static Observable getRankObserver()
    {
	return rankObserver;
    }

    public static String getAbbreviation(String rank)
    {
	String abbrev = abbreviations.get(rank);
	Rank r = null;
	if(abbrev == null) {
	    r = get(rank);
	    if(r != null)
		abbrev = r.getAbbreviation();
	}

	if(abbrev == null || abbrev.equals(rank)) {
	    String[] parts = rank.split("-");
	    if(parts.length < 2) {
		if(rank.startsWith("super")) {
		    parts = new String[2];
		    parts[0] = rank.substring(0, 5);
		    parts[1] = rank.substring(5, rank.length());
		}
		else if(rank.startsWith("sub")) {
		    parts = new String[2];
		    parts[0] = rank.substring(0, 3);
		    parts[1] = rank.substring(3, rank.length());
		}
	    }
	    if(parts.length > 1) {
		r = Rank.get(parts[1]);
		if(r == null) {
		    abbrev = parts[0] + parts[1];
		}
		else {
		    if(parts[0].equals("super"))
			parts[0] = "sup";
		    abbrev = parts[0] + r.getAbbreviation();
		}
	    }
	    else
		abbrev = rank;
	}
	return abbrev;
    }

    //need reconsider....
    public static String getUnabbreviation(String rank)
    {
	Iterator<String> iterator = abbreviations.keySet().iterator();
	String target = null;
	while(iterator.hasNext()) {
	    target = iterator.next();
	    if(rank.equals(abbreviations.get(target)))
		break;
	}
	if(rank.equals(abbreviations.get(target)))
	    return target;

	return null;
    }

    public static String getRank(NameUsage<?, ?> usage)
    {
	if(usage != null) {
	    Rank rank = usage.getRank();
	    if(rank != null)
		return rank.getName();
	}
	return "";
    }

}

class RankObserver
    extends Observable
    implements Observer
{
    public RankObserver()
    {
	super();
    }

    public void update(Observable observer, Object arg)
    {
	setChanged();
	notifyObservers(arg);
    }

}
