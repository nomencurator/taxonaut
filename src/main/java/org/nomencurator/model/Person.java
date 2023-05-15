/*
 * Person.java:  a Java implementation of Person class
 * for Nomencurator data strucutre
 *
 * Copyright (c) 1999, 2002, 2003, 2004, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069, JP17300071
 */

package org.nomencurator.model;

import java.io.Serializable;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.nomencurator.io.sql.NamedObjectConnection;

import org.nomencurator.util.ArrayUtility;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An implementation of {@code Person} data structure of Nomencurator data model
 *
 * @see 	org.nomencurator.model.NamedObject
 * @see 	org.nomencurator.model.Affiliation
 * @see 	org.nomencurator.model.Publication
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 *
 * @version 	07 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class Person
    extends Agent
    implements Serializable, Cloneable
{
private static final long serialVersionUID = -8873884348900762590L;

    public static final boolean ABBREVIATE = true;

    /** {@code Vector} representing affiliation list of the {@code Person} */
    //protected Vector affiliations;
    protected Affiliation[] affiliations;

    /** Title part of author's name, e.g. Sir or Load */
    protected String title;

    /** Person's first name */
    protected String firstName;

    /** Person's middle name */
    protected String middleNames;

    /** prefix of author's surname, e.g. van, von or de */
    protected String surnamePrefix;

    /** Person's surname */
    protected String surname;

    /** Epithet part of author's name, e.g. Jr. or Sen. */
    protected String epithet;

    /** Year of author's first publication */
    protected int firstPublication;

    /** Year of author's last publication */
    protected int lastPublication;

    public boolean isTeam()
    {
	return (members != null);
    }


    /**
     * Constructs an empty {@code Person} object
     */
    public Person()
    {
	super();
	author();
    }
    
    /**
     * Constructs an {@code Auahot} having
     * {@code author} as its name, i.e. perisitent ID
     *
     * @param author {@code String} representing its name,
     * i.e. perisitent ID
     */
    public Person(String author)
    {
	super(author);
    }
    
    /**
     * Constructs a copy of given {@code name}
     *
     * @param name {@code Name} representation of an {@code Person}
     */
    public Person(Name<?> name)
    {
	//don't use NamedObject(String) constructor
	this();
	
	if(name instanceof Person){
	    author((Person)name);
	}
	else
	    setPersistentID(name.getLiteral());
    }
    
    /**
     * Constructs a non-trivial {@code Person} object.
     *
     * @param title {@code String} represents title of author's name
     * @param firstName {@code String} represents author's first name
     * @param middleNames {@code String} represents author's middle name
     * @param surname {@code String} represents author's surname
     * @param epithet {@code String} represents epithet of author's name
     * @param birthDate {@code Date} of birth
     * @param deathDate {@code Date} of death
     * @param firstPublication year of (known) first publication in {@code int}
     * @param lastPublication year of (known) last publication in {@code int}
     * @param afiiliations affiliation list as a {@code Vector} 
     * @param publications publication list as a {@code Vector} 
     */
    public Person(String title, String firstName, String middleNames,
		  String surnamePrefix, String  surname, String epithet,
		  Calendar birthDate, Calendar deathDate,
		  int firstPublication, int lastPublication,
		  Collection<? extends Affiliation> affiliations, Collection<? extends Publication> publications)
    {
	this();
	author(title, firstName, middleNames, surnamePrefix, surname, epithet,
	       birthDate, deathDate, firstPublication, lastPublication,
	       affiliations, publications);
    }
    
    /**
     * Constructs copy of {@code author}
     *
     * @param author {@code Person}
     */
    public Person(Person author)
    {
	super();
	author(author);
    }

    /**
     * Constructs an {@code Person} object using XML data
     * given by {@code xml}
     *
     * @param xml {@code Element} specifying {@code Person}
     *
     */
    public Person(Element xml)
    {
	super();
	NodeList childNodes = xml.getChildNodes();
	int subChildCount = childNodes.getLength();

	boolean toBeResolved = false;

	String persistentID = null;
	for (int j = 0; j < subChildCount; j++) {
	    Node child = childNodes.item(j);
	    if(child.getNodeType () == Node.ELEMENT_NODE) {
		Element element = (Element)child;
		String tagName = element.getTagName();

		if (tagName.equals ("oid"))
		    persistentID = getString(element);
		else if(tagName.equals ("surname"))
		    surname = getString(element);
		else if(tagName.equals ("firstName"))
		    firstName = getString(element);
		else if(tagName.equals ("middleNames"))
		    middleNames =getString(element);
		else if(tagName.equals ("surnamePrefix"))
		    surnamePrefix = getString(element);
		else if(tagName.equals ("title"))
		    title = getString(element);
		else if(tagName.equals ("epithet"))
		    epithet = getString(element);
		else if(tagName.equals ("birthDate")) {
		    beginningDate = getCalendar(getString(element));
		}
		else if(tagName.equals ("deathDate")) {
		    endingDate = getCalendar(getString(element));
		}
		else if(tagName.equals ("publication")) {
		    String pID = getString(element);
		    Publication p =
			(Publication)curator.get(pID);
		    if(p != null) {
			ArrayUtility.add(p, publications);
			toBeResolved = true;
		    }
		    else {
			p = new Publication();
			p.setLiteral(pID);
			curator.put(p);
			addPublication(p);
		    }
		}
		/*
		else if(tagName.equals ("affiliation")) {
		    String pID = getString(element);
		    Affiliation a =
			(Affiliation)curator.get(pID);
		    if(a == null) {
			a = new Affiliation();
			a.setLiteral(pID);
			curator.put(a);
			addAffiliation(a);
		    }
		    addAffiliation(a);
		}
		*/
		else{}
            }
	    
	}

	if(persistentID != null &&
	   !persistentID.equals(getLiteral()))
	    setLiteral(persistentID); //i.e. other key data are empty

	if(toBeResolved)
	    curator.resolve(this);

    }

    /*
     * Sets empty values to attributes.
     */
    private void author()
    {
	title      = ANONYMOUS;
	firstName  = ANONYMOUS;
	middleNames = ANONYMOUS;
	surname    = ANONYMOUS;
	epithet    = ANONYMOUS;
	beginningDate      = null;
	endingDate      = null;
	firstPublication  = 0;
	lastPublication   = 0;
	//affiliations = new Vector();
	//publications = new Vector();
    }
    
    /*
     * Sets attribute values as in {@code author}
     *
     * @param author {@code Person} of which values to be set
     */
    private void author(Person author)
    {
	if(author == null)
	    return;

	author(author.title, author.firstName, author.middleNames,
	       author.surnamePrefix, author.surname, author.epithet, 
	       author.beginningDate, author.endingDate, author.firstPublication, author.lastPublication,
	       author.affiliations, author.publications);
    }
    
    /**
     * Sets given paratemeters to attributes
     *
     * @param title {@code String} represents title of author's name
     * @param firstName {@code String} represents author's first name
     * @param middleNames {@code String} represents author's middle name
     * @param surname {@code String} represents author's surname
     * @param epithet {@code String} represents epithet of author's name
     * @param birthDate {@code Date} of birth
     * @param deathDate {@code Date} of death
     * @param firstPublication year of (known) first publication in {@code int}
     * @param lastPublication year of (known) last publication in {@code int}
     * @param afiiliations affiliation list as a {@code Vector} 
     * @param publications publication list as a {@code Vector} 
     */
    private void author(String title, String firstName, String middleNames,
			String surnamePrefix, String  surname, String epithet,
			Calendar birthDate, Calendar deathDate,
			int firstPublication, int lastPublication,
			Collection<? extends Affiliation> affiliations, Collection<? extends Publication> publications)
    {
	this.title      = title;
	this.firstName  = firstName;
	this.middleNames = middleNames;
	this.surnamePrefix  = surnamePrefix;
	this.surname    = surname;
	this.epithet    = epithet;
	this.beginningDate      = birthDate;
	this.endingDate      = deathDate;
	this.firstPublication = firstPublication;
	this.lastPublication  = lastPublication;

	if(affiliations != null) {
	    int size = affiliations.size();
	    if(size > 0) {
		this.affiliations = affiliations.toArray(new Affiliation[size]);
	    }
	}

	if(publications != null) {
	    int size = publications.size();
	    if(size > 0) {
		this.publications = publications.toArray(new Publication[size]);
	    }
	}

	//this.publications = publications;
    }

    /*
     * Sets given paratemeters to attributes
     *
     * @param title {@code String} represents title of author's name
     * @param firstName {@code String} represents author's first name
     * @param middleNames {@code String} represents author's middle name
     * @param surname {@code String} represents author's surname
     * @param epithet {@code String} represents epithet of author's name
     * @param birthDate {@code Date} of birth
     * @param deathDate {@code Date} of death
     * @param firstPublication year of (known) first publication in {@code int}
     * @param lastPublication year of (known) last publication in {@code int}
     * @param afiiliations affiliation list as a {@code Vector} 
     * @param publications publication list as a {@code Vector} 
     */
    private void author(String title, String firstName, String middleNames,
			String surnamePrefix, String  surname, String epithet,
			Calendar birthDate, Calendar deathDate,
			int firstPublication, int lastPublication,
			Affiliation[] affiliations, Publication[] publications)
    {
	this.title      = title;
	this.firstName  = firstName;
	this.middleNames = middleNames;
	this.surnamePrefix  = surnamePrefix;
	this.surname    = surname;
	this.epithet    = epithet;
	this.beginningDate      = birthDate;
	this.endingDate      = deathDate;
	this.firstPublication = firstPublication;
	this.lastPublication  = lastPublication;

	if(affiliations == null) {
	    this.affiliations = affiliations;
	}
	else {
	    int size = affiliations.length;
	    if(size > 0) {
		this.affiliations = new Affiliation[size];
		System.arraycopy(affiliations, 0, this.affiliations, 0, size);
	    }
	}

	if(publications == null) {
	    this.publications = null;
	}
	else {
	    int size = publications.length;
	    if(size > 0) {
		this.publications = new Publication[size];
		System.arraycopy(publications, 0, this.publications, 0, size);
	    }
	}

    }

    /**
     * Returns a persistent ID representing this {@code Person}
     * with specified  {@code separator}.  It contains class name header
     * if {@code withClassName} true.
     * The subclasses must provide this method.
     *
     * @param separator {@code String} to be used as the field separator
     * @param withClassName {@code boolean} specifying with or without
     * class name header
     *
     * @return String representing this {@code Person}
     */
    public String getPersistentID(String separator, boolean withClassName)
    {
	if(entity != null)
	    return ((Person)getEntity()).getPersistentID(separator, withClassName);

	StringBuffer pid = null;
	if(withClassName)
	    pid = getClassNameHeaderBuffer();
	else
	    pid = new StringBuffer();

	String str = getFullname();
	if(str != null)
	    pid.append(str);
	//chenge it as locale independent!
	//pid.append(separator).append(birthDate);
	//pid.append(separator).append(deathDate);
	pid.append(separator);
	if(beginningDate != null) {
	    appendCalendar(pid, beginningDate, ".");
	}
	pid.append(separator);
	if(endingDate != null) {
	    appendCalendar(pid, endingDate, ".");
	}
	return pid.toString();
    }
    
    /**
     * Returnes number of fields separators in persistent ID
     *
     * @return int representing number of fields separators in persistent ID
     */ 
    public int getFieldSepartorsCount()
    {
	return 2;
    }

    /**
     * Merges {@code namedObject} with this {@code Person}
     * if possible, but not yet implemented.
     * It returns true if merged.
     *
     * @param namedObject a {@code NamedObject} to be merged
     *
     * @return true if merged, or false if not mergiable
     */
    public boolean merge(NamedObject<?> nr)
    {
	if(!getClassNameHeader().equals(nr.getClassNameHeader()))
	    return false;
	return false; //not yet implemented
    }

    /**
     * Returns full name of author in {@code String}
     *
     * @return full name of author in {@code String}
     */
    public String getFullname()
    {
	if(entity != null)
	    return ((Person)getEntity()).getFullname();

	return getFullname(" ");
    }
    
    /**
     * Returns full name of author in {@code String}
     *
     * @return full name of author in {@code String}
     */
    public String getFullname(boolean abbreviate)
    {
	if(entity != null)
	    return ((Person)getEntity()).getFullname(abbreviate);

	return getFullname(" ", abbreviate);
    }
    
    /**
     * Returns full name of author in {@code String}.
     * Each name components is separated by {@code nameSeparator}
     *
     * @param nameSeparator {@code String} to be used to separate name components
     *
     * @return full name of author in {@code String}
     */
    public String getFullname(String nameSeparator)
    {
	if(entity != null)
	    return ((Person)getEntity()).getFullname(nameSeparator);

	return getFullname(nameSeparator, false);
    }


    /**
     * Returns full name of author in {@code String}.
     * Each name components is separated by {@code nameSeparator}
     *
     * @param nameSeparator {@code String} to be used to separate name components
     *
     * @return full name of author in {@code String}
     */
    public String getFullname(String nameSeparator, boolean abbreviate)
    {
	if(entity != null)
	    return ((Person)getEntity()).getFullname(nameSeparator, abbreviate);

	StringBuffer s = new StringBuffer();

	if(isNominal()) {
	    StringTokenizer tokens = 
		new StringTokenizer(getPersistentIDComponent(0), ",");
	    if(tokens.countTokens() < 1)
		return null;

	    s.append(tokens.nextToken());
	    if(!tokens.hasMoreTokens())
		return s.toString();

	    if(s.length() > 0)
		s.append(",").append(nameSeparator);
	    tokens = new StringTokenizer(tokens.nextToken());
	    int i = 0;
	    if(tokens.countTokens() > 0) {
		while(tokens.hasMoreTokens()) {
		    if(i > 0)
			s.append(nameSeparator);
		    if(abbreviate) {
			s.append(tokens.nextToken().charAt(0));
			s.append('.');
		    }
		    else
			s.append(tokens.nextToken());
		    i++;
		}
	    }
	    return s.toString();
	}

	if(surnamePrefix != null && surnamePrefix.length() > 0) {
	    s.append(nameSeparator).append(surnamePrefix);
	    if(surname != null && surname.length() > 0)
		s.append(nameSeparator);
	}

	if(surname != null)
	    s.append(surname);

	if(epithet != null && epithet.length() > 0)
	    s.append(nameSeparator).append(epithet);

	if(firstName != null && firstName.length() > 0) {
	    s.append(",").append(nameSeparator);
	    if(abbreviate)
		s.append(firstName.charAt(0)).append(".");
	    else
		s.append(firstName);
	}

	if(middleNames != null && middleNames.length() > 0) {
	    s.append(",").append(nameSeparator);
	    StringTokenizer tokens = new StringTokenizer(middleNames);
	    int i = 0;
	    while (tokens.hasMoreTokens()) {
		String m = tokens.nextToken();
		if(m.length() > 0) {
		    if(i > 0)
			s.append(nameSeparator);
		    if(abbreviate) {
			s.append(m.charAt(0));
			s.append('.');
		    }
		    else
			s.append(m);
		    i++;
		}
	    }
	}

	if(title != null && title.length() > 0)
	    s.append(",").append(nameSeparator).append(title);

	return s.toString();
    }
    
    /**
     * Parses a {@code line} and sets values of this object accordint to it
     *
     * @param line {@code String} containing fragment of data to be set
     */
    public void parseLine(String line){
    }

    /**
     * Sets {@code title} as title of author's name
     *
     * @param title {@code String} containing title of author's name to be set
     */
    public void setTitle(String title)
    {
	if(entity != null)
	    ((Person)getEntity()).setTitle(title);

	if(this.title == title ||
	   (title != null && title.equals(this.title)))
	    return;

	this.title = title;
    }

    /**
     * Returns title of author's name
     *
     * @return {@code String} representing title of author's name
     */
    public String getTitle()
    {
	if(entity != null)
	    return ((Person)getEntity()).title;

	return title;
    }

    /**
     * Sets {@code firstName} as author's first name
     *
     * @param firstName {@code String} containing author's first name to be set
     */
    public void setFirstName(String firstName)
    {
	if(entity != null)
	    ((Person)getEntity()).setFirstName(firstName);

	if(this.firstName == firstName ||
	   (firstName != null && firstName.equals(this.firstName)))
	    return;

	this.firstName = firstName;
    }

    /**
     * Returns author's first name
     *
     * @return {@code String} representing author's first name
     */
    public String getFirstName()
    {
	if(entity != null)
	    return ((Person)getEntity()).firstName;

	if(!isNominal())
	    return firstName;

	StringTokenizer tokens = 
	    new StringTokenizer(getPersistentIDComponent(0), ",");
	if(tokens.countTokens() < 2)
	    return null;

	tokens.nextToken(); // discard surname
	tokens =  new StringTokenizer(tokens.nextToken());

	if(tokens.countTokens() < 1)
	    return null;
	return tokens.nextToken();
    }

    /**
     * Sets {@code middleNames} as author's middle name
     *
     * @param middleNames {@code String} containing author's middle name to be set
     */
    public void setMiddleNames(String middleNames)
    {
	if(entity != null)
	    ((Person)getEntity()).setMiddleNames(middleNames);

	if(this.middleNames == middleNames ||
	   (middleNames != null && middleNames.equals(this.middleNames)))
	    return;

	this.middleNames = middleNames;
    }

    /**
     * Returns author's middle name
     *
     * @return {@code String} representing author's middle name
     */
    public String getMiddleNames()
    {
	if(entity != null)
	    return ((Person)getEntity()).getMiddleNames();

	if(!isNominal())
	    return middleNames;

	StringTokenizer tokens = 
	    new StringTokenizer(getPersistentIDComponent(0), ",");
	if(tokens.countTokens() < 2)
	    return null;

	tokens.nextToken(); // discard surname
	tokens =  new StringTokenizer(tokens.nextToken());

	if(tokens.countTokens() < 2)
	    return null;
	tokens.nextToken(); // discard first name
	return tokens.nextToken();
    }

    /**
     * Sets {@code surname} as author's surname
     *
     * @param surname {@code String} containing author's surname to be set
     */
    public void setSurname(String surname)
    {
	if(entity != null)
	    ((Person)getEntity()).setSurname(surname);

	if(this.surname == surname ||
	   (surname != null && surname.equals(this.surname)))
	    return;

	this.surname = surname;
    }

    /**
     * Returns author's surname
     *
     * @return {@code String} representing author's surname
     */
    public String getSurname()
    {
	if(entity != null)
	    return ((Person)getEntity()).surname;

	if(!isNominal())
	    return surname;

	StringTokenizer tokens = 
	    new StringTokenizer(getPersistentIDComponent(0), ",");
	if(tokens.countTokens() < 1)
	    return null;

	return tokens.nextToken();
    }

    /**
     * Returns author's surname
     *
     * @return {@code String} representing author's surname
     */
    public String getFullSurname()
    {
	if(entity != null)
	    return ((Person)getEntity()).getFullSurname();

	StringBuffer buffer = new StringBuffer();
	boolean appended = false;
	if(surnamePrefix != null &&
	   surnamePrefix.length() > 0) {
	    buffer.append(surnamePrefix);
	    buffer.append(' ');
	    appended = true;
	}
	if(surname != null &&
	   surname.length() > 0) {
	    buffer.append(surname);
	    appended = true;
	}
	if(epithet != null &&
	   epithet.length() > 0) {
	    if(appended)
		buffer.append(' ');
	    buffer.append(epithet);
	    appended = true;
	}

	return buffer.toString();
    }

    
    /**
     * Sets {@code epithet} of author's surname
     *
     * @param epithet {@code String} containing epithet of author's surname to be set
     */
    public void setEpithet(String epithet)
    {
	if(entity != null)
	    ((Person)getEntity()).setEpithet(epithet);

	if(this.epithet == epithet ||
	   (epithet != null && epithet.equals(this.epithet)))
	    return;

	this.epithet = epithet;
    }

    /**
     * Returns epithet of author's name
     *
     * @return {@code String} representing epithet of author's name
     */
    public String getEpithet()
    {
	if(entity != null)
	    return ((Person)getEntity()).epithet;

	return epithet;
    }

    /**
     * Sets {@code prefix} of author's surname
     *
     * @param prefix {@code String} containing prefix of author's surname to be set
     */
    public void setSurnamePrefix(String prefix)
    {
	if(entity != null)
	    ((Person)getEntity()).setSurnamePrefix(prefix);

	if(surnamePrefix == prefix ||
	   (prefix != null && prefix.equals(surnamePrefix)))
	    return;

	surnamePrefix = prefix;
    }

    /**
     * Returns prefix of author's name
     *
     * @return {@code String} representing prefix of author's name
     */
    public String getSurnamePrefix()
    {
	if(entity != null)
	    return ((Person)getEntity()).getSurnamePrefix();

	return surnamePrefix;
    }

    /**
     * Returns full name of author in {@code String} with abbreviated 
     * first and middle names.
     * Each name components is separated by a space
     *
     * @return full name of author with abbreviated first and middle names in {@code String}
     */
    public String getInitializedName()
    {
	if(entity != null)
	    return ((Person)getEntity()).getInitializedName();

	return getInitializedName(" ");
    }

    /**
     * Returns full name of author in {@code String} with abbreviated 
     * first and middle names.
     * Each name components is separated by {@code nameSeparator}
     *
     * @param nameSeparator {@code String} to be used to separate name components
     *
     * @return full name of author with abbreviated first and middle names in {@code String}
     */
    public String getInitializedName(String nameSeparator)
    {
	if(entity != null)
	    return ((Person)getEntity()).getInitializedName(nameSeparator);

	StringBuffer name = new StringBuffer();
	if(surnamePrefix != null && surnamePrefix.length() > 0) {
	    name.append(nameSeparator).append(surnamePrefix);
	    if(surname != null && surname.length() > 0)
		name.append(nameSeparator);
	}

	if(surname != null)
	    name.append(surname);

	if(epithet != null && epithet.length() > 0)
	    name.append(nameSeparator).append(epithet);

	if((firstName != null && firstName.length() > 0) ||
	   (middleNames != null && middleNames.length() > 0)) {
	    name.append(',').append(nameSeparator);
	    if(firstName != null && firstName.length() > 0)
		name.append(initialize(firstName, nameSeparator));
	    
	    if(middleNames != null && middleNames.length() > 0)
		name.append(nameSeparator).append(initialize(middleNames, nameSeparator));
	}
	
	return name.toString();
    }

    /**
     * Returns abbreviated expression of {@code name} separated
     * by {@code nameSeparator} if {@code name} consists of
     * multiple parts.
     *
     * @param name {@code String} representing name to be abbreviated
     * @param nameSeparator {@code String} to be used to separate name components
     *
     * @return abbreviated expression of {@code name}
     */ 
    public static String initialize(String name, String nameSeparator)
    {
	StringTokenizer st = new StringTokenizer(name);
	StringBuffer initials = new StringBuffer();
	int tokens = 0;
	while (st.hasMoreTokens()) {
	    if(tokens != 0)
		initials.append(nameSeparator);
	    initials.append(st.nextToken().charAt(0));
	    initials.append(".");
	}
	return initials.toString();
    }

    /**
     * Returns {@code Enumeration} of author's <COCE>Publication}s
     * or null if none
     *
     * @return Enumeration of author's {@code Publication}s
     * or null if none
     */
    public Publication[] getPublications()
    {
	if(entity != null)
	    return ((Person)getEntity()).getPublications();

	return ArrayUtility.copy(publications);
    }

    /**
     * Sets {@code publications} as {@code Vector} representing author's publication list
     *
     * @param publications {@code Vector} representing author's publication list
     */
    public void setPublications(Collection<? extends Publication> publications)
    {
	if(entity != null)
	    ((Person)getEntity()).setPublications(publications);

	if(this.publications != null) {
	    for(int i = 0; i < this.publications.length; i++) {
		this.publications[i].removeAuthor(this);
		this.publications[i] = null;
	    }
	}

	if(publications == null)
	    return;

	int size = publications.size();
	this.publications = publications.toArray(new Publication[size]);
    }

    /**
     * Adds {@code publication} to author's publication list
     * with returning true if it is added successfully, or false if not.
     *
     * @param publication {@code Publication} to be added to author's publication list
     *
     * @return true if {@code publication} was added to the publication list successfully, or false if not.
     */
    public boolean addPublication(Publication publication)
    {
	if(entity != null)
	    return ((Person)getEntity()).addPublication(publication);
	
	Publication[] added = ArrayUtility.add(publication, publications);
	if(added != publications) {
	    if(publications != null)
		ArrayUtility.clear(publications);
	    publications = added;
	    return true;
	}
	return false;
    }

    /**
     * Removes {@code publication} from author's publication list.
     *
     * @param publication {@code Publication} to be removed from author's publication list
     */
    public void removePublication(Publication publication)
    {
	if(entity != null)
	    ((Person)getEntity()).removePublication(publication);
	else {
	    ArrayUtility.remove(publication, publications);
	    publication.removeAuthor(this);
	}
    }

    /**
     * Clears author's publication list.
     */
    public void clearPublication()
    {
	if(entity != null)
	    ((Person)getEntity()).clearPublication();
	else
	    ArrayUtility.clear(publications);
    }

    /**
     * Returns {@code Enumeration} of author's {@code Affiliation}s,
     * or null if none
     *
     * @return {@code Enumeration} of author's {@code Affiliation}s
     * or null if none
     */
    public Affiliation[] getAffiliations()
    {
	if(entity != null)
	    return ((Person)getEntity()).getAffiliations();

	Affiliation[] toReturn = null;
	synchronized (affiliations) {
	    if(affiliations != null) {
		toReturn = new Affiliation[affiliations.length];
		System.arraycopy(affiliations, 0, toReturn, 0, affiliations.length);
	    }
	}
	return toReturn;
    }

    /**
     * Sets {@code affiliations} as {@code Vector} representing author's affiliation list
     *
     * @param affiliations {@code Collection} representing author's affiliation list
     */
    public void setAffiliations(Collection<Affiliation> affiliations)
    {
	if(entity != null) {
	    ((Person)getEntity()).setAffiliations(affiliations);
	    return;
	}
	if(this.affiliations != null)
	    ArrayUtility.clear(this.affiliations);

	if(affiliations == null)
	    return;

	int size = affiliations.size();
	if(size == 0)
	    return;

	this.affiliations = affiliations.toArray(new Affiliation[size]);
    }

    /**
     * Adds {@code affiliation} to author's affiliation list
     * with returning true if it is added successfully, or false if not.
     *
     * @param affiliation {@code Affiliation} to be added to author's affiliation list
     *
     * @return true if {@code affiliation} was added to the affiliation list successfully, or false if not.
     */
    public boolean addAffiliation(Affiliation affiliation)
    {
	if(entity != null)
	    return ((Person)getEntity()).addAffiliation(affiliation);

	Affiliation[] added = ArrayUtility.add(affiliation, affiliations);
	if(added != affiliations) {
	    if(affiliations != null)
		ArrayUtility.clear(affiliations);
	    affiliations = added;
	    return true;
	}
	return false;
    }

    /**
     * Removes {@code affiliation} from author's affiliation list.
     *
     * @param affiliation {@code Affiliation} to be removed from author's affiliation list
     */
    public void removeAffiliation(Affiliation affiliation)
    {
	if(entity != null)
	    ((Person)getEntity()).removeAffiliation(affiliation);
	else
	    ArrayUtility.remove(affiliation, affiliations);
    }

    /**
     * Clears author's affiliation list.
     *
     */
    public void clearAffiliations()
    {
	if(entity != null)
	    ((Person)getEntity()).clearAffiliations();
	else
	    ArrayUtility.clear(affiliations);
    }

    public Object clone()
    {
	/*
	Person author = new Person(this);
	author.affiliations = (Vector)affiliations.clone();
	author.publications = (Vector)publications.clone();
	return author;
	*/
	return new Person(this);
    }

    /**
     * Returns XML {@code String} representing this objet 
     *
     * @return XML {@code String} representing this objet
     */
    public String toXML()
    {
	if(entity != null)
	    return ((Person)getEntity()).toXML();

	StringBuffer buf = new StringBuffer();
	
	buf.append("<Person>\n");
	buf.append("<oid>").append(getPersistentID()).append("</oid>\n");
	buf.append("<surname>").append(getSurname()).append("</surname>\n");
	buf.append("<firstName>").append(getFirstName()).append("</firstName>\n");
	buf.append("<middleNames>").append(getMiddleNames()).append("</middleNames>\n");
	buf.append("<title>").append(getTitle()).append("</title>\n");
	buf.append("<epithet>").append(getEpithet()).append("</epithet>\n");
	buf.append("<surrnamePrefix>").append(getSurnamePrefix()).append("</surnamePrefix>\n");
	if(getBeginningDate() != null) {
    	    buf.append("<birthDate>").append(getBeginningDate().toString()).append("</birthDate>\n");
    	}
    	if(getEndingDate() != null) {
    	    buf.append("<deathDate>").append(getEndingDate().toString()).append("</deathDate>\n");
    	}
	if(publications != null) {
	    for(int i = 0; i < publications.length; i++) {
    	        buf.append("<publication>").append(publications[i].getPersistentID()).append("</publication>\n");
	    }
	}
	/*
	if(affiliations != null) {
	    for(int i = 0; i < affiliations.length; i++) {
    	        buf.append("<affiliation>").append(affiliations[i].getPersistentID()).append("</affiliation>\n");
	    }
	}
	*/
	
	// buf.append("<previous>").append(getPrevious().getPersistentID()).append("</previous>");
	// buf.append("<next>").append(getNext().getPersistentID()).append("</next>");
	buf.append("</Person>\n");
	
        return buf.toString();
    }	
	
    
    /**
     * create XML String of the all Related NamedObject
     *
     * @return XML String of this {@code Person} object
     */
    public String toRelevantXML()
    {
	if(entity != null)
	    return ((Person)getEntity()).toRelevantXML();

	// create XML String of the Person itself
	StringBuffer buf = new StringBuffer();
	buf.append(toXML());
	
	// create XML of the Publications
	if(publications != null) {
	    for(int i = 0; i < publications.length; i++) {
		buf.append(publications[i].toXML());
	    }
    	}
	// create XML of the Affiliation
	if(affiliations != null) {
	    for(Affiliation affiliation: affiliations) {
		affiliation.toXML(buf);
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
     * Returns author's name in format appropriate to use as a part of
     * view name
     *
     * @return String representing author's name
     */
    public String getPersonViewName()
    {
	if(entity != null)
	    return ((Person)getEntity()).getPersonViewName();

	StringBuffer buffer = new StringBuffer();
	getPersonViewName(buffer);
	return buffer.toString();
    }

    /**
     * Returns author's name in format appropriate to use as a part of
     * view name
     *
     * @param buffer {@code StringBuffer} to append author's name
     *
     * @return StringBufer containing author's name
     */
    public boolean getPersonViewName(StringBuffer buffer)
    {
	if(entity != null)
	    return ((Person)getEntity()).getPersonViewName(buffer);

	boolean appended = false;
	if(surnamePrefix != null && surnamePrefix.length() != 0) {
	    buffer.append(surnamePrefix);
	    buffer.append(' ');
	    appended = true;
	}

	if(surname != null && surname.length() != 0) {
	    buffer.append(surname);
	    appended = true;
	}

	if(epithet != null && epithet.length() != 0) {
	    if(appended)
		buffer.append(' ');
	    buffer.append(epithet);
	    appended = true;
	}

	return appended;
    }

    /**
     * Sets a {@code object} as the entity of the name
     *
     * @param object representing the entity
     */
    //public void setEntity(Object object)
    public void setEntity(Person object)
    {
	if(object == this ||
	   (object != null && !(object instanceof Person)))
	    throw new IllegalArgumentException(object.getClass().getName() + " can't be an entity of " + getClass().getName());
	// entity = (Person)object;
	super.setEntity(object);
    }

    /**
     * Returns a summarized expression of this {@code NamedObject}
     *
     * @return String representing summary of this {@code NamedObject}
     */
    public String getSummary()
    {
	if(entity != null)
	    return ((Person)getEntity()).getSummary();

	return getFullname(true);
    }
}
