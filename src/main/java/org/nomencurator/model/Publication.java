/*
 * Publication.java:  a Java implementation of Publication class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 1999, 2002, 2003, 2004, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
 * All rights reserved.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.nomencurator.io.sql.NamedObjectConnection;

import org.nomencurator.util.ArrayUtility;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An implementation of Publication data structure of Nomencurator data model
 *
 * @see 	org.nomencurator.model.NamedObject
 * @see 	org.nomencurator.model.Author
 * @see 	org.nomencurator.model.Appearance
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 *
 * @version 	09 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class Publication
    extends AbstractNamedObject<Publication>
    implements Serializable
{
    private static final long serialVersionUID = -7690519396181073630L;

    /** Constant indicating ISBN */
    public  static final int ISBN = 0;

    /** Constant indicating ISSN */
    public  static final int ISSN = ISBN + 1;

    /** Constant indicating neithr ISBN nor ISSN*/
    public  static final int NEITHER = ISBN - 1;

    /** Index of author(s) in persistentID */
    public static final int AUTHOR = 0;
    /** Index of year in persistentID*/
    public static final int YEAR = AUTHOR + 1;
    /** Index of title or ISXN in persistentID*/
    public static final int TITLE = YEAR + 1;
    public static final int DOI   = TITLE;
    /** Index of volume in persistentID*/
    public static final int VOLUME = TITLE + 1;
    /** Index of issue number in persistentID*/
    public static final int ISSUE = VOLUME + 1;
    /** Index of first page in persistentID*/
    public static final int FIRST_PAGE = ISSUE + 1;
    /** Index of last page in persistentID*/
    public static final int LAST_PAGE = FIRST_PAGE + 1;
    /** number of items in the persistentID*/
    public static final int PID_ITEMS = LAST_PAGE;


    /**
     * Default contents of {@code pulicationTypeModel}
     */
    public static String[]  publicationTypes = {
	"", //0
	"Journal Article", //1
	"Book", //2
	"Book Section", //3
	"Manuscript", //4
	"Edited Book", //5
	"Magazine Article", //6
	"Newspaper Article", //7
	"Conference Proceedings", //8
	"Thesis", //9
	"Report", //10
	"Personal Communication", //11
	"Book Series", //12
	"Journal",  //13
	"Magazine",  //14
	"Computer Program", //15
	"Electronic Source", //16
	"Audiovisual Material", //17
	"Film or Broadcast", //18
	"Artwork", //19
	"Patent", //20
	"Map", //21
	"Hearing", //22
	"Bill", //23
	"Statute", //24
	"Case", //25
	"Generic", //26
	"other" //27
    };

    public static final int UNSPECIFIED = 0;
    public static final int JOURNAL_ARTICLE = UNSPECIFIED + 1;
    public static final int BOOK = JOURNAL_ARTICLE + 1;
    public static final int BOOK_SECTION = BOOK + 1;
    public static final int MANUSCRIPT = BOOK_SECTION + 1;
    public static final int EDITED_BOOK = MANUSCRIPT + 1;
    public static final int MAGAZINE_ARTICLE = EDITED_BOOK + 1;
    public static final int NEWSPAPER_ARTICLE = MAGAZINE_ARTICLE + 1;
    public static final int CONFERENCE_PROCEEDINGS = NEWSPAPER_ARTICLE + 1;
    public static final int THESIS = CONFERENCE_PROCEEDINGS + 1;
    public static final int REPORT = THESIS + 1;
    public static final int PERSONAL_COMMUNICATION = REPORT + 1;
    public static final int BOOK_SERIES = PERSONAL_COMMUNICATION + 1;
    public static final int JOURNAL = BOOK_SERIES + 1;
    public static final int MAGAZINE = JOURNAL + 1;
    public static final int COMPUTER_PROGRAM = MAGAZINE + 1;
    public static final int ELECTRONIC_SOURCE = COMPUTER_PROGRAM + 1;
    public static final int AUDIOVISUAL_MATERIAL = ELECTRONIC_SOURCE + 1;
    public static final int FILM_OR_BROADCAST = AUDIOVISUAL_MATERIAL + 1;
    public static final int ARTWORK = FILM_OR_BROADCAST + 1;
    public static final int PATENT = ARTWORK + 1;
    public static final int MAP = PATENT + 1;
    public static final int HEARING = MAP + 1;
    public static final int BILL = HEARING + 1;
    public static final int STATUTE = BILL + 1;
    public static final int CASE = STATUTE + 1;
    public static final int GENERIC = 0xFFFF;

    /** Type of publication */
    protected int publicationType;

    /** {@code Vector} representing the list of {@code Author}s */
    //protected Vector<Author> authors;
    protected Author[] authors;

    /**
     * {@code Vector} representing the list of {@code Appearance}s 
     * appeared in the publication
     */
    //protected Vector<Appearance> appearances;
    protected Appearance[] appearances;

    /** {@code String} representing author list as apperared in the publiation */
    protected String authorNames;

    /** {@code String} representing affiliation list as apperared in the publiation */
    protected String affiliations;
    
    /** {@code String} representing citation title, e.g. journal name */
    protected String citationTitle;

    /** {@code String} representing DOI of the publication */
    protected String doi;

    /** {@code String} representing contents title, e.g. title of an article */
    protected String contentsTitle;

    /** {@code String} representing ISBN (in case of book) or ISSN (in case of periodicals) */
    protected String isxn;

    /** {@code Vector} to hold multiple ISBN (there are!) */
    //protected Vector isbn;
    protected String[] isbn;

    /** {@code Vector} to hold multiple ISSN (is there?) */
    //protected Vector issn;
    protected String[] issn;

    /** {@code String} representing publication year */
    protected String year;

    /** {@code String} representing volume of the publication for articles, periodicals etc. */
    protected String volume;

    /** {@code String} representing number of the publication for articles, periodicals etc. */
    protected String issue;

    /** {@code String} representing first page the publication */
    protected String firstPage;

    /** {@code String} representing last page the publication */
    protected String lastPage;

    /** {@code String} representeing when the manuscript was received */
    protected String   received;

    /** {@code String} representing when the revised manuscript was received */
    protected String   revised;

    /** {@code String} representing when the manuscript was acceppted for publication */
    protected String   accepted;

    /** {@code String} representing when the publication was published */
    protected String   published;

    /** {@code String} representing publisher of the publication */
    protected String publisher;

    /** {@code String} representing place where the publication was published */
    protected String place;

    /** {@code Publication} containging this publication, e.g. edited book containing a chapter */
    protected Publication container;

    /** {@code Vector} represents a list of {@code Publication}s contained in this {@code Publication},
     * e.g. chapters in a edited book */
    //protected Vector<Publication> contents;
    protected Publication[] contents;

    /** {@code Vector} represents a list of {@code Publication}s cited in this {@code Publication} */
    //protected Vector<Publication> references;
    protected Publication[] references;

    private static final int SQL_PID = 1;
    private static final int SQL_AUTHORNAMES = SQL_PID + 1;
    private static final int SQL_AFFILIATIONS = SQL_AUTHORNAMES + 1;
    private static final int SQL_CITATIONTITLE = SQL_AFFILIATIONS + 1;
    private static final int SQL_ISXN = SQL_CITATIONTITLE + 1;
    private static final int SQL_YEAR = SQL_ISXN + 1; //int
    private static final int SQL_VOLUME = SQL_YEAR + 1;
    private static final int SQL_ISSUE = SQL_VOLUME + 1;
    private static final int SQL_FIRSTPAGE = SQL_ISSUE + 1;
    private static final int SQL_LATPAGE = SQL_FIRSTPAGE + 1;
    private static final int SQL_NOTES = SQL_LATPAGE + 1;
    private static final int SQL_RECEIVED = SQL_NOTES + 1; 
    private static final int SQL_REVISED = SQL_RECEIVED + 1; 
    private static final int SQL_ACCEPTED = SQL_REVISED + 1; 
    private static final int SQL_PUBLISHED  = SQL_ACCEPTED + 1;
    private static final int SQL_COLUMNS  = SQL_PUBLISHED + 1; //number of columns

    /**
     * list of an article and a preposition
     */
    private static String[] articlesAndPrepositionsList = {
	"a", "about", "af", "along", "an", "and", "at", "au",
	"aus", "avec", "behind", "bei", "beyond", "by",
	"d'", "da", "dans", "das", "de",
	"del", "dell'", "della", "delle", "dem",
	"den", "der", "des", "det", "di",
	"die", "do", "du", "e", "ein",
	"einem", "einer", "eines", "el", "en",
	"et", "for",
	new String(new char[] {'f', 0xf6, 'r'}), // O-umlaut
	"for", "foer",
	"fra", "from",
	new String(new char[] {'f', 0xfc, 'r'}), // U-umlaut
	"fur", "fuer",
	"i", "il", "im", "in", "l'",
	"la", "le", "les", "los", "mit",
	"of", "on", "os", "pour", "pro",
	"som", "sur", "the", "till", "to",
	new String(new char[] {0xfc, 'b', 'e', 'r'}), // U-umlaut
	"uber", "ueber",
	"uma", "un", "una", "und",
	"une", "van", "von", "with", "y", "zu", "zur"
    };
    
    protected final static  String[][] wordAbbreviationMap =  {
	/*
	{"journal", "J."}, {"international", "Int."}, {"science", "Sci."},
	{"adavance", "Adv."}, {"adavances", "Adv."}, {"microbiology", "Microb."},
	{"analysis", "Anal."}, {"analytical", "Anal."}, {"analytically", "Anal."},
	{"chemical", "Chem."}, {"chemically", "Chem."}, {"chemistry", "Chem."},
	{"molecule", "Mol."}, {"molecular", "Mol."}
	*/
    };

    protected final static  String[][] startingMap =  {
	{"journal", "J."}, {"international", "Int."}, {"science", "Sci."},
	{"adavance", "Adv."}, {"microb", "Microb."},
	{"analy", "Anal."}, {"chemi", "Chem."},
	{"molecul", "Mol."}, {"environ", "Environ."} 
    };

    protected final static String[][] endingMap = {
	{"ology", "ol."}, {"ological", "ol."}, {"ologically", "ol."},
	{"ography", "ogr."}, {"ographical", "ogr."}, {"ographically", "ogr."},
	{"analysis", "anal."}, {"analytical", "anal."}, {"analytically", "anal."},
	{"chemical", "chem."}, {"chemically", "chem."}, {"chemistry", "chem."},
	{"molecule", "mol."}, {"molecular", "mol."},
	{"mental", "."}, {"etical", "."}
    };

    protected static HashMap<String, String> titleAbbreviation = new HashMap<String, String>();

    protected static HashMap<String, String> wordAbbreviations = new HashMap<String, String>(wordAbbreviationMap.length);

    protected static HashMap<String, String> startingAbbreviations = new HashMap<String, String>(startingMap.length);

    protected static HashMap<String, String> endingAbbreviations = new HashMap<String, String>(endingMap.length);

    protected static HashMap<String, String> articlesAndPrepositions = new HashMap<String, String>(articlesAndPrepositionsList.length);
    

    static {
	for (int i = 0; i < wordAbbreviationMap.length; i++) {
	    wordAbbreviations.put(wordAbbreviationMap[i][0], wordAbbreviationMap[i][1]);
	}

	for (int i = 0; i < startingMap.length; i++) {
	    startingAbbreviations.put(startingMap[i][0], startingMap[i][1]);
	}

	for (int i = 0; i < endingMap.length; i++) {
	    endingAbbreviations.put(endingMap[i][0], endingMap[i][1]);
	}

	for (int i = 0; i < articlesAndPrepositionsList.length; i++) {
	    articlesAndPrepositions.put(articlesAndPrepositionsList[i],
					articlesAndPrepositionsList[i]);
	}
    }

    /**
     * Constructs an empty {@code Publication} object
     */
    public Publication()
    {
	super();
    }
    
    /**
     * Constructs an {@code Auahot} having
     * {@code name} as its name, i.e. perisitent ID
     *
     * @param name {@code String} representing its name,
     * i.e. perisitent ID
     */
    public Publication(String name)
    {
	super(name);
    }
    
    /**
     * Constructs a copy of given {@code name}
     *
     * @param name {@code Name} representation of an {@code Publication}
     */
    public Publication(Name<? extends Publication> name)
    {
	//don't use NamedObject(String) constructor
	this();
	
	if(name instanceof Publication)
	    publication((Publication)name);
	else
	    setPersistentID(name.getLiteral());
    }

    /**
     * Constructs a {@code Publication} object using
     * based on SQL {@code resultSet}
     *
     * @param resultSet {@code java.sql.ResultSet} representing
     * SQL result
     */
    public Publication(ResultSet resultSet)
    throws java.sql.SQLException
    {
	this(resultSet.getString(SQL_AUTHORNAMES),
	     resultSet.getString(SQL_AFFILIATIONS),
	     resultSet.getString(SQL_CITATIONTITLE),
	     null, //contentsTitle
	     resultSet.getString(SQL_ISXN),
	     resultSet.getString(SQL_YEAR),
	     resultSet.getString(SQL_VOLUME),
	     resultSet.getString(SQL_ISSUE),
	     resultSet.getString(SQL_FIRSTPAGE),
	     resultSet.getString(SQL_LATPAGE),
	     resultSet.getString(SQL_NOTES),
	     resultSet.getString(SQL_RECEIVED),
	     resultSet.getString(SQL_REVISED),
	     resultSet.getString(SQL_ACCEPTED),
	     resultSet.getString(SQL_PUBLISHED),
	     null, //authors
	     null, //appearances
	     null, //container
	     null, //contents
	     null  //references
	     );

	setPersistentID(resultSet.getString(SQL_PID));     
    }

    /**
     * Constructs a non-trivial {@code Publication} object.
     *
     * @param authorNames {@code String} represents author names as appeared in the publication
     * @param affiliations {@code String} represents affiliations of authors as appeared in the publication 
     * @param citationTitle {@code String} represents citational title of the publication
     * @param contentsTitle {@code String} represents title of the article, chapter etc.
     * @param isxn {@code String} represents ISBN or ISSN of the publication if available
     * @param year {@code int} represents year of publication
     * @param volume {@code String} represents volume of the publication
     * @param issue {@code String} represents issue of the publication
     * @param firstPage {@code String} represents first page of the publication 
     * @param lastPage {@code String} represents last page of the publication
     * @param notes {@code String} represents notes of the publication
     * @param received {@code String} representing when the manuscript was received
     * @param revised {@code String} representing when the revised manuscript was received
     * @param accepted {@code String} representing when the manuscript was acceppted to publish
     * @param authors {@code List} representing the list of {@code Author}s
     * @param appearances {@code List} representing the list of {@code Appearance}s 
     * @param container {@code Publication} containging this publication, e.g. edited book containing a chapter
     * @param contents{@code List} represents a list of {@code Publication}s contained in this {@code Publication},
     * e.g. chapters in a edited book
     */    
    public Publication(String authorNames,
		       String affiliations,
		       String citationTitle,
		       String contentsTitle,
		       String isxn,
		       String year,
		       String volume,
		       String issue,
		       String firstPage,
		       String lastPage,
		       String notes,
		       String received,
		       String revised,
		       String accepted,
		       String published,
		       List<Author> authors,
		       List<Appearance> appearances,
		       Publication container,
		       List<Publication> contents,
		       List<Publication> references)
    {
	this();
	publication(false, authorNames, affiliations, citationTitle, contentsTitle, isxn, 
		    year, volume, issue, firstPage, lastPage, notes,
		    received, revised, accepted, published,
		    authors, appearances, container, contents, references);
    }

    /**
     * Constructs a deep copy of {@code publication}
     *
     * @param publication {@code Publication} to be copied deeply
     */
    public Publication(Publication publication)
    {
	this();
	publication(publication);
    }


    /**
     * Constructs an {@code Publication} object using XML data
     * given by {@code xml}
     *
     * @param xml {@code Element} specifying {@code Publication}
     *
     */
    public Publication(Element xml)
    {
	super();
	NodeList nodeList = xml.getChildNodes();
	int subChildCount = nodeList.getLength();
	String persistentID = null;
	HashMap<String, String> hash = new HashMap<String, String>();
	int authorCount = 0;
	boolean toBeResolved = false;
	List<Appearance> appearanceList = null;
	for (int j = 0; j < subChildCount; j++) {
	    Node node = nodeList.item(j);

	    if(node.getNodeType () == Node.ELEMENT_NODE) {
		Element element = (Element)node;
		String tagName = element.getTagName();
		String id = element.getAttribute("id");

		if (tagName.equals ("oid")) 
		    persistentID = getString(element);
		else if(tagName.equals ("authors"))
		    authorNames = getString(element);
		else if(tagName.equals ("affiliations"))
		    affiliations = getString(element);
		else if(tagName.equals ("author")) {
		    String pID = getString(element);
		    hash.put(id, pID);
		    authorCount++;
		}
		else if(tagName.equals ("citationTitle"))
		    citationTitle = getString(element);
		else if(tagName.equals ("contentsTitle"))
		    contentsTitle = getString(element);
		else if(tagName.equals ("isxn"))
		    isxn = getString(element);
		else if(tagName.equals ("year"))
		    year = getString(element);
		else if(tagName.equals ("volume"))
		    volume = getString(element);
		else if(tagName.equals ("issue"))
		    issue = getString(element);
		else if(tagName.equals ("startpage"))
		    firstPage = getString(element);
		else if(tagName.equals ("lastpage"))
		    lastPage = getString(element);
		else if(tagName.equals ("notes"))
		    setNotes(getString(element));
		else if(tagName.equals ("publisher"))
		    publisher = getString(element);
		else if(tagName.equals ("place"))
		    place = getString(element);
		else if(tagName.equals ("appearance")) {
		    String pID = getString(element);
		    Appearance ap =
			(Appearance)curator.get(pID);
		    if(ap != null) {
			if(appearanceList == null)
			    appearanceList = new ArrayList<Appearance>();
			appearanceList.add(ap);
			toBeResolved = true;
		    }
		    else {
			ap = new Appearance();
			ap.setLiteral(pID);
			curator.put(ap);
			//addAppearance(ap);
		    }
		}
		else{}
            }
        }

	if(appearanceList != null) {
	    appearances = new Appearance[appearanceList.size()];
	    appearanceList.toArray(appearances);
	    for(int i = 0; i < appearances.length; i++)
		appearances[i].setPublication(this);
	}
	
	if(persistentID != null &&
	   !persistentID.equals(getLiteral()))
	    setLiteral(persistentID); //i.e. other key data are empty

	if(authorCount > 0) {
	    authors = new Author[authorCount];
	    String pID = null;
	    for(int i = 0; i < authorCount; i++) {
		pID = hash.get(Integer.toString(i));
		Author au = (Author)curator.get(pID);
		if(au != null) {
		    authors[i] = au;
		    toBeResolved = true;
		}
		else {
		    au = new Author();
		    au.setLiteral(pID);
		    curator.put(au);
		    authors[i] = au;
		}
	    }
	    for(int i = 0; i < authors.length; i++) {
		authors[i].addPublication(this);
	    }
	}
	/*
	if(toBeResolved)
	    curator.resolve(this);
	*/
    }

    /**
     * Copies {@code Publication} {@code p} to this object deeply
     *
     * @param p {@code Publication} to be copied deeply
     */
    protected void publication(Publication p)
    {
	if(p == null)
	    return;
	publication(true,
		    p.authorNames,
		    p.affiliations,
		    p.citationTitle,
		    p.contentsTitle,
		    p.isxn,
		    p.year,
		    p.volume,
		    p.issue,
		    p.firstPage,
		    p.lastPage,
		    p.notes,
		    p.received,
		    p.revised,
		    p.accepted,
		    p.published,
		    p.authors,
		    p.appearances,
		    p.container,
		    p.contents,
		    p.references);
    }
    
    /**
     * Copy parameters to this object in deep or shallow depending on {@code deepCopy}
     *
     * @param deepCopy boolean indicating wheter deep or shallow copy to be done
     * @param authorNames {@code String} represents author names as appeared in the publication
     * @param affiliations {@code String} represents affiliations of authors as appeared in the publication 
     * @param citationTitle {@code String} represents citational title of the publication
     * @param contentsTitle {@code String} represents title of the article, chapter etc.
     * @param isxn {@code String} represents ISBN or ISSN of the publication if available
     * @param year {@code int} represents year of publication
     * @param volume {@code String} represents volume of the publication
     * @param issue {@code String} represents issue of the publication
     * @param firstPage {@code String} represents first page of the publication 
     * @param lastPage {@code String} represents last page of the publication
     * @param notes {@code String} represents notes of the publication
     * @param received {@code String} representing when the manuscript was received
     * @param revised {@code String} representing when the revised manuscript was received
     * @param accepted {@code String} representing when the manuscript was acceppted to publish
     * @param published {@code String} representing when the publication published
     * @param authors {@code Vector} representing the list of {@code Author}s
     * @param appearances {@code Vector} representing the list of {@code Appearance}s 
     * @param container {@code Publication} containging this publication, e.g. edited book containing a chapter
     * @param  contents{@code Vector} represents a list of {@code Publication}s contained in this {@code Publication},
     * e.g. chapters in a edited book
     */    
    protected void publication(boolean deepCopy,
			       String authorNames,
			       String affiliations,
			       String citationTitle,
			       String contentsTitle,
			       String isxn,
			       String year,
			       String volume,
			       String issue,
			       String firstPage,
			       String lastPage,
			       String notes,
			       String received,
			       String revised,
			       String accepted,
			       String published,
			       Collection<Author> authors,
			       Collection<Appearance> appearances,
			       Publication container,
			       Collection<Publication> contents,
			       Collection<Publication> references)
    {
	publication(deepCopy,
		    authorNames,
		    affiliations,
		    citationTitle,
		    contentsTitle,
		    isxn,
		    year,
		    volume,
		    issue,
		    firstPage,
		    lastPage,
		    notes,
		    received,
		    revised,
		    accepted,
		    published,
		    container);

	if(//deepCopy &&
	   authors != null &&
	   authors.size() > 0) {
	    this.authors = new Author[authors.size()];
	    authors.toArray(this.authors);
	}
	else {
	    if(this.authors != null) {
		Author[] tmp = this.authors;
		this.authors = null;
		for(int i = 0; i < tmp.length; i++)
		    tmp[i].removePublication(this);
		ArrayUtility.clear(tmp);
	    }
	    else
		this.authors = null;
	}

	if(deepCopy &&
	   appearances != null &&
	   appearances.size() > 0) {
	    this.appearances = new Appearance[appearances.size()];
	    appearances.toArray(this.appearances);
	}
	else {
	    if(this.appearances != null) {
		Appearance[] tmp = this.appearances;
		this.appearances = null;
		for(int i = 0; i < tmp.length; i++)
		    tmp[i].setPublication(null);
		ArrayUtility.clear(tmp);
	    }
	    else
		this.appearances = null;
	}

	if(deepCopy &&
	   contents != null &&
	   contents.size() > 0) {
	    this.contents = new Publication[contents.size()];
	    contents.toArray(this.contents);
	}
	else {
	    if(this.contents != null) {
		Publication[] tmp = this.contents;
		this.contents = null;
		for(int i = 0; i < tmp.length; i++)
		    tmp[i].setContainer(null);
		ArrayUtility.clear(tmp);
	    }
	    else
		this.contents = null;
	}


	if(//deepCopy &&
	   references != null &&
	   references.size() > 0) {
	    this.references = new Publication[references.size()];
	    references.toArray(this.references);
	}
	else {
	    if(this.references != null) {
		Publication[] tmp = this.references;
		this.references = null;
		//for(int i = 0; i < tmp.length; i++)
		    //tmp[i].removePublication(this);
		ArrayUtility.clear(tmp);
	    }
	    else
		this.references = null;
	}
    }

    /**
     * Copy parameters to this object in deep or shallow depending on {@code deepCopy}
     *
     * @param deepCopy boolean indicating wheter deep or shallow copy to be done
     * @param authorNames {@code String} represents author names as appeared in the publication
     * @param affiliations {@code String} represents affiliations of authors as appeared in the publication 
     * @param citationTitle {@code String} represents citational title of the publication
     * @param contentsTitle {@code String} represents title of the article, chapter etc.
     * @param isxn {@code String} represents ISBN or ISSN of the publication if available
     * @param year {@code int} represents year of publication
     * @param volume {@code String} represents volume of the publication
     * @param issue {@code String} represents issue of the publication
     * @param firstPage {@code String} represents first page of the publication 
     * @param lastPage {@code String} represents last page of the publication
     * @param notes {@code String} represents notes of the publication
     * @param received {@code String} representing when the manuscript was received
     * @param revised {@code String} representing when the revised manuscript was received
     * @param accepted {@code String} representing when the manuscript was acceppted to publish
     * @param published {@code String} representing when the publication published
     * @param authors {@code Vector} representing the list of {@code Author}s
     * @param appearances {@code Vector} representing the list of {@code Appearance}s 
     * @param container {@code Publication} containging this publication, e.g. edited book containing a chapter
     * @param  contents{@code Vector} represents a list of {@code Publication}s contained in this {@code Publication},
     * e.g. chapters in a edited book
     */    
    protected void publication(boolean deepCopy,
			       String authorNames,
			       String affiliations,
			       String citationTitle,
			       String contentsTitle,
			       String isxn,
			       String year,
			       String volume,
			       String issue,
			       String firstPage,
			       String lastPage,
			       String notes,
			       String received,
			       String revised,
			       String accepted,
			       String published,
			       Author[] authors,
			       Appearance[] appearances,
			       Publication container,
			       Publication[] contents,
			       Publication[] references)
    {
	publication(deepCopy,
		    authorNames,
		    affiliations,
		    citationTitle,
		    contentsTitle,
		    isxn,
		    year,
		    volume,
		    issue,
		    firstPage,
		    lastPage,
		    notes,
		    received,
		    revised,
		    accepted,
		    published,
		    container);
	

	if(deepCopy &&
	   authors != null &&
	   authors.length > 0) {
	    this.authors = new Author[authors.length];
	    System.arraycopy(authors, 0, this.authors, 0, authors.length);
	}
	else {
	    if(this.authors != null) {
		Author[] tmp = this.authors;
		this.authors = null;
		for(int i = 0; i < tmp.length; i++)
		    tmp[i].removePublication(this);
		ArrayUtility.clear(tmp);
	    }
	    else
		this.authors = null;
	}

	if(deepCopy &&
	   appearances != null &&
	   appearances.length > 0) {
	    this.appearances = new Appearance[appearances.length];
	    System.arraycopy(appearances, 0, this.appearances, 0, appearances.length);
	}
	else {
	    if(this.appearances != null) {
		Appearance[] tmp = this.appearances;
		this.appearances = null;
		for(int i = 0; i < tmp.length; i++)
		    tmp[i].setPublication(null);
		ArrayUtility.clear(tmp);
	    }
	    else
		this.appearances = null;
	}

	if(deepCopy &&
	   contents != null &&
	   contents.length > 0) {
	    this.contents = new Publication[contents.length];
	    System.arraycopy(contents, 0, this.contents, 0, contents.length);
	}
	else {
	    if(this.contents != null) {
		Publication[] tmp = this.contents;
		this.contents = null;
		for(int i = 0; i < tmp.length; i++)
		    tmp[i].setContainer(null);
		ArrayUtility.clear(tmp);
	    }
	    else
		this.contents = null;
	}


	if(//deepCopy &&
	   references != null &&
	   references.length > 0) {
	    this.references = new Publication[references.length];
	    System.arraycopy(references, 0, this.references, 0, references.length);
	}
	else {
	    if(this.references != null) {
		Publication[] tmp = this.references;
		this.references = null;
		/*
		for(int i = 0; i < tmp.length; i++)
		    tmp[i].removePublication(this);
		*/
		ArrayUtility.clear(tmp);
	    }
	    else
		this.references = null;
	}
    }
    /**
     * Copy parameters to this object in deep or shallow depending on {@code deepCopy}
     *
     * @param deepCopy boolean indicating wheter deep or shallow copy to be done
     * @param authorNames {@code String} represents author names as appeared in the publication
     * @param affiliations {@code String} represents affiliations of authors as appeared in the publication 
     * @param citationTitle {@code String} represents citational title of the publication
     * @param contentsTitle {@code String} represents title of the article, chapter etc.
     * @param isxn {@code String} represents ISBN or ISSN of the publication if available
     * @param year {@code int} represents year of publication
     * @param volume {@code String} represents volume of the publication
     * @param issue {@code String} represents issue of the publication
     * @param firstPage {@code String} represents first page of the publication 
     * @param lastPage {@code String} represents last page of the publication
     * @param notes {@code String} represents notes of the publication
     * @param received {@code String} representing when the manuscript was received
     * @param revised {@code String} representing when the revised manuscript was received
     * @param accepted {@code String} representing when the manuscript was acceppted to publish
     * @param published {@code String} representing when the publication published
     * @param container {@code Publication} containging this publication, e.g. edited book containing a chapter
     * e.g. chapters in a edited book
     */    
    protected void publication(boolean deepCopy,
			       String authorNames,
			       String affiliations,
			       String citationTitle,
			       String contentsTitle,
			       String isxn,
			       String year,
			       String volume,
			       String issue,
			       String firstPage,
			       String lastPage,
			       String notes,
			       String received,
			       String revised,
			       String accepted,
			       String published,
			       Publication container)
    {
	if(deepCopy && authorNames != null)
	    this.authorNames = new String(authorNames);
	else
	    this.authorNames = authorNames;

	if(deepCopy && affiliations != null)
	    this.affiliations = new String(affiliations);
	else
	    this.affiliations = affiliations;

	if(deepCopy && citationTitle != null)
	    this.citationTitle = new String(citationTitle);
	else
	    this.citationTitle = citationTitle;

	if(deepCopy && contentsTitle != null)
	    this.contentsTitle = new String(contentsTitle);
	else
	    this.contentsTitle = contentsTitle;

	if(deepCopy && isxn != null)
	    this.isxn = new String(isxn);
	else
	    this.isxn = isxn;

	this.year = year;

	if(deepCopy && volume != null)
	    this.volume = new String(volume);
	else
	    this.volume = volume;

	if(deepCopy && issue != null)
	    this.issue = new String(issue);
	else
	    this.issue = issue;

	if(deepCopy && firstPage != null)
	    this.firstPage = new String(firstPage);
	else
	    this.firstPage = firstPage;

	if(deepCopy && lastPage != null)
	    this.lastPage = new String(lastPage);
	else
	    this.lastPage = lastPage;

	if(deepCopy && notes != null)
		setNotes(new String(notes));
	else
	    setNotes(notes);

	if(deepCopy && received != null)
	    this.received = new String(received);
	else
	    this.received = received;

	if(deepCopy && revised != null)
	    this.revised = new String(revised);
	else
	    this.revised = revised;

	if(deepCopy && accepted != null)
	    this.accepted = new String(accepted);
	else
	    this.accepted = accepted;

	if(deepCopy && published != null)
	    this.published = new String(published);
	else
	    this.published = published;

	this.container = container;

    }

    /**
     * Returns a persistent ID representing this {@code Publication}
     * with specified  {@code separator}.  It contains class name header
     * if {@code withClassName} true.
     *
     * @param separator {@code char} to be used as the field separator
     * @param withClassName {@code boolean} specifying with or without
     * class name header
     *
     * @return String representing this {@code Publication}
     */
    public String getPersistentID(String separator, boolean withClassName)
    {
	if(entity != null)
	    return getEntity().getPersistentID(separator, withClassName);

	if(literal != null && literal.length() != 0) {
	    if(withClassName)
		return literal;
	    else 
		return literal.substring(getClassNameHeader().length());
	}

	StringBuffer ret;

	if(withClassName)
	    ret = getClassNameHeaderBuffer();
	else
	    ret = new StringBuffer();
	if(authorNames != null)
	    ret.append(authorNames);
	ret.append(separator);
	if(year != null)
	    ret.append(year);
	ret.append(separator);
	String s = getDOI();
	if (s == null || s.length() == 0) 
	    s = getISXN();
	if (s == null || s.length() == 0) 
	    s = getCitationTitle();
	if(s != null)
	    ret.append(s);
	ret.append(separator);
	if(volume != null)
	    ret.append(volume);
	ret.append(separator);
	if(issue != null)
	    ret.append(issue);
	ret.append(separator);
	if(firstPage != null)
	    ret.append(firstPage);
	ret.append(separator);
	if(lastPage != null)
	    ret.append(lastPage);
	
	return ret.toString();
    }
    
    /**
     * Returnes number of fields separators in persistent ID
     *
     * @return int representing number of fields separators in persistent ID
     */ 
    public int getFieldSepartorsCount()
    {
	return PID_ITEMS; //after author, year, journal, volume, number, firstPage
    }

    /**
     * Merges {@code namedObject} with this {@code Publication}
     * if possible, but not yet implemented.
     * It returns true if merged.
     *
     * @param namedObject a {@code NamedObject} to be merged
     *
     * @return true if merged, or false if not mergiable
     */
    public boolean merge(NamedObject<?> namedObject){
	if(!(namedObject instanceof Publication))
	    return false;
	return false; //not yet implemented
    }
    
    boolean decomposed = false;

    /**
     * Decomposes given persistent ID
     */
    private void decomposePID()
    {
	if(entity != null) {
	    getEntity().decomposePID();
	    return;
	}

	if(literal == null ||
	   literal.length() == 0 ||
	   decomposed)
	    return;

	int index = literal.indexOf(classNameSeparator);
	if(index == -1)
	    return;
	index += classNameSeparator.length();
	String s = literal.substring(index);

	index = s.indexOf(fieldSeparator);
	authorNames = s.substring(0, index);
	index += fieldSeparator.length();

	s = substring(index);
	index = s.indexOf(fieldSeparator);
	year = s.substring(0, index);
	index += fieldSeparator.length();

	//rethink...
	s = substring(index);
	index = s.indexOf(fieldSeparator);
	citationTitle = s.substring(0, index);
	index += fieldSeparator.length();
	
	s = substring(index);
	index = s.indexOf(fieldSeparator);
	volume = s.substring(0, index);
	index += fieldSeparator.length();

	s = substring(index);
	index = s.indexOf(fieldSeparator);
	issue = s.substring(0, index);
	index += fieldSeparator.length();

	s = substring(index);
	index = s.indexOf(fieldSeparator);
	firstPage = s.substring(0, index);
	index += fieldSeparator.length();

	s = substring(index);
	index = s.indexOf(fieldSeparator);
	lastPage = s.substring(0, index);
    }

    /**
     * Returns authors' names
     *
     * @return {@code String} authors' names as appeared in the publication
     */
    public String getAuthorNames()
    {
	if(entity != null)
	    return getEntity().getAuthorNames();

	if(!isNominal())
	    return authorNames;

	return getPersistentIDComponent(AUTHOR);
    }

    /**
     * Sets {@code authorName} as authors' names appeared in the publication
     *
     * @param authorName {@code String} representing authors' names as appeared in the publication
     */ 
    public void setAuthorNames(String authorNames)
    {
	if(entity != null)
	    getEntity().setAuthorNames(authorNames);

	if(this.authorNames == authorNames ||
	   (authorNames != null && authorNames.equals(this.authorNames)))
	    return;
	this.authorNames = authorNames;
    }
    
    /**
     * Returns affiliation list
     *
     * @return {@code String} affiliation list as appeared in the publication
     */
    public String getAffiliations()
    {
	if(entity != null)
	    return getEntity().getAffiliations();

	if(affiliations == null || affiliations.length() == 0) 
	    decomposePID();
	return affiliations;
    }

    /**
     * Sets {@code affiliations} appeared in the publication
     *
     * @param affiliations {@code String} representing affiliation list appeared in the publication
     */ 
    public void setAffiliations(String affiliations)
    {
	if(entity != null)
	    getEntity().setAffiliations(affiliations);

	if(this.affiliations == affiliations ||
	   (affiliations != null && affiliations.equals(this.affiliations)))
	   return;

	this.affiliations = affiliations;
    }

    /**
     * Returns type of publication
     *
     * @return int representing type of publication
     */
    public int getPublicationType()
    {
	if(entity != null)
	    return getEntity().getPublicationType();

	return publicationType;
    }
    
    /**
     * Sets {@code type} of the publication
     *
     * @param type {@code int} representing type of the publication
     */ 
    public void setPublicationType(int type)
    {
	if(entity != null)
	    getEntity().setPublicationType(type);

	publicationType = type;
    }

    /**
     * Returns canonical citation
     *
     * @return {@code String} representing canonical citation of the publication
     */
    public String getCanonicalCitation()
    {
	return getCanonicalCitation(new StringBuffer()).toString();
    }

    /**
     * Returns canonical citation
     *
     * @return {@code String} representing canonical citation of the publication
     */
    public StringBuffer getCanonicalCitation(StringBuffer buffer)
    {
	if(buffer == null)
	    buffer = new StringBuffer();
	switch (getPublicationType()) {
	case JOURNAL_ARTICLE:
	    buffer.append(canonicalize(getCitationTitle()));
	    if(buffer.length() > 0)
		buffer.append(' ');
	    String v = getVolume();
	    if(v != null && v.length() > 0) {
		buffer.append(v);
		if(buffer.length() > 0)
		    buffer.append(' ');
	    }
	    String fp = getFirstPage();
	    if(fp != null && fp.length() > 0) {
		if(v != null && v.length() > 0)
		    buffer.append(':');
		else
		    buffer.append(' ');

		buffer.append(fp);
		String lp = getLastPage();
		if(lp != null && lp.length() > 0) {
		    if(!fp.equals(lp)) {
			buffer.append('-').append(lp);
		    }
		}
	    }
	    break;
	default:
	    buffer.append(getCitationTitle());
	}
	return buffer;
    }

    public static String canonicalize(String text)
    {
	if(text == null || text.length() == 0)
	    return "";

	StringTokenizer tokens = new StringTokenizer(text);
	String token = null;
	String abbreviation = null;
	StringBuffer buffer = new StringBuffer();
	while(tokens.hasMoreElements()) {
	    token = tokens.nextToken();
	    abbreviation = token.toLowerCase();
	    if(articlesAndPrepositions.get(abbreviation) != null)
		continue;
	    abbreviation = wordAbbreviations.get(abbreviation);
	    if(abbreviation != null) {
		if(buffer.length() > 0)
		    buffer.append(' ');
		buffer.append(abbreviation);
		continue;
	    }

	    abbreviation = token.toLowerCase();
	    String starting = null;
	    Iterator<String> keys = startingAbbreviations.keySet().iterator();
	    String key = null;
	    while(starting == null && keys.hasNext()) {
		key = keys.next();
		if(abbreviation.startsWith(key))
		    starting = startingAbbreviations.get(key);
	    }
	    if(starting != null) {
		if(buffer.length() > 0)
		    buffer.append(' ');
		buffer.append(starting);
		continue;
	    }

	    abbreviation = token.toLowerCase();
	    String ending = null;
	    keys = endingAbbreviations.keySet().iterator();
	    key = null;
	    while(ending == null && keys.hasNext()) {
		key = keys.next();
		if(abbreviation.endsWith(key))
		    ending = endingAbbreviations.get(key);
	    }
	    if(ending != null) {
		if(buffer.length() > 0)
		    buffer.append(' ');
		buffer.append(token.substring(0, token.length() - key.length()));
		buffer.append(ending);
		continue;
	    }

	    if(buffer.length() > 0)
		buffer.append(' ');
	    buffer.append(token);
	}

	return buffer.toString();
    }

    /**
     * Returns citation title of the publication, e.g. journal name
     *
     * @return {@code String} representing citation title of the publication, e.g. journal name
     */
    public String getCitationTitle()
    {
	if(entity != null)
	    return getEntity().getCitationTitle();

	/*
	if(citationTitle == null || citationTitle.length() == 0) 
	    decomposePID();
	*/

	if(!isNominal())
	    return citationTitle;

	return getPersistentIDComponent(2);
    }
    
    /**
     * Sets {@code citationTitle} of the publication
     *
     * @param citationTitle {@code String} representing citation title of the publication, e.g. journal name
     */
    public void setCitationTitle(String citationTitle)
    {
	if(entity != null)
	    getEntity().setCitationTitle(citationTitle);

	if(this.citationTitle == citationTitle ||
	   (citationTitle != null && citationTitle.equals(this.citationTitle)))
	    return;

	this.citationTitle = citationTitle;
    }

    /**
     * Returns contents title of the publication, e.g. article's title
     *
     * @return {@code String} representing contents title of the publication, e.g. article's title
     */
    public String getTitle()
    {
	if(entity != null)
	    return getEntity().getTitle();

	if(!isNominal()) {
	    if(contentsTitle == null || contentsTitle.length() == 0) 
		return contentsTitle;
	    if(isxn == null || isxn.length() == 0) 
		return isxn;
	}

	return getPersistentIDComponent(TITLE);
    }

    /**
     * Returns contents title of the publication, e.g. article's title
     *
     * @return {@code String} representing contents title of the publication, e.g. article's title
     */
    public String getContentsTitle()
    {
	if(entity != null)
	    return getEntity().getContentsTitle();

	if(!isNominal())
	    return contentsTitle;

	return getPersistentIDComponent(TITLE);
    }

    /**
     * Sets {@code contentsTitle} of the publication
     *
     * @param contentsTitle {@code String} representing contents title of the publication, e.g. article's title
     */
    public void setContentsTitle(String contentsTitle)
    {
	if(entity != null)
	    getEntity().setContentsTitle(contentsTitle);

	this.contentsTitle = contentsTitle;
    }

    /**
     * Returns DOI of publication
     *
     * @return {@code String} representing DOI of publication
     */
    public String getDOI()
    {
	if(entity != null)
	    return getEntity().getDOI();

	if(!isNominal())
	    return doi;

	String d = getPersistentIDComponent(DOI);

	if(d.startsWith("doi:"))
	   return d;
	
	return null;
    }
    
    /**
     * Sets {@code doi} of the publication
     *
     * @param doi {@code String} representing DOI of the publication
     */ 
    public void setDOI(String doi)
    {
	if(entity != null)
	    getEntity().setDOI(doi);

	if(this.doi == doi ||
	   (doi != null && doi.equals(this.doi)))
	    return;

	this.doi = doi;
    }
    


    /**
     * Returns ISBN or ISXN 
     *
     * @return {@code String}
     */
    public String getISXN()
    {
	if(entity != null)
	    return getEntity().getISXN();

	if(isxn == null || isxn.length() == 0) 
	    decomposePID();
	return isxn;
    }

    /**
     * Returns {@code String} representing ISBN, or 
     * null if it is not ISBN
     *
     * @return {@code String} representing ISBN or null if it is not ISBN
     */
    public String getISBN()
    {
	if(entity != null)
	    return getEntity().getISBN();
	return getISXN("ISBN");
    }
    
    /**
     * Returns {@code Enumeration} of ISBN, or 
     * null if ISBN is not specified
     *
     * @return {@code Enumeration} of ISBN or null if no ISBN is specified
     */
    public String[] getISBNs()
    {
	if(entity != null)
	    return getEntity().getISBNs();

	return ArrayUtility.copy(isbn);
    }
    
    /**
     * Returns {@code String} representing ISSN, or 
     * null if it is not ISSN
     *
     * @return {@code String} representing ISSN or null if it is not ISSN
     */
    public String getISSN()
    {
	if(entity != null)
	    return getEntity().getISSN();

	return getISXN("ISSN");
    }

    /**
     * Returns {@code Enumeration} of ISSN, or 
     * null if ISSN is not specified
     *
     * @return {@code Enumeration} of ISSN or null if no ISSN is specified
     */
    public String[] getISSNs()
    {
	if(entity != null)
	    return getEntity().getISSNs();

	return ArrayUtility.copy(issn);
    }
    
    
    /**
     * Returns {@code String} it {@code getISXN} starts with
     * {@code heading} or null if no.
     *
     * @param heading {@code String} specifying ISBN or ISSN.
     *
     * @return {@code String} representing ISXN or null if it does not start with {@code heading}
     */
    public String getISXN(String heading)
    {
	if(entity != null)
	    return getEntity().getISXN(heading);

	String value = getISXN();
	if(value.startsWith(heading))
	   return value;
	else
	    return null;
    }

    /**
     * Sets {@code isxn} as ISBN or ISSN of the publication
     *
     * @param isxn {@code String} to be set as ISBN or ISSN.
     */
    public void setISXN(String isxn)
    {
	if(entity != null)
	    getEntity().setISXN(isxn);
	if(this.isxn == isxn ||
	   (isxn != null && isxn.equals(this.isxn)))
	    return;

	this.isxn = isxn;
    }
    
    /**
     * Returns year of publication in {@code String}
     *
     * @return {@code String} representing year of publication
     */
    public String getYear()
    {
	if(entity != null)
	    return getEntity().getYear();
	/*
	if(year == null || year.length() == 0) 
	    decomposePID();
	*/
	if(!isNominal())
	    return year;

	return getPersistentIDComponent(YEAR);
    }
    
    /**
     * Sets {@code year} of publication
     *
     * @param year {@code String} representing year of publication
     */ 
    public void setYear(String year)
    {
	if(entity != null)
	    getEntity().setYear(year);

	if(this.year == year ||
	   (year != null && year.equals(this.year)))
	    return;

	this.year = year;
    }
    
    /**
     * Returns volume of periodical containing the publication
     *
     * @return {@code String} representing volume of periodical containing the publication
     */
    public String getVolume()
    {
	if(entity != null)
	    return getEntity().getVolume();

	if(!isNominal())
	    return volume;

	return getPersistentIDComponent(VOLUME);
    }
    
    /**
     * Sets {@code volume} of the publication
     *
     * @param volume {@code String} representing volume of the publication
     */
    public void setVolume(String volume)
    {
	if(entity != null)
	    getEntity().setVolume(volume);

	if(volume == this.volume ||
	   (volume != null && volume.equals(this.volume)))
	    return;

	this.volume = volume;
    }
    
    /**
     * Returns number of journal issue etc. containing the publication
     *
     * @return String representing number of journal issue etc. containing the publication
     */
    public String getIssue ()
    {
	if(entity != null)
	    return getEntity().getIssue();

	if(!isNominal())
	    return issue;

	return getPersistentIDComponent(ISSUE);
    }
    
    /**
     * Sets {@code issue} as issue of journal issue etc. containing the publication
     *
     * @param issue {@code String} representing issue of journal issue etc. containing the publication
     */
    public void setIssue(String issue)
    {
	if(entity != null)
	    getEntity().setIssue(issue);

	if(issue == this.issue ||
	   (issue != null && issue.equals(this.issue)))
	    return;

	this.issue = issue;
    }
    
    /**
     * Returns {@code String} representing first page of the publication
     *
     * @return {@code String} representing first page of the publication
     */
    public String getFirstPage()
    {
	if(entity != null)
	    return getEntity().getFirstPage();

	if(!isNominal())
	    return firstPage;

	return getPersistentIDComponent(FIRST_PAGE);
    }
    
    /**
     * Sets {@code page} as the first page of the publication
     *
     * @param page {@code String} representing the first page of the publication
     */
    public void setFirstPage(String page)
    {
	if(entity != null)
	    getEntity().setFirstPage(page);

	if(page == firstPage ||
	   (page != null && page.equals(firstPage)))
	    return;

	firstPage = page;
    }
    
    /**
     * Returns {@code String} representing last page of the publication
     *
     * @return {@code String} representing last page of the publication
     */
    public String getLastPage()
    {
	if(entity != null)
	    return getEntity().getLastPage();

	if(!isNominal())
	    return lastPage;

	return getPersistentIDComponent(LAST_PAGE);
    }
    
    /**
     * Sets {@code page} as the last page of the publication
     *
     * @param page {@code String} representing the last page of the publication
     */
    public void setLastPage(String page)
    {
	if(entity != null)
	    getEntity().setLastPage(page);

	if(entity != null)
	    getEntity().setLastPage(page);

	if(page == lastPage ||
	   (page != null && page.equals(lastPage)))
	    return;

	lastPage = page;
    }
    
    /**
     * Parses a {@code line} and sets values of this object accordint to it
     *
     * @param line {@code String} containing fragment of data to be set
     */
    public void parseLine(String line)
    {
	if(entity != null)
	    getEntity().parseLine(line);
    }

    /**
     * Returns {@code Publication} containing the pulication,
     * e.g. an edited book containing this publciation as a chapter
     *
     * @return {@code Publication} containing the pulication
     */
    public Publication getContainer()
    {
	if(entity != null)
	    return getEntity().getContainer();

	return container;
    }

    /**
     * Sets {@code publication} as container of this publication,
     * e.g. an edited book containing this publciation as a chapter
     *
     * @return {@code Publication} to be set as container of the pulication
     */ 
    public void setContainer(Publication publication)
    {
	if(entity != null)
	    getEntity().setContainer(publication);

	if(container == publication)
	    return;
	if(container != null)
	    container.removeContents(this);
	container = publication;
	if(container != null)
	    container.addContents(this);
    }

    /**
     * Returns name of publisher in {@code String}
     *
     * @return Strings represnting name of publisher
     */
    public String getPublisher()
    {
	if(entity != null)
	    return getEntity().getPublisher();

	return publisher;
    }

    /**
     * Sets {@code publisher} as name of the publisher
     *
     * @param publisher {@code String} representing name of the publisher
     *
     */
    public void setPublisher(String publisher)
    {
	if(entity != null)
	    getEntity().setPublisher(publisher);

	if(publisher == this.publisher ||
	   (publisher != null && publisher.equals(this.publisher)))
	    return;

	this.publisher = publisher;
    }

    /**
     * Returns {@code String} representing place of publication
     *
     * @return String representing place of publication
     */
    public String getPlace()
    {
	if(entity != null)
	    return getEntity().getPlace();
	return place;
    }

    /**
     * Sets {@code place} as place of publication
     *
     * @param place {@code String} representing place of publication
     */
    public void setPlace(String place)
    {
	if(entity != null)
	    getEntity().setPlace(place);

	if(place == this.place ||
	   (place != null && place.equals(this.place)))
	    return;

	this.place = place;
    }

    /**
     * Returns {@code String} representing when the manuscript was received
     *
     * @return {@code String} representing when the manuscript was received
     */
    public String getReceived()
    {
	return received;
    }

    /**
     * Sets {@code date} representing when the manuscript was received
     *
     * @param date {@code String} representing when the manuscript was received
     */
    public void setReceived(String date)
    {
	if(entity != null)
	    getEntity().setReceived(date);

	if(date == received)
	    return;
	received = date;
    }

    /**
     * Returns {@code String} representing when the manuscript was revised
     *
     * @return {@code String} representing when the manuscript was revised
     */
    public String getRevised()
    {
	if(entity != null)
	    return getEntity().getReceived();
	return revised;
    }

    /**
     * Sets {@code date} representing when the manuscript was reviseed
     *
     * @param date {@code String} representing when the manuscript was revised
     */
    public void setRevised(String date)
    {
	if(entity != null)
	    getEntity().setReceived(date);

	if(revised == date)
	    return;

	revised = date;
    }

    /**
     * Returns {@code String} representing when the manuscript was accepted for publication
     *
     * @return {@code String} representing when the manuscript was accepted for publication
     */
    public String getAccepted()
    {
	if(entity != null)
	    return getEntity().getAccepted();
	return accepted;
    }

    /**
     * Sets {@code date} representing when the manuscript was accepted for publication
     *
     * @param date {@code String} representing when the manuscript was accepted for publication
     */
    public void setAccepted(String date)
    {
	if(entity != null)
	    getEntity().setAccepted(date);

	if(accepted == date)
	    return;
	accepted = date;
    }

    /**
     * Returns {@code String} representing when the publication was published
     *
     * @return {@code String} representing when the publication was published
     */
    public String getPublished()
    {
	if(entity != null)
	    return getEntity().getPublished();
	return published;
    }

    /**
     * Sets {@code date} representing when the publication was published
     *
     * @param date {@code String} representing when the publication was published
     */
    public void setPublished(String date)
    {
	if(entity != null)
	    getEntity().setPublished(date);

	if(published == date)
	    return;
	published = date;
    }

    public String getCanonicalAuthorYearCitation()
    {
	if(entity != null)
	    return getEntity().getCanonicalAuthorYearCitation();

	return getCanonicalAuthorYearCitation(new StringBuffer()).toString();
    }

    public String getCanonicalAuthorYearCitation(StringBuffer buffer)
    {
	if(entity != null)
	    return getEntity().getCanonicalAuthorYearCitation(buffer);

	if (buffer == null)
	    buffer =  
		getCanonicalAuthorName(new StringBuffer());
	String yr = getYear();
	if(yr != null && yr.length() > 0) {
	    if(buffer.length() > 0)
		buffer.append(' ');
	    buffer.append('(').append(yr).append(") ");
	}
	if(buffer.length() > 0)
	    return getCanonicalCitation(buffer).toString();

	else
	    return "";
    }

    /**
     * Returns canonical representation of authors' name
     *
     * @return aurthors' name
     */
    public String getCanonicalAuthorName()
    {
	if(entity != null)
	    return getEntity().getCanonicalAuthorName();

	return getCanonicalAuthorName(new StringBuffer()).toString();
    }

    /**
     * Returns canonical representation of authors' name
     *
     * @return aurthors' name
     */
    @SuppressWarnings({"fallthrough"})
    public StringBuffer getCanonicalAuthorName(StringBuffer buffer)
    {
	if(entity != null)
	    return getEntity().getCanonicalAuthorName(buffer);

	if(authors == null)
	    return null;

	if(buffer == null)
	    buffer = new StringBuffer();
	int index = 0;
	switch(getAuthorCount()) {
	case 2:
	    buffer.append(authors[index].getSurname());
	    buffer.append(" and ");
	    index++;
	case 1:
	    buffer.append(authors[index].getSurname());
	    break;
	default:
	    buffer.append(authors[index].getSurname());
	    buffer.append("et al.");
	    break;
	}

	return buffer;
    }

    /**
     * Returns number of authors of this {@code Pulbication}
     *
     * @return number of authors
     */
    public int getAuthorCount()
    {
	if(entity != null)
	    return getEntity().getAuthorCount();

	if(authors == null)
	    return -1;
	return authors.length;
    }

    /**
     * Returns {@code Enumeration} of {@code Author}s
     * or null if none
     *
     * @return Enumeration of {@code Author}s
     * or null if none
     */
    public Author[] getAuthors()
    {
	if(entity != null)
	    return getEntity().getAuthors();

	return ArrayUtility.copy(authors);
    }
    
    /**
     * Sets {@code authors} as {@code Author}s list of this publication
     *
     * @param authors {@code Vector} representing {@code Author}s list of this publication
     */
    public void setAuthors(Collection<Author> authors)
    {
	if(entity != null) {
	    getEntity().setAuthors(authors);
	    return;
	}
	
	if(authors == null) {
	    if(this.authors != null) {
		Author[] tmp = this.authors;
		ArrayUtility.clear(this.authors);
		for(int i = 0; i < tmp.length; i++)
		    tmp[i].removePublication(this);
		this.authors = null;
	    }
	    return;
	}

	if(this.authors == null) {
	    this.authors = new Author[authors.size()];
	}
	else {
	    Author[] tmp = this.authors;
	    ArrayUtility.clear(this.authors);
	    for(int i = 0; i < tmp.length; i++)
		tmp[i].removePublication(this);
	    if(this.authors.length != authors.size())
		this.authors = new Author[authors.size()];
	}
	authors.toArray(this.authors);
    }

    /**
     * Adds {@code author} to the author list
     *
     * @param author {@code Author} to be added to the author list
     */
    public boolean addAuthor(Author author)
    {
	if(entity != null)
	    return getEntity().addAuthor(author);

	Author[] added = ArrayUtility.add(author, authors);
	if(authors != added) {
	    ArrayUtility.clear(authors);
	    authors = added;
	    author.addPublication(this);
	    return true;
	}

	return false;
    }

    /**
     * Removes {@code author} from the author list
     *
     * @param author {@code Author} to be removed form the author list
     */
    public void removeAuthor(Agent agent)
    {
	if(entity != null) {
	    getEntity().removeAuthor(agent);
	    return;
	}
    }

    /**
     * Removes {@code author} from the author list
     *
     * @param author {@code Author} to be removed form the author list
     */
    public void removeAuthor(Author author)
    {
	if(entity != null) {
	    getEntity().removeAuthor(author);
	    return;
	}

	if(authors == null)
	    return;

	Author[] removed = ArrayUtility.remove(author, authors);
	if(removed != authors) {
	    ArrayUtility.clear(authors);
	    authors = removed;
	    author.removePublication(this);
	}
    }

    /**
     * Returns {@code Enumeration} of contents
     * or null if none
     *
     * @return Enumeration of contents
     * or null if none
     */
    public Publication[] getContents()
    {
	if(entity != null)
	    return getEntity().getContents();

	return ArrayUtility.copy(contents);
    }

    /**
     * Sets {@code contents} as {@code Vector} representing table of contents
     *
     * @param contents {@code Vector} representing table of contents
     */
    public void setContents(Collection<Publication> contents)
    {
	if(entity != null) {
	    getEntity().setContents(contents);
	    return;
	}

	if(this.contents != null)
	    ArrayUtility.clear(this.contents);

	this.contents = new Publication[contents.size()];
	contents.toArray(this.contents);
    }

    /**
     * Adds {@code publication} to the table of contents
     *
     * @param publication {@code Publication} to be added to the table of contents
     */
    public boolean addContents(Publication publication)
    {
	if(entity != null)
	    return getEntity().addContents(publication);

	if(publication == null)
	   return false;

	if(ArrayUtility.contains(publication, contents))
	   return false;
	
	Publication[] added = ArrayUtility.add(publication, contents);
	if(added != contents) {
	    contents = added;
	    if(contents != null)
	    publication.setContainer(this);
	    return true;
	}
	return false;

    }

    /**
     * Removes {@code publication} from the table of contents
     *
     * @param publication {@code Publication} to be removed from the table of contents
     */
    public void removeContents(Publication publication)
    {
	if(entity != null) {
	    getEntity().removeContents(publication);
	    return;
	}

	if(contents == null ||
	   publication == null)
	    return;

	Publication[] tmp = ArrayUtility.remove(publication, contents);
	if(tmp != contents) {
	    publication.setContainer(null);
	    ArrayUtility.clear(contents);
	    contents = tmp;
	}
    }

    /**
     * Returns {@code Enumeration} of appearances
     * or null if none
     *
     * @return Enumeration of appearances
     * or null if none
     */
    public Appearance[] getAppearances()
    {
	if(entity != null)
	    return getEntity().getAppearances();

	return ArrayUtility.copy(appearances);
    }

    /**
     * Sets {@code appearances} as {@code Vector} representing list of {@code Appearance}s
     *
     * @param appearances {@code Vector} representing table of {@code Appearance}s
     */
    public void setAppearances(Collection<Appearance> appearances)
    {
	if(entity != null) {
	    getEntity().setAppearances(appearances);
	    return;
	}

	if(this.appearances != null) {
	    Appearance[] tmp = this.appearances;
	    this.appearances = null;
	    for(int i = 0; i < tmp.length; i++)
		tmp[i].setPublication(null);
	    ArrayUtility.clear(tmp);
	}

	if(appearances == null)
	    return;

	this.appearances = new Appearance[appearances.size()];
	appearances.toArray(this.appearances);
	for(int i = 0; i < this.appearances.length; i++)
	    this.appearances[i].setPublication(this);
    }

    /**
     * Adds {@code appearance} to the list of {@code Appearance}s
     *
     * @param appearance {@code Appearance} to be added to the list of {@code Appearance}s
     */
    public void addAppearance(Appearance appearance)
    {
	if(entity != null) {
	    getEntity().addAppearance(appearance);
	    return;
	}

	if(appearance == null)
	    return;

	Appearance[] tmp = ArrayUtility.add(appearance, appearances);
	if(tmp != appearances) {
	    ArrayUtility.clear(appearances);
	    appearances = tmp;
	    appearance.setPublication(this);
	}
    }

    /**
     * Removes {@code appearance} from the list of {@code Appearance}s
     *
     * @param appearance {@code Appearance} to be removed from the list of {@code Appearance}s
     */
    public void removeAppearance(Appearance appearance)
    {
	if(entity != null)
	    getEntity().removeAppearance(appearance);

	if(appearances == null ||
	   appearance == null)
	    return;

	Appearance[] tmp = ArrayUtility.remove(appearance, appearances);
	if(tmp != appearances) {
	    appearance.setPublication(null);
	    ArrayUtility.clear(appearances);
	    appearances = tmp;
	}
    }

    /*
    public String getAuthorListSummary()
    {
	if(entity != null)
	    return getEntity().getAuthorListSummary();

	return getAuthorListSummary(authors);
    }

    public static String getAuthorListSummary(Vector authors)
    {
	StringBuffer buffer = new StringBuffer();
	
	if(authors != null && authors.size() > 0) {
	    buffer.append(((Author)authors.elementAt(0)).getFullname(Author.ABBREVIATE));
	    switch(authors.size()) {
	    case 1:
		break;
	    case 2:
		buffer.append(" & ");
		buffer.append(((Author)authors.elementAt(1)).getFullname(Author.ABBREVIATE));
		break;
	    default:
		buffer.append(" et al.");
	    }
	}

	return buffer.toString();
    }
    */
	
    /**
     * create XML String 
     *
     * @return XML String of this {@code Publcation} object
     */
    public String toXML()
    {
	if(entity != null)
	    return getEntity().toXML();

	StringBuffer buf = new StringBuffer();
	buf.append("<Publication>\n");
	buf.append("<oid>").append(getPersistentID()).append("</oid>\n");
	if(authorNames != null)
	    buf.append("<authors>").append(authorNames).append("</authors>\n");
	String s = getAffiliations();
	if(s != null)
	    buf.append("<affiliation>").append(s).append("</affiliation>\n");
        if(authors != null) {
	    for (int i=0; i < authors.length; i++) {
		buf.append("<author id=\"").append(i).append("\">").append(authors[i].getPersistentID()).append("</author>\n");
	    }
	}

	s = getCitationTitle();
	if(s != null)
	    buf.append("<citationTitle>").append(s).append("</citationTitle>\n");

	s = getContentsTitle();
	if(s != null)
	    buf.append("<contentsTitle>").append(s).append("</contentsTitle>\n");

	s = getDOI();
	if(s != null)
	    buf.append("<doi>").append(s).append("</doi>\n");

	s = getISXN();
	if(s != null)
	    buf.append("<isxn>").append(s).append("</isxn>\n");

	s = getYear();
	if(s != null)
	    buf.append("<year>").append(s).append("</year>\n");

	s = getVolume();
	if(s != null)
	    buf.append("<volume>").append(s).append("</volume>\n");

	s = getIssue();
	if(s != null)
	    buf.append("<issue>").append(s).append("</issue>\n");

	s = getFirstPage();
	if(s != null)
	    buf.append("<startpage>").append(s).append("</startpage>\n");

	s = getLastPage();
	if(s != null)
	    buf.append("<lastpage>").append(s).append("</lastpage>\n");

	s = getNotes();
	if(s != null)
	    buf.append("<notes>").append(s).append("</notes>\n");

	if(getReceived() != null)
	    buf.append("<received>").append(getReceived()).append("</received>\n");

	if(getRevised() != null)
	    buf.append("<revised>").append(getRevised()).append("</revised>\n");

	if(getAccepted() != null)
	    buf.append("<accepted>").append(getAccepted()).append("</accepted>\n");

	if(getPublished() != null)
	    buf.append("<published>").append(getPublished()).append("</published>\n");

	s = getPublisher();
	if(s != null)
	    buf.append("<publisher>").append(s).append("</publisher>\n");

	s = getPlace();
	if(s != null)
	    buf.append("<place>").append(s).append("</place>\n");

	//if(s != null)
	// buf.append("<partof>").append(getPartOf()).append("</partof>\n");
	// buf.append("<parts>").append(getParts()).append("</parts>\n");

        if(appearances != null) {
	    for(int i = 0; i < appearances.length; i++) {
    	        buf.append("<appearance>").append(appearances[i].getPersistentID()).append("</appearance>\n");
	    }
	}
	buf.append("</Publication>\n");
	
        return buf.toString();
    }
    
    /**
     * create XML String of the all Related NamedObject
     *
     * @return XML String of this {@code Publication} object and required objects for PublicationPanel
     */
    public String toRelevantXML()
    {
	if(entity != null)
	    return getEntity().toRelevantXML();

	// create XML String of the Publication itself
	StringBuffer buf = new StringBuffer();
	buf.append(toXML());
	
	// create XML of the Authors
	if(authors != null) {
            for(int i = 0; i < authors.length; i++) {
    	        buf.append(authors[i].toXML());
	    }
    	}

	// create XML of the Appearances
	if(appearances != null) {
	    for(int i = 0; i < appearances.length; i++) {
    	        buf.append(appearances[i].toXML());
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

    public String getViewName()
    {
	if(entity != null)
	    return getEntity().getViewName();

	StringBuffer buffer = new StringBuffer();
	getViewName(buffer);
	return buffer.toString();
    }

    public boolean getViewName(StringBuffer buffer)
    {
	if(entity != null)
	    return getEntity().getViewName(buffer);

	boolean appended = getAuthorsViewName(buffer);

	/*
	String viewYear = null;
	if(year != null && year.length() != 0)
	    viewYear = year;
	else {
	    viewYear = getPersistentIDComponent(YEAR);
	}
	*/
	String viewYear = getYear();

	if(viewYear != null && viewYear.length() != 0) {
	    if(appended)
		buffer.append(' ');
	    buffer.append(viewYear);
	    appended = true;
	}

	return appended;
    }

    public String getAuthorsViewName()
    {
	if(entity != null)
	    return getEntity().getAuthorsViewName();
	StringBuffer buffer = new StringBuffer();
	getAuthorsViewName(buffer);
	return buffer.toString();
    }

    public boolean getAuthorsViewName(StringBuffer buffer)
    {
	if(entity != null)
	    return getEntity().getAuthorsViewName(buffer);

	if(isNominal()) {
	    buffer.append(getPersistentIDComponent(AUTHOR));
	    return true;
	}

	boolean appended = false;

	if(authors != null &&
	   authors.length > 0) {
	    switch(authors.length) {
	    case 1:
		authors[0].getAuthorViewName(buffer);
		break;
	    case 2:
		authors[0].getAuthorViewName(buffer);
		buffer.append(" & ");
		authors[1].getAuthorViewName(buffer);
		break;
	    default:
		authors[0].getAuthorViewName(buffer);
		buffer.append(" et al.");
		break;
	    }
	    appended = true;
	}
	else if(authorNames != null && authorNames.length() != 0) {
	    buffer.append(authorNames);
	    appended = true;
	}

	return appended;
    }

    /**
     * Sets a {@code object} as the entity of the name
     *
     * @param object representing the entity
     */
    /*
    public void setEntity(Object object)
    {
	if(object == this || 
	   (object != null && !(object instanceof Publication)))
	    throw new IllegalArgumentException(object.getClass().getName() + " can't be an entity of " + getClass().getName());
	entity = (Publication)object;
	super.setEntity(object);
    }
    */

    /**
     * Returns a summarized expression of this {@code NamedObject}
     *
     * @return String representing summary of this {@code NamedObject}
     */
    public String getSummary()
    {
	if(entity != null)
	    return getEntity().getSummary();

	return getCanonicalAuthorYearCitation();
    }

}
