/*
 * Rank.java:  a Java implementation of Rank class
 * for TaxoNote, an user interface model for Nomencurator
 *
 * Copyright (c) 2001, 2002, 2003, 2004, 2005, 2006, 2014, 2015, 2016, 2019 Nozomi `James' Ytow
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;

import org.nomencurator.beans.PropertyChanger;

/**
 * Rank provides rank tracking without parsing name of rank
 *
 * @version 	06 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public class Rank
    extends PropertyChanger    
    implements Serializable, PropertyChangeListener
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

    /** Nomenclature codes */
    public enum NomenclatureCode {
    /** Constant representing ICBN */
	ICBN(1),
    /** Constant representing ICNB */
	ICNB(2),
    /** Constant representing ICNCP */
	ICNCP(4),
    /** Constant representing ICNV */
	ICNV(8),
    /** Constant representing ICZN */
	ICZN(0x10);

	private int id;

	private NomenclatureCode(int id)
	{
	    this.id = id;
	}

	public int getId() { return id; }
    }
    
    /** Code compliance of the rank */
    protected HashSet<NomenclatureCode> codeCompliance;

    /** Nomenclatural conventions */
    public enum NomenclatureConvention {
	/** Constant representing zoological naming convention */
	Zoological(1),
	/** Constant representing botanical naming convention */
	Botanical(2),
	/** Constant representing algal naming convention */
	Algal(4),
	/** Constant representing fungus naming convention */
	Fungul(8),
	/** Constant representing bacteriological naming convention */
	Bactriological(0x10),
	/** Constant representing virological naming convention */
	Virological(0x20);

	private int id;

	private NomenclatureConvention(int id)
	{
	    this.id = id;
	}

	public int getId() { return id; }

	/** name endings */
	protected static HashMap<NomenclatureConvention, String> endings
	= new HashMap<NomenclatureConvention, String>(NomenclatureConvention.values().length);

	public static Collection<String> getEndings()
	{
	    return endings.values();
	}

	public static boolean addEnding(NomenclatureConvention convention, String ending)
	{
	    return !Objects.equals(ending, endings.put(convention, ending));
	}

	/**
	 * Returns {@code String} representing name ending
	 * for taxa of the {@code Rank} in given
	 * {@code convention}, or null if it is not available.
	 *
	 * @param convention {@code int} representing a naming convention
	 *
	 * @return String representing name ending
	 * for taxa of the {@code Rank} in
	 * {@code convention}, or null
	 * if it is not available.
	 */
	public static String getEnding(NomenclatureConvention convention)
	{
	    return endings.get(convention);
	}
    }
    
    /** Code relevant information */
    protected HashSet<NomenclatureConvention> convention;

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
     * {@code Hashtable} to hold {@code Rank}s
     */
    static Map<String, Rank> ranks = new HashMap<String, Rank>();

    /**
     * {@code Hashtable} to hold abbreviations of rank name
     */
    static Map<String, String> abbreviations = new HashMap<String, String>();

    /**
     * {@code Vector} to hold {@code Rank}s
     */
    static List<Rank> sortedRanks = Collections.synchronizedList(new ArrayList<Rank>());

    static final RankObserver rankObserver = new RankObserver();

    /**
     * Returns the {@link RankObserver} observing
     * {@code Rank} providing a satic proxy to observe
     * {@code Rank}
     *
     * @return {@link RankObserver} observing {@code Rank}
     */
    public static RankObserver getRankObserver()
    {
	return rankObserver;
    }

    static {
	/*
	 * Put deafult {@code Rank}s to the {@code Hashtable}
	 * with making links to upper and lower {@code Rank}s
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
	 * Create and put partially linked {@code Rank}s to the
	 * {@code Hashtable}
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
	 * Create and put equivalent {@code Rank}s to the
	 * {@code Hashtable}
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
     * Constructs a species {@code Rank} as default.
     */
    public Rank()
    {
	//this("species");
	this("");
    }

    /**
     * Constructs a {@code Rank} with given {@code name}
     * pointing itself as both higher and lower {@code Rank}
     *
     * @param name name of the rank
     */
    public Rank(String name)
    {
	this(name, false);
    }

    /**
     * Constructs a {@code Rank} with given {@code name}
     * pointing itself as both higher and lower {@code Rank}
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
     * Construts a {@code Rank} with given {@code name}
     * pointing to an {@code equivalent} {@code Rank}
     *
     * @param name name of the rank
     * @param equivalent equivalent {@code Rank}
     */
    public Rank(String name, Rank equivalent)
    {
	this(name, null, null, equivalent);
    }

    /**
     * Construts a {@code Rank} with given {@code name}
     * pointing to {@code higher} and {@code lower}
     * {@code Rank}s
     *
     * @param name name of the rank
     * @param higher higher {@code Rank}
     * @param lower lower {@code Rank}
     */
    public Rank(String name, Rank higher, Rank lower)
    {
	this(name, higher, lower, null);
    }

    /**
     * Construts a {@code Rank} with given {@code name}
     * pointing to {@code higher}, {@code lower} and
     * {@code equivalent} {@code Rank}s
     *
     * @param name name of the rank
     * @param higher higher {@code Rank}
     * @param lower lower {@code Rank}
     * @param equivalent equivalent {@code Rank}
     */
    public Rank(String name, Rank higher, Rank lower, Rank equivalent)
    {
	setEquivalent(equivalent);
	setHigher(higher);
	setLower(lower);
	setName(name);
    }

    /**
     * Replaces a {@code Rank} in the {@code Hashtable}
     * with given {@code rank}
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
	rank.addPropertyChangeListener(rankObserver);
	rankObserver.addPropertyChangeListener(rank);
	rankObserver.propertyChange(new PropertyChangeEvent(rank, "put", null, null));
    }

    /**
     * Removes {@code rank} from the {@code Hashtable}
     *
     * @param rank to be removed
     *
     */
    public static void remove(Rank rank)
    {
	if(rank == null)
	    return;

	rank.removePropertyChangeListener(rankObserver);	
	rankObserver.removePropertyChangeListener(rank);
	sortedRanks.remove(rank);
	ranks.remove(rank.getName());
	rankObserver.propertyChange(new PropertyChangeEvent(rank, "remove", null, null));	
    }

    /**
     * Removes {@code Rank} having {@code rankName} from the {@code Hashtable}
     *
     * @param rankName name of {@code Rank} to be removed
     *
     */
    public static void remove(String rankName)
    {
	remove(get(rankName));
    }

    /**
     * Returns a {@code Rank} of given {@code name}
     * in the {@code Hashtable}
     *
     * @return a {@code Rank} of given {@code name} or null
     * if no {@code Rank} in the {@code Hashtable} has the {@code name}.
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
     * Sets name to be given {@code name}
     * and replace {@code Rank} in the {@code Hashtable} 
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

	String oldName = this.name;
	this.name = name;
	if(this.name != null) {
	    int length = this.name.length();
	    if(length > maxLength)
		maxLength = length;
	    if(length < minLength)
		minLength = length;
	}

	rankObserver.propertyChange(new PropertyChangeEvent(this, "name", oldName, name));
    }

    /**
     * Returns the name of the {@code Rank}
     *
     * @return name {@code String} of the {@code Rank}
     */
    public String getName()
    {
	return name;
    }

    /**
     * Sets abbreviation to be given {@code abbreviation}
     * and replace {@code Rank} in the {@code Hashtable} 
     * keyed by the abbreviation to this
     *
     * @param abbreviation to be set
     */
    public void setAbbreviation(String abbreviation)
    {
	if(abbreviation != null && abbreviation.equals(this.abbreviation))
	    return;

	String oldAbbreviation = this.abbreviation;
	this.abbreviation = abbreviation;

	rankObserver.propertyChange(new PropertyChangeEvent(this, "abbreviation", oldAbbreviation, abbreviation));
    }

    /**
     * Returns the abbreviation of the {@code Rank}
     *
     * @return abbreviation {@code String} of the {@code Rank}
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
	if(optional == state)
	    return;

	boolean oldState = optional;
	optional = state;
	rankObserver.propertyChange(new PropertyChangeEvent(this, "optional", Boolean.valueOf(oldState), Boolean.valueOf(state)));
    }

    /**
     * Sets higher rank to be given {@code higher} {@code Rank}.
     *
     * @param higher {@code Rank}
     */
    public void setHigher(Rank higher)
    {
	if (this.higher == higher)
	    return;

	Rank oldHigher = this.higher;;	
	this.higher = higher;
	rankObserver.propertyChange(new PropertyChangeEvent(this, "higher",  oldHigher, higher));
    }

    /**
     * Returns higher rank of this {@code Rank}.
     *
     * @return higher {@code Rank}, of null if no
     * {@code Rank} is pointed as higher {@code Rank}
     */
    public Rank getHigher()
    {
	return higher;
    }

    /**
     * Sets lower rank to be given {@code lower} {@code Rank}.
     *
     * @param lower {@code Rank}
     */
    public void setLower(Rank lower)
    {
	if (this.lower == lower)
	    return;

	Rank oldLower = this.lower;;	
	this.lower = lower;
	rankObserver.propertyChange(new PropertyChangeEvent(this, "lower",  oldLower, lower));
    }

    /**
     * Returns lower rank of this {@code Rank}.
     *
     * @return lower {@code Rank}, of null if no
     * {@code Rank} is pointed as lower {@code Rank}
     */
    public Rank getLower()
    {
	return lower;
    }

    /**
     * Sets equivalent rank to be given {@code equivalent} {@code Rank}.
     *
     * @param equivalent {@code Rank}
     */
    public void setEquivalent(Rank equivalent)
    {
	if (this.equivalent == equivalent)
	    return;

	Rank oldEquivalent = this.equivalent;;	
	this.equivalent = equivalent;
	rankObserver.propertyChange(new PropertyChangeEvent(this, "equivalent",  oldEquivalent, equivalent));
    }

    /**
     * Returns a {@code Rank} equivalent to this {@code Rank}.
     *
     * @return {@code Rank} equivalent to this, or null if no
     * {@code Rank} is equivalent to this
     */
    public Rank getEquivalent()
    {
	return equivalent;
    }

    /**
     * Returns {@code Iterator} of nomenclatural Codes to which the rank is compliant.
     *
     * @return {@code Iterator} of compliant nomenclatural Codes 
     */
    public Iterator<NomenclatureCode> getCodeCompliance()
    {
	if(Objects.isNull(codeCompliance))
	    codeCompliance = new HashSet<NomenclatureCode>(NomenclatureCode.values().length);
	return codeCompliance.iterator();
    }

    /**
     * Declare that the rank is Code compliant under the {@code nomenclatureCode}.
     *
     * @param {@code nomenclatureCode} {@link NomenclatureCode} to which the rank is Code compliant 
     * @return {@code true} if the nomenclature Code specified successfully, 
     * or {@code false} if the nomenclatural Code is already specified, or specified Code is {@code null}
     */
    public boolean addCodeCompliance(NomenclatureCode nomenclatureCode)
    {
	if (Objects.isNull(nomenclatureCode))
	    return false;
	if(Objects.isNull(codeCompliance))
	    codeCompliance = new HashSet<NomenclatureCode>(NomenclatureCode.values().length);
	return codeCompliance.add(nomenclatureCode);
    }

        /**
     * Declare that the rank is Code complieant under the {@code nomenclatureCode}.
     *
     * @param {@code nomenclatureCode} {@link NomenclatureCode} to which the rank is Code compliant 
     * @return {@code true} if the nomenclature Code specified successfully, 
     * or {@code false} if the nomenclatural Code is already specified, or specified Code is {@code null}
     */
    public boolean addCodeCompliance(int nomenclatureCode)
    {
	boolean result = true;
	for (NomenclatureCode c: NomenclatureCode.values()) {
	    if (0 != (c.getId() & nomenclatureCode)) {
		result &= addCodeCompliance(c);
	    }
	}

	return result;
    }

    /**
     * Declare that the rank is not Code complieant under the {@code nomenclatureCode}.
     *
     * @param {@code nomenclatureCode} {@link NomenclatureCode} to which the rank is not Code compliant 
     * @return {@code true} if the rank was made Code incompliant under the {@code nomenclatureCode}
     */
    public boolean removeCodeCompliance(NomenclatureCode nomenclatureCode)
    {
	if (Objects.isNull(nomenclatureCode) || Objects.isNull(codeCompliance))
	    return false;

	return codeCompliance.remove(nomenclatureCode);
    }

    /**
     * Declare that the rank is Code complieant under the {@code nomenclatureCode}.
     *
     * @param {@code nomenclatureCode} {@link NomenclatureCode} to which the rank is Code compliant 
     * @return {@code true} if the nomenclature Code specified successfully, 
     * or {@code false} if the nomenclatural Code is already specified, or specified Code is {@code null}
     */
    public boolean setCodeCompliance(NomenclatureCode nomenclatureCode)
    {
	return addCodeCompliance(nomenclatureCode);
    }
    
    /**
     * Declare that the rank is Code complieant under the {@code nomenclatureCodes}.
     *
     * @param {@code nomenclatureCode} {@link Collection} of {@link NomenclatureCode}s to which the rank is Code compliant 
     * @return {@code true} if the nomenclature Code specified successfully, 
     * or {@code false} if the nomenclatural Code is already specified, or specified Code is {@code null}
     */
    public boolean setCodeCompliance(Collection<NomenclatureCode> nomenclatureCodes)
    {
	if (Objects.isNull(nomenclatureCodes))
	    return false;
	if(Objects.isNull(codeCompliance)) {
	    codeCompliance = new HashSet<NomenclatureCode>(nomenclatureCodes);
	    return true;
	}
	else if (!codeCompliance.isEmpty()) {
	    codeCompliance.clear();
	}
	return codeCompliance.addAll(nomenclatureCodes);
    }

    /**
     * Declare that the rank is Code complieant under the {@code nomenclatureCodes}.
     *
     * @param {@code nomenclatureCode} {@code int} as bit OR of {@link NomenclatureCode} id.
     * @return {@code true} if the nomenclature Code specified successfully, 
     * or {@code false} if the nomenclatural Code is already specified, or specified Code is {@code null}
     */
    public boolean setCodeCompliance(int nomenclatureCodes)
    {
	if(Objects.isNull(codeCompliance)) {
	    codeCompliance = new HashSet<NomenclatureCode>(NomenclatureCode.values().length);
	}
	else if (!codeCompliance.isEmpty()) {
	    codeCompliance.clear();
	}
	return addCodeCompliance(nomenclatureCodes);
    }

    /**
     * Returns wheter the rank is Code complieant with the {@code nomenclatureCode}. 
     *
     * @param {@code nomenclatureCode} {@link NomenclatureCoe} to test Code compliant of the rank 
     * @return {@code true} if the rank is Code compliant under the {@code nomenclatureCode}.
     * or {@code null}, if either the rank is Code incopmilant with the {@code nomenclatureCode}
     * or  {@code nomenclatureCode} is {@code null}
     */
    public boolean isCompliant(NomenclatureCode nomenclatureCode)
    {
	if (Objects.isNull(nomenclatureCode) || Objects.isNull(codeCompliance))
	    return false;

	return codeCompliance.contains(nomenclatureCode);
    }

    /**
     * Returns {@code Iterator} of nomenclatural conventions relevant to the rank.
     *
     * @return {@code Iterator} of relevant nomenclatural conventions.
     */
    public Iterator<NomenclatureConvention> getConventions()
    {
	if(Objects.isNull(convention))
	    convention = new HashSet<NomenclatureConvention>(NomenclatureConvention.values().length);
	return convention.iterator();
    }

    /**
     * Declare that the rank is used in the {@code nomenclatureConvention}.
     *
     * @param {@code nomenclatureConvention} {@link NomenclatureConvention} using the {@code Rank}
     * @return {@code true} if the nomenclature convention is dclared to use {@code Rank} successfully, 
     * or {@code false} if the nomenclatural convention is already declared, or specified convention is {@code null}
     */
    public boolean addConvention(NomenclatureConvention nomenclatureConvention)
    {
	if (Objects.isNull(nomenclatureConvention))
	    return false;
	if(Objects.isNull(convention))
	    convention = new HashSet<NomenclatureConvention>(NomenclatureConvention.values().length);
	return convention.add(nomenclatureConvention);
    }

    /**
     * Declare that the rank is used in the {@code nomenclatureConvention}.
     *
     * @param {@code nomenclatureConventions} id of {@link NomenclatureConvention} to add.  It could be bit OR of ids to add multiple conventions.
     * @return {@code true} if the nomenclature convention is dclared to use {@code Rank} successfully, 
     * or {@code false} if the nomenclatural convention is already declared, or specified convention is {@code null}
     */
    public boolean addConvention(int nomenclatureConventions)
    {
	boolean result = true;
	for (NomenclatureConvention c: NomenclatureConvention.values()) {
	    if (0 != (c.getId()  & nomenclatureConventions)) {
		result &= addConvention(c);
	    }
	}
	return result;
    }

    /**
     * Declare that the rank is used in the {@code nomenclatureConvention}.
     *
     * @param {@code nomenclatureConventions} id of {@link NomenclatureConvention} to add.  It could be bit OR of ids to add multiple conventions.
     * @return {@code true} if the nomenclature convention is dclared to use {@code Rank} successfully, 
     * or {@code false} if the nomenclatural convention is already declared, or specified convention is {@code null}
     */
    public boolean setConvention(int nomenclatureConventions)
    {
	if(Objects.nonNull(convention) && !convention.isEmpty()) {
	    convention.clear();
	    }
	    
	return addCodeCompliance(nomenclatureConventions);
    }


    
    /**
     * Declare that the rank is not used under the {@code nomenclatureConvention}.
     *
     * @param {@code nomenclatureConvention} {@link NomenclatureConvention} not usind the {@code Rank}
     * @return {@code true} if the rank declared unused under the {@code nomenclatureConvention}
     */
    public boolean removeConvention(NomenclatureConvention nomenclatureConvention)
    {
	if (Objects.isNull(nomenclatureConvention) || Objects.isNull(convention))
	    return false;

	return convention.remove(nomenclatureConvention);
    }

    /**
     * Returns wheter the {@code Rank} is used in the {@code nomenclatureConvention}. 
     *
     * @param {@code nomenclatureConvention} {@link NomenclatureCoe} to test under which the {@code Rank} is used
     * @return {@code true} if the {@code Rank} is used under the {@code nomenclatureConvention}.
     * or {@code null}, if either the {@the Rank} is not used under the {@code nomenclatureConvention}
     * or  {@code nomenclatureConvention} is {@code null}
     */
    public boolean isUsed(NomenclatureConvention nomenclatureConvention)
    {
	if (Objects.isNull(nomenclatureConvention) || Objects.isNull(convention))
	    return false;

	return convention.contains(nomenclatureConvention);
    }

    /**
     * Returns whether this is fully linked to both higher and lower {@code Rank}s.
     * It also tries its equivalent {@code Rank} if given.
     *
     * @return true if both higher and lower {@code Rank} are non null,
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
     * Returns nearest fully linked {@code Rank}
     *
     * @return nearest fully linked {@code Rank}
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
     * Returns nearest fully linked {@code Rank}
     *
     * @return nearest fully linked {@code Rank}
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
     * Returns nearest fully linked {@code Rank}
     *
     * @return nearest fully linked {@code Rank}
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
     * Returns whether this is higher than {@code rank}
     *
     * @param rank name of {@code Rank} to be compared
     *
     * @return true if this is higher than given {@code rank}
     */
    public boolean isHigher(String rank)
    {
	return isHigher(get(rank));
    }

    /**
     * Returns whether this is higher than given {@code rank}
     *
     * @param rank to be compared
     *
     * @return true if this is higher than given {@code rank}
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
     * Returns whether this is lower than given {@code rank}
     *
     * @param rank name to be compared
     *
     * @return true if this is lower than given {@code rank}
     */
    public boolean isLower(String rank)
    {
	return isLower(get(rank));
    }

    /**
     * Returns whether this is lower than given {@code rank}
     *
     * @param rank to be compared
     *
     * @return true if this is lower than given {@code rank}
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
     * given {@code rank}
     *
     * @param rank to be tracked from top rank
     *
     * @return an Array of {@code Rank} indicating path from top to
     * given {@code rank}
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
	    && Objects.equals(this.getCodeCompliance(),theOther.getCodeCompliance())
	    ;
    }

    @Override
    public int hashCode() {
	return Objects.hash(super.hashCode(),
			    getName(),
			    getAbbreviation(),
			    isOptional(),
			    getCodeCompliance()
			    );
    }


    /**
     * Returnes an array of rank names appropriate to {@code TextListModel}.
     * 
     * @return Array of {@code String} representing rank names appropriate to {@code TextListModel}.
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
     * Returns {@code String} representing rank name
     *
     * @return String representing rank name
     */
    public String toString()
    {
	return getName();
    }

    /**
     * Returns highest {@code Rank}
     *
     * @return {@code Rank} at top of rank hierarchy
     */
    public static Rank getHighest()
    {
	return sortedRanks.get(0);
    }

    /**
     * Returns a List of sorted {@code Rank}s
     *
     * @return List of sorted {@code Rank}s
     */
    public static final List<Rank> getSortedRanks()
    {
	return sortedRanks;
    }

    /**
     * Returns an array of sorted {@code Rank}s
     *
     * @return array of sorted {@code Rank}s
     */
    public static Rank[] getSortedArray()
    {
	int ranks = sortedRanks.size();
	Rank[] sorted = new Rank[ranks];
	System.arraycopy(sortedRanks.toArray(), 0, sorted, 0, ranks);
	return sorted;
    }

    /**
     * Returns an array of {@code String}s
     * representing sorted {@code Rank}s' name
     *
     * @return array of {@code Rank}s' name
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

    public static String getRank(NameUsage<?> usage)
    {
	if(usage != null) {
	    Rank rank = usage.getRank();
	    if(rank != null)
		return rank.getName();
	}
	return "";
    }

    volatile PropertyChangeEvent currentEvent = null;    
    public void propertyChange(PropertyChangeEvent event)
    {
	if (event.getSource() == this)
	    return;
	
	while (Objects.nonNull(currentEvent)) { }
	currentEvent = event;
	// action... 
	currentEvent = null;
    }
    
}

    class RankObserver
	extends PropertyChanger
	implements PropertyChangeListener
    {
	volatile PropertyChangeEvent currentEvent = null;
	public RankObserver()
	{
	    super();
	}
	
	public void propertyChange(PropertyChangeEvent event)
	{
	    while (Objects.nonNull(currentEvent)) { }
	    currentEvent = event;
	    firePropertyChange(event);
	    currentEvent = null;
	}
    }
