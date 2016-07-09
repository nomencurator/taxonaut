/*
 * NewickReader.java:  read a NEWICK format file
 *
 * Copyright (c) 2005, 2015, 2016 Nozomi `James' Ytow
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
import java.util.Collection;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.DefaultNameUsageNode;
import org.nomencurator.model.DefaultNameUsage;

/**
 * {@code NewickReader} reads and extract trees from
 * a NEWICK format file
 *
 * @see <A HREF="http://evolution.genetics.washington.edu/phylip/newicktree.html">http://evolution.genetics.washington.edu/phylip/newicktree.html</A>
 * @see org.nomencurator.model.NameUsage
 *
 * @version 	09 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class NewickReader
{
    /** {@code BufferedReader} to read a file */
    protected BufferedReader reader;

    /** A line of the file under parsing */
    protected String line;

    /** Index to a character in the {@code line} under parsing */
    protected int index;

    /** Constructs a {@code NewickReader} */
    public NewickReader()
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
    public NameUsage<?>[] parseTrees(File file)
	throws FileNotFoundException, IOException
    {
	reader = new BufferedReader(new FileReader(file));
	skipLines();
	if(line == null)
	    return null;

	Collection<NameUsage<?>> roots = parseTrees();

	if(roots.isEmpty())
	    return null;

	NameUsage<?>[] nodes = roots.toArray(new NameUsage<?>[roots.size()]);
	roots.clear();
	return nodes;
    }

    protected Collection<NameUsage<?>> parseTrees()
	throws IOException
    {
	Collection<NameUsage<?>> roots = new ArrayList<NameUsage<?>>();
	do {
	    NameUsage<?> root = parseTreeLine();
	    if(root != null)
	    roots.add(root);
	} while(hasMoreTrees());

	return roots;
    }

    protected void skipLines()
	throws IOException
    {
	readLine();
    }

    protected void readLine()
	throws IOException
    {
	index = 0;
	line = reader.readLine();
	if(line != null)
	    line = line.trim();
    }

    protected String getNodeName(String key)
    {
	return key;
    }

    protected boolean hasMoreTrees()
	throws IOException
    {
	index = 0;
	line = reader.readLine();
	if(line != null)
	    line = line.trim();
	return (line != null);
    }

    protected NameUsage<?> parseTreeLine()
	throws IOException
    {
	return parseSubTreeLine(null);
    }

    @SuppressWarnings("unchecked")
    protected NameUsage<?> parseSubTreeLine(NameUsage<?> parent)
	throws IOException
    {
	NameUsage<?> current = null;
	while(line != null &&
	      index < line.length() &&
	      line.charAt(index) != ';') {
	    if(line.charAt(index) == '(') {
		index++;
		current = parent.create();
		current.setHigherNameUsage(current.getClass().cast(parent));

		if(index == line.length()) {
		    line = reader.readLine().trim();
		    index = 0;
		}

		if(index < line.length()) {
		    parseSubTreeLine(current);

		    if(index < line.length()) {
			if(line.charAt(index) == ')' ||
			   line.charAt(index) == ',' ||
			   line.charAt(index) == ';'){
			    // current.setLiteral("(unnamed)");
			}
			else {
			    parseNodeToken(current);
			}
		    }
		}
	    }
	    else if(line.charAt(index) == ')') {
		index++;
		return current;
	    }
	    else if(line.charAt(index) == ','){
		//current = null;
		index++;
	    }
	    else {
		int i = index;
		NameUsage<?> toParse = parent.create();
		parseNodeToken(toParse).setHigherNameUsage(toParse.getClass().cast(parent));
	    }
	}
	return current;
    }

    protected NameUsage<?> parseNodeToken(NameUsage<?> node)
    {
	int comma = line.indexOf(",", index);
	int last = line.indexOf(")", index);
	if(last == -1)
	    last = line.indexOf(";", index);
	if(last == -1 && comma == -1)
	    return node;

	if(comma > -1 &&
	   (last == -1 ||
	    comma < last))
	    last = comma;
	String token = line.substring(index, last);

	int colon = token.indexOf(":");
	String name = null;
	String value = null;
	int length = token.length();
	if(colon == -1)
	    colon = length;
	if(colon > 0) {
	    name = token.substring(0, colon);
	    if(colon < length)
		value = token.substring(colon + 1);
	}
	else {
	    value = token.substring(1);
	}

	if(name == null || name.length() == 0)
	    //name = "unnamed ";
	    name = "";
	name = getNodeName(name);
	node.setLiteral(name);
	if(value != null)
	    node.setNotes(value);

	NameUsage<?> p = (NameUsage<?>)node.getHigherNameUsage();
	index = last;

	return node;
    }

    //test routine
    /*
    public static void main(String[] args)
    {
	NewickReader reader = new NewickReader();
	NameUsage<?>[] roots = null;
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
