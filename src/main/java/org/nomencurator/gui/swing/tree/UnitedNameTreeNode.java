/*
 * UnitedNameTreeNode.java:  a MutableTreeNode as node in UnitedNameTreeModels
 *
 * Copyright (c) 2003, 2005, 2005, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.nomencurator.model.NameUsage;

/**
 * {@code UnitedNameTreeNode} wrapping a {@code NameUsage}
 *
 * @version 	02 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class UnitedNameTreeNode
    extends AbstractNameTreeNode<UnitedNameTreeNode>
{
    private static final long serialVersionUID = 1555933091687612569L;

    /** {@code List} of parents if it has multiple parents,
	i.e. there is an inconsistency on this node */
    protected ArrayList<UnitedNameTreeNode> parents;

    protected ArrayList<NameTreeNode> mappedNodes;

    public UnitedNameTreeNode(NameUsage<?> name)
    {
	super();
	setRankedName(name.getRankedName(true));
	setLiteral(name.getLiteral());
    }

    public UnitedNameTreeNode(NameTreeNode name)
    {
	super();
	setRankedName(name.getRankedName(true));
	setLiteral(name.getLiteral());
    }

    public UnitedNameTreeNode(String name)
    {
	super();
	setRankedName(name);
    }

    public UnitedNameTreeNode(String rank, String name)
    {
	super();
	setName(rank, name);
    }

    public void trim()
    {
	super.trim();
	if(parents != null)
	    parents.trimToSize();
	if(mappedNodes != null)
	    mappedNodes.trimToSize();
    }

    public void insert(MutableTreeNode newChild, int childIndex)
    {
	if(newChild == null)
	    return;

	if(mappedNodes == null) {
	    mappedNodes = createNodeMap();
	}
	else if(mappedNodes.indexOf(newChild) != -1)
	    return;

	super.insert(newChild, childIndex);

	if (newChild instanceof NameTreeNode)
	    mappedNodes.add((NameTreeNode)newChild);
    }


    /*
    public void add(NameTreeNode node)
    {
	super.add(node);
    }
    */

    public void remove(NameTreeNode node)
    {
	if(node == null ||
	   mappedNodes == null ||
	   mappedNodes.indexOf(node) == -1)
	    return;

	super.remove(node);

	mappedNodes.remove(node);
    }

    protected ArrayList<NameTreeNode> createNodeMap()
    {
	return new ArrayList<NameTreeNode>(1);
    }

    /**
     * Returns true if there are inconsistency between
     * mapped nodes
     */
    public boolean isConsistent()
    {
	//?
	return (parent != null && !parents.isEmpty());
    }

    public Iterator<?> getParents()
    {
	if(parents != null)
	    return parents.iterator();

	if(parent == null)
	    return Collections.emptyIterator();

	return new Iterator<TreeNode>() {
	    boolean remains = true;

	    public boolean hasNext() {
		return remains;
	    }

	    public TreeNode next() {
		synchronized (parent) {
		    if(remains) {
			remains = false;
			return parent;
		    }
		}
		throw new NoSuchElementException("No more parent");
	    }
	};
    }

    public void addParent(UnitedNameTreeNode parent)
    {
	if(parents == null)
	    parents = new ArrayList<UnitedNameTreeNode>();
	parents.add(parent);
    }

    public void removeParent(UnitedNameTreeNode parent)
    {
	if(parents != null) {
	    parents.remove(parent);
	    if(parents.isEmpty())
		parents = null;
	}
    }

    public TreeNode[][] getPaths()
    {
	TreeNode[][] paths;
	if(parents != null) {
	    synchronized(parents) {
		paths =
		    new TreeNode[parents.size()][];
		int i = 0;
		for(UnitedNameTreeNode node: parents) {
		    paths[i++] = node.getPath();
		}
	    }
	}
	else if(parent == null) {
	    paths = new TreeNode[0][0];
	}
	else {
	    paths = new TreeNode[1][];
	    paths[0] = getPath();
	}
	return paths;
    }

    public void setRankedName(String rankedName)
    {
	if(rankedName == null) {
	    rankedName = "";
	    setLiteral("");
	}	    
	this.rankedName = rankedName;
    }

    public void setName(String rank, String literal)
    {
	setLiteral(literal);
	StringBuffer buffer = new StringBuffer();
	if(rank != null && rank.length() > 0) {
	    buffer.append(rank);
	    if(literal != null && literal.length() > 0)
		buffer.append(' ');
	}
	if(literal != null && literal.length() > 0) {
	    buffer.append(literal);
	    setLiteral(literal);
	}

	setRankedName(buffer.toString());
    }

    public void setLiteral(String literal)
    {
	if(literal == null)
	    literal = "";
	this.literal = literal;
    }

    public String getRank()
    {
	String name = getRankedName();
	int firstWS = name.indexOf(' ');
	int literalIndex = name.indexOf(getLiteral());
	if(firstWS != -1 && literalIndex > 0 && firstWS < literalIndex)
	    return name.substring(0, firstWS);
	return null;
    }

    public String getLiteral()
    {
	return literal;
    }

    public String getRankedName()
    {
	//return rankedName;
	return getLiteral();
    }

    protected String getRankedName(boolean abbreviate)
    {
	return getRankedName();
    }

    public String toString()
    {
	//return getName();
	//return getLiteral();
	return getRankedName();
    }


    public String getToolTipText()
    {
	return getToolTipText(1);
    }

    public void setParent(MutableTreeNode newParent)
    {
	super.setParent(newParent);
	toolTipText = null;
    }

    protected String getUnrankedName()
    {
	String name = getLiteral();
	int index = name.indexOf(' ');
	if(index != -1) {
	    name = name.substring(index + 1);
	}
	return name;
    }

    protected void checkLowerNameUsage()
    {
    }
}
