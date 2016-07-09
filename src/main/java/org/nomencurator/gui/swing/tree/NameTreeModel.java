/*
 * NameTreeModel.java:  a TreeModel retaining NameTreeNodes
 *
 * Copyright (c) 2002, 2003, 2004, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.Rank;

import org.nomencurator.model.gbif.NubNameUsage;

import org.nomencurator.util.MapUtility;

/**
 * {@code TreeModel} to manage {@code NameTreeNode}
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeModel
    extends DefaultTreeModel
{
    private static final long serialVersionUID = -468186197558060266L;

    protected static MapUtility<String, NameTreeNode> nameTreeNodeUtility =
	new MapUtility<String, NameTreeNode>();

    protected String viewName;

    protected Map<String, Set<NameTreeNode>> names;

    protected Map<String, NameTreeNode> rankedNames;

    protected Map<String, Map<String, NameTreeNode>> namesAtRank;

    protected Map<String, NameTreeNode> childParent;

    protected boolean rootToBeVisible;

    /**
     * Constructs {@code NameTreeModel}
     * using {@code node} to construct
     * root tree node of this model
     *
     * @param node {@code NameUsage} to be
     * used as the root node
     */
    public NameTreeModel(NameUsage<?> node)
    {
	this(new NameTreeNode(/*(NameUsage<NameUsage<?>>)*/node));
    }

    /**
     * Constructs {@code NameTreeModel}
     * using {@code node} as the root node.
     *
     * @param node {@code NameNodeTree} to be
     * the root node
     */
    public NameTreeModel(NameTreeNode node)
    {
	super(node);
	initializeLocalVars(node);
    }

    protected void initializeLocalVars(NameTreeNode node)
    {
	names = new HashMap<String, Set<NameTreeNode>>();
	rankedNames = new HashMap<String, NameTreeNode>();
	namesAtRank = new HashMap<String, Map<String, NameTreeNode>>();
	if(node == null)
	    return;
	Enumeration<?> e = node.depthFirstEnumeration();
	StringBuffer rankedName = new StringBuffer();
	while(e.hasMoreElements()) {
	    Object element = e.nextElement();
	    if (!(element instanceof NameTreeNode))
		continue;
	    node = (NameTreeNode)element;
	    Object userObject = node.getUserObject();
	    if (!(userObject instanceof NameUsage))
		continue;
	    NameUsage<?> n = (NameUsage<?>)userObject;
	    if(n.getLiteral() == null)
		continue;
	    rankedName.delete(0, rankedName.length());
	    String name = Rank.getRank(n);

	    if(name != null)
		rankedName.append(name).append(' ');
	    else
		name = "";

	    Map<String, NameTreeNode> atRank = namesAtRank.get(name);
	    if(atRank == null) {
		atRank = new HashMap<String, NameTreeNode>();
		namesAtRank.put(name, atRank);
	    }

	    name = n.getLiteral();
	    
	    atRank.put(name, node);

	    if(name != null)
		rankedName.append(name);
	    //rankedNames.put(rankedName.toString(), node);
	    rankedNames.put(node.getRankedName(), node);

	    Set<NameTreeNode> h = names.get(name);
	    if(h == null) {
		h = new HashSet<NameTreeNode>();
		names.put(name, h);
	    }
	    h.add(node);

	}
	rootToBeVisible = true;
    }

    /**
     * Returns view name of the name tree.
     * If it is set by {@code setViewName()},
     * the set name is used.  Instead, it returns
     * view name held by the root {@code NameUsage}
     *
     * @return String representing view name of this tree
     *
     * @see #setViewName(String)
     * @see NameUsage#getViewName
     */
    public String getViewName()
    {
	if(viewName != null)
	    return viewName;

	Object root = getRoot();
	if(root == null)
	    return "";

	NameUsage<?> rootUsage = (NameUsage<?>)((NameTreeNode)root).getUserObject();

	// String viewName = rootUsage.getViewName();
	String viewName = rootUsage.getDatasetTitle();

	if ((viewName == null || viewName.length() == 0) 
	    && (rootUsage instanceof NubNameUsage)) {
	    viewName = rootUsage.getViewName();
	    //viewName = ((NubNameUsage)rootUsage).getDatasetTitle();
	}

	return (viewName == null) ? "" : viewName;
    }

    /**
     * Sets {@code name} as view name of this tree
     *
     * @see #getViewName()
     */
    public void setViewName(String name)
    {
	viewName = name;
    }

    /**
     * Returns {@code Enumeration} of names in this tree,
     * or null if no name is contained.
     *
     * @returns {@code Enumeration} of names in this tree,
     * or null if no name is contained.
     */
    public Set<String> getNames()
    {
	if(names == null)
	    return null;
	return names.keySet();
    }

    /**
     * Returns {@code Enumeration} of names in this tree,
     * or null if no name is contained.
     *
     * @returns {@code Enumeration} of names in this tree,
     * or null if no name is contained.
     */
    public Map<String, Set<NameTreeNode>> getNameSet()
    {
	return names;
    }

    public NameTreeNode getNodeFor(String name)
    {
	return rankedNames.get(name);
    }

    public NameTreeNode getNode(NameUsage<?> nameUsage)
    {
	NameTreeNode rootNode = (NameTreeNode)getRoot();
	NameUsage<?> root = rootNode.getNameUsage();
	NameUsage<?> current = nameUsage;
	List<NameUsage<?>> pathList = new ArrayList<NameUsage<?>>();
	while(current != root) { //FIXME
	    pathList.add(current);
	    NameUsage<?> tmp = current.getHigherNameUsage();
	    if(tmp == null) {
		break;
	    }
	    current = current.getHigherNameUsage();
	}
	
	NameUsage<?>[] path = pathList.toArray(new NameUsage<?>[pathList.size()]);

	NameTreeNode treeNode = rootNode;
	for(int i = path.length - 1; i > -1; i--) {
	    int children = treeNode.getChildCount();
	    NameTreeNode child = null;
	    for(int j = 0; j < children; j++) {
		TreeNode childNode = treeNode.getChildAt(j);
		if (childNode instanceof NameTreeNode) {
		    child = (NameTreeNode)childNode;
		}
		else {
		    child = null;
		}
		if(child != null
		   && child.getNameUsage() == path[i]) 
			break;
	    }
	    if(child != null
	       && child.getNameUsage() != path[i])
		return null;
	    treeNode = child;
	}
			
	return treeNode;
    }

    /**
     * Returns {@code Enumeration} of {@code NameTreeNode}
     * having {@code name} in this tree, or null if no node
     * of the tree has {@code name}
     * @param name {@code String} representing name
     *
     * @return {@code Enumeration} of {@code NameTreeNode}
     * having {@code name} in this tree, or null if no node
     * of the tree has {@code name}
     */
    public Iterator<NameTreeNode> getNodes(String name)
    {
	if(name == null)
	    return null;

	Set<NameTreeNode> h = names.get(name);
	if(h == null)
	    return null;

	return h.iterator();
    }

    /**
     * Returns {@code Enumeration} of rank names in this tree,
     * or null if no name is contained.
     *
     * @returns {@code Enumeration} of rank names in this tree,
     * or null if no name is contained.
     */
    public Iterator<String> getRankNames()
    {
	if(namesAtRank == null)
	    return null;
	return namesAtRank.keySet().iterator();
    }

    /**
     * Returns {@code Enumeration} of names in this tree,
     * or null if no name is contained.
     *
     * @returns {@code Enumeration} of names in this tree,
     * or null if no name is contained.
     */
    public Iterator<String> getNamesAtRank(String rank)
    {
	Map<String, NameTreeNode> atRank = getNameSetAtRank(rank);
	if(atRank == null)
	    return null;
	return atRank.keySet().iterator();
    }

    /**
     * Returns {@code Hashtable} of names at {@code rank}
     * in this tree, or null if no name at the {@code rank}
     * is contained.
     *
     * @returns {@code Hashtable} of names in this tree,
     * or null if no name is contained.
     */
    public Map<String, NameTreeNode> getNameSetAtRank(String rank)
    {
	return namesAtRank.get(rank);
    }

    /**
     * Returns {@code Enumeration} of names in this tree,
     * or null if no name is contained.
     *
     * @returns {@code Enumeration} of names in this tree,
     * or null if no name is contained.
     */
    public Map<String, Map<String, NameTreeNode>> getNamesAtRankSet()
    {
	return namesAtRank;
    }

    public boolean isRootVisible()
    {
	return rootToBeVisible;
    }

    public void setRootVisible(boolean rootVisible)
    {
	rootToBeVisible = rootVisible;
    }

    public Map<String, NameTreeNode> getChildParentRelationships()
    {
	if(childParent == null)
	    calcurateChildParentRelationships();
	return childParent;
    }

    public Map<String, NameTreeNode> calcurateChildParentRelationships()
    {
	if(childParent == null)
	    childParent = new HashMap<String, NameTreeNode>();
	else
	    childParent.clear();

	Enumeration<?> e = 
	    ((DefaultMutableTreeNode)getRoot()).breadthFirstEnumeration();
	while(e.hasMoreElements()) {
	    Object element = e.nextElement();
	    if (element instanceof NameTreeNode) {
		NameTreeNode n = (NameTreeNode)element;
		childParent.put(n.getChildParentLiteral(), n);
	    }
	}

	return childParent;
    }

    public NameTreeModel getCommonSubtree(NameTreeModel model)
    {
	return getCommonSubtree(model.getChildParentRelationships());
    }

    public NameTreeModel getCommonSubtree(Map<String, NameTreeNode> relationship)
    {
	NameTreeModel model = 
	    recreateTree(nameTreeNodeUtility.intersection(getChildParentRelationships(), relationship));
	model.setRootVisible(true);
	return model;
   }

    public NameTreeModel getUncommonSubtree(NameTreeModel model)
    {
	return getUncommonSubtree(model.getChildParentRelationships());
    }

    public NameTreeModel getUncommonSubtree(Map<String, NameTreeNode> relationship)
    {
	Map<String, NameTreeNode> h = getChildParentRelationships();

	return recreateTree(nameTreeNodeUtility.xor(h, nameTreeNodeUtility.intersection(h, relationship)));
    }

    public NameTreeModel[] getSubtrees(NameTreeModel model)
    {
	return getSubtrees(model.getChildParentRelationships());
    }

    public NameTreeModel[] getSubtrees(Map<String, NameTreeNode> relationship)
    {
	Map<String, NameTreeNode> h = getChildParentRelationships();
	Map<String, NameTreeNode> common = 
	    nameTreeNodeUtility.intersection(h, relationship);
	Map<String, NameTreeNode> uncommon = nameTreeNodeUtility.xor(h, common);

	NameTreeModel commonModel = recreateTree(common);
	commonModel.setRootVisible(true);

	return new NameTreeModel[] {
	    commonModel, recreateTree(uncommon)};
    }

    protected NameTreeModel recreateTree(Map<String, NameTreeNode> nodes)
    {
	List<NameTreeNode> highestNodes = getHighestNodes(nodes);

	NameTreeNode rootNode = highestNodes.get(0);
	if(highestNodes.size() == 1) {
	    rootNode = 
		(NameTreeNode)rootNode.getParent();
	}
	else {
	    for(NameTreeNode n : highestNodes) {
		rootNode = (NameTreeNode)rootNode.getSharedAncestor(n);
	    }
	}
	
	if(rootNode == null)
	    rootNode = (NameTreeNode)getRoot();

	rootNode =
	    new NameTreeNode(rootNode.getNameUsage(), false);
	rootNode.setAllowsChildren(true);

	rootNode = getSubtree(rootNode, nodes);

	while(!nodes.isEmpty()) {
	    highestNodes = getHighestNodes(nodes);
	    for(NameTreeNode n : highestNodes) {
		rootNode.add(getSubtree(n, nodes));
		nodes.remove(n.getChildParentLiteral());
	    }
	}

	NameTreeModel model = 
	    new NameTreeModel(rootNode);
	//model.setRootVisible(false);
	model.setRootVisible(true);
	if(viewName != null)
	    model.setViewName(viewName);
	return model;
    }

    protected List<NameTreeNode> getHighestNodes(Map<String, NameTreeNode> nodes)
    {
	List<NameTreeNode> highestNodes = new ArrayList<NameTreeNode>();
	int lowestLevel = Integer.MAX_VALUE;
	Collection<NameTreeNode> treeNodes = nodes.values();
	for (NameTreeNode node : treeNodes) {
	    int level = node.getLevel();
	    if(level < lowestLevel) {
		highestNodes.clear();
		highestNodes.add(node);
		lowestLevel = level;
	    }
	    else if(level == lowestLevel) {
		if(!highestNodes.contains(node))
		    highestNodes.add(node);
	    }
	}

	return highestNodes;
    }

    protected NameTreeNode getSubtree(NameTreeNode node,
				      Map<String, NameTreeNode> nodes)
    {
	NameTreeNode rootNode = 
	    new NameTreeNode(node.getNameUsage(), false);
	rootNode.setAllowsChildren(true);
	Enumeration<?> e = node.children();
	NameTreeNode child = null;
	while(e.hasMoreElements()) {
	    Object element = e.nextElement();
	    if (element instanceof NameTreeNode) {
		child = (NameTreeNode)element;
	    }
	    String literal = child.getChildParentLiteral();
	    child = nodes.get(literal);
	    if(child == null)
		continue;
	    if(child.isLeaf()) {
		child = new NameTreeNode(child.getNameUsage());
		nodes.remove(literal);
	    }
	    else
		child = getSubtree(child, nodes);

	    rootNode.add(child);
	}
	nodes.remove(node.getChildParentLiteral());
	return rootNode;
    }
}
