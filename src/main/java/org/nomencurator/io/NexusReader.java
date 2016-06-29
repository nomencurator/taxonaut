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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.nomencurator.model.NameUsage;

/**
 * <CODE>NexusReader</CODE> reads and extract trees from
 * a NEXUS format file
 *
 * @see Syst. Biol. 46(4):590-621, 1997
 * @see org.nomencurator.model.NameUsage
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NexusReader
    extends NewickReader
{
    public static String NEXUS_PREFIX = "#NEXUS";
    public static String NEXUS_BLOCK_END = "end;";
    public static String NEXUS_TAXA_BLOCK = "begin taxa;";
    public static String NEXUS_TAXA_LABEL = "taxlabels;";
    public static String NEXUS_TRANSLATE = "translate";
    public static String NEXUS_TREES_BLOCK = "begin trees;";
    public static String NEXUS_TREE = "tree";
    public static String NEXUS_TOKEN_DELIMITER = "\t\n\r\f;,";

    protected Map<String, String> translator;

    protected Map<NameUsage<?, ?>, String> treeNames;

    protected boolean inTreeBlock;

    protected String lowerCaseLine;

    public NameUsage<?, ?>[] parseTrees(File file)
	throws FileNotFoundException, IOException
    {
	if(translator != null)
	    translator.clear();

	inTreeBlock = false;

	return super.parseTrees(file);
    }

    protected void provideTranslator() 
    {
	if(translator == null)
	    translator = new HashMap<String, String>();
    }

    protected void provideTreeNames() 
    {
	if(treeNames == null) 
	    treeNames = new HashMap<NameUsage<?, ?>, String>();
    }


    protected void skipLines()
	throws IOException
    {
	readLine();
	if(line == null)
	    return;
	if(!line.startsWith(NEXUS_PREFIX)) {
	    line = null;
	    throw new IOException("Not NEXUS format");
	}

	while(line != null && !lowerCaseLine.startsWith(NEXUS_TREES_BLOCK)) {
	    if(lowerCaseLine.startsWith(NEXUS_TAXA_BLOCK)) {
		readLine();
		while(!lowerCaseLine.startsWith(NEXUS_TAXA_LABEL) 
		      && !lowerCaseLine.startsWith(NEXUS_BLOCK_END)) {
		    readLine();
		}
		if(lowerCaseLine.startsWith(NEXUS_TAXA_LABEL)) {
		    provideTranslator();
		    int index = 0;
		    while(!lowerCaseLine.startsWith(NEXUS_BLOCK_END)) {
			StringTokenizer tokens =
			    new StringTokenizer(line, NEXUS_TOKEN_DELIMITER);
			if(index == 0) {
			    tokens.nextToken();
			    index++;
			}
			while(tokens.hasMoreTokens()) {
			    String token = tokens.nextToken();
			    if(token.length() > 0) {
				translator.put(String.valueOf(index), token);
			    }
			}
			readLine();
		    }
		}
	    } // end of NEXUS_TAXA_BLOCK
	    readLine();
	}
	if(line == null)
	    return;
	inTreeBlock = true;
	while(line != null && !lowerCaseLine.startsWith(NEXUS_BLOCK_END) &&
	      !lowerCaseLine.startsWith(NEXUS_TREE)) {
	    if(lowerCaseLine.startsWith(NEXUS_TRANSLATE)) {
		provideTranslator();
		boolean isTranslate = true;
		while(isTranslate) {
		    StringTokenizer tokens =
			new StringTokenizer(line);
			//			new StringTokenizer(line, NEXUS_TOKEN_DELIMITER);
		    while(tokens.hasMoreTokens()) {
			String key = tokens.nextToken();
			if(key.length() > 0 && tokens.hasMoreTokens()) {
			    String value = tokens.nextToken();
			    int end = value.indexOf(";");
			    if(end > -1)
				value = value.substring(0, end);
			    end = value.indexOf(",");
			    if(end > -1)
				value = value.substring(0, end);
			    translator.put(key, value);
			}
		    }
		    if(line.indexOf(";") != -1)
			isTranslate = false;
		    if(isTranslate)
			readLine();
		}
	    }
	    readLine();
	}
    }

    protected void readLine()
	throws IOException
    {
	super.readLine();
	if(line != null)
	    lowerCaseLine = line.toLowerCase();
	else
	    lowerCaseLine = null;
    }

    protected String getNodeName(String key)
    {
	String name = null;
	if(translator != null)
	    name = translator.get(key);
	if(name == null)
	    name = key;
	return name;
    }

    protected boolean hasMoreTrees()
	throws IOException
    {
	if(!inTreeBlock)
	    return false;

	index = 0;
	line = reader.readLine();
	if(line == null)
	    return false;

	line = line.trim();
	if(lowerCaseLine.startsWith(NEXUS_BLOCK_END))
	    inTreeBlock = false;

	return inTreeBlock;
    }


    protected NameUsage<?, ?> parseTreeLine()
	throws IOException
    {
	NameUsage<?, ?> root = null;
	/*
	while(line != null && !lowerCaseLine.startsWith(NEXUS_BLOCK_END)
	      && !lowerCaseLine.startsWith(NEXUS_TREE)) {
	    if(lowerCaseLine.startsWith(NEXUS_BLOCK_END))
		inTreeBlock = false;
	*/
	    if(lowerCaseLine.startsWith(NEXUS_TREE)) {
		index = NEXUS_TREE.length() + 1;
		int lineEnd = line.length();
		skipWhitespace();
		if(index == lineEnd)
		    return null;
		
		int end = index;
		while(end < lineEnd && line.charAt(end) != '=')
		    end++;
		
		if(end >= lineEnd)
		    return null;
		String treeName = line.substring(index, end);
		index = end + 1;
		skipWhitespace();
		if(line.charAt(index) == '[') {
		    while(index < lineEnd && line.charAt(index) != ']')
			index++;
		    if(index == lineEnd)
			return null;
		    else
			index++;
		    skipWhitespace();
		}

		root = super.parseTreeLine();
		setTreeName(root, treeName);
	    }
	    /*
	    readLine();
	}
	    */
	return root;
    }

    protected void skipWhitespace()
    {
	int lineEnd = line.length();
	while(index < lineEnd && Character.isWhitespace(line.charAt(index)))
	    index++;
    }
	

    public void setTreeName(NameUsage<?, ?> root, String name)
    {
	if(root == null || name == null)
	    return;

	provideTreeNames();
	treeNames.put(root, name);
    }

    public String getTreeName(NameUsage<?, ?> root)
    {
	if(root == null || treeNames == null)
	    return null;

	return treeNames.get(root);
    }

}
