/*
 * ISIReader.java: read a ISI export format file
 *
 * Copyright (c) 2006, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nomencurator.model.Agent;
import org.nomencurator.model.Publication;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.DefaultNameUsageNode;
import org.nomencurator.model.DefaultNameUsage;

/**
 * {@code ISIReader} reads and extract relevant data
 * a ISI format file
 *
 * @see <A HREF="http://isibasic.com/help/helpprn.html#exporting">http://isibasic.com/help/helpprn.html#exporting</A>
 * @see org.nomencurator.model.Publication
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class ISIReader
{
    /** {@code BufferedReader} to read a file */
    protected BufferedReader reader;

    /** A line of the file under parsing */
    protected String line;

    /** Index to a character in the {@code line} under parsing */
    protected int index;

    /* ISI field tags */
    /*
FN 	File type
	VR 	File format version number
	PT 	Publication type (e.g., book, journal, book in series)
	AU 	Author(s)
	TI 	Article title
	DE 	Author keywords
	ID 	KeyWords Plus
	AB 	Abstract
	RP 	Reprint address
	CI 	Research addresses
	EM 	Authors' Internet e-mail address(es)
	TC 	Times cited
	NR 	Cited reference count
	CR 	Cited references
	CP 	Cited patent
	BP 	Beginning page
	EP 	Ending page
	PG 	Page count
	DT 	Document type
	LA 	Language
	SN 	ISSN
	SO 	Full source title
	J9 	29-character source title abbreviation
	JI 	ISO source title abbreviation
	SE 	Book series title
	BS 	Book series subtitle
	PY 	Publication year
	PD 	Publication date
	VL 	Volume
	IS 	Issue
	PN 	Part number
	SU 	Supplement
	SI 	Special issue
	GA 	ISI document delivery number
	PU 	Publisher
	PI 	Publisher city
	PA 	Publisher address
	WP 	Publisher web address
	ER 	End of record
    */
    public static String FILE_TYPE = "FN";
    public static String VERSION = "VR";
    public static String PUBLICATION_TYPE = "PT";
    public static String AUTHORS = "AU";
    public static String ARTICLE_TITLE = "TI";
    public static String AUTHOR_KEYWORDS = "DE";
    public static String KEYWORD_PLUS = "ID";
    public static String ABSTRACT = "AB";
    public static String REPRINT_ADDRESS = "RP";
    public static String RESEARCH_ADDRESSES = "CI";	
    public static String AUTHORS_E_MAIL_ADDRESSES = "EM";
    public static String TIMES_CITED = "TC";
    public static String CITED_REFERENCE_COUNT = "NR";
    public static String CITED_REFERENCES = "CR";
    public static String CITED_PATENT = "CP";
    public static String BEGINNING_PAGE = "BP";
    public static String ENDING_PAGE = "EP";
    public static String PAGE_COUNT = "PG";
    public static String DOCUMENT_TYPE = "DT";
    public static String LANGUAGE = "LA";
    public static String ISSN = "SN";
    public static String FULL_SOURCE_TITLE = "SO";

    /** 29-character source title abbreviation */
    public static String SOURCE_TIETLE_ABBREV_29 = "J9";

    /** ISO source title abbreviation */
    public static String ISO_SOURCE_TITLE_ABBREV = "JI";
    
    /** Book series title */
    public static String BOOK_SERIES_TITLE = "SE";
    /*
public static String BS 	Book series subtitle
public static String PY 	Publication year
public static String PD 	Publication date
public static String VL 	Volume
public static String IS 	Issue
public static String PN 	Part number
public static String SU 	Supplement
public static String SI 	Special issue
public static String GA 	ISI document delivery number
public static String PU 	Publisher
public static String PI 	Publisher city
public static String PA 	Publisher address
public static String WP 	Publisher web address
public static String ER 	End of record
    */
    /** Constructs a {@code NewickReader} */
    public ISIReader()
    {
    }

    /**
     * Opens and parses {@code file} to extract tree(s)
     *
     * @param file {@code File} to be opened and parsed
     * @return an array of tree(s) extracted, or null if
     * no tree is contained in the {@code file}
     * @exception FileNotFoundException
     * @exception IOException
     */
    public Object[][] parse(File file)
	throws FileNotFoundException, IOException
    {
	reader = new BufferedReader(new FileReader(file));
	readLine();
	if(line == null)
	    return null;

	List<Publication> publications = new ArrayList<>();
	List<Agent> authors = new ArrayList<>();
	List<Object> institutions = new ArrayList<>();
	List<NameUsage<?>> nameUsages = new ArrayList<>();
	List<Object> relationships = new ArrayList<>();

	//	if(getField()[0][0].equals;
	//analyse();

	int size = 0;
	if(!relationships.isEmpty())
	    size = 5;
	else if(!nameUsages.isEmpty())
	    size = 4;
	else if(!institutions.isEmpty())
	    size = 3;
	else if(!authors.isEmpty())
	    size = 2;
	else if(!publications.isEmpty())
	    size = 1;

	if(size == 0) {
	    publications.clear();
	    authors.clear();
	    institutions.clear();
	    nameUsages.clear();
	    relationships.clear();
	    return new Object[0][0];
	}

	Object[][] objects = new Object[size][];

	size = 4;
	if(!relationships.isEmpty()) {
	    objects[size] = relationships.toArray();
	    relationships.clear();
	}
	else
	    objects[size] = new Object[0];

	size--;

	if(!nameUsages.isEmpty()) {
	    objects[size] = nameUsages.toArray();
	    nameUsages.clear();
	}
	else
	    objects[size] = new Object[0];

	size--;

	if(!institutions.isEmpty()) {
	    objects[size] = institutions.toArray();
	    institutions.clear();
	}
	else
	    objects[size] = new Object[0];

	size--;

	if(!authors.isEmpty()) {
	    objects[size] = authors.toArray();
	    authors.clear();
	}
	else
	    objects[size] = new Object[0];

	size--;

	if(!publications.isEmpty()) {
	    objects[size] = publications.toArray();
	    publications.clear();
	}
	else
	    objects[size] = new Object[0];

	return objects;
    }

    protected void skipBlankLine()
	throws IOException
    {
	do {
	    readLine();
	}
	while(line != null && line.length() == 0);
    }

    protected void readLine()
	throws IOException
    {
	index = 0;
	line = reader.readLine();
	if(line != null)
	    line = line.trim();
    }

    protected String[][] getField()
	throws IOException
    {
	String fieldName = line.substring(0, 2);
	if(fieldName.equals("AU"))
	   return getAuthors(fieldName);

	StringBuffer buffer = new StringBuffer(line.substring(3));

	reader.readLine();
	while(line.charAt(0) == ' ') {
	    buffer.append(line.substring(3));
	    reader.readLine();
	}

	return new String[][]{{fieldName}, {buffer.toString()}};
	   
    }

    protected String[][] getAuthors(String fieldName)
	throws IOException
    {
	List<String> authorNames = new ArrayList<>();

	authorNames.add(line.substring(3));

	reader.readLine();
	while(line.charAt(0) == ' ') {
	    authorNames.add(line.substring(3));
	    reader.readLine();
	}

	int authorCount = authorNames.size();
	String[] authors = new String[authorCount];
	for (int i = 0; i < authorCount; i++)
	    authors[i] = authorNames.get(i);

	authorNames.clear();
	return new String[][]{{fieldName}, authors};
    }


    //test code
    /*
    public static void main(String[] args)
    {
	NewickReader reader = new NewickReader();
	NameUsage[] roots = null;
	try{
	    roots = reader.parseTrees(new File(args[0]));
	}
	catch(Exception e) {
	    e.printStackTrace();
	}

	org.nomencurator.swing.NameTree tree = new org.nomencurator.swing.NameTree(roots[0]);

	javax.swing.JFrame frame = new javax.swing.JFrame("TreeTest");
	frame.getContentPane().add(tree);
	frame.show();

    }
    */
}
