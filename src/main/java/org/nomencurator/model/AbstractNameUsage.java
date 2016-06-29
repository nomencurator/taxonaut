/*
 * AbstractNameUsage.java:  a Java implementation of NameUsage class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 1999, 2002, 2003, 2004, 2005, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

// import com.google.common.base.Objects;

import java.io.Serializable;

import java.lang.reflect.Array;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

//import org.nomencurator.sql.NamedObjectConnection;
import org.nomencurator.io.sql.NamedObjectConnection;

import org.nomencurator.io.Exchangable;
import org.nomencurator.io.NameUsageExchanger;
import org.nomencurator.io.ObjectExchanger;

import org.nomencurator.util.ArrayUtility;
import org.nomencurator.util.Locales;

import org.nomencurator.model.vocabulary.TypeStatus;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An implementation of {@code NameUsage} in Nomencurator
 * data model.
 * It was referred to as NameRecord in the original publication.
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org</A>
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractNameUsage<N extends NameUsage<?, ?>, T extends N>
						  //public abstract class AbstractNameUsage<T extends NameUsage<?>>
    extends AbstractNamedObject<N, T>
    implements NameUsage<N, T>, Serializable
{
    private static final long serialVersionUID = 5100114850590005954L;

    protected boolean contentsResolved;

    protected boolean hierarchyResolved;

    /** {@code Locale} of this name */
    protected Locale locale;
    //protected String locale;

    /** The name used by this usage */
    protected String nameLiteral;

    protected Rank rank;

    /** {@code String} representing rank of this name usage.  It may be null if rank-less system */
    protected String rankLiteral;

    /** {@code Appearance} where this usage was recorded */
    //protected Appearance appearance;

    /** The sens of this {@code NameUsage}.  It may designate the first usage of the name */
    protected NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> sensu;
    //protected NameUsage<? extends N, ? extends T> sensu;

    /** {@code Vector} containing authors of this {@code NameUsage} */
    protected List<Author> authors;

    protected String authority;

    /** year of authority indicated in the original publication */
    protected Integer year;

    /** pointer to higher {@code NameUsage} */
    protected NameUsage<N, T> higherNameUsage;
    
    /** index of this in sibling {@code NameUsage}s */
    protected int siblingIndex;

    /** boolean indeicating assignment to the higher taxon is incertae sedis or not */
    protected boolean incertaeSedis;

    /**
     * {@code List} of lower {@code NameUsage}s
     *  just under this {@code NameUsage}.
     */
    protected List<NameUsage<N, T>> lowerNameUsages;

    /** boolean indicating this taxon is type or not */
    protected boolean isType;

    /** {@code NameUsage} designated as type of this {@code NameUsage} */
    protected NameUsage<? extends N, ? extends N> typeUsage;

    /**
     * {@code Map} from {@code NameUsage} to a {@code TypeStatus}
     * representing a list of {@code NameUsage}s designated as type or types
     * of this {@code NameUsage} designated in a single {@code Appearance}.
     */
    protected Map<NameUsage<? extends N, ? extends N>, TypeStatus> typeDesignators;

    /**
     * {@code String} representing type status of this {@code NameUsage}.
     * If this {@code NameUsage} represents a taxon, the value is NOMINOTYPE.
     * If this {@code NameUsage} represents a specimen ID, the valume is one of
     * "holotype", 
     */
    protected String typeOfType;

    /**
     * A constant indicates non-specimen type, i.e. type taxon.
     * The word "nominotype" was taken from ICZN 4th ed. 
     * Art. 37, 44, 47 and 61.2.
     */
    public static final String NOMINOTYPE = "nominotype";

    /**
     * A constatns indicates allotype.
     *
     */
    public static final String ALLOTYPE = "allotype";

    public static final String COTYPE = "cotype";
    public static final String GENOTYPE = "genotype";
    public static final String HAPANTOTYPE = "hapantotype";
    public static final String HOLOTYPE = "holotype";
    public static final String LECTOTYPE = "lectotype";
    public static final String NEOTYPE = "neotype";
    public static final String PARALECTOTYPE = "paralectotype";
    public static final String PARATYPE = "paratype";
    public static final String SYNTYPE = "syntype";
    public static final String TOPOTYPE = "topotype";
    public static final String TYPE_SERIES = "type series";
    public static final String TYPE_HOST = "type host";

    /**
     * {@code Vector} containing {@code Annotation}s made by this {@code NameUsage}
     */
    //protected Vector annotations;
    protected Collection<Annotation> annotations;

    /**
     * {@code Hashtable} of which keys provide a list of literal values
     * included in the concept represented by this {@code NameUsage}
     */
    protected Map<String, NameUsage<? extends N, ? extends N>> includants;

    /**
     * {@code Map} of which keys provide a list of literal values
     * excluded in the concept represented by this {@code NameUsage}
     */
    protected Map<String, NameUsage<? extends N, ? extends N>> excludants;

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean equals(Object object) {
	if(object == this) return true;
	if(object == null) return false;
	if(getClass() != object.getClass()) return false;

	final AbstractNameUsage<N, T> that = (AbstractNameUsage<N, T>) object;
	return super.equals(object)
	    && Objects.equals(this.contentsResolved, that.contentsResolved)
	    && Objects.equals(this.hierarchyResolved, that.hierarchyResolved)
	    && Objects.equals(this.locale, that.locale)
	    && Objects.equals(this.nameLiteral, that.nameLiteral)
	    && Objects.equals(this.rank, that.rank)
	    && Objects.equals(this.rankLiteral, that.rankLiteral)
	    && Objects.equals(this.sensu, that.sensu)
	    && Objects.equals(this.authors, that.authors)
	    && Objects.equals(this.authority, that.authority)
	    && Objects.equals(this.higherNameUsage, that.higherNameUsage)
	    && Objects.equals(this.siblingIndex, that.siblingIndex)
	    && Objects.equals(this.incertaeSedis, that.incertaeSedis)
	    && Objects.equals(getSortedNameList(this.lowerNameUsages), getSortedNameList(that.lowerNameUsages))
	    && Objects.equals(this.isType, that.isType)
	    && Objects.equals(this.typeUsage, that.typeUsage)
	    && Objects.equals(this.typeDesignators, that.typeDesignators)
	    && Objects.equals(this.typeOfType, that.typeOfType)
	    && Objects.equals(this.annotations, that.annotations)
	    && Objects.equals(this.includants, that.includants)
	    && Objects.equals(this.excludants, that.excludants)
	    ;
    }

    @Override
    public int hashCode() {
	return Objects.hash(super.hashCode(),
			    contentsResolved, 
			    hierarchyResolved, 
			    locale, 
			    nameLiteral, 
			    rank, 
			    rankLiteral, 
			    sensu, 
			    authors, 
			    authority, 
			    higherNameUsage, 
			    siblingIndex, 
			    incertaeSedis,
			    getSortedNameList(lowerNameUsages),
			    isType,
			    typeUsage,
			    typeDesignators,
			    typeOfType,
			    annotations,
			    includants,
			    excludants
			    );
    }

    /** Constructs an "empty" {@code NameUsage} */
    protected AbstractNameUsage()
    {
	super();
    }

    /**
     * Constructs an "empty" {@code NameUsage}
     * appeared in {@code appearance}
     *
     * @param appearance {@code Appearance} where the name used
     */
    protected AbstractNameUsage(Appearance apperance)
    {
	this();
	setAppearance(apperance);
    }

    /**
     * Constructs a {@code NameUsage} object having
     * {@code persistentID} as its representation,
     */
    protected AbstractNameUsage(String persistentID)
    {
	super(persistentID);
    }

    /**
     * Constructs a {@code NameUsage} based on
     * {@code nameUsage}
     */
    protected AbstractNameUsage(Name<?,?> nameUsage)
    {
	//don't use NamedObject(String) constructor
	this();
	
	if(nameUsage instanceof NamedObject){
	    if(nameUsage instanceof NameUsage){
		//copy code
	    }
	    else //incompatible
		literal = "";
	}
	else{
	    if(this.isA(nameUsage)) {
		literal = nameUsage.getLiteral();
	    }
	    else
		literal = "";	 
	}
    }

    /**
     * Constructs a deep copy of {@code nameUsage}
     *
     * @param nameUsage {@code NameUsage} to be copied deeply
     */
    protected AbstractNameUsage(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage)
    //protected AbstractNameUsage(NameUsage<N, T> nameUsage)
    {
	this();
	if(nameUsage == null)
	    return;

	setRankLiteral(nameUsage.getRankLiteral());
	setLiteral(nameUsage.getLiteral());
	setLocale(nameUsage.getLocale());

	setSensu(nameUsage.getSensu());

	setAuthorityYear(nameUsage.getAuthorityYear());

	NameUsage<N, T> higherNameUsage = getNameUsage(nameUsage).getHigherNameUsage();
	if(higherNameUsage != null) {
	    setHigherNameUsage(higherNameUsage);
	    setIndex(nameUsage.getIndex());
	    setIncertaeSedis(nameUsage.isIncertaeSedis());
	}
	setType(nameUsage.isType());
	setTypeOfType(nameUsage.getTypeOfType());

	//NameUsage<? extends N, ? extends N>[] lowerNameUsagesToCopy = nameUsage.getLowerNameUsages();
	if(nameUsage.getLowerNameUsagesCount() > 0) {
	    setLowerNameUsages(nameUsage.getLowerNameUsages());
	    /*
	    List<NameUsage<N, T>> source = nameUsage.getLowerNameUsages();
	    List<NameUsage<N, T>> lowers = Collections.synchronizedList(new ArrayList<NameUsage<N, T>>(source.size()));
	    for (NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> usage : source) {
		lowers.add((NameUsage<N, T>)usage);
	    }
	    setLowerNameUsages(lowers);
	    */
	}
	
	Appearance appearance = nameUsage.getAppearance();
	if(appearance != null) {
	    setAppearance(appearance);
	}
	else {
	   Publication publication = nameUsage.getPublication(); 
	   if(publication != null)
	       setPublication(publication);
	   else {
	   }
	}

	Collection<Annotation> annotationsToCopy = nameUsage.getAnnotations();
	if(annotationsToCopy != null) {
	    setAnnotations(annotationsToCopy);
	}
    }

    /**
     * Constructs a {@code NameUsage} by giving
     * its attributes.
     *
     * @param rank {@code String} indicating name of rank
     * @param name {@code String} indicating ascribed name
     * @param auth {@code Name} of authoritative name usage
     * @param rec  {@code Name} of recording name usage
     * @param type boolean, true if the {@code NameUsage} is name bearing type
     * @param higher {@code Name} of higher taxon
     * @param lower array of lower taxa's {@code Name}s
     */
    protected AbstractNameUsage(String rankLiteral, String name,
		     Name<N, T> auth, Name<N, T> rec,
		     boolean type,
		     Name<N, T> higher, Name<N, T> [] lower)
    {
	this();
	
	this.rankLiteral = rankLiteral;
	nameLiteral     = name;
	sensu = createNameUsage(auth);
	setAppearance(new Appearance(rec));
	this.isType = type;
	higherNameUsage    = createNameUsage(higher);
	
	for(int i = 0; i < lower.length; i++){
	    addLowerNameUsage(createNameUsage(lower[i]));
	}
	
    }

    /**
     * Constructs an {@code NameUage} object using XML data
     * given by {@code xml}
     *
     * @param xml {@code Element} specifying a {@code NameUsage}
     *
     */
    protected AbstractNameUsage(Element xml)
    {
	this(xml, null);
    }

    /**
     * Constructs an {@code NameUage} object using XML data
     * given by {@code xml}
     *
     * @param xml {@code Element} specifying a {@code NameUsage}
     * @param appearance {@code Appearance} where the name used
     */
    //@SuppressWarnings({"unchecked"})
    protected AbstractNameUsage(Element xml, Appearance ap)
    {
	super();
	NodeList nodeList = xml.getChildNodes();
	int nodeCount = nodeList.getLength();
	String persistentID = null;
	boolean toBeResolved = false;
	List<Annotation> annotationList = null;
	List<NameUsage<N, T>> typeDesignatorList = null;
	Name<?, ?> object = null;

	for (int j = 0; j < nodeCount; j++) {
	    Node node = nodeList.item(j);
	    if(node.getNodeType () == Node.ELEMENT_NODE) {
		Element element = (Element)node;
		String tagName = element.getTagName();
		
		if  (tagName.equals ("oid"))
		    persistentID = getString(element);
		else if(tagName.equals ("rank"))
		    rankLiteral =  getString(element);
		else if(tagName.equals ("name"))
		    nameLiteral =  getString(element);
		else if(tagName.equals ("sensu")) {
		    String pID = getString(element);
		    Name<?, ?> name = curator.get(pID);
		    if(isAssignableFrom(name))
			sensu = (NameUsage<?, ?>)name;
		    if(sensu == null) {
			sensu = createNameUsage();
			sensu.setLiteral(pID);
			curator.put(sensu);
		    }
		    /*
		    if(linkFlag) {
			LinkRecord rec = new LinkRecord(nameUsage.persistentID(), getString(element));
			NameUsageAuthority.getInstance().addLinkRecord(rec);
		    } else {
			nameUsage.parseLine("authority{" + getString(element) + "}");
		    }
		    */
		}
		else if(tagName.equals("notes"))
		    setNotes(getString(element));
		else if(tagName.equals("locale")) {
		    /*
		    String localeID = getString(element);
		    if(localeID == null || localeID.length() <= 2) {
			locale = new Locale(" ", "");
		    } else {
			String lang = localeID.substring(0, localeID.indexOf("_"));
			String coun = localeID.substring(localeID.indexOf("_") + 1) ;
			locale = new Locale(lang, coun);
		    }
		    */
		    // locale = getString(element);
		    locale = Locales.get(getString(element));
		}
		else if(tagName.equals ("appearance")) {
		    String pID = getString(element);
		    if(ap != null &&
		       ap.getLiteral().equals(pID)) {
			setAppearance(ap);
			toBeResolved = true;
		    }
		    else {
			Appearance appearance = (Appearance)curator.get(pID);
			if(appearance != null) {
			    setAppearance(appearance);
			    toBeResolved = true;
			}
			else {
			    appearance = new Appearance();
			    setSource(appearance);
			    appearance.setLiteral(pID);
			    curator.put(appearance);
			    appearance.addNameUsage(this);
			}

		    /*
		    if(linkFlag) {
			LinkRecord rec = new LinkRecord(nameUsage.persistentID(), getString(element);
			NameUsageRecorder.getInstance().addLinkRecord(rec);
		    } else {
			nameUsage.parseLine("appearance{" + getString(element) + "}");
                    }
		    */
		    }
		}
		else if(tagName.equals ("higher")) {
		    String pID = getString(element);
		    higherNameUsage =  getNameUsage(curator.get(pID));
		    if(higherNameUsage != null) {
			toBeResolved = true;
		    }
		    else {
			higherNameUsage = createNameUsage();
			higherNameUsage.setLiteral(pID);
			curator.put(higherNameUsage);
			higherNameUsage.addLowerNameUsage(this);
		    }
		    /*
		    if(linkFlag) {
			LinkRecord rec = new LinkRecord(nameUsage.persistentID(), getString(element);
			NameUsageHigher.getInstance().addLinkRecord(rec);
		    } else {
			nameUsage.parseLine("higher{" + getString(element) + "}");
		    }
		    */
		}
		else if(tagName.equals ("lowerTaxon")) {
		    String pID = getString(element);
		    NameUsage<N, T> n = getNameUsage(curator.get(pID));
		    if(n != null) {
			addLowerNameUsage(n);
			toBeResolved = true;
		    }
		    else {
			n = createNameUsage();
			n.setLiteral(pID);
			curator.put(n);
			addLowerNameUsage(n);
		    }
		    /*
		    if(linkFlag) {
			LinkRecord rec = new LinkRecord(getString(element), nameUsage.persistentID());
			NameUsageHigher.getInstance().addLinkRecord(rec);
		    } else {
			nameUsage.parseLine("lower{" + getString(element) + "}");
		    }
		    */
		}
		else if(tagName.equals ("isType")) {
		    isType = Boolean.valueOf(getString(element)).booleanValue();
		}
		else if(tagName.equals ("type")) {
		    typeOfType = element.getAttribute("type");
		    String pID = getString(element);
		    NameUsage<N, T> n = getNameUsage(curator.get(pID));
		    if(n != null) {
			toBeResolved = true;
		    }
		    else {
			n = createNameUsage();
			n.setLiteral(pID);
			curator.put(n);
		    }
		    typeUsage = n;
		}
		else if(tagName.equals("typeDesignator")) {
		    String pID = getString(element);
		    NameUsage<N, T> n = getNameUsage(curator.get(pID));
		    if(n != null) {
			toBeResolved = true;
		    }
		    else {
			n = createNameUsage();
			n.setLiteral(pID);
			curator.put(n);
		    }
		    if(typeDesignatorList == null)
			typeDesignatorList = Collections.synchronizedList(new ArrayList<NameUsage<N, T>>());
		    typeDesignatorList.add(n);
		}
		else if(tagName.equals ("annotation")) {
		    String pID = getString(element);
		    Annotation a = null;
		    object = curator.get(pID);
		    if(object != null && object instanceof Annotation)
			a = (Annotation)object;
		    if(a != null) {
			if(annotationList == null)
			    annotationList = Collections.synchronizedList(new ArrayList<Annotation>());
			annotationList.add(a);
			toBeResolved = true;
		    }
		    else {
			a = new Annotation();
			a.setLiteral(pID);
			curator.put(a);
			addAnnotation(a);
		    }

		    /*
		    if(linkFlag) {
			LinkRecord rec = new LinkRecord(getString(element), nameUsage.persistentID());
			AnnotationFrom.getInstance().addLinkRecord(rec);
		    } else {
			nameUsage.parseLine("annotation{" + getString(element) + "}");
		    }
		    */
		}
		else{}
            }
        }
	if(annotationList != null && annotationList.size() > 0) {
	    annotations = annotationList;
	}
	if(typeDesignatorList != null) {
	    typeDesignators = Collections.synchronizedMap(new HashMap<NameUsage<? extends N, ? extends N>, TypeStatus>());
	    for(NameUsage<? extends N, ? extends N> designator : typeDesignatorList) {
		typeDesignators.put(designator, null);
	    }
	}

	if(persistentID != null &&
	   !persistentID.equals(getLiteral()))
	    setLiteral(persistentID); //i.e. other key data are empty

	if(isType)
	    setType(true);

	if(toBeResolved)
	    curator.resolve(this);
    }

    public AbstractNameUsage<N, T> create()
    {
	return createNameUsage();
    }

    public NameUsage<N, T> clone()
    {
	//AbstractNameUsage<N, T> n = createNameUsage();
	//NameUsage<N, T> n = createNameUsage();
	AbstractNameUsage<N, T> n = create();
	copyTo(n);
	return n;
    }

    protected void copy(AbstractNameUsage<N, T> source)
    {
	source.copyTo(this);
    }

    protected void copyTo(NameUsage<N, T> dest)
    {
	dest.setRankLiteral(getRankLiteral());
	dest.setLiteral(getLiteral());
	dest.setLocale(getLocale());

	Appearance appearance = getAppearance();
	if(appearance != null) {
	    dest.setAppearance(appearance);
	}
	else {
	    dest.setPublication(getPublication()); 
	}

	dest.setSensu(getSensu());

	dest.setAuthorityYear(getAuthorityYear());

	NameUsage<N, T> higherNameUsage = dest.getHigherNameUsage();
	if(higherNameUsage != null) {
	    dest.setHigherNameUsage(higherNameUsage.clone());
	    dest.setIndex(getIndex());
	    dest.setIncertaeSedis(isIncertaeSedis());
	}
	dest.setType(isType());
	dest.setTypeOfType(getTypeOfType());

	/*
	NameUsage<N, T>[] lowerNameUsagesArray = getLowerNameUsages();
	if(lowerNameUsagesArray != null) {
	    lowerNameUsagesArray = ArrayUtility.copy(lowerNameUsagesArray);
	    for(NameUsage<N, T> lowerNameUsage : lowerNameUsagesArray) {
		lowerNameUsage = lowerNameUsage.clone();
		lowerNameUsage.setHigherNameUsage(n);
	    }
	    dest.setLowerNameUsages(lowerNameUsagesArray);
	}
	*/

	if(getLowerNameUsagesCount() > 0) {
	    dest.setLowerNameUsages(Collections.synchronizedList(new ArrayList<NameUsage<N, T>>(getLowerNameUsages())));
	}
	
	Collection<Annotation> annotationToCopy = getAnnotations();
	if(annotationToCopy == null || annotationToCopy.size() == 0) {
	    dest.setAnnotations(null);
	}
	else {
	    dest.setAnnotations(Collections.synchronizedList(new ArrayList<Annotation>(annotationToCopy)));
	}
    }


    /**
     * Reteruns class name without package part
     *
     * @return class name without package part
     */
    public String getClassName()
    {
	return "NameUsage";
    }

    
    /**
     * Returns a persistent ID representing this {@code NameUsage}
     * with specified  {@code separator}.  It contains class name header
     * if {@code withClassName} true.
     *
     * @param separator {@code char} to be used as the field separator
     * @param withClassName {@code boolean} specifying with or without
     * class name header
     *
     * @return String representing this {@code NameUsage}
     */
    public String getPersistentID(String separator, boolean withClassName)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getPersistentID(separator, withClassName);

	if(literal != null && literal.length() != 0)
	    return literal;
	
	StringBuffer buffer = null;
	if(withClassName)
	    buffer = getClassNameHeaderBuffer();
	else
	    buffer = new StringBuffer();

	if(rankLiteral != null)
	     buffer.append(rankLiteral);
	buffer.append(separator);
	if(nameLiteral != null)
	    buffer.append(nameLiteral);
	buffer.append(separator);

	Appearance appearance = getAppearance();
	if(appearance == null){
	    //request code here;
	}
	if(appearance == null) {
	    buffer.append(new Appearance().getEmptyPersistentID(separator, false));
	}
	else{
	    buffer.append(appearance.getPersistentID(separator, false));
	}
	return buffer.toString();
    }
    
    /**
     * Returnes number of fields separators in persistent ID
     *
     * @return int representing number of fields separators in persistent ID
     */ 
    public int getFieldSepartorsCount()
    {
	//return 21;
	return 9;
    }

    public void parseLine(String line)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.parseLine(line);
	    return;
	}

	Appearance appearance = null;

	String s = peal(line);
	if(line.startsWith("rank"))
	    rankLiteral = s;
	else if(line.startsWith("group"))
	    rankLiteral = s;
	else if(line.startsWith("majorname"))
	    //	    major = s;
	    nameLiteral = s;
	else if(line.startsWith("minorname"))
	    //	    minor = s;
	    nameLiteral = s;
	else if(line.startsWith("sensu"))
	    //      authority = new Publication(s.substring(s.indexOf("Publication::")));
	    //	    authority = new Appearance(s);
	    sensu = createNameUsage(s);
	else if(line.startsWith("recorder"))
	    appearance = new Appearance(s);
	else if(line.startsWith("lower"))
	    addLowerNameUsage(createNameUsage(s));
	else if(line.startsWith("higher"))
	    higherNameUsage  = createNameUsage(s);
	else if(line.startsWith("type"))
	    isType = Boolean.valueOf(s).booleanValue();
	else{
	    if(appearance == null){
		appearance = new Appearance();
	    }
	    appearance.addAnnotation(new Annotation(s));
	}
	setSource(appearance);
    }
    
    public boolean merge(NamedObject<?, ?> namedObject)
    {
	if(!(namedObject instanceof NameUsage))
	    return false;
	return false; //not yet implemented
    }

    @Override
    public Boolean isSynonymOf(final NameUsage<?, ?> nameUsage)
    {
	if(nameUsage == null)
	    return Boolean.FALSE;

	return nameUsage.synonym(this);
    }


    /**
     * Returns {@code String} representing rank name
     *
     * @return {@code String} representing rank name
     */ 
    public Rank getRank()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getRank();

	if(!isNominal())
	    return rank;

	return Rank.get(getRankLiteral());
    }
    
    /**
     * Returns {@code String} representing rank name
     *
     * @return {@code String} representing rank name
     */ 
    public String getRankLiteral()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getRankLiteral();

	if(!isNominal())
	    return rankLiteral;

	return getPersistentIDComponent(0);
    }

    /**
     * Sets {@code rank}
     *
     * @param rank {@code Rank} to be set
     */ 
    public void setRank(Rank rank)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setRank(rank);
	    return;
	}

	if(rank == this.rank ||
	   (rank != null && rank.equals(this.rank)))
	    return;

	this.rank = rank;
	String rankLiteral = (rank==null)? null : rank.getName();
	setRankLiteral(rankLiteral);
    }
    
    /**
     * Sets {@code rank} as rank name
     *
     * @param rank {@code String} representing rank name
     */ 
    public void setRankLiteral(String rankLiteral)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setRankLiteral(rankLiteral);
	    return;
	}

	if(rankLiteral == this.rankLiteral ||
	   (rankLiteral != null && rankLiteral.equals(this.rankLiteral)))
	    return;

	this.rankLiteral = rankLiteral;

	setRank(Rank.get(this.rankLiteral));
    }
    
    /**
     * Returns {@code String} representing the name used
     *
     * @return {@code String} representing the name used
     */ 
    public String getUsedName()
    {
	return getLiteral();
    }

    /**
     * Returns {@code String} representing the name used
     *
     * @return {@code String} representing the name used
     */ 
    public String getLiteral()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getLiteral();

	if(!isNominal())
	    return nameLiteral;

	return getPersistentIDComponent(1);
    }
    
    /**
     * Sets {@code name} as the name used
     *
     * @param name {@code String} representing the name used
     */ 
    public void setUsedName(String name)
    {
	setLiteral(name);
    }
    /**
     * Sets {@code name} as the name used
     *
     * @param name {@code String} representing the name used
     */ 
    public void setLiteral(String name)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setLiteral(name);
	    return;
	}

	if(name == nameLiteral ||
	   (name != null && name.equals(nameLiteral)))
	    return;

	nameLiteral = name;
    }
    
    /**
     * Returns {@code Locale} representing locale
     *
     * @return {@code Locale} representing locale
     */ 
    public Locale getLocale()
    //public String getLocale()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getLocale();

	return locale;
    }
    
    /**
     * Sets {@code locale} as locale of this {@code NameUsage}
     *
     * @param locale {@code Locale} to be set
     */ 
    public void setLocale(Locale locale)
    //public void setLocale(String locale)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setLocale(locale);
	    return;
	}

	if(this.locale == locale ||
	   (locale != null && locale.equals(this.locale)))
	    return;

	this.locale = locale;
    }
    
    /*
     * Returns {@code Publication} encoding this {@code NameUsage}
     *
     * @return {@code Publication} encoding this {@code NameUsage}
     */
    public Publication getPublication()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getPublication();

	NamedObject<?, ?> src = getSource();

	if(src != null) {
	    if(src instanceof Publication)
		return (Publication)src;
	    if(src instanceof Appearance)
		return ((Appearance)src).getPublication();
	}

	return null;
    }
    
    /*
     * Sets {@code publication} as {@code Apperance} encoding this {@code NameUsage}
     *
     * @param publication {@code Publication} encoding this {@code NameUsage}
     */
    public void setPublication(Publication publication)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setPublication(publication);
	    return;
	}

	NamedObject<?, ?> src = getSource();
	if(src == publication)
	    return;

	Appearance appearance = getAppearance();

	if(appearance == null) {
	    setSource(publication);
	    return;
	}

	appearance.setPublication(publication);
    }
    
    /*
     * Returns {@code Appearance} encoding this {@code NameUsage}
     *
     * @return {@code Appearance} encoding this {@code NameUsage}
     */
    public Appearance getAppearance()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getAppearance();

	NamedObject<?, ?> src = getSource();
	if(src != null &&
	   src instanceof Appearance)
	    return (Appearance)src;

	return null;
    }
    
    /*
     * Sets {@code appearance} as {@code Apperance} encoding this {@code NameUsage}
     *
     * @param appearance {@code Appearance} encoding this {@code NameUsage}
     */
    public void setAppearance(Appearance appearance)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setAppearance(appearance);
	    return;
	}

	Appearance a = getAppearance();

	if(appearance == a)
	    return;

	if(a != null) {
	    setSource(null);
	    //a.removeNameUsage(this);
	}

	setSource(appearance);
	if(appearance != null)
	    appearance.addNameUsage(this);
    }
    
    /**
     * Returns {@code NameUsage} representing higher taxon
     *
     * @return {@code NameUsage} representing higher taxon
     */
    public NameUsage<N, T> getHigherNameUsage()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getHigherNameUsage();

	return higherNameUsage;
    }

    //public List<NameUsage<?>> getNameUsagePath() 
    public List<NameUsage<N, T>> getNameUsagePath() 
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getNameUsagePath();

	Stack<NameUsage<N, T>> stack = new Stack<NameUsage<N, T>>();
	NameUsage<N, T> higher = getHigherNameUsage();
	while (higher != null && stack.search(higher) == -1) {
	    stack.push(higher);
	    higher = getNameUsage(higher).getHigherNameUsage();
	}

	List<NameUsage<N, T>> nameUsagePath = null;
	int size = stack.size();
	if(size > 0) {
	    nameUsagePath = new ArrayList<NameUsage<N, T>>(size);
	    while(!stack.empty()){
		nameUsagePath.add(stack.pop());
	    }
	}
	return nameUsagePath;
    }
    
    /**
     * Sets {@code higherNameUsage} as higher taxon of this
     * {@code NameUsage}
     *
     * @param higherNameUsage {@code NameUsage} representing higher taxon
     */
    //public boolean setHigherNameUsage(NameUsage<? extends N, ? extends N> higherNameUsage)
    //public boolean setHigherNameUsage(NameUsage<? extends N, ? extends T> higherNameUsage)
    public synchronized boolean setHigherNameUsage(NameUsage<N, T> higherNameUsage)
    //public boolean setHigherNameUsage(N higherNameUsage)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    return n.setHigherNameUsage(higherNameUsage);
	}

	n = higherNameUsage;
	if(n != null) {
	    n = n.getNameUsage();
	    if(n == null)
		n = higherNameUsage;
	}

	higherNameUsage = n;

	if(this.higherNameUsage == higherNameUsage) {
	    return false;
	}

	List<NameUsage<N, T>> nameUsagePath = getNameUsagePath();

	if(nameUsagePath != null &&  (nameUsagePath.contains(getNameUsage()) || nameUsagePath.contains(getNameUsage(getEntity()))))
	    return false;

	// optimistic code....
	/*
	if(higherNameUsage == this.higherNameUsage ||
	   (higherNameUsage != null && this.higherNameUsage != null &&
	    higherNameUsage.getNameUsage() == this.higherNameUsage.getNameUsage())
	   )
	    return;
	*/

	n = getHigherNameUsage();

	if(n != null) {
	    this.higherNameUsage = null;
	    n.removeLowerNameUsage(this);
	}
	
	this.higherNameUsage = higherNameUsage;
	if(this.higherNameUsage != null) {
	    this.higherNameUsage.addLowerNameUsage(this);
	}

	return true;
    }

    /**
     * Sets {@code higherNameUsage} as higher taxon where this
     * {@code NameUsage} as the {@code index}th 
     * lower taxon of the {@code higherNameUsage}
     *
     * @param higherNameUsage {@code NameUsage} representing higher taxon
     * @param index {@code int} representing postion of this
     * {@code NameUsgae}
     */
    //public void setHigherNameUsage(NameUsage<? extends N, ? extends N> higherNameUsage, int index)
    //public boolean setHigherNameUsage(NameUsage<? extends N, ? extends T> higherNameUsage, int index)
    public boolean setHigherNameUsage(NameUsage<N, T> higherNameUsage, int index)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.setHigherNameUsage(higherNameUsage, index);

	boolean result = setHigherNameUsage(higherNameUsage);
	if(higherNameUsage != null)
	    setIndex(index);
	else
	    setIndex(-1);

	return result;
    }

    /**
     * Returns position of this {@code NameUsage} in its siblings,
     * or -1 if it does not have sibrings
     *
     * @return {@code int} representing postion of this
     * {@code NameUsage} in its siblings,
     * or -1 if it does not have siblings
     */
    public int getIndex()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getIndex();

	return siblingIndex;
    }

    /**
     * Sets {@code index} as position of this {@code NameUsage} in its siblings,
     * or -1 if it does not have siblings
     *
     * @param {@code index} representing postion of this
     * {@code NameUsage} in its siblings,
     * or -1 if it does not have siblings
     */
    public void setIndex(int index)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setIndex(index);
	    return;
	}

	siblingIndex = index;
    }

    /**
     * Returns true if the assignment to the higher taxon
     * should be considerd as incertaes sedis
     *
     * @return true if the assignment to the higher taxon
     * should be considerd as incertae sedis
     */
    public boolean isIncertaeSedis()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.isIncertaeSedis();

	return incertaeSedis;
    }

    /**
     * Sets incertaeSedis to indicate whether the assignment to the higher taxon should be considerd as incertae sedis
     *
     * @param incertaeCeis {@code boolean} true if the assignment to the higher taxon
     * should be considerd as incertae sedis
     */
    public void setIncertaeSedis(boolean incertaeSedis)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setIncertaeSedis(incertaeSedis);
	    return;
	}

	if(this.incertaeSedis == incertaeSedis)
	    return;

	this.incertaeSedis = incertaeSedis;
    }

    /**
     * Returns number of lower taxa
     *
     * @return number of lower taxa
     */
    public int getLowerNameUsagesCount()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getLowerNameUsagesCount();

	if(lowerNameUsages != null)
	    //return lowerNameUsages.length;
	    return lowerNameUsages.size();

	return 0;
    }

    /**
     * Makes the object to use array instead of
     * {@code Vector} as container of
     * lower taxa
     *
     * @see setLowerNameUsagesCapacity(int);
     */
    public void toArray()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.toArray();
	    return;
	}

	/*
	if(lowerNameUsagesArray != null ||
	   lowerNameUsages == null)
	    return;

	setLowerNameUsagesCapacity(getLowerNameUsagesCount());
	*/
    }

    /**
     * Tests if {@code nameUsage} is a direct
     * lower taxon of this object
     *
     * @param nameUsage {@code NameUsage} to be tested
     * @return true if this object contains {@code nameUsage}
     * as a lower taxon just below
     */
    public boolean contains(NameUsage<? extends N, ? extends N> nameUsage)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.contains(nameUsage);

	//don't think null object as an empty set
	if(nameUsage == null || lowerNameUsages == null ||! isAssignableFrom(nameUsage))
	    return false;

	return (findLowerNameUsage(getNameUsage(nameUsage)) != -1);
	//return (find<NameUsage>(nameUsage, lowerNameUsages) != -1);

	/*
	if(lowerNameUsagesArray == null)
	    return false;

	boolean contains = false;
	int i = 0;
	for(i = 0; !contains && i < lowerNameUsagesArray.length; i++) {
	    contains = 
		(lowerNameUsagesArray[i] == nameUsage ||
		 lowerNameUsagesArray[i].getEntity() == nameUsage ||
		 lowerNameUsagesArray[i] == nameUsage.getEntity() ||
		 lowerNameUsagesArray[i].getEntity() == nameUsage.getEntity());
	}

	return contains;
	*/
    }

    /**
     * Tests if {@code nameUsage} is a 
     * lower taxon of this object, recursively if
     * {@code recursive} is true
     *
     * @param nameUsage {@code NameUsage} to be tested
     * @param recursive true to recursive search
     * @return true if this object contains {@code nameUsage}
     * as a lower taxon
     */
    public boolean contains(NameUsage<? extends N, ? extends N> nameUsage, boolean recursive)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.contains(nameUsage, recursive);

	if(nameUsage == null || lowerNameUsages == null ||! isAssignableFrom(nameUsage))
	    return false;

	boolean contains = contains(nameUsage);
	if(!recursive || contains)
	    return contains;
	/*
	for(int i = 0; i < lowerNameUsages.length && !contains; i++) {
	    if(lowerNameUsages[i].contains(nameUsage, recursive))
		return true;
	}
	*/

	n = getNameUsage(nameUsage);

	//for(NameUsage<? extends N, ? extends N> lowerNameUsage : lowerNameUsages) {
	for(NameUsage<?, ?> lowerNameUsage : lowerNameUsages) {
	    //	    if(getNameUsage(lowerNameUsage).contains(nameUsage, recursive))
	    if(getNameUsage(lowerNameUsage).contains(n, recursive))
		return true;
	}

	return false;
    }

    /**
     * Returns {@code Enumeration} of lower taxa
     * or null if none
     *
     * @return an array of lower taxa
     * or null if none
     */
    public List<NameUsage<N, T>> getLowerNameUsages()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getLowerNameUsages();

	return lowerNameUsages;
	/*
	if(lowerNameUsages == null)
	    return null;

	NameUsage<N, T>[] toReturn = null;
	synchronized (lowerNameUsages) {
	    toReturn = ArrayUtility.copy(lowerNameUsages);
	}

	return toReturn;
	*/
    }

    /**
     * Sets {@code lowerNameUsages} as the list of lower taxa
     *
     * @param lowerNameUsages {@code Vector} representing the list of lower taxa
     */
    @SuppressWarnings({"unchecked"})
    public void setLowerNameUsages(List<? extends NameUsage<?, ?>> lowerNameUsages)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setLowerNameUsages(lowerNameUsages);
	    return;
	}

	removeLowerNameUsages();
	if(lowerNameUsages == null || lowerNameUsages.size() == 0)
	    return;

	if(this.lowerNameUsages == null)
	    this.lowerNameUsages = new ArrayList<NameUsage<N, T>>(lowerNameUsages.size());
	for(NameUsage<?, ?> nameUsage : lowerNameUsages) {
	    if(isAssignableFrom(nameUsage))
		addLowerNameUsage((NameUsage<N, T>)nameUsage);
	    else
		addLowerNameUsage(createNameUsage(nameUsage));
	}
    }

    /**
     * Sets {@code lowerNameUsages} as the list of lower taxa
     *
     * @param lowerNameUsages an array of {@code NameUsage}s
     * representing the list of lower taxa
     */
    /*
    public void setLowerNameUsages(NameUsage<? extends N, ? extends N>[] lowerNameUsagesArray)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setLowerNameUsages(lowerNameUsages);
	    return;
	}
	
	removeLowerNameUsages();
	if(lowerNameUsagesArray == null || lowerNameUsagesArray.length == 0)
	    return;

	for(NameUsage<? extends N, ? extends N> lowerNameUsage : lowerNameUsagesArray) {
	    this.addLowerNameUsage(lowerNameUsage);
	}
    }
    */

    /**
     * Adds {@code nameUsage} to the list of lower taxa
     * It returns true if the {@code nameUsage} added to the
     * list successfuly, or false if the {@code nameUsage} is
     * already in the list.
     *
     * @param nameUsage {@code NameUsage} to be added to the list of lower taxa
     *
     * @return true if {@code nameUsage} was appended to the list of lower taxa
     * successfully, or false if {@code nameUsage} is already in the list
     */
    //public boolean addLowerNameUsage(NameUsage<? extends N, ? extends N> nameUsage)
    //public boolean addLowerNameUsage(NameUsage<?, ?> nameUsage)
    public synchronized boolean addLowerNameUsage(NameUsage<N, T> nameUsage)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.addLowerNameUsage(nameUsage);

	n = nameUsage;
	if(n != null) {
	    n = n.getNameUsage();
	    if(n == null)
		n = nameUsage;
	}
	nameUsage = n;

	if(this.lowerNameUsages != null
	   && this.lowerNameUsages.contains(nameUsage))
	    return false;

	if(nameUsage == null //if null object, or
	   || nameUsage == this //if this object itself, or
	   || contains(nameUsage) //if already contained
	   || nameUsage.getNameUsage() == this //if proxy of this object, or
	   || !n.isAssignableFrom(nameUsage) //if not an instancs of NameUsage<N, T>
	   )
	    return false;

	n = n.getNameUsage(nameUsage);
	List<NameUsage<N, T>> nameUsagePath = getNameUsagePath();
	if(nameUsagePath != null && ( nameUsagePath.contains(nameUsage) || nameUsagePath.contains(nameUsage.getEntity())))
	    return false;

	if(this.lowerNameUsages == null)
	    //this.lowerNameUsages = Collections.synchronizedList(new ArrayList<NameUsage<N, T>>());
	    this.lowerNameUsages = new ArrayList<NameUsage<N, T>>();

	boolean result = true;

	/*
	n = getNameUsage(nameUsage);
	if(n == null) {
	    Object object = nameUsage.getNameUsage();
	    for(NameUsage<N, T> toTest: this.lowerNameUsages) {
		n = toTest.getNameUsage();
		if(toTest == nameUsage || toTest.equals(nameUsage)
		   || n == nameUsage || n.equals(nameUsage)
		   || toTest == object || toTest.equals(object)
		   || n == object || n.equals(object))
		    result = false;
	    }
	    if(result) {
		n = createNameUsage(nameUsage);
	    }
	}
	*/

	/*
	if(n != null) {
	    result = this.lowerNameUsages.add(n);
	    if(result) {
		getNameUsage(nameUsage).setHigherNameUsage(this);
	    }
	}
	*/

	result = this.lowerNameUsages.add(nameUsage);
	nameUsage.setHigherNameUsage(this);

	return result;
    }

    /**
     * Removes {@code nameUsage} from the list of lower taxa
     *
     * @param nameUsage {@code NameUsage} to be removed fromthe list of lower taxa
     * @return true if {@code nameUsage} is a lower taxon of the object,
     * or false if else.  It also returns null if <tt>nameUsage</tt> is null.
     */
    //public boolean removeLowerNameUsage(NameUsage<? extends N, ? extends N> nameUsage)
    public synchronized boolean removeLowerNameUsage(NameUsage<N, T> nameUsage)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.removeLowerNameUsage(nameUsage);

	n = nameUsage;
	if(n != null) {
	    n = n.getNameUsage();
	    if(n == null)
		n = nameUsage;
	}
	nameUsage = n;

	/*
	if(lowerNameUsages != null && ArrayUtility.contains(nameUsage, lowerNameUsages)) {
	    NameUsage[] removed = 
		ArrayUtility.remove(nameUsage, lowerNameUsages);
	    if(removed != lowerNameUsages) {
		nameUsage.setHigherNameUsage(null);
		ArrayUtility.clear(lowerNameUsages);
		lowerNameUsages = removed;
		return true;
	    }
	}
	*/

	if(lowerNameUsages != null && lowerNameUsages.contains(nameUsage)) {
	    nameUsage.setHigherNameUsage(null);
	    return lowerNameUsages.remove(nameUsage);
	}
	return false;
    }

    /**
     * Removes all lower taxa
     *
     */
    public void removeLowerNameUsages()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.removeLowerNameUsages();
	    return;
	}

	if(lowerNameUsages == null)
	    return;

	/*
	for(int i = 0; i < lowerNameUsages.length; i++) {
	    NameUsage l = lowerNameUsages[i];
	    lowerNameUsages[i] = null;
	    l.setHigherNameUsage(null);
	}
	*/

	for(NameUsage<? extends N, ? extends N> lowerNameUsage : lowerNameUsages) {
	    lowerNameUsage.setHigherNameUsage(null);
	}

	lowerNameUsages.clear();

	lowerNameUsages = null;
    }

    /**
     * Returns {@code NameUsage} representing type taxon of this
     * {@code NameUsage}, or null if no type is designated
     *
     * @return NamedObject representing type taxon of this
     * {@code NameUsage}, or null if no type is designated
     */
    public NameUsage<? extends N, ? extends N> getTypeUsage()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getTypeUsage();

	return typeUsage;
    }

    /**
     * Returns {@code NameUsage} representing type taxon of this
     * {@code NameUsage}, or null if no type is designated
     *
     * @return NamedObject representing type taxon of this
     * {@code NameUsage}, or null if no type is designated
     */
    public String getTypeOfType()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getTypeOfType();

	return typeOfType;
    }

    /**
     * Returns {@code NameUsage} representing type taxon of this
     * {@code NameUsage}, or null if no type is designated
     *
     * @return NamedObject representing type taxon of this
     * {@code NameUsage}, or null if no type is designated
     */
    public void setTypeOfType(String typeOfType)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setTypeOfType(typeOfType);
	    return;
	}

	if(typeOfType == this.typeOfType ||
	   (this.typeOfType != null &&
	    this.typeOfType.equals(typeOfType)))
	    return;

	if(typeOfType == null)
	    setType(false);
	else {
	    setType(true);
	    this.typeOfType = typeOfType;
	}

    }

    /**
     * Returns true if this {@code NamedObject} is a type
     *
     * @return true if this {@code NamedObject} is a type
     */
    public boolean isType()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.isType();

	if(typeDesignators != null)
	    return true;
	else if(higherNameUsage != null && higherNameUsage.getTypeUsage()!= null)
	    return higherNameUsage.getTypeUsage().getEntity() == getEntity();
	else
	    return false;
    }
    
    /**
     * Sets boolean as type of taxon
     *
     * @param type boolean true if this taxon is a nominal type
     */
    public void setType(boolean type)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setType(type);
	    return;
	}

	if(higherNameUsage == null)
	    return;

	if(type)
	    higherNameUsage.setTypeUsage(this);
	else if(higherNameUsage.getTypeUsage() == this)
	    higherNameUsage.setTypeUsage(null, null);
    }

    /**
     * Sets boolean as type of taxon
     *
     * @param type boolean true if this taxon is a nominal type
     */
    public void setType(String typeOfType)
    {
	if(typeOfType == null)
	    return;
	if(higherNameUsage != null)
	    higherNameUsage.setTypeUsage(this, typeOfType);
    }

    /**
     * Sets {@code type} as type of this {@code NameUsage}.
     * The  {@code type} may be null.
     *
     * @param type {@code NameUsage} to be designated as the type of
     * this {@code NameUsage}
     */
    public void setTypeUsage(NameUsage<? extends N, ? extends N> typeUsage)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setTypeUsage(typeUsage);
	}
	else {
	    if(typeUsage != null) {
		NameUsage<N, T> typeUsageEntity = getNameUsage(typeUsage).getNameUsage();
		if(typeUsage == this.typeUsage || typeUsage == this
		   || typeUsageEntity == this.typeUsage || typeUsageEntity == this)
		    return;
	    }

	    this.typeUsage = typeUsage;
	}
    }

    /**
     * Sets {@code type} as {@code typeOfType} of this {@code NameUsage}
     * The  {@code type} may be null.  If {@code type} is null,
     * {@code typeOfType} is ignored.
     *
     * @param type {@code NameUsage} to be designated as the type of
     * this {@code NameUsage}
     * @param typeOfType {@code String} represents type of type, e.g.
     * holotype.
     * 
     */
    public void setTypeUsage(NameUsage<? extends N, ? extends N> typeUsage, String typeOfType)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setTypeUsage(typeUsage, typeOfType);
	    return;
	}

	setTypeUsage(typeUsage);
	if(typeUsage != null) {
	    setTypeOfType(typeOfType);
	}
	else {
	    setTypeOfType(null);
	}
    }

    /**
     * Adds {@code designator} as one of {@code NameUsage}s
     * designating this {@code NameUsage} as its type.
     *
     * @param designator {@code NameUsage} designating this {@code NameUsage}
     * as its type
     */
    /*
    public void addTypeDesignator(NameUsage<? extends N, ? extends N> designator, TypeStatus typeStatus)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.addTypeDesignator(designator, typeStatus);
	    return;
	}

	if(designator == null)
	    return;
    */
	/*
	if(higherNameUsage == null &&
	   typeDesignators == null &&
	   typeStatus == TypeStatus.NOMINOTYPE) {
	    setHigherNameUsage(designator);
	    this.typeStatus = typeStatus;
	    return;
	}

	if(higherNameUsage == designator)
	    return;
	*/

	//if(ArrayUtility.contains(designator, typeDesignators))
	/*
	if(typeDesignators != null && typeDesignators.contains(designator))
	    return;
	*/
    /*
	if(typeDesignators == null) {
    */
	    /*
	    //typeDesignators = new NameUsage[2];
	    typeDesignators = ArrayUtility.create(designator.getClass(),2);
	    typeDesignators[0] = higherNameUsage;
	    higherNameUsage = null;
	    typeDesignators[1] = designator;
	    */
    /*
	    typeDesignators = Collections.synchronizedMap(new HashMap<NameUsage<? extends N, ? extends N>, TypeStatus>());
	}
	synchronized(typeDesignators) {
	    typeDesignators.put(designator, typeStatus);
    */
	    /*
	    NameUsage<? extends N, ? extends N>[] tmp = ArrayUtility.create(typeDesignators[0].getClass(),typeDesignators.length + 1);
	    System.arraycopy(typeDesignators, 0, tmp, 0, typeDesignators.length);
	    tmp[typeDesignators.length] = designator;
	    ArrayUtility.clear(typeDesignators);
	    typeDesignators = tmp;
	    */
	    /*
	    getNameUsage(designator).setTypeUsage(getNameUsage(this));
	}
    }
	    */
    /**
     * Removes {@code designator} from the list of {@code NameUsage}s
     * designating this {@code NameUsage} as its type.
     *
     * @param designator {@code NameUsage} to be remvoed from designator list
     * of this {@code NameUsage}
     */
    /*
    public void removeTypeDesignator(NameUsage<? extends N, ? extends N> designator)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.removeTypeDesignator(designator);
	    return;
	}

	if(typeDesignators != null) {
	    typeDesignators.remove(designator);
	}
	getNameUage(designator).setTypeUsage(null);

    */
	/*

	if(typeDesignators == null) {
	    if(higherNameUsage == designator) {
		higherNameUsage.setTypeUsage(null, null);
		higherNameUsage = null;
	    }
	    return;
	}

	int d = findTypeDesignator(designator);
	if(d == -1)
	    return;

	designator.setTypeUsage(null, null);

	NameUsage[] tmp = null;
	if(typeDesignators.length == 2) {
	    higherNameUsage = getNameUsage(typeDesignators[(d == 0)? 1 : 0]);
	}
	else {
	    tmp = ArrayUtility.remove(designator, typeDesignators);
	}
	synchronized (this) {
	    ArrayUtility.clear(typeDesignators);
	    typeDesignators = tmp;
	}
	*/
    /*
    }
    */
    /**
     * Returns {@code Enumeration} of {@code Annotatoin}s
     * or null if none
     *
     * @return An array of {@code Annotatoin}s
     * or null if none
     */
    public Collection<Annotation> getAnnotations()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    return n.getAnnotations();
	}

	if(annotations == null || annotations.size() == 0)
	    return null;

	return annotations;
    }

    public Collection<Annotation> getAnnotations(String linkType)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    return n.getAnnotations(linkType);
	}

	annotations = getAnnotations();

	if(linkType == null && annotations != null)
	    return Collections.synchronizedList(new ArrayList<Annotation>(annotations));

	Collection<Annotation> result = Collections.synchronizedList(new ArrayList<Annotation>());
	if(annotations != null) {
	    for(Annotation annotation : annotations) {
		if(linkType.equals(annotation.getLinkType()))
		    result.add(annotation);
	    }
	}
	return result;
    }

    /**
     * Sets {@code annotations} as the list of {@code Annotation}s made by this {@code NameUsage}
     *
     * @param annotations An array of {@code Annotation}s made by this {@code NameUsage}
     */
    public void setAnnotations(Collection<? extends Annotation> annotations)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setAnnotations(annotations);
	    return;
	}

	if(this.annotations != null) {
	    this.annotations.clear();
	}

	if(annotations == null || annotations.size() == 0) {
	    this.annotations = null;
	    return;
	}

	if(this.annotations == null) {
	    this.annotations = Collections.synchronizedList(new ArrayList<Annotation>(annotations));
	}
	else {
	    for (Annotation annotation:annotations) {
		this.annotations.add(annotation);
	    }
	}
    }

    /**
     * Adds {@code Annotation} to the list of {@code Annotation}s made by this {@code NameUsage}
     * It returns true if the {@code annotation} added to the
     * list successfuly, or false if the {@code annotation} is
     * already in the list.
     *
     * @param annotation {@code Annotation} to be added to the list of {@code Annotation}s made by this {@code NameUsage}
     *
     * @return true if {@code annotation} was appended to the list of {@code Annotation}s made by this {@code NameUsage}
     * successfully, or false if {@code annotation} is already in the list
     */
    public boolean addAnnotation(Annotation annotation)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.addAnnotation(annotation);

	if(annotation == null)
	    return false;

	Annotation a = annotation.getEntity();
	if(a != null && a != annotation)
	    annotation = a;

	if(this.annotations == null) {
	    this.annotations = Collections.synchronizedList(new ArrayList<Annotation>());
	}

	boolean result = false;
	synchronized(this.annotations) {
	    if(!this.annotations.contains(annotation)) {
		result = this.annotations.add(annotation);
	    }
	}

	return result;
    }

    /**
     * Removes {@code annotation} from the list of {@code Annotation}s made by this {@code NameUsage}
     *
     * @param annotation {@code NameUsage} to be removed fromthe list of {@code Annotation}s made by this {@code NameUsage}
     */
    public boolean removeAnnotation(Annotation annotation)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    return n.removeAnnotation(annotation);
	}

	if(this.annotations == null || !this.annotations.contains(annotation))
	    return false;

	annotation.removeAnnotator(this);
	return this.annotations.remove(annotation);
    }

    /**
     * Removes all {@code Annotation} from the list of {@code Annotation}s made by this {@code NameUsage}
     */
    public void removeAnnotations()
    {
	setAnnotations(null);
    }

    /**
     * Returns {@code NameUsage} representing in which sensu the name
     * is used by this {@code NameUsage}.
     * It may designate the authoritative usage of the name.
     *
     * @return {@code NameUsage} representing {@code NameUsage} in which sens
     * it was used.  It may desiganate the authoritative usage of the name as {@code NameUsage}.
     */
    //public NameUsage<? extends N, ? extends N> getSensu()
    public NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> getSensu()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getSensu();

	return sensu;
    }

    /**
     * Sets {@code sensu} in which the {@code NameUsage} is used.
     * It may designate the autoritative usage of the name.
     *
     * @param sensu {@code NameUsage} representing the sens of this {@code NameUsage}
     */
    //public void setSensu(NameUsage<? extends N, ? extends N> sensu)
    public void setSensu(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> sensu)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setSensu(sensu);
	    return;
	}

	if(this.sensu == sensu)
	    return;

	this.sensu = sensu;
    }

    /**
     * Returns {@code Enumeration} of author of the name,
     * or null if none.
     * It shall be null if authors of the name match to authors of autority
     * {@code NameUsage}.
     *
     * @return Enumertion of author of the name
     * or null if none.
     */
    public Collection<Author> getAuthors()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getAuthors();

	Collection<Author> toReturn = null;
	if(authors == null || authors.size() == 0) {
	    Publication publication = getPublication();
	    if(publication != null) {
		toReturn = getAuthors();
	    }
	}
	else {
	    toReturn = authors;
	}
	return toReturn;
    }

    @Override
    public void setAuthors(Collection<? extends Author> authors)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setAuthors(authors);
	    return;
	}

	if(this.authors != null)
	    this.authors.clear();

	if(authors == null || authors.size() == 0)
	    return;

	if(authors == null)  {
	    this.authors = Collections.synchronizedList(new ArrayList<Author>(authors));
	}
	else {
	    for(Author author : authors) {
		this.authors.add(author);
	    }
	}
    }

    /**
     * Adds {@code Author} to the list of {@code Author}s of the name.
     * It returns true if the {@code author} added to the
     * list successfuly, or false if the {@code author} is
     * already in the list.
     * <P>
     * Is shall not be called if authors  of thename matche to authors of autority
     * {@code NameUsage}.
     *
     * @param author {@code Author} to be added to the list of {@code Author}s of the name
     *
     * @return true if {@code author} was appended to the list
     * successfully, or false if {@code author} is already in the list
     */
    public boolean addAuthor(Author author)
    {
	//fixme
	if(author == null)
	    return false;

	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.addAuthor(author);

	if(authors == null)
	    authors = Collections.synchronizedList(new ArrayList<Author>());

	if(authors.contains(author))
	    return false;

	return authors.add(author);
    }

    /**
     * Removes {@code author} from the list of {@code Author}s of the name
     *
     * @param author {@code NameUsage} to be removed fromthe list of {@code Author}s of the name
     */
    public boolean removeAuthor(Author author)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    return n.removeAuthor(author);
	}

	if(authors == null || !authors.contains(author))
	    return false;

	return authors.remove(author);
    }

    /**
     * Removes {@code author} from the list of {@code Author}s of the name
     *
     * @param author {@code NameUsage} to be removed fromthe list of {@code Author}s of the name
     */
    public void removeAuthors()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    if(getClass().isInstance(n))
		((AbstractNameUsage<N, T>)n).removeAuthors();
	}
	else if(authors != null) {
		authors.clear();
	}
    }

    /**
     * Returns authority, or null if unspecified/unknown.
     *
     * @return String representing authority
     */
    public String getAuthority()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getAuthority();

	return authority;
    }

    /**
     * Sets {@code authority}.
     *
     * @param authority of the {@code NameUsage}
     */
    public void setAuthority(String authority)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setAuthority(authority);
	    return;
	}

	this.authority = authority;
    }


    /**
     * Returns authority year in {@code int}.
     * It must be zero if authors of authority {@code NameUsage}
     * are nominal authority of the name.
     *
     * @return int representing authority year.
     * i.e. first usage record of the name
     */
    public Integer getAuthorityYear()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getAuthorityYear();

	return year;
    }

    /**
     * Sets year as authority year.
     * It shall be used only if authority {@code NameUsage}
     * gives aurthority author names difference from authors
     * of the authority {@code NameUsage}, or if
     * the authority {@code NameUsage} is unknown but
     * author names and year are known.
     *
     * @param year {@code int} representing authority year.
     */
    public void setAuthorityYear(Integer year)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.setAuthorityYear(year);
	    return;
	}

	if(this.year == year || Objects.equals(this.year, year))
	    return;

	this.year = year;
    }

    /**
     * create XML String 
     *
     * @return XML String representing this NameUsage object
     */
    public String toXML()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.toXML();

	//Enumeration e = null;
	StringBuffer buf = new StringBuffer();
	buf.append("<NameUsage>\n");
	buf.append("<oid>").append(getPersistentID()).append("</oid>\n");
	if(rankLiteral != null)
	    buf.append("<rank>").append(rankLiteral).append("</rank>\n");
	if(nameLiteral != null)
	    buf.append("<name>").append(nameLiteral).append("</name>\n");

	if(locale != null)
	    //buf.append("<locale>").append(locale.toString()).append("</locale>\n");
	    buf.append("<locale>").append(locale).append("</locale>\n");

	Appearance appearance = getAppearance();
	if(appearance != null)
    	    buf.append("<appearance>").append(appearance.getPersistentID()).append("</appearance>\n");
	else {
	    Publication publication = getPublication();
	    if(publication != null)
		buf.append("<publication>").append(publication.getPersistentID()).append("</publication>\n");
	}

	if(sensu != null)
    	    buf.append("<sensu>").append(sensu.getPersistentID()).append("</sensu>\n");

	if(authors != null) {
	    for (Author author : authors) {
		buf.append("<author>").append(author.getPersistentID()).append("</author>\n");
	    }
	}

	if(year != 0)
    	    buf.append("<year>").append(year).append("</year>\n");

    	if(higherNameUsage != null) {
    	    buf.append("<higher");
	    if(incertaeSedis)
		buf.append(" attribute=incertaeSedis");
	    buf.append('>').append(higherNameUsage.getPersistentID()).append("</higher>\n");
    	}
	
	if(lowerNameUsages != null){
	    synchronized(lowerNameUsages) {
		for(NameUsage<? extends N, ? extends N> lowerNameUsage : lowerNameUsages) {
		    buf.append("<lowerNameUsage>").append(lowerNameUsage.getPersistentID()).append("</lowerNameUsage>\n");
		}
	    }
	}

	if(annotations != null) {
	    for(Annotation annotation : annotations) {
		buf.append("<annotation>").append(annotation.getPersistentID()).append("</annotation>\n");
	    }
	}

	if(getNotes() != null)
	    buf.append("<notes>").append(getNotes()).append("</notes>\n");
	
	if(isType())
	    buf.append("<isType>true</isType>\n");

	if(typeUsage != null) {
	    buf.append("<type");
	    if(typeOfType != null)
		buf.append(" type=\"").append(typeOfType).append("\"");
	    buf.append(">").append(typeUsage.getPersistentID()).append("</type>\n");
	}

	/*
	if(typeDesignators != null) {
	    synchronized(typeDesignators) {
		for(int i = 0; i < typeDesignators.length; i++) {		
		    buf.append("<typeDesignator>").append(typeDesignators[i].getName()).append("</typeDesignator>");
		}
	    }
	}
	*/

	buf.append("</NameUsage>\n");
	
        return buf.toString();
    }

    /**
     * create XML String of the all related {@code NamedObject}s
     *
     * @return XML String representing this {@code NameUsage} object
     */
    public String toRelevantXML()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.toRelevantXML();

	// create XML String of the NameUsage itself
	StringBuffer buf = new StringBuffer();
	buf.append(toXML());
	
	// create XML of the higher NameUsage
	if(higherNameUsage != null) {
    	    buf.append(higherNameUsage.toXML());
        }

	// create XML of the lower NameUsage
	if(lowerNameUsages != null) {
	    synchronized(lowerNameUsages) {
		for(NameUsage<? extends N, ? extends N> lowerNameUsage : lowerNameUsages) {
		    buf.append(lowerNameUsage.toXML());
		}
	    }
        }

	// create XML of the authority NameUsage
	if(sensu != null) {
    	    buf.append(sensu.toXML());
    	}

	if(authors != null) {
	    for(Author author :authors) {
		buf.append(author.toXML());
	    }
	}

	// create XML of the recorder Appearance
	Appearance appearance = getAppearance();
	if(appearance != null) {
    	    buf.append(appearance.toXML());
    	}
	
	// create XML of the related Annotation
	if(annotations != null) {
	    for(Annotation annotation : annotations) {
    	        buf.append(annotation.toXML());
	    }
    	}

	return buf.toString();
    }

    /**
     * Sets valuse of this {@code NamedObject} to
     * {@code statement} using {@code connection}.
     * from specified {@code index} of the {@code statement}
     *
     * @param statement {@code PraredStatement} to which
     * value of the this {@code NamedObject} to be set
     * @param connection {@code NamedObjectConnection}
     * to be used to set values
     * @param index {@code int} from where values to be set
     * into the {@code statement}
     *
     * @return int index of the next parameter to be set if there is
     *
     * @exception SQLException
     */
    public int setValues(PreparedStatement statement,
			 NamedObjectConnection<?> connection,
			 int index)
			  throws SQLException
    {
	return connection.setValues(statement, this, index);
    }


    /**
     * Appends a SQL to insert to a relevant table at the end of
     * {@code buffer}.  If {@code buffer} is null,
     * it creates a new {@code StringBuffer}.
     *
     * @param buffer {@code StringBuffer} to which the
     * SQL to be appended, or null to create a new
     * CODE>StringBuffer}
     *
     * @return StringBuffer containing the SQL at the end
     */
    public StringBuffer toSQL(StringBuffer buffer,
			      int objectID)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return ((SQLSerializable)n).toSQL(buffer, objectID);

	buffer = super.toSQL(buffer, objectID);
	/*
	String[] sql = new String[8];
	int i = 0;
	sql[i++] = getPersistentID();
	sql[i++] = rankLiteral;
	sql[i++] = nameLiteral;
	sql[i++] = (authority == null)?   "null" : authority.getPersistentID();
	sql[i++] = (appearance == null)?    "null" : appearance.getPersistentID();
	sql[i++] = (higherNameUsage == null)?      "null" : higherNameUsage.getPersistentID();
	sql[i++] = new Boolean(isType).toString();

	return sql;
	*/
	return buffer;
    }

    /**
     * Sets paremeters to {@code statement} to INSERT, UPDATE 
     * or DELETE
     *
     * @param statement {@code PreparedStatement} to which 
     * paramters to be set
     *
     * @param objectID unique ID number in {@code long}
     * to be assigned to the object on the databaes
     *
     * @return number of parameters
     */
    public int setUpdateParamters(PreparedStatement statement,
				  int objectID)
	throws SQLException
    {
	int field = 1 + super.setUpdateParamters(statement, objectID);

	return field;
    }


    /**
     * Returns name of taxonomic view where this {@code NameUsage}
     * belongs to.
     *
     * @return String representing name of the taxonomic view
     */
    public String getViewName()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getViewName();

	return getViewName(false);
    }

    /**
     * Returns name of taxonomic view where this {@code NameUsage}
     * belongs to.  The parameter {@code toSort} determines
     * words order in the name.  If {@code toSort} is true,
     * the first word represents the year of name publciation.
     *
     * @param toSort boolean to determine words order
     *
     * @return String representing name of the taxonomic view
     */
    public String getViewName(boolean toSort)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getViewName(toSort);

	Publication publication = getPublication();
	if(publication == null)
	    return "";

	String viewName = publication.getViewName();
	if(!toSort)
	    return viewName;
	else
	    return viewName;
    }

    /**
     * Returns yaer of publication as {@code String}
     *
     * @return String representing the year of name publication
     */
    public String getYear()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getYear();
	
	Publication p = getPublication();
	if(p != null)
	    return p.getYear();

	return null;
    }

    /**
     * Sets a {@code object} as the entity of the name
     *
     * @param object representing the entity
     */
    /*
    public void setEntity(Object object)
    {
	if(object == null ||
	   (object != null && !(object instanceof NameUsage)))
	    throw new IllegalArgumentException(object.getClass().getName() + " can't be an entity of " + getClass().getName());
	super.setEntity(object);
    }
    */

    /**
     * Returns root {@code NameUsage} of the name hierarchy
     * where this {@code NameUsage} belongs to.
     *
     * @return root {@code NameUsage} of the name hierarhcy.
     */
    public NameUsage<N, T> getRoot()
    {
	NameUsage<N, T> root = getHigherNameUsage();

	if(root != this && root != null)
	    return root.getRoot();

	return root;
    }

    /**
     * Returns true if lower taxa are assigned to this
     *
     * @return whether lower taxa are assigned
     */
    public boolean hasLowerNameUsages()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.hasLowerNameUsages();

	return (getLowerNameUsagesCount() > 0);
    }

    /**
     * Returns {@code NameUsage} proxied by this {@code NameUsag}
     *
     * @return NameUsage proxied by this {@code NameUsag}
     */
    public NameUsage<N, T> getNameUsage()
    {
	if(entity == null || !isAssignableFrom(entity)) {
	    return this;
	}
	/*
	if(entity instanceof NameUsage) {
	    //return ((NameUsage)entity).getNameUsage();
	    return getNameUsage(entity);
	}
	return null;
	*/
	return getNameUsage(entity).getNameUsage();
    }

    /**
     * Gets entity of this {@code Name}.
     * Returns itself if it doesn't have entity separately,
     * or entity if the entity non-null.
     * If the entity is an instance of {@code Name},
     * the method returns the result of recursive apply of 
     * this method
     *
     * @return Object enitity representied by the name.  It is never null.
     */
    /*
    public Object getEntity()
    {
	return getNameUsage();
    }
    */

    /**
     * Returns {@code Hashtable} containing
     * leaf taxa, i.e. taxa without children,
     * under this {@code NameUsage}
     * or null if none
     *
     * @return {@code Hashtable} containing
     * leaf taxa under this {@code NameUsage}
     * or null if none
     */
    public Map<String, NameUsage<? extends N, ? extends N>> getLeafNameUsages()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getLeafNameUsages();

	if(!hasLowerNameUsages())
	    return null;

	Map<String, NameUsage<? extends N, ? extends N>> leafNameUsageMap =  Collections.synchronizedMap(new HashMap<String, NameUsage<? extends N, ? extends N>>());

	putLeafNameUsagesTo(leafNameUsageMap);

	return leafNameUsageMap;
    }

    public Map<String, NameUsage<? extends N, ? extends N>> putLeafNameUsagesTo(Map<String, NameUsage<? extends N, ? extends N>> leafNameUsageMap)
    {
	if(leafNameUsageMap == null)
	    leafNameUsageMap = Collections.synchronizedMap(new HashMap<String, NameUsage<? extends N, ? extends N>>());

	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    return n.putLeafNameUsagesTo(leafNameUsageMap);
	}
	if(!hasLowerNameUsages()) {
	    leafNameUsageMap.put(getLiteral(), this);
	}
	else if(lowerNameUsages != null) {
	    synchronized (lowerNameUsages) {
		for(NameUsage<? extends N, ? extends N> lowerNameUsage : lowerNameUsages) {
		    getNameUsage(lowerNameUsage).putLeafNameUsagesTo(leafNameUsageMap);
		}
	    }
	}

	return leafNameUsageMap;
    }

    /**
     * Returns {@code Hashtable} containing
     * lower taxa at {@code rank} under this
     * {@code NameUsage} or null if none
     *
     * @param rank {@code Rank} of {@code NameUsage}
     * to be included in return set of
     *
     * @return {@code Hashtable} containing
     * lower taxa at {@code rank} under this
     * {@code NameUsage} or null if none
     */
    public Map<String, NameUsage<? extends N, ? extends N>> getLowerNameUsagesAt(Rank rank)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getLowerNameUsagesAt(rank);

	if(rankLiteral == null || 
	   rank.isHigher(getRankLiteral()) ||
	   !hasLowerNameUsages())
	   return null;

	Map<String, NameUsage<? extends N, ? extends N>> lowerNameUsageMap = new HashMap<String, NameUsage<? extends N, ? extends N>>();

	putLowerNameUsagesAt(rank, lowerNameUsageMap);

	return lowerNameUsageMap;
    }

    public void putLowerNameUsagesAt(Rank rank, Map<String, NameUsage<? extends N, ? extends N>> lowerNameUsageMap)
    {
	if(lowerNameUsageMap == null)
	    lowerNameUsageMap = Collections.synchronizedMap(new HashMap<String, NameUsage<? extends N, ? extends N>>());

	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.putLowerNameUsagesAt(rank, lowerNameUsageMap);
	    return;
	}

	if(rank.equals(n.getRankLiteral())) {
	    lowerNameUsageMap.put(getLiteral(), this);
	    return;
	}

	if(rank.isLower(n.getRankLiteral()))
	    return;
	
	if(lowerNameUsages != null) {
	    synchronized(lowerNameUsages) {
		for(NameUsage<? extends N, ? extends N> lowerNameUsage : lowerNameUsages) {
		    getNameUsage(lowerNameUsage).putLowerNameUsagesAt(rank, lowerNameUsageMap);
		}
	    }
	}
    }

    /**
     * Returns {@code Hashtable} containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage} or null if none
     *
     * @param rank lowest {@code Rank} of {@code NameUsage}
     * to be included in return set
     *
     * @return {@code Hashtable} containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage} or null if none
     */
    public Map<String, NameUsage<? extends N, ? extends N>> getLeafNameUsagesUntil(String rank)
    {
	return getLeafNameUsagesUntil(Rank.get(rank));
    }

    /**
     * Returns {@code Hashtable} containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage} or null if none
     *
     * @param rank lowest {@code Rank} of {@code NameUsage}
     * to be included in return set
     *
     * @return {@code Hashtable} containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage} or null if none
     */
    public Map<String, NameUsage<? extends N, ? extends N>> getLeafNameUsagesUntil(Rank rank)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getLowerNameUsagesAt(rank);

	if(rank == null || 
	   rank.isHigher(getRankLiteral()) ||
	   !hasLowerNameUsages())
	   return null;

	Map<String, NameUsage<? extends N, ? extends N>> lowerNameUsageMap = new HashMap<String, NameUsage<? extends N, ? extends N>>();

	putLeafNameUsagesUntil(rank, lowerNameUsageMap);

	return lowerNameUsageMap;
    }

    public void putLeafNameUsagesUntil(Rank rank, Map<String, NameUsage<? extends N, ? extends N>> leaves)
    {
	if(leaves == null)
	    leaves = Collections.synchronizedMap(new HashMap<String, NameUsage<? extends N, ? extends N>>());

	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.putLowerNameUsagesAt(rank, leaves);
	    return;
	}

	if(rank.isLower(n.getRankLiteral()))
	    return;

	if(rank.equals(n.getRankLiteral()) ||
	   !hasLowerNameUsages()) {
	    leaves.put(getLiteral(), this);
	    return;
	}

	if(lowerNameUsages != null) {
	    synchronized (lowerNameUsages) {
		for(NameUsage<? extends N, ? extends N> lowerNameUsage : lowerNameUsages) {
		    getNameUsage(lowerNameUsage).putLeafNameUsagesUntil(rank, leaves);
		}
	    }
	}
    }

    /**
     * Returns {@code Hashtable} containing all
     * lower taxa under this {@code NameUsage}
     * or null if none
     *
     * @return {@code Hashtable} containing all
     * lower taxa under this {@code NameUsage}
     * or null if none
     */
    public Map<String, NameUsage<? extends N, ? extends N>> getAllLowerNameUsages()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getAllLowerNameUsages();

	if(!hasLowerNameUsages())
	    return null;

	Map<String, NameUsage<? extends N, ? extends N>> allLowerNameUsages = Collections.synchronizedMap(new HashMap<String, NameUsage<? extends N, ? extends N>>());

	putAllLowerNameUsagesTo(allLowerNameUsages);

	return allLowerNameUsages;
    }

    public void putAllLowerNameUsagesTo(Map<String, NameUsage<? extends N, ? extends N>> lowerNameUsageMap)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this) {
	    n.putAllLowerNameUsagesTo(lowerNameUsageMap);
	    return;
	}

	lowerNameUsageMap.put(getLiteral(), this);

	if(lowerNameUsages !=  null) {
	    synchronized(lowerNameUsages) {
		for(NameUsage<? extends N, ? extends N> lowerNameUsage : lowerNameUsages) {
		    getNameUsage(lowerNameUsage).putLeafNameUsagesTo(lowerNameUsageMap);
		}
	    }
	}
    }

    /**
     * Returns {@code Hashtable} of 
     * {@code Hashtable}s, containing
     * leaf taxa, i.e. taxa without children,
     * under this {@code NameUsage}, indexed by
     * their ascribed names, or null if none
     *
     * @return {@code Hashtable} of {@code Hashtable}s
     * containing leaf taxa, under this {@code NameUsage},
     * indexed by their ascribed names, or null if none
     */
    public Map<String, Set<NameUsage<? extends N, ? extends N>>> getLeafNames()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getLeafNames();

	Map<String, NameUsage<? extends N, ? extends N>> taxa = getLeafNameUsages();
	Map<String, Set<NameUsage<? extends N, ? extends N>>> names = convertNameUsagesToNames(taxa);
	taxa.clear();
	return names;
    }

    /**
     * Returns {@code Hashtable} of 
     * {@code Hashtable}s containing
     * lower taxa at {@code rank} under this
     * {@code NameUsage}, indexed by
     * their ascribed names, or null if none
     *
     * @param rank {@code Rank} of {@code NameUsage}
     * to be included in return set of
     *
     * @return {@code Hashtable} of {@code Hashtable}s containing
     * lower taxa at {@code rank} under this
     * {@code NameUsage}, indexed by their ascribed names,
     * or null if none
     */
    public Map<String, Set<NameUsage<? extends N, ? extends N>>> getLowerNamesAt(Rank rank)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getLowerNamesAt(rank);

	Map<String, NameUsage<? extends N, ? extends N>> taxa = getLowerNameUsagesAt(rank);
	Map<String, Set<NameUsage<? extends N, ? extends N>>> names = convertNameUsagesToNames(taxa);
	taxa.clear();
	return names;
    }

    /**
     * Returns {@code Hashtable} of 
     * {@code Hashtable}s containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage}, indexed by
     * their ascribed names, or null if none
     *
     * @param rank lowest {@code Rank} of {@code NameUsage}
     * to be included in return set
     *
     * @return {@code Hashtable} of {@code Hashtable}s containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage}, indexed by their ascribed names,
     * or null if none
     */
    public Map<String, Set<NameUsage<? extends N, ? extends N>>> getLeafNamesUntil(String rank)
    {
	return getLeafNamesUntil(Rank.get(rank));
    }

    /**
     * Returns {@code Hashtable} of {@code Hashtable}s containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage} or null if none
     *
     * @param rank lowest {@code Rank} of {@code NameUsage}
     * to be included in return set
     *
     * @return {@code Hashtable} of {@code Hashtable}s containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage}, indexed by their ascribed names,
     * or null if none
     */
    public Map<String, Set<NameUsage<? extends N, ? extends N>>> getLeafNamesUntil(Rank rank)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getLeafNamesUntil(rank);

	Map<String, NameUsage<? extends N, ? extends N>> taxa = getLeafNameUsagesUntil(rank);
	Map<String, Set<NameUsage<? extends N, ? extends N>>> names = convertNameUsagesToNames(taxa);
	taxa.clear();
	return names;
    }

    /**
     * Returns {@code Hashtable} of {@code Hashtable}s containing all
     * lower taxa under this {@code NameUsage}
     * or null if none
     *
     * @return {@code Hashtable} of {@code Hashtable}s containing all
     * lower taxa under this {@code NameUsage}, indexed by their ascribed names,
     * or null if none
     */
    public Map<String, Set<NameUsage<? extends N, ? extends N>>> getAllLowerNames()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getAllLowerNames();

	Map<String, NameUsage<? extends N, ? extends N>> taxa = getAllLowerNameUsages();
	Map<String, Set<NameUsage<? extends N, ? extends N>>> names = convertNameUsagesToNames(taxa);
	taxa.clear();
	return names;
    }


    /**
     * Returns {@code Hashtable} indexed by ascribed names
     * of {@code NameUsages} in {@code taxa}, i.e. name
     * list of {@code taxa}.  Because there may be two or more
     * {@code NameUsages} having the same ascribed name in
     * {@code taxa}, returned {@code Hashtable} values
     * are {@code Hashtable} containing {@code NameUsages}s.
     *
     * @param taxa {@code Hashtable} of {@code NameUsages}s
     * to be converted to name list
     *
     * @return {@code Hashtable} of {@code Hashtable}s of
     * {@code NameUsages}s indexed by asricbed names
     */
    public Map<String, Set<NameUsage<? extends N, ? extends N>>> convertNameUsagesToNames(Map<String, NameUsage<? extends N, ? extends N>> taxa)
    {
	Collection<NameUsage<? extends N, ? extends N>> c = taxa.values();
	if(c == null)
	    return null;
	Iterator<NameUsage<? extends N, ? extends N>> e = c.iterator();

	Map<String, Set<NameUsage<? extends N, ? extends N>>> names = 
	    Collections.synchronizedMap(new HashMap<String, Set<NameUsage<? extends N, ? extends N>>>());

	while(e.hasNext()) {
	    NameUsage<? extends N, ? extends N> n = e.next();
	    String name = n.getLiteral();
	    Set<NameUsage<? extends N, ? extends N>> lowerNameUsagesSet = names.get(name);
	    if(lowerNameUsagesSet == null) {
		lowerNameUsagesSet = Collections.synchronizedSet(new HashSet<NameUsage<? extends N, ? extends N>>(1));
		names.put(name, lowerNameUsagesSet);
	    }
	    lowerNameUsagesSet.add(n);
	}

	return names;
    }

    /**
     * Returns {@code Hashtable} indexed by ascribed names
     * of {@code NameUsages} in {@code taxa}, i.e. name
     * list of {@code taxa}.  Because there may be two or more
     * {@code NameUsages} having the same ascribed name in
     * {@code taxa}, returned {@code Hashtable} values
     * are {@code Hashtable} containing {@code NameUsages}s.
     * These inner {@code Hashtable} are indexed by rank.  It
     * is unsafe to apply it on {@code taxa} contaiing
     * multiple hierarhies where combination of rank and ascribed name
     * is unnecessary to be uniquie to a {@code NameUsages}.
     *
     * @param taxa {@code Hashtable} of {@code NameUsages}s
     * to be converted to name list
     *
     * @return {@code Hashtable} of {@code Hashtable}s of
     * {@code NameUsages}s indexed by asricbed names
     */
    public Map<String, Map<String, Set<NameUsage<? extends N, ? extends N>>>> convertNameUsagesToRankedNames(Map<String, NameUsage<? extends N, ? extends N>> taxa)
    {
	Map<String, Set<NameUsage<? extends N, ? extends N>>> names = convertNameUsagesToNames(taxa);
	Map<String, Map<String, Set<NameUsage<? extends N, ? extends N>>>> rankedNames = convertNamesToRankedNames(names);
	names.clear();
	return rankedNames;
    }

    /**
     * Returns {@code Hashtable} indexed by ascribed names
     * of {@code NameUsages} in {@code names}, i.e. name
     * list of {@code names}.  Because there may be two or more
     * {@code NameUsages} having the same ascribed name in
     * {@code names}, returned {@code Hashtable} values
     * are {@code Hashtable} containing {@code NameUsages}s.
     *
     * @param names {@code Hashtable} of {@code NameUsages}s
     * to be converted to name list
     *
     * @return {@code Hashtable} of {@code Hashtable}s of
     * {@code NameUsages}s indexed by asricbed names
     */
    public Map<String, Map<String, Set<NameUsage<? extends N, ? extends N>>>> convertNamesToRankedNames(Map<String, Set<NameUsage<? extends N, ? extends N>>> names)
    {
	Set<String> keys = names.keySet();
	if(keys == null  || keys.size() == 0)
	    return null;

	Map<String, Map<String, Set<NameUsage<? extends N, ? extends N>>>> rankedNames = Collections.synchronizedMap(new HashMap<String, Map<String, Set<NameUsage<? extends N, ? extends N>>>>());

	for(String name : keys) {
	    Map<String, Set<NameUsage<? extends N, ? extends N>>> nameList = rankedNames.get(name);
	    if(nameList == null) {
		nameList = Collections.synchronizedMap(new HashMap<String, Set<NameUsage<? extends N, ? extends N>>>());
		rankedNames.put(name, nameList);
	    }
	    Set<NameUsage<? extends N, ? extends N>> nameUsages =  names.get(name);
	    for(NameUsage<? extends N, ? extends N> n : nameUsages) {
		String rankedName = n.getRankLiteral();
		Set<NameUsage<? extends N, ? extends N>> nameSet = nameList.get(rankedName);
		if(nameSet == null) {
		    nameSet = Collections.synchronizedSet(new HashSet<NameUsage<? extends N, ? extends N>>());
		    nameList.put(rankedName, nameSet);
		}
		nameSet.add(n);
	    }
	}

	return rankedNames;
    }

    public Map<String, NameUsage<? extends N, ? extends N>> getSiblings()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getSiblings();

	NameUsage<N, T> parent = getHigherNameUsage();
	if(parent == null)
	    return Collections.synchronizedMap(new HashMap<String, NameUsage<? extends N, ? extends N>>());
	Map<String, NameUsage<? extends N, ? extends N>> siblings = 
	    Collections.synchronizedMap(new HashMap<String, NameUsage<? extends N, ? extends N>>(parent.getLowerNameUsagesSet()));
	NameUsage<? extends N, ? extends N> toRemove = siblings.get(getLiteral());
	if(toRemove == this)
	    siblings.remove(getLiteral());
	return siblings;
    }

    public Map<String, NameUsage<? extends N, ? extends N>> getLowerNameUsagesSet()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getLowerNameUsagesSet();

	Map<String, NameUsage<? extends N, ? extends N>> lowerNameUsagesSet = new HashMap<String, NameUsage<? extends N, ? extends N>>();
	if(lowerNameUsages != null) {
	    synchronized(lowerNameUsages) {
		for(NameUsage<? extends N, ? extends N> lowerNameUsage: lowerNameUsages) {
		    lowerNameUsagesSet.put(lowerNameUsage.getLiteral(), lowerNameUsage);
		}
	    }
	}
	return lowerNameUsagesSet;
    }

    public Map<String, NameUsage<? extends N, ? extends N>> getIncludants()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getIncludants();

	if(includants != null ||
	   !hasLowerNameUsages())
	    return includants;

	includants = new HashMap<String, NameUsage<? extends N, ? extends N>>();
	synchronized(lowerNameUsages) {
	    if(lowerNameUsages != null) {
		for(NameUsage<? extends N, ? extends N> lowerNameUsage: lowerNameUsages) {
		    Map<String, NameUsage<? extends N, ? extends N>> lowers = getNameUsage(lowerNameUsage).getIncludants();
		    if(lowers == null)
			includants.put(n.getRankedName(true), n);
		    else
			includants.putAll(lowers);
		}
	    }
	}

	return includants;
    }

    public void clearIncludants()
    {
	clearHashtable(includants);
	includants = null;
    }

    @Override
    public  Map<String, NameUsage<? extends N, ? extends N>> getExcludants()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getExcludants();

	n = getHigherNameUsage();
	if(excludants != null ||
	   n == null)
	    return excludants;

	excludants = Collections.synchronizedMap(new HashMap<String, NameUsage<? extends N, ? extends N>>());
	List<NameUsage<N, T>> lowerNameUsagesList = n.getLowerNameUsages();
	Map<String, NameUsage<? extends N, ? extends N>> h = null;
	for(NameUsage<N, T> lowerNameUsage : lowerNameUsagesList) {
	    NameUsage<N, T> taxon = getNameUsage(lowerNameUsage).getNameUsage();
	    if(lowerNameUsage == this || taxon == this)
		continue;
	    h = taxon.getIncludants();
	    if(h == null)
		excludants.put(n.getRankedName(true), taxon);
	    else {
		synchronized(h) {
		    synchronized(excludants) {
			excludants.putAll(h);
		    }
		}
	    }
	}

	n = getHigherNameUsage().getHigherNameUsage();
	if(n != null) {
	    h = n.getExcludants();
	    if(h != null) {
		synchronized(h) {
		    synchronized(excludants) {
			excludants.putAll(h);
		    }
		}
	    }
	}

	return excludants;
    }

    public void clearExcludants()
    {
	excludants.clear();
	excludants = null;
    }

    protected void clearHashtable(Map<?, ?> map)
    {
	if(map == null)
	    return;

	for(Object value : map.values()) {
	    if(value instanceof Map) {
		Map<?, ?> m = (Map<?, ?>)value;
		clearHashtable(m);
		m.clear();
	    }
	}
	map.clear();
    }

    public String getRankedName()
    {
	return getRankedName(false);
    }

    public String getRankedName(boolean abbreviate)
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.getRankedName();

	StringBuffer buffer = new StringBuffer();

	String str = getRankLiteral();
	if(abbreviate && str != null)
	    str = Rank.getAbbreviation(str);
	if(str == null)
	    str = getRankLiteral();
	if(str != null && str.length() > 0)
	    buffer.append(str);
	str = getLiteral();
	if(str != null && str.length() > 0) {
	    if(buffer.length() > 0)
		buffer.append(' ');
	    buffer.append(str);
	}

	return buffer.toString();
    }

    /**
     * Returns a summarized expression of this {@code NamedObject}
     *
     * @return String representing summary of this {@code NamedObject}
     */
    public String getSummary()
    {
	if(entity != null)
	    return ((AbstractNameUsage)getEntity()).getSummary();

	StringBuffer buffer = new StringBuffer();
	String str = getRankLiteral();
	if(str != null)
	    buffer.append(str);
	
	str = getLiteral();
	if(str != null && str.length() > 0) {
	    if(buffer.length() > 0)
		buffer.append(' ');
	    buffer.append(str);
	}

	str = getAuthority();
	if(str != null && str.length() > 0) {
	    if(buffer.length() > 0)
		buffer.append(' ');
	    buffer.append(str);
	}
	String authorityString = str;

	Appearance a = getAppearance();
	if(a != null) {
	    str = a.getSummary();
	    if(str != null && str.length() > 0) {
		if(authorityString != null &&
		   authorityString.length() > 0)
		    buffer.append(" in");
		if(buffer.length() > 0)
		    buffer.append(' ');
		buffer.append(str);
	    }
	}
	
	return buffer.toString();
    }


    protected boolean findAnnotation(Annotation annotation)
    {
	if(annotation == null || annotations == null)
	    return false;

	return annotations.contains(annotation.getEntity());
    }

    protected int findAuthor(Author author)
    {
	if(author == null || authors == null)
	    return -1;

	return authors.indexOf(author.getEntity());

    }

    protected int findLowerNameUsage(NameUsage<? extends N, ? extends N> nameUsage)
    {
	if(nameUsage == null || lowerNameUsages == null)
	    return -1;
	
	//nameUsage = (NameUsage<? extends N, ? extends N>)nameUsage.getEntity();
	nameUsage = getNameUsage(nameUsage.getEntity());

	int i = 0;
	for(NameUsage<? extends N, ? extends N> lowerNameUsage : lowerNameUsages) {
	    if(lowerNameUsage.getEntity() == nameUsage)
		return i;
	    i++;
	}

	return -1;
    }

    /*
    protected int findTypeDesignator(NameUsage designator)
    {
	if(designator == null || typeDesignators == null)
	    return -1;
	
	designator = (NameUsage)designator.getEntity();

	for(int i = 0; i < typeDesignators.length; i++) {
	    if(typeDesignators[i].getEntity() == designator)
		return i;
	}

	return -1;
    }
    */

    protected abstract AbstractNameUsage<N, T> createNameUsage();

    protected abstract NameUsage<N, T> createNameUsage(Name<?, ?> name);

    protected abstract NameUsage<N, T> createNameUsage(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage);

    protected abstract NameUsage<N, T> createNameUsage(String persistentID);

    public boolean isResolved()
    {
	return (isContentsResolved() && isLowerNameUsagesResolved());
    }

    public boolean isContentsResolved()
    {
	return contentsResolved;
    }

    public void setContentsResolved(boolean resolved)
    {
	contentsResolved = resolved;
    }

    public boolean isLowerNameUsagesResolved()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.isLowerNameUsagesResolved();

	List<NameUsage<N, T>> empty = Collections.emptyList();
	List<NameUsage<N, T>> lowers = getLowerNameUsages();	
	if (Objects.equals(empty, lowers))
	    return true;

	if(lowers == null)
	    return false;

	boolean lowerResolved = true;
	
	for (NameUsage<N, T> lower : lowers) {
	    lowerResolved &= lower.isContentsResolved();
	}

	return lowerResolved;
    }

    public boolean isHierarchyResolved()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.isHierarchyResolved();

	return (!(higherNameUsage == null || !isResolvedInFullDepth()));
    }


    public boolean isResolvedInFullDepth()
    {
	NameUsage<N, T> n = getNameUsage();
	if(n != this)
	    return n.isResolvedInFullDepth();

	boolean resolvedInFullDepth = isLowerNameUsagesResolved();

	if(!resolvedInFullDepth)
	    return false;

	List<NameUsage<N, T>> lowers = getLowerNameUsages();	
	for (NameUsage<N, T> lower : lowers) {
	    resolvedInFullDepth &= lower.isResolvedInFullDepth();
	}

	return resolvedInFullDepth;
    }


    @SuppressWarnings({"unchecked"})
    public NameUsage<N, T> getNameUsage(Object object) {
	if(isAssignableFrom(object))
	    return (NameUsage<N, T>) getClass().cast(object);
	else
	    return null;
    }

    @SuppressWarnings({"unchecked"})
    public AbstractNameUsage<N, T> getAbstractNameUsage(Object object) {
	if(isAssignableFrom(object))
	    return getClass().cast(object);
	else
	    return null;
    }

    public Integer getDescendantCount()
    {
	return null;
    }
}
